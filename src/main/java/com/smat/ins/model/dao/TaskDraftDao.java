package com.smat.ins.model.dao;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.TaskDraft;

public interface TaskDraftDao extends GenericDao<TaskDraft, Integer> {
    TaskDraft findByTaskId(Integer taskId);
    TaskDraft findLatestByOwner(Integer ownerId);
    // find latest draft by owner and task type (e.g. inspection_equipment, emp_training)
    TaskDraft findLatestByOwnerAndType(Integer ownerId, String taskType);
}
