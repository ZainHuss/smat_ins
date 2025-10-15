package com.smat.ins.model.service;


import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.EmpCertificationWorkflow;

public interface EmpCertificationWorkflowService extends GenericService<EmpCertificationWorkflow, Integer>{
	public EmpCertificationWorkflow getCurrentInspectionFormWorkFlow(Integer empCertId);
}
