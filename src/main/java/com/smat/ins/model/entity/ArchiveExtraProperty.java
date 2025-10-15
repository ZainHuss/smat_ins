package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * ArchiveExtraProperty entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ArchiveExtraProperty implements java.io.Serializable {

	// Fields

	private Long id;
	private String arabicTemplateName;
	private String englishTemplateName;
	private byte[] templateJsonDescription;
	private String databaseEntityName;
	private Set archiveDocuments = new HashSet(0);
	private Set extraPropertyDefinitions = new HashSet(0);

	// Constructors

	/** default constructor */
	public ArchiveExtraProperty() {
	}

	/** minimal constructor */
	public ArchiveExtraProperty(String arabicTemplateName, String englishTemplateName, byte[] templateJsonDescription,
			String databaseEntityName) {
		this.arabicTemplateName = arabicTemplateName;
		this.englishTemplateName = englishTemplateName;
		this.templateJsonDescription = templateJsonDescription;
		this.databaseEntityName = databaseEntityName;
	}

	/** full constructor */
	public ArchiveExtraProperty(String arabicTemplateName, String englishTemplateName, byte[] templateJsonDescription,
			String databaseEntityName, Set archiveDocuments, Set extraPropertyDefinitions) {
		this.arabicTemplateName = arabicTemplateName;
		this.englishTemplateName = englishTemplateName;
		this.templateJsonDescription = templateJsonDescription;
		this.databaseEntityName = databaseEntityName;
		this.archiveDocuments = archiveDocuments;
		this.extraPropertyDefinitions = extraPropertyDefinitions;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getArabicTemplateName() {
		return this.arabicTemplateName;
	}

	public void setArabicTemplateName(String arabicTemplateName) {
		this.arabicTemplateName = arabicTemplateName;
	}

	public String getEnglishTemplateName() {
		return this.englishTemplateName;
	}

	public void setEnglishTemplateName(String englishTemplateName) {
		this.englishTemplateName = englishTemplateName;
	}

	public byte[] getTemplateJsonDescription() {
		return this.templateJsonDescription;
	}

	public void setTemplateJsonDescription(byte[] templateJsonDescription) {
		this.templateJsonDescription = templateJsonDescription;
	}

	public String getDatabaseEntityName() {
		return this.databaseEntityName;
	}

	public void setDatabaseEntityName(String databaseEntityName) {
		this.databaseEntityName = databaseEntityName;
	}

	public Set getArchiveDocuments() {
		return this.archiveDocuments;
	}

	public void setArchiveDocuments(Set archiveDocuments) {
		this.archiveDocuments = archiveDocuments;
	}

	public Set getExtraPropertyDefinitions() {
		return this.extraPropertyDefinitions;
	}

	public void setExtraPropertyDefinitions(Set extraPropertyDefinitions) {
		this.extraPropertyDefinitions = extraPropertyDefinitions;
	}

}