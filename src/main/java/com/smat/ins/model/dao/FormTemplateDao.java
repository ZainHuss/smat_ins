package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.FormTemplate;


public interface FormTemplateDao extends
		GenericDao<FormTemplate, Integer> {
	public Integer getMaxFormCode();
	public FormTemplate getBy(Integer formId);
	
	public FormTemplate getBy(String equipmentCatCode);
	
	public FormTemplate getByTemplateName(String templateName);
	
	public Boolean checkIfUsed(Integer formId);
	
	public List<FormTemplate> getWithAllRelated(); 
}
