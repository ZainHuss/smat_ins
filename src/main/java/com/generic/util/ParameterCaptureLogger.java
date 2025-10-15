package com.generic.util;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * P6Spy MessageFormattingStrategy to capture and store executed SQL and its parameters.
 * This class intercepts SQL statements processed by P6Spy and extracts parameter values
 * by comparing the prepared and executed SQL strings. It associates these details
 * with a transaction ID (txId) stored in a ThreadLocal, allowing the ProductionQueryAuditInterceptor
 * to retrieve the data later in the transaction lifecycle.
 */
public class ParameterCaptureLogger implements MessageFormattingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ParameterCaptureLogger.class);

    // Stores QueryData indexed by transaction ID (txId)
    // Using ConcurrentHashMap for thread-safe access across different threads if needed,
    // though primary access is via txId in a ThreadLocal context.
    private static final ConcurrentHashMap<String, QueryData> QUERY_DATA_BY_TX = new ConcurrentHashMap<>();

    // ThreadLocal to hold the current transaction ID, ensuring each thread
    // has its own isolated txId for correlating P6Spy events with Hibernate transactions.
    private static final ThreadLocal<String> TX_ID = new ThreadLocal<>();

    // Maximum length for parameter values before truncation, moved here from interceptor
    // as parameter sanitization/truncation now happens at this stage.
    private static final int MAX_PARAMETER_VALUE_LENGTH = 1000;

    /**
     * Enum to represent the inferred type of an extracted SQL parameter.
     * Aids in understanding the data captured.
     */
    public enum ParameterType {
        STRING, NUMBER, BOOLEAN, NULL, SQL_FUNCTION, UNKNOWN
    }

    /**
     * Represents an extracted parameter, including its name (from prepared SQL),
     * its actual value (from executed SQL), its inferred type, and if it was truncated.
     */
    public static class ExtractedParameter {
        public String name; // This field will be set when added to the map in extractParameters
        public Object value;
        public ParameterType type;
        public boolean truncated;
        public int originalEndIndex; // Stores the index in the original SQL where the value ended

        public ExtractedParameter(String name, Object value, ParameterType type, boolean truncated, int originalEndIndex) {
            this.name = name;
            this.value = value;
            this.type = type;
            this.truncated = truncated;
            this.originalEndIndex = originalEndIndex;
        }

        @Override
        public String toString() {
            // Provides a readable representation for debugging
            return "{" +
                   "name='" + name + '\'' +
                   ", value=" + (value instanceof String ? "'" + value + "'" : value) +
                   ", type=" + type +
                   (truncated ? ", truncated=" + truncated : "") +
                   '}';
        }
    }

    /**
     * Formats the P6Spy message and, for relevant categories, extracts and stores
     * query parameters.
     *
     * @param connectionId The ID of the database connection.
     * @param now The timestamp of the event.
     * @param elapsed The elapsed time in milliseconds.
     * @param category The category of the SQL statement (e.g., "statement", "prepared_statement").
     * @param prepared The prepared SQL statement (with placeholders).
     * @param sql The executed SQL statement (with actual values).
     * @param url The JDBC URL.
     * @return A formatted message to be logged by P6Spy.
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed,
                               String category, String prepared, String sql, String url) {
        logger.debug("formatMessage called with category: {}, sql: {}, prepared: {}, url: {}",
                    category, sql, prepared, url);

        if (sql == null || sql.trim().isEmpty()) {
            logger.debug("Empty or null SQL; skipping parameter capture.");
            return "";
        }

        // Only process statement, prepared_statement, or batch categories for parameter extraction
        if (category.equals("statement") || category.equals("prepared_statement") || category.equals("batch")) {
            // Get or generate a transaction ID. This txId is set by the Hibernate interceptor.
            // If P6Spy processes a query not within an audited Hibernate transaction, a new UUID will be generated.
            String txId = TX_ID.get() != null ? TX_ID.get() : UUID.randomUUID().toString();

            // Extract parameters using the prepared and executed SQL
            Map<String, Object> params = extractParameters(txId, prepared, sql);

            // Store the query data (prepared, executed, URL, and extracted parameters)
            storeQueryData(txId, prepared, sql, url, params);
            logger.debug("[{}] Stored query data: category={}, params={}, url={}", txId, category, params, url);
        } else {
            logger.debug("Skipping parameter extraction for category: {}", category);
        }

        return String.format("Executed in %dms: %s", elapsed, sql);
    }

    /**
     * Extracts parameter values from the executed SQL by comparing it with the prepared SQL.
     * This method attempts to align the two SQL strings and identify where placeholder
     * values have been substituted.
     *
     * @param txId The transaction ID for logging context.
     * @param preparedSql The SQL statement with placeholders (e.g., "SELECT * FROM users WHERE id = ?").
     * @param executedSql The SQL statement with actual values (e.g., "SELECT * FROM users WHERE id = 123").
     * @return A Map where keys are placeholder names (e.g., "?1", ":paramName") and values are the extracted objects.
     * Returns an empty map if extraction fails or no parameters are found.
     */
    private Map<String, Object> extractParameters(String txId, String preparedSql, String executedSql) {
        Map<String, Object> params = new LinkedHashMap<>(); // Use LinkedHashMap to preserve order of parameters
        boolean extractionSuccessful = true; // Flag to track success of extraction

        try {
            logger.debug("[{}] Attempting to extract parameters. Prepared SQL: '{}', Executed SQL: '{}'",
                        txId, preparedSql, executedSql);

            // Normalize SQL strings for consistent comparison (handle whitespace, etc.)
            String normPreparedSql = normalizeSql(preparedSql);
            String normExecSql = normalizeSql(executedSql);

            // If prepared SQL is empty or identical to executed SQL, there are no parameters to extract.
            if (normPreparedSql.isEmpty() || normPreparedSql.equals(normExecSql)) {
                logger.debug("[{}] Prepared SQL is empty or identical to executed SQL; no parameters to extract.", txId);
                return params;
            }

            // Find all placeholders (both '?' and ':paramName') in the prepared SQL
            List<Placeholder> placeholders = findPlaceholders(normPreparedSql);

            if (placeholders.isEmpty()) {
                logger.debug("[{}] No placeholders found in prepared SQL; returning empty map.", txId);
                return params;
            }

            int preparedIndex = 0; // Current position in normalized prepared SQL
            int executedIndex = 0; // Current position in normalized executed SQL

            // Iterate through each identified placeholder
            for (Placeholder p : placeholders) {
                // Align the prepared and executed SQL strings up to the current placeholder's position.
                // This ensures we match the static parts of the query before extracting the dynamic parameter.
                while (preparedIndex < p.position && preparedIndex < normPreparedSql.length() &&
                       executedIndex < normExecSql.length()) {
                    char prepChar = normPreparedSql.charAt(preparedIndex);
                    char execChar = normExecSql.charAt(executedIndex);

                    // Character by character comparison. If a mismatch occurs, parameter extraction is aborted
                    // for this query, as the SQL structures might not align as expected.
                    if (!isEquivalent(prepChar, execChar)) {
                        String prepSnippet = getSnippet(normPreparedSql, preparedIndex);
                        String execSnippet = getSnippet(normExecSql, executedIndex);
                        logger.warn("[{}] Mismatch at position {}. Prepared: '{}' vs Executed: '{}'. Prepared snippet: '{}', Executed snippet: '{}'. Parameter extraction aborted for this query.",
                                   txId, preparedIndex, prepChar, execChar, prepSnippet, execSnippet);
                        extractionSuccessful = false;
                        return new LinkedHashMap<>(); // Return empty map indicating failure
                    }
                    preparedIndex++;
                    executedIndex++;
                }

                // If preparedIndex goes out of bounds before reaching the placeholder, it indicates
                // a structural problem with the prepared SQL or an error in parsing.
                if (preparedIndex > normPreparedSql.length()) {
                    logger.warn("[{}] Prepared SQL exhausted before reaching placeholder '{}'. Aborting parameter extraction.", txId, p.name);
                    extractionSuccessful = false;
                    return new LinkedHashMap<>(); // Return empty map indicating failure
                }

                // Advance the prepared SQL index past the placeholder itself (e.g., past '?' or ':paramName')
                preparedIndex += p.name.length();

                // Skip any whitespace or commas that might appear in the executed SQL right before the value
                // (e.g., "IN ( 1 , 2 )" vs "IN (?,?)")
                executedIndex = skipWhitespaceAndComma(normExecSql, executedIndex);

                // Extract the actual parameter value from the executed SQL
                ExtractedParameter extractedParam = extractParameterValue(normExecSql, executedIndex);

                // Update the executed SQL index to point after the extracted parameter value
                executedIndex = extractedParam.originalEndIndex;

                // Add the extracted and sanitized parameter to the map
                params.put(p.name, sanitizeParameterValue(extractedParam.value));

                // Skip any whitespace or commas that might appear immediately after the value
                executedIndex = skipWhitespaceAndComma(normExecSql, executedIndex);
            }

            logger.debug("[{}] Successfully extracted parameters: {}", txId, params);
        } catch (Exception e) {
            // Catch any unexpected exceptions during extraction and log them.
            logger.error("[{}] Failed to extract parameters due to unexpected exception: {}", txId, e.getMessage(), e);
            extractionSuccessful = false; // Mark as failed
            return new LinkedHashMap<>(); // Return empty map on error
        } finally {
            // Store the extraction status in the QueryData
            // Note: QueryData is created *after* this method returns, so this flag
            // is effectively set when the QueryData is constructed.
        }
        return params;
    }

    /**
     * Finds all SQL placeholders ('?' for positional, ':paramName' for named) in a given SQL string.
     *
     * @param sql The normalized SQL string to scan for placeholders.
     * @return A list of `Placeholder` objects, each containing its position and name.
     */
    private List<Placeholder> findPlaceholders(String sql) {
        List<Placeholder> placeholders = new ArrayList<>();
        int i = 0;
        int positionalParamCount = 1; // Counter for '?' placeholders (e.g., ?1, ?2, ...)
        while (i < sql.length()) {
            char currentChar = sql.charAt(i);
            if (currentChar == '?') {
                // Found a positional parameter
                placeholders.add(new Placeholder(i, "?" + positionalParamCount));
                positionalParamCount++;
                i++; // Move past '?'
            } else if (currentChar == ':') {
                // Found a potential named parameter (e.g., :paramName)
                StringBuilder paramName = new StringBuilder();
                int nameStart = i + 1; // Start of the parameter name (after ':')
                i++; // Move past ':'
                // Collect characters that are part of the parameter name (letters, digits)
                while (i < sql.length() && Character.isLetterOrDigit(sql.charAt(i))) {
                    paramName.append(sql.charAt(i));
                    i++;
                }
                if (paramName.length() > 0) {
                    placeholders.add(new Placeholder(nameStart - 1, ":" + paramName)); // Store with leading ':'
                }
            } else {
                i++; // Move to next character
            }
        }
        return placeholders;
    }

    /**
     * Extracts a single parameter value from the executed SQL starting at a given index.
     * This method handles quoted strings (single, double, backtick) and attempts basic
     * type inference.
     *
     * @param sql The normalized executed SQL string.
     * @param startIndex The starting index in the SQL string where the parameter value is expected.
     * @return An `ExtractedParameter` object containing the value, its inferred type,
     * and the index in the original string where the value ended.
     */
    private ExtractedParameter extractParameterValue(String sql, int startIndex) {
        StringBuilder valueBuilder = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = '\0'; // Stores the type of quote used (', ", `)
        int i = startIndex;

        while (i < sql.length()) {
            char c = sql.charAt(i);

            if (c == '\'' || c == '"' || c == '`') {
                if (!inQuotes) {
                    // Start of a quoted string
                    inQuotes = true;
                    quoteChar = c;
                    valueBuilder.append(c);
                } else if (c == quoteChar) {
                    // Possible end of quoted string or escaped quote
                    if (i + 1 < sql.length() && sql.charAt(i + 1) == quoteChar) {
                        // Detected escaped quote (e.g., 'It''s a string')
                        valueBuilder.append(c).append(c); // Append both quotes
                        i += 2; // Skip both characters
                        continue; // Continue scanning for more characters within the string
                    } else {
                        // End of quoted string
                        inQuotes = false;
                        valueBuilder.append(c); // Append the closing quote
                        i++;
                        break; // End of parameter value extraction
                    }
                } else {
                    // A different quote character inside a quoted string, treat as part of the string
                    valueBuilder.append(c);
                }
            } else if (!inQuotes && (c == ' ' || c == ',' || c == ')' || c == ';' || c == '\n' || c == '\r')) {
                // End of parameter value if not in quotes and a delimiter is found
                break;
            } else {
                valueBuilder.append(c); // Append character to value
            }
            i++;
        }

        String rawValue = valueBuilder.toString().trim();
        Object finalValue = rawValue;
        ParameterType type = ParameterType.UNKNOWN;
        boolean truncated = false;

        // Attempt to unquote the value and infer its data type
        if ((rawValue.startsWith("'") && rawValue.endsWith("'")) ||
            (rawValue.startsWith("\"") && rawValue.endsWith("\"")) ||
            (rawValue.startsWith("`") && rawValue.endsWith("`"))) {
            // Remove surrounding quotes for string values
            finalValue = rawValue.substring(1, rawValue.length() - 1);
            type = ParameterType.STRING;
        } else if (rawValue.equalsIgnoreCase("NULL")) {
            finalValue = null;
            type = ParameterType.NULL;
        } else if (rawValue.equalsIgnoreCase("TRUE") || rawValue.equalsIgnoreCase("FALSE")) {
            finalValue = Boolean.parseBoolean(rawValue);
            type = ParameterType.BOOLEAN;
        } else if (rawValue.matches("-?\\d+(\\.\\d+)?")) { // Simple regex for integers or decimals
            try {
                if (rawValue.contains(".")) {
                    finalValue = Double.parseDouble(rawValue);
                } else {
                    finalValue = Long.parseLong(rawValue);
                }
                type = ParameterType.NUMBER;
            } catch (NumberFormatException e) {
                // If parsing fails, it's not a valid number; treat as unknown string
                type = ParameterType.UNKNOWN;
            }
        } else if (rawValue.matches("(?i)(SYSDATE|CURRENT_TIMESTAMP|NOW|GETDATE|UUID\\(\\))(\\s*\\(\\s*\\))?")) {
            // Recognized SQL functions (e.g., for default values, not bound parameters)
            type = ParameterType.SQL_FUNCTION;
        }

        // Apply truncation for the value itself (before potential redaction)
        if (finalValue instanceof String && ((String) finalValue).length() > MAX_PARAMETER_VALUE_LENGTH) {
            finalValue = ((String) finalValue).substring(0, MAX_PARAMETER_VALUE_LENGTH) + "...[truncated]";
            truncated = true;
        }

        // Corrected constructor call: pass 'null' for name as it's not known at this point
        return new ExtractedParameter(null, finalValue, type, truncated, i);
    }

    /**
     * Skips whitespace and commas from the current index in the SQL string.
     *
     * @param sql The SQL string.
     * @param index The starting index.
     * @return The new index after skipping whitespace and commas.
     */
    private int skipWhitespaceAndComma(String sql, int index) {
        while (index < sql.length() &&
               (sql.charAt(index) == ' ' || sql.charAt(index) == ',' || sql.charAt(index) == '\n' || sql.charAt(index) == '\r')) {
            index++;
        }
        return index;
    }

    /**
     * Normalizes a SQL string by trimming, replacing multiple whitespaces with single spaces,
     * and standardizing whitespace around commas. This helps in consistent comparison.
     *
     * @param sql The SQL string to normalize.
     * @return The normalized SQL string.
     */
    private String normalizeSql(String sql) {
        if (sql == null) {
            return "";
        }
        return sql.trim()
                  .replaceAll("\\s+", " ") // Replace multiple whitespaces with a single space
                  .replaceAll("\\s*,\\s*", ","); // Standardize commas (no spaces around them)
    }

    /**
     * Checks if two characters are equivalent, considering common SQL formatting variations.
     * For instance, a space might be equivalent to a comma in certain list contexts.
     *
     * @param c1 First character.
     * @param c2 Second character.
     * @return true if characters are identical or equivalent, false otherwise.
     */
    private boolean isEquivalent(char c1, char c2) {
        if (c1 == c2) {
            return true;
        }
        // Allows for slight variations in SQL formatting like spaces vs. commas in lists
        return (c1 == ' ' && c2 == ',') || (c1 == ',' && c2 == ' ');
    }

    /**
     * Generates a snippet of the SQL string around a given index for debugging purposes.
     *
     * @param sql The SQL string.
     * @param index The central index for the snippet.
     * @return A substring representing the snippet.
     */
    private String getSnippet(String sql, int index) {
        int start = Math.max(0, index - 10);
        int end = Math.min(sql.length(), index + 10);
        return sql.substring(start, end).replace("\n", "\\n").replace("\r", "\\r");
    }

    /**
     * Redacts sensitive parameter values (e.g., passwords, tokens) if they match a pattern.
     * This is applied to the final extracted value.
     *
     * @param value The object value of the parameter.
     * @return The redacted value or the original value if not sensitive.
     */
    private Object sanitizeParameterValue(Object value) {
        if (value == null) {
            return null;
        }
        String strValue = value.toString();
        // Regex to identify common sensitive keywords (case-insensitive)
        if (strValue.toLowerCase().matches(".*(password|secret|token|key|credential|ssn|credit_card|card_number).*")) {
            return "[REDACTED]";
        }
        return value; // Return original value (truncation already handled in extractParameterValue)
    }

    /**
     * Internal class to hold information about a SQL placeholder found in prepared SQL.
     */
    private static class Placeholder {
        int position; // Starting index of the placeholder in the prepared SQL
        String name;   // The placeholder string itself (e.g., "?1", ":userId")

        Placeholder(int position, String name) {
            this.position = position;
            this.name = name;
        }
    }

    /**
     * Data class to encapsulate all information related to a captured SQL query.
     * This is what is stored in `QUERY_DATA_BY_TX` and retrieved by the interceptor.
     */
    public static class QueryData {
        String preparedSql;
        String executedSql;
        String url;
        Map<String, Object> parameters; // Map of extracted parameter names to their sanitized/truncated values
        boolean parameterExtractionSuccessful; // Flag indicating if parameter extraction was successful

        QueryData(String preparedSql, String executedSql, String url, Map<String, Object> parameters) {
            this.preparedSql = preparedSql;
            this.executedSql = executedSql;
            this.url = url;
            this.parameters = parameters;
            // Determine extraction success based on if parameters were expected but not found
            this.parameterExtractionSuccessful = !(parameters.isEmpty() && preparedSql != null &&
                                                    (preparedSql.contains("?") || preparedSql.contains(":")));
        }

        public String getPreparedSql() {
            return preparedSql;
        }

        public String getExecutedSql() {
            return executedSql;
        }

        public String getUrl() {
            return url;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public boolean isParameterExtractionSuccessful() {
            return parameterExtractionSuccessful;
        }
    }

    /**
     * Stores the `QueryData` in the `QUERY_DATA_BY_TX` map, associated with the given transaction ID.
     *
     * @param txId The transaction ID.
     * @param preparedSql The prepared SQL string.
     * @param executedSql The executed SQL string.
     * @param url The JDBC URL.
     * @param params The extracted parameters.
     */
    private void storeQueryData(String txId, String preparedSql, String executedSql, String url, Map<String, Object> params) {
        QUERY_DATA_BY_TX.put(txId, new QueryData(preparedSql, executedSql, url, params));
        logger.debug("[{}] QueryData stored.", txId);
    }

    /**
     * Retrieves `QueryData` associated with a specific transaction ID.
     *
     * @param txId The transaction ID.
     * @return The `QueryData` object, or a default empty `QueryData` if not found.
     */
    public static QueryData getQueryData(String txId) {
        return QUERY_DATA_BY_TX.getOrDefault(txId, new QueryData("", "", "", new LinkedHashMap<>()));
    }

    /**
     * Clears `QueryData` for a given transaction ID from the map.
     * Should be called after the audit record has been processed or the transaction completes.
     *
     * @param txId The transaction ID to clear.
     */
    public static void clearQueryData(String txId) {
        logger.debug("Clearing query data for txId: {}", txId);
        QUERY_DATA_BY_TX.remove(txId);
    }

    /**
     * Sets the transaction ID in the `ThreadLocal` for the current thread.
     * This allows P6Spy to associate subsequent queries with this txId.
     *
     * @param txId The transaction ID to set.
     */
    public static void setTxId(String txId) {
        TX_ID.set(txId);
        logger.debug("Set txId for current thread: {}", txId);
    }

    /**
     * Clears the transaction ID from the `ThreadLocal` for the current thread.
     * Should be called at the end of a transaction to prevent thread local leakage.
     */
    public static void clearTxId() {
        logger.debug("Clearing txId from current thread.");
        TX_ID.remove();
    }
}
