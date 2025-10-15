package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.UserAlias;

public interface UserAliasService extends GenericService<UserAlias, Long> {
	public List<UserAlias> getBySysUser(SysUser sysUser) throws Exception;

	public List<UserAlias> getAllWithDetails()throws Exception;

	public Integer getMaxUserAliasCodeByOrg(Organization organization)throws Exception;
	
	public List<Organization> getListDivanBySysUser(SysUser sysUser)throws Exception;
	
	public List<UserAlias> getListRecipients(UserAlias userAlias)throws Exception;
}
