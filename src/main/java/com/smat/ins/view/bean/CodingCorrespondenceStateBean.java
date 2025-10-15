package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.service.CorrespondenceStateService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingCorrespondenceStateBean implements Serializable {

	// #region "CodingProperties"
	private static final long serialVersionUID = 4600306662046762257L;
	
	private CorrespondenceState correspondenceState;

	private List<CorrespondenceState> correspondenceStateList;

	private List<CorrespondenceState> selectedCorrespondenceStateList;

	

	private CorrespondenceStateService correspondenceStateService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {

		try {
			correspondenceState = new CorrespondenceState();
			correspondenceStateList= new ArrayList<CorrespondenceState>();
			selectedCorrespondenceStateList= new ArrayList<CorrespondenceState>();
			correspondenceStateList=correspondenceStateService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}
	
	public CodingCorrespondenceStateBean() {
		correspondenceStateService = (CorrespondenceStateService) BeanUtility.getBean("correspondenceStateService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	}
	
	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.correspondenceState = new CorrespondenceState();
	}

	public boolean doValidate() {
		boolean result = true;
		if (correspondenceState.getArabicName() == null || correspondenceState.getArabicName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				correspondenceStateService.saveOrUpdate(correspondenceState);
				correspondenceStateList=correspondenceStateService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceState");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceState");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedCorrespondenceState() {
		return this.selectedCorrespondenceStateList != null && !this.selectedCorrespondenceStateList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedCorrespondenceState()) {
			int size = this.selectedCorrespondenceStateList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("correspondenceStatesSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneCorrespondenceStateSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deleteCorrespondenceState() {
		try {

			correspondenceStateService.delete(correspondenceState);
			this.correspondenceState = null;
			correspondenceStateList = correspondenceStateService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceState");
			PrimeFaces.current().executeScript("PF('widgetVarCorrespondenceState').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceState");
			e.printStackTrace();
		}
	}

	public void deleteSelectedCorrespondenceState() {
		try {   
			correspondenceStateService.delete(selectedCorrespondenceStateList);
			this.selectedCorrespondenceStateList = null;
			correspondenceStateList = correspondenceStateService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceState");
			PrimeFaces.current().executeScript("PF('widgetVarCorrespondenceState').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-correspondenceState");
			e.printStackTrace();
		}

	}

	public CorrespondenceState getCorrespondenceState() {
		return correspondenceState;
	}

	public void setCorrespondenceState(CorrespondenceState correspondenceState) {
		this.correspondenceState = correspondenceState;
	}

	public List<CorrespondenceState> getCorrespondenceStateList() {
		return correspondenceStateList;
	}

	public void setCorrespondenceStateList(List<CorrespondenceState> correspondenceStateList) {
		this.correspondenceStateList = correspondenceStateList;
	}

	public List<CorrespondenceState> getSelectedCorrespondenceStateList() {
		return selectedCorrespondenceStateList;
	}

	public void setSelectedCorrespondenceStateList(List<CorrespondenceState> selectedCorrespondenceStateList) {
		this.selectedCorrespondenceStateList = selectedCorrespondenceStateList;
	}



	
	
}
