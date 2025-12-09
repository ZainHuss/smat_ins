package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;

import com.smat.ins.util.UtilityHelper;

/**
 * Company entity. @author MyEclipse Persistence Tools
 */

@Audited
public class Company implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -3506455891596395935L;
	private Integer id;
	private String name;
	private String phone;
	private String mobile;
	private String email;
	private String code;
	private String address;
	private String contactPerson;
	private String details;
	private Set equipmentInspectionCertificates = new HashSet(0);
	private Set equipmentInspectionForms = new HashSet(0);
	private Set employees = new HashSet(0);
	private Set tasks = new HashSet(0);

	// Constructors

	/** default constructor */
	public Company() {
	}

	/** minimal constructor */
	public Company(String code) {
		this.code = code;
	}

	/** full constructor */
	public Company(String name, String phone, String mobile, String code, String address,String contactPerson,String details,
			Set equipmentInspectionCertificates, Set equipmentInspectionForms, Set employees, Set tasks) {
		this.name = name;
		this.phone = phone;
		this.mobile = mobile;
		this.code = code;
		this.address = address;
		this.contactPerson=contactPerson;
		this.details=details;
		this.equipmentInspectionCertificates = equipmentInspectionCertificates;
		this.equipmentInspectionForms = equipmentInspectionForms;
		this.employees = employees;
		this.tasks = tasks;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Set getEquipmentInspectionCertificates() {
		return this.equipmentInspectionCertificates;
	}

	public void setEquipmentInspectionCertificates(Set equipmentInspectionCertificates) {
		this.equipmentInspectionCertificates = equipmentInspectionCertificates;
	}

	public Set getEquipmentInspectionForms() {
		return this.equipmentInspectionForms;
	}

	public void setEquipmentInspectionForms(Set equipmentInspectionForms) {
		this.equipmentInspectionForms = equipmentInspectionForms;
	}

	public Set getEmployees() {
		return this.employees;
	}

	public void setEmployees(Set employees) {
		this.employees = employees;
	}

	public Set getTasks() {
		return this.tasks;
	}

	public void setTasks(Set tasks) {
		this.tasks = tasks;
	}

	@Override
	public boolean equals(Object other) {

		return (other instanceof Company) && (id != null) ? id.equals(((Company) other).id) : (other == this);
	}

	@Override
	public int hashCode() {
		return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}

}