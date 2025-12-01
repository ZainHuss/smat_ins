package com.smat.ins.servlet;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.smat.ins.model.entity.EquipmentInspectionCertificate;
import com.smat.ins.model.service.EquipmentInspectionCertificateService;
import com.smat.ins.util.BeanUtility;

@WebServlet("/api/equipment-cert/*")
public class EquipmentCertificateExportServlet extends HttpServlet {

    private EquipmentInspectionCertificateService equipmentInspectionCertificateService;
    private com.smat.ins.model.service.EquipmentInspectionFormService equipmentInspectionFormService;

    @Override
    public void init() throws ServletException {
        // Get the Spring application context
        equipmentInspectionCertificateService = (EquipmentInspectionCertificateService) BeanUtility.getBean(this.getServletContext(), "equipmentInspectionCertificateService");
        try {
            equipmentInspectionFormService = (com.smat.ins.model.service.EquipmentInspectionFormService) BeanUtility.getBean(this.getServletContext(), "equipmentInspectionFormService");
        } catch (Exception ignore) {
            equipmentInspectionFormService = null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Try query parameters first
        String serialNo = request.getParameter("serialNo");
        String stickerNo = request.getParameter("stickerNo");

        // If not provided as query params, try to parse from pathInfo:
        // Accept both /api/equipment-cert/serial&sticker  and /api/equipment-cert/serial/sticker
        if ((serialNo == null || stickerNo == null) ) {
            String pathInfo = request.getPathInfo(); // e.g. /202509200001&SK00001
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
                if (parts.length >= 2) {
                    serialNo = (serialNo == null) ? parts[0] : serialNo;
                    stickerNo = (stickerNo == null) ? parts[1] : stickerNo;
                }
            }
        }

        // If stickerNo is missing, accept reportNo instead (either as query param or as second path segment)
        String reportNo = request.getParameter("reportNo");

        EquipmentInspectionCertificate certificate = null;
        try {
            if ((serialNo == null || serialNo.isEmpty()) || ((stickerNo == null || stickerNo.isEmpty()) && (reportNo == null || reportNo.isEmpty()))) {
                // try parse from pathInfo if present (existing behavior)
                String pathInfo = request.getPathInfo(); // e.g. /202509200001&SK00001 or /serial&reportNo
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
                        // treat second part as stickerNo OR reportNo; we'll decide below
                        if (stickerNo == null || stickerNo.isEmpty()) {
                            // prefer stickerNo as-is, but if missing we'll treat as reportNo
                            stickerNo = parts[1];
                        }
                    }
                }
            }

            // Primary lookup: serialNo + stickerNo (backwards-compatible)
            if (serialNo != null && !serialNo.isEmpty() && stickerNo != null && !stickerNo.isEmpty()) {
                certificate = equipmentInspectionCertificateService.getBy(serialNo, stickerNo);
            }

            // If not found and reportNo provided, lookup by reportNo
            if (certificate == null) {
                if ((reportNo == null || reportNo.isEmpty()) && (stickerNo != null && !stickerNo.isEmpty())) {
                    // maybe the caller used path where second segment was actually reportNo
                    // try treat stickerNo variable as reportNo
                    reportNo = stickerNo;
                }
                if (reportNo != null && !reportNo.isEmpty() && equipmentInspectionFormService != null) {
                    try {
                        com.smat.ins.model.entity.EquipmentInspectionForm form = equipmentInspectionFormService.findByUniqueField("reportNo", reportNo);
                        if (form != null && form.getId() != null) {
                            certificate = equipmentInspectionCertificateService.getBy(form.getId());
                        }
                    } catch (Exception ex) {
                        // ignore and continue; certificate stays null
                    }
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters or invalid request");
            return;
        }

        // If caller explicitly asked for the raw full certificate (keep backward-compatible access),
        // return the stored PDF directly. Otherwise show a small landing page that embeds the
        // summary PDF and shows a link/button to download the full certificate.
        String downloadFlag = request.getParameter("download");
        String rawFlag = request.getParameter("raw");
        boolean returnRaw = (downloadFlag != null && ("full".equalsIgnoreCase(downloadFlag) || "raw".equalsIgnoreCase(downloadFlag))) || (rawFlag != null && ("1".equals(rawFlag) || "true".equalsIgnoreCase(rawFlag)));

        if (returnRaw) {
            // Check certificate exists
            if (certificate == null || certificate.getCertificateDocument() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found");
                return;
            }

            // Set response headers for PDF download / inline display
            response.setContentType("application/pdf");
            // Avoid problematic characters in filename (replace & with _)
            String safeFileName = "certificate-" + serialNo + "_" + stickerNo + ".pdf";
            response.setHeader("Content-Disposition", "inline; filename=\"" + safeFileName + "\"");
            response.setContentLength(certificate.getCertificateDocument().length);

            // Write PDF content to response
            try (OutputStream out = response.getOutputStream()) {
                out.write(certificate.getCertificateDocument());
                out.flush();
            }
            return;
        }

        // Landing page (HTML) that embeds the generated summary PDF and provides a link/button
        // to download the full stored certificate. This keeps the stored file accessible while
        // showing a lightweight, attractive summary by default.
        String ctx = request.getContextPath();
        String summaryUrl = ctx + "/api/equipment-cert/summary?serialNo=" + java.net.URLEncoder.encode(serialNo, "UTF-8") + "&stickerNo=" + java.net.URLEncoder.encode(stickerNo, "UTF-8");
        String fullUrl = ctx + request.getServletPath() + "/" + java.net.URLEncoder.encode(serialNo + "&" + stickerNo, "UTF-8") + "?download=full";

        response.setContentType("text/html; charset=UTF-8");
        try (java.io.PrintWriter pw = response.getWriter()) {
            pw.println("<!doctype html><html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">" +
                    "<title>Certificate</title>");
            pw.println("<style>body{font-family:Arial,Helvetica,sans-serif;margin:0;padding:0;background:#f6f8fb;color:#222} .wrap{max-width:900px;margin:24px auto;padding:18px;background:#fff;border-radius:8px;box-shadow:0 2px 8px rgba(0,0,0,0.08)} .hdr{display:flex;justify-content:space-between;align-items:center} .btn{display:inline-block;padding:10px 16px;background:#0b6efd;color:#fff;text-decoration:none;border-radius:6px;font-weight:600} .muted{color:#666;font-size:0.95rem} iframe{width:100%;height:700px;border:0;margin-top:12px;border-radius:4px}</style>");
            pw.println("</head><body><div class=\"wrap\"><div class=\"hdr\"><div><h2 style=\"margin:0;\">Inspection Certificate</h2><div class=\"muted\">Serial: " + serialNo + " &amp; Sticker: " + stickerNo + "</div></div><div><a class=\"btn\" href=\"" + fullUrl + "\">Download Full Certificate</a></div></div>");
            pw.println("<p class=\"muted\">Below is a short summary of the report. Use the button to download the full certificate and attachments.</p>");
            pw.println("<iframe src=\"" + summaryUrl + "\"></iframe>");
            pw.println("</div></body></html>");
            pw.flush();
        }
    }
}
