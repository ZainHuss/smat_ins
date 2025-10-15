package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.PriorityType;
import com.smat.ins.model.service.PriorityTypeService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingPriorityTypeBean implements Serializable {

	// #region "CodingProperties"
	private static final long serialVersionUID = 4600306662046762257L;
	
	private PriorityType priorityType;

	private List<PriorityType> priorityTypeList;

	private List<PriorityType> selectedPriorityTypeList;

	

	private PriorityTypeService priorityTypeService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {

		try {
			priorityType = new PriorityType();
			priorityTypeList= new ArrayList<PriorityType>();
			selectedPriorityTypeList= new ArrayList<PriorityType>();
			priorityTypeList=priorityTypeService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}
	
	public CodingPriorityTypeBean() {
		priorityTypeService = (PriorityTypeService) BeanUtility.getBean("priorityTypeService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	}
	
	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.priorityType = new PriorityType();
	}

	public boolean doValidate() {
		boolean result = true;
		if (priorityType.getArabicName() == null || priorityType.getArabicName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				priorityTypeService.saveOrUpdate(priorityType);
				priorityTypeList=priorityTypeService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-priorityType");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-priorityType");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedPriorityType() {
		return this.selectedPriorityTypeList != null && !this.selectedPriorityTypeList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedPriorityType()) {
			int size = this.selectedPriorityTypeList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("priorityTypesSelectedNum")
					: localizationService.getInterfaceLabel().getString("onePriorityTypeSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deletePriorityType() {
		try {

			priorityTypeService.delete(priorityType);
			this.priorityType = null;
			priorityTypeList = priorityTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-priorityType");
			PrimeFaces.current().executeScript("PF('widgetVarPriorityType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-priorityType");
			e.printStackTrace();
		}
	}

	public void deleteSelectedPriorityType() {
		try {   
			priorityTypeService.delete(selectedPriorityTypeList);
			this.selectedPriorityTypeList = null;
			priorityTypeList = priorityTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-priorityType");
			PrimeFaces.current().executeScript("PF('widgetVarPriorityType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-priorityType");
			e.printStackTrace();
		}

	}

	public PriorityType getPriorityType() {
		return priorityType;
	}

	public void setPriorityType(PriorityType priorityType) {
		this.priorityType = priorityType;
	}

	public List<PriorityType> getPriorityTypeList() {
		return priorityTypeList;
	}

	public void setPriorityTypeList(List<PriorityType> priorityTypeList) {
		this.priorityTypeList = priorityTypeList;
	}

	public List<PriorityType> getSelectedPriorityTypeList() {
		return selectedPriorityTypeList;
	}

	public void setSelectedPriorityTypeList(List<PriorityType> selectedPriorityTypeList) {
		this.selectedPriorityTypeList = selectedPriorityTypeList;
	}



	
	
}
