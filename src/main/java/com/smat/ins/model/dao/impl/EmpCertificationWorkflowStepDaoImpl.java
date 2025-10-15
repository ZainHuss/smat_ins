package com.smat.ins.model.dao.impl;


import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.EmpCertificationWorkflowStepDao;
import com.smat.ins.model.entity.EmpCertification;
import com.smat.ins.model.entity.EmpCertificationWorkflowStep;



public class EmpCertificationWorkflowStepDaoImpl extends GenericDaoImpl<EmpCertificationWorkflowStep, Integer> implements EmpCertificationWorkflowStepDao {
	@Override
	public List<EmpCertificationWorkflowStep> getByInspectionForm(Integer empCertId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<EmpCertificationWorkflowStep> empCertificationWorkflowSteps = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EmpCertificationWorkflowStep> criteriaQuery = criteriaBuilder
					.createQuery(EmpCertificationWorkflowStep.class);
			Root<EmpCertificationWorkflowStep> root = criteriaQuery.from(EmpCertificationWorkflowStep.class);
			Join<EmpCertificationWorkflowStep, EmpCertification> joinEmpCertification = root
					.join("empCertification");

			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(joinEmpCertification.get("id"), empCertId));
			TypedQuery<EmpCertificationWorkflowStep> typedQuery = session.createQuery(criteriaQuery);
			empCertificationWorkflowSteps = typedQuery.getResultList();
			return empCertificationWorkflowSteps;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return empCertificationWorkflowSteps;
		}
	}

	@Override
	public List<EmpCertificationWorkflowStep> getByInspectionFormAndStepCode(Integer empCertId, String stepCode) {
		// TODO Auto-generated method stub
		Session session = null;
		List<EmpCertificationWorkflowStep> empCertificationWorkflowSteps = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EmpCertificationWorkflowStep> criteriaQuery = criteriaBuilder
					.createQuery(EmpCertificationWorkflowStep.class);
			Root<EmpCertificationWorkflowStep> root = criteriaQuery.from(EmpCertificationWorkflowStep.class);
			Join<EmpCertificationWorkflowStep, EmpCertification> joinEmpCertification = root
					.join("empCertification");

			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(joinEmpCertification.get("id"), empCertId),
					criteriaBuilder.equal(root.get("workflowDefinition").get("step").get("code"), stepCode));
			TypedQuery<EmpCertificationWorkflowStep> typedQuery = session.createQuery(criteriaQuery);
			empCertificationWorkflowSteps = typedQuery.getResultList();
			return empCertificationWorkflowSteps;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return empCertificationWorkflowSteps;
		}
	}

	@Override
	public Short getLastStepSeq(Integer empCertId) {
		// TODO Auto-generated method stub
		Session session = null;
		Short maxId = null;

		try {
			session = sessionFactory.getCurrentSession();
           if(empCertId == null) {
			maxId = (Short) session.createNativeQuery(
					"SELECT max(efws.step_seq) AS max_step_seq  FROM emp_certification_workflow_step efws WHERE efws.cert_id is null".toLowerCase())
					.addScalar("max_step_seq").uniqueResult();
           }else {
        	   maxId = (Short) session.createNativeQuery(
   					"SELECT max(efws.step_seq) AS max_step_seq  FROM emp_certification_workflow_step efws WHERE efws.cert_id=?".toLowerCase())
   					.setParameter(1, empCertId).addScalar("max_step_seq").uniqueResult(); 
           }
			if (maxId == null)
				return 0;
			else
				return maxId;
		} catch (HibernateException e) {
			e.printStackTrace();
			return maxId;
		}
	}

	@Override
	public EmpCertificationWorkflowStep getLastStep(Integer empCertId) {
		// TODO Auto-generated method stub
		Session session = null;
		EmpCertificationWorkflowStep empCertificationWorkflowStep = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EmpCertificationWorkflowStep> criteriaQuery = criteriaBuilder
					.createQuery(EmpCertificationWorkflowStep.class);
			Root<EmpCertificationWorkflowStep> root = criteriaQuery.from(EmpCertificationWorkflowStep.class);
			Join<EmpCertificationWorkflowStep, EmpCertification> joinEmpCertification = root
					.join("empCertification");

			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(joinEmpCertification.get("id"), empCertId));
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("stepSeq")));
			TypedQuery<EmpCertificationWorkflowStep> typedQuery = session.createQuery(criteriaQuery);
			typedQuery.setFirstResult(0);
			typedQuery.setMaxResults(1);
			List<EmpCertificationWorkflowStep> empCertificationWorkflowSteps = typedQuery.getResultList();

			if (empCertificationWorkflowSteps != null && !empCertificationWorkflowSteps.isEmpty())
				empCertificationWorkflowStep = empCertificationWorkflowSteps.get(0);
			else
				empCertificationWorkflowStep = null;
			return empCertificationWorkflowStep;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return empCertificationWorkflowStep;
		}
	}

}
