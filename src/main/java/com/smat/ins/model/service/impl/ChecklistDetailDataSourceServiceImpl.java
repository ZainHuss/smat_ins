package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.ChecklistDetailDataSourceDao;

import com.smat.ins.model.service.ChecklistDetailDataSourceService;

import com.smat.ins.model.entity.ChecklistDetailDataSource;

public class ChecklistDetailDataSourceServiceImpl extends
		GenericServiceImpl<ChecklistDetailDataSource, ChecklistDetailDataSourceDao, Integer> implements   ChecklistDetailDataSourceService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3108443955818277208L;

	@Override
	public List<ChecklistDetailDataSource> getByDataSource(String code) {
		// TODO Auto-generated method stub
		return dao.getByDataSource(code);
	}
	@Override
	public List<ChecklistDetailDataSource> getByDataSourceId(int id){
		return dao.getByDataSourceId(id);
	}
}
