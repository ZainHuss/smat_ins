package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.InspectionFormWorkflowStep;

public interface InspectionFormWorkflowStepService extends GenericService<InspectionFormWorkflowStep, Long> {
	public List<InspectionFormWorkflowStep> getByInspectionForm(Long inspectionFormId);

	public List<InspectionFormWorkflowStep> getByInspectionFormAndStepCode(Long inspectionFormId, String stepCode);

	public Short getLastStepSeq(Long inspectionFormId);

	public InspectionFormWorkflowStep getLastStep(Long inspectionFormId);
}
