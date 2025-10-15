package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * InspectionFormWorkflow entity. @author MyEclipse Persistence Tools
 */

@Audited
public class InspectionFormWorkflow implements java.io.Serializable {

	// Fields

	private Integer id;
	private WorkflowDefinition workflowDefinition;
	private EquipmentInspectionForm equipmentInspectionForm;
	private Task task;
	private byte[] inspectionFormDocument;
	private SysUser reviewedBy;

	// Constructors

	/** default constructor */
	public InspectionFormWorkflow() {
	}

	/** full constructor */
	public InspectionFormWorkflow(WorkflowDefinition workflowDefinition,
			EquipmentInspectionForm equipmentInspectionForm, Task task, byte[] inspectionFormDocument,
			SysUser reviewedBy) {
		this.workflowDefinition = workflowDefinition;
		this.equipmentInspectionForm = equipmentInspectionForm;
		this.task = task;
		this.inspectionFormDocument = inspectionFormDocument;
		this.reviewedBy = reviewedBy;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
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

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public byte[] getInspectionFormDocument() {
		return this.inspectionFormDocument;
	}

	public void setInspectionFormDocument(byte[] inspectionFormDocument) {
		this.inspectionFormDocument = inspectionFormDocument;
	}

	public SysUser getReviewedBy() {
		return this.reviewedBy;
	}

	public void setReviewedBy(SysUser reviewedBy) {
		this.reviewedBy = reviewedBy;
	}

}