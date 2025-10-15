package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * UserAlias entity. @author MyEclipse Persistence Tools
 */
@Audited
public class UserAlias implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 5356164271045491988L;
	private Long id;
	private Organization organizationByRootOrganization;
	private Organization organizationByDivan;
	private Organization organizationByOrganization;
	private SysUser sysUserByFrozenUser;
	private JobPosition jobPosition;
	private SysUser sysUserBySysUser;
	private SysUser sysUserByCreatorUser;
	private String arAliasName;
	private String enAliasName;
	private String userCode;
	private Boolean isDefault;
	private Short definedLevel;
	private Short lowSendLevel;
	private Short highSendLevel;
	private Short weight;
	private Boolean isFrozen;
	private Date frozenDate;
	private Date createdDate;
	private Boolean hierarchyClosure;
	private Set archiveDocuments = new HashSet(0);
	private Set correspondenceRecipients = new HashSet(0);
	private Set accessByUserAliases = new HashSet(0);
	private Set correspondences = new HashSet(0);
	private Set correspondenceTransmissionsForToAlias = new HashSet(0);
	private Set tasksForAssignTo = new HashSet(0);
	private Set tasksForAssigner = new HashSet(0);
	private Set folderSharings = new HashSet(0);
	private Set cabinets = new HashSet(0);
	private Set groupMembers = new HashSet(0);
	private Set correspondenceNotes = new HashSet(0);
	private Set documentSharings = new HashSet(0);
	private Set correspondenceTransmissionsForFromAlias = new HashSet(0);

	// Constructors

	/** default constructor */
	public UserAlias() {
	}

	/** full constructor */
	public UserAlias(Organization organizationByRootOrganization, Organization organizationByDivan,
			Organization organizationByOrganization, SysUser sysUserByFrozenUser, JobPosition jobPosition,
			SysUser sysUserBySysUser, SysUser sysUserByCreatorUser, String arAliasName, String enAliasName,
			String userCode, Boolean isDefault, Short definedLevel, Short lowSendLevel, Short highSendLevel,
			Short weight, Boolean isFrozen, Date frozenDate, Date createdDate, Boolean hierarchyClosure,
			Set archiveDocuments, Set correspondenceRecipients, Set accessByUserAliases, Set correspondences,
			Set correspondenceTransmissionsForToAlias, Set tasksForAssignTo, Set tasksForAssigner, Set folderSharings,
			Set cabinets, Set groupMembers, Set correspondenceNotes, Set documentSharings,
			Set correspondenceTransmissionsForFromAlias) {
		this.organizationByRootOrganization = organizationByRootOrganization;
		this.organizationByDivan = organizationByDivan;
		this.organizationByOrganization = organizationByOrganization;
		this.sysUserByFrozenUser = sysUserByFrozenUser;
		this.jobPosition = jobPosition;
		this.sysUserBySysUser = sysUserBySysUser;
		this.sysUserByCreatorUser = sysUserByCreatorUser;
		this.arAliasName = arAliasName;
		this.enAliasName = enAliasName;
		this.userCode = userCode;
		this.isDefault = isDefault;
		this.definedLevel = definedLevel;
		this.lowSendLevel = lowSendLevel;
		this.highSendLevel = highSendLevel;
		this.weight = weight;
		this.isFrozen = isFrozen;
		this.frozenDate = frozenDate;
		this.createdDate = createdDate;
		this.hierarchyClosure = hierarchyClosure;
		this.archiveDocuments = archiveDocuments;
		this.correspondenceRecipients = correspondenceRecipients;
		this.accessByUserAliases = accessByUserAliases;
		this.correspondences = correspondences;
		this.correspondenceTransmissionsForToAlias = correspondenceTransmissionsForToAlias;
		this.tasksForAssignTo = tasksForAssignTo;
		this.tasksForAssigner = tasksForAssigner;
		this.folderSharings = folderSharings;
		this.cabinets = cabinets;
		this.groupMembers = groupMembers;
		this.correspondenceNotes = correspondenceNotes;
		this.documentSharings = documentSharings;
		this.correspondenceTransmissionsForFromAlias = correspondenceTransmissionsForFromAlias;
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

	public Organization getOrganizationByDivan() {
		return this.organizationByDivan;
	}

	public void setOrganizationByDivan(Organization organizationByDivan) {
		this.organizationByDivan = organizationByDivan;
	}

	public Organization getOrganizationByOrganization() {
		return this.organizationByOrganization;
	}

	public void setOrganizationByOrganization(Organization organizationByOrganization) {
		this.organizationByOrganization = organizationByOrganization;
	}

	public SysUser getSysUserByFrozenUser() {
		return this.sysUserByFrozenUser;
	}

	public void setSysUserByFrozenUser(SysUser sysUserByFrozenUser) {
		this.sysUserByFrozenUser = sysUserByFrozenUser;
	}

	public JobPosition getJobPosition() {
		return this.jobPosition;
	}

	public void setJobPosition(JobPosition jobPosition) {
		this.jobPosition = jobPosition;
	}

	public SysUser getSysUserBySysUser() {
		return this.sysUserBySysUser;
	}

	public void setSysUserBySysUser(SysUser sysUserBySysUser) {
		this.sysUserBySysUser = sysUserBySysUser;
	}

	public SysUser getSysUserByCreatorUser() {
		return this.sysUserByCreatorUser;
	}

	public void setSysUserByCreatorUser(SysUser sysUserByCreatorUser) {
		this.sysUserByCreatorUser = sysUserByCreatorUser;
	}

	public String getArAliasName() {
		return this.arAliasName;
	}

	public void setArAliasName(String arAliasName) {
		this.arAliasName = arAliasName;
	}

	public String getEnAliasName() {
		return this.enAliasName;
	}

	public void setEnAliasName(String enAliasName) {
		this.enAliasName = enAliasName;
	}

	public String getUserCode() {
		return this.userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public Boolean getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public Short getDefinedLevel() {
		return this.definedLevel;
	}

	public void setDefinedLevel(Short definedLevel) {
		this.definedLevel = definedLevel;
	}

	public Short getLowSendLevel() {
		return this.lowSendLevel;
	}

	public void setLowSendLevel(Short lowSendLevel) {
		this.lowSendLevel = lowSendLevel;
	}

	public Short getHighSendLevel() {
		return this.highSendLevel;
	}

	public void setHighSendLevel(Short highSendLevel) {
		this.highSendLevel = highSendLevel;
	}

	public Short getWeight() {
		return this.weight;
	}

	public void setWeight(Short weight) {
		this.weight = weight;
	}

	public Boolean getIsFrozen() {
		return this.isFrozen;
	}

	public void setIsFrozen(Boolean isFrozen) {
		this.isFrozen = isFrozen;
	}

	public Date getFrozenDate() {
		return this.frozenDate;
	}

	public void setFrozenDate(Date frozenDate) {
		this.frozenDate = frozenDate;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Boolean getHierarchyClosure() {
		return this.hierarchyClosure;
	}

	public void setHierarchyClosure(Boolean hierarchyClosure) {
		this.hierarchyClosure = hierarchyClosure;
	}

	public Set getArchiveDocuments() {
		return this.archiveDocuments;
	}

	public void setArchiveDocuments(Set archiveDocuments) {
		this.archiveDocuments = archiveDocuments;
	}

	public Set getCorrespondenceRecipients() {
		return this.correspondenceRecipients;
	}

	public void setCorrespondenceRecipients(Set correspondenceRecipients) {
		this.correspondenceRecipients = correspondenceRecipients;
	}

	public Set getAccessByUserAliases() {
		return this.accessByUserAliases;
	}

	public void setAccessByUserAliases(Set accessByUserAliases) {
		this.accessByUserAliases = accessByUserAliases;
	}

	public Set getCorrespondences() {
		return this.correspondences;
	}

	public void setCorrespondences(Set correspondences) {
		this.correspondences = correspondences;
	}

	public Set getCorrespondenceTransmissionsForToAlias() {
		return this.correspondenceTransmissionsForToAlias;
	}

	public void setCorrespondenceTransmissionsForToAlias(Set correspondenceTransmissionsForToAlias) {
		this.correspondenceTransmissionsForToAlias = correspondenceTransmissionsForToAlias;
	}

	public Set getTasksForAssignTo() {
		return this.tasksForAssignTo;
	}

	public void setTasksForAssignTo(Set tasksForAssignTo) {
		this.tasksForAssignTo = tasksForAssignTo;
	}

	public Set getTasksForAssigner() {
		return this.tasksForAssigner;
	}

	public void setTasksForAssigner(Set tasksForAssigner) {
		this.tasksForAssigner = tasksForAssigner;
	}

	public Set getFolderSharings() {
		return this.folderSharings;
	}

	public void setFolderSharings(Set folderSharings) {
		this.folderSharings = folderSharings;
	}

	public Set getCabinets() {
		return this.cabinets;
	}

	public void setCabinets(Set cabinets) {
		this.cabinets = cabinets;
	}

	public Set getGroupMembers() {
		return this.groupMembers;
	}

	public void setGroupMembers(Set groupMembers) {
		this.groupMembers = groupMembers;
	}

	public Set getCorrespondenceNotes() {
		return this.correspondenceNotes;
	}

	public void setCorrespondenceNotes(Set correspondenceNotes) {
		this.correspondenceNotes = correspondenceNotes;
	}

	public Set getDocumentSharings() {
		return this.documentSharings;
	}

	public void setDocumentSharings(Set documentSharings) {
		this.documentSharings = documentSharings;
	}

	public Set getCorrespondenceTransmissionsForFromAlias() {
		return this.correspondenceTransmissionsForFromAlias;
	}

	public void setCorrespondenceTransmissionsForFromAlias(Set correspondenceTransmissionsForFromAlias) {
		this.correspondenceTransmissionsForFromAlias = correspondenceTransmissionsForFromAlias;
	}
	
	@Override
	public boolean equals(Object other) {

		return (other instanceof UserAlias) && (id != null) ? id.equals(((UserAlias) other).id) : (other == this);
	}

	@Override
	public int hashCode() {
		return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
	}

	@Override
	public String toString() {
		return getAliasName();
	}

	public String getAliasName() {
		if (UtilityHelper.getLocale().getLanguage().contains("en"))
			return getEnAliasName();
		else
			return getArAliasName();
	}

}