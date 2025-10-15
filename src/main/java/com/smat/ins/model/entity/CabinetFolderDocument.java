package com.smat.ins.model.entity;

import java.util.Date;

import org.hibernate.envers.Audited;

/**
 * CabinetFolderDocument entity. @author MyEclipse Persistence Tools
 */

@Audited
public class CabinetFolderDocument implements java.io.Serializable {

	// Fields

	private Long id;
	private CabinetFolder cabinetFolder;
	private SysUser sysUser;
	private ArchiveDocument archiveDocument;
	private Date createdDate;

	// Constructors

	/** default constructor */
	public CabinetFolderDocument() {
	}

	/** minimal constructor */
	public CabinetFolderDocument(CabinetFolder cabinetFolder, SysUser sysUser, ArchiveDocument archiveDocument) {
		this.cabinetFolder = cabinetFolder;
		this.sysUser = sysUser;
		this.archiveDocument = archiveDocument;
	}

	/** full constructor */
	public CabinetFolderDocument(CabinetFolder cabinetFolder, SysUser sysUser, ArchiveDocument archiveDocument,
			Date createdDate) {
		this.cabinetFolder = cabinetFolder;
		this.sysUser = sysUser;
		this.archiveDocument = archiveDocument;
		this.createdDate = createdDate;
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

	public SysUser getSysUser() {
		return this.sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public ArchiveDocument getArchiveDocument() {
		return this.archiveDocument;
	}

	public void setArchiveDocument(ArchiveDocument archiveDocument) {
		this.archiveDocument = archiveDocument;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}