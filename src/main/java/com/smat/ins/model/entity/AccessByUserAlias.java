package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * AccessByUserAlias entity. @author MyEclipse Persistence Tools
 */
@Audited
public class AccessByUserAlias implements java.io.Serializable {

	// Fields

	private Long id;
	private CabinetFolder cabinetFolder;
	private ArchiveAccessManagement archiveAccessManagement;
	private Cabinet cabinet;
	private UserAlias userAlias;
	private AccessType accessType;
	private ArchiveDocument archiveDocument;

	// Constructors

	/** default constructor */
	public AccessByUserAlias() {
	}

	/** minimal constructor */
	public AccessByUserAlias(ArchiveAccessManagement archiveAccessManagement, AccessType accessType) {
		this.archiveAccessManagement = archiveAccessManagement;
		this.accessType = accessType;
	}

	/** full constructor */
	public AccessByUserAlias(CabinetFolder cabinetFolder, ArchiveAccessManagement archiveAccessManagement,
			Cabinet cabinet, UserAlias userAlias, AccessType accessType, ArchiveDocument archiveDocument) {
		this.cabinetFolder = cabinetFolder;
		this.archiveAccessManagement = archiveAccessManagement;
		this.cabinet = cabinet;
		this.userAlias = userAlias;
		this.accessType = accessType;
		this.archiveDocument = archiveDocument;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CabinetFolder getCabinetFolder() {
		return this.cabinetFolder;
	}

	public void setCabinetFolder(CabinetFolder cabinetFolder) {
		this.cabinetFolder = cabinetFolder;
	}

	public ArchiveAccessManagement getArchiveAccessManagement() {
		return this.archiveAccessManagement;
	}

	public void setArchiveAccessManagement(ArchiveAccessManagement archiveAccessManagement) {
		this.archiveAccessManagement = archiveAccessManagement;
	}

	public Cabinet getCabinet() {
		return this.cabinet;
	}

	public void setCabinet(Cabinet cabinet) {
		this.cabinet = cabinet;
	}

	public UserAlias getUserAlias() {
		return this.userAlias;
	}

	public void setUserAlias(UserAlias userAlias) {
		this.userAlias = userAlias;
	}

	public AccessType getAccessType() {
		return this.accessType;
	}

	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	public ArchiveDocument getArchiveDocument() {
		return this.archiveDocument;
	}

	public void setArchiveDocument(ArchiveDocument archiveDocument) {
		this.archiveDocument = archiveDocument;
	}

}