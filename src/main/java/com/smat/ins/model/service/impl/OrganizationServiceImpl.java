package com.smat.ins.model.service.impl;


import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.dao.OrganizationDao;
import com.smat.ins.model.service.OrganizationService;

public class OrganizationServiceImpl extends
		GenericServiceImpl<Organization, OrganizationDao, Long> implements   OrganizationService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7735719131255022819L;

	@Override
	public List<Organization> getByParent(Organization organization, Integer start, Integer pageSize) throws Exception{
		// TODO Auto-generated method stub
		return dao.getByParent(organization, start, pageSize);
	}

	@Override
	public Long getByParentCount(Organization organization) throws Exception{
		// TODO Auto-generated method stub
		return dao.getByParentCount(organization);
	}

	@Override
	public List<Organization> getByNameOrCode(String searchKey)throws Exception {
		// TODO Auto-generated method stub
		return dao.getByNameOrCode(searchKey);
	}

	@Override
	public List<Organization> getByNameOrCode(String arabicName, String code)throws Exception {
		// TODO Auto-generated method stub
		return dao.getByNameOrCode(arabicName, code);
	}

	@Override
	public Organization getOrganizationWithDetails(Organization organization)throws Exception {
		// TODO Auto-generated method stub
		return dao.getOrganizationWithDetails(organization);
	}

	@Override
	public List<Organization> getAllWithDetails()throws Exception {
		// TODO Auto-generated method stub
		return dao.getAllWithDetails();
	}

	@Override
	public Organization getParentOrganization(Organization organization) throws Exception{
		// TODO Auto-generated method stub
		return dao.getParentOrganization(organization);
	}

	@Override
	public Integer getMaxLevelOrgCode(Organization parentOrg) throws Exception{
		// TODO Auto-generated method stub
		return dao.getMaxLevelOrgCode(parentOrg);
	}

	@Override
	public boolean checkIfFound(Organization organization)throws Exception {
		// TODO Auto-generated method stub
		return dao.checkIfFound(organization);
	}

}
