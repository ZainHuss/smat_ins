package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.PeriodType;
import com.smat.ins.model.service.PeriodTypeService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingPeriodTypeBean implements Serializable {

	// #region "CodingProperties"
	private static final long serialVersionUID = 4600306662046762257L;
	
	private PeriodType periodType;

	private List<PeriodType> periodTypeList;

	private List<PeriodType> selectedPeriodTypeList;

	

	private PeriodTypeService periodTypeService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {

		try {
			periodType = new PeriodType();
			periodTypeList= new ArrayList<PeriodType>();
			selectedPeriodTypeList= new ArrayList<PeriodType>();
			periodTypeList=periodTypeService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}
	
	public CodingPeriodTypeBean() {
		periodTypeService = (PeriodTypeService) BeanUtility.getBean("periodTypeService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	}
	
	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.periodType = new PeriodType();
	}

	public boolean doValidate() {
		boolean result = true;
		if (periodType.getArabicName() == null || periodType.getArabicName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				periodTypeService.saveOrUpdate(periodType);
				periodTypeList=periodTypeService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-periodType");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-periodType");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedPeriodType() {
		return this.selectedPeriodTypeList != null && !this.selectedPeriodTypeList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedPeriodType()) {
			int size = this.selectedPeriodTypeList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("periodTypesSelectedNum")
					: localizationService.getInterfaceLabel().getString("onePeriodTypeSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deletePeriodType() {
		try {

			periodTypeService.delete(periodType);
			this.periodType = null;
			periodTypeList = periodTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-periodType");
			PrimeFaces.current().executeScript("PF('widgetVarPeriodType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-periodType");
			e.printStackTrace();
		}
	}

	public void deleteSelectedPeriodType() {
		try {   
			periodTypeService.delete(selectedPeriodTypeList);
			this.selectedPeriodTypeList = null;
			periodTypeList = periodTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-periodType");
			PrimeFaces.current().executeScript("PF('widgetVarPeriodType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-periodType");
			e.printStackTrace();
		}

	}

	public PeriodType getPeriodType() {
		return periodType;
	}

	public void setPeriodType(PeriodType periodType) {
		this.periodType = periodType;
	}

	public List<PeriodType> getPeriodTypeList() {
		return periodTypeList;
	}

	public void setPeriodTypeList(List<PeriodType> periodTypeList) {
		this.periodTypeList = periodTypeList;
	}

	public List<PeriodType> getSelectedPeriodTypeList() {
		return selectedPeriodTypeList;
	}

	public void setSelectedPeriodTypeList(List<PeriodType> selectedPeriodTypeList) {
		this.selectedPeriodTypeList = selectedPeriodTypeList;
	}



	
	
}
