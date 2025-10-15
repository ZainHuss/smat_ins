package com.smat.ins.model.dao.impl;


import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.InspectionFormWorkflowDao;

import com.smat.ins.model.entity.InspectionFormWorkflow;


public class InspectionFormWorkflowDaoImpl extends
		GenericDaoImpl<InspectionFormWorkflow, Integer> implements   InspectionFormWorkflowDao {

	@Override
	public InspectionFormWorkflow getCurrentInspectionFormWorkFlow(Long inspectionFormId) {
		// TODO Auto-generated method stub
		Session session = null;
		InspectionFormWorkflow inspectionFormWorkflow = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<InspectionFormWorkflow> criteriaQuery = criteriaBuilder.createQuery(InspectionFormWorkflow.class);
			Root<InspectionFormWorkflow> root = criteriaQuery.from(InspectionFormWorkflow.class);
			root.fetch("equipmentInspectionForm",JoinType.INNER);
			root.fetch("task",JoinType.INNER);
			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(root.get("equipmentInspectionForm").get("id"), inspectionFormId));
			TypedQuery<InspectionFormWorkflow> typedQuery = session.createQuery(criteriaQuery);
			inspectionFormWorkflow = typedQuery.getSingleResult();
			return inspectionFormWorkflow;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return inspectionFormWorkflow;
		}
	}


}
