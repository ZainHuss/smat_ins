package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.EmpCertificationWorkflowStep;


public interface EmpCertificationWorkflowStepDao extends GenericDao<EmpCertificationWorkflowStep, Integer> {
	public List<EmpCertificationWorkflowStep> getByInspectionForm(Integer empCertId);
	public List<EmpCertificationWorkflowStep> getByInspectionFormAndStepCode(Integer empCertId,String stepCode);
	
	public Short getLastStepSeq(Integer empCertId);
	
	public EmpCertificationWorkflowStep getLastStep(Integer empCertId);
}
