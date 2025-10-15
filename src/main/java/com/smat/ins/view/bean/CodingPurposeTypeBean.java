package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.PurposeType;
import com.smat.ins.model.service.PurposeTypeService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingPurposeTypeBean implements Serializable {

	// #region "CodingProperties"
	private static final long serialVersionUID = 4600306662046762257L;
	
	private PurposeType purposeType;

	private List<PurposeType> purposeTypeList;

	private List<PurposeType> selectedPurposeTypeList;

	

	private PurposeTypeService purposeTypeService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {

		try {
			purposeType = new PurposeType();
			purposeTypeList= new ArrayList<PurposeType>();
			selectedPurposeTypeList= new ArrayList<PurposeType>();
			purposeTypeList=purposeTypeService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}
	
	public CodingPurposeTypeBean() {
		purposeTypeService = (PurposeTypeService) BeanUtility.getBean("purposeTypeService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	}
	
	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.purposeType = new PurposeType();
	}

	public boolean doValidate() {
		boolean result = true;
		if (purposeType.getArabicName() == null || purposeType.getArabicName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				purposeTypeService.saveOrUpdate(purposeType);
				purposeTypeList=purposeTypeService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-purposeType");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-purposeType");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedPurposeType() {
		return this.selectedPurposeTypeList != null && !this.selectedPurposeTypeList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedPurposeType()) {
			int size = this.selectedPurposeTypeList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("purposeTypesSelectedNum")
					: localizationService.getInterfaceLabel().getString("onePurposeTypeSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deletePurposeType() {
		try {

			purposeTypeService.delete(purposeType);
			this.purposeType = null;
			purposeTypeList = purposeTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-purposeType");
			PrimeFaces.current().executeScript("PF('widgetVarPurposeType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-purposeType");
			e.printStackTrace();
		}
	}

	public void deleteSelectedPurposeType() {
		try {   
			purposeTypeService.delete(selectedPurposeTypeList);
			this.selectedPurposeTypeList = null;
			purposeTypeList = purposeTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-purposeType");
			PrimeFaces.current().executeScript("PF('widgetVarPurposeType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-purposeType");
			e.printStackTrace();
		}

	}

	public PurposeType getPurposeType() {
		return purposeType;
	}

	public void setPurposeType(PurposeType purposeType) {
		this.purposeType = purposeType;
	}

	public List<PurposeType> getPurposeTypeList() {
		return purposeTypeList;
	}

	public void setPurposeTypeList(List<PurposeType> purposeTypeList) {
		this.purposeTypeList = purposeTypeList;
	}

	public List<PurposeType> getSelectedPurposeTypeList() {
		return selectedPurposeTypeList;
	}

	public void setSelectedPurposeTypeList(List<PurposeType> selectedPurposeTypeList) {
		this.selectedPurposeTypeList = selectedPurposeTypeList;
	}



	
	
}
