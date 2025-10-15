package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * SysRole entity. @author MyEclipse Persistence Tools
 */

@Audited
public class SysRole implements java.io.Serializable {

	// Fields

	private Short id;
	private String name;
	private String code;
	private String description;
	private Set sysRolePermissions = new HashSet(0);
	private Set sysUserRoles = new HashSet(0);

	// Constructors

	/** default constructor */
	public SysRole() {
	}

	/** full constructor */
	public SysRole(String name, String code, String description, Set sysRolePermissions, Set sysUserRoles) {
		this.name = name;
		this.code = code;
		this.description = description;
		this.sysRolePermissions = sysRolePermissions;
		this.sysUserRoles = sysUserRoles;
	}

	// Property accessors

	public Short getId() {
		return this.id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set getSysRolePermissions() {
		return this.sysRolePermissions;
	}

	public void setSysRolePermissions(Set sysRolePermissions) {
		this.sysRolePermissions = sysRolePermissions;
	}

	public Set getSysUserRoles() {
		return this.sysUserRoles;
	}

	public void setSysUserRoles(Set sysUserRoles) {
		this.sysUserRoles = sysUserRoles;
	}

}