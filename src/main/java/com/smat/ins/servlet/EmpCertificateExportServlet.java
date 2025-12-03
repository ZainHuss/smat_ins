package com.smat.ins.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.smat.ins.model.entity.EmpCertification;
import com.smat.ins.model.service.EmpCertificationService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.UtilityHelper;
import com.smat.ins.util.CertificatePdfGenerator;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;


@WebServlet("/api/emp-cert/*")
public class EmpCertificateExportServlet extends HttpServlet {
    
    private EmpCertificationService empCertificationService;
    
    @Override
    public void init() throws ServletException {
        // Get the Spring application context
        empCertificationService = (EmpCertificationService) BeanUtility.getBean(this.getServletContext(), "empCertificationService");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Parse URL path to get certNumber and tsNumber
        String pathInfo = request.getPathInfo(); // e.g. /CERT123&TS456
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing path parameters: /certNumber&tsNumber");
            return;
        }
        
        // Remove leading slash and split by & or /
        String params = pathInfo.substring(1);
        String[] parts;
        if (params.contains("&")) {
            parts = params.split("&", 2);
        } else if (params.contains("/")) {
            parts = params.split("/", 3);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path format. Expected: /certNumber&tsNumber or /certNumber/tsNumber");
            return;
        }
        
        if (parts.length != 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path format. Expected: /certNumber&tsNumber or /certNumber/tsNumber");
            return;
        }
        
        String certNumber = parts[0];
        String tsNumber = parts[1];
        
        try {
            // Get certificate from service
            EmpCertification empCertification = empCertificationService.getByCertNumberAndTsNumber(certNumber, tsNumber);
            
            if (empCertification == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found for certNumber: " + certNumber + " and tsNumber: " + tsNumber);
                return;
            }
            
            // Generate certificate PDF on-the-fly using the new iText generator
            ServletContext context = getServletContext();
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CertNumber", empCertification.getCertNumber());
            parameters.put("TsNumber", empCertification.getTsNumber());
            parameters.put("IssueDate", empCertification.getIssueDate());
            parameters.put("ExpiryDate", empCertification.getExpiryDate());
            parameters.put("EmployeeName", empCertification.getEmployee().getFullName());
            parameters.put("CompanyName", empCertification.getEmployee().getCompany().getName());
            parameters.put("CertType", empCertification.getEmpCertificationType().getCertName());
            parameters.put("EmployeeId", empCertification.getEmployee().getIdNumber());

            parameters.put("logoLeftPath", context.getRealPath("views/jasper/images/logo-left.png"));
            parameters.put("logoRightPath", context.getRealPath("views/jasper/images/logo-right.png"));

            // keep same QR payload as before
            String qrText = getBaseURL(request) + "/api/emp-cert/" + certNumber + "&" + tsNumber;
            byte[] qrCodeBytes = generateQRCode(qrText);
            parameters.put("QRCodeImage", new java.io.ByteArrayInputStream(qrCodeBytes));

            if (empCertification.getEmployee().getEmployeePhoto() != null) {
                parameters.put("EmployeePhoto", new java.io.ByteArrayInputStream(empCertification.getEmployee().getEmployeePhoto()));
            } else {
                parameters.put("EmployeePhoto", null);
            }

            byte[] pdfData = CertificatePdfGenerator.generate(parameters, context);
            
            // Set response headers for PDF display
            response.setContentType("application/pdf");
            String safeFileName = "emp-certificate-" + certNumber + "_" + tsNumber + ".pdf";
            response.setHeader("Content-Disposition", "inline; filename=\"" + safeFileName + "\"");
            response.setContentLength(pdfData.length);
            
            // Write PDF content to response
            try (OutputStream out = response.getOutputStream()) {
                out.write(pdfData);
                out.flush();
            }
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating certificate: " + e.getMessage());
        }
    }
    
    private byte[] generateCertificatePDF(HttpServletRequest request,EmpCertification empCertification, String certNumber, String tsNumber) throws Exception {
        // Prepare parameters for Jasper report
        Map<String, Object> parameters = new HashMap<>();
        
        // Certificate data
        parameters.put("CertNumber", empCertification.getCertNumber());
        parameters.put("TsNumber", empCertification.getTsNumber());
        parameters.put("IssueDate", empCertification.getIssueDate());
        parameters.put("ExpiryDate", empCertification.getExpiryDate());
        parameters.put("EmployeeName", empCertification.getEmployee().getFullName());
        parameters.put("CompanyName", empCertification.getEmployee().getCompany().getName());
        parameters.put("CertType", empCertification.getEmpCertificationType().getCertName());
        parameters.put("EmployeeId", empCertification.getEmployee().getIdNumber());
        
       
        ServletContext context = getServletContext();
        parameters.put("logoLeftPath", context.getRealPath("views/jasper/images/logo-left.png"));
        parameters.put("logoRightPath", context.getRealPath("views/jasper/images/logo-right.png"));
        
        // Generate QR code with same algorithm as Sticker
        String qrText = getBaseURL(request) + "/api/emp-cert/" + certNumber + "&" + tsNumber;
        byte[] qrCodeBytes = generateQRCode(qrText);
        parameters.put("QRCodeImage", new java.io.ByteArrayInputStream(qrCodeBytes));
        
        // Employee photo (placeholder if not available)
        if (empCertification.getEmployee().getEmployeePhoto() != null) {
            parameters.put("EmployeePhoto", new java.io.ByteArrayInputStream(empCertification.getEmployee().getEmployeePhoto()));
        } else {
            // Use a placeholder image or null
            parameters.put("EmployeePhoto", null);
        }
        
        // Fill and export report
        String reportPath = context.getRealPath("views/jasper/emp-certification.jasper");
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, parameters, new JREmptyDataSource());
        
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    
    private byte[] generateQRCode(String text) throws Exception {
        // Use same QR code generation algorithm as StickerManagementBean
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 100, 100);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
        return baos.toByteArray();
    }
    
    private String getBaseURL(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();
        
        // Check if we need to include port
        boolean needPort = (scheme.equals("http") && port != 80) || 
                          (scheme.equals("https") && port != 443);
        
        return scheme + "://" + serverName + (needPort ? ":" + port : "") + contextPath;
    }
}
