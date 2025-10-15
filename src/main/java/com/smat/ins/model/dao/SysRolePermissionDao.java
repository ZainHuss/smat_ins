package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.SysRolePermission;

public interface SysRolePermissionDao extends
		GenericDao<SysRolePermission, Long> {
	public List<SysRolePermission> getBySysRoleWithDetails(Short sysRoleID);
	
}
