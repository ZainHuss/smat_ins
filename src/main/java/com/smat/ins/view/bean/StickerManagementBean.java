package com.smat.ins.view.bean;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.smat.ins.model.entity.Sticker;
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
            userAliasList = userAliasService.getAllWithDetails();
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
            // If using multi-select recipients, require at least one
            if ((selectedUserAliases == null || selectedUserAliases.isEmpty()) && selectedUserAlias == null) {
                UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("youShouldSelectUser"));
                return;
            }

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

            // Determine recipients: prefer selectedUserAliases if set, otherwise single selectedUserAlias
            List<UserAlias> recipients = new ArrayList<>();
            if (selectedUserAliases != null && !selectedUserAliases.isEmpty()) {
                recipients.addAll(selectedUserAliases);
            } else if (selectedUserAlias != null) {
                recipients.add(selectedUserAlias);
            }

            for (UserAlias recipient : recipients) {
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
                    if (recipient != null && recipient.getSysUserBySysUser() != null)
                        sticker.setSysUserByForUser(recipient.getSysUserBySysUser());
                    stickerService.save(sticker);
                }
            }

            // بعد الإنشاء، نقوم بالبحث مرة أخرى لتحديث النتائج
            searchStickers();
            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
            log.error("Failed to generate stickers", e);
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

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Stickers");

            // Header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Serial Number");
            header.createCell(2).setCellValue("Sticker Number");
            header.createCell(3).setCellValue("Assigned To");
            header.createCell(4).setCellValue("Status");
            header.createCell(5).setCellValue("Year");

            // Data rows
            int rowNum = 1;
            for (Sticker s : filteredStickers) {
                Row row = sheet.createRow(rowNum++);

                // ID
                Object idObj = s.getId();
                row.createCell(0).setCellValue(idObj != null ? idObj.toString() : "");

                // Serial number
                row.createCell(1).setCellValue(s.getSerialNo() != null ? s.getSerialNo() : "");

                // Sticker number
                row.createCell(2).setCellValue(s.getStickerNo() != null ? s.getStickerNo() : "");

                // Assigned to
                String assigned = (s.getSysUserByForUser() != null && s.getSysUserByForUser().getDisplayName() != null)
                        ? s.getSysUserByForUser().getDisplayName() : "";
                row.createCell(3).setCellValue(assigned);

                // Status
                Boolean used = s.getIsUsed();
                row.createCell(4).setCellValue((used != null && used) ? "Used" : "Available");

                // Year (إن وُجد)
                row.createCell(5).setCellValue(s.getYear() != null ? s.getYear().toString() : "");
            }

            // ضبط عرض الأعمدة
            for (int i = 0; i <= 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // كتابة الـ workbook إلى مصفوفة بايت
            workbook.write(out);
            byte[] bytes = out.toByteArray();

            // إعداد الاستجابة وإرساله
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "stickers_" + System.currentTimeMillis() + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentLength(bytes.length);

            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();

            facesContext.responseComplete();

            // رسالة نجاح اختيارية
            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

        } catch (Exception e) {
            log.error("Failed to export stickers to Excel", e);
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
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