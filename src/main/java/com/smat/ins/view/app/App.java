/*
 * Copyright 2009-2021 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smat.ins.view.app;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;

import java.io.Serializable;

@Named
@SessionScoped
public class App implements Serializable {

	private static final long serialVersionUID = 1L;
	private String theme = "saga";
	private boolean darkMode = false;
	private String inputStyle = "outlined";
	private AppLocale locale;


	private LocalizationService localizationService;

	public String getTheme() {
		return theme;
	}

	public boolean isDarkMode() {
		return darkMode;
	}

	public void setDarkMode(boolean darkMode) {
		this.darkMode = darkMode;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getInputStyle() {
		return inputStyle;
	}

	public void setInputStyle(String inputStyle) {
		this.inputStyle = inputStyle;
	}

	public String getInputStyleClass() {
		return this.inputStyle.equals("filled") ? "ui-input-filled" : "";
	}

	public AppLocale getLocale() {
		return locale;
	}

	public void setLocale(AppLocale locale) {
		this.locale = locale;
	}
	
	

	

	public App() {
		super();
		// TODO Auto-generated constructor stub

	}

	@PostConstruct
	public void init() {
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
		locale = new AppLocale(localizationService.getLocalCode().equalsIgnoreCase("ar") ? "Arabic" : "English",
				localizationService.getLocalCode().equalsIgnoreCase("ar") ? "ar" : "en",
				localizationService.getCurrentLocal(),
				localizationService.getXhtmlDirection().equalsIgnoreCase("rtl") ? true : false);
	}

	public void changeTheme(String theme, boolean darkMode) {
		this.theme = theme;
		this.darkMode = darkMode;
	}

	public void changeLocal(String localeCode) {
		localizationService.changeLocaliztion(localeCode);
		locale = new AppLocale(localizationService.getLocalCode().equalsIgnoreCase("ar") ? "Arabic" : "English",
				localizationService.getLocalCode().equalsIgnoreCase("ar") ? "ar" : "en",
				localizationService.getCurrentLocal(),
				localizationService.getXhtmlDirection().equalsIgnoreCase("rtl") ? true : false);
	}

	public String navigate(String url) {
		return url + "?faces-redirect=true";
	}

}
