package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

/**
 * EquipmentInspectionForm entity. @author MyEclipse Persistence Tools
 */

@Audited
public class EquipmentInspectionForm implements java.io.Serializable {

	// Fields

	private Long id;
	private Company company;
	private Sticker sticker;
	private EquipmentType equipmentType;
	private SysUser sysUserByReviewedBy;
	private EquipmentCategory equipmentCategory;
	private ExaminationType examinationType;
	private SysUser sysUserByInspectionBy;
	private SysUser sysUserByAssignerBy;
	private String reportNo;
	private String timeSheetNo;
	private String jobNo;
	private String stickerNo;
	private String nameAndAddressOfEmployer;
	private Date dateOfThoroughExamination;
	private Date nextExaminationDate;
	private Date previousExaminationDate;
	private Set equipmentInspectionFormItems = new HashSet(0);
	private Set inspectionFormWorkflows = new HashSet(0);
	private Set inspectionFormWorkflowSteps = new HashSet(0);
	private Set equipmentInspectionCertificates = new HashSet(0);

	// Constructors

	/** default constructor */
	public EquipmentInspectionForm() {
	}

	/** full constructor */
	public EquipmentInspectionForm(Company company, Sticker sticker, EquipmentType equipmentType,
			SysUser sysUserByReviewedBy, EquipmentCategory equipmentCategory, ExaminationType examinationType,
			SysUser sysUserByInspectionBy,SysUser sysUserByAssignerBy, String reportNo, String timeSheetNo, String jobNo, String stickerNo,
			String nameAndAddressOfEmployer, Date dateOfThoroughExamination, Date nextExaminationDate,
			Date previousExaminationDate, Set equipmentInspectionFormItems, Set inspectionFormWorkflows,
			Set inspectionFormWorkflowSteps, Set equipmentInspectionCertificates) {
		this.company = company;
		this.sticker = sticker;
		this.equipmentType = equipmentType;
		this.sysUserByReviewedBy = sysUserByReviewedBy;
		this.equipmentCategory = equipmentCategory;
		this.examinationType = examinationType;
		this.sysUserByInspectionBy = sysUserByInspectionBy;
		this.sysUserByAssignerBy = sysUserByAssignerBy;
		this.reportNo = reportNo;
		this.timeSheetNo = timeSheetNo;
		this.jobNo = jobNo;
		this.stickerNo = stickerNo;
		this.nameAndAddressOfEmployer = nameAndAddressOfEmployer;
		this.dateOfThoroughExamination = dateOfThoroughExamination;
		this.nextExaminationDate = nextExaminationDate;
		this.previousExaminationDate = previousExaminationDate;
		this.equipmentInspectionFormItems = equipmentInspectionFormItems;
		this.inspectionFormWorkflows = inspectionFormWorkflows;
		this.inspectionFormWorkflowSteps = inspectionFormWorkflowSteps;
		this.equipmentInspectionCertificates = equipmentInspectionCertificates;
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

	public Sticker getSticker() {
		return this.sticker;
	}

	public void setSticker(Sticker sticker) {
		this.sticker = sticker;
	}

	public EquipmentType getEquipmentType() {
		return this.equipmentType;
	}

	public void setEquipmentType(EquipmentType equipmentType) {
		this.equipmentType = equipmentType;
	}

	public SysUser getSysUserByReviewedBy() {
		return this.sysUserByReviewedBy;
	}

	public void setSysUserByReviewedBy(SysUser sysUserByReviewedBy) {
		this.sysUserByReviewedBy = sysUserByReviewedBy;
	}

	public EquipmentCategory getEquipmentCategory() {
		return this.equipmentCategory;
	}

	public void setEquipmentCategory(EquipmentCategory equipmentCategory) {
		this.equipmentCategory = equipmentCategory;
	}

	public ExaminationType getExaminationType() {
		return this.examinationType;
	}

	public void setExaminationType(ExaminationType examinationType) {
		this.examinationType = examinationType;
	}

	public SysUser getSysUserByInspectionBy() {
		return this.sysUserByInspectionBy;
	}

	public SysUser getsysUserByAssignerBy() {
		return this.sysUserByAssignerBy;
	}
	public void setSysUserByInspectionBy(SysUser sysUserByInspectionBy) {
		this.sysUserByInspectionBy = sysUserByInspectionBy;
	}

    public void setsysUserByAssignerBy(SysUser sysUserByAssignerBy) {
        this.sysUserByAssignerBy = sysUserByAssignerBy;
    }


	public String getReportNo() {
		return this.reportNo;
	}

	public void setReportNo(String reportNo) {
		this.reportNo = reportNo;
	}

	public String getTimeSheetNo() {
		return this.timeSheetNo;
	}

	public void setTimeSheetNo(String timeSheetNo) {
		this.timeSheetNo = timeSheetNo;
	}

	public String getJobNo() {
		return this.jobNo;
	}

	public void setJobNo(String jobNo) {
		this.jobNo = jobNo;
	}

	public String getStickerNo() {
		return this.stickerNo;
	}

	public void setStickerNo(String stickerNo) {
		this.stickerNo = stickerNo;
	}

	public String getNameAndAddressOfEmployer() {
		return this.nameAndAddressOfEmployer;
	}

	public void setNameAndAddressOfEmployer(String nameAndAddressOfEmployer) {
		this.nameAndAddressOfEmployer = nameAndAddressOfEmployer;
	}

	public Date getDateOfThoroughExamination() {
		return this.dateOfThoroughExamination;
	}

	public void setDateOfThoroughExamination(Date dateOfThoroughExamination) {
		this.dateOfThoroughExamination = dateOfThoroughExamination;
	}

	public Date getNextExaminationDate() {
		return this.nextExaminationDate;
	}

	public void setNextExaminationDate(Date nextExaminationDate) {
		this.nextExaminationDate = nextExaminationDate;
	}

	public Date getPreviousExaminationDate() {
		return this.previousExaminationDate;
	}

	public void setPreviousExaminationDate(Date previousExaminationDate) {
		this.previousExaminationDate = previousExaminationDate;
	}

	public Set getEquipmentInspectionFormItems() {
		return this.equipmentInspectionFormItems;
	}

	public void setEquipmentInspectionFormItems(Set equipmentInspectionFormItems) {
		this.equipmentInspectionFormItems = equipmentInspectionFormItems;
	}

	public Set getInspectionFormWorkflows() {
		return this.inspectionFormWorkflows;
	}

	public void setInspectionFormWorkflows(Set inspectionFormWorkflows) {
		this.inspectionFormWorkflows = inspectionFormWorkflows;
	}

	public Set getInspectionFormWorkflowSteps() {
		return this.inspectionFormWorkflowSteps;
	}

	public void setInspectionFormWorkflowSteps(Set inspectionFormWorkflowSteps) {
		this.inspectionFormWorkflowSteps = inspectionFormWorkflowSteps;
	}

	public Set getEquipmentInspectionCertificates() {
		return this.equipmentInspectionCertificates;
	}

	public void setEquipmentInspectionCertificates(Set equipmentInspectionCertificates) {
		this.equipmentInspectionCertificates = equipmentInspectionCertificates;
	}

}