package com.smat.ins.view.bean;

import java.io.InputStream;
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
import com.smat.ins.model.entity.ArchiveDocumentFile;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.service.ArchiveDocumentFileService;
import com.smat.ins.model.service.ArchiveDocumentService;
import com.smat.ins.model.service.CabinetDefinitionService;
import com.smat.ins.model.service.CabinetFolderService;
import com.smat.ins.model.service.CabinetService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

@Named
@RequestScoped
public class DocumentDownloadBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5577125910163790815L;
	
	private StreamedContent file;
	private Long archDocId;
	private Long cabinetFolderId;
	private Long cabinetId;
	private Long archDocFileId;
	private Long cabinetDefinitionId;
	
	private ArchiveDocument archiveDocument;
	private CabinetFolder cabinetFolder;
	private Cabinet cabinet;
	private CabinetDefinition cabinetDefinition;
	private ArchiveDocumentFile archiveDocumentFile;
	private ArchiveDocumentService archiveDocumentService;
	private ArchiveDocumentFileService archiveDocumentFileService;
	private CabinetFolderService cabinetFolderService;
	private CabinetService cabinetService;
	private CabinetDefinitionService cabinetDefinitionService;
	private LocalizationService localizationService;
	


    public DocumentDownloadBean() {
    	archiveDocumentService=(ArchiveDocumentService) BeanUtility.getBean("archiveDocumentService");
    	cabinetFolderService=(CabinetFolderService) BeanUtility.getBean("cabinetFolderService");
        cabinetService=(CabinetService) BeanUtility.getBean("cabinetService");
        archiveDocumentFileService=(ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");
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
			if (UtilityHelper.getSessionAttr("archDocFileId") != null)
				archDocFileId = (Long) UtilityHelper.getSessionAttr("archDocFileId");
			if (UtilityHelper.getSessionAttr("cabinetDefinitionId") != null)
				cabinetDefinitionId = (Long) UtilityHelper.getSessionAttr("cabinetDefinitionId");
    	
			archiveDocument=archiveDocumentService.findById(archDocId);
			archiveDocumentFile=archiveDocumentFileService.findById(archDocFileId);
			cabinetFolder=cabinetFolderService.findById(cabinetFolderId);
			cabinet=cabinetService.findById(cabinetId);
			cabinetDefinition=cabinetDefinitionService.findById(cabinetDefinitionId);
			
			String rootPath = getRootPath(archiveDocument, cabinetFolderId);
			String mainLocation="";
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				mainLocation = cabinet.getCabinetLocation().getWinLocation();
			} else if (os.contains("osx")) {
				mainLocation = cabinet.getCabinetLocation().getMacLocation();
			} else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
				mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
			}
			String docPath = mainLocation + FileSystems.getDefault().getSeparator()
					+ cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
					+ FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
					+ FileSystems.getDefault().getSeparator() + rootPath + FileSystems.getDefault().getSeparator()
					+ archiveDocumentFile.getCode()+"."+archiveDocumentFile.getExtension() ;
			InputStream inputStream = Files.newInputStream(Paths.get(docPath));
			file = DefaultStreamedContent.builder().name(archiveDocumentFile.getName())
					.contentType(archiveDocumentFile.getMimeType()).stream(() -> inputStream).build();
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
