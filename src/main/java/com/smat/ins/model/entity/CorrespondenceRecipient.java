package com.smat.ins.model.entity;

import java.util.Date;

import org.hibernate.envers.Audited;

/**
 * CorrespondenceRecipient entity. @author MyEclipse Persistence Tools
 */

@Audited
public class CorrespondenceRecipient implements java.io.Serializable {

	// Fields

	private Long id;
	private CorrespondenceNote correspondenceNote;
	private Organization organization;
	private UserAlias userAlias;
	private PurposeType purposeType;
	private PriorityType priorityType;
	private Correspondence correspondence;
	private Boolean isViewed;
	private Date viewedDate;
	private Boolean isArchived;
	private Date archivedDate;
	private Boolean isProcessed;
	private Date processingDate;

	private String note;
	private String htmlNote;

	// Constructors

	/** default constructor */
	public CorrespondenceRecipient() {
	}

	/** full constructor */
	public CorrespondenceRecipient(CorrespondenceNote correspondenceNote, Organization organization,
			UserAlias userAlias, PurposeType purposeType, PriorityType priorityType, Correspondence correspondence,
			Boolean isViewed, Date viewedDate, Boolean isArchived, Date archivedDate, Boolean isProcessed,
			Date processingDate) {
		this.correspondenceNote = correspondenceNote;
		this.organization = organization;
		this.userAlias = userAlias;
		this.purposeType = purposeType;
		this.priorityType = priorityType;
		this.correspondence = correspondence;
		this.isViewed = isViewed;
		this.viewedDate = viewedDate;
		this.isArchived = isArchived;
		this.archivedDate = archivedDate;
		this.isProcessed = isProcessed;
		this.processingDate = processingDate;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CorrespondenceNote getCorrespondenceNote() {
		return this.correspondenceNote;
	}

	public void setCorrespondenceNote(CorrespondenceNote correspondenceNote) {
		this.correspondenceNote = correspondenceNote;
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

	public PurposeType getPurposeType() {
		return this.purposeType;
	}

	public void setPurposeType(PurposeType purposeType) {
		this.purposeType = purposeType;
	}

	public PriorityType getPriorityType() {
		return this.priorityType;
	}

	public void setPriorityType(PriorityType priorityType) {
		this.priorityType = priorityType;
	}

	public Correspondence getCorrespondence() {
		return this.correspondence;
	}

	public void setCorrespondence(Correspondence correspondence) {
		this.correspondence = correspondence;
	}

	public Boolean getIsViewed() {
		return this.isViewed;
	}

	public void setIsViewed(Boolean isViewed) {
		this.isViewed = isViewed;
	}

	public Date getViewedDate() {
		return this.viewedDate;
	}

	public void setViewedDate(Date viewedDate) {
		this.viewedDate = viewedDate;
	}

	public Boolean getIsArchived() {
		return this.isArchived;
	}

	public void setIsArchived(Boolean isArchived) {
		this.isArchived = isArchived;
	}

	public Date getArchivedDate() {
		return this.archivedDate;
	}

	public void setArchivedDate(Date archivedDate) {
		this.archivedDate = archivedDate;
	}

	public Boolean getIsProcessed() {
		return this.isProcessed;
	}

	public void setIsProcessed(Boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	public Date getProcessingDate() {
		return this.processingDate;
	}

	public void setProcessingDate(Date processingDate) {
		this.processingDate = processingDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getHtmlNote() {
		return htmlNote;
	}

	public void setHtmlNote(String htmlNote) {
		this.htmlNote = htmlNote;
	}
		

}