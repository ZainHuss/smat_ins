package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.EquipmentCategoryDao;

import com.smat.ins.model.service.EquipmentCategoryService;

import com.smat.ins.model.entity.EquipmentCategory;

public class EquipmentCategoryServiceImpl extends
		GenericServiceImpl<EquipmentCategory, EquipmentCategoryDao, Short> implements   EquipmentCategoryService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2182663931097235633L;

	@Override
	public List<EquipmentCategory> getCatWithTemplateCreated() {
		// TODO Auto-generated method stub
		return dao.getCatWithTemplateCreated();
	}

}
