package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * WorkflowDefinition entity. @author MyEclipse Persistence Tools
 */
@Audited
public class WorkflowDefinition implements java.io.Serializable {

	// Fields

	private Integer id;
	private Step step;
	private Workflow workflow;
	private WorkflowDefinition workflowDefinitionByPrevious;
	private WorkflowDefinition workflowDefinitionByNext;
	private Boolean initialStep;
	private Boolean finalStep;
	private Set inspectionFormWorkflowSteps = new HashSet(0);
	private Set workflowDefinitionsForPrevious = new HashSet(0);
	private Set workflowDefinitionsForNext = new HashSet(0);
	private Set inspectionFormWorkflows = new HashSet(0);

	// Constructors

	/** default constructor */
	public WorkflowDefinition() {
	}

	/** full constructor */
	public WorkflowDefinition(Step step, Workflow workflow, WorkflowDefinition workflowDefinitionByPrevious,
			WorkflowDefinition workflowDefinitionByNext, Boolean initialStep, Boolean finalStep,
			Set inspectionFormWorkflowSteps, Set workflowDefinitionsForPrevious, Set workflowDefinitionsForNext,
			Set inspectionFormWorkflows) {
		this.step = step;
		this.workflow = workflow;
		this.workflowDefinitionByPrevious = workflowDefinitionByPrevious;
		this.workflowDefinitionByNext = workflowDefinitionByNext;
		this.initialStep = initialStep;
		this.finalStep = finalStep;
		this.inspectionFormWorkflowSteps = inspectionFormWorkflowSteps;
		this.workflowDefinitionsForPrevious = workflowDefinitionsForPrevious;
		this.workflowDefinitionsForNext = workflowDefinitionsForNext;
		this.inspectionFormWorkflows = inspectionFormWorkflows;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Step getStep() {
		return this.step;
	}

	public void setStep(Step step) {
		this.step = step;
	}

	public Workflow getWorkflow() {
		return this.workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public WorkflowDefinition getWorkflowDefinitionByPrevious() {
		return this.workflowDefinitionByPrevious;
	}

	public void setWorkflowDefinitionByPrevious(WorkflowDefinition workflowDefinitionByPrevious) {
		this.workflowDefinitionByPrevious = workflowDefinitionByPrevious;
	}

	public WorkflowDefinition getWorkflowDefinitionByNext() {
		return this.workflowDefinitionByNext;
	}

	public void setWorkflowDefinitionByNext(WorkflowDefinition workflowDefinitionByNext) {
		this.workflowDefinitionByNext = workflowDefinitionByNext;
	}

	public Boolean getInitialStep() {
		return this.initialStep;
	}

	public void setInitialStep(Boolean initialStep) {
		this.initialStep = initialStep;
	}

	public Boolean getFinalStep() {
		return this.finalStep;
	}

	public void setFinalStep(Boolean finalStep) {
		this.finalStep = finalStep;
	}

	public Set getInspectionFormWorkflowSteps() {
		return this.inspectionFormWorkflowSteps;
	}

	public void setInspectionFormWorkflowSteps(Set inspectionFormWorkflowSteps) {
		this.inspectionFormWorkflowSteps = inspectionFormWorkflowSteps;
	}

	public Set getWorkflowDefinitionsForPrevious() {
		return this.workflowDefinitionsForPrevious;
	}

	public void setWorkflowDefinitionsForPrevious(Set workflowDefinitionsForPrevious) {
		this.workflowDefinitionsForPrevious = workflowDefinitionsForPrevious;
	}

	public Set getWorkflowDefinitionsForNext() {
		return this.workflowDefinitionsForNext;
	}

	public void setWorkflowDefinitionsForNext(Set workflowDefinitionsForNext) {
		this.workflowDefinitionsForNext = workflowDefinitionsForNext;
	}

	public Set getInspectionFormWorkflows() {
		return this.inspectionFormWorkflows;
	}

	public void setInspectionFormWorkflows(Set inspectionFormWorkflows) {
		this.inspectionFormWorkflows = inspectionFormWorkflows;
	}

}