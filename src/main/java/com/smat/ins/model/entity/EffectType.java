package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * EffectType entity. @author MyEclipse Persistence Tools
 */

@Audited
public class EffectType implements java.io.Serializable {

	// Fields

	private Short id;
	private String arabicName;
	private String englishName;
	private String code;
	private Set jobPositions = new HashSet(0);

	// Constructors

	/** default constructor */
	public EffectType() {
	}

	/** minimal constructor */
	public EffectType(String arabicName, String englishName, String code) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
	}

	/** full constructor */
	public EffectType(String arabicName, String englishName, String code, Set jobPositions) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.jobPositions = jobPositions;
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

	public Set getJobPositions() {
		return this.jobPositions;
	}

	public void setJobPositions(Set jobPositions) {
		this.jobPositions = jobPositions;
	}
	
	@Override
	public boolean equals(Object other) {

		return (other instanceof EffectType) && (id != null) ? id.equals(((EffectType) other).id)
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