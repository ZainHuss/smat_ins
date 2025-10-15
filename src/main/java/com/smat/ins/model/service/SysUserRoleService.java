package com.smat.ins.model.service;


import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.SysRole;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.SysUserRole;

public interface SysUserRoleService extends
		GenericService<SysUserRole, Long> {
	public List<SysUserRole> getAllWithDetails()throws Exception;
	public List<SysUserRole> getRoleBySysUser(SysUser sysUser)throws Exception;
	public List<SysRole> getBySysUser(SysUser sysUser)throws Exception;
}
