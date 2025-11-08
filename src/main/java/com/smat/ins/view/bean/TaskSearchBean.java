package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.smat.ins.model.entity.Company;
import com.smat.ins.model.entity.EmpCertification;
import com.smat.ins.model.entity.EquipmentCategory;
import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.InspectionFormWorkflow;
import com.smat.ins.model.entity.ServiceType;
import com.smat.ins.model.entity.Task;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.service.CompanyService;
import com.smat.ins.model.service.EmpCertificationService;
import com.smat.ins.model.service.EquipmentCategoryService;
import com.smat.ins.model.service.EquipmentInspectionFormService;
import com.smat.ins.model.service.ServiceTypeService;
import com.smat.ins.model.service.TaskService;
import com.smat.ins.model.service.UserAliasService;
import com.smat.ins.util.BeanUtility;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Named
@ViewScoped
public class TaskSearchBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Search criteria fields
    private String taskDescription;
    private Boolean isCompleted;
    private Boolean isDone;
    private Date fromDate;
    private Date toDate;
    private UserAlias assignee;

    // New search criteria fields
    private Company company;
    private EquipmentCategory equipmentCategory;
    private ServiceType serviceType;
    private UserAlias assigner;
    private Date completedFromDate;
    private Date completedToDate;
    private Date doneFromDate;
    private Date doneToDate;
    private String taskStatus; // Combined status filter
    private String workOrderFilter; // search by work order / correspondence id or jobNo

    // Results
    private List<Task> searchResults = new ArrayList<>();
    // cached grouped results by work order (computed on demand)
    private Map<String, List<Task>> groupedResultsByWorkOrder;
    private List<UserAlias> userAliasList = new ArrayList<>();
    private List<Company> companyList = new ArrayList<>();
    private List<EquipmentCategory> equipmentCategoryList = new ArrayList<>();
    private List<ServiceType> serviceTypeList = new ArrayList<>();

    // Services
    private TaskService taskService;
    private UserAliasService userAliasService;
    private CompanyService companyService;
    private EquipmentCategoryService equipmentCategoryService;
    private ServiceTypeService serviceTypeService;
    private EquipmentInspectionFormService equipmentInspectionFormService;
    private EmpCertificationService empCertificationService;

    @Inject
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        taskService = (TaskService) BeanUtility.getBean("taskService");
        userAliasService = (UserAliasService) BeanUtility.getBean("userAliasService");
        companyService = (CompanyService) BeanUtility.getBean("companyService");
        equipmentCategoryService = (EquipmentCategoryService) BeanUtility.getBean("equipmentCategoryService");
        serviceTypeService = (ServiceTypeService) BeanUtility.getBean("serviceTypeService");
        equipmentInspectionFormService = (EquipmentInspectionFormService) BeanUtility
                .getBean("equipmentInspectionFormService");
        empCertificationService = (EmpCertificationService) BeanUtility.getBean("empCertificationService");
        try {
            userAliasList = userAliasService.findAll();
            companyList = companyService.findAll();
            equipmentCategoryList = equipmentCategoryService.findAll();
            serviceTypeList = serviceTypeService.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchTasks() {
        try {
            // Get all tasks first (you might want to implement more specific search methods
            // in TaskService)
            searchResults = taskService.findAll();

            // Apply filters in memory (or better, implement these filters in your DAO)
            if (taskDescription != null && !taskDescription.isEmpty()) {
                searchResults.removeIf(task -> task.getTaskDescription() == null
                        || !task.getTaskDescription().toLowerCase().contains(taskDescription.toLowerCase()));
            }

            if (isCompleted != null) {
                searchResults.removeIf(task -> !isCompleted.equals(task.getIsCompleted()));
            }

            if (isDone != null) {
                searchResults.removeIf(task -> !isDone.equals(task.getIsDone()));
            }

            if (fromDate != null) {
                searchResults.removeIf(task -> task.getCreatedDate() == null || task.getCreatedDate().before(fromDate));
            }

            if (toDate != null) {
                searchResults.removeIf(task -> task.getCreatedDate() == null || task.getCreatedDate().after(toDate));
            }

            if (assignee != null) {
                searchResults.removeIf(task -> task.getUserAliasByAssignTo() == null
                        || !assignee.getId().equals(task.getUserAliasByAssignTo().getId()));
            }

            // New filters
            if (company != null) {
                searchResults.removeIf(
                        task -> task.getCompany() == null || !company.getId().equals(task.getCompany().getId()));
            }

            if (equipmentCategory != null) {
                searchResults.removeIf(task -> task.getEquipmentCategory() == null
                        || !equipmentCategory.getId().equals(task.getEquipmentCategory().getId()));
            }

            if (serviceType != null) {
                searchResults.removeIf(task -> task.getServiceType() == null
                        || !serviceType.getId().equals(task.getServiceType().getId()));
            }

            if (assigner != null) {
                searchResults.removeIf(task -> task.getUserAliasByAssigner() == null
                        || !assigner.getId().equals(task.getUserAliasByAssigner().getId()));
            }

            if (completedFromDate != null) {
                searchResults.removeIf(
                        task -> task.getCompletedDate() == null || task.getCompletedDate().before(completedFromDate));
            }

            if (completedToDate != null) {
                searchResults.removeIf(
                        task -> task.getCompletedDate() == null || task.getCompletedDate().after(completedToDate));
            }

            if (doneFromDate != null) {
                searchResults.removeIf(task -> task.getDoneDate() == null || task.getDoneDate().before(doneFromDate));
            }

            if (doneToDate != null) {
                searchResults.removeIf(task -> task.getDoneDate() == null || task.getDoneDate().after(doneToDate));
            }

            // Task status filter
            if (taskStatus != null && !taskStatus.isEmpty()) {
                switch (taskStatus) {
                    case "pending":
                        searchResults.removeIf(task -> Boolean.TRUE.equals(task.getIsCompleted())
                                || Boolean.TRUE.equals(task.getIsDone()));
                        break;
                    case "completed":
                        searchResults.removeIf(task -> !Boolean.TRUE.equals(task.getIsCompleted()));
                        break;
                    case "done":
                        searchResults.removeIf(task -> !Boolean.TRUE.equals(task.getIsDone()));
                        break;
                    case "in_progress":
                        searchResults.removeIf(task -> Boolean.TRUE.equals(task.getIsCompleted())
                                || Boolean.TRUE.equals(task.getIsDone()));
                        break;
                }
            }
            // Work order filter (search by correspondence id or jobNo)
            if (workOrderFilter != null && !workOrderFilter.trim().isEmpty()) {
                final String filter = workOrderFilter.trim().toLowerCase();
                searchResults.removeIf(task -> {
                    try {
                        String w = getWorkOrderForTask(task);
                        return w == null || !w.toLowerCase().contains(filter);
                    } catch (Exception e) {
                        return true;
                    }
                });
            }

            // reset grouped cache so UI will recompute
            groupedResultsByWorkOrder = null;

        } catch (Exception e) {
            e.printStackTrace();
            // Handle error
        }
    }

    // الدالة العامة التي تُرجع تسمية الحالة (تُستخدم في الـ XHTML)
    public String getInspectionFormWorkflowStatus(Task task) {
        TaskStatus status = resolveTaskStatus(task);
        return status != null ? status.getLabel() : TaskStatus.PENDING.getLabel();
    }

    private TaskStatus resolveTaskStatus(Task task) {
        if (task == null) return TaskStatus.PENDING;

        try {
            // 1) Finished (طباعة/إغلاق نهائي)
            if (task.getDoneDate() != null) return TaskStatus.FINISHED;

            // 2) Completed (مهام مكتملة لكن ربما لم تُطبع بعد)
            if (task.getCompletedDate() != null) return TaskStatus.COMPLETED;

            // 3) افتح workflows المتعلّقة بالتفتيش أولاً (equipment)
            try {
                if (task.getInspectionFormWorkflows() != null && !task.getInspectionFormWorkflows().isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Set<com.smat.ins.model.entity.InspectionFormWorkflow> flows = task.getInspectionFormWorkflows();
                    for (com.smat.ins.model.entity.InspectionFormWorkflow wf : flows) {
                        if (wf == null || wf.getWorkflowDefinition() == null || wf.getWorkflowDefinition().getStep() == null) continue;
                        String name = wf.getWorkflowDefinition().getStep().getEnglishName();
                        if (name == null) continue;
                        String ln = name.toLowerCase();

                        // Inspect / Submitted => Inspected
                        if (ln.contains("submitted") || ln.contains("inspected")) return TaskStatus.INSPECTED;

                        // Any review-related wording => Inspected (Under review)
                        if (ln.contains("review") || ln.contains("under review") || ln.contains("in review")) {
                            return TaskStatus.INSPECTED_UNDER_REVIEW;
                        }

                        // Approved/Completed/Finished => Completed (or Finished if explicit)
                        if (ln.contains("approved") || ln.contains("completed") || ln.contains("finished")) return TaskStatus.COMPLETED;

                        // Rejected => Rejected
                        if (ln.contains("rejected")) return TaskStatus.REJECTED;

                        // Legacy fixed/repair wording => treat as Finished now
                        if (ln.contains("fixed") || ln.contains("repaired") || ln.contains("repair completed") || ln.contains("issue fixed")) {
                            return TaskStatus.FINISHED; // <-- changed: map legacy fixed -> Finished
                        }
                    }
                }
            } catch (Exception e) { /* ignore and fallback */ }

            // 4) EmpCertification workflows (Employee training)
            try {
                if (task.getEmpCertificationWorkflows() != null && !task.getEmpCertificationWorkflows().isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Set<com.smat.ins.model.entity.EmpCertificationWorkflow> ewfs = task.getEmpCertificationWorkflows();
                    for (com.smat.ins.model.entity.EmpCertificationWorkflow ew : ewfs) {
                        if (ew == null || ew.getWorkflowDefinition() == null || ew.getWorkflowDefinition().getStep() == null) continue;
                        String name = ew.getWorkflowDefinition().getStep().getEnglishName();
                        if (name == null) continue;
                        String ln = name.toLowerCase();

                        if (ln.contains("submitted") || ln.contains("inspected")) return TaskStatus.INSPECTED;

                        if (ln.contains("review") || ln.contains("under review") || ln.contains("in review")) {
                            return TaskStatus.INSPECTED_UNDER_REVIEW;
                        }

                        if (ln.contains("approved") || ln.contains("completed") || ln.contains("finished")) return TaskStatus.COMPLETED;

                        if (ln.contains("rejected")) return TaskStatus.REJECTED;

                        if (ln.contains("fixed") || ln.contains("repaired") || ln.contains("repair completed") || ln.contains("issue fixed")) {
                            return TaskStatus.FINISHED; // <-- changed here too
                        }
                    }
                }
            } catch (Exception e) { /* ignore and fallback */ }

            // 5) Fallback: check related forms loaded via services
            try {
                EmpCertification empCert = empCertificationService.getBy(task.getId());
                if (empCert != null) {
                    if (empCert.getSysUserByReviewedBy() != null) return TaskStatus.REVIEWED;
                    if (empCert.getSysUserByInspectedBy() != null) return TaskStatus.INSPECTED;
                    if (empCert.getStatus() != null && !empCert.getStatus().trim().isEmpty()) {
                        String st = empCert.getStatus().toLowerCase();
                        if (st.contains("approved") || st.contains("completed") || st.contains("finished")) return TaskStatus.COMPLETED;
                        if (st.contains("rejected")) return TaskStatus.REJECTED;
                        if (st.contains("fixed") || st.contains("repaired") || st.contains("repair completed") || st.contains("issue fixed")) {
                            return TaskStatus.FINISHED; // <-- legacy -> finished
                        }
                        if (st.contains("review") || st.contains("under review")) return TaskStatus.INSPECTED_UNDER_REVIEW;
                    }
                }
            } catch (Exception e) { /* ignore */ }

            try {
                EquipmentInspectionForm eif = equipmentInspectionFormService.getBy(task.getId());
                if (eif != null) {
                    if (eif.getSysUserByReviewedBy() != null) return TaskStatus.REVIEWED;
                    if (eif.getSysUserByInspectionBy() != null) return TaskStatus.INSPECTED;
                    // optionally check textual fields on the form if you have a status field there
                }
            } catch (Exception e) { /* ignore */ }

            // 6) If assigned to someone -> Assigned
            if (task.getUserAliasByAssignTo() != null) return TaskStatus.ASSIGNED;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Default
        return TaskStatus.PENDING;
    }





    public void clearSearch() {
        taskDescription = null;
        isCompleted = null;
        isDone = null;
        fromDate = null;
        toDate = null;
        assignee = null;

        // Clear new filters
        company = null;
        equipmentCategory = null;
        serviceType = null;
        assigner = null;
        completedFromDate = null;
        completedToDate = null;
        doneFromDate = null;
        doneToDate = null;
        taskStatus = null;
    workOrderFilter = null;
        searchResults.clear();
        groupedResultsByWorkOrder = null;
    }

    public String getReviewerTask(Task task) {
        if (task.getEquipmentCategory() != null) {
            EquipmentInspectionForm equipmentInspectionForm = equipmentInspectionFormService.getBy(task.getId());
            if (equipmentInspectionForm != null)
                if (equipmentInspectionForm.getSysUserByReviewedBy() != null)
                    return equipmentInspectionForm.getSysUserByReviewedBy().getDisplayName();
                else
                    return "Not assigned";
        } else {
            EmpCertification empCertification = empCertificationService.getBy(task.getId());
            if (empCertification != null)
                if (empCertification.getSysUserByReviewedBy() != null)
                    return empCertification.getSysUserByReviewedBy().getDisplayName();
                else
                    return "Not assigned";
        }

        return "Not assigned";
    }
    public static enum TaskStatus {
        PENDING("Pending"),
        ASSIGNED("Assigned"),
        INSPECTED("Inspected"),
        INSPECTED_UNDER_REVIEW("Inspected (Under review)"),
        REVIEWED("Reviewed"),
        COMPLETED("Completed"),
        FINISHED("Finished"),
        REJECTED("Rejected"),
        ON_HOLD("On Hold"),
        CANCELLED("Cancelled"),
        RESOLVED("Resolved"); // اختياري إن كنت تستعمله

        private final String label;
        TaskStatus(String label) { this.label = label; }
        public String getLabel() { return label; }
        @Override public String toString() { return label; }
    }
    public String getIconForStatusLabel(String statusLabel) {
        if (statusLabel == null) return "pi-clock";
        String s = statusLabel.toLowerCase();
        if (s.contains("assigned")) return "pi-user-plus";
        if (s.contains("inspected") && s.contains("under review")) return "pi-eye";
        if (s.contains("inspected")) return "pi-search";
        if (s.contains("reviewed")) return "pi-check";
        if (s.contains("completed")) return "pi-check";
        if (s.contains("finished") || s.contains("done")) return "pi-check-circle"; // covers legacy fixed -> finished
        if (s.contains("resolved")) return "pi-check-circle";
        if (s.contains("rejected")) return "pi-times";
        if (s.contains("hold") || s.contains("on hold")) return "pi-pause";
        if (s.contains("cancel")) return "pi-ban";
        return "pi-clock";
    }

    public String getStatusCssClass(String statusLabel) {
        if (statusLabel == null) return "status-badge pending";
        // normalize to safe token: lowercase, non-alnum -> dash, collapse dashes
        String token = statusLabel.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")   // replace non-alphanumeric with hyphen
                .replaceAll("^-+|-+$", "")       // trim leading/trailing hyphens
                .replaceAll("-{2,}", "-");       // collapse multiple hyphens
        if (token.isEmpty()) token = "pending";
        return "status-badge " + token; // e.g. "status-badge inspected-under-review"
    }

    public String getInspectorTask(Task task) {
        if (task.getEquipmentCategory() != null) {
            EquipmentInspectionForm equipmentInspectionForm = equipmentInspectionFormService.getBy(task.getId());
            if (equipmentInspectionForm != null)
                if (equipmentInspectionForm.getSysUserByInspectionBy() != null)
                    return equipmentInspectionForm.getSysUserByInspectionBy().getDisplayName();
                else
                    return "Not assigned";
        } else {
            EmpCertification empCertification = empCertificationService.getBy(task.getId());
            if (empCertification != null)
                if (empCertification.getSysUserByInspectedBy() != null)
                    return empCertification.getSysUserByInspectedBy().getDisplayName();
                else
                    return "Not assigned";
        }

        return "Not assigned";
    }
    public void exportToExcel() {
        try {
            // Create workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Tasks Report");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Create date style
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setBorderBottom(BorderStyle.THIN);
            dateStyle.setBorderTop(BorderStyle.THIN);
            dateStyle.setBorderLeft(BorderStyle.THIN);
            dateStyle.setBorderRight(BorderStyle.THIN);
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Task ID", "Company", "Equipment", "Service",
                    "Assigner", "Inspector", "Reviewer", "Status",
                    "Created Date", "Task Description"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Populate data rows
            int rowNum = 1;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (Task task : searchResults) {
                Row row = sheet.createRow(rowNum++);

                // Task ID
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(task.getId());
                cell0.setCellStyle(dataStyle);

                // Company
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(task.getCompany() != null ? task.getCompany().getName() : "");
                cell1.setCellStyle(dataStyle);

                // Equipment
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(task.getEquipmentCategory() != null ? task.getEquipmentCategory().getName() : "");
                cell2.setCellStyle(dataStyle);

                // Service
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(task.getServiceType() != null ? task.getServiceType().getName() : "");
                cell3.setCellStyle(dataStyle);

                // Assigner
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(task.getUserAliasByAssigner() != null ?
                        task.getUserAliasByAssigner().getSysUserBySysUser().getDisplayName() : "");
                cell4.setCellStyle(dataStyle);

                // Inspector
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(getInspectorTask(task));
                cell5.setCellStyle(dataStyle);

                // Reviewer
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(getReviewerTask(task));
                cell6.setCellStyle(dataStyle);

                // Status
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(getInspectionFormWorkflowStatus(task));
                cell7.setCellStyle(dataStyle);

                // Created Date
                Cell cell8 = row.createCell(8);
                if (task.getCreatedDate() != null) {
                    cell8.setCellValue(dateFormat.format(task.getCreatedDate()));
                } else {
                    cell8.setCellValue("");
                }
                cell8.setCellStyle(dataStyle);

                // Task Description
                Cell cell9 = row.createCell(9);
                cell9.setCellValue(task.getTaskDescription() != null ? task.getTaskDescription() : "");
                cell9.setCellStyle(dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Prepare response
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"tasks_report_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx\"");

            // Write workbook to response
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            response.getOutputStream().write(outputStream.toByteArray());
            response.getOutputStream().flush();
            response.getOutputStream().close();

            facesContext.responseComplete();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle error - you might want to add FacesMessage here
        }
    }


    // Getters and Setters
    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public UserAlias getAssignee() {
        return assignee;
    }

    public void setAssignee(UserAlias assignee) {
        this.assignee = assignee;
    }

    public List<Task> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Task> searchResults) {
        this.searchResults = searchResults;
    }

    public List<UserAlias> getUserAliasList() {
        return userAliasList;
    }

    public void setUserAliasList(List<UserAlias> userAliasList) {
        this.userAliasList = userAliasList;
    }

    // New getters and setters
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public EquipmentCategory getEquipmentCategory() {
        return equipmentCategory;
    }

    public void setEquipmentCategory(EquipmentCategory equipmentCategory) {
        this.equipmentCategory = equipmentCategory;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public UserAlias getAssigner() {
        return assigner;
    }

    public void setAssigner(UserAlias assigner) {
        this.assigner = assigner;
    }

    public Date getCompletedFromDate() {
        return completedFromDate;
    }

    public void setCompletedFromDate(Date completedFromDate) {
        this.completedFromDate = completedFromDate;
    }

    public Date getCompletedToDate() {
        return completedToDate;
    }

    public void setCompletedToDate(Date completedToDate) {
        this.completedToDate = completedToDate;
    }

    public Date getDoneFromDate() {
        return doneFromDate;
    }

    public void setDoneFromDate(Date doneFromDate) {
        this.doneFromDate = doneFromDate;
    }

    public Date getDoneToDate() {
        return doneToDate;
    }

    public void setDoneToDate(Date doneToDate) {
        this.doneToDate = doneToDate;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public List<Company> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }

    public List<EquipmentCategory> getEquipmentCategoryList() {
        return equipmentCategoryList;
    }

    public void setEquipmentCategoryList(List<EquipmentCategory> equipmentCategoryList) {
        this.equipmentCategoryList = equipmentCategoryList;
    }

    public List<ServiceType> getServiceTypeList() {
        return serviceTypeList;
    }

    public void setServiceTypeList(List<ServiceType> serviceTypeList) {
        this.serviceTypeList = serviceTypeList;
    }

    // Work order filter getter/setter
    public String getWorkOrderFilter() {
        return workOrderFilter;
    }

    public void setWorkOrderFilter(String workOrderFilter) {
        this.workOrderFilter = workOrderFilter;
    }

    /**
     * Compute a canonical work-order string for a Task.
     * Preference order:
     *  1) Correspondence id (first correspondenceTask) -> String.valueOf(id)
     *  2) EquipmentInspectionForm.jobNo
     *  3) EmpCertification.certNumber (if applicable)
     *  4) null if none
     */
    private String getWorkOrderForTask(Task task) {
        if (task == null) return null;
        try {
            // 1) correspondence id
            if (task.getCorrespondenceTasks() != null && !task.getCorrespondenceTasks().isEmpty()) {
                Object first = task.getCorrespondenceTasks().iterator().next();
                if (first instanceof com.smat.ins.model.entity.CorrespondenceTask) {
                    com.smat.ins.model.entity.CorrespondenceTask ct = (com.smat.ins.model.entity.CorrespondenceTask) first;
                    if (ct.getCorrespondence() != null && ct.getCorrespondence().getId() != null) {
                        return String.valueOf(ct.getCorrespondence().getId());
                    }
                }
            }

            // 2) EquipmentInspectionForm.jobNo
            try {
                EquipmentInspectionForm eif = equipmentInspectionFormService.getBy(task.getId());
                if (eif != null && eif.getJobNo() != null && !eif.getJobNo().trim().isEmpty())
                    return eif.getJobNo();
            } catch (Exception e) { /* ignore */ }

            // 3) EmpCertification.certNumber
            try {
                EmpCertification ec = empCertificationService.getBy(task.getId());
                if (ec != null && ec.getCertNumber() != null && !ec.getCertNumber().trim().isEmpty())
                    return ec.getCertNumber();
            } catch (Exception e) { /* ignore */ }

        } catch (Exception e) {
            // swallow - best-effort only
        }
        return null;
    }

    // Public wrapper for use in EL to get normalized work order key (never null)
    public String getWorkOrderKey(Task task) {
        String k = getWorkOrderForTask(task);
        return k == null ? "No Work Order" : k;
    }

    /**
     * Group searchResults by canonical work order string. Returns an ordered map (insertion order).
     */
    public Map<String, List<Task>> getGroupedResultsByWorkOrder() {
        if (groupedResultsByWorkOrder != null) return groupedResultsByWorkOrder;
        groupedResultsByWorkOrder = new LinkedHashMap<>();
        if (searchResults == null) return groupedResultsByWorkOrder;
        for (Task t : searchResults) {
            String key = getWorkOrderForTask(t);
            if (key == null) key = "No Work Order";
            List<Task> list = groupedResultsByWorkOrder.get(key);
            if (list == null) {
                list = new ArrayList<>();
                groupedResultsByWorkOrder.put(key, list);
            }
            list.add(t);
        }
        return groupedResultsByWorkOrder;
    }

}
