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
    
    @Override
    public void init() throws ServletException {
        // Get the Spring application context
    	equipmentInspectionCertificateService = (EquipmentInspectionCertificateService) BeanUtility.getBean(this.getServletContext(), "equipmentInspectionCertificateService");
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
        
        // Validate required parameters
        if (serialNo == null || serialNo.isEmpty() || stickerNo == null || stickerNo.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters: serialNo and stickerNo");
            return;
        }
        
        // Get certificate from service
        EquipmentInspectionCertificate certificate = equipmentInspectionCertificateService.getBy(serialNo, stickerNo);
        
        // Check if certificate exists and has document
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
    }
}
