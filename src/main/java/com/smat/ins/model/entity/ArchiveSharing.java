package com.smat.ins.model.entity;

import java.util.Date;

import org.hibernate.envers.Audited;

/**
 * ArchiveSharing entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ArchiveSharing implements java.io.Serializable {

	// Fields

	private Long id;
	private SharingType sharingType;
	private SysUser sysUser;
	private Date sharingExpiredDate;
	private Date sharingDate;
	private String sharingLink;
	private DocumentSharing documentSharing;
	private FolderSharing folderSharing;

	// Constructors

	/** default constructor */
	public ArchiveSharing() {
	}

	/** minimal constructor */
	public ArchiveSharing(SharingType sharingType, SysUser sysUser) {
		this.sharingType = sharingType;
		this.sysUser = sysUser;
	}

	/** full constructor */
	public ArchiveSharing(SharingType sharingType, SysUser sysUser, Date sharingExpiredDate, Date sharingDate,
			String sharingLink, DocumentSharing documentSharing, FolderSharing folderSharing) {
		this.sharingType = sharingType;
		this.sysUser = sysUser;
		this.sharingExpiredDate = sharingExpiredDate;
		this.sharingDate = sharingDate;
		this.sharingLink = sharingLink;
		this.documentSharing = documentSharing;
		this.folderSharing = folderSharing;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SharingType getSharingType() {
		return this.sharingType;
	}

	public void setSharingType(SharingType sharingType) {
		this.sharingType = sharingType;
	}

	public SysUser getSysUser() {
		return this.sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	public Date getSharingExpiredDate() {
		return this.sharingExpiredDate;
	}

	public void setSharingExpiredDate(Date sharingExpiredDate) {
		this.sharingExpiredDate = sharingExpiredDate;
	}

	public Date getSharingDate() {
		return this.sharingDate;
	}

	public void setSharingDate(Date sharingDate) {
		this.sharingDate = sharingDate;
	}

	public String getSharingLink() {
		return this.sharingLink;
	}

	public void setSharingLink(String sharingLink) {
		this.sharingLink = sharingLink;
	}

	public DocumentSharing getDocumentSharing() {
		return this.documentSharing;
	}

	public void setDocumentSharing(DocumentSharing documentSharing) {
		this.documentSharing = documentSharing;
	}

	public FolderSharing getFolderSharing() {
		return this.folderSharing;
	}

	public void setFolderSharing(FolderSharing folderSharing) {
		this.folderSharing = folderSharing;
	}

}