package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * PropertyType entity. @author MyEclipse Persistence Tools
 */

@Audited
public class PropertyType implements java.io.Serializable {

	// Fields

	private Short id;
	private String name;
	private String description;
	private Set extraPropertyDefinitions = new HashSet(0);

	// Constructors

	/** default constructor */
	public PropertyType() {
	}

	/** full constructor */
	public PropertyType(String name, String description, Set extraPropertyDefinitions) {
		this.name = name;
		this.description = description;
		this.extraPropertyDefinitions = extraPropertyDefinitions;
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

	public Set getExtraPropertyDefinitions() {
		return this.extraPropertyDefinitions;
	}

	public void setExtraPropertyDefinitions(Set extraPropertyDefinitions) {
		this.extraPropertyDefinitions = extraPropertyDefinitions;
	}

}