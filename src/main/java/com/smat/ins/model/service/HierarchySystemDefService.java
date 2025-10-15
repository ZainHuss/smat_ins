package com.smat.ins.model.service;


import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.HierarchySystem;
import com.smat.ins.model.entity.HierarchySystemDef;

public interface HierarchySystemDefService extends GenericService<HierarchySystemDef, Integer> {
	public List<HierarchySystemDef> getByHierarchySystem(HierarchySystem hierarchySystem)throws Exception;
}
