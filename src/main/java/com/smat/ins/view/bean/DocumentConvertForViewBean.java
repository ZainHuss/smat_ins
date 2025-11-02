package com.smat.ins.view.bean;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.PageSet;
import com.aspose.words.SaveFormat;
import com.aspose.words.TiffCompression;
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
public class DocumentConvertForViewBean implements Serializable  {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
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



    public DocumentConvertForViewBean() {

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
                    + archiveDocumentFile.getCode()+"."+archiveDocumentFile.getExtension();
            String docPathPdf = mainLocation + FileSystems.getDefault().getSeparator()
                    + cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                    + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                    + FileSystems.getDefault().getSeparator() + rootPath + FileSystems.getDefault().getSeparator()
                    + archiveDocumentFile.getCode()+"_converted.pdf" ;
            Path originalPath = Paths.get(docPath);
            Path convertedPath = Paths.get(docPathPdf);
            if (!Files.exists(originalPath)) {
                throw new Exception("Original file not found: " + docPath);
            }
            long origSize = Files.size(originalPath);
            int origSample = (int) Math.min(16, origSize);
            byte[] origHeader = new byte[origSample];
            try (InputStream s = Files.newInputStream(originalPath)) {
                s.read(origHeader, 0, origSample);
            }
            System.out.println("DocumentConvertForViewBean: original=" + docPath + " size=" + origSize
                    + " header=" + bytesToHex(origHeader) + " asText='" + new String(origHeader, 0, origHeader.length) + "'");
            // If original is already PDF, stream it directly (avoid Aspose conversion)
            String ext = archiveDocumentFile.getExtension();
            if (ext != null && ext.equalsIgnoreCase("pdf")) {
                InputStream inputStream = Files.newInputStream(originalPath);
                file = DefaultStreamedContent.builder().name(archiveDocumentFile.getName())
                        .contentType("application/pdf").stream(() -> inputStream).build();
                return;
            }
            // Ensure converted PDF exists
            if (!Files.exists(convertedPath)) {
                try {
                    Document document = new Document(docPath);
                    // explicitly save as PDF to avoid format inference issues
                    document.save(docPathPdf, SaveFormat.PDF);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("DocumentConvertForViewBean: Aspose conversion failed for=" + docPath + " error=" + ex.getMessage());
                    // If conversion failed, ensure any partially-created file is removed
                    try {
                        if (Files.exists(convertedPath)) {
                            Files.delete(convertedPath);
                        }
                    } catch (Exception delEx) {
                        // ignore deletion errors but log
                        delEx.printStackTrace();
                    }
                    // inform user and stop processing
                    UtilityHelper.addErrorMessage(localizationService.getInterfaceLabel().getString("fileCouldNotPreview"));
                    return;
                }
            }
            long convSize = Files.size(convertedPath);
            int convSample = (int) Math.min(16, convSize);
            byte[] convHeader = new byte[convSample];
            try (InputStream s = Files.newInputStream(convertedPath)) {
                s.read(convHeader, 0, convSample);
            }
            System.out.println("DocumentConvertForViewBean: converted=" + docPathPdf + " size=" + convSize
                    + " header=" + bytesToHex(convHeader) + " asText='" + new String(convHeader, 0, convHeader.length) + "'");
            // validate converted file looks like a PDF (starts with %PDF)
            String convHeaderText = new String(convHeader, 0, convHeader.length);
            if (!convHeaderText.startsWith("%PDF")) {
                System.out.println("DocumentConvertForViewBean: converted file does not have PDF header - treating as conversion failure for=" + docPathPdf);
                // cleanup invalid converted file
                try {
                    Files.deleteIfExists(convertedPath);
                } catch (Exception delEx) {
                    delEx.printStackTrace();
                }
                UtilityHelper.addErrorMessage(localizationService.getInterfaceLabel().getString("fileCouldNotPreview"));
                return;
            }
            InputStream inputStream = Files.newInputStream(convertedPath);
            file = DefaultStreamedContent.builder().name(archiveDocumentFile.getName())
                    .contentType("application/pdf").stream(() -> inputStream).build();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            UtilityHelper.addErrorMessage(localizationService.getInterfaceLabel().getString("fileNotFound"));
        }


    }

    public StreamedContent getFile() {
        return file;
    }

    private static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
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
