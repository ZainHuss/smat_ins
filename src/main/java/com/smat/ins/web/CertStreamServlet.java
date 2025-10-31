package com.smat.ins.web;

import com.smat.ins.util.UtilityHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet(name = "CertStreamServlet", urlPatterns = {"/attachments/cert"})
public class CertStreamServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not available");
            return;
        }
        Object sessObj = UtilityHelper.getSessionAttr("certPdfBytes");
        if (sessObj == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not available");
            return;
        }

        byte[] pdfBytes = null;
        try {
            if (sessObj instanceof byte[]) {
                pdfBytes = (byte[]) sessObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pdfBytes == null || pdfBytes.length == 0) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not available");
            return;
        }

        resp.reset();
        resp.setContentType("application/pdf");
        String fileName = "certificate.pdf";
        resp.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        resp.setContentLength(pdfBytes.length);

        try (OutputStream out = resp.getOutputStream()) {
            out.write(pdfBytes);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // clear the session attribute once consumed
            UtilityHelper.putSessionAttr("certPdfBytes", null);
        }
    }
}
