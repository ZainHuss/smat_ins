package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * CabinetLocation entity. @author MyEclipse Persistence Tools
 */

@Audited
public class CabinetLocation implements java.io.Serializable {

	// Fields

	private Long id;
	private SysUser sysUser;
	private String winLocation;
	private String linuxLocation;
	private String macLocation;
	private Boolean isDefault;
	private Date createdDate;
	private Set cabinets = new HashSet(0);

	private Boolean isEditMode;

	// Constructors

	/** default constructor */
	public CabinetLocation() {
	}

	/** full constructor */
	public CabinetLocation(SysUser sysUser, String winLocation, String linuxLocation, String macLocation,
			Boolean isDefault, Date createdDate, Set cabinets) {
		this.sysUser = sysUser;
		this.winLocation = winLocation;
		this.linuxLocation = linuxLocation;
		this.macLocation = macLocation;
		this.isDefault = isDefault;
		this.createdDate = createdDate;
		this.cabinets = cabinets;
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

	public String getWinLocation() {
		return winLocation;
	}

	public void setWinLocation(String winLocation) {
		this.winLocation = winLocation;
	}

	public String getLinuxLocation() {
		return linuxLocation;
	}

	public void setLinuxLocation(String linuxLocation) {
		this.linuxLocation = linuxLocation;
	}

	public String getMacLocation() {
		return macLocation;
	}

	public void setMacLocation(String macLocation) {
		this.macLocation = macLocation;
	}

	public Boolean getIsDefault() {
		return this.isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Set getCabinets() {
		return this.cabinets;
	}

	public void setCabinets(Set cabinets) {
		this.cabinets = cabinets;
	}

	public Boolean getIsEditMode() {
		return isEditMode;
	}

	public void setIsEditMode(Boolean isEditMode) {
		this.isEditMode = isEditMode;
	}

	public String getLocation() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return winLocation;
		} else if (os.contains("osx")) {
			return macLocation;
		} else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
			return linuxLocation;
		}
		return "";
	}

}