package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.GeneralEquipmentItem;

public interface GeneralEquipmentItemService extends
		GenericService<GeneralEquipmentItem, Integer> {
	public Integer getMaxGeneralEquipmentCode();
}
