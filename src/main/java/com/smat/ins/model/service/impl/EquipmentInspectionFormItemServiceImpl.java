package com.smat.ins.model.service.impl;



import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.EquipmentInspectionFormItemDao;

import com.smat.ins.model.service.EquipmentInspectionFormItemService;

import com.smat.ins.model.entity.EquipmentInspectionFormItem;

public class EquipmentInspectionFormItemServiceImpl extends
		GenericServiceImpl<EquipmentInspectionFormItem, EquipmentInspectionFormItemDao, Long> implements   EquipmentInspectionFormItemService {

	@Override
	public List<EquipmentInspectionFormItem> getByEquipmentForm(Long inspectionFormId) {
		// TODO Auto-generated method stub
		return dao.getByEquipmentForm(inspectionFormId);
	}

}
