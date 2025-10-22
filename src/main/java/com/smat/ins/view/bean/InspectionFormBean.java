package com.smat.ins.view.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.MarkupLevel;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.ReportingEngine;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.SdtType;
import com.aspose.words.StructuredDocumentTag;
import com.google.common.io.ByteSource;
import com.smat.ins.model.entity.ChecklistDetailDataSource;
import com.smat.ins.model.entity.ColumnContent;
import com.smat.ins.model.entity.Company;
import com.smat.ins.model.entity.EquipmentCategory;
import com.smat.ins.model.entity.EquipmentInspectionCertificate;
import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.EquipmentInspectionFormItem;
import com.smat.ins.model.entity.EquipmentType;
import com.smat.ins.model.entity.ExaminationType;
import com.smat.ins.model.entity.FormColumn;
import com.smat.ins.model.entity.FormRow;
import com.smat.ins.model.entity.FormTemplate;
import com.smat.ins.model.entity.GeneralEquipmentItem;
import com.smat.ins.model.entity.InspectionFormWorkflow;
import com.smat.ins.model.entity.InspectionFormWorkflowStep;
import com.smat.ins.model.entity.Sticker;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.Task;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.entity.WorkflowDefinition;
import com.smat.ins.model.service.ChecklistDetailDataSourceService;
import com.smat.ins.model.service.ColumnContentService;
import com.smat.ins.model.service.CompanyService;
import com.smat.ins.model.service.EquipmentCategoryService;
import com.smat.ins.model.service.EquipmentInspectionCertificateService;
import com.smat.ins.model.service.EquipmentInspectionFormService;
import com.smat.ins.model.service.EquipmentTypeService;
import com.smat.ins.model.service.ExaminationTypeService;
import com.smat.ins.model.service.FormColumnService;
import com.smat.ins.model.service.FormRowService;
import com.smat.ins.model.service.FormTemplateService;
import com.smat.ins.model.service.InspectionFormWorkflowService;
import com.smat.ins.model.service.InspectionFormWorkflowStepService;
import com.smat.ins.model.service.StickerService;
import com.smat.ins.model.service.SysUserService;
import com.smat.ins.model.service.TaskService;
import com.smat.ins.model.service.UserAliasService;
import com.smat.ins.model.service.WorkflowDefinitionService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;

import com.smat.ins.util.QRCodeGenerator;
import com.smat.ins.util.UtilityHelper;
import com.aspose.words.*;

@Named
@ViewScoped
public class InspectionFormBean implements Serializable {

	// #region "properties"

	/**
	 * 
	 */
	private static final long serialVersionUID = -3900928087796493653L;
	private String equipmentCatStr;
	private String equipmentCatCode;

	private String taskIdStr;
	private Integer taskId;

	private String permission;

	private boolean disabled;

	private String persistentMode;

	private String step;

	private String stepComment;
	private boolean viewOnly;
	private boolean disableSticker;

	public String getStepComment() {
		return stepComment;
	}

	public void setStepComment(String stepComment) {
		this.stepComment = stepComment;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getPersistentMode() {
		return persistentMode;
	}

	public void setPersistentMode(String persistentMode) {
		this.persistentMode = persistentMode;
	}

	private String comment;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	

	public boolean isDisableSticker() {
		return disableSticker;
	}

	public void setDisableSticker(boolean disableSticker) {
		this.disableSticker = disableSticker;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getEquipmentCatStr() {
		return equipmentCatStr;
	}

	public void setEquipmentCatStr(String equipmentCatStr) {
		this.equipmentCatStr = equipmentCatStr;
	}

	public String getEquipmentCatCode() {
		return equipmentCatCode;
	}

	public void setEquipmentCatCode(String equipmentCatCode) {
		this.equipmentCatCode = equipmentCatCode;
	}

	public String getTaskIdStr() {
		return taskIdStr;
	}

	public void setTaskIdStr(String taskIdStr) {
		this.taskIdStr = taskIdStr;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	private EquipmentCategory equipmentCategory;

	private EquipmentInspectionForm equipmentInspectionForm;
	private InspectionFormWorkflow inspectionFormWorkflow;
	private InspectionFormWorkflowStep inspectionFormWorkflowStep;

	private EquipmentType equipmentType;

	private ExaminationType examinationType;

	private Company company;

	private List<Company> companies;

	private List<EquipmentType> equipmentTypes;

	private List<ExaminationType> examinationTypes;

	private List<Sticker> stickers;
	private List<UserAlias> userAliasRecipientList;
	private UserAlias selectedUserAliasRecipient;

	private FormTemplate formTemplate;
	private GeneralEquipmentItem generalEquipmentItem;
	private List<GeneralEquipmentItem> generalEquipmentItems;
	private List<FormRow> formRows;
	private List<FormColumn> formColumns;
	private List<ColumnContent> columnContents;
	private FormRow formRow;
	private FormColumn formColumn;
	private ColumnContent columnContent;
	private FormRow selectedFormRow;
	private FormColumn selectedFormColumn;
	private ColumnContent selectedColumnContent;
	private List<FormColumn> formColumnsPerRow;
	private List<ColumnContent> columnContentsPerColumn;
	private Task task;

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

	public List<FormRow> getFormRows() {
		return formRows;
	}

	public void setFormRows(List<FormRow> formRows) {
		this.formRows = formRows;
	}

	public List<FormColumn> getFormColumns() {
		return formColumns;
	}

	public void setFormColumns(List<FormColumn> formColumns) {
		this.formColumns = formColumns;
	}

	public List<ColumnContent> getColumnContents() {
		return columnContents;
	}

	public void setColumnContents(List<ColumnContent> columnContents) {
		this.columnContents = columnContents;
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

	public EquipmentCategory getEquipmentCategory() {
		return equipmentCategory;
	}

	public void setEquipmentCategory(EquipmentCategory equipmentCategory) {
		this.equipmentCategory = equipmentCategory;
	}

	public EquipmentInspectionForm getEquipmentInspectionForm() {
		return equipmentInspectionForm;
	}

	public void setEquipmentInspectionForm(EquipmentInspectionForm equipmentInspectionForm) {
		this.equipmentInspectionForm = equipmentInspectionForm;
	}

	public InspectionFormWorkflow getInspectionFormWorkflow() {
		return inspectionFormWorkflow;
	}

	public void setInspectionFormWorkflow(InspectionFormWorkflow inspectionFormWorkflow) {
		this.inspectionFormWorkflow = inspectionFormWorkflow;
	}

	public InspectionFormWorkflowStep getInspectionFormWorkflowStep() {
		return inspectionFormWorkflowStep;
	}

	public void setInspectionFormWorkflowStep(InspectionFormWorkflowStep inspectionFormWorkflowStep) {
		this.inspectionFormWorkflowStep = inspectionFormWorkflowStep;
	}

	public EquipmentType getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(EquipmentType equipmentType) {
		this.equipmentType = equipmentType;
	}

	public ExaminationType getExaminationType() {
		return examinationType;
	}

	public void setExaminationType(ExaminationType examinationType) {
		this.examinationType = examinationType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public FormTemplate getFormTemplate() {
		return formTemplate;
	}

	public void setFormTemplate(FormTemplate formTemplate) {
		this.formTemplate = formTemplate;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}

	public List<EquipmentType> getEquipmentTypes() {
		return equipmentTypes;
	}

	public void setEquipmentTypes(List<EquipmentType> equipmentTypes) {
		this.equipmentTypes = equipmentTypes;
	}

	public List<ExaminationType> getExaminationTypes() {
		return examinationTypes;
	}

	public void setExaminationTypes(List<ExaminationType> examinationTypes) {
		this.examinationTypes = examinationTypes;
	}

	public List<Sticker> getStickers() {
		return stickers;
	}

	public void setStickers(List<Sticker> stickers) {
		this.stickers = stickers;
	}

	public List<UserAlias> getUserAliasRecipientList() {
		return userAliasRecipientList;
	}

	public void setUserAliasRecipientList(List<UserAlias> userAliasRecipientList) {
		this.userAliasRecipientList = userAliasRecipientList;
	}

	public UserAlias getSelectedUserAliasRecipient() {
		return selectedUserAliasRecipient;
	}

	public void setSelectedUserAliasRecipient(UserAlias selectedUserAliasRecipient) {
		this.selectedUserAliasRecipient = selectedUserAliasRecipient;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
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

	private EquipmentInspectionFormService equipmentInspectionFormService;

	private InspectionFormWorkflowService inspectionFormWorkflowService;

	private InspectionFormWorkflowStepService inspectionFormWorkflowStepService;

	private EquipmentTypeService equipmentTypeService;

	private EquipmentCategoryService equipmentCategoryService;

	private ExaminationTypeService examinationTypeService;

	private ChecklistDetailDataSourceService checklistDetailDataSourceService;
	private WorkflowDefinitionService workflowDefinitionService;

	private CompanyService companyService;
	private StickerService stickerService;
	private FormTemplateService formTemplateService;
	private FormRowService formRowService;
	private FormColumnService formColumnService;
	private ColumnContentService columnContentService;
	private TaskService taskService;
	private EquipmentInspectionCertificateService equipmentInspectionCertificateService;
	private UserAliasService userAliasService;
	private SysUserService sysUserService;

	private LocalizationService localizationService;

	// #endregion

	@Inject
	private LoginBean loginBean;

	public InspectionFormBean() {
		super();
		// TODO Auto-generated constructor stub
		try {
			equipmentTypeService = (EquipmentTypeService) BeanUtility.getBean("equipmentTypeService");
			equipmentCategoryService = (EquipmentCategoryService) BeanUtility.getBean("equipmentCategoryService");
			examinationTypeService = (ExaminationTypeService) BeanUtility.getBean("examinationTypeService");
			equipmentInspectionFormService = (EquipmentInspectionFormService) BeanUtility
					.getBean("equipmentInspectionFormService");

			checklistDetailDataSourceService = (ChecklistDetailDataSourceService) BeanUtility
					.getBean("checklistDetailDataSourceService");
			workflowDefinitionService = (WorkflowDefinitionService) BeanUtility.getBean("workflowDefinitionService");
			companyService = (CompanyService) BeanUtility.getBean("companyService");
			stickerService = (StickerService) BeanUtility.getBean("stickerService");
			localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
			formTemplateService = (FormTemplateService) BeanUtility.getBean("formTemplateService");
			formRowService = (FormRowService) BeanUtility.getBean("formRowService");
			formColumnService = (FormColumnService) BeanUtility.getBean("formColumnService");
			columnContentService = (ColumnContentService) BeanUtility.getBean("columnContentService");
			taskService = (TaskService) BeanUtility.getBean("taskService");
			inspectionFormWorkflowService = (InspectionFormWorkflowService) BeanUtility
					.getBean("inspectionFormWorkflowService");
			equipmentInspectionCertificateService = (EquipmentInspectionCertificateService) BeanUtility
					.getBean("equipmentInspectionCertificateService");

			inspectionFormWorkflowStepService = (InspectionFormWorkflowStepService) BeanUtility
					.getBean("inspectionFormWorkflowStepService");
			userAliasService = (UserAliasService) BeanUtility.getBean("userAliasService");
			sysUserService = (SysUserService) BeanUtility.getBean("sysUserService");

			formRows = new ArrayList<FormRow>();
			formColumns = new ArrayList<FormColumn>();
			columnContents = new ArrayList<ColumnContent>();
			formRow = new FormRow();
			formColumn = new FormColumn();
			columnContent = new ColumnContent();
			userAliasRecipientList = new ArrayList<UserAlias>();
			selectedUserAliasRecipient = new UserAlias();
			SysUser sysUserLogin = (SysUser) UtilityHelper.getSessionAttr("user");
			List<UserAlias> myUserAliasList = userAliasService.getBySysUser(sysUserLogin);
			if (myUserAliasList != null && !myUserAliasList.isEmpty()) {
				UserAlias userAliasOwner = myUserAliasList.get(0);
				List<UserAlias> userAliasRecipientListDb = userAliasService.getListRecipients(userAliasOwner);
				for (UserAlias userAlias : userAliasRecipientListDb) {
					if (sysUserService.isUserHasPermission(userAlias.getSysUserBySysUser().getId(), "011"))
						userAliasRecipientList.add(userAlias);

				}
			}
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}

		step = "01";
		disableSticker=false;
	}

	@PostConstruct
	public void init() {
		try {
			equipmentCatStr = UtilityHelper.getRequestParameter("eqct");
			taskIdStr = UtilityHelper.getRequestParameter("t");
			permission = UtilityHelper.getRequestParameter("p");
			persistentMode = UtilityHelper.getRequestParameter("m");
		    String mode = UtilityHelper.getRequestParameter("mode");
		    if ("view".equals(mode)) {
		        viewOnly = true; // يجعل كل الحقول للعرض فقط
		    }
			if (persistentMode != null)
				persistentMode = UtilityHelper.decipher(persistentMode);
			if (permission != null) {
				permission = UtilityHelper.decipher(permission);
				if (permission.equalsIgnoreCase("readOnly"))
					disabled = true;
				if (permission.equalsIgnoreCase("editable"))
					disabled = false;
			}
			if (taskIdStr != null) {
				taskId = Integer.valueOf(UtilityHelper.decipher(taskIdStr));
				task = taskService.findById(taskId);
			}

			if (equipmentCatStr != null) {
				equipmentCatCode = UtilityHelper.decipher(equipmentCatStr);
				equipmentCategory = equipmentCategoryService.findByUniqueField("code", equipmentCatCode);

				if (!disabled && persistentMode.equals("insert")) {
					if (equipmentCategory != null) {
						formTemplate = formTemplateService.getBy(equipmentCategory.getCode());
						if (formTemplate != null) {
							formRows = formRowService.getBy(formTemplate.getId());
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
					}
					equipmentInspectionForm = new EquipmentInspectionForm();

					Integer maxReportNo = equipmentInspectionFormService
							.getMaxReportNoCodeByEquipmentCat(equipmentCatCode);
					if (maxReportNo != null) {
						equipmentInspectionForm.setReportNo("A0247" + String.format("%0" + 5 + "d", maxReportNo + 1));
					}

					Integer maxTimeSheetNo = equipmentInspectionFormService
							.getMaxTimeSheetNoCodeByEquipmentCat(equipmentCatCode);
					if (maxTimeSheetNo != null) {
						equipmentInspectionForm
								.setTimeSheetNo("TS" + String.format("%0" + 5 + "d", maxTimeSheetNo + 1));
					}

					Integer maxJobNo = equipmentInspectionFormService.getMaxJobNoCodeByEquipmentCat(equipmentCatCode);
					if (maxJobNo != null) {
						equipmentInspectionForm.setJobNo("JO" + String.format("%0" + 5 + "d", maxJobNo + 1));
					}

					Integer maxStickerNo = equipmentInspectionFormService
							.getMaxStickerNoCodeByEquipmentCat(equipmentCatCode);
					if (maxStickerNo != null) {
						equipmentInspectionForm.setStickerNo("SK" + String.format("%0" + 5 + "d", maxStickerNo + 1));
					}

					equipmentInspectionForm.setDateOfThoroughExamination(Calendar.getInstance().getTime());
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.MONTH, 6);
					Date updatedDate = cal.getTime();
					equipmentInspectionForm.setNextExaminationDate(updatedDate);
					if (task != null)
						equipmentInspectionForm.setCompany(task.getCompany());

				} else {
					if (taskIdStr != null) {
						equipmentInspectionForm = equipmentInspectionFormService.getBy(taskId);
						if (equipmentInspectionForm != null) {
							this.inspectionFormWorkflow = inspectionFormWorkflowService
									.getCurrentInspectionFormWorkFlow(equipmentInspectionForm.getId());
							this.inspectionFormWorkflowStep = inspectionFormWorkflowStepService
									.getLastStep(equipmentInspectionForm.getId());
							if (this.inspectionFormWorkflowStep != null)
								stepComment = this.inspectionFormWorkflowStep.getSysUserComment();
							step = this.inspectionFormWorkflow.getWorkflowDefinition().getStep().getCode();
							if(step.equals("01"))
								disableSticker=true;
						}
						List<EquipmentInspectionFormItem> equipmentInspectionFormItems = new ArrayList<EquipmentInspectionFormItem>(
								equipmentInspectionForm.getEquipmentInspectionFormItems());
						formTemplate = formTemplateService.getBy(equipmentCategory.getCode());
						if (formTemplate != null) {
							formRows = formRowService.getBy(formTemplate.getId());
							for (FormRow formRow : formRows) {
								formColumnsPerRow = formColumnService.getBy(formRow.getId());
								for (FormColumn formColumn : formColumnsPerRow) {
									columnContentsPerColumn = columnContentService.getBy(formColumn.getId());
									for (ColumnContent columnContentObj : columnContentsPerColumn) {
										for (EquipmentInspectionFormItem equipmentInspectionFormItem : equipmentInspectionFormItems) {
											if (equipmentInspectionFormItem.getAliasName()
													.equalsIgnoreCase(columnContentObj.getAliasName())) {
												columnContentObj
														.setContentValue(equipmentInspectionFormItem.getItemValue());
												break;
											}
										}
									}
									columnContents.addAll(columnContentsPerColumn);
								}
								formColumns.addAll(formColumnsPerRow);
							}
							PrimeFaces.current().ajax().update("form:panelGridDaynamicContent");
						}
					}
				}
			}

			examinationTypes = examinationTypeService.findAll();
			equipmentTypes = equipmentTypeService.findAll();
			companies = companyService.findAll();
			Map<String, Object> criteria = new HashMap<String, Object>();
			criteria.put("sysUserByForUser.id", loginBean.getUser().getId());
			criteria.put("isUsed",false);
			stickers = stickerService.findByCriteria(criteria);


		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void defineEquipmentCategoryByCode(Integer taskId, String code) {
		try {

			taskIdStr = UtilityHelper.cipher(taskId.toString());
			equipmentCatStr = UtilityHelper.cipher(code);

			equipmentInspectionForm = equipmentInspectionFormService.getBy(taskId);
			InspectionFormWorkflow inspectionFormWorkflow = null;
			if (equipmentInspectionForm != null) {
				if (equipmentInspectionForm.getInspectionFormWorkflows() != null) {
					inspectionFormWorkflow = (InspectionFormWorkflow) equipmentInspectionForm
							.getInspectionFormWorkflows().iterator().next();
				}
			}
			if (loginBean.hasSysPermission("011") && loginBean.hasSysPermission("010")) {
				if (equipmentInspectionForm == null) {
					permission = UtilityHelper.cipher("editable");
					persistentMode = UtilityHelper.cipher("insert");
				} else {
					if (inspectionFormWorkflow != null) {
						if (inspectionFormWorkflow.getWorkflowDefinition().getStep().getCode().equals("02")) {
							permission = UtilityHelper.cipher("readOnly");
							persistentMode = UtilityHelper.cipher("changeStep");
						}
						if (inspectionFormWorkflow.getWorkflowDefinition().getStep().getCode().equals("01")) {
							permission = UtilityHelper.cipher("editable");
							persistentMode = UtilityHelper.cipher("update");
						}
					}
				}

			} else if (loginBean.hasSysPermission("011")) {
				permission = UtilityHelper.cipher("readOnly");
				persistentMode = UtilityHelper.cipher("changeStep");
			} else if (loginBean.hasSysPermission("010")) {
				permission = UtilityHelper.cipher("editable");
				if (equipmentInspectionForm == null) {
					persistentMode = UtilityHelper.cipher("insert");
				} else {
					if (inspectionFormWorkflow != null) {
						if (inspectionFormWorkflow.getWorkflowDefinition().getStep().getCode().equals("01"))
							persistentMode = UtilityHelper.cipher("update");
					}
				}
			}

		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<ChecklistDetailDataSource> getBy(String dataSourceCode) {
		return checklistDetailDataSourceService.getByDataSource(dataSourceCode);
	}

	public boolean doValidate() {
		try {
			if (comment == null || comment.isEmpty()) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("youShouldEnterComment"));
				return false;
			}
			
			if(equipmentInspectionForm.getSticker()==null) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("youShouldChooseSticker"));
				return false;
			}
			// Add validation for selectedUserAliasRecipient if needed (e.g., must be
			// selected for certain steps)
			if (UtilityHelper.decipher(persistentMode).equals("insert")) {
				if (selectedUserAliasRecipient == null || selectedUserAliasRecipient.getSysUserBySysUser() == null) {
					UtilityHelper.addErrorMessage("Please select a reviewer.");
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

    public String doSave() {
        if (!doValidate())
            return "";
        try {
            WorkflowDefinition workflowDefinitionInit = workflowDefinitionService.getInitStep((short) 1);
            WorkflowDefinition workflowDefinitionFinal = workflowDefinitionService.getFinalStep((short) 1);
            Set<InspectionFormWorkflow> inspectionFormWorkflows = new HashSet<InspectionFormWorkflow>();
            Set<InspectionFormWorkflowStep> inspectionFormWorkflowSteps = new HashSet<InspectionFormWorkflowStep>();

            Short maxStepSeq = null;
            // protect against null sticker (keep original behavior)
            if (equipmentInspectionForm != null && equipmentInspectionForm.getSticker() != null) {
                equipmentInspectionForm.setStickerNo(equipmentInspectionForm.getSticker().getStickerNo());
            }

            // ----------------- CHANGE STEP (Reviewer approves) - MODIFIED -----------------
            if (UtilityHelper.decipher(persistentMode).equals("changeStep")) {

                // ensure sticker no kept (defensive)
                if (equipmentInspectionForm != null && equipmentInspectionForm.getSticker() != null) {
                    equipmentInspectionForm.setStickerNo(equipmentInspectionForm.getSticker().getStickerNo());
                }

                // 1) Sync dynamic columnContents -> EquipmentInspectionFormItem (update existing or create new)
                // ensure collection initialized
                if (equipmentInspectionForm.getEquipmentInspectionFormItems() == null) {
                    equipmentInspectionForm.setEquipmentInspectionFormItems(new HashSet<EquipmentInspectionFormItem>());
                }

                // build a map of existing items by alias for quick lookup
                Map<String, EquipmentInspectionFormItem> existingItemsByAlias = new HashMap<>();
                for (Object existingObj : equipmentInspectionForm.getEquipmentInspectionFormItems()) {
                    if (existingObj instanceof EquipmentInspectionFormItem) {
                        EquipmentInspectionFormItem existingItem = (EquipmentInspectionFormItem) existingObj;
                        if (existingItem.getAliasName() != null) {
                            existingItemsByAlias.put(existingItem.getAliasName().toLowerCase(), existingItem);
                        }
                    }
                }

                // iterate columnContents and update/create items
                for (ColumnContent cc : columnContents) {
                    String alias = cc.getAliasName();
                    if (alias == null) continue;
                    String key = alias.toLowerCase();
                    Object value = cc.getContentValue();

                    EquipmentInspectionFormItem item = existingItemsByAlias.get(key);
                    if (item == null) {
                        item = new EquipmentInspectionFormItem();
                        item.setEquipmentInspectionForm(equipmentInspectionForm);
                        item.setAliasName(cc.getAliasName());
                        item.setGeneralEquipmentItem(cc.getGeneralEquipmentItem());
                        // add newly created item to the form's collection
                        equipmentInspectionForm.getEquipmentInspectionFormItems().add(item);
                        existingItemsByAlias.put(key, item);
                    }

                    // convert incoming value to String because setItemValue expects String
                    String valueAsString = null;
                    if (value != null) {
                        if (value instanceof Date) {
                            // use existing helper to format date (date-only)
                            valueAsString = formatDate((Date) value);
                        } else {
                            valueAsString = String.valueOf(value);
                        }
                    }
                    item.setItemValue(valueAsString);
                }

                // 2) keep other important fields up-to-date (name/address, reviewer)
                equipmentInspectionForm.setSysUserByReviewedBy(loginBean.getUser());
                equipmentInspectionForm.setNameAndAddressOfEmployer(
                        equipmentInspectionForm.getCompany() != null
                                ? equipmentInspectionForm.getCompany().getName()
                                : equipmentInspectionForm.getNameAndAddressOfEmployer());

                // 3) prepare workflow and step as before
                maxStepSeq = inspectionFormWorkflowStepService.getLastStepSeq(equipmentInspectionForm.getId());
                InspectionFormWorkflow inspectionFormWorkflow = inspectionFormWorkflowService
                        .getCurrentInspectionFormWorkFlow(equipmentInspectionForm.getId());

                inspectionFormWorkflow.setEquipmentInspectionForm(equipmentInspectionForm);
                inspectionFormWorkflow.setWorkflowDefinition(workflowDefinitionFinal);
                inspectionFormWorkflow.setTask(task);

                InspectionFormWorkflowStep inspectionFormWorkflowStep = new InspectionFormWorkflowStep();
                inspectionFormWorkflowStep.setEquipmentInspectionForm(equipmentInspectionForm);
                inspectionFormWorkflowStep.setInspectionFormDocument("document-final".getBytes());
                inspectionFormWorkflowStep.setProcessDate(Calendar.getInstance().getTime());
                inspectionFormWorkflowStep.setSysUser(loginBean.getUser());
                inspectionFormWorkflowStep.setSysUserComment(comment);
                inspectionFormWorkflowStep.setStepSeq((short) (maxStepSeq + 1));
                inspectionFormWorkflowStep.setWorkflowDefinition(workflowDefinitionFinal);

                // 4) finally persist (this should save the updated items too)
                equipmentInspectionFormService.saveToStep(equipmentInspectionForm, inspectionFormWorkflow,
                        inspectionFormWorkflowStep);

                // 5) set local state and print if you want same behavior
                step = "03";
                doPrint();
                UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
                return "";
            }
            // ----------------- END changeStep -----------------

            // original behavior for building items in other branches (insert/update)
            for (ColumnContent columnContent : columnContents) {
                EquipmentInspectionFormItem equipmentInspectionFormItem = new EquipmentInspectionFormItem();
                equipmentInspectionFormItem.setEquipmentInspectionForm(equipmentInspectionForm);
                equipmentInspectionFormItem.setGeneralEquipmentItem(columnContent.getGeneralEquipmentItem());
                equipmentInspectionFormItem.setAliasName(columnContent.getAliasName());

                // convert content value to String (defensive, since setItemValue expects String)
                Object ccVal = columnContent.getContentValue();
                String ccValStr = null;
                if (ccVal != null) {
                    if (ccVal instanceof Date) {
                        ccValStr = formatDate((Date) ccVal);
                    } else {
                        ccValStr = String.valueOf(ccVal);
                    }
                }
                equipmentInspectionFormItem.setItemValue(ccValStr);

                equipmentInspectionForm.getEquipmentInspectionFormItems().add(equipmentInspectionFormItem);
            }

            equipmentInspectionForm.setEquipmentCategory(equipmentCategory);
            equipmentInspectionForm.setEquipmentType(equipmentType);
            equipmentInspectionForm.setExaminationType(examinationType);

            if (UtilityHelper.decipher(persistentMode).equals("update")) {
                maxStepSeq = inspectionFormWorkflowStepService.getLastStepSeq(equipmentInspectionForm.getId());

                equipmentInspectionForm.setSysUserByReviewedBy(loginBean.getUser());
                equipmentInspectionForm.setNameAndAddressOfEmployer(equipmentInspectionForm.getCompany().getName());

                ((InspectionFormWorkflow) equipmentInspectionForm.getInspectionFormWorkflows().iterator().next())
                        .setWorkflowDefinition(workflowDefinitionInit.getWorkflowDefinitionByNext());
                ((InspectionFormWorkflow) equipmentInspectionForm.getInspectionFormWorkflows().iterator().next())
                        .setReviewedBy(selectedUserAliasRecipient.getSysUserBySysUser());

                InspectionFormWorkflowStep inspectionFormWorkflowStepTwo = new InspectionFormWorkflowStep();
                inspectionFormWorkflowStepTwo.setEquipmentInspectionForm(equipmentInspectionForm);
                inspectionFormWorkflowStepTwo
                        .setWorkflowDefinition(workflowDefinitionInit.getWorkflowDefinitionByNext());
                inspectionFormWorkflowStepTwo.setInspectionFormDocument("doc".getBytes());
                inspectionFormWorkflowStepTwo.setProcessDate(Calendar.getInstance().getTime());
                inspectionFormWorkflowStepTwo.setSysUser(loginBean.getUser());
                inspectionFormWorkflowStepTwo.setSysUserComment(comment);
                inspectionFormWorkflowStepTwo.setStepSeq((short) (maxStepSeq + 1));

                equipmentInspectionForm.getInspectionFormWorkflowSteps().add(inspectionFormWorkflowStepTwo);

                equipmentInspectionFormService.merge(equipmentInspectionForm);
                UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

                return "pretty:inspection/my-tasks";

            } else if (UtilityHelper.decipher(persistentMode).equals("insert")) {
                maxStepSeq = inspectionFormWorkflowStepService.getLastStepSeq(null);
                equipmentInspectionForm.setSysUserByInspectionBy(loginBean.getUser());
                equipmentInspectionForm.setNameAndAddressOfEmployer(equipmentInspectionForm.getCompany().getName());

                InspectionFormWorkflow inspectionFormWorkflow = new InspectionFormWorkflow();
                inspectionFormWorkflow.setEquipmentInspectionForm(equipmentInspectionForm);
                inspectionFormWorkflow.setWorkflowDefinition(workflowDefinitionInit.getWorkflowDefinitionByNext());
                inspectionFormWorkflow.setTask(task);
                inspectionFormWorkflow.setReviewedBy(selectedUserAliasRecipient.getSysUserBySysUser());
                inspectionFormWorkflow.setInspectionFormDocument("document-init".getBytes());
                inspectionFormWorkflows.add(inspectionFormWorkflow);
                equipmentInspectionForm.setInspectionFormWorkflows(inspectionFormWorkflows);

                InspectionFormWorkflowStep inspectionFormWorkflowStepOne = new InspectionFormWorkflowStep();
                inspectionFormWorkflowStepOne.setEquipmentInspectionForm(equipmentInspectionForm);
                inspectionFormWorkflowStepOne.setWorkflowDefinition(workflowDefinitionInit);
                inspectionFormWorkflowStepOne.setInspectionFormDocument("doc".getBytes());
                inspectionFormWorkflowStepOne.setProcessDate(Calendar.getInstance().getTime());
                inspectionFormWorkflowStepOne.setSysUser(loginBean.getUser());
                inspectionFormWorkflowStepOne.setSysUserComment(comment);
                inspectionFormWorkflowStepOne.setStepSeq((short) (maxStepSeq + 1));
                inspectionFormWorkflowSteps.add(inspectionFormWorkflowStepOne);

                InspectionFormWorkflowStep inspectionFormWorkflowStepTwo = new InspectionFormWorkflowStep();
                inspectionFormWorkflowStepTwo.setEquipmentInspectionForm(equipmentInspectionForm);
                inspectionFormWorkflowStepTwo
                        .setWorkflowDefinition(workflowDefinitionInit.getWorkflowDefinitionByNext());
                inspectionFormWorkflowStepTwo.setInspectionFormDocument("doc".getBytes());
                inspectionFormWorkflowStepTwo.setProcessDate(Calendar.getInstance().getTime());
                inspectionFormWorkflowStepTwo.setSysUser(loginBean.getUser());
                inspectionFormWorkflowStepTwo.setSysUserComment(comment);
                inspectionFormWorkflowStepTwo.setStepSeq((short) (inspectionFormWorkflowStepOne.getStepSeq() + 1));

                inspectionFormWorkflowSteps.add(inspectionFormWorkflowStepTwo);
                equipmentInspectionForm.setInspectionFormWorkflowSteps(inspectionFormWorkflowSteps);

                equipmentInspectionFormService.saveOrUpdate(equipmentInspectionForm);
                Sticker sticker = stickerService.findByUniqueField("stickerNo", equipmentInspectionForm.getStickerNo());
                if (sticker != null) {
                    sticker.setIsUsed(true);
                    sticker.setIsPrinted(true);
                    sticker.setSysUserByCreatedBy(loginBean.getUser());
                    sticker.setSysUserByPrintedBy(loginBean.getUser());
                    stickerService.update(sticker);
                }
                UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
                return "pretty:inspection/my-tasks";
            }
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
            return "";
        }
        return "";
    }


	public String returnToInspector() {
		if (!doValidate())
			return "";
		try {
			WorkflowDefinition workflowDefinitionInit = workflowDefinitionService.getInitStep((short) 1);
			Short maxStepSeq = null;
			equipmentInspectionForm.setStickerNo(equipmentInspectionForm.getSticker().getStickerNo());
			if (disabled) {
				maxStepSeq = inspectionFormWorkflowStepService.getLastStepSeq(equipmentInspectionForm.getId());
				InspectionFormWorkflow inspectionFormWorkflow = inspectionFormWorkflowService
						.getCurrentInspectionFormWorkFlow(equipmentInspectionForm.getId());

				equipmentInspectionForm.setSysUserByReviewedBy(loginBean.getUser());
				equipmentInspectionForm.setNameAndAddressOfEmployer(equipmentInspectionForm.getCompany().getName());
				inspectionFormWorkflow.setEquipmentInspectionForm(equipmentInspectionForm);
				inspectionFormWorkflow.setWorkflowDefinition(workflowDefinitionInit);
				inspectionFormWorkflow.setTask(task);
				inspectionFormWorkflow.setReviewedBy(null);

				InspectionFormWorkflowStep inspectionFormWorkflowStep = new InspectionFormWorkflowStep();
				inspectionFormWorkflowStep.setEquipmentInspectionForm(equipmentInspectionForm);
				inspectionFormWorkflowStep.setInspectionFormDocument("document-final".getBytes());
				inspectionFormWorkflowStep.setProcessDate(Calendar.getInstance().getTime());
				inspectionFormWorkflowStep.setSysUser(loginBean.getUser());
				inspectionFormWorkflowStep.setSysUserComment(comment);
				inspectionFormWorkflowStep.setStepSeq((short) (maxStepSeq + 1));
				inspectionFormWorkflowStep.setWorkflowDefinition(workflowDefinitionInit);

				equipmentInspectionFormService.saveToStep(equipmentInspectionForm, inspectionFormWorkflow,
						inspectionFormWorkflowStep);
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				return "pretty:inspection/my-tasks";
			}
		} catch (Exception e) {
			e.printStackTrace();
			UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
			return "";
		}
		return "";
	}
    // helper: convert any object to safe string
    private String safeString(Object o) {
        return (o == null) ? "" : String.valueOf(o);
    }

    // helper: format Date to date-only string (no time)
    private String formatDate(Date d) {
        if (d == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM.yyyy");
        return sdf.format(d).toUpperCase();
    }



    public void doPrint() {
        try {
            if (formTemplate == null || formTemplate.getPrintedDoc() == null) {
                UtilityHelper.addErrorMessage("Template not available for printing.");
                return;
            }

            // Create document from template binary (same approach as original)
            Document document = new Document(ByteSource.wrap(formTemplate.getPrintedDoc().getData()).openStream());

            // Create and populate data map — format dates as strings (no time)
            Map<String, Object> data = new HashMap<>();
            if (equipmentInspectionForm != null) {
                data.put("ReportNo", equipmentInspectionForm.getReportNo() != null ? equipmentInspectionForm.getReportNo() : "");
                data.put("TsNo", equipmentInspectionForm.getTimeSheetNo() != null ? equipmentInspectionForm.getTimeSheetNo() : "");
                data.put("JobNo", equipmentInspectionForm.getJobNo() != null ? equipmentInspectionForm.getJobNo() : "");
                data.put("StNo", equipmentInspectionForm.getStickerNo() != null ? equipmentInspectionForm.getStickerNo() : "");
                data.put("Dte", formatDate(equipmentInspectionForm.getDateOfThoroughExamination()));
                data.put("Ned", formatDate(equipmentInspectionForm.getNextExaminationDate()));
                data.put("Ped", formatDate(equipmentInspectionForm.getPreviousExaminationDate()));
                data.put("Company", equipmentInspectionForm.getNameAndAddressOfEmployer() != null ? equipmentInspectionForm.getNameAndAddressOfEmployer() : "");
                data.put("Address", equipmentInspectionForm.getCompany() != null ? equipmentInspectionForm.getCompany().getAddress() : "");
                data.put("ExType", equipmentInspectionForm.getExaminationType() != null ? equipmentInspectionForm.getExaminationType().getEnglishName() : "");
                data.put("insBy", equipmentInspectionForm.getSysUserByInspectionBy() != null ? equipmentInspectionForm.getSysUserByInspectionBy().getEnDisplayName() : "");
                data.put("reviewedBy", loginBean.getUser() != null ? loginBean.getUser().getEnDisplayName() : "");
            }

            // Generate QR code data (use same parameters as الأصلي)
            String qrCodeData = UtilityHelper.getBaseURL() + "api/equipment-cert/" +
                    (equipmentInspectionForm != null && equipmentInspectionForm.getSticker() != null ? equipmentInspectionForm.getSticker().getSerialNo() : "") +
                    "&" +
                    (equipmentInspectionForm != null && equipmentInspectionForm.getSticker() != null ? equipmentInspectionForm.getSticker().getStickerNo() : "");
            byte[] qrCodeBytes = QRCodeGenerator.generateQrCodeImage(qrCodeData, 5, 1); // كما في الأصل

            // Try to insert QR via bookmark if present, otherwise put bytes in data map
            Bookmark qrCodeBookmark = document.getRange().getBookmarks().get("QRCodeImage");
            if (qrCodeBookmark != null && qrCodeBytes != null) {
                Shape qrCodeShape = new Shape(document, ShapeType.IMAGE);
                qrCodeShape.getImageData().setImageBytes(qrCodeBytes);
                qrCodeShape.setWidth(75);
                qrCodeShape.setHeight(75);
                // keep alignment/positioning similar to original snippet
                qrCodeShape.setRelativeHorizontalPosition(RelativeHorizontalPosition.MARGIN);
                qrCodeShape.setHorizontalAlignment(HorizontalAlignment.CENTER);
                qrCodeShape.setRelativeVerticalPosition(RelativeVerticalPosition.PARAGRAPH);
                qrCodeShape.setWrapType(WrapType.NONE);
                qrCodeBookmark.getBookmarkStart().getParentNode().appendChild(qrCodeShape);
            } else {
                if (qrCodeBytes != null) data.put("QRCodeImage", qrCodeBytes);
            }

            // Issued date (today) formatted (uppercase)
            data.put("issureDate", formatDate(Calendar.getInstance().getTime()));

            // Put form items into data map — if item value is Date, format it to date-only string
            List<EquipmentInspectionFormItem> equipmentInspectionFormItems = new ArrayList<EquipmentInspectionFormItem>();
            if (equipmentInspectionForm != null && equipmentInspectionForm.getEquipmentInspectionFormItems() != null) {
                equipmentInspectionFormItems.addAll(equipmentInspectionForm.getEquipmentInspectionFormItems());
                if (equipmentInspectionFormItems != null && !equipmentInspectionFormItems.isEmpty()) {
                    for (EquipmentInspectionFormItem item : equipmentInspectionFormItems) {
                        Object val = item.getItemValue();
                        if (val instanceof Date) {
                            data.put(item.getAliasName(), formatDate((Date) val));
                        } else {
                            data.put(item.getAliasName(), val != null ? val.toString() : "");
                        }
                    }
                }
            }

            // Use ReportingEngine to fill the template with data
            ReportingEngine engine = new ReportingEngine();
            engine.buildReport(document, data, "data");

            // Replace text runs with "True"/"False" to checkboxes (preserve original behavior)
            NodeCollection runs = document.getChildNodes(NodeType.RUN, true);
            for (Run run : (Iterable<Run>) runs) {
                String text = run.getText();
                if (text != null) text = text.trim();
                if (text != null && (text.equalsIgnoreCase("True") || text.equalsIgnoreCase("False"))) {
                    boolean isChecked = text.equalsIgnoreCase("True");
                    DocumentBuilder builder = new DocumentBuilder(document);
                    builder.moveTo(run);
                    StructuredDocumentTag checkbox = new StructuredDocumentTag(document, SdtType.CHECKBOX, MarkupLevel.INLINE);
                    checkbox.setChecked(isChecked);
                    builder.insertNode(checkbox);
                    run.remove();
                }
            }

            // Insert signatures (if bookmarks exist)
            byte[] signatureInspecByPlaceholderByte = equipmentInspectionForm != null && equipmentInspectionForm.getSysUserByInspectionBy() != null
                    ? equipmentInspectionForm.getSysUserByInspectionBy().getSignturePhoto() : null;

            byte[] signtureReviewedByPlaceholderByte = equipmentInspectionForm != null && equipmentInspectionForm.getSysUserByReviewedBy() != null
                    ? equipmentInspectionForm.getSysUserByReviewedBy().getSignturePhoto() : null;
            Bookmark bookmarkInspectImg = document.getRange().getBookmarks().get("inspectedImg");
            if (bookmarkInspectImg != null && signatureInspecByPlaceholderByte != null) {
                Shape shape = new Shape(document, ShapeType.IMAGE);
                shape.getImageData().setImageBytes(signatureInspecByPlaceholderByte);
                shape.setWidth(160);
                shape.setHeight(40);
                bookmarkInspectImg.getBookmarkStart().getParentNode().appendChild(shape);
            }
            Bookmark bookmarkInspectByImg = document.getRange().getBookmarks().get("inspectedByImg");
            if (bookmarkInspectByImg != null && signatureInspecByPlaceholderByte != null) {
                Shape shape = new Shape(document, ShapeType.IMAGE);
                shape.getImageData().setImageBytes(signatureInspecByPlaceholderByte);
                shape.setWidth(160);
                shape.setHeight(40);
                bookmarkInspectByImg.getBookmarkStart().getParentNode().appendChild(shape);
            }

            Bookmark bookmarkReviewedByImg = document.getRange().getBookmarks().get("reviewedByImg");
            if (bookmarkReviewedByImg != null && signtureReviewedByPlaceholderByte != null) {
                Shape shape = new Shape(document, ShapeType.IMAGE);
                shape.getImageData().setImageBytes(signtureReviewedByPlaceholderByte);
                shape.setWidth(160);
                shape.setHeight(40);
                bookmarkReviewedByImg.getBookmarkStart().getParentNode().appendChild(shape);
            }

            // Save the filled document to PDF bytes
            ByteArrayOutputStream pdfDocOutputStream = new ByteArrayOutputStream();
            document.save(pdfDocOutputStream, SaveFormat.PDF);

            // Persist or update EquipmentInspectionCertificate (same logic as الأصلي)
            EquipmentInspectionCertificate equipmentInspectionCertificate = equipmentInspectionCertificateService
                    .getBy(equipmentInspectionForm.getId());
            if (equipmentInspectionCertificate == null) {
                equipmentInspectionCertificate = new EquipmentInspectionCertificate();
                equipmentInspectionCertificate.setCertificateDocument(pdfDocOutputStream.toByteArray());
                equipmentInspectionCertificate.setAllowReprintCert(false);
                equipmentInspectionCertificate.setCompany(equipmentInspectionForm.getCompany());
                equipmentInspectionCertificate.setCreatedDate(Calendar.getInstance().getTime());
                equipmentInspectionCertificate.setEquipmentInspectionForm(equipmentInspectionForm);
                equipmentInspectionCertificate.setIsPrinted(true);
                equipmentInspectionCertificate.setIssueDate(Calendar.getInstance().getTime());
                equipmentInspectionCertificate.setSysUserByAllowReprintBy(null);
                equipmentInspectionCertificate.setSysUserByCreatedBy(loginBean.getUser());
                equipmentInspectionCertificate.setSysUserByReprintBy(null);
                equipmentInspectionCertificate = equipmentInspectionCertificateService.save(equipmentInspectionCertificate);
            } else {
                equipmentInspectionCertificate.setCertificateDocument(pdfDocOutputStream.toByteArray());
                equipmentInspectionCertificate.setAllowReprintCert(false);
                equipmentInspectionCertificate.setCompany(equipmentInspectionForm.getCompany());
                equipmentInspectionCertificate.setCreatedDate(Calendar.getInstance().getTime());
                equipmentInspectionCertificate.setEquipmentInspectionForm(equipmentInspectionForm);
                equipmentInspectionCertificate.setIsPrinted(true);
                equipmentInspectionCertificate.setIssueDate(Calendar.getInstance().getTime());
                equipmentInspectionCertificate.setSysUserByAllowReprintBy(null);
                equipmentInspectionCertificate.setSysUserByCreatedBy(loginBean.getUser());
                equipmentInspectionCertificate.setSysUserByReprintBy(null);
                equipmentInspectionCertificateService.update(equipmentInspectionCertificate);
            }

            UtilityHelper.putSessionAttr("equipCertId", equipmentInspectionCertificate.getId());
            PrimeFaces.current().ajax().update("form_viewer:manage-viewer-content");
            PrimeFaces.current().executeScript("PF('viewerWidgetVar').show();");

        } catch (Exception e) {
            e.printStackTrace();
            // keep same error messaging as original (or localization message if you prefer)
            try {
                UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
            } catch (Exception ex) {
                UtilityHelper.addErrorMessage("Error during printing: " + ex.getMessage());
            }
        }
    }


    private byte[] generateEquipmentPdfBytes(EquipmentInspectionForm eForm, FormTemplate tpl) throws Exception {
        if (eForm == null) throw new IllegalArgumentException("EquipmentInspectionForm is null");
        if (tpl == null || tpl.getPrintedDoc() == null) throw new IllegalArgumentException("Form template missing");

        Document document = new Document(ByteSource.wrap(tpl.getPrintedDoc().getData()).openStream());

        Map<String, Object> data = new HashMap<>();
        data.put("ReportNo", eForm.getReportNo() != null ? eForm.getReportNo() : "");
        data.put("TsNo", eForm.getTimeSheetNo() != null ? eForm.getTimeSheetNo() : "");
        data.put("JobNo", eForm.getJobNo() != null ? eForm.getJobNo() : "");
        data.put("StNo", eForm.getStickerNo() != null ? eForm.getStickerNo() : "");
        data.put("Dte", formatDate(eForm.getDateOfThoroughExamination()));
        data.put("Ned", formatDate(eForm.getNextExaminationDate()));
        data.put("Ped", formatDate(eForm.getPreviousExaminationDate()));
        data.put("company", eForm.getNameAndAddressOfEmployer() != null ? eForm.getNameAndAddressOfEmployer() : "");
        data.put("Address", eForm.getCompany() != null ? eForm.getCompany().getAddress() : "");
        data.put("ExType", eForm.getExaminationType() != null ? eForm.getExaminationType().getEnglishName() : "");
        data.put("insBy", eForm.getSysUserByInspectionBy() != null ? eForm.getSysUserByInspectionBy().getEnDisplayName() : "");
        data.put("reviewedBy", loginBean != null && loginBean.getUser() != null ? loginBean.getUser().getEnDisplayName() : "");
        data.put("issureDate", formatDate(Calendar.getInstance().getTime()));

        // QR
        String qrCodeData = UtilityHelper.getBaseURL() + "api/equipment-cert/" +
                (eForm.getSticker() != null ? eForm.getSticker().getSerialNo() : "") + "&" +
                (eForm.getSticker() != null ? eForm.getSticker().getStickerNo() : "");
        byte[] qrBytes = QRCodeGenerator.generateQrCodeImage(qrCodeData, 5, 1);
        Bookmark qrB = document.getRange().getBookmarks().get("QRCodeImage");
        if (qrB != null && qrBytes != null) {
            Shape qrShape = new Shape(document, ShapeType.IMAGE);
            qrShape.getImageData().setImageBytes(qrBytes);
            qrShape.setWidth(75);
            qrShape.setHeight(75);
            qrB.getBookmarkStart().getParentNode().appendChild(qrShape);
        } else {
            if (qrBytes != null) data.put("QRCodeImage", qrBytes);
        }

        // Items: format Date values to date-only strings
        if (eForm.getEquipmentInspectionFormItems() != null) {
            for (Object obj : eForm.getEquipmentInspectionFormItems()) {
                if (obj instanceof EquipmentInspectionFormItem) {
                    EquipmentInspectionFormItem item = (EquipmentInspectionFormItem) obj;
                    Object value = item.getItemValue();
                    if (value instanceof Date) {
                        data.put(item.getAliasName(), formatDate((Date) value));
                    } else {
                        data.put(item.getAliasName(), value != null ? value.toString() : "");
                    }
                }
            }
        }

        ReportingEngine engine = new ReportingEngine();
        engine.buildReport(document, data, "data");

        // signatures
        if (eForm.getSysUserByInspectionBy() != null && eForm.getSysUserByInspectionBy().getSignturePhoto() != null) {
            Bookmark b = document.getRange().getBookmarks().get("inspectedByImg");
            if (b != null) {
                Shape shape = new Shape(document, ShapeType.IMAGE);
                shape.getImageData().setImageBytes(eForm.getSysUserByInspectionBy().getSignturePhoto());
                shape.setWidth(160); shape.setHeight(40);
                b.getBookmarkStart().getParentNode().appendChild(shape);
            }
        }
        if (eForm.getSysUserByReviewedBy() != null && eForm.getSysUserByReviewedBy().getSignturePhoto() != null) {
            Bookmark b2 = document.getRange().getBookmarks().get("reviewedByImg");
            if (b2 != null) {
                Shape shape = new Shape(document, ShapeType.IMAGE);
                shape.getImageData().setImageBytes(eForm.getSysUserByReviewedBy().getSignturePhoto());
                shape.setWidth(160); shape.setHeight(40);
                b2.getBookmarkStart().getParentNode().appendChild(shape);
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out, SaveFormat.PDF);
        return out.toByteArray();
    }


	public void downloadCert(Integer taskId) {
	    FacesContext fc = FacesContext.getCurrentInstance();
	    try {
	        EquipmentInspectionForm eForm = equipmentInspectionFormService.getBy(taskId);
	        if (eForm == null) {
	            UtilityHelper.addErrorMessage("Form not found for task: " + taskId);
	            return;
	        }

	        FormTemplate tpl = this.formTemplate;
	        if (tpl == null) {
	            if (eForm.getEquipmentCategory() != null) {
	                tpl = formTemplateService.getBy(eForm.getEquipmentCategory().getCode());
	            }
	        }
	        if (tpl == null || tpl.getPrintedDoc() == null) {
	            UtilityHelper.addErrorMessage("Template not available for this equipment category.");
	            return;
	        }

	        byte[] pdfBytes = generateEquipmentPdfBytes(eForm, tpl);
	        if (pdfBytes == null || pdfBytes.length == 0) {
	            UtilityHelper.addErrorMessage("Failed to generate PDF.");
	            return;
	        }

	        ExternalContext ec = fc.getExternalContext();
	        HttpServletResponse response = (HttpServletResponse) ec.getResponse();
	        response.reset();
	        response.setContentType("application/pdf");
	        String fname = "EquipmentCertificate_" + (eForm.getReportNo() != null ? eForm.getReportNo() : taskId) + ".pdf";
	        response.setHeader("Content-Disposition", "inline; filename=\"" + fname + "\"");
	        response.setContentLength(pdfBytes.length);

	        try (ServletOutputStream os = response.getOutputStream()) {
	            os.write(pdfBytes);
	            os.flush();
	        }
	        fc.responseComplete();
	    } catch (Exception e) {
	        e.printStackTrace();
	        UtilityHelper.addErrorMessage("Failed to generate certificate: " + e.getMessage());
	    }
	}
	// helper: convert any object to safe string





	public String returnStyle() {
		if (disabled)
			return "display:block;";
		else
			return "display:none;";
	}

	public boolean isViewOnly() {
	    return viewOnly;
	}
}

