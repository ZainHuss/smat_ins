package com.smat.ins.model.dao;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.Company;

public interface CompanyDao extends
		GenericDao<Company, Integer> {

	public Integer getMaxCompanyCode();
}
