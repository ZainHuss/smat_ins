package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.FormRow;

public interface FormRowDao extends GenericDao<FormRow, Integer> {

	public List<FormRow> getBy(Integer formId);
	
	public Integer getMaxId(Integer formId);

}
