package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.TaskDraftDao;
import com.smat.ins.model.entity.TaskDraft;

public class TaskDraftDaoImpl extends GenericDaoImpl<TaskDraft, Integer> implements TaskDraftDao {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(TaskDraftDaoImpl.class);

    @Override
    public TaskDraft findByTaskId(Integer taskId) {
        if (taskId == null) return null;
        try {
            Session session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TaskDraft> cq = cb.createQuery(TaskDraft.class);
            Root<TaskDraft> root = cq.from(TaskDraft.class);
            cq.select(root).where(cb.equal(root.get("taskId"), taskId));
            TypedQuery<TaskDraft> q = session.createQuery(cq);
            List<TaskDraft> list = q.getResultList();
            return (list == null || list.isEmpty()) ? null : list.get(0);
        } catch (Exception e) {
            log.error("Error in findByTaskId(" + taskId + ")", e);
            return null;
        }
    }

    @Override
    public TaskDraft findLatestByOwner(Integer ownerId) {
        if (ownerId == null) return null;
        try {
            Session session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TaskDraft> cq = cb.createQuery(TaskDraft.class);
            Root<TaskDraft> root = cq.from(TaskDraft.class);
            cq.select(root).where(cb.equal(root.get("draftOwnerId"), ownerId));
            cq.orderBy(cb.desc(root.get("createdDate")));
            TypedQuery<TaskDraft> q = session.createQuery(cq);
            q.setMaxResults(1);
            List<TaskDraft> list = q.getResultList();
            return (list == null || list.isEmpty()) ? null : list.get(0);
        } catch (Exception e) {
            log.error("Error in findLatestByOwner(" + ownerId + ")", e);
            return null;
        }
    }

    @Override
    public TaskDraft findLatestByOwnerAndType(Integer ownerId, String taskType) {
        if (ownerId == null || taskType == null) return null;
        try {
            Session session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TaskDraft> cq = cb.createQuery(TaskDraft.class);
            Root<TaskDraft> root = cq.from(TaskDraft.class);
            cq.select(root).where(cb.and(cb.equal(root.get("draftOwnerId"), ownerId), cb.equal(root.get("taskType"), taskType)));
            cq.orderBy(cb.desc(root.get("createdDate")));
            TypedQuery<TaskDraft> q = session.createQuery(cq);
            q.setMaxResults(1);
            List<TaskDraft> list = q.getResultList();
            return (list == null || list.isEmpty()) ? null : list.get(0);
        } catch (Exception e) {
            log.error("Error in findLatestByOwnerAndType(" + ownerId + "," + taskType + ")", e);
            return null;
        }
    }

    // Note: GenericDaoImpl usually already provides insert/update/delete/getById
    // If getById in parent throws checked exception, avoid rethrowing here - use try/catch where used.
}
