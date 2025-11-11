package com.smat.ins.view.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.smat.ins.model.entity.Company;
import com.smat.ins.model.entity.EmpCertification;
import com.smat.ins.model.entity.EmpCertificationType;
import com.smat.ins.model.entity.Employee;
import com.smat.ins.model.entity.EquipmentCategory;
import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.Task;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.service.CompanyService;
import com.smat.ins.model.service.CompletedTaskService;
import com.smat.ins.model.service.EmpCertificationService;
import com.smat.ins.model.service.EquipmentCategoryService;
import com.smat.ins.model.service.EquipmentInspectionFormService;
import com.smat.ins.model.service.OrganizationService;
import com.smat.ins.model.service.SysUserService;
import com.smat.ins.model.service.TaskService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.QRCodeGenerator;
import com.smat.ins.util.UtilityHelper;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import java.text.SimpleDateFormat;
import java.net.URLEncoder;

import org.primefaces.PrimeFaces;


/**
 * Managed bean for searching and displaying completed tasks
 * based on workflow completion status (step code '03')
 */
@Named
@ViewScoped
public class CompletedTaskSearchBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Search criteria fields
    private String taskType = ""; // "equipment", "employee", or "" for both
    private Date fromDate;
    private Date toDate;
    private Company company;
    private EquipmentCategory equipmentCategory;
    private SysUserService sysUserService;
    private String certNumberSearch;
    private Organization organizationSearch;

    public String getCertNumberSearch() {
        return certNumberSearch;
    }

    public void setCertNumberSearch(String certNumberSearch) {
        this.certNumberSearch = certNumberSearch;
    }


    // Results
    private List<Map<String, Object>> searchResults = new ArrayList<>();
    private Map<String, Object> statistics;
    private boolean searchPerformed = false;
    private Map<String, String> smatToDisplayName = new HashMap<>();

    // Dropdown lists
    private List<Company> companyList = new ArrayList<>();
    private List<EquipmentCategory> equipmentCategoryList = new ArrayList<>();
    private List<Organization> organizationList = new ArrayList<>();

    // Services
    private CompletedTaskService completedTaskService;
    private CompanyService companyService;
    private EquipmentCategoryService equipmentCategoryService;
    private EquipmentInspectionFormService equipmentInspectionFormService;
    private EmpCertificationService empCertificationService;
    private TaskService taskService;
    private OrganizationService organizationService;

    // new field for filtering by Work Order (job number)
    private String workOrderFilter;

    public String getWorkOrderFilter() {
        return workOrderFilter;
    }

    public void setWorkOrderFilter(String workOrderFilter) {
        this.workOrderFilter = workOrderFilter;
    }


    @Inject
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        try {
            // Initialize services
            completedTaskService = (CompletedTaskService) BeanUtility.getBean("completedTaskService");
            companyService = (CompanyService) BeanUtility.getBean("companyService");
            equipmentCategoryService = (EquipmentCategoryService) BeanUtility.getBean("equipmentCategoryService");
            sysUserService = (SysUserService) BeanUtility.getBean("sysUserService");
            equipmentInspectionFormService=(EquipmentInspectionFormService) BeanUtility.getBean("equipmentInspectionFormService");
            empCertificationService=(EmpCertificationService) BeanUtility.getBean("empCertificationService");
            taskService=(TaskService) BeanUtility.getBean("taskService");
            organizationService = (OrganizationService) BeanUtility.getBean("organizationService");

            // Build local map for SMAT/usercode -> display name
            buildUserMap();

            // Load dropdown data
            loadDropdownData();

            // Perform initial search to show all completed tasks
            searchCompletedTasks();

        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage("Error initializing completed task search: " + e.getMessage());
        }
    }

    /**
     * Build a lookup map from possible SMAT/user codes to a display name.
     * This reduces DB lookups during rendering and handles values like "SMAT00001",
     * usernames, or numeric ids.
     */
    private void buildUserMap() {
        try {
            List<SysUser> allUsers = sysUserService.findAll();
            if (allUsers == null) return;

            for (SysUser u : allUsers) {
                String display = null;

                // Prefer enDisplayName if available
                try {
                    if (u.getEnDisplayName() != null && !u.getEnDisplayName().trim().isEmpty()) {
                        display = u.getEnDisplayName().trim();
                    }
                } catch (Exception ex) {
                    // ignore
                }

                // Fallback to first+last name
                if (display == null || display.isEmpty()) {
                    try {
                        String fn = u.getFirstName() != null ? u.getFirstName().trim() : "";
                        String ln = u.getLastName() != null ? u.getLastName().trim() : "";
                        String combined = (fn + " " + ln).trim();
                        if (!combined.isEmpty()) {
                            display = combined;
                        }
                    } catch (Exception ex) {
                        // ignore
                    }
                }

                // Fallback to username or id
                if (display == null || display.isEmpty()) {
                    try {
                        if (u.getUserName() != null && !u.getUserName().trim().isEmpty()) {
                            display = u.getUserName().trim();
                        } else {
                            display = String.valueOf(u.getId());
                        }
                    } catch (Exception ex) {
                        display = String.valueOf(u.getId());
                    }
                }

                // Put mappings for likely keys:
                // - sysUserCode (SMAT code)
                // - userName
                // - numeric id as string
                try {
                    if (u.getSysUserCode() != null && !u.getSysUserCode().trim().isEmpty()) {
                        smatToDisplayName.put(u.getSysUserCode().trim(), display);
                    }
                } catch (Exception ex) {
                    // ignore
                }

                try {
                    if (u.getUserName() != null && !u.getUserName().trim().isEmpty()) {
                        smatToDisplayName.put(u.getUserName().trim(), display);
                    }
                } catch (Exception ex) {
                    // ignore
                }

                try {
                    if (u.getId() != null) {
                        smatToDisplayName.put(String.valueOf(u.getId()), display);
                    }
                } catch (Exception ex) {
                    // ignore
                }

                // Also map each UserAlias.userCode -> alias display name (prefer alias name)
                try {
                    if (u.getUserAliasesForSysUser() != null) {
                        for (Object o : u.getUserAliasesForSysUser()) {
                            if (o instanceof UserAlias) {
                                UserAlias ua = (UserAlias) o;
                                if (ua.getUserCode() != null && !ua.getUserCode().trim().isEmpty()) {
                                    String aliasDisplay = null;
                                    if (ua.getEnAliasName() != null && !ua.getEnAliasName().trim().isEmpty()) {
                                        aliasDisplay = ua.getEnAliasName().trim();
                                    } else if (ua.getArAliasName() != null && !ua.getArAliasName().trim().isEmpty()) {
                                        aliasDisplay = ua.getArAliasName().trim();
                                    } else {
                                        aliasDisplay = display;
                                    }
                                    smatToDisplayName.put(ua.getUserCode().trim(), aliasDisplay);
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    // ignore
                }
            }

            // Optional: debug log size
            // System.out.println("Built smatToDisplayName map, size=" + smatToDisplayName.size());

        } catch (Exception e) {
            // If building map fails, log and continue (we'll fallback per-request)
            e.printStackTrace();
        }
    }

    private void loadDropdownData() {
        try {
            companyList = companyService.findAll();
            equipmentCategoryList = equipmentCategoryService.findAll();
            organizationList = organizationService.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage("Error loading dropdown data: " + e.getMessage());
        }
    }

    /**
     * Search for completed tasks based on the current criteria
     */
    public void searchCompletedTasks() {
        try {
            // Determine task type filter
            String taskTypeFilter = null;
            if (taskType != null && !taskType.isEmpty()) {
                taskTypeFilter = taskType;
            }

            // Get company ID
            Integer companyId = (company != null) ? company.getId() : null;

            // Get equipment category ID (only relevant for equipment tasks)
            Short equipmentCategoryId = null;
            if (equipmentCategory != null && ("equipment".equals(taskTypeFilter) || taskTypeFilter == null)) {
                equipmentCategoryId = equipmentCategory.getId();
            }

            // Get organization ID
            Long organizationId = this.organizationId; // new field populated by the xhtml selectOneMenu

            // ----- BACKFILL: ensure tasks that reached step '03' have completed_date set -----
            try {
                // This will update tasks (equipment & employee) whose workflow is at '03' and completed_date is NULL
                completedTaskService.backfillMissingCompletedDates(fromDate, toDate, companyId, equipmentCategoryId);
            } catch (Exception bfEx) {
                bfEx.printStackTrace();
                addWarningMessage("Warning: failed to backfill completed dates. Search will proceed but some items may show empty completed date.");
            }
            // ---------------------------------------------------------------------------------

            // Perform search (original)
            List<Map<String, Object>> rawResults = completedTaskService.getCompletedTasks(
                    taskTypeFilter, fromDate, toDate, companyId, equipmentCategoryId);

            // ---- Robust local filtering by Cert Number (if user provided certNumberSearch) ----
            if (certNumberSearch != null && !certNumberSearch.trim().isEmpty() && rawResults != null) {
                String q = certNumberSearch.trim().toLowerCase();
                List<Map<String, Object>> filtered = new ArrayList<>();

                for (Map<String, Object> r : rawResults) {
                    if (r == null) continue;
                    boolean match = false;

                    // 1) Direct exact key "Cert Num"
                    Object v = r.get("Cert Num");
                    if (!match && v != null && String.valueOf(v).toLowerCase().contains(q)) {
                        match = true;
                    }

                    // 2) Try common alternate keys
                    if (!match) {
                        String[] altKeys = {"certNumber", "certNo", "cert_num", "certnum", "cert", "reportNo", "report_no", "reportno", "reference", "stickerNo", "stickerno", "sticker"};
                        for (String k : altKeys) {
                            v = r.get(k);
                            if (v != null && String.valueOf(v).toLowerCase().contains(q)) {
                                match = true;
                                break;
                            }
                        }
                    }

                    // 3) Fallback: any key name containing "cert"/"report"/"reference"/"sticker"
                    if (!match) {
                        for (Map.Entry<String, Object> e : r.entrySet()) {
                            String keyName = e.getKey() == null ? "" : String.valueOf(e.getKey()).toLowerCase();
                            if (keyName.contains("cert") || keyName.contains("report") || keyName.contains("reference") || keyName.contains("sticker")) {
                                Object val = e.getValue();
                                if (val != null && String.valueOf(val).toLowerCase().contains(q)) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (match) filtered.add(r);
                }
                rawResults = filtered;
            }
            // ---- end certNumber filtering ----

            // Prepare the final modifiable list
            searchResults = new ArrayList<>();
            if (rawResults != null) {
                int idx = 0;
                for (Map<String, Object> originalRow : rawResults) {
                    Map<String, Object> row = new HashMap<>();
                    if (originalRow != null) {
                        row.putAll(originalRow);
                    }

                    try {
                        // ---- BEGIN: populate organizationName fallback logic ----
                        String organizationName = null;

                        // 1) إذا الخريطة تحتوي بالفعل organizationName فاستعملها
                        try {
                            Object orgVal = row.get("organizationName");
                            if (orgVal != null && !String.valueOf(orgVal).trim().isEmpty()) {
                                organizationName = String.valueOf(orgVal).trim();
                            }
                        } catch (Exception ignore) {}

                        // 2) SysUser object path
                        if ((organizationName == null || organizationName.isEmpty())) {
                            try {
                                Object suObj = row.get("sysUser");
                                if (suObj instanceof SysUser) {
                                    SysUser su = (SysUser) suObj;
                                    if (su.getOrganizationByOrganization() != null
                                            && su.getOrganizationByOrganization().getName() != null) {
                                        organizationName = su.getOrganizationByOrganization().getName().trim();
                                    }
                                }
                            } catch (Exception ignore) {}
                        }

                        // 3) Try to use taskId to find forms/certs for organization
                        if ((organizationName == null || organizationName.isEmpty())) {
                            try {
                                Object idObj = row.get("taskId");
                                Integer localTid = null;
                                if (idObj instanceof Number) {
                                    localTid = ((Number) idObj).intValue();
                                } else if (idObj != null) {
                                    try { localTid = Integer.valueOf(String.valueOf(idObj)); } catch (NumberFormatException nfe) { localTid = null; }
                                }

                                if (localTid != null) {
                                    // Equipment inspection -> try form's inspectionBy or reviewedBy
                                    try {
                                        EquipmentInspectionForm form = equipmentInspectionFormService.getBy(localTid);
                                        if (form != null) {
                                            SysUser su = form.getSysUserByInspectionBy() != null ? form.getSysUserByInspectionBy()
                                                    : form.getSysUserByReviewedBy();
                                            if (su != null && su.getOrganizationByOrganization() != null
                                                    && su.getOrganizationByOrganization().getName() != null) {
                                                organizationName = su.getOrganizationByOrganization().getName().trim();
                                            }
                                        }
                                    } catch (Exception ignore) {}

                                    // Employee certification -> try cert's inspectedBy or reviewedBy
                                    if (organizationName == null || organizationName.isEmpty()) {
                                        try {
                                            EmpCertification cert = empCertificationService.getBy(localTid);
                                            if (cert != null) {
                                                SysUser su = cert.getSysUserByInspectedBy() != null ? cert.getSysUserByInspectedBy()
                                                        : cert.getSysUserByReviewedBy();
                                                if (su != null && su.getOrganizationByOrganization() != null
                                                        && su.getOrganizationByOrganization().getName() != null) {
                                                    organizationName = su.getOrganizationByOrganization().getName().trim();
                                                }
                                            }
                                        } catch (Exception ignore) {}
                                    }
                                }
                            } catch (Exception ignore) {}
                        }

                        // 4) Fallback to companyName
                        if (organizationName == null || organizationName.isEmpty()) {
                            try {
                                Object comp = row.get("companyName");
                                if (comp != null && !String.valueOf(comp).trim().isEmpty()) {
                                    organizationName = String.valueOf(comp).trim();
                                }
                            } catch (Exception ignore) {}
                        }

                        // 5) Put final value
                        row.put("organizationName", (organizationName != null && !organizationName.trim().isEmpty()) ? organizationName : "-");
                        // ---- END: populate organizationName fallback logic ----

                        // Normalize taskId to Integer if possible
                        Object idObj = row.get("taskId");
                        Integer tid = null;
                        if (idObj instanceof Number) {
                            tid = ((Number) idObj).intValue();
                        } else if (idObj != null) {
                            try {
                                tid = Integer.valueOf(String.valueOf(idObj));
                            } catch (NumberFormatException nfe) {
                                tid = null;
                            }
                        }

                        // Ensure jobNo / timeSheetNo etc are populated (this will set 'jobNo' key)
                        populateJobAndTimesheet(row, tid);

                        populateWorkOrder(row, tid);


                        String inspector = "Not assigned";
                        String reviewer = "Not assigned";

                        if (tid != null) {
                            try {
                                inspector = getInspectorTask(tid);
                                if (inspector == null || inspector.trim().isEmpty()) inspector = "Not assigned";
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                inspector = "Not assigned";
                            }
                            try {
                                reviewer = getReviewerTask(tid);
                                if (reviewer == null || reviewer.trim().isEmpty()) reviewer = "Not assigned";
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                reviewer = "Not assigned";
                            }
                        } else {
                            Object iobj = row.get("inspector");
                            if (iobj == null) iobj = row.get("inspectorName");
                            if (iobj != null && !String.valueOf(iobj).trim().isEmpty()) inspector = String.valueOf(iobj);

                            Object robj = row.get("reviewer");
                            if (robj == null) robj = row.get("reviewerName");
                            if (robj != null && !String.valueOf(robj).trim().isEmpty()) reviewer = String.valueOf(robj);
                        }

                        row.put("inspectorName", inspector);
                        row.put("reviewerName", reviewer);

                    } catch (Exception e) {
                        try {
                            row.put("inspectorName", row.getOrDefault("inspectorName", "Not assigned"));
                            row.put("reviewerName", row.getOrDefault("reviewerName", "Not assigned"));
                        } catch (Exception ignore) { }
                        e.printStackTrace();
                    }

                    searchResults.add(row);
                    idx++;
                }
            }

            // ---------- Post-process organization filter (apply AFTER we populated organizationName etc) ----------
            if (organizationId != null && searchResults != null && !searchResults.isEmpty()) {
                String targetOrgName = null;
                if (organizationList != null) {
                    for (Organization o : organizationList) {
                        if (o != null && o.getId() != null && o.getId().longValue() == organizationId.longValue()) {
                            targetOrgName = o.getName();
                            break;
                        }
                    }
                }

                List<Map<String, Object>> filteredResults = new ArrayList<>();
                for (Map<String, Object> row : searchResults) {
                    if (row == null) continue;
                    boolean keep = false;

                    // try id from row (if exists)
                    Long rowOrgId = extractOrganizationIdFromRow(row);
                    if (rowOrgId != null && rowOrgId.longValue() == organizationId.longValue()) {
                        keep = true;
                    }

                    // try organizationName
                    if (!keep && targetOrgName != null) {
                        Object orgNameObj = row.get("organizationName");
                        String orgName = orgNameObj == null ? null : String.valueOf(orgNameObj).trim();
                        if (orgName != null && !orgName.isEmpty() && orgName.equalsIgnoreCase(targetOrgName)) {
                            keep = true;
                        }
                    }

                    // final fallback: compare companyName to targetOrgName
                    if (!keep && targetOrgName != null) {
                        Object comp = row.get("companyName");
                        if (comp != null && String.valueOf(comp).trim().equalsIgnoreCase(targetOrgName)) {
                            keep = true;
                        }
                    }

                    if (keep) filteredResults.add(row);
                }

                searchResults = filteredResults;
            }
            // ---------- end org post-filter ----------

            // ---- NEW: Apply workOrderFilter AFTER we populated 'jobNo' (and possibly after organization filter) ----
            if (workOrderFilter != null && !workOrderFilter.trim().isEmpty() && searchResults != null && !searchResults.isEmpty()) {
                String q = workOrderFilter.trim().toLowerCase();
                List<Map<String, Object>> wf = new ArrayList<>();
                    // Prefer an explicit 'workOrder' value (populated from Task->Correspondence or explicit workOrder fields)
                    // Fall back to jobNo only if workOrder is not present.
                String[] workOrderKeys = {"workOrder", "workOrderNo", "workOrderNumber", "jobNo", "job_number", "job_no", "jobNum", "jobNumber"};
                for (Map<String,Object> row : searchResults) {
                    if (row == null) continue;
                    String value = "";
                    for (String k : workOrderKeys) {
                        Object vv = row.get(k);
                        if (vv != null && !String.valueOf(vv).trim().isEmpty()) {
                            value = String.valueOf(vv).trim();
                            break;
                        }
                    }
                    if (value.toLowerCase().contains(q)) {
                        wf.add(row);
                    }
                }
                searchResults = wf;
            }
            // ---- end workOrder filter ----

            statistics = completedTaskService.getTaskCompletionStatistics(fromDate, toDate, companyId);

            searchPerformed = true;

            // If after filtering there are zero results, give a helpful warning
            if ((searchResults == null || searchResults.isEmpty())) {
                addWarningMessage("No completed tasks found for the given search criteria.");
            } else {
                addInfoMessage("Found " + (searchResults != null ? searchResults.size() : 0) + " completed tasks");
            }

        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage("Error searching completed tasks: " + e.getMessage());
            if (searchResults != null) searchResults.clear();
            statistics = null;
        }
    }

    /**
     * Group current searchResults by Work Order (jobNo or alternate keys).
     * Returns a LinkedHashMap ordered with numeric/alphanumeric work orders first (descending),
     * and "No Work Order" last.
     */
    public Map<String, List<Map<String, Object>>> getGroupedResultsByWorkOrder() {
        LinkedHashMap<String, List<Map<String, Object>>> groups = new LinkedHashMap<>();
        if (searchResults == null || searchResults.isEmpty()) return groups;

    // Prefer 'workOrder' (may contain correspondence id set earlier) before falling back to jobNo
    String[] workOrderKeys = {"workOrder", "workOrderNo", "workOrderNumber", "jobNo", "job_number", "job_no", "jobNum", "jobNumber"};

        // collect groups (unsorted)
        for (Map<String, Object> row : searchResults) {
            if (row == null) continue;
            String wo = null;
            for (String k : workOrderKeys) {
                Object v = row.get(k);
                if (v != null && !String.valueOf(v).trim().isEmpty()) {
                    wo = String.valueOf(v).trim();
                    break;
                }
            }
            if (wo == null || wo.isEmpty()) wo = "No Work Order";

            List<Map<String, Object>> list = groups.get(wo);
            if (list == null) {
                list = new ArrayList<>();
                groups.put(wo, list);
            }
            list.add(row);
        }

        // sort groups: put non-"No Work Order" first (attempt numeric desc where possible), "No Work Order" last
        List<Map.Entry<String, List<Map<String, Object>>>> entries = new ArrayList<>(groups.entrySet());
        entries.sort((e1, e2) -> {
            String k1 = e1.getKey();
            String k2 = e2.getKey();
            // keep "No Work Order" last
            if ("No Work Order".equals(k1) && "No Work Order".equals(k2)) return 0;
            if ("No Work Order".equals(k1)) return 1;
            if ("No Work Order".equals(k2)) return -1;

            // try numeric compare
            try {
                long n1 = Long.parseLong(k1.replaceAll("[^0-9]", ""));
                long n2 = Long.parseLong(k2.replaceAll("[^0-9]", ""));
                return Long.compare(n2, n1); // descending
            } catch (Exception ex) {
                // fallback to string compare descending
                return k2.compareTo(k1);
            }
        });

        LinkedHashMap<String, List<Map<String, Object>>> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, List<Map<String, Object>>> e : entries) {
            sorted.put(e.getKey(), e.getValue());
        }
        return sorted;
    }
    /**
     * Try to extract a String value by trying several getter method names via reflection.
     * Returns null if no getter found or invocation fails / returns null/empty.
     */
    private String extractStringUsingReflection(Object obj, String[] candidateGetters) {
        if (obj == null) return null;
        for (String getter : candidateGetters) {
            try {
                java.lang.reflect.Method m = null;
                try {
                    m = obj.getClass().getMethod(getter);
                } catch (NoSuchMethodException nsme) {
                    // try property-style "get" + capitalized field if not exact provided
                    // but we assume candidateGetters already are method names like "getWorkOrderNumber"
                }
                if (m != null) {
                    Object val = m.invoke(obj);
                    if (val != null) {
                        String s = String.valueOf(val).trim();
                        if (!s.isEmpty()) return s;
                    }
                }
            } catch (Exception e) {
                // ignore and try next candidate
            }
        }
        return null;
    }


    /**
     * Ensure the result row contains a normalized 'workOrder' key.
     * Search order:
     *  1) existing keys in the row (common names)
     *  2) try Task entity via taskService
     *  3) try EquipmentInspectionForm via equipmentInspectionFormService
     *  4) try EmpCertification via empCertificationService
     * If nothing found, put "No Work Order".
     */
    private void populateWorkOrder(Map<String, Object> row, Integer taskId) {
        if (row == null) return;

    // 1) try common keys already present in the row
    // Note: do NOT treat jobNo as a direct synonym for workOrder here - keep jobNo as a separate field.
    // This avoids showing job number when an explicit work order value exists or should be shown.
    String[] candidateKeys = {"workOrder", "workOrderNo", "workOrderNumber", "work_order_no", "work_order", "woNumber", "wo_no"};
        for (String k : candidateKeys) {
            try {
                Object v = row.get(k);
                if (v != null && !String.valueOf(v).trim().isEmpty()) {
                    row.put("workOrder", String.valueOf(v).trim());
                    return;
                }
            } catch (Exception ignore) {}
        }

        // 2) if still missing, try to extract from Task entity, EquipmentInspectionForm, EmpCertification using reflection
        String[] getters = new String[] {
                "getWorkOrderNumber", "getWorkOrderNo", "getWorkOrder", "getWoNumber", "getWoNo", "getWorkOrderNum"
        };

        // try Task
        try {
            if (taskId != null) {
                Task t = null;
                try { t = taskService.findById(taskId); } catch (Exception ignore) {}
                if (t != null) {
                    String val = extractStringUsingReflection(t, getters);
                    if (val != null) { row.put("workOrder", val); return; }
                    // If Task does not expose a workOrder property, try to use its first related Correspondence id
                    try {
                        if (t.getCorrespondenceTasks() != null && !t.getCorrespondenceTasks().isEmpty()) {
                            for (Object o : t.getCorrespondenceTasks()) {
                                try {
                                    com.smat.ins.model.entity.CorrespondenceTask ct = (com.smat.ins.model.entity.CorrespondenceTask) o;
                                    if (ct != null && ct.getCorrespondence() != null && ct.getCorrespondence().getId() != null) {
                                        // Use correspondence id as the displayed "workOrder" (matches TaskManagementBean behavior)
                                        row.put("workOrder", String.valueOf(ct.getCorrespondence().getId()));
                                        return;
                                    }
                                } catch (Exception ignoreCt) {
                                    // continue to next
                                }
                            }
                        }
                    } catch (Exception ignoreAll) {}
                }
            }
        } catch (Exception ignore) {}

        // try EquipmentInspectionForm
        try {
            if (taskId != null) {
                EquipmentInspectionForm form = null;
                try { form = equipmentInspectionFormService.getBy(taskId); } catch (Exception ignore) {}
                if (form != null) {
                    String val = extractStringUsingReflection(form, getters);
                    if (val != null) { row.put("workOrder", val); return; }
                }
            }
        } catch (Exception ignore) {}

        // try EmpCertification
        try {
            if (taskId != null) {
                EmpCertification cert = null;
                try { cert = empCertificationService.getBy(taskId); } catch (Exception ignore) {}
                if (cert != null) {
                    String val = extractStringUsingReflection(cert, getters);
                    if (val != null) { row.put("workOrder", val); return; }
                }
            }
        } catch (Exception ignore) {}

        // final fallback
        row.put("workOrder", "No Work Order");
    }






    /**
     * Try to extract organization id from a result row map.
     * Looks for numeric keys or Organization instance under common keys.
     */
    @SuppressWarnings("unchecked")
    private Long extractOrganizationIdFromRow(Map<String, Object> row) {
        if (row == null) return null;
        // شيفرة مفاتيح شائعة
        String[] idKeys = {"organizationId", "organization_id", "orgId", "org_id", "organizationIdLong", "organization", "org"};
        for (String k : idKeys) {
            try {
                Object v = row.get(k);
                if (v == null) continue;
                if (v instanceof Organization) {
                    Organization o = (Organization) v;
                    if (o.getId() != null) return o.getId().longValue();
                }
                if (v instanceof Number) {
                    return ((Number) v).longValue();
                }
                String s = String.valueOf(v).trim();
                if (s.matches("^\\d+$")) {
                    return Long.valueOf(s);
                }
            } catch (Exception ignore) {}
        }

        // فحص احتياطي: أي حقل اسمه يحتوي "org" أو "organization"
        for (Map.Entry<String, Object> e : row.entrySet()) {
            try {
                String key = e.getKey() == null ? "" : e.getKey().toLowerCase();
                if (key.contains("org") || key.contains("organization") || key.contains("org_id")) {
                    Object v = e.getValue();
                    if (v instanceof Number) return ((Number) v).longValue();
                    String s = String.valueOf(v).trim();
                    if (s.matches("^\\d+$")) return Long.valueOf(s);
                }
            } catch (Exception ignore) {}
        }

        return null;
    }




    public String getReviewerTask(Integer taskId) {
        try {
            if (taskId == null) return "Not assigned";

            Task task = taskService.findById(taskId);
            if (task == null) return "Not assigned";

            // Equipment inspection path
            if (task.getEquipmentCategory() != null) {
                EquipmentInspectionForm form = equipmentInspectionFormService.getBy(taskId);
                if (form != null && form.getSysUserByReviewedBy() != null) {
                    String name = form.getSysUserByReviewedBy().getDisplayName();
                    return (name != null && !name.trim().isEmpty()) ? name : "Not assigned";
                }
                return "Not assigned";
            }

            // Employee certification path
            EmpCertification cert = empCertificationService.getBy(taskId);
            if (cert != null && cert.getSysUserByReviewedBy() != null) {
                String name = cert.getSysUserByReviewedBy().getDisplayName();
                return (name != null && !name.trim().isEmpty()) ? name : "Not assigned";
            }

            return "Not assigned";
        } catch (Exception e) {
            e.printStackTrace();
            return "Not assigned";
        }
    }

    public String getInspectorTask(Integer taskId) {
        try {
            if (taskId == null) return "Not assigned";

            Task task = taskService.findById(taskId);
            if (task == null) return "Not assigned";

            // Equipment inspection path
            if (task.getEquipmentCategory() != null) {
                EquipmentInspectionForm form = equipmentInspectionFormService.getBy(taskId);
                if (form != null && form.getSysUserByInspectionBy() != null) {
                    String name = form.getSysUserByInspectionBy().getDisplayName();
                    return (name != null && !name.trim().isEmpty()) ? name : "Not assigned";
                }
                return "Not assigned";
            }

            // Employee certification path
            EmpCertification cert = empCertificationService.getBy(taskId);
            if (cert != null && cert.getSysUserByInspectedBy() != null) {
                String name = cert.getSysUserByInspectedBy().getDisplayName();
                return (name != null && !name.trim().isEmpty()) ? name : "Not assigned";
            }

            return "Not assigned";
        } catch (Exception e) {
            // log and return safe fallback
            e.printStackTrace();
            return "Not assigned";
        }
    }



    public void doPrint(Integer certId) {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            EmpCertification empCertification = empCertificationService.findBy(certId);
            if (empCertification == null) {
                UtilityHelper.addErrorMessage("Certificate data is not available for id: " + certId);
                return;
            }
            Employee employee = empCertification.getEmployee();
            if (employee == null) {
                UtilityHelper.addErrorMessage("Employee data is not available for this certificate.");
                return;
            }

            ExternalContext externalContext = fc.getExternalContext();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CertNumber", safeString(empCertification.getCertNumber()));
            parameters.put("TsNumber", empCertification.getTsNumber());
            parameters.put("IssueDate", empCertification.getIssueDate());
            parameters.put("ExpiryDate", empCertification.getExpiryDate());
            parameters.put("EmployeeName", safeString(employee.getFullName()));
            parameters.put("CompanyName", employee.getCompany() != null ? safeString(employee.getCompany().getName()) : "");
            parameters.put("CertType", empCertification.getEmpCertificationType() != null ? safeString(empCertification.getEmpCertificationType().getCertName()) : "");
            parameters.put("EmployeeId", safeString(employee.getIdNumber()));

            // صورة الموظف
            if (employee.getEmployeePhoto() != null) {
                parameters.put("EmployeePhoto", new ByteArrayInputStream(employee.getEmployeePhoto()));
            } else {
                parameters.put("EmployeePhoto", null);
            }

            // تمرير مسارات اللوغو (String) كما هو متوقع في jrxml
            String logoLeftAbsPath = externalContext.getRealPath("/views/jasper/images/logo-left.png");
            String logoRightAbsPath = externalContext.getRealPath("/views/jasper/images/logo-right.png");
            parameters.put("logoLeftPath", logoLeftAbsPath);
            parameters.put("logoRightPath", logoRightAbsPath);

            // QR code
            String qrCodeData = UtilityHelper.getBaseURL() + "api/emp-cert/" + safeString(empCertification.getCertNumber()) + "&" + safeString(empCertification.getTsNumber());
            byte[] qrCodeImage = QRCodeGenerator.generateQrCodeImage(qrCodeData, 100, 100);
            if (qrCodeImage != null) {
                parameters.put("QRCodeImage", new ByteArrayInputStream(qrCodeImage));
            }

            String jasperPath = getServletContextPath("views/jasper/emp-certification.jasper");
            if (jasperPath == null) {
                UtilityHelper.addErrorMessage("Report template not found.");
                return;
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, new JREmptyDataSource());

            // تصدير PDF
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            response.reset();
            response.setContentType("application/pdf");
            String filename = "EmployeeCertification_" + (empCertification.getCertNumber() != null ? empCertification.getCertNumber() : certId) + ".pdf";
            response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
            response.setContentLength(pdfBytes.length);

            try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
                servletOutputStream.write(pdfBytes);
                servletOutputStream.flush();
            }

            fc.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Failed to generate report: " + e.getMessage());
        }
    }

    /**
     * Download all attachments for an equipment inspection task as a ZIP.
     * This will try to reuse InspectionFormBean.prepareAttachmentsZipDownload()
     * to populate any expected session attributes then open the /attachments/zip URL.
     */
    public void downloadAttachmentsForEquipment(Integer taskId, Object reportNoObj) {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            EquipmentInspectionForm form = null;
            if (taskId != null) {
                try { form = equipmentInspectionFormService.getBy(taskId); } catch (Exception ignore) { }
            }

            // Try to reuse InspectionFormBean to prepare session attributes (best-effort)
            try {
                com.smat.ins.view.bean.InspectionFormBean inspectionFormBean = (com.smat.ins.view.bean.InspectionFormBean) BeanUtility.getBean("inspectionFormBean");
                if (inspectionFormBean != null) {
                    if (form != null) inspectionFormBean.setEquipmentInspectionForm(form);
                    try { inspectionFormBean.prepareAttachmentsZipDownload(); } catch (Exception ignore) {}
                }
            } catch (Exception ignore) {}

            // determine folder name: prefer provided reportNo parameter, else use form data
            String folderName = null;
            if (reportNoObj != null && String.valueOf(reportNoObj).trim().length() > 0) {
                folderName = String.valueOf(reportNoObj).trim();
            } else if (form != null) {
                if (form.getReportNo() != null && !form.getReportNo().trim().isEmpty()) folderName = form.getReportNo().trim();
                else if (form.getId() != null) folderName = "form_" + form.getId();
            }

            if (folderName != null) {
                String ctx = fc.getExternalContext().getRequestContextPath();
                String url = ctx + "/attachments/zip?type=ins&reportNo=" + URLEncoder.encode(folderName, "UTF-8");
                PrimeFaces.current().executeScript("window.open('" + url + "', '_blank');");
            } else {
                UtilityHelper.addErrorMessage("No report/folder available for attachments download.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Failed to prepare attachments download: " + e.getMessage());
        }
    }

    /**
     * Download all attachments for an employee certification task as a ZIP.
     * Reuses EmpCertificationBean.prepareAttachmentsZipDownload() when possible.
     */
    public void downloadAttachmentsForEmployee(Integer certId, Object certNumberObj) {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            EmpCertification cert = null;
            if (certId != null) {
                try { cert = empCertificationService.getBy(certId); } catch (Exception ignore) { }
            }

            // Try to reuse EmpCertificationBean to prepare session attributes (best-effort)
            try {
                com.smat.ins.view.bean.EmpCertificationBean empBean = (com.smat.ins.view.bean.EmpCertificationBean) BeanUtility.getBean("empCertificationBean");
                if (empBean != null) {
                    if (cert != null) {
                        empBean.setEmpCertification(cert);
                        if (cert.getEmployee() != null) empBean.setEmployee(cert.getEmployee());
                    }
                    try { empBean.prepareAttachmentsZipDownload(); } catch (Exception ignore) {}
                }
            } catch (Exception ignore) {}

            String folderName = null;
            if (certNumberObj != null && String.valueOf(certNumberObj).trim().length() > 0) {
                folderName = String.valueOf(certNumberObj).trim();
            } else if (cert != null) {
                if (cert.getCertNumber() != null && !cert.getCertNumber().trim().isEmpty()) folderName = cert.getCertNumber().trim();
                else if (cert.getId() != null) folderName = "emp_" + cert.getId();
                else if (cert.getEmployee() != null && cert.getEmployee().getId() != null) folderName = "emp_" + cert.getEmployee().getId();
            }

            if (folderName != null) {
                String ctx = fc.getExternalContext().getRequestContextPath();
                String url = ctx + "/attachments/zip?type=emp&certNo=" + URLEncoder.encode(folderName, "UTF-8");
                PrimeFaces.current().executeScript("window.open('" + url + "', '_blank');");
            } else {
                UtilityHelper.addErrorMessage("No certificate/folder available for attachments download.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Failed to prepare attachments download: " + e.getMessage());
        }
    }



    // helper (إذا ليس موجود مسبقاً في هذا الكلاس)
    private String safeString(Object o) {
        return o == null ? "" : String.valueOf(o);
    }


    private String getServletContextPath(String relativePath) {
        return ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(relativePath);
    }

    private void exportReport(JasperPrint jasperPrint, String reportName) throws IOException, JRException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline; filename=" + reportName + "_" + System.currentTimeMillis() + ".pdf");
            JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Try to translate a value (SMAT id, username, or numeric id) into a display name.
     * Accepts any Object because the SQL result map may contain different value types.
     */
    public String getDisplayNameBySmatId(Object smatObj) {
        if (smatObj == null) return "-";
        String smatId = String.valueOf(smatObj).trim();
        if (smatId.isEmpty()) return "-";

        // 1) direct lookup in the prebuilt map
        String display = smatToDisplayName.get(smatId);
        if (display != null && !display.trim().isEmpty()) {
            return display;
        }

        // 2) try normalized keys (uppercase, trimmed)
        display = smatToDisplayName.get(smatId.toUpperCase());
        if (display != null && !display.trim().isEmpty()) {
            return display;
        }

        // 3) if it's purely numeric, try fetching SysUser by id
        if (smatId.matches("^\\d+$")) {
            try {
                Long id = Long.valueOf(smatId);
                SysUser u = sysUserService.findById(id);
                if (u != null) {
                    // Prefer enDisplayName, then first+last, then username
                    if (u.getEnDisplayName() != null && !u.getEnDisplayName().trim().isEmpty()) {
                        return u.getEnDisplayName();
                    }
                    String fn = u.getFirstName() != null ? u.getFirstName() : "";
                    String ln = u.getLastName() != null ? u.getLastName() : "";
                    if (!(fn + ln).trim().isEmpty()) {
                        return (fn + " " + ln).trim();
                    }
                    if (u.getUserName() != null && !u.getUserName().trim().isEmpty()) {
                        return u.getUserName();
                    }
                    return String.valueOf(u.getId());
                }
            } catch (Exception e) {
                // ignore and fallback to returning original smatId
            }
        }

        // 4) fallback - return original identifier
        return smatId;
    }

    /**
     * Clear all search criteria and results
     */
    public void clearSearch() {
        // إعادة تهيئة المعايير
        taskType = "";
        fromDate = null;
        toDate = null;
        company = null;
        equipmentCategory = null;
        certNumberSearch = null;
        organizationSearch = null;    // optional keep
        organizationId = null;        // NEW: reset id
        searchResults.clear();
        statistics = null;
        searchPerformed = false;

        searchCompletedTasks();
        addInfoMessage("Search criteria cleared");
    }




    /**
     * Format completed date for display: returns "-" when null/empty/"null",
     * otherwise returns formatted date "dd/MM/yyyy" or string representation.
     */
    public String formatCompletedDate(Object dateObj) {
        if (dateObj == null) return "-";
        String s = dateObj.toString();
        if (s.trim().isEmpty() || "null".equalsIgnoreCase(s.trim())) return "-";
        try {
            if (dateObj instanceof java.util.Date) {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                return df.format((java.util.Date) dateObj);
            }
            if (dateObj instanceof Number) {
                long ts = ((Number) dateObj).longValue();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                return df.format(new java.util.Date(ts));
            }
            try {
                long possibleMillis = Long.parseLong(s);
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                return df.format(new java.util.Date(possibleMillis));
            } catch (NumberFormatException nfe) { }
            if (s.length() >= 10) {
                String first10 = s.substring(0, 10);
                if (first10.matches("\\d{4}[-/]\\d{2}[-/]\\d{2}")) {
                    String[] parts = first10.split("[-/]");
                    return parts[2] + "/" + parts[1] + "/" + parts[0];
                }
            }
            return s.trim();
        } catch (Exception e) {
            return "-";
        }
    }



    /**
     * Export search results to Excel (placeholder method)
     */
    public void exportToExcel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            if (searchResults == null || searchResults.isEmpty()) {
                addWarningMessage("No data to export. Please perform a search first.");
                return;
            }

            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Completed Tasks");

            String[] headers = {"Task ID", "Task Type", "Description", "Company", "Sticker No","Cert Number", "Created Date", "Completed Date", "Inspector", "Reviewer"};
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);

            // Style للهيدر (خلفية رمادية + Bold)
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Map<String, Object> rowMap : searchResults) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(safeString(rowMap.get("taskId")));
                row.createCell(1).setCellValue(safeString(rowMap.get("taskType")));
                row.createCell(2).setCellValue(safeString(rowMap.get("taskDescription")));
                row.createCell(3).setCellValue(safeString(rowMap.get("companyName")));
                row.createCell(4).setCellValue(safeString(rowMap.get("stickerNo")));
                row.createCell(5).setCellValue(safeString(rowMap.get("certNumber")));
                row.createCell(6).setCellValue(safeString(rowMap.get("createdDate")));
                row.createCell(7).setCellValue(safeString(rowMap.get("completedDate")));
                row.createCell(8).setCellValue(safeString(rowMap.get("inspectorName")));
                row.createCell(9).setCellValue(safeString(rowMap.get("reviewerName")));
            }

            // === ضبط عرض الأعمدة أوتوماتيكياً ===
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"completed_tasks.xlsx\"");

            try (ServletOutputStream out = response.getOutputStream()) {
                workbook.write(out);
                out.flush();
            }
            workbook.close();

            facesContext.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage("Error exporting to Excel: " + e.getMessage());
        }
    }



    /**
     * Get CSS class for task type badge
     */
    public String getTaskTypeCssClass(String taskType) {
        if ("Equipment Inspection".equals(taskType)) {
            return "badge badge-primary";
        } else if ("Employee Certification".equals(taskType)) {
            return "badge badge-success";
        } else {
            return "badge badge-secondary";
        }
    }

    /**
     * Get formatted display text for task type
     */
    public String getTaskTypeDisplayText(String taskType) {
        if ("Equipment Inspection".equals(taskType)) {
            return "Equipment";
        } else if ("Employee Certification".equals(taskType)) {
            return "Employee";
        } else {
            return taskType;
        }
    }
    /** returns first non-null argument or null */
    private Object firstNonNull(Object... vals) {
        for (Object v : vals) if (v != null) return v;
        return null;
    }

    /**
     * Ensure row contains normalized keys jobNo and timeSheetNo (and some report/sticker fields).
     * If not present, try to load EquipmentInspectionForm by taskId and fill values from it.
     */
    @SuppressWarnings("unchecked")
    private void populateJobAndTimesheet(Map<String,Object> row, Integer taskId) {
        if (row == null) return;

        // try common key names already present in the row
        Object job = firstNonNull(
                row.get("jobNo"),
                row.get("job_number"),
                row.get("job_no"),
                row.get("jobNum"),
                row.get("jobNumber"),
                row.get("JobNo")
        );

        Object ts = firstNonNull(
                row.get("timeSheetNo"),
                row.get("time_sheet_no"),
                row.get("time_sheet"),
                row.get("timesheet"),
                row.get("timeSheet"),
                row.get("tsNumber"),
                row.get("ts_no"),
                row.get("ts")
        );

        // if missing, try to fetch from EquipmentInspectionForm (only if we have a taskId)
        if ((job == null || ts == null) && taskId != null) {
            try {
                EquipmentInspectionForm form = equipmentInspectionFormService.getBy(taskId);
                if (form != null) {
                    if (job == null) {
                        if (form.getJobNo() != null && !form.getJobNo().trim().isEmpty()) job = form.getJobNo();
                    }
                    if (ts == null) {
                        if (form.getTimeSheetNo() != null && !form.getTimeSheetNo().trim().isEmpty()) ts = form.getTimeSheetNo();
                    }
                    // also populate reportNo / stickerNo if missing in row
                    if ((row.get("reportNo") == null || String.valueOf(row.get("reportNo")).trim().isEmpty()) && form.getReportNo() != null) {
                        row.put("reportNo", form.getReportNo());
                    }
                    if ((row.get("stickerNo") == null || String.valueOf(row.get("stickerNo")).trim().isEmpty()) && form.getStickerNo() != null) {
                        row.put("stickerNo", form.getStickerNo());
                    }
                }
            } catch (Exception e) {
                // لا تُقاطع التنفيذ، فقط سجل لو تحب
                e.printStackTrace();
            }
        }

        // Put normalized keys (use "-" as fallback display if still null)
        row.put("jobNo", job != null ? job : row.getOrDefault("jobNo", "-"));
        row.put("timeSheetNo", ts != null ? ts : row.getOrDefault("timeSheetNo", "-"));
    }


    /**
     * Check if equipment category filter should be shown
     */
    public boolean isEquipmentCategoryFilterVisible() {
        return "equipment".equals(taskType) || taskType == null || taskType.isEmpty();
    }

    // Utility methods for messages
    private void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
    }

    private void addWarningMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", message));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    // Getters and Setters
    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
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

    public List<Map<String, Object>> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Map<String, Object>> searchResults) {
        this.searchResults = searchResults;
    }

    public Map<String, Object> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Object> statistics) {
        this.statistics = statistics;
    }

    public boolean isSearchPerformed() {
        return searchPerformed;
    }

    public void setSearchPerformed(boolean searchPerformed) {
        this.searchPerformed = searchPerformed;
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
    // new: organization id selected from ui (we keep organizationList as before)
    private Long organizationId;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }


    // DEBUG helpers
    public boolean isDebugSearchPerformed() {
        return this.searchPerformed;
    }

    public int getDebugResultsSize() {
        return (this.searchResults == null) ? 0 : this.searchResults.size();
    }

    // Getters and Setters for organization
    public Organization getOrganizationSearch() {
        return organizationSearch;
    }

    public void setOrganizationSearch(Organization organizationSearch) {
        this.organizationSearch = organizationSearch;
    }

    public List<Organization> getOrganizationList() {
        return organizationList;
    }

    public void setOrganizationList(List<Organization> organizationList) {
        this.organizationList = organizationList;
    }
}