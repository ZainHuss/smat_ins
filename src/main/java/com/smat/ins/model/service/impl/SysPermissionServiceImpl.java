package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.SysPermission;
import com.smat.ins.model.dao.SysPermissionDao;
import com.smat.ins.model.service.SysPermissionService;


public class SysPermissionServiceImpl extends GenericServiceImpl<SysPermission, SysPermissionDao, Short>
		implements SysPermissionService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7537159344871000524L;

	@Override
	public List<SysPermission> getBySysUser(Long sysUserID) throws Exception {
		// TODO Auto-generated method stub
		return dao.getBySysUser(sysUserID);
	}

	@Override
	public List<SysPermission> getExceptInRole(Short roleId)throws Exception {
		// TODO Auto-generated method stub
		return dao.getExceptInRole(roleId);
	}

	@Override
	public List<SysPermission> getByCode(String code) throws Exception {
		// TODO Auto-generated method stub
		return dao.getByCode(code);
	}
}
