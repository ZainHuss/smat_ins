package com.smat.ins.web;

import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.CabinetDefaultsCreator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.List;

@WebServlet(name = "ZipAttachmentsServlet", urlPatterns = {"/attachments/zip"})
public class ZipAttachmentsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type"); // "ins" or "emp"
        String reportNo = req.getParameter("reportNo");
        String certNo = req.getParameter("certNo");

        try {
            String targetCabinetCode;
            String folderName = null;
            if ("emp".equalsIgnoreCase(type)) {
                targetCabinetCode = "EMP-DEFAULT";
                folderName = certNo;
            } else {
                targetCabinetCode = "INS-DEFAULT";
                folderName = reportNo;
            }
            if (folderName == null || folderName.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing reportNo/certNo");
                return;
            }

            // find cabinet and drawer
            com.smat.ins.model.service.CabinetService cabinetService = (com.smat.ins.model.service.CabinetService) BeanUtility.getBean(req.getServletContext(), "cabinetService");
            com.smat.ins.model.service.CabinetDefinitionService cabinetDefinitionService = (com.smat.ins.model.service.CabinetDefinitionService) BeanUtility.getBean(req.getServletContext(), "cabinetDefinitionService");
            com.smat.ins.model.service.CabinetFolderService cabinetFolderService = (com.smat.ins.model.service.CabinetFolderService) BeanUtility.getBean(req.getServletContext(), "cabinetFolderService");

            com.smat.ins.model.entity.Cabinet targetCabinet = null;
            for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
            }
            if (targetCabinet == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Target cabinet not found");
                return;
            }

            com.smat.ins.model.entity.CabinetDefinition def = null;
            if (targetCabinet.getCabinetDefinitions() != null) {
                for (Object od : targetCabinet.getCabinetDefinitions()) {
                    com.smat.ins.model.entity.CabinetDefinition cd = (com.smat.ins.model.entity.CabinetDefinition) od;
                    if ("01".equals(cd.getCode())) { def = cd; break; }
                }
            }
            if (def == null) {
                if (targetCabinet.getCabinetDefinitions() != null && !targetCabinet.getCabinetDefinitions().isEmpty())
                    def = (com.smat.ins.model.entity.CabinetDefinition) targetCabinet.getCabinetDefinitions().iterator().next();
            }
            if (def == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Cabinet definition not found");
                return;
            }

            // find folder by name
            com.smat.ins.model.entity.CabinetFolder cabinetFolder = null;
            List<com.smat.ins.model.entity.CabinetFolder> existing = cabinetFolderService.getByCabinetDefinition(def);
            if (existing != null) {
                for (com.smat.ins.model.entity.CabinetFolder f : existing) {
                    String fn = folderName == null ? "" : folderName.trim().toLowerCase();
                    String fa = f.getArabicName() == null ? "" : f.getArabicName().trim().toLowerCase();
                    String fe = f.getEnglishName() == null ? "" : f.getEnglishName().trim().toLowerCase();
                    if (fn.equals(fa) || fn.equals(fe)) {
                        cabinetFolder = f;
                        break;
                    }
                }
            }
            if (cabinetFolder == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Folder not found");
                return;
            }

            // determine physical folder path
            String mainLocation = CabinetDefaultsCreator.selectMainLocation(targetCabinet.getCabinetLocation());
            String sep = FileSystems.getDefault().getSeparator();
            Path folderPath = Paths.get(mainLocation, targetCabinet.getCode(), def.getCode(), cabinetFolder.getCode());
            if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No files for folder");
                return;
            }

            String zipName = folderName.replaceAll("[^a-zA-Z0-9._-]", "_") + "_attachments.zip";
            resp.setContentType("application/zip");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + "\"");

            try (OutputStream out = resp.getOutputStream(); ZipOutputStream zos = new ZipOutputStream(out)) {
                // Prefer zipping only files registered in DB (ArchiveDocumentFile linked to this cabinetFolder).
                com.smat.ins.model.service.CabinetFolderDocumentService cabinetFolderDocumentService = (com.smat.ins.model.service.CabinetFolderDocumentService) BeanUtility.getBean(req.getServletContext(), "cabinetFolderDocumentService");
                com.smat.ins.model.service.ArchiveDocumentFileService archiveDocumentFileService = (com.smat.ins.model.service.ArchiveDocumentFileService) BeanUtility.getBean(req.getServletContext(), "archiveDocumentFileService");

                java.util.List<com.smat.ins.model.entity.CabinetFolderDocument> items = cabinetFolderDocumentService.getByCabinetFolder(cabinetFolder);
                if (items != null) {
                    for (com.smat.ins.model.entity.CabinetFolderDocument cfd : items) {
                        com.smat.ins.model.entity.ArchiveDocument ad = cfd.getArchiveDocument();
                        if (ad == null) continue;
                        java.util.List<com.smat.ins.model.entity.ArchiveDocumentFile> files = archiveDocumentFileService.getBy(ad);
                        if (files == null) continue;
                        for (com.smat.ins.model.entity.ArchiveDocumentFile df : files) {
                            try {
                                if (df == null || df.getServerPath() == null) continue;
                                Path p = Paths.get(df.getServerPath());
                                if (!Files.exists(p) || !Files.isRegularFile(p)) continue;
                                // ensure the file is inside the requested folderPath
                                if (!p.toAbsolutePath().startsWith(folderPath.toAbsolutePath())) {
                                    // skip files that are outside the folder (safety)
                                    continue;
                                }
                                Path rel = folderPath.relativize(p);
                                ZipEntry ze = new ZipEntry(rel.toString().replace('\\', '/'));
                                zos.putNextEntry(ze);
                                Files.copy(p, zos);
                                zos.closeEntry();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
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
