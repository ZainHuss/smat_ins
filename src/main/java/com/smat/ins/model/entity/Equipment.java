package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;


/**
 * Equipment entity. @author MyEclipse Persistence Tools
 */

@Audited
public class Equipment  implements java.io.Serializable {


    // Fields    

     private Integer id;
     private EmpCertificationType empCertificationType;
     private String equipmentName;
     private String description;
     private Set empCertifications = new HashSet(0);


    // Constructors

    /** default constructor */
    public Equipment() {
    }

	/** minimal constructor */
    public Equipment(String equipmentName) {
        this.equipmentName = equipmentName;
    }
    
    /** full constructor */
    public Equipment(EmpCertificationType empCertificationType, String equipmentName, String description, Set empCertifications) {
        this.empCertificationType = empCertificationType;
        this.equipmentName = equipmentName;
        this.description = description;
        this.empCertifications = empCertifications;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public EmpCertificationType getEmpCertificationType() {
        return this.empCertificationType;
    }
    
    public void setEmpCertificationType(EmpCertificationType empCertificationType) {
        this.empCertificationType = empCertificationType;
    }

    public String getEquipmentName() {
        return this.equipmentName;
    }
    
    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public Set getEmpCertifications() {
        return this.empCertifications;
    }
    
    public void setEmpCertifications(Set empCertifications) {
        this.empCertifications = empCertifications;
    }
   








}