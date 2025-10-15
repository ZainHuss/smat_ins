package com.smat.ins.model.service.impl;


import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.HierarchySystem;
import com.smat.ins.model.entity.HierarchySystemDef;
import com.smat.ins.model.dao.HierarchySystemDefDao;
import com.smat.ins.model.service.HierarchySystemDefService;

public class HierarchySystemDefServiceImpl extends
		GenericServiceImpl<HierarchySystemDef, HierarchySystemDefDao, Integer> implements HierarchySystemDefService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1776202742598571458L;

	@Override
	public List<HierarchySystemDef> getByHierarchySystem(HierarchySystem hierarchySystem) throws Exception{
		// TODO Auto-generated method stub
		return dao.getByHierarchySystem(hierarchySystem);
	}

}
