package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * AccessByJobPosition entity. @author MyEclipse Persistence Tools
 */

@Audited
public class AccessByJobPosition implements java.io.Serializable {

	// Fields

	private Long id;
	private CabinetFolder cabinetFolder;
	private ArchiveAccessManagement archiveAccessManagement;
	private Cabinet cabinet;
	private AccessType accessType;
	private JobPosition jobPosition;
	private ArchiveDocument archiveDocument;

	// Constructors

	/** default constructor */
	public AccessByJobPosition() {
	}

	/** minimal constructor */
	public AccessByJobPosition(ArchiveAccessManagement archiveAccessManagement, AccessType accessType,
			JobPosition jobPosition) {
		this.archiveAccessManagement = archiveAccessManagement;
		this.accessType = accessType;
		this.jobPosition = jobPosition;
	}

	/** full constructor */
	public AccessByJobPosition(CabinetFolder cabinetFolder, ArchiveAccessManagement archiveAccessManagement,
			Cabinet cabinet, AccessType accessType, JobPosition jobPosition, ArchiveDocument archiveDocument) {
		this.cabinetFolder = cabinetFolder;
		this.archiveAccessManagement = archiveAccessManagement;
		this.cabinet = cabinet;
		this.accessType = accessType;
		this.jobPosition = jobPosition;
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

	public AccessType getAccessType() {
		return this.accessType;
	}

	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	public JobPosition getJobPosition() {
		return this.jobPosition;
	}

	public void setJobPosition(JobPosition jobPosition) {
		this.jobPosition = jobPosition;
	}

	public ArchiveDocument getArchiveDocument() {
		return this.archiveDocument;
	}

	public void setArchiveDocument(ArchiveDocument archiveDocument) {
		this.archiveDocument = archiveDocument;
	}

}