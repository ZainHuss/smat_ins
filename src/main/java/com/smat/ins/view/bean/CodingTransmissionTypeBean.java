package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.TransmissionType;
import com.smat.ins.model.service.TransmissionTypeService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingTransmissionTypeBean implements Serializable {

	// #region "CodingProperties"
	private static final long serialVersionUID = 4600306662046762257L;
	
	private TransmissionType transmissionType;

	private List<TransmissionType> transmissionTypeList;

	private List<TransmissionType> selectedTransmissionTypeList;

	

	private TransmissionTypeService transmissionTypeService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {

		try {
			transmissionType = new TransmissionType();
			transmissionTypeList= new ArrayList<TransmissionType>();
			selectedTransmissionTypeList= new ArrayList<TransmissionType>();
			transmissionTypeList=transmissionTypeService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}
	
	public CodingTransmissionTypeBean() {
		transmissionTypeService = (TransmissionTypeService) BeanUtility.getBean("transmissionTypeService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	}
	
	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.transmissionType = new TransmissionType();
	}

	public boolean doValidate() {
		boolean result = true;
		if (transmissionType.getArabicName() == null || transmissionType.getArabicName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				transmissionTypeService.saveOrUpdate(transmissionType);
				transmissionTypeList=transmissionTypeService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-transmissionType");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-transmissionType");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedTransmissionType() {
		return this.selectedTransmissionTypeList != null && !this.selectedTransmissionTypeList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedTransmissionType()) {
			int size = this.selectedTransmissionTypeList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("transmissionTypesSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneTransmissionTypeSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deleteTransmissionType() {
		try {

			transmissionTypeService.delete(transmissionType);
			this.transmissionType = null;
			transmissionTypeList = transmissionTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-transmissionType");
			PrimeFaces.current().executeScript("PF('widgetVarTransmissionType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-transmissionType");
			e.printStackTrace();
		}
	}

	public void deleteSelectedTransmissionType() {
		try {   
			transmissionTypeService.delete(selectedTransmissionTypeList);
			this.selectedTransmissionTypeList = null;
			transmissionTypeList = transmissionTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-transmissionType");
			PrimeFaces.current().executeScript("PF('widgetVarTransmissionType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-transmissionType");
			e.printStackTrace();
		}

	}

	public TransmissionType getTransmissionType() {
		return transmissionType;
	}

	public void setTransmissionType(TransmissionType transmissionType) {
		this.transmissionType = transmissionType;
	}

	public List<TransmissionType> getTransmissionTypeList() {
		return transmissionTypeList;
	}

	public void setTransmissionTypeList(List<TransmissionType> transmissionTypeList) {
		this.transmissionTypeList = transmissionTypeList;
	}

	public List<TransmissionType> getSelectedTransmissionTypeList() {
		return selectedTransmissionTypeList;
	}

	public void setSelectedTransmissionTypeList(List<TransmissionType> selectedTransmissionTypeList) {
		this.selectedTransmissionTypeList = selectedTransmissionTypeList;
	}



	
	
}
