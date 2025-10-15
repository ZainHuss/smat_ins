package com.smat.ins.util;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;



public class LocalizationService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3585060837304336545L;

	// define current Localization in the bean
	private Locale currentLocal;
	
	//define current localization code
	private String localCode;
	
	// define direction in xhtml page
	private String xhtmlDirection;

	// resource bundle general properties of application
	private  ResourceBundle applicationProperties;
	private  ResourceBundle infoMessage;
	private  ResourceBundle errorMessage;
	private  ResourceBundle interfaceLabel;
	private  ResourceBundle interfaceLabelAnas;
	
	public LocalizationService() {
		applicationProperties = ResourceBundle.getBundle(
				"com.smat.ins.view.resources.config");
		if (applicationProperties.containsKey("defaultInterfaceLocal")) {
			if (applicationProperties.getString("defaultInterfaceLocal")
					.equals(null)
					|| applicationProperties.getString("defaultInterfaceLocal")
							.isEmpty()) {
				currentLocal = new Locale("en");
				localCode = "en";
				xhtmlDirection="ltr";
				
			} else {
				currentLocal = new Locale(
						applicationProperties
								.getString("defaultInterfaceLocal"));
				localCode = applicationProperties
						.getString("defaultInterfaceLocal");
				xhtmlDirection=applicationProperties.getString("defaultXhtmlDirection");
			}
		} else {
			currentLocal = new Locale("en");
			localCode = "en";
			xhtmlDirection="ltr";
		}
		
		applicationProperties=ResourceBundle.getBundle("com.smat.ins.view.resources.config",currentLocal);
		infoMessage=ResourceBundle.getBundle("com.smat.ins.view.resources.infoMessages", currentLocal);
		errorMessage=ResourceBundle.getBundle("com.smat.ins.view.resources.errorMessages", currentLocal);
		interfaceLabel=ResourceBundle.getBundle("com.smat.ins.view.resources.interface", currentLocal);
		interfaceLabelAnas=ResourceBundle.getBundle("com.smat.ins.view.resources.interface-anas", currentLocal);
		
	}
	
	public void changeLocaliztion(String localCode) {
		currentLocal = new Locale(localCode);
		this.localCode = localCode;
		xhtmlDirection=localCode.equalsIgnoreCase("ar")?"rtl":"ltr";
		infoMessage = ResourceBundle.getBundle("com.smat.ins.view.resources.infoMessages", currentLocal);
		interfaceLabel=ResourceBundle.getBundle("com.smat.ins.view.resources.interface", currentLocal);
		interfaceLabelAnas=ResourceBundle.getBundle("com.smat.ins.view.resources.interface-anas", currentLocal);
		errorMessage=ResourceBundle.getBundle("com.smat.ins.view.resources.errorMessages", currentLocal);
	}
	
	public Locale getCurrentLocal() {
		return currentLocal;
	}
	public void setCurrentLocal(Locale currentLocal) {
		this.currentLocal = currentLocal;
	}

	public String getLocalCode() {
		return localCode;
	}

	public void setLocalCode(String localCode) {
		this.localCode = localCode;
	}

	public String getXhtmlDirection() {
		return xhtmlDirection;
	}

	public void setXhtmlDirection(String xhtmlDirection) {
		this.xhtmlDirection = xhtmlDirection;
	}

	public ResourceBundle getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(ResourceBundle applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public ResourceBundle getInfoMessage() {
		return infoMessage;
	}

	public void setInfoMessage(ResourceBundle infoMessage) {
		this.infoMessage = infoMessage;
	}

	public ResourceBundle getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(ResourceBundle errorMessage) {
		this.errorMessage = errorMessage;
	}


	public ResourceBundle getInterfaceLabel() {
		return interfaceLabel;
	}


	public void setInterfaceLabel(ResourceBundle interfaceLabel) {
		this.interfaceLabel = interfaceLabel;
	}

	public ResourceBundle getInterfaceLabelAnas() {
		return interfaceLabelAnas;
	}

	public void setInterfaceLabelAnas(ResourceBundle interfaceLabelAnas) {
		this.interfaceLabelAnas = interfaceLabelAnas;
	}

}
