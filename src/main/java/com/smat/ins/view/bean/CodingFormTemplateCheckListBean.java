/*
 * package com.smat.ins.view.bean;
 * 
 * import java.io.Serializable; import java.util.ArrayList; import
 * java.util.List;
 * 
 * import javax.annotation.PostConstruct; import javax.annotation.PreDestroy;
 * import javax.faces.view.ViewScoped; import javax.inject.Named;
 * 
 * import org.primefaces.PrimeFaces;
 * 
 * import com.smat.ins.model.entity.ChecklistDataSource; import
 * com.smat.ins.model.entity.ChecklistDetailDataSource; import
 * com.smat.ins.model.entity.EquipmentCategory; import
 * com.smat.ins.model.entity.EquipmentChecklistTemplateItem; import
 * com.smat.ins.model.entity.GeneralCkecklistItem; import
 * com.smat.ins.model.entity.ItemType; import
 * com.smat.ins.model.service.EquipmentChecklistTemplateItemService; import
 * com.smat.ins.model.service.GeneralCkecklistItemService; import
 * com.smat.ins.model.service.ItemTypeService; import
 * com.smat.ins.model.service.ChecklistDataSourceService; import
 * com.smat.ins.model.service.ChecklistDetailDataSourceService; import
 * com.smat.ins.model.service.EquipmentCategoryService; import
 * com.smat.ins.util.BeanUtility; import com.smat.ins.util.LocalizationService;
 * import com.smat.ins.util.UtilityHelper;
 * 
 * 
 * @Named
 * 
 * @ViewScoped public class CodingFormTemplateCheckListBean implements
 * Serializable {
 * 
 * // #region "CodingProperties" private static final long serialVersionUID =
 * 4600306662046762257L;
 * 
 * private EquipmentChecklistTemplateItem equipmentChecklistTemplateItem;
 * private EquipmentCategory equipmentCategory; private String valueToAdd;
 * private ChecklistDataSource objChecklistDataSource; private boolean editMode;
 * private boolean predefinedDataSource; private ChecklistDataSource
 * selectedPredefindDatSource;
 * 
 * private List<EquipmentChecklistTemplateItem> eITIList; private
 * List<EquipmentChecklistTemplateItem> selectedeITIList; private
 * List<EquipmentCategory> equipmentCategoryList; private
 * List<ChecklistDetailDataSource> checklistDetailDataSourceList; private
 * List<String> dataSourceList; private List<ChecklistDataSource>
 * predefinedDatasourceList;
 * 
 * private List<GeneralCkecklistItem> generalCkecklistItems;
 * 
 * private EquipmentChecklistTemplateItemService
 * equipmentChecklistTemplateItemService; private EquipmentCategoryService
 * equipmentCategoryService; private LocalizationService localizationService;
 * private GeneralCkecklistItemService generalCkecklistItemService; private
 * ChecklistDataSourceService checklistDataSourceService; private
 * ChecklistDetailDataSourceService checklistDetailDataSourceService;
 * 
 * // #endregion
 * 
 * @PostConstruct public void init() {
 * 
 * try { equipmentChecklistTemplateItem = new EquipmentChecklistTemplateItem();
 * //equipmentChecklistTemplateItem.setGeneralCkecklistItem(new
 * GeneralCkecklistItem()); equipmentCategory = new EquipmentCategory();
 * predefinedDatasourceList = new ArrayList<ChecklistDataSource>();
 * checklistDetailDataSourceList = new ArrayList<ChecklistDetailDataSource>();
 * editMode = false; predefinedDataSource = true;
 * 
 * ChecklistDetailDataSource objDataSourceToAdd = new
 * ChecklistDetailDataSource(); ChecklistDataSource objChecklistDS = new
 * ChecklistDataSource();
 * objDataSourceToAdd.setChecklistDataSource(objChecklistDS);
 * GeneralCkecklistItem objGeneralCheckListItem = new GeneralCkecklistItem();
 * objGeneralCheckListItem.setChecklistDataSource(objChecklistDS);
 * equipmentChecklistTemplateItem.setGeneralCkecklistItem(
 * objGeneralCheckListItem);
 * 
 * eITIList= new ArrayList<EquipmentChecklistTemplateItem>();
 * equipmentCategoryList= new ArrayList<EquipmentCategory>(); selectedeITIList=
 * new ArrayList<EquipmentChecklistTemplateItem>(); dataSourceList = new
 * ArrayList<String>(); eITIList =
 * equipmentChecklistTemplateItemService.findAll(); equipmentCategoryList =
 * equipmentCategoryService.findAll(); predefinedDatasourceList =
 * checklistDataSourceService.findAll();
 * generalCkecklistItems=generalCkecklistItemService.findAll();
 * 
 * 
 * } catch (Exception e) {
 * UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString
 * ("errorDuringGetData")); e.printStackTrace(); } } public void
 * togglePredefinedDataSource() { predefinedDataSource = !predefinedDataSource;
 * //equipmentChecklistTemplateItem.getGeneralCkecklistItem().
 * setChecklistDataSource(new ChecklistDataSource());
 * 
 * } public CodingFormTemplateCheckListBean() {
 * equipmentChecklistTemplateItemService =
 * (EquipmentChecklistTemplateItemService)
 * BeanUtility.getBean("equipmentChecklistTemplateItemService");
 * equipmentCategoryService = (EquipmentCategoryService)
 * BeanUtility.getBean("equipmentCategoryService"); generalCkecklistItemService
 * = (GeneralCkecklistItemService)
 * BeanUtility.getBean("generalCkecklistItemService");
 * checklistDataSourceService = (ChecklistDataSourceService)
 * BeanUtility.getBean("checklistDataSourceService");
 * checklistDetailDataSourceService = (ChecklistDetailDataSourceService)
 * BeanUtility.getBean("checklistDetailDataSourceService"); localizationService
 * = (LocalizationService) BeanUtility.getBean("localizationService"); }
 * 
 * @PreDestroy public void destroy() {
 * 
 * }
 * 
 * public void openNew() { predefinedDataSource = false; valueToAdd = "";
 * selectedPredefindDatSource = new ChecklistDataSource();
 * this.equipmentChecklistTemplateItem = new EquipmentChecklistTemplateItem();
 * GeneralCkecklistItem gei = new GeneralCkecklistItem();
 * gei.setChecklistDataSource(new ChecklistDataSource());
 * this.equipmentChecklistTemplateItem.setGeneralCkecklistItem(gei);
 * checklistDetailDataSourceList = new ArrayList<ChecklistDetailDataSource>();
 * editMode = false; }
 * 
 * @SuppressWarnings("unchecked") public void
 * setEditMode(EquipmentChecklistTemplateItem obj) throws Exception {
 * predefinedDataSource = false; valueToAdd = ""; selectedPredefindDatSource =
 * new ChecklistDataSource(); editMode = true; checklistDetailDataSourceList =
 * new ArrayList<ChecklistDetailDataSource>(); equipmentChecklistTemplateItem =
 * equipmentChecklistTemplateItemService.findById(obj.getId());
 * ChecklistDataSource cdsObj =
 * equipmentChecklistTemplateItem.getGeneralCkecklistItem().
 * getChecklistDataSource(); //checklistDetailDataSourceList = new
 * ArrayList<ChecklistDetailDataSource>(cdsObj.getChecklistDetailDataSources());
 * checklistDetailDataSourceList =
 * checklistDetailDataSourceService.getByDataSourceId(cdsObj.getId());
 * selectedPredefindDatSource = cdsObj; }
 * 
 * public void addToDatasourceList() throws Exception {
 * ChecklistDetailDataSource objToAdd = new ChecklistDetailDataSource();
 * objToAdd.setItemValue(valueToAdd); objToAdd.setItemCode("----");
 * checklistDetailDataSourceList.add(objToAdd); if(editMode) {
 * objToAdd.setChecklistDataSource(equipmentChecklistTemplateItem.
 * getGeneralCkecklistItem().getChecklistDataSource());
 * checklistDetailDataSourceService.saveOrUpdate(objToAdd); } valueToAdd = "";
 * } public void deleteDataSourceItem(ChecklistDetailDataSource obj) throws
 * Exception { checklistDetailDataSourceList.remove(obj); if(editMode &&
 * obj.getId() != null && obj.getId() > 0)
 * checklistDetailDataSourceService.delete(obj); } public boolean doValidate() {
 * boolean result = true; if
 * (equipmentChecklistTemplateItem.getEquipmentCategory() == null ||
 * equipmentChecklistTemplateItem.getGeneralCkecklistItem() == null) {
 * UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().
 * getString("arabicName") + "  " +
 * localizationService.getErrorMessage().getString("validateInput")); result =
 * false; }
 * 
 * return result; }
 * 
 * public void save() { if (doValidate()) { try {
 * 
 * equipmentChecklistTemplateItemService.saveOrUpdate(
 * equipmentChecklistTemplateItem);
 * 
 * eITIList = equipmentChecklistTemplateItemService.findAll();
 * UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString(
 * "operationSuccess")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentChecklistTemplateItem");
 * PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()"); }
 * catch (Exception e) {
 * UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString
 * ("operationFaild")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentChecklistTemplateItem"); e.printStackTrace(); } } }
 * 
 * public boolean hasSelectedEquipmentChecklistTemplateItem() { return
 * this.selectedeITIList != null && !this.selectedeITIList.isEmpty(); }
 * 
 * public String getDeleteButtonMessage() {
 * 
 * if (hasSelectedEquipmentChecklistTemplateItem()) { int size =
 * this.selectedeITIList.size(); return size > 1 ? size + " " +
 * localizationService.getInterfaceLabel().getString(
 * "equipmentChecklistTemplateItemSelectedNum") :
 * localizationService.getInterfaceLabel().getString(
 * "oneequipmentChecklistTemplateItemSelected"); }
 * 
 * return localizationService.getInterfaceLabel().getString("delete"); }
 * 
 * public void deleteEquipmentChecklistTemplateItem() { try {
 * 
 * equipmentChecklistTemplateItemService.delete(equipmentChecklistTemplateItem);
 * generalCkecklistItemService.delete(equipmentChecklistTemplateItem.
 * getGeneralCkecklistItem()); this.equipmentChecklistTemplateItem = null;
 * eITIList = equipmentChecklistTemplateItemService.findAll();
 * UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString(
 * "operationSuccess")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentChecklistTemplateItem");
 * PrimeFaces.current().executeScript(
 * "PF('widgetVarDeleteDialog').clearFilters()"); } catch (Exception e) {
 * UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString
 * ("operationFaild")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentChecklistTemplateItem"); e.printStackTrace(); } }
 * 
 * public void deleteSelectedEquipmentChecklistTemplateItem() { try {
 * equipmentChecklistTemplateItemService.delete(selectedeITIList);
 * this.selectedeITIList = null; eITIList =
 * equipmentChecklistTemplateItemService.findAll();
 * UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString(
 * "operationSuccess")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentChecklistTemplateItem");
 * PrimeFaces.current().executeScript(
 * "PF('dtEquipmentChecklistTemplateItem').clearFilters()"); } catch (Exception
 * e) {
 * UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString
 * ("operationFaild")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentChecklistTemplateItem"); e.printStackTrace(); }
 * 
 * }
 * 
 * public EquipmentChecklistTemplateItem getEquipmentChecklistTemplateItem() {
 * return equipmentChecklistTemplateItem; }
 * 
 * public void setEquipmentChecklistTemplateItem(EquipmentChecklistTemplateItem
 * equipmentChecklistTemplateItem) { this.equipmentChecklistTemplateItem =
 * equipmentChecklistTemplateItem; }
 * 
 * public List<EquipmentChecklistTemplateItem> geteITIList() { return eITIList;
 * }
 * 
 * public void seteITIList(List<EquipmentChecklistTemplateItem> eITIList) {
 * this.eITIList = eITIList; }
 * 
 * public List<EquipmentChecklistTemplateItem> getSelectedeITIList() { return
 * selectedeITIList; }
 * 
 * public void setSelectedeITIList(List<EquipmentChecklistTemplateItem>
 * selectedeITIList) { this.selectedeITIList = selectedeITIList; }
 * 
 * public EquipmentCategory getEquipmentCategory() { return equipmentCategory; }
 * 
 * public void setEquipmentCategory(EquipmentCategory equipmentCategory) {
 * this.equipmentCategory = equipmentCategory; }
 * 
 * public List<EquipmentCategory> getEquipmentCategoryList() { return
 * equipmentCategoryList; }
 * 
 * public void setEquipmentCategoryList(List<EquipmentCategory>
 * equipmentCategoryList) { this.equipmentCategoryList = equipmentCategoryList;
 * }
 * 
 * public String getValueToAdd() { return valueToAdd; }
 * 
 * public void setValueToAdd(String valueToAdd) { this.valueToAdd = valueToAdd;
 * }
 * 
 * public ChecklistDataSource getObjChecklistDataSource() { return
 * objChecklistDataSource; }
 * 
 * public void setObjChecklistDataSource(ChecklistDataSource
 * objChecklistDataSource) { this.objChecklistDataSource =
 * objChecklistDataSource; }
 * 
 * public List<String> getDataSourceList() { return dataSourceList; }
 * 
 * public void setDataSourceList(List<String> dataSourceList) {
 * this.dataSourceList = dataSourceList; }
 * 
 * public List<ChecklistDetailDataSource> getChecklistDetailDataSourceList() {
 * return checklistDetailDataSourceList; }
 * 
 * public void setChecklistDetailDataSourceList(List<ChecklistDetailDataSource>
 * checklistDetailDataSourceList) { this.checklistDetailDataSourceList =
 * checklistDetailDataSourceList; }
 * 
 * public boolean isEditMode() { return editMode; }
 * 
 * public void setEditMode(boolean editMode) { this.editMode = editMode; }
 * 
 * public boolean isPredefinedDataSource() { return predefinedDataSource; }
 * 
 * public void setPredefinedDataSource(boolean predefinedDataSource) {
 * this.predefinedDataSource = predefinedDataSource; } public
 * List<ChecklistDataSource> getPredefinedDatasourceList() { return
 * predefinedDatasourceList; } public void
 * setPredefinedDatasourceList(List<ChecklistDataSource>
 * predefinedDatasourceList) { this.predefinedDatasourceList =
 * predefinedDatasourceList; } public ChecklistDataSource
 * getSelectedPredefindDatSource() { return selectedPredefindDatSource; } public
 * void setSelectedPredefindDatSource(ChecklistDataSource
 * selectedPredefindDatSource) { this.selectedPredefindDatSource =
 * selectedPredefindDatSource; } public List<GeneralCkecklistItem>
 * getGeneralCkecklistItems() { return generalCkecklistItems; } public void
 * setGeneralCkecklistItems(List<GeneralCkecklistItem> generalCkecklistItems) {
 * this.generalCkecklistItems = generalCkecklistItems; }
 * 
 * 
 * 
 * }
 */