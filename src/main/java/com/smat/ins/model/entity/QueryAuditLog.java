package com.smat.ins.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;

/**
 * JPA Entity for storing query audit logs.
 * This entity captures details about executed database queries,
 * including query text, parameters, execution time, user information,
 * and client details, for auditing and analysis purposes.
 */
@Entity
@Table(name = "query_audit_log", indexes = {
    @Index(name = "idx_query_audit_entity", columnList = "entity_name"),
    @Index(name = "idx_query_audit_time", columnList = "execution_time"),
    @Index(name = "idx_query_audit_user", columnList = "username"),
    @Index(name = "idx_query_audit_txid", columnList = "transaction_id")
})
public class QueryAuditLog implements Serializable {
    
    // Updated serialVersionUID after field rename
    private static final long serialVersionUID = 3962556480994261708L; // Incremented serialVersionUID

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "audit_uuid", nullable = false, unique = true, length = 36)
    private String auditUuId; 
    
    @Column(name = "transaction_id", nullable = false, length = 36)
    private String txId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String query;
    
    @Column(columnDefinition = "TEXT")
    private String parameters;
    
    @Column(name = "entity_name", length = 255)
    private String entityName;
    
    @Column(name = "execution_time", nullable = false)
    private LocalDateTime executionTime;
    
    @Column(name = "username", length = 100)
    private String username;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "execution_duration_ms")
    private Long executionDuration;
    
    @Column(name = "application_name", length = 100)
    private String applicationName;

    @Column(name = "session_id", length = 255)
    private String sessionId;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "param_extraction_successful")
    private Boolean paramExtractionSuccessful;

    // --- Getters and setters for all fields ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Renamed getter and setter for auditUuId
    public String getAuditUuId() { return auditUuId; }
    public void setAuditUuId(String auditUuId) { this.auditUuId = auditUuId; }

    public String getTxId() { return txId; }
    public void setTxId(String txId) { this.txId = txId; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public LocalDateTime getExecutionTime() { return executionTime; }
    public void setExecutionTime(LocalDateTime executionTime) { this.executionTime = executionTime; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Long getExecutionDuration() { return executionDuration; }
    public void setExecutionDuration(Long executionDuration) { this.executionDuration = executionDuration; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Boolean getParamExtractionSuccessful() { return paramExtractionSuccessful; }
    public void setParamExtractionSuccessful(Boolean paramExtractionSuccessful) { this.paramExtractionSuccessful = paramExtractionSuccessful; }
}
