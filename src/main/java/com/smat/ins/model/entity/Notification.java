package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * Notification entity. @author MyEclipse Persistence Tools
 */

@Audited
public class Notification implements java.io.Serializable {

	// Fields

	private Long id;
	private NotificationType notificationType;
	private Correspondence correspondence;
	private String title;
	private String subject;
	private Double notificationPeriod;

	// Constructors

	/** default constructor */
	public Notification() {
	}

	/** full constructor */
	public Notification(NotificationType notificationType, Correspondence correspondence, String title, String subject,
			Double notificationPeriod) {
		this.notificationType = notificationType;
		this.correspondence = correspondence;
		this.title = title;
		this.subject = subject;
		this.notificationPeriod = notificationPeriod;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NotificationType getNotificationType() {
		return this.notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public Correspondence getCorrespondence() {
		return this.correspondence;
	}

	public void setCorrespondence(Correspondence correspondence) {
		this.correspondence = correspondence;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Double getNotificationPeriod() {
		return this.notificationPeriod;
	}

	public void setNotificationPeriod(Double notificationPeriod) {
		this.notificationPeriod = notificationPeriod;
	}

}