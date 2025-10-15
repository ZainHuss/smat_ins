package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * ColumnContent entity. @author MyEclipse Persistence Tools
 */

@Audited
public class ColumnContent implements java.io.Serializable {

	// Fields

	private Integer id;
	private FormColumn formColumn;
	private GeneralEquipmentItem generalEquipmentItem;
	private String aliasName;
	private String contentValue;
	private Short contentOrder;
	private Boolean readOnly;
	private Boolean disabled;
	
	private Boolean isNew;

	// Constructors

	/** default constructor */
	public ColumnContent() {
	}

	/** full constructor */
	public ColumnContent(FormColumn formColumn, GeneralEquipmentItem generalEquipmentItem, String aliasName, String contentValue,Short contentOrder,
			Boolean readOnly, Boolean disabled) {
		this.formColumn = formColumn;
		this.generalEquipmentItem = generalEquipmentItem;
		this.aliasName = aliasName;
		this.contentValue=contentValue;
		this.readOnly = readOnly;
		this.disabled = disabled;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FormColumn getFormColumn() {
		return this.formColumn;
	}

	public void setFormColumn(FormColumn formColumn) {
		this.formColumn = formColumn;
	}

	public GeneralEquipmentItem getGeneralEquipmentItem() {
		return this.generalEquipmentItem;
	}

	public void setGeneralEquipmentItem(GeneralEquipmentItem generalEquipmentItem) {
		this.generalEquipmentItem = generalEquipmentItem;
	}

	public String getAliasName() {
		return this.aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	
	public String getContentValue() {
		return contentValue;
	}

	public void setContentValue(String contentValue) {
		this.contentValue = contentValue;
	}

	public Short getContentOrder() {
		return contentOrder;
	}

	public void setContentOrder(Short contentOrder) {
		this.contentOrder = contentOrder;
	}

	public Boolean getReadOnly() {
		return this.readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Boolean getDisabled() {
		return this.disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	
	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

}