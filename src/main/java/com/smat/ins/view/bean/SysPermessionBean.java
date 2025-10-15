package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.SysPermission;
import com.smat.ins.model.service.SysPermissionService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped

public class SysPermessionBean implements Serializable {

	//#region "properties"
	private static final long serialVersionUID = 4600306662046762257L;
	
	private SysPermission sysPermission;

	private List<SysPermission> sysPermessionList;

	private List<SysPermission> selectedSysPermessionList;

	

	private SysPermissionService sysPermissionService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {

		try {
			sysPermission = new SysPermission();
			sysPermessionList=sysPermissionService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}
	
	public SysPermessionBean() {
		sysPermissionService = (SysPermissionService) BeanUtility.getBean("sysPermissionService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	}
	
	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.sysPermission = new SysPermission();
	}

	public boolean doValidate() {
		boolean result = true;
		if (sysPermission.getName() == null || sysPermission.getName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("name") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				sysPermissionService.saveOrUpdate(sysPermission);
				sysPermessionList=sysPermissionService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dt-SysPermessions");
				PrimeFaces.current().executeScript("PF('manageSysPermessionDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dt-SysPermessions");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedSysPermessions() {
		return this.selectedSysPermessionList != null && !this.selectedSysPermessionList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedSysPermessions()) {
			int size = this.selectedSysPermessionList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("sysPermessionsSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneSysPermessionSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deleteSysPermession() {
		try {

			sysPermissionService.delete(sysPermission);
			this.sysPermission = null;
			sysPermessionList = sysPermissionService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-SysPermessions");
			PrimeFaces.current().executeScript("PF('dtSysPermessions').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-SysPermessions");
			e.printStackTrace();
		}
	}

	public void deleteSelectedSysPermessions() {
		try {   
			sysPermissionService.delete(selectedSysPermessionList);
			this.selectedSysPermessionList = null;
			sysPermessionList = sysPermissionService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-SysPermessions");
			PrimeFaces.current().executeScript("PF('dtSysPermessions').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-SysPermessions");
			e.printStackTrace();
		}

	}

	public SysPermission getSysPermession() {
		return sysPermission;
	}

	public void setSysPermession(SysPermission sysPermission) {
		this.sysPermission = sysPermission;
	}

	public List<SysPermission> getSysPermessionList() {
		return sysPermessionList;
	}

	public void setSysPermessionList(List<SysPermission> sysPermessionList) {
		this.sysPermessionList = sysPermessionList;
	}

	public List<SysPermission> getSelectedSysPermessionList() {
		return selectedSysPermessionList;
	}

	public void setSelectedSysPermessionList(List<SysPermission> selectedSysPermessionList) {
		this.selectedSysPermessionList = selectedSysPermessionList;
	}

	
	
}
