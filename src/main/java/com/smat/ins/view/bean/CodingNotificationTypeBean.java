package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.NotificationType;
import com.smat.ins.model.service.NotificationTypeService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingNotificationTypeBean implements Serializable {

	// #region "CodingProperties"
	private static final long serialVersionUID = 4600306662046762257L;
	
	private NotificationType notificationType;

	private List<NotificationType> notificationTypeList;

	private List<NotificationType> selectedNotificationTypeList;

	

	private NotificationTypeService notificationTypeService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {

		try {
			notificationType = new NotificationType();
			notificationTypeList= new ArrayList<NotificationType>();
			selectedNotificationTypeList= new ArrayList<NotificationType>();
			notificationTypeList=notificationTypeService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}
	
	public CodingNotificationTypeBean() {
		notificationTypeService = (NotificationTypeService) BeanUtility.getBean("notificationTypeService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	}
	
	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.notificationType = new NotificationType();
	}

	public boolean doValidate() {
		boolean result = true;
		if (notificationType.getArabicName() == null || notificationType.getArabicName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				notificationTypeService.saveOrUpdate(notificationType);
				notificationTypeList=notificationTypeService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationType");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationType");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedNotificationType() {
		return this.selectedNotificationTypeList != null && !this.selectedNotificationTypeList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedNotificationType()) {
			int size = this.selectedNotificationTypeList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("notificationTypesSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneNotificationTypeSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deleteNotificationType() {
		try {

			notificationTypeService.delete(notificationType);
			this.notificationType = null;
			notificationTypeList = notificationTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationType");
			PrimeFaces.current().executeScript("PF('widgetVarNotificationType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationType");
			e.printStackTrace();
		}
	}

	public void deleteSelectedNotificationType() {
		try {   
			notificationTypeService.delete(selectedNotificationTypeList);
			this.selectedNotificationTypeList = null;
			notificationTypeList = notificationTypeService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationType");
			PrimeFaces.current().executeScript("PF('widgetVarNotificationType').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-notificationType");
			e.printStackTrace();
		}

	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public List<NotificationType> getNotificationTypeList() {
		return notificationTypeList;
	}

	public void setNotificationTypeList(List<NotificationType> notificationTypeList) {
		this.notificationTypeList = notificationTypeList;
	}

	public List<NotificationType> getSelectedNotificationTypeList() {
		return selectedNotificationTypeList;
	}

	public void setSelectedNotificationTypeList(List<NotificationType> selectedNotificationTypeList) {
		this.selectedNotificationTypeList = selectedNotificationTypeList;
	}



	
	
}
