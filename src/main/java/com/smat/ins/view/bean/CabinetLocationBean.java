package com.smat.ins.view.bean;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.CabinetLocation;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.service.CabinetLocationService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;



@Named
@ViewScoped
public class CabinetLocationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6575766034303871439L;
    //#region properties
	
	private CabinetLocation defaultCabinetLocation;
	private CabinetLocation cabinetLocation;
	private List<CabinetLocation> cabinetLocations;
	private List<CabinetLocation> selectedCabinetLocations;
	private SysUser sysUserLogin;
	
	public CabinetLocation getDefaultCabinetLocation() {
		return defaultCabinetLocation;
	}
	public void setDefaultCabinetLocation(CabinetLocation defaultCabinetLocation) {
		this.defaultCabinetLocation = defaultCabinetLocation;
	}
	
	public List<CabinetLocation> getSelectedCabinetLocations() {
		return selectedCabinetLocations;
	}
	public void setSelectedCabinetLocations(List<CabinetLocation> selectedCabinetLocations) {
		this.selectedCabinetLocations = selectedCabinetLocations;
	}
	public List<CabinetLocation> getCabinetLocations() {
		return cabinetLocations;
	}
	public void setCabinetLocations(List<CabinetLocation> cabinetLocations) {
		this.cabinetLocations = cabinetLocations;
	}
	 
	public CabinetLocation getCabinetLocation() {
		return cabinetLocation;
	}
	public void setCabinetLocation(CabinetLocation cabinetLocation) {
		this.cabinetLocation = cabinetLocation;
	}
	//#endregion
	
	//#region services

	 private CabinetLocationService cabinetLocationService;
	 private LocalizationService localizationService;
	 
	//#endregion
	 
	public CabinetLocationBean() throws Exception {
		localizationService=(LocalizationService) BeanUtility.getBean("localizationService");
		cabinetLocationService=(CabinetLocationService) BeanUtility.getBean("cabinetLocationService");
		defaultCabinetLocation=cabinetLocationService.getDefaultLocation();
		sysUserLogin = (SysUser) UtilityHelper.getSessionAttr("user");
	}
	
	@PostConstruct
	public void init() {
		cabinetLocation=new CabinetLocation();
	    selectedCabinetLocations=new ArrayList<CabinetLocation>();
		try {
			cabinetLocations=cabinetLocationService.getCabinetLocationExceptDefault();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));

		}
	}
	
	@PreDestroy
	public void destroy() {
		
	}
	
	public void editDefaultLocation() {
		this.defaultCabinetLocation.setIsEditMode(true);
	}
	
	public void doEditDefaultLocation() {
		try {
			cabinetLocationService.update(defaultCabinetLocation);
			this.defaultCabinetLocation.setIsEditMode(false);
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			e.printStackTrace();
		}
	}
	
	public void cancelEditMode() {
		this.defaultCabinetLocation.setIsEditMode(false);
	}
	
	public void openNew() {

		this.cabinetLocation = new CabinetLocation();
		defaultCabinetLocation.setIsEditMode(false);
	}

	public boolean doValidate() {
		boolean result = true;
		if (cabinetLocation.getWinLocation() == null || cabinetLocation.getWinLocation().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("locationStorage") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		
		if (cabinetLocation.getLinuxLocation() == null || cabinetLocation.getLinuxLocation().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("locationStorage") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		
		if (cabinetLocation.getMacLocation() == null || cabinetLocation.getMacLocation().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("locationStorage") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				cabinetLocation.setCreatedDate(Calendar.getInstance().getTime());
				cabinetLocation.setSysUser(sysUserLogin);
				cabinetLocationService.saveOrUpdate(cabinetLocation);
				String mainLocation="";
				String os = System.getProperty("os.name").toLowerCase();
				if (os.contains("win")) {
					mainLocation = cabinetLocation.getWinLocation();
				} else if (os.contains("osx")) {
					mainLocation = cabinetLocation.getMacLocation();
				} else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
					mainLocation = cabinetLocation.getLinuxLocation();
				}
				Files.createDirectories(Paths.get(mainLocation));
				cabinetLocations=cabinetLocationService.getCabinetLocationExceptDefault();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dt-cabinet-location");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dt-cabinet-location");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedCabinetLocation() {
		return this.selectedCabinetLocations != null && !this.selectedCabinetLocations.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedCabinetLocation()) {
			int size = this.selectedCabinetLocations.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("cabinetLocationSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneCabinetLocationSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deleteCabinetLocation() {
		try {
            
			cabinetLocationService.delete(cabinetLocation);
			this.cabinetLocation = null;
			cabinetLocations=cabinetLocationService.getCabinetLocationExceptDefault();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-cabinet-location");
			PrimeFaces.current().executeScript("PF('dtCabinetLocation').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-cabinet-location");
			e.printStackTrace();
		}
	}

	public void deleteSelectedCabinetLocation() {
		try {   
			cabinetLocationService.delete(selectedCabinetLocations);
			this.selectedCabinetLocations = null;
			cabinetLocations=cabinetLocationService.getCabinetLocationExceptDefault();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-cabinet-locations");
			PrimeFaces.current().executeScript("PF('dtCabinetLocation').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-cabinet-locations");
			e.printStackTrace();
		}

	}


}
