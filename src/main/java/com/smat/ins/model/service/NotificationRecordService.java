package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.NotificationRecord;

public interface NotificationRecordService extends GenericService<NotificationRecord, Long> {
    List<NotificationRecord> getUnreadByUser(Long userId);
}
