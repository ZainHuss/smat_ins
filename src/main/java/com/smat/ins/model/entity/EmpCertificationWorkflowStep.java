package com.smat.ins.model.entity;

import java.util.Date;

import org.hibernate.envers.Audited;


/**
 * EmpCertificationWorkflowStep entity. @author MyEclipse Persistence Tools
 */

@Audited
public class EmpCertificationWorkflowStep  implements java.io.Serializable {


    // Fields    

     private Long id;
     private EmpCertification empCertification;
     private WorkflowDefinition workflowDefinition;
     private SysUser sysUser;
     private String sysUserComment;
     private Date processDate;
     private Short stepSeq;


    // Constructors

    /** default constructor */
    public EmpCertificationWorkflowStep() {
    }

    
    /** full constructor */
    public EmpCertificationWorkflowStep(EmpCertification empCertification, WorkflowDefinition workflowDefinition, SysUser sysUser, String sysUserComment, Date processDate, Short stepSeq) {
        this.empCertification = empCertification;
        this.workflowDefinition = workflowDefinition;
        this.sysUser = sysUser;
        this.sysUserComment = sysUserComment;
        this.processDate = processDate;
        this.stepSeq = stepSeq;
    }

   
    // Property accessors

    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public EmpCertification getEmpCertification() {
        return this.empCertification;
    }
    
    public void setEmpCertification(EmpCertification empCertification) {
        this.empCertification = empCertification;
    }

    public WorkflowDefinition getWorkflowDefinition() {
        return this.workflowDefinition;
    }
    
    public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) {
        this.workflowDefinition = workflowDefinition;
    }

    public SysUser getSysUser() {
        return this.sysUser;
    }
    
    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    public String getSysUserComment() {
        return this.sysUserComment;
    }
    
    public void setSysUserComment(String sysUserComment) {
        this.sysUserComment = sysUserComment;
    }

    public Date getProcessDate() {
        return this.processDate;
    }
    
    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Short getStepSeq() {
        return this.stepSeq;
    }
    
    public void setStepSeq(Short stepSeq) {
        this.stepSeq = stepSeq;
    }
   








}