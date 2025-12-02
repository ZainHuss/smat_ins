package com.smat.ins.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/attachments/previewPdf")
public class PreviewPdfServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing token");
            return;
        }

        String sessionKey = "previewPdf_" + token;
        Object obj = req.getSession().getAttribute(sessionKey);
        if (obj == null || !(obj instanceof byte[])) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Preview not found or expired");
            return;
        }

        byte[] pdfBytes = (byte[]) obj;

        resp.reset();
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=inspection_preview.pdf");
        resp.setContentLength(pdfBytes.length);
        try {
            resp.getOutputStream().write(pdfBytes);
            resp.getOutputStream().flush();
        } finally {
            // remove token from session to avoid memory leak
            try { req.getSession().removeAttribute(sessionKey); } catch (Exception ignore) {}
        }
    }
}
