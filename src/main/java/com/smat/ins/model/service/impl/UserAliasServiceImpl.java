package com.smat.ins.model.service.impl;


import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.dao.UserAliasDao;
import com.smat.ins.model.service.UserAliasService;

public class UserAliasServiceImpl extends
		GenericServiceImpl<UserAlias, UserAliasDao, Long> implements   UserAliasService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public List<UserAlias> getBySysUser(SysUser sysUser)throws Exception {
		// TODO Auto-generated method stub
		return dao.getBySysUser(sysUser);
	}

	@Override
	public List<UserAlias> getAllWithDetails() throws Exception{
		// TODO Auto-generated method stub
		return dao.getAllWithDetails();
	}

	@Override
	public Integer getMaxUserAliasCodeByOrg(Organization organization) throws Exception {
		// TODO Auto-generated method stub
		return dao.getMaxUserAliasCodeByOrg(organization);
	}

	@Override
	public List<Organization> getListDivanBySysUser(SysUser sysUser) throws Exception {
		// TODO Auto-generated method stub
		return dao.getListDivanBySysUser(sysUser);
	}

	@Override
	public List<UserAlias> getListRecipients(UserAlias userAlias) throws Exception {
		// TODO Auto-generated method stub
		return dao.getListRecipients(userAlias);
	}

}
