package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.WorkflowDefinition;

public interface WorkflowDefinitionDao extends
		GenericDao<WorkflowDefinition, Integer> {
	
	public List<WorkflowDefinition> getByWorkflow(Short workflowId);
	public WorkflowDefinition getInitStep(Short workflowId);
	
	public WorkflowDefinition getFinalStep(Short workflowId);

}
