package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * WorkingGroup entity. @author MyEclipse Persistence Tools
 */

@Audited
public class WorkingGroup implements java.io.Serializable {

	// Fields

	private Short id;
	private SysUser sysUser;
	private String arabicName;
	private String englishName;
	private String code;
	private Date createdDate;
	private Set groupMembers = new HashSet(0);

	// Constructors

	/** default constructor */
	public WorkingGroup() {
	}

	/** full constructor */
	public WorkingGroup(SysUser sysUser, String arabicName, String englishName, String code, Date createdDate,
			Set groupMembers) {
		this.sysUser = sysUser;
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.createdDate = createdDate;
		this.groupMembers = groupMembers;
	}

	// Property accessors

	public Short getId() {
		return this.id;
	}

	public void setId(Short id) {
		this.id = id;
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

	public Set getGroupMembers() {
		return this.groupMembers;
	}

	public void setGroupMembers(Set groupMembers) {
		this.groupMembers = groupMembers;
	}
	
	@Override
	public boolean equals(Object other) {

		return (other instanceof WorkingGroup) && (id != null) ? id.equals(((WorkingGroup) other).id)
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

}