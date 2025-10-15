package com.smat.ins.model.entity;

import java.util.Date;

import org.hibernate.envers.Audited;

@Audited
public class NotificationRecord implements java.io.Serializable {

    private Long id;
    private SysUser user;
    private Integer taskId; // references Task id (Integer in project)
    private String taskType; // "employee" or "equipment"
    private String title;
    private String message;
    private Boolean isRead = Boolean.FALSE;
    private Date createdAt = new Date();

    public NotificationRecord() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
