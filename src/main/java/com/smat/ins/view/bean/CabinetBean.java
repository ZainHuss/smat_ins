package com.smat.ins.view.bean;


import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.entity.CabinetLocation;
import com.smat.ins.model.entity.CabinetType;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.service.CabinetDefinitionService;
import com.smat.ins.model.service.CabinetLocationService;
import com.smat.ins.model.service.CabinetService;
import com.smat.ins.model.service.CabinetTypeService;
import com.smat.ins.model.service.UserAliasService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

import net.bytebuddy.asm.Advice.This;

@Named
@ViewScoped
public class CabinetBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long cabinetID;
	private String cabinetStr;
	private Boolean userAliasBool = true;
	private Boolean organizationBool = true;
	private String val;

	private Cabinet cabinet;
	private Organization organization;
	private SysUser sysUserLogin;
	private CabinetDefinition cabinetDefinition;
	private CabinetType cabinetType;

	private List<CabinetType> cabinetTypeList;
	private List<CabinetLocation> cabinetLocationList;
	private List<Cabinet> cabinetList;
	private List<UserAlias> allUserAliasList;
	private List<CabinetDefinition> cabinetDefinitionList;
	private ResourceBundle applicationSetting;
	private Integer cabinetCodeLength;
	private Integer cabinetDefinitionCodeLength;

	private CabinetService cabinetService;
	private CabinetTypeService cabinetTypeService;
	private CabinetLocationService cabinetLocationService;
	private UserAliasService userAliasService;
	private CabinetDefinitionService cabinetDefinitionService;

	private LocalizationService localizationService;

	@PostConstruct
	public void init() {
		cabinetStr = UtilityHelper.getRequestParameter("ca");

		try {

			if (cabinetStr != null) {
				cabinetID = Long.valueOf(UtilityHelper.decipher(cabinetStr));
				cabinet = cabinetService.findById(cabinetID);
				if (cabinet != null) {
					cabinetDefinitionList = cabinetDefinitionService.getByCabinet(cabinet);

				}
			}

		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}

	public CabinetBean() throws Exception {
		cabinet = new Cabinet();
		organization = new Organization();
		cabinetDefinition = new CabinetDefinition();

		cabinetTypeList = new ArrayList<CabinetType>();
		cabinetLocationList = new ArrayList<CabinetLocation>();
		cabinetList = new ArrayList<Cabinet>();
		allUserAliasList = new ArrayList<UserAlias>();
		cabinetDefinitionList = new ArrayList<CabinetDefinition>();

		cabinetService = (CabinetService) BeanUtility.getBean("cabinetService");
		cabinetLocationService = (CabinetLocationService) BeanUtility.getBean("cabinetLocationService");
		cabinetTypeService = (CabinetTypeService) BeanUtility.getBean("cabinetTypeService");
		userAliasService = (UserAliasService) BeanUtility.getBean("userAliasService");
		cabinetDefinitionService = (CabinetDefinitionService) BeanUtility.getBean("cabinetDefinitionService");

		sysUserLogin = (SysUser) UtilityHelper.getSessionAttr("user");

		cabinetTypeList = cabinetTypeService.findAll();
		if (cabinetTypeList != null && cabinetTypeList.size() > 0) {
			cabinetType = cabinetTypeList.get(0);
		} else {
			cabinetType = new CabinetType();
			cabinetType.setCode("00");
			cabinetType.setArabicName("\\u0627\\u0641\\u062A\\u0631\\u0627\\u0636\\u064A");
			cabinetType.setEnglishName("Default");
		}
		cabinetLocationList = cabinetLocationService.findAll();
		cabinetList = cabinetService.findAll();
		allUserAliasList = userAliasService.getAllWithDetails();

		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
		applicationSetting = ResourceBundle.getBundle("com.smat.ins.view.resources.applicationSetting");
		if (applicationSetting.containsKey("maxCabinetCodeLength")
				&& applicationSetting.getString("maxCabinetCodeLength") != null) {
			cabinetCodeLength = Integer.parseInt(applicationSetting.getString("maxCabinetCodeLength"));

		} else {
			cabinetCodeLength = 6;
		}

		if (applicationSetting.containsKey("maxDrawerCodeLength")
				&& applicationSetting.getString("maxDrawerCodeLength") != null) {
			cabinetDefinitionCodeLength = Integer.parseInt(applicationSetting.getString("maxDrawerCodeLength"));

		} else {
			cabinetDefinitionCodeLength = 2;
		}

		Integer maxCabinetCodeLength = cabinetService.getMaxCabinetCode();
		if (maxCabinetCodeLength != null) {
			cabinet.setCode(cabinetType.getCode() + "-"
					+ String.format("%0" + cabinetCodeLength + "d", maxCabinetCodeLength + 1));
		}
	}

	public void updateCabinetCode() throws Exception {
		if (cabinet != null && cabinet.getCabinetType() != null) {
			cabinetType = cabinet.getCabinetType();
			Integer maxCabinetCodeLength = cabinetService.getMaxCabinetCode();
			if (maxCabinetCodeLength != null) {
				cabinet.setCode(cabinetType.getCode() + "-"
						+ String.format("%0" + cabinetCodeLength + "d", maxCabinetCodeLength + 1));
			}
		}
	}

	public boolean doValidate() throws Exception {
		boolean result = true;

		if (cabinet.getArabicName() == null || cabinet.getArabicName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (cabinet.getEnglishName() == null || cabinet.getEnglishName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("englishName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (cabinet.getCabinetLocation() == null) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("cabinetLocation") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (cabinet.getCabinetType() == null) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("cabinetType") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		return result;
	}

	public String save() {
		try {
			if (doValidate()) {
				
				cabinet.setCreatedDate(Calendar.getInstance().getTime());
				cabinet.setSysUser(sysUserLogin);

				// Create Main Folder
				String mainLocation="";
				String os = System.getProperty("os.name").toLowerCase();
				if (os.contains("win")) {
					mainLocation = cabinet.getCabinetLocation().getWinLocation();
				} else if (os.contains("osx")) {
					mainLocation = cabinet.getCabinetLocation().getMacLocation();
				} else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
					mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
				}
				Files.createDirectories(Paths.get(mainLocation
						+ FileSystems.getDefault().getSeparator() + cabinet.getCode()));

				// Create sub drawer
				for (CabinetDefinition cabinetDefinition : cabinetDefinitionList) {

					Files.createDirectories(Paths.get(mainLocation
							+ FileSystems.getDefault().getSeparator() + cabinet.getCode()
							+ FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()));

				}

				cabinetService.saveOrUpdate(cabinet);

				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				return "pretty:cabinets/view";
			}
		} catch (Exception e) {
			UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
			return null;
		}
		return null;
	}

	public void selectOrganization(Organization organization) {
		this.organization = organization;
		cabinet.setOrganization(organization);
	}

	public void openNewCabinetDefinition() throws Exception {
		cabinetDefinition = new CabinetDefinition();
		cabinetDefinition.setIsEditMode(false);
		if (cabinetDefinitionList != null)
			cabinetDefinition.setDrawerOrder(cabinetDefinitionList.size() + 1);

		if (cabinetDefinition.getDrawerOrder() != null) {
			cabinetDefinition.setCode(
					String.format("%0" + cabinetDefinitionCodeLength + "d", cabinetDefinition.getDrawerOrder()));
		}
	}
	
	

	public void addCabinetDefinition() throws Exception {
		if (this.cabinetDefinition.getIsEditMode()) {
			cabinetDefinitionList.remove(cabinetDefinition);
			cabinet.getCabinetDefinitions().remove(cabinetDefinition);
			for(CabinetDefinition definition : cabinetDefinitionList) {
				if (definition.getDrawerArabicName().trim()
						.equals(this.cabinetDefinition.getDrawerArabicName().trim())
						&& definition.getDrawerEnglishName().trim()
								.equals(this.cabinetDefinition.getDrawerEnglishName().trim())) {
					UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("nameAlreadyFound"));
					return;
				}
			}
			
			cabinetDefinitionList.add(cabinetDefinition);
			cabinet.getCabinetDefinitions().add(cabinetDefinition);
		} else {
			this.cabinetDefinition.setCabinet(cabinet);
			this.cabinetDefinition.setSysUser(sysUserLogin);
			this.cabinetDefinition.setCreatedDate(Calendar.getInstance().getTime());
			cabinet.getCabinetDefinitions().add(this.cabinetDefinition);
			cabinetDefinitionList.add(cabinetDefinition);
		}

		UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("newItemHasBeenAddedToList"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-cabinetDefinition",
				"form:manage-cabinetDefinition-content");

		this.cabinetDefinition = new CabinetDefinition();
		cabinetDefinition.setIsEditMode(false);
		if (cabinetDefinitionList != null)
			this.cabinetDefinition.setDrawerOrder(cabinetDefinitionList.size() + 1);
		if (this.cabinetDefinition.getDrawerOrder() != null) {
			this.cabinetDefinition.setCode(
					String.format("%0" + cabinetDefinitionCodeLength + "d", this.cabinetDefinition.getDrawerOrder()));
		}
		
			
	}

	public void removeCabinetDefinition(CabinetDefinition cabinetDefinition) throws Exception {
		cabinetDefinitionList.remove(cabinetDefinition);
		cabinet.getCabinetDefinitions().remove(cabinetDefinition);
		UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-cabinetDefinition");

	}

	public void assignCabinetDefinition(CabinetDefinition cabinetDefinition) {

		this.cabinetDefinition = cabinetDefinition;
		this.cabinetDefinition.setIsEditMode(true);
	}

	public void assignCabinet(Cabinet cabinet) throws Exception {
		cabinetID = cabinet.getId();
		cabinetStr = UtilityHelper.cipher(cabinetID.toString());
	}

	public void deleteCabinet() {
		try {
			cabinetService.delete(cabinet);
			cabinetList = cabinetService.findAll();
			this.cabinet = new Cabinet();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-cabinet");
			PrimeFaces.current().executeScript("PF('dtCabinet').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-cabinet");
			e.printStackTrace();
		}
	}

	public void displayPanel() {
		if (val.equals("1")) {
			setUserAliasBool(true);
			setOrganizationBool(true);
			PrimeFaces.current().ajax().update("form:outputLabel_userAlias", "form:selectOneMenu_userAlias",
					"form:outputLabel_organization", "form:inputText_organization", "form:commandButton_organization");

		} else if (val.equals("2")) {
			setUserAliasBool(false);
			setOrganizationBool(true);
			PrimeFaces.current().ajax().update("form:outputLabel_userAlias", "form:selectOneMenu_userAlias",
					"form:outputLabel_organization", "form:inputText_organization", "form:commandButton_organization");

			cabinet.setOrganization(null);
			organization = new Organization();

		} else if (val.equals("3")) {
			setUserAliasBool(true);
			setOrganizationBool(false);
			PrimeFaces.current().ajax().update("form:outputLabel_userAlias", "form:selectOneMenu_userAlias",
					"form:outputLabel_organization", "form:inputText_organization", "form:commandButton_organization");

			cabinet.setUserAlias(null);

		}

	}

	public String imgIcon(CabinetType cabinetType) {
		switch (cabinetType.getCode()) {
		case "01":
			return "images/stock-photo-archive-cabinet-with-folders.png";
		case "02":
			return "images/blue-archive-cabinet-icon.png";
		case "03":
			return "images/stock-photo-archive-cabinet-icon.png";
		default:
			return "images/stock-photo-archive-cabinet-with-folders.png";
		}
	}

	public Cabinet getCabinet() {
		return cabinet;
	}

	public void setCabinet(Cabinet cabinet) {
		this.cabinet = cabinet;
	}

	public List<CabinetType> getCabinetTypeList() {
		return cabinetTypeList;
	}

	public void setCabinetTypeList(List<CabinetType> cabinetTypeList) {
		this.cabinetTypeList = cabinetTypeList;
	}

	public List<CabinetLocation> getCabinetLocationList() {
		return cabinetLocationList;
	}

	public void setCabinetLocationList(List<CabinetLocation> cabinetLocationList) {
		this.cabinetLocationList = cabinetLocationList;
	}

	public List<Cabinet> getCabinetList() {
		return cabinetList;
	}

	public void setCabinetList(List<Cabinet> cabinetList) {
		this.cabinetList = cabinetList;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	

	public List<UserAlias> getAllUserAliasList() {
		return allUserAliasList;
	}

	public void setAllUserAliasList(List<UserAlias> allUserAliasList) {
		this.allUserAliasList = allUserAliasList;
	}

	public SysUser getSysUserLogin() {
		return sysUserLogin;
	}

	public void setSysUserLogin(SysUser sysUserLogin) {
		this.sysUserLogin = sysUserLogin;
	}

	public CabinetDefinition getCabinetDefinition() {
		return cabinetDefinition;
	}

	public void setCabinetDefinition(CabinetDefinition cabinetDefinition) {
		this.cabinetDefinition = cabinetDefinition;
	}

	public List<CabinetDefinition> getCabinetDefinitionList() {
		return cabinetDefinitionList;
	}

	public void setCabinetDefinitionList(List<CabinetDefinition> cabinetDefinitionList) {
		this.cabinetDefinitionList = cabinetDefinitionList;
	}

	public Long getCabinetID() {
		return cabinetID;
	}

	public void setCabinetID(Long cabinetID) {
		this.cabinetID = cabinetID;
	}

	public String getCabinetStr() {
		return cabinetStr;
	}

	public void setCabinetStr(String cabinetStr) {
		this.cabinetStr = cabinetStr;
	}

	public Boolean getUserAliasBool() {
		return userAliasBool;
	}

	public void setUserAliasBool(Boolean userAliasBool) {
		this.userAliasBool = userAliasBool;
	}

	public Boolean getOrganizationBool() {
		return organizationBool;
	}

	public void setOrganizationBool(Boolean organizationBool) {
		this.organizationBool = organizationBool;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

}
