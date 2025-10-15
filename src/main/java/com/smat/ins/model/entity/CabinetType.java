package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * CabinetType entity. @author MyEclipse Persistence Tools
 */

@Audited
public class CabinetType implements java.io.Serializable {

	// Fields

	private Short id;
	private String arabicName;
	private String englishName;
	private String code;
	private Set cabinets = new HashSet(0);

	// Constructors

	/** default constructor */
	public CabinetType() {
	}

	/** full constructor */
	public CabinetType(String arabicName, String englishName, String code, Set cabinets) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.cabinets = cabinets;
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

	public Set getCabinets() {
		return this.cabinets;
	}

	public void setCabinets(Set cabinets) {
		this.cabinets = cabinets;
	}
	@Override
	public boolean equals(Object other) {

		return (other instanceof CabinetType) && (id != null) ? id.equals(((CabinetType) other).id)
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