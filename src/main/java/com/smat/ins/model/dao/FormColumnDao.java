package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.FormColumn;

public interface FormColumnDao extends GenericDao<FormColumn, Integer> {
	public List<FormColumn> getBy(Integer rowId);

	public Integer getMaxId(Integer rowId);
}
