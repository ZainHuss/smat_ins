package com.smat.ins.util;

import java.io.File;
import java.io.Serializable;



import org.springframework.core.io.ClassPathResource;







public class AsposeLicenseService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8246093994685654774L;

	public AsposeLicenseService() {
		super();
		// TODO Auto-generated constructor stub
		try {
			com.aspose.pdf.License pdfLicense = new com.aspose.pdf.License();
			
			com.aspose.words.License wordLicense=new com.aspose.words.License();
			
			File filelicense = new ClassPathResource("Aspose.Total.lic").getFile();
			pdfLicense.setLicense(filelicense.getAbsolutePath());
			
			wordLicense.setLicense(filelicense.getAbsolutePath());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public Boolean getIsAsposePdfLicense() {
		return com.aspose.pdf.Document.isLicensed();
	}
	
	
	
	

}
