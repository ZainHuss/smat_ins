package com.smat.ins.model.entity;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import org.hibernate.envers.Audited;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


public class GenericArchiveDoc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 237897065888171938L;

	private Integer id;
	private String name;
	private String extension;
	private String mimeType;
	private Long fileSize;
	private byte[] data;
	
	private StreamedContent dynamicImage;

	public GenericArchiveDoc() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GenericArchiveDoc(Integer id, String name, String extension, String mimeType, Long fileSize, byte[] data) {
		super();
		this.id = id;
		this.name = name;
		this.extension = extension;
		this.mimeType = mimeType;
		this.fileSize = fileSize;
		this.data = data;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public StreamedContent getDynamicImage() {
	    return DefaultStreamedContent.builder()
		    .stream(() -> new ByteArrayInputStream(data))
		    .contentType("image/png")
		    .build();
	}

}
