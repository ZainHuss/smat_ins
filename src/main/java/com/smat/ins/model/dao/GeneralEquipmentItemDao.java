package com.smat.ins.model.dao;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.GeneralEquipmentItem;

public interface GeneralEquipmentItemDao extends
		GenericDao<GeneralEquipmentItem, Integer> {
	
	public Integer getMaxGeneralEquipmentCode();

}
