package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * CorrespondenceTask entity. @author MyEclipse Persistence Tools
 */

@Audited
public class CorrespondenceTask implements java.io.Serializable {

	// Fields

	private Long id;
	private Task task;
	private Correspondence correspondence;

	// Constructors

	/** default constructor */
	public CorrespondenceTask() {
	}

	/** full constructor */
	public CorrespondenceTask(Task task, Correspondence correspondence) {
		this.task = task;
		this.correspondence = correspondence;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Task getTask() {
		return this.task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Correspondence getCorrespondence() {
		return this.correspondence;
	}

	public void setCorrespondence(Correspondence correspondence) {
		this.correspondence = correspondence;
	}

}