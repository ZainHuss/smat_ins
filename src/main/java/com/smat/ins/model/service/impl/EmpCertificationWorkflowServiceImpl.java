package com.smat.ins.model.service.impl;


import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.dao.EmpCertificationWorkflowDao;
import com.smat.ins.model.entity.EmpCertificationWorkflow;
import com.smat.ins.model.service.EmpCertificationWorkflowService;

public class EmpCertificationWorkflowServiceImpl extends GenericServiceImpl<EmpCertificationWorkflow,EmpCertificationWorkflowDao, Integer> implements EmpCertificationWorkflowService{

	@Override
	public EmpCertificationWorkflow getCurrentInspectionFormWorkFlow(Integer empCertId) {
		// TODO Auto-generated method stub
		return dao.getCurrentInspectionFormWorkFlow(empCertId);
	}

}
