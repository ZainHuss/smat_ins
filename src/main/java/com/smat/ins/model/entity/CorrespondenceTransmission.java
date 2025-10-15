package com.smat.ins.model.entity;

import java.util.Date;

import org.hibernate.envers.Audited;

/**
 * CorrespondenceTransmission entity. @author MyEclipse Persistence Tools
 */

@Audited
public class CorrespondenceTransmission implements java.io.Serializable {

	// Fields

	private Long id;
	private TransmissionType transmissionType;
	private UserAlias userAliasByToAlias;
	private UserAlias userAliasByFromAlias;
	private Correspondence correspondence;
	private Date transmissionDate;

	// Constructors

	/** default constructor */
	public CorrespondenceTransmission() {
	}

	/** full constructor */
	public CorrespondenceTransmission(TransmissionType transmissionType, UserAlias userAliasByToAlias,
			UserAlias userAliasByFromAlias, Correspondence correspondence, Date transmissionDate) {
		this.transmissionType = transmissionType;
		this.userAliasByToAlias = userAliasByToAlias;
		this.userAliasByFromAlias = userAliasByFromAlias;
		this.correspondence = correspondence;
		this.transmissionDate = transmissionDate;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TransmissionType getTransmissionType() {
		return this.transmissionType;
	}

	public void setTransmissionType(TransmissionType transmissionType) {
		this.transmissionType = transmissionType;
	}

	public UserAlias getUserAliasByToAlias() {
		return this.userAliasByToAlias;
	}

	public void setUserAliasByToAlias(UserAlias userAliasByToAlias) {
		this.userAliasByToAlias = userAliasByToAlias;
	}

	public UserAlias getUserAliasByFromAlias() {
		return this.userAliasByFromAlias;
	}

	public void setUserAliasByFromAlias(UserAlias userAliasByFromAlias) {
		this.userAliasByFromAlias = userAliasByFromAlias;
	}

	public Correspondence getCorrespondence() {
		return this.correspondence;
	}

	public void setCorrespondence(Correspondence correspondence) {
		this.correspondence = correspondence;
	}

	public Date getTransmissionDate() {
		return this.transmissionDate;
	}

	public void setTransmissionDate(Date transmissionDate) {
		this.transmissionDate = transmissionDate;
	}

}