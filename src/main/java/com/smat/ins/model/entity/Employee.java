package com.smat.ins.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;


/**
 * Employee entity. @author MyEclipse Persistence Tools
 */

@Audited
public class Employee  implements java.io.Serializable {


    // Fields    

     private Integer id;
     private Company company;
     private String fullName;
     private String idNumber;
     private String nationality;
     private Date dateOfBirth;
     private byte[] employeePhoto;
     private Set empCertifications = new HashSet(0);


    // Constructors

    /** default constructor */
    public Employee() {
    }

	/** minimal constructor */
    public Employee(String fullName, String idNumber) {
        this.fullName = fullName;
        this.idNumber = idNumber;
    }
    
    /** full constructor */
    public Employee(Company company, String fullName, String idNumber, String nationality, Date dateOfBirth, Set empCertifications) {
        this.company = company;
        this.fullName = fullName;
        this.idNumber = idNumber;
        this.nationality = nationality;
        this.dateOfBirth = dateOfBirth;
        this.empCertifications = empCertifications;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public Company getCompany() {
        return this.company;
    }
    
    public void setCompany(Company company) {
        this.company = company;
    }

    public String getFullName() {
        return this.fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIdNumber() {
        return this.idNumber;
    }
    
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getNationality() {
        return this.nationality;
    }
    
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    

    public byte[] getEmployeePhoto() {
		return employeePhoto;
	}

	public void setEmployeePhoto(byte[] employeePhoto) {
		this.employeePhoto = employeePhoto;
	}

	public Set getEmpCertifications() {
        return this.empCertifications;
    }
    
    public void setEmpCertifications(Set empCertifications) {
        this.empCertifications = empCertifications;
    }
   


}