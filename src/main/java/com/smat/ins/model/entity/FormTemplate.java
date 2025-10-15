package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * FormTemplate entity. @author MyEclipse Persistence Tools
 */

@Audited
public class FormTemplate implements java.io.Serializable {

	// Fields

	 private Integer id;
     private PrintedDoc printedDoc;
     private EquipmentCategory equipmentCategory;
     private SysUser sysUser;
     private String title;
     private String description;
     private Date createdDate;
     private String code;
     private String prefix;
     private Set formRows = new HashSet(0);


    // Constructors

    /** default constructor */
    public FormTemplate() {
    }

	/** minimal constructor */
    public FormTemplate(String title, String code, String prefix) {
        this.title = title;
        this.code = code;
        this.prefix = prefix;
    }
    
    /** full constructor */
    public FormTemplate(PrintedDoc printedDoc, EquipmentCategory equipmentCategory, SysUser sysUser, String title, String description, Date createdDate, String code, String prefix, Set formRows) {
        this.printedDoc = printedDoc;
        this.equipmentCategory = equipmentCategory;
        this.sysUser = sysUser;
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.code = code;
        this.prefix = prefix;
        this.formRows = formRows;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public PrintedDoc getPrintedDoc() {
        return this.printedDoc;
    }
    
    public void setPrintedDoc(PrintedDoc printedDoc) {
        this.printedDoc = printedDoc;
    }

    public EquipmentCategory getEquipmentCategory() {
        return this.equipmentCategory;
    }
    
    public void setEquipmentCategory(EquipmentCategory equipmentCategory) {
        this.equipmentCategory = equipmentCategory;
    }

    public SysUser getSysUser() {
        return this.sysUser;
    }
    
    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

    public String getPrefix() {
        return this.prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Set getFormRows() {
        return this.formRows;
    }
    
    public void setFormRows(Set formRows) {
        this.formRows = formRows;
    }

}