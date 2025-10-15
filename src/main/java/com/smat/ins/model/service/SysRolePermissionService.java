package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.SysRolePermission;

public interface SysRolePermissionService extends
		GenericService<SysRolePermission, Long> {
	public List<SysRolePermission> getBySysRoleWithDetails(Short sysRoleID)throws Exception;
}
