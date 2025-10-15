package com.smat.ins.model.entity;

import java.util.Date;

import org.hibernate.envers.Audited;

/**
 * ArchiveAccessManagement entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ArchiveAccessManagement implements java.io.Serializable {

	// Fields

	private Long id;
	private AccessManagementType accessManagementType;
	private SysUser sysUser;
	private Date createdDate;
	private AccessByUserAlias accessByUserAlias;
	private AccessByJobPosition accessByJobPosition;

	// Constructors

	/** default constructor */
	public ArchiveAccessManagement() {
	}

	/** minimal constructor */
	public ArchiveAccessManagement(AccessManagementType accessManagementType, SysUser sysUser) {
		this.accessManagementType = accessManagementType;
		this.sysUser = sysUser;
	}

	/** full constructor */
	public ArchiveAccessManagement(AccessManagementType accessManagementType, SysUser sysUser, Date createdDate,
			AccessByUserAlias accessByUserAlias, AccessByJobPosition accessByJobPosition) {
		this.accessManagementType = accessManagementType;
		this.sysUser = sysUser;
		this.createdDate = createdDate;
		this.accessByUserAlias = accessByUserAlias;
		this.accessByJobPosition = accessByJobPosition;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccessManagementType getAccessManagementType() {
		return this.accessManagementType;
	}

	public void setAccessManagementType(AccessManagementType accessManagementType) {
		this.accessManagementType = accessManagementType;
	}

	public SysUser getSysUser() {
		return this.sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public AccessByUserAlias getAccessByUserAlias() {
		return this.accessByUserAlias;
	}

	public void setAccessByUserAlias(AccessByUserAlias accessByUserAlias) {
		this.accessByUserAlias = accessByUserAlias;
	}

	public AccessByJobPosition getAccessByJobPosition() {
		return this.accessByJobPosition;
	}

	public void setAccessByJobPosition(AccessByJobPosition accessByJobPosition) {
		this.accessByJobPosition = accessByJobPosition;
	}

}