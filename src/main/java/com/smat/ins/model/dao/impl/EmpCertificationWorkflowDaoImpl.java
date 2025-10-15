package com.smat.ins.model.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.EmpCertificationWorkflowDao;
import com.smat.ins.model.entity.EmpCertificationWorkflow;


public class EmpCertificationWorkflowDaoImpl extends GenericDaoImpl<EmpCertificationWorkflow, Integer> implements EmpCertificationWorkflowDao{
	@Override
	public EmpCertificationWorkflow getCurrentInspectionFormWorkFlow(Integer empCertId) {
		// TODO Auto-generated method stub
		Session session = null;
		EmpCertificationWorkflow empCertificationWorkflow = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EmpCertificationWorkflow> criteriaQuery = criteriaBuilder.createQuery(EmpCertificationWorkflow.class);
			Root<EmpCertificationWorkflow> root = criteriaQuery.from(EmpCertificationWorkflow.class);
			root.fetch("empCertification",JoinType.INNER);
			root.fetch("task",JoinType.INNER);
			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(root.get("empCertification").get("id"), empCertId));
			TypedQuery<EmpCertificationWorkflow> typedQuery = session.createQuery(criteriaQuery);
			empCertificationWorkflow = typedQuery.getSingleResult();
			return empCertificationWorkflow;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return empCertificationWorkflow;
		}
	}
}
