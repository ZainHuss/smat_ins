package com.smat.ins.web;

import com.smat.ins.util.BeanUtility;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ZipByIdsServlet", urlPatterns = {"/attachments/zipByIds"})
public class ZipByIdsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idsParam = req.getParameter("ids");
        if (idsParam == null || idsParam.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing ids parameter");
            return;
        }

        try {
            String[] parts = idsParam.split(",");
            List<com.smat.ins.model.entity.ArchiveDocumentFile> files = new ArrayList<>();
            com.smat.ins.model.service.ArchiveDocumentFileService archiveDocumentFileService = (com.smat.ins.model.service.ArchiveDocumentFileService) BeanUtility.getBean(req.getServletContext(), "archiveDocumentFileService");
            for (String p : parts) {
                try {
                    Long id = Long.valueOf(p.trim());
                    com.smat.ins.model.entity.ArchiveDocumentFile f = archiveDocumentFileService.findById(id);
                    if (f != null) files.add(f);
                } catch (Exception ignored) { }
            }

            if (files.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No files found for provided ids");
                return;
            }

            resp.setContentType("application/zip");
            resp.setHeader("Content-Disposition", "attachment; filename=attachments.zip");

            try (OutputStream out = resp.getOutputStream(); ZipOutputStream zos = new ZipOutputStream(out)) {
                for (com.smat.ins.model.entity.ArchiveDocumentFile adf : files) {
                    try {
                        String serverPath = adf.getServerPath();
                        Path filePath = Paths.get(serverPath);
                        if (Files.isDirectory(filePath)) {
                            // append code + extension if folder
                            String fileName = adf.getCode() + (adf.getExtension() != null ? "." + adf.getExtension() : "");
                            filePath = filePath.resolve(fileName);
                        }
                        if (!Files.exists(filePath)) continue;
                        String entryName = (adf.getName() != null && !adf.getName().trim().isEmpty()) ? adf.getName() : filePath.getFileName().toString();
                        ZipEntry ze = new ZipEntry(entryName.replace('\\', '/'));
                        zos.putNextEntry(ze);
                        Files.copy(filePath, zos);
                        zos.closeEntry();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                zos.finish();
                zos.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating zip: " + e.getMessage());
        }
    }
}
