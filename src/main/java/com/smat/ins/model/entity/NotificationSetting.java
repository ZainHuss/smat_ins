package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * NotificationSetting entity. @author MyEclipse Persistence Tools
 */

@Audited
public class NotificationSetting implements java.io.Serializable {

	// Fields

	private Short id;
	private NotificationStyle notificationStyle;
	private PeriodType periodType;

	// Constructors

	/** default constructor */
	public NotificationSetting() {
	}

	/** full constructor */
	public NotificationSetting(NotificationStyle notificationStyle, PeriodType periodType) {
		this.notificationStyle = notificationStyle;
		this.periodType = periodType;
	}

	// Property accessors

	public Short getId() {
		return this.id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public NotificationStyle getNotificationStyle() {
		return this.notificationStyle;
	}

	public void setNotificationStyle(NotificationStyle notificationStyle) {
		this.notificationStyle = notificationStyle;
	}

	public PeriodType getPeriodType() {
		return this.periodType;
	}

	public void setPeriodType(PeriodType periodType) {
		this.periodType = periodType;
	}

}