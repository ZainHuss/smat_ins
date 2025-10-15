package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.NotificationStyle;
import com.smat.ins.model.service.NotificationStyleService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingNotificationStyleBean implements Serializable {

	// #region "CodingProperties"
	private static final long serialVersionUID = 4600306662046762257L;
	
	private NotificationStyle notificationStyle;

	private List<NotificationStyle> notificationStyleList;

	private List<NotificationStyle> selectedNotificationStyleList;

	

	private NotificationStyleService notificationStyleService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {

		try {
			notificationStyle = new NotificationStyle();
			notificationStyleList= new ArrayList<NotificationStyle>();
			selectedNotificationStyleList= new ArrayList<NotificationStyle>();
			notificationStyleList=notificationStyleService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}
	
	public CodingNotificationStyleBean() {
		notificationStyleService = (NotificationStyleService) BeanUtility.getBean("notificationStyleService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	}
	
	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.notificationStyle = new NotificationStyle();
	}

	public boolean doValidate() {
		boolean result = true;
		if (notificationStyle.getArabicName() == null || notificationStyle.getArabicName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				notificationStyleService.saveOrUpdate(notificationStyle);
				notificationStyleList=notificationStyleService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationStyle");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationStyle");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedNotificationStyle() {
		return this.selectedNotificationStyleList != null && !this.selectedNotificationStyleList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedNotificationStyle()) {
			int size = this.selectedNotificationStyleList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("notificationStylesSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneNotificationStyleSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deleteNotificationStyle() {
		try {

			notificationStyleService.delete(notificationStyle);
			this.notificationStyle = null;
			notificationStyleList = notificationStyleService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationStyle");
			PrimeFaces.current().executeScript("PF('widgetVarNotificationStyle').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationStyle");
			e.printStackTrace();
		}
	}

	public void deleteSelectedNotificationStyle() {
		try {   
			notificationStyleService.delete(selectedNotificationStyleList);
			this.selectedNotificationStyleList = null;
			notificationStyleList = notificationStyleService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationStyle");
			PrimeFaces.current().executeScript("PF('widgetVarNotificationStyle').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationStyle");
			e.printStackTrace();
		}

	}

	public NotificationStyle getNotificationStyle() {
		return notificationStyle;
	}

	public void setNotificationStyle(NotificationStyle notificationStyle) {
		this.notificationStyle = notificationStyle;
	}

	public List<NotificationStyle> getNotificationStyleList() {
		return notificationStyleList;
	}

	public void setNotificationStyleList(List<NotificationStyle> notificationStyleList) {
		this.notificationStyleList = notificationStyleList;
	}

	public List<NotificationStyle> getSelectedNotificationStyleList() {
		return selectedNotificationStyleList;
	}

	public void setSelectedNotificationStyleList(List<NotificationStyle> selectedNotificationStyleList) {
		this.selectedNotificationStyleList = selectedNotificationStyleList;
	}



	
	
}
