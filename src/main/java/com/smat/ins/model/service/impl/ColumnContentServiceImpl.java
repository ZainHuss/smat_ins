package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.ColumnContentDao;

import com.smat.ins.model.service.ColumnContentService;

import com.smat.ins.model.entity.ColumnContent;

public class ColumnContentServiceImpl extends
		GenericServiceImpl<ColumnContent, ColumnContentDao, Integer> implements   ColumnContentService {

	@Override
	public List<ColumnContent> getBy(Integer columnId) {
		// TODO Auto-generated method stub
		return dao.getBy(columnId);
	}

	@Override
	public Integer getMaxAliasNameCodeByCat(String templatePrefix) {
		// TODO Auto-generated method stub
		return dao.getMaxAliasNameCodeByCat(templatePrefix);
	}

	@Override
	public Integer getMaxId(Integer columnId) {
		// TODO Auto-generated method stub
		return dao.getMaxId(columnId);
	}

}
