package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysRole;
import com.smat.ins.model.entity.SysUser;

public interface SysUserDao extends GenericDao<SysUser, Long> {
	public SysUser auth(String userName, String password) throws Exception;

	public List<SysRole> getExceptInUser(SysUser sysUser);
	
	public Integer getMaxUserCodeByOrg(Organization organization);
	
	public Integer getMaxUserNameCodeByRootOrg(Organization rootOrganization);
	
	public List<SysUser> getPagesByGloblFilter(Integer offset, Integer pageSize,String filterValue,String sortField,String sortOrder);
	
	public Long getCountByGloblFilter(String filterValue);
	
	public Boolean isUserHasPermission(Long userId,String permissionCode);
	
	public List<SysUser> listUserHasPersmission(String permissionCode);
}
