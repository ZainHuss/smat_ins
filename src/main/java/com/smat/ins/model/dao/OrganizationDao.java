package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.Organization;

public interface OrganizationDao extends
		GenericDao<Organization, Long> {
	public List<Organization> getByParent(Organization organization,Integer start, Integer pageSize);
	public Long getByParentCount(Organization organization);
	public List<Organization> getByNameOrCode(String searchKey);
	public List<Organization> getByNameOrCode(String arabicName, String code);
	public Organization getOrganizationWithDetails(Organization organization);
	public List<Organization> getAllWithDetails();
	
	public Organization getParentOrganization(Organization organization);
	
	public Integer getMaxLevelOrgCode(Organization parentOrg);
	
	public boolean checkIfFound(Organization organization);
}
