package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.FormColumn;

public interface FormColumnService extends GenericService<FormColumn, Integer> {
	public List<FormColumn> getBy(Integer rowId);

	public Integer getMaxId(Integer rowId);
}
