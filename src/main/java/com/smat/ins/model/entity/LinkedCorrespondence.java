package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * LinkedCorrespondence entity. @author MyEclipse Persistence Tools
 */


@Audited
public class LinkedCorrespondence implements java.io.Serializable {

	// Fields

	private Long id;
	private Correspondence correspondenceByLinkedCorrespondence;
	private Correspondence correspondenceByMasterCorrespondence;

	// Constructors

	/** default constructor */
	public LinkedCorrespondence() {
	}

	/** full constructor */
	public LinkedCorrespondence(Correspondence correspondenceByLinkedCorrespondence,
			Correspondence correspondenceByMasterCorrespondence) {
		this.correspondenceByLinkedCorrespondence = correspondenceByLinkedCorrespondence;
		this.correspondenceByMasterCorrespondence = correspondenceByMasterCorrespondence;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Correspondence getCorrespondenceByLinkedCorrespondence() {
		return this.correspondenceByLinkedCorrespondence;
	}

	public void setCorrespondenceByLinkedCorrespondence(Correspondence correspondenceByLinkedCorrespondence) {
		this.correspondenceByLinkedCorrespondence = correspondenceByLinkedCorrespondence;
	}

	public Correspondence getCorrespondenceByMasterCorrespondence() {
		return this.correspondenceByMasterCorrespondence;
	}

	public void setCorrespondenceByMasterCorrespondence(Correspondence correspondenceByMasterCorrespondence) {
		this.correspondenceByMasterCorrespondence = correspondenceByMasterCorrespondence;
	}

}