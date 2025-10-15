package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.HierarchySystem;
import com.smat.ins.model.entity.HierarchySystemDef;

public interface HierarchySystemDefDao extends GenericDao<HierarchySystemDef, Integer> {
	public List<HierarchySystemDef> getByHierarchySystem(HierarchySystem hierarchySystem);
}
