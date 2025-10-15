package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;



/**
 * EmpCertificationType entity. @author MyEclipse Persistence Tools
 */
@Audited
public class EmpCertificationType  implements java.io.Serializable {


    // Fields    

     private Integer id;
     private String certName;
     private String description;
     private Integer validityPeriodMonths;
     private Set equipments = new HashSet(0);
     private Set empCertifications = new HashSet(0);


    // Constructors

    /** default constructor */
    public EmpCertificationType() {
    }

	/** minimal constructor */
    public EmpCertificationType(String certName) {
        this.certName = certName;
    }
    
    /** full constructor */
    public EmpCertificationType(String certName, String description, Integer validityPeriodMonths, Set equipments, Set empCertifications) {
        this.certName = certName;
        this.description = description;
        this.validityPeriodMonths = validityPeriodMonths;
        this.equipments = equipments;
        this.empCertifications = empCertifications;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getCertName() {
        return this.certName;
    }
    
    public void setCertName(String certName) {
        this.certName = certName;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getValidityPeriodMonths() {
        return this.validityPeriodMonths;
    }
    
    public void setValidityPeriodMonths(Integer validityPeriodMonths) {
        this.validityPeriodMonths = validityPeriodMonths;
    }

    public Set getEquipments() {
        return this.equipments;
    }
    
    public void setEquipments(Set equipments) {
        this.equipments = equipments;
    }

    public Set getEmpCertifications() {
        return this.empCertifications;
    }
    
    public void setEmpCertifications(Set empCertifications) {
        this.empCertifications = empCertifications;
    }
   
    @Override
	public boolean equals(Object other) {

		return (other instanceof EmpCertificationType) && (id != null) ? id.equals(((EmpCertificationType) other).id)
				: (other == this);
	}

	@Override
	public int hashCode() {
		return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
	}

	@Override
	public String toString() {
		return getCertName();
	}


}