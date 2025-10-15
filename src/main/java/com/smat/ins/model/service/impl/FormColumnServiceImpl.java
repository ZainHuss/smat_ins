package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.FormColumnDao;

import com.smat.ins.model.service.FormColumnService;

import com.smat.ins.model.entity.FormColumn;

public class FormColumnServiceImpl extends
		GenericServiceImpl<FormColumn, FormColumnDao, Integer> implements   FormColumnService {

	@Override
	public List<FormColumn> getBy(Integer rowId) {
		// TODO Auto-generated method stub
		return dao.getBy(rowId);
	}

	@Override
	public Integer getMaxId(Integer rowId) {
		// TODO Auto-generated method stub
		return dao.getMaxId(rowId);
	}

}
