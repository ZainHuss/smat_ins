package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.dao.NotificationRecordDao;
import com.smat.ins.model.entity.NotificationRecord;
import com.smat.ins.model.service.NotificationRecordService;

public class NotificationRecordServiceImpl extends GenericServiceImpl<NotificationRecord, NotificationRecordDao, Long>
        implements NotificationRecordService {

    private static final long serialVersionUID = 1L;

    @Override
    public List<NotificationRecord> getUnreadByUser(Long userId) {
        try {
            String hql = "from NotificationRecord nr where nr.user.id = :uid and nr.isRead = false order by nr.createdAt desc";
            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("uid", userId);
            return dao.executeHQLQuery(hql, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
