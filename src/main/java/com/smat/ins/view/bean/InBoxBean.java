package com.smat.ins.view.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.compress.utils.FileNameUtils;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.ResponsiveOption;
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
import com.smat.ins.model.data.lazyloading.LazyAllCorrespondenceRecipientDataModel;
import com.smat.ins.model.data.lazyloading.LazyCorrespondenceRecipientDataModel;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.ArchiveDocumentFile;
import com.smat.ins.model.entity.ArchiveDocumentType;
import com.smat.ins.model.entity.BoxType;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.entity.CabinetFolderDocument;
import com.smat.ins.model.entity.CabinetType;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.CorrespondenceArchive;
import com.smat.ins.model.entity.CorrespondenceNote;
import com.smat.ins.model.entity.CorrespondenceRecipient;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.CorrespondenceTransmission;
import com.smat.ins.model.entity.LinkedCorrespondence;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.PriorityType;
import com.smat.ins.model.entity.PurposeType;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.Task;
import com.smat.ins.model.entity.TransmissionType;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.entity.utility.LanguageOcr;
import com.smat.ins.model.service.ArchiveDocumentFileService;
import com.smat.ins.model.service.ArchiveDocumentService;
import com.smat.ins.model.service.ArchiveDocumentTypeService;
import com.smat.ins.model.service.BoxTypeService;
import com.smat.ins.model.service.CabinetDefinitionService;
import com.smat.ins.model.service.CabinetFolderDocumentService;
import com.smat.ins.model.service.CabinetFolderService;
import com.smat.ins.model.service.CabinetService;
import com.smat.ins.model.service.CorrespondenceArchiveService;
import com.smat.ins.model.service.CorrespondenceNoteService;
import com.smat.ins.model.service.CorrespondenceRecipientService;
import com.smat.ins.model.service.CorrespondenceService;
import com.smat.ins.model.service.CorrespondenceStateService;
import com.smat.ins.model.service.LinkedCorrespondenceService;
import com.smat.ins.model.service.OrganizationService;
import com.smat.ins.model.service.PriorityTypeService;
import com.smat.ins.model.service.PurposeTypeService;
import com.smat.ins.model.service.TaskService;
import com.smat.ins.model.service.TransmissionTypeService;
import com.smat.ins.model.service.UserAliasService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LanguageUtil;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;
import com.smat.ins.util.model.Language;
import com.smat.ins.util.model.NetworkElement;
import com.smat.ins.view.app.App;

import net.sourceforge.tess4j.Tesseract;

@Named
@ViewScoped
public class InBoxBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// #region"properties"
	// entity object
	private Long correspondenceID;
	private String correspondenceStr;

	private Long correspondenceRecipientID;
	private String correspondenceRecipientStr;

	private String noteMsg;

	private Correspondence correspondence;
	private CorrespondenceRecipient correspondenceRecipient;
	private SysUser sysUserLogin;
	private CorrespondenceState correspondenceState;
	private CorrespondenceNote correspondenceNote;
	private BoxType boxType;
	private UserAlias userAliasOwner;
	private TransmissionType transmissionType;
	private boolean toArchive;
	private String comment;
	private boolean isTask;

	// list

	private List<UserAlias> myUserAliasList;
	private List<CorrespondenceArchive> correspondenceArchives;
	private List<ArchiveDocumentFile> archiveDocumentFilesList;
	private List<LinkedCorrespondence> linkedCorrespondences;
	private List<CorrespondenceNote> correspondenceNotes;
	private List<UserAlias> selectedUserAliasRecipients;
	private List<Task> tasks;

	// LazyData Model
	private LazyDataModel<CorrespondenceRecipient> correspondenceRecipientLazyDataModel;
	private LazyDataModel<CorrespondenceRecipient> allCorrespondenceRecipientLazyDataModel;

	public Correspondence getCorrespondence() {
		return correspondence;
	}

	public void setCorrespondence(Correspondence correspondence) {
		this.correspondence = correspondence;
	}

	public SysUser getSysUserLogin() {
		return sysUserLogin;
	}

	public void setSysUserLogin(SysUser sysUserLogin) {
		this.sysUserLogin = sysUserLogin;
	}

	public LazyDataModel<CorrespondenceRecipient> getCorrespondenceRecipientLazyDataModel() {
		return correspondenceRecipientLazyDataModel;
	}

	public void setCorrespondenceRecipientLazyDataModel(
			LazyDataModel<CorrespondenceRecipient> correspondenceRecipientLazyDataModel) {
		this.correspondenceRecipientLazyDataModel = correspondenceRecipientLazyDataModel;
	}

	public LazyDataModel<CorrespondenceRecipient> getAllCorrespondenceRecipientLazyDataModel() {
		return allCorrespondenceRecipientLazyDataModel;
	}

	public void setAllCorrespondenceRecipientLazyDataModel(
			LazyDataModel<CorrespondenceRecipient> allCorrespondenceRecipientLazyDataModel) {
		this.allCorrespondenceRecipientLazyDataModel = allCorrespondenceRecipientLazyDataModel;
	}

	public List<UserAlias> getMyUserAliasList() {
		return myUserAliasList;
	}

	public void setMyUserAliasList(List<UserAlias> myUserAliasList) {
		this.myUserAliasList = myUserAliasList;
	}

	public CorrespondenceState getCorrespondenceState() {
		return correspondenceState;
	}

	public void setCorrespondenceState(CorrespondenceState correspondenceState) {
		this.correspondenceState = correspondenceState;
	}

	public BoxType getBoxType() {
		return boxType;
	}

	public void setBoxType(BoxType boxType) {
		this.boxType = boxType;
	}

	public UserAlias getUserAliasOwner() {
		return userAliasOwner;
	}

	public void setUserAliasOwner(UserAlias userAliasOwner) {
		this.userAliasOwner = userAliasOwner;
	}

	public TransmissionType getTransmissionType() {
		return transmissionType;
	}

	public void setTransmissionType(TransmissionType transmissionType) {
		this.transmissionType = transmissionType;
	}

	public boolean isToArchive() {
		return toArchive;
	}

	public void setToArchive(boolean toArchive) {
		this.toArchive = toArchive;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isTask() {
		return isTask;
	}

	public void setTask(boolean isTask) {
		this.isTask = isTask;
	}

	public Long getCorrespondenceID() {
		return correspondenceID;
	}

	public String getCorrespondenceStr() {
		return correspondenceStr;
	}

	public void setCorrespondenceID(Long correspondenceID) {
		this.correspondenceID = correspondenceID;
	}

	public void setCorrespondenceStr(String correspondenceStr) {
		this.correspondenceStr = correspondenceStr;
	}

	public Long getCorrespondenceRecipientID() {
		return correspondenceRecipientID;
	}

	public void setCorrespondenceRecipientID(Long correspondenceRecipientID) {
		this.correspondenceRecipientID = correspondenceRecipientID;
	}

	public String getCorrespondenceRecipientStr() {
		return correspondenceRecipientStr;
	}

	public void setCorrespondenceRecipientStr(String correspondenceRecipientStr) {
		this.correspondenceRecipientStr = correspondenceRecipientStr;
	}

	public CorrespondenceRecipient getCorrespondenceRecipient() {
		return correspondenceRecipient;
	}

	public void setCorrespondenceRecipient(CorrespondenceRecipient correspondenceRecipient) {
		this.correspondenceRecipient = correspondenceRecipient;
	}

	public CorrespondenceNote getCorrespondenceNote() {
		return correspondenceNote;
	}

	public void setCorrespondenceNote(CorrespondenceNote correspondenceNote) {
		this.correspondenceNote = correspondenceNote;
	}

	public String getNoteMsg() {
		return noteMsg;
	}

	public void setNoteMsg(String noteMsg) {
		this.noteMsg = noteMsg;
	}

	public List<CorrespondenceArchive> getCorrespondenceArchives() {
		return correspondenceArchives;
	}

	public void setCorrespondenceArchives(List<CorrespondenceArchive> correspondenceArchives) {
		this.correspondenceArchives = correspondenceArchives;
	}

	public List<ArchiveDocumentFile> getArchiveDocumentFilesList() {
		return archiveDocumentFilesList;
	}

	public void setArchiveDocumentFilesList(List<ArchiveDocumentFile> archiveDocumentFilesList) {
		this.archiveDocumentFilesList = archiveDocumentFilesList;
	}

	public List<LinkedCorrespondence> getLinkedCorrespondences() {
		return linkedCorrespondences;
	}

	public void setLinkedCorrespondences(List<LinkedCorrespondence> linkedCorrespondences) {
		this.linkedCorrespondences = linkedCorrespondences;
	}

	public List<CorrespondenceNote> getCorrespondenceNotes() {
		return correspondenceNotes;
	}

	public void setCorrespondenceNotes(List<CorrespondenceNote> correspondenceNotes) {
		this.correspondenceNotes = correspondenceNotes;
	}

	public List<UserAlias> getSelectedUserAliasRecipients() {
		return selectedUserAliasRecipients;
	}

	public void setSelectedUserAliasRecipients(List<UserAlias> selectedUserAliasRecipients) {
		this.selectedUserAliasRecipients = selectedUserAliasRecipients;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	// #endregion

	// #region"Service"
	// Service
	private CorrespondenceService correspondenceService;
	private CorrespondenceRecipientService correspondenceRecipientService;
	private CorrespondenceStateService correspondenceStateService;
	private BoxTypeService boxTypeService;
	private UserAliasService userAliasService;
	private CorrespondenceNoteService correspondenceNoteService;
	private CorrespondenceArchiveService correspondenceArchiveService;
	private ArchiveDocumentFileService archiveDocumentFileService;
	private LinkedCorrespondenceService linkedCorrespondenceService;
	private CabinetFolderDocumentService cabinetFolderDocumentService;
	private TransmissionTypeService transmissionTypeService;
	private TaskService taskService;

	private LocalizationService localizationService;
	// #endregion

	@Inject
	private LoginBean loginBean;

	@Inject
	App app;

	@Inject
	@Push(channel = "inboxCount")
	private PushContext pushContext;

	@Inject
	@Push(channel = "inbox")
	private PushContext inboxPushContext;

	@PostConstruct
	public void init() {
		languages = LanguageUtil.getAllLanguages(app.getLocale().getLocale());
		correspondenceStr = UtilityHelper.getRequestParameter("oi");
		correspondenceRecipientStr = UtilityHelper.getRequestParameter("oicr");

		try {
			if (correspondenceStr != null) {
				correspondenceID = Long.valueOf(UtilityHelper.decipher(correspondenceStr));
				correspondence = correspondenceService.findById(correspondenceID);
				correspondenceArchives = correspondenceArchiveService.getByCorrespondence(correspondenceID);
				for (CorrespondenceArchive correspondenceArchive : correspondenceArchives) {
					List<ArchiveDocumentFile> archiveDocFiles = archiveDocumentFileService
							.getBy(correspondenceArchive.getArchiveDocument());
					archiveDocumentFilesList.addAll(archiveDocFiles);
				}
				linkedCorrespondences = linkedCorrespondenceService.getByCorrespondence(correspondenceID);

			}
			if (correspondenceRecipientStr != null) {
				correspondenceRecipientID = Long.valueOf(UtilityHelper.decipher(correspondenceRecipientStr));
				correspondenceRecipient = correspondenceRecipientService.findById(correspondenceRecipientID);
				if (correspondenceRecipient.getPurposeType().getCode().equalsIgnoreCase("03")) {
					if (loginBean.hasSysPermission("010")) {
						tasks = taskService.getListInitialTaskByCorrespondence(
								correspondenceRecipient.getCorrespondence().getId(),
								correspondenceRecipient.getUserAlias().getId());
					}

					if (loginBean.hasSysPermission("011")) {
						tasks = taskService.getListReviewdTaskByCorrespondence(
								correspondenceRecipient.getCorrespondence().getId(),loginBean.getUser().getId());
					}
					comment = localizationService.getInfoMessage().getString("goToExecuteTask");
					isTask = true;
				} else {
					comment = "";
					isTask = false;
				}
				correspondenceNote = correspondenceNoteService
						.findById(correspondenceRecipient.getCorrespondenceNote().getId());
				noteMsg = new String(Base64.getMimeDecoder().decode(correspondenceNote.getNote()));
				correspondenceNotes = correspondenceNoteService.getCorrespondenceNotes(correspondence);
				userAliasRecipientList = userAliasService.getListRecipients(correspondenceRecipient.getUserAlias());
			}

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

	public InBoxBean() throws Exception {
		toArchive = false;
		correspondence = new Correspondence();
		correspondenceRecipient = new CorrespondenceRecipient();
		correspondenceNote = new CorrespondenceNote();
		purposeType = new PurposeType();
		priorityType = new PriorityType();
		cabinet = new Cabinet();
		myCabinets = new ArrayList<Cabinet>();
		cabinetDefinition = new CabinetDefinition();
		cabinetDefinitions = new ArrayList<CabinetDefinition>();
		mailAttachementList = new ArrayList<ArchiveDocument>();
		archiveDocumentFilesList = new ArrayList<ArchiveDocumentFile>();
		linkedCorrespondences = new ArrayList<LinkedCorrespondence>();
		selectedUserAliasRecipients = new ArrayList<UserAlias>();
		recipientList = new ArrayList<CorrespondenceRecipient>();
		userAliasRecipientList = new ArrayList<UserAlias>();
		correspondenceService = (CorrespondenceService) BeanUtility.getBean("correspondenceService");
		correspondenceStateService = (CorrespondenceStateService) BeanUtility.getBean("correspondenceStateService");
		boxTypeService = (BoxTypeService) BeanUtility.getBean("boxTypeService");
		correspondenceRecipientService = (CorrespondenceRecipientService) BeanUtility
				.getBean("correspondenceRecipientService");
		correspondenceNoteService = (CorrespondenceNoteService) BeanUtility.getBean("correspondenceNoteService");
		correspondenceArchiveService = (CorrespondenceArchiveService) BeanUtility
				.getBean("correspondenceArchiveService");
		userAliasService = (UserAliasService) BeanUtility.getBean("userAliasService");
		archiveDocumentFileService = (ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");
		linkedCorrespondenceService = (LinkedCorrespondenceService) BeanUtility.getBean("linkedCorrespondenceService");
		cabinetFolderDocumentService = (CabinetFolderDocumentService) BeanUtility
				.getBean("cabinetFolderDocumentService");
		cabinetService = (CabinetService) BeanUtility.getBean("cabinetService");
		cabinetDefinitionService = (CabinetDefinitionService) BeanUtility.getBean("cabinetDefinitionService");
		cabinetFolderService = (CabinetFolderService) BeanUtility.getBean("cabinetFolderService");
		archiveDocumentService = (ArchiveDocumentService) BeanUtility.getBean("archiveDocumentService");
		organizationService = (OrganizationService) BeanUtility.getBean("organizationService");
		archiveDocumentTypeService = (ArchiveDocumentTypeService) BeanUtility.getBean("archiveDocumentTypeService");
		purposeTypeService = (PurposeTypeService) BeanUtility.getBean("purposeTypeService");
		priorityTypeService = (PriorityTypeService) BeanUtility.getBean("priorityTypeService");
		taskService = (TaskService) BeanUtility.getBean("taskService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
		transmissionTypeService = (TransmissionTypeService) BeanUtility.getBean("transmissionTypeService");
		transmissionType = transmissionTypeService.findByUniqueField("code", "02");

		purposeTypes = purposeTypeService.findAll();
		priorityTypes = priorityTypeService.findAll();

		sysUserLogin = (SysUser) UtilityHelper.getSessionAttr("user");

		myUserAliasList = userAliasService.getBySysUser(sysUserLogin);

		// normal state
		correspondenceState = correspondenceStateService.findByUniqueField("code", "02");
		// in box
		boxType = boxTypeService.findByUniqueField("enCode", "IN");

		if (myUserAliasList != null && !myUserAliasList.isEmpty()) {
			userAliasOwner = myUserAliasList.get(0);
			correspondenceRecipientLazyDataModel = new LazyCorrespondenceRecipientDataModel(sysUserLogin,
					correspondenceState);
			myCabinets = cabinetService.myCabinets(userAliasOwner);
			if (myCabinets != null && !myCabinets.isEmpty()) {
				cabinet = myCabinets.get(0);
				cabinetDefinitions = cabinetDefinitionService.getByCabinet(cabinet);
			}
		}
		languages = new ArrayList<>();
		responsiveOptions = new ArrayList<>();
		archiveDocument = new ArchiveDocument();
		archiveDocuments = new ArrayList<ArchiveDocument>();
		archiveDocumentChildrens = new ArrayList<ArchiveDocument>();
		archiveDocumentFiles = new ArrayList<ArchiveDocumentFile>();
		filesListInDir = new ArrayList<String>();
		responsiveOptions.add(new ResponsiveOption("1024px", 3, 3));
		responsiveOptions.add(new ResponsiveOption("768px", 2, 2));
		responsiveOptions.add(new ResponsiveOption("560px", 1, 1));
		pageIndex = 0;
		viewDocFiles = false;

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
	}

	public Long calcTotalNumOfNewCorrespondence(String boxCategory) throws Exception {
		switch (boxCategory.trim()) {
		case "new":
			return correspondenceRecipientService.getInboxCountBySysUser(sysUserLogin, correspondenceState);
		case "all":
			return correspondenceRecipientService.getAllInboxCountBySysUser(sysUserLogin, correspondenceState);
		}
		return null;
	}

	public String decodeNote(byte[] noteEncoded) {
		return new String(Base64.getMimeDecoder().decode(noteEncoded));
	}

	public String decode(byte[] encoded) {
		return new String(Base64.getMimeDecoder().decode(encoded));
	}

	public void assignCorrespondence(CorrespondenceRecipient correspondenceRecipient) throws Exception {
		correspondenceID = correspondenceRecipient.getCorrespondence().getId();
		correspondenceStr = UtilityHelper.cipher(correspondenceID.toString());

		correspondenceRecipientID = correspondenceRecipient.getId();
		correspondenceRecipientStr = UtilityHelper.cipher(correspondenceRecipientID.toString());
		if (correspondenceRecipient.getIsViewed() == false) {
			correspondenceRecipient.setIsViewed(true);
			correspondenceRecipient.setViewedDate(Calendar.getInstance().getTime());
			correspondenceRecipientService.merge(correspondenceRecipient);
		}

	}

	public void prepareDocument(ArchiveDocumentFile archiveDocumentFile) throws Exception {
		CabinetFolder cabinetFolder = cabinetFolderDocumentService.getBy(archiveDocumentFile.getArchiveDocument());
		CabinetDefinition cabinetDefinition = cabinetFolder.getCabinetDefinition();
		Cabinet cabinet = cabinetDefinition.getCabinet();
		UtilityHelper.putSessionAttr("archDocId", archiveDocumentFile.getArchiveDocument().getId());
		UtilityHelper.putSessionAttr("archDocFileId", archiveDocumentFile.getId());
		UtilityHelper.putSessionAttr("cabinetFolderId", cabinetFolder.getId());
		UtilityHelper.putSessionAttr("cabinetId", cabinet.getId());
		UtilityHelper.putSessionAttr("cabinetDefinitionId", cabinetDefinition.getId());
	}

	public void prepareDocumentViewer(ArchiveDocumentFile archiveDocumentFile) throws Exception {
		CabinetFolder cabinetFolder = cabinetFolderDocumentService.getBy(archiveDocumentFile.getArchiveDocument());
		CabinetDefinition cabinetDefinition = cabinetFolder.getCabinetDefinition();
		Cabinet cabinet = cabinetDefinition.getCabinet();
		UtilityHelper.putSessionAttr("archDocId", archiveDocumentFile.getArchiveDocument().getId());
		UtilityHelper.putSessionAttr("archDocFileId", archiveDocumentFile.getId());
		UtilityHelper.putSessionAttr("cabinetFolderId", cabinetFolder.getId());
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

	public boolean doValidate() {
		boolean result = true;
		if (recipientList.size() == 0) {
			UtilityHelper
					.addWarnMessage(localizationService.getErrorMessage().getString("noRecipientsHaveBeenIdentified"));
			result = false;
		}

		return result;
	}

	public String doComment() {
		try {
			CorrespondenceNote recipientNote = new CorrespondenceNote();
			recipientNote.setCorrespondence(correspondence);
			recipientNote.setDeleted(null);
			recipientNote.setDeletedDate(null);
			recipientNote.setHtmlNote(Base64.getMimeEncoder().encode(comment.getBytes()));
			recipientNote.setNote(Base64.getMimeEncoder().encode(comment.getBytes()));
			recipientNote.setNoteDate(Calendar.getInstance().getTime());
			recipientNote.setOrganization(userAliasOwner.getOrganizationByDivan());
			recipientNote.setSeqByCorrespondence(0);
			recipientNote.setToArchive(toArchive);
			recipientNote.setUserAlias(correspondenceRecipient.getUserAlias());
			CorrespondenceTransmission correspondenceTransmissionRecpient = new CorrespondenceTransmission();
			correspondenceTransmissionRecpient.setCorrespondence(correspondence);
			correspondenceTransmissionRecpient.setTransmissionDate(Calendar.getInstance().getTime());
			correspondenceTransmissionRecpient.setTransmissionType(transmissionType);
			correspondenceTransmissionRecpient
					.setUserAliasByFromAlias(correspondenceRecipient.getCorrespondenceNote().getUserAlias());
			correspondenceTransmissionRecpient.setUserAliasByToAlias(correspondenceRecipient.getUserAlias());

			Set<CorrespondenceNote> correspondenceNotes = new HashSet<CorrespondenceNote>();
			Set<CorrespondenceTransmission> correspondenceTransmissions = new HashSet<CorrespondenceTransmission>();
			correspondenceNotes.add(recipientNote);
			correspondenceTransmissions.add(correspondenceTransmissionRecpient);
			Integer seq = 0;
			for (CorrespondenceRecipient correspondenceRecipient : recipientList) {
				CorrespondenceNote correspondenceNote = new CorrespondenceNote();
				correspondenceNote.setCorrespondence(correspondence);
				correspondenceNote.setDeleted(null);
				correspondenceNote.setDeletedDate(null);
				correspondenceNote
						.setHtmlNote(Base64.getMimeEncoder().encode(correspondenceRecipient.getHtmlNote().getBytes()));
				correspondenceNote
						.setNote(Base64.getMimeEncoder().encode(correspondenceRecipient.getNote().getBytes()));
				correspondenceNote.setNoteDate(Calendar.getInstance().getTime());
				correspondenceNote.setOrganization(userAliasOwner.getOrganizationByDivan());
				correspondenceNote.setSeqByCorrespondence(++seq);
				correspondenceNote.setToArchive(null);
				correspondenceNote.setUserAlias(userAliasOwner);
				correspondenceRecipient.setCorrespondenceNote(correspondenceNote);
				correspondenceNotes.add(correspondenceNote);
				CorrespondenceTransmission correspondenceTransmission = new CorrespondenceTransmission();
				correspondenceTransmission.setCorrespondence(correspondence);
				correspondenceTransmission.setTransmissionDate(Calendar.getInstance().getTime());
				correspondenceTransmission.setTransmissionType(transmissionType);
				correspondenceTransmission.setUserAliasByFromAlias(userAliasOwner);
				correspondenceTransmission.setUserAliasByToAlias(correspondenceRecipient.getUserAlias());
				correspondenceTransmissions.add(correspondenceTransmission);
			}
			correspondence.setCorrespondenceTransmissions(correspondenceTransmissions);
			correspondence.setCorrespondenceNotes(correspondenceNotes);
			correspondence.setCorrespondenceRecipients(new HashSet<>(recipientList));
			Set<CorrespondenceArchive> correspondenceArchives = new HashSet<CorrespondenceArchive>();
			for (ArchiveDocument archiveDocument : mailAttachementList) {
				CorrespondenceArchive correspondenceArchive = new CorrespondenceArchive();
				correspondenceArchive.setArchiveDocument(archiveDocument);
				correspondenceArchive.setCorrespondence(correspondence);
				correspondenceArchives.add(correspondenceArchive);
			}
			correspondence.setCorrespondenceArchives(correspondenceArchives);
			correspondenceRecipient.setIsProcessed(true);
			correspondenceRecipient.setProcessingDate(Calendar.getInstance().getTime());
			correspondenceService.updateCorrespondenceWithRecipient(correspondence, correspondenceRecipient);

			for (CorrespondenceRecipient recipient : recipientList) {
				Long inboxNewCount = correspondenceRecipientService
						.getInboxCountBySysUser(recipient.getUserAlias().getSysUserBySysUser(), correspondenceState);
				pushContext.send(inboxNewCount.toString(), recipient.getUserAlias().getSysUserBySysUser().getId());
				inboxPushContext.send("ajaxListenerEvent", recipient.getUserAlias().getSysUserBySysUser().getId());
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "";
		}

		return "pretty:inBox";
	}

	public CorrespondenceRecipient getBy(CorrespondenceNote note) {
		if (note.getCorrespondenceRecipients().isEmpty()) {
			return null;
		} else {
			CorrespondenceRecipient correspondenceRecipient = (CorrespondenceRecipient) note
					.getCorrespondenceRecipients().iterator().next();
			return correspondenceRecipient;
		}
	}

	public void onTabChange(TabChangeEvent event) {
		allCorrespondenceRecipientLazyDataModel = new LazyAllCorrespondenceRecipientDataModel(sysUserLogin,
				correspondenceState);
	}

	public void viewNotifictions() {
		correspondenceRecipientLazyDataModel = new LazyCorrespondenceRecipientDataModel(sysUserLogin,
				correspondenceState);
	}

	public void viewInbox(SysUser sysUser) {
		correspondenceRecipientLazyDataModel = new LazyCorrespondenceRecipientDataModel(sysUser, correspondenceState);
		allCorrespondenceRecipientLazyDataModel = new LazyAllCorrespondenceRecipientDataModel(sysUser,
				correspondenceState);
	}

	public String rowStyleDatatable(CorrespondenceRecipient correspondenceRecipient) {
		if (correspondenceRecipient.getIsViewed() == null || !correspondenceRecipient.getIsViewed())
			return "new-correspondence";
		else
			return "";
	}

	public String iconMail(CorrespondenceRecipient correspondenceRecipient) {
		if (correspondenceRecipient.getIsViewed() == null || !correspondenceRecipient.getIsViewed())
			return "fa fa-envelope text-new";
		else
			return "fa fa-envelope-open-o text-open";
	}

	// #region"recipient "

	private PurposeType purposeType;
	private PriorityType priorityType;
	private String note;
	private String htmlNote;

	private List<PurposeType> purposeTypes;
	private List<PriorityType> priorityTypes;
	private List<CorrespondenceRecipient> recipientList;
	private List<UserAlias> userAliasRecipientList;

	private PurposeTypeService purposeTypeService;
	private PriorityTypeService priorityTypeService;

	public PurposeType getPurposeType() {
		return purposeType;
	}

	public void setPurposeType(PurposeType purposeType) {
		this.purposeType = purposeType;
	}

	public PriorityType getPriorityType() {
		return priorityType;
	}

	public void setPriorityType(PriorityType priorityType) {
		this.priorityType = priorityType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getHtmlNote() {
		return htmlNote;
	}

	public void setHtmlNote(String htmlNote) {
		this.htmlNote = htmlNote;
	}

	public List<PurposeType> getPurposeTypes() {
		return purposeTypes;
	}

	public void setPurposeTypes(List<PurposeType> purposeTypes) {
		this.purposeTypes = purposeTypes;
	}

	public List<PriorityType> getPriorityTypes() {
		return priorityTypes;
	}

	public void setPriorityTypes(List<PriorityType> priorityTypes) {
		this.priorityTypes = priorityTypes;
	}

	public List<CorrespondenceRecipient> getRecipientList() {
		return recipientList;
	}

	public void setRecipientList(List<CorrespondenceRecipient> recipientList) {
		this.recipientList = recipientList;
	}

	public List<UserAlias> getUserAliasRecipientList() {
		return userAliasRecipientList;
	}

	public void setUserAliasRecipientList(List<UserAlias> userAliasRecipientList) {
		this.userAliasRecipientList = userAliasRecipientList;
	}

	public void addCorrespondenceRecipient() {

		for (UserAlias userAlias : selectedUserAliasRecipients) {
			CorrespondenceRecipient correspondenceRecipient = new CorrespondenceRecipient();
			correspondenceRecipient.setUserAlias(userAlias);
			correspondenceRecipient.setPurposeType(purposeType);
			correspondenceRecipient.setPriorityType(priorityType);
			correspondenceRecipient.setOrganization(userAlias.getOrganizationByDivan());
			correspondenceRecipient.setCorrespondence(correspondence);
			correspondenceRecipient.setIsViewed(false);
			correspondenceRecipient.setNote(note);
			correspondenceRecipient.setHtmlNote(htmlNote);
			if (!checkIfContain(correspondenceRecipient))
				recipientList.add(correspondenceRecipient);
		}

		setNote("");
		setHtmlNote("");

		PrimeFaces.current().ajax().update("form:messages", "form:panelGrid_editor");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-correspondenceRecipient");

	}

	public boolean checkIfContain(CorrespondenceRecipient correspondenceRecipient) {
		Boolean result = false;
		for (CorrespondenceRecipient recipient : recipientList) {
			if (recipient.getUserAlias().getId().equals(correspondenceRecipient.getUserAlias().getId())) {
				result = true;
				break;
			}
		}
		return result;
	}

	public void removeCorrespondenceRecipient(CorrespondenceRecipient correspondenceRecipient) {
		recipientList.remove(correspondenceRecipient);
		for (UserAlias toAlias : selectedUserAliasRecipients) {
			if (toAlias.getId().equals(correspondenceRecipient.getUserAlias().getId())) {
				selectedUserAliasRecipients.remove(toAlias);
				break;
			}
		}

		PrimeFaces.current().ajax().update("form:dt-correspondenceRecipient",
				"form:selectCheckBoxMenu_UserAliasRecpient");

	}

	public void updateBadge() {
		PrimeFaces.current().ajax().update("form:badge_recipient_size", "form:badge_recipients");

	}

	public String localizationCkEditorToolBar() {
		if (UtilityHelper.getLocale().getLanguage().contains("en"))
			return "[{ name: 'document', groups: [ 'mode', 'document', 'doctools' ], items: [ 'Source', '-', 'Save', 'NewPage', 'ExportPdf', 'Preview', 'Print', '-', 'Templates' ] },{ name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', '-', 'Undo', 'Redo' ] },{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Find', 'Replace', '-', 'SelectAll', '-', 'Scayt' ] },{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'CopyFormatting', 'RemoveFormat' ] },{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl', 'Language' ] },{ name: 'links', items: [ 'Link', 'Unlink']},{ name: 'insert', items: [ 'Image', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak'] },{ name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },{ name: 'colors', items: [ 'TextColor', 'BGColor' ] },{ name: 'tools', items: [ 'Maximize']},{ name: 'others', items: [ '-' ] }]";
		else
			return "[{ name: 'document', groups: [ 'mode', 'document', 'doctools' ], items: [ 'Source', '-', 'Save', 'NewPage', 'ExportPdf', 'Preview', 'Print', '-', 'Templates' ] },{ name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', '-', 'Undo', 'Redo' ] },{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Find', 'Replace', '-', 'SelectAll', '-', 'Scayt' ] },{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'CopyFormatting', 'RemoveFormat' ] },{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyRight', 'JustifyCenter', 'JustifyLeft', 'JustifyBlock', '-', 'BidiRtl', 'BidiLtr', 'Language' ] },{ name: 'links', items: [ 'Link', 'Unlink']},{ name: 'insert', items: [ 'Image', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak'] },{ name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },{ name: 'colors', items: [ 'TextColor', 'BGColor' ] },{ name: 'tools', items: [ 'Maximize']},{ name: 'others', items: [ '-' ] }]";
	}

	public String localizationCkEditorNoteToolBar() {
		if (UtilityHelper.getLocale().getLanguage().contains("en"))
			return "[{ name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', '-', 'Undo', 'Redo' ] },{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Find', 'Replace', '-', 'SelectAll', '-', 'Scayt' ] },{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'CopyFormatting', 'RemoveFormat' ] },{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl' ] },{ name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },{ name: 'colors', items: [ 'TextColor', 'BGColor' ] }]";
		else
			return "[{ name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', '-', 'Undo', 'Redo' ] },{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Find', 'Replace', '-', 'SelectAll', '-', 'Scayt' ] },{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'CopyFormatting', 'RemoveFormat' ] },{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyRight', 'JustifyCenter', 'JustifyLeft', 'JustifyBlock', '-', 'BidiRtl', 'BidiLtr' ] },{ name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },{ name: 'colors', items: [ 'TextColor', 'BGColor' ] }]";
	}
	// #endregion

	// #region "archive module"
	private Cabinet cabinet;
	private CabinetDefinition cabinetDefinition;
	private CabinetFolder cabinetFolder;
	private ArchiveDocument archiveDocument;
	private ArchiveDocument selectedArchiveDocument;

	private TreeNode root;
	private TreeNode node;
	private TreeNode selectedNode;

	private Short pageIndex;
	private Boolean viewDocFiles;
	private List<Cabinet> myCabinets;
	private List<CabinetDefinition> cabinetDefinitions;
	private List<CabinetFolder> cabinetFolders;
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

	public Cabinet getCabinet() {
		return cabinet;
	}

	public void setCabinet(Cabinet cabinet) {
		this.cabinet = cabinet;
	}

	public CabinetDefinition getCabinetDefinition() {
		return cabinetDefinition;
	}

	public void setCabinetDefinition(CabinetDefinition cabinetDefinition) {
		this.cabinetDefinition = cabinetDefinition;
	}

	public CabinetFolder getCabinetFolder() {
		return cabinetFolder;
	}

	public void setCabinetFolder(CabinetFolder cabinetFolder) {
		this.cabinetFolder = cabinetFolder;
	}

	public ArchiveDocument getArchiveDocument() {
		return archiveDocument;
	}

	public void setArchiveDocument(ArchiveDocument archiveDocument) {
		this.archiveDocument = archiveDocument;
	}

	public ArchiveDocument getSelectedArchiveDocument() {
		return selectedArchiveDocument;
	}

	public void setSelectedArchiveDocument(ArchiveDocument selectedArchiveDocument) {
		this.selectedArchiveDocument = selectedArchiveDocument;
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

	public List<CabinetDefinition> getCabinetDefinitions() {
		return cabinetDefinitions;
	}

	public void setCabinetDefinitions(List<CabinetDefinition> cabinetDefinitions) {
		this.cabinetDefinitions = cabinetDefinitions;
	}

	public List<CabinetFolder> getCabinetFolders() {
		return cabinetFolders;
	}

	public void setCabinetFolders(List<CabinetFolder> cabinetFolders) {
		this.cabinetFolders = cabinetFolders;
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

	public List<Cabinet> getMyCabinets() {
		return myCabinets;
	}

	public void setMyCabinets(List<Cabinet> myCabinets) {
		this.myCabinets = myCabinets;
	}

	public List<ArchiveDocumentType> getArchiveDocumentTypes() {
		return archiveDocumentTypes;
	}

	public void setArchiveDocumentTypes(List<ArchiveDocumentType> archiveDocumentTypes) {
		this.archiveDocumentTypes = archiveDocumentTypes;
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

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
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

	public List<ResponsiveOption> getResponsiveOptions() {
		return responsiveOptions;
	}

	public void setResponsiveOptions(List<ResponsiveOption> responsiveOptions) {
		this.responsiveOptions = responsiveOptions;
	}

	private CabinetService cabinetService;
	private CabinetDefinitionService cabinetDefinitionService;
	private CabinetFolderService cabinetFolderService;
	private ArchiveDocumentService archiveDocumentService;
	private ArchiveDocumentTypeService archiveDocumentTypeService;

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

	public void assignCabinetAndContinue() throws Exception {
		if (myCabinets != null && myCabinets.size() > 0) {
			this.cabinet = myCabinets.get(0);
			cabinetDefinitions = cabinetDefinitionService.getByCabinet(this.cabinet);
			if (cabinetDefinitions != null && cabinetDefinitions.size() > 0) {
				this.cabinetDefinition = cabinetDefinitions.get(0);
				cabinetFolders = cabinetFolderService.getByCabinetDefinition(cabinetDefinition);

				UtilityHelper.putSessionAttr("pageIndex", pageIndex);
			}
		}
	}

	public void assignCabinet(Cabinet cabinet) throws Exception {
		this.cabinet = cabinet;
		cabinetDefinitions = cabinetDefinitionService.getByCabinet(this.cabinet);
		if (cabinetDefinitions != null && cabinetDefinitions.size() > 0) {
			this.cabinetDefinition = cabinetDefinitions.get(0);
			cabinetFolders = cabinetFolderService.getByCabinetDefinition(cabinetDefinition);

			UtilityHelper.putSessionAttr("pageIndex", pageIndex);
		}
	}

	public void assignCabinetDefinition(CabinetDefinition cabinetDefinition) throws Exception {
		if (cabinetDefinition != null) {
			this.cabinetDefinition = cabinetDefinition;
			cabinetFolders = cabinetFolderService.getByCabinetDefinition(cabinetDefinition);

			UtilityHelper.putSessionAttr("pageIndex", pageIndex);
		}
	}

	public void openNewCabinetFolder() throws Exception {

		this.cabinetFolder = new CabinetFolder();

		Integer maxFolderCodeLength = cabinetFolderService.getMaxCabinetCode(cabinetDefinition);
		if (maxFolderCodeLength != null) {
			cabinetFolder.setCode(String.format("%0" + folderCodeLength + "d", maxFolderCodeLength + 1));
		}
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
				cabinetFolder.setSysUser(sysUserLogin);
				cabinetFolder.setCreatedDate(Calendar.getInstance().getTime());
				cabinetFolderService.saveOrUpdate(cabinetFolder);
				cabinetFolders = cabinetFolderService.getByCabinetDefinition(cabinetDefinition);
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

	public void assignCabinetFolder(CabinetFolder cabinetFolder) throws Exception {
		this.cabinetFolder = cabinetFolder;
		archiveDocumentChildrens = new ArrayList<ArchiveDocument>();
		archiveDocuments = archiveDocumentService.getByParent(null, cabinetFolder.getId(), null, null);
		archiveDocumentChildrens.addAll(archiveDocuments);
		intializeRootAndFirstLevel();
		viewDocFiles = false;
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
		Long maxArchiveDocumentCodeLenght = archiveDocumentService.getMaxArchiveDocumentCode(null,
				this.cabinetFolder.getId());
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

	private void intializeRootAndFirstLevel() throws Exception {
		this.root = new DefaultTreeNode("Root", null, null);
		for (ArchiveDocument rootArchiveDocument : archiveDocuments) {
			this.node = new DefaultTreeNode(rootArchiveDocument, this.root);
			List<ArchiveDocument> firstLevel = archiveDocumentService.getByParent(rootArchiveDocument,
					cabinetFolder.getId(), null, null);
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
					.getByParent(((ArchiveDocument) treeNode.getData()), cabinetFolder.getId(), null, null);
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
			selectedArchiveDocument = archiveDocumentService.getArchiveDocumentWithDetails(
					(ArchiveDocument) selectedNode.getData(), this.cabinetFolder.getId());
			breadcrumbList = getListBreadcrumbs(selectedArchiveDocument, this.cabinetFolder.getId());
			archiveDocumentChildrens = archiveDocumentService.getByParent(selectedArchiveDocument,
					this.cabinetFolder.getId(), null, null);
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
			breadcrumbList = getListBreadcrumbs(selectedArchiveDocument, cabinetFolder.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		archiveDocumentChildrens = archiveDocumentService.getByParent(selectedArchiveDocument, cabinetFolder.getId(),
				null, null);
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
				cabinetFolder.getId());
		if (maxArchiveDocumentCodeLenght != null) {
			this.archiveDocument.setSeq(maxArchiveDocumentCodeLenght + 1);
			this.archiveDocument
					.setCode(String.format("%0" + documentCodeLength + "d", maxArchiveDocumentCodeLenght + 1));
		}
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
					.getMaxArchiveDocumentCode(selectedArchiveDocument, cabinetFolder.getId());
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
			Long maxArchiveDocumentCodeLenght = archiveDocumentService.getMaxArchiveDocumentCode(null,
					cabinetFolder.getId());
			if (maxArchiveDocumentCodeLenght != null) {
				archiveDocument.setSeq(maxArchiveDocumentCodeLenght + 1);
				archiveDocument
						.setCode(String.format("%0" + documentCodeLength + "d", maxArchiveDocumentCodeLenght + 1));
			}
		}
	}

	public void doSaveArchiveDoc() {

		if (doValidateArchiveDoc()) {
			try {
				String rootPath = "";
				if (selectedArchiveDocument != null)
					rootPath = getRootPath(selectedArchiveDocument, cabinetFolder.getId());
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
				String dirPath = mainLocation+ FileSystems.getDefault().getSeparator()
						+ cabinet.getCode() + FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
						+ FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
						+ FileSystems.getDefault().getSeparator() + rootPath + archiveDocument.getCode();
				Files.createDirectories(Paths.get(dirPath));

				if (archiveDocument.getIsEdit()) {
					archiveDocumentService.updateArchiveDoc(archiveDocument, archiveDocumentFiles, dirPath);
					PrimeFaces.current().ajax().update("PF('addArchiveWidgetVar').hide();");
					UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				} else {

					if (selectedArchiveDocument != null) {
						archiveDocument.setArchiveDocument(selectedArchiveDocument);
					}

					archiveDocument.setSysUserByCreatorUser(sysUserLogin);
					archiveDocument.setCreatedDate(Calendar.getInstance().getTime());
					CabinetFolderDocument cabinetFolderDocument = new CabinetFolderDocument();
					cabinetFolderDocument.setArchiveDocument(archiveDocument);
					cabinetFolderDocument.setCabinetFolder(cabinetFolder);
					cabinetFolderDocument.setCreatedDate(Calendar.getInstance().getTime());
					cabinetFolderDocument.setSysUser(sysUserLogin);

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

	private String getRootPath(ArchiveDocument archiveDocument, Long cabinetFolderId) throws Exception {
		String rootPath = archiveDocument.getCode();
		while (archiveDocument.getArchiveDocument() != null) {
			archiveDocument = archiveDocumentService.getParentArchiveDocument(archiveDocument, cabinetFolderId);
			rootPath += FileSystems.getDefault().getSeparator() + archiveDocument.getCode();

		}
		return rootPath;
	}

	public void reset() throws Exception {
		this.archiveDocument = new ArchiveDocument();
		if (archiveDocumentTypes != null && !archiveDocumentTypes.isEmpty())
			this.archiveDocument.setArchiveDocumentType(archiveDocumentTypes.get(0));
		this.archiveDocument.setYear((short) Calendar.getInstance().get(Calendar.YEAR));
		Long maxArchiveDocumentCodeLenght = archiveDocumentService.getMaxArchiveDocumentCode(selectedArchiveDocument,
				cabinetFolder.getId());
		if (maxArchiveDocumentCodeLenght != null) {
			archiveDocument.setSeq(maxArchiveDocumentCodeLenght + 1);
			archiveDocument.setCode(String.format("%0" + documentCodeLength + "d", maxArchiveDocumentCodeLenght + 1));
		}
	}

	public boolean doValidateArchiveDoc() {
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

			ArchiveDocument archiveDocumentObj = archiveDocumentService.getBy(cabinetFolder.getId(),
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

	private List<ArchiveDocument> getListBreadcrumbs(ArchiveDocument archiveDocument, Long cabinetFolderId)
			throws Exception {
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
				rootPath = getRootPath(selectedArchiveDocument, cabinetFolder.getId());
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
				rootPath = getRootPath(selectedArchiveDocument, this.cabinetFolder.getId());
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
				rootPath = getRootPath(selectedArchiveDocument, cabinetFolder.getId());
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
			UtilityHelper.putSessionAttr("cabinetFolderId", cabinetFolder.getId());
			UtilityHelper.putSessionAttr("cabinetId", cabinet.getId());
			UtilityHelper.putSessionAttr("cabinetDefinitionId", cabinetDefinition.getId());

			String rootPath = "";
			if (selectedArchiveDocument != null)
				rootPath = getRootPath(selectedArchiveDocument, cabinetFolder.getId());
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
			String dirPath = mainLocation+ FileSystems.getDefault().getSeparator()
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

	// #endregion

	// #region "Organization"
	private OrganizationService organizationService;

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
			selectedArchiveDocument = archiveDocumentService.getArchiveDocumentWithDetails(
					(ArchiveDocument) selectedNode.getData(), this.cabinetFolder.getId());
			breadcrumbList = getListBreadcrumbs(selectedArchiveDocument, this.cabinetFolder.getId());
			archiveDocumentChildrens = archiveDocumentService.getByParent(selectedArchiveDocument,
					this.cabinetFolder.getId(), null, null);
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
			if (event.getFile().getFileName().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 100) {
				UtilityHelper
						.addWarnMessage(localizationService.getInterfaceLabel().getString("fileNameMaxLengthShouldBe")
								+ ":  " + event.getFile().getFileName());
				return;
			}
			ArchiveDocumentFile archiveDocumentFile = new ArchiveDocumentFile();
			archiveDocumentFile.setArchiveDocument(archiveDocument);
			archiveDocumentFile.setName(new String(event.getFile().getFileName().getBytes("ISO-8859-1"), "UTF-8"));
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
				UtilityHelper.addWarnMessage(new String(
						localizationService.getInterfaceLabel().getString("theFile").getBytes(Charset.defaultCharset()),
						"UTF-8")
						+ " ( "
						+ new String(archiveDocumentInstance.getName().trim().getBytes(Charset.defaultCharset()),
								"UTF-8")
						+ " ) " + new String(localizationService.getInterfaceLabel().getString("hadNotBeenAdded")
								.getBytes(Charset.defaultCharset()), "UTF-8"));
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
					rootPath = getRootPath(selectedArchiveDocument, cabinetFolder.getId());
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
			List<ArchiveDocument> archiveDocumentResultList = archiveDocumentService.getBy(cabinetFolder.getId(),
					archiveDocument.getArabicName(), archiveDocument.getEnglishName(), archiveDocument.getDescription(),
					archiveDocument.getArchiveDocumentType(), archiveDocument.getOrganizationByDivan(),
					archiveDocument.getFromEntity(), archiveDocument.getSubject(), archiveDocument.getYear());
			for (ArchiveDocument archiveDocumentResult : archiveDocumentResultList)
				if (archiveDocumentResult != null) {
					String rootPath = "";
					if (rootSearch) {
						rootPath = "";
					} else {

						if (archiveDocumentResult != null)
							rootPath = getRootPath(archiveDocumentResult, cabinetFolder.getId());
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
					String dirPath = mainLocation
							+ FileSystems.getDefault().getSeparator() + cabinet.getCode()
							+ FileSystems.getDefault().getSeparator() + cabinetDefinition.getCode()
							+ FileSystems.getDefault().getSeparator() + cabinetFolder.getCode()
							+ FileSystems.getDefault().getSeparator() + rootPath;
					File dir = new File(dirPath);
					filesListInDir = new ArrayList<String>();
					populateFilesList(dir);
					List<ArchiveDocumentFile> archiveDocumentFiles = new ArrayList<ArchiveDocumentFile>();
					archiveDocumentFiles.addAll(archiveDocumentResult.getArchiveDocumentFiles());
					for (ArchiveDocumentFile archiveDocumentFile : archiveDocumentFiles) {
						for (String filePath : filesListInDir) {
							String[] path = filePath.split(Pattern.quote(FileSystems.getDefault().getSeparator()));
							String fileCode = path[path.length - 1].split(Pattern.quote("."))[0];
							if (fileCode.equalsIgnoreCase(archiveDocumentFile.getCode().trim())) {
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

	// #endregion

	// #region "mail attachement"
	private List<ArchiveDocument> mailAttachementList;

	public List<ArchiveDocument> getMailAttachementList() {
		return mailAttachementList;
	}

	public void setMailAttachementList(List<ArchiveDocument> mailAttachementList) {
		this.mailAttachementList = mailAttachementList;
	}

	public boolean checkIfDocInAttachementList(ArchiveDocument archiveDocument) {
		boolean result = false;
		for (ArchiveDocument document : mailAttachementList)
			if (document.getId().equals(archiveDocument.getId())) {
				result = true;
				break;
			}
		return result;
	}

	public void addToMailAttachementList(ArchiveDocument archiveDocument) {
		if (!checkIfDocInAttachementList(archiveDocument))
			mailAttachementList.add(archiveDocument);

	}

	public void removeFromMailAttachementList(ArchiveDocument archiveDocument) {
		if (checkIfDocInAttachementList(archiveDocument))
			mailAttachementList.remove(archiveDocument);

	}

	// #endregion

}
