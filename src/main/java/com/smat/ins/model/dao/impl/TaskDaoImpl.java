package com.smat.ins.model.dao.impl;

import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.query.Query; // للاستخدام مع Hibernate 5.2+
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import org.hibernate.query.Query;
import com.smat.ins.model.dao.TaskDao;
import com.smat.ins.model.entity.Correspondence;

import com.smat.ins.model.entity.CorrespondenceRecipient;
import com.smat.ins.model.entity.CorrespondenceTask;
import com.smat.ins.model.entity.EmpCertificationWorkflow;
import com.smat.ins.model.entity.EquipmentCategory;
import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.InspectionFormWorkflow;
import com.smat.ins.model.entity.Step;
import com.smat.ins.model.entity.Task;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.entity.WorkflowDefinition;

public class TaskDaoImpl extends GenericDaoImpl<Task, Integer> implements TaskDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5962996104804247943L;


    @Override
    public void updateTaskStatus(int taskId, int status) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            
            // استخدام HQL لتحديث الحقل مباشرة
            String hql = "UPDATE Task t SET t.is_active = :status WHERE t.id = :taskId";
            Query<?> query = session.createQuery(hql);
            query.setParameter("status", status == 1);
            query.setParameter("taskId", taskId);
            
            int result = query.executeUpdate();
            
            if (result == 0) {
                log.warn("No task found with ID: " + taskId);
            } else {
                log.info("Task status updated successfully. Task ID: " + taskId + ", New status: " + (status == 1));
            }
            
        } catch (Exception e) {
            log.error("Error updating task status for task ID: " + taskId, e);
            throw new RuntimeException("Error updating task status: " + e.getMessage(), e);
        }
    }

    // ... (بقية الأساليب الحالية) ...

   @Override
    public boolean update(Task task) {
        getCurrentSession().update(task);
        return true;
    }
	@Override
	public List<Task> getListInitialTaskByCorrespondence(Long correspondenceId, Long correspondenceRecipientId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Task> tasks = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
			Root<Task> root = criteriaQuery.from(Task.class);

			root.join("equipmentCategory", JoinType.INNER);

			Join<Task, CorrespondenceTask> joinCorrespondenceTask = root.join("correspondenceTasks", JoinType.INNER);

			Join<CorrespondenceTask, Correspondence> joinCorrespondence = joinCorrespondenceTask.join("correspondence",
					JoinType.INNER);
			Join<Task, InspectionFormWorkflow> joinInspectionFormWorkflow = root.join("inspectionFormWorkflows",
					JoinType.LEFT);
			Join<InspectionFormWorkflow, WorkflowDefinition> joinWorkflowDefinition = joinInspectionFormWorkflow
					.join("workflowDefinition", JoinType.INNER);
			criteriaQuery.select(root).distinct(true);
			Predicate taskIdIsNull = criteriaBuilder.isNull(joinInspectionFormWorkflow.get("task").get("id"));
			Predicate stepInspector = criteriaBuilder.equal(joinWorkflowDefinition.get("step").get("code"), "01");
			Predicate finalPredicate = criteriaBuilder.or(taskIdIsNull, stepInspector);

			criteriaQuery.select(root).distinct(true);

			criteriaQuery.where(criteriaBuilder.equal(root.get("serviceType"),1),criteriaBuilder.equal(joinCorrespondence.get("id"), correspondenceId),
					criteriaBuilder.equal(root.get("userAliasByAssignTo").get("id"), correspondenceRecipientId),
					finalPredicate);

			TypedQuery<Task> typedQuery = session.createQuery(criteriaQuery);

			tasks = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return tasks;
	}

	@Override
	public List<Task> getListIntialTaskByRecepient(Long correspondenceRecipientId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Task> tasks = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
			Root<Task> root = criteriaQuery.from(Task.class);
			Join<Task, InspectionFormWorkflow> joinInspectionFormWorkflow = root.join("inspectionFormWorkflows",
					JoinType.LEFT);
			Join<InspectionFormWorkflow, WorkflowDefinition> joinWorkflowDefinition = joinInspectionFormWorkflow
					.join("workflowDefinition", JoinType.LEFT);
			Join<WorkflowDefinition, Step> joinStep = joinWorkflowDefinition.join("step", JoinType.LEFT);
			criteriaQuery.select(root).distinct(true);
			Predicate taskIdIsNull = criteriaBuilder.isNull(joinInspectionFormWorkflow.get("task").get("id"));
			Predicate stepInspector = criteriaBuilder.equal(joinStep.get("code"), "01");
			Predicate finalPredicate = criteriaBuilder.or(taskIdIsNull, stepInspector);
			criteriaQuery.where(criteriaBuilder.equal(root.get("serviceType"),1),
					criteriaBuilder.equal(root.get("userAliasByAssignTo").get("id"), correspondenceRecipientId),
					finalPredicate);

			TypedQuery<Task> typedQuery = session.createQuery(criteriaQuery);

			tasks = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return tasks;
	}

	@Override
	public List<Task> getListReviewdTaskByCorrespondence(Long correspondenceId, Long reviewedUserId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Task> tasks = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
			Root<Task> root = criteriaQuery.from(Task.class);

			root.join("equipmentCategory", JoinType.INNER);

			Join<Task, CorrespondenceTask> joinCorrespondenceTask = root.join("correspondenceTasks", JoinType.INNER);

			Join<CorrespondenceTask, Correspondence> joinCorrespondence = joinCorrespondenceTask.join("correspondence",
					JoinType.INNER);

			Join<Task, InspectionFormWorkflow> joinInspectionFormWorkflow = root.join("inspectionFormWorkflows",
					JoinType.INNER);
			Join<InspectionFormWorkflow, WorkflowDefinition> joinWorkflowDefinition = joinInspectionFormWorkflow
					.join("workflowDefinition", JoinType.INNER);
			Join<WorkflowDefinition, Step> joinStep = joinWorkflowDefinition.join("step", JoinType.LEFT);

			criteriaQuery.select(root).distinct(true);

			criteriaQuery.where(criteriaBuilder.equal(root.get("serviceType"),1),criteriaBuilder.equal(joinCorrespondence.get("id"), correspondenceId),
					criteriaBuilder.equal(joinStep.get("code"), "02"),
					criteriaBuilder.equal(joinInspectionFormWorkflow.get("reviewedBy").get("id"), reviewedUserId));

			TypedQuery<Task> typedQuery = session.createQuery(criteriaQuery);

			tasks = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return tasks;
	}

	@Override
	public List<Task> getListReviewedTask(Long reviewedUserId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Task> tasks = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
			Root<Task> root = criteriaQuery.from(Task.class);

			root.join("equipmentCategory", JoinType.INNER);

			Join<Task, InspectionFormWorkflow> joinInspectionFormWorkflow = root.join("inspectionFormWorkflows",
					JoinType.INNER);
			Join<InspectionFormWorkflow, WorkflowDefinition> joinWorkflowDefinition = joinInspectionFormWorkflow
					.join("workflowDefinition", JoinType.INNER);
			criteriaQuery.select(root).distinct(true);

			criteriaQuery.where(criteriaBuilder.equal(root.get("serviceType"),1),criteriaBuilder.equal(joinWorkflowDefinition.get("step").get("code"), "02"),
					criteriaBuilder.equal(joinInspectionFormWorkflow.get("reviewedBy").get("id"), reviewedUserId));

			TypedQuery<Task> typedQuery = session.createQuery(criteriaQuery);

			tasks = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return tasks;
	}

	@Override
	public List<Task> searchTasks(String description, Boolean isCompleted, Boolean isDone, Date fromDate, Date toDate,
			Long assigneeId) {
		Session session = null;
		List<Task> tasks = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Task> cq = cb.createQuery(Task.class);
			Root<Task> root = cq.from(Task.class);

			List<Predicate> predicates = new ArrayList<>();

			if (description != null && !description.isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("taskDescription")), "%" + description.toLowerCase() + "%"));
			}

			if (isCompleted != null) {
				predicates.add(cb.equal(root.get("isCompleted"), isCompleted));
			}

			if (isDone != null) {
				predicates.add(cb.equal(root.get("isDone"), isDone));
			}

			if (fromDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), fromDate));
			}

			if (toDate != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("createdDate"), toDate));
			}

			if (assigneeId != null) {
				Join<Task, UserAlias> assigneeJoin = root.join("userAliasByAssignTo", JoinType.INNER);
				predicates.add(cb.equal(assigneeJoin.get("id"), assigneeId));
			}

			cq.where(predicates.toArray(new Predicate[0]));
			cq.orderBy(cb.desc(root.get("createdDate")));

			tasks = session.createQuery(cq).getResultList();
		} catch (Exception e) {
			log.error("Error searching tasks", e);
		}
		return tasks;
	}

	@Override
	public List<Task> getListInitialEmpTaskByCorrespondence(Long correspondenceId, Long correspondenceRecipientId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Task> tasks = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
			Root<Task> root = criteriaQuery.from(Task.class);


			Join<Task, CorrespondenceTask> joinCorrespondenceTask = root.join("correspondenceTasks", JoinType.INNER);

			Join<CorrespondenceTask, Correspondence> joinCorrespondence = joinCorrespondenceTask.join("correspondence",
					JoinType.INNER);
			Join<Task, EmpCertificationWorkflow> joinEmpCertificationWorkflow = root.join("empCertificationWorkflows",
					JoinType.LEFT);
			Join<EmpCertificationWorkflow, WorkflowDefinition> joinWorkflowDefinition = joinEmpCertificationWorkflow
					.join("workflowDefinition", JoinType.INNER);
			criteriaQuery.select(root).distinct(true);
			Predicate taskIdIsNull = criteriaBuilder.isNull(joinEmpCertificationWorkflow.get("task").get("id"));
			Predicate stepInspector = criteriaBuilder.equal(joinWorkflowDefinition.get("step").get("code"), "01");
			Predicate finalPredicate = criteriaBuilder.or(taskIdIsNull, stepInspector);

			criteriaQuery.select(root).distinct(true);

			criteriaQuery.where(criteriaBuilder.equal(root.get("serviceType"),2),criteriaBuilder.equal(joinCorrespondence.get("id"), correspondenceId),
					criteriaBuilder.equal(root.get("userAliasByAssignTo").get("id"), correspondenceRecipientId),
					finalPredicate);

			TypedQuery<Task> typedQuery = session.createQuery(criteriaQuery);

			tasks = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return tasks;
	}

	@Override
	public List<Task> getListIntialEmpTaskByRecepient(Long correspondenceRecipientId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Task> tasks = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
			Root<Task> root = criteriaQuery.from(Task.class);
			Join<Task, EmpCertificationWorkflow> joinEmpCertificationWorkflow = root.join("empCertificationWorkflows",
					JoinType.LEFT);
			Join<EmpCertificationWorkflow, WorkflowDefinition> joinWorkflowDefinition = joinEmpCertificationWorkflow
					.join("workflowDefinition", JoinType.LEFT);
			Join<WorkflowDefinition, Step> joinStep = joinWorkflowDefinition.join("step", JoinType.LEFT);
			criteriaQuery.select(root).distinct(true);
			Predicate taskIdIsNull = criteriaBuilder.isNull(joinEmpCertificationWorkflow.get("task").get("id"));
			Predicate stepInspector = criteriaBuilder.equal(joinStep.get("code"), "01");
			Predicate finalPredicate = criteriaBuilder.or(taskIdIsNull, stepInspector);
			criteriaQuery.where(criteriaBuilder.equal(root.get("serviceType"),2),
					criteriaBuilder.equal(root.get("userAliasByAssignTo").get("id"), correspondenceRecipientId),
					finalPredicate);

			TypedQuery<Task> typedQuery = session.createQuery(criteriaQuery);

			tasks = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return tasks;
	}

	@Override
	public List<Task> getListReviewdEmpTaskByCorrespondence(Long correspondenceId, Long reviewedUserId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Task> tasks = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
			Root<Task> root = criteriaQuery.from(Task.class);


			Join<Task, CorrespondenceTask> joinCorrespondenceTask = root.join("correspondenceTasks", JoinType.INNER);

			Join<CorrespondenceTask, Correspondence> joinCorrespondence = joinCorrespondenceTask.join("correspondence",
					JoinType.INNER);

			Join<Task, EmpCertificationWorkflow> joinEmpCertificationWorkflow = root.join("empCertificationWorkflows",
					JoinType.INNER);
			Join<EmpCertificationWorkflow, WorkflowDefinition> joinWorkflowDefinition = joinEmpCertificationWorkflow
					.join("workflowDefinition", JoinType.INNER);
			Join<WorkflowDefinition, Step> joinStep = joinWorkflowDefinition.join("step", JoinType.LEFT);

			criteriaQuery.select(root).distinct(true);

			criteriaQuery.where(criteriaBuilder.equal(root.get("serviceType"),2),criteriaBuilder.equal(joinCorrespondence.get("id"), correspondenceId),
					criteriaBuilder.equal(joinStep.get("code"), "02"),
					criteriaBuilder.equal(joinEmpCertificationWorkflow.get("sysUser").get("id"), reviewedUserId));

			TypedQuery<Task> typedQuery = session.createQuery(criteriaQuery);

			tasks = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return tasks;
	}

	@Override
	public List<Task> getListReviewedEmpTask(Long reviewedUserId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Task> tasks = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
			Root<Task> root = criteriaQuery.from(Task.class);

			Join<Task, EmpCertificationWorkflow> joinEmpCertificationWorkflow = root.join("empCertificationWorkflows",
					JoinType.INNER);
			Join<InspectionFormWorkflow, WorkflowDefinition> joinWorkflowDefinition = joinEmpCertificationWorkflow
					.join("workflowDefinition", JoinType.INNER);
			criteriaQuery.select(root).distinct(true);

			criteriaQuery.where(criteriaBuilder.equal(root.get("serviceType"),2),criteriaBuilder.equal(joinWorkflowDefinition.get("step").get("code"), "02"),
					criteriaBuilder.equal(joinEmpCertificationWorkflow.get("sysUser").get("id"), reviewedUserId));

			TypedQuery<Task> typedQuery = session.createQuery(criteriaQuery);

			tasks = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return tasks;
	}

	
	

}
