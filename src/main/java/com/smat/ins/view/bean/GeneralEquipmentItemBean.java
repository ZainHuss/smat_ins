package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.ChecklistDataSource;
import com.smat.ins.model.entity.GeneralEquipmentItem;
import com.smat.ins.model.entity.ItemType;
import com.smat.ins.model.service.ChecklistDataSourceService;
import com.smat.ins.model.service.GeneralEquipmentItemService;
import com.smat.ins.model.service.ItemTypeService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

@Named
@ViewScoped
public class GeneralEquipmentItemBean implements Serializable {
   
	// #region "CodingProperties"
		private static final long serialVersionUID = 4600306662046762257L;
		
		private GeneralEquipmentItem generalEquipmentItem;

		private List<GeneralEquipmentItem> generalEquipmentItemList;

		private List<GeneralEquipmentItem> selectGeneralEquipmentItemList;
		
		private List<ItemType> itemTypes;
		
		private List<ChecklistDataSource> checklistDataSources;

		

		private GeneralEquipmentItemService generalEquipmentItemService;
		private ItemTypeService itemTypeService;
		private ChecklistDataSourceService checklistDataSourceService;
		private LocalizationService localizationService;
		// #endregion
		@PostConstruct
		public void init() {

			try {
				generalEquipmentItem = new GeneralEquipmentItem();
				generalEquipmentItemList= new ArrayList<GeneralEquipmentItem>();
				selectGeneralEquipmentItemList= new ArrayList<GeneralEquipmentItem>();
				generalEquipmentItemList=generalEquipmentItemService.findAll();
				itemTypes=itemTypeService.findAll();
				checklistDataSources=checklistDataSourceService.findAll();
				
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
				e.printStackTrace();
			}
		}
		
		public GeneralEquipmentItemBean() {
			generalEquipmentItemService = (GeneralEquipmentItemService) BeanUtility.getBean("generalEquipmentItemService");
			itemTypeService=(ItemTypeService) BeanUtility.getBean("itemTypeService");
			checklistDataSourceService=(ChecklistDataSourceService) BeanUtility.getBean("checklistDataSourceService");
			localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
		}
		
		@PreDestroy
		public void destroy() {

		}

		public void openNew() {

			this.generalEquipmentItem = new GeneralEquipmentItem();
			Integer maxFormCode = generalEquipmentItemService.getMaxGeneralEquipmentCode();
			generalEquipmentItem.setItemCode(String.format("%0" + 3 + "d", maxFormCode + 1));
		}
		
		

		public boolean doValidate() {
			boolean result = true;
			if (generalEquipmentItem.getItemText() == null || generalEquipmentItem.getItemText().trim().isEmpty()) {
				UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
						+ localizationService.getErrorMessage().getString("validateInput"));
				result = false;
			}

			return result;
		}
		
		public void save() {
			if (doValidate()) {
				try {
					generalEquipmentItemService.saveOrUpdate(generalEquipmentItem);
					generalEquipmentItemList=generalEquipmentItemService.findAll();
					UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
					PrimeFaces.current().ajax().update("form:messages", "form:dataTable");
					PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
				} catch (Exception e) {
					UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
					PrimeFaces.current().ajax().update("form:messages", "form:dataTable");
					e.printStackTrace();
				}
			}
		}

		public boolean hasSelected() {
			return this.selectGeneralEquipmentItemList != null && !this.selectGeneralEquipmentItemList.isEmpty();
		}
		
		public String getDeleteButtonMessage() {

			if (hasSelected()) {
				int size = this.selectGeneralEquipmentItemList.size();
				return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("generalEquipmentItemSelectedNum")
						: localizationService.getInterfaceLabel().getString("oneGeneralEquipmentItemSelected");
			}

			return localizationService.getInterfaceLabel().getString("delete");
		}

		public void delete() {
			try {

				generalEquipmentItemService.delete(generalEquipmentItem);
				this.generalEquipmentItem = null;
				generalEquipmentItemList = generalEquipmentItemService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable");
				PrimeFaces.current().executeScript("PF('widgetVarDataTable').clearFilters()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable");
				e.printStackTrace();
			}
		}

		public void deleteSelected() {
			try {   
				generalEquipmentItemService.delete(selectGeneralEquipmentItemList);
				this.selectGeneralEquipmentItemList = null;
				generalEquipmentItemList = generalEquipmentItemService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable");
				PrimeFaces.current().executeScript("PF('widgetVarDataTable').clearFilters()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable");
				e.printStackTrace();
			}

		}

		public GeneralEquipmentItem getGeneralEquipmentItem() {
			return generalEquipmentItem;
		}

		public void setGeneralEquipmentItem(GeneralEquipmentItem generalEquipmentItem) {
			this.generalEquipmentItem = generalEquipmentItem;
		}

		public List<GeneralEquipmentItem> getGeneralEquipmentItemList() {
			return generalEquipmentItemList;
		}

		public void setGeneralEquipmentItemList(List<GeneralEquipmentItem> generalEquipmentItemList) {
			this.generalEquipmentItemList = generalEquipmentItemList;
		}

		public List<GeneralEquipmentItem> getSelectGeneralEquipmentItemList() {
			return selectGeneralEquipmentItemList;
		}

		public void setSelectGeneralEquipmentItemList(List<GeneralEquipmentItem> selectGeneralEquipmentItemList) {
			this.selectGeneralEquipmentItemList = selectGeneralEquipmentItemList;
		}

		public List<ItemType> getItemTypes() {
			return itemTypes;
		}

		public void setItemTypes(List<ItemType> itemTypes) {
			this.itemTypes = itemTypes;
		}

		public List<ChecklistDataSource> getChecklistDataSources() {
			return checklistDataSources;
		}

		public void setChecklistDataSources(List<ChecklistDataSource> checklistDataSources) {
			this.checklistDataSources = checklistDataSources;
		}	
	
}
