package com.smat.ins.view.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FileUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.ResponsiveOption;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.StraightConnector;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.endpoint.RectangleEndPoint;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import org.primefaces.model.diagram.overlay.LabelOverlay;
import org.primefaces.shaded.commons.io.FilenameUtils;

import com.aspose.pdf.Document;

import com.aspose.pdf.TextFragmentAbsorber;
import com.aspose.pdf.TextFragmentCollection;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.ArchiveDocumentFile;
import com.smat.ins.model.entity.ArchiveDocumentType;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.entity.CabinetFolderDocument;
import com.smat.ins.model.entity.CabinetType;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.entity.utility.LanguageOcr;
import com.smat.ins.model.service.ArchiveDocumentFileService;
import com.smat.ins.model.service.ArchiveDocumentService;
import com.smat.ins.model.service.ArchiveDocumentTypeService;
import com.smat.ins.model.service.CabinetDefinitionService;
import com.smat.ins.model.service.CabinetFolderService;
import com.smat.ins.model.service.CabinetService;
import com.smat.ins.model.service.OrganizationService;
import com.smat.ins.model.service.UserAliasService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LanguageUtil;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;
import com.smat.ins.util.model.Language;
import com.smat.ins.util.model.NetworkElement;
import com.smat.ins.view.app.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;



import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject; // أو @ManagedProperty حسب حالة مشروعك
import javax.inject.Named;
import javax.faces.view.ViewScoped;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.ArchiveDocumentFile;
import com.smat.ins.model.service.ArchiveDocumentService;
import com.smat.ins.model.service.CabinetFolderService; // إن وُجد وتحتاجه


import net.sourceforge.tess4j.Tesseract;

@Named
@ViewScoped
public class MyCabinetBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3890306657863713951L;
    private static final Logger LOG = LoggerFactory.getLogger(MyCabinetBean.class);
    // #region "properties"

    private Long cabinetID;
    private String cabinetStr;

    private Long cabinetDefinitionID;
    private String cabinetDefinitionStr;

    private Long cabinetFolderId;
    private String cabinetFolderStr;

    private Short pageIndex;
    private Boolean viewDocFiles;

    private SysUser sysUser;
    private UserAlias myUserAlias;
    private Cabinet cabinet;
    private CabinetDefinition cabinetDefinition;
    private CabinetFolder cabinetFolder;
    private ArchiveDocument archiveDocument;
    private ArchiveDocument selectedArchiveDocument;
    private StreamedContent file;

    private Long archDocFileParam;
    private Long archDocParam;
    private Long cabinetParam;
    private Long cabinetFolderParam;
    private Long cabinetDefinitionParam;
    private String docUrl;

    private TreeNode root;
    private TreeNode node;
    private TreeNode selectedNode;

    private List<Cabinet> myCabinets;
    private List<UserAlias> myUserAliasList;
    private List<CabinetDefinition> cabinetDefinitionList;
    private List<CabinetFolder> cabinetFolderList;
    private List<ArchiveDocumentType> archiveDocumentTypes;
    private List<ArchiveDocument> archiveDocuments;
    private List<ArchiveDocument> archiveDocumentChildrens;
    private List<Language> languages;
    private List<ArchiveDocumentFile> archiveDocumentFiles;
    private List<ArchiveDocument> breadcrumbList;

    private List<ResponsiveOption> responsiveOptions;

    private ResourceBundle applicationSetting;
    private Integer folderCodeLength;
    private Integer documentCodeLength;

    private String searchKey;

    public ArchiveDocument getSelectedArchiveDocument() {
        return selectedArchiveDocument;
    }

    public void setSelectedArchiveDocument(ArchiveDocument selectedArchiveDocument) {
        this.selectedArchiveDocument = selectedArchiveDocument;
    }

    public StreamedContent getFile() {
        return file;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getNode() {
        return node;
    }

    public void setNode(TreeNode node) {
        this.node = node;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public List<ArchiveDocument> getArchiveDocuments() {
        return archiveDocuments;
    }

    public void setArchiveDocuments(List<ArchiveDocument> archiveDocuments) {
        this.archiveDocuments = archiveDocuments;
    }

    public List<ArchiveDocument> getArchiveDocumentChildrens() {
        return archiveDocumentChildrens;
    }

    public void setArchiveDocumentChildrens(List<ArchiveDocument> archiveDocumentChildrens) {
        this.archiveDocumentChildrens = archiveDocumentChildrens;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public Short getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Short pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Boolean getViewDocFiles() {
        return viewDocFiles;
    }

    public void setViewDocFiles(Boolean viewDocFiles) {
        this.viewDocFiles = viewDocFiles;
    }

    public ArchiveDocument getArchiveDocument() {
        return archiveDocument;
    }

    public void setArchiveDocument(ArchiveDocument archiveDocument) {
        this.archiveDocument = archiveDocument;
    }

    public List<Cabinet> getMyCabinets() {
        return myCabinets;
    }

    public void setMyCabinets(List<Cabinet> myCabinets) {
        this.myCabinets = myCabinets;
    }

    public List<UserAlias> getMyUserAliasList() {
        return myUserAliasList;
    }

    public void setMyUserAliasList(List<UserAlias> myUserAliasList) {
        this.myUserAliasList = myUserAliasList;
    }

    public UserAlias getMyUserAlias() {
        return myUserAlias;
    }

    public void setMyUserAlias(UserAlias myUserAlias) {
        this.myUserAlias = myUserAlias;
    }

    public List<ResponsiveOption> getResponsiveOptions() {
        return responsiveOptions;
    }

    public void setResponsiveOptions(List<ResponsiveOption> responsiveOptions) {
        this.responsiveOptions = responsiveOptions;
    }

    public List<CabinetDefinition> getCabinetDefinitionList() {
        return cabinetDefinitionList;
    }

    public void setCabinetDefinitionList(List<CabinetDefinition> cabinetDefinitionList) {
        this.cabinetDefinitionList = cabinetDefinitionList;
    }

    public List<ArchiveDocumentType> getArchiveDocumentTypes() {
        return archiveDocumentTypes;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public void setArchiveDocumentTypes(List<ArchiveDocumentType> archiveDocumentTypes) {
        this.archiveDocumentTypes = archiveDocumentTypes;
    }

    public List<ArchiveDocumentFile> getArchiveDocumentFiles() {
        return archiveDocumentFiles;
    }

    public void setArchiveDocumentFiles(List<ArchiveDocumentFile> archiveDocumentFiles) {
        this.archiveDocumentFiles = archiveDocumentFiles;
    }

    public List<ArchiveDocument> getBreadcrumbList() {
        return breadcrumbList;
    }

    public void setBreadcrumbList(List<ArchiveDocument> breadcrumbList) {
        this.breadcrumbList = breadcrumbList;
    }

    public Long getCabinetID() {
        return cabinetID;
    }

    public String getCabinetStr() {
        return cabinetStr;
    }

    public void setCabinetID(Long cabinetID) {
        this.cabinetID = cabinetID;
    }

    public void setCabinetStr(String cabinetStr) {
        this.cabinetStr = cabinetStr;
    }

    public CabinetDefinition getCabinetDefinition() {
        return cabinetDefinition;
    }

    public void setCabinetDefinition(CabinetDefinition cabinetDefinition) {
        this.cabinetDefinition = cabinetDefinition;
    }

    public List<CabinetFolder> getCabinetFolderList() {
        return cabinetFolderList;
    }

    public void setCabinetFolderList(List<CabinetFolder> cabinetFolderList) {
        this.cabinetFolderList = cabinetFolderList;
    }

    public CabinetFolder getCabinetFolder() {
        return cabinetFolder;
    }

    public void setCabinetFolder(CabinetFolder cabinetFolder) {
        this.cabinetFolder = cabinetFolder;
    }

    public Long getCabinetDefinitionID() {
        return cabinetDefinitionID;
    }

    public String getCabinetDefinitionStr() {
        return cabinetDefinitionStr;
    }

    public void setCabinetDefinitionID(Long cabinetDefinitionID) {
        this.cabinetDefinitionID = cabinetDefinitionID;
    }

    public void setCabinetDefinitionStr(String cabinetDefinitionStr) {
        this.cabinetDefinitionStr = cabinetDefinitionStr;
    }

    public Long getCabinetFolderId() {
        return cabinetFolderId;
    }

    public void setCabinetFolderId(Long cabinetFolderId) {
        this.cabinetFolderId = cabinetFolderId;
    }

    public String getCabinetFolderStr() {
        return cabinetFolderStr;
    }

    public void setCabinetFolderStr(String cabinetFolderStr) {
        this.cabinetFolderStr = cabinetFolderStr;
    }

    public Cabinet getCabinet() {
        return cabinet;
    }

    public void setCabinet(Cabinet cabinet) {
        this.cabinet = cabinet;
    }
    public Long getArchDocFileParam() {
        return archDocFileParam;
    }

    public void setArchDocFileParam(Long archDocFileParam) {
        this.archDocFileParam = archDocFileParam;
    }

    public Long getArchDocParam() {
        return archDocParam;
    }

    public void setArchDocParam(Long archDocParam) {
        this.archDocParam = archDocParam;
    }

    public Long getCabinetParam() {
        return cabinetParam;
    }

    public void setCabinetParam(Long cabinetParam) {
        this.cabinetParam = cabinetParam;
    }

    public Long getCabinetFolderParam() {
        return cabinetFolderParam;
    }

    public void setCabinetFolderParam(Long cabinetFolderParam) {
        this.cabinetFolderParam = cabinetFolderParam;
    }

    public Long getCabinetDefinitionParam() {
        return cabinetDefinitionParam;
    }

    public void setCabinetDefinitionParam(Long cabinetDefinitionParam) {
        this.cabinetDefinitionParam = cabinetDefinitionParam;
    }


    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }
    // #endregion





    // #region "services"
    private CabinetService cabinetService;
    private UserAliasService userAliasService;
    private CabinetDefinitionService cabinetDefinitionService;
    private CabinetFolderService cabinetFolderService;

    private ArchiveDocumentService archiveDocumentService;
    private ArchiveDocumentFileService archiveDocumentFileService;

    private ArchiveDocumentTypeService archiveDocumentTypeService;
    private OrganizationService organizationService;
    private LocalizationService localizationService;

    // #endregion

    @Inject
    App app;

    @PostConstruct
    public void init() {
        languages = LanguageUtil.getAllLanguages(app.getLocale().getLocale());
        cabinetStr = UtilityHelper.getRequestParameter("cd");
        cabinetFolderStr = UtilityHelper.getRequestParameter("cf");
        try {
            if (cabinetStr != null) {
                cabinetID = Long.valueOf(UtilityHelper.decipher(cabinetStr));

                cabinet = cabinetService.findById(cabinetID);

                if (cabinet != null) {
                    cabinetDefinitionList = cabinetDefinitionService.getByCabinet(cabinet);
                    if (cabinetDefinitionList != null && !cabinetDefinitionList.isEmpty()) {
                        cabinetDefinition = cabinetDefinitionList.get(0);
                        cabinetFolderList = cabinetFolderService.getByCabinetDefinition(cabinetDefinition);
                    }
                }
            }

            if (cabinetFolderStr != null) {
                cabinetFolderId = Long.valueOf(UtilityHelper.decipher(cabinetFolderStr));
                cabinetFolder = cabinetFolderService.findById(cabinetFolderId);
            }

            archiveDocuments = archiveDocumentService.getByParent(null, cabinetFolderId, null, null);
            archiveDocumentChildrens.clear();
            archiveDocumentChildrens.addAll(archiveDocuments);
            intializeRootAndFirstLevel();
            viewDocFiles = false;

            documentTypeList = new ArrayList<String>();
            documentTypeList.add("Pdf");
            documentTypeList.add("Word");
            documentTypeList.add("txt");

            usingOcr = false;

            languagesOcrList = new ArrayList<LanguageOcr>();
            LanguageOcr languageOcrArabic = new LanguageOcr(applicationSetting.getString("ocrArabicLabelAr"),
                    applicationSetting.getString("ocrArabicLabelEn"), "Ar");
            LanguageOcr languageOcrEnglish = new LanguageOcr(applicationSetting.getString("ocrEnglishLabelAr"),
                    applicationSetting.getString("ocrEnglishLabelEn"), "En");
            languagesOcrList.add(languageOcrArabic);
            languagesOcrList.add(languageOcrEnglish);

        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
            e.printStackTrace();
        }

    }

    public void onLoad() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            try {
                if (UtilityHelper.getSessionAttr("cabinetDefinitionStr") != null) {
                    cabinetDefinitionStr = (String) UtilityHelper.getSessionAttr("cabinetDefinitionStr");
                    cabinetDefinitionID = Long.valueOf(UtilityHelper.decipher(cabinetDefinitionStr));

                    cabinetDefinition = cabinetDefinitionService.findById(cabinetDefinitionID);
                    if (cabinetDefinition != null)
                        cabinetFolderList = cabinetFolderService.getByCabinetDefinition(cabinetDefinition);
                }
                if (UtilityHelper.getSessionAttr("pageIndex") != null) {
                    pageIndex = (Short) UtilityHelper.getSessionAttr("pageIndex");
                }
                archiveDocuments = archiveDocumentService.getByParent(null, cabinetFolderId, null, null);
                archiveDocumentChildrens.clear();
                archiveDocumentChildrens.addAll(archiveDocuments);
                intializeRootAndFirstLevel();
                viewDocFiles = false;
            } catch (Exception e) {
                UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
                e.printStackTrace();
            }

        }
    }

    public MyCabinetBean() {
        try {
            myUserAlias = new UserAlias();
            cabinet = new Cabinet();
            cabinetDefinition = new CabinetDefinition();
            cabinetFolder = new CabinetFolder();

            myCabinets = new ArrayList<Cabinet>();
            myUserAliasList = new ArrayList<UserAlias>();
            cabinetDefinitionList = new ArrayList<CabinetDefinition>();
            cabinetFolderList = new ArrayList<CabinetFolder>();
            archiveDocument = new ArchiveDocument();
            archiveDocument.setIsEdit(false);
            archiveDocuments = new ArrayList<ArchiveDocument>();
            archiveDocumentChildrens = new ArrayList<ArchiveDocument>();
            archiveDocumentFiles = new ArrayList<ArchiveDocumentFile>();
            filesListInDir = new ArrayList<String>();

            cabinetService = (CabinetService) BeanUtility.getBean("cabinetService");
            userAliasService = (UserAliasService) BeanUtility.getBean("userAliasService");
            cabinetDefinitionService = (CabinetDefinitionService) BeanUtility.getBean("cabinetDefinitionService");
            cabinetFolderService = (CabinetFolderService) BeanUtility.getBean("cabinetFolderService");

            archiveDocumentService = (ArchiveDocumentService) BeanUtility.getBean("archiveDocumentService");
            archiveDocumentFileService = (ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");
            archiveDocumentTypeService = (ArchiveDocumentTypeService) BeanUtility.getBean("archiveDocumentTypeService");
            organizationService = (OrganizationService) BeanUtility.getBean("organizationService");
            localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
            sysUser = (SysUser) UtilityHelper.getSessionAttr("user");
            myUserAliasList = userAliasService.getBySysUser(sysUser);
            archiveDocumentTypes = archiveDocumentTypeService.findAll();
            if (myUserAliasList != null && !myUserAliasList.isEmpty()) {
                myUserAlias = myUserAliasList.get(0);
                myCabinets = cabinetService.myCabinets(myUserAlias);
            }

            searchKey = "";

            responsiveOptions = new ArrayList<>();
            responsiveOptions.add(new ResponsiveOption("1024px", 3, 3));
            responsiveOptions.add(new ResponsiveOption("768px", 2, 2));
            responsiveOptions.add(new ResponsiveOption("560px", 1, 1));

            applicationSetting = ResourceBundle.getBundle("com.smat.ins.view.resources.applicationSetting");
            if (applicationSetting.containsKey("maxFolderCodeLength")
                    && applicationSetting.getString("maxFolderCodeLength") != null) {
                folderCodeLength = Integer.parseInt(applicationSetting.getString("maxFolderCodeLength"));

            } else {
                folderCodeLength = 3;
            }

            if (applicationSetting.containsKey("maxDocumentCodeLength")
                    && applicationSetting.getString("maxDocumentCodeLength") != null) {
                documentCodeLength = Integer.parseInt(applicationSetting.getString("maxDocumentCodeLength"));

            } else {
                documentCodeLength = 9;
            }

            pageIndex = 0;
            viewDocFiles = false;

        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
            e.printStackTrace();
        }

    }

    @PreDestroy
    public void destroy() {

    }

    public String imgIcon(CabinetType cabinetType) {
        switch (cabinetType.getCode()) {
            case "01":
                return "images/stock-photo-archive-cabinet-with-folders.png";
            case "02":
                return "images/blue-archive-cabinet-icon.png";
            case "03":
                return "images/stock-photo-archive-cabinet-icon.png";
            default:
                return "images/stock-photo-archive-cabinet-with-folders.png";
        }
    }

    public void assignCabinet(Cabinet cabinet) throws Exception {
        cabinetID = cabinet.getId();
        cabinetStr = UtilityHelper.cipher(cabinetID.toString());
    }

    public void assignCabinetDefinition(CabinetDefinition cabinetDefinition) throws Exception {
        if (cabinetDefinition != null) {
            this.cabinetDefinition = cabinetDefinition;
            cabinetFolderList = cabinetFolderService.getByCabinetDefinition(cabinetDefinition);
            cabinetDefinitionID = cabinetDefinition.getId();
            cabinetDefinitionStr = UtilityHelper.cipher(cabinetDefinitionID.toString());
            UtilityHelper.putSessionAttr("cabinetDefinitionStr", cabinetDefinitionStr);
            UtilityHelper.putSessionAttr("pageIndex", pageIndex);
        }
    }

    public void assignCabinetFolder(CabinetFolder cabinetFolder) throws Exception {
        cabinetFolderId = cabinetFolder.getId();
        cabinetFolderStr = UtilityHelper.cipher(cabinetFolderId.toString());

        cabinetID = cabinetFolder.getCabinetDefinition().getCabinet().getId();
        cabinetStr = UtilityHelper.cipher(cabinetID.toString());
    }

    public boolean doValidateCabinetFolder() {
        boolean result = true;
        if (cabinetFolder.getArabicName() == null || cabinetFolder.getArabicName().trim().isEmpty()) {
            UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
                    + localizationService.getErrorMessage().getString("validateInput"));
            result = false;
        }

        return result;
    }

    public void saveCabinetFolder() {
        if (doValidateCabinetFolder()) {
            try {
                // Create Folder
                String mainLocation="";
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    mainLocation = cabinet.getCabinetLocation().getWinLocation();
                } else if (os.contains("osx")) {
                    mainLocation = cabinet.getCabinetLocation().getMacLocation();
                } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
                    mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
                }
                Files.createDirectories(Paths.get(mainLocation
                        + FileSystems.getDefault().getSeparator() + cabinet.getCode()
                        + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                        + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()));

                cabinetFolder.setCabinetDefinition(cabinetDefinition);
                cabinetFolder.setSysUser(sysUser);
                cabinetFolder.setCreatedDate(Calendar.getInstance().getTime());
                cabinetFolderService.saveOrUpdate(cabinetFolder);
                cabinetFolderList = cabinetFolderService.getByCabinetDefinition(cabinetDefinition);
                this.cabinetFolder = new CabinetFolder();

                Integer maxFolderCodeLength = cabinetFolderService.getMaxCabinetCode(cabinetDefinition);
                if (maxFolderCodeLength != null) {
                    cabinetFolder.setCode(String.format("%0" + folderCodeLength + "d", maxFolderCodeLength + 1));
                }

                UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
                PrimeFaces.current().ajax().update("form:messages", "form:dv-folders");

            } catch (Exception e) {
                UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
                PrimeFaces.current().ajax().update("form:messages", ":form:dv-folders");
                e.printStackTrace();
            }
        }
    }

    public void openNewCabinetFolder() throws Exception {

        this.cabinetFolder = new CabinetFolder();

        Integer maxFolderCodeLength = cabinetFolderService.getMaxCabinetCode(cabinetDefinition);
        if (maxFolderCodeLength != null) {
            cabinetFolder.setCode(String.format("%0" + folderCodeLength + "d", maxFolderCodeLength + 1));
        }
    }

    public void openNewDocument() throws Exception {
        this.selectedNode = null;
        this.selectedArchiveDocument = null;
        this.archiveDocument = new ArchiveDocument();
        this.archiveDocument.setIsEdit(false);
        this.archiveDocument.setLang("ar");
        archiveDocumentFiles.clear();
        if (archiveDocumentTypes != null && !archiveDocumentTypes.isEmpty())
            this.archiveDocument.setArchiveDocumentType(archiveDocumentTypes.get(0));
        this.archiveDocument.setYear((short) Calendar.getInstance().get(Calendar.YEAR));
        Long maxArchiveDocumentCodeLenght = archiveDocumentService.getMaxArchiveDocumentCode(null, cabinetFolderId);
        if (maxArchiveDocumentCodeLenght != null) {
            archiveDocument.setSeq(maxArchiveDocumentCodeLenght + 1);
            archiveDocument.setCode(String.format("%0" + documentCodeLength + "d", maxArchiveDocumentCodeLenght + 1));
        }
    }

    public void checkOrUncheckIfDirectory() {
        if (!this.archiveDocument.getIsDirectory()) {
            if (archiveDocumentTypes != null && !archiveDocumentTypes.isEmpty())
                this.archiveDocument.setArchiveDocumentType(archiveDocumentTypes.get(0));
            this.archiveDocument.setYear((short) Calendar.getInstance().get(Calendar.YEAR));
        } else {
            this.archiveDocument.setArchiveDocumentType(null);
            this.archiveDocument.setYear(null);
        }
    }

    // #region "Organization"
    private Organization getRootOrganization(Organization organization) throws Exception {
        Organization rootOrganization = organization;
        Organization parentOrganization = organizationService.getParentOrganization(organization);
        if (rootOrganization.equals(parentOrganization))
            return parentOrganization;
        else
            return getRootOrganization(parentOrganization);
    }

    private Organization getOrganizationDivan(Organization organization) throws Exception {
        organization = organizationService.getOrganizationWithDetails(organization);
        if (organization.getIsDivan())
            return organization;
        else if (organization.getOrganization() == null)
            return null;
        else
            return getOrganizationDivan(organization.getOrganization());
    }

    public void selectOrganization(Organization organization) throws Exception {
        this.archiveDocument.setOrganizationByRootOrganization(getRootOrganization(organization));
        this.archiveDocument.setOrganizationByDivan(getOrganizationDivan(organization));
        this.archiveDocument.setOrganizationByOrganization(organization);

    }
    // #endregion

    // #region "archiveDocument"

    private void intializeRootAndFirstLevel() throws Exception {
        this.root = new DefaultTreeNode("Root", null, null);
        for (ArchiveDocument rootArchiveDocument : archiveDocuments) {
            this.node = new DefaultTreeNode(rootArchiveDocument, this.root);
            List<ArchiveDocument> firstLevel = archiveDocumentService.getByParent(rootArchiveDocument, cabinetFolderId,
                    null, null);
            for (ArchiveDocument archiveDocument : firstLevel) {

                @SuppressWarnings("unused")
                TreeNode node = new DefaultTreeNode(archiveDocument, this.node);
            }
        }
    }

    public void onNodeArchExpand(NodeExpandEvent event) throws Exception {
        event.getTreeNode().setSelected(false);
        List<TreeNode> listChild = event.getTreeNode().getChildren();
        for (TreeNode treeNode : listChild) {
            if (treeNode.getChildCount() > 0)
                return;
            List<ArchiveDocument> listContactChild = archiveDocumentService
                    .getByParent(((ArchiveDocument) treeNode.getData()), cabinetFolderId, null, null);
            for (ArchiveDocument archiveDocument : listContactChild) {

                @SuppressWarnings("unused")
                TreeNode node = new DefaultTreeNode(archiveDocument, treeNode);
            }
        }
    }

    public void onNodeArchCollapse(NodeCollapseEvent event) {
        event.getTreeNode().setSelected(false);
        collapseTree(event.getTreeNode());
    }

    public void collapseTree(TreeNode node) {

        if (node.getChildCount() > 0) {
            node.setExpanded(false);
            node.setSelected(false);

        }

    }

    public void onNodeArchSelect(NodeSelectEvent event) throws Exception {
        if (selectedNode != null) {
            selectedArchiveDocument = archiveDocumentService
                    .getArchiveDocumentWithDetails((ArchiveDocument) selectedNode.getData(), cabinetFolderId);
            breadcrumbList = getListBreadcrumbs(selectedArchiveDocument, cabinetFolderId);
            archiveDocumentChildrens = archiveDocumentService.getByParent(selectedArchiveDocument, cabinetFolderId,
                    null, null);
            if (!selectedArchiveDocument.getIsDirectory()) {
                archiveDocumentFiles = archiveDocumentFileService.getBy(selectedArchiveDocument);
                viewDocFiles = true;
            } else {
                viewDocFiles = false;
            }
            selectedNode = event.getTreeNode();

        }

    }

    public void getArchiveDocChildren(Long archiveDocumentId) throws Exception {
        try {
            this.selectedArchiveDocument = archiveDocumentService.findById(archiveDocumentId);
            breadcrumbList = getListBreadcrumbs(selectedArchiveDocument, cabinetFolderId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        archiveDocumentChildrens = archiveDocumentService.getByParent(selectedArchiveDocument, cabinetFolderId, null,
                null);
        if (!selectedArchiveDocument.getIsDirectory()) {
            archiveDocumentFiles = archiveDocumentFileService.getBy(selectedArchiveDocument);
            viewDocFiles = true;
        } else {
            viewDocFiles = false;
        }
    }

    public void viewDocumentaAttribute(Long archiveDocumentId) {
        try {
            this.selectedArchiveDocument = archiveDocumentService.findById(archiveDocumentId);
            viewDocFiles = false;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public Boolean renderAddArchDoc() {
        if (viewDocFiles)
            return false;
        else if (selectedArchiveDocument != null && !selectedArchiveDocument.getIsDirectory())
            return false;
        else
            return true;
    }

    public Boolean renderAddArchiveFile() {
        if (viewDocFiles)
            return true;
        if (selectedArchiveDocument != null && !selectedArchiveDocument.getIsDirectory())
            return true;
        else
            return false;
    }

    public void addNodeArchive(ArchiveDocument archiveDocument) throws Exception {
        this.archiveDocument = new ArchiveDocument();
        this.archiveDocument.setIsEdit(false);
        this.archiveDocument.setLang("ar");
        archiveDocumentFiles.clear();
        if (archiveDocumentTypes != null && !archiveDocumentTypes.isEmpty())
            this.archiveDocument.setArchiveDocumentType(archiveDocumentTypes.get(0));
        this.archiveDocument.setYear((short) Calendar.getInstance().get(Calendar.YEAR));
        Long maxArchiveDocumentCodeLenght = archiveDocumentService.getMaxArchiveDocumentCode(archiveDocument,
                cabinetFolderId);
        if (maxArchiveDocumentCodeLenght != null) {
            this.archiveDocument.setSeq(maxArchiveDocumentCodeLenght + 1);
            this.archiveDocument
                    .setCode(String.format("%0" + documentCodeLength + "d", maxArchiveDocumentCodeLenght + 1));
        }
    }

    public void editNodeArchive(ArchiveDocument archiveDocument) throws Exception {
        this.archiveDocument = archiveDocument;
        this.archiveDocument.setIsEdit(true);
        archiveDocumentFiles = new ArrayList<ArchiveDocumentFile>();
        archiveDocumentFiles = archiveDocumentFileService.getBy(archiveDocument);

    }

    public void addArchiveDoc() throws Exception {
        if (selectedArchiveDocument != null) {

            this.archiveDocument = new ArchiveDocument();
            this.archiveDocument.setIsEdit(false);
            this.archiveDocument.setLang("ar");
            archiveDocumentFiles.clear();
            if (archiveDocumentTypes != null && !archiveDocumentTypes.isEmpty())
                this.archiveDocument.setArchiveDocumentType(archiveDocumentTypes.get(0));
            this.archiveDocument.setYear((short) Calendar.getInstance().get(Calendar.YEAR));
            Long maxArchiveDocumentCodeLenght = archiveDocumentService
                    .getMaxArchiveDocumentCode(selectedArchiveDocument, cabinetFolderId);
            if (maxArchiveDocumentCodeLenght != null) {
                this.archiveDocument.setSeq(maxArchiveDocumentCodeLenght + 1);
                this.archiveDocument
                        .setCode(String.format("%0" + documentCodeLength + "d", maxArchiveDocumentCodeLenght + 1));
            }
        } else {
            this.selectedNode = null;
            this.selectedArchiveDocument = null;
            this.archiveDocument = new ArchiveDocument();
            this.archiveDocument.setIsEdit(false);
            this.archiveDocument.setLang("ar");
            archiveDocumentFiles.clear();
            if (archiveDocumentTypes != null && !archiveDocumentTypes.isEmpty())
                this.archiveDocument.setArchiveDocumentType(archiveDocumentTypes.get(0));
            this.archiveDocument.setYear((short) Calendar.getInstance().get(Calendar.YEAR));
            Long maxArchiveDocumentCodeLenght = archiveDocumentService.getMaxArchiveDocumentCode(null, cabinetFolderId);
            if (maxArchiveDocumentCodeLenght != null) {
                archiveDocument.setSeq(maxArchiveDocumentCodeLenght + 1);
                archiveDocument
                        .setCode(String.format("%0" + documentCodeLength + "d", maxArchiveDocumentCodeLenght + 1));
            }
        }
    }

    public void doSave() {

        if (doValidate()) {
            try {
                String rootPath = "";
                if (selectedArchiveDocument != null)
                    rootPath = getRootPath(selectedArchiveDocument, cabinetFolderId);
                else
                    rootPath = "";
                String mainLocation="";
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    mainLocation = cabinet.getCabinetLocation().getWinLocation();
                } else if (os.contains("osx")) {
                    mainLocation = cabinet.getCabinetLocation().getMacLocation();
                } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
                    mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
                }
                String dirPath = mainLocation + FileSystems.getDefault().getSeparator()
                        + cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                        + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                        + FileSystems.getDefault().getSeparator() + rootPath + archiveDocument.getCode();
                String archDocLogicalPath =cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                        + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                        + FileSystems.getDefault().getSeparator() + rootPath + archiveDocument.getCode();
                Files.createDirectories(Paths.get(dirPath));

                for(ArchiveDocumentFile archiveDocumentFile : archiveDocumentFiles) {
                    archiveDocumentFile.setServerPath(dirPath+FileSystems.getDefault().getSeparator());;
                    archiveDocumentFile.setLogicalPath(archDocLogicalPath+FileSystems.getDefault().getSeparator());
                }

                if (archiveDocument.getIsEdit()) {
                    archiveDocumentService.updateArchiveDoc(archiveDocument, archiveDocumentFiles, dirPath);
                    PrimeFaces.current().ajax().update("PF('addArchiveWidgetVar').hide();");
                    UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
                } else {

                    if (selectedArchiveDocument != null) {
                        archiveDocument.setArchiveDocument(selectedArchiveDocument);
                    }

                    archiveDocument.setSysUserByCreatorUser(sysUser);
                    archiveDocument.setCreatedDate(Calendar.getInstance().getTime());
                    CabinetFolderDocument cabinetFolderDocument = new CabinetFolderDocument();
                    cabinetFolderDocument.setArchiveDocument(archiveDocument);
                    cabinetFolderDocument.setCabinetFolder(cabinetFolder);
                    cabinetFolderDocument.setCreatedDate(Calendar.getInstance().getTime());
                    cabinetFolderDocument.setSysUser(sysUser);

                    ArchiveDocument objArchiveDocument = archiveDocumentService.savewithDoc(archiveDocument,
                            cabinetFolderDocument, archiveDocumentFiles, dirPath);
                    archiveDocumentFiles.clear();
                    if (objArchiveDocument != null) {
                        if (selectedArchiveDocument == null) {
                            if (this.root != null) {
                                TreeNode node = new DefaultTreeNode(objArchiveDocument, this.root);
                                this.root.setExpanded(true);
                                node.setExpanded(true);
                            } else {
                                this.root = new DefaultTreeNode("Root", null, null);
                                TreeNode node = new DefaultTreeNode(objArchiveDocument, this.root);
                                this.root.setExpanded(true);
                                node.setExpanded(true);
                            }
                        } else {
                            TreeNode node = new DefaultTreeNode(objArchiveDocument, selectedNode);
                            selectedNode.setExpanded(true);
                            node.setExpanded(true);
                        }
                        if (objArchiveDocument.getArchiveDocument() == null)
                            selectedArchiveDocument = null;
                        else
                            selectedArchiveDocument = objArchiveDocument.getArchiveDocument();
                        reset();

                        UtilityHelper
                                .addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
            }
        }

    }

    public void deleteArchiveNode(ArchiveDocument archiveDocument) {
        try {
            if (archiveDocument.getArchiveDocuments().isEmpty()) {
                String rootPath = "";
                if (archiveDocument != null)
                    rootPath = getRootPath(archiveDocument, cabinetFolderId);
                String mainLocation="";
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    mainLocation = cabinet.getCabinetLocation().getWinLocation();
                } else if (os.contains("osx")) {
                    mainLocation = cabinet.getCabinetLocation().getMacLocation();
                } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
                    mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
                }
                String dirPath = mainLocation + FileSystems.getDefault().getSeparator()
                        + cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                        + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                        + FileSystems.getDefault().getSeparator() + rootPath;
                FileUtils.deleteDirectory(new File(dirPath));
                if (!archiveDocument.getIsDirectory()) {
                    List<ArchiveDocumentFile> archiveDocumentFiles = archiveDocumentFileService.getBy(archiveDocument);
                    for (ArchiveDocumentFile archiveDocumentFile : archiveDocumentFiles) {
                        archiveDocumentFileService.delete(archiveDocumentFile);
                    }
                    archiveDocumentService.deleteArchiveDoc(archiveDocument);
                } else {
                    archiveDocumentService.deleteArchiveDoc(archiveDocument);
                }
                archiveDocuments = archiveDocumentService.getByParent(null, cabinetFolderId, null, null);
                if (archiveDocuments != null && !archiveDocuments.isEmpty())
                    selectedArchiveDocument = archiveDocuments.get(0);
                else
                    selectedArchiveDocument = null;
                archiveDocumentChildrens.addAll(archiveDocuments);
                intializeRootAndFirstLevel();

                UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
            } else {
                UtilityHelper.addInfoMessage(
                        localizationService.getErrorMessage().getString("youCannotDeleteNodeContainsSubItem"));

            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    private String getRootPath(ArchiveDocument archiveDocument, Long cabinetFolderId) throws Exception {
        String rootPath = "";
        List<ArchiveDocument> result = new ArrayList<ArchiveDocument>();
        result.add(archiveDocument);
        while (archiveDocument.getArchiveDocument() != null) {
            archiveDocument = archiveDocumentService.getParentArchiveDocument(archiveDocument, cabinetFolderId);
            result.add(archiveDocument);
        }

        int i = result.size();

        for (; i > 0;) {
            i--;
            ArchiveDocument documentItem = result.get(i);
            rootPath += documentItem.getCode() + FileSystems.getDefault().getSeparator();
        }
        return rootPath;
    }

    private List<ArchiveDocument> getListBreadcrumbs(ArchiveDocument archiveDocument, Long cabinetFolderId) throws Exception {
        List<ArchiveDocument> result = new ArrayList<ArchiveDocument>();
        List<ArchiveDocument> fResult = new ArrayList<ArchiveDocument>();
        result.add(archiveDocument);
        while (archiveDocument.getArchiveDocument() != null) {
            archiveDocument = archiveDocumentService.getParentArchiveDocument(archiveDocument, cabinetFolderId);
            result.add(archiveDocument);
        }
        int i = result.size();

        for (; i > 0;) {
            i--;
            ArchiveDocument documentItem = result.get(i);
            fResult.add(documentItem);
        }
        return fResult;
    }

    public void deleteArchiveDocumentFile(ArchiveDocumentFile archiveDocumentFile) throws Exception {
        boolean result = false;
        try {
            result = archiveDocumentFileService.delete(archiveDocumentFile);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
        }
        if (result) {
            archiveDocumentFiles.remove(archiveDocumentFile);
            String rootPath = "";
            if (selectedArchiveDocument != null)
                rootPath = getRootPath(selectedArchiveDocument, cabinetFolderId);
            else
                rootPath = "";
            String mainLocation="";
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                mainLocation = cabinet.getCabinetLocation().getWinLocation();
            } else if (os.contains("osx")) {
                mainLocation = cabinet.getCabinetLocation().getMacLocation();
            } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
                mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
            }
            String dirPath = mainLocation + FileSystems.getDefault().getSeparator()
                    + cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                    + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                    + FileSystems.getDefault().getSeparator() + rootPath + FileSystems.getDefault().getSeparator()
                    + archiveDocumentFile.getCode() + "." + archiveDocumentFile.getExtension();
            String docPathPdf = mainLocation + FileSystems.getDefault().getSeparator()
                    + cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                    + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                    + FileSystems.getDefault().getSeparator() + rootPath + FileSystems.getDefault().getSeparator()
                    + archiveDocumentFile.getCode() + "_converted.pdf";
            try {

                Files.delete(Paths.get(dirPath));
                if (Files.exists(Paths.get(docPathPdf)))
                    Files.delete(Paths.get(docPathPdf));
                UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
            }

        }

    }

    public void reset() throws Exception {
        this.archiveDocument = new ArchiveDocument();
        this.archiveDocument.setIsEdit(false);
        if (archiveDocumentTypes != null && !archiveDocumentTypes.isEmpty())
            this.archiveDocument.setArchiveDocumentType(archiveDocumentTypes.get(0));
        this.archiveDocument.setYear((short) Calendar.getInstance().get(Calendar.YEAR));
        Long maxArchiveDocumentCodeLenght = archiveDocumentService.getMaxArchiveDocumentCode(selectedArchiveDocument,
                cabinetFolderId);
        if (maxArchiveDocumentCodeLenght != null) {
            archiveDocument.setSeq(maxArchiveDocumentCodeLenght + 1);
            archiveDocument.setCode(String.format("%0" + documentCodeLength + "d", maxArchiveDocumentCodeLenght + 1));
        }
    }

    public void viewFolderContent() throws Exception {
        viewDocFiles = false;
        selectedArchiveDocument = null;
        selectedNode = null;
        archiveDocuments = archiveDocumentService.getByParent(null, cabinetFolderId, null, null);

        archiveDocumentChildrens.clear();
        archiveDocumentChildrens.addAll(archiveDocuments);
    }

    public void refreshContent() throws Exception {
        if (selectedArchiveDocument != null) {
            if (!selectedArchiveDocument.getIsDirectory()) {
                archiveDocumentFiles = archiveDocumentFileService.getBy(selectedArchiveDocument);
                viewDocFiles = true;
            } else {
                viewDocFiles = false;
                archiveDocuments = archiveDocumentService.getByParent(selectedArchiveDocument, cabinetFolderId, null,
                        null);
                archiveDocumentChildrens.clear();
                archiveDocumentChildrens.addAll(archiveDocuments);
            }
        } else {
            viewDocFiles = false;
            selectedArchiveDocument = null;
            selectedNode = null;
            archiveDocuments = archiveDocumentService.getByParent(null, cabinetFolderId, null, null);
            archiveDocumentChildrens.clear();
            archiveDocumentChildrens.addAll(archiveDocuments);
        }
    }

    public boolean doValidate() {
        boolean result = true;

        if (archiveDocument.getArabicName() == null || archiveDocument.getArabicName().trim().equals("")) {
            UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
                    + localizationService.getErrorMessage().getString("validateInput"));
            result = false;
        }

        if (archiveDocument.getEnglishName() == null || archiveDocument.getEnglishName().trim().equals("")) {
            UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("englishName") + "  "
                    + localizationService.getErrorMessage().getString("validateInput"));
            result = false;
        }

        try {

            ArchiveDocument archiveDocumentObj = archiveDocumentService.getBy(cabinetFolderId,
                    archiveDocument.getArabicName().trim(), archiveDocument.getEnglishName().trim());
            if (archiveDocumentObj != null) {
                UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("name") + "  "
                        + localizationService.getErrorMessage().getString("duplicate"));
                result = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (archiveDocument.getDescription() == null || archiveDocument.getDescription().trim().equals("")) {
            UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("description") + "  "
                    + localizationService.getErrorMessage().getString("validateInput"));
            result = false;
        }

        if (!archiveDocument.getIsDirectory()) {
            if (archiveDocument.getFromEntity() == null || archiveDocument.getFromEntity().trim().equals("")) {
                UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("side") + "  "
                        + localizationService.getErrorMessage().getString("validateInput"));
                result = false;
            }

            if (archiveDocument.getYear() == null) {
                UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("year") + "  "
                        + localizationService.getErrorMessage().getString("validateInput"));
                result = false;
            }

            if (archiveDocument.getSubject() == null || archiveDocument.getSubject().trim().equals("")) {
                UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("subject") + "  "
                        + localizationService.getErrorMessage().getString("validateInput"));
                result = false;
            }
        }

        return result;
    }

    public void prepareArchiveDocFiles() {
        archiveDocumentFiles.clear();
    }

    public void prepareDocumentForSearch(Boolean rootSearch) {
        if (rootSearch)
            selectedArchiveDocument = null;
        searchWord = "";
        this.rootSearch = rootSearch;
        this.archiveDocument = new ArchiveDocument();
        this.archiveDocument.setIsEdit(false);
        this.archiveDocument.setLang("ar");
        archiveDocumentFiles.clear();
        if (archiveDocumentTypes != null && !archiveDocumentTypes.isEmpty())
            this.archiveDocument.setArchiveDocumentType(archiveDocumentTypes.get(0));
        this.archiveDocument.setYear((short) Calendar.getInstance().get(Calendar.YEAR));
        archiveDocumentFilesResult = new ArrayList<ArchiveDocumentFile>();
        usingOcr = false;
        modelPhysicalPath = new DefaultDiagramModel();
        modelPhysicalPath.setMaxConnections(-1);
        modelPhysicalPath.setContainment(false);
    }

    public void doSaveArchiveDocFiles() {
        try {
            String rootPath = "";
            if (selectedArchiveDocument != null)
                rootPath = getRootPath(selectedArchiveDocument, cabinetFolderId);
            else
                rootPath = "";
            String mainLocation="";
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                mainLocation = cabinet.getCabinetLocation().getWinLocation();
            } else if (os.contains("osx")) {
                mainLocation = cabinet.getCabinetLocation().getMacLocation();
            } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
                mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
            }
            String dirPath = mainLocation + FileSystems.getDefault().getSeparator()
                    + cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                    + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                    + FileSystems.getDefault().getSeparator() + rootPath;
            String archDocLogicalPath = cabinet.getCode() + FileSystems.getDefault().getSeparator()
                    + cabinetDefinition.getCode() + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                    + FileSystems.getDefault().getSeparator() + rootPath + archiveDocument.getCode();
            for (ArchiveDocumentFile archiveDocumentFile : archiveDocumentFiles) {
                archiveDocumentFile.setServerPath(dirPath + FileSystems.getDefault().getSeparator());
                ;
                archiveDocumentFile.setLogicalPath(archDocLogicalPath + FileSystems.getDefault().getSeparator());
            }
            archiveDocumentService.saveArchiveDocument(selectedArchiveDocument, archiveDocumentFiles, dirPath);
            archiveDocumentFiles.clear();
            if (!selectedArchiveDocument.getIsDirectory()) {
                archiveDocumentFiles = archiveDocumentFileService.getBy(selectedArchiveDocument);
                viewDocFiles = true;
            } else {
                viewDocFiles = false;
            }
            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
        }

    }

    public void deleteAllDocInFolder() {
        try {
            archiveDocumentFileService.delete(archiveDocumentFiles);
            String rootPath = "";
            if (selectedArchiveDocument != null)
                rootPath = getRootPath(selectedArchiveDocument, cabinetFolderId);
            else
                rootPath = "";
            String mainLocation="";
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                mainLocation = cabinet.getCabinetLocation().getWinLocation();
            } else if (os.contains("osx")) {
                mainLocation = cabinet.getCabinetLocation().getMacLocation();
            } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
                mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
            }
            String dirPath = mainLocation + FileSystems.getDefault().getSeparator()
                    + cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                    + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                    + FileSystems.getDefault().getSeparator() + rootPath;
            File dir = new File(dirPath);
            filesListInDir = new ArrayList<String>();
            populateFilesList(dir);
            for (String filePath : filesListInDir) {
                if (Files.exists(Paths.get(filePath)))
                    Files.delete(Paths.get(filePath));
            }
            archiveDocumentFiles.clear();
            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
        }
    }

    public void ZipDocumentForDownload() throws Exception {
        try {
            UtilityHelper.putSessionAttr("archDocId", selectedArchiveDocument.getId());
            UtilityHelper.putSessionAttr("cabinetFolderId", cabinetFolderId);
            UtilityHelper.putSessionAttr("cabinetId", cabinet.getId());
            UtilityHelper.putSessionAttr("cabinetDefinitionId", cabinetDefinition.getId());

            String rootPath = "";
            if (selectedArchiveDocument != null)
                rootPath = getRootPath(selectedArchiveDocument, cabinetFolderId);
            else
                rootPath = "";
            String mainLocation="";
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                mainLocation = cabinet.getCabinetLocation().getWinLocation();
            } else if (os.contains("osx")) {
                mainLocation = cabinet.getCabinetLocation().getMacLocation();
            } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
                mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
            }
            String dirPath = mainLocation + FileSystems.getDefault().getSeparator()
                    + cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                    + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                    + FileSystems.getDefault().getSeparator() + rootPath;
            File dir = new File(dirPath);
            String zipDirName = mainLocation + FileSystems.getDefault().getSeparator()
                    + cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                    + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                    + FileSystems.getDefault().getSeparator() + rootPath + FileSystems.getDefault().getSeparator()
                    + "all.zip";
            if (Files.exists(Paths.get(zipDirName)))
                Files.delete(Paths.get(zipDirName));
            filesListInDir = new ArrayList<String>();
            this.zipDirectory(dir, zipDirName);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void prepareDocument(ArchiveDocumentFile archiveDocumentFile) {

        UtilityHelper.putSessionAttr("archDocId", archiveDocumentFile.getArchiveDocument().getId());
        UtilityHelper.putSessionAttr("archDocFileId", archiveDocumentFile.getId());
        UtilityHelper.putSessionAttr("cabinetFolderId", cabinetFolderId);
        UtilityHelper.putSessionAttr("cabinetId", cabinet.getId());
        UtilityHelper.putSessionAttr("cabinetDefinitionId", cabinetDefinition.getId());
    }

    public void prepareDocumentViewer(ArchiveDocumentFile archiveDocumentFile) {

        UtilityHelper.putSessionAttr("archDocId", archiveDocumentFile.getArchiveDocument().getId());
        UtilityHelper.putSessionAttr("archDocFileId", archiveDocumentFile.getId());
        UtilityHelper.putSessionAttr("cabinetFolderId", cabinetFolderId);
        UtilityHelper.putSessionAttr("cabinetId", cabinet.getId());
        UtilityHelper.putSessionAttr("cabinetDefinitionId", cabinetDefinition.getId());

        String[] photoExtension = { "gif", "jpeg", "jpg", "png", "tif" };
        String fileExt = archiveDocumentFile.getExtension();
        Boolean photoExt = fileExt != null && Arrays.stream(photoExtension).anyMatch(e -> e.equalsIgnoreCase(fileExt));
        if (archiveDocumentFile.getExtension().equalsIgnoreCase("pdf")) {
            PrimeFaces.current().ajax().update("form:manage-viewer-content");
            PrimeFaces.current().executeScript("PF('viewerWidgetVar').show();");

        } else if (photoExt) {
            PrimeFaces.current().ajax().update("form:manage-photo-viewer-content");
            PrimeFaces.current().executeScript("PF('photoViewerWidgetVar').show();");
        } else if (archiveDocumentFile.getExtension().equalsIgnoreCase("doc")
                || archiveDocumentFile.getExtension().equalsIgnoreCase("docx")) {
            PrimeFaces.current().ajax().update("form:manage-doc-viewer-content");
            PrimeFaces.current().executeScript("PF('convertedDocWidgetVar').show();");
        } else {
            UtilityHelper.addErrorMessage(localizationService.getInterfaceLabel().getString("fileCouldNotPreview"));
            PrimeFaces.current().ajax().update("form:messages");
        }
    }


    public Boolean extensionIn(ArchiveDocumentFile archiveDocumentFile) {
        String[] docExtension = { "pdf", "doc", "docx", "gif", "jpeg", "jpg", "png", "tif" };
        String fileExt = archiveDocumentFile.getExtension();
        Boolean docExt = fileExt != null && Arrays.stream(docExtension).anyMatch(e -> e.equalsIgnoreCase(fileExt));
        return docExt;
    }

    // #endregion

    // #region "zip files"

    private List<String> filesListInDir;

    private void zipDirectory(File dir, String zipDirName) {
        try {
            populateFilesList(dir);
            // now zip files one by one
            // create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (String filePath : filesListInDir) {

                // for ZipEntry we need to keep only relative file path, so we used substring on
                // absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);
                // read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile())
                filesListInDir.add(file.getAbsolutePath());
            else
                populateFilesList(file);
        }
    }

    private static void zipSingleFile(File file, String zipFileName) {
        try {
            // create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            // add a new Zip Entry to the ZipOutputStream
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            // read the file and write to ZipOutputStream
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            // Close the zip entry to write to zip file
            zos.closeEntry();
            // Close resources
            zos.close();
            fis.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // #endregion

    // #region "uploader"

    public void updateArchPanel() throws Exception {
        if (selectedNode != null) {
            selectedArchiveDocument = archiveDocumentService
                    .getArchiveDocumentWithDetails((ArchiveDocument) selectedNode.getData(), cabinetFolderId);
            breadcrumbList = getListBreadcrumbs(selectedArchiveDocument, cabinetFolderId);
            archiveDocumentChildrens = archiveDocumentService.getByParent(selectedArchiveDocument, cabinetFolderId,
                    null, null);
            if (!selectedArchiveDocument.getIsDirectory()) {
                archiveDocumentFiles = archiveDocumentFileService.getBy(selectedArchiveDocument);
                viewDocFiles = true;
            } else {
                viewDocFiles = false;
            }

        }
    }

    public void uploadArchiveDoc(FileUploadEvent event) {
        try {

            String fileName = new String(event.getFile().getFileName().getBytes("ISO-8859-1"), "UTF-8");
            if (fileName.length() > 250) {
                UtilityHelper
                        .addWarnMessage(localizationService.getInterfaceLabel().getString("fileNameMaxLengthShouldBe")
                                + ":  " + fileName);
                return;
            }
            ArchiveDocumentFile archiveDocumentFile = new ArchiveDocumentFile();
            archiveDocumentFile.setArchiveDocument(archiveDocument);
            archiveDocumentFile.setName(fileName);
            archiveDocumentFile.setExtension(FilenameUtils.getExtension(event.getFile().getFileName()));
            archiveDocumentFile.setFileSize(event.getFile().getSize());
            archiveDocumentFile.setMimeType(event.getFile().getContentType());
            archiveDocumentFile.setContent(event.getFile().getContent());
            if (!foundInUploader(archiveDocumentFile))
                archiveDocumentFiles.add(archiveDocumentFile);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

    }
    // imports: FacesMessage, FacesContext, List, ArrayList, UploadedFile if needed
    public void prepareAttachmentForFolder(String targetFolderCode) {
        LOG.info("prepareAttachmentForFolder called for code => '{}'", targetFolderCode);
        try {
            Long cabinetFolderId = (this.cabinetFolder != null) ? this.cabinetFolder.getId() : null;
            LOG.info("current cabinetFolderId = {}", cabinetFolderId);

            ArchiveDocument target = null;

            // 1) البحث داخل الـ cabinet (إن وُجد id)
            if (cabinetFolderId != null) {
                try {
                    target = archiveDocumentService.getByCode(cabinetFolderId, targetFolderCode);
                    if (target != null) {
                        LOG.info("Found target by code inside cabinet: id={}", target.getId());
                    } else {
                        LOG.info("getByCode returned null for '{}' inside cabinet {}", targetFolderCode, cabinetFolderId);
                    }
                } catch (Exception e) {
                    LOG.warn("Error calling getByCode(cabinet, code): {}", e.getMessage(), e);
                }
            }

            // 2) البحث العام إذا لم يُعثر داخل الـ cabinet
            if (target == null) {
                try {
                    List<ArchiveDocument> global = archiveDocumentService.findByCodeOrNameGlobal(targetFolderCode);
                    if (global != null && !global.isEmpty()) {
                        target = global.get(0);
                        LOG.info("Found target by global search: id={}", target.getId());
                    } else {
                        LOG.info("getByNameOrCode global returned no results for '{}'", targetFolderCode);
                    }
                } catch (Exception e) {
                    LOG.warn("Error calling findByCodeOrNameGlobal: {}", e.getMessage(), e);
                }
            }

            if (target == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Folder not found", targetFolderCode));
                LOG.warn("Folder not found: {}", targetFolderCode);
                return;
            }

            // 3) ضبط المتغيرات وقبول الملفات
            this.selectedArchiveDocument = target;
            try {
                List<ArchiveDocumentFile> files = archiveDocumentService.getFilesByDocument(target);
                this.archiveDocumentFiles = (files != null) ? files : new ArrayList<>();
                LOG.info("Loaded {} files for document id={}", this.archiveDocumentFiles.size(), target.getId());
            } catch (Exception e) {
                LOG.warn("Error fetching files for document: {}", e.getMessage(), e);
                this.archiveDocumentFiles = new ArrayList<>();
            }

            // 4) فتح نافذة الرفع (PrimeFaces)
            PrimeFaces.current().executeScript("PF('udocumentFileWidgetVar').show();");
            PrimeFaces.current().ajax().update("form:manage-add-udoc-file-content");

        } catch (Exception e) {
            LOG.error("prepareAttachmentForFolder error", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error preparing folder", e.getMessage()));
        }
    }



    public void uploadArchiveDocToSelectedArchive(FileUploadEvent event) {
        try {
            String fileName = new String(event.getFile().getFileName().getBytes("ISO-8859-1"), "UTF-8");
            if (fileName.length() > 250) {
                UtilityHelper
                        .addWarnMessage(localizationService.getInterfaceLabel().getString("fileNameMaxLengthShouldBe")
                                + ":  " + fileName);
                return;
            }
            ArchiveDocumentFile archiveDocumentFile = new ArchiveDocumentFile();
            archiveDocumentFile.setArchiveDocument(selectedArchiveDocument);
            archiveDocumentFile.setName(fileName);
            archiveDocumentFile.setExtension(FilenameUtils.getExtension(event.getFile().getFileName()));
            archiveDocumentFile.setFileSize(event.getFile().getSize());
            archiveDocumentFile.setMimeType(event.getFile().getContentType());
            archiveDocumentFile.setContent(event.getFile().getContent());
            if (!foundInUploader(archiveDocumentFile))
                archiveDocumentFiles.add(archiveDocumentFile);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

    }

    public boolean foundInUploader(ArchiveDocumentFile archiveDocumentInstance) throws UnsupportedEncodingException {
        boolean result = false;
        for (ArchiveDocumentFile archiveDocumentFile : archiveDocumentFiles) {
            if (archiveDocumentFile.getName().trim().equalsIgnoreCase(archiveDocumentInstance.getName().trim())
                    && archiveDocumentFile.getExtension().trim()
                    .equalsIgnoreCase(archiveDocumentInstance.getExtension().trim())
                    && archiveDocumentFile.getFileSize().equals(archiveDocumentInstance.getFileSize())) {
                UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("theFile") + " ( "
                        + archiveDocumentInstance.getName().trim() + " ) "
                        + localizationService.getInterfaceLabel().getString("hadNotBeenAdded"));
                PrimeFaces.current().ajax().update("form:messages");
                result = true;
            }
        }
        return result;
    }

    public String docImgIconByDocEx(String docEx) {
        switch (docEx) {
            case "png":
                return "images/photography-icon-png-7.png";
            case "jpg":
                return "images/photography-icon-png-7.png";
            case "jpeg":
                return "images/photography-icon-png-7.png";
            case "tif":
                return "images/photography-icon-png-7.png";
            case "gif":
                return "images/photography-icon-png-7.png";
            case "pdf":
                return "images/PDF-doc-256.png";
            case "txt":
                return "images/txt-300x300.png";
            case "pptx":
                return "images/microsoft-powerpoint.png";
            case "ppt":
                return "images/microsoft-powerpoint.png";
            case "docx":
                return "images/microsoft-icon-png-12761.png";
            case "doc":
                return "images/microsoft-icon-png-12761.png";
            case "xlsx":
                return "images/microsoft-office-excel.png";
            case "xls":
                return "images/microsoft-office-excel.png";
            default:
                return "images/pngegg.png";
        }
    }

    public void deleteAllDocFiles() {
        archiveDocumentFiles.clear();
    }

    public void deleteDocFile(ArchiveDocumentFile archiveDocumentFile) {
        archiveDocumentFiles.remove(archiveDocumentFile);
    }

    // #endregion

    // #region "document search"
    private Boolean rootSearch;

    private DefaultDiagramModel modelPhysicalPath;

    public DefaultDiagramModel getModelPhysicalPath() {
        return modelPhysicalPath;
    }

    private Cabinet cabinetSearchResult;

    public Cabinet getCabinetSearchResult() {
        return cabinetSearchResult;
    }

    public void setCabinetSearchResult(Cabinet cabinetSearchResult) {
        this.cabinetSearchResult = cabinetSearchResult;
    }

    private CabinetDefinition cabinetDefinitionSearchResult;

    public CabinetDefinition getCabinetDefinitionSearchResult() {
        return cabinetDefinitionSearchResult;
    }

    public void setCabinetDefinitionSearchResult(CabinetDefinition cabinetDefinitionSearchResult) {
        this.cabinetDefinitionSearchResult = cabinetDefinitionSearchResult;
    }

    private CabinetFolder cabinetFolderSearchResult;

    public CabinetFolder getCabinetFolderSearchResult() {
        return cabinetFolderSearchResult;
    }

    public void setCabinetFolderSearchResult(CabinetFolder cabinetFolderSearchResult) {
        this.cabinetFolderSearchResult = cabinetFolderSearchResult;
    }

    private ArchiveDocumentFile archiveDocumentFileSearchResult;

    public ArchiveDocumentFile getArchiveDocumentFileSearchResult() {
        return archiveDocumentFileSearchResult;
    }

    public void setArchiveDocumentFileSearchResult(ArchiveDocumentFile archiveDocumentFileSearchResult) {
        this.archiveDocumentFileSearchResult = archiveDocumentFileSearchResult;
    }

    private ArchiveDocument archiveDocumentSearchResult;

    public ArchiveDocument getArchiveDocumentSearchResult() {
        return archiveDocumentSearchResult;
    }

    public void setArchiveDocumentSearchResult(ArchiveDocument archiveDocumentSearchResult) {
        this.archiveDocumentSearchResult = archiveDocumentSearchResult;
    }

    private Boolean usingOcr;

    public Boolean getUsingOcr() {
        return usingOcr;
    }

    public void setUsingOcr(Boolean usingOcr) {
        this.usingOcr = usingOcr;
    }

    private String searchWord;

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    private String documentType;

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    private List<String> documentTypeList;

    public List<String> getDocumentTypeList() {
        return documentTypeList;
    }

    public void setDocumentTypeList(List<String> documentTypeList) {
        this.documentTypeList = documentTypeList;
    }

    private List<ArchiveDocumentFile> archiveDocumentFilesResult;

    public List<ArchiveDocumentFile> getArchiveDocumentFilesResult() {
        return archiveDocumentFilesResult;
    }

    public void setArchiveDocumentFilesResult(List<ArchiveDocumentFile> archiveDocumentFilesResult) {
        this.archiveDocumentFilesResult = archiveDocumentFilesResult;
    }

    private List<LanguageOcr> languagesOcrList;

    public List<LanguageOcr> getLanguagesOcrList() {
        return languagesOcrList;
    }

    public void setLanguagesOcrList(List<LanguageOcr> languagesOcrList) {
        this.languagesOcrList = languagesOcrList;
    }

    private LanguageOcr languageOcr;

    public LanguageOcr getLanguageOcr() {
        return languageOcr;
    }

    public void setLanguageOcr(LanguageOcr languageOcr) {
        this.languageOcr = languageOcr;
    }

    public void getArchiveDocumentFilePhysicalPath(Long archiveDocumentFileId) {
        try {
            for (ArchiveDocumentFile archiveDocumentFile : archiveDocumentFilesResult) {
                if (archiveDocumentFile.getId().equals(archiveDocumentFileId)) {
                    String[] path = archiveDocumentFile.getPhysicalPath()
                            .split(Pattern.quote(FileSystems.getDefault().getSeparator()));
                    String fileCode = path[path.length - 1].split(Pattern.quote("."))[0];
                    String archDocCode = path[path.length - 2];
                    String folderCode = "";
                    String drawerCode = "";
                    String cabinetCode = "";
                    for (String pathStr : path) {
                        if (pathStr.length() == 3)
                            folderCode = pathStr;
                        if (pathStr.length() == 2)
                            drawerCode = pathStr;
                        if (pathStr.contains("-"))
                            cabinetCode = pathStr;
                    }
                    cabinetSearchResult = cabinetService.findByUniqueField("code", cabinetCode);
                    cabinetDefinitionSearchResult = cabinetDefinitionService.getBy(cabinetSearchResult.getId(),
                            drawerCode);
                    cabinetFolderSearchResult = cabinetFolderService.getBy(cabinetDefinitionSearchResult.getId(),
                            folderCode);

                    archiveDocumentFileSearchResult = archiveDocumentFile;
                    archiveDocumentSearchResult = archiveDocumentFileSearchResult.getArchiveDocument();

                    modelPhysicalPath = new DefaultDiagramModel();
                    modelPhysicalPath.setMaxConnections(-1);
                    modelPhysicalPath.setContainment(false);

                    modelPhysicalPath.getDefaultConnectionOverlays().add(new ArrowOverlay(20, 20, 1, 1));
                    StraightConnector connector = new StraightConnector();
                    connector.setPaintStyle("{stroke:'#98AFC7', strokeWidth:3}");
                    connector.setHoverPaintStyle("{stroke:'#5C738B'}");
                    modelPhysicalPath.setDefaultConnector(connector);

                    Element elementCabinet = new Element(
                            new NetworkElement(cabinetSearchResult.getName(), "images/elementCabinet.png"), "5em",
                            "2em");
                    EndPoint endPointCA = createRectangleEndPoint(EndPointAnchor.RIGHT);
                    endPointCA.setSource(true);
                    elementCabinet.addEndPoint(endPointCA);
                    modelPhysicalPath.addElement(elementCabinet);

                    Element elementDrawer = new Element(new NetworkElement(
                            cabinetDefinitionSearchResult.getDrawerName(), "images/element-drawer.png"), "23em", "2em");
                    EndPoint endPointCB = createDotEndPoint(EndPointAnchor.LEFT);
                    endPointCB.setTarget(true);
                    elementDrawer.addEndPoint(endPointCB);

                    EndPoint endPointCC = createRectangleEndPoint(EndPointAnchor.RIGHT);
                    endPointCC.setSource(true);
                    elementDrawer.addEndPoint(endPointCC);
                    modelPhysicalPath.addElement(elementDrawer);

                    Element elementCabinetFolder = new Element(
                            new NetworkElement(cabinetFolderSearchResult.getName(), "images/elementCabinetFolder.png"),
                            "41em", "2em");
                    EndPoint endPointCD = createDotEndPoint(EndPointAnchor.LEFT);
                    endPointCD.setTarget(true);
                    elementCabinetFolder.addEndPoint(endPointCD);

                    EndPoint endPointCE = createRectangleEndPoint(EndPointAnchor.RIGHT);
                    endPointCE.setSource(true);
                    elementCabinetFolder.addEndPoint(endPointCE);
                    modelPhysicalPath.addElement(elementCabinetFolder);

                    Element elementArchiveDocument = new Element(new NetworkElement(
                            archiveDocumentSearchResult.getName(), "images/elementArchiveDocument.png"), "59em", "2em");
                    EndPoint endPointCF = createDotEndPoint(EndPointAnchor.LEFT);
                    endPointCF.setTarget(true);
                    elementArchiveDocument.addEndPoint(endPointCF);
                    EndPoint endPointCG = createRectangleEndPoint(EndPointAnchor.RIGHT);
                    endPointCG.setSource(true);
                    elementArchiveDocument.addEndPoint(endPointCG);
                    modelPhysicalPath.addElement(elementArchiveDocument);

                    Element elementArchiveDocumentFile = new Element(
                            new NetworkElement(archiveDocumentFileSearchResult.getCode(),
                                    "images/elementArchiveDocumentFile.png"),
                            "77em", "2em");
                    EndPoint endPointCH = createDotEndPoint(EndPointAnchor.LEFT);
                    endPointCH.setTarget(true);
                    elementArchiveDocumentFile.addEndPoint(endPointCH);
                    modelPhysicalPath.addElement(elementArchiveDocumentFile);

                    modelPhysicalPath.connect(createConnection(elementCabinet.getEndPoints().get(0),
                            elementDrawer.getEndPoints().get(0), null));
                    modelPhysicalPath.connect(createConnection(elementDrawer.getEndPoints().get(1),
                            elementCabinetFolder.getEndPoints().get(0), null));
                    modelPhysicalPath.connect(createConnection(elementCabinetFolder.getEndPoints().get(1),
                            elementArchiveDocument.getEndPoints().get(0), null));
                    modelPhysicalPath.connect(createConnection(elementArchiveDocument.getEndPoints().get(1),
                            elementArchiveDocumentFile.getEndPoints().get(0), null));

                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void resetPhysicalPath() {
        modelPhysicalPath = new DefaultDiagramModel();
        modelPhysicalPath.setMaxConnections(-1);
        modelPhysicalPath.setContainment(false);
    }

    private Connection createConnection(EndPoint from, EndPoint to, String label) {
        Connection conn = new Connection(from, to);
        conn.getOverlays().add(new ArrowOverlay(20, 20, 1, 1));

        if (label != null) {
            conn.getOverlays().add(new LabelOverlay(label, "flow-label", 0.5));
        }

        return conn;
    }

    private EndPoint createDotEndPoint(EndPointAnchor anchor) {
        DotEndPoint endPoint = new DotEndPoint(anchor);
        endPoint.setScope("network");
        endPoint.setTarget(true);
        endPoint.setStyle("{fill:'#98AFC7'}");
        endPoint.setHoverStyle("{fill:'#5C738B'}");

        return endPoint;
    }

    private EndPoint createRectangleEndPoint(EndPointAnchor anchor) {
        RectangleEndPoint endPoint = new RectangleEndPoint(anchor);
        endPoint.setScope("network");
        endPoint.setSource(true);
        endPoint.setStyle("{fill:'#98AFC7'}");
        endPoint.setHoverStyle("{fill:'#5C738B'}");

        return endPoint;
    }

    public void doSearchInContent() {

        try {
            String rootPath = "";
            if (rootSearch) {
                rootPath = "";
            } else {

                if (selectedArchiveDocument != null)
                    rootPath = getRootPath(selectedArchiveDocument, cabinetFolderId);
                else
                    rootPath = "";
            }

            String mainLocation="";
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                mainLocation = cabinet.getCabinetLocation().getWinLocation();
            } else if (os.contains("osx")) {
                mainLocation = cabinet.getCabinetLocation().getMacLocation();
            } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
                mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
            }

            String dirPath = mainLocation+ FileSystems.getDefault().getSeparator()
                    + cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                    + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                    + FileSystems.getDefault().getSeparator() + rootPath;
            File dir = new File(dirPath);
            filesListInDir = new ArrayList<String>();
            populateFilesList(dir);
            archiveDocumentFilesResult = new ArrayList<ArchiveDocumentFile>();
            archiveDocumentFilesResult.clear();
            for (String filePath : filesListInDir) {
                File file = new File(filePath);
                String fileExt = FileNameUtils.getExtension(file.getName());

                if (fileExt.equalsIgnoreCase(documentType)) {
                    if (usingOcr) {
                        Tesseract tesseract = new Tesseract();

                        tesseract.setDatapath(localizationService.getApplicationProperties().getString("tessdata"));
                        if (languageOcr.getCode().equalsIgnoreCase("Ar"))
                            tesseract.setLanguage("ara");
                        else
                            tesseract.setLanguage("eng");

                        String textExtractor = tesseract.doOCR(new File(filePath));

                        boolean isFound = textExtractor.indexOf(searchWord) != -1 ? true : false;

                        if (isFound) {
                            String[] path = filePath.split(Pattern.quote(FileSystems.getDefault().getSeparator()));
                            String fileCode = path[path.length - 1].split(Pattern.quote("."))[0];
                            String archDocCode = path[path.length - 2];
                            String folderCode = "";
                            String drawerCode = "";
                            String cabinetCode = "";
                            for (String pathStr : path) {
                                if (pathStr.length() == 3)
                                    folderCode = pathStr;
                                if (pathStr.length() == 2)
                                    drawerCode = pathStr;
                                if (pathStr.contains("-"))
                                    cabinetCode = pathStr;
                            }
                            List<ArchiveDocumentFile> archiveDocumentFilesQuery = archiveDocumentFileService
                                    .getBy(fileCode, archDocCode, folderCode, drawerCode, cabinetCode);
                            for (ArchiveDocumentFile archiveDocumentFile : archiveDocumentFilesQuery) {
                                archiveDocumentFile.setPhysicalPath(filePath);
                            }
                            archiveDocumentFilesResult.addAll(archiveDocumentFilesQuery);
                            break;
                        }

                    } else {
                        // Open document
                        Document pdfDocument = null;
                        try {
                            pdfDocument = new Document(filePath);

                            // Create TextAbsorber object to find all instances of the input search phrase
                            TextFragmentAbsorber textFragmentAbsorber = new TextFragmentAbsorber(searchWord);

                            // Accept the absorber for all the pages
                            pdfDocument.getPages().accept(textFragmentAbsorber);

                            // Get the extracted text fragments into collection
                            TextFragmentCollection textFragmentCollection = textFragmentAbsorber.getTextFragments();

                            if (textFragmentCollection.size() > 0) {
                                String[] path = filePath.split(Pattern.quote(FileSystems.getDefault().getSeparator()));
                                String fileCode = path[path.length - 1].split(Pattern.quote("."))[0];
                                String archDocCode = path[path.length - 2];
                                String folderCode = "";
                                String drawerCode = "";
                                String cabinetCode = "";
                                for (String pathStr : path) {
                                    if (pathStr.length() == 3)
                                        folderCode = pathStr;
                                    if (pathStr.length() == 2)
                                        drawerCode = pathStr;
                                    if (pathStr.contains("-"))
                                        cabinetCode = pathStr;
                                }
                                List<ArchiveDocumentFile> archiveDocumentFilesQuery = archiveDocumentFileService
                                        .getBy(fileCode, archDocCode, folderCode, drawerCode, cabinetCode);
                                for (ArchiveDocumentFile archiveDocumentFile : archiveDocumentFilesQuery) {
                                    archiveDocumentFile.setPhysicalPath(filePath);
                                }
                                archiveDocumentFilesResult.addAll(archiveDocumentFilesQuery);

                            }
                        } finally {
                            pdfDocument.close();
                            pdfDocument.freeMemory();
                        }
                    }

                } else {
                    continue;
                }

            }

        } catch (Exception e) {
            // TODO: handle exception

            e.printStackTrace();
        }
    }

    public void doSearchInAttribute() {
        try {
            archiveDocumentFilesResult = new ArrayList<ArchiveDocumentFile>();
            archiveDocumentFilesResult.clear();
            List<ArchiveDocument>  archiveDocumentResultList = archiveDocumentService.getBy(cabinetFolderId,
                    archiveDocument.getArabicName(), archiveDocument.getEnglishName(), archiveDocument.getDescription(),
                    archiveDocument.getArchiveDocumentType(), archiveDocument.getOrganizationByDivan(), archiveDocument.getFromEntity(), archiveDocument.getSubject(), archiveDocument.getYear());
            for (ArchiveDocument archiveDocumentResult : archiveDocumentResultList)
                if (archiveDocumentResult != null) {
                    String rootPath = "";
                    if (rootSearch) {
                        rootPath = "";
                    } else {

                        if (archiveDocumentResult != null)
                            rootPath = getRootPath(archiveDocumentResult, cabinetFolderId);
                        else
                            rootPath = "";
                    }

                    String mainLocation="";
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        mainLocation = cabinet.getCabinetLocation().getWinLocation();
                    } else if (os.contains("osx")) {
                        mainLocation = cabinet.getCabinetLocation().getMacLocation();
                    } else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
                        mainLocation = cabinet.getCabinetLocation().getLinuxLocation();
                    }

                    String dirPath = mainLocation + FileSystems.getDefault().getSeparator()
                            + cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
                            + FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
                            + FileSystems.getDefault().getSeparator() + rootPath;
                    File dir = new File(dirPath);
                    filesListInDir = new ArrayList<String>();
                    populateFilesList(dir);
                    List<ArchiveDocumentFile> archiveDocumentFiles=new ArrayList<ArchiveDocumentFile>();
                    archiveDocumentFiles.addAll(archiveDocumentResult.getArchiveDocumentFiles());
                    for (ArchiveDocumentFile archiveDocumentFile :archiveDocumentFiles ) {
                        for(String filePath :filesListInDir) {
                            String[] path = filePath.split(Pattern.quote(FileSystems.getDefault().getSeparator()));
                            String fileCode = path[path.length - 1].split(Pattern.quote("."))[0];
                            if(fileCode.equalsIgnoreCase(archiveDocumentFile.getCode().trim())) {
                                archiveDocumentFile.setPhysicalPath(filePath);
                                break;
                            }
                        }
                    }

                    archiveDocumentFilesResult.addAll(archiveDocumentFiles);
                }
        } catch (Exception e) {
            // TODO: handle exception

            e.printStackTrace();
        }
    }
    // داخل الكلاس MyCabinetBean
    private boolean showDocumentDialog = false;

    public boolean isShowDocumentDialog() {
        return showDocumentDialog;
    }

    public void setShowDocumentDialog(boolean showDocumentDialog) {
        this.showDocumentDialog = showDocumentDialog;
    }
    public void prepareAttachmentForFolder(String targetFolderCode, CabinetFolder cabinetFolder) {
        this.cabinetFolder = cabinetFolder; // أو فقط استخدم cabinetFolder.getId()
        prepareAttachmentForFolder(targetFolderCode); // إذا تريد إعادة استعمال المنطق القديم
    }



    // #endregion
}