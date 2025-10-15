package com.smat.ins.model.entity;

import org.hibernate.envers.Audited;

/**
 * EquipmentInspectionFormItem entity. @author MyEclipse Persistence Tools
 */

@Audited
public class EquipmentInspectionFormItem implements java.io.Serializable {

	// Fields

	private Integer id;
	private GeneralEquipmentItem generalEquipmentItem;
	private EquipmentInspectionForm equipmentInspectionForm;
	private String aliasName;
	private String itemValue;

	// Constructors

	/** default constructor */
	public EquipmentInspectionFormItem() {
	}

	/** minimal constructor */
	public EquipmentInspectionFormItem(GeneralEquipmentItem generalEquipmentItem,
			EquipmentInspectionForm equipmentInspectionForm) {
		this.generalEquipmentItem = generalEquipmentItem;
		this.equipmentInspectionForm = equipmentInspectionForm;
	}

	/** full constructor */
	public EquipmentInspectionFormItem(GeneralEquipmentItem generalEquipmentItem,
			EquipmentInspectionForm equipmentInspectionForm, String aliasName, String itemValue) {
		this.generalEquipmentItem = generalEquipmentItem;
		this.equipmentInspectionForm = equipmentInspectionForm;
		this.aliasName = aliasName;
		this.itemValue = itemValue;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public GeneralEquipmentItem getGeneralEquipmentItem() {
		return this.generalEquipmentItem;
	}

	public void setGeneralEquipmentItem(GeneralEquipmentItem generalEquipmentItem) {
		this.generalEquipmentItem = generalEquipmentItem;
	}

	public EquipmentInspectionForm getEquipmentInspectionForm() {
		return this.equipmentInspectionForm;
	}

	public void setEquipmentInspectionForm(EquipmentInspectionForm equipmentInspectionForm) {
		this.equipmentInspectionForm = equipmentInspectionForm;
	}

	public String getAliasName() {
		return this.aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getItemValue() {
		return this.itemValue;
	}

	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}

}