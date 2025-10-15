package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * CorrespondenceArchive entity. @author MyEclipse Persistence Tools
 */

@Audited
public class CorrespondenceArchive implements java.io.Serializable {

	// Fields

	private Long id;
	private ArchiveDocument archiveDocument;
	private Correspondence correspondence;

	// Constructors

	/** default constructor */
	public CorrespondenceArchive() {
	}

	/** full constructor */
	public CorrespondenceArchive(ArchiveDocument archiveDocument, Correspondence correspondence) {
		this.archiveDocument = archiveDocument;
		this.correspondence = correspondence;
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

	public Correspondence getCorrespondence() {
		return this.correspondence;
	}

	public void setCorrespondence(Correspondence correspondence) {
		this.correspondence = correspondence;
	}

}