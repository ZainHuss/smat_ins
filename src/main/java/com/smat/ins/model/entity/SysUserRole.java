package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * SysUserRole entity. @author MyEclipse Persistence Tools
 */

@Audited
public class SysUserRole implements java.io.Serializable {

	// Fields

	private Long id;
	private SysRole sysRole;
	private SysUser sysUser;

	// Constructors

	/** default constructor */
	public SysUserRole() {
	}

	/** full constructor */
	public SysUserRole(SysRole sysRole, SysUser sysUser) {
		this.sysRole = sysRole;
		this.sysUser = sysUser;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SysRole getSysRole() {
		return this.sysRole;
	}

	public void setSysRole(SysRole sysRole) {
		this.sysRole = sysRole;
	}

	public SysUser getSysUser() {
		return this.sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

}