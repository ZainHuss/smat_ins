package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.WorkflowDefinitionDao;

import com.smat.ins.model.service.WorkflowDefinitionService;

import com.smat.ins.model.entity.WorkflowDefinition;

public class WorkflowDefinitionServiceImpl extends
		GenericServiceImpl<WorkflowDefinition, WorkflowDefinitionDao, Integer> implements   WorkflowDefinitionService {

	@Override
	public List<WorkflowDefinition> getByWorkflow(Short workflowId) {
		// TODO Auto-generated method stub
		return dao.getByWorkflow(workflowId);
	}

	@Override
	public WorkflowDefinition getInitStep(Short workflowId) {
		// TODO Auto-generated method stub
		return dao.getInitStep(workflowId);
	}

	@Override
	public WorkflowDefinition getFinalStep(Short workflowId) {
		// TODO Auto-generated method stub
		return dao.getFinalStep(workflowId);
	}

}
