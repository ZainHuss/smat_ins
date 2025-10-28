package com.smat.ins.model.service;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.TaskDraft;

public interface TaskDraftService extends GenericService<TaskDraft, Integer> {
    TaskDraft insert(TaskDraft draft);
    TaskDraft findByTaskId(Integer taskId);
    TaskDraft findLatestByOwner(Integer ownerId);
    TaskDraft saveOrUpdate(TaskDraft draft);
    TaskDraft findLatestByOwnerAndType(Integer ownerId, String taskType);
}
