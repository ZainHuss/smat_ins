package com.smat.ins.model.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.dao.TaskDraftDao;
import com.smat.ins.model.entity.TaskDraft;
import com.smat.ins.model.service.TaskDraftService;

@Service("taskDraftService")
@Transactional
public class TaskDraftServiceImpl extends GenericServiceImpl<TaskDraft, TaskDraftDao, Integer> implements TaskDraftService {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(TaskDraftServiceImpl.class);

    public TaskDraftServiceImpl() {
        super();
    }

    public TaskDraftServiceImpl(TaskDraftDao dao) {
        super();
        this.dao = dao;
    }

    @Override
    public TaskDraft insert(TaskDraft draft) {
        try {
            return dao.insert(draft);
        } catch (Exception e) {
            log.error("Error inserting TaskDraft", e);
            return null;
        }
    }

    @Override
    public TaskDraft saveOrUpdate(TaskDraft draft) {
        try {
            // If draft is linked to a task, update the existing draft for that task (single-draft-per-task)
            if (draft.getTaskId() != null) {
                TaskDraft existingByTask = dao.findByTaskId(draft.getTaskId());
                if (existingByTask != null && existingByTask.getId() != null) {
                    existingByTask.setDraftData(draft.getDraftData());
                    existingByTask.setDraftOwnerId(draft.getDraftOwnerId());
                    existingByTask.setDraftOwnerName(draft.getDraftOwnerName());
                    existingByTask.setTaskType(draft.getTaskType());
                    existingByTask.setVersion((existingByTask.getVersion() == null ? 1 : existingByTask.getVersion()) + 1);
                    existingByTask.setUpdatedDate(new java.util.Date());
                    dao.insertOrupdate(existingByTask);
                    return existingByTask;
                }
            }

            // Otherwise (no task link) always create a new draft record (versioned) and do NOT overwrite previous drafts
            TaskDraft latest = null;
            if (draft.getDraftOwnerId() != null && draft.getTaskType() != null) {
                latest = dao.findLatestByOwnerAndType(draft.getDraftOwnerId(), draft.getTaskType());
            }
            int nextVersion = (latest == null || latest.getVersion() == null) ? 1 : (latest.getVersion() + 1);
            draft.setVersion(nextVersion);
            if (draft.getCreatedDate() == null) draft.setCreatedDate(new java.util.Date());
            draft.setUpdatedDate(new java.util.Date());
            dao.insert(draft);
            return draft;
        } catch (Exception e) {
            log.error("Error saving/updating TaskDraft", e);
            return null;
        }
    }

    @Override
    public TaskDraft findByTaskId(Integer taskId) {
        return dao.findByTaskId(taskId);
    }

    @Override
    public TaskDraft findLatestByOwner(Integer ownerId) {
        return dao.findLatestByOwner(ownerId);
    }

    @Override
    public TaskDraft findLatestByOwnerAndType(Integer ownerId, String taskType) {
        return dao.findLatestByOwnerAndType(ownerId, taskType);
    }
}
