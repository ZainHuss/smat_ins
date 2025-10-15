package com.smat.ins.model.service;


import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.SysPermission;

public interface SysPermissionService extends
		GenericService<SysPermission, Short> {
	public List<SysPermission> getBySysUser(Long sysUserID)throws Exception;
	public List<SysPermission> getExceptInRole(Short roleId)throws Exception;
	public List<SysPermission> getByCode(String code)throws Exception;

}
