package com.smat.ins.model.entity.utility;

import java.io.Serializable;

import com.smat.ins.util.UtilityHelper;

public class LanguageOcr implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6207357864761711834L;
	
	private String arabicName;
	
	private String englishName;
	
	private String code;

	public LanguageOcr() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LanguageOcr(String arabicName, String englishName, String code) {
		super();
		this.arabicName = arabicName;
		this.englishName = englishName;
		this.code = code;
	}

	public String getArabicName() {
		return arabicName;
	}

	public void setArabicName(String arabicName) {
		this.arabicName = arabicName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		if (UtilityHelper.getLocale().getLanguage().contains("en"))
			return getEnglishName();
		else
			return getArabicName();
	}
	
	

}
