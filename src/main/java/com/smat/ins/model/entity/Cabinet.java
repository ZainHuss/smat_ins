package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * Cabinet entity. @author MyEclipse Persistence Tools
 */

@Audited
public class Cabinet implements java.io.Serializable {

	// Fields

	private Long id;
	private SysUser sysUser;
	private UserAlias userAlias;
	private Organization organization;
	private CabinetLocation cabinetLocation;
	private CabinetType cabinetType;
	private String arabicName;
	private String englishName;
	private String arabicDescription;
	private String englishDescription;
	private String code;
	private Date createdDate;
	private Set cabinetDefinitions = new HashSet(0);
	private Set accessByUserAliases = new HashSet(0);
	private Set accessByJobPositions = new HashSet(0);

	// Constructors

	/** default constructor */
	public Cabinet() {
	}

	/** minimal constructor */
	public Cabinet(SysUser sysUser, CabinetLocation cabinetLocation, CabinetType cabinetType) {
		this.sysUser = sysUser;
		this.cabinetLocation = cabinetLocation;
		this.cabinetType = cabinetType;
	}

	/** full constructor */
	public Cabinet(SysUser sysUser, UserAlias userAlias, Organization organization, CabinetLocation cabinetLocation,
			CabinetType cabinetType, String arabicName, String englishName, String arabicDescription,
			String englishDescription, String code, Date createdDate, Set cabinetDefinitions, Set accessByUserAliases,
			Set accessByJobPositions) {
		this.sysUser = sysUser;
		this.userAlias = userAlias;
		this.organization = organization;
		this.cabinetLocation = cabinetLocation;
		this.cabinetType = cabinetType;
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.arabicDescription = arabicDescription;
		this.englishDescription = englishDescription;
		this.code = code;
		this.createdDate = createdDate;
		this.cabinetDefinitions = cabinetDefinitions;
		this.accessByUserAliases = accessByUserAliases;
		this.accessByJobPositions = accessByJobPositions;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SysUser getSysUser() {
		return this.sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public UserAlias getUserAlias() {
		return this.userAlias;
	}

	public void setUserAlias(UserAlias userAlias) {
		this.userAlias = userAlias;
	}

	public Organization getOrganization() {
		return this.organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public CabinetLocation getCabinetLocation() {
		return this.cabinetLocation;
	}

	public void setCabinetLocation(CabinetLocation cabinetLocation) {
		this.cabinetLocation = cabinetLocation;
	}

	public CabinetType getCabinetType() {
		return this.cabinetType;
	}

	public void setCabinetType(CabinetType cabinetType) {
		this.cabinetType = cabinetType;
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

	public Set getCabinetDefinitions() {
		return this.cabinetDefinitions;
	}

	public void setCabinetDefinitions(Set cabinetDefinitions) {
		this.cabinetDefinitions = cabinetDefinitions;
	}

	public Set getAccessByUserAliases() {
		return this.accessByUserAliases;
	}

	public void setAccessByUserAliases(Set accessByUserAliases) {
		this.accessByUserAliases = accessByUserAliases;
	}

	public Set getAccessByJobPositions() {
		return this.accessByJobPositions;
	}

	public void setAccessByJobPositions(Set accessByJobPositions) {
		this.accessByJobPositions = accessByJobPositions;
	}
	
	@Override
	public boolean equals(Object other) {

		return (other instanceof Cabinet) && (id != null) ? id.equals(((Cabinet) other).id)
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