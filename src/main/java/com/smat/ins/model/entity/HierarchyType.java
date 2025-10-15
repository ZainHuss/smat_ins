package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * HierarchyType entity. @author MyEclipse Persistence Tools
 */

@Audited
public class HierarchyType implements java.io.Serializable {

	// Fields

	private Short id;
	private String arabicName;
	private String englishName;
	private String code;
	private Set hierarchySystemDefs = new HashSet(0);
	private Set jobPositionHierarchies = new HashSet(0);

	// Constructors

	/** default constructor */
	public HierarchyType() {
	}

	/** minimal constructor */
	public HierarchyType(String arabicName, String englishName) {
		this.arabicName = arabicName;
		this.englishName = englishName;
	}

	/** full constructor */
	public HierarchyType(String arabicName, String englishName, String code, Set hierarchySystemDefs,
			Set jobPositionHierarchies) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.hierarchySystemDefs = hierarchySystemDefs;
		this.jobPositionHierarchies = jobPositionHierarchies;
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

	public Set getHierarchySystemDefs() {
		return this.hierarchySystemDefs;
	}

	public void setHierarchySystemDefs(Set hierarchySystemDefs) {
		this.hierarchySystemDefs = hierarchySystemDefs;
	}

	public Set getJobPositionHierarchies() {
		return this.jobPositionHierarchies;
	}

	public void setJobPositionHierarchies(Set jobPositionHierarchies) {
		this.jobPositionHierarchies = jobPositionHierarchies;
	}

	@Override
	public boolean equals(Object other) {

		return (other instanceof HierarchyType) && (id != null) ? id.equals(((HierarchyType) other).id)
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