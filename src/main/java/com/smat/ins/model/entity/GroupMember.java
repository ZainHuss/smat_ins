package com.smat.ins.model.entity;

import java.util.Date;

import org.hibernate.envers.Audited;

/**
 * GroupMember entity. @author MyEclipse Persistence Tools
 */

@Audited
public class GroupMember implements java.io.Serializable {

	// Fields

	private Long id;
	private WorkingGroup workingGroup;
	private SysUser sysUser;
	private UserAlias userAlias;
	private Date createdDate;
	private Boolean isDisabled;

	// Constructors

	/** default constructor */
	public GroupMember() {
	}

	/** full constructor */
	public GroupMember(WorkingGroup workingGroup, SysUser sysUser, UserAlias userAlias, Date createdDate,
			Boolean isDisabled) {
		this.workingGroup = workingGroup;
		this.sysUser = sysUser;
		this.userAlias = userAlias;
		this.createdDate = createdDate;
		this.isDisabled = isDisabled;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public WorkingGroup getWorkingGroup() {
		return this.workingGroup;
	}

	public void setWorkingGroup(WorkingGroup workingGroup) {
		this.workingGroup = workingGroup;
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

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Boolean getIsDisabled() {
		return this.isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

}