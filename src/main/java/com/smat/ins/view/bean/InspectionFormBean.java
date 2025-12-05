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


import com.aspose.words.Bookmark;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.SaveFormat;
import com.aspose.words.Shape;
import com.aspose.words.ShapeType;
import com.aspose.words.WrapType;
import org.primefaces.PrimeFaces;

import javax.faces.context.FacesContext;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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
    // Sentinel sticker used to represent explicit "N\\A" selection in the UI
    private Sticker naSentinel;
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
        try { this.previousFormExists = null; } catch (Exception ignore) {}
        try { this.lastFiveFormsForCategory = null; } catch (Exception ignore) {}
    }

    public EquipmentInspectionForm getEquipmentInspectionForm() {
        return equipmentInspectionForm;
    }

    public void setEquipmentInspectionForm(EquipmentInspectionForm equipmentInspectionForm) {
        this.equipmentInspectionForm = equipmentInspectionForm;
        try { this.previousFormExists = null; } catch (Exception ignore) {}
        try { this.lastFiveFormsForCategory = null; } catch (Exception ignore) {}
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
        try {
            if (stickers == null) return new ArrayList<Sticker>();
            com.smat.ins.model.entity.SysUser current = null;
            try { current = loginBean.getUser(); } catch (Exception ignore) {}

            List<Sticker> result = new ArrayList<>();
            if (current != null) {
                for (Sticker s : stickers) {
                    try {
                        boolean notUsed = (s.getIsUsed() == null) || !s.getIsUsed();
                        if (s.getSysUserByForUser() != null && s.getSysUserByForUser().getId() != null
                                && s.getSysUserByForUser().getId().equals(current.getId()) && notUsed) {
                            result.add(s);
                        }
                    } catch (Exception ignore) {}
                }
            }

            // Ensure the currently selected sticker (if any) is present so the selectOneMenu can bind
            try {
                if (equipmentInspectionForm != null && equipmentInspectionForm.getSticker() != null) {
                    Sticker cur = equipmentInspectionForm.getSticker();
                    boolean contains = false;
                    for (Sticker s : result) {
                        if (s != null && cur != null && s.getStickerNo() != null && s.getStickerNo().equals(cur.getStickerNo())) { contains = true; break; }
                    }
                    if (!contains) result.add(0, cur);
                }
            } catch (Exception ignore) {}

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return stickers;
        }
    }

    public void setStickers(List<Sticker> stickers) {
        this.stickers = stickers;
    }

    public Sticker getNaSentinel() {
        return naSentinel;
    }

    public void setNaSentinel(Sticker naSentinel) {
        this.naSentinel = naSentinel;
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
        // initialize sentinel representing explicit N\A selection in the UI
        try {
            naSentinel = new Sticker();
            naSentinel.setStickerNo("N\\A");
        } catch (Exception ignore) {
            naSentinel = new Sticker();
        }
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
            // Run sticker vs Pass/Fail compatibility first so user sees message immediately
            if (!validateResultStickerCompatibility()) {
                return false;
            }

            // Allow explicit N\A: if sticker reference is null but stickerNo == "N\A" we accept it.
            if (equipmentInspectionForm.getSticker() == null) {
                String sNo = null;
                try {
                    sNo = equipmentInspectionForm.getStickerNo();
                } catch (Exception ignore) { }
                if (sNo == null || !"N\\A".equalsIgnoreCase(sNo.trim())) {
                    UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("youShouldChooseSticker"));
                    return false;
                }
            }
            // Add validation for selectedUserAliasRecipient if needed (e.g., must be
            // selected for certain steps)
            if (UtilityHelper.decipher(persistentMode).equals("insert")) {
                if (selectedUserAliasRecipient == null || selectedUserAliasRecipient.getSysUserBySysUser() == null) {
                    UtilityHelper.addErrorMessage("Please select a reviewer.");
                    return false;
                }
            }
            // (other validations follow)
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Validate the compatibility between any dropdown/result values in the
     * dynamic `columnContents` and the selected sticker. Rules implemented:
     * - If any template field value equals PASS -> sticker must NOT be N\\A
     * - If any template field value equals FAIL -> sticker MUST be N\\A
     * - If both PASS and FAIL are present -> block and show message
     *
     * Messages are displayed as info messages (same style as operation messages).
     */
    private boolean validateResultStickerCompatibility() {
        try {
            if (equipmentInspectionForm == null) return true;

            // determine stickerNo (prefer linked Sticker if present)
            String stickerNo = null;
            try {
                if (equipmentInspectionForm.getSticker() != null && equipmentInspectionForm.getSticker().getStickerNo() != null) {
                    stickerNo = equipmentInspectionForm.getSticker().getStickerNo();
                } else if (equipmentInspectionForm.getStickerNo() != null) {
                    stickerNo = equipmentInspectionForm.getStickerNo();
                }
            } catch (Exception ignore) { }
            boolean stickerIsNA = stickerNo != null && "N\\A".equalsIgnoreCase(stickerNo.trim());

            boolean anyPass = false;
            boolean anyFail = false;
            if (this.columnContents != null) {
                for (ColumnContent cc : this.columnContents) {
                    if (cc == null) continue;
                    Object val = cc.getContentValue();
                    if (val == null) continue;
                    String s = String.valueOf(val).trim();
                    if (s.equalsIgnoreCase("pass") || s.equalsIgnoreCase("passed")) anyPass = true;
                    if (s.equalsIgnoreCase("fail") || s.equalsIgnoreCase("failed")) anyFail = true;
                }
            }

            if (anyPass && anyFail) {
                UtilityHelper.addErrorMessage("Conflicting Pass/Fail values in template — please correct before saving.");
                return false;
            }

            if (anyPass && stickerIsNA) {
                UtilityHelper.addErrorMessage("cant choose 'N\\A' for sticker when result is 'Pass'");
                return false;
            }

            if (anyFail && !stickerIsNA) {
                UtilityHelper.addErrorMessage("cant choose sticker number other than 'N\\A' when result is 'Fail'");
                return false;
            }

            return true;
        } catch (Exception e) {
            // don't block save for unexpected errors in validation; log and allow
            e.printStackTrace();
            return true;
        }
    }

    public String doSave() {
        if (!doValidate()) {
            try { org.primefaces.PrimeFaces.current().ajax().addCallbackParam("approveOk", false); } catch (Exception ignore) {}
            return "";
        }
        try {
            // If user explicitly selected the UI sentinel for "N\\A",
            // clear the sticker relationship (we only persist stickerNo="N\\A").
            normalizeNaSentinelSticker();

            // Always update createdDate to current time when the user clicks Save
            try {
                if (this.equipmentInspectionForm != null) {
                    this.equipmentInspectionForm.setCreatedDate(Calendar.getInstance().getTime());
                }
            } catch (Exception ignore) {}

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
                inspectionFormWorkflowStep.setSysUserComment(comment != null ? comment : "");
                inspectionFormWorkflowStep.setStepSeq((short) (maxStepSeq + 1));
                inspectionFormWorkflowStep.setWorkflowDefinition(workflowDefinitionFinal);

                // 4) finally persist (this should save the updated items too)
                equipmentInspectionFormService.saveToStep(equipmentInspectionForm, inspectionFormWorkflow,
                        inspectionFormWorkflowStep);

                // 5) set local state and print if you want same behavior
                step = "03";
                doPrint();

                // Show a full-screen blocking overlay on the client and redirect
                try {
                    String ctx = ((javax.faces.context.FacesContext) javax.faces.context.FacesContext.getCurrentInstance()).getExternalContext().getRequestContextPath();
                    String script = "(function(){"
                            + "var existing=document.getElementById('approveBlocker'); if(existing) existing.remove();"
                            + "var div=document.createElement('div');"
                            + "div.id='approveBlocker';"
                            + "div.style.position='fixed';div.style.top='0';div.style.left='0';div.style.width='100%';div.style.height='100%';"
                            + "div.style.background='rgba(0,0,0,0.55)';div.style.zIndex='2147483647';div.style.display='flex';div.style.alignItems='center';div.style.justifyContent='center';"
                            + "div.innerHTML=\"<div style='text-align:center;color:white;font-weight:600'><i class='pi pi-spin pi-spinner' style='font-size:3rem'></i><div style='margin-top:1rem'>Preparing download... Please wait...</div></div>\";"
                            + "document.body.appendChild(div);"
                            + "setTimeout(function(){ window.location.href='" + ctx + "/tasks/my-tasks'; },5000);"
                            + "})();";
                    org.primefaces.PrimeFaces.current().executeScript(script);
                } catch (Exception ex) {
                    // don't break the success path if client script fails
                    ex.printStackTrace();
                }

                try { org.primefaces.PrimeFaces.current().ajax().addCallbackParam("approveOk", true); } catch (Exception ignore) {}
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
                inspectionFormWorkflowStepTwo.setSysUserComment(comment != null ? comment : "");
                inspectionFormWorkflowStepTwo.setStepSeq((short) (maxStepSeq + 1));

                equipmentInspectionForm.getInspectionFormWorkflowSteps().add(inspectionFormWorkflowStepTwo);

                // If the current user is the inspector who filled the form, update createdDate
                try {
                    if (equipmentInspectionForm.getSysUserByInspectionBy() != null && loginBean.getUser() != null &&
                            equipmentInspectionForm.getSysUserByInspectionBy().getId() != null && equipmentInspectionForm.getSysUserByInspectionBy().getId().equals(loginBean.getUser().getId())) {
                        equipmentInspectionForm.setCreatedDate(Calendar.getInstance().getTime());
                    }
                } catch (Exception ignore) {}

                equipmentInspectionFormService.merge(equipmentInspectionForm);
                UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

                return "pretty:inspection/my-tasks";

            } else if (UtilityHelper.decipher(persistentMode).equals("insert")) {
                maxStepSeq = inspectionFormWorkflowStepService.getLastStepSeq(null);
                equipmentInspectionForm.setSysUserByInspectionBy(loginBean.getUser());
                // record the timestamp when inspector saves the form for the first time
                try { equipmentInspectionForm.setCreatedDate(Calendar.getInstance().getTime()); } catch (Exception ignore) {}
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
            try { org.primefaces.PrimeFaces.current().ajax().addCallbackParam("approveOk", false); } catch (Exception ignore) {}
            UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
            return "";
        }
        return "";
    }

    public String returnToInspector() {
        if (!doValidate())
            return "";
        try {
            // ensure sentinel N\A does not remain as a transient Sticker reference
            normalizeNaSentinelSticker();
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

    // Public helper for Facelets to format dates consistently
    public String formatDateForDisplay(java.util.Date d) {
        try {
            return formatDate(d);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Safely format a date representative for an EquipmentInspectionForm.
     * Some code previously referred to `createdDate` which does not exist on
     * the entity; prefer using `dateOfThoroughExamination` then `nextExaminationDate`.
     */
    public String formatFormDate(EquipmentInspectionForm f) {
        try {
            if (f == null) return "";
            // Prefer createdDate (date when inspector filled/saved the form)
            try { if (f.getCreatedDate() != null) return formatDateForDisplay(f.getCreatedDate()); } catch (Exception ign) {}
            if (f.getDateOfThoroughExamination() != null) return formatDateForDisplay(f.getDateOfThoroughExamination());
            if (f.getNextExaminationDate() != null) return formatDateForDisplay(f.getNextExaminationDate());
            if (f.getPreviousExaminationDate() != null) return formatDateForDisplay(f.getPreviousExaminationDate());
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * If the selected sticker is the UI sentinel representing explicit "N\\A",
     * clear the sticker relationship so Hibernate won't try to persist an
     * unsaved transient Sticker. We still keep stickerNo="N\\A" on the form.
     */
    private void normalizeNaSentinelSticker() {
        try {
            if (equipmentInspectionForm == null) return;
            com.smat.ins.model.entity.Sticker sel = equipmentInspectionForm.getSticker();
            if (sel == null) return;
            String sNo = sel.getStickerNo();
            if (sNo != null && sNo.equals("N\\A")) {
                // clear the relationship (leave stickerNo populated)
                equipmentInspectionForm.setSticker(null);
                equipmentInspectionForm.setStickerNo("N\\A");
            }
        } catch (Exception ignore) {
            // non-fatal — best-effort
        }
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

            // Build QR using serialNo (if available) and reportNo instead of stickerNo
            String qrCodeData = UtilityHelper.getBaseURL() + "api/equipment-cert/" +
                    (equipmentInspectionForm != null && equipmentInspectionForm.getSticker() != null ? equipmentInspectionForm.getSticker().getSerialNo() : "") +
                    "&" +
                    (equipmentInspectionForm != null && equipmentInspectionForm.getReportNo() != null ? equipmentInspectionForm.getReportNo() : "");
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



    /**
     * Generate PDF bytes for the given equipment inspection form using the provided template.
     * Important: inserts QR as an INLINE Shape centered by paragraph alignment to avoid overlap.
     */
    private byte[] generateEquipmentPdfBytes(EquipmentInspectionForm eForm, FormTemplate tpl) throws Exception {
        if (eForm == null) throw new IllegalArgumentException("EquipmentInspectionForm is null");
        if (tpl == null || tpl.getPrintedDoc() == null) throw new IllegalArgumentException("Form template missing");

        com.aspose.words.Document document = new com.aspose.words.Document(com.google.common.io.ByteSource.wrap(tpl.getPrintedDoc().getData()).openStream());

        Map<String, Object> data = new HashMap<>();
        data.put("ReportNo", eForm.getReportNo() != null ? eForm.getReportNo() : "");
        data.put("TsNo", eForm.getTimeSheetNo() != null ? eForm.getTimeSheetNo() : "");
        data.put("JobNo", eForm.getJobNo() != null ? eForm.getJobNo() : "");
        data.put("StNo", eForm.getStickerNo() != null ? eForm.getStickerNo() : "");
        data.put("Dte", formatDate(eForm.getDateOfThoroughExamination()));
        data.put("Ned", formatDate(eForm.getNextExaminationDate()));
        data.put("Ped", formatDate(eForm.getPreviousExaminationDate()));

        String companyName = "";
        if (eForm.getNameAndAddressOfEmployer() != null && !eForm.getNameAndAddressOfEmployer().trim().isEmpty()) {
            companyName = eForm.getNameAndAddressOfEmployer().trim();
        } else if (eForm.getCompany() != null && eForm.getCompany().getName() != null && !eForm.getCompany().getName().trim().isEmpty()) {
            companyName = eForm.getCompany().getName().trim();
        }
        data.put("company", companyName);
        data.put("Company", companyName);
        data.put("Address", eForm.getCompany() != null ? eForm.getCompany().getAddress() : "");
        data.put("ExType", eForm.getExaminationType() != null ? eForm.getExaminationType().getEnglishName() : "");
        data.put("insBy", eForm.getSysUserByInspectionBy() != null ? eForm.getSysUserByInspectionBy().getEnDisplayName() : "");
        data.put("reviewedBy", loginBean != null && loginBean.getUser() != null ? loginBean.getUser().getEnDisplayName() : "");
        data.put("issureDate", formatDate(java.util.Calendar.getInstance().getTime()));

        // --- NOTE: For preview we DO NOT generate/insert QR or signatures.
        // Replace bookmarks that would hold QR/signature with explanatory text.

        java.util.function.BiConsumer<com.aspose.words.Bookmark, String> replaceBookmarkWithText =
                (bookmark, message) -> {
                    try {
                        if (bookmark == null) return;
                        com.aspose.words.BookmarkStart bs = bookmark.getBookmarkStart();
                        com.aspose.words.BookmarkEnd be = bookmark.getBookmarkEnd();

                        java.util.List<com.aspose.words.Node> nodesBetween = new java.util.ArrayList<>();
                        if (bs != null && be != null) {
                            com.aspose.words.Node cur = bs.getNextSibling();
                            while (cur != null && cur != be) {
                                com.aspose.words.Node next = cur.getNextSibling();
                                nodesBetween.add(cur);
                                cur = next;
                            }
                            for (com.aspose.words.Node n : nodesBetween) {
                                try { n.remove(); } catch (Exception ignore) {}
                            }
                            // use a DocumentBuilder constructed from the Document (cast DocumentBase -> Document)
                            com.aspose.words.Document docForBuilder = (com.aspose.words.Document) bookmark.getBookmarkStart().getDocument();
                            com.aspose.words.DocumentBuilder b = new com.aspose.words.DocumentBuilder(docForBuilder);
                            b.moveTo(bs);
                            b.getParagraphFormat().setAlignment(com.aspose.words.ParagraphAlignment.CENTER);
                            b.getFont().setSize(9);
                            b.getFont().setItalic(true);
                            try { b.getFont().setColor(java.awt.Color.DARK_GRAY); } catch (Exception ignore) {}
                            b.write(message);
                        } else if (bs != null) {
                            com.aspose.words.Document docForBuilder = (com.aspose.words.Document) bookmark.getBookmarkStart().getDocument();
                            com.aspose.words.DocumentBuilder b = new com.aspose.words.DocumentBuilder(docForBuilder);
                            b.moveTo(bs);
                            b.getParagraphFormat().setAlignment(com.aspose.words.ParagraphAlignment.CENTER);
                            b.getFont().setSize(9);
                            b.getFont().setItalic(true);
                            try { b.getFont().setColor(java.awt.Color.DARK_GRAY); } catch (Exception ignore) {}
                            b.write(message);
                        }
                        try { if (bs != null) bs.remove(); } catch (Exception ignore) {}
                        try { if (be != null) be.remove(); } catch (Exception ignore) {}
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                };

        // Replace QR and signature bookmarks
        com.aspose.words.Bookmark qrBookmark = document.getRange().getBookmarks().get("QRCodeImage");
        replaceBookmarkWithText.accept(qrBookmark, "(QR code omitted in preview)");

        com.aspose.words.Bookmark sig1 = document.getRange().getBookmarks().get("inspectedByImg");
        replaceBookmarkWithText.accept(sig1, "(Signature omitted in preview)");

        com.aspose.words.Bookmark sig2 = document.getRange().getBookmarks().get("reviewedByImg");
        replaceBookmarkWithText.accept(sig2, "(Signature omitted in preview)");

        // Insert prominent banner at top
        try {
            com.aspose.words.DocumentBuilder headerBuilder = new com.aspose.words.DocumentBuilder(document);
            headerBuilder.moveToDocumentStart();

            headerBuilder.getParagraphFormat().setAlignment(com.aspose.words.ParagraphAlignment.CENTER);
            headerBuilder.getParagraphFormat().setSpaceAfter(6);
            headerBuilder.getFont().setSize(14);
            headerBuilder.getFont().setBold(true);

            headerBuilder.writeln();

            headerBuilder.getFont().setSize(10);
            headerBuilder.getFont().setBold(false);
            headerBuilder.getFont().setItalic(true);
            try { headerBuilder.getFont().setColor(java.awt.Color.DARK_GRAY); } catch (Exception ignore) {}
            headerBuilder.write("There is no QR code for previewed reports. Signatures are not included in preview.");
            headerBuilder.writeln();

            headerBuilder.getParagraphFormat().setSpaceAfter(8);
            headerBuilder.getFont().clearFormatting();
            headerBuilder.writeln();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // map items
        if (eForm.getEquipmentInspectionFormItems() != null) {
            for (Object obj : eForm.getEquipmentInspectionFormItems()) {
                if (obj instanceof EquipmentInspectionFormItem) {
                    EquipmentInspectionFormItem item = (EquipmentInspectionFormItem) obj;
                    Object value = item.getItemValue();
                    if (value instanceof java.util.Date) {
                        data.put(item.getAliasName(), formatDate((java.util.Date) value));
                    } else {
                        data.put(item.getAliasName(), value != null ? value.toString() : "");
                    }
                }
            }
        }

        // Also merge any unsaved UI field values from `columnContents` so preview
        // reflects the current form state even when items are not yet persisted.
        try {
            if (this.columnContents != null) {
                for (ColumnContent cc : this.columnContents) {
                    if (cc == null) continue;
                    String alias = cc.getAliasName();
                    if (alias == null) continue;
                    Object v = cc.getContentValue();
                    if (v instanceof java.util.Date) {
                        data.put(alias, formatDate((java.util.Date) v));
                    } else {
                        data.put(alias, v != null ? v.toString() : "");
                    }
                }
            }
        } catch (Exception ignore) {
            // non-fatal: preview will still work with persisted items
        }

        // build report
        ReportingEngine engine = new ReportingEngine();
        engine.buildReport(document, data, "data");

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        document.save(out, com.aspose.words.SaveFormat.PDF);
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


    /**
     * Prepare a preview PDF from the current in-memory `equipmentInspectionForm`.
     * Stores the generated PDF bytes in the HTTP session under a token and
     * returns the token to the client via PrimeFaces callback param `previewToken`.
     */
    public void preparePreviewPdf() {
        try {
            if (this.equipmentInspectionForm == null) {
                UtilityHelper.addErrorMessage("No form available for preview");
                return;
            }

            FormTemplate tpl = this.formTemplate;
            if (tpl == null && this.equipmentInspectionForm.getEquipmentCategory() != null) {
                tpl = formTemplateService.getBy(this.equipmentInspectionForm.getEquipmentCategory().getCode());
            }
            if (tpl == null || tpl.getPrintedDoc() == null) {
                UtilityHelper.addErrorMessage("Template not available for preview");
                return;
            }

            byte[] pdfBytes = generateEquipmentPdfBytes(this.equipmentInspectionForm, tpl);
            if (pdfBytes == null || pdfBytes.length == 0) {
                UtilityHelper.addErrorMessage("Failed to generate preview PDF");
                return;
            }

            String token = UUID.randomUUID().toString();
            String sessionKey = "previewPdf_" + token;

            FacesContext fc = FacesContext.getCurrentInstance();
            if (fc != null) {
                // نخزن في session map (خيار مناسب للتجارب)
                fc.getExternalContext().getSessionMap().put(sessionKey, pdfBytes);

                // نرجع الـ token للعميل عبر callback param
                try {
                    PrimeFaces.current().ajax().addCallbackParam("previewToken", token);
                } catch (Exception ignore) { /* لا نفشل إن لم يستطع إرسال callback */ }
            } else {
                UtilityHelper.addErrorMessage("FacesContext not available");
            }

        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Error preparing preview: " + e.getMessage());
        }
    }

    /**
     * Load the latest EquipmentInspectionForm previously filled by the current user
     * and copy matching fields (primitive fields and matching alias-based items)
     * into the current in-memory `equipmentInspectionForm` and `columnContents`.
     * Visible only to inspectors in the UI.
     */
    public void loadLastUserForm() {
        try {
            // Ensure a user is logged in (feature is visible only to inspectors),
            // but search for the last form across ALL inspectors for the same
            // equipment category — so forms are shared between inspectors.
            if (loginBean == null || loginBean.getUser() == null) {
                UtilityHelper.addErrorMessage("User not available");
                return;
            }

            java.util.Map<String, Object> criteria = new java.util.HashMap<>();

            // Require an equipment category to scope the search. Only load previous
            // forms that belong to the same equipment category; do NOT filter by
            // the inspector user id so the last form by any inspector is used.
            String catCode = null;
            if (this.equipmentInspectionForm != null && this.equipmentInspectionForm.getEquipmentCategory() != null
                    && this.equipmentInspectionForm.getEquipmentCategory().getCode() != null) {
                catCode = this.equipmentInspectionForm.getEquipmentCategory().getCode();
            } else if (this.equipmentCategory != null && this.equipmentCategory.getCode() != null) {
                catCode = this.equipmentCategory.getCode();
            } else if (this.equipmentCatCode != null && !this.equipmentCatCode.trim().isEmpty()) {
                catCode = this.equipmentCatCode;
            }
            if (catCode == null) {
                UtilityHelper.addInfoMessage("No equipment category selected — cannot load previous form.");
                return;
            }
            criteria.put("equipmentCategory.code", catCode);

            // fetch latest by id descending (most recent)
            java.util.List<com.smat.ins.model.entity.EquipmentInspectionForm> list = null;
            try {
                list = equipmentInspectionFormService.findByCriteria(criteria, 0, 1, "id", false);
            } catch (Exception ex) {
                // fallback to simple criteria without sort
                try { list = equipmentInspectionFormService.findByCriteria(criteria, 0, 1); } catch (Exception ign) { list = null; }
            }

            if (list == null || list.isEmpty()) {
                UtilityHelper.addInfoMessage("No previous filled form found for the same equipment category.");
                return;
            }

            com.smat.ins.model.entity.EquipmentInspectionForm source = list.get(0);
            if (source == null) {
                UtilityHelper.addInfoMessage("No previous filled form found for the same equipment category.");
                return;
            }

            // ensure current form exists
            if (this.equipmentInspectionForm == null) this.equipmentInspectionForm = new EquipmentInspectionForm();

            // copy simple fields only when source has value (overwrite by design)
            // NOTE: we intentionally do NOT copy sticker/reportNo (see below)
            try {
                // copy name/address textual field
                if (source.getNameAndAddressOfEmployer() != null && !source.getNameAndAddressOfEmployer().trim().isEmpty()) {
                    this.equipmentInspectionForm.setNameAndAddressOfEmployer(source.getNameAndAddressOfEmployer());
                }

                // If the source references a Company entity, try to load the canonical Company
                // via service so the selectOneMenu can bind correctly. Then overwrite the
                // Company's address with the textual nameAndAddressOfEmployer so the
                // textarea shows the expected content (user requested behavior).
                if (source.getCompany() != null) {
                    try {
                        Integer srcCid = source.getCompany().getId();
                        com.smat.ins.model.entity.Company resolved = null;
                        if (srcCid != null) {
                            try { resolved = companyService.findById(srcCid); } catch (Exception ign) {}
                        }
                        if (resolved == null) {
                            // fallback: create lightweight company to hold address
                            resolved = new com.smat.ins.model.entity.Company();
                            try { resolved.setName(source.getCompany().getName()); } catch (Exception ignoreName) {}
                        }
                        // overwrite address from the textual NameAndAddress field when available
                        try {
                            String srcNameAddr = source.getNameAndAddressOfEmployer();
                            if (srcNameAddr != null && !srcNameAddr.trim().isEmpty()) {
                                resolved.setAddress(srcNameAddr);
                            } else {
                                // if no textual field present, preserve resolved.address if any
                                try { if (resolved.getAddress() == null || resolved.getAddress().trim().isEmpty()) resolved.setAddress(source.getCompany().getAddress()); } catch (Exception ign) {}
                            }
                        } catch (Exception ignoreAddr) {}

                        this.equipmentInspectionForm.setCompany(resolved);
                    } catch (Exception ignoreSetCompany) {}
                }
            } catch (Exception ignore) {}
            try { if (source.getDateOfThoroughExamination() != null) this.equipmentInspectionForm.setDateOfThoroughExamination(source.getDateOfThoroughExamination()); } catch (Exception ignore) {}
            try { if (source.getNextExaminationDate() != null) this.equipmentInspectionForm.setNextExaminationDate(source.getNextExaminationDate()); } catch (Exception ignore) {}
            try { if (source.getPreviousExaminationDate() != null) this.equipmentInspectionForm.setPreviousExaminationDate(source.getPreviousExaminationDate()); } catch (Exception ignore) {}
            try { if (source.getExaminationType() != null) { this.examinationType = source.getExaminationType(); this.equipmentInspectionForm.setExaminationType(source.getExaminationType()); } } catch (Exception ignore) {}
            try { if (source.getEquipmentType() != null) { this.equipmentType = source.getEquipmentType(); this.equipmentInspectionForm.setEquipmentType(source.getEquipmentType()); } } catch (Exception ignore) {}
            // Do NOT copy sticker or report number from previous form (user requested)
            // sticker/reportNo intentionally skipped to avoid reusing identifiers
            try { if (source.getTimeSheetNo() != null) this.equipmentInspectionForm.setTimeSheetNo(source.getTimeSheetNo()); } catch (Exception ignore) {}
            try { if (source.getJobNo() != null) this.equipmentInspectionForm.setJobNo(source.getJobNo()); } catch (Exception ignore) {}

            // Note: intentionally do NOT copy nameAndAddressOfEmployer into the current form
            // (user requested this field should not be restored).

            // Merge dynamic items: map source items by aliasName AND by generalEquipmentItem.itemCode
            java.util.Map<String, String> itemsByAlias = new java.util.HashMap<>();
            java.util.Map<String, String> itemsByItemCode = new java.util.HashMap<>();
            try {
                if (source.getEquipmentInspectionFormItems() != null) {
                    for (Object o : source.getEquipmentInspectionFormItems()) {
                        if (o instanceof com.smat.ins.model.entity.EquipmentInspectionFormItem) {
                            com.smat.ins.model.entity.EquipmentInspectionFormItem it = (com.smat.ins.model.entity.EquipmentInspectionFormItem) o;
                            try {
                                if (it.getAliasName() != null) itemsByAlias.put(it.getAliasName().toLowerCase(), it.getItemValue());
                            } catch (Exception ignoreAlias) {}
                            try {
                                if (it.getGeneralEquipmentItem() != null && it.getGeneralEquipmentItem().getItemCode() != null) {
                                    String code = it.getGeneralEquipmentItem().getItemCode().toLowerCase();
                                    if (!itemsByItemCode.containsKey(code)) itemsByItemCode.put(code, it.getItemValue());
                                }
                            } catch (Exception ignoreCode) {}
                        }
                    }
                }
            } catch (Exception ignore) {}

            // Apply values to current columnContents by alias match first, then by itemCode match
            try {
                if (this.columnContents != null && (!itemsByAlias.isEmpty() || !itemsByItemCode.isEmpty())) {
                    for (com.smat.ins.model.entity.ColumnContent cc : this.columnContents) {
                        try {
                            if (cc == null) continue;
                            String alias = cc.getAliasName();
                            String val = null;
                            if (alias != null) val = itemsByAlias.get(alias.toLowerCase());
                            if (val == null) {
                                try {
                                    if (cc.getGeneralEquipmentItem() != null && cc.getGeneralEquipmentItem().getItemCode() != null) {
                                        String code = cc.getGeneralEquipmentItem().getItemCode().toLowerCase();
                                        val = itemsByItemCode.get(code);
                                    }
                                } catch (Exception ignoreCode) {}
                            }
                            if (val != null) cc.setContentValue(val);
                        } catch (Exception ignore) {}
                    }
                }
            } catch (Exception ignore) {}

            // If the source had stickerNo 'N\\A', ensure we preserve the sentinel behavior
            try { normalizeNaSentinelSticker(); } catch (Exception ignore) {}

            UtilityHelper.addInfoMessage("Loaded last filled form and populated matching fields.");
            try { PrimeFaces.current().ajax().update("@form"); } catch (Exception ignore) {}

        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Error loading last filled form: " + e.getMessage());
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

    // Cache: whether current user has a previous filled form (used to render top-center button)
    private Boolean previousFormExists = null;
    // Cache for recent forms in same equipment category
    private java.util.List<EquipmentInspectionForm> lastFiveFormsForCategory = null;

    /**
     * Return true when the current logged-in user has at least one previously filled
     * EquipmentInspectionForm. The result is cached for the view to avoid repeated DB calls.
     */
    public boolean isPreviousFormExists() {
        try {
            if (previousFormExists != null) return previousFormExists.booleanValue();
            previousFormExists = Boolean.FALSE;
            if (loginBean == null || loginBean.getUser() == null) return previousFormExists;

            // Determine equipment category; if not available, return false
            String catCode = null;
            if (this.equipmentInspectionForm != null && this.equipmentInspectionForm.getEquipmentCategory() != null
                    && this.equipmentInspectionForm.getEquipmentCategory().getCode() != null) {
                catCode = this.equipmentInspectionForm.getEquipmentCategory().getCode();
            } else if (this.equipmentCategory != null && this.equipmentCategory.getCode() != null) {
                catCode = this.equipmentCategory.getCode();
            } else if (this.equipmentCatCode != null && !this.equipmentCatCode.trim().isEmpty()) {
                catCode = this.equipmentCatCode;
            }
            if (catCode == null) return previousFormExists;

            java.util.Map<String, Object> criteria = new java.util.HashMap<>();
            // Only check for any form in the same equipment category (shared across inspectors)
            criteria.put("equipmentCategory.code", catCode);
            java.util.List<com.smat.ins.model.entity.EquipmentInspectionForm> list = null;
            try {
                list = equipmentInspectionFormService.findByCriteria(criteria, 0, 1);
            } catch (Exception ex) {
                try { list = equipmentInspectionFormService.findByCriteria(criteria, 0, 1, "id", false); } catch (Exception ign) { list = null; }
            }
            if (list != null && !list.isEmpty()) previousFormExists = Boolean.TRUE;
        } catch (Exception e) {
            previousFormExists = Boolean.FALSE;
        }
        return previousFormExists.booleanValue();
    }

    /**
     * Return up to 5 most recent EquipmentInspectionForm entries for the current
     * equipment category. The result is cached for the view and invalidated when
     * the category or current form changes.
     */
    public java.util.List<EquipmentInspectionForm> getLastFiveFormsForCategory() {
        try {
            if (lastFiveFormsForCategory != null) return lastFiveFormsForCategory;
            lastFiveFormsForCategory = new java.util.ArrayList<>();

            String catCode = null;
            if (this.equipmentInspectionForm != null && this.equipmentInspectionForm.getEquipmentCategory() != null
                    && this.equipmentInspectionForm.getEquipmentCategory().getCode() != null) {
                catCode = this.equipmentInspectionForm.getEquipmentCategory().getCode();
            } else if (this.equipmentCategory != null && this.equipmentCategory.getCode() != null) {
                catCode = this.equipmentCategory.getCode();
            } else if (this.equipmentCatCode != null && !this.equipmentCatCode.trim().isEmpty()) {
                catCode = this.equipmentCatCode;
            }
            if (catCode == null) return lastFiveFormsForCategory;

            java.util.Map<String, Object> criteria = new java.util.HashMap<>();
            criteria.put("equipmentCategory.code", catCode);
            try {
                lastFiveFormsForCategory = equipmentInspectionFormService.findByCriteria(criteria, 0, 5, "id", false);
            } catch (Exception ex) {
                try { lastFiveFormsForCategory = equipmentInspectionFormService.findByCriteria(criteria, 0, 5); } catch (Exception ign) { lastFiveFormsForCategory = new java.util.ArrayList<>(); }
            }

            // Ensure pinned forms (any inspector) are shown first in the list and preserved
            try {
                // Fetch a slightly larger window to allow collecting pinned items
                java.util.List<EquipmentInspectionForm> windowList = null;
                try { windowList = equipmentInspectionFormService.findByCriteria(criteria, 0, 15, "id", false); } catch (Exception ign) { try { windowList = equipmentInspectionFormService.findByCriteria(criteria, 0, 15); } catch (Exception ignore2) { windowList = lastFiveFormsForCategory; } }

                if (windowList == null) windowList = lastFiveFormsForCategory;

                java.util.List<EquipmentInspectionForm> pinned = new java.util.ArrayList<>();
                java.util.List<EquipmentInspectionForm> notPinned = new java.util.ArrayList<>();

                for (EquipmentInspectionForm f : windowList) {
                    try {
                        if (f != null && f.getPinnedBy() != null) {
                            // include pinned ones (unique)
                            boolean found = false;
                            for (EquipmentInspectionForm pf : pinned) { if (pf != null && pf.getId() != null && f.getId() != null && pf.getId().equals(f.getId())) { found = true; break; } }
                            if (!found) pinned.add(f);
                        } else {
                            // non-pinned candidates
                            notPinned.add(f);
                        }
                    } catch (Exception ignore) {}
                }

                // Build final result: pinned first, then latest non-pinned, ensure uniqueness and limit to 5
                java.util.List<EquipmentInspectionForm> result = new java.util.ArrayList<>();
                for (EquipmentInspectionForm pf : pinned) { if (pf != null) result.add(pf); }
                for (EquipmentInspectionForm nf : notPinned) { if (nf != null) {
                    // avoid duplicates
                    boolean exists = false;
                    for (EquipmentInspectionForm r : result) { if (r != null && r.getId() != null && nf.getId() != null && r.getId().equals(nf.getId())) { exists = true; break; } }
                    if (!exists) result.add(nf);
                    if (result.size() >= 5) break;
                } }

                // If result empty (fallback), use lastFiveFormsForCategory already loaded
                if (result.isEmpty()) {
                    // keep previous behavior
                } else {
                    // ensure final trimming to exactly up to 5
                    while (result.size() > 5) result.remove(result.size()-1);
                    lastFiveFormsForCategory = result;
                }
            } catch (Exception ignore) {}

            return lastFiveFormsForCategory != null ? lastFiveFormsForCategory : new java.util.ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Return true if the given form is pinned by the currently logged-in inspector.
     */
    public boolean isPinnedByCurrent(EquipmentInspectionForm f) {
        try {
            if (f == null || f.getPinnedBy() == null || loginBean == null || loginBean.getUser() == null) return false;
            return f.getPinnedBy().getId() != null && f.getPinnedBy().getId().equals(loginBean.getUser().getId());
        } catch (Exception e) { return false; }
    }

    /**
     * Toggle pin state for the currently-loaded equipmentInspectionForm.
     * Only the inspector who set the pin can remove it.
     */
    public void togglePinCurrentForm() {
        try {
            if (equipmentInspectionForm == null) { UtilityHelper.addErrorMessage("No form loaded to pin"); return; }
            if (loginBean == null || loginBean.getUser() == null) { UtilityHelper.addErrorMessage("User not available"); return; }
            Long curId = loginBean.getUser().getId();
            if (equipmentInspectionForm.getPinnedBy() == null) {
                equipmentInspectionForm.setPinnedBy(loginBean.getUser());
                try { equipmentInspectionFormService.saveOrUpdate(equipmentInspectionForm); } catch (Exception ignore) { }
                UtilityHelper.addInfoMessage("Form pinned — it will remain in Recent fills (until you unpin)");
            } else {
                try {
                    if (equipmentInspectionForm.getPinnedBy().getId() != null && equipmentInspectionForm.getPinnedBy().getId().equals(curId)) {
                        equipmentInspectionForm.setPinnedBy(null);
                        try { equipmentInspectionFormService.saveOrUpdate(equipmentInspectionForm); } catch (Exception ignore) {}
                        UtilityHelper.addInfoMessage("Pin removed");
                    } else {
                        UtilityHelper.addErrorMessage("Only the inspector who pinned this form can remove the pin");
                    }
                } catch (Exception ignore) { UtilityHelper.addErrorMessage("Unable to toggle pin"); }
            }
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Error toggling pin: " + e.getMessage());
        } finally {
            try { this.lastFiveFormsForCategory = null; } catch (Exception ignore) {}
            try { PrimeFaces.current().ajax().update("form"); } catch (Exception ignore) {}
        }
    }

    /**
     * Toggle pin state for a given form id (used by recent-cards pin action).
     */
    public void togglePinById(Integer formId) {
        try {
            if (formId == null) { UtilityHelper.addErrorMessage("Invalid form"); return; }
            EquipmentInspectionForm f = null;
            try { f = equipmentInspectionFormService.findById(formId.longValue()); } catch (Exception ign) { try { f = equipmentInspectionFormService.getBy(formId); } catch (Exception ignore) { f = null; } }
            if (f == null) { UtilityHelper.addErrorMessage("Form not found"); return; }
            if (loginBean == null || loginBean.getUser() == null) { UtilityHelper.addErrorMessage("User not available"); return; }
            Long curId = loginBean.getUser().getId();
            if (f.getPinnedBy() == null) {
                // Prevent pinning forms that were inspected/created by another inspector
                try {
                    if (f.getSysUserByInspectionBy() != null && f.getSysUserByInspectionBy().getId() != null && !f.getSysUserByInspectionBy().getId().equals(curId)) {
                        UtilityHelper.addErrorMessage("You can only pin forms you inspected");
                        return;
                    }
                } catch (Exception ignore) {}

                f.setPinnedBy(loginBean.getUser());
                try { equipmentInspectionFormService.saveOrUpdate(f); } catch (Exception ignore) {}
                UtilityHelper.addInfoMessage("Form pinned");
            } else {
                try {
                    if (f.getPinnedBy().getId() != null && f.getPinnedBy().getId().equals(curId)) {
                        f.setPinnedBy(null);
                        try { equipmentInspectionFormService.saveOrUpdate(f); } catch (Exception ignore) {}
                        UtilityHelper.addInfoMessage("Pin removed");
                    } else {
                        UtilityHelper.addErrorMessage("Only the inspector who pinned this form can remove the pin");
                    }
                } catch (Exception ignore) { UtilityHelper.addErrorMessage("Unable to toggle pin"); }
            }
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Error toggling pin: " + e.getMessage());
        } finally {
            try { this.lastFiveFormsForCategory = null; } catch (Exception ignore) {}
            try { PrimeFaces.current().ajax().update("form"); } catch (Exception ignore) {}
        }
    }

    /**
     * Load a specific previous form by id (must be from the same equipment category)
     * and apply its values into the current in-memory form (same rules as loadLastUserForm).
     */
    public void loadFormById(Integer formId) {
        try {
            if (formId == null) { UtilityHelper.addErrorMessage("Invalid form selected"); return; }
            EquipmentInspectionForm source = null;
            try { source = equipmentInspectionFormService.findById(formId.longValue()); } catch (Exception ex) { }
            if (source == null) {
                try { source = equipmentInspectionFormService.getBy(formId); } catch (Exception ignore) {}
            }
            if (source == null) { UtilityHelper.addInfoMessage("Selected form not found"); return; }

            // Ensure category matches current category to be safe
            String curCat = null;
            if (this.equipmentInspectionForm != null && this.equipmentInspectionForm.getEquipmentCategory() != null) curCat = this.equipmentInspectionForm.getEquipmentCategory().getCode();
            if (curCat == null && this.equipmentCategory != null) curCat = this.equipmentCategory.getCode();
            if (curCat == null && this.equipmentCatCode != null) curCat = this.equipmentCatCode;
            String srcCat = null;
            try { if (source.getEquipmentCategory() != null) srcCat = source.getEquipmentCategory().getCode(); } catch (Exception ignore) {}
            if (curCat != null && srcCat != null && !curCat.equals(srcCat)) { UtilityHelper.addInfoMessage("Selected form does not belong to the same equipment category."); return; }

            // Apply same copy logic as loadLastUserForm (without user-scoped search)
            if (this.equipmentInspectionForm == null) this.equipmentInspectionForm = new EquipmentInspectionForm();

            try {
                // assign company entity but DO NOT overwrite company.address from free-text
                if (source.getCompany() != null) {
                    try {
                        Integer srcCid = source.getCompany().getId();
                        com.smat.ins.model.entity.Company resolved = null;
                        if (srcCid != null) {
                            try { resolved = companyService.findById(srcCid); } catch (Exception ign) {}
                        }
                        if (resolved == null) {
                            resolved = new com.smat.ins.model.entity.Company();
                            try { resolved.setName(source.getCompany().getName()); } catch (Exception ignoreName) {}
                        }
                        this.equipmentInspectionForm.setCompany(resolved);
                    } catch (Exception ignoreSetCompany) {}
                }
            } catch (Exception ignore) {}
            try { if (source.getDateOfThoroughExamination() != null) this.equipmentInspectionForm.setDateOfThoroughExamination(source.getDateOfThoroughExamination()); } catch (Exception ignore) {}
            try { if (source.getNextExaminationDate() != null) this.equipmentInspectionForm.setNextExaminationDate(source.getNextExaminationDate()); } catch (Exception ignore) {}
            try { if (source.getPreviousExaminationDate() != null) this.equipmentInspectionForm.setPreviousExaminationDate(source.getPreviousExaminationDate()); } catch (Exception ignore) {}
            try { if (source.getExaminationType() != null) { this.examinationType = source.getExaminationType(); this.equipmentInspectionForm.setExaminationType(source.getExaminationType()); } } catch (Exception ignore) {}
            try { if (source.getEquipmentType() != null) { this.equipmentType = source.getEquipmentType(); this.equipmentInspectionForm.setEquipmentType(source.getEquipmentType()); } } catch (Exception ignore) {}

            // Do NOT copy sticker/reportNo
            try { if (source.getTimeSheetNo() != null) this.equipmentInspectionForm.setTimeSheetNo(source.getTimeSheetNo()); } catch (Exception ignore) {}
            try { if (source.getJobNo() != null) this.equipmentInspectionForm.setJobNo(source.getJobNo()); } catch (Exception ignore) {}

            // Merge dynamic items (alias then itemCode)
            java.util.Map<String, String> itemsByAlias = new java.util.HashMap<>();
            java.util.Map<String, String> itemsByItemCode = new java.util.HashMap<>();
            try {
                if (source.getEquipmentInspectionFormItems() != null) {
                    for (Object o : source.getEquipmentInspectionFormItems()) {
                        if (o instanceof com.smat.ins.model.entity.EquipmentInspectionFormItem) {
                            com.smat.ins.model.entity.EquipmentInspectionFormItem it = (com.smat.ins.model.entity.EquipmentInspectionFormItem) o;
                            try { if (it.getAliasName() != null) itemsByAlias.put(it.getAliasName().toLowerCase(), it.getItemValue()); } catch (Exception ignoreAlias) {}
                            try { if (it.getGeneralEquipmentItem() != null && it.getGeneralEquipmentItem().getItemCode() != null) { String code = it.getGeneralEquipmentItem().getItemCode().toLowerCase(); if (!itemsByItemCode.containsKey(code)) itemsByItemCode.put(code, it.getItemValue()); } } catch (Exception ignoreCode) {}
                        }
                    }
                }
            } catch (Exception ignore) {}

            try {
                if (this.columnContents != null && (!itemsByAlias.isEmpty() || !itemsByItemCode.isEmpty())) {
                    for (com.smat.ins.model.entity.ColumnContent cc : this.columnContents) {
                        try {
                            if (cc == null) continue;
                            String alias = cc.getAliasName();
                            String val = null;
                            if (alias != null) val = itemsByAlias.get(alias.toLowerCase());
                            if (val == null) {
                                try {
                                    if (cc.getGeneralEquipmentItem() != null && cc.getGeneralEquipmentItem().getItemCode() != null) {
                                        String code = cc.getGeneralEquipmentItem().getItemCode().toLowerCase();
                                        val = itemsByItemCode.get(code);
                                    }
                                } catch (Exception ignoreCode) {}
                            }
                            if (val != null) cc.setContentValue(val);
                        } catch (Exception ignore) {}
                    }
                }
            } catch (Exception ignore) {}

            try { normalizeNaSentinelSticker(); } catch (Exception ignore) {}

            UtilityHelper.addInfoMessage("Loaded selected form and populated matching fields.");
            try { PrimeFaces.current().ajax().update("@form"); } catch (Exception ignore) {}
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Error loading selected form: " + e.getMessage());
        }
    }
}

