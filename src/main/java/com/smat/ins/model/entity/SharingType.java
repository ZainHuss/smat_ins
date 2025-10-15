package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * SharingType entity. @author MyEclipse Persistence Tools
 */

@Audited
public class SharingType implements java.io.Serializable {

	// Fields

	private Short id;
	private String arabicName;
	private String englishName;
	private String code;
	private Set archiveSharings = new HashSet(0);

	// Constructors

	/** default constructor */
	public SharingType() {
	}

	/** full constructor */
	public SharingType(String arabicName, String englishName, String code, Set archiveSharings) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.archiveSharings = archiveSharings;
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

	public Set getArchiveSharings() {
		return this.archiveSharings;
	}

	public void setArchiveSharings(Set archiveSharings) {
		this.archiveSharings = archiveSharings;
	}

}