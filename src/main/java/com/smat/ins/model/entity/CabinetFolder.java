package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * CabinetFolder entity. @author MyEclipse Persistence Tools
 */

@Audited
public class CabinetFolder implements java.io.Serializable {

	// Fields

	private Long id;
	private CabinetDefinition cabinetDefinition;
	private SysUser sysUser;
	private String arabicName;
	private String englishName;
	private String arabicDescription;
	private String englishDescription;
	private String code;
	private Date createdDate;
	private Set accessByUserAliases = new HashSet(0);
	private Set cabinetFolderDocuments = new HashSet(0);
	private Set folderSharings = new HashSet(0);
	private Set accessByJobPositions = new HashSet(0);

	// Constructors

	/** default constructor */
	public CabinetFolder() {
	}

	/** minimal constructor */
	public CabinetFolder(CabinetDefinition cabinetDefinition, SysUser sysUser) {
		this.cabinetDefinition = cabinetDefinition;
		this.sysUser = sysUser;
	}

	/** full constructor */
	public CabinetFolder(CabinetDefinition cabinetDefinition, SysUser sysUser, String arabicName, String englishName,
			String arabicDescription, String englishDescription, String code, Date createdDate,
			Set cabinetFolderDocuments, Set accessByJobPositions, Set folderSharings, Set accessByUserAliases) {
		this.cabinetDefinition = cabinetDefinition;
		this.sysUser = sysUser;
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.arabicDescription = arabicDescription;
		this.englishDescription = englishDescription;
		this.code = code;
		this.createdDate = createdDate;
		this.accessByUserAliases = accessByUserAliases;
		this.cabinetFolderDocuments = cabinetFolderDocuments;
		this.folderSharings = folderSharings;
		this.accessByJobPositions = accessByJobPositions;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CabinetDefinition getCabinetDefinition() {
		return this.cabinetDefinition;
	}

	public void setCabinetDefinition(CabinetDefinition cabinetDefinition) {
		this.cabinetDefinition = cabinetDefinition;
	}

	public SysUser getSysUser() {
		return this.sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
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

	public String getArabicDescription() {
		return this.arabicDescription;
	}

	public void setArabicDescription(String arabicDescription) {
		this.arabicDescription = arabicDescription;
	}

	public String getEnglishDescription() {
		return this.englishDescription;
	}

	public void setEnglishDescription(String englishDescription) {
		this.englishDescription = englishDescription;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Set getAccessByUserAliases() {
		return this.accessByUserAliases;
	}

	public void setAccessByUserAliases(Set accessByUserAliases) {
		this.accessByUserAliases = accessByUserAliases;
	}

	public Set getCabinetFolderDocuments() {
		return this.cabinetFolderDocuments;
	}

	public void setCabinetFolderDocuments(Set cabinetFolderDocuments) {
		this.cabinetFolderDocuments = cabinetFolderDocuments;
	}

	public Set getFolderSharings() {
		return this.folderSharings;
	}

	public void setFolderSharings(Set folderSharings) {
		this.folderSharings = folderSharings;
	}

	public Set getAccessByJobPositions() {
		return this.accessByJobPositions;
	}

	public void setAccessByJobPositions(Set accessByJobPositions) {
		this.accessByJobPositions = accessByJobPositions;
	}

	
	@Override
	public boolean equals(Object other) {

		return (other instanceof CabinetFolder) && (id != null) ? id.equals(((CabinetFolder) other).id)
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
	
	public String getDescription() {
		if (UtilityHelper.getLocale().getLanguage().contains("en"))
			return getEnglishDescription();
		else
			return getArabicDescription();
	}

}