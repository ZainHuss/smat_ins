package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * EmpCertificationWorkflow entity. @author MyEclipse Persistence Tools
 */

@Audited
public class EmpCertificationWorkflow  implements java.io.Serializable {


    // Fields    

     private Integer id;
     private EmpCertification empCertification;
     private SysUser sysUser;
     private WorkflowDefinition workflowDefinition;
     private Task task;


    // Constructors

    /** default constructor */
    public EmpCertificationWorkflow() {
    }

	/** minimal constructor */
    public EmpCertificationWorkflow(EmpCertification empCertification, WorkflowDefinition workflowDefinition, Task task) {
        this.empCertification = empCertification;
        this.workflowDefinition = workflowDefinition;
        this.task = task;
    }
    
    /** full constructor */
    public EmpCertificationWorkflow(EmpCertification empCertification, SysUser sysUser, WorkflowDefinition workflowDefinition, Task task) {
        this.empCertification = empCertification;
        this.sysUser = sysUser;
        this.workflowDefinition = workflowDefinition;
        this.task = task;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public EmpCertification getEmpCertification() {
        return this.empCertification;
    }
    
    public void setEmpCertification(EmpCertification empCertification) {
        this.empCertification = empCertification;
    }

    public SysUser getSysUser() {
        return this.sysUser;
    }
    
    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    public WorkflowDefinition getWorkflowDefinition() {
        return this.workflowDefinition;
    }
    
    public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) {
        this.workflowDefinition = workflowDefinition;
    }

    public Task getTask() {
        return this.task;
    }
    
    public void setTask(Task task) {
        this.task = task;
    }
   








}