package com.smat.ins.model.entity;

import java.util.Date;

import org.hibernate.envers.Audited;

/**
 * ArchiveDocumentFile entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ArchiveDocumentFile implements java.io.Serializable {

	// Fields

	private Long id;
	private Double version;
	private ArchiveDocument archiveDocument;
	private String name;
	private String code;
	private Date versionDate;
	private String versionAuthor;
	private String versionRationale;
	private Boolean minorVersion;
	private Boolean majorVersion;
	private String extension;
	private String mimeType;
	private String uuid;
	private Long fileSize;
	private Date createdDate;
	private String logicalPath;
	private String serverPath;
	private byte[] content;
	private String physicalPath;

	// Constructors

	/** default constructor */
	public ArchiveDocumentFile() {
	}

	/** full constructor */
	public ArchiveDocumentFile(ArchiveDocument archiveDocument, String name, String code, Date versionDate,
			String versionAuthor, String versionRationale, Boolean minorVersion, Boolean majorVersion, String extension,
			String mimeType, String uuid, Long fileSize, Date createdDate, String logicalPath, String serverPath) {
		this.archiveDocument = archiveDocument;
		this.name = name;
		this.code = code;
		this.versionDate = versionDate;
		this.versionAuthor = versionAuthor;
		this.versionRationale = versionRationale;
		this.minorVersion = minorVersion;
		this.majorVersion = majorVersion;
		this.extension = extension;
		this.mimeType = mimeType;
		this.uuid = uuid;
		this.fileSize = fileSize;
		this.createdDate = createdDate;
		this.logicalPath = logicalPath;
		this.serverPath = serverPath;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getVersion() {
		return this.version;
	}

	public void setVersion(Double version) {
		this.version = version;
	}

	public ArchiveDocument getArchiveDocument() {
		return this.archiveDocument;
	}

	public void setArchiveDocument(ArchiveDocument archiveDocument) {
		this.archiveDocument = archiveDocument;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getVersionDate() {
		return this.versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

	public String getVersionAuthor() {
		return this.versionAuthor;
	}

	public void setVersionAuthor(String versionAuthor) {
		this.versionAuthor = versionAuthor;
	}

	public String getVersionRationale() {
		return this.versionRationale;
	}

	public void setVersionRationale(String versionRationale) {
		this.versionRationale = versionRationale;
	}

	public Boolean getMinorVersion() {
		return this.minorVersion;
	}

	public void setMinorVersion(Boolean minorVersion) {
		this.minorVersion = minorVersion;
	}

	public Boolean getMajorVersion() {
		return this.majorVersion;
	}

	public void setMajorVersion(Boolean majorVersion) {
		this.majorVersion = majorVersion;
	}

	public String getExtension() {
		return this.extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getLogicalPath() {
		return this.logicalPath;
	}

	public void setLogicalPath(String logicalPath) {
		this.logicalPath = logicalPath;
	}

	public String getServerPath() {
		return this.serverPath;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getPhysicalPath() {
		return physicalPath;
	}

	public void setPhysicalPath(String physicalPath) {
		this.physicalPath = physicalPath;
	}
	
	
	
}