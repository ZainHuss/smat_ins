package com.smat.ins.model.entity;

import java.io.Serializable;
import java.util.Date;

public class TaskDraft implements Serializable {
    private static final long serialVersionUID = 1L;
    private String taskType;
    private Integer version = 1;

    private Integer id;
    private Integer taskId;          // nullable if draft not linked to task yet
    private Integer draftOwnerId;    // sys_user id (يفضّل long إذا عندك Long في المستخدمين)
    private String draftOwnerName;   // optional
    private byte[] draftData;
    // تخزين JSON bytes
    private Date createdDate;
    private Date updatedDate;
    private Boolean isActive = true;


    // getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }
    public Integer getDraftOwnerId() { return draftOwnerId; }
    public void setDraftOwnerId(Integer draftOwnerId) { this.draftOwnerId = draftOwnerId; }
    public String getDraftOwnerName() { return draftOwnerName; }
    public void setDraftOwnerName(String draftOwnerName) { this.draftOwnerName = draftOwnerName; }
    public byte[] getDraftData() { return draftData; }
    public void setDraftData(byte[] draftData) { this.draftData = draftData; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Date getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Date updatedDate) { this.updatedDate = updatedDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
