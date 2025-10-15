package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * ServiceType entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ServiceType implements java.io.Serializable {

	// Fields

	private Short id;
	private String arabicName;
	private String englishName;
	private String code;
	private String arabicDescription;
	private String englishDescription;
	private Set tasks = new HashSet(0);

	// Constructors

	/** default constructor */
	public ServiceType() {
	}

	/** minimal constructor */
	public ServiceType(String arabicName, String englishName, String code) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
	}

	/** full constructor */
	public ServiceType(String arabicName, String englishName, String code, String arabicDescription,
			String englishDescription, Set tasks) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.arabicDescription = arabicDescription;
		this.englishDescription = englishDescription;
		this.tasks = tasks;
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

	public String getArabicDescription() {
		return this.arabicDescription;
	}

	public void setArabicDescription(String arabicDescription) {
		this.arabicDescription = arabicDescription;
	}

	public String getEnglishDescription() {
		return this.englishDescription;
	}

	public void setEnglishDescription(String englishDescription) {
		this.englishDescription = englishDescription;
	}

	public Set getTasks() {
		return this.tasks;
	}

	public void setTasks(Set tasks) {
		this.tasks = tasks;
	}
	
	@Override
	public boolean equals(Object other) {

		return (other instanceof ServiceType) && (id != null) ? id.equals(((ServiceType) other).id)
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