package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.ColumnContent;

public interface ColumnContentService extends GenericService<ColumnContent, Integer> {
	public List<ColumnContent> getBy(Integer columnId);

	public Integer getMaxAliasNameCodeByCat(String templatePrefix);
	
	public Integer getMaxId(Integer columnId);
}
