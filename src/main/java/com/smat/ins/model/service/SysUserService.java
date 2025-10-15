package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysRole;
import com.smat.ins.model.entity.SysUser;

public interface SysUserService extends GenericService<SysUser, Long> {

	public SysUser auth(String userName, String password) throws Exception;

	public List<SysRole> getExceptInUser(SysUser sysUser) throws Exception;

	public Integer getMaxUserCodeByOrg(Organization organization) throws Exception;

	public Integer getMaxUserNameCodeByRootOrg(Organization rootOrganization) throws Exception;

	public List<SysUser> getPagesByGloblFilter(Integer offset, Integer pageSize, String filterValue, String sortField,
			String sortOrder) throws Exception;

	public Long getCountByGloblFilter(String filterValue) throws Exception;

	public Boolean isUserHasPermission(Long userId, String permissionCode);
	
	public List<SysUser> listUserHasPersmission(String permissionCode);
	

}
