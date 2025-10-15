package com.smat.ins.model.service;


import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.EmpCertificationWorkflowStep;

public interface EmpCertificationWorkflowStepService extends GenericService<EmpCertificationWorkflowStep, Integer> {
	public List<EmpCertificationWorkflowStep> getByInspectionForm(Integer empCertId);
	public List<EmpCertificationWorkflowStep> getByInspectionFormAndStepCode(Integer empCertId,String stepCode);
	
	public Short getLastStepSeq(Integer empCertId);
	
	public EmpCertificationWorkflowStep getLastStep(Integer empCertId);
}
