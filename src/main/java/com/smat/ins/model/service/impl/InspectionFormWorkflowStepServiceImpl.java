package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.InspectionFormWorkflowStepDao;

import com.smat.ins.model.service.InspectionFormWorkflowStepService;

import com.smat.ins.model.entity.InspectionFormWorkflowStep;

public class InspectionFormWorkflowStepServiceImpl extends
		GenericServiceImpl<InspectionFormWorkflowStep, InspectionFormWorkflowStepDao, Long> implements   InspectionFormWorkflowStepService {

	@Override
	public List<InspectionFormWorkflowStep> getByInspectionForm(Long inspectionFormId) {
		// TODO Auto-generated method stub
		return dao.getByInspectionForm(inspectionFormId);
	}

	@Override
	public List<InspectionFormWorkflowStep> getByInspectionFormAndStepCode(Long inspectionFormId, String stepCode) {
		// TODO Auto-generated method stub
		return dao.getByInspectionFormAndStepCode(inspectionFormId, stepCode);
	}

	@Override
	public Short getLastStepSeq(Long inspectionFormId) {
		// TODO Auto-generated method stub
		return dao.getLastStepSeq(inspectionFormId);
	}

	@Override
	public InspectionFormWorkflowStep getLastStep(Long inspectionFormId) {
		// TODO Auto-generated method stub
		return dao.getLastStep(inspectionFormId);
	}

}
