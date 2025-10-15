package com.smat.ins.model.service.impl;


import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.dao.EmpCertificationWorkflowStepDao;
import com.smat.ins.model.entity.EmpCertificationWorkflowStep;
import com.smat.ins.model.service.EmpCertificationWorkflowStepService;

public class EmpCertificationWorkflowStepServiceImpl extends GenericServiceImpl<EmpCertificationWorkflowStep,EmpCertificationWorkflowStepDao, Integer> implements EmpCertificationWorkflowStepService {

	@Override
	public List<EmpCertificationWorkflowStep> getByInspectionForm(Integer empCertId) {
		// TODO Auto-generated method stub
		return dao.getByInspectionForm(empCertId);
	}

	@Override
	public List<EmpCertificationWorkflowStep> getByInspectionFormAndStepCode(Integer empCertId, String stepCode) {
		// TODO Auto-generated method stub
		return getByInspectionFormAndStepCode(empCertId, stepCode);
	}

	@Override
	public Short getLastStepSeq(Integer empCertId) {
		// TODO Auto-generated method stub
		return dao.getLastStepSeq(empCertId);
	}

	@Override
	public EmpCertificationWorkflowStep getLastStep(Integer empCertId) {
		// TODO Auto-generated method stub
		return dao.getLastStep(empCertId);
	}

}
