package com.smat.ins.view.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import com.smat.ins.model.entity.Task;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.service.TaskService;
import com.smat.ins.model.service.UserAliasService;
import com.smat.ins.util.BeanUtility;
//new imports for export functionality
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
// Java / IO / util
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// JSF / Servlet
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

// Apache POI (SS + XSSF)
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.FillPatternType;

// ZXing (QR generation)
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

// Your project classes (تعديل الحزمة إذا كانت مختلفة في مشروعك)
import com.smat.ins.model.entity.Sticker;
import com.smat.ins.util.UtilityHelper;

// Optional: logging (إذا تستخدم logger في الـbean)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Named
@ViewScoped
public class TaskManagementBean implements Serializable {

    private static final long serialVersionUID = 7045989318270076532L;

    private List<Task> tasks;
    private List<Task> activeTasks; // قائمة المهام النشطة فقط
    private List<Task> deactivatedTasks; // قائمة المهام المعطلة فقط
    private String sortBy = "id";
    private boolean showOnlyDelayedTasks = false;
    private boolean showDeactivatedTasks = false; // لعرض المهام المعطلة بدلاً من النشطة
    // filter by correspondence id (optional)
    private Long correspondenceIdFilter;

    @Inject
    private LoginBean loginBean;
    private TaskService taskService;
    private UserAliasService userAliasService;

    public TaskManagementBean() {
        taskService = (TaskService) BeanUtility.getBean("taskService");
        userAliasService = (UserAliasService) BeanUtility.getBean("userAliasService");
        tasks = new ArrayList<>();
        activeTasks = new ArrayList<>();
        deactivatedTasks = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        try {

            System.out.println("=== TASK MANAGEMENT BEAN INIT ===");
            this.sortBy = "id";
            loadAndSortTasks();
            System.out.println("Init completed successfully");
        } catch (Exception e) {
            System.out.println("Error in init: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAndSortTasks() throws Exception {
        loadTasks();
        filterActiveTasks(); // تصفية المهام النشطة
        filterDeactivatedTasks(); // تصفية المهام المعطلة
        sortTasks();
        if (showOnlyDelayedTasks && !showDeactivatedTasks) {
            filterDelayedTasks();
        }
    }

    private void loadTasks() throws Exception {
        tasks.clear();
        System.out.println("=== START LOADING TASKS ===");
        System.out.println("Current user ID: " + loginBean.getUser().getId());
        System.out.println("User permissions - 010: " + loginBean.hasSysPermission("010"));
        System.out.println("User permissions - 011: " + loginBean.hasSysPermission("011"));

        List<UserAlias> myUserAliases = userAliasService.getBySysUser(loginBean.getUser());
        System.out.println("User aliases found: " + myUserAliases.size());

        for (UserAlias userAlias : myUserAliases) {
            System.out.println("Processing alias ID: " + userAlias.getId());

            if (loginBean.hasSysPermission("010")) {
                List<Task> initialTasks;
                List<Task> initialEmpTasks;
                if (correspondenceIdFilter != null) {
                    // use the correspondence-scoped retrieval
                    initialTasks = taskService.getListInitialTaskByCorrespondence(correspondenceIdFilter, userAlias.getId());
                    initialEmpTasks = taskService.getListInitialEmpTaskByCorrespondence(correspondenceIdFilter, userAlias.getId());
                } else {
                    initialTasks = taskService.getListIntialTaskByRecepient(userAlias.getId());
                    initialEmpTasks = taskService.getListIntialEmpTaskByRecepient(userAlias.getId());
                }

                System.out.println("Initial tasks: " + initialTasks.size());
                System.out.println("Initial emp tasks: " + initialEmpTasks.size());

                // تحقق من حالة is_active لكل مهمة
                for (Task task : initialTasks) {
                    System.out.println("Initial Task ID: " + task.getId() + ", Active: " + task.getIs_active());
                }
                for (Task task : initialEmpTasks) {
                    System.out.println("Initial Emp Task ID: " + task.getId() + ", Active: " + task.getIs_active());
                }

                tasks.addAll(initialTasks);
                tasks.addAll(initialEmpTasks);
            }

            if (loginBean.hasSysPermission("011")) {
                List<Task> reviewedTasks;
                List<Task> reviewedEmpTasks;
                if (correspondenceIdFilter != null) {
                    reviewedTasks = taskService.getListReviewdTaskByCorrespondence(correspondenceIdFilter, loginBean.getUser().getId());
                    reviewedEmpTasks = taskService.getListReviewdEmpTaskByCorrespondence(correspondenceIdFilter, loginBean.getUser().getId());
                } else {
                    reviewedTasks = taskService.getListReviewedTask(loginBean.getUser().getId());
                    reviewedEmpTasks = taskService.getListReviewedEmpTask(loginBean.getUser().getId());
                }

                System.out.println("Reviewed tasks: " + reviewedTasks.size());
                System.out.println("Reviewed emp tasks: " + reviewedEmpTasks.size());

                for (Task task : reviewedTasks) {
                    System.out.println("Reviewed Task ID: " + task.getId() + ", Active: " + task.getIs_active());
                }
                for (Task task : reviewedEmpTasks) {
                    System.out.println("Reviewed Emp Task ID: " + task.getId() + ", Active: " + task.getIs_active());
                }

                tasks.addAll(reviewedTasks);
                tasks.addAll(reviewedEmpTasks);
            }
        }

        System.out.println("Total tasks loaded: " + tasks.size());
        System.out.println("=== END LOADING TASKS ===");
    }

    // apply correspondence filter and reload tasks
    public void applyCorrespondenceFilter() {
        try {
            System.out.println("Applying correspondence filter: " + correspondenceIdFilter);
            loadAndSortTasks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear the correspondence filter and reload tasks
     */
    public void clearCorrespondenceFilter() {
        this.correspondenceIdFilter = null;
        try {
            loadAndSortTasks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * تصفية المهام النشطة فقط (isActive = true)
     */
    private void filterActiveTasks() {
        System.out.println("=== FILTERING ACTIVE TASKS ===");
        System.out.println("Total tasks before filter: " + tasks.size());

        activeTasks = tasks.stream()
                .filter(task -> {
                    boolean isActive = task.getIs_active() != null && task.getIs_active();
                    System.out.println("Task ID: " + task.getId() + ", isActive: " + isActive);
                    return isActive;
                })
                .collect(Collectors.toList());

        System.out.println("Active tasks after filter: " + activeTasks.size());
        System.out.println("=== END FILTERING ===");
    }

    /**
     * تصفية المهام المعطلة فقط (isActive = false)
     */
    private void filterDeactivatedTasks() {
        System.out.println("=== FILTERING DEACTIVATED TASKS ===");
        System.out.println("Total tasks before filter: " + tasks.size());

        deactivatedTasks = tasks.stream()
                .filter(task -> {
                    boolean isDeactivated = task.getIs_active() != null && !task.getIs_active();
                    System.out.println("Task ID: " + task.getId() + ", isDeactivated: " + isDeactivated);
                    return isDeactivated;
                })
                .collect(Collectors.toList());

        System.out.println("Deactivated tasks after filter: " + deactivatedTasks.size());
        System.out.println("=== END FILTERING ===");
    }

    public void sortTasks() {
        List<Task> tasksToSort = showDeactivatedTasks ? deactivatedTasks : activeTasks;
        if (tasksToSort == null || tasksToSort.isEmpty()) return;

        switch (sortBy) {
            case "id":
                tasksToSort.sort(Comparator.comparingInt(Task::getId).reversed());
                break;
            case "correspondence":
                // sort by first associated correspondence id (descending). Nulls last.
                tasksToSort.sort(Comparator.comparing((Task t) -> getFirstCorrespondenceId(t),
                        Comparator.nullsLast(Long::compareTo)).reversed());
                break;
            case "date":
                tasksToSort.sort(Comparator.comparing(Task::getCreatedDate).reversed());
                break;
            case "assigner":
                tasksToSort.sort(Comparator.comparing(
                        task -> task.getUserAliasByAssigner().getSysUserBySysUser().getFullName()));
                break;
            case "type":
                tasksToSort.sort(Comparator.comparing(
                        task -> task.getServiceType().getName()));
                break;
            case "priority":
                tasksToSort.sort(Comparator
                        .comparingInt((Task task) -> calculateTaskDelay(task.getCreatedDate()))
                        .reversed()
                        .thenComparing(Task::getCreatedDate).reversed());
                break;
        }
    }

    public void filterDelayedTasks() {
        if (activeTasks == null) return;
        activeTasks.removeIf(task -> calculateTaskDelay(task.getCreatedDate()) <= 0);
    }

    /**
     * Return the id of the first linked Correspondence for a Task, or null if none.
     */
    private Long getFirstCorrespondenceId(Task t) {
        if (t == null) return null;
        try {
            if (t.getCorrespondenceTasks() != null && !t.getCorrespondenceTasks().isEmpty()) {
                // correspondenceTasks may be a Set - pick first entry
                for (Object o : t.getCorrespondenceTasks()) {
                    try {
                        com.smat.ins.model.entity.CorrespondenceTask ct = (com.smat.ins.model.entity.CorrespondenceTask) o;
                        if (ct != null && ct.getCorrespondence() != null && ct.getCorrespondence().getId() != null) {
                            return ct.getCorrespondence().getId();
                        }
                    } catch (Exception ignore) {
                        // continue
                    }
                }
            }
        } catch (Exception e) {
            // ignore lazy-loading issues here
        }
        return null;
    }

    /**
     * Calculates the delay in days for a task
     * @param taskDate The creation date of the task
     * @return Number of days delayed (positive if delayed, 0 if on time, negative if still has time)
     */
    public int calculateTaskDelay(Date taskDate) {
        if (taskDate == null) return 0;

        LocalDate createdDate = taskDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate currentDate = LocalDate.now();

        return (int) ChronoUnit.DAYS.between(createdDate, currentDate);
    }

    /**
     * NEW METHOD: Calculates progress percentage based on timestamp
     * @param timestamp The timestamp to calculate progress for
     * @return Progress percentage as string (e.g. "75%")
     */
    public String getProgressPercentage(Timestamp timestamp) {
        if (timestamp == null) return "0%";

        Date taskDate = new Date(timestamp.getTime());
        int delayDays = calculateTaskDelay(taskDate);

        // Example calculation - adjust according to your business logic
        int progress = 100 - Math.min(Math.max(delayDays, 0), 100);
        return progress + "%";
    }

    /**
     * Determines the severity class based on delay days
     * @param taskDate The creation date of the task
     * @return CSS class name for the delay indicator
     */
    public String getDelaySeverity(Date taskDate) {
        int delayDays = calculateTaskDelay(taskDate);

        if (delayDays <= 0) {
            return "delay-normal";
        } else if (delayDays <= 2) {
            return "delay-warning";
        } else if (delayDays <= 5) {
            return "delay-high";
        } else {
            return "delay-critical";
        }
    }

    /**
     * Gets a human-readable delay description
     * @param taskDate The creation date of the task
     * @return String describing the delay status
     */
    public String getDelayDescription(Date taskDate) {
        int delayDays = calculateTaskDelay(taskDate);

        if (delayDays < 0) {
            return Math.abs(delayDays) + " day" + (Math.abs(delayDays) == 1 ? "" : "s") + " remaining";
        } else if (delayDays == 0) {
            return "Due today";
        } else {
            return delayDays + " day" + (delayDays == 1 ? "" : "s") + " overdue";
        }
    }

    /**
     * Formats the task date for display
     * @param taskDate The creation date of the task
     * @return Formatted date string
     */
    public String getFormattedTaskDate(Date taskDate) {
        if (taskDate == null) return "";
        return new java.text.SimpleDateFormat("dd MMM yyyy").format(taskDate);
    }

    // Methods for showing/hiding deactivated tasks
    public void showDeactivatedTasks() {
        this.showDeactivatedTasks = true;
        try {
            sortTasks(); // إعادة ترتيب المهام المعطلة
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideDeactivatedTasks() {
        this.showDeactivatedTasks = false;
        try {
            sortTasks(); // إعادة ترتيب المهام النشطة
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTaskStatus(int taskId, int status) {
        try {
            taskService.updateTaskStatus(taskId, status);
            init(); // إعادة تحميل المهام
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Task status updated successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update task status: " + e.getMessage()));
        }
    }
    public void exportToExcel() {
        List<Task> exportList = showDeactivatedTasks ? deactivatedTasks : activeTasks;
        try {
            byte[] data = buildExcel(exportList);
            sendResponse(data,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "tasks_export_" + timeStampForFile() + ".xlsx");
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Export Error", "Failed to export Excel: " + e.getMessage()));
        }
    }


    private byte[] buildExcel(List<Task> tasksList) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Tasks");
            int rownum = 0;

            // Header style
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            // Date style
            CellStyle dateStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            short df = createHelper.createDataFormat().getFormat("yyyy-MM-dd");
            dateStyle.setDataFormat(df);

            // Wrap style for description
            CellStyle wrapStyle = wb.createCellStyle();
            wrapStyle.setWrapText(true);

            // Define columns to match template fields (NOTE: "Work Order" replaces "Correspondence ID")
            String[] cols = new String[] {
                    "Task ID",              // 0
                    "Work Order",           // 1 (uses getFirstCorrespondenceId(t) as before)
                    "Created Date",         // 2 (date cell)
                    "Assigner",             // 3
                    "Company",              // 4
                    "Service Type",         // 5
                    "Equipment Category",   // 6 (nullable)
                    "Description",          // 7 (wrapped)
                    "Status",               // 8 (Active/Deactivated)
                    "Delay Days",           // 9 (int)
                    "Progress (%)"          // 10 (int)
            };

            // Header row
            Row header = sheet.createRow(rownum++);
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }

            if (tasksList != null) {
                for (Task t : tasksList) {
                    Row r = sheet.createRow(rownum++);
                    int c = 0;

                    // Task ID
                    if (t.getId() != null) {
                        r.createCell(c++).setCellValue(t.getId());
                    } else {
                        r.createCell(c++).setCellValue("");
                    }

                    // Work Order (we keep the same retrieval method as before)
                    Long corrId = getFirstCorrespondenceId(t);
                    if (corrId != null) {
                        r.createCell(c++).setCellValue(corrId);
                    } else {
                        r.createCell(c++).setCellValue("No Correspondence");
                    }

                    // Created Date (date cell if available)
                    Cell dateCell = r.createCell(c++);
                    if (t.getCreatedDate() != null) {
                        dateCell.setCellValue(t.getCreatedDate());
                        dateCell.setCellStyle(dateStyle);
                    } else {
                        dateCell.setCellValue("");
                    }

                    // Assigner
                    String assigner = safeGetAssignerName(t);
                    r.createCell(c++).setCellValue(assigner);

                    // Company
                    String companyName = "";
                    try {
                        companyName = t.getCompany() != null ? safe(t.getCompany().getName()) : "";
                    } catch (Exception ex) {
                        companyName = "";
                    }
                    r.createCell(c++).setCellValue(companyName);

                    // Service Type
                    String serviceTypeName = "";
                    try {
                        serviceTypeName = t.getServiceType() != null ? safe(t.getServiceType().getName()) : "";
                    } catch (Exception ex) {
                        serviceTypeName = "";
                    }
                    r.createCell(c++).setCellValue(serviceTypeName);

                    // Equipment Category presence / name
                    String equipmentCategoryName = "";
                    try {
                        if (t.getEquipmentCategory() != null) {
                            equipmentCategoryName = safe(t.getEquipmentCategory().getName() != null ? t.getEquipmentCategory().getName() : "Equipment Inspection");
                        } else {
                            equipmentCategoryName = "";
                        }
                    } catch (Exception ex) {
                        equipmentCategoryName = "";
                    }
                    r.createCell(c++).setCellValue(equipmentCategoryName);

                    // Description (wrap text)
                    Cell descCell = r.createCell(c++);
                    descCell.setCellValue(safe(t.getTaskDescription()));
                    descCell.setCellStyle(wrapStyle);

                    // Status (active / deactivated)
                    boolean active = t.getIs_active() != null && t.getIs_active();
                    r.createCell(c++).setCellValue(active ? "Active" : "Deactivated");

                    // Delay Days (calculateTaskDelay uses Date)
                    int delayDays = 0;
                    try {
                        delayDays = calculateTaskDelay(t.getCreatedDate());
                    } catch (Exception ex) {
                        delayDays = 0;
                    }
                    r.createCell(c++).setCellValue(delayDays);

                    // Progress percentage (numeric)
                    int progressPercent = 0;
                    try {
                        int clamped = Math.min(Math.max(delayDays, 0), 100);
                        progressPercent = 100 - clamped;
                    } catch (Exception ex) {
                        progressPercent = 0;
                    }
                    r.createCell(c++).setCellValue(progressPercent);
                }
            }

            // Autosize columns (reasonable limit)
            for (int i = 0; i < cols.length; i++) {
                try {
                    sheet.autoSizeColumn(i);
                    int width = sheet.getColumnWidth(i);
                    int maxWidth = 256 * 60; // ~60 characters max
                    if (width > maxWidth) sheet.setColumnWidth(i, maxWidth);
                } catch (Exception ex) {
                    // ignore autosize errors
                }
            }

            wb.write(out);
            return out.toByteArray();
        }
    }


    private void sendResponse(byte[] bytes, String contentType, String filename) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset();
        ec.setResponseContentType(contentType);
        ec.setResponseHeader("Content-Length", String.valueOf(bytes.length));
        String encodedName = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        ec.setResponseHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedName);
        ec.getResponseOutputStream().write(bytes);
        fc.responseComplete();
    }

    private String safe(String s) {
        return s == null ? "" : s.replaceAll("\\r?\\n", " ").trim();
    }

    private String shorten(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    private String safeGetAssignerName(Task t) {
        try {
            if (t.getUserAliasByAssigner() != null && t.getUserAliasByAssigner().getSysUserBySysUser() != null) {
                return safe(t.getUserAliasByAssigner().getSysUserBySysUser().getFullName());
            }
        } catch (Exception e) { /* ignore */ }
        return "";
    }

    private String timeStampForFile() {
        return new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    // Getters and Setters
    public List<Task> getTasks() {
        return tasks;
    }

    public List<Task> getActiveTasks() {
        return activeTasks;
    }

    public List<Task> getDeactivatedTasks() {
        return deactivatedTasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        System.out.println("SET sortBy: " + sortBy + " - Thread: " + Thread.currentThread().getName());
        this.sortBy = sortBy;
    }

    public boolean isShowOnlyDelayedTasks() {
        return showOnlyDelayedTasks;
    }

    public void setShowOnlyDelayedTasks(boolean showOnlyDelayedTasks) {
        this.showOnlyDelayedTasks = showOnlyDelayedTasks;
    }

    public boolean isShowDeactivatedTasks() {
        return showDeactivatedTasks;
    }

    public void setShowDeactivatedTasks(boolean showDeactivatedTasks) {
        this.showDeactivatedTasks = showDeactivatedTasks;
    }

    public void toggleDelayedTasksFilter() {
        showOnlyDelayedTasks = !showOnlyDelayedTasks;
        try {
            loadAndSortTasks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleSortChange() {
        System.out.println("handleSortChange called - sortBy: " + sortBy);
        sortTasks();
    }

    // getter/setter for correspondence filter
    public Long getCorrespondenceIdFilter() {
        return correspondenceIdFilter;
    }

    public void setCorrespondenceIdFilter(Long correspondenceIdFilter) {
        this.correspondenceIdFilter = correspondenceIdFilter;
    }
    /**
     * Groups current visible tasks (active or deactivated based on showDeactivatedTasks)
     * by Correspondence ID. Returns a LinkedHashMap to preserve insertion order.
     * Keys are human-readable (e.g. "Correspondence #123" or "No Correspondence").
     */
    public Map<String, List<Task>> getGroupedTasksByCorrespondence() {
        List<Task> source = showDeactivatedTasks ? deactivatedTasks : activeTasks;
        if (source == null) return new LinkedHashMap<>();

        Map<Long, List<Task>> rawGroups = source.stream()
                .collect(Collectors.groupingBy(t -> getFirstCorrespondenceId(t), LinkedHashMap::new, Collectors.toList()));

        LinkedHashMap<String, List<Task>> result = new LinkedHashMap<>();
        List<Map.Entry<Long, List<Task>>> ordered = rawGroups.entrySet().stream()
                .sorted((e1, e2) -> {
                    if (e1.getKey() == null && e2.getKey() == null) return 0;
                    if (e1.getKey() == null) return 1;
                    if (e2.getKey() == null) return -1;
                    return e2.getKey().compareTo(e1.getKey());
                })
                .collect(Collectors.toList());

        for (Map.Entry<Long, List<Task>> e : ordered) {
            Long cid = e.getKey();
            // هنا نعرض الرقم فقط؛ للمهام بدون correspondence نترك تسمية "No Correspondence"
            String key = (cid == null) ? "No Correspondence" : String.valueOf(cid);
            result.put(key, e.getValue());
        }

        return result;
    }

} 