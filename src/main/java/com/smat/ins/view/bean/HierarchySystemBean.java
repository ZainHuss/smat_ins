package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.DragDropEvent;

import com.smat.ins.model.entity.HierarchySystem;
import com.smat.ins.model.entity.HierarchySystemDef;
import com.smat.ins.model.entity.HierarchyType;
import com.smat.ins.model.service.HierarchySystemDefService;
import com.smat.ins.model.service.HierarchySystemService;
import com.smat.ins.model.service.HierarchyTypeService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

@Named
@ViewScoped
public class HierarchySystemBean implements Serializable {

	// #region "properties"
	private static final long serialVersionUID = -6940976497901194753L;

	// entity object
	private HierarchySystem hierarchySystem;
	private HierarchySystemDef hierarchySystemDef;

	private Short hierarchySystemID;
	private String hierarchySystemStr;

	// list
	private List<HierarchySystem> hierarchySystemList;
	private List<HierarchySystem> selectedHierarchySystemList;

	private List<HierarchySystemDef> hierarchySystemDefList;
	
	private List<HierarchyType> hierarchyTypeList;
	
	// Service
	private HierarchySystemService hierarchySystemService;
	private HierarchySystemDefService hierarchySystemDefService;
	private HierarchyTypeService hierarchyTypeService;
	private LocalizationService localizationService;

	// #endregion
	@PostConstruct
	public void init() {
		hierarchySystemStr = UtilityHelper.getRequestParameter("hs");
		try {
			if (hierarchySystemStr != null) {

				hierarchySystemID = Short.valueOf(UtilityHelper.decipher(hierarchySystemStr));
				hierarchySystem = hierarchySystemService.findById(hierarchySystemID);
				
				hierarchySystemDefList = hierarchySystemDefService.getByHierarchySystem(hierarchySystem);


			}

		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}

	public HierarchySystemBean() throws Exception {
		hierarchySystem = new HierarchySystem();
		hierarchySystemDef = new HierarchySystemDef();

		hierarchySystemList = new ArrayList<HierarchySystem>();
		selectedHierarchySystemList = new ArrayList<HierarchySystem>();
		hierarchySystemDefList = new ArrayList<HierarchySystemDef>();
		hierarchyTypeList = new ArrayList<HierarchyType>();
		
		hierarchySystemService = (HierarchySystemService) BeanUtility.getBean("hierarchySystemService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
		hierarchySystemDefService = (HierarchySystemDefService) BeanUtility.getBean("hierarchySystemDefService");
		hierarchyTypeService = (HierarchyTypeService) BeanUtility.getBean("hierarchyTypeService");

		
		hierarchySystemList = hierarchySystemService.findAll();
		hierarchyTypeList =hierarchyTypeService.findAll();
	}

	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.hierarchySystem = new HierarchySystem();
	}

	public boolean doValidate() throws Exception {
		boolean result = true;
		
		if (hierarchySystem.getArabicName() == null || hierarchySystem.getArabicName().equals("")) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (hierarchySystem.getEnglishName() == null || hierarchySystem.getEnglishName().equals("")) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("englishName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		return result;
	}
	public boolean doValidateHierarchySystemDef() throws Exception {
		boolean result = true;
		
		if (hierarchySystemDef.getArabicName() == null || hierarchySystemDef.getArabicName().equals("")) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (hierarchySystemDef.getEnglishName() == null || hierarchySystemDef.getEnglishName().equals("")) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("englishName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		return result;
	}
	public boolean doValidateUpdate() throws Exception {
		boolean result = true;
		if (hierarchySystem.getName() == null || hierarchySystem.getName().equals("")) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("name") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}

	public String save() throws Exception {
		String str = "";
		if (doValidate()) {
			for (HierarchySystemDef hierarchySystemDef : hierarchySystemDefList) {
				hierarchySystemDef.setHierarchySystem(hierarchySystem);
			}
			hierarchySystem.setHierarchySystemDefs(new HashSet<HierarchySystemDef>(hierarchySystemDefList));
			if (hierarchySystemService.merge(hierarchySystem)) {
				hierarchySystemList = hierarchySystemService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

				str = "pretty:hierarchySystem";
			}

		}
		return str;
	}

	public String update() throws Exception {
		String str = "";
		if (doValidateUpdate()) {
			if (hierarchySystemService.update(hierarchySystem)) {
				hierarchySystemList = hierarchySystemService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				str = "pretty:hierarchySystem";
			}
		}
		return str;
	}

	public void deleteHierarchySystem() {
		try {

			hierarchySystemService.delete(hierarchySystem);
			this.hierarchySystem = null;
			hierarchySystemList = hierarchySystemService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-hierarchySystems");
			PrimeFaces.current().executeScript("PF('dtHierarchySystems').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-hierarchySystems");
			e.printStackTrace();
		}
	}

	public void deleteSelectedHierarchySystems() {
		try {
			hierarchySystemService.delete(selectedHierarchySystemList);
			this.selectedHierarchySystemList = null;
			hierarchySystemList = hierarchySystemService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-hierarchySystems");
			PrimeFaces.current().executeScript("PF('dtHierarchySystems').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-hierarchySystems");
			e.printStackTrace();
		}

	}

	public void assignHierarchySystem(HierarchySystem hierarchySystem) throws Exception {
		hierarchySystemID = hierarchySystem.getId();
		hierarchySystemStr = UtilityHelper.cipher(hierarchySystemID.toString());

	}

	public void getDetailsByHierarchySystem(HierarchySystem hierarchySystem) throws Exception {
		hierarchySystemDefList = hierarchySystemDefService.getByHierarchySystem(hierarchySystem);
	}

	public void rest() throws Exception {
		hierarchySystem = new HierarchySystem();

	}

	public boolean hasSelectedHierarchySystems() {
		return this.selectedHierarchySystemList != null && !this.selectedHierarchySystemList.isEmpty();
	}

	public String getDeleteButtonMessage() {

		if (hasSelectedHierarchySystems()) {
			int size = this.selectedHierarchySystemList.size();
			return size > 1
					? size + " " + localizationService.getInterfaceLabel().getString("hierarchySystemsSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneHierarchySystemSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void removeHierarchySystemDef(HierarchySystemDef hierarchySystemDef) {
		hierarchySystemDefList.remove(hierarchySystemDef);
	}
	public void addHierarchySystemDef() throws Exception {
		if (doValidateHierarchySystemDef()) {
			hierarchySystemDefList.add(hierarchySystemDef);
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-hierarchySystemDef", "form:manage-HierarchySystemDef-content");
			PrimeFaces.current().executeScript("PF('manageHierarchySystemDefDialog').hide()");
		}
		
		
	}
	public void openNewHierarchySystemDef() {
		hierarchySystemDef=new HierarchySystemDef();
	}
	public HierarchySystem getHierarchySystem() {
		return hierarchySystem;
	}

	public void setHierarchySystem(HierarchySystem hierarchySystem) {
		this.hierarchySystem = hierarchySystem;
	}

	public List<HierarchySystem> getHierarchySystemList() {
		return hierarchySystemList;
	}

	public void setHierarchySystemList(List<HierarchySystem> hierarchySystemList) {
		this.hierarchySystemList = hierarchySystemList;
	}

	public List<HierarchySystem> getSelectedHierarchySystemList() {
		return selectedHierarchySystemList;
	}

	public void setSelectedHierarchySystemList(List<HierarchySystem> selectedHierarchySystemList) {
		this.selectedHierarchySystemList = selectedHierarchySystemList;
	}

	public Short getHierarchySystemID() {
		return hierarchySystemID;
	}

	public void setHierarchySystemID(Short hierarchySystemID) {
		this.hierarchySystemID = hierarchySystemID;
	}

	public String getHierarchySystemStr() {
		return hierarchySystemStr;
	}

	public void setHierarchySystemStr(String hierarchySystemStr) {
		this.hierarchySystemStr = hierarchySystemStr;
	}

	public List<HierarchySystemDef> getHierarchySystemDefList() {
		return hierarchySystemDefList;
	}

	public void setHierarchySystemDefList(List<HierarchySystemDef> hierarchySystemDefList) {
		this.hierarchySystemDefList = hierarchySystemDefList;
	}

	public HierarchySystemDef getHierarchySystemDef() {
		return hierarchySystemDef;
	}

	public void setHierarchySystemDef(HierarchySystemDef hierarchySystemDef) {
		this.hierarchySystemDef = hierarchySystemDef;
	}

	public List<HierarchyType> getHierarchyTypeList() {
		return hierarchyTypeList;
	}

	public void setHierarchyTypeList(List<HierarchyType> hierarchyTypeList) {
		this.hierarchyTypeList = hierarchyTypeList;
	}

}
