package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.FormTemplateDao;
import com.smat.ins.model.dao.PrintedDocDao;
import com.smat.ins.model.service.FormTemplateService;

import com.smat.ins.model.entity.FormTemplate;
import com.smat.ins.model.entity.PrintedDoc;

public class FormTemplateServiceImpl extends
		GenericServiceImpl<FormTemplate, FormTemplateDao, Integer> implements   FormTemplateService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8221688880575870176L;
	
	private PrintedDocDao printedDocDao;
	
	

	public PrintedDocDao getPrintedDocDao() {
		return printedDocDao;
	}

	public void setPrintedDocDao(PrintedDocDao printedDocDao) {
		this.printedDocDao = printedDocDao;
	}

	@Override
	public Integer getMaxFormCode() {
		// TODO Auto-generated method stub
		return dao.getMaxFormCode();
	}

	@Override
	public FormTemplate getBy(Integer formId) {
		// TODO Auto-generated method stub
		return dao.getBy(formId);
	}

	@Override
	public FormTemplate getBy(String equipmentCatCode) {
		// TODO Auto-generated method stub
		return dao.getBy(equipmentCatCode);
	}

	@Override
	public FormTemplate getByTemplateName(String templateName) {
		// TODO Auto-generated method stub
		return dao.getByTemplateName(templateName);
	}

	@Override
	public Boolean checkIfUsed(Integer formId) {
		// TODO Auto-generated method stub
		return dao.checkIfUsed(formId);
	}

	@Override
	public FormTemplate saveTemplate(FormTemplate formTemplate, PrintedDoc printedDoc) throws Exception {
		// TODO Auto-generated method stub
		PrintedDoc doc=printedDocDao.insert(printedDoc);
		formTemplate.setPrintedDoc(doc);
		FormTemplate template=dao.insert(formTemplate);
		return template;
	}

	@Override
	public List<FormTemplate> getWithAllRelated() {
		// TODO Auto-generated method stub
		return dao.getWithAllRelated();
	}

}
