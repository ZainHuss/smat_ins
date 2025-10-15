package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.SysRolePermission;
import com.smat.ins.model.dao.SysRolePermissionDao;
import com.smat.ins.model.service.SysRolePermissionService;

public class SysRolePermissionServiceImpl extends
		GenericServiceImpl<SysRolePermission, SysRolePermissionDao, Long> implements   SysRolePermissionService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public List<SysRolePermission> getBySysRoleWithDetails(Short sysRoleID) throws Exception {
		// TODO Auto-generated method stub
		return dao.getBySysRoleWithDetails(sysRoleID);
	}

}
