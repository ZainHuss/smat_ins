package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * ArchiveDocumentTag entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ArchiveDocumentTag implements java.io.Serializable {

	// Fields

	private Long id;
	private ArchiveDocument archiveDocument;
	private String arabicName;
	private String englishName;

	// Constructors

	/** default constructor */
	public ArchiveDocumentTag() {
	}

	/** minimal constructor */
	public ArchiveDocumentTag(ArchiveDocument archiveDocument) {
		this.archiveDocument = archiveDocument;
	}

	/** full constructor */
	public ArchiveDocumentTag(ArchiveDocument archiveDocument, String arabicName, String englishName) {
		this.archiveDocument = archiveDocument;
		this.arabicName = arabicName;
		this.englishName = englishName;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ArchiveDocument getArchiveDocument() {
		return this.archiveDocument;
	}

	public void setArchiveDocument(ArchiveDocument archiveDocument) {
		this.archiveDocument = archiveDocument;
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

}