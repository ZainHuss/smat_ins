package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * ArchiveDocumentType entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ArchiveDocumentType implements java.io.Serializable {

	// Fields

	private Short id;
	private String arabicName;
	private String englishName;
	private String code;
	private Set archiveDocuments = new HashSet(0);

	// Constructors

	/** default constructor */
	public ArchiveDocumentType() {
	}

	/** full constructor */
	public ArchiveDocumentType(String arabicName, String englishName, String code, Set archiveDocuments) {
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
		this.archiveDocuments = archiveDocuments;
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

	public Set getArchiveDocuments() {
		return this.archiveDocuments;
	}

	public void setArchiveDocuments(Set archiveDocuments) {
		this.archiveDocuments = archiveDocuments;
	}
	
	@Override
	public boolean equals(Object other) {

		return (other instanceof ArchiveDocumentType) && (id != null) ? id.equals(((ArchiveDocumentType) other).id)
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