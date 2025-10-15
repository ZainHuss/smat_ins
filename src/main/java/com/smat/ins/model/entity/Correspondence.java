package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;



/**
 * Correspondence entity. @author MyEclipse Persistence Tools
 */

@Audited
public class Correspondence extends LocalDatePatternConverter implements java.io.Serializable {

	// Fields

	private Long id;
	private CorrespondenceState correspondenceState;
	private SysUser sysUserByDeleteBySysUser;
	private Organization organizationByRootOrganization;
	private CorrespondenceType correspondenceType;
	private Organization organizationByDivan;
	private SysUser sysUserByCreatorSysUser;
	private Organization organizationByOrganization;
	private PriorityType priorityType;
	private BoxType boxType;
	private UserAlias userAlias;
	private Long seq;
	private Integer year;
	private Date correspondenceDate;
	private String title;
	private byte[] subject;
	private byte[] htmlSubject;
	private String fromNumber;
	private String fromEntity;
	private Date fromDate;
	private Date createdDate;
	private Date deleteDate;
	private Set correspondenceArchives = new HashSet(0);
	private Set correspondenceTransmissions = new HashSet(0);
	private Set correspondenceNotes = new HashSet(0);
	private Set notifications = new HashSet(0);
	private Set linkedCorrespondencesForLinkedCorrespondence = new HashSet(0);
	private Set linkedCorrespondencesForMasterCorrespondence = new HashSet(0);
	private Set correspondenceRecipients = new HashSet(0);
	private Set correspondenceTasks = new HashSet(0);

	// Constructors

	/** default constructor */
	public Correspondence() {
	}

	/** full constructor */
	public Correspondence(CorrespondenceState correspondenceState, SysUser sysUserByDeleteBySysUser,
			Organization organizationByRootOrganization, CorrespondenceType correspondenceType,
			Organization organizationByDivan, SysUser sysUserByCreatorSysUser, Organization organizationByOrganization,
			PriorityType priorityType, UserAlias userAlias, BoxType boxType, Long seq, Integer year,
			Date correspondenceDate, String title, byte[] subject, byte[] htmlSubject, String fromNumber,
			String fromEntity, Date fromDate, Date createdDate, Date deleteDate, Set correspondenceRecipients,
			Set notifications, Set linkedCorrespondencesForLinkedCorrespondence,
			Set linkedCorrespondencesForMasterCorrespondence, Set correspondenceTransmissions, Set correspondenceNotes,
			Set correspondenceArchives,Set correspondenceTasks) {
		this.correspondenceState = correspondenceState;
		this.sysUserByDeleteBySysUser = sysUserByDeleteBySysUser;
		this.organizationByRootOrganization = organizationByRootOrganization;
		this.correspondenceType = correspondenceType;
		this.organizationByDivan = organizationByDivan;
		this.sysUserByCreatorSysUser = sysUserByCreatorSysUser;
		this.organizationByOrganization = organizationByOrganization;
		this.priorityType = priorityType;
		this.boxType = boxType;
		this.userAlias = userAlias;
		this.seq = seq;
		this.year = year;
		this.correspondenceDate = correspondenceDate;
		this.title = title;
		this.subject = subject;
		this.htmlSubject = htmlSubject;
		this.fromNumber = fromNumber;
		this.fromEntity = fromEntity;
		this.fromDate = fromDate;
		this.createdDate = createdDate;
		this.deleteDate = deleteDate;
		this.correspondenceArchives = correspondenceArchives;
		this.correspondenceTransmissions = correspondenceTransmissions;
		this.correspondenceNotes = correspondenceNotes;
		this.notifications = notifications;
		this.linkedCorrespondencesForLinkedCorrespondence = linkedCorrespondencesForLinkedCorrespondence;
		this.linkedCorrespondencesForMasterCorrespondence = linkedCorrespondencesForMasterCorrespondence;
		this.correspondenceRecipients = correspondenceRecipients;
		this.correspondenceTasks=correspondenceTasks;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CorrespondenceState getCorrespondenceState() {
		return this.correspondenceState;
	}

	public void setCorrespondenceState(CorrespondenceState correspondenceState) {
		this.correspondenceState = correspondenceState;
	}

	public SysUser getSysUserByDeleteBySysUser() {
		return this.sysUserByDeleteBySysUser;
	}

	public void setSysUserByDeleteBySysUser(SysUser sysUserByDeleteBySysUser) {
		this.sysUserByDeleteBySysUser = sysUserByDeleteBySysUser;
	}

	public Organization getOrganizationByRootOrganization() {
		return this.organizationByRootOrganization;
	}

	public void setOrganizationByRootOrganization(Organization organizationByRootOrganization) {
		this.organizationByRootOrganization = organizationByRootOrganization;
	}

	public CorrespondenceType getCorrespondenceType() {
		return this.correspondenceType;
	}

	public void setCorrespondenceType(CorrespondenceType correspondenceType) {
		this.correspondenceType = correspondenceType;
	}

	public Organization getOrganizationByDivan() {
		return this.organizationByDivan;
	}

	public void setOrganizationByDivan(Organization organizationByDivan) {
		this.organizationByDivan = organizationByDivan;
	}

	public SysUser getSysUserByCreatorSysUser() {
		return this.sysUserByCreatorSysUser;
	}

	public void setSysUserByCreatorSysUser(SysUser sysUserByCreatorSysUser) {
		this.sysUserByCreatorSysUser = sysUserByCreatorSysUser;
	}

	public Organization getOrganizationByOrganization() {
		return this.organizationByOrganization;
	}

	public void setOrganizationByOrganization(Organization organizationByOrganization) {
		this.organizationByOrganization = organizationByOrganization;
	}

	public PriorityType getPriorityType() {
		return this.priorityType;
	}

	public void setPriorityType(PriorityType priorityType) {
		this.priorityType = priorityType;
	}

	public BoxType getBoxType() {
		return this.boxType;
	}

	public void setBoxType(BoxType boxType) {
		this.boxType = boxType;
	}

	public UserAlias getUserAlias() {
		return this.userAlias;
	}

	public void setUserAlias(UserAlias userAlias) {
		this.userAlias = userAlias;
	}

	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Date getCorrespondenceDate() {
		return this.correspondenceDate;
	}

	public void setCorrespondenceDate(Date correspondenceDate) {
		this.correspondenceDate = correspondenceDate;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public byte[] getSubject() {
		return this.subject;
	}

	public void setSubject(byte[] subject) {
		this.subject = subject;
	}

	public byte[] getHtmlSubject() {
		return this.htmlSubject;
	}

	public void setHtmlSubject(byte[] htmlSubject) {
		this.htmlSubject = htmlSubject;
	}

	public String getFromNumber() {
		return this.fromNumber;
	}

	public void setFromNumber(String fromNumber) {
		this.fromNumber = fromNumber;
	}

	public String getFromEntity() {
		return this.fromEntity;
	}

	public void setFromEntity(String fromEntity) {
		this.fromEntity = fromEntity;
	}

	public Date getFromDate() {
		return this.fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	

	public Date getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
	}

	public Set getCorrespondenceArchives() {
		return this.correspondenceArchives;
	}

	public void setCorrespondenceArchives(Set correspondenceArchives) {
		this.correspondenceArchives = correspondenceArchives;
	}

	public Set getCorrespondenceTransmissions() {
		return this.correspondenceTransmissions;
	}

	public void setCorrespondenceTransmissions(Set correspondenceTransmissions) {
		this.correspondenceTransmissions = correspondenceTransmissions;
	}

	public Set getCorrespondenceNotes() {
		return this.correspondenceNotes;
	}

	public void setCorrespondenceNotes(Set correspondenceNotes) {
		this.correspondenceNotes = correspondenceNotes;
	}

	public Set getNotifications() {
		return this.notifications;
	}

	public void setNotifications(Set notifications) {
		this.notifications = notifications;
	}

	public Set getLinkedCorrespondencesForLinkedCorrespondence() {
		return this.linkedCorrespondencesForLinkedCorrespondence;
	}

	public void setLinkedCorrespondencesForLinkedCorrespondence(Set linkedCorrespondencesForLinkedCorrespondence) {
		this.linkedCorrespondencesForLinkedCorrespondence = linkedCorrespondencesForLinkedCorrespondence;
	}

	public Set getLinkedCorrespondencesForMasterCorrespondence() {
		return this.linkedCorrespondencesForMasterCorrespondence;
	}

	public void setLinkedCorrespondencesForMasterCorrespondence(Set linkedCorrespondencesForMasterCorrespondence) {
		this.linkedCorrespondencesForMasterCorrespondence = linkedCorrespondencesForMasterCorrespondence;
	}

	public Set getCorrespondenceRecipients() {
		return this.correspondenceRecipients;
	}

	public void setCorrespondenceRecipients(Set correspondenceRecipients) {
		this.correspondenceRecipients = correspondenceRecipients;
	}
	

	public Set getCorrespondenceTasks() {
		return correspondenceTasks;
	}

	public void setCorrespondenceTasks(Set correspondenceTasks) {
		this.correspondenceTasks = correspondenceTasks;
	}

	public String getCorrespondenceSeq() {
		if (organizationByDivan != null && organizationByDivan.getCode() != null
				&& !organizationByDivan.getCode().isEmpty())
			return organizationByDivan.getOrganizationCode() + "/" + boxType.getCode() + "/" + String.valueOf(seq);
		else
			return null;
	}
}