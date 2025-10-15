package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * ExtraPropertyDefinition entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ExtraPropertyDefinition implements java.io.Serializable {

	// Fields

	private Long id;
	private ArchiveExtraProperty archiveExtraProperty;
	private PropertyType propertyType;
	private String propertyName;

	// Constructors

	/** default constructor */
	public ExtraPropertyDefinition() {
	}

	/** full constructor */
	public ExtraPropertyDefinition(ArchiveExtraProperty archiveExtraProperty, PropertyType propertyType,
			String propertyName) {
		this.archiveExtraProperty = archiveExtraProperty;
		this.propertyType = propertyType;
		this.propertyName = propertyName;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ArchiveExtraProperty getArchiveExtraProperty() {
		return this.archiveExtraProperty;
	}

	public void setArchiveExtraProperty(ArchiveExtraProperty archiveExtraProperty) {
		this.archiveExtraProperty = archiveExtraProperty;
	}

	public PropertyType getPropertyType() {
		return this.propertyType;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

}