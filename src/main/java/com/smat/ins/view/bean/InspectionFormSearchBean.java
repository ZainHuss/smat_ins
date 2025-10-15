package com.smat.ins.view.bean;

import com.smat.ins.model.entity.*;
import com.smat.ins.model.service.*;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class InspectionFormSearchBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(InspectionFormSearchBean.class);

    // Search criteria
    private EquipmentCategory selectedCategory;
    private String reportNo;
    private String timeSheetNo;
    private String jobNo;
    private String stickerNo;
    private Company selectedCompany;
    private Step selectedStep;
    private List<DynamicField> dynamicFields = new ArrayList<>();
    private LazyDataModel<EquipmentInspectionForm> lazyDataModel;

    // Lists for dropdowns
    private List<EquipmentCategory> categories;
    private List<Company> companies;
    private List<Step> availableSteps;

    // Services
    private EquipmentCategoryService equipmentCategoryService;
    private CompanyService companyService;
    private FormTemplateService formTemplateService;
    private FormRowService formRowService;
    private FormColumnService formColumnService;
    private ColumnContentService columnContentService;
    private EquipmentInspectionFormService equipmentInspectionFormService;
    private EquipmentInspectionFormItemService equipmentInspectionFormItemService;
    private InspectionFormWorkflowService inspectionFormWorkflowService;
    private ChecklistDetailDataSourceService checklistDetailDataSourceService;
    private LocalizationService localizationService;
    private SysUserService sysUserService;
    private StepService stepService;

    @Inject
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        try {
            // Initialize services using BeanUtility
            equipmentCategoryService = (EquipmentCategoryService) BeanUtility.getBean("equipmentCategoryService");
            companyService = (CompanyService) BeanUtility.getBean("companyService");
            formTemplateService = (FormTemplateService) BeanUtility.getBean("formTemplateService");
            formRowService = (FormRowService) BeanUtility.getBean("formRowService");
            formColumnService = (FormColumnService) BeanUtility.getBean("formColumnService");
            columnContentService = (ColumnContentService) BeanUtility.getBean("columnContentService");
            equipmentInspectionFormItemService = (EquipmentInspectionFormItemService) BeanUtility.getBean("equipmentInspectionFormItemService");
            equipmentInspectionFormService = (EquipmentInspectionFormService) BeanUtility.getBean("equipmentInspectionFormService");
            inspectionFormWorkflowService = (InspectionFormWorkflowService) BeanUtility.getBean("inspectionFormWorkflowService");
            checklistDetailDataSourceService = (ChecklistDetailDataSourceService) BeanUtility.getBean("checklistDetailDataSourceService");
            localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
            sysUserService = (SysUserService) BeanUtility.getBean("sysUserService");
            stepService = (StepService) BeanUtility.getBean("stepService");

            // Initialize lists
            categories = equipmentCategoryService.findAll();
            companies = companyService.findAll();
            availableSteps = stepService.findAll();

            // Initialize lazy data model
            initializeLazyDataModel();
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
            LOGGER.error("Error initializing InspectionFormSearchBean", e);
        }
    }

    private void initializeLazyDataModel() {
        lazyDataModel = new LazyDataModel<EquipmentInspectionForm>() {
            @Override
            public List<EquipmentInspectionForm> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                try {
                    // Build criteria from search form inputs
                    Map<String, Object> criteria = new HashMap<>();
                    if (reportNo != null && !reportNo.trim().isEmpty()) {
                        criteria.put("reportNo", "%" + reportNo.trim() + "%");
                    }
                    if (timeSheetNo != null && !timeSheetNo.trim().isEmpty()) {
                        criteria.put("timeSheetNo", "%" + timeSheetNo.trim() + "%");
                    }
                    if (jobNo != null && !jobNo.trim().isEmpty()) {
                        criteria.put("jobNo", "%" + jobNo.trim() + "%");
                    }
                    if (stickerNo != null && !stickerNo.trim().isEmpty()) {
                        criteria.put("stickerNo", "%" + stickerNo.trim() + "%");
                    }
                    if (selectedCompany != null) {
                        criteria.put("company.id", selectedCompany.getId());
                    }
                    if (selectedCategory != null) {
                        criteria.put("equipmentCategory.id", selectedCategory.getId());
                    }
                    if (selectedStep != null) {
                        criteria.put("currentWorkflowStep.step.id", selectedStep.getId());
                    }

                    // Handle sorting
                    String sortField = null;
                    boolean ascending = true;
                    if (sortBy != null && !sortBy.isEmpty()) {
                        SortMeta sortMeta = sortBy.values().iterator().next();
                        sortField = sortMeta.getField();
                        ascending = sortMeta.getOrder().isAscending();
                        if (sortBy.size() > 1) {
                            LOGGER.warn("Multiple sort fields provided, using only the first: {}", sortField);
                        }
                    }

                    // Handle table filters
                    if (filterBy != null) {
                        for (FilterMeta filter : filterBy.values()) {
                            if (filter.getFilterValue() != null) {
                                String field = filter.getField();
                                Object value = filter.getFilterValue();
                                if (value instanceof String && !((String) value).isEmpty()) {
                                    criteria.put(field, "%" + value.toString().toLowerCase() + "%");
                                } else if (value != null) {
                                    criteria.put(field, value);
                                }
                            }
                        }
                    }

                    // Fetch forms
                    List<EquipmentInspectionForm> forms = equipmentInspectionFormService.findByCriteria(criteria, first, pageSize, sortField, ascending);

                    // Filter by dynamic fields
                    if (!dynamicFields.isEmpty()) {
                        List<DynamicField> activeFields = dynamicFields.stream()
                            .filter(field -> field.getValue() != null && !String.valueOf(field.getValue()).trim().isEmpty())
                            .collect(Collectors.toList());
                        if (!activeFields.isEmpty()) {
                            Map<String, Object> itemCriteria = new HashMap<>();
                            for (DynamicField field : activeFields) {
                                itemCriteria.put("aliasName", field.getAliasName());
                                itemCriteria.put("itemValue", field.getType().equals("03") ?
                                    String.valueOf(field.getValue()) :
                                    "%" + String.valueOf(field.getValue()).toLowerCase() + "%");
                            }
                            List<EquipmentInspectionFormItem> matchingItems = equipmentInspectionFormItemService.findByCriteria(itemCriteria, 0, Integer.MAX_VALUE, null, true);
                            Set<Long> matchingFormIds = matchingItems.stream()
                                .map(item -> item.getEquipmentInspectionForm().getId())
                                .collect(Collectors.toSet());
                            forms = forms.stream()
                                .filter(form -> matchingFormIds.contains(form.getId()))
                                .collect(Collectors.toList());
                        }
                    }

                    // Set row count
                    setRowCount(countForms(criteria).intValue());
                    return forms;
                } catch (Exception e) {
                    UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("searchFailed"));
                    LOGGER.error("Error loading data for LazyDataModel", e);
                    return Collections.emptyList();
                }
            }

            @Override
            public String getRowKey(EquipmentInspectionForm form) {
                return String.valueOf(form.getId());
            }

            @Override
            public EquipmentInspectionForm getRowData(String rowKey) {
                try {
                    return equipmentInspectionFormService.findById(Long.parseLong(rowKey));
                } catch (Exception e) {
                    LOGGER.error("Error getting row data", e);
                    return null;
                }
            }

            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                return 0;
            }
        };
    }

    public void loadTemplateFields() {
        dynamicFields.clear();
        if (selectedCategory != null) {
            try {
                FormTemplate template = formTemplateService.getBy(selectedCategory.getCode());
                if (template != null) {
                    List<FormRow> rows = formRowService.getBy(template.getId());
                    for (FormRow row : rows) {
                        List<FormColumn> columns = formColumnService.getBy(row.getId());
                        for (FormColumn column : columns) {
                            List<ColumnContent> contents = columnContentService.getBy(column.getId());
                            for (ColumnContent content : contents) {
                                GeneralEquipmentItem item = content.getGeneralEquipmentItem();
                                DynamicField field = new DynamicField();
                                field.setLabel(item.getItemText());
                                field.setType(item.getItemType().getCode());
                                field.setAliasName(content.getAliasName());
                                if ("04".equals(field.getType())) {
                                    field.setChecklistCode(item.getChecklistDataSource().getCode());
                                }
                                dynamicFields.add(field);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
                LOGGER.error("Error loading template fields", e);
            }
        }
    }

    public List<ChecklistDetailDataSource> getChecklistOptions(String checklistCode) {
        try {
            return checklistDetailDataSourceService.getByDataSource(checklistCode);
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
            LOGGER.error("Error fetching checklist options for code: {}", checklistCode, e);
            return Collections.emptyList();
        }
    }

    public void search() {
        try {
            if (!loginBean.hasSysPermission("010") && !loginBean.hasSysPermission("011")) {
                UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("accessDenied"));
                return;
            }
            lazyDataModel.setRowCount(countForms(new HashMap<>()).intValue());
            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("searchForm:resultsTable");
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("searchFailed"));
            LOGGER.error("Error during search", e);
        }
    }

    public void reset() {
        try {
            selectedCategory = null;
            reportNo = null;
            timeSheetNo = null;
            jobNo = null;
            stickerNo = null;
            selectedCompany = null;
            selectedStep = null;
            dynamicFields.clear();
            lazyDataModel.setRowCount(0);
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("resetFailed"));
            LOGGER.error("Error during reset", e);
        }
    }

    private Long countForms(Map<String, Object> criteria) {
        try {
            if (reportNo != null && !reportNo.trim().isEmpty()) {
                criteria.put("reportNo", "%" + reportNo.trim() + "%");
            }
            if (timeSheetNo != null && !timeSheetNo.trim().isEmpty()) {
                criteria.put("timeSheetNo", "%" + timeSheetNo.trim() + "%");
            }
            if (jobNo != null && !jobNo.trim().isEmpty()) {
                criteria.put("jobNo", "%" + jobNo.trim() + "%");
            }
            if (stickerNo != null && !stickerNo.trim().isEmpty()) {
                criteria.put("stickerNo", "%" + stickerNo.trim() + "%");
            }
            if (selectedCompany != null) {
                criteria.put("company.id", selectedCompany.getId());
            }
            if (selectedCategory != null) {
                criteria.put("equipmentCategory.id", selectedCategory.getId());
            }
            if (selectedStep != null) {
                criteria.put("currentWorkflowStep.step.id", selectedStep.getId());
            }
            return equipmentInspectionFormService.countByCriteria(criteria);
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("searchFailed"));
            LOGGER.error("Error counting forms", e);
            return 0L;
        }
    }

    public String viewForm(EquipmentInspectionForm form) {
        try {
            UtilityHelper.putSessionAttr("taskId", getTaskId(form));
            UtilityHelper.putSessionAttr("eqct", UtilityHelper.cipher(form.getEquipmentCategory().getCode()));
            UtilityHelper.putSessionAttr("p", UtilityHelper.cipher("readOnly"));
            UtilityHelper.putSessionAttr("m", UtilityHelper.cipher("changeStep"));
            return "pretty:inspection/form";
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("navigationFailed"));
            LOGGER.error("Error navigating to view form", e);
            return "";
        }
    }

    public String editForm(EquipmentInspectionForm form) {
        try {
            UtilityHelper.putSessionAttr("taskId", getTaskId(form));
            UtilityHelper.putSessionAttr("eqct", UtilityHelper.cipher(form.getEquipmentCategory().getCode()));
            UtilityHelper.putSessionAttr("p", UtilityHelper.cipher("editable"));
            UtilityHelper.putSessionAttr("m", UtilityHelper.cipher("update"));
            return "pretty:inspection/form";
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("navigationFailed"));
            LOGGER.error("Error navigating to edit form", e);
            return "";
        }
    }

    public boolean canEdit(EquipmentInspectionForm form) {
        try {
            return loginBean.hasSysPermission("010") && getWorkflowStep(form).equals("01");
        } catch (Exception e) {
            LOGGER.error("Error checking edit permission", e);
            return false;
        }
    }

    public String getWorkflowStepLabel(EquipmentInspectionForm form) {
        try {
            String step = getWorkflowStep(form);
            switch (step) {
                case "01": return localizationService.getInfoMessage().getString("inspection");
                case "02": return localizationService.getInfoMessage().getString("review");
                case "03": return localizationService.getInfoMessage().getString("final");
                default: return localizationService.getInfoMessage().getString("unknown");
            }
        } catch (Exception e) {
            LOGGER.error("Error getting workflow step label", e);
            return localizationService.getInfoMessage().getString("unknown");
        }
    }

    public String getWorkflowStepStatusClass(EquipmentInspectionForm form) {
        try {
            String step = getWorkflowStep(form);
            switch (step) {
                case "01": return "status-pending";
                case "02": return "status-approved";
                case "03": return "status-rejected";
                default: return "";
            }
        } catch (Exception e) {
            LOGGER.error("Error getting workflow step status class", e);
            return "";
        }
    }

    private String getWorkflowStep(EquipmentInspectionForm form) {
        try {
            InspectionFormWorkflow workflow = inspectionFormWorkflowService.getCurrentInspectionFormWorkFlow(form.getId());
            return workflow != null ? workflow.getWorkflowDefinition().getStep().getCode() : "";
        } catch (Exception e) {
            LOGGER.error("Error getting workflow step", e);
            return "";
        }
    }

    private Integer getTaskId(EquipmentInspectionForm form) {
        try {
            InspectionFormWorkflow workflow = inspectionFormWorkflowService.getCurrentInspectionFormWorkFlow(form.getId());
            return workflow != null && workflow.getTask() != null ? workflow.getTask().getId() : null;
        } catch (Exception e) {
            LOGGER.error("Error getting task ID", e);
            return null;
        }
    }

    // Getters and setters
    public EquipmentCategory getSelectedCategory() { return selectedCategory; }
    public void setSelectedCategory(EquipmentCategory selectedCategory) { this.selectedCategory = selectedCategory; }
    public String getReportNo() { return reportNo; }
    public void setReportNo(String reportNo) { this.reportNo = reportNo; }
    public String getTimeSheetNo() { return timeSheetNo; }
    public void setTimeSheetNo(String timeSheetNo) { this.timeSheetNo = timeSheetNo; }
    public String getJobNo() { return jobNo; }
    public void setJobNo(String jobNo) { this.jobNo = jobNo; }
    public String getStickerNo() { return stickerNo; }
    public void setStickerNo(String stickerNo) { this.stickerNo = stickerNo; }
    public Company getSelectedCompany() { return selectedCompany; }
    public void setSelectedCompany(Company selectedCompany) { this.selectedCompany = selectedCompany; }
    public Step getSelectedStep() { return selectedStep; }
    public void setSelectedStep(Step selectedStep) { this.selectedStep = selectedStep; }
    public List<DynamicField> getDynamicFields() { return dynamicFields; }
    public LazyDataModel<EquipmentInspectionForm> getLazyDataModel() { return lazyDataModel; }
    public List<EquipmentCategory> getCategories() { return categories; }
    public List<Company> getCompanies() { return companies; }
    public List<Step> getAvailableSteps() { return availableSteps; }

    // Inner class for dynamic fields
    public static class DynamicField {
        private String label;
        private String type;
        private String aliasName;
        private Object value;
        private String checklistCode;

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getAliasName() { return aliasName; }
        public void setAliasName(String aliasName) { this.aliasName = aliasName; }
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        public String getChecklistCode() { return checklistCode; }
        public void setChecklistCode(String checklistCode) { this.checklistCode = checklistCode; }
    }
}