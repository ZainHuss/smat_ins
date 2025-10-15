package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * FolderSharing entity. @author MyEclipse Persistence Tools
 */

@Audited
public class FolderSharing implements java.io.Serializable {

	// Fields

	private Long id;
	private CabinetFolder cabinetFolder;
	private ArchiveSharing archiveSharing;
	private UserAlias userAlias;
	private Organization organization;
	private SharingToType sharingToType;

	// Constructors

	/** default constructor */
	public FolderSharing() {
	}

	/** minimal constructor */
	public FolderSharing(CabinetFolder cabinetFolder, ArchiveSharing archiveSharing, SharingToType sharingToType) {
		this.cabinetFolder = cabinetFolder;
		this.archiveSharing = archiveSharing;
		this.sharingToType = sharingToType;
	}

	/** full constructor */
	public FolderSharing(CabinetFolder cabinetFolder, ArchiveSharing archiveSharing, UserAlias userAlias,
			Organization organization, SharingToType sharingToType) {
		this.cabinetFolder = cabinetFolder;
		this.archiveSharing = archiveSharing;
		this.userAlias = userAlias;
		this.organization = organization;
		this.sharingToType = sharingToType;
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

}