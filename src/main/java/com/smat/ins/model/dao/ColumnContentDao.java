package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.ColumnContent;


public interface ColumnContentDao extends
		GenericDao<ColumnContent, Integer> {
	public List<ColumnContent> getBy(Integer columnId);
	
	public Integer getMaxAliasNameCodeByCat(String templatePrefix);
	
	public Integer getMaxId(Integer columnId);
}
