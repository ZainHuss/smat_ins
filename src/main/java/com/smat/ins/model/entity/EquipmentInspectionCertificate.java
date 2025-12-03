package com.smat.ins.model.entity;

import java.util.Date;

import org.hibernate.envers.Audited;

/**
 * EquipmentInspectionCertificate entity. @author MyEclipse Persistence Tools
 */

@Audited
public class EquipmentInspectionCertificate implements java.io.Serializable {

    // Fields

    private Long id;
    private Company company;
    private SysUser sysUserByAllowReprintBy;
    private SysUser sysUserByCreatedBy;
    private SysUser sysUserByReprintBy;
    private EquipmentInspectionForm equipmentInspectionForm;
    private Date issueDate;
    private byte[] certificateDocument;
    private Boolean isPrinted;
    private Date createdDate;
    private Boolean allowReprintCert;

    // Constructors

    /** default constructor */
    public EquipmentInspectionCertificate() {
    }

    /** minimal constructor */
    public EquipmentInspectionCertificate(Company company, SysUser sysUserByCreatedBy,
                                          EquipmentInspectionForm equipmentInspectionForm, Date issueDate, byte[] certificateDocument,
                                          Date createdDate) {
        this.company = company;
        this.sysUserByCreatedBy = sysUserByCreatedBy;
        this.equipmentInspectionForm = equipmentInspectionForm;
        this.issueDate = issueDate;
        this.certificateDocument = certificateDocument;
        this.createdDate = createdDate;
    }

    /** full constructor */
    public EquipmentInspectionCertificate(Company company, SysUser sysUserByAllowReprintBy, SysUser sysUserByCreatedBy,
                                          SysUser sysUserByReprintBy, EquipmentInspectionForm equipmentInspectionForm, Date issueDate,
                                          byte[] certificateDocument, Boolean isPrinted, Date createdDate, Boolean allowReprintCert) {
        this.company = company;
        this.sysUserByAllowReprintBy = sysUserByAllowReprintBy;
        this.sysUserByCreatedBy = sysUserByCreatedBy;
        this.sysUserByReprintBy = sysUserByReprintBy;
        this.equipmentInspectionForm = equipmentInspectionForm;
        this.issueDate = issueDate;
        this.certificateDocument = certificateDocument;
        this.isPrinted = isPrinted;
        this.createdDate = createdDate;
        this.allowReprintCert = allowReprintCert;
    }

    // Property accessors

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public SysUser getSysUserByAllowReprintBy() {
        return this.sysUserByAllowReprintBy;
    }

    public void setSysUserByAllowReprintBy(SysUser sysUserByAllowReprintBy) {
        this.sysUserByAllowReprintBy = sysUserByAllowReprintBy;
    }

    public SysUser getSysUserByCreatedBy() {
        return this.sysUserByCreatedBy;
    }

    public void setSysUserByCreatedBy(SysUser sysUserByCreatedBy) {
        this.sysUserByCreatedBy = sysUserByCreatedBy;
    }

    public SysUser getSysUserByReprintBy() {
        return this.sysUserByReprintBy;
    }

    public void setSysUserByReprintBy(SysUser sysUserByReprintBy) {
        this.sysUserByReprintBy = sysUserByReprintBy;
    }

    public EquipmentInspectionForm getEquipmentInspectionForm() {
        return this.equipmentInspectionForm;
    }

    public void setEquipmentInspectionForm(EquipmentInspectionForm equipmentInspectionForm) {
        this.equipmentInspectionForm = equipmentInspectionForm;
    }

    public Date getIssueDate() {
        return this.issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public byte[] getCertificateDocument() {
        return this.certificateDocument;
    }

    public void setCertificateDocument(byte[] certificateDocument) {
        this.certificateDocument = certificateDocument;
    }

    public Boolean getIsPrinted() {
        return this.isPrinted;
    }

    public void setIsPrinted(Boolean isPrinted) {
        this.isPrinted = isPrinted;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getAllowReprintCert() {
        return this.allowReprintCert;
    }

    public void setAllowReprintCert(Boolean allowReprintCert) {
        this.allowReprintCert = allowReprintCert;
    }

}