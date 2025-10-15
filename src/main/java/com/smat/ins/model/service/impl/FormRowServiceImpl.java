package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.FormRowDao;

import com.smat.ins.model.service.FormRowService;

import com.smat.ins.model.entity.FormRow;

public class FormRowServiceImpl extends
		GenericServiceImpl<FormRow, FormRowDao, Integer> implements   FormRowService {

	@Override
	public List<FormRow> getBy(Integer formId) {
		// TODO Auto-generated method stub
		return dao.getBy(formId);
	}

	@Override
	public Integer getMaxId(Integer formId) {
		// TODO Auto-generated method stub
		return dao.getMaxId(formId);
	}

}
