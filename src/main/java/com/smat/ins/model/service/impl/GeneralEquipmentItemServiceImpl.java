package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.GeneralEquipmentItemDao;

import com.smat.ins.model.service.GeneralEquipmentItemService;

import com.smat.ins.model.entity.GeneralEquipmentItem;

public class GeneralEquipmentItemServiceImpl extends
		GenericServiceImpl<GeneralEquipmentItem, GeneralEquipmentItemDao, Integer> implements   GeneralEquipmentItemService {

	@Override
	public Integer getMaxGeneralEquipmentCode() {
		// TODO Auto-generated method stub
		return dao.getMaxGeneralEquipmentCode();
	}

}
