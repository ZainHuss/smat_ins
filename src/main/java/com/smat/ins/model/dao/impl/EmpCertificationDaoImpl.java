package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.EmpCertificationDao;
import com.smat.ins.model.entity.EmpCertification;
import com.smat.ins.model.entity.EmpCertificationWorkflow;
import com.smat.ins.model.entity.Task;

public class EmpCertificationDaoImpl extends GenericDaoImpl<EmpCertification, Integer> implements EmpCertificationDao {

	@Override
	public Integer getMaxCertNo() {
		Session session = null;
		Integer maxCertNo = null;
		try {
			session = sessionFactory.getCurrentSession();
			return Integer.parseInt((String) session.createNativeQuery(
					"SELECT COALESCE(MAX(SUBSTR(ec.cert_number,-7)),0) as max_cert_no from emp_certification ec \r\n"
							.toLowerCase())
					.addScalar("max_cert_no").uniqueResult());
		} catch (HibernateException e) {
			e.printStackTrace();
			return maxCertNo;
		}
	}

	@Override
	public Integer getMaxTimeSheetNo() {
		Session session = null;
		Integer maxTsNo = null;
		try {
			session = sessionFactory.getCurrentSession();
			return Integer.parseInt((String) session.createNativeQuery(
					"SELECT COALESCE(MAX(SUBSTR(ec.ts_number,-5)),0) as ts_number from emp_certification ec \r\n"
							.toLowerCase())
					.addScalar("ts_number").uniqueResult());
		} catch (HibernateException e) {
			e.printStackTrace();
			return maxTsNo;
		}
	}

	@Override
	public List<EmpCertification> getForReview() {
		Session session = null;
		List<EmpCertification> empCertifications = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EmpCertification> criteriaQuery = criteriaBuilder.createQuery(EmpCertification.class);
			Root<EmpCertification> root = criteriaQuery.from(EmpCertification.class);
			Join<EmpCertification, EmpCertificationWorkflow> joinEmpCertificationWorkflow = root.join("empCertificationWorkflows");

			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(joinEmpCertificationWorkflow.get("workflowDefinition").get("step").get("code"), "02"));
			TypedQuery<EmpCertification> typedQuery = session.createQuery(criteriaQuery);
			empCertifications = typedQuery.getResultList();
			return empCertifications;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return empCertifications;
		}
	}

	@Override
	public EmpCertification getBy(Integer taskId) {
		Session session = null;
		EmpCertification empCertification = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EmpCertification> criteriaQuery = criteriaBuilder.createQuery(EmpCertification.class);
			Root<EmpCertification> root = criteriaQuery.from(EmpCertification.class);
			root.fetch("equipments", JoinType.LEFT);
			root.fetch("employee", JoinType.LEFT);
			root.fetch("empCertificationType", JoinType.LEFT);
			root.fetch("sysUserByReviewedBy", JoinType.LEFT);
			root.fetch("sysUserByInspectedBy", JoinType.LEFT);
			root.fetch("sysUserByIssuedBy", JoinType.LEFT);
			Join<EmpCertification, EmpCertificationWorkflow> joinEmpCertificationWorkflow = root.join("empCertificationWorkflows", JoinType.LEFT);
			Join<EmpCertificationWorkflow, Task> joinTask = joinEmpCertificationWorkflow.join("task", JoinType.LEFT);

			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(joinTask.get("id"), taskId));
			TypedQuery<EmpCertification> typedQuery = session.createQuery(criteriaQuery);
			empCertification = typedQuery.getSingleResult();
			return empCertification;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return empCertification;
		}
	}

	@Override
	public EmpCertification findBy(Integer certId) {
		Session session = null;
		EmpCertification empCertification = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EmpCertification> criteriaQuery = criteriaBuilder.createQuery(EmpCertification.class);
			Root<EmpCertification> root = criteriaQuery.from(EmpCertification.class);
			root.fetch("equipments", JoinType.LEFT);
			root.fetch("employee", JoinType.LEFT);
			root.fetch("empCertificationType", JoinType.LEFT);
			root.fetch("sysUserByReviewedBy", JoinType.LEFT);
			root.fetch("sysUserByInspectedBy", JoinType.LEFT);
			root.fetch("sysUserByIssuedBy", JoinType.LEFT);
			Join<EmpCertification, EmpCertificationWorkflow> joinEmpCertificationWorkflow = root.join("empCertificationWorkflows", JoinType.LEFT);
			Join<EmpCertificationWorkflow, Task> joinTask = joinEmpCertificationWorkflow.join("task", JoinType.LEFT);
			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(root.get("id"), certId));
			TypedQuery<EmpCertification> typedQuery = session.createQuery(criteriaQuery);
			empCertification = typedQuery.getSingleResult();
			return empCertification;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return empCertification;
		}
	}

	@Override
	public EmpCertification getByCertNumberAndTsNumber(String certNumber, String tsNumber) {
	    Session session = null;
	    EmpCertification empCertification = null;
	    try {
	        session = sessionFactory.getCurrentSession();

	        // Use JPQL/HQL with named parameters to avoid any placeholder mismatch
	        String hql = "select e from EmpCertification e "
	                   + "left join fetch e.employee emp "
	                   + "left join fetch e.empCertificationType et "
	                   + "left join fetch e.equipments eq "
	                   + "where e.certNumber = :certNumber and e.tsNumber = :tsNumber";

	        TypedQuery<EmpCertification> query = session.createQuery(hql, EmpCertification.class);
	        query.setParameter("certNumber", certNumber);
	        query.setParameter("tsNumber", tsNumber);

	        // Use getResultList to avoid NoResultException and return null if nothing found
	        List<EmpCertification> results = query.getResultList();
	        if (results != null && !results.isEmpty()) {
	            empCertification = results.get(0);
	        }
	        return empCertification;
	    } catch (Exception e) {
	        log.error(persistentClass + " can't be fetched from DB because of the following Exception for cert=" 
	                  + certNumber + " ts=" + tsNumber, e);
	        e.printStackTrace();
	        return empCertification;
	    }
	}
}
