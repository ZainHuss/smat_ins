package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * Task entity. @author MyEclipse Persistence Tools
 */

@Audited
public class Task implements java.io.Serializable {

	// Fields

	private Integer id;
	private Company company;
	private UserAlias userAliasByAssigner;
	private SysUser sysUser;
	private EquipmentCategory equipmentCategory;
	private UserAlias userAliasByAssignTo;
	private ServiceType serviceType;
	private String taskDescription;
	private Boolean isCompleted;
	private Boolean isDone;
	private Date createdDate;
	private Date completedDate;
	private Date doneDate;
	private Set empCertificationWorkflows = new HashSet(0);
	private Set correspondenceTasks = new HashSet(0);
	private Set inspectionFormWorkflows = new HashSet(0);
	private Boolean is_active;
	// Constructors

		/** default constructor */
		public Task() {
		}

		/** minimal constructor */
		public Task(UserAlias userAliasByAssigner, SysUser sysUser, UserAlias userAliasByAssignTo, ServiceType serviceType,
				Date createdDate, Boolean is_active) {
			this.userAliasByAssigner = userAliasByAssigner;
			this.sysUser = sysUser;
			this.userAliasByAssignTo = userAliasByAssignTo;
			this.serviceType = serviceType;
			this.createdDate = createdDate;
			this.is_active=true;
		}

		/** full constructor */
		public Task(Company company, UserAlias userAliasByAssigner, SysUser sysUser, EquipmentCategory equipmentCategory,
				UserAlias userAliasByAssignTo, ServiceType serviceType, String taskDescription, Boolean isCompleted,
				Boolean isDone, Date createdDate, Date completedDate, Date doneDate, Set empCertificationWorkflows,
				Set correspondenceTasks, Set inspectionFormWorkflows , Boolean is_active) {
			this.company = company;
			this.userAliasByAssigner = userAliasByAssigner;
			this.sysUser = sysUser;
			this.equipmentCategory = equipmentCategory;
			this.userAliasByAssignTo = userAliasByAssignTo;
			this.serviceType = serviceType;
			this.taskDescription = taskDescription;
			this.isCompleted = isCompleted;
			this.isDone = isDone;
			this.createdDate = createdDate;
			this.completedDate = completedDate;
			this.doneDate = doneDate;
			this.empCertificationWorkflows = empCertificationWorkflows;
			this.correspondenceTasks = correspondenceTasks;
			this.inspectionFormWorkflows = inspectionFormWorkflows;
			this.is_active = true;
		}

		// Property accessors

		public Integer getId() {
			return this.id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Company getCompany() {
			return this.company;
		}

		public void setCompany(Company company) {
			this.company = company;
		}

		public UserAlias getUserAliasByAssigner() {
			return this.userAliasByAssigner;
		}

		public void setUserAliasByAssigner(UserAlias userAliasByAssigner) {
			this.userAliasByAssigner = userAliasByAssigner;
		}

		public SysUser getSysUser() {
			return this.sysUser;
		}

		public void setSysUser(SysUser sysUser) {
			this.sysUser = sysUser;
		}

		public EquipmentCategory getEquipmentCategory() {
			return this.equipmentCategory;
		}

		public void setEquipmentCategory(EquipmentCategory equipmentCategory) {
			this.equipmentCategory = equipmentCategory;
		}

		public UserAlias getUserAliasByAssignTo() {
			return this.userAliasByAssignTo;
		}

		public void setUserAliasByAssignTo(UserAlias userAliasByAssignTo) {
			this.userAliasByAssignTo = userAliasByAssignTo;
		}

		public ServiceType getServiceType() {
			return this.serviceType;
		}

		public void setServiceType(ServiceType serviceType) {
			this.serviceType = serviceType;
		}

		public String getTaskDescription() {
			return this.taskDescription;
		}

		public void setTaskDescription(String taskDescription) {
			this.taskDescription = taskDescription;
		}

		public Boolean getIsCompleted() {
			return this.isCompleted;
		}

		public void setIsCompleted(Boolean isCompleted) {
			this.isCompleted = isCompleted;
		}

		public Boolean getIsDone() {
			return this.isDone;
		}

		public void setIsDone(Boolean isDone) {
			this.isDone = isDone;
		}

		public Date getCreatedDate() {
			return this.createdDate;
		}

		public void setCreatedDate(Date createdDate) {
			this.createdDate = createdDate;
		}

		public Date getCompletedDate() {
			return this.completedDate;
		}

		public void setCompletedDate(Date completedDate) {
			this.completedDate = completedDate;
		}

		public Date getDoneDate() {
			return this.doneDate;
		}

		public void setDoneDate(Date doneDate) {
			this.doneDate = doneDate;
		}

		public Set getEmpCertificationWorkflows() {
			return this.empCertificationWorkflows;
		}

		public void setEmpCertificationWorkflows(Set empCertificationWorkflows) {
			this.empCertificationWorkflows = empCertificationWorkflows;
		}

		public Set getCorrespondenceTasks() {
			return this.correspondenceTasks;
		}

		public void setCorrespondenceTasks(Set correspondenceTasks) {
			this.correspondenceTasks = correspondenceTasks;
		}

		public Set getInspectionFormWorkflows() {
			return this.inspectionFormWorkflows;
		}

		public void setInspectionFormWorkflows(Set inspectionFormWorkflows) {
			this.inspectionFormWorkflows = inspectionFormWorkflows;
		}

		public Boolean getIs_active() {
			return is_active;
		}

		public void setIs_active(Boolean is_active) {
			this.is_active = is_active;
		}

		
	
}