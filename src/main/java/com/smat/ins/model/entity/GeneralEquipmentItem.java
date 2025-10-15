package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * GeneralEquipmentItem entity. @author MyEclipse Persistence Tools
 */

@Audited
public class GeneralEquipmentItem implements java.io.Serializable {

	// Fields

	private Integer id;
	private ChecklistDataSource checklistDataSource;
	private ItemType itemType;
	private String itemText;
	private String itemCode;
	private String forItem;
	private Set columnContents = new HashSet(0);
	private Set equipmentInspectionFormItems = new HashSet(0);

	// Constructors

	/** default constructor */
	public GeneralEquipmentItem() {
	}

	/** minimal constructor */
	public GeneralEquipmentItem(ItemType itemType) {
		this.itemType = itemType;
	}

	/** full constructor */
	public GeneralEquipmentItem(ChecklistDataSource checklistDataSource, ItemType itemType, String itemText,
			String itemCode, String forItem, Set columnContents, Set equipmentInspectionFormItems) {
		this.checklistDataSource = checklistDataSource;
		this.itemType = itemType;
		this.itemText = itemText;
		this.itemCode = itemCode;
		this.forItem = forItem;
		this.columnContents = columnContents;
		this.equipmentInspectionFormItems = equipmentInspectionFormItems;
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

	public ItemType getItemType() {
		return this.itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public String getItemText() {
		return this.itemText;
	}

	public void setItemText(String itemText) {
		this.itemText = itemText;
	}

	public String getItemCode() {
		return this.itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getForItem() {
		return this.forItem;
	}

	public void setForItem(String forItem) {
		this.forItem = forItem;
	}

	public Set getColumnContents() {
		return this.columnContents;
	}

	public void setColumnContents(Set columnContents) {
		this.columnContents = columnContents;
	}

	public Set getEquipmentInspectionFormItems() {
		return this.equipmentInspectionFormItems;
	}

	public void setEquipmentInspectionFormItems(Set equipmentInspectionFormItems) {
		this.equipmentInspectionFormItems = equipmentInspectionFormItems;
	}

}