package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.InspectionFormWorkflowDao;

import com.smat.ins.model.service.InspectionFormWorkflowService;

import com.smat.ins.model.entity.InspectionFormWorkflow;

public class InspectionFormWorkflowServiceImpl extends
		GenericServiceImpl<InspectionFormWorkflow, InspectionFormWorkflowDao, Integer> implements   InspectionFormWorkflowService {

	@Override
	public InspectionFormWorkflow getCurrentInspectionFormWorkFlow(Long inspectionFormId) {
		// TODO Auto-generated method stub
		return dao.getCurrentInspectionFormWorkFlow(inspectionFormId);
	}

}
