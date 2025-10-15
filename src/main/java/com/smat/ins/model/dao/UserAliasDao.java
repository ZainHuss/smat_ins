package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.JobPosition;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.UserAlias;

public interface UserAliasDao extends
		GenericDao<UserAlias, Long> {
	public List<Organization> getListDivanBySysUser(SysUser sysUser);
	public List<UserAlias> getBySysUser(SysUser sysUser);
	
	public List<UserAlias> getAllWithDetails();
	
	public Integer getMaxUserAliasCodeByOrg(Organization organization);
	
	public List<UserAlias> getListRecipients(UserAlias userAlias);
	
}
