package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * Sticker entity. @author MyEclipse Persistence Tools
 */

@Audited
public class Sticker implements java.io.Serializable {

	// Fields

	private Long id;
	private SysUser sysUserByDeletedBy;
	private SysUser sysUserByCreatedBy;
	private SysUser sysUserByPrintedBy;
	private SysUser sysUserByForUser;
	private Long seq;
	private String serialNo;
	private Short year;
	private Boolean isPrinted;
	private Boolean isUsed;
	private String stickerNo;
	private Set equipmentInspectionForms = new HashSet(0);

	// Constructors

	/** default constructor */
	public Sticker() {
	}

	/** full constructor */
	public Sticker(SysUser sysUserByDeletedBy, SysUser sysUserByCreatedBy, SysUser sysUserByPrintedBy,
			SysUser sysUserByForUser, Long seq, String serialNo, Short year, Boolean isPrinted, Boolean isUsed,
			String stickerNo, Set equipmentInspectionForms) {
		this.sysUserByDeletedBy = sysUserByDeletedBy;
		this.sysUserByCreatedBy = sysUserByCreatedBy;
		this.sysUserByPrintedBy = sysUserByPrintedBy;
		this.sysUserByForUser = sysUserByForUser;
		this.seq = seq;
		this.serialNo = serialNo;
		this.year = year;
		this.isPrinted = isPrinted;
		this.isUsed = isUsed;
		this.stickerNo = stickerNo;
		this.equipmentInspectionForms = equipmentInspectionForms;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SysUser getSysUserByDeletedBy() {
		return this.sysUserByDeletedBy;
	}

	public void setSysUserByDeletedBy(SysUser sysUserByDeletedBy) {
		this.sysUserByDeletedBy = sysUserByDeletedBy;
	}

	public SysUser getSysUserByCreatedBy() {
		return this.sysUserByCreatedBy;
	}

	public void setSysUserByCreatedBy(SysUser sysUserByCreatedBy) {
		this.sysUserByCreatedBy = sysUserByCreatedBy;
	}

	public SysUser getSysUserByPrintedBy() {
		return this.sysUserByPrintedBy;
	}

	public void setSysUserByPrintedBy(SysUser sysUserByPrintedBy) {
		this.sysUserByPrintedBy = sysUserByPrintedBy;
	}

	public SysUser getSysUserByForUser() {
		return this.sysUserByForUser;
	}

	public void setSysUserByForUser(SysUser sysUserByForUser) {
		this.sysUserByForUser = sysUserByForUser;
	}

	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public String getSerialNo() {
		return this.serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public Short getYear() {
		return this.year;
	}

	public void setYear(Short year) {
		this.year = year;
	}

	public Boolean getIsPrinted() {
		return this.isPrinted;
	}

	public void setIsPrinted(Boolean isPrinted) {
		this.isPrinted = isPrinted;
	}

	public Boolean getIsUsed() {
		return this.isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public String getStickerNo() {
		return this.stickerNo;
	}

	public void setStickerNo(String stickerNo) {
		this.stickerNo = stickerNo;
	}

	public Set getEquipmentInspectionForms() {
		return this.equipmentInspectionForms;
	}

	public void setEquipmentInspectionForms(Set equipmentInspectionForms) {
		this.equipmentInspectionForms = equipmentInspectionForms;
	}
	
	@Override
	public boolean equals(Object other) {

		return (other instanceof Sticker) && (id != null) ? id.equals(((Sticker) other).id)
				: (other == this);
	}

	@Override
	public int hashCode() {
		return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
	}

	@Override
	public String toString() {
		return getStickerNo();
	}

}