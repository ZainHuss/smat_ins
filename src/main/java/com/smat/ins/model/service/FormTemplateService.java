package com.smat.ins.model.service;


import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.FormTemplate;
import com.smat.ins.model.entity.PrintedDoc;

public interface FormTemplateService extends GenericService<FormTemplate, Integer> {
	public Integer getMaxFormCode();

	public FormTemplate getBy(Integer formId);
	
	public FormTemplate getBy(String equipmentCatCode);
	
	public FormTemplate getByTemplateName(String templateName);
	
	public Boolean checkIfUsed(Integer formId);
	
	public FormTemplate saveTemplate(FormTemplate formTemplate,PrintedDoc printedDoc) throws Exception;
	
	public List<FormTemplate> getWithAllRelated();
}
