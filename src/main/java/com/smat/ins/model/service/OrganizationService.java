package com.smat.ins.model.service;


import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.Organization;

public interface OrganizationService extends
		GenericService<Organization, Long> {
	public List<Organization> getByParent(Organization organization,Integer start, Integer pageSize)throws Exception;
	public Long getByParentCount(Organization organization)throws Exception;
	public List<Organization> getByNameOrCode(String searchKey)throws Exception;
	public List<Organization> getByNameOrCode(String arabicName, String code)throws Exception;
	public Organization getOrganizationWithDetails(Organization organization)throws Exception;
	public List<Organization> getAllWithDetails()throws Exception;
	
	public Organization getParentOrganization(Organization organization)throws Exception;
	
	public Integer getMaxLevelOrgCode(Organization parentOrg)throws Exception;
	public boolean checkIfFound(Organization organization)throws Exception;
}
