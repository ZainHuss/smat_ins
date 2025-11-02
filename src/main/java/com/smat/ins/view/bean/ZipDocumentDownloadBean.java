package com.smat.ins.view.bean;

import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

import javax.inject.Named;

import org.primefaces.PrimeFaces;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.service.ArchiveDocumentService;
import com.smat.ins.model.service.CabinetDefinitionService;
import com.smat.ins.model.service.CabinetFolderService;
import com.smat.ins.model.service.CabinetService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

@Named
@RequestScoped
public class ZipDocumentDownloadBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5577125910163790815L;
	
	private StreamedContent file;
	private Long archDocId;
	private Long cabinetFolderId;
	private Long cabinetId;
	private Long cabinetDefinitionId;
	
	private ArchiveDocument archiveDocument;
	private CabinetFolder cabinetFolder;
	private Cabinet cabinet;
	private CabinetDefinition cabinetDefinition;
	private ArchiveDocumentService archiveDocumentService;
	private CabinetFolderService cabinetFolderService;
	private CabinetService cabinetService;
	private CabinetDefinitionService cabinetDefinitionService;
	private LocalizationService localizationService;
	


    public ZipDocumentDownloadBean() {
    	archiveDocumentService=(ArchiveDocumentService) BeanUtility.getBean("archiveDocumentService");
    	cabinetFolderService=(CabinetFolderService) BeanUtility.getBean("cabinetFolderService");
        cabinetService=(CabinetService) BeanUtility.getBean("cabinetService");
        cabinetDefinitionService=(CabinetDefinitionService) BeanUtility.getBean("cabinetDefinitionService");
        localizationService=(LocalizationService) BeanUtility.getBean("localizationService");
    }

    @PostConstruct
    public void init() {
    	try {
			if (UtilityHelper.getSessionAttr("archDocId") != null)
				archDocId = (Long) UtilityHelper.getSessionAttr("archDocId");
			if (UtilityHelper.getSessionAttr("cabinetFolderId") != null)
				cabinetFolderId = (Long) UtilityHelper.getSessionAttr("cabinetFolderId");
			if (UtilityHelper.getSessionAttr("cabinetId") != null)
				cabinetId = (Long) UtilityHelper.getSessionAttr("cabinetId");

			if (UtilityHelper.getSessionAttr("cabinetDefinitionId") != null)
				cabinetDefinitionId = (Long) UtilityHelper.getSessionAttr("cabinetDefinitionId");

			archiveDocument=archiveDocumentService.findById(archDocId);
			cabinetFolder=cabinetFolderService.findById(cabinetFolderId);
			cabinet=cabinetService.findById(cabinetId);
			cabinetDefinition=cabinetDefinitionService.findById(cabinetDefinitionId);

			// We must zip only files that are physically inside the cabinet folder
			// identified by cabinetFolder (i.e. the folder that corresponds to the
			// reportNo/certNo). Previously code appended a "rootPath" derived from
			// ArchiveDocument ancestry which could point inside other subfolders and
			// lead to including files the user did not upload. Build base path using
			// the cabinet/cabinetDefinition/cabinetFolder only.
			String mainLocation="";
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				mainLocation = cabinet.getCabinetLocation().getWinLocation();
			} else if (os.contains("osx")) {
				mainLocation = cabinet.getCabinetLocation().getMacLocation();
			} else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
				mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
			}

	    // base path points to the physical folder that matches the CabinetFolder
	    // (this corresponds to the folder named by reportNo/certNo)
	    Path base = Paths.get(mainLocation, cabinet.getCode(), cabinetDefinition.getCode(), cabinetFolder.getCode());
			Path zipPath = base.resolve("all.zip");
			if (!Files.exists(zipPath)) {
				// Create zip but include only files registered in DB for this cabinetFolder.
				try {
					Files.createDirectories(base);
					// collect files from DB
					com.smat.ins.model.service.CabinetFolderDocumentService cabinetFolderDocumentService = (com.smat.ins.model.service.CabinetFolderDocumentService) BeanUtility.getBean("cabinetFolderDocumentService");
					com.smat.ins.model.service.ArchiveDocumentFileService archiveDocumentFileService = (com.smat.ins.model.service.ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");

					try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
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
										if (!p.toAbsolutePath().startsWith(base.toAbsolutePath())) continue; // safety
										Path rel = base.relativize(p);
										ZipEntry ze = new ZipEntry(rel.toString().replace('\\', '/'));
										zos.putNextEntry(ze);
										Files.copy(p, zos);
										zos.closeEntry();
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					PrimeFaces.current().executeScript("PF('statusDialog').hide();");
					UtilityHelper.addErrorMessage(localizationService.getInterfaceLabel().getString("fileNotFound"));
					return;
				}
			}
			InputStream inputStream = Files.newInputStream(zipPath);
			file = DefaultStreamedContent.builder().name("all").contentType("application/zip").stream(() -> inputStream).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			PrimeFaces.current().executeScript("PF('statusDialog').hide();");
			UtilityHelper.addErrorMessage(localizationService.getInterfaceLabel().getString("fileNotFound"));
		}


    }

    public StreamedContent getFile() {
        return file;
    }

    private String getRootPath(ArchiveDocument archiveDocument, Long cabinetFolderId) throws Exception {
    	String rootPath = "";
		List<ArchiveDocument> result=new ArrayList<ArchiveDocument>();
		result.add(archiveDocument);
		while (archiveDocument.getArchiveDocument() != null) {
			archiveDocument = archiveDocumentService.getParentArchiveDocument(archiveDocument, cabinetFolderId);
			result.add(archiveDocument);
		}

		int i=result.size();

		for (;i>0;) {
			i--;
			ArchiveDocument documentItem=result.get(i);
			rootPath+=documentItem.getCode()+FileSystems.getDefault().getSeparator();
		}
		return rootPath;
	}

}
