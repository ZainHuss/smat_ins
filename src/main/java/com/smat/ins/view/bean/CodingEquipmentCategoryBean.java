package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.EquipmentCategory;
import com.smat.ins.model.service.EquipmentCategoryService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingEquipmentCategoryBean implements Serializable {

	// #region "CodingProperties"
	private static final long serialVersionUID = 4600306662046762257L;
	
	private EquipmentCategory equipmentCategory;

	private List<EquipmentCategory> equipmentCategoryList;

	private List<EquipmentCategory> selectedEquipmentCategoryList;

	

	private EquipmentCategoryService equipmentCategoryService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {

		try {
			equipmentCategory = new EquipmentCategory();
			equipmentCategoryList= new ArrayList<EquipmentCategory>();
			selectedEquipmentCategoryList= new ArrayList<EquipmentCategory>();
			equipmentCategoryList=equipmentCategoryService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}
	
	public CodingEquipmentCategoryBean() {
		equipmentCategoryService = (EquipmentCategoryService) BeanUtility.getBean("equipmentCategoryService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	}
	
	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.equipmentCategory = new EquipmentCategory();
	}

	public boolean doValidate() {
		boolean result = true;
		if (equipmentCategory.getArabicName() == null || equipmentCategory.getArabicName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				equipmentCategoryService.saveOrUpdate(equipmentCategory);
				equipmentCategoryList=equipmentCategoryService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-equipmentCategory");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-equipmentCategory");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedEquipmentCategory() {
		return this.selectedEquipmentCategoryList != null && !this.selectedEquipmentCategoryList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedEquipmentCategory()) {
			int size = this.selectedEquipmentCategoryList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("equipmentCategoriesSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneEquipmentCategorySelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deleteEquipmentCategory() {
		try {

			equipmentCategoryService.delete(equipmentCategory);
			this.equipmentCategory = null;
			equipmentCategoryList = equipmentCategoryService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-equipmentCategory");
			PrimeFaces.current().executeScript("PF('widgetVarEquipmentCategory').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-equipmentCategory");
			e.printStackTrace();
		}
	}

	public void deleteSelectedEquipmentCategory() {
		try {   
			equipmentCategoryService.delete(selectedEquipmentCategoryList);
			this.selectedEquipmentCategoryList = null;
			equipmentCategoryList = equipmentCategoryService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-equipmentCategory");
			PrimeFaces.current().executeScript("PF('widgetVarEquipmentCategory').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-equipmentCategory");
			e.printStackTrace();
		}

	}

	public EquipmentCategory getEquipmentCategory() {
		return equipmentCategory;
	}

	public void setEquipmentCategory(EquipmentCategory equipmentCategory) {
		this.equipmentCategory = equipmentCategory;
	}

	public List<EquipmentCategory> getEquipmentCategoryList() {
		return equipmentCategoryList;
	}

	public void setEquipmentCategoryList(List<EquipmentCategory> equipmentCategoryList) {
		this.equipmentCategoryList = equipmentCategoryList;
	}

	public List<EquipmentCategory> getSelectedEquipmentCategoryList() {
		return selectedEquipmentCategoryList;
	}

	public void setSelectedEquipmentCategoryList(List<EquipmentCategory> selectedEquipmentCategoryList) {
		this.selectedEquipmentCategoryList = selectedEquipmentCategoryList;
	}

}
