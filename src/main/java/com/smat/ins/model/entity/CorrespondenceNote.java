package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * CorrespondenceNote entity. @author MyEclipse Persistence Tools
 */

@Audited
public class CorrespondenceNote implements java.io.Serializable {

	// Fields

	private Long id;
	private Organization organization;
	private UserAlias userAlias;
	private Correspondence correspondence;
	private Date noteDate;
	private byte[] note;
	private byte[] htmlNote;
	private Boolean deleted;
	private Date deletedDate;
	private Boolean toArchive;
	private Integer seqByCorrespondence;
	private Set correspondenceRecipients = new HashSet(0);

	// Constructors

	/** default constructor */
	public CorrespondenceNote() {
	}

	/** full constructor */
	public CorrespondenceNote(Organization organization, UserAlias userAlias, Correspondence correspondence,
			Date noteDate, byte[] note, byte[] htmlNote, Boolean deleted, Date deletedDate, Boolean toArchive,
			Integer seqByCorrespondence, Set correspondenceRecipients) {
		this.organization = organization;
		this.userAlias = userAlias;
		this.correspondence = correspondence;
		this.noteDate = noteDate;
		this.note = note;
		this.htmlNote = htmlNote;
		this.deleted = deleted;
		this.deletedDate = deletedDate;
		this.toArchive = toArchive;
		this.seqByCorrespondence = seqByCorrespondence;
		this.correspondenceRecipients = correspondenceRecipients;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Organization getOrganization() {
		return this.organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public UserAlias getUserAlias() {
		return this.userAlias;
	}

	public void setUserAlias(UserAlias userAlias) {
		this.userAlias = userAlias;
	}

	public Correspondence getCorrespondence() {
		return this.correspondence;
	}

	public void setCorrespondence(Correspondence correspondence) {
		this.correspondence = correspondence;
	}

	public Date getNoteDate() {
		return this.noteDate;
	}

	public void setNoteDate(Date noteDate) {
		this.noteDate = noteDate;
	}

	public byte[] getNote() {
		return this.note;
	}

	public void setNote(byte[] note) {
		this.note = note;
	}

	public byte[] getHtmlNote() {
		return this.htmlNote;
	}

	public void setHtmlNote(byte[] htmlNote) {
		this.htmlNote = htmlNote;
	}

	public Boolean getDeleted() {
		return this.deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Date getDeletedDate() {
		return this.deletedDate;
	}

	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}

	public Boolean getToArchive() {
		return this.toArchive;
	}

	public void setToArchive(Boolean toArchive) {
		this.toArchive = toArchive;
	}

	public Integer getSeqByCorrespondence() {
		return this.seqByCorrespondence;
	}

	public void setSeqByCorrespondence(Integer seqByCorrespondence) {
		this.seqByCorrespondence = seqByCorrespondence;
	}

	public Set getCorrespondenceRecipients() {
		return this.correspondenceRecipients;
	}

	public void setCorrespondenceRecipients(Set correspondenceRecipients) {
		this.correspondenceRecipients = correspondenceRecipients;
	}

}