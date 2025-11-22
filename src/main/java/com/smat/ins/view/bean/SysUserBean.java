package com.smat.ins.view.bean;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.shaded.commons.io.FilenameUtils;

import com.smat.ins.model.data.lazyloading.LazyUserDataModel;
import com.smat.ins.model.entity.GenericArchiveDoc;
import com.smat.ins.model.entity.JobPosition;
import com.smat.ins.model.entity.Organization;

import com.smat.ins.model.entity.SysRole;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.SysUserRole;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.service.JobPositionService;
import com.smat.ins.model.service.OrganizationService;
import com.smat.ins.model.service.SysUserRoleService;
import com.smat.ins.model.service.SysUserService;
import com.smat.ins.model.service.UserAliasService;
import com.smat.ins.util.BCrypt;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;

@Named
@ViewScoped
public class SysUserBean implements Serializable {

	// #region "properties"
	private static final long serialVersionUID = -6940976497901194753L;

	// entity object
	private SysUser sysUser;
	private SysUserRole sysUserRole;
	private UserAlias userAlias;

	private Long sysUserID;
	private String sysUserStr;
	private String sysUserForUserAliasStr;
	private String sysUserForRoleStr;

	// list

	private List<SysUser> selectedSysUserList;
	private List<SysRole> sysRoleList;
	private List<SysRole> selectedSysRoleList;

	private List<SysUserRole> sysUserRoleList;
	private List<JobPosition> jobPositionList;
	private List<UserAlias> userAliasList;
	private List<Organization> organizationList;
	private List<GenericArchiveDoc> genericArchiveDocs;
	private ResourceBundle applicationSetting;
	private Integer userAliasCodeLength;
	private Integer userCodeLength;
	private Integer userNameCodeLength;

	private LazyDataModel<SysUser> lazyDataModelSysUser;

	public SysUser getSysUser() {
		return sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public SysUserRole getSysUserRole() {
		return sysUserRole;
	}

	public void setSysUserRole(SysUserRole sysUserRole) {
		this.sysUserRole = sysUserRole;
	}

	public LazyDataModel<SysUser> getLazyDataModelSysUser() {
		return lazyDataModelSysUser;
	}

	public void setLazyDataModelSysUser(LazyDataModel<SysUser> lazyDataModelSysUser) {
		this.lazyDataModelSysUser = lazyDataModelSysUser;
	}

	public List<SysUserRole> getSysUserRoleList() {
		return sysUserRoleList;
	}

	public void setSysUserRoleList(List<SysUserRole> sysUserRoleList) {
		this.sysUserRoleList = sysUserRoleList;
	}

	public List<SysRole> getSysRoleList() {
		return sysRoleList;
	}

	public void setSysRoleList(List<SysRole> sysRoleList) {
		this.sysRoleList = sysRoleList;
	}

	public List<SysRole> getSelectedSysRoleList() {
		return selectedSysRoleList;
	}

	public void setSelectedSysRoleList(List<SysRole> selectedSysRoleList) {
		this.selectedSysRoleList = selectedSysRoleList;
	}

	public List<SysUser> getSelectedSysUserList() {
		return selectedSysUserList;
	}

	public void setSelectedSysUserList(List<SysUser> selectedSysUserList) {
		this.selectedSysUserList = selectedSysUserList;
	}

	public List<JobPosition> getJobPositionList() {
		return jobPositionList;
	}

	public void setJobPositionList(List<JobPosition> jobPositionList) {
		this.jobPositionList = jobPositionList;
	}

	public UserAlias getUserAlias() {
		return userAlias;
	}

	public void setUserAlias(UserAlias userAlias) {
		this.userAlias = userAlias;
	}

	public List<UserAlias> getUserAliasList() {
		return userAliasList;
	}

	public void setUserAliasList(List<UserAlias> userAliasList) {
		this.userAliasList = userAliasList;
	}

	public List<Organization> getOrganizationList() {
		return organizationList;
	}

	public void setOrganizationList(List<Organization> organizationList) {
		this.organizationList = organizationList;
	}
	
	public List<GenericArchiveDoc> getGenericArchiveDocs() {
		return genericArchiveDocs;
	}

	public void setGenericArchiveDocs(List<GenericArchiveDoc> genericArchiveDocs) {
		this.genericArchiveDocs = genericArchiveDocs;
	}

	public Long getSysUserID() {
		return sysUserID;
	}

	public void setSysUserID(Long sysUserID) {
		this.sysUserID = sysUserID;
	}

	public String getSysUserStr() {
		return sysUserStr;
	}

	public void setSysUserStr(String sysUserStr) {
		this.sysUserStr = sysUserStr;
	}

	public String getSysUserForRoleStr() {
		return sysUserForRoleStr;
	}

	public void setSysUserForRoleStr(String sysUserForRoleStr) {
		this.sysUserForRoleStr = sysUserForRoleStr;
	}

	public String getSysUserForUserAliasStr() {
		return sysUserForUserAliasStr;
	}

	public void setSysUserForUserAliasStr(String sysUserForUserAliasStr) {
		this.sysUserForUserAliasStr = sysUserForUserAliasStr;
	}

	// #endregion

	// #region"services"
	// Service
	private SysUserService sysUserService;
	private SysUserRoleService sysUserRoleService;
	private JobPositionService jobPositionService;
	private UserAliasService userAliasService;
	private OrganizationService organizationService;
	private LocalizationService localizationService;
	// #endregion

	@PostConstruct
	public void init() {

		sysUserStr = UtilityHelper.getRequestParameter("su");
		sysUserForRoleStr = UtilityHelper.getRequestParameter("ur");
		sysUserForUserAliasStr = UtilityHelper.getRequestParameter("ua");
		try {

			if (sysUserStr != null) {
				sysUserID = Long.valueOf(UtilityHelper.decipher(sysUserStr));
				sysUser = sysUserService.findById(sysUserID);
				sysUser.setIsEditMode(true);
				sysUser.setIsAlterUserAliasMode(false);
				if (sysUser != null) {
					userAliasList = userAliasService.getBySysUser(sysUser);
				}
				
				GenericArchiveDoc archiveDoc = new GenericArchiveDoc();
				archiveDoc.setId(1);
				archiveDoc.setData(sysUser.getSignturePhoto());
				
				genericArchiveDocs.add(archiveDoc);

			}

			if (sysUserForUserAliasStr != null) {
				sysUserID = Long.valueOf(UtilityHelper.decipher(sysUserForUserAliasStr));
				sysUser = sysUserService.findById(sysUserID);
				sysUser.setIsAlterUserAliasMode(true);
				sysUser.setIsEditMode(true);
				if (sysUser != null) {
					userAliasList = userAliasService.getBySysUser(sysUser);
				}

			}

			if (sysUserForRoleStr != null) {
				sysUserID = Long.valueOf(UtilityHelper.decipher(sysUserForRoleStr));
				sysUser = sysUserService.findById(sysUserID);
				if (sysUser != null) {
					sysUserRoleList = sysUserRoleService.getRoleBySysUser(sysUser);
				}
			}
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}

	}

	public SysUserBean() throws Exception {
		sysUser = new SysUser();
		sysUser.setIsAlterUserAliasMode(false);
		sysUser.setIsEditMode(false);
		userAliasList = new ArrayList<>();
		userAlias = new UserAlias();
		sysUserRole = new SysUserRole();

		selectedSysUserList = new ArrayList<SysUser>();
		sysRoleList = new ArrayList<SysRole>();
		selectedSysRoleList = new ArrayList<SysRole>();
		sysUserRoleList = new ArrayList<SysUserRole>();
		jobPositionList = new ArrayList<JobPosition>();
		userAliasList = new ArrayList<UserAlias>();
		organizationList = new ArrayList<Organization>();
		genericArchiveDocs=new ArrayList<GenericArchiveDoc>();

		sysUserService = (SysUserService) BeanUtility.getBean("sysUserService");
		sysUserRoleService = (SysUserRoleService) BeanUtility.getBean("sysUserRoleService");
		jobPositionService = (JobPositionService) BeanUtility.getBean("jobPositionService");
		userAliasService = (UserAliasService) BeanUtility.getBean("userAliasService");
		organizationService = (OrganizationService) BeanUtility.getBean("organizationService");

		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");

		lazyDataModelSysUser = new LazyUserDataModel();
		jobPositionList = jobPositionService.findAll();
		organizationList = organizationService.getAllWithDetails();
		applicationSetting = ResourceBundle.getBundle("com.smat.ins.view.resources.applicationSetting");
		if (applicationSetting.containsKey("maxUserAliasCodeLength")
				&& applicationSetting.getString("maxUserAliasCodeLength") != null) {
			userAliasCodeLength = Integer.parseInt(applicationSetting.getString("maxUserAliasCodeLength"));

		} else {
			userAliasCodeLength = 5;
		}

		if (applicationSetting.containsKey("maxUserCodeLength")
				&& applicationSetting.getString("maxUserCodeLength") != null) {
			userCodeLength = Integer.parseInt(applicationSetting.getString("maxUserCodeLength"));

		} else {
			userCodeLength = 6;
		}

		if (applicationSetting.containsKey("maxUserNameCodeLength")
				&& applicationSetting.getString("maxUserNameCodeLength") != null) {
			userNameCodeLength = Integer.parseInt(applicationSetting.getString("maxUserNameCodeLength"));

		} else {
			userNameCodeLength = 6;
		}
	}

	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.sysUser = new SysUser();
	}

	public boolean hasSelectedSysUsers() {
		return this.selectedSysUserList != null && !this.selectedSysUserList.isEmpty();
	}

	public String getDeleteButtonMessage() {

		if (hasSelectedSysUsers()) {
			int size = this.selectedSysUserList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("sysUsersSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneSysUserSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public boolean doValidate() throws Exception {
		boolean result = true;

		if (sysUser.getUserName() == null || sysUser.getUserName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("userName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (sysUser.getPassword() == null || sysUser.getPassword().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("password") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}
		if (userAliasList == null || userAliasList.isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getErrorMessage().getString("youShouldAddOneUserAliasAtLeast"));
			result = false;
		}
		
		if(genericArchiveDocs==null || genericArchiveDocs.isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getErrorMessage().getString("youShouldAddOneSignture"));
			result = false;
		}
		return result;
	}

	public String save() {
		try {
			if (doValidate()) {
				if (sysUserForUserAliasStr != null) {
					sysUser.setPassword(sysUser.getPassword());
				} else {
					sysUser.setPassword(BCrypt.hashpw(sysUser.getPassword(), BCrypt.gensalt()));
				}
				
				sysUser.setSignturePhoto(genericArchiveDocs.get(0).getData());
				sysUserService.merge(sysUser);
				lazyDataModelSysUser = new LazyUserDataModel();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				return "pretty:users";
			}
		} catch (Exception e) {
			UtilityHelper.addInfoMessage(localizationService.getErrorMessage().getString("operationFaild"));
			return null;
		}
		return null;
	}

	public void deleteSelectedSysUsers() {
		try {
			sysUserService.delete(selectedSysUserList);
			this.selectedSysRoleList = null;
			lazyDataModelSysUser = new LazyUserDataModel();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysUsers");
			PrimeFaces.current().executeScript("PF('dtSysUsers').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysUsers");
			e.printStackTrace();
		}

	}

	/**
	 * Disable selected users: set disabled=true and expire passwordValidity immediately
	 */
	public void disableSelectedSysUsers() {
		try {
			if (selectedSysUserList == null || selectedSysUserList.isEmpty()) {
				return;
			}
			java.util.Date now = new java.util.Date();
			for (SysUser u : selectedSysUserList) {
				try {
					u.setDisabled(true);
					// set passwordValidity to now to make it expired immediately
					u.setPasswordValidity(new java.sql.Date(now.getTime()));
					sysUserService.merge(u);
				} catch (Exception ex) {
					// log and continue with others
					ex.printStackTrace();
				}
			}

			this.selectedSysRoleList = null;
			lazyDataModelSysUser = new LazyUserDataModel();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysUsers");
			PrimeFaces.current().executeScript("PF('dtSysUsers').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysUsers");
			e.printStackTrace();
		}

	}

	/**
	 * Toggle selected users: if enabled -> disable and expire password; if disabled -> enable and clear expiry
	 */
	public void toggleSelectedSysUsers() {
		try {
			if (selectedSysUserList == null || selectedSysUserList.isEmpty()) {
				return;
			}
			java.util.Date now = new java.util.Date();
			for (SysUser u : selectedSysUserList) {
				try {
					boolean isDisabled = u.getDisabled() != null && u.getDisabled();
					if (!isDisabled) {
						// disable user
						u.setDisabled(true);
						u.setPasswordValidity(new java.sql.Date(now.getTime()));
					} else {
						// enable user
						u.setDisabled(false);
						u.setPasswordValidity(null);
					}
					sysUserService.merge(u);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			this.selectedSysRoleList = null;
			lazyDataModelSysUser = new LazyUserDataModel();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysUsers");
			PrimeFaces.current().executeScript("PF('dtSysUsers').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysUsers");
			e.printStackTrace();
		}

	}

	public void deleteSysUser() {
		try {
			// toggle single user instead of physical delete: disable -> enable, enable -> disable
			if (sysUser != null) {
				java.util.Date now = new java.util.Date();
				boolean isDisabled = sysUser.getDisabled() != null && sysUser.getDisabled();
				if (!isDisabled) {
					sysUser.setDisabled(true);
					sysUser.setPasswordValidity(new java.sql.Date(now.getTime()));
				} else {
					sysUser.setDisabled(false);
					sysUser.setPasswordValidity(null);
				}
				sysUserService.merge(sysUser);
			}
			this.sysUser = new SysUser();
			lazyDataModelSysUser = new LazyUserDataModel();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysUsers");
			PrimeFaces.current().executeScript("PF('dtSysUsers').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dt-sysUsers");
			e.printStackTrace();
		}
	}

	public void addSysRolesToUser() throws Exception {
		List<SysUserRole> sysUserRoles = new ArrayList<SysUserRole>();
		for (SysRole sysRole : selectedSysRoleList) {
			SysUserRole userRole = new SysUserRole();
			userRole.setSysUser(sysUser);
			userRole.setSysRole(sysRole);
			sysUserRoles.add(userRole);
		}
		if (sysUserRoleService.saveAll(sysUserRoles) != null) {
			sysUserRoleList = sysUserRoleService.getRoleBySysUser(sysUser);
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:userRoles_dataTable");
			PrimeFaces.current().executeScript("PF('sysRoleAddDialog').hide()");
		}
	}

	public void deleteSysRolesFromUser() {
		try {
			if (sysUserRoleService.delete(sysUserRole)) {
				this.sysUserRole = new SysUserRole();
				sysUserRoleList = sysUserRoleService.getRoleBySysUser(sysUser);

				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:userRoles_dataTable");
			}
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:userRoles_dataTable");
			e.printStackTrace();
		}

	}

	public void defineSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
		this.sysUser.setPassword("");
	}

	public void resetPass() {
		try {
			if (sysUser.getPassword() == null || sysUser.getPassword().trim().isEmpty()) {
				UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("password") + "  "
						+ localizationService.getErrorMessage().getString("validateInput"));
				return;
			}
			sysUser.setPassword(BCrypt.hashpw(sysUser.getPassword(), BCrypt.gensalt()));
			sysUserService.merge(sysUser);
			lazyDataModelSysUser = new LazyUserDataModel();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));

		}
	}

	public void assignSysUser(SysUser sysUser) throws Exception {
		sysUserID = sysUser.getId();
		sysUserStr = UtilityHelper.cipher(sysUserID.toString());
	}

	public void assignSysUserForUserAlias(SysUser sysUser) throws Exception {
		sysUserID = sysUser.getId();
		sysUserForUserAliasStr = UtilityHelper.cipher(sysUserID.toString());
	}

	public void assignSysUserRole(SysUserRole sysUserRole) {
		this.sysUserRole = sysUserRole;
	}

	public void getRolesByUser(SysUser sysUser) throws Exception {
		sysUserID = sysUser.getId();
		sysUserForRoleStr = UtilityHelper.cipher(sysUserID.toString());
	}

	public void getSysRolesAvailable() throws Exception {
		sysRoleList = sysUserService.getExceptInUser(sysUser);
	}

	public void reset() {
		sysUser = new SysUser();
		userAliasList = new ArrayList<UserAlias>();
	}

	public void addUserAlias() throws Exception {
		userAliasList.add(userAlias);

		if (!sysUser.getUserAliasesForSysUser().contains(userAlias)) {
			userAlias.setSysUserBySysUser(sysUser);
			sysUser.getUserAliasesForSysUser().add(userAlias);
			userAlias = new UserAlias();

			if (userAliasList != null) {
				userAlias.setWeight((short) (userAliasList.size() + 1));
				PrimeFaces.current().ajax().update("form:inputText_weight");
			}
			if (jobPositionList != null && !jobPositionList.isEmpty())
				initialAuthorityLevel(jobPositionList.get(0));
		}
		UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-userAlias", "form:manage-userAlias-content");
	}

	public void removeUserAlias(UserAlias userAlias) throws Exception {
		userAliasList.remove(userAlias);
		Iterator<UserAlias> userAliasIterator = sysUser.getUserAliasesForSysUser().iterator();
		while (userAliasIterator.hasNext()) {
			UserAlias userAliasIter = userAliasIterator.next();
			if (userAlias.getUserCode().equals(userAliasIter.getUserCode())) {
				sysUser.getUserAliasesForSysUser().remove(userAliasIter);
				break;
			}
		}
		UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-userAlias");

	}

	public void assignUserAlias(UserAlias userAlias) {
		this.userAlias = userAlias;
	}

	public void openNewUserAlias() throws Exception {
		userAlias = new UserAlias();

		if (userAliasList != null) {
			userAlias.setWeight((short) (userAliasList.size() + 1));
			PrimeFaces.current().ajax().update("form:inputText_weight");
		}
		if (jobPositionList != null && !jobPositionList.isEmpty())
			initialAuthorityLevel(jobPositionList.get(0));
		
		userAlias.setArAliasName("Oficcer");
		userAlias.setEnAliasName("Officer");
	}

	public void updateExistingUserAlias(UserAlias userAlias) {
		userAliasList.remove(userAlias);
		sysUser.getUserAliasesForSysUser().remove(userAlias);
		this.userAlias = userAlias;
		initialAuthorityLevel(userAlias.getJobPosition());
	}

	public void initialAuthorityLevel(JobPosition jobPosition) {
		if (jobPosition != null)
			this.userAlias.setJobPosition(jobPosition);
		if (this.userAlias != null && this.userAlias.getJobPosition() != null) {
			this.userAlias.setDefinedLevel(this.userAlias.getJobPosition().getDefinedLevel());
			this.userAlias.setLowSendLevel(this.userAlias.getJobPosition().getLowSendLevel());
			this.userAlias.setHighSendLevel(this.userAlias.getJobPosition().getHighSendLevel());
			PrimeFaces.current().ajax().update("form:inputText_definedLevel", "form:inputText_lowSendLevel",
					"form:inputText_highSendLevel");
		}
	}

	public void defineAuthorityLevel() {

		if (this.userAlias != null && this.userAlias.getJobPosition() != null) {
			this.userAlias.setDefinedLevel(this.userAlias.getJobPosition().getDefinedLevel());
			this.userAlias.setLowSendLevel(this.userAlias.getJobPosition().getLowSendLevel());
			this.userAlias.setHighSendLevel(this.userAlias.getJobPosition().getHighSendLevel());
			PrimeFaces.current().ajax().update("form:inputText_definedLevel", "form:inputText_lowSendLevel",
					"form:inputText_highSendLevel");
		}
	}

	public String defineUserStyle() {
		if (sysUserStr != null)
			return "background-color:#F5F5F5;border-bottom-color:black;color:black;";
		else if (sysUserForUserAliasStr != null)
			return "background-color:#FFF0F5;border-bottom-color:black;color:black;";
		else
			return "";
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
			GenericArchiveDoc genericArchiveDoc = new GenericArchiveDoc();

			genericArchiveDoc.setName(fileName);
			genericArchiveDoc.setExtension(FilenameUtils.getExtension(event.getFile().getFileName()));
			genericArchiveDoc.setFileSize(event.getFile().getSize());
			genericArchiveDoc.setMimeType(event.getFile().getContentType());
			genericArchiveDoc.setData(event.getFile().getContent());

			if (genericArchiveDocs.size() == 1) {
				UtilityHelper.addWarnMessage(
						localizationService.getInterfaceLabel().getString("youCannotUploadMoreThanOneFile"));
				return;
			}

			if (!foundInUploader(genericArchiveDoc))
				genericArchiveDocs.add(genericArchiveDoc);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	public boolean foundInUploader(GenericArchiveDoc genericArchiveDoc) throws UnsupportedEncodingException {
		boolean result = false;
		for (GenericArchiveDoc archiveDoc : genericArchiveDocs) {
			if (archiveDoc.getName().trim().equalsIgnoreCase(genericArchiveDoc.getName().trim())
					&& archiveDoc.getExtension().trim()
							.equalsIgnoreCase(genericArchiveDoc.getExtension().trim())
					&& archiveDoc.getFileSize().equals(genericArchiveDoc.getFileSize())) {
				UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("theFile") + " ( "
						+ archiveDoc.getName().trim() + " ) "
						+ localizationService.getInterfaceLabel().getString("hadNotBeenAdded"));
				PrimeFaces.current().ajax().update("form:messages");
				result = true;
			}
		}
		return result;
	}

	public void deleteDocFile(GenericArchiveDoc genericArchiveDoc) {
		genericArchiveDocs.remove(genericArchiveDoc);
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
		case "dotx":
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

	

	// #region "Organization"

	public void selectSysUserOrganization(Organization organization) throws Exception {
		Integer maxUserCodeLength = sysUserService.getMaxUserCodeByOrg(organization);
		if (maxUserCodeLength != null && organization != null) {
			this.sysUser.setSysUserCode(organization.getPrefixCode() + "-" + organization.getCode() + "-"
					+ String.format("%0" + userCodeLength + "d", maxUserCodeLength + 1));
		}
		this.sysUser.setOrganizationByOrganization(organization);
		if (sysUserStr == null) {
			Organization rootOrganization = getRootOrganization(organization);
			this.sysUser.setOrganizationByRootOrganization(rootOrganization);
			Integer maxUserNameCodeLength = sysUserService.getMaxUserNameCodeByRootOrg(rootOrganization);
			if (maxUserNameCodeLength != null && rootOrganization != null) {
				this.sysUser.setUserName(rootOrganization.getPrefixCode() + "."
						+ String.format("%0" + userNameCodeLength + "d", maxUserNameCodeLength + 1));
			}
		}
	}

	public void selectOrganization(Organization organization) throws Exception {
		UserAlias lastSameUserAliasOrg = null;
		if (userAliasList != null && !userAliasList.isEmpty()) {
			for (UserAlias userAliasOrg : userAliasList) {
				if (userAliasOrg.getOrganizationByOrganization().equals(organization))
					lastSameUserAliasOrg = userAliasOrg;
			}
			if (lastSameUserAliasOrg != null) {
				Integer maxUserAliasCodeLength = Integer.parseInt(lastSameUserAliasOrg.getUserCode().split("-")[2]);
				this.userAlias.setUserCode(organization.getPrefixCode() + "-" + organization.getCode() + "-"
						+ String.format("%0" + userAliasCodeLength + "d", maxUserAliasCodeLength + 1));
			} else {
				Integer maxUserAliasCodeLength = userAliasService.getMaxUserAliasCodeByOrg(organization);
				if (maxUserAliasCodeLength != null && organization != null) {
					this.userAlias.setUserCode(organization.getPrefixCode() + "-" + organization.getCode() + "-"
							+ String.format("%0" + userAliasCodeLength + "d", maxUserAliasCodeLength + 1));
				}
			}

		} else {
			Integer maxUserAliasCodeLength = userAliasService.getMaxUserAliasCodeByOrg(organization);
			if (maxUserAliasCodeLength != null && organization != null) {
				this.userAlias.setUserCode(organization.getPrefixCode() + "-" + organization.getCode() + "-"
						+ String.format("%0" + userAliasCodeLength + "d", maxUserAliasCodeLength + 1));
			}
		}
		this.userAlias.setOrganizationByOrganization(organization);
		this.userAlias.setOrganizationByRootOrganization(getRootOrganization(organization));
		this.userAlias.setOrganizationByDivan(getOrganizationDivan(organization));
	}

	public void resetOrganization() {
		this.userAlias.setOrganizationByOrganization(null);
	}

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

	// #endregion

}
