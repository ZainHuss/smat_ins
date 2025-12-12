package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

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
            Object result = session.createNativeQuery(
                            "SELECT COALESCE(MAX(CAST(SUBSTR(ec.cert_number,-7) AS UNSIGNED)),0) as max_cert_no from emp_certification ec")
                    .getSingleResult();
            if (result instanceof Number) {
                return ((Number) result).intValue();
            } else if (result != null) {
                return Integer.parseInt(result.toString());
            } else {
                return 0;
            }
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
            Object result = session.createNativeQuery(
                            "SELECT COALESCE(MAX(CAST(SUBSTR(ec.ts_number,-5) AS UNSIGNED)),0) as ts_number from emp_certification ec")
                    .getSingleResult();
            if (result instanceof Number) {
                return ((Number) result).intValue();
            } else if (result != null) {
                return Integer.parseInt(result.toString());
            } else {
                return 0;
            }
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
            javax.persistence.criteria.CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            javax.persistence.criteria.CriteriaQuery<EmpCertification> criteriaQuery = criteriaBuilder.createQuery(EmpCertification.class);
            javax.persistence.criteria.Root<EmpCertification> root = criteriaQuery.from(EmpCertification.class);
            javax.persistence.criteria.Join<EmpCertification, EmpCertificationWorkflow> joinEmpCertificationWorkflow = root.join("empCertificationWorkflows");

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
            javax.persistence.criteria.CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            javax.persistence.criteria.CriteriaQuery<EmpCertification> criteriaQuery = criteriaBuilder.createQuery(EmpCertification.class);
            javax.persistence.criteria.Root<EmpCertification> root = criteriaQuery.from(EmpCertification.class);
            root.fetch("equipments", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("employee", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("empCertificationType", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("sysUserByReviewedBy", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("sysUserByInspectedBy", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("sysUserByIssuedBy", javax.persistence.criteria.JoinType.LEFT);
            javax.persistence.criteria.Join<EmpCertification, EmpCertificationWorkflow> joinEmpCertificationWorkflow = root.join("empCertificationWorkflows", javax.persistence.criteria.JoinType.LEFT);
            javax.persistence.criteria.Join<EmpCertificationWorkflow, Task> joinTask = joinEmpCertificationWorkflow.join("task", javax.persistence.criteria.JoinType.LEFT);

            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(joinTask.get("id"), taskId));
            TypedQuery<EmpCertification> typedQuery = session.createQuery(criteriaQuery);
            java.util.List<EmpCertification> results = typedQuery.getResultList();
            if (results != null && !results.isEmpty()) {
                empCertification = results.get(0);
            }
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
            javax.persistence.criteria.CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            javax.persistence.criteria.CriteriaQuery<EmpCertification> criteriaQuery = criteriaBuilder.createQuery(EmpCertification.class);
            javax.persistence.criteria.Root<EmpCertification> root = criteriaQuery.from(EmpCertification.class);
            root.fetch("equipments", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("employee", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("empCertificationType", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("sysUserByReviewedBy", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("sysUserByInspectedBy", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("sysUserByIssuedBy", javax.persistence.criteria.JoinType.LEFT);
            javax.persistence.criteria.Join<EmpCertification, EmpCertificationWorkflow> joinEmpCertificationWorkflow = root.join("empCertificationWorkflows", javax.persistence.criteria.JoinType.LEFT);
            javax.persistence.criteria.Join<EmpCertificationWorkflow, Task> joinTask = joinEmpCertificationWorkflow.join("task", javax.persistence.criteria.JoinType.LEFT);
            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(root.get("id"), certId));
            TypedQuery<EmpCertification> typedQuery = session.createQuery(criteriaQuery);
            java.util.List<EmpCertification> results = typedQuery.getResultList();
            if (results != null && !results.isEmpty()) {
                empCertification = results.get(0);
            }
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

            // Use Criteria API to avoid string-based HQL property resolution issues in IDE
            javax.persistence.criteria.CriteriaBuilder cb = session.getCriteriaBuilder();
            javax.persistence.criteria.CriteriaQuery<EmpCertification> cq = cb.createQuery(EmpCertification.class);
            javax.persistence.criteria.Root<EmpCertification> root = cq.from(EmpCertification.class);
            root.fetch("employee", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("empCertificationType", javax.persistence.criteria.JoinType.LEFT);
            root.fetch("equipments", javax.persistence.criteria.JoinType.LEFT);

            cq.select(root);
            cq.where(cb.and(cb.equal(root.get("certNumber"), certNumber), cb.equal(root.get("tsNumber"), tsNumber)));

            TypedQuery<EmpCertification> query = session.createQuery(cq);

            java.util.List<EmpCertification> results = query.getResultList();
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

    /**
     * NEW: get next cert sequence value in a transactional-safe way.
     *
     * NOTE: This method MUST be called inside a DB transaction so that the SELECT ... FOR UPDATE
     * actually locks the row (e.g. service method annotated with @Transactional).
     */
    @Override
    public Integer getNextCertSeq() {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();

            // try to read existing seq row with FOR UPDATE
            @SuppressWarnings("unchecked")
            java.util.List<Object> seqList = session.createNativeQuery(
                            "SELECT last_val FROM emp_cert_seq WHERE seq_name = 'default' FOR UPDATE")
                    .getResultList();

            Integer nextVal = null;

            if (seqList == null || seqList.isEmpty()) {
                // compute max from existing cert numbers (substr -7)
                Object maxObj = session.createNativeQuery(
                                "SELECT COALESCE(MAX(CAST(SUBSTR(ec.cert_number,-7) AS UNSIGNED)),0) FROM emp_certification ec")
                        .getSingleResult();

                int maxVal = 0;
                if (maxObj instanceof Number) {
                    maxVal = ((Number) maxObj).intValue();
                } else if (maxObj != null) {
                    maxVal = Integer.parseInt(maxObj.toString());
                }
                nextVal = maxVal + 1;

                // insert initial row
                session.createNativeQuery(
                                "INSERT INTO emp_cert_seq (seq_name, last_val) VALUES ('default', ?)")
                        .setParameter(1, nextVal)
                        .executeUpdate();
            } else {
                Object curObj = seqList.get(0);
                int cur = 0;
                if (curObj instanceof Number) {
                    cur = ((Number) curObj).intValue();
                } else if (curObj != null) {
                    cur = Integer.parseInt(curObj.toString());
                }
                nextVal = cur + 1;

                session.createNativeQuery(
                                "UPDATE emp_cert_seq SET last_val = ? WHERE seq_name = 'default'")
                        .setParameter(1, nextVal)
                        .executeUpdate();
            }
            return nextVal;
        } catch (Exception e) {
            log.error("getNextCertSeq failed - " + e.getMessage(), e);
            throw e;
        }
    }
}
