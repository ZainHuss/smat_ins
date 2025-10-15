package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * HierarchySystem entity. @author MyEclipse Persistence Tools
 */

@Audited
public class HierarchySystem implements java.io.Serializable {

	// Fields

	private Short id;
	private String arabicName;
	private String englishName;
	private String code;
	private Set organizations = new HashSet(0);
	private Set hierarchySystemDefs = new HashSet(0);

	// Constructors

	/** default constructor */
	public HierarchySystem() {
	}

	/** minimal constructor */
	public HierarchySystem(String arabicName, String englishName, String code) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
	}

	/** full constructor */
	public HierarchySystem(String arabicName, String englishName, String code, Set organizations,
			Set hierarchySystemDefs) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.organizations = organizations;
		this.hierarchySystemDefs = hierarchySystemDefs;
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

	public Set getOrganizations() {
		return this.organizations;
	}

	public void setOrganizations(Set organizations) {
		this.organizations = organizations;
	}

	public Set getHierarchySystemDefs() {
		return this.hierarchySystemDefs;
	}

	public void setHierarchySystemDefs(Set hierarchySystemDefs) {
		this.hierarchySystemDefs = hierarchySystemDefs;
	}

	@Override
	public boolean equals(Object other) {

		return (other instanceof HierarchySystem) && (id != null) ? id.equals(((HierarchySystem) other).id)
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