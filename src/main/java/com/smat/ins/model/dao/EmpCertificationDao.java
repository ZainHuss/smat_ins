package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.EmpCertification;


public interface EmpCertificationDao extends GenericDao<EmpCertification, Integer> {
	
	public Integer getMaxCertNo();

	public Integer getMaxTimeSheetNo();
	
	public List<EmpCertification> getForReview();
	
	public EmpCertification getBy(Integer taskId);
	
	public EmpCertification findBy(Integer certId);
	
	public EmpCertification getByCertNumberAndTsNumber(String certNumber, String tsNumber);

}
