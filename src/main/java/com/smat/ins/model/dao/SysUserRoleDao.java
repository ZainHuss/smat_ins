package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.SysRole;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.SysUserRole;

public interface SysUserRoleDao extends GenericDao<SysUserRole, Long> {
	public List<SysUserRole> getAllWithDetails();

	public List<SysUserRole> getRoleBySysUser(SysUser sysUser);

	public List<SysRole> getBySysUser(SysUser sysUser);
}
