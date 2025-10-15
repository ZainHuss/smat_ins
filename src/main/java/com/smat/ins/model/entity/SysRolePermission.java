package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * SysRolePermission entity. @author MyEclipse Persistence Tools
 */

@Audited
public class SysRolePermission implements java.io.Serializable {

	// Fields

	private Short id;
	private SysRole sysRole;
	private SysPermission sysPermission;

	// Constructors

	/** default constructor */
	public SysRolePermission() {
	}

	/** full constructor */
	public SysRolePermission(SysRole sysRole, SysPermission sysPermission) {
		this.sysRole = sysRole;
		this.sysPermission = sysPermission;
	}

	// Property accessors

	public Short getId() {
		return this.id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public SysRole getSysRole() {
		return this.sysRole;
	}

	public void setSysRole(SysRole sysRole) {
		this.sysRole = sysRole;
	}

	public SysPermission getSysPermission() {
		return this.sysPermission;
	}

	public void setSysPermission(SysPermission sysPermission) {
		this.sysPermission = sysPermission;
	}

}