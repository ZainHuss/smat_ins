package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * Organization entity. @author MyEclipse Persistence Tools
 */

@Audited
public class Organization implements java.io.Serializable {

	// Fields

	private Long id;
	private HierarchySystemDef hierarchySystemDef;
	private HierarchySystem hierarchySystem;
	private Organization organization;
	private String arabicName;
	private String englishName;
	private String code;
	private String prefixCode;
	private String suffixCode;
	private Boolean isDivan;
	private Boolean hasPrivateHierarchy;
	private Set userAliasesForOrganization = new HashSet(0);
	private Set userAliasesForRootOrganization = new HashSet(0);
	private Set archiveDocumentsForOrganization = new HashSet(0);
	private Set correspondenceRecipients = new HashSet(0);
	private Set userAliasesForDivan = new HashSet(0);
	private Set sysUsersForRootOrganization = new HashSet(0);
	private Set organizations = new HashSet(0);
	private Set sysUsersForOrganization = new HashSet(0);
	private Set archiveDocumentsForRootOrganization = new HashSet(0);
	private Set cabinets = new HashSet(0);
	private Set correspondenceNotes = new HashSet(0);
	private Set archiveDocumentsForDivan = new HashSet(0);
	private Set folderSharings = new HashSet(0);
	private Set correspondencesForRootOrganization = new HashSet(0);
	private Set correspondencesForOrganization = new HashSet(0);
	private Set correspondencesForDivan = new HashSet(0);
	private Set documentSharings = new HashSet(0);

	// Constructors

	/** default constructor */
	public Organization() {
	}

	/** minimal constructor */
	public Organization(HierarchySystemDef hierarchySystemDef, Boolean hasPrivateHierarchy) {
		this.hierarchySystemDef = hierarchySystemDef;
		this.hasPrivateHierarchy = hasPrivateHierarchy;
	}

	/** full constructor */
	public Organization(HierarchySystemDef hierarchySystemDef, HierarchySystem hierarchySystem,
			Organization organization, String arabicName, String englishName, String code, String prefixCode,
			String suffixCode, Boolean isDivan, Boolean hasPrivateHierarchy, Set userAliasesForOrganization,
			Set userAliasesForRootOrganization, Set archiveDocumentsForOrganization, Set correspondenceRecipients,
			Set userAliasesForDivan, Set sysUsersForRootOrganization, Set organizations, Set sysUsersForOrganization,
			Set archiveDocumentsForRootOrganization, Set cabinets, Set correspondenceNotes,
			Set archiveDocumentsForDivan, Set folderSharings, Set correspondencesForRootOrganization,
			Set correspondencesForOrganization, Set correspondencesForDivan, Set documentSharings) {
		this.hierarchySystemDef = hierarchySystemDef;
		this.hierarchySystem = hierarchySystem;
		this.organization = organization;
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.prefixCode = prefixCode;
		this.suffixCode = suffixCode;
		this.isDivan = isDivan;
		this.hasPrivateHierarchy = hasPrivateHierarchy;
		this.userAliasesForOrganization = userAliasesForOrganization;
		this.userAliasesForRootOrganization = userAliasesForRootOrganization;
		this.archiveDocumentsForOrganization = archiveDocumentsForOrganization;
		this.correspondenceRecipients = correspondenceRecipients;
		this.userAliasesForDivan = userAliasesForDivan;
		this.sysUsersForRootOrganization = sysUsersForRootOrganization;
		this.organizations = organizations;
		this.sysUsersForOrganization = sysUsersForOrganization;
		this.archiveDocumentsForRootOrganization = archiveDocumentsForRootOrganization;
		this.cabinets = cabinets;
		this.correspondenceNotes = correspondenceNotes;
		this.archiveDocumentsForDivan = archiveDocumentsForDivan;
		this.folderSharings = folderSharings;
		this.correspondencesForRootOrganization = correspondencesForRootOrganization;
		this.correspondencesForOrganization = correspondencesForOrganization;
		this.correspondencesForDivan = correspondencesForDivan;
		this.documentSharings = documentSharings;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public HierarchySystemDef getHierarchySystemDef() {
		return this.hierarchySystemDef;
	}

	public void setHierarchySystemDef(HierarchySystemDef hierarchySystemDef) {
		this.hierarchySystemDef = hierarchySystemDef;
	}

	public HierarchySystem getHierarchySystem() {
		return this.hierarchySystem;
	}

	public void setHierarchySystem(HierarchySystem hierarchySystem) {
		this.hierarchySystem = hierarchySystem;
	}

	public Organization getOrganization() {
		return this.organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public String getArabicName() {
		return this.arabicName;
	}

	public void setArabicName(String arabicName) {
		this.arabicName = arabicName;
	}

	public String getEnglishName() {
		return this.englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPrefixCode() {
		return this.prefixCode;
	}

	public void setPrefixCode(String prefixCode) {
		this.prefixCode = prefixCode;
	}

	public String getSuffixCode() {
		return this.suffixCode;
	}

	public void setSuffixCode(String suffixCode) {
		this.suffixCode = suffixCode;
	}

	public Boolean getIsDivan() {
		return this.isDivan;
	}

	public void setIsDivan(Boolean isDivan) {
		this.isDivan = isDivan;
	}

	public Boolean getHasPrivateHierarchy() {
		return this.hasPrivateHierarchy;
	}

	public void setHasPrivateHierarchy(Boolean hasPrivateHierarchy) {
		this.hasPrivateHierarchy = hasPrivateHierarchy;
	}

	public Set getUserAliasesForOrganization() {
		return this.userAliasesForOrganization;
	}

	public void setUserAliasesForOrganization(Set userAliasesForOrganization) {
		this.userAliasesForOrganization = userAliasesForOrganization;
	}

	public Set getUserAliasesForRootOrganization() {
		return this.userAliasesForRootOrganization;
	}

	public void setUserAliasesForRootOrganization(Set userAliasesForRootOrganization) {
		this.userAliasesForRootOrganization = userAliasesForRootOrganization;
	}

	public Set getArchiveDocumentsForOrganization() {
		return this.archiveDocumentsForOrganization;
	}

	public void setArchiveDocumentsForOrganization(Set archiveDocumentsForOrganization) {
		this.archiveDocumentsForOrganization = archiveDocumentsForOrganization;
	}

	public Set getCorrespondenceRecipients() {
		return this.correspondenceRecipients;
	}

	public void setCorrespondenceRecipients(Set correspondenceRecipients) {
		this.correspondenceRecipients = correspondenceRecipients;
	}

	public Set getUserAliasesForDivan() {
		return this.userAliasesForDivan;
	}

	public void setUserAliasesForDivan(Set userAliasesForDivan) {
		this.userAliasesForDivan = userAliasesForDivan;
	}

	public Set getSysUsersForRootOrganization() {
		return this.sysUsersForRootOrganization;
	}

	public void setSysUsersForRootOrganization(Set sysUsersForRootOrganization) {
		this.sysUsersForRootOrganization = sysUsersForRootOrganization;
	}

	public Set getOrganizations() {
		return this.organizations;
	}

	public void setOrganizations(Set organizations) {
		this.organizations = organizations;
	}

	public Set getSysUsersForOrganization() {
		return this.sysUsersForOrganization;
	}

	public void setSysUsersForOrganization(Set sysUsersForOrganization) {
		this.sysUsersForOrganization = sysUsersForOrganization;
	}

	public Set getArchiveDocumentsForRootOrganization() {
		return this.archiveDocumentsForRootOrganization;
	}

	public void setArchiveDocumentsForRootOrganization(Set archiveDocumentsForRootOrganization) {
		this.archiveDocumentsForRootOrganization = archiveDocumentsForRootOrganization;
	}

	public Set getCabinets() {
		return this.cabinets;
	}

	public void setCabinets(Set cabinets) {
		this.cabinets = cabinets;
	}

	public Set getCorrespondenceNotes() {
		return this.correspondenceNotes;
	}

	public void setCorrespondenceNotes(Set correspondenceNotes) {
		this.correspondenceNotes = correspondenceNotes;
	}

	public Set getArchiveDocumentsForDivan() {
		return this.archiveDocumentsForDivan;
	}

	public void setArchiveDocumentsForDivan(Set archiveDocumentsForDivan) {
		this.archiveDocumentsForDivan = archiveDocumentsForDivan;
	}

	public Set getFolderSharings() {
		return this.folderSharings;
	}

	public void setFolderSharings(Set folderSharings) {
		this.folderSharings = folderSharings;
	}

	public Set getCorrespondencesForRootOrganization() {
		return this.correspondencesForRootOrganization;
	}

	public void setCorrespondencesForRootOrganization(Set correspondencesForRootOrganization) {
		this.correspondencesForRootOrganization = correspondencesForRootOrganization;
	}

	public Set getCorrespondencesForOrganization() {
		return this.correspondencesForOrganization;
	}

	public void setCorrespondencesForOrganization(Set correspondencesForOrganization) {
		this.correspondencesForOrganization = correspondencesForOrganization;
	}

	public Set getCorrespondencesForDivan() {
		return this.correspondencesForDivan;
	}

	public void setCorrespondencesForDivan(Set correspondencesForDivan) {
		this.correspondencesForDivan = correspondencesForDivan;
	}

	public Set getDocumentSharings() {
		return this.documentSharings;
	}

	public void setDocumentSharings(Set documentSharings) {
		this.documentSharings = documentSharings;
	}
	
	@Override
	public boolean equals(Object other) {

		return (other instanceof Organization) && (id != null) ? id.equals(((Organization) other).id) : (other == this);
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

	public String getOrganizationCode() {
		String fullOrgCode = "";
		if (prefixCode != null && !prefixCode.isEmpty())
			fullOrgCode += prefixCode;
		if (code != null && !code.isEmpty())
			fullOrgCode += code;
		if (suffixCode != null && !suffixCode.isEmpty())
			fullOrgCode += suffixCode;
		return fullOrgCode;
	}

}