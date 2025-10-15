package com.smat.ins.model.service.impl;


import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.SysRole;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.SysUserRole;
import com.smat.ins.model.dao.SysUserRoleDao;
import com.smat.ins.model.service.SysUserRoleService;

public class SysUserRoleServiceImpl extends
		GenericServiceImpl<SysUserRole, SysUserRoleDao, Long> implements   SysUserRoleService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2070198675674264375L;

	@Override
	public List<SysUserRole> getAllWithDetails() throws Exception{
		// TODO Auto-generated method stub
		return dao.getAllWithDetails();
	}

	@Override
	public List<SysUserRole> getRoleBySysUser(SysUser sysUser) throws Exception {
		// TODO Auto-generated method stub
		return dao.getRoleBySysUser(sysUser);
	}

	@Override
	public List<SysRole> getBySysUser(SysUser sysUser) throws Exception {
		// TODO Auto-generated method stub
		return dao.getBySysUser(sysUser);
	}

	

}
