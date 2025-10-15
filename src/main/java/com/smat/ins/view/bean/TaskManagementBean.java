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
                List<Task> initialTasks = taskService.getListIntialTaskByRecepient(userAlias.getId());
                List<Task> initialEmpTasks = taskService.getListIntialEmpTaskByRecepient(userAlias.getId());
                
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
                List<Task> reviewedTasks = taskService.getListReviewedTask(loginBean.getUser().getId());
                List<Task> reviewedEmpTasks = taskService.getListReviewedEmpTask(loginBean.getUser().getId());
                
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

            Row header = sheet.createRow(rownum++);
            String[] cols = new String[] {"Task ID", "Date", "Assigner", "Company", "Type", "Description", "Status"};
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }

            if (tasksList != null) {
                for (Task t : tasksList) {
                    Row r = sheet.createRow(rownum++);
                    int c = 0;
                    // assume getId() returns int
                    r.createCell(c++).setCellValue(t.getId());
                    r.createCell(c++).setCellValue(getFormattedTaskDate(t.getCreatedDate()));
                    String assigner = safeGetAssignerName(t);
                    r.createCell(c++).setCellValue(assigner);
                    r.createCell(c++).setCellValue(t.getCompany() != null ? safe(t.getCompany().getName()) : "");
                    r.createCell(c++).setCellValue(t.getServiceType() != null ? safe(t.getServiceType().getName()) : "");
                    r.createCell(c++).setCellValue(safe(t.getTaskDescription()));
                    boolean active = t.getIs_active() != null && t.getIs_active();
                    r.createCell(c++).setCellValue(active ? "Active" : "Deactivated");
                }
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

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
} 