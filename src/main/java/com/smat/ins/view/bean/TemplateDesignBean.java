
package com.smat.ins.view.bean;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.shaded.commons.io.FilenameUtils;

import com.smat.ins.model.entity.ChecklistDetailDataSource;
import com.smat.ins.model.entity.ColumnContent;
import com.smat.ins.model.entity.EquipmentCategory;
import com.smat.ins.model.entity.FormColumn;
import com.smat.ins.model.entity.FormRow;
import com.smat.ins.model.entity.FormTemplate;
import com.smat.ins.model.entity.GeneralEquipmentItem;
import com.smat.ins.model.entity.PrintedDoc;
import com.smat.ins.model.service.ChecklistDetailDataSourceService;
import com.smat.ins.model.service.ColumnContentService;
import com.smat.ins.model.service.EquipmentCategoryService;
import com.smat.ins.model.service.FormColumnService;
import com.smat.ins.model.service.FormRowService;
import com.smat.ins.model.service.FormTemplateService;
import com.smat.ins.model.service.GeneralEquipmentItemService;

import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

@Named
@ViewScoped
public class TemplateDesignBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8391590484083765780L;

	// #region "properties"
	private Integer formId;
	private String ftStr;
	private Integer contentILoop;
	private Boolean isLocked;

	Integer maxColumnId = 0;
	Integer maxRowId = 0;
	Integer maxContentId = 0;

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public String getFtStr() {
		return ftStr;
	}

	public void setFtStr(String ftStr) {
		this.ftStr = ftStr;
	}

	public Boolean getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}

	private FormTemplate formTemplate;
	private FormTemplate copyTemplate;
	private List<FormTemplate> formTemplates;

	private GeneralEquipmentItem generalEquipmentItem;

	private List<GeneralEquipmentItem> generalEquipmentItems;

	private List<EquipmentCategory> equipmentCategories;

	private PrintedDoc printedDoc;
	private PrintedDoc copyPrintedDoc;

	public PrintedDoc getCopyPrintedDoc() {
		return copyPrintedDoc;
	}

	public void setCopyPrintedDoc(PrintedDoc copyPrintedDoc) {
		this.copyPrintedDoc = copyPrintedDoc;
	}

	private List<PrintedDoc> printedDocs;

	public FormTemplate getFormTemplate() {
		return formTemplate;
	}

	public void setFormTemplate(FormTemplate formTemplate) {
		this.formTemplate = formTemplate;
	}

	public FormTemplate getCopyTemplate() {
		return copyTemplate;
	}

	public void setCopyTemplate(FormTemplate copyTemplate) {
		this.copyTemplate = copyTemplate;
	}

	public List<FormTemplate> getFormTemplates() {
		return formTemplates;
	}

	public void setFormTemplates(List<FormTemplate> formTemplates) {
		this.formTemplates = formTemplates;
	}

	public GeneralEquipmentItem getGeneralEquipmentItem() {
		return generalEquipmentItem;
	}

	public void setGeneralEquipmentItem(GeneralEquipmentItem generalEquipmentItem) {
		this.generalEquipmentItem = generalEquipmentItem;
	}

	public List<GeneralEquipmentItem> getGeneralEquipmentItems() {
		return generalEquipmentItems;
	}

	public void setGeneralEquipmentItems(List<GeneralEquipmentItem> generalEquipmentItems) {
		this.generalEquipmentItems = generalEquipmentItems;
	}

	public List<EquipmentCategory> getEquipmentCategories() {
		return equipmentCategories;
	}

	public void setEquipmentCategories(List<EquipmentCategory> equipmentCategories) {
		this.equipmentCategories = equipmentCategories;
	}

	public PrintedDoc getPrintedDoc() {
		return printedDoc;
	}

	public void setPrintedDoc(PrintedDoc printedDoc) {
		this.printedDoc = printedDoc;
	}

	public List<PrintedDoc> getPrintedDocs() {
		return printedDocs;
	}

	public void setPrintedDocs(List<PrintedDoc> printedDocs) {
		this.printedDocs = printedDocs;
	}

	private List<FormRow> formRows;
	private List<FormRow> copyFormRows;

	public List<FormRow> getCopyFormRows() {
		return copyFormRows;
	}

	public void setCopyFormRows(List<FormRow> copyFormRows) {
		this.copyFormRows = copyFormRows;
	}

	public List<FormRow> getFormRows() {
		return formRows;
	}

	public void setFormRows(List<FormRow> formRows) {
		this.formRows = formRows;
	}

	private List<FormColumn> formColumns;

	private List<FormColumn> copyFormColumns;

	public List<FormColumn> getCopyFormColumns() {
		return copyFormColumns;
	}

	public void setCopyFormColumns(List<FormColumn> copyFormColumns) {
		this.copyFormColumns = copyFormColumns;
	}

	public List<FormColumn> getFormColumns() {
		return formColumns;
	}

	public void setFormColumns(List<FormColumn> formColumns) {
		this.formColumns = formColumns;
	}

	private List<ColumnContent> columnContents;
	private List<ColumnContent> copyColumnContents;

	public List<ColumnContent> getCopyColumnContents() {
		return copyColumnContents;
	}

	public void setCopyColumnContents(List<ColumnContent> copyColumnContents) {
		this.copyColumnContents = copyColumnContents;
	}

	public List<ColumnContent> getColumnContents() {
		return columnContents;
	}

	public void setColumnContents(List<ColumnContent> columnContents) {
		this.columnContents = columnContents;
	}

	private FormRow formRow;
	private FormColumn formColumn;
	private ColumnContent columnContent;

	private FormRow selectedFormRow;
	private FormColumn selectedFormColumn;
	private ColumnContent selectedColumnContent;

	public FormRow getSelectedFormRow() {
		return selectedFormRow;
	}

	public void setSelectedFormRow(FormRow selectedFormRow) {
		this.selectedFormRow = selectedFormRow;
	}

	public FormColumn getSelectedFormColumn() {
		return selectedFormColumn;
	}

	public void setSelectedFormColumn(FormColumn selectedFormColumn) {
		this.selectedFormColumn = selectedFormColumn;
	}

	public ColumnContent getSelectedColumnContent() {
		return selectedColumnContent;
	}

	public void setSelectedColumnContent(ColumnContent selectedColumnContent) {
		this.selectedColumnContent = selectedColumnContent;
	}

	public FormRow getFormRow() {
		return formRow;
	}

	public void setFormRow(FormRow formRow) {
		this.formRow = formRow;
	}

	public FormColumn getFormColumn() {
		return formColumn;
	}

	public void setFormColumn(FormColumn formColumn) {
		this.formColumn = formColumn;
	}

	public ColumnContent getColumnContent() {
		return columnContent;
	}

	public void setColumnContent(ColumnContent columnContent) {
		this.columnContent = columnContent;
	}

	private List<FormColumn> formColumnsPerRow;

	private List<ColumnContent> columnContentsPerColumn;

	public List<FormColumn> getFormColumnsPerRow() {
		return formColumnsPerRow;
	}

	public void setFormColumnsPerRow(List<FormColumn> formColumnsPerRow) {
		this.formColumnsPerRow = formColumnsPerRow;
	}

	public List<ColumnContent> getColumnContentsPerColumn() {
		return columnContentsPerColumn;
	}

	public void setColumnContentsPerColumn(List<ColumnContent> columnContentsPerColumn) {
		this.columnContentsPerColumn = columnContentsPerColumn;
	}

	public List<FormColumn> getFormColumnsPerRow(FormRow formRow) {
		List<FormColumn> resultsColumns = new ArrayList<FormColumn>();
		for (FormColumn formColumn : formColumns) {
			if (formColumn.getFormRow().getId().equals(formRow.getId()))
				resultsColumns.add(formColumn);
		}
		return resultsColumns;
	}

	public List<ColumnContent> getColumnContentByColumn(FormColumn formColumn) {
		List<ColumnContent> resultColumnContents = new ArrayList<ColumnContent>();
		for (ColumnContent columnContent : this.columnContents) {
			if (columnContent.getFormColumn().getId().equals(formColumn.getId())) {
				if (columnContent.getFormColumn().getFormRow().getId().equals(formColumn.getFormRow().getId()))
					resultColumnContents.add(columnContent);
			}
		}
		return resultColumnContents;
	}

	// #endregion

	// #region "services"

	private EquipmentCategoryService equipmentCategoryService;
	private FormTemplateService formTemplateService;
	private FormRowService formRowService;
	private FormColumnService formColumnService;
	private ColumnContentService columnContentService;
	private GeneralEquipmentItemService generalEquipmentItemService;
	private ChecklistDetailDataSourceService checklistDetailDataSourceService;
	private LocalizationService localizationService;
	// #endregion

	@Inject
	private LoginBean loginBean;

	public TemplateDesignBean() {
		super(); // TODO Auto-generated constructor
		formTemplateService = (FormTemplateService) BeanUtility.getBean("formTemplateService");
		formRowService = (FormRowService) BeanUtility.getBean("formRowService");
		formColumnService = (FormColumnService) BeanUtility.getBean("formColumnService");
		columnContentService = (ColumnContentService) BeanUtility.getBean("columnContentService");
		equipmentCategoryService = (EquipmentCategoryService) BeanUtility.getBean("equipmentCategoryService");
		generalEquipmentItemService = (GeneralEquipmentItemService) BeanUtility.getBean("generalEquipmentItemService");
		checklistDetailDataSourceService = (ChecklistDetailDataSourceService) BeanUtility
				.getBean("checklistDetailDataSourceService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
		formRows = new ArrayList<FormRow>();
		formColumns = new ArrayList<FormColumn>();
		columnContents = new ArrayList<ColumnContent>();
		formRow = new FormRow();
		formColumn = new FormColumn();
		columnContent = new ColumnContent();
		contentILoop = 0;
		isLocked = false;

		printedDoc = new PrintedDoc();
		printedDocs = new ArrayList<PrintedDoc>();
	}

	@PostConstruct
	public void init() throws Exception {
		ftStr = UtilityHelper.getRequestParameter("ft");
		try {
			if (ftStr != null) {
				formId = Integer.valueOf(UtilityHelper.decipher(ftStr));
				formTemplate = formTemplateService.getBy(formId);

				contentILoop = columnContentService.getMaxAliasNameCodeByCat(formTemplate.getPrefix());

				if (formTemplate != null) {
					if (formTemplate.getPrintedDoc() != null) {
						printedDoc = formTemplate.getPrintedDoc();
						printedDocs.add(printedDoc);
					}

					Boolean isFound = formTemplateService.checkIfUsed(formId);
					if (isFound)
						isLocked = true;
					formRows = formRowService.getBy(formId);
					if (formRows != null && !formRows.isEmpty()) {
						selectedFormRow = formRows.get(0);
						formColumnsPerRow = formColumnService.getBy(selectedFormRow.getId());
						if (selectedFormRow.getFormColumns() != null && !selectedFormRow.getFormColumns().isEmpty()) {
							selectedFormColumn = ((FormColumn) selectedFormRow.getFormColumns().iterator().next());
							columnContentsPerColumn = columnContentService.getBy(selectedFormColumn.getId());
						}
					}
					for (FormRow formRow : formRows) {
						formColumnsPerRow = formColumnService.getBy(formRow.getId());
						for (FormColumn formColumn : formColumnsPerRow) {
							columnContentsPerColumn = columnContentService.getBy(formColumn.getId());
							columnContents.addAll(columnContentsPerColumn);
						}
						formColumns.addAll(formColumnsPerRow);
					}
					PrimeFaces.current().ajax().update("form:panelGridDaynamicContent");
				}
			} else {
				formTemplate = new FormTemplate();
				Integer maxFormCode = formTemplateService.getMaxFormCode();
				formTemplate.setCode(String.format("%0" + 3 + "d", maxFormCode + 1));
			}
		} catch (Exception ex) {

		}

		formTemplates = formTemplateService.getWithAllRelated();
		generalEquipmentItems = generalEquipmentItemService.findAll();
		equipmentCategories = equipmentCategoryService.findAll();

	}

	public boolean doValidate(String ftStr) {
		boolean result = true;

		if (formTemplate.getEquipmentCategory() == null) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("equipmentCatReq"));
			result = false;
		}

		if (ftStr == null) {

			if (formTemplate.getTitle() == null || formTemplate.getTitle().isEmpty()) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("titleFieldReq"));
				result = false;
			}

			FormTemplate isExist = formTemplateService.getByTemplateName(formTemplate.getTitle().trim());
			if (isExist != null) {
				UtilityHelper
						.addErrorMessage(localizationService.getErrorMessage().getString("formTemplateIsAlreadyAdded"));
				result = false;
			}
		}

		if (formRows == null && formRows.size() == 0) {
			UtilityHelper
					.addErrorMessage(localizationService.getErrorMessage().getString("youShouldEnterOneRowAtLeast"));
			result = false;
		}

		if (formColumns == null && formColumns.size() == 0) {
			UtilityHelper
					.addErrorMessage(localizationService.getErrorMessage().getString("youShouldEnterOneColumnAtLeast"));
			result = false;
		}
		if (printedDocs == null || printedDocs.size() == 0) {
			UtilityHelper
					.addErrorMessage(localizationService.getErrorMessage().getString("youShouldUploadFileTemplate"));
			result = false;
		}
		return result;
	}

	public String doSave() {
		try {
			if (doValidate(ftStr)) {
				// Ensure proper ordering of rows before saving
				for (int i = 0; i < formRows.size(); i++) {
					FormRow row = formRows.get(i);
					row.setRowNum((short) (i + 1));
				}

				// Ensure proper ordering of columns within each row
				for (FormRow row : formRows) {
					short columnOrder = 1;
					for (FormColumn column : formColumns) {
						if ((ftStr == null && column.getFormRow().equals(row))
								|| (ftStr != null && column.getFormRow().getId().equals(row.getId()))) {
							column.setColumnOrder(columnOrder++);
						}
					}
				}

				// Ensure proper ordering of content within each column
				for (FormColumn column : formColumns) {
					short contentOrder = 1;
					for (ColumnContent content : columnContents) {
						if ((ftStr == null && content.getFormColumn().equals(column))
								|| (ftStr != null && content.getFormColumn().getId().equals(column.getId()))) {
							content.setContentOrder(contentOrder++);
						}
					}
				}

				formTemplate.setFormRows(new HashSet<>(formRows));
				for (FormRow formRow : formRows) {
					if (ftStr == null)
						formRow.setId(null);
					else if (formRow.getIsNew() != null && formRow.getIsNew())
						formRow.setId(null);

					Set<FormColumn> setformColumns = new HashSet<FormColumn>();
					for (FormColumn formColumn : formColumns) {
						if (ftStr == null) {
							if (formColumn.getFormRow().equals(formRow))
								setformColumns.add(formColumn);
						} else if (formRow.getIsNew() != null && formRow.getIsNew()) {
							if (formColumn.getFormRow().equals(formRow))
								setformColumns.add(formColumn);
						} else {
							if (formColumn.getFormRow().getId().equals(formRow.getId()))
								setformColumns.add(formColumn);
						}
					}
					formRow.setFormColumns(setformColumns);
				}

				for (FormColumn formColumn : formColumns) {
					if (ftStr == null)
						formColumn.setId(null);
					else if (formColumn.getIsNew() != null && formColumn.getIsNew())
						formColumn.setId(null);
					Set<ColumnContent> setColumnContents = new HashSet<ColumnContent>();
					for (ColumnContent columnContent : columnContents) {
						if (ftStr == null)
							columnContent.setId(null);
						else if (columnContent.getIsNew() != null && columnContent.getIsNew())
							columnContent.setId(null);
						if (ftStr == null) {
							if (columnContent.getFormColumn().equals(formColumn))
								setColumnContents.add(columnContent);
						} else if (columnContent.getIsNew() != null && columnContent.getIsNew()) {

							setColumnContents.add(columnContent);
						} else {
							if (columnContent.getFormColumn().getId().equals(formColumn.getId()))
								setColumnContents.add(columnContent);
						}
					}
					formColumn.setColumnContents(setColumnContents);
				}

				if (printedDocs != null && printedDocs.size() > 0)
					formTemplate.setPrintedDoc(printedDocs.get(0));

				if (ftStr == null)
					formTemplateService.saveOrUpdate(formTemplate);
				else
					formTemplateService.merge(formTemplate);

				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

				return "pretty:viewTemplate";
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
			return "";
		}
	}

	public void addRow() {

		formColumnsPerRow = new ArrayList<FormColumn>();
		if (ftStr == null)
			formRow.setId(formRows.size() + 1);
		else {
			if (maxRowId == 0)
				maxRowId = formRowService.getMaxId(formId) + 1;
			else
				maxRowId++;
			formRow.setId(maxRowId);
		}
		formRow.setFormTemplate(formTemplate);

		// Ensure proper row numbering
		short maxRowNum = 0;
		for (FormRow existingRow : formRows) {
			if (existingRow.getRowNum() > maxRowNum) {
				maxRowNum = existingRow.getRowNum();
			}
		}
		formRow.setRowNum((short) (maxRowNum + 1));
		formRow.setIsNew(true);
		formRows.add(formRow);
		selectedFormRow = formRow;
		formRow = new FormRow();

	}

	public void addColumn() {
		if (selectedFormRow == null) {
			UtilityHelper.addErrorMessage(
					localizationService.getErrorMessage().getString("youShouldSelectRowBeforeContinue"));
			return;
		}

		columnContentsPerColumn = new ArrayList<ColumnContent>();

		if (ftStr == null)
			formColumn.setId(formColumnsPerRow.size() + 1);
		else {
			if (maxColumnId == 0)
				maxColumnId = formColumnService.getMaxId(selectedFormRow.getId()) + 1;
			else
				maxColumnId++;
			formColumn.setId(maxColumnId);
		}
		formColumn.setFormRow(selectedFormRow);

		// Ensure proper column ordering
		short maxColumnOrder = 0;
		for (FormColumn existingColumn : formColumns) {
			if (existingColumn.getFormRow() != null && selectedFormRow != null
					&& existingColumn.getFormRow().getId() != null && selectedFormRow.getId() != null
					&& existingColumn.getFormRow().getId().equals(selectedFormRow.getId())
					&& existingColumn.getColumnOrder() > maxColumnOrder) {
				maxColumnOrder = existingColumn.getColumnOrder();
			}
		}
		formColumn.setColumnOrder((short) (maxColumnOrder + 1));
		formColumn.setIsNew(true);
		formColumnsPerRow.add(formColumn);
		formColumns.add(formColumn);
		selectedFormColumn = formColumn;
		formColumn = new FormColumn();

	}

	public void addColumnContent() {
		if (selectedFormColumn == null) {
			UtilityHelper.addErrorMessage(
					localizationService.getErrorMessage().getString("youShouldSelectColumnBeforeContinue"));
			return;
		}
		if (ftStr == null)
			columnContent.setId(columnContentsPerColumn.size() + 1);
		else {
			if (maxContentId == 0)
				maxContentId = columnContentService.getMaxId(selectedFormColumn.getId());
			else
				maxContentId++;
			columnContent.setId(maxContentId);
		}
		columnContent.setFormColumn(selectedFormColumn);
		columnContent.setGeneralEquipmentItem(generalEquipmentItem);

		// Ensure proper content ordering
		short maxContentOrder = 0;
		for (ColumnContent existingContent : columnContents) {
			if (existingContent.getFormColumn() != null && selectedFormColumn != null
					&& existingContent.getFormColumn().getId() != null && selectedFormColumn.getId() != null
					&& existingContent.getFormColumn().getId().equals(selectedFormColumn.getId())
					&& existingContent.getContentOrder() > maxContentOrder) {
				maxContentOrder = existingContent.getContentOrder();
			}
		}
		columnContent.setContentOrder((short) (maxContentOrder + 1));
		columnContent.setIsNew(true);
		columnContentsPerColumn.add(columnContent);
		columnContents.add(columnContent);
		columnContent = new ColumnContent();
		contentILoop++;
	}

	public void assignFormId(FormTemplate formTemplate) throws Exception {
		this.formTemplate = formTemplate;
		ftStr = UtilityHelper.cipher(formTemplate.getId().toString());
	}

	public void deleteRow(FormRow formRow) {
		try {
			for (FormColumn formColumn : formColumns) {
				if (ftStr != null) {
					if (formColumn.getFormRow().getId().equals(formRow.getId())) {
						UtilityHelper.addErrorMessage(
								localizationService.getErrorMessage().getString("youShouldDeleteRelatedColumnFirst"));
						return;
					}
				}
				if (formColumn.getFormRow().equals(formRow)) {
					UtilityHelper.addErrorMessage(
							localizationService.getErrorMessage().getString("youShouldDeleteRelatedColumnFirst"));
					return;
				}
			}
			if (ftStr == null) {
				formRows.remove(formRow);
				formTemplate.getFormRows().remove(formRow);
			} else {
				for (int i = 0; i < formRows.size(); i++) {
					if (formRow.getId().equals(formRows.get(i).getId()))
						formRows.remove(i);
				}
			}

			// Update row numbers for remaining rows to maintain proper ordering
			short rowNum = 1;
			for (FormRow row : formRows) {
				row.setRowNum(rowNum++);
			}

			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
		} catch (Exception e) {
			e.printStackTrace();
			UtilityHelper.addErrorMessage(
					localizationService.getErrorMessage().getString("youShouldDeleteRelatedColumnFirst"));
		}
	}

	public void deleteColumn(FormColumn formColumn) {
		try {
			for (ColumnContent columnContent : columnContents) {
				if (ftStr != null) {
					if (columnContent.getFormColumn().getId().equals(formColumn.getId())) {
						UtilityHelper.addErrorMessage(
								localizationService.getErrorMessage().getString("youShouldDeleteRelatedContentFirst"));
						return;
					}
				}
				if (columnContent.getFormColumn().equals(formColumn)) {
					UtilityHelper.addErrorMessage(
							localizationService.getErrorMessage().getString("youShouldDeleteRelatedContentFirst"));
					return;
				}
			}

			// Store the row ID before removing the column
			Integer rowId = formColumn.getFormRow().getId();

			if (ftStr == null) {
				formColumns.remove(formColumn);
				formColumnsPerRow.remove(formColumn);
				if (selectedFormRow != null)
					selectedFormRow.getFormColumns().remove(formColumn);
			} else {
				for (int i = 0; i < formColumns.size(); i++) {
					if (formColumn.getId().equals(formColumns.get(i).getId()))
						formColumns.remove(i);
				}

				for (int i = 0; i < formColumnsPerRow.size(); i++) {
					if (formColumn.getId().equals(formColumnsPerRow.get(i).getId()))
						formColumnsPerRow.remove(i);
				}
			}

			// Update column order for remaining columns in the same row
			short columnOrder = 1;
			for (FormColumn col : formColumns) {
				if (col.getFormRow().getId().equals(rowId)) {
					col.setColumnOrder(columnOrder++);
				}
			}

			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
		} catch (Exception e) {
			e.printStackTrace();
			UtilityHelper.addErrorMessage(
					localizationService.getErrorMessage().getString("youShouldDeleteRelatedContentFirst"));
		}
	}

	public void deleteContent(ColumnContent columnContent) {
		try {
			// Store the column ID before removing the content
			Integer columnId = columnContent.getFormColumn().getId();

			if (ftStr == null) {
				columnContents.remove(columnContent);
				columnContentsPerColumn.remove(columnContent);
				if (selectedFormColumn != null)
					selectedFormColumn.getColumnContents().remove(columnContent);
			} else {

				for (int i = 0; i < columnContents.size(); i++) {
					if (columnContent.getId().equals(columnContents.get(i).getId()))
						columnContents.remove(i);
				}

				for (int i = 0; i < columnContentsPerColumn.size(); i++) {
					if (columnContent.getId().equals(columnContentsPerColumn.get(i).getId()))
						columnContentsPerColumn.remove(i);
				}
			}

			// Update content order for remaining content in the same column
			short contentOrder = 1;
			for (ColumnContent content : columnContents) {
				if (content.getFormColumn().getId().equals(columnId)) {
					content.setContentOrder(contentOrder++);
				}
			}

			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

		} catch (Exception e) {
			e.printStackTrace();
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
		}
	}

	private String calcPrefix(String eCat) {
		String initials = "";
		for (String s : eCat.split(" ")) {
			initials += s.charAt(0);
		}
		return initials.toLowerCase();
	}

	public void applyPrefix() {
		FormTemplate isExist = formTemplateService.getBy(formTemplate.getEquipmentCategory().getCode());
		if (isExist != null) {
			UtilityHelper
					.addErrorMessage(localizationService.getErrorMessage().getString("formTemplateIsAlreadyAdded"));
			formTemplate.setEquipmentCategory(null);
			return;
		}

		String prefix = calcPrefix(formTemplate.getEquipmentCategory().getEnglishName());
		formTemplate.setPrefix(prefix);
	}

	public void deleteTemplate(FormTemplate formTemplate) {
		try {
			Boolean isFound = formTemplateService.checkIfUsed(formTemplate.getId());
			if (isFound) {
				UtilityHelper.addErrorMessage(
						localizationService.getErrorMessage().getString("youCannotDeleteFormTemplate"));
				return;
			}
			formTemplateService.delete(formTemplate);
			formTemplates = formTemplateService.getWithAllRelated();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

		} catch (Exception e) {
			e.fillInStackTrace();
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
		}
	}

	public void applyContentAliasName() {
		if (formTemplate.getEquipmentCategory() == null) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("equipmentCatReq"));
			return;
		}

		Integer maxAliasNameCode = 0;
		if (contentILoop == 0) {
			maxAliasNameCode = columnContentService.getMaxAliasNameCodeByCat(formTemplate.getPrefix());
			columnContent
					.setAliasName(formTemplate.getPrefix() + "." + String.format("%0" + 3 + "d", maxAliasNameCode + 1));
		} else {

			columnContent
					.setAliasName(formTemplate.getPrefix() + "." + String.format("%0" + 3 + "d", contentILoop + 1));

		}

	}

	public List<ChecklistDetailDataSource> getBy(String dataSourceCode) {
		return checklistDetailDataSourceService.getByDataSource(dataSourceCode);
	}

	public void listFormColumnsPerRow() {
		if (selectedFormRow != null  ) {
			if(selectedFormRow.getIsNew() !=null && selectedFormRow.getIsNew())
				formColumnsPerRow = new ArrayList<FormColumn>();
			else
			    formColumnsPerRow = formColumnService.getBy(selectedFormRow.getId());
			if (formColumnsPerRow != null && !formColumnsPerRow.isEmpty()) {
				selectedFormColumn = formColumnsPerRow.get(0);
				
				if (selectedFormColumn != null) {
					if(selectedFormColumn.getIsNew()!=null && selectedFormColumn.getIsNew())
						columnContentsPerColumn = new ArrayList<ColumnContent>();
					else
					    columnContentsPerColumn = columnContentService.getBy(selectedFormColumn.getId());
				}
				else
					columnContentsPerColumn = new ArrayList<ColumnContent>();
			}
		} else
			formColumnsPerRow = new ArrayList<FormColumn>();
	}

	public void listColumnContentPerColumn() {
		if (selectedFormColumn != null) {
			if(selectedFormColumn.getIsNew()!=null && selectedFormColumn.getIsNew())
				columnContentsPerColumn = new ArrayList<ColumnContent>();
			else
			    columnContentsPerColumn = columnContentService.getBy(selectedFormColumn.getId());
		}
		else
			columnContentsPerColumn = new ArrayList<ColumnContent>();
	}

	public String docImgIconByDocEx(String docEx) {
		switch (docEx) {
		case "png":
			return "images/photography-icon-png-7.png";
		case "jpg":
			return "images/photography-icon-png-7.png";
		case "jpeg":
			return "images/photography-icon-png-7.png";
		case "tif":
			return "images/photography-icon-png-7.png";
		case "gif":
			return "images/photography-icon-png-7.png";
		case "pdf":
			return "images/PDF-doc-256.png";
		case "txt":
			return "images/txt-300x300.png";
		case "pptx":
			return "images/microsoft-powerpoint.png";
		case "ppt":
			return "images/microsoft-powerpoint.png";
		case "docx":
			return "images/microsoft-icon-png-12761.png";
		case "dotx":
			return "images/microsoft-icon-png-12761.png";
		case "doc":
			return "images/microsoft-icon-png-12761.png";
		case "xlsx":
			return "images/microsoft-office-excel.png";
		case "xls":
			return "images/microsoft-office-excel.png";
		default:
			return "images/pngegg.png";
		}
	}

	public void uploadArchiveDocToSelectedArchive(FileUploadEvent event) {
		try {
			String fileName = new String(event.getFile().getFileName().getBytes("ISO-8859-1"), "UTF-8");
			if (fileName.length() > 250) {
				UtilityHelper
						.addWarnMessage(localizationService.getInterfaceLabel().getString("fileNameMaxLengthShouldBe")
								+ ":  " + fileName);
				return;
			}
			PrintedDoc archiveDocumentFile = new PrintedDoc();

			archiveDocumentFile.setName(fileName);
			archiveDocumentFile.setExtension(FilenameUtils.getExtension(event.getFile().getFileName()));
			archiveDocumentFile.setFileSize(event.getFile().getSize());
			archiveDocumentFile.setMimeType(event.getFile().getContentType());
			archiveDocumentFile.setData(event.getFile().getContent());

			if (printedDocs.size() == 1) {
				UtilityHelper.addWarnMessage(
						localizationService.getInterfaceLabel().getString("youCannotUploadMoreThanOneFile"));
				return;
			}

			if (!foundInUploader(archiveDocumentFile))
				printedDocs.add(archiveDocumentFile);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	public boolean foundInUploader(PrintedDoc archiveDocumentInstance) throws UnsupportedEncodingException {
		boolean result = false;
		for (PrintedDoc archiveDocumentFile : printedDocs) {
			if (archiveDocumentFile.getName().trim().equalsIgnoreCase(archiveDocumentInstance.getName().trim())
					&& archiveDocumentFile.getExtension().trim()
							.equalsIgnoreCase(archiveDocumentInstance.getExtension().trim())
					&& archiveDocumentFile.getFileSize().equals(archiveDocumentInstance.getFileSize())) {
				UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("theFile") + " ( "
						+ archiveDocumentInstance.getName().trim() + " ) "
						+ localizationService.getInterfaceLabel().getString("hadNotBeenAdded"));
				PrimeFaces.current().ajax().update("form:messages");
				result = true;
			}
		}
		return result;
	}

	public void deleteDocFile(PrintedDoc archiveDocumentFile) {
		printedDocs.remove(archiveDocumentFile);
	}

	public void applyCopyPrefix() {
		FormTemplate isExist = formTemplateService.getBy(copyTemplate.getEquipmentCategory().getCode());
		if (isExist != null) {
			UtilityHelper
					.addErrorMessage(localizationService.getErrorMessage().getString("formTemplateIsAlreadyAdded"));
			copyTemplate.setEquipmentCategory(null);
			return;
		}

		String prefix = calcPrefix(copyTemplate.getEquipmentCategory().getEnglishName());
		copyTemplate.setPrefix(prefix);

		Integer contentILoop = 0;
		for (ColumnContent columnContent : this.copyColumnContents) {
			columnContent.setAliasName(copyTemplate.getPrefix() + "." + String.format("%0" + 3 + "d", ++contentILoop));
		}

	}

	public void copyTemplate(FormTemplate formTemplate) {
		copyFormRows = new ArrayList<FormRow>();
		copyFormColumns = new ArrayList<FormColumn>();
		copyColumnContents = new ArrayList<ColumnContent>();
		this.copyTemplate = formTemplate;
		this.copyTemplate.setTitle("Copy of " + formTemplate.getTitle());
		this.copyTemplate.setDescription("Copy of " + formTemplate.getDescription());
		Integer maxFormCode = formTemplateService.getMaxFormCode();
		this.copyTemplate.setCode(String.format("%0" + 3 + "d", maxFormCode + 1));
		if (copyTemplate != null) {
			copyFormRows = formRowService.getBy(copyTemplate.getId());
			for (FormRow formRow : copyFormRows) {
				formRow.setFormTemplate(copyTemplate);
				List<FormColumn> formColumnsPerRow = formColumnService.getBy(formRow.getId());
				for (FormColumn formColumn : formColumnsPerRow) {
					formColumn.setFormRow(formRow);
					List<ColumnContent> columnContentsPerColumn = columnContentService.getBy(formColumn.getId());
					for (ColumnContent columnContent : columnContentsPerColumn) {
						columnContent.setId(null);
						columnContent.setFormColumn(formColumn);
					}
					formColumn.setId(null);
					formColumn.setColumnContents(new HashSet<>(columnContentsPerColumn));
					copyColumnContents.addAll(columnContentsPerColumn);

				}
				copyFormColumns.addAll(formColumnsPerRow);
				formRow.setId(null);
				formRow.setFormColumns(new HashSet<>(formColumnsPerRow));
			}
			copyTemplate.setId(null);
			copyTemplate.setFormRows(new HashSet<>(copyFormRows));
			copyPrintedDoc = formTemplate.getPrintedDoc();
			if (copyPrintedDoc != null)
				copyPrintedDoc.setId(null);
		}

	}

	public void doSaveCopyTemplate() {
		try {
			/**
			 * pre insert
			 */
			FormTemplate isExist = formTemplateService.getBy(copyTemplate.getEquipmentCategory().getCode());
			if (isExist != null) {
				UtilityHelper
						.addErrorMessage(localizationService.getErrorMessage().getString("formTemplateIsAlreadyAdded"));
				copyTemplate.setEquipmentCategory(null);
				return;
			}

			formTemplateService.save(copyTemplate);

			initCopy();

			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
		}
	}

	public void initCopy() throws Exception {
		formRows = new ArrayList<FormRow>();
		formColumns = new ArrayList<FormColumn>();
		columnContents = new ArrayList<ColumnContent>();
		formRow = new FormRow();
		formColumn = new FormColumn();
		columnContent = new ColumnContent();
		contentILoop = 0;
		isLocked = false;

		printedDoc = new PrintedDoc();
		printedDocs = new ArrayList<PrintedDoc>();
		ftStr = UtilityHelper.getRequestParameter("ft");
		try {
			if (ftStr != null) {
				formId = Integer.valueOf(UtilityHelper.decipher(ftStr));
				formTemplate = formTemplateService.getBy(formId);

				contentILoop = columnContentService.getMaxAliasNameCodeByCat(formTemplate.getPrefix());

				if (formTemplate != null) {
					if (formTemplate.getPrintedDoc() != null) {
						printedDoc = formTemplate.getPrintedDoc();
						printedDocs.add(printedDoc);
					}

					Boolean isFound = formTemplateService.checkIfUsed(formId);
					if (isFound)
						isLocked = true;
					formRows = formRowService.getBy(formId);
					if (formRows != null && !formRows.isEmpty()) {
						selectedFormRow = formRows.get(0);
						formColumnsPerRow = formColumnService.getBy(selectedFormRow.getId());
						if (selectedFormRow.getFormColumns() != null && !selectedFormRow.getFormColumns().isEmpty()) {
							selectedFormColumn = ((FormColumn) selectedFormRow.getFormColumns().iterator().next());
							columnContentsPerColumn = columnContentService.getBy(selectedFormColumn.getId());
						}
					}
					for (FormRow formRow : formRows) {
						formColumnsPerRow = formColumnService.getBy(formRow.getId());
						for (FormColumn formColumn : formColumnsPerRow) {
							columnContentsPerColumn = columnContentService.getBy(formColumn.getId());
							columnContents.addAll(columnContentsPerColumn);
						}
						formColumns.addAll(formColumnsPerRow);
					}
					PrimeFaces.current().ajax().update("form:panelGridDaynamicContent");
				}
			} else {
				formTemplate = new FormTemplate();
				Integer maxFormCode = formTemplateService.getMaxFormCode();
				formTemplate.setCode(String.format("%0" + 3 + "d", maxFormCode + 1));
			}
		} catch (Exception ex) {

		}

		formTemplates = formTemplateService.getWithAllRelated();
		generalEquipmentItems = generalEquipmentItemService.findAll();
		equipmentCategories = equipmentCategoryService.findAll();
	}

}
