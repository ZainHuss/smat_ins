package com.smat.ins.model.service.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smat.ins.model.service.SeqReservationService;

@Service("seqReservationService")
@Transactional
public class SeqReservationServiceImpl implements SeqReservationService {

    private static final Logger log = LoggerFactory.getLogger(SeqReservationServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;

    // setter for XML property injection
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    // ---------------- equipment reservation ----------------
    @Override
    public Integer getReservedReportNoForTask(Integer taskId) {
        if (taskId == null) return null;
        Session session = getCurrentSession();
        try {
            @SuppressWarnings("unchecked")
            List<Object> rows = session.createNativeQuery(
                    "SELECT reserved_val FROM equipment_inspection_seq_reservation WHERE task_id = ? AND (status='reserved' OR status='confirmed') LIMIT 1")
                    .setParameter(1, taskId)
                    .getResultList();
            if (rows != null && !rows.isEmpty()) {
                Object o = rows.get(0);
                if (o instanceof Number) return ((Number)o).intValue();
                return Integer.parseInt(o.toString());
            }
            return null;
        } catch (Exception e) {
            log.error("getReservedReportNoForTask failed", e);
            return null;
        }
    }

    @Override
    public Integer reserveReportNoForTask(Integer taskId, String equipmentCatCode, Long reservedBy) {
        if (taskId == null || equipmentCatCode == null) return null;
        Session session = getCurrentSession();
        try {
            // idempotent: return existing if present
            Integer existing = getReservedReportNoForTask(taskId);
            if (existing != null) return existing;
        // Use a global sequence row to serialize reservations and avoid duplicates across rapid opens
        final String GLOBAL_CODE = "GLOBAL";

        // Acquire FOR UPDATE on the global seq row
        @SuppressWarnings("unchecked")
        List<Object> globalList = session.createNativeQuery(
            "SELECT last_val FROM equipment_inspection_seq WHERE equipment_cat_code = ? FOR UPDATE")
            .setParameter(1, GLOBAL_CODE)
            .getResultList();

        Integer globalLast;
        if (globalList == null || globalList.isEmpty()) {
        // Initialize global last_val from the max existing report_no and existing seqs
        Object maxObj = session.createNativeQuery(
            "SELECT COALESCE(MAX(CAST(SUBSTR(eif.report_no,-5) AS UNSIGNED)),0) FROM equipment_inspection_form eif")
            .getSingleResult();
        int maxVal = 0;
        if (maxObj instanceof Number) maxVal = ((Number) maxObj).intValue();
        else if (maxObj != null) maxVal = Integer.parseInt(maxObj.toString());

        Object seqMaxObj = session.createNativeQuery(
            "SELECT COALESCE(MAX(last_val),0) FROM equipment_inspection_seq")
            .getSingleResult();
        if (seqMaxObj instanceof Number) {
            int seqMax = ((Number) seqMaxObj).intValue();
            if (seqMax > maxVal) maxVal = seqMax;
        } else if (seqMaxObj != null) {
            int seqMax = Integer.parseInt(seqMaxObj.toString());
            if (seqMax > maxVal) maxVal = seqMax;
        }

        globalLast = maxVal;
        session.createNativeQuery(
            "INSERT INTO equipment_inspection_seq (equipment_cat_code, last_val) VALUES (?, ?)")
            .setParameter(1, GLOBAL_CODE)
            .setParameter(2, globalLast)
            .executeUpdate();
        } else {
        Object curObj = globalList.get(0);
        int cur = 0;
        if (curObj instanceof Number) cur = ((Number) curObj).intValue();
        else if (curObj != null) cur = Integer.parseInt(curObj.toString());
        globalLast = cur;
        }

        // Compute next candidate globally and ensure it's not already used (loop if needed)
        int candidate = globalLast + 1;
        while (true) {
        String formatted = String.format("A0247%05d", candidate);
        Object exists = session.createNativeQuery(
            "SELECT COUNT(1) FROM equipment_inspection_form WHERE report_no = ?")
            .setParameter(1, formatted)
            .getSingleResult();
        long count = 0;
        if (exists instanceof Number) count = ((Number) exists).longValue();
        else if (exists != null) count = Long.parseLong(exists.toString());
        if (count == 0) break;
        candidate++;
        }

        // persist new global last_val
        session.createNativeQuery(
            "UPDATE equipment_inspection_seq SET last_val = ? WHERE equipment_cat_code = ?")
            .setParameter(1, candidate)
            .setParameter(2, GLOBAL_CODE)
            .executeUpdate();

        // keep per-category seq in sync
        @SuppressWarnings("unchecked")
        List<Object> catList = session.createNativeQuery(
            "SELECT last_val FROM equipment_inspection_seq WHERE equipment_cat_code = ? FOR UPDATE")
            .setParameter(1, equipmentCatCode)
            .getResultList();
        if (catList == null || catList.isEmpty()) {
        session.createNativeQuery(
            "INSERT INTO equipment_inspection_seq (equipment_cat_code, last_val) VALUES (?, ?)")
            .setParameter(1, equipmentCatCode)
            .setParameter(2, candidate)
            .executeUpdate();
        } else {
        session.createNativeQuery(
            "UPDATE equipment_inspection_seq SET last_val = ? WHERE equipment_cat_code = ?")
            .setParameter(1, candidate)
            .setParameter(2, equipmentCatCode)
            .executeUpdate();
        }

        // insert reservation row with the candidate
        session.createNativeQuery(
            "INSERT INTO equipment_inspection_seq_reservation (equipment_cat_code, reserved_val, task_id, reserved_by, status) VALUES (?, ?, ?, ?, 'reserved')")
            .setParameter(1, equipmentCatCode)
            .setParameter(2, candidate)
            .setParameter(3, taskId)
            .setParameter(4, reservedBy)
            .executeUpdate();

        return candidate;
        } catch (Exception e) {
            log.error("reserveReportNoForTask failed", e);
            throw e;
        }
    }

    @Override
    public Boolean confirmReservedReportNoForTask(Integer taskId) {
        if (taskId == null) return false;
        Session session = getCurrentSession();
        try {
            int updated = session.createNativeQuery(
                    "UPDATE equipment_inspection_seq_reservation SET status='confirmed' WHERE task_id = ? AND status = 'reserved'")
                    .setParameter(1, taskId)
                    .executeUpdate();
            return updated > 0;
        } catch (Exception e) {
            log.error("confirmReservedReportNoForTask failed", e);
            throw e;
        }
    }

    @Override
    public Boolean releaseReservedReportNoForTask(Integer taskId) {
        if (taskId == null) return false;
        Session session = getCurrentSession();
        try {
            int updated = session.createNativeQuery(
                    "UPDATE equipment_inspection_seq_reservation SET status='released' WHERE task_id = ? AND status = 'reserved'")
                    .setParameter(1, taskId)
                    .executeUpdate();
            return updated > 0;
        } catch (Exception e) {
            log.error("releaseReservedReportNoForTask failed", e);
            throw e;
        }
    }

    // ---------------- employee cert reservation ----------------
    @Override
    public Integer getReservedCertNoForTask(Integer taskId) {
        if (taskId == null) return null;
        Session session = getCurrentSession();
        try {
            @SuppressWarnings("unchecked")
            List<Object> rows = session.createNativeQuery(
                    "SELECT reserved_val FROM emp_cert_seq_reservation WHERE task_id = ? AND (status='reserved' OR status='confirmed') LIMIT 1")
                    .setParameter(1, taskId)
                    .getResultList();
            if (rows != null && !rows.isEmpty()) {
                Object o = rows.get(0);
                if (o instanceof Number) return ((Number)o).intValue();
                return Integer.parseInt(o.toString());
            }
            return null;
        } catch (Exception e) {
            log.error("getReservedCertNoForTask failed", e);
            return null;
        }
    }

    @Override
    public Integer reserveCertNoForTask(Integer taskId, Long reservedBy) {
        if (taskId == null) return null;
        Session session = getCurrentSession();
        try {
            Integer existing = getReservedCertNoForTask(taskId);
            if (existing != null) return existing;

            @SuppressWarnings("unchecked")
            List<Object> seqList = session.createNativeQuery(
                    "SELECT last_val FROM emp_cert_seq WHERE seq_name = 'default' FOR UPDATE")
                    .getResultList();

            Integer nextVal = null;
            if (seqList == null || seqList.isEmpty()) {
                Object maxObj = session.createNativeQuery(
                        "SELECT COALESCE(MAX(CAST(SUBSTR(ec.cert_number,-7) AS UNSIGNED)),0) FROM emp_certification ec")
                        .getSingleResult();
                int maxVal = 0;
                if (maxObj instanceof Number) maxVal = ((Number) maxObj).intValue();
                else if (maxObj != null) maxVal = Integer.parseInt(maxObj.toString());
                nextVal = maxVal + 1;
                session.createNativeQuery(
                        "INSERT INTO emp_cert_seq (seq_name, last_val) VALUES ('default', ?)")
                        .setParameter(1, nextVal)
                        .executeUpdate();
            } else {
                Object curObj = seqList.get(0);
                int cur = 0;
                if (curObj instanceof Number) cur = ((Number) curObj).intValue();
                else if (curObj != null) cur = Integer.parseInt(curObj.toString());
                nextVal = cur + 1;
                session.createNativeQuery(
                        "UPDATE emp_cert_seq SET last_val = ? WHERE seq_name = 'default'")
                        .setParameter(1, nextVal)
                        .executeUpdate();
            }

        session.createNativeQuery(
            "INSERT INTO emp_cert_seq_reservation (seq_name, reserved_val, task_id, reserved_by, status) VALUES ('default', ?, ?, ?, 'reserved')")
            .setParameter(1, nextVal)
            .setParameter(2, taskId)
            .setParameter(3, reservedBy)
            .executeUpdate();

            return nextVal;
        } catch (Exception e) {
            log.error("reserveCertNoForTask failed", e);
            throw e;
        }
    }

    @Override
    public Boolean confirmReservedCertNoForTask(Integer taskId) {
        if (taskId == null) return false;
        Session session = getCurrentSession();
        try {
            int updated = session.createNativeQuery(
                    "UPDATE emp_cert_seq_reservation SET status='confirmed' WHERE task_id = ? AND status = 'reserved'")
                    .setParameter(1, taskId)
                    .executeUpdate();
            return updated > 0;
        } catch (Exception e) {
            log.error("confirmReservedCertNoForTask failed", e);
            throw e;
        }
    }

    @Override
    public Boolean releaseReservedCertNoForTask(Integer taskId) {
        if (taskId == null) return false;
        Session session = getCurrentSession();
        try {
            int updated = session.createNativeQuery(
                    "UPDATE emp_cert_seq_reservation SET status='released' WHERE task_id = ? AND status = 'reserved'")
                    .setParameter(1, taskId)
                    .executeUpdate();
            return updated > 0;
        } catch (Exception e) {
            log.error("releaseReservedCertNoForTask failed", e);
            throw e;
        }
    }

}
