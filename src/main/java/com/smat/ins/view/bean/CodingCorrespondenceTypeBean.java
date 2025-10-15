package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.CorrespondenceType;
import com.smat.ins.model.service.CorrespondenceTypeService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingCorrespondenceTypeBean implements Serializable {

	// #region "CodingProperties"
	private static final long serialVersionUID = 4600306662046762257L;
	
	private CorrespondenceType correspondenceType;

	private List<CorrespondenceType> correspondenceTypeList;

	private List<CorrespondenceType> selectedCorrespondenceTypeList;

	

	private CorrespondenceTypeService correspondenceTypeService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {

		try {
			correspondenceType = new CorrespondenceType();
			correspondenceTypeList= new ArrayList<CorrespondenceType>();
			selectedCorrespondenceTypeList= new ArrayList<CorrespondenceType>();
			correspondenceTypeList=correspondenceTypeService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}
	
	public CodingCorrespondenceTypeBean() {
		correspondenceTypeService = (CorrespondenceTypeService) BeanUtility.getBean("correspondenceTypeService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	}
	
	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.correspondenceType = new CorrespondenceType();
	}

	public boolean doValidate() {
		boolean result = true;
		if (correspondenceType.getArabicName() == null || correspondenceType.getArabicName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				correspondenceTypeService.saveOrUpdate(correspondenceType);
				correspondenceTypeList=correspondenceTypeService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceType");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceType");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedCorrespondenceType() {
		return this.selectedCorrespondenceTypeList != null && !this.selectedCorrespondenceTypeList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedCorrespondenceType()) {
			int size = this.selectedCorrespondenceTypeList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("correspondenceTypesSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneCorrespondenceTypeSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deleteCorrespondenceType() {
		try {

			correspondenceTypeService.delete(correspondenceType);
			this.correspondenceType = null;
			correspondenceTypeList = correspondenceTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceType");
			PrimeFaces.current().executeScript("PF('widgetVarCorrespondenceType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceType");
			e.printStackTrace();
		}
	}

	public void deleteSelectedCorrespondenceType() {
		try {   
			correspondenceTypeService.delete(selectedCorrespondenceTypeList);
			this.selectedCorrespondenceTypeList = null;
			correspondenceTypeList = correspondenceTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceType");
			PrimeFaces.current().executeScript("PF('widgetVarCorrespondenceType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceType");
			e.printStackTrace();
		}

	}

	public CorrespondenceType getCorrespondenceType() {
		return correspondenceType;
	}

	public void setCorrespondenceType(CorrespondenceType correspondenceType) {
		this.correspondenceType = correspondenceType;
	}

	public List<CorrespondenceType> getCorrespondenceTypeList() {
		return correspondenceTypeList;
	}

	public void setCorrespondenceTypeList(List<CorrespondenceType> correspondenceTypeList) {
		this.correspondenceTypeList = correspondenceTypeList;
	}

	public List<CorrespondenceType> getSelectedCorrespondenceTypeList() {
		return selectedCorrespondenceTypeList;
	}

	public void setSelectedCorrespondenceTypeList(List<CorrespondenceType> selectedCorrespondenceTypeList) {
		this.selectedCorrespondenceTypeList = selectedCorrespondenceTypeList;
	}



	
	
}
