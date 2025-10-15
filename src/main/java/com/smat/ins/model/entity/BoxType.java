package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * BoxType entity. @author MyEclipse Persistence Tools
 */

@Audited
public class BoxType implements java.io.Serializable {

	// Fields

	private Short id;
	private String arabicName;
	private String englishName;
	private String arCode;
	private String enCode;
	private Set correspondences = new HashSet(0);

	// Constructors

	/** default constructor */
	public BoxType() {
	}

	/** full constructor */
	public BoxType(String arabicName, String englishName, String arCode, String enCode, Set correspondences) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.arCode = arCode;
		this.enCode = enCode;
		this.correspondences = correspondences;
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

	public String getArCode() {
		return this.arCode;
	}

	public void setArCode(String arCode) {
		this.arCode = arCode;
	}

	public String getEnCode() {
		return this.enCode;
	}

	public void setEnCode(String enCode) {
		this.enCode = enCode;
	}

	public Set getCorrespondences() {
		return this.correspondences;
	}

	public void setCorrespondences(Set correspondences) {
		this.correspondences = correspondences;
	}
	
	@Override
	public boolean equals(Object other) {

		return (other instanceof BoxType) && (id != null) ? id.equals(((BoxType) other).id)
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
	
	public String getCode() {
		if (UtilityHelper.getLocale().getLanguage().contains("en"))
			return getEnCode();
		else
			return getArCode();
	}

}