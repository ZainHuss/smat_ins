package com.smat.ins.model.entity;

import java.util.Date;

import org.hibernate.envers.Audited;

/**
 * InspectionFormWorkflowStep entity. @author MyEclipse Persistence Tools
 */

@Audited
public class InspectionFormWorkflowStep implements java.io.Serializable {

	// Fields

	private Long id;
	private WorkflowDefinition workflowDefinition;
	private EquipmentInspectionForm equipmentInspectionForm;
	private SysUser sysUser;
	private byte[] inspectionFormDocument;
	private String sysUserComment;
	private Date processDate;
	private Short stepSeq;

	// Constructors

	/** default constructor */
	public InspectionFormWorkflowStep() {
	}

	/** full constructor */
	public InspectionFormWorkflowStep(WorkflowDefinition workflowDefinition,
			EquipmentInspectionForm equipmentInspectionForm, SysUser sysUser, byte[] inspectionFormDocument,
			String sysUserComment, Date processDate,Short stepSeq) {
		this.workflowDefinition = workflowDefinition;
		this.equipmentInspectionForm = equipmentInspectionForm;
		this.sysUser = sysUser;
		this.inspectionFormDocument = inspectionFormDocument;
		this.sysUserComment = sysUserComment;
		this.processDate = processDate;
		this.stepSeq=stepSeq;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public WorkflowDefinition getWorkflowDefinition() {
		return this.workflowDefinition;
	}

	public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) {
		this.workflowDefinition = workflowDefinition;
	}

	public EquipmentInspectionForm getEquipmentInspectionForm() {
		return this.equipmentInspectionForm;
	}

	public void setEquipmentInspectionForm(EquipmentInspectionForm equipmentInspectionForm) {
		this.equipmentInspectionForm = equipmentInspectionForm;
	}

	public SysUser getSysUser() {
		return this.sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public byte[] getInspectionFormDocument() {
		return this.inspectionFormDocument;
	}

	public void setInspectionFormDocument(byte[] inspectionFormDocument) {
		this.inspectionFormDocument = inspectionFormDocument;
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
		return stepSeq;
	}

	public void setStepSeq(Short stepSeq) {
		this.stepSeq = stepSeq;
	}
	
}