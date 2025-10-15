package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * ChecklistDetailDataSource entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ChecklistDetailDataSource implements java.io.Serializable {

	// Fields

	private Integer id;
	private ChecklistDataSource checklistDataSource;
	private String itemValue;
	private String itemCode;

	// Constructors

	/** default constructor */
	public ChecklistDetailDataSource() {
	}

	/** full constructor */
	public ChecklistDetailDataSource(ChecklistDataSource checklistDataSource, String itemValue, String itemCode) {
		this.checklistDataSource = checklistDataSource;
		this.itemValue = itemValue;
		this.itemCode = itemCode;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ChecklistDataSource getChecklistDataSource() {
		return this.checklistDataSource;
	}

	public void setChecklistDataSource(ChecklistDataSource checklistDataSource) {
		this.checklistDataSource = checklistDataSource;
	}

	public String getItemValue() {
		return this.itemValue;
	}

	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}

	public String getItemCode() {
		return this.itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

}