package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * SysUserSetting entity. @author MyEclipse Persistence Tools
 */

@Audited
public class SysUserSetting implements java.io.Serializable {

	// Fields

	private Long id;
	private SysUser sysUser;
	private String attrKey;
	private String attrValue;

	// Constructors

	/** default constructor */
	public SysUserSetting() {
	}

	/** full constructor */
	public SysUserSetting(SysUser sysUser, String attrKey, String attrValue) {
		this.sysUser = sysUser;
		this.attrKey = attrKey;
		this.attrValue = attrValue;
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

	public String getAttrKey() {
		return this.attrKey;
	}

	public void setAttrKey(String attrKey) {
		this.attrKey = attrKey;
	}

	public String getAttrValue() {
		return this.attrValue;
	}

	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}

}