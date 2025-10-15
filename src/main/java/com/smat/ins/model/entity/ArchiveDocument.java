package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * ArchiveDocument entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ArchiveDocument implements java.io.Serializable {

	// Fields

	private Long id;
	private ArchiveExtraProperty archiveExtraProperty;
	private SysUser sysUserByDeletedBySysUser;
	private Organization organizationByRootOrganization;
	private Organization organizationByDivan;
	private SysUser sysUserByLockedSysUser;
	private Organization organizationByOrganization;
	private ArchiveDocument archiveDocument;
	private UserAlias userAlias;
	private ArchiveDocumentType archiveDocumentType;
	private SysUser sysUserByCreatorUser;
	private String fromEntity;
	private Boolean isEncrypted;
	private Boolean isDeleted;
	private Long seq;
	private Short year;
	private String code;
	private String arabicName;
	private String englishName;
	private String subject;
	private String description;
	private String lang;
	private Boolean isDirectory;
	private Date createdDate;
	private Boolean isLocked;
	private Boolean inheritMailPermission;
	private Boolean inheritParentAttribute;
	private Boolean inheritAttributeToChild;
	private Set archiveDocumentFiles = new HashSet(0);
	private Set archiveDocuments = new HashSet(0);
	private Set accessByJobPositions = new HashSet(0);
	private Set correspondenceArchives = new HashSet(0);
	private Set accessByUserAliases = new HashSet(0);
	private Set documentSharings = new HashSet(0);
	private Set cabinetFolderDocuments = new HashSet(0);
	private Set archiveDocumentTags = new HashSet(0);
    private Boolean isEdit;

	// Constructors

	/** default constructor */
	public ArchiveDocument() {
	}

	/** minimal constructor */
	public ArchiveDocument(ArchiveDocumentType archiveDocumentType) {
		this.archiveDocumentType = archiveDocumentType;
	}

	/** full constructor */
	public ArchiveDocument(ArchiveExtraProperty archiveExtraProperty, Organization organizationByRootOrganization,
			Organization organizationByDivan, SysUser sysUserByLockedSysUser, Organization organizationByOrganization,
			ArchiveDocument archiveDocument, UserAlias userAlias, ArchiveDocumentType archiveDocumentType,
			SysUser sysUserByCreatorUser, String fromEntity, Boolean isEncrypted, Boolean isDeleted, Long seq,
			Short year, String code, String arabicName,String englishName, String subject, String description, String lang, Boolean isDirectory,
			Date createdDate, Boolean isLocked, Boolean inheritMailPermission, Boolean inheritParentAttribute,
			Boolean inheritAttributeToChild, Set archiveDocumentFiles, Set archiveDocuments, Set accessByJobPositions,
			Set correspondenceArchives, Set accessByUserAliases, Set documentSharings, Set cabinetFolderDocuments,
			Set archiveDocumentTags) {
		this.archiveExtraProperty = archiveExtraProperty;
		this.sysUserByDeletedBySysUser = sysUserByDeletedBySysUser;
		this.organizationByRootOrganization = organizationByRootOrganization;
		this.organizationByDivan = organizationByDivan;
		this.sysUserByLockedSysUser = sysUserByLockedSysUser;
		this.organizationByOrganization = organizationByOrganization;
		this.archiveDocument = archiveDocument;
		this.userAlias = userAlias;
		this.archiveDocumentType = archiveDocumentType;
		this.sysUserByCreatorUser = sysUserByCreatorUser;
		this.fromEntity = fromEntity;
		this.isEncrypted = isEncrypted;
		this.isDeleted = isDeleted;
		this.seq = seq;
		this.year = year;
		this.code = code;
		this.arabicName = arabicName;
		this.englishName=englishName;
		this.subject = subject;
		this.description = description;
		this.lang = lang;
		this.isDirectory = isDirectory;
		this.createdDate = createdDate;
		this.isLocked = isLocked;
		this.inheritMailPermission = inheritMailPermission;
		this.inheritParentAttribute = inheritParentAttribute;
		this.inheritAttributeToChild = inheritAttributeToChild;
		this.archiveDocumentFiles = archiveDocumentFiles;
		this.archiveDocuments = archiveDocuments;
		this.accessByJobPositions = accessByJobPositions;
		this.correspondenceArchives = correspondenceArchives;
		this.accessByUserAliases = accessByUserAliases;
		this.documentSharings = documentSharings;
		this.cabinetFolderDocuments = cabinetFolderDocuments;
		this.archiveDocumentTags = archiveDocumentTags;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ArchiveExtraProperty getArchiveExtraProperty() {
		return this.archiveExtraProperty;
	}

	public void setArchiveExtraProperty(ArchiveExtraProperty archiveExtraProperty) {
		this.archiveExtraProperty = archiveExtraProperty;
	}

	public SysUser getSysUserByDeletedBySysUser() {
		return this.sysUserByDeletedBySysUser;
	}

	public void setSysUserByDeletedBySysUser(SysUser sysUserByDeletedBySysUser) {
		this.sysUserByDeletedBySysUser = sysUserByDeletedBySysUser;
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

	public SysUser getSysUserByLockedSysUser() {
		return this.sysUserByLockedSysUser;
	}

	public void setSysUserByLockedSysUser(SysUser sysUserByLockedSysUser) {
		this.sysUserByLockedSysUser = sysUserByLockedSysUser;
	}

	public Organization getOrganizationByOrganization() {
		return this.organizationByOrganization;
	}

	public void setOrganizationByOrganization(Organization organizationByOrganization) {
		this.organizationByOrganization = organizationByOrganization;
	}

	public ArchiveDocument getArchiveDocument() {
		return this.archiveDocument;
	}

	public void setArchiveDocument(ArchiveDocument archiveDocument) {
		this.archiveDocument = archiveDocument;
	}

	public UserAlias getUserAlias() {
		return this.userAlias;
	}

	public void setUserAlias(UserAlias userAlias) {
		this.userAlias = userAlias;
	}

	public ArchiveDocumentType getArchiveDocumentType() {
		return this.archiveDocumentType;
	}

	public void setArchiveDocumentType(ArchiveDocumentType archiveDocumentType) {
		this.archiveDocumentType = archiveDocumentType;
	}

	public SysUser getSysUserByCreatorUser() {
		return this.sysUserByCreatorUser;
	}

	public void setSysUserByCreatorUser(SysUser sysUserByCreatorUser) {
		this.sysUserByCreatorUser = sysUserByCreatorUser;
	}

	public String getFromEntity() {
		return this.fromEntity;
	}

	public void setFromEntity(String fromEntity) {
		this.fromEntity = fromEntity;
	}

	public Boolean getIsEncrypted() {
		return this.isEncrypted;
	}

	public void setIsEncrypted(Boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Short getYear() {
		return this.year;
	}

	public void setYear(Short year) {
		this.year = year;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getArabicName() {
		return arabicName;
	}

	public void setArabicName(String arabicName) {
		this.arabicName = arabicName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLang() {
		return this.lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Boolean getIsDirectory() {
		return this.isDirectory;
	}

	public void setIsDirectory(Boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Boolean getIsLocked() {
		return this.isLocked;
	}

	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}

	public Boolean getInheritMailPermission() {
		return this.inheritMailPermission;
	}

	public void setInheritMailPermission(Boolean inheritMailPermission) {
		this.inheritMailPermission = inheritMailPermission;
	}

	public Boolean getInheritParentAttribute() {
		return this.inheritParentAttribute;
	}

	public void setInheritParentAttribute(Boolean inheritParentAttribute) {
		this.inheritParentAttribute = inheritParentAttribute;
	}

	public Boolean getInheritAttributeToChild() {
		return this.inheritAttributeToChild;
	}

	public void setInheritAttributeToChild(Boolean inheritAttributeToChild) {
		this.inheritAttributeToChild = inheritAttributeToChild;
	}

	public Set getArchiveDocumentFiles() {
		return this.archiveDocumentFiles;
	}

	public void setArchiveDocumentFiles(Set archiveDocumentFiles) {
		this.archiveDocumentFiles = archiveDocumentFiles;
	}

	public Set getArchiveDocuments() {
		return this.archiveDocuments;
	}

	public void setArchiveDocuments(Set archiveDocuments) {
		this.archiveDocuments = archiveDocuments;
	}

	public Set getAccessByJobPositions() {
		return this.accessByJobPositions;
	}

	public void setAccessByJobPositions(Set accessByJobPositions) {
		this.accessByJobPositions = accessByJobPositions;
	}

	public Set getCorrespondenceArchives() {
		return this.correspondenceArchives;
	}

	public void setCorrespondenceArchives(Set correspondenceArchives) {
		this.correspondenceArchives = correspondenceArchives;
	}

	public Set getAccessByUserAliases() {
		return this.accessByUserAliases;
	}

	public void setAccessByUserAliases(Set accessByUserAliases) {
		this.accessByUserAliases = accessByUserAliases;
	}

	public Set getDocumentSharings() {
		return this.documentSharings;
	}

	public void setDocumentSharings(Set documentSharings) {
		this.documentSharings = documentSharings;
	}

	public Set getCabinetFolderDocuments() {
		return this.cabinetFolderDocuments;
	}

	public void setCabinetFolderDocuments(Set cabinetFolderDocuments) {
		this.cabinetFolderDocuments = cabinetFolderDocuments;
	}

	public Set getArchiveDocumentTags() {
		return this.archiveDocumentTags;
	}

	public void setArchiveDocumentTags(Set archiveDocumentTags) {
		this.archiveDocumentTags = archiveDocumentTags;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	@Override
	public boolean equals(Object other) {

		return (other instanceof ArchiveDocument) && (id != null) ? id.equals(((ArchiveDocument) other).id)
				: (other == this);
	}

	@Override
	public int hashCode() {
		return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		if (UtilityHelper.getLocale().getLanguage().contains("en"))
			return getEnglishName();
		else
			return getArabicName();
	}
}