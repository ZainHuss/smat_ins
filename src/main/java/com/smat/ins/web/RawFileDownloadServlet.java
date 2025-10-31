package com.smat.ins.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.smat.ins.model.entity.ArchiveDocumentFile;
import com.smat.ins.model.service.ArchiveDocumentFileService;
import com.smat.ins.util.BeanUtility;

/**
 * Simple servlet to stream the raw stored file by archiveDocumentFile id.
 * Usage: /attachments/raw?archDocFileId=123
 */
@WebServlet(urlPatterns = "/attachments/raw")
public class RawFileDownloadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ArchiveDocumentFileService archiveDocumentFileService;

    @Override
    public void init() throws ServletException {
        super.init();
        archiveDocumentFileService = (ArchiveDocumentFileService) BeanUtility.getBean(getServletContext(), "archiveDocumentFileService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("archDocFileId");
        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "archDocFileId is required");
            return;
        }

        Long id = null;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid archDocFileId");
            return;
        }

        ArchiveDocumentFile docFile = null;
        try {
            docFile = archiveDocumentFileService.findById(id);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error looking up file record: " + e.getMessage());
            return;
        }
        if (docFile == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String serverPath = docFile.getServerPath();
        String filePath = serverPath;
        // If serverPath looks like a directory (ends with separator), append code+ext
        if (serverPath != null && (serverPath.endsWith("\\") || serverPath.endsWith("/"))) {
            filePath = serverPath + docFile.getCode() + "." + docFile.getExtension();
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found on disk: " + filePath);
            return;
        }

        String mime = docFile.getMimeType();
        if (mime == null || mime.trim().isEmpty()) mime = "application/octet-stream";

        resp.setContentType(mime);
        String escapedName = docFile.getName();
        if (escapedName == null || escapedName.trim().isEmpty()) {
            escapedName = docFile.getCode() + "." + docFile.getExtension();
        }
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + escapedName + "\"");

        try (InputStream in = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                resp.getOutputStream().write(buffer, 0, len);
            }
            resp.getOutputStream().flush();
        } catch (IOException e) {
            throw e;
        }
    }
}
