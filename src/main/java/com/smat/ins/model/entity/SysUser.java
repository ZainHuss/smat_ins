package com.smat.ins.model.entity;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;


import com.smat.ins.util.UtilityHelper;

/**
 * SysUser entity. @author MyEclipse Persistence Tools
 */

@Audited
public class SysUser implements java.io.Serializable {

	// Fields

	private Long id;
    private Organization organizationByRootOrganization;
    private Organization organizationByOrganization;
    private String firstName;
    private String lastName;
    private String motherName;
    private String fatherName;
    private String arDisplayName;
    private String enDisplayName;
    private String userName;
    private String password;
    private Boolean passMustChangeFirstTime;
    private Date passwordValidity;
    private Date passFirstChangeDate;
    private Boolean disabled;
    private Date disabledUntilDate;
    private String sysUserCode;
    private Boolean isSuperAdmin;
    private Boolean isAdmin;
    private String signture;
    private byte[] signturePhoto;
    private Set cabinetFolderDocuments = new HashSet(0);
    private Set equipmentInspectionCertificatesForCreatedBy = new HashSet(0);
    private Set formTemplates = new HashSet(0);
    private Set correspondencesForCreatorSysUser = new HashSet(0);
    private Set archiveDocumentsForLockedSysUser = new HashSet(0);
    private Set archiveDocumentsForCreatorUser = new HashSet(0);
    private Set workingGroups = new HashSet(0);
    private Set correspondencesForDeletedBySysUser = new HashSet(0);
    private Set archiveDocumentsForDeletedBySysUser = new HashSet(0);
    private Set sysUserSettings = new HashSet(0);
    private Set groupMembers = new HashSet(0);
    private Set cabinetDefinitions = new HashSet(0);
    private Set inspectionFormWorkflowSteps = new HashSet(0);
    private Set cabinetFolders = new HashSet(0);
    private Set equipmentInspectionFormsForReviewedBy = new HashSet(0);
    private Set cabinets = new HashSet(0);
    private Set empCertificationsForInspectedBy = new HashSet(0);
    private Set stickersForCreatedBy = new HashSet(0);
    private Set archiveAccessManagements = new HashSet(0);
    private Set stickersForDeletedBy = new HashSet(0);
    private Set empCertificationsForIssuedBy = new HashSet(0);
    private Set inspectionFormWorkflows = new HashSet(0);
    private Set stickersForPrintedBy = new HashSet(0);
    private Set archiveSharings = new HashSet(0);
    private Set tasks = new HashSet(0);
    private Set empCertificationWorkflows = new HashSet(0);
    private Set empCertificationsForReviewedBy = new HashSet(0);
    private Set equipmentInspectionFormsForInspectionBy = new HashSet(0);
    private Set userAliasesForSysUser = new HashSet(0);
    private Set stickersForForUser = new HashSet(0);
    private Set empCertificationWorkflowSteps = new HashSet(0);
    private Set cabinetLocations = new HashSet(0);
    private Set userAliasesForCreatorUser = new HashSet(0);
    private Set sysUserRoles = new HashSet(0);
    private Set userAliasesForFrozenUser = new HashSet(0);
    private Set equipmentInspectionCertificatesForAllowReprintBy = new HashSet(0);
    private Set equipmentInspectionCertificatesForReprintBy = new HashSet(0);

	private String fullName;
	private Boolean isEditMode;
	private Boolean isAlterUserAliasMode;

	 // Constructors

    /** default constructor */
    public SysUser() {
    }

	/** minimal constructor */
    public SysUser(Organization organizationByRootOrganization, Organization organizationByOrganization, String sysUserCode) {
        this.organizationByRootOrganization = organizationByRootOrganization;
        this.organizationByOrganization = organizationByOrganization;
        this.sysUserCode = sysUserCode;
    }
    
    /** full constructor */
    public SysUser(Organization organizationByRootOrganization, Organization organizationByOrganization, String firstName, String lastName, String motherName, String fatherName, String arDisplayName, String enDisplayName, String userName, String password, Boolean passMustChangeFirstTime, Date passwordValidity, Date passFirstChangeDate, Boolean disabled, Date disabledUntilDate, String sysUserCode, Boolean isSuperAdmin, Boolean isAdmin, String signture, byte[] signturePhoto, Set cabinetFolderDocuments, Set equipmentInspectionCertificatesForCreatedBy, Set formTemplates, Set correspondencesForCreatorSysUser, Set archiveDocumentsForLockedSysUser, Set archiveDocumentsForCreatorUser, Set workingGroups, Set correspondencesForDeletedBySysUser, Set archiveDocumentsForDeletedBySysUser, Set sysUserSettings, Set groupMembers, Set cabinetDefinitions, Set inspectionFormWorkflowSteps, Set cabinetFolders, Set equipmentInspectionFormsForReviewedBy, Set cabinets, Set empCertificationsForInspectedBy, Set stickersForCreatedBy, Set archiveAccessManagements, Set stickersForDeletedBy, Set empCertificationsForIssuedBy, Set inspectionFormWorkflows, Set stickersForPrintedBy, Set archiveSharings, Set tasks, Set empCertificationWorkflows, Set empCertificationsForReviewedBy, Set equipmentInspectionFormsForInspectionBy, Set userAliasesForSysUser, Set stickersForForUser, Set empCertificationWorkflowSteps, Set cabinetLocations, Set userAliasesForCreatorUser, Set sysUserRoles, Set userAliasesForFrozenUser, Set equipmentInspectionCertificatesForAllowReprintBy, Set equipmentInspectionCertificatesForReprintBy) {
        this.organizationByRootOrganization = organizationByRootOrganization;
        this.organizationByOrganization = organizationByOrganization;
        this.firstName = firstName;
        this.lastName = lastName;
        this.motherName = motherName;
        this.fatherName = fatherName;
        this.arDisplayName = arDisplayName;
        this.enDisplayName = enDisplayName;
        this.userName = userName;
        this.password = password;
        this.passMustChangeFirstTime = passMustChangeFirstTime;
        this.passwordValidity = passwordValidity;
        this.passFirstChangeDate = passFirstChangeDate;
        this.disabled = disabled;
        this.disabledUntilDate = disabledUntilDate;
        this.sysUserCode = sysUserCode;
        this.isSuperAdmin = isSuperAdmin;
        this.isAdmin = isAdmin;
        this.signture = signture;
        this.signturePhoto = signturePhoto;
        this.cabinetFolderDocuments = cabinetFolderDocuments;
        this.equipmentInspectionCertificatesForCreatedBy = equipmentInspectionCertificatesForCreatedBy;
        this.formTemplates = formTemplates;
        this.correspondencesForCreatorSysUser = correspondencesForCreatorSysUser;
        this.archiveDocumentsForLockedSysUser = archiveDocumentsForLockedSysUser;
        this.archiveDocumentsForCreatorUser = archiveDocumentsForCreatorUser;
        this.workingGroups = workingGroups;
        this.correspondencesForDeletedBySysUser = correspondencesForDeletedBySysUser;
        this.archiveDocumentsForDeletedBySysUser = archiveDocumentsForDeletedBySysUser;
        this.sysUserSettings = sysUserSettings;
        this.groupMembers = groupMembers;
        this.cabinetDefinitions = cabinetDefinitions;
        this.inspectionFormWorkflowSteps = inspectionFormWorkflowSteps;
        this.cabinetFolders = cabinetFolders;
        this.equipmentInspectionFormsForReviewedBy = equipmentInspectionFormsForReviewedBy;
        this.cabinets = cabinets;
        this.empCertificationsForInspectedBy = empCertificationsForInspectedBy;
        this.stickersForCreatedBy = stickersForCreatedBy;
        this.archiveAccessManagements = archiveAccessManagements;
        this.stickersForDeletedBy = stickersForDeletedBy;
        this.empCertificationsForIssuedBy = empCertificationsForIssuedBy;
        this.inspectionFormWorkflows = inspectionFormWorkflows;
        this.stickersForPrintedBy = stickersForPrintedBy;
        this.archiveSharings = archiveSharings;
        this.tasks = tasks;
        this.empCertificationWorkflows = empCertificationWorkflows;
        this.empCertificationsForReviewedBy = empCertificationsForReviewedBy;
        this.equipmentInspectionFormsForInspectionBy = equipmentInspectionFormsForInspectionBy;
        this.userAliasesForSysUser = userAliasesForSysUser;
        this.stickersForForUser = stickersForForUser;
        this.empCertificationWorkflowSteps = empCertificationWorkflowSteps;
        this.cabinetLocations = cabinetLocations;
        this.userAliasesForCreatorUser = userAliasesForCreatorUser;
        this.sysUserRoles = sysUserRoles;
        this.userAliasesForFrozenUser = userAliasesForFrozenUser;
        this.equipmentInspectionCertificatesForAllowReprintBy = equipmentInspectionCertificatesForAllowReprintBy;
        this.equipmentInspectionCertificatesForReprintBy = equipmentInspectionCertificatesForReprintBy;
    }

   
    // Property accessors

    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Organization getOrganizationByRootOrganization() {
        return this.organizationByRootOrganization;
    }
    
    public void setOrganizationByRootOrganization(Organization organizationByRootOrganization) {
        this.organizationByRootOrganization = organizationByRootOrganization;
    }

    public Organization getOrganizationByOrganization() {
        return this.organizationByOrganization;
    }
    
    public void setOrganizationByOrganization(Organization organizationByOrganization) {
        this.organizationByOrganization = organizationByOrganization;
    }

    public String getFirstName() {
        return this.firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMotherName() {
        return this.motherName;
    }
    
    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getFatherName() {
        return this.fatherName;
    }
    
    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getArDisplayName() {
        return this.arDisplayName;
    }
    
    public void setArDisplayName(String arDisplayName) {
        this.arDisplayName = arDisplayName;
    }

    public String getEnDisplayName() {
        return this.enDisplayName;
    }
    
    public void setEnDisplayName(String enDisplayName) {
        this.enDisplayName = enDisplayName;
    }

    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getPassMustChangeFirstTime() {
        return this.passMustChangeFirstTime;
    }
    
    public void setPassMustChangeFirstTime(Boolean passMustChangeFirstTime) {
        this.passMustChangeFirstTime = passMustChangeFirstTime;
    }

    public Date getPasswordValidity() {
        return this.passwordValidity;
    }
    
    public void setPasswordValidity(Date passwordValidity) {
        this.passwordValidity = passwordValidity;
    }

    public Date getPassFirstChangeDate() {
        return this.passFirstChangeDate;
    }
    
    public void setPassFirstChangeDate(Date passFirstChangeDate) {
        this.passFirstChangeDate = passFirstChangeDate;
    }

    public Boolean getDisabled() {
        return this.disabled;
    }
    
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Date getDisabledUntilDate() {
        return this.disabledUntilDate;
    }
    
    public void setDisabledUntilDate(Date disabledUntilDate) {
        this.disabledUntilDate = disabledUntilDate;
    }

    public String getSysUserCode() {
        return this.sysUserCode;
    }
    
    public void setSysUserCode(String sysUserCode) {
        this.sysUserCode = sysUserCode;
    }

    public Boolean getIsSuperAdmin() {
        return this.isSuperAdmin;
    }
    
    public void setIsSuperAdmin(Boolean isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public Boolean getIsAdmin() {
        return this.isAdmin;
    }
    
    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getSignture() {
        return this.signture;
    }
    
    public void setSignture(String signture) {
        this.signture = signture;
    }

    public byte[] getSignturePhoto() {
        return this.signturePhoto;
    }
    
    public void setSignturePhoto(byte[] signturePhoto) {
        this.signturePhoto = signturePhoto;
    }

    public Set getCabinetFolderDocuments() {
        return this.cabinetFolderDocuments;
    }
    
    public void setCabinetFolderDocuments(Set cabinetFolderDocuments) {
        this.cabinetFolderDocuments = cabinetFolderDocuments;
    }

    public Set getEquipmentInspectionCertificatesForCreatedBy() {
        return this.equipmentInspectionCertificatesForCreatedBy;
    }
    
    public void setEquipmentInspectionCertificatesForCreatedBy(Set equipmentInspectionCertificatesForCreatedBy) {
        this.equipmentInspectionCertificatesForCreatedBy = equipmentInspectionCertificatesForCreatedBy;
    }

    public Set getFormTemplates() {
        return this.formTemplates;
    }
    
    public void setFormTemplates(Set formTemplates) {
        this.formTemplates = formTemplates;
    }

    public Set getCorrespondencesForCreatorSysUser() {
        return this.correspondencesForCreatorSysUser;
    }
    
    public void setCorrespondencesForCreatorSysUser(Set correspondencesForCreatorSysUser) {
        this.correspondencesForCreatorSysUser = correspondencesForCreatorSysUser;
    }

    public Set getArchiveDocumentsForLockedSysUser() {
        return this.archiveDocumentsForLockedSysUser;
    }
    
    public void setArchiveDocumentsForLockedSysUser(Set archiveDocumentsForLockedSysUser) {
        this.archiveDocumentsForLockedSysUser = archiveDocumentsForLockedSysUser;
    }

    public Set getArchiveDocumentsForCreatorUser() {
        return this.archiveDocumentsForCreatorUser;
    }
    
    public void setArchiveDocumentsForCreatorUser(Set archiveDocumentsForCreatorUser) {
        this.archiveDocumentsForCreatorUser = archiveDocumentsForCreatorUser;
    }

    public Set getWorkingGroups() {
        return this.workingGroups;
    }
    
    public void setWorkingGroups(Set workingGroups) {
        this.workingGroups = workingGroups;
    }

    public Set getCorrespondencesForDeletedBySysUser() {
        return this.correspondencesForDeletedBySysUser;
    }
    
    public void setCorrespondencesForDeletedBySysUser(Set correspondencesForDeletedBySysUser) {
        this.correspondencesForDeletedBySysUser = correspondencesForDeletedBySysUser;
    }

    public Set getArchiveDocumentsForDeletedBySysUser() {
        return this.archiveDocumentsForDeletedBySysUser;
    }
    
    public void setArchiveDocumentsForDeletedBySysUser(Set archiveDocumentsForDeletedBySysUser) {
        this.archiveDocumentsForDeletedBySysUser = archiveDocumentsForDeletedBySysUser;
    }

    public Set getSysUserSettings() {
        return this.sysUserSettings;
    }
    
    public void setSysUserSettings(Set sysUserSettings) {
        this.sysUserSettings = sysUserSettings;
    }

    public Set getGroupMembers() {
        return this.groupMembers;
    }
    
    public void setGroupMembers(Set groupMembers) {
        this.groupMembers = groupMembers;
    }

    public Set getCabinetDefinitions() {
        return this.cabinetDefinitions;
    }
    
    public void setCabinetDefinitions(Set cabinetDefinitions) {
        this.cabinetDefinitions = cabinetDefinitions;
    }

    public Set getInspectionFormWorkflowSteps() {
        return this.inspectionFormWorkflowSteps;
    }
    
    public void setInspectionFormWorkflowSteps(Set inspectionFormWorkflowSteps) {
        this.inspectionFormWorkflowSteps = inspectionFormWorkflowSteps;
    }

    public Set getCabinetFolders() {
        return this.cabinetFolders;
    }
    
    public void setCabinetFolders(Set cabinetFolders) {
        this.cabinetFolders = cabinetFolders;
    }

    public Set getEquipmentInspectionFormsForReviewedBy() {
        return this.equipmentInspectionFormsForReviewedBy;
    }
    
    public void setEquipmentInspectionFormsForReviewedBy(Set equipmentInspectionFormsForReviewedBy) {
        this.equipmentInspectionFormsForReviewedBy = equipmentInspectionFormsForReviewedBy;
    }

    public Set getCabinets() {
        return this.cabinets;
    }
    
    public void setCabinets(Set cabinets) {
        this.cabinets = cabinets;
    }

    public Set getEmpCertificationsForInspectedBy() {
        return this.empCertificationsForInspectedBy;
    }
    
    public void setEmpCertificationsForInspectedBy(Set empCertificationsForInspectedBy) {
        this.empCertificationsForInspectedBy = empCertificationsForInspectedBy;
    }

    public Set getStickersForCreatedBy() {
        return this.stickersForCreatedBy;
    }
    
    public void setStickersForCreatedBy(Set stickersForCreatedBy) {
        this.stickersForCreatedBy = stickersForCreatedBy;
    }

    public Set getArchiveAccessManagements() {
        return this.archiveAccessManagements;
    }
    
    public void setArchiveAccessManagements(Set archiveAccessManagements) {
        this.archiveAccessManagements = archiveAccessManagements;
    }

    public Set getStickersForDeletedBy() {
        return this.stickersForDeletedBy;
    }
    
    public void setStickersForDeletedBy(Set stickersForDeletedBy) {
        this.stickersForDeletedBy = stickersForDeletedBy;
    }

    public Set getEmpCertificationsForIssuedBy() {
        return this.empCertificationsForIssuedBy;
    }
    
    public void setEmpCertificationsForIssuedBy(Set empCertificationsForIssuedBy) {
        this.empCertificationsForIssuedBy = empCertificationsForIssuedBy;
    }

    public Set getInspectionFormWorkflows() {
        return this.inspectionFormWorkflows;
    }
    
    public void setInspectionFormWorkflows(Set inspectionFormWorkflows) {
        this.inspectionFormWorkflows = inspectionFormWorkflows;
    }

    public Set getStickersForPrintedBy() {
        return this.stickersForPrintedBy;
    }
    
    public void setStickersForPrintedBy(Set stickersForPrintedBy) {
        this.stickersForPrintedBy = stickersForPrintedBy;
    }

    public Set getArchiveSharings() {
        return this.archiveSharings;
    }
    
    public void setArchiveSharings(Set archiveSharings) {
        this.archiveSharings = archiveSharings;
    }

    public Set getTasks() {
        return this.tasks;
    }
    
    public void setTasks(Set tasks) {
        this.tasks = tasks;
    }

    public Set getEmpCertificationWorkflows() {
        return this.empCertificationWorkflows;
    }
    
    public void setEmpCertificationWorkflows(Set empCertificationWorkflows) {
        this.empCertificationWorkflows = empCertificationWorkflows;
    }

    public Set getEmpCertificationsForReviewedBy() {
        return this.empCertificationsForReviewedBy;
    }
    
    public void setEmpCertificationsForReviewedBy(Set empCertificationsForReviewedBy) {
        this.empCertificationsForReviewedBy = empCertificationsForReviewedBy;
    }

    public Set getEquipmentInspectionFormsForInspectionBy() {
        return this.equipmentInspectionFormsForInspectionBy;
    }
    
    public void setEquipmentInspectionFormsForInspectionBy(Set equipmentInspectionFormsForInspectionBy) {
        this.equipmentInspectionFormsForInspectionBy = equipmentInspectionFormsForInspectionBy;
    }

    public Set getUserAliasesForSysUser() {
        return this.userAliasesForSysUser;
    }
    
    public void setUserAliasesForSysUser(Set userAliasesForSysUser) {
        this.userAliasesForSysUser = userAliasesForSysUser;
    }

    public Set getStickersForForUser() {
        return this.stickersForForUser;
    }
    
    public void setStickersForForUser(Set stickersForForUser) {
        this.stickersForForUser = stickersForForUser;
    }

    public Set getEmpCertificationWorkflowSteps() {
        return this.empCertificationWorkflowSteps;
    }
    
    public void setEmpCertificationWorkflowSteps(Set empCertificationWorkflowSteps) {
        this.empCertificationWorkflowSteps = empCertificationWorkflowSteps;
    }

    public Set getCabinetLocations() {
        return this.cabinetLocations;
    }
    
    public void setCabinetLocations(Set cabinetLocations) {
        this.cabinetLocations = cabinetLocations;
    }

    public Set getUserAliasesForCreatorUser() {
        return this.userAliasesForCreatorUser;
    }
    
    public void setUserAliasesForCreatorUser(Set userAliasesForCreatorUser) {
        this.userAliasesForCreatorUser = userAliasesForCreatorUser;
    }

    public Set getSysUserRoles() {
        return this.sysUserRoles;
    }
    
    public void setSysUserRoles(Set sysUserRoles) {
        this.sysUserRoles = sysUserRoles;
    }

    public Set getUserAliasesForFrozenUser() {
        return this.userAliasesForFrozenUser;
    }
    
    public void setUserAliasesForFrozenUser(Set userAliasesForFrozenUser) {
        this.userAliasesForFrozenUser = userAliasesForFrozenUser;
    }

    public Set getEquipmentInspectionCertificatesForAllowReprintBy() {
        return this.equipmentInspectionCertificatesForAllowReprintBy;
    }
    
    public void setEquipmentInspectionCertificatesForAllowReprintBy(Set equipmentInspectionCertificatesForAllowReprintBy) {
        this.equipmentInspectionCertificatesForAllowReprintBy = equipmentInspectionCertificatesForAllowReprintBy;
    }

    public Set getEquipmentInspectionCertificatesForReprintBy() {
        return this.equipmentInspectionCertificatesForReprintBy;
    }
    
    public void setEquipmentInspectionCertificatesForReprintBy(Set equipmentInspectionCertificatesForReprintBy) {
        this.equipmentInspectionCertificatesForReprintBy = equipmentInspectionCertificatesForReprintBy;
    }

	public String getFullName() {
		if (firstName != null && !firstName.trim().isEmpty() && lastName != null && !lastName.trim().isEmpty()) {
			if (fatherName != null && !fatherName.trim().isEmpty())
				return firstName.trim() + " " + fatherName.trim() + " " + lastName.trim();
			else
				return firstName.trim() + " " + lastName.trim();
		} else
			return this.fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDisplayName() {
		if (UtilityHelper.getLocale().getLanguage().contains("en"))
			return getEnDisplayName();
		else
			return getArDisplayName();
	}

	public Boolean getIsEditMode() {
		return isEditMode;
	}

	public void setIsEditMode(Boolean isEditMode) {
		this.isEditMode = isEditMode;
	}

	public Boolean getIsAlterUserAliasMode() {
		return isAlterUserAliasMode;
	}

	public void setIsAlterUserAliasMode(Boolean isAlterUserAliasMode) {
		this.isAlterUserAliasMode = isAlterUserAliasMode;
	}

	@Override
	public boolean equals(Object other) {

		return (other instanceof SysUser) && (id != null) ? id.equals(((SysUser) other).id) : (other == this);
	}

	@Override
	public int hashCode() {
		return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
	
	

}