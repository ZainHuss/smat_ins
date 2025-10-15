package com.smat.ins.model.dao;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.EmpCertificationWorkflow;
import com.smat.ins.model.entity.InspectionFormWorkflow;

public interface EmpCertificationWorkflowDao extends GenericDao<EmpCertificationWorkflow, Integer>{
	public EmpCertificationWorkflow getCurrentInspectionFormWorkFlow(Integer empCertId);
}
