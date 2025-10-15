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
 * import com.smat.ins.model.entity.EquipmentCategory; import
 * com.smat.ins.model.entity.EquipmentInspectionTemplateItem; import
 * com.smat.ins.model.entity.GeneralEquipmentItem; import
 * com.smat.ins.model.entity.ItemType; import
 * com.smat.ins.model.service.EquipmentInspectionTemplateItemService; import
 * com.smat.ins.model.service.GeneralEquipmentItemService; import
 * com.smat.ins.model.service.ItemTypeService; import
 * com.smat.ins.model.service.EquipmentCategoryService; import
 * com.smat.ins.util.BeanUtility; import com.smat.ins.util.LocalizationService;
 * import com.smat.ins.util.UtilityHelper;
 * 
 * 
 * @Named
 * 
 * @ViewScoped public class CodingFormTemplateBean implements Serializable {
 * 
 * // #region "CodingProperties" private static final long serialVersionUID =
 * 4600306662046762257L;
 * 
 * private EquipmentInspectionTemplateItem equipmentInspectionTemplateItem;
 * private EquipmentCategory equipmentCategory;
 * 
 * private List<EquipmentInspectionTemplateItem> eITIList; private
 * List<EquipmentInspectionTemplateItem> selectedeITIList; private
 * List<EquipmentCategory> equipmentCategoryList; private List<ItemType>
 * itemTypeList;
 * 
 * 
 * private EquipmentInspectionTemplateItemService
 * equipmentInspectionTemplateItemService; private EquipmentCategoryService
 * equipmentCategoryService; private LocalizationService localizationService;
 * private ItemTypeService itemTypeService; private GeneralEquipmentItemService
 * generalEquipmentItemService; // #endregion
 * 
 * @PostConstruct public void init() {
 * 
 * try { equipmentInspectionTemplateItem = new
 * EquipmentInspectionTemplateItem(); GeneralEquipmentItem gei = new
 * GeneralEquipmentItem(); gei.setItemType(new ItemType());
 * equipmentInspectionTemplateItem.setGeneralEquipmentItem(gei);
 * equipmentCategory = new EquipmentCategory(); eITIList= new
 * ArrayList<EquipmentInspectionTemplateItem>(); equipmentCategoryList= new
 * ArrayList<EquipmentCategory>(); itemTypeList= new ArrayList<ItemType>();
 * selectedeITIList= new ArrayList<EquipmentInspectionTemplateItem>(); eITIList
 * = equipmentInspectionTemplateItemService.findAll(); equipmentCategoryList =
 * equipmentCategoryService.findAll(); itemTypeList = itemTypeService.findAll();
 * 
 * } catch (Exception e) {
 * UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString
 * ("errorDuringGetData")); e.printStackTrace(); } }
 * 
 * public CodingFormTemplateBean() { equipmentInspectionTemplateItemService =
 * (EquipmentInspectionTemplateItemService)
 * BeanUtility.getBean("equipmentInspectionTemplateItemService");
 * equipmentCategoryService = (EquipmentCategoryService)
 * BeanUtility.getBean("equipmentCategoryService"); itemTypeService =
 * (ItemTypeService) BeanUtility.getBean("itemTypeService"); itemTypeService =
 * (ItemTypeService) BeanUtility.getBean("itemTypeService");
 * generalEquipmentItemService = (GeneralEquipmentItemService)
 * BeanUtility.getBean("generalEquipmentItemService"); localizationService =
 * (LocalizationService) BeanUtility.getBean("localizationService"); }
 * 
 * @PreDestroy public void destroy() {
 * 
 * }
 * 
 * public void openNew() {
 * 
 * this.equipmentInspectionTemplateItem = new EquipmentInspectionTemplateItem();
 * GeneralEquipmentItem gei = new GeneralEquipmentItem(); gei.setItemType(new
 * ItemType());
 * this.equipmentInspectionTemplateItem.setGeneralEquipmentItem(gei); }
 * 
 * public boolean doValidate() { boolean result = true; if
 * (equipmentInspectionTemplateItem.getEquipmentCategory() == null ||
 * equipmentInspectionTemplateItem.getGeneralEquipmentItem() == null) {
 * UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().
 * getString("arabicName") + "  " +
 * localizationService.getErrorMessage().getString("validateInput")); result =
 * false; }
 * 
 * return result; }
 * 
 * public void save() { if (doValidate()) { try {
 * equipmentInspectionTemplateItem.setGeneralEquipmentItem(
 * generalEquipmentItemService.saveOrUpdate(equipmentInspectionTemplateItem.
 * getGeneralEquipmentItem()));
 * equipmentInspectionTemplateItemService.saveOrUpdate(
 * equipmentInspectionTemplateItem); eITIList =
 * equipmentInspectionTemplateItemService.findAll();
 * UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString(
 * "operationSuccess")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentInspectionTemplateItem");
 * PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()"); }
 * catch (Exception e) {
 * UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString
 * ("operationFaild")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentInspectionTemplateItem"); e.printStackTrace(); } } }
 * 
 * public boolean hasSelectedEquipmentInspectionTemplateItem() { return
 * this.selectedeITIList != null && !this.selectedeITIList.isEmpty(); }
 * 
 * public String getDeleteButtonMessage() {
 * 
 * if (hasSelectedEquipmentInspectionTemplateItem()) { int size =
 * this.selectedeITIList.size(); return size > 1 ? size + " " +
 * localizationService.getInterfaceLabel().getString(
 * "equipmentInspectionTemplateItemSelectedNum") :
 * localizationService.getInterfaceLabel().getString(
 * "oneequipmentInspectionTemplateItemSelected"); }
 * 
 * return localizationService.getInterfaceLabel().getString("delete"); }
 * 
 * public void deleteEquipmentInspectionTemplateItem() { try {
 * 
 * equipmentInspectionTemplateItemService.delete(equipmentInspectionTemplateItem
 * ); this.equipmentInspectionTemplateItem = null; eITIList =
 * equipmentInspectionTemplateItemService.findAll();
 * UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString(
 * "operationSuccess")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentInspectionTemplateItem");
 * PrimeFaces.current().executeScript(
 * "PF('widgetVarDeleteDialog').clearFilters()"); } catch (Exception e) {
 * UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString
 * ("operationFaild")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentInspectionTemplateItem"); e.printStackTrace(); } }
 * 
 * public void deleteSelectedEquipmentInspectionTemplateItem() { try {
 * equipmentInspectionTemplateItemService.delete(selectedeITIList);
 * this.selectedeITIList = null; eITIList =
 * equipmentInspectionTemplateItemService.findAll();
 * UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString(
 * "operationSuccess")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentInspectionTemplateItem");
 * PrimeFaces.current().executeScript(
 * "PF('dtEquipmentInspectionTemplateItem').clearFilters()"); } catch (Exception
 * e) {
 * UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString
 * ("operationFaild")); PrimeFaces.current().ajax().update("form:messages",
 * "form:dataTable-equipmentInspectionTemplateItem"); e.printStackTrace(); }
 * 
 * }
 * 
 * public EquipmentInspectionTemplateItem getEquipmentInspectionTemplateItem() {
 * return equipmentInspectionTemplateItem; }
 * 
 * public void
 * setEquipmentInspectionTemplateItem(EquipmentInspectionTemplateItem
 * equipmentInspectionTemplateItem) { this.equipmentInspectionTemplateItem =
 * equipmentInspectionTemplateItem; }
 * 
 * public List<EquipmentInspectionTemplateItem> geteITIList() { return eITIList;
 * }
 * 
 * public void seteITIList(List<EquipmentInspectionTemplateItem> eITIList) {
 * this.eITIList = eITIList; }
 * 
 * public List<EquipmentInspectionTemplateItem> getSelectedeITIList() { return
 * selectedeITIList; }
 * 
 * public void setSelectedeITIList(List<EquipmentInspectionTemplateItem>
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
 * public List<ItemType> getItemTypeList() { return itemTypeList; }
 * 
 * public void setItemTypeList(List<ItemType> itemTypeList) { this.itemTypeList
 * = itemTypeList; }
 * 
 * }
 */