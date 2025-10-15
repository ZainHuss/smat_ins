package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.WorkflowDefinition;

public interface WorkflowDefinitionService extends
		GenericService<WorkflowDefinition, Integer> {
	public List<WorkflowDefinition> getByWorkflow(Short workflowId);
	public WorkflowDefinition getInitStep(Short workflowId);
	public WorkflowDefinition getFinalStep(Short workflowId);
}
