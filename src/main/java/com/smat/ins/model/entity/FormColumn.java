package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * FormColumn entity. @author MyEclipse Persistence Tools
 */

@Audited
public class FormColumn implements java.io.Serializable {

	// Fields

	private Integer id;
	private FormRow formRow;
	private Short colSpan;
	private Short rowSpan;
	private String style;
	private String styleClass;
	private Short columnOrder;
	private Set columnContents = new HashSet(0);
	
	private Boolean isNew;

	// Constructors

	/** default constructor */
	public FormColumn() {
	}

	/** minimal constructor */
	public FormColumn(FormRow formRow, Short columnOrder) {
		this.formRow = formRow;
		this.columnOrder = columnOrder;
	}

	/** full constructor */
	public FormColumn(FormRow formRow, Short colSpan, Short rowSpan, String style, String styleClass, Short columnOrder,
			Set columnContents) {
		this.formRow = formRow;
		this.colSpan = colSpan;
		this.rowSpan = rowSpan;
		this.style = style;
		this.styleClass = styleClass;
		this.columnOrder = columnOrder;
		this.columnContents = columnContents;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FormRow getFormRow() {
		return this.formRow;
	}

	public void setFormRow(FormRow formRow) {
		this.formRow = formRow;
	}

	public Short getColSpan() {
		return this.colSpan;
	}

	public void setColSpan(Short colSpan) {
		this.colSpan = colSpan;
	}

	public Short getRowSpan() {
		return this.rowSpan;
	}

	public void setRowSpan(Short rowSpan) {
		this.rowSpan = rowSpan;
	}

	public String getStyle() {
		return this.style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		return this.styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public Short getColumnOrder() {
		return this.columnOrder;
	}

	public void setColumnOrder(Short columnOrder) {
		this.columnOrder = columnOrder;
	}

	public Set getColumnContents() {
		return this.columnContents;
	}

	public void setColumnContents(Set columnContents) {
		this.columnContents = columnContents;
	}
	
	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

}