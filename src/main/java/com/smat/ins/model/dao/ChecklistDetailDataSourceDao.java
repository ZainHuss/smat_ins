package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.ChecklistDetailDataSource;

public interface ChecklistDetailDataSourceDao extends GenericDao<ChecklistDetailDataSource, Integer> {
	public List<ChecklistDetailDataSource> getByDataSource(String code);
	public List<ChecklistDetailDataSource> getByDataSourceId(int id);
}
