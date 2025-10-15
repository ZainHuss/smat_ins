package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * HierarchySystemDef entity. @author MyEclipse Persistence Tools
 */

@Audited
public class HierarchySystemDef implements java.io.Serializable {

	// Fields

	private Short id;
	private HierarchyType hierarchyType;
	private HierarchySystem hierarchySystem;
	private String arabicName;
	private String englishName;
	private String code;
	private Set organizations = new HashSet(0);

	// Constructors

	/** default constructor */
	public HierarchySystemDef() {
	}

	/** minimal constructor */
	public HierarchySystemDef(HierarchyType hierarchyType, HierarchySystem hierarchySystem, String arabicName,
			String englishName) {
		this.hierarchyType = hierarchyType;
		this.hierarchySystem = hierarchySystem;
		this.arabicName = arabicName;
		this.englishName = englishName;
	}

	/** full constructor */
	public HierarchySystemDef(HierarchyType hierarchyType, HierarchySystem hierarchySystem, String arabicName,
			String englishName, String code, Set organizations) {
		this.hierarchyType = hierarchyType;
		this.hierarchySystem = hierarchySystem;
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.organizations = organizations;
	}

	// Property accessors

	public Short getId() {
		return this.id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public HierarchyType getHierarchyType() {
		return this.hierarchyType;
	}

	public void setHierarchyType(HierarchyType hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

	public HierarchySystem getHierarchySystem() {
		return this.hierarchySystem;
	}

	public void setHierarchySystem(HierarchySystem hierarchySystem) {
		this.hierarchySystem = hierarchySystem;
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

	@Override
	public boolean equals(Object other) {

		return (other instanceof HierarchySystemDef) && (id != null) ? id.equals(((HierarchySystemDef) other).id)
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