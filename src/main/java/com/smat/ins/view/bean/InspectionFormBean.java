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
// import javax.inject.Inject;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletContext;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smat.ins.model.entity.TaskDraft;
import com.smat.ins.model.service.TaskDraftService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Named
@ViewScoped
public class InspectionFormBean implements Serializable {

    // #region "properties"

    /**
     *
     */
    private static final long serialVersionUID = -3900928087796493653L;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String equipmentCatStr;
    private String equipmentCatCode;
    private TaskDraftService taskDraftService; // inject via BeanUtility or setter


    private String taskIdStr;
    private Integer taskId;

    private String permission;

    private boolean disabled;

    private String persistentMode;

    private String step;

    private String stepComment;
    private boolean viewOnly;
    private boolean disableSticker;
    private boolean savingDraft = false;

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

    public boolean isSavingDraft() {
        return savingDraft;
    }

    public void setSavingDraft(boolean savingDraft) {
        this.savingDraft = savingDraft;
    }

    // prepareSaveDraft will be invoked by an ajax remoteCommand to set the flag before full submit
    public void prepareSaveDraft() {
        this.savingDraft = true;
    }

    /**
     * Return true when the current view is the inspector stage (i.e. not the final reviewer/print step).
     * The print button is shown when step == '03', so drafts should be available when step != '03'.
     */
    public boolean isInspectorVisible() {
        // Inspector stage is step "01". Also allow null (new/insert mode) to show draft.
        try {
            if (this.step == null) return true;
            return "01".equals(this.step);
        } catch (Exception e) {
            return false;
        }
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
            taskDraftService = (TaskDraftService) BeanUtility.getBean("taskDraftService");


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
            // Populate reviewer list: include every UserAlias that belongs to any SysUser who has permission '011'
            try {
                java.util.List<com.smat.ins.model.entity.SysUser> reviewers = sysUserService
                        .listUserHasPersmission("011");
                if (reviewers != null) {
                    for (com.smat.ins.model.entity.SysUser su : reviewers) {
                        try {
                            java.util.List<UserAlias> aliases = userAliasService.getBySysUser(su);
                            if (aliases != null && !aliases.isEmpty()) {
                                userAliasRecipientList.addAll(aliases);
                            }
                        } catch (Exception ignore) {
                            // ignore per-user alias fetch problems and continue
                        }
                    }
                }
            } catch (Exception e) {
                // fallback to previous behavior (organization-based recipients filtered by permission)
                List<UserAlias> myUserAliasList = userAliasService.getBySysUser(sysUserLogin);
                if (myUserAliasList != null && !myUserAliasList.isEmpty()) {
                    UserAlias userAliasOwner = myUserAliasList.get(0);
                    List<UserAlias> userAliasRecipientListDb = userAliasService.getListRecipients(userAliasOwner);
                    for (UserAlias userAlias : userAliasRecipientListDb) {
                        if (sysUserService.isUserHasPermission(userAlias.getSysUserBySysUser().getId(), "011"))
                            userAliasRecipientList.add(userAlias);
                    }
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

            // ensure we have an equipment category code when task provides one
            try {
                if ((equipmentCatCode == null || equipmentCatCode.trim().isEmpty()) && task != null && task.getEquipmentCategory() != null) {
                    EquipmentCategory tc = task.getEquipmentCategory();
                    if (tc != null && tc.getCode() != null) {
                        equipmentCatCode = tc.getCode();
                        equipmentCategory = equipmentCategoryService.findByUniqueField("code", equipmentCatCode);
                    }
                }
            } catch (Exception ignore) {}

            if (equipmentCatStr != null) {
                equipmentCatCode = UtilityHelper.decipher(equipmentCatStr);
                equipmentCategory = equipmentCategoryService.findByUniqueField("code", equipmentCatCode);

                if (!disabled && "insert".equals(persistentMode)) { // استخدام equals للسلامة من NPE
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

                    // If a form already exists for this task, reuse it (prevents duplicate/new=1 issues)
                    if (task != null) {
                        try {
                            EquipmentInspectionForm existing = equipmentInspectionFormService.getBy(task.getId());
                            if (existing != null) {
                                equipmentInspectionForm = existing;
                                // make sure equipmentCategory is populated for UI
                                if (equipmentInspectionForm.getEquipmentCategory() != null && equipmentInspectionForm.getEquipmentCategory().getCode() != null) {
                                    equipmentCatCode = equipmentInspectionForm.getEquipmentCategory().getCode();
                                }
                            }
                        } catch (Exception ex) {
                            // ignore and continue to create new
                            ex.printStackTrace();
                        }
                    }

                    if (equipmentInspectionForm == null) {
                        equipmentInspectionForm = new EquipmentInspectionForm();
                    }

                    // ====== NEW: try to reserve reportNo bound to task when available ======
                    Integer nextSeq = null;
                    try {
                        try {
                            com.smat.ins.model.service.SeqReservationService seqReservationService = (com.smat.ins.model.service.SeqReservationService) BeanUtility.getBean("seqReservationService");
                            if (task != null && seqReservationService != null) {
                                Integer reserved = seqReservationService.getReservedReportNoForTask(task.getId());
                                if (reserved == null) {
                                    Long reservedBy = null; try { if (loginBean != null && loginBean.getUser() != null) reservedBy = loginBean.getUser().getId(); } catch (Exception ignore) {}
                                    reserved = seqReservationService.reserveReportNoForTask(task.getId(), equipmentCatCode, reservedBy);
                                }
                                if (reserved != null) nextSeq = reserved;
                            }
                        } catch (Exception ex) {
                            // ignore reservation failures and fallback to existing behavior
                            ex.printStackTrace();
                        }

                        if (nextSeq == null) {
                            // default behavior: use existing service sequence method (transactional)
                            nextSeq = equipmentInspectionFormService.getNextReportSeqByEquipmentCat(equipmentCatCode);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if (equipmentInspectionForm.getReportNo() == null || equipmentInspectionForm.getReportNo().trim().isEmpty()) {
                        if (nextSeq != null) {
                            equipmentInspectionForm.setReportNo("A0247" + String.format("%05d", nextSeq));
                        } else {
                            Integer maxReportNo = equipmentInspectionFormService.getMaxReportNoCodeByEquipmentCat(equipmentCatCode);
                            int fallback = (maxReportNo == null) ? 1 : (maxReportNo + 1);
                            equipmentInspectionForm.setReportNo("A0247" + String.format("%05d", fallback));
                        }
                    }
                    // =================================================================

                    // Timesheet, Job, Sticker كما كانت سابقاً
                    try {
                        Integer maxTimeSheetNo = equipmentInspectionFormService
                                .getMaxTimeSheetNoCodeByEquipmentCat(equipmentCatCode);
                        if (maxTimeSheetNo != null) {
                            equipmentInspectionForm
                                    .setTimeSheetNo("TS" + String.format("%05d", maxTimeSheetNo + 1));
                        }

                        Integer maxJobNo = equipmentInspectionFormService.getMaxJobNoCodeByEquipmentCat(equipmentCatCode);
                        if (maxJobNo != null) {
                            equipmentInspectionForm.setJobNo("JO" + String.format("%05d", maxJobNo + 1));
                        }

                        Integer maxStickerNo = equipmentInspectionFormService
                                .getMaxStickerNoCodeByEquipmentCat(equipmentCatCode);
                        if (maxStickerNo != null) {
                            equipmentInspectionForm.setStickerNo("SK" + String.format("%05d", maxStickerNo + 1));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    equipmentInspectionForm.setDateOfThoroughExamination(Calendar.getInstance().getTime());
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, 6);
                    Date updatedDate = cal.getTime();
                    equipmentInspectionForm.setNextExaminationDate(updatedDate);
                    if (task != null)
                        equipmentInspectionForm.setCompany(task.getCompany());

                } else {
                    // حالة العرض أو التعديل (كما كانت سابقًا)
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
                            if (step.equals("01"))
                                disableSticker = true;
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
            // Show all available stickers (not limited to current user)
            criteria.put("isUsed", false);
            stickers = stickerService.findByCriteria(criteria);

        } catch (Exception e) {
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

    private String getServletContextPath(String relativePath) {
        return ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(relativePath);
    }

    /**
     * Handle attachments uploaded from the inspection form.
     * Files are stored under ../attachments/cabinet_for_inspection/{reportNo or id}/attachment
     */
    public void handleAttachmentUpload(org.primefaces.event.FileUploadEvent event) {
        try {
            if (event.getFile() == null || event.getFile().getContent() == null) return;
            if (event.getFile().getSize() > 30L * 1024L * 1024L) {
                UtilityHelper.addErrorMessage("File size exceeds 30MB limit");
                return;
            }

            // services
            com.smat.ins.model.service.CabinetService cabinetService = (com.smat.ins.model.service.CabinetService) BeanUtility.getBean("cabinetService");
            com.smat.ins.model.service.CabinetFolderService cabinetFolderService = (com.smat.ins.model.service.CabinetFolderService) BeanUtility.getBean("cabinetFolderService");
            com.smat.ins.model.service.ArchiveDocumentService archiveDocumentService = (com.smat.ins.model.service.ArchiveDocumentService) BeanUtility.getBean("archiveDocumentService");
            com.smat.ins.model.service.ArchiveDocumentFileService archiveDocumentFileService = (com.smat.ins.model.service.ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");
            com.smat.ins.model.service.CabinetFolderDocumentService cabinetFolderDocumentService = (com.smat.ins.model.service.CabinetFolderDocumentService) BeanUtility.getBean("cabinetFolderDocumentService");

            // find inspection cabinet
            String targetCabinetCode = "INS-DEFAULT";
            com.smat.ins.model.entity.Cabinet targetCabinet = null;
            for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
            }
            if (targetCabinet == null) {
                com.smat.ins.util.CabinetDefaultsCreator.ensureDefaultCabinets(loginBean.getUser());
                for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                    if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
                }
            }
            if (targetCabinet == null) {
                UtilityHelper.addErrorMessage("Inspection cabinet not available");
                return;
            }

            // pick drawer
            com.smat.ins.model.entity.CabinetDefinition def = null;
            if (targetCabinet.getCabinetDefinitions() != null) {
                for (Object od : targetCabinet.getCabinetDefinitions()) {
                    com.smat.ins.model.entity.CabinetDefinition cd = (com.smat.ins.model.entity.CabinetDefinition) od;
                    if ("01".equals(cd.getCode())) { def = cd; break; }
                }
            }
            if (def == null && targetCabinet.getCabinetDefinitions() != null && !targetCabinet.getCabinetDefinitions().isEmpty())
                def = (com.smat.ins.model.entity.CabinetDefinition) targetCabinet.getCabinetDefinitions().iterator().next();
            if (def == null) { UtilityHelper.addErrorMessage("No cabinet definition found"); return; }

            // folder per task/report - reuse folder named by reportNo if exists
            String folderName = null;
            if (equipmentInspectionForm != null && equipmentInspectionForm.getReportNo() != null && !equipmentInspectionForm.getReportNo().trim().isEmpty()) {
                folderName = equipmentInspectionForm.getReportNo().trim();
            } else if (equipmentInspectionForm != null && equipmentInspectionForm.getId() != null) {
                folderName = "form_" + equipmentInspectionForm.getId().toString();
            } else {
                folderName = "form_" + String.valueOf(System.currentTimeMillis());
            }

            com.smat.ins.model.entity.CabinetFolder cabinetFolder = null;
            try {
                java.util.List<com.smat.ins.model.entity.CabinetFolder> existing = cabinetFolderService.getByCabinetDefinition(def);
                if (existing != null) {
                    for (com.smat.ins.model.entity.CabinetFolder f : existing) {
                        String fn = folderName == null ? "" : folderName.trim().toLowerCase();
                        String fa = f.getArabicName() == null ? "" : f.getArabicName().trim().toLowerCase();
                        String fe = f.getEnglishName() == null ? "" : f.getEnglishName().trim().toLowerCase();
                        if (fn.equals(fa) || fn.equals(fe)) {
                            cabinetFolder = f;
                            break;
                        }
                    }
                }
            } catch (Exception ignore) {}

            if (cabinetFolder == null) {
                cabinetFolder = new com.smat.ins.model.entity.CabinetFolder();
                cabinetFolder.setCabinetDefinition(def);
                cabinetFolder.setSysUser(loginBean.getUser());
                cabinetFolder.setArabicName(folderName);
                cabinetFolder.setEnglishName(folderName);
                int nextCode = 1;
                try { java.util.List<com.smat.ins.model.entity.CabinetFolder> existing2 = cabinetFolderService.getByCabinetDefinition(def); nextCode = existing2 != null ? existing2.size() + 1 : 1; } catch (Exception ignore) {}
                cabinetFolder.setCode(String.format("%03d", nextCode));
                cabinetFolder.setCreatedDate(new java.util.Date());
                cabinetFolderService.saveOrUpdate(cabinetFolder);
            }

            // create disk path
            String mainLocation = com.smat.ins.util.CabinetDefaultsCreator.selectMainLocation(targetCabinet.getCabinetLocation());
            java.nio.file.Path folderPath = Paths.get(mainLocation, targetCabinet.getCode(), def.getCode(), cabinetFolder.getCode());
            Files.createDirectories(folderPath);

            // store file
            String original = event.getFile().getFileName();
            String safe = original.replaceAll("[^a-zA-Z0-9._-]", "_");

            // create ArchiveDocument + file
            com.smat.ins.model.entity.ArchiveDocument archiveDocument = new com.smat.ins.model.entity.ArchiveDocument();
            // ensure archiveDocumentType is set (DB column non-null)
            try {
                com.smat.ins.model.service.ArchiveDocumentTypeService archiveDocumentTypeService = (com.smat.ins.model.service.ArchiveDocumentTypeService) BeanUtility.getBean("archiveDocumentTypeService");
                java.util.List<com.smat.ins.model.entity.ArchiveDocumentType> types = archiveDocumentTypeService.findAll();
                if (types != null && !types.isEmpty()) archiveDocument.setArchiveDocumentType(types.get(0));
            } catch (Exception ignore) {}
            archiveDocument.setArabicName(original); archiveDocument.setEnglishName(original); archiveDocument.setIsDirectory(false);
            archiveDocument.setCreatedDate(new java.util.Date()); archiveDocument.setSysUserByCreatorUser(loginBean.getUser());
            // set organization and root organization from the user who uploaded the attachment
            try {
                com.smat.ins.model.service.OrganizationService organizationService = (com.smat.ins.model.service.OrganizationService) BeanUtility.getBean("organizationService");
                com.smat.ins.model.entity.Organization userOrg = null;
                if (loginBean.getUser() != null) userOrg = loginBean.getUser().getOrganizationByOrganization();
                if (userOrg != null) {
                    archiveDocument.setOrganizationByOrganization(userOrg);
                    // find root organization by walking up parents
                    com.smat.ins.model.entity.Organization rootOrg = userOrg;
                    try {
                        com.smat.ins.model.entity.Organization parentOrg = organizationService.getParentOrganization(rootOrg);
                        while (parentOrg != null && !parentOrg.equals(rootOrg)) {
                            rootOrg = parentOrg;
                            parentOrg = organizationService.getParentOrganization(rootOrg);
                        }
                    } catch (Exception ignore) {
                        // if service fails, ignore and keep userOrg as rootOrg
                    }
                    archiveDocument.setOrganizationByRootOrganization(rootOrg);
                }
            } catch (Exception ignore) {}
            archiveDocumentService.saveOrUpdate(archiveDocument);

            com.smat.ins.model.entity.ArchiveDocumentFile docFile = new com.smat.ins.model.entity.ArchiveDocumentFile();
            docFile.setArchiveDocument(archiveDocument);
            docFile.setName(original);
            String ext = org.apache.commons.io.FilenameUtils.getExtension(original);
            docFile.setExtension(ext);
            docFile.setMimeType(event.getFile().getContentType());
            docFile.setUuid(java.util.UUID.randomUUID().toString());
            docFile.setFileSize(event.getFile().getSize());
            docFile.setCreatedDate(new java.util.Date());

            // determine next file code (use ArchiveDocumentFileService helper to get max code)
            try {
                Long maxCode = archiveDocumentFileService.getMaxArchiveDocumentFileCode(archiveDocument);
                int codeLength = 9; // default (matches ArchiveDocumentServiceImpl)
                String fileCode = String.format("%0" + codeLength + "d", (maxCode == null ? 0L : maxCode) + 1L);
                docFile.setCode(fileCode);

                String storedName = fileCode + "." + ext;
                java.nio.file.Path target = folderPath.resolve(storedName);
                Files.write(target, event.getFile().getContent(), java.nio.file.StandardOpenOption.CREATE_NEW);

                String logical = targetCabinet.getCode() + "/" + def.getCode() + "/" + cabinetFolder.getCode() + "/" + storedName;
                docFile.setLogicalPath(logical);
                docFile.setServerPath(target.toString());

            } catch (Exception ex) {
                // fallback to original behaviour if service fails
                int seq = 1; try { seq += (int) java.nio.file.Files.list(folderPath).count(); } catch (Exception ignore) {}
                String storedName = String.format("%03d_%s", seq, safe);
                java.nio.file.Path target = folderPath.resolve(storedName);
                Files.write(target, event.getFile().getContent(), java.nio.file.StandardOpenOption.CREATE_NEW);
                String logical = targetCabinet.getCode() + "/" + def.getCode() + "/" + cabinetFolder.getCode() + "/" + storedName;
                docFile.setLogicalPath(logical); docFile.setServerPath(target.toString());
            }

            archiveDocumentFileService.saveOrUpdate(docFile);

            com.smat.ins.model.entity.CabinetFolderDocument cfd = new com.smat.ins.model.entity.CabinetFolderDocument();
            cfd.setCabinetFolder(cabinetFolder); cfd.setSysUser(loginBean.getUser()); cfd.setArchiveDocument(archiveDocument);
            cfd.setCreatedDate(new java.util.Date()); cabinetFolderDocumentService.saveOrUpdate(cfd);

            UtilityHelper.addInfoMessage("Attachment uploaded to cabinet: " + original);

        } catch (Exception e) {
            UtilityHelper.addErrorMessage("Error uploading attachment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<ChecklistDetailDataSource> getBy(String dataSourceCode) {
        return checklistDetailDataSourceService.getByDataSource(dataSourceCode);
    }

    public boolean doValidate() {
        try {


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
            equipmentInspectionForm.setStickerNo(equipmentInspectionForm.getSticker().getStickerNo());

            if (UtilityHelper.decipher(persistentMode).equals("changeStep")) {

                maxStepSeq = inspectionFormWorkflowStepService.getLastStepSeq(equipmentInspectionForm.getId());
                InspectionFormWorkflow inspectionFormWorkflow = inspectionFormWorkflowService
                        .getCurrentInspectionFormWorkFlow(equipmentInspectionForm.getId());
                equipmentInspectionForm.setSysUserByReviewedBy(loginBean.getUser());
                equipmentInspectionForm.setNameAndAddressOfEmployer(equipmentInspectionForm.getCompany().getName());
                inspectionFormWorkflow.setEquipmentInspectionForm(equipmentInspectionForm);
                inspectionFormWorkflow.setWorkflowDefinition(workflowDefinitionFinal);
                inspectionFormWorkflow.setTask(task);

                InspectionFormWorkflowStep inspectionFormWorkflowStep = new InspectionFormWorkflowStep();
                inspectionFormWorkflowStep.setEquipmentInspectionForm(equipmentInspectionForm);
                inspectionFormWorkflowStep.setInspectionFormDocument("document-final".getBytes());
                inspectionFormWorkflowStep.setProcessDate(Calendar.getInstance().getTime());
                inspectionFormWorkflowStep.setSysUser(loginBean.getUser());
                inspectionFormWorkflowStep.setSysUserComment(comment != null ? comment : "");
                inspectionFormWorkflowStep.setStepSeq((short) (maxStepSeq + 1));
                inspectionFormWorkflowStep.setWorkflowDefinition(workflowDefinitionFinal);

                equipmentInspectionFormService.saveToStep(equipmentInspectionForm, inspectionFormWorkflow,
                        inspectionFormWorkflowStep);
                step = "03";
                doPrint();
                UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
                return "";
            }

            for (ColumnContent columnContent : columnContents) {
                EquipmentInspectionFormItem equipmentInspectionFormItem = new EquipmentInspectionFormItem();
                equipmentInspectionFormItem.setEquipmentInspectionForm(equipmentInspectionForm);
                equipmentInspectionFormItem.setGeneralEquipmentItem(columnContent.getGeneralEquipmentItem());
                equipmentInspectionFormItem.setAliasName(columnContent.getAliasName());
                equipmentInspectionFormItem.setItemValue(columnContent.getContentValue());
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
                inspectionFormWorkflowStepTwo.setSysUserComment(comment != null ? comment : "");
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
                inspectionFormWorkflowStepOne.setSysUserComment(comment != null ? comment : "");
                inspectionFormWorkflowStepOne.setStepSeq((short) (maxStepSeq + 1));
                inspectionFormWorkflowSteps.add(inspectionFormWorkflowStepOne);
                InspectionFormWorkflowStep inspectionFormWorkflowStepTwo = new InspectionFormWorkflowStep();
                inspectionFormWorkflowStepTwo.setEquipmentInspectionForm(equipmentInspectionForm);
                inspectionFormWorkflowStepTwo
                        .setWorkflowDefinition(workflowDefinitionInit.getWorkflowDefinitionByNext());
                inspectionFormWorkflowStepTwo.setInspectionFormDocument("doc".getBytes());
                inspectionFormWorkflowStepTwo.setProcessDate(Calendar.getInstance().getTime());
                inspectionFormWorkflowStepTwo.setSysUser(loginBean.getUser());
                inspectionFormWorkflowStepTwo.setSysUserComment(comment != null ? comment : "");
                inspectionFormWorkflowStepTwo.setStepSeq((short) (inspectionFormWorkflowStepOne.getStepSeq() + 1));

                inspectionFormWorkflowSteps.add(inspectionFormWorkflowStepTwo);
                equipmentInspectionForm.setInspectionFormWorkflowSteps(inspectionFormWorkflowSteps);

                equipmentInspectionFormService.saveOrUpdate(equipmentInspectionForm);
                // confirm reservation if applicable
                try {
                    if (task != null) {
                        com.smat.ins.model.service.SeqReservationService seqReservationService = (com.smat.ins.model.service.SeqReservationService) BeanUtility.getBean("seqReservationService");
                        if (seqReservationService != null) {
                            try { seqReservationService.confirmReservedReportNoForTask(task.getId()); } catch (Exception ignore) {}
                        }
                    }
                } catch (Exception ignore) {}
                Sticker sticker = stickerService.findByUniqueField("stickerNo", equipmentInspectionForm.getStickerNo());
                sticker.setIsUsed(true);
                sticker.setIsPrinted(true);
                sticker.setSysUserByCreatedBy(loginBean.getUser());
                sticker.setSysUserByPrintedBy(loginBean.getUser());
                stickerService.update(sticker);
                UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
                return "pretty:inspection/my-tasks";
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
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
                inspectionFormWorkflowStep.setSysUserComment(comment != null ? comment : "");
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
            // prepare attachments zip session attributes so ZipDocumentDownloadBean can build/download it
            try { prepareAttachmentsZipDownload(); } catch (Exception ignore) {}

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

                // ===== Company fallback logic =====
                String companyName = "";
                if (equipmentInspectionForm.getNameAndAddressOfEmployer() != null && !equipmentInspectionForm.getNameAndAddressOfEmployer().trim().isEmpty()) {
                    companyName = equipmentInspectionForm.getNameAndAddressOfEmployer().trim();
                } else if (equipmentInspectionForm.getCompany() != null
                        && equipmentInspectionForm.getCompany().getName() != null
                        && !equipmentInspectionForm.getCompany().getName().trim().isEmpty()) {
                    companyName = equipmentInspectionForm.getCompany().getName().trim();
                }
                // ضع كلا التمثيلين المفتاحيين للقالب (حساسية الحالة)
                data.put("Company", companyName);
                data.put("company", companyName);

                // Address (fallback to company.address if present)
                String companyAddress = "";
                if (equipmentInspectionForm.getCompany() != null && equipmentInspectionForm.getCompany().getAddress() != null) {
                    companyAddress = equipmentInspectionForm.getCompany().getAddress();
                }
                data.put("Address", companyAddress);

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

            // --- Persist a copy of the generated PDF into the inspection attachments folder ---
            try {
                byte[] pdfBytes = pdfDocOutputStream.toByteArray();

                // services used for storing attachments (reuse same pattern as handleAttachmentUpload)
                com.smat.ins.model.service.CabinetService cabinetService = (com.smat.ins.model.service.CabinetService) BeanUtility.getBean("cabinetService");
                com.smat.ins.model.service.CabinetFolderService cabinetFolderService = (com.smat.ins.model.service.CabinetFolderService) BeanUtility.getBean("cabinetFolderService");
                com.smat.ins.model.service.ArchiveDocumentService archiveDocumentService = (com.smat.ins.model.service.ArchiveDocumentService) BeanUtility.getBean("archiveDocumentService");
                com.smat.ins.model.service.ArchiveDocumentFileService archiveDocumentFileService = (com.smat.ins.model.service.ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");
                com.smat.ins.model.service.CabinetFolderDocumentService cabinetFolderDocumentService = (com.smat.ins.model.service.CabinetFolderDocumentService) BeanUtility.getBean("cabinetFolderDocumentService");

                // locate/ensure inspection cabinet
                String targetCabinetCode = "INS-DEFAULT";
                com.smat.ins.model.entity.Cabinet targetCabinet = null;
                for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                    if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
                }
                if (targetCabinet == null) {
                    com.smat.ins.util.CabinetDefaultsCreator.ensureDefaultCabinets(loginBean.getUser());
                    for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                        if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
                    }
                }
                if (targetCabinet != null) {
                    // pick drawer/definition code "01"
                    com.smat.ins.model.entity.CabinetDefinition def = null;
                    if (targetCabinet.getCabinetDefinitions() != null) {
                        for (Object od : targetCabinet.getCabinetDefinitions()) {
                            com.smat.ins.model.entity.CabinetDefinition cd = (com.smat.ins.model.entity.CabinetDefinition) od;
                            if ("01".equals(cd.getCode())) { def = cd; break; }
                        }
                    }
                    if (def == null && targetCabinet.getCabinetDefinitions() != null && !targetCabinet.getCabinetDefinitions().isEmpty())
                        def = (com.smat.ins.model.entity.CabinetDefinition) targetCabinet.getCabinetDefinitions().iterator().next();

                    if (def != null) {
                        // determine folder name (reportNo preferred)
                        String folderName = null;
                        if (equipmentInspectionForm != null && equipmentInspectionForm.getReportNo() != null && !equipmentInspectionForm.getReportNo().trim().isEmpty()) {
                            folderName = equipmentInspectionForm.getReportNo().trim();
                        } else if (equipmentInspectionForm != null && equipmentInspectionForm.getId() != null) {
                            folderName = "form_" + equipmentInspectionForm.getId().toString();
                        } else {
                            folderName = "form_" + String.valueOf(System.currentTimeMillis());
                        }

                        com.smat.ins.model.entity.CabinetFolder cabinetFolder = null;
                        try {
                            java.util.List<com.smat.ins.model.entity.CabinetFolder> existing = cabinetFolderService.getByCabinetDefinition(def);
                            if (existing != null) {
                                for (com.smat.ins.model.entity.CabinetFolder f : existing) {
                                    String fn = folderName == null ? "" : folderName.trim().toLowerCase();
                                    String fa = f.getArabicName() == null ? "" : f.getArabicName().trim().toLowerCase();
                                    String fe = f.getEnglishName() == null ? "" : f.getEnglishName().trim().toLowerCase();
                                    if (fn.equals(fa) || fn.equals(fe)) { cabinetFolder = f; break; }
                                }
                            }
                        } catch (Exception ignore) {}

                        if (cabinetFolder == null) {
                            cabinetFolder = new com.smat.ins.model.entity.CabinetFolder();
                            cabinetFolder.setCabinetDefinition(def);
                            cabinetFolder.setSysUser(loginBean.getUser());
                            cabinetFolder.setArabicName(folderName);
                            cabinetFolder.setEnglishName(folderName);
                            int nextCode = 1;
                            try { java.util.List<com.smat.ins.model.entity.CabinetFolder> existing2 = cabinetFolderService.getByCabinetDefinition(def); nextCode = existing2 != null ? existing2.size() + 1 : 1; } catch (Exception ignore) {}
                            cabinetFolder.setCode(String.format("%03d", nextCode));
                            cabinetFolder.setCreatedDate(new java.util.Date());
                            cabinetFolderService.saveOrUpdate(cabinetFolder);
                        }

                        // create physical folder
                        String mainLocation = com.smat.ins.util.CabinetDefaultsCreator.selectMainLocation(targetCabinet.getCabinetLocation());
                        java.nio.file.Path folderPath = Paths.get(mainLocation, targetCabinet.getCode(), def.getCode(), cabinetFolder.getCode());
                        Files.createDirectories(folderPath);

                        // create ArchiveDocument and ArchiveDocumentFile for the pdf
                        String original = "EquipmentCertificate_" + folderName + ".pdf";
                        String safe = original.replaceAll("[^a-zA-Z0-9._-]", "_");

                        com.smat.ins.model.entity.ArchiveDocument archiveDocument = new com.smat.ins.model.entity.ArchiveDocument();
                        try {
                            com.smat.ins.model.service.ArchiveDocumentTypeService archiveDocumentTypeService = (com.smat.ins.model.service.ArchiveDocumentTypeService) BeanUtility.getBean("archiveDocumentTypeService");
                            java.util.List<com.smat.ins.model.entity.ArchiveDocumentType> types = archiveDocumentTypeService.findAll();
                            if (types != null && !types.isEmpty()) archiveDocument.setArchiveDocumentType(types.get(0));
                        } catch (Exception ignore) {}
                        archiveDocument.setArabicName(original); archiveDocument.setEnglishName(original); archiveDocument.setIsDirectory(false);
                        archiveDocument.setCreatedDate(new java.util.Date()); archiveDocument.setSysUserByCreatorUser(loginBean.getUser());
                        archiveDocumentService.saveOrUpdate(archiveDocument);

                        com.smat.ins.model.entity.ArchiveDocumentFile docFile = new com.smat.ins.model.entity.ArchiveDocumentFile();
                        docFile.setArchiveDocument(archiveDocument);
                        docFile.setName(original);
                        String ext = "pdf";
                        docFile.setExtension(ext);
                        docFile.setMimeType("application/pdf");
                        docFile.setUuid(java.util.UUID.randomUUID().toString());
                        docFile.setFileSize((long) pdfBytes.length);
                        docFile.setCreatedDate(new java.util.Date());

                        try {
                            Long maxCode = archiveDocumentFileService.getMaxArchiveDocumentFileCode(archiveDocument);
                            int codeLength = 9;
                            String fileCode = String.format("%0" + codeLength + "d", (maxCode == null ? 0L : maxCode) + 1L);
                            docFile.setCode(fileCode);

                            // Save with fixed friendly name 'inspection_report.pdf' (avoid numeric prefix)
                            String storedName = "inspection_report." + ext;
                            java.nio.file.Path target = folderPath.resolve(storedName);
                            // If file already exists, Files.write with CREATE_NEW will throw; let fallback handle unique naming
                            Files.write(target, pdfBytes, StandardOpenOption.CREATE_NEW);

                            String logical = targetCabinet.getCode() + "/" + def.getCode() + "/" + cabinetFolder.getCode() + "/" + storedName;
                            docFile.setLogicalPath(logical);
                            docFile.setServerPath(target.toString());

                        } catch (Exception ex) {
                            // fallback: use timestamp to produce a unique friendly name
                            String storedName = "inspection_report_" + System.currentTimeMillis() + "." + ext;
                            java.nio.file.Path target = folderPath.resolve(storedName);
                            Files.write(target, pdfBytes, StandardOpenOption.CREATE_NEW);
                            String logical = targetCabinet.getCode() + "/" + def.getCode() + "/" + cabinetFolder.getCode() + "/" + storedName;
                            docFile.setLogicalPath(logical); docFile.setServerPath(target.toString());
                        }

                        archiveDocumentFileService.saveOrUpdate(docFile);

                        com.smat.ins.model.entity.CabinetFolderDocument cfd = new com.smat.ins.model.entity.CabinetFolderDocument();
                        cfd.setCabinetFolder(cabinetFolder); cfd.setSysUser(loginBean.getUser()); cfd.setArchiveDocument(archiveDocument);
                        cfd.setCreatedDate(new java.util.Date()); cabinetFolderDocumentService.saveOrUpdate(cfd);
                    }
                }
            } catch (Exception writeEx) {
                // don't break printing if storing attachment fails; just log
                writeEx.printStackTrace();
            }

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

            // If there are attachments for this report folder, open a zip download in a new tab
            try {
                String folderName = null;
                if (equipmentInspectionForm != null && equipmentInspectionForm.getReportNo() != null && !equipmentInspectionForm.getReportNo().trim().isEmpty()) {
                    folderName = equipmentInspectionForm.getReportNo().trim();
                } else if (equipmentInspectionForm != null && equipmentInspectionForm.getId() != null) {
                    folderName = "form_" + equipmentInspectionForm.getId().toString();
                }
                if (folderName != null && hasFilesInCabinetFolder("INS-DEFAULT", "01", folderName)) {
                    String ctx = ((javax.faces.context.FacesContext) javax.faces.context.FacesContext.getCurrentInstance()).getExternalContext().getRequestContextPath();
                    String url = ctx + "/attachments/zip?type=ins&reportNo=" + java.net.URLEncoder.encode(folderName, "UTF-8");
                    PrimeFaces.current().executeScript("window.open('" + url + "', '_blank')");
                }
            } catch (Exception ignore) {}

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

    /**
     * Locate the cabinet folder used for this inspection (by reportNo or id),
     * and populate session attributes expected by ZipDocumentDownloadBean:
     * archDocId, cabinetFolderId, cabinetId, cabinetDefinitionId
     */
    public void prepareAttachmentsZipDownload() throws Exception {
        try {
            com.smat.ins.model.service.CabinetService cabinetService = (com.smat.ins.model.service.CabinetService) BeanUtility.getBean("cabinetService");
            com.smat.ins.model.service.CabinetFolderService cabinetFolderService = (com.smat.ins.model.service.CabinetFolderService) BeanUtility.getBean("cabinetFolderService");
            com.smat.ins.model.service.CabinetFolderDocumentService cabinetFolderDocumentService = (com.smat.ins.model.service.CabinetFolderDocumentService) BeanUtility.getBean("cabinetFolderDocumentService");
            com.smat.ins.model.service.CabinetDefinitionService cabinetDefinitionService = (com.smat.ins.model.service.CabinetDefinitionService) BeanUtility.getBean("cabinetDefinitionService");

            String targetCabinetCode = "INS-DEFAULT";
            com.smat.ins.model.entity.Cabinet targetCabinet = null;
            for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
            }
            if (targetCabinet == null) return;

            com.smat.ins.model.entity.CabinetDefinition def = null;
            if (targetCabinet.getCabinetDefinitions() != null) {
                for (Object od : targetCabinet.getCabinetDefinitions()) {
                    com.smat.ins.model.entity.CabinetDefinition cd = (com.smat.ins.model.entity.CabinetDefinition) od;
                    if ("01".equals(cd.getCode())) { def = cd; break; }
                }
            }
            if (def == null && targetCabinet.getCabinetDefinitions() != null && !targetCabinet.getCabinetDefinitions().isEmpty())
                def = (com.smat.ins.model.entity.CabinetDefinition) targetCabinet.getCabinetDefinitions().iterator().next();
            if (def == null) return;

            String folderName = null;
            if (equipmentInspectionForm != null && equipmentInspectionForm.getReportNo() != null && !equipmentInspectionForm.getReportNo().trim().isEmpty()) {
                folderName = equipmentInspectionForm.getReportNo().trim();
            } else if (equipmentInspectionForm != null && equipmentInspectionForm.getId() != null) {
                folderName = "form_" + equipmentInspectionForm.getId().toString();
            } else {
                return; // nothing to prepare
            }

            com.smat.ins.model.entity.CabinetFolder cabinetFolder = null;
            try {
                java.util.List<com.smat.ins.model.entity.CabinetFolder> existing = cabinetFolderService.getByCabinetDefinition(def);
                if (existing != null) {
                    for (com.smat.ins.model.entity.CabinetFolder f : existing) {
                        String fn = folderName == null ? "" : folderName.trim().toLowerCase();
                        String fa = f.getArabicName() == null ? "" : f.getArabicName().trim().toLowerCase();
                        String fe = f.getEnglishName() == null ? "" : f.getEnglishName().trim().toLowerCase();
                        if (fn.equals(fa) || fn.equals(fe)) {
                            cabinetFolder = f;
                            break;
                        }
                    }
                }
            } catch (Exception ignore) {}

            if (cabinetFolder == null) return;

            java.util.List<com.smat.ins.model.entity.CabinetFolderDocument> items = cabinetFolderDocumentService.getByCabinetFolder(cabinetFolder);
            if (items == null || items.isEmpty()) return;
            com.smat.ins.model.entity.ArchiveDocument firstDoc = items.get(0).getArchiveDocument();
            if (firstDoc == null) return;

            UtilityHelper.putSessionAttr("archDocId", firstDoc.getId());
            UtilityHelper.putSessionAttr("cabinetFolderId", cabinetFolder.getId());
            UtilityHelper.putSessionAttr("cabinetId", targetCabinet.getId());
            UtilityHelper.putSessionAttr("cabinetDefinitionId", def.getId());

        } catch (Exception e) {
            // swallow - caller will continue printing even if zip not prepared
            e.printStackTrace();
        }
    }

    /**
     * Return the list of ArchiveDocumentFile objects that are recorded in the
     * cabinet folder for this inspection form. Returns empty list on error.
     */
    public java.util.List<com.smat.ins.model.entity.ArchiveDocumentFile> getAttachmentFiles() {
        try {
            com.smat.ins.model.service.CabinetService cabinetService = (com.smat.ins.model.service.CabinetService) BeanUtility.getBean("cabinetService");
            com.smat.ins.model.service.CabinetFolderService cabinetFolderService = (com.smat.ins.model.service.CabinetFolderService) BeanUtility.getBean("cabinetFolderService");
            com.smat.ins.model.service.CabinetFolderDocumentService cabinetFolderDocumentService = (com.smat.ins.model.service.CabinetFolderDocumentService) BeanUtility.getBean("cabinetFolderDocumentService");
            com.smat.ins.model.service.ArchiveDocumentFileService archiveDocumentFileService = (com.smat.ins.model.service.ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");

            String targetCabinetCode = "INS-DEFAULT";
            com.smat.ins.model.entity.Cabinet targetCabinet = null;
            for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
            }
            if (targetCabinet == null) return java.util.Collections.emptyList();

            com.smat.ins.model.entity.CabinetDefinition def = null;
            if (targetCabinet.getCabinetDefinitions() != null) {
                for (Object od : targetCabinet.getCabinetDefinitions()) {
                    com.smat.ins.model.entity.CabinetDefinition cd = (com.smat.ins.model.entity.CabinetDefinition) od;
                    if ("01".equals(cd.getCode())) { def = cd; break; }
                }
            }
            if (def == null && targetCabinet.getCabinetDefinitions() != null && !targetCabinet.getCabinetDefinitions().isEmpty())
                def = (com.smat.ins.model.entity.CabinetDefinition) targetCabinet.getCabinetDefinitions().iterator().next();
            if (def == null) return java.util.Collections.emptyList();

            String folderName = null;
            if (equipmentInspectionForm != null && equipmentInspectionForm.getReportNo() != null && !equipmentInspectionForm.getReportNo().trim().isEmpty()) {
                folderName = equipmentInspectionForm.getReportNo().trim();
            } else if (equipmentInspectionForm != null && equipmentInspectionForm.getId() != null) {
                folderName = "form_" + equipmentInspectionForm.getId().toString();
            } else {
                return java.util.Collections.emptyList();
            }

            com.smat.ins.model.entity.CabinetFolder cabinetFolder = null;
            try {
                java.util.List<com.smat.ins.model.entity.CabinetFolder> existing = cabinetFolderService.getByCabinetDefinition(def);
                if (existing != null) {
                    for (com.smat.ins.model.entity.CabinetFolder f : existing) {
                        String fn = folderName == null ? "" : folderName.trim().toLowerCase();
                        String fa = f.getArabicName() == null ? "" : f.getArabicName().trim().toLowerCase();
                        String fe = f.getEnglishName() == null ? "" : f.getEnglishName().trim().toLowerCase();
                        if (fn.equals(fa) || fn.equals(fe)) { cabinetFolder = f; break; }
                    }
                }
            } catch (Exception ignore) {}
            if (cabinetFolder == null) return java.util.Collections.emptyList();

            java.util.List<com.smat.ins.model.entity.CabinetFolderDocument> items = cabinetFolderDocumentService.getByCabinetFolder(cabinetFolder);
            if (items == null || items.isEmpty()) return java.util.Collections.emptyList();

            java.util.List<com.smat.ins.model.entity.ArchiveDocumentFile> result = new java.util.ArrayList<>();
            for (com.smat.ins.model.entity.CabinetFolderDocument cfd : items) {
                com.smat.ins.model.entity.ArchiveDocument ad = cfd.getArchiveDocument();
                if (ad == null) continue;
                java.util.List<com.smat.ins.model.entity.ArchiveDocumentFile> files = archiveDocumentFileService.getBy(ad);
                if (files != null && !files.isEmpty()) result.addAll(files);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    // Check whether the given cabinet (by code) and drawer (code) contains a folder
    // with given folderName and that the physical folder contains at least one regular file.
    private boolean hasFilesInCabinetFolder(String cabinetCode, String drawerCode, String folderName) {
        try {
            com.smat.ins.model.service.CabinetService cabinetService = (com.smat.ins.model.service.CabinetService) BeanUtility.getBean("cabinetService");
            com.smat.ins.model.service.CabinetDefinitionService cabinetDefinitionService = (com.smat.ins.model.service.CabinetDefinitionService) BeanUtility.getBean("cabinetDefinitionService");
            com.smat.ins.model.service.CabinetFolderService cabinetFolderService = (com.smat.ins.model.service.CabinetFolderService) BeanUtility.getBean("cabinetFolderService");

            com.smat.ins.model.entity.Cabinet targetCabinet = null;
            for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                if (cabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
            }
            if (targetCabinet == null) return false;

            com.smat.ins.model.entity.CabinetDefinition def = null;
            if (targetCabinet.getCabinetDefinitions() != null) {
                for (Object od : targetCabinet.getCabinetDefinitions()) {
                    com.smat.ins.model.entity.CabinetDefinition cd = (com.smat.ins.model.entity.CabinetDefinition) od;
                    if (drawerCode.equals(cd.getCode())) { def = cd; break; }
                }
            }
            if (def == null) return false;

            java.util.List<com.smat.ins.model.entity.CabinetFolder> existing = cabinetFolderService.getByCabinetDefinition(def);
            com.smat.ins.model.entity.CabinetFolder cabinetFolder = null;
            if (existing != null) {
                for (com.smat.ins.model.entity.CabinetFolder f : existing) {
                    String fn = folderName == null ? "" : folderName.trim().toLowerCase();
                    String fa = f.getArabicName() == null ? "" : f.getArabicName().trim().toLowerCase();
                    String fe = f.getEnglishName() == null ? "" : f.getEnglishName().trim().toLowerCase();
                    if (fn.equals(fa) || fn.equals(fe)) { cabinetFolder = f; break; }
                }
            }
            if (cabinetFolder == null) return false;

            String mainLocation = com.smat.ins.util.CabinetDefaultsCreator.selectMainLocation(targetCabinet.getCabinetLocation());
            java.nio.file.Path folderPath = java.nio.file.Paths.get(mainLocation, targetCabinet.getCode(), def.getCode(), cabinetFolder.getCode());
            if (!java.nio.file.Files.exists(folderPath) || !java.nio.file.Files.isDirectory(folderPath)) return false;
            try (java.util.stream.Stream<java.nio.file.Path> s = java.nio.file.Files.list(folderPath)) {
                return s.anyMatch(p -> java.nio.file.Files.isRegularFile(p));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

        // ===== Company fallback logic =====
        String companyName = "";
        if (eForm.getNameAndAddressOfEmployer() != null && !eForm.getNameAndAddressOfEmployer().trim().isEmpty()) {
            companyName = eForm.getNameAndAddressOfEmployer().trim();
        } else if (eForm.getCompany() != null && eForm.getCompany().getName() != null && !eForm.getCompany().getName().trim().isEmpty()) {
            companyName = eForm.getCompany().getName().trim();
        }
        data.put("company", companyName);
        data.put("Company", companyName); // also add uppercase key for template compatibility

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

    /**
     * Delete an attachment (ArchiveDocumentFile) by id.
     * This removes the DB record, deletes the physical file if present,
     * and deletes the parent ArchiveDocument when it has no remaining files.
     */
    public void deleteAttachment(java.lang.Long archiveDocumentFileId) {
        try {
            if (archiveDocumentFileId == null) {
                UtilityHelper.addErrorMessage("Invalid attachment id");
                return;
            }

            com.smat.ins.model.service.ArchiveDocumentFileService archiveDocumentFileService = (com.smat.ins.model.service.ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");
            com.smat.ins.model.service.ArchiveDocumentService archiveDocumentService = (com.smat.ins.model.service.ArchiveDocumentService) BeanUtility.getBean("archiveDocumentService");

            com.smat.ins.model.entity.ArchiveDocumentFile docFile = archiveDocumentFileService.findById(archiveDocumentFileId);
            if (docFile == null) {
                UtilityHelper.addErrorMessage("Attachment not found");
                return;
            }

            com.smat.ins.model.entity.ArchiveDocument archiveDocument = docFile.getArchiveDocument();
            String serverPath = docFile.getServerPath();

            // delete DB record for the file
            try {
                archiveDocumentFileService.delete(docFile);
            } catch (Exception ex) {
                // attempt fallback: mark error but continue trying to remove file
                ex.printStackTrace();
            }

            // remove physical file if present
            try {
                if (serverPath != null && !serverPath.trim().isEmpty()) {
                    java.nio.file.Path p = java.nio.file.Paths.get(serverPath);
                    java.nio.file.Files.deleteIfExists(p);
                }
            } catch (Exception ex) {
                // non-fatal
                ex.printStackTrace();
            }

            // if no remaining files belong to this archiveDocument, delete the archiveDocument
            try {
                if (archiveDocument != null) {
                    java.util.List<com.smat.ins.model.entity.ArchiveDocumentFile> remaining = null;
                    try { remaining = archiveDocumentFileService.getBy(archiveDocument); } catch (Exception ign) { }
                    if (remaining == null || remaining.isEmpty()) {
                        try { archiveDocumentService.deleteArchiveDoc(archiveDocument); } catch (Exception ign) { ign.printStackTrace(); }
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); }

            UtilityHelper.addInfoMessage("Attachment deleted");
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Error deleting attachment: " + e.getMessage());
        } finally {
            try { PrimeFaces.current().ajax().update(":form:ins_attachments_section"); } catch (Exception ignore) {}
        }
    }


    // Save current form as draft
    public void saveDraft() {
        try {
            TaskDraft draft = new TaskDraft();
            if (task != null) draft.setTaskId(task.getId());
            if (loginBean != null && loginBean.getUser() != null) {
                draft.setDraftOwnerId(loginBean.getUser().getId().intValue());
                draft.setDraftOwnerName(loginBean.getUser().getDisplayName());
            }
            // set a clear task type so we can scope drafts per form
            draft.setTaskType("inspection_equipment");
            Map<String,Object> payload = new HashMap<>();
            // convert to safe shallow structures to avoid serializing full JPA graphs
            payload.put("equipmentInspectionForm", com.smat.ins.util.DraftUtils.toSafeStructure(this.equipmentInspectionForm));
            payload.put("columnContents", com.smat.ins.util.DraftUtils.toSafeStructure(this.columnContents));
            payload.put("examinationType", com.smat.ins.util.DraftUtils.toSafeStructure(this.examinationType));
            payload.put("equipmentType", com.smat.ins.util.DraftUtils.toSafeStructure(this.equipmentType));
            // add a few extra UI-related fields so we can fully restore the form
            payload.put("stepComment", this.stepComment);
            payload.put("persistentMode", this.persistentMode);
            payload.put("disabled", this.disabled);
            byte[] bytes = objectMapper.writeValueAsBytes(payload);
            draft.setDraftData(bytes);
            draft.setCreatedDate(new Date());
            TaskDraft saved = taskDraftService.saveOrUpdate(draft);
            if (saved != null) UtilityHelper.addInfoMessage("Draft saved successfully (v=" + saved.getVersion() + ")");
            else UtilityHelper.addErrorMessage("Failed to save draft");
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Error saving draft: " + e.getMessage());
        } finally {
            // reset the savingDraft flag so normal validations resume
            try { this.savingDraft = false; } catch (Exception ex) {}
        }
    }
    public void loadDraft() {
        try {

            TaskDraft draft = null;
            if (task != null && task.getId() != null) {
                draft = taskDraftService.findByTaskId(task.getId());
            }
            if (draft == null) {
                SysUser current = loginBean.getUser();
                if (current != null) {
                    // try to get a draft scoped to this form type first
                    draft = taskDraftService.findLatestByOwnerAndType(current.getId().intValue(), "inspection_equipment");
                    if (draft == null) {
                        // fallback to any latest draft of the owner
                        draft = taskDraftService.findLatestByOwner(current.getId().intValue());
                    }
                }
            }

            if (draft == null) {
                UtilityHelper.addInfoMessage("No draft found.");
                return;
            }

            byte[] data = draft.getDraftData();
            if (data == null || data.length == 0) {
                UtilityHelper.addInfoMessage("Draft is empty.");
                return;
            }

            Map<String, Object> payload = objectMapper.readValue(data, Map.class);

            // Try best-effort restoring from the shallow structures we stored
            if (payload.containsKey("equipmentInspectionForm")) {
                try {
                    Object raw = payload.get("equipmentInspectionForm");
                    if (raw instanceof Map) {
                        Map<String, Object> m = (Map<String, Object>) raw;
                        EquipmentInspectionForm eif = new EquipmentInspectionForm();
                        // primitive/string fields
                        if (m.containsKey("reportNo")) eif.setReportNo((String) m.get("reportNo"));
                        if (m.containsKey("timeSheetNo")) eif.setTimeSheetNo((String) m.get("timeSheetNo"));
                        if (m.containsKey("jobNo")) eif.setJobNo((String) m.get("jobNo"));
                        if (m.containsKey("stickerNo")) eif.setStickerNo((String) m.get("stickerNo"));
                        if (m.containsKey("nameAndAddressOfEmployer")) eif.setNameAndAddressOfEmployer((String) m.get("nameAndAddressOfEmployer"));
                        // dates may be serialized as timestamps/strings
                        try { if (m.containsKey("dateOfThoroughExamination")) eif.setDateOfThoroughExamination(objectMapper.convertValue(m.get("dateOfThoroughExamination"), java.util.Date.class)); } catch (Exception ex) {}
                        try { if (m.containsKey("nextExaminationDate")) eif.setNextExaminationDate(objectMapper.convertValue(m.get("nextExaminationDate"), java.util.Date.class)); } catch (Exception ex) {}
                        try { if (m.containsKey("previousExaminationDate")) eif.setPreviousExaminationDate(objectMapper.convertValue(m.get("previousExaminationDate"), java.util.Date.class)); } catch (Exception ex) {}

                        // relations by id (DraftUtils stores related entity id under fieldName + "_id")
                        try {
                            Object companyIdObj = m.get("company_id") != null ? m.get("company_id") : (m.get("company") instanceof Map ? ((Map) m.get("company")).get("id") : null);
                            if (companyIdObj != null) {
                                Integer cid = objectMapper.convertValue(companyIdObj, Integer.class);
                                try { eif.setCompany(companyService.findById(cid)); } catch (Exception ex) {}
                            }
                        } catch (Throwable t) {}

                        try {
                            Object etIdObj = m.get("equipmentType_id") != null ? m.get("equipmentType_id") : (m.get("equipmentType") instanceof Map ? ((Map) m.get("equipmentType")).get("id") : null);
                            if (etIdObj != null) {
                                Number n = objectMapper.convertValue(etIdObj, Number.class);
                                if (n != null) {
                                    Short etid = n.shortValue();
                                    try { this.equipmentType = equipmentTypeService.findById(etid); eif.setEquipmentType(this.equipmentType); } catch (Exception ex) {}
                                }
                            }
                        } catch (Throwable t) {}

                        try {
                            Object exTypeIdObj = m.get("examinationType_id") != null ? m.get("examinationType_id") : (m.get("examinationType") instanceof Map ? ((Map) m.get("examinationType")).get("id") : null);
                            if (exTypeIdObj != null) {
                                Number n2 = objectMapper.convertValue(exTypeIdObj, Number.class);
                                if (n2 != null) {
                                    Short exid = n2.shortValue();
                                    try { this.examinationType = examinationTypeService.findById(exid); eif.setExaminationType(this.examinationType); } catch (Exception ex) {}
                                }
                            }
                        } catch (Throwable t) {}

                        // if stickerNo present, try to resolve the Sticker entity so the selectOneMenu can bind to it
                        try {
                            if (eif.getStickerNo() != null && !eif.getStickerNo().isEmpty()) {
                                Sticker st = null;
                                try {
                                    st = stickerService.findByUniqueField("stickerNo", eif.getStickerNo());
                                } catch (Exception ex) {
                                    // ignore
                                }
                                if (st == null) {
                                    // create a lightweight placeholder so UI shows the value
                                    st = new Sticker();
                                    st.setStickerNo(eif.getStickerNo());
                                }
                                eif.setSticker(st);
                                // ensure stickers list contains it so selectItems include the current sticker
                                if (this.stickers == null) this.stickers = new java.util.ArrayList<>();
                                boolean contains = false;
                                for (Sticker s : this.stickers) { if (s != null && s.getStickerNo() != null && s.getStickerNo().equals(st.getStickerNo())) { contains = true; break; } }
                                if (!contains) this.stickers.add(0, st);
                            }
                        } catch (Throwable t) {}
                        this.equipmentInspectionForm = eif;
                    } else {
                        EquipmentInspectionForm eif = objectMapper.convertValue(raw, EquipmentInspectionForm.class);
                        this.equipmentInspectionForm = eif;
                    }
                } catch (Exception ex) {
                    // ignore and keep current
                }
            }

            if (payload.containsKey("columnContents")) {
                try {
                    List<?> rawContents = (List<?>) payload.get("columnContents");
                    List<ColumnContent> loaded = new ArrayList<>();

                    // map to keep resolved form columns
                    Map<Integer, FormColumn> formColumnById = new java.util.HashMap<>();
                    if (this.formColumns != null) {
                        for (FormColumn fc : this.formColumns) {
                            if (fc != null && fc.getId() != null) formColumnById.put(fc.getId(), fc);
                        }
                    } else {
                        this.formColumns = new ArrayList<>();
                    }

                    // Resolve general equipment item service lazily
                    com.smat.ins.model.service.GeneralEquipmentItemService geService = null;

                    for (Object rc : rawContents) {
                        if (rc == null) continue;
                        ColumnContent cc = new ColumnContent();
                        if (rc instanceof Map) {
                            Map m = (Map) rc;
                            // id
                            if (m.get("id") != null) {
                                try { cc.setId(objectMapper.convertValue(m.get("id"), Integer.class)); } catch (Exception ex) {}
                            }
                            // aliasName
                            if (m.get("aliasName") != null) cc.setAliasName(String.valueOf(m.get("aliasName")));
                            // contentValue
                            if (m.get("contentValue") != null) cc.setContentValue(String.valueOf(m.get("contentValue")));
                            // contentOrder
                            if (m.get("contentOrder") != null) {
                                try {
                                    Number nc = objectMapper.convertValue(m.get("contentOrder"), Number.class);
                                    if (nc != null) cc.setContentOrder(Short.valueOf(nc.shortValue()));
                                } catch (Exception ex) {}
                            }

                            // formColumn_id (may be present as number)
                            Object fcIdObj = m.get("formColumn_id") != null ? m.get("formColumn_id") : (m.get("formColumn") instanceof Map ? ((Map) m.get("formColumn")).get("id") : null);
                            if (fcIdObj != null) {
                                try {
                                    Integer nInt = objectMapper.convertValue(fcIdObj, Integer.class);
                                    if (nInt != null) {
                                        Integer fcId = nInt.intValue();
                                        FormColumn real = formColumnById.get(fcId);
                                        if (real == null) {
                                            try {
                                                real = formColumnService.findById(fcId);
                                                if (real != null) {
                                                    formColumnById.put(real.getId(), real);
                                                    this.formColumns.add(real);
                                                }
                                            } catch (Exception ex) {
                                                // ignore missing column
                                            }
                                        }
                                        if (real != null) cc.setFormColumn(real);
                                    }
                                } catch (Exception ex) {}
                            }

                            // generalEquipmentItem_id
                            Object geIdObj = m.get("generalEquipmentItem_id") != null ? m.get("generalEquipmentItem_id") : (m.get("generalEquipmentItem") instanceof Map ? ((Map) m.get("generalEquipmentItem")).get("id") : null);
                            if (geIdObj != null) {
                                try {
                                    Integer ngInt = objectMapper.convertValue(geIdObj, Integer.class);
                                    if (ngInt != null) {
                                        Integer gid = ngInt.intValue();
                                        try {
                                            if (geService == null) geService = (com.smat.ins.model.service.GeneralEquipmentItemService) BeanUtility.getBean("generalEquipmentItemService");
                                            if (geService != null) {
                                                GeneralEquipmentItem ge = geService.findById(gid);
                                                if (ge != null) cc.setGeneralEquipmentItem(ge);
                                            }
                                        } catch (Exception ex) {
                                            // ignore
                                        }
                                    }
                                } catch (Exception ex) {}
                            }

                        } else {
                            // fallback: try to convert the whole object
                            try {
                                ColumnContent conv = objectMapper.convertValue(rc, ColumnContent.class);
                                if (conv != null) cc = conv;
                            } catch (Exception ex) {
                                // ignore
                            }
                        }
                        loaded.add(cc);
                    }

                    // Ensure formRows contains rows referenced by the loaded columns
                    if ((this.formRows == null || this.formRows.isEmpty()) && !formColumnById.isEmpty()) {
                        java.util.Set<FormRow> rows = new java.util.LinkedHashSet<>();
                        for (FormColumn fc : formColumnById.values()) {
                            if (fc != null && fc.getFormRow() != null) rows.add(fc.getFormRow());
                        }
                        this.formRows = new ArrayList<>(rows);
                    }

                    this.columnContents = loaded;
                    // refresh the dynamic content and sticker select on UI so restored values appear
                    try {
                        PrimeFaces.current().ajax().update("form:panelGridDaynamicContent");
                        PrimeFaces.current().ajax().update("form:selectoneMenu_sticker");
                    } catch (Exception ex) {
                        // ignore if PrimeFaces not available in this context
                    }
                } catch (Exception ex) {
                    // ignore
                }
            }

            if (payload.containsKey("equipmentType")) {
                try { this.equipmentType = objectMapper.convertValue(payload.get("equipmentType"), EquipmentType.class); } catch (Exception ex) {}
            }
            if (payload.containsKey("examinationType")) {
                try { this.examinationType = objectMapper.convertValue(payload.get("examinationType"), ExaminationType.class); } catch (Exception ex) {}
            }

            if (payload.containsKey("stepComment")) {
                this.stepComment = (payload.get("stepComment") != null) ? payload.get("stepComment").toString() : null;
            }
            if (payload.containsKey("persistentMode")) {
                this.persistentMode = payload.get("persistentMode") != null ? payload.get("persistentMode").toString() : null;
            }

            UtilityHelper.addInfoMessage("Draft loaded successfully.");
            PrimeFaces.current().ajax().update("@form"); // تحدّث الواجهة
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Error loading draft: " + e.getMessage());
        }
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

