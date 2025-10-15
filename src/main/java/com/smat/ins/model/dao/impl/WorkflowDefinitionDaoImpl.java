package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.WorkflowDefinitionDao;
import com.smat.ins.model.entity.Step;
import com.smat.ins.model.entity.Workflow;
import com.smat.ins.model.entity.WorkflowDefinition;

public class WorkflowDefinitionDaoImpl extends
		GenericDaoImpl<WorkflowDefinition, Integer> implements   WorkflowDefinitionDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1730527225385053169L;

	@Override
	public List<WorkflowDefinition> getByWorkflow(Short workflowId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<WorkflowDefinition> workflowDefinitions = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<WorkflowDefinition> criteriaQuery = criteriaBuilder.createQuery(WorkflowDefinition.class);
			Root<WorkflowDefinition> root = criteriaQuery.from(WorkflowDefinition.class);

			Join<WorkflowDefinition, Workflow> joinWorkflow = root.join("workflow", JoinType.LEFT);
            root.join("workflowDefinitionByPrevious",JoinType.LEFT);
            root.join("workflowDefinitionByNext",JoinType.LEFT);
		    
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(joinWorkflow.get("id"), workflowId));

			TypedQuery<WorkflowDefinition> typedQuery = session.createQuery(criteriaQuery);

			workflowDefinitions = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return workflowDefinitions;
	}

	@Override
	public WorkflowDefinition getInitStep(Short workflowId) {
		// TODO Auto-generated method stub
		Session session = null;
		WorkflowDefinition workflowDefinition = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<WorkflowDefinition> criteriaQuery = criteriaBuilder.createQuery(WorkflowDefinition.class);
			Root<WorkflowDefinition> root = criteriaQuery.from(WorkflowDefinition.class);

			Join<WorkflowDefinition, Workflow> joinWorkflow = root.join("workflow", JoinType.LEFT);
			Join<WorkflowDefinition, WorkflowDefinition> joinWorkflowDefinitionByPrevious=root.join("workflowDefinitionByPrevious",JoinType.LEFT);
			Join<WorkflowDefinition, WorkflowDefinition> joinWorkflowDefinitionByNext= root.join("workflowDefinitionByNext",JoinType.LEFT);
			Join<WorkflowDefinition, Step> joinStep= joinWorkflowDefinitionByNext.join("step",JoinType.LEFT);
		
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(joinWorkflow.get("id"), workflowId),criteriaBuilder.equal(root.get("initialStep"), true));

			TypedQuery<WorkflowDefinition> typedQuery = session.createQuery(criteriaQuery);

			workflowDefinition = typedQuery.getSingleResult();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return workflowDefinition;
	}

	@Override
	public WorkflowDefinition getFinalStep(Short workflowId) {
		// TODO Auto-generated method stub
		Session session = null;
		WorkflowDefinition workflowDefinition = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<WorkflowDefinition> criteriaQuery = criteriaBuilder.createQuery(WorkflowDefinition.class);
			Root<WorkflowDefinition> root = criteriaQuery.from(WorkflowDefinition.class);

			Join<WorkflowDefinition, Workflow> joinWorkflow = root.join("workflow", JoinType.LEFT);
			Join<WorkflowDefinition, WorkflowDefinition> joinWorkflowDefinitionByPrevious=root.join("workflowDefinitionByPrevious",JoinType.LEFT);
			Join<WorkflowDefinition, WorkflowDefinition> joinWorkflowDefinitionByNext= root.join("workflowDefinitionByNext",JoinType.LEFT);
			Join<WorkflowDefinition, Step> joinStep= joinWorkflowDefinitionByNext.join("step",JoinType.LEFT);
		
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(joinWorkflow.get("id"), workflowId),criteriaBuilder.equal(root.get("finalStep"), true));

			TypedQuery<WorkflowDefinition> typedQuery = session.createQuery(criteriaQuery);

			workflowDefinition = typedQuery.getSingleResult();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return workflowDefinition;
	}

}
