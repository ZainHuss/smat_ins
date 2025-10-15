package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.InspectionFormWorkflowStep;

public interface InspectionFormWorkflowStepDao extends
		GenericDao<InspectionFormWorkflowStep, Long> {
	
	public List<InspectionFormWorkflowStep> getByInspectionForm(Long inspectionFormId);
	public List<InspectionFormWorkflowStep> getByInspectionFormAndStepCode(Long inspectionFormId,String stepCode);
	
	public Short getLastStepSeq(Long inspectionFormId);
	
	public InspectionFormWorkflowStep getLastStep(Long inspectionFormId);

}
