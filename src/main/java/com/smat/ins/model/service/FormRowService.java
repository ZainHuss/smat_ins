package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.FormRow;

public interface FormRowService extends GenericService<FormRow, Integer> {
	public List<FormRow> getBy(Integer formId);

	public Integer getMaxId(Integer formId);
}
