package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * EquipmentCategory entity. @author MyEclipse Persistence Tools
 */

@Audited
public class EquipmentCategory implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -7260699182068020409L;
	private Short id;
	private String arabicName;
	private String englishName;
	private String arabicDescription;
	private String englishDescription;
	private String code;
	private Set tasks = new HashSet(0);
	private Set equipmentInspectionForms = new HashSet(0);
	private Set formTemplates = new HashSet(0);

	// Constructors

	/** default constructor */
	public EquipmentCategory() {
	}

	/** full constructor */
	public EquipmentCategory(String arabicName, String englishName, String arabicDescription, String englishDescription,
			String code, Set tasks, Set equipmentInspectionForms, Set formTemplates) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.arabicDescription = arabicDescription;
		this.englishDescription = englishDescription;
		this.code = code;
		this.tasks = tasks;
		this.equipmentInspectionForms = equipmentInspectionForms;
		this.formTemplates = formTemplates;
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

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Set getTasks() {
		return this.tasks;
	}

	public void setTasks(Set tasks) {
		this.tasks = tasks;
	}

	public Set getEquipmentInspectionForms() {
		return this.equipmentInspectionForms;
	}

	public void setEquipmentInspectionForms(Set equipmentInspectionForms) {
		this.equipmentInspectionForms = equipmentInspectionForms;
	}

	public Set getFormTemplates() {
		return this.formTemplates;
	}

	public void setFormTemplates(Set formTemplates) {
		this.formTemplates = formTemplates;
	}

	@Override
	public boolean equals(Object other) {

		return (other instanceof EquipmentCategory) && (id != null) ? id.equals(((EquipmentCategory) other).id)
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