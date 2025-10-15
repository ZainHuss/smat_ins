package com.smat.ins.model.service.impl;




import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysRole;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.dao.SysUserDao;
import com.smat.ins.model.service.SysUserService;



public class SysUserServiceImpl extends
		GenericServiceImpl<SysUser, SysUserDao, Long> implements   SysUserService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6016876780772562291L;
 

	@Override
	public SysUser auth(String userName, String password) throws Exception {
		// TODO Auto-generated method stub
		return dao.auth(userName, password);
	}

	@Override
	public List<SysRole> getExceptInUser(SysUser sysUser) throws Exception {
		// TODO Auto-generated method stub
		return dao.getExceptInUser(sysUser);
	}

	@Override
	public Integer getMaxUserCodeByOrg(Organization organization) throws Exception {
		// TODO Auto-generated method stub
		return dao.getMaxUserCodeByOrg(organization);
	}

	@Override
	public Integer getMaxUserNameCodeByRootOrg(Organization rootOrganization) throws Exception {
		// TODO Auto-generated method stub
		return dao.getMaxUserNameCodeByRootOrg(rootOrganization);
	}

	@Override
	public List<SysUser> getPagesByGloblFilter(Integer offset, Integer pageSize,String filterValue,String sortField,String sortOrder) throws Exception {
		// TODO Auto-generated method stub
		return dao.getPagesByGloblFilter(offset, pageSize, filterValue, sortField, sortOrder);
	}

	@Override
	public Long getCountByGloblFilter(String filterValue) throws Exception {
		// TODO Auto-generated method stub
		return dao.getCountByGloblFilter(filterValue);
	}

	@Override
	public Boolean isUserHasPermission(Long userId, String permissionCode) {
		// TODO Auto-generated method stub
		return dao.isUserHasPermission(userId, permissionCode);
	}

	@Override
	public List<SysUser> listUserHasPersmission(String permissionCode) {
		// TODO Auto-generated method stub
		return dao.listUserHasPersmission(permissionCode);
	}
	


}
