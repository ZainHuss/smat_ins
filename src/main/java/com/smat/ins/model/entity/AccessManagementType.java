package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * AccessManagementType entity. @author MyEclipse Persistence Tools
 */

@Audited
public class AccessManagementType implements java.io.Serializable {

	// Fields

	private Short id;
	private String arabicName;
	private String englishName;
	private String code;
	private Set archiveAccessManagements = new HashSet(0);

	// Constructors

	/** default constructor */
	public AccessManagementType() {
	}

	/** full constructor */
	public AccessManagementType(String arabicName, String englishName, String code, Set archiveAccessManagements) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.archiveAccessManagements = archiveAccessManagements;
	}

	// Property accessors

	public Short getId() {
		return this.id;
	}

	public void setId(Short id) {
		this.id = id;
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

	public Set getArchiveAccessManagements() {
		return this.archiveAccessManagements;
	}

	public void setArchiveAccessManagements(Set archiveAccessManagements) {
		this.archiveAccessManagements = archiveAccessManagements;
	}

}