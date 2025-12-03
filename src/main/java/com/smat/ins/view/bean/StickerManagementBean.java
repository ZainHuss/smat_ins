package com.smat.ins.view.bean;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.smat.ins.model.entity.Sticker;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.service.StickerService;
import com.smat.ins.model.service.UserAliasService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.annotation.PostConstruct;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.faces.view.ViewScoped;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletResponse;
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



@Named("stickerManagementBean")
@ViewScoped
@Getter
@Setter
public class StickerManagementBean implements Serializable {
    private static final long serialVersionUID = 1039804782420560635L;
    private static final Logger log = LoggerFactory.getLogger(StickerManagementBean.class);

    private List<Sticker> stickers;
    private List<Sticker> filteredStickers;
    private Sticker sticker;
    private Integer generatedStickerNum;
    private String assignFromStickerNo;
    private String assignToStickerNo;
    private Sticker exportFromSticker;
    private Sticker exportToSticker;
    private String availableStickers;
    private String usedStickers;
    private int filteredResultsCount; // إضافة خاصية عدد النتائج المصفاة
    private StickerService stickerService;
    private List<UserAlias> userAliasList;
    private UserAlias selectedUserAlias;
    // support multiple recipients
    private List<UserAlias> selectedUserAliases;
    private LocalizationService localizationService;
    private UserAliasService userAliasService;
    private String searchText;
    private Boolean showAvailable = true;
    private Boolean showUsed = true;
    private int totalStickers; // إضافة خاصية العدد الإجمالي للملصقات

    @Inject
    private LoginBean loginBean;

    public StickerManagementBean() {
        stickerService = (StickerService) BeanUtility.getBean("stickerService");
        userAliasService = (UserAliasService) BeanUtility.getBean("userAliasService");
        localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
    }

    @PostConstruct
    public void init() {
        try {
            // Load only inspector user aliases using the same approach as mail/create.xhtml
            // That bean filters users by role code == "001" (Inspector). Prefer that method over
            // job-position heuristics because roles are authoritative.
            userAliasList = new ArrayList<>();
            try {
                List<UserAlias> allAliases = userAliasService.getAllWithDetails();
                if (allAliases != null) {
                    for (UserAlias ua : allAliases) {
                        if (ua == null || ua.getSysUserBySysUser() == null)
                            continue;
                        if (ua.getSysUserBySysUser().getSysUserRoles() != null) {
                            for (Object oRole : ua.getSysUserBySysUser().getSysUserRoles()) {
                                try {
                                    com.smat.ins.model.entity.SysUserRole sur = (com.smat.ins.model.entity.SysUserRole) oRole;
                                    if (sur != null && sur.getSysRole() != null && "001".equals(sur.getSysRole().getCode())) {
                                        userAliasList.add(ua);
                                        break;
                                    }
                                } catch (Exception ignore) {
                                    // ignore malformed role entries and continue
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // fallback: previous heuristic based on jobPosition containing 'inspect'
                try {
                    List<UserAlias> allAliases = userAliasService.getAllWithDetails();
                    if (allAliases == null) allAliases = new ArrayList<>();
                    userAliasList = allAliases.stream().filter(ua -> {
                        try {
                            if (ua == null) return false;
                            if (ua.getJobPosition() == null) return false;
                            String code = ua.getJobPosition().getCode() == null ? "" : ua.getJobPosition().getCode();
                            String en = ua.getJobPosition().getEnglishName() == null ? "" : ua.getJobPosition().getEnglishName();
                            String ar = ua.getJobPosition().getArabicName() == null ? "" : ua.getJobPosition().getArabicName();
                            String combined = (code + " " + en + " " + ar).toLowerCase();
                            return combined.contains("inspect");
                        } catch (Exception ex) {
                            return false;
                        }
                    }).collect(Collectors.toList());
                } catch (Exception ignore) {
                    userAliasList = new ArrayList<>();
                }
            }
            stickers = stickerService.findAll();
            // initialize multi-select recipients
            selectedUserAliases = new ArrayList<>();
            filterStickers();
            calculateStatistics();
            updateFilteredResultsCount(); // تحديث عدد النتائج المصفاة عند التهيئة
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
        }
    }

    private void calculateStatistics() {
        if (stickers != null) {
            long availableCount = stickers.stream().filter(sticker -> !sticker.getIsUsed()).count();
            long usedCount = stickers.stream().filter(Sticker::getIsUsed).count();

            availableStickers = String.valueOf(availableCount);
            usedStickers = String.valueOf(usedCount);
            totalStickers = stickers.size(); // تحديث العدد الإجمالي
        } else {
            availableStickers = "0";
            usedStickers = "0";
            totalStickers = 0;
        }
    }

    // دالة لتحديث عدد النتائج المصفاة
    private void updateFilteredResultsCount() {
        if (filteredStickers != null) {
            this.filteredResultsCount = filteredStickers.size();
        } else {
            this.filteredResultsCount = 0;
        }
    }

    /**
     * فتح تقرير PDF للستيكر المستخدم بناءً على reportNo لأول EquipmentInspectionForm
     * سيتم استخدام الانعكاس لجلب الحقل reportNo لأن مجموعة النماذج قد تكون عامة
     */
    public void viewReportForSticker(Sticker sticker) {
        try {
            if (sticker == null || sticker.getEquipmentInspectionForms() == null || sticker.getEquipmentInspectionForms().isEmpty()) {
                UtilityHelper.addErrorMessage("لا يوجد تقرير مرتبط بهذا الستيكر.");
                return;
            }
            Object firstFormObj = sticker.getEquipmentInspectionForms().iterator().next();
            String reportNo = null;
            try {
                java.lang.reflect.Method getReportNoMethod = firstFormObj.getClass().getMethod("getReportNo");
                Object reportNoObj = getReportNoMethod.invoke(firstFormObj);
                if (reportNoObj != null) reportNo = reportNoObj.toString();
            } catch (Exception e) {
                UtilityHelper.addErrorMessage("تعذر جلب رقم التقرير لهذا الستيكر.");
                log.debug("Reflection failed while getting reportNo for sticker: {}", sticker, e);
                return;
            }
            if (reportNo == null || reportNo.trim().isEmpty()) {
                UtilityHelper.addErrorMessage("رقم التقرير غير متوفر لهذا الستيكر.");
                return;
            }
            FacesContext fc = FacesContext.getCurrentInstance();
            String ctx = fc.getExternalContext().getRequestContextPath();
            String url = ctx + "/attachments/inspection/reports/" + java.net.URLEncoder.encode(reportNo, "UTF-8") + ".pdf";
            org.primefaces.PrimeFaces.current().executeScript("window.open('" + url + "', '_blank');");
        } catch (Exception e) {
            UtilityHelper.addErrorMessage("حدث خطأ أثناء محاولة عرض التقرير: " + e.getMessage());
            log.error("Error while opening report for sticker", e);
        }
    }

    /**
     * Wrapper action for use from JSF when method-parameters aren't supported by EL implementation.
     * The UI should set `stickerManagementBean.sticker` via <f:setPropertyActionListener> and call this.
     */
    public void viewReport() {
        viewReportForSticker(this.sticker);
    }

    /**
     * يُعيد رقم التقرير الأول المرتبط بالستيكر إن وُجد، أو سلاسل فارغة خلاف ذلك.
     * هذه الدالة مفيدة لاستعمالها في EL لتفادي استدعاء iterator().next مباشرةً.
     */
    public String getFirstReportNo(Sticker sticker) {
        try {
            if (sticker == null || sticker.getEquipmentInspectionForms() == null || sticker.getEquipmentInspectionForms().isEmpty()) {
                return "";
            }
            Object firstFormObj = sticker.getEquipmentInspectionForms().iterator().next();
            try {
                java.lang.reflect.Method getReportNoMethod = firstFormObj.getClass().getMethod("getReportNo");
                Object reportNoObj = getReportNoMethod.invoke(firstFormObj);
                if (reportNoObj != null) return reportNoObj.toString();
            } catch (Exception e) {
                log.debug("Unable to fetch reportNo by reflection for sticker {}", sticker, e);
            }
        } catch (Exception e) {
            log.debug("Error in getFirstReportNo", e);
        }
        return "";
    }

    /**
     * Overloaded helper to support EL calls where the runtime type may be unknown
     * or the argument is null. Some EL implementations attempt method resolution
     * with a null parameter which causes a MethodNotFoundException; this overload
     * accepts Object and delegates appropriately.
     */
    public String getFirstReportNo(Object stickerObj) {
        if (stickerObj == null) return "";
        if (stickerObj instanceof Sticker) {
            return getFirstReportNo((Sticker) stickerObj);
        }

        // Best-effort reflection: try to find equipmentInspectionForms and extract first reportNo
        try {
            java.lang.reflect.Method m = null;
            try {
                m = stickerObj.getClass().getMethod("getEquipmentInspectionForms");
            } catch (NoSuchMethodException nsme) {
                // ignore
            }
            if (m != null) {
                Object forms = m.invoke(stickerObj);
                if (forms instanceof java.util.Collection) {
                    java.util.Collection<?> col = (java.util.Collection<?>) forms;
                    if (!col.isEmpty()) {
                        Object first = col.iterator().next();
                        if (first != null) {
                            try {
                                java.lang.reflect.Method gr = first.getClass().getMethod("getReportNo");
                                Object reportNoObj = gr.invoke(first);
                                if (reportNoObj != null) return reportNoObj.toString();
                            } catch (Exception ignore) { }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ignore and return empty
        }
        return "";
    }

    public void filterStickers() {
        if (stickers == null) {
            filteredStickers = new ArrayList<>();
            updateFilteredResultsCount(); // تحديث العدد
            return;
        }

        filteredStickers = stickers.stream().filter(sticker -> {
            boolean matchesStatus = false;
            if (showAvailable && !sticker.getIsUsed()) {
                matchesStatus = true;
            }
            if (showUsed && sticker.getIsUsed()) {
                matchesStatus = true;
            }
            return matchesStatus;
        }).collect(Collectors.toList());

        updateFilteredResultsCount(); // تحديث العدد بعد التصفية
    }

    public void searchStickers() {
        try {
            // الحصول على جميع الملصقات أولاً
            List<Sticker> allStickers = stickerService.findAll();

            // تطبيق فلتر المستخدم المحدد
            if (selectedUserAlias != null) {
                allStickers = allStickers.stream()
                        .filter(sticker -> sticker.getSysUserByForUser() != null && sticker.getSysUserByForUser()
                                .getId().equals(selectedUserAlias.getSysUserBySysUser().getId()))
                        .collect(Collectors.toList());
            }

            // تطبيق البحث النصي إذا كان موجوداً
            if (searchText != null && !searchText.trim().isEmpty()) {
                String searchTerm = searchText.toLowerCase().trim();
                allStickers = allStickers.stream().filter(sticker -> (sticker.getSerialNo() != null
                                && sticker.getSerialNo().toLowerCase().contains(searchTerm))
                                || (sticker.getStickerNo() != null && sticker.getStickerNo().toLowerCase().contains(searchTerm))
                                || (sticker.getSysUserByForUser() != null
                                && sticker.getSysUserByForUser().getDisplayName() != null
                                && sticker.getSysUserByForUser().getDisplayName().toLowerCase().contains(searchTerm)))
                        .collect(Collectors.toList());
            }

            stickers = allStickers;
            filterStickers(); // تطبيق فلتر الحالة (متاح/مستخدم)
            calculateStatistics(); // تحديث الإحصائيات بعد البحث
            updateFilteredResultsCount(); // تحديث عدد النتائج المصفاة بعد البحث

        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringSearch"));
            log.error("Error during sticker search", e);
        }
    }

    public void searchStickersByUser() {
        // عند تغيير المستخدم، نقوم بإجراء البحث الكامل
        searchStickers();
    }

    public void findStickerForSelectedUser() {
        // هذه الوظيفة أصبحت زائدة عن الحاجة بعد دمجها في searchStickers
        searchStickers();
    }

    public void generateStickers() {
        try {
            // Generate stickers as global (not assigned to any user).

            if (generatedStickerNum == null || generatedStickerNum <= 0) {
                UtilityHelper
                        .addErrorMessage(localizationService.getErrorMessage().getString("youShouldEnterPositiveVal"));
                return;
            }
            if (generatedStickerNum > 1000) {
                UtilityHelper.addErrorMessage(
                        localizationService.getErrorMessage().getString("youShouldEnterValSmallThan1000"));
                return;
            }

            // إضافة تأخير بسيط لإظهار الأنيميشن
            try {
                Thread.sleep(1500); // تأخير 1.5 ثانية
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) + 1;
            int day = now.get(Calendar.DAY_OF_MONTH);

            // Create the requested number of stickers, optionally assigning them to a user
            for (int i = 0; i < generatedStickerNum; i++) {
                Sticker sticker = new Sticker();
                sticker.setSeq(stickerService.getLastSeq());
                sticker.setSerialNo(
                        String.format("%d%02d%02d%04d", year, month, day, stickerService.getLastSerialID() + 1));
                sticker.setStickerNo("SK" + String.format("%05d", stickerService.getLastStickerNo() + 1));
                sticker.setSysUserByCreatedBy(loginBean.getUser());
                sticker.setYear((short) year);
                sticker.setIsUsed(false);
                sticker.setIsPrinted(false);
                // NOTE: Do NOT auto-assign generated stickers to any user here.
                // Generated stickers should remain unassigned so the operator can
                // explicitly assign them later using the "Assign From Available" action.
                stickerService.save(sticker);
            }

            // بعد الإنشاء، نقوم بالبحث مرة أخرى لتحديث النتائج
            searchStickers();
            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
            log.error("Failed to generate stickers", e);
        }
    }

    // Deprecated: old assign-by-count removed in favor of range-based assignment

    /**
     * Assign a specific sticker number range to the selected user.
     * Example: from SK00001 to SK00020 will assign those sticker records (if available & unassigned).
     * If any sticker in the requested range is already assigned to another user, the whole operation is aborted
     * and an error message is shown listing the conflicting sticker.
     */
    public void assignRangeToUser() {
        try {
            if (selectedUserAlias == null || selectedUserAlias.getSysUserBySysUser() == null) {
                UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("youShouldSelectUser"));
                return;
            }

            if (assignFromStickerNo == null || assignFromStickerNo.trim().isEmpty()
                    || assignToStickerNo == null || assignToStickerNo.trim().isEmpty()) {
                UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("youShouldEnterPositiveVal"));
                return;
            }

            // Extract numeric parts from sticker numbers (e.g., SK00012 -> 12)
            String fromDigits = assignFromStickerNo.replaceAll("\\D", "");
            String toDigits = assignToStickerNo.replaceAll("\\D", "");
            if (fromDigits.isEmpty() || toDigits.isEmpty()) {
                UtilityHelper.addErrorMessage("Invalid sticker number format. Use e.g. SK00001");
                return;
            }

            int fromNum;
            int toNum;
            try {
                fromNum = Integer.parseInt(fromDigits);
                toNum = Integer.parseInt(toDigits);
            } catch (NumberFormatException nfe) {
                UtilityHelper.addErrorMessage("Invalid sticker number format. Use numeric suffix like SK00001");
                return;
            }

            if (fromNum > toNum) {
                UtilityHelper.addErrorMessage("From number must be less than or equal to To number");
                return;
            }

            // Build list of stickers that fall within the requested range
            List<Sticker> all = stickerService.findAll();
            List<Sticker> range = all.stream()
                    .filter(s -> s.getStickerNo() != null)
                    .filter(s -> {
                        String digits = s.getStickerNo().replaceAll("\\D", "");
                        if (digits.isEmpty()) return false;
                        try {
                            int v = Integer.parseInt(digits);
                            return v >= fromNum && v <= toNum;
                        } catch (NumberFormatException ex) {
                            return false;
                        }
                    })
                    .sorted((a, b) -> {
                        Long as = a.getSeq() == null ? 0L : a.getSeq();
                        Long bs = b.getSeq() == null ? 0L : b.getSeq();
                        return as.compareTo(bs);
                    })
                    .collect(Collectors.toList());

            if (range.isEmpty()) {
                UtilityHelper.addErrorMessage("No stickers found in the specified range");
                return;
            }

            SysUser target = selectedUserAlias.getSysUserBySysUser();

            // Check for conflicts: any sticker already assigned to a different user
            for (Sticker s : range) {
                if (s.getSysUserByForUser() != null && !s.getSysUserByForUser().getId().equals(target.getId())) {
                    String msg = String.format("Sticker number %s already assigned for another user: %s",
                            s.getStickerNo(), s.getSysUserByForUser().getDisplayName());
                    UtilityHelper.addErrorMessage(msg);
                    return; // abort operation
                }
            }

            // Assign all stickers in the range (including those already assigned to same user)
            for (Sticker s : range) {
                s.setSysUserByForUser(target);
                try {
                    stickerService.update(s);
                } catch (Exception ex) {
                    log.error("Failed to assign sticker {}", s, ex);
                }
            }

            // Refresh results and show success
            searchStickers();
            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

            // Clear inputs
            assignFromStickerNo = null;
            assignToStickerNo = null;

        } catch (Exception e) {
            log.error("Error while assigning sticker range", e);
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
        }
    }

    public void selectAllRecipients() {
        if (userAliasList != null) {
            selectedUserAliases = new ArrayList<>(userAliasList);
        }
    }

    public void clearRecipients() {
        if (selectedUserAliases != null) selectedUserAliases.clear();
        selectedUserAlias = null;
    }


    public void exportStickers() {
        try {
            if (stickers == null || stickers.isEmpty()) {
                UtilityHelper.addErrorMessage(
                        localizationService.getErrorMessage().getString("stickerListIsEmptyPleaseGenerateFirst"));
                return;
            }

            List<Map<String, Object>> dataSource = new ArrayList<>();
            for (Sticker sticker : stickers) {
                Map<String, Object> record = new HashMap<>();
                record.put("stickerNo", sticker.getStickerNo());

                // Generate unique QR code
                String qrText = UtilityHelper.getBaseURL() + "/api/equipment-cert/" + sticker.getSerialNo() + "&"
                        + sticker.getStickerNo();
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 100, 100);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
                record.put("smatQrCode", baos.toByteArray());

                dataSource.add(record);
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("lstSticker", dataSource);
            parameters.put("logoLeftPath", getServletContextPath("views/jasper/images/logo-left.png"));
            parameters.put("logoRightPath", getServletContextPath("views/jasper/images/logo-right.png"));
            parameters.put("iconMailPath", getServletContextPath("views/jasper/images/icon-mail.png"));
            parameters.put("iconMobilePath", getServletContextPath("views/jasper/images/icon-mobile.png"));
            parameters.put("iconSitePath", getServletContextPath("views/jasper/images/icon-site.png"));
            parameters.put("iconWhatsPath", getServletContextPath("views/jasper/images/icon-whats.png"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(getServletContextPath("views/jasper/Sticker.jasper"),
                    parameters, new JRBeanCollectionDataSource(dataSource));
            exportReport(jasperPrint, "Stickers");

            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
            log.error("Failed to export stickers", e);
        }
    }

    public void exportStickers(Sticker sticker) {
        try {
            if (stickers == null || stickers.isEmpty()) {
                UtilityHelper.addErrorMessage(
                        localizationService.getErrorMessage().getString("stickerListIsEmptyPleaseGenerateFirst"));
                return;
            }

            List<Map<String, Object>> dataSource = new ArrayList<>();
            if (sticker != null) {
                Map<String, Object> record = new HashMap<>();
                record.put("stickerNo", sticker.getStickerNo());

                // Generate unique QR code
                String qrText = UtilityHelper.getBaseURL() + "/api/equipment-cert/" + sticker.getSerialNo() + "&"
                        + sticker.getStickerNo();
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 100, 100);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
                record.put("smatQrCode", baos.toByteArray());

                dataSource.add(record);
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("lstSticker", dataSource);
            parameters.put("logoLeftPath", getServletContextPath("views/jasper/images/logo-left.png"));
            parameters.put("logoRightPath", getServletContextPath("views/jasper/images/logo-right.png"));
            parameters.put("iconMailPath", getServletContextPath("views/jasper/images/icon-mail.png"));
            parameters.put("iconMobilePath", getServletContextPath("views/jasper/images/icon-mobile.png"));
            parameters.put("iconSitePath", getServletContextPath("views/jasper/images/icon-site.png"));
            parameters.put("iconWhatsPath", getServletContextPath("views/jasper/images/icon-whats.png"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(getServletContextPath("views/jasper/Sticker.jasper"),
                    parameters, new JRBeanCollectionDataSource(dataSource));
            exportReport(jasperPrint, "Stickers");

            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
            log.error("Failed to export stickers", e);
        }
    }

    private String getServletContextPath(String relativePath) {
        return ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext())
                .getRealPath(relativePath);
    }

    private byte[] exportReport(JasperPrint jasperPrint, String reportName) throws IOException, JRException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            response.setHeader("Content-type", "application/pdf");
            response.setHeader("Content-disposition",
                    "inline; filename=" + reportName + "_" + System.currentTimeMillis() + ".pdf");
            response.setHeader("pragma", "public");
            byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
            JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
            facesContext.responseComplete();
            return data;
        } catch (Exception e) {
            log.error("Failed to export report: " + reportName, e);
            throw e;
        }
    }
    public void exportToExcel() {
        // نتأكد أن هناك بيانات للتصدير
        if (filteredStickers == null || filteredStickers.isEmpty()) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("stickerListIsEmptyPleaseGenerateFirst"));
            return;
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

        // حجم الـQR بالبيكسل (مربع)
        final int QR_PIXEL_SIZE = 150;
        // عمود الصورة (0-based index). هنا نستخدم العمود الأخير (index 7) كما في التصميم السابق.
        final int QR_COLUMN_INDEX = 7;

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Stickers");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Headers: removed QR Text column, keep QR Code image column
            String[] headers = new String[] {
                    "ID", "Serial Number", "Sticker Number", "Assigned To",
                    "Report No", "Status", "Year", "QR Code (Image)"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell hcell = headerRow.createCell(i);
                hcell.setCellValue(headers[i]);
                hcell.setCellStyle(headerStyle);
            }

            // Data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setWrapText(true);
            dataStyle.setVerticalAlignment(VerticalAlignment.TOP);

            CreationHelper creationHelper = workbook.getCreationHelper();
            Drawing<?> drawing = sheet.createDrawingPatriarch();

            int rowNum = 1;
            for (Sticker s : filteredStickers) {
                Row row = sheet.createRow(rowNum);

                // ضبط ارتفاع الصف ليطابق حجم الـQR (نستخدم نقاط: تقريباً 1 نقطة ≈ 1 بكسل على شاشات عادية)
                // نضع ارتفاع الصف مساويًا لحجم الـQR بالـpoints (تجريبي، يمكن التعديل إذا لزم)
                row.setHeightInPoints((short) QR_PIXEL_SIZE);

                // Column 0: ID
                Object idObj = s.getId();
                Cell c0 = row.createCell(0);
                c0.setCellValue(idObj != null ? String.valueOf(idObj) : "");
                c0.setCellStyle(dataStyle);

                // Column 1: Serial Number
                Cell c1 = row.createCell(1);
                c1.setCellValue(s.getSerialNo() != null ? s.getSerialNo() : "");
                c1.setCellStyle(dataStyle);

                // Column 2: Sticker Number
                Cell c2 = row.createCell(2);
                c2.setCellValue(s.getStickerNo() != null ? s.getStickerNo() : "");
                c2.setCellStyle(dataStyle);

                // Column 3: Assigned To
                String assigned = (s.getSysUserByForUser() != null && s.getSysUserByForUser().getDisplayName() != null)
                        ? s.getSysUserByForUser().getDisplayName() : "";
                Cell c3 = row.createCell(3);
                c3.setCellValue(assigned);
                c3.setCellStyle(dataStyle);

                // Column 4: Report No
                String reportNo = "";
                try {
                    String first = this.getFirstReportNo(s);
                    if (first != null && !first.trim().isEmpty()) {
                        reportNo = first;
                    } else if (s.getEquipmentInspectionForms() != null && !s.getEquipmentInspectionForms().isEmpty()) {
                        Object firstFormObj = s.getEquipmentInspectionForms().iterator().next();
                        if (firstFormObj instanceof com.smat.ins.model.entity.EquipmentInspectionForm) {
                            com.smat.ins.model.entity.EquipmentInspectionForm ef = (com.smat.ins.model.entity.EquipmentInspectionForm) firstFormObj;
                            if (ef.getReportNo() != null) reportNo = ef.getReportNo();
                        }
                    }
                } catch (Exception ex) {
                    // fallback silent
                }
                Cell c4 = row.createCell(4);
                c4.setCellValue(reportNo);
                c4.setCellStyle(dataStyle);

                // Column 5: Status (Used / Available)
                Boolean used = s.getIsUsed();
                Cell c5 = row.createCell(5);
                c5.setCellValue((used != null && used) ? "Used" : "Available");
                c5.setCellStyle(dataStyle);

                // Column 6: Year
                Cell c6 = row.createCell(6);
                c6.setCellValue(s.getYear() != null ? String.valueOf(s.getYear()) : "");
                c6.setCellStyle(dataStyle);

                // Column 7: QR Code image (generate PNG and embed)
                try {
                    String qrText = UtilityHelper.getBaseURL() + "/api/equipment-cert/" + (s.getSerialNo() != null ? s.getSerialNo() : "") + "&"
                            + (s.getStickerNo() != null ? s.getStickerNo() : "");
                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                    BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, QR_PIXEL_SIZE, QR_PIXEL_SIZE);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
                    byte[] qrBytes = baos.toByteArray();
                    int pictureIdx = workbook.addPicture(qrBytes, Workbook.PICTURE_TYPE_PNG);
                    ClientAnchor anchor = creationHelper.createClientAnchor();

                    // place image in column QR_COLUMN_INDEX at this row
                    anchor.setCol1(QR_COLUMN_INDEX);
                    anchor.setRow1(rowNum);
                    anchor.setCol2(QR_COLUMN_INDEX + 1);
                    anchor.setRow2(rowNum + 1);
                    // optional: adjust dx/dy if needed via anchor.setDx1/... but keep defaults for portability

                    Picture pict = drawing.createPicture(anchor, pictureIdx);

                    // resize to exactly fit cell (try 1.0; adjust if necessary)
                    try {
                        pict.resize(1.0); // try to fill the target cell area
                    } catch (Exception ex) {
                        // fallback: ignore resize failure
                    }
                } catch (Exception qrEx) {
                    // if QR generation fails, write placeholder text in the image column
                    Cell c7 = row.createCell(QR_COLUMN_INDEX);
                    c7.setCellValue("[QR gen failed]");
                    c7.setCellStyle(dataStyle);
                }

                rowNum++;
            }

            // Auto-size textual columns (skip QR image column to avoid expensive ops)
            for (int i = 0; i <= QR_COLUMN_INDEX - 1; i++) {
                try {
                    sheet.autoSizeColumn(i);
                    int width = sheet.getColumnWidth(i);
                    int maxWidth = 256 * 60; // limit to ~60 characters
                    if (width > maxWidth) sheet.setColumnWidth(i, maxWidth);
                } catch (Exception ex) {
                    // ignore autosize issues for large sheets
                }
            }

            // Set a column width that better matches the QR_PIXEL_SIZE so the cell becomes roughly square.
            // Approximation: columnWidthUnits = pixelWidth * 256 / 7 (Excel uses approx 7 pixels per character)
            int colWidthUnits = QR_PIXEL_SIZE * 256 / 7;
            sheet.setColumnWidth(QR_COLUMN_INDEX, colWidthUnits);

            // Write workbook to byte array
            workbook.write(out);
            byte[] bytes = out.toByteArray();

            // Prepare and send response
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "stickers_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentLength(bytes.length);

            try (ServletOutputStream sos = response.getOutputStream()) {
                sos.write(bytes);
                sos.flush();
            }

            facesContext.responseComplete();

            // Optional success message
            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

        } catch (Exception e) {
            log.error("Failed to export stickers to Excel", e);
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
        }
    }

    /**
     * Export only stickers within the selected from/to range (inclusive).
     * The dialog binds `exportFromSticker` and `exportToSticker`.
     */
    public void exportStickersRange() {
        try {
            if (exportFromSticker == null || exportToSticker == null) {
                UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("youShouldEnterPositiveVal"));
                return;
            }

            // Extract numeric suffixes from stickerNo values
            int fromNum = extractNumericSuffix(exportFromSticker.getStickerNo());
            int toNum = extractNumericSuffix(exportToSticker.getStickerNo());
            if (fromNum < 0 || toNum < 0) {
                UtilityHelper.addErrorMessage("Invalid sticker number format selected for export.");
                return;
            }
            if (fromNum > toNum) {
                UtilityHelper.addErrorMessage("From must be less or equal To for export range.");
                return;
            }

            List<Sticker> all = stickerService.findAll();
            List<Sticker> range = all.stream()
                    .filter(s -> s.getStickerNo() != null)
                    .filter(s -> {
                        int v = extractNumericSuffix(s.getStickerNo());
                        return v >= fromNum && v <= toNum;
                    })
                    .sorted((a, b) -> {
                        Long as = a.getSeq() == null ? 0L : a.getSeq();
                        Long bs = b.getSeq() == null ? 0L : b.getSeq();
                        return as.compareTo(bs);
                    }).collect(Collectors.toList());

            if (range.isEmpty()) {
                UtilityHelper.addErrorMessage("No stickers found in the selected range.");
                return;
            }

            // reuse existing export/report generation by building a datasource and filling jasper
            List<Map<String, Object>> dataSource = new ArrayList<>();
            for (Sticker sticker : range) {
                Map<String, Object> record = new HashMap<>();
                record.put("stickerNo", sticker.getStickerNo());
                String qrText = UtilityHelper.getBaseURL() + "/api/equipment-cert/" + sticker.getSerialNo() + "&"
                        + sticker.getStickerNo();
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 100, 100);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
                record.put("smatQrCode", baos.toByteArray());
                dataSource.add(record);
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("lstSticker", dataSource);
            parameters.put("logoLeftPath", getServletContextPath("views/jasper/images/logo-left.png"));
            parameters.put("logoRightPath", getServletContextPath("views/jasper/images/logo-right.png"));
            parameters.put("iconMailPath", getServletContextPath("views/jasper/images/icon-mail.png"));
            parameters.put("iconMobilePath", getServletContextPath("views/jasper/images/icon-mobile.png"));
            parameters.put("iconSitePath", getServletContextPath("views/jasper/images/icon-site.png"));
            parameters.put("iconWhatsPath", getServletContextPath("views/jasper/images/icon-whats.png"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(getServletContextPath("views/jasper/Sticker.jasper"),
                    parameters, new JRBeanCollectionDataSource(dataSource));
            exportReport(jasperPrint, "Stickers_Range");

            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
            // clear selection
            exportFromSticker = null;
            exportToSticker = null;
        } catch (Exception e) {
            log.error("Failed to export selected sticker range", e);
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
        }
    }

    private int extractNumericSuffix(String stickerNo) {
        if (stickerNo == null) return -1;
        String digits = stickerNo.replaceAll("\\D", "");
        if (digits.isEmpty()) return -1;
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }




    // Manual getters and setters to ensure Lombok works correctly
    public List<Sticker> getStickers() {
        return stickers;
    }

    public void setStickers(List<Sticker> stickers) {
        this.stickers = stickers;
        calculateStatistics(); // تحديث الإحصائيات عند تعيين الملصقات
    }

    public List<Sticker> getFilteredStickers() {
        if (filteredStickers == null) {
            filterStickers();
        }
        return filteredStickers;
    }

    public void setFilteredStickers(List<Sticker> filteredStickers) {
        this.filteredStickers = filteredStickers;
        updateFilteredResultsCount(); // تحديث العدد عند تعيين القيمة
    }

    public int getFilteredResultsCount() {
        return filteredResultsCount;
    }

    public void setFilteredResultsCount(int filteredResultsCount) {
        this.filteredResultsCount = filteredResultsCount;
    }

    public int getTotalStickers() {
        return totalStickers;
    }

    public void setTotalStickers(int totalStickers) {
        this.totalStickers = totalStickers;
    }

    public Sticker getExportFromSticker() {
        return exportFromSticker;
    }

    public void setExportFromSticker(Sticker exportFromSticker) {
        this.exportFromSticker = exportFromSticker;
    }

    public Sticker getExportToSticker() {
        return exportToSticker;
    }

    public void setExportToSticker(Sticker exportToSticker) {
        this.exportToSticker = exportToSticker;
    }

    

    public List<UserAlias> getUserAliasList() {
        return userAliasList;
    }

    public void setUserAliasList(List<UserAlias> userAliasList) {
        this.userAliasList = userAliasList;
    }

    public UserAlias getSelectedUserAlias() {
        return selectedUserAlias;
    }

    public void setSelectedUserAlias(UserAlias selectedUserAlias) {
        this.selectedUserAlias = selectedUserAlias;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getAvailableStickers() {
        return availableStickers;
    }

    public void setAvailableStickers(String availableStickers) {
        this.availableStickers = availableStickers;
    }

    public String getUsedStickers() {
        return usedStickers;
    }

    public void setUsedStickers(String usedStickers) {
        this.usedStickers = usedStickers;
    }

    public Boolean getShowAvailable() {
        return showAvailable;
    }

    public void setShowAvailable(Boolean showAvailable) {
        this.showAvailable = showAvailable;
        // عند تغيير حالة الفلتر، نقوم بتحديث النتائج
        filterStickers();
    }

    public Boolean getShowUsed() {
        return showUsed;
    }

    public void setShowUsed(Boolean showUsed) {
        this.showUsed = showUsed;
        // عند تغيير حالة الفلتر، نقوم بتحديث النتائج
        filterStickers();
    }

}