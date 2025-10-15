package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * SharingToType entity. @author MyEclipse Persistence Tools
 */

@Audited
public class SharingToType implements java.io.Serializable {

	// Fields

	private Short id;
	private String arabicName;
	private String englishName;
	private String code;
	private Set documentSharings = new HashSet(0);
	private Set folderSharings = new HashSet(0);

	// Constructors

	/** default constructor */
	public SharingToType() {
	}

	/** full constructor */
	public SharingToType(String arabicName, String englishName, String code, Set documentSharings, Set folderSharings) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.documentSharings = documentSharings;
		this.folderSharings = folderSharings;
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

	public Set getDocumentSharings() {
		return this.documentSharings;
	}

	public void setDocumentSharings(Set documentSharings) {
		this.documentSharings = documentSharings;
	}

	public Set getFolderSharings() {
		return this.folderSharings;
	}

	public void setFolderSharings(Set folderSharings) {
		this.folderSharings = folderSharings;
	}

}