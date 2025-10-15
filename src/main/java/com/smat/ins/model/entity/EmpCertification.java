package com.smat.ins.model.entity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * EmpCertification entity. @author MyEclipse Persistence Tools
 */
@Audited
public class EmpCertification implements java.io.Serializable {

	// Fields

	private Integer id;
	private EmpCertificationType empCertificationType;
	private SysUser sysUserByReviewedBy;
	private SysUser sysUserByInspectedBy;
	private Employee employee;
	private SysUser sysUserByIssuedBy;
	private Date issueDate;
	private Date expiryDate;
	private String certNumber;
	private String tsNumber;
	private String status;
	private Timestamp createdAt;
	private Set empCertificationWorkflows = new HashSet(0);
	private Set equipments = new HashSet(0);
	private Set empCertificationWorkflowSteps = new HashSet(0);

	// Constructors

	/** default constructor */
	public EmpCertification() {
		this.employee=new Employee();
	}

	/** minimal constructor */
	public EmpCertification(Date issueDate, Date expiryDate) {
		this.issueDate = issueDate;
		this.expiryDate = expiryDate;
	}

	/** full constructor */
	public EmpCertification(EmpCertificationType empCertificationType, SysUser sysUserByReviewedBy,
			SysUser sysUserByInspectedBy, Employee employee, SysUser sysUserByIssuedBy, Date issueDate, Date expiryDate,
			String certNumber, String tsNumber, String status, Timestamp createdAt, Set empCertificationWorkflows,
			Set equipments, Set empCertificationWorkflowSteps) {
		this.empCertificationType = empCertificationType;
		this.sysUserByReviewedBy = sysUserByReviewedBy;
		this.sysUserByInspectedBy = sysUserByInspectedBy;
		this.employee = employee;
		this.sysUserByIssuedBy = sysUserByIssuedBy;
		this.issueDate = issueDate;
		this.expiryDate = expiryDate;
		this.certNumber = certNumber;
		this.tsNumber = tsNumber;
		this.status = status;
		this.createdAt = createdAt;
		this.empCertificationWorkflows = empCertificationWorkflows;
		this.equipments = equipments;
		this.empCertificationWorkflowSteps = empCertificationWorkflowSteps;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public EmpCertificationType getEmpCertificationType() {
		return this.empCertificationType;
	}

	public void setEmpCertificationType(EmpCertificationType empCertificationType) {
		this.empCertificationType = empCertificationType;
	}

	public SysUser getSysUserByReviewedBy() {
		return this.sysUserByReviewedBy;
	}

	public void setSysUserByReviewedBy(SysUser sysUserByReviewedBy) {
		this.sysUserByReviewedBy = sysUserByReviewedBy;
	}

	public SysUser getSysUserByInspectedBy() {
		return this.sysUserByInspectedBy;
	}

	public void setSysUserByInspectedBy(SysUser sysUserByInspectedBy) {
		this.sysUserByInspectedBy = sysUserByInspectedBy;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public SysUser getSysUserByIssuedBy() {
		return this.sysUserByIssuedBy;
	}

	public void setSysUserByIssuedBy(SysUser sysUserByIssuedBy) {
		this.sysUserByIssuedBy = sysUserByIssuedBy;
	}

	public Date getIssueDate() {
		return this.issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Date getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getCertNumber() {
		return this.certNumber;
	}

	public void setCertNumber(String certNumber) {
		this.certNumber = certNumber;
	}

	public String getTsNumber() {
		return this.tsNumber;
	}

	public void setTsNumber(String tsNumber) {
		this.tsNumber = tsNumber;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Set getEmpCertificationWorkflows() {
		return this.empCertificationWorkflows;
	}

	public void setEmpCertificationWorkflows(Set empCertificationWorkflows) {
		this.empCertificationWorkflows = empCertificationWorkflows;
	}

	public Set getEquipments() {
		return this.equipments;
	}

	public void setEquipments(Set equipments) {
		this.equipments = equipments;
	}

	public Set getEmpCertificationWorkflowSteps() {
		return this.empCertificationWorkflowSteps;
	}

	public void setEmpCertificationWorkflowSteps(Set empCertificationWorkflowSteps) {
		this.empCertificationWorkflowSteps = empCertificationWorkflowSteps;
	}

}