package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * CabinetDefinition entity. @author MyEclipse Persistence Tools
 */

@Audited
public class CabinetDefinition implements java.io.Serializable {

	// Fields

	private Long id;
	private Cabinet cabinet;
	private SysUser sysUser;
	private String drawerArabicName;
	private String drawerEnglishName;
	private String code;
	private Integer drawerOrder;
	private Date createdDate;
	private Set cabinetFolders = new HashSet(0);
	
	private Boolean isEditMode;

	// Constructors

	/** default constructor */
	public CabinetDefinition() {
	}

	/** minimal constructor */
	public CabinetDefinition(Cabinet cabinet, SysUser sysUser) {
		this.cabinet = cabinet;
		this.sysUser = sysUser;
	}

	/** full constructor */
	public CabinetDefinition(Cabinet cabinet, SysUser sysUser, String drawerArabicName, String drawerEnglishName,
			String code, Integer drawerOrder, Date createdDate, Set cabinetFolders) {
		this.cabinet = cabinet;
		this.sysUser = sysUser;
		this.drawerArabicName = drawerArabicName;
		this.drawerEnglishName = drawerEnglishName;
		this.code = code;
		this.drawerOrder = drawerOrder;
		this.createdDate = createdDate;
		this.cabinetFolders = cabinetFolders;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cabinet getCabinet() {
		return this.cabinet;
	}

	public void setCabinet(Cabinet cabinet) {
		this.cabinet = cabinet;
	}

	public SysUser getSysUser() {
		return this.sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public String getDrawerArabicName() {
		return this.drawerArabicName;
	}

	public void setDrawerArabicName(String drawerArabicName) {
		this.drawerArabicName = drawerArabicName;
	}

	public String getDrawerEnglishName() {
		return this.drawerEnglishName;
	}

	public void setDrawerEnglishName(String drawerEnglishName) {
		this.drawerEnglishName = drawerEnglishName;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getDrawerOrder() {
		return this.drawerOrder;
	}

	public void setDrawerOrder(Integer drawerOrder) {
		this.drawerOrder = drawerOrder;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Set getCabinetFolders() {
		return this.cabinetFolders;
	}

	public void setCabinetFolders(Set cabinetFolders) {
		this.cabinetFolders = cabinetFolders;
	}
	
	
	
	public Boolean getIsEditMode() {
		return isEditMode;
	}

	public void setIsEditMode(Boolean isEditMode) {
		this.isEditMode = isEditMode;
	}

	@Override
	public boolean equals(Object other) {

		return (other instanceof CabinetDefinition) && (id != null) ? id.equals(((CabinetDefinition) other).id)
				: (other == this);
	}

	@Override
	public int hashCode() {
		return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
	}

	@Override
	public String toString() {
		return getDrawerName();
	}

	public String getDrawerName() {
		if (UtilityHelper.getLocale().getLanguage().contains("en"))
			return getDrawerEnglishName();
		else
			return getDrawerArabicName();
	}
	
	

}