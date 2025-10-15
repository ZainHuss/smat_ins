package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * DocumentSharing entity. @author MyEclipse Persistence Tools
 */

@Audited
public class DocumentSharing implements java.io.Serializable {

	// Fields

	private Long id;
	private ArchiveSharing archiveSharing;
	private UserAlias userAlias;
	private Organization organization;
	private SharingToType sharingToType;
	private ArchiveDocument archiveDocument;

	// Constructors

	/** default constructor */
	public DocumentSharing() {
	}

	/** minimal constructor */
	public DocumentSharing(ArchiveSharing archiveSharing, SharingToType sharingToType,
			ArchiveDocument archiveDocument) {
		this.archiveSharing = archiveSharing;
		this.sharingToType = sharingToType;
		this.archiveDocument = archiveDocument;
	}

	/** full constructor */
	public DocumentSharing(ArchiveSharing archiveSharing, UserAlias userAlias, Organization organization,
			SharingToType sharingToType, ArchiveDocument archiveDocument) {
		this.archiveSharing = archiveSharing;
		this.userAlias = userAlias;
		this.organization = organization;
		this.sharingToType = sharingToType;
		this.archiveDocument = archiveDocument;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ArchiveSharing getArchiveSharing() {
		return this.archiveSharing;
	}

	public void setArchiveSharing(ArchiveSharing archiveSharing) {
		this.archiveSharing = archiveSharing;
	}

	public UserAlias getUserAlias() {
		return this.userAlias;
	}

	public void setUserAlias(UserAlias userAlias) {
		this.userAlias = userAlias;
	}

	public Organization getOrganization() {
		return this.organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public SharingToType getSharingToType() {
		return this.sharingToType;
	}

	public void setSharingToType(SharingToType sharingToType) {
		this.sharingToType = sharingToType;
	}

	public ArchiveDocument getArchiveDocument() {
		return this.archiveDocument;
	}

	public void setArchiveDocument(ArchiveDocument archiveDocument) {
		this.archiveDocument = archiveDocument;
	}

}