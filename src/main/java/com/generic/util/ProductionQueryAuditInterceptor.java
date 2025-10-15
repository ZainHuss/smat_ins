package com.generic.util;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import com.smat.ins.model.entity.QueryAuditLog;
import com.smat.ins.model.entity.SysUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A Hibernate interceptor for auditing SELECT queries in a production environment.
 * This interceptor captures details about executed SELECT statements, including
 * parameters (via integration with ParameterCaptureLogger), user information,
 * IP address, and execution duration. It processes audit tasks asynchronously
 * to minimize impact on application performance.
 */
public class ProductionQueryAuditInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = -4684346370886747412L;
    private static final Logger logger = LoggerFactory.getLogger(ProductionQueryAuditInterceptor.class);

    // --- Configurable Properties (initialized from Spring Environment) ---
    private final Pattern enversPattern;
    private final int maxJsonLength;
    private final int defaultThreadPoolSize;
    private final long shutdownTimeoutSeconds;
    private final List<Pattern> excludedPatterns;
    private final boolean auditEnabled;
    private final int auditSamplingRate;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private SessionFactory sessionFactory;
    private final Environment environment;
    private final ExecutorService auditExecutor;
    private final ThreadLocal<QueryContext> queryContext = ThreadLocal.withInitial(() -> null);
    private final ConcurrentMap<String, String> entityNameCache = new ConcurrentHashMap<>(256);
    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * Constructor for the interceptor. Initializes configurable properties from the Spring Environment.
     *
     * @param environment The Spring Environment to read application properties.
     */
    public ProductionQueryAuditInterceptor(Environment environment) {
        this.environment = environment;

        this.auditEnabled = environment.getProperty("audit.query.enabled", Boolean.class, true);
        this.auditSamplingRate = environment.getProperty("audit.query.sampling-rate", Integer.class, 1);
        this.defaultThreadPoolSize = environment.getProperty("audit.executor.threads", Integer.class, 4);
        this.shutdownTimeoutSeconds = environment.getProperty("audit.executor.shutdown-timeout-seconds", Long.class, 30L);
        this.maxJsonLength = environment.getProperty("audit.json.max-length", Integer.class, 4000);

        this.enversPattern = Pattern.compile(
                environment.getProperty("audit.exclude.envers-pattern", "FROM (REVINFO|CUSTOM_REVISION)"),
                Pattern.CASE_INSENSITIVE);

        String excludedRegexes = environment.getProperty("audit.exclude.sql-patterns",
                                                         "NEXTVAL|CURRVAL|FROM DUAL|IDENTITY|SYSDATE|SELECT 1");
        this.excludedPatterns = new ArrayList<>();
        for (String regex : excludedRegexes.split("\\|")) {
             if (!regex.trim().isEmpty()) {
                 this.excludedPatterns.add(Pattern.compile(regex.trim(), Pattern.CASE_INSENSITIVE));
             }
        }

        if (this.excludedPatterns.isEmpty()) {
            logger.warn("No specific SQL exclusion patterns configured (audit.exclude.sql-patterns). Using default patterns.");
            this.excludedPatterns.add(Pattern.compile("NEXTVAL", Pattern.CASE_INSENSITIVE));
            this.excludedPatterns.add(Pattern.compile("CURRVAL", Pattern.CASE_INSENSITIVE));
            this.excludedPatterns.add(Pattern.compile("FROM DUAL", Pattern.CASE_INSENSITIVE));
            this.excludedPatterns.add(Pattern.compile("IDENTITY", Pattern.CASE_INSENSITIVE));
            this.excludedPatterns.add(Pattern.compile("SYSDATE", Pattern.CASE_INSENSITIVE));
            this.excludedPatterns.add(Pattern.compile("SELECT 1", Pattern.CASE_INSENSITIVE));
        }

        this.auditExecutor = createAuditExecutor();
    }

    /**
     * Sets the Hibernate SessionFactory. This method is typically called by the Spring
     * application context after the interceptor is created.
     *
     * @param sessionFactory The SessionFactory to use for saving audit logs.
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Creates and configures the `ExecutorService` for asynchronous audit log saving.
     * Uses a `ThreadPoolExecutor` with a bounded queue and `CallerRunsPolicy` to handle
     * potential overload gracefully.
     *
     * @return The configured ExecutorService.
     */
    private ExecutorService createAuditExecutor() {
        int poolSize = this.defaultThreadPoolSize;
        int queueCapacity = environment.getProperty("audit.executor.queue-capacity", Integer.class, 1000);

        return new ThreadPoolExecutor(
                poolSize,
                poolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new AuditThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * Called by Hibernate when a SQL statement is prepared. This is the entry point
     * for capturing query details.
     *
     * @param sql The SQL statement being prepared.
     * @return The original SQL statement (no modification).
     */
    @Override
    public String onPrepareStatement(String sql) {
        if (!auditEnabled) {
            return sql;
        }

        if (auditSamplingRate > 1 && ThreadLocalRandom.current().nextInt(auditSamplingRate) != 0) {
            logger.debug("Skipping SQL audit due to sampling rate ({}).", auditSamplingRate);
            return sql;
        }

        if (shouldAudit(sql)) {
            QueryContext context = new QueryContext(sql);
            context.setStartTime(System.nanoTime());
            String txId = UUID.randomUUID().toString();
            context.setTxId(txId);

            ParameterCaptureLogger.setTxId(txId);
            queryContext.set(context);

            MDC.put("txId", txId);
            MDC.put("username", getCurrentUsername());
            MDC.put("ipAddress", getClientIPAddress());

            logger.debug("[{}] Preparing SQL for auditing: {}", txId, sql);
        }
        return sql;
    }

    /**
     * Called by Hibernate when an entity is loaded. Used to determine the entity
     * name associated with the current query.
     *
     * @param entity The entity object being loaded.
     * @param id The ID of the entity.
     * @param state The current state of the entity's properties.
     * @param propertyNames The names of the entity's properties.
     * @param types The types of the entity's properties.
     * @return Always returns false to indicate no modification to the load process.
     */
    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, org.hibernate.type.Type[] types) {
        QueryContext context = queryContext.get();
        if (context != null) {
            String entitySimpleName = entity.getClass().getSimpleName();
            context.setEntityName(entitySimpleName);
            logger.debug("[{}] Detected entity for audit: {}", context.getTxId(), context.getEntityName());
        }
        return false;
    }

    /**
     * Called by Hibernate after a transaction completes (either commits or rolls back).
     * This is the point where the audit log is finalized and submitted for saving.
     *
     * @param tx The completed transaction.
     */
    @Override
    public void afterTransactionCompletion(Transaction tx) {
        QueryContext context = queryContext.get();
        if (context != null && shouldAudit(context.getSql())) {
            try {
                context.setUsername(getCurrentUsername());
                context.setIpAddress(getClientIPAddress());
                context.setSessionId(getHttpSessionId());
                context.setUserAgent(getUserAgent());

                extractQueryData(context);

                submitAuditTask(context);
            } catch (Exception e) {
                logger.error("[{}] Error during audit preparation: {}", context.getTxId(), e.getMessage(), e);
            } finally {
                ParameterCaptureLogger.clearQueryData(context.getTxId());
                ParameterCaptureLogger.clearTxId();
                queryContext.remove();
                MDC.remove("txId");
                MDC.remove("username");
                MDC.remove("ipAddress");
            }
        } else if (context != null) {
            ParameterCaptureLogger.clearQueryData(context.getTxId());
            ParameterCaptureLogger.clearTxId();
            queryContext.remove();
            MDC.remove("txId");
            MDC.remove("username");
            MDC.remove("ipAddress");
        }
    }

    /**
     * Retrieves the captured query data (including parameters) from `ParameterCaptureLogger`
     * and populates it into the `QueryContext`.
     *
     * @param context The current `QueryContext`.
     */
    private void extractQueryData(QueryContext context) {
        try {
            ParameterCaptureLogger.QueryData queryData = ParameterCaptureLogger.getQueryData(context.getTxId());
            context.setParameters(queryData.getParameters());
            context.setPreparedSql(queryData.getPreparedSql());
            context.setExecutedSql(queryData.getExecutedSql());
            context.setUrl(queryData.getUrl());
            context.setParameterExtractionSuccessful(queryData.isParameterExtractionSuccessful());

            if (!queryData.isParameterExtractionSuccessful()) {
                logger.warn("[{}] Parameter extraction for this query reported failure. Prepared SQL: {}",
                           context.getTxId(), queryData.getPreparedSql());
            }
            logger.debug("[{}] Extracted query data: params={}, preparedSql={}, executedSql={}, url={}, success={}",
                        context.getTxId(), queryData.getParameters(), queryData.getPreparedSql(),
                        queryData.getExecutedSql(), queryData.getUrl(), queryData.isParameterExtractionSuccessful());
        } catch (Exception e) {
            logger.error("[{}] Failed to retrieve query data from ParameterCaptureLogger: {}", context.getTxId(), e.getMessage(), e);
            context.setParameterExtractionSuccessful(false);
        }
    }

    /**
     * Submits the `QueryContext` to the `auditExecutor` for asynchronous processing.
     *
     * @param context The `QueryContext` to audit.
     */
    private void submitAuditTask(QueryContext context) {
        try {
            auditExecutor.submit(() -> {
                MDC.put("txId", context.getTxId());
                // The auditUuId is generated here and put into MDC for the audit thread.
                // It will be retrieved by auditQuery to populate QueryAuditLog.auditUuId.
                MDC.put("auditUuId", UUID.randomUUID().toString());
                MDC.put("username", context.getUsername());
                MDC.put("ipAddress", context.getIpAddress());
                try {
                    auditQuery(context);
                } catch (Exception e) {
                    logger.error("[{}] Failed to execute audit query task: {}", context.getTxId(), e.getMessage(), e);
                } finally {
                    MDC.remove("txId");
                    MDC.remove("auditUuId");
                    MDC.remove("username");
                    MDC.remove("ipAddress");
                }
            });
        } catch (RejectedExecutionException e) {
            logger.warn("[{}] Audit executor queue full or shut down - dropping audit record.", context.getTxId());
        }
    }

    /**
     * Determines whether a given SQL statement should be audited based on
     * configuration rules (SELECT, Envers exclusion, specific pattern exclusions).
     *
     * @param sql The SQL statement to check.
     * @return true if the SQL should be audited, false otherwise.
     */
    private boolean shouldAudit(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }
        String normalizedSql = sql.trim().toUpperCase();

        if (!normalizedSql.startsWith("SELECT") || enversPattern.matcher(normalizedSql).find()) {
            return false;
        }

        for (Pattern pattern : excludedPatterns) {
            if (pattern.matcher(normalizedSql).find()) {
                return false;
            }
        }
        return true;
    }

    /**
     * The core auditing logic, executed asynchronously. Creates and saves the
     * `QueryAuditLog` entity.
     *
     * @param context The `QueryContext` containing all details for the audit log.
     */
    private void auditQuery(QueryContext context) {
        long startTime = System.currentTimeMillis();
        // Get auditUuId from MDC set in submitAuditTask. This is the unique ID for the audit log entry.
        String auditUuId = MDC.get("auditUuId");

        try {
            QueryAuditLog auditLog = createAuditLog(context, auditUuId); // Pass auditUuId
            saveAuditLog(auditLog);
            logger.debug("[{}] Audit {} completed in {}ms.", context.getTxId(), auditUuId, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            logger.error("[{}] Audit {} failed after {}ms: {}", context.getTxId(), auditUuId, System.currentTimeMillis() - startTime, e.getMessage(), e);
        }
    }

    /**
     * Creates a `QueryAuditLog` entity from the `QueryContext` data.
     *
     * @param context The `QueryContext` holding the query details.
     * @param auditUuId The unique UUID for this audit log entry.
     * @return A populated `QueryAuditLog` instance.
     */
    private QueryAuditLog createAuditLog(QueryContext context, String auditUuId) {
        QueryAuditLog auditLog = new QueryAuditLog();
        auditLog.setAuditUuId(auditUuId); // Set the renamed field
        auditLog.setTxId(context.getTxId()); // Set the txId from context
        auditLog.setQuery(context.getExecutedSql());
        auditLog.setEntityName(
                context.getEntityName() != null ? context.getEntityName() : resolveEntityName(context.getSql()));
        auditLog.setExecutionTime(LocalDateTime.now());
        auditLog.setUsername(context.getUsername());
        auditLog.setIpAddress(context.getIpAddress());
        auditLog.setExecutionDuration(context.getDurationMs());
        auditLog.setApplicationName(getApplicationName());
        auditLog.setSessionId(context.getSessionId());
        auditLog.setUserAgent(context.getUserAgent());
        auditLog.setParamExtractionSuccessful(context.isParameterExtractionSuccessful());

        Map<String, Object> auditData = new LinkedHashMap<>();
        auditData.put("txId", context.getTxId());
        auditData.put("auditUuId", auditUuId); // Also add to JSON for clarity
        auditData.put("originalSql", context.getSql());
        auditData.put("preparedSql", context.getPreparedSql());
        auditData.put("executedSql", context.getExecutedSql());
        auditData.put("url", context.getUrl());
        auditData.put("parameters", context.getParameters());
        auditData.put("parameterExtractionSuccessful", context.isParameterExtractionSuccessful());
        auditData.put("entityName", auditLog.getEntityName());
        auditData.put("executionTime", ISO_FORMATTER.format(auditLog.getExecutionTime()));
        auditData.put("username", context.getUsername());
        auditData.put("ipAddress", context.getIpAddress());
        auditData.put("sessionId", context.getSessionId());
        auditData.put("userAgent", context.getUserAgent());

        auditLog.setParameters(serializeAuditData(auditData));
        return auditLog;
    }

    /**
     * Converts a CLOB or String object to a String. Handles potential errors during conversion.
     *
     * @param clobOrString The object which might be a Clob or a String.
     * @return The String representation or an error message.
     */
    private String convertClobToString(Object clobOrString) {
        if (clobOrString == null)
            return null;
        try {
            if (clobOrString instanceof Clob) {
                Clob clob = (Clob) clobOrString;
                return clob.getSubString(1, (int) clob.length());
            } else if (clobOrString instanceof String) {
                return (String) clobOrString;
            }
            return clobOrString.toString();
        } catch (Exception e) {
            logger.warn("Failed to convert Clob to String: {}", e.getMessage(), e);
            return "ERROR_CONVERTING_CLOB";
        }
    }

    /**
     * Serializes the audit data map into a JSON string. Truncates the JSON if it exceeds
     * the configured maximum length.
     *
     * @param auditData The map of audit data.
     * @return The JSON string representation of the audit data.
     */
    private String serializeAuditData(Map<String, Object> auditData) {
        try {
            String json = objectMapper.writeValueAsString(auditData);
            if (json.length() > maxJsonLength) {
                logger.warn("[{}] Audit data exceeds max length ({} chars); truncating to {} characters. Original length: {}",
                           auditData.get("txId"), maxJsonLength, maxJsonLength, json.length());
                json = json.substring(0, maxJsonLength) + "...[truncated]";
            }
            return json;
        } catch (JsonProcessingException e) {
            logger.warn("[{}] Failed to serialize audit data to JSON: {}", auditData.get("txId"), e.getMessage(), e);
            return "{\"error\": \"Failed to serialize audit data\", \"txId\": \"" + auditData.get("txId") + "\"}";
        }
    }

    /**
     * Resolves the entity name from the SQL query. Uses a cache to avoid repeated parsing.
     *
     * @param sql The SQL query string.
     * @return The extracted entity/table name, or "unknown".
     */
    private String resolveEntityName(String sql) {
        return entityNameCache.computeIfAbsent(sql, this::extractEntityName);
    }

    /**
     * Attempts to extract the table/entity name from a SQL SELECT query by looking for the "FROM" clause.
     *
     * @param sql The SQL query string.
     * @return The extracted table/entity name, or "unknown" if not found.
     */
    private String extractEntityName(String sql) {
        String upperSql = sql.toUpperCase().trim().replaceAll("\\s+", " ");
        int fromIndex = upperSql.indexOf(" FROM ");
        if (fromIndex > 0) {
            String fromClause = upperSql.substring(fromIndex + 6);
            String[] parts = fromClause.split("[ ,\\n\\t(]");
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    String tableName = part.trim();
                    if (tableName.contains(".")) {
                        tableName = tableName.substring(tableName.lastIndexOf(".") + 1);
                    }
                    tableName = tableName.replaceAll("[\\[\\]`\"']", "");
                    return tableName.isEmpty() ? "unknown" : tableName;
                }
            }
        }
        return "unknown";
    }

    /**
     * Centralized method to get the current `HttpServletRequest` if available in the web context.
     *
     * @return An `Optional` containing the `HttpServletRequest` if present, otherwise empty.
     */
    private Optional<HttpServletRequest> getCurrentHttpRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return Optional.ofNullable(attributes).map(ServletRequestAttributes::getRequest);
        } catch (IllegalStateException e) {
            logger.debug("Not in a web request context: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Retrieves the current authenticated username, preferring Spring Security context.
     * Falls back to HttpSession attribute if Spring Security is not available or user is anonymous.
     *
     * @return The username, or "system" if no user can be determined.
     */
    private String getCurrentUsername() {
        return getCurrentHttpRequest()
                .map(request -> {
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        SysUser user = (SysUser) session.getAttribute("user");
                        if (user != null) {
                            return user.getUserName();
                        }
                    }
                    return null;
                })
                .orElse("system");
    }

    /**
     * Retrieves the client's IP address from various HTTP headers or `getRemoteAddr()`.
     * Handles common proxy/load balancer headers.
     *
     * @return The client's IP address, or "unknown".
     */
    private String getClientIPAddress() {
        return getCurrentHttpRequest()
                .map(request -> {
                    String ip = request.getHeader("X-Forwarded-For");
                    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                        ip = request.getHeader("Proxy-Client-IP");
                    }
                    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                        ip = request.getHeader("WL-Proxy-Client-IP");
                    }
                    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                        ip = request.getHeader("HTTP_CLIENT_IP");
                    }
                    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                    }
                    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                        ip = request.getRemoteAddr();
                    }
                    return ip != null ? ip.split(",")[0].trim() : "unknown";
                })
                .orElse("unknown");
    }

    /**
     * Retrieves the HTTP Session ID if an active session exists.
     *
     * @return The session ID string, or "no-session" if not found.
     */
    private String getHttpSessionId() {
        return getCurrentHttpRequest()
                .map(request -> request.getSession(false))
                .map(HttpSession::getId)
                .orElse("no-session");
    }

    /**
     * Retrieves the User-Agent header from the HTTP request.
     *
     * @return The User-Agent string, or "unknown-agent".
     */
    private String getUserAgent() {
        return getCurrentHttpRequest()
                .map(request -> request.getHeader("User-Agent"))
                .orElse("unknown-agent");
    }

    /**
     * Retrieves the application name from Spring Environment properties.
     *
     * @return The application name, or "unknown".
     */
    private String getApplicationName() {
        return environment.getProperty("spring.application.name", "unknown");
    }

    /**
     * Saves the `QueryAuditLog` entity to the database. This operation is performed
     * in its own Hibernate session and transaction to ensure it does not interfere
     * with the main application transaction. Errors are logged but not rethrown.
     *
     * @param auditLog The `QueryAuditLog` entity to save.
     */
    private void saveAuditLog(QueryAuditLog auditLog) {
        if (sessionFactory == null) {
            logger.error("SessionFactory is not initialized; cannot save audit log for auditUuId {}", auditLog.getAuditUuId());
            return;
        }
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(auditLog);
            transaction.commit();
            logger.debug("[{}] Successfully saved audit log for auditUuId {}.", auditLog.getTxId(), auditLog.getAuditUuId());
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                    logger.warn("[{}] Rolled back audit log transaction for auditUuId {}.", auditLog.getTxId(), auditLog.getAuditUuId());
                } catch (Exception rollbackEx) {
                    logger.error("[{}] Failed to rollback audit log transaction for auditUuId {}: {}", auditLog.getTxId(), auditLog.getAuditUuId(), rollbackEx.getMessage(), rollbackEx);
                }
            }
            logger.error("[{}] Failed to save audit log for auditUuId {}: {}", auditLog.getTxId(), auditLog.getAuditUuId(), e.getMessage(), e);
        } finally {
            if (session != null) {
                try {
                    session.close();
                    logger.debug("Closed audit session for auditUuId {}.", auditLog.getAuditUuId());
                } catch (Exception closeEx) {
                    logger.error("Failed to close audit session for auditUuId {}: {}", auditLog.getAuditUuId(), closeEx.getMessage(), closeEx);
                }
            }
        }
    }

    /**
     * Shuts down the audit executor gracefully.
     * Tries to await termination for a configured period, then forces shutdown if necessary.
     */
    public void shutdown() {
        logger.info("Initiating graceful shutdown of audit executor.");
        auditExecutor.shutdown();
        try {
            if (!auditExecutor.awaitTermination(shutdownTimeoutSeconds, TimeUnit.SECONDS)) {
                logger.warn("Audit executor did not shut down cleanly within {} seconds. Forcing shutdown.", shutdownTimeoutSeconds);
                auditExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.warn("Audit executor shutdown interrupted. Forcing shutdown.", e);
            auditExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("Audit executor shutdown complete.");
    }

    /**
     * Internal class to hold all relevant context information for a single query's audit.
     * This context is created at the start of a query (onPrepareStatement) and populated
     * throughout its lifecycle until the transaction completes (afterTransactionCompletion).
     */
    private static class QueryContext {
        private final String sql;
        private Map<String, Object> parameters;
        private String preparedSql;
        private String executedSql;
        private String url;
        private long startTime;
        private String username;
        private String ipAddress;
        private String entityName;
        private String txId;
        private String sessionId;
        private String userAgent;
        private boolean parameterExtractionSuccessful = true;

        public QueryContext(String sql) {
            this.sql = sql;
            this.parameters = new LinkedHashMap<>();
        }

        public String getSql() { return sql; }
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
        public String getPreparedSql() { return preparedSql; }
        public void setPreparedSql(String preparedSql) { this.preparedSql = preparedSql; }
        public String getExecutedSql() { return executedSql; }
        public void setExecutedSql(String executedSql) { this.executedSql = executedSql; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public void setStartTime(long startTime) { this.startTime = startTime; }

        public long getDurationMs() {
            return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        }

        public String getUsername() { return username != null ? username : "system"; }
        public void setUsername(String username) { this.username = username; }
        public String getIpAddress() { return ipAddress != null ? ipAddress : "unknown"; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        public String getEntityName() { return entityName; }
        public void setEntityName(String entityName) { this.entityName = entityName; }
        public String getTxId() { return txId; }
        public void setTxId(String txId) { this.txId = txId; }

        public String getSessionId() { return sessionId != null ? sessionId : "no-session"; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getUserAgent() { return userAgent != null ? userAgent : "unknown-agent"; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

        public boolean isParameterExtractionSuccessful() { return parameterExtractionSuccessful; }
        public void setParameterExtractionSuccessful(boolean parameterExtractionSuccessful) { this.parameterExtractionSuccessful = parameterExtractionSuccessful; }
    }

    private static class AuditThreadFactory implements ThreadFactory {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName("query-audit-" + thread.getId());
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler(
                    (t, e) -> logger.error("Uncaught exception in audit thread " + t.getName(), e));
            return thread;
        }
    }
}
