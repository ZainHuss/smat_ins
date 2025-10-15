package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;



/**
 * ChecklistDataSource entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ChecklistDataSource implements java.io.Serializable {

	// Fields

	private Short id;
	private String name;
	private String description;
	private String code;
	private Set generalEquipmentItems = new HashSet(0);
	private Set checklistDetailDataSources = new HashSet(0);

	// Constructors

	/** default constructor */
	public ChecklistDataSource() {
	}

	/** minimal constructor */
	public ChecklistDataSource(String name, String description, String code) {
		this.name = name;
		this.description = description;
		this.code = code;
	}

	/** full constructor */
	public ChecklistDataSource(String name, String description, String code, Set generalEquipmentItems,
			Set checklistDetailDataSources) {
		this.name = name;
		this.description = description;
		this.code = code;
		this.generalEquipmentItems = generalEquipmentItems;
		this.checklistDetailDataSources = checklistDetailDataSources;
	}

	// Property accessors

	public Short getId() {
		return this.id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Set getGeneralEquipmentItems() {
		return this.generalEquipmentItems;
	}

	public void setGeneralEquipmentItems(Set generalEquipmentItems) {
		this.generalEquipmentItems = generalEquipmentItems;
	}

	public Set getChecklistDetailDataSources() {
		return this.checklistDetailDataSources;
	}

	public void setChecklistDetailDataSources(Set checklistDetailDataSources) {
		this.checklistDetailDataSources = checklistDetailDataSources;
	}
	
	public boolean equals(Object other) {

		return (other instanceof ChecklistDataSource) && (id != null) ? id.equals(((ChecklistDataSource) other).id) : (other == this);
	}
	
	@Override
	public int hashCode() {
		return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}

	
}