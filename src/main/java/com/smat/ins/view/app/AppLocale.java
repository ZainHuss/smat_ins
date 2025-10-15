package com.smat.ins.view.app;

import java.util.Locale;

public class AppLocale {

	private String name;
	private String code;
	private Locale locale;
	private boolean rtl;

	public AppLocale() {
	}

	public AppLocale(int id, Locale locale) {
		this(locale.getDisplayCountry(), locale.getCountry().toLowerCase(), locale);
	}

	public AppLocale(Locale locale, boolean rtl) {
		this(locale.getDisplayCountry(), locale.getCountry().toLowerCase(), locale);
		this.rtl = rtl;
	}

	public AppLocale(int id, String name, String code) {
		this(name, code, null);
	}

	public AppLocale(String name, String code, Locale locale) {
		this.name = name;
		this.code = code;
		this.locale = locale;
	}
	
	public AppLocale(String name,String code,Locale locale,boolean rtl) {
		this.name=name;
		this.code=code;
		this.locale=locale;
		this.rtl=rtl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getLanguage() {
		return locale == null ? "en" : locale.getLanguage();
	}

	public String getDisplayLanguage() {
		return locale == null ? "English" : locale.getDisplayLanguage();
	}

	public boolean isRtl() {
		return rtl;
	}

	public void setRtl(boolean rtl) {
		this.rtl = rtl;
	}

}
