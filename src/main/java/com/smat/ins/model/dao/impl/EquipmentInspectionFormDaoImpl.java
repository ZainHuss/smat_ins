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
import com.smat.ins.model.dao.EquipmentInspectionFormDao;

import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.InspectionFormWorkflow;
import com.smat.ins.model.entity.Task;

public class EquipmentInspectionFormDaoImpl extends
        GenericDaoImpl<EquipmentInspectionForm, Long> implements EquipmentInspectionFormDao {

    private static final long serialVersionUID = 896152081302055230L;

    @Override
    public Integer getMaxReportNoCodeByEquipmentCat(String code) {
        Session session = null;
        Integer maxReportNoCode = null;

        try {
            session = sessionFactory.getCurrentSession();
        Object result = session.createNativeQuery(
            "SELECT COALESCE(MAX(CAST(SUBSTR(eif.report_no,-5) AS UNSIGNED)),0) as Max_Report_Code from equipment_inspection_form eif "
                + "INNER JOIN equipment_category ec ON eif.equipment_category = ec.id WHERE ec.code= ?")
            .setParameter(1, code).getSingleResult();
        if (result instanceof Number) return ((Number) result).intValue();
        if (result != null) return Integer.parseInt(result.toString());
        return 0;

        } catch (HibernateException e) {
            e.printStackTrace();
            return maxReportNoCode;
        }
    }

    @Override
    public Integer getNextReportSeqByEquipmentCat(String code) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();

            // نقرأ صف التسلسل مع قفل FOR UPDATE داخل المعاملة
            @SuppressWarnings("unchecked")
            java.util.List<Object> seqList = session.createNativeQuery(
                            "SELECT last_val FROM equipment_inspection_seq WHERE equipment_cat_code = ? FOR UPDATE")
                    .setParameter(1, code)
                    .getResultList();

            Integer nextVal = null;

            if (seqList == null || seqList.isEmpty()) {
                // لا يوجد صف بعد: نقرأ MAX من الجدول لإيجاد نقطة البداية
                Object maxObj = session.createNativeQuery(
                                "SELECT COALESCE(MAX(CAST(SUBSTR(eif.report_no,-5) AS UNSIGNED)),0) " +
                                        "FROM equipment_inspection_form eif " +
                                        "INNER JOIN equipment_category ec ON eif.equipment_category = ec.id " +
                                        "WHERE ec.code = ?")
                        .setParameter(1, code)
                        .getSingleResult();

                int maxVal = 0;
                if (maxObj instanceof Number) {
                    maxVal = ((Number) maxObj).intValue();
                } else if (maxObj != null) {
                    maxVal = Integer.parseInt(maxObj.toString());
                }
                nextVal = maxVal + 1;

                // ندرج صف البداية
                session.createNativeQuery(
                                "INSERT INTO equipment_inspection_seq (equipment_cat_code, last_val) VALUES (?, ?)")
                        .setParameter(1, code)
                        .setParameter(2, nextVal)
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
                                "UPDATE equipment_inspection_seq SET last_val = ? WHERE equipment_cat_code = ?")
                        .setParameter(1, nextVal)
                        .setParameter(2, code)
                        .executeUpdate();
            }

            return nextVal;

        } catch (Exception e) {
            log.error("getNextReportSeqByEquipmentCat failed for code=" + code + " - " + e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public Integer getMaxTimeSheetNoCodeByEquipmentCat(String code) {
        Session session = null;
        Integer maxTimeSheetNoCode = null;

        try {
            session = sessionFactory.getCurrentSession();
        Object result = session.createNativeQuery(
            "SELECT COALESCE(MAX(CAST(SUBSTR(eif.time_sheet_no,-5) AS UNSIGNED)),0) as Max_Time_Sheet_Code from equipment_inspection_form eif "
                + "INNER JOIN equipment_category ec ON eif.equipment_category = ec.id WHERE ec.code= ?")
            .setParameter(1, code).getSingleResult();
        if (result instanceof Number) return ((Number) result).intValue();
        if (result != null) return Integer.parseInt(result.toString());
        return 0;

        } catch (HibernateException e) {
            e.printStackTrace();
            return maxTimeSheetNoCode;
        }
    }

    @Override
    public Integer getMaxJobNoCodeByEquipmentCat(String code) {
        Session session = null;
        Integer maxJobNoCode = null;

        try {
            session = sessionFactory.getCurrentSession();
        Object result = session.createNativeQuery(
            "SELECT COALESCE(MAX(CAST(SUBSTR(eif.job_no,-5) AS UNSIGNED)),0) as Max_Job_Code from equipment_inspection_form eif "
                + "INNER JOIN equipment_category ec ON eif.equipment_category = ec.id WHERE ec.code= ?")
            .setParameter(1, code).getSingleResult();
        if (result instanceof Number) return ((Number) result).intValue();
        if (result != null) return Integer.parseInt(result.toString());
        return 0;

        } catch (HibernateException e) {
            e.printStackTrace();
            return maxJobNoCode;
        }
    }

    @Override
    public Integer getMaxStickerNoCodeByEquipmentCat(String code) {
        Session session = null;
        Integer maxStickerNoCode = null;

        try {
            session = sessionFactory.getCurrentSession();
        Object result = session.createNativeQuery(
            "SELECT COALESCE(MAX(CAST(SUBSTR(eif.sticker_no,-5) AS UNSIGNED)),0) as Max_Sticker_Code from equipment_inspection_form eif "
                + "INNER JOIN equipment_category ec ON eif.equipment_category = ec.id WHERE ec.code= ?")
            .setParameter(1, code).getSingleResult();
        if (result instanceof Number) return ((Number) result).intValue();
        if (result != null) return Integer.parseInt(result.toString());
        return 0;

        } catch (HibernateException e) {
            e.printStackTrace();
            return maxStickerNoCode;
        }
    }

    @Override
    public List<EquipmentInspectionForm> getForReview() {
        Session session = null;
        List<EquipmentInspectionForm> equipmentInspectionForms = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<EquipmentInspectionForm> criteriaQuery = criteriaBuilder
                    .createQuery(EquipmentInspectionForm.class);
            Root<EquipmentInspectionForm> root = criteriaQuery.from(EquipmentInspectionForm.class);
            // use LEFT join to avoid excluding forms without workflow records
            Join<EquipmentInspectionForm, InspectionFormWorkflow> joinInspectionFormWorkflow = root
                    .join("inspectionFormWorkflows", JoinType.LEFT);

            criteriaQuery.select(root);
            criteriaQuery.where(
                    criteriaBuilder.equal(joinInspectionFormWorkflow.get("workflowDefinition").get("step").get("code"),
                            "02"));
            criteriaQuery.distinct(true);
            TypedQuery<EquipmentInspectionForm> typedQuery = session.createQuery(criteriaQuery);
            equipmentInspectionForms = typedQuery.getResultList();
            return equipmentInspectionForms;
        } catch (Exception e) {
            log.error(persistentClass + " can't be fetched from DB in getForReview() - exception: " + e.getMessage());
            e.printStackTrace();
            return equipmentInspectionForms;
        }
    }

    @Override
    public EquipmentInspectionForm getBy(Integer taskId) {
        Session session = null;
        EquipmentInspectionForm equipmentInspectionForm = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<EquipmentInspectionForm> criteriaQuery = criteriaBuilder
                    .createQuery(EquipmentInspectionForm.class);
            Root<EquipmentInspectionForm> root = criteriaQuery.from(EquipmentInspectionForm.class);

            // Fetch collections/associations with LEFT to avoid excluding parent
            root.fetch("equipmentInspectionFormItems", JoinType.LEFT);
            root.fetch("sticker", JoinType.LEFT);

            // Join workflow and task with LEFT joins in case some relations are missing
            Join<EquipmentInspectionForm, InspectionFormWorkflow> joinInspectionFormWorkflow = root
                    .join("inspectionFormWorkflows", JoinType.LEFT);
            Join<InspectionFormWorkflow, Task> joinTask = joinInspectionFormWorkflow.join("task", JoinType.LEFT);

            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(joinTask.get("id"), taskId));
            criteriaQuery.distinct(true);

            TypedQuery<EquipmentInspectionForm> typedQuery = session.createQuery(criteriaQuery);
            // use getResultList with maxResults(1) to avoid NoResultException
            typedQuery.setMaxResults(1);
            List<EquipmentInspectionForm> results = typedQuery.getResultList();
            if (results == null || results.isEmpty()) {
                log.debug("EquipmentInspectionFormDaoImpl.getBy - no EquipmentInspectionForm found for taskId: "
                        + taskId);
                return null;
            }
            equipmentInspectionForm = results.get(0);
            return equipmentInspectionForm;
        } catch (Exception e) {
            log.error(persistentClass + " can't be fetched from DB because of the following Exception: " + e.getMessage()
                    + " (taskId=" + taskId + ")");
            e.printStackTrace();
            return equipmentInspectionForm;
        }
    }

}
