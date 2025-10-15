package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.ChecklistDetailDataSource;

public interface ChecklistDetailDataSourceService extends
		GenericService<ChecklistDetailDataSource, Integer> {
	public List<ChecklistDetailDataSource> getByDataSource(String code);
	public List<ChecklistDetailDataSource> getByDataSourceId(int id);
}
