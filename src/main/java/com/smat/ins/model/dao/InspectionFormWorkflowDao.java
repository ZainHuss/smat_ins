package com.smat.ins.model.dao;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.InspectionFormWorkflow;

public interface InspectionFormWorkflowDao extends
		GenericDao<InspectionFormWorkflow, Integer> {

	public InspectionFormWorkflow getCurrentInspectionFormWorkFlow(Long inspectionFormId);
	
}
