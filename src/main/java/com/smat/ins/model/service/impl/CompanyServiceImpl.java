package com.smat.ins.model.service.impl;



import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.CompanyDao;

import com.smat.ins.model.service.CompanyService;

import com.smat.ins.model.entity.Company;

public class CompanyServiceImpl extends
		GenericServiceImpl<Company, CompanyDao, Integer> implements   CompanyService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -859809524019849322L;

	@Override
	public Integer getMaxCompanyCode() {
		// TODO Auto-generated method stub
		return dao.getMaxCompanyCode();
	}

}
