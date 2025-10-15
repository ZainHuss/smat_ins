package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * FormRow entity. @author MyEclipse Persistence Tools
 */

@Audited
public class FormRow implements java.io.Serializable {

	// Fields

	private Integer id;
	private FormTemplate formTemplate;
	private Boolean isHeader;
	private Boolean isFooter;
	private Short rowNum;
	private Set formColumns = new HashSet(0);
	
	private Boolean isNew;

	// Constructors

	/** default constructor */
	public FormRow() {
	}

	/** minimal constructor */
	public FormRow(FormTemplate formTemplate) {
		this.formTemplate = formTemplate;
	}

	/** full constructor */
	public FormRow(FormTemplate formTemplate, Boolean isHeader, Boolean isFooter, Short rowNum, Set formColumns) {
		this.formTemplate = formTemplate;
		this.isHeader = isHeader;
		this.isFooter = isFooter;
		this.rowNum = rowNum;
		this.formColumns = formColumns;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FormTemplate getFormTemplate() {
		return this.formTemplate;
	}

	public void setFormTemplate(FormTemplate formTemplate) {
		this.formTemplate = formTemplate;
	}

	public Boolean getIsHeader() {
		return this.isHeader;
	}

	public void setIsHeader(Boolean isHeader) {
		this.isHeader = isHeader;
	}

	public Boolean getIsFooter() {
		return this.isFooter;
	}

	public void setIsFooter(Boolean isFooter) {
		this.isFooter = isFooter;
	}

	public Short getRowNum() {
		return this.rowNum;
	}

	public void setRowNum(Short rowNum) {
		this.rowNum = rowNum;
	}

	public Set getFormColumns() {
		return this.formColumns;
	}

	public void setFormColumns(Set formColumns) {
		this.formColumns = formColumns;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}
	
	

}