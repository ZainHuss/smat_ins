package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;

import com.smat.ins.model.entity.EquipmentInspectionFormItem;

public interface EquipmentInspectionFormItemDao extends
		GenericDao<EquipmentInspectionFormItem, Long> {
	
	public List<EquipmentInspectionFormItem> getByEquipmentForm(Long inspectionFormId);

}
