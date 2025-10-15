package com.smat.ins.view.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.smat.ins.model.entity.NotificationRecord;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.service.NotificationRecordService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.UtilityHelper;

@Named
@ViewScoped
public class NotificationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private NotificationRecordService notificationRecordService;
    private List<NotificationRecord> unreadNotifications;

    @PostConstruct
    public void init() {
        notificationRecordService = (NotificationRecordService) BeanUtility.getBean("notificationRecordService");
        SysUser user = (SysUser) UtilityHelper.getSessionAttr("user");
        if (user != null) {
            unreadNotifications = notificationRecordService.getUnreadByUser(user.getId());
        }
    }


}
