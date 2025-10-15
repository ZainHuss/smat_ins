package com.smat.ins.model.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.envers.Audited;


/**
 * PrintedDoc entity. @author MyEclipse Persistence Tools
 */

@Audited
public class PrintedDoc  implements java.io.Serializable {


    // Fields    

     private Integer id;
     private String name;
     private String extension;
     private String mimeType;
     private Long fileSize;
     private byte[] data;
     private Set formTemplates = new HashSet(0);


    // Constructors

    /** default constructor */
    public PrintedDoc() {
    }

    
    /** full constructor */
    public PrintedDoc(String name, String extension, String mimeType, Long fileSize, byte[] data, Set formTemplates) {
        this.name = name;
        this.extension = extension;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.data = data;
        this.formTemplates = formTemplates;
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

    public String getExtension() {
        return this.extension;
    }
    
    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getMimeType() {
        return this.mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return this.fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getData() {
        return this.data;
    }
    
    public void setData(byte[] data) {
        this.data = data;
    }

    public Set getFormTemplates() {
        return this.formTemplates;
    }
    
    public void setFormTemplates(Set formTemplates) {
        this.formTemplates = formTemplates;
    }

}