package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * AccessType entity. @author MyEclipse Persistence Tools
 */

@Audited
public class AccessType implements java.io.Serializable {

	// Fields

	private Short id;
	private String arabicName;
	private String englishName;
	private String code;
	private Set accessByUserAliases = new HashSet(0);
	private Set accessByJobPositions = new HashSet(0);

	// Constructors

	/** default constructor */
	public AccessType() {
	}

	/** full constructor */
	public AccessType(String arabicName, String englishName, String code, Set accessByUserAliases,
			Set accessByJobPositions) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.accessByUserAliases = accessByUserAliases;
		this.accessByJobPositions = accessByJobPositions;
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

	public Set getAccessByUserAliases() {
		return this.accessByUserAliases;
	}

	public void setAccessByUserAliases(Set accessByUserAliases) {
		this.accessByUserAliases = accessByUserAliases;
	}

	public Set getAccessByJobPositions() {
		return this.accessByJobPositions;
	}

	public void setAccessByJobPositions(Set accessByJobPositions) {
		this.accessByJobPositions = accessByJobPositions;
	}

}