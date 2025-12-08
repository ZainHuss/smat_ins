package com.smat.ins.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.smat.ins.model.entity.EquipmentInspectionCertificate;
import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.EquipmentInspectionFormItem;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.service.EquipmentInspectionCertificateService;
import com.smat.ins.model.service.EquipmentInspectionFormService;
import com.smat.ins.util.BeanUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.BaseColor;

/**
 * EquipmentCertificateSummaryServlet (iText 5 version)
 * - Enhanced design with modern colors, better typography and improved layout
 * - Professional color scheme with gradients and subtle shadows
 * - Better spacing and visual hierarchy
 */
@WebServlet("/api/equipment-cert/summary/*")
public class EquipmentCertificateSummaryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(EquipmentCertificateSummaryServlet.class);
    private EquipmentInspectionCertificateService certificateService;
    private EquipmentInspectionFormService equipmentInspectionFormService;

    // Modern color palette
    private static final BaseColor PRIMARY_COLOR = new BaseColor(14, 102, 233); // Professional blue
    private static final BaseColor SECONDARY_COLOR = new BaseColor(108, 117, 125); // Gray
    private static final BaseColor SUCCESS_COLOR = new BaseColor(22, 163, 74); // Green
    private static final BaseColor DANGER_COLOR = new BaseColor(220, 38, 38); // Red
    private static final BaseColor WARNING_COLOR = new BaseColor(245, 158, 11); // Amber
    private static final BaseColor LIGHT_BG = new BaseColor(248, 249, 250);
    private static final BaseColor CARD_BG = new BaseColor(255, 255, 255);
    private static final BaseColor BORDER_COLOR = new BaseColor(222, 226, 230);

    @Override
    public void init() throws ServletException {
        super.init();
        certificateService = (EquipmentInspectionCertificateService) BeanUtility.getBean(this.getServletContext(), "equipmentInspectionCertificateService");
        equipmentInspectionFormService = (EquipmentInspectionFormService) BeanUtility.getBean(this.getServletContext(), "equipmentInspectionFormService");
        if (certificateService == null) {
            throw new ServletException("equipmentInspectionCertificateService bean not found in servlet context");
        }
        if (equipmentInspectionFormService == null) {
            // do not throw; we'll try best-effort branch resolution without it
        }
    }

    private String safeUserDisplay(SysUser u) {
        try {
            if (u == null) return "";
            if (u.getEnDisplayName() != null && !u.getEnDisplayName().trim().isEmpty()) return u.getEnDisplayName().trim();
            String fn = u.getFirstName() != null ? u.getFirstName().trim() : "";
            String ln = u.getLastName() != null ? u.getLastName().trim() : "";
            String combined = (fn + " " + ln).trim();
            if (!combined.isEmpty()) return combined;
            if (u.getUserName() != null && !u.getUserName().trim().isEmpty()) return u.getUserName().trim();
            if (u.getSysUserCode() != null && !u.getSysUserCode().trim().isEmpty()) return u.getSysUserCode().trim();
            if (u.getId() != null) return String.valueOf(u.getId());
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private String safeHtml(Object o) {
        if (o == null) return "";
        String s = String.valueOf(o);
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private Map<String, Object> buildReportParams(EquipmentInspectionCertificate cert, String serialNo, String stickerNo) {
        Map<String, Object> reportParams = new HashMap<>();
        EquipmentInspectionForm form = cert.getEquipmentInspectionForm();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        reportParams.put("ReportNo", form != null && form.getReportNo() != null ? form.getReportNo() : "");
        reportParams.put("StNo", form != null && form.getStickerNo() != null ? form.getStickerNo() : stickerNo);
        reportParams.put("TsNo", form != null && form.getTimeSheetNo() != null ? form.getTimeSheetNo() : "");
        reportParams.put("JobNo", form != null && form.getJobNo() != null ? form.getJobNo() : "");
        reportParams.put("Company", form != null && form.getCompany() != null && form.getCompany().getName() != null ? form.getCompany().getName() : "");
        reportParams.put("DateOfThoroughExamination", form != null && form.getDateOfThoroughExamination() != null ? df.format(form.getDateOfThoroughExamination()) : "");
        reportParams.put("NextExaminationDate", form != null && form.getNextExaminationDate() != null ? df.format(form.getNextExaminationDate()) : "");

        // equipment category
        String equipCat = "";
        try {
            if (form != null && form.getEquipmentCategory() != null) {
                try {
                    equipCat = form.getEquipmentCategory().getName();
                } catch (Throwable t) {
                    // FacesContext may be null in servlet
                }
                if (equipCat == null || equipCat.trim().isEmpty()) {
                    try {
                        if (form.getEquipmentCategory().getEnglishName() != null && !form.getEquipmentCategory().getEnglishName().trim().isEmpty())
                            equipCat = form.getEquipmentCategory().getEnglishName();
                        else if (form.getEquipmentCategory().getArabicName() != null)
                            equipCat = form.getEquipmentCategory().getArabicName();
                    } catch (Throwable t) {
                        // ignore
                    }
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        reportParams.put("EquipmentCategory", equipCat != null ? equipCat : "");

        // examination type - removed as requested

        // Inspector and Reviewer
        try {
            String insp = "";
            String rev = "";
            if (form != null) {
                try { insp = safeUserDisplay(form.getSysUserByInspectionBy()); } catch (Throwable t) {}
                try { rev = safeUserDisplay(form.getSysUserByReviewedBy()); } catch (Throwable t) {}
            }
            reportParams.put("InspectorName", insp != null && !insp.trim().isEmpty() ? insp : "-");
            reportParams.put("ReviewerName", rev != null && !rev.trim().isEmpty() ? rev : "-");
        } catch (Throwable t) {
            reportParams.put("InspectorName", "-");
            reportParams.put("ReviewerName", "-");
        }

        // Address
        try {
            String address = "";
            if (form != null) {
                try {
                    if ((form.getCompany() == null || form.getCompany().getAddress() == null) && equipmentInspectionFormService != null && form.getId() != null) {
                        try {
                            EquipmentInspectionForm reloaded = (EquipmentInspectionForm) equipmentInspectionFormService.findByIdWithChildren(form.getId());
                            if (reloaded != null) form = reloaded;
                        } catch (Throwable reloadEx) {
                            log.debug("Could not reload EquipmentInspectionForm to fetch company/address", reloadEx);
                        }
                    }
                } catch (Throwable ignore) {}

                try {
                    if (form.getCompany() != null && form.getCompany().getAddress() != null && !form.getCompany().getAddress().trim().isEmpty()) {
                        address = form.getCompany().getAddress();
                    }
                } catch (Throwable t) {
                    log.debug("Error accessing company.address on form id {}: {}", form.getId(), t.getMessage());
                }

                if ((address == null || address.trim().isEmpty())) {
                    try {
                        if (form.getNameAndAddressOfEmployer() != null && !form.getNameAndAddressOfEmployer().trim().isEmpty()) {
                            address = form.getNameAndAddressOfEmployer();
                        }
                    } catch (Throwable t) {
                        // ignore
                    }
                }
            }
            if (address == null) address = "";
            reportParams.put("Address", address);
        } catch (Throwable t) {
            log.error("Unexpected error while building Address report param", t);
            reportParams.put("Address", "");
        }

        // logos
        String logoLeft = this.getServletContext().getRealPath("/views/jasper/images/logo-left.png");
        String logoRight = this.getServletContext().getRealPath("/views/jasper/images/logo-right.png");
        reportParams.put("logoLeftPath", logoLeft);
        reportParams.put("logoRightPath", logoRight);

        return reportParams;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String serialNo = request.getParameter("serialNo");
        String stickerNo = request.getParameter("stickerNo");
        String reportNo = request.getParameter("reportNo");

        if ((serialNo == null || stickerNo == null) && request.getPathInfo() != null) {
            String pathInfo = request.getPathInfo();
            if (pathInfo.startsWith("/")) pathInfo = pathInfo.substring(1);
            if (pathInfo.contains("&")) {
                String[] parts = pathInfo.split("&", 2);
                if (parts.length >= 2) {
                    if (serialNo == null) serialNo = parts[0];
                    if (stickerNo == null) stickerNo = parts[1];
                }
            } else if (pathInfo.contains("/")) {
                String[] parts = pathInfo.split("/", 2);
                if (parts.length >= 2) {
                    if (serialNo == null) serialNo = parts[0];
                    if (stickerNo == null) stickerNo = parts[1];
                }
            } else {
                if (serialNo == null && !pathInfo.isEmpty()) serialNo = pathInfo;
            }
        }

        // Allow reportNo as fallback when stickerNo is not provided (QRs may carry reportNo)
        // If serialNo missing, attempt to resolve by reportNo (useful when QR encodes reportNo)
        if (serialNo == null || serialNo.isEmpty()) {
            // Try parse from pathInfo first (if the caller used /serial&report or /serial/report)
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                String params = pathInfo.substring(1);
                String[] parts;
                if (params.contains("&")) {
                    parts = params.split("&", 2);
                } else if (params.contains("/")) {
                    parts = params.split("/", 3);
                } else {
                    parts = new String[] { params };
                }
                if (parts.length >= 1 && (serialNo == null || serialNo.isEmpty())) {
                    serialNo = parts[0];
                }
                if (parts.length >= 2) {
                    if (stickerNo == null || stickerNo.isEmpty()) {
                        stickerNo = parts[1];
                    }
                    // if reportNo param is missing, treat second path part as reportNo candidate
                    if ((reportNo == null || reportNo.isEmpty()) && (parts.length >= 2)) {
                        reportNo = parts[1];
                    }
                }
            }

            // If still missing serialNo but reportNo provided, attempt to resolve the form/certificate by reportNo
            if ((serialNo == null || serialNo.isEmpty()) && reportNo != null && !reportNo.isEmpty() && equipmentInspectionFormService != null) {
                try {
                    com.smat.ins.model.entity.EquipmentInspectionForm form = equipmentInspectionFormService.findByUniqueField("reportNo", reportNo);
                    if (form != null && form.getId() != null) {
                        EquipmentInspectionCertificate certTry = certificateService.getBy(form.getId());
                        if (certTry != null) {
                            // We have resolved the certificate — use it and continue
                            EquipmentInspectionCertificate cert = certTry;
                            EquipmentInspectionForm formResolved = cert.getEquipmentInspectionForm();
                            // reload form with children to ensure company, users, items are available
                            try {
                                if (equipmentInspectionFormService != null && formResolved != null && formResolved.getId() != null) {
                                    EquipmentInspectionForm reloaded = (EquipmentInspectionForm) equipmentInspectionFormService.findByIdWithChildren(formResolved.getId());
                                    if (reloaded != null) formResolved = reloaded;
                                }
                            } catch (Throwable reloadEx) {
                                log.debug("Could not reload form id {}: {}", formResolved != null ? formResolved.getId() : null, reloadEx.getMessage());
                            }

                            String stickerVal = "-";
                            String serialDisplay = "-";
                            if (formResolved != null) {
                                if (formResolved.getSticker() != null) {
                                    if (formResolved.getSticker().getStickerNo() != null && !formResolved.getSticker().getStickerNo().trim().isEmpty()) {
                                        stickerVal = formResolved.getSticker().getStickerNo();
                                    }
                                    if (formResolved.getSticker().getSerialNo() != null && !formResolved.getSticker().getSerialNo().trim().isEmpty()) {
                                        serialDisplay = formResolved.getSticker().getSerialNo();
                                    }
                                }
                                if ((stickerVal == null || "".equals(stickerVal)) && formResolved.getStickerNo() != null && !formResolved.getStickerNo().trim().isEmpty()) {
                                    stickerVal = formResolved.getStickerNo();
                                }
                                if ((serialDisplay == null || "".equals(serialDisplay)) && formResolved.getSticker() == null) {
                                    // no sticker entity, keep serialDisplay as '-'
                                }
                            }

                            Map<String, Object> params = buildReportParams(cert, serialDisplay, (stickerVal == null || stickerVal.isEmpty()) ? "-" : stickerVal);
                            String formResult = computeResultFromForm(formResolved);
                            String normResult = null;
                            if (formResult != null) {
                                normResult = normalizeResultString(formResult);
                            }
                            if (normResult == null) {
                                String rawResult = extractResultFromObjects(cert, formResolved);
                                normResult = normalizeResultString(rawResult);
                            }
                            if (normResult == null) normResult = "-";
                            params.put("Result", normResult);

                            String format = request.getParameter("format");
                            boolean forceHtml = "html".equalsIgnoreCase(format);
                            if (forceHtml) {
                                sendFallbackHtml(response, params);
                                return;
                            }

                            String fileName = "summary_" + (params.get("ReportNo") != null && !((String) params.get("ReportNo")).isEmpty() ? params.get("ReportNo") : serialDisplay) + ".pdf";
                            try {
                                response.reset();
                                response.setContentType("application/pdf");
                                String encoded = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
                                response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + encoded);

                                try (OutputStream os = response.getOutputStream()) {
                                    generatePdf(os, params);
                                    os.flush();
                                }
                            } catch (Exception pdfEx) {
                                pdfEx.printStackTrace();
                                sendFallbackHtml(response, params);
                            }
                            return;
                        }
                    }
                } catch (Exception ex) {
                    log.debug("Could not resolve certificate by reportNo {}: {}", reportNo, ex.getMessage());
                }
            }
        }

        // At this point serialNo may be present or not; if still missing and no reportNo path succeeded,
        // try to build a summary from the form using reportNo (if available). Otherwise show a friendly
        // fallback page with '-' placeholders instead of returning HTTP 400.
        if (serialNo == null || serialNo.isEmpty()) {
            if (reportNo != null && !reportNo.trim().isEmpty() && equipmentInspectionFormService != null) {
                try {
                    EquipmentInspectionForm form = (EquipmentInspectionForm) equipmentInspectionFormService.findByUniqueField("reportNo", reportNo);
                    if (form != null) {
                        // reload with children so we can access company, sticker, users, items
                        try {
                            EquipmentInspectionForm reloaded = (EquipmentInspectionForm) equipmentInspectionFormService.findByIdWithChildren(form.getId());
                            if (reloaded != null) form = reloaded;
                        } catch (Throwable t) {
                            log.debug("Could not reload form id {}: {}", form.getId(), t.getMessage());
                        }

                        // Determine sticker and serial display
                        String stickerDisplay = "-";
                        String serialDisplay = "-";
                        if (form.getSticker() != null) {
                            if (form.getSticker().getStickerNo() != null && !form.getSticker().getStickerNo().trim().isEmpty()) {
                                stickerDisplay = form.getSticker().getStickerNo();
                            }
                            if (form.getSticker().getSerialNo() != null && !form.getSticker().getSerialNo().trim().isEmpty()) {
                                serialDisplay = form.getSticker().getSerialNo();
                            }
                        }
                        if ((stickerDisplay == null || "".equals(stickerDisplay)) && form.getStickerNo() != null && !form.getStickerNo().trim().isEmpty()) {
                            stickerDisplay = form.getStickerNo();
                        }

                        // Build params from form
                        Map<String, Object> params = new HashMap<>();
                        params.put("ReportNo", form.getReportNo() != null ? form.getReportNo() : reportNo);
                        params.put("StNo", stickerDisplay != null ? stickerDisplay : "-");
                        params.put("TsNo", form.getTimeSheetNo() != null ? form.getTimeSheetNo() : "");
                        params.put("JobNo", form.getJobNo() != null ? form.getJobNo() : "");
                        params.put("Company", (form.getCompany() != null && form.getCompany().getName() != null) ? form.getCompany().getName() : "");
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        params.put("DateOfThoroughExamination", form.getDateOfThoroughExamination() != null ? df.format(form.getDateOfThoroughExamination()) : "");
                        params.put("NextExaminationDate", form.getNextExaminationDate() != null ? df.format(form.getNextExaminationDate()) : "");

                        // equipment category
                        String equipCat = "";
                        try {
                            if (form.getEquipmentCategory() != null) {
                                try { equipCat = form.getEquipmentCategory().getName(); } catch (Throwable tt) {}
                                if (equipCat == null || equipCat.trim().isEmpty()) {
                                    try {
                                        if (form.getEquipmentCategory().getEnglishName() != null && !form.getEquipmentCategory().getEnglishName().trim().isEmpty())
                                            equipCat = form.getEquipmentCategory().getEnglishName();
                                        else if (form.getEquipmentCategory().getArabicName() != null)
                                            equipCat = form.getEquipmentCategory().getArabicName();
                                    } catch (Throwable t2) {}
                                }
                            }
                        } catch (Throwable t3) {}
                        params.put("EquipmentCategory", equipCat != null ? equipCat : "");

                        // Inspector / Reviewer
                        String insp = "-";
                        String rev = "-";
                        try {
                            if (form.getSysUserByInspectionBy() != null) insp = safeUserDisplay(form.getSysUserByInspectionBy());
                        } catch (Throwable tt) {}
                        try {
                            if (form.getSysUserByReviewedBy() != null) rev = safeUserDisplay(form.getSysUserByReviewedBy());
                        } catch (Throwable tt) {}
                        params.put("InspectorName", insp != null && !insp.trim().isEmpty() ? insp : "-");
                        params.put("ReviewerName", rev != null && !rev.trim().isEmpty() ? rev : "-");

                        // Address
                        String address = "";
                        try {
                            if (form.getCompany() != null && form.getCompany().getAddress() != null) address = form.getCompany().getAddress();
                            if ((address == null || address.trim().isEmpty()) && form.getNameAndAddressOfEmployer() != null) address = form.getNameAndAddressOfEmployer();
                        } catch (Throwable tt) {}
                        params.put("Address", address != null ? address : "");

                        // Result
                        String formResult = computeResultFromForm(form);
                        String normResult = null;
                        if (formResult != null) normResult = normalizeResultString(formResult);
                        if (normResult == null) {
                            String rawResult = extractResultFromObjects(form);
                            normResult = normalizeResultString(rawResult);
                        }
                        if (normResult == null) normResult = "-";
                        params.put("Result", normResult);
                        // expose serial number when available (avoid unused local variable)
                        params.put("SerialNo", serialDisplay != null ? serialDisplay : "-");

                        sendFallbackHtml(response, params);
                        return;
                    }
                } catch (Exception ex) {
                    log.debug("Could not load form by reportNo {}: {}", reportNo, ex.getMessage());
                }
            }

            // default fallback with placeholders
            
            String stickerDisplay = (stickerNo == null || stickerNo.trim().isEmpty()) ? "-" : stickerNo;
            Map<String, Object> params = new HashMap<>();
            params.put("ReportNo", reportNo != null ? reportNo : "");
            params.put("StNo", stickerDisplay);
            params.put("TsNo", "");
            params.put("JobNo", "");
            params.put("Company", "");
            params.put("DateOfThoroughExamination", "");
            params.put("NextExaminationDate", "");
            params.put("EquipmentCategory", "");
            params.put("InspectorName", "-");
            params.put("ReviewerName", "-");
            params.put("Address", "");
            params.put("Result", "-");
            sendFallbackHtml(response, params);
            return;
        }

        try {
            EquipmentInspectionCertificate cert = null;
            // Primary: try lookup by serialNo + stickerNo (if stickerNo provided)
            if (stickerNo != null && !stickerNo.isEmpty()) {
                cert = certificateService.getBy(serialNo, stickerNo);
            }

            // Fallback: allow reportNo (query param) or when second path segment is actually reportNo
            if (cert == null) {
                if ((reportNo == null || reportNo.isEmpty()) && (stickerNo != null && !stickerNo.isEmpty())) {
                    // maybe the caller used path where second segment was actually reportNo
                    reportNo = stickerNo;
                }
                if (reportNo != null && !reportNo.isEmpty() && equipmentInspectionFormService != null) {
                    try {
                        com.smat.ins.model.entity.EquipmentInspectionForm form = equipmentInspectionFormService.findByUniqueField("reportNo", reportNo);
                        if (form != null && form.getId() != null) {
                            cert = certificateService.getBy(form.getId());
                        }
                    } catch (Exception ex) {
                        log.debug("Could not resolve certificate by reportNo {}: {}", reportNo, ex.getMessage());
                    }
                }
            }

            if (cert == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found");
                return;
            }

            EquipmentInspectionForm form = cert.getEquipmentInspectionForm();
            // reload form with children to ensure company, users, items are available
            try {
                if (equipmentInspectionFormService != null && form != null && form.getId() != null) {
                    EquipmentInspectionForm reloaded = (EquipmentInspectionForm) equipmentInspectionFormService.findByIdWithChildren(form.getId());
                    if (reloaded != null) form = reloaded;
                }
            } catch (Throwable reloadEx) {
                log.debug("Could not reload form id {}: {}", form != null ? form.getId() : null, reloadEx.getMessage());
            }

            // Determine sticker display value (prefer sticker entity, then form.stickerNo, then passed param)
            String stickerDisplay;
            if (form != null && form.getSticker() != null && form.getSticker().getStickerNo() != null && !form.getSticker().getStickerNo().trim().isEmpty()) {
                stickerDisplay = form.getSticker().getStickerNo();
            } else if (form != null && form.getStickerNo() != null && !form.getStickerNo().trim().isEmpty()) {
                stickerDisplay = form.getStickerNo();
            } else if (stickerNo != null && !stickerNo.trim().isEmpty()) {
                // passed param (could be reportNo in some QR cases) - avoid showing reportNo here
                stickerDisplay = "-";
            } else {
                stickerDisplay = "-";
            }

            // For serial display (used in filename), prefer sticker.serialNo when available
            String serialDisplay = serialNo;
            if ((serialDisplay == null || serialDisplay.trim().isEmpty()) && form != null && form.getSticker() != null && form.getSticker().getSerialNo() != null) {
                serialDisplay = form.getSticker().getSerialNo();
            }
            if (serialDisplay == null) serialDisplay = "-";

            Map<String, Object> params = buildReportParams(cert, serialDisplay, stickerDisplay);

            String formResult = computeResultFromForm(form);
            String normResult = null;
            if (formResult != null) {
                normResult = normalizeResultString(formResult);
            }
            if (normResult == null) {
                String rawResult = extractResultFromObjects(cert, form);
                normResult = normalizeResultString(rawResult);
            }
            if (normResult == null) normResult = "-";
            params.put("Result", normResult);

            String format = request.getParameter("format");
            boolean forceHtml = "html".equalsIgnoreCase(format);

            if (forceHtml) {
                sendFallbackHtml(response, params);
                return;
            }

            String fileName = "summary_" + (params.get("ReportNo") != null && !((String) params.get("ReportNo")).isEmpty() ? params.get("ReportNo") : serialNo) + ".pdf";
            try {
                response.reset();
                response.setContentType("application/pdf");
                String encoded = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
                response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + encoded);

                try (OutputStream os = response.getOutputStream()) {
                    generatePdf(os, params);
                    os.flush();
                }
            } catch (Exception pdfEx) {
                pdfEx.printStackTrace();
                sendFallbackHtml(response, params);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    // Enhanced HTML with modern design
    private void sendFallbackHtml(HttpServletResponse response, Map<String, Object> p) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        String result = p.get("Result") != null ? String.valueOf(p.get("Result")) : "-";
        try (java.io.PrintWriter pw = response.getWriter()) {
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang='en'>");
            pw.println("<head>");
            pw.println("<meta charset='utf-8'>");
            pw.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            pw.println("<title>Certificate Summary - SMAT</title>");
            pw.println("<link href='https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
            pw.println("<style>");
            pw.println(":root {");
            pw.println("  --primary: #0e66e9;");
            pw.println("  --primary-dark: #0a56d4;");
            pw.println("  --secondary: #6c757d;");
            pw.println("  --success: #16a34a;");
            pw.println("  --danger: #dc2626;");
            pw.println("  --warning: #f59e0b;");
            pw.println("  --light: #f8f9fa;");
            pw.println("  --dark: #212529;");
            pw.println("  --border: #dee2e6;");
            pw.println("  --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);");
            pw.println("  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);");
            pw.println("}");
            pw.println("* { margin: 0; padding: 0; box-sizing: border-box; }");
            pw.println("body { font-family: 'Inter', system-ui, -apple-system, sans-serif; background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%); min-height: 100vh; padding: 24px; color: var(--dark); line-height: 1.6; }");
            pw.println(".container { max-width: 1200px; margin: 0 auto; }");
            pw.println(".header { background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%); color: white; padding: 32px; border-radius: 20px 20px 0 0; box-shadow: var(--shadow-lg); margin-bottom: 0; }");
            pw.println(".header h1 { font-size: 1.8rem; font-weight: 700; margin-bottom: 8px; letter-spacing: -0.025em; }");
            pw.println(".header .subtitle { font-size: 0.95rem; opacity: 0.9; font-weight: 400; margin-bottom: 16px; }");
            pw.println(".header-meta { display: flex; gap: 24px; flex-wrap: wrap; background: rgba(255, 255, 255, 0.15); padding: 16px 20px; border-radius: 12px; backdrop-filter: blur(10px); }");
            pw.println(".meta-item { display: flex; flex-direction: column; }");
            pw.println(".meta-label { font-size: 0.875rem; opacity: 0.8; font-weight: 500; margin-bottom: 4px; }");
            pw.println(".meta-value { font-size: 1rem; font-weight: 600; }");
            pw.println(".content-grid { display: grid; grid-template-columns: 1fr 400px; gap: 24px; margin-top: 24px; }");
            pw.println(".main-card { background: white; padding: 32px; border-radius: 0 0 0 20px; box-shadow: var(--shadow); }");
            pw.println(".sidebar { background: white; padding: 32px; border-radius: 0 0 20px 0; box-shadow: var(--shadow); }");
            pw.println(".card-title { font-size: 1.3rem; font-weight: 600; color: var(--dark); margin-bottom: 24px; padding-bottom: 16px; border-bottom: 2px solid var(--light); position: relative; }");
            pw.println(".card-title::after { content: ''; position: absolute; bottom: -2px; left: 0; width: 60px; height: 2px; background: var(--primary); }");
            pw.println(".data-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }");
            pw.println(".data-item { margin-bottom: 20px; }");
            pw.println(".data-label { font-size: 0.875rem; color: var(--secondary); font-weight: 500; margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.05em; }");
            pw.println(".data-value { font-size: 1.1rem; font-weight: 600; color: var(--dark); line-height: 1.5; }");
            pw.println(".result-section { background: linear-gradient(135deg, var(--light) 0%, #f1f3f4 100%); padding: 24px; border-radius: 16px; border-left: 4px solid var(--primary); margin: 24px 0; }");
            pw.println(".result-label { font-size: 0.9rem; color: var(--secondary); font-weight: 500; margin-bottom: 8px; }");
            pw.println(".result-badge { display: inline-flex; align-items: center; padding: 12px 24px; border-radius: 50px; font-weight: 700; font-size: 1.1rem; box-shadow: var(--shadow); }");
            pw.println(".badge-pass { background: linear-gradient(135deg, var(--success) 0%, #14b8a6 100%); color: white; }");
            pw.println(".badge-fail { background: linear-gradient(135deg, var(--danger) 0%, #ef4444 100%); color: white; }");
            pw.println(".badge-na { background: linear-gradient(135deg, var(--secondary) 0%, #9ca3af 100%); color: white; }");
            pw.println(".contact-section { background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%); color: white; padding: 32px; border-radius: 20px; box-shadow: var(--shadow-lg); margin-top: 32px; }");
            pw.println(".contact-title { font-size: 1.3rem; font-weight: 700; margin-bottom: 24px; text-align: center; }");
            pw.println(".contact-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; }");
            pw.println(".contact-item { display: flex; align-items: center; gap: 16px; padding: 16px; background: rgba(255, 255, 255, 0.15); border-radius: 12px; backdrop-filter: blur(10px); transition: transform 0.2s ease; }");
            pw.println(".contact-item:hover { transform: translateY(-2px); background: rgba(255, 255, 255, 0.2); }");
            pw.println(".contact-icon { width: 48px; height: 48px; background: rgba(255, 255, 255, 0.2); border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; flex-shrink: 0; }");
            pw.println(".contact-text { font-weight: 600; }");
            pw.println(".footer { text-align: center; margin-top: 32px; padding: 20px; color: var(--secondary); font-size: 0.875rem; }");
            pw.println(".btn-download { display: inline-flex; align-items: center; gap: 8px; background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%); color: white; padding: 12px 24px; border-radius: 10px; text-decoration: none; font-weight: 600; box-shadow: var(--shadow); transition: all 0.3s ease; border: none; cursor: pointer; }");
            pw.println(".btn-download:hover { transform: translateY(-2px); box-shadow: var(--shadow-lg); }");
            pw.println("@media (max-width: 968px) { .content-grid { grid-template-columns: 1fr; } .main-card, .sidebar { border-radius: 0 0 20px 20px; } }");
            pw.println("@media (max-width: 640px) { .data-grid { grid-template-columns: 1fr; } .header-meta { flex-direction: column; gap: 12px; } }");
            pw.println("</style>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<div class='container'>");

            // Header Section
            pw.println("<div class='header'>");
            pw.println("<h1>EQUIPMENT INSPECTION - Certificate Summary</h1>");
            pw.println("<div class='subtitle'>Professional Equipment Inspection Summary Report</div>");
            pw.println("<div class='header-meta'>");
            pw.println("<div class='meta-item'><span class='meta-label'>REPORT NO</span><span class='meta-value'>" + safeHtml(p.get("ReportNo")) + "</span></div>");
            pw.println("<div class='meta-item'><span class='meta-label'>STICKER NO</span><span class='meta-value'>" + safeHtml(p.get("StNo")) + "</span></div>");
            pw.println("<div class='meta-item'><span class='meta-label'>EQUIPMENT CATEGORY</span><span class='meta-value'>" + safeHtml(p.get("EquipmentCategory")) + "</span></div>");
            pw.println("</div>");
            pw.println("</div>");

            // Main Content Grid
            pw.println("<div class='content-grid'>");

            // Left Column - Main Content
            pw.println("<div class='main-card'>");
            pw.println("<h2 class='card-title'>Inspection Details</h2>");
            pw.println("<div class='data-grid'>");

            pw.println("<div class='data-item'><div class='data-label'>Company</div><div class='data-value'>" + safeHtml(p.get("Company")) + "</div></div>");
            pw.println("<div class='data-item'><div class='data-label'>Job Number</div><div class='data-value'>" + safeHtml(p.get("JobNo")) + "</div></div>");
            pw.println("<div class='data-item'><div class='data-label'>TS Number</div><div class='data-value'>" + safeHtml(p.get("TsNo")) + "</div></div>");
            pw.println("<div class='data-item'><div class='data-label'>Inspection Date</div><div class='data-value'>" + safeHtml(p.get("DateOfThoroughExamination")) + "</div></div>");
            pw.println("<div class='data-item'><div class='data-label'>Next Examination</div><div class='data-value'>" + safeHtml(p.get("NextExaminationDate")) + "</div></div>");
            pw.println("<div class='data-item'><div class='data-label'>Inspector</div><div class='data-value'>" + safeHtml(p.get("InspectorName")) + "</div></div>");
            pw.println("<div class='data-item'><div class='data-label'>Reviewer</div><div class='data-value'>" + safeHtml(p.get("ReviewerName")) + "</div></div>");

            pw.println("</div>"); // close data-grid

            // Address Section
            pw.println("<div class='data-item' style='grid-column: 1 / -1; margin-top: 16px;'>");
            pw.println("<div class='data-label'>Address</div>");
            pw.println("<div class='data-value' style='background: var(--light); padding: 16px; border-radius: 8px; border-left: 4px solid var(--primary);'>" + safeHtml(p.get("Address")) + "</div>");
            pw.println("</div>");

            pw.println("</div>"); // close main-card

            // Right Column - Sidebar
            pw.println("<div class='sidebar'>");
            pw.println("<h2 class='card-title'>Quick Actions</h2>");

            // Result Section
            pw.println("<div class='result-section'>");
            pw.println("<div class='result-label'>INSPECTION RESULT</div>");
            String badgeClass = "badge-na";
            if ("Pass".equalsIgnoreCase(result)) badgeClass = "badge-pass";
            else if ("Fail".equalsIgnoreCase(result)) badgeClass = "badge-fail";
            pw.println("<div class='result-badge " + badgeClass + "'>" + safeHtml(result) + "</div>");
            pw.println("</div>");

            // Download Button
            pw.println("<button class='btn-download' onclick='window.print()' style='width: 100%; justify-content: center; margin-bottom: 20px;'>");
            pw.println("<span>Print Report</span>");
            pw.println("</button>");

            pw.println("<button class='btn-download' onclick='location.href=\"?format=pdf\"' style='width: 100%; justify-content: center; background: linear-gradient(135deg, var(--success) 0%, #14b8a6 100%);'>");
            pw.println("<span>Download Full PDF</span>");
            pw.println("</button>");

            // Status Information
            pw.println("<div style='margin-top: 32px; padding: 20px; background: var(--light); border-radius: 12px;'>");
            pw.println("<div style='font-weight: 600; color: var(--dark); margin-bottom: 12px;'>Report Status</div>");
            pw.println("<div style='display: flex; align-items: center; gap: 8px; margin-bottom: 8px;'><div style='width: 8px; height: 8px; background: var(--success); border-radius: 50%;'></div><span>Completed</span></div>");
            pw.println("<div style='display: flex; align-items: center; gap: 8px;'><div style='width: 8px; height: 8px; background: var(--primary); border-radius: 50%;'></div><span>Digitally Signed</span></div>");
            pw.println("</div>");

            pw.println("</div>"); // close sidebar
            pw.println("</div>"); // close content-grid

            // Contact Section
            pw.println("<div class='contact-section'>");
            pw.println("<div class='contact-title'>Get In Touch</div>");
            pw.println("<div class='contact-grid'>");

            pw.println("<div class='contact-item'>");
            pw.println("<div class='contact-icon'>📱</div>");
            pw.println("<div class='contact-text'>");
            pw.println("<div>WhatsApp</div>");
            pw.println("<div>+966 55 859 4515</div>");
            pw.println("</div>");
            pw.println("</div>");

            pw.println("<div class='contact-item'>");
            pw.println("<div class='contact-icon'>📞</div>");
            pw.println("<div class='contact-text'>");
            pw.println("<div>Phone</div>");
            pw.println("<div>+966 55 859 4515</div>");
            pw.println("</div>");
            pw.println("</div>");

            pw.println("<div class='contact-item'>");
            pw.println("<div class='contact-icon'>✉️</div>");
            pw.println("<div class='contact-text'>");
            pw.println("<div>Email</div>");
            pw.println("<div>info@smat-ins.com</div>");
            pw.println("</div>");
            pw.println("</div>");

            pw.println("<div class='contact-item'>");
            pw.println("<div class='contact-icon'>🌐</div>");
            pw.println("<div class='contact-text'>");
            pw.println("<div>Website</div>");
            pw.println("<div>www.smat-ins.com</div>");
            pw.println("</div>");
            pw.println("</div>");

            pw.println("</div>"); // close contact-grid
            pw.println("</div>"); // close contact-section

            // Footer
            pw.println("<div class='footer'>");
            pw.println("<div>Generated on " + new java.text.SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss").format(new java.util.Date()) + "</div>");
            pw.println("<div style='margin-top: 8px;'>SMAT Inspection Services - Professional Equipment Certification</div>");
            pw.println("</div>");

            pw.println("</div>"); // close container
            pw.println("</body>");
            pw.println("</html>");
            pw.flush();
        }
    }

    // Enhanced PDF generation with modern design
    private void generatePdf(OutputStream os, Map<String, Object> p) throws Exception {
        Document document = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter writer = PdfWriter.getInstance(document, os);

        writer.setPageEvent(new PdfPageEventHandler());
        document.open();

        // Enhanced Fonts
        Font titleFont = new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f, PRIMARY_COLOR));
        Font labelFont = new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, SECONDARY_COLOR));
        Font valueFont = new Font(FontFactory.getFont(FontFactory.HELVETICA, 9f, BaseColor.BLACK));
        Font contactTitleFont = new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, BaseColor.WHITE));
        Font contactTextFont = new Font(FontFactory.getFont(FontFactory.HELVETICA, 8f, BaseColor.WHITE));
        Font footerFont = new Font(FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7f, SECONDARY_COLOR));

        // Header with logos and compact title
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{1.5f, 2f, 1.5f});
        header.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        header.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        // Left logo
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(PdfPCell.NO_BORDER);
        leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        Image leftImg = loadImageResourceOrPath((String) p.get("logoLeftPath"));
        if (leftImg != null) {
            leftImg.scaleToFit(80f, 50f);
            leftCell.addElement(leftImg);
        }
        header.addCell(leftCell);

        // Compact title cell
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(PdfPCell.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph mainTitle = new Paragraph("EQUIPMENT INSPECTION", titleFont);
        mainTitle.setAlignment(Element.ALIGN_CENTER);
        mainTitle.setSpacingAfter(2f);

        Paragraph subTitle = new Paragraph("Certificate Summary", new Font(FontFactory.getFont(FontFactory.HELVETICA, 10f, SECONDARY_COLOR)));
        subTitle.setAlignment(Element.ALIGN_CENTER);

        titleCell.addElement(mainTitle);
        titleCell.addElement(subTitle);
        header.addCell(titleCell);

        // Right logo
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(PdfPCell.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Image rightImg = loadImageResourceOrPath((String) p.get("logoRightPath"));
        if (rightImg != null) {
            rightImg.scaleToFit(80f, 50f);
            rightCell.addElement(rightImg);
        }
        header.addCell(rightCell);

        document.add(header);

        // Thin separator
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(PRIMARY_COLOR);
        separator.setPercentage(100f);
        separator.setLineWidth(1f);
        separator.setOffset(-6f);
        document.add(new Chunk(separator));

        // Compact report identification
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(8f);
        infoTable.setSpacingAfter(6f);
        infoTable.setWidths(new float[]{1f, 1f});

        PdfPCell leftInfo = createInfoCell("Report No:", (String) p.get("ReportNo"), true);
        infoTable.addCell(leftInfo);

        PdfPCell rightInfo = createInfoCell("Sticker No:", (String) p.get("StNo"), true);
        infoTable.addCell(rightInfo);

        document.add(infoTable);

        // Equipment category - compact
        String equipCat = p.get("EquipmentCategory") != null ? String.valueOf(p.get("EquipmentCategory")) : "";
        if (!equipCat.isEmpty()) {
            Paragraph categoryPara = new Paragraph();
            categoryPara.add(new Phrase("Equipment Category: ", labelFont));
            categoryPara.add(new Phrase(equipCat, valueFont));
            categoryPara.setAlignment(Element.ALIGN_CENTER);
            categoryPara.setSpacingAfter(10f);
            document.add(categoryPara);
        }

        // Main content table with improved spacing
        PdfPTable mainTable = new PdfPTable(2);
        mainTable.setWidthPercentage(100);
        mainTable.setSpacingBefore(8f);
        mainTable.setWidths(new float[]{1.2f, 2f});

        // Add rows - Examination Type removed as requested
        addEnhancedKeyValueRow(mainTable, "Company:", (String) p.get("Company"), labelFont, valueFont);
        addEnhancedKeyValueRow(mainTable, "Address:", (String) p.get("Address"), labelFont, valueFont);
        addEnhancedKeyValueRow(mainTable, "Job Number:", (String) p.get("JobNo"), labelFont, valueFont);
        addEnhancedKeyValueRow(mainTable, "TS Number:", (String) p.get("TsNo"), labelFont, valueFont);
        addEnhancedKeyValueRow(mainTable, "Inspection Date:", (String) p.get("DateOfThoroughExamination"), labelFont, valueFont);
        addEnhancedKeyValueRow(mainTable, "Next Examination:", (String) p.get("NextExaminationDate"), labelFont, valueFont);
        addEnhancedKeyValueRow(mainTable, "Inspector:", (String) p.get("InspectorName"), labelFont, valueFont);
        addEnhancedKeyValueRow(mainTable, "Reviewer:", (String) p.get("ReviewerName"), labelFont, valueFont);

        document.add(mainTable);

        // Compact Result section
        String result = p.get("Result") != null ? String.valueOf(p.get("Result")) : "-";
        BaseColor resultColor = SECONDARY_COLOR;
        if ("Pass".equalsIgnoreCase(result)) resultColor = SUCCESS_COLOR;
        else if ("Fail".equalsIgnoreCase(result)) resultColor = DANGER_COLOR;

        PdfPTable resultTable = new PdfPTable(1);
        resultTable.setWidthPercentage(70);
        resultTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        resultTable.setSpacingBefore(15f);
        resultTable.setSpacingAfter(12f);

        PdfPCell resultCell = new PdfPCell();
        resultCell.setBorder(Rectangle.NO_BORDER);
        resultCell.setBackgroundColor(LIGHT_BG);
        resultCell.setPadding(12f);
        resultCell.setBorderWidth(1f);
        resultCell.setBorderColor(PRIMARY_COLOR);

        Paragraph resultLabel = new Paragraph("INSPECTION RESULT", new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, SECONDARY_COLOR)));
        resultLabel.setAlignment(Element.ALIGN_CENTER);
        resultLabel.setSpacingAfter(6f);
        resultCell.addElement(resultLabel);

        Font resultValueFont = new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14f, resultColor));
        Paragraph resultValue = new Paragraph(result.toUpperCase(), resultValueFont);
        resultValue.setAlignment(Element.ALIGN_CENTER);
        resultCell.addElement(resultValue);

        resultTable.addCell(resultCell);
        document.add(resultTable);

        // Enhanced Contact Section without logos
        PdfPTable contactTable = new PdfPTable(1);
        contactTable.setWidthPercentage(100);
        contactTable.setSpacingBefore(15f);

        PdfPCell contactCell = new PdfPCell();
        contactCell.setBorder(Rectangle.NO_BORDER);
        contactCell.setBackgroundColor(PRIMARY_COLOR);
        contactCell.setPadding(15f);

        // Contact title
        Paragraph contactTitle = new Paragraph("CONTACT INFORMATION", contactTitleFont);
        contactTitle.setAlignment(Element.ALIGN_CENTER);
        contactTitle.setSpacingAfter(12f);
        contactCell.addElement(contactTitle);

        // Contact grid (2 columns)
        PdfPTable contactGrid = new PdfPTable(2);
        contactGrid.setWidthPercentage(100);
        contactGrid.setWidths(new float[]{1f, 1f});

        // Contact items without logos - using text only
        PdfPCell leftContact = createContactCell("WhatsApp:", "+966 55 859 4515", contactTextFont);
        contactGrid.addCell(leftContact);

        PdfPCell rightContact = createContactCell("Phone:", "+966 55 859 4515", contactTextFont);
        contactGrid.addCell(rightContact);

        // Second row
        PdfPCell emailContact = createContactCell("Email:", "info@smat-ins.com", contactTextFont);
        contactGrid.addCell(emailContact);

        PdfPCell webContact = createContactCell("Website:", "www.smat-ins.com", contactTextFont);
        contactGrid.addCell(webContact);

        contactCell.addElement(contactGrid);
        contactTable.addCell(contactCell);
        document.add(contactTable);

        // Compact footer
        Paragraph footer = new Paragraph("Generated on " + new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss").format(new java.util.Date()) + " | SMAT Inspection Services", footerFont);
        footer.setSpacingBefore(12f);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }

    // Helper method to create enhanced key-value rows
    private void addEnhancedKeyValueRow(PdfPTable table, String key, String value, Font keyFont, Font valFont) {
        if (value == null || value.trim().isEmpty()) value = "-";

        // Key cell with light background
        PdfPCell keyCell = new PdfPCell(new Phrase(key, keyFont));
        keyCell.setBorder(Rectangle.NO_BORDER);
        keyCell.setPadding(6f);
        keyCell.setPaddingLeft(10f);
        keyCell.setBackgroundColor(LIGHT_BG);
        keyCell.setBorderWidthRight(1f);
        keyCell.setBorderColorRight(BORDER_COLOR);
        table.addCell(keyCell);

        // Value cell
        PdfPCell valCell = new PdfPCell(new Phrase(value, valFont));
        valCell.setBorder(Rectangle.NO_BORDER);
        valCell.setPadding(6f);
        valCell.setPaddingLeft(10f);
        table.addCell(valCell);
    }

    // Helper method to create info cells
    private PdfPCell createInfoCell(String label, String value, boolean highlight) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4f);

        Paragraph p = new Paragraph();
        p.add(new Phrase(label + " ", new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, SECONDARY_COLOR))));
        p.add(new Phrase(value != null ? value : "-", new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, highlight ? PRIMARY_COLOR : BaseColor.BLACK))));
        p.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(p);
        return cell;
    }

    // Helper method to create contact cells without icons
    private PdfPCell createContactCell(String type, String value, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(6f);
        cell.setBackgroundColor(new BaseColor(14, 102, 233, 100));

        Paragraph p = new Paragraph();
        p.add(new Phrase(type + " ", new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8f, BaseColor.WHITE))));
        p.add(new Phrase(value, font));
        p.setAlignment(Element.ALIGN_LEFT);

        cell.addElement(p);
        return cell;
    }

    // Inner class for page events (header/footer)
    class PdfPageEventHandler extends com.itextpdf.text.pdf.PdfPageEventHelper {
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfPTable footer = new PdfPTable(1);
                footer.setTotalWidth(document.getPageSize().getWidth() - 80);
                footer.getDefaultCell().setBorder(Rectangle.TOP);
                footer.getDefaultCell().setBorderColor(BORDER_COLOR);
                footer.getDefaultCell().setBorderWidthTop(1f);
                footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                footer.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
                footer.getDefaultCell().setPaddingTop(6f);

                Font footerFont = new Font(FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7f, SECONDARY_COLOR));
                Paragraph footerText = new Paragraph("Page " + writer.getPageNumber() + " | Confidential Document - SMAT Inspection Services", footerFont);
                footer.addCell(footerText);

                footer.writeSelectedRows(0, -1, 40, 30, writer.getDirectContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Image loadImageResourceOrPath(String path) {
        try {
            if (path != null) {
                File f = new File(path);
                if (f.exists() && f.isFile()) {
                    return Image.getInstance(path);
                }
            }
            String resourceBase = "/views/jasper/images/";
            String[] candidates = {"logo-left.png", "logo-right.png"};
            if (path != null) {
                File f2 = new File(path);
                String name = f2.getName();
                candidates = new String[]{name};
            }
            for (String cand : candidates) {
                String resourcePath = resourceBase + cand;
                try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                    if (is != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buf = new byte[4096];
                        int r;
                        while ((r = is.read(buf)) != -1) baos.write(buf, 0, r);
                        return Image.getInstance(baos.toByteArray());
                    }
                } catch (Throwable t) {
                    // ignore and try next
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }

    private String extractResultFromObjects(Object... objs) {
        String[] stringGetters = {"getResult", "getStatus", "getOutcome", "getResultCode", "getDecision", "getResultText"};
        String[] booleanGetters = {"isPass", "getIsPass", "isPassed", "getIsPassed", "isOk", "getIsOk"};
        for (Object obj : objs) {
            if (obj == null) continue;
            Class<?> cls = obj.getClass();
            for (String bn : booleanGetters) {
                try {
                    Method m = cls.getMethod(bn);
                    Object val = m.invoke(obj);
                    if (val instanceof Boolean) {
                        return ((Boolean) val) ? "Pass" : "Fail";
                    } else if (val != null) {
                        String s = String.valueOf(val);
                        String norm = normalizeResultString(s);
                        if (norm != null) return norm;
                    }
                } catch (NoSuchMethodException nsme) {
                    // ignore
                } catch (Throwable t) {
                    // ignore
                }
            }
            for (String gn : stringGetters) {
                try {
                    Method m = cls.getMethod(gn);
                    Object val = m.invoke(obj);
                    if (val != null) {
                        String s = String.valueOf(val);
                        String norm = normalizeResultString(s);
                        if (norm != null) return norm;
                    }
                } catch (NoSuchMethodException nsme) {
                    // ignore
                } catch (Throwable t) {
                    // ignore
                }
            }
        }
        return null;
    }

    private String normalizeResultString(String raw) {
        if (raw == null) return null;
        String s = raw.trim().toLowerCase();
        if (s.isEmpty()) return null;
        if (s.matches("^(pass|passed|ok|success|true|1)$")) return "Pass";
        if (s.matches("^(fail|failed|ng|false|0|rejected)$")) return "Fail";
        if (s.contains("pass") || s.contains("ok") || s.contains("success")) return "Pass";
        if (s.contains("fail") || s.contains("ng") || s.contains("reject") || s.contains("no")) return "Fail";
        return null;
    }

    private String computeResultFromForm(EquipmentInspectionForm form) {
        try {
            if (form == null) return null;
            @SuppressWarnings("unchecked")
            java.util.Collection<EquipmentInspectionFormItem> items = (java.util.Collection<EquipmentInspectionFormItem>) form.getEquipmentInspectionFormItems();
            if (items == null || items.isEmpty()) return null;

            boolean anyPass = false;
            for (EquipmentInspectionFormItem it : items) {
                if (it == null) continue;
                String v = it.getItemValue();
                if (v == null) continue;
                String s = v.trim().toLowerCase();
                if (s.isEmpty()) continue;
                if (s.equals("fail") || s.equals("failed") || s.contains("fail") || s.equals("ng") || s.equals("no")) {
                    return "Fail";
                }
                if (s.equals("pass") || s.equals("passed") || s.contains("pass") || s.equals("ok") || s.equals("true")) {
                    anyPass = true;
                }
            }
            if (anyPass) return "Pass";
        } catch (Throwable t) {
            // ignore and fallback
        }
        return null;
    }
}