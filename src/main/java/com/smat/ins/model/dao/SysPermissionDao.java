package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.SysPermission;

public interface SysPermissionDao extends
		GenericDao<SysPermission, Short> {
	public List<SysPermission> getBySysUser(Long sysUserID);
	public List<SysPermission> getExceptInRole(Short roleId);
	public List<SysPermission> getByCode(String code);

}
