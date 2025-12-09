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
    private UserAlias inspector;
    private UserAlias reviewer;

    // Results
    private List<Task> searchResults = new ArrayList<>();
    // cached display names for inspector/reviewer per task id to avoid N+1 during rendering
    private Map<Integer, String> inspectorNameByTask = new HashMap<>();
    private Map<Integer, String> reviewerNameByTask = new HashMap<>();
    // cached grouped results by work order (computed on demand)
    private Map<String, List<Task>> groupedResultsByWorkOrder;
    private List<UserAlias> userAliasList = new ArrayList<>();
    private List<UserAlias> inspectorList = new ArrayList<>();
    private List<UserAlias> reviewerList = new ArrayList<>();
    private List<UserAlias> coordinatorList = new ArrayList<>();
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
            // populate role-specific alias lists (Inspector=001, Reviewer=002, Coordinator=004)
            try {
                List<UserAlias> allAliases = userAliasService.getAllWithDetails();
                if (allAliases != null) {
                    for (UserAlias ua : allAliases) {
                        if (ua == null || ua.getSysUserBySysUser() == null) continue;
                        if (ua.getSysUserBySysUser().getSysUserRoles() != null) {
                            for (Object oRole : ua.getSysUserBySysUser().getSysUserRoles()) {
                                try {
                                    com.smat.ins.model.entity.SysUserRole sur = (com.smat.ins.model.entity.SysUserRole) oRole;
                                    if (sur != null && sur.getSysRole() != null) {
                                        String code = sur.getSysRole().getCode();
                                        if ("001".equals(code)) { // inspector
                                            inspectorList.add(ua); break;
                                        } else if ("002".equals(code)) { // reviewer
                                            reviewerList.add(ua); break;
                                        } else if ("004".equals(code)) { // coordinator
                                            coordinatorList.add(ua); break;
                                        }
                                    }
                                } catch (Exception ignore) {}
                            }
                        }
                    }
                }
            } catch (Exception ignore) {}
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

            // Inspector filter: primary source is task.assign_to (userAliasByAssignTo)
            if (inspector != null) {
                searchResults.removeIf(task -> {
                    try {
                        if (task.getUserAliasByAssignTo() != null && task.getUserAliasByAssignTo().getSysUserBySysUser() != null) {
                            return !inspector.getSysUserBySysUser().getId()
                                    .equals(task.getUserAliasByAssignTo().getSysUserBySysUser().getId());
                        }

                        // If assign_to not present, fallback to form-based inspector if available
                        try {
                            if (task.getEquipmentCategory() != null && equipmentInspectionFormService != null) {
                                EquipmentInspectionForm eif = equipmentInspectionFormService.getBy(task.getId());
                                if (eif != null && eif.getSysUserByInspectionBy() != null) {
                                    return !inspector.getSysUserBySysUser().getId()
                                            .equals(eif.getSysUserByInspectionBy().getId());
                                }
                            } else if (empCertificationService != null) {
                                try {
                                    EmpCertification ec = empCertificationService.getBy(task.getId());
                                    if (ec != null && ec.getSysUserByInspectedBy() != null) {
                                        return !inspector.getSysUserBySysUser().getId()
                                                .equals(ec.getSysUserByInspectedBy().getId());
                                    }
                                } catch (Exception ex) { /* ignore and treat as not matched */ }
                            }
                        } catch (Exception ignore) { }

                        // If neither assign_to nor form info matched, exclude the task
                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                });
            }

            // Reviewer filter: primary source is workflow tables attached to the Task
            if (reviewer != null) {
                searchResults.removeIf(task -> {
                    try {
                        // 1) Check inspectionFormWorkflows on the task
                        try {
                            if (task.getInspectionFormWorkflows() != null && !task.getInspectionFormWorkflows().isEmpty()) {
                                for (Object ow : task.getInspectionFormWorkflows()) {
                                    com.smat.ins.model.entity.InspectionFormWorkflow wf = (com.smat.ins.model.entity.InspectionFormWorkflow) ow;
                                    if (wf != null && wf.getReviewedBy() != null) {
                                        return !reviewer.getSysUserBySysUser().getId().equals(wf.getReviewedBy().getId());
                                    }
                                }
                            }
                        } catch (Exception ignore) {}

                        // 2) Check empCertificationWorkflows on the task
                        try {
                            if (task.getEmpCertificationWorkflows() != null && !task.getEmpCertificationWorkflows().isEmpty()) {
                                for (Object ow : task.getEmpCertificationWorkflows()) {
                                    com.smat.ins.model.entity.EmpCertificationWorkflow ew = (com.smat.ins.model.entity.EmpCertificationWorkflow) ow;
                                    if (ew != null && ew.getSysUser() != null) {
                                        return !reviewer.getSysUserBySysUser().getId().equals(ew.getSysUser().getId());
                                    }
                                }
                            }
                        } catch (Exception ignore) {}

                        // 3) Fallback: check task.assignTo (some flows assign reviewer there)
                        if (task.getUserAliasByAssignTo() != null && task.getUserAliasByAssignTo().getSysUserBySysUser() != null) {
                            return !reviewer.getSysUserBySysUser().getId().equals(task.getUserAliasByAssignTo().getSysUserBySysUser().getId());
                        }

                    } catch (Exception e) {
                        return true;
                    }
                    return true;
                });
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

            // Prefetch inspector/reviewer display names for visible tasks to avoid N+1 in the UI
            inspectorNameByTask.clear();
            reviewerNameByTask.clear();
            if (searchResults != null) {
                for (Task task : searchResults) {
                    try {
                        Integer tid = task.getId();
                        if (tid == null) continue;
                        // Prefer using service.getBy(taskId) which queries via workflow
                        try {
                            EquipmentInspectionForm eif = null;
                            EmpCertification ec = null;
                            try { eif = equipmentInspectionFormService.getBy(tid); } catch (Exception ignore) { eif = null; }
                            try { ec = empCertificationService.getBy(tid); } catch (Exception ignore) { ec = null; }

                            if (eif != null && eif.getSysUserByInspectionBy() != null) {
                                inspectorNameByTask.put(tid, eif.getSysUserByInspectionBy().getDisplayName());
                            } else if (ec != null && ec.getSysUserByInspectedBy() != null) {
                                inspectorNameByTask.put(tid, ec.getSysUserByInspectedBy().getDisplayName());
                            }

                            if (eif != null && eif.getSysUserByReviewedBy() != null) {
                                reviewerNameByTask.put(tid, eif.getSysUserByReviewedBy().getDisplayName());
                            } else if (ec != null && ec.getSysUserByReviewedBy() != null) {
                                reviewerNameByTask.put(tid, ec.getSysUserByReviewedBy().getDisplayName());
                            }
                        } catch (Exception e) {
                            // ignore per-task
                        }
                    } catch (Exception ex) { /* ignore */ }
                }
            }

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
        if (task == null) return "Not assigned";

        // 1) If workflow attached to the task contains reviewer info, use it (immediate)
        try {
            if (task.getInspectionFormWorkflows() != null && !task.getInspectionFormWorkflows().isEmpty()) {
                for (Object ow : task.getInspectionFormWorkflows()) {
                    com.smat.ins.model.entity.InspectionFormWorkflow wf = (com.smat.ins.model.entity.InspectionFormWorkflow) ow;
                    if (wf != null && wf.getReviewedBy() != null) {
                        return wf.getReviewedBy().getDisplayName();
                    }
                }
            }
        } catch (Exception ignore) {}

        try {
            if (task.getEmpCertificationWorkflows() != null && !task.getEmpCertificationWorkflows().isEmpty()) {
                for (Object ow : task.getEmpCertificationWorkflows()) {
                    com.smat.ins.model.entity.EmpCertificationWorkflow ew = (com.smat.ins.model.entity.EmpCertificationWorkflow) ow;
                    if (ew != null && ew.getSysUser() != null) {
                        return ew.getSysUser().getDisplayName();
                    }
                }
            }
        } catch (Exception ignore) {}

        // 2) Next: check cached prefetch
        Integer tid = task.getId();
        if (tid != null && reviewerNameByTask.containsKey(tid)) return reviewerNameByTask.get(tid);

        // 3) Fallback to form-based reviewer (service lookup)
        try {
            if (task.getEquipmentCategory() != null) {
                EquipmentInspectionForm equipmentInspectionForm = equipmentInspectionFormService.getBy(task.getId());
                if (equipmentInspectionForm != null && equipmentInspectionForm.getSysUserByReviewedBy() != null)
                    return equipmentInspectionForm.getSysUserByReviewedBy().getDisplayName();
            } else {
                EmpCertification empCertification = empCertificationService.getBy(task.getId());
                if (empCertification != null && empCertification.getSysUserByReviewedBy() != null)
                    return empCertification.getSysUserByReviewedBy().getDisplayName();
            }
        } catch (Exception ignore) {}

        // 4) final fallback
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
        if (task == null) return "Not assigned";
        // Primary: read directly from task.assign_to
        try {
            if (task.getUserAliasByAssignTo() != null && task.getUserAliasByAssignTo().getSysUserBySysUser() != null) {
                return task.getUserAliasByAssignTo().getSysUserBySysUser().getDisplayName();
            }
        } catch (Exception ignore) {}

        // Next: check cached prefetch
        Integer tid = task.getId();
        if (tid != null && inspectorNameByTask.containsKey(tid)) return inspectorNameByTask.get(tid);

        // Fallback: check form-based inspector
        try {
            if (task.getEquipmentCategory() != null) {
                EquipmentInspectionForm equipmentInspectionForm = equipmentInspectionFormService.getBy(task.getId());
                if (equipmentInspectionForm != null && equipmentInspectionForm.getSysUserByInspectionBy() != null)
                    return equipmentInspectionForm.getSysUserByInspectionBy().getDisplayName();
            } else {
                EmpCertification empCertification = empCertificationService.getBy(task.getId());
                if (empCertification != null && empCertification.getSysUserByInspectedBy() != null)
                    return empCertification.getSysUserByInspectedBy().getDisplayName();
            }
        } catch (Exception ignore) {}

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

            // Create header row (including Work Order as first column)
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Work Order", "Task ID", "Company", "Equipment", "Service",
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

            for (Task task : searchResults) {
                Row row = sheet.createRow(rowNum++);

                // Work Order (use public wrapper to guarantee non-null label)
                Cell cell0 = row.createCell(0);
                String workOrder = getWorkOrderKey(task);
                cell0.setCellValue(workOrder != null ? workOrder : "");
                cell0.setCellStyle(dataStyle);

                // Task ID
                Cell cell1 = row.createCell(1);
                if (task.getId() != null) {
                    // depending on type of id, set as numeric or string
                    try {
                        cell1.setCellValue(Long.parseLong(String.valueOf(task.getId())));
                    } catch (Exception ex) {
                        cell1.setCellValue(String.valueOf(task.getId()));
                    }
                } else {
                    cell1.setCellValue("");
                }
                cell1.setCellStyle(dataStyle);

                // Company
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(task.getCompany() != null ? task.getCompany().getName() : "");
                cell2.setCellStyle(dataStyle);

                // Equipment
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(task.getEquipmentCategory() != null ? task.getEquipmentCategory().getName() : "");
                cell3.setCellStyle(dataStyle);

                // Service
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(task.getServiceType() != null ? task.getServiceType().getName() : "");
                cell4.setCellStyle(dataStyle);

                // Assigner
                Cell cell5 = row.createCell(5);
                String assignerName = "";
                if (task.getUserAliasByAssigner() != null && task.getUserAliasByAssigner().getSysUserBySysUser() != null) {
                    assignerName = task.getUserAliasByAssigner().getSysUserBySysUser().getDisplayName();
                }
                cell5.setCellValue(assignerName);
                cell5.setCellStyle(dataStyle);

                // Inspector
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(getInspectorTask(task));
                cell6.setCellStyle(dataStyle);

                // Reviewer
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(getReviewerTask(task));
                cell7.setCellStyle(dataStyle);

                // Status
                Cell cell8 = row.createCell(8);
                cell8.setCellValue(getInspectionFormWorkflowStatus(task));
                cell8.setCellStyle(dataStyle);

                // Created Date (use proper date cell if available)
                Cell cell9 = row.createCell(9);
                if (task.getCreatedDate() != null) {
                    cell9.setCellValue(task.getCreatedDate());
                    cell9.setCellStyle(dateStyle);
                } else {
                    cell9.setCellValue("");
                    cell9.setCellStyle(dataStyle);
                }

                // Task Description
                Cell cell10 = row.createCell(10);
                cell10.setCellValue(task.getTaskDescription() != null ? task.getTaskDescription() : "");
                cell10.setCellStyle(dataStyle);
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

    public UserAlias getInspector() { return inspector; }
    public void setInspector(UserAlias inspector) { this.inspector = inspector; }

    public UserAlias getReviewer() { return reviewer; }
    public void setReviewer(UserAlias reviewer) { this.reviewer = reviewer; }

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

    public List<UserAlias> getInspectorList() { return inspectorList; }
    public void setInspectorList(List<UserAlias> inspectorList) { this.inspectorList = inspectorList; }

    public List<UserAlias> getReviewerList() { return reviewerList; }
    public void setReviewerList(List<UserAlias> reviewerList) { this.reviewerList = reviewerList; }

    public List<UserAlias> getCoordinatorList() { return coordinatorList; }
    public void setCoordinatorList(List<UserAlias> coordinatorList) { this.coordinatorList = coordinatorList; }

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
