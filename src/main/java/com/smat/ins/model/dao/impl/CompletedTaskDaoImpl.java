package com.smat.ins.model.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.CompletedTaskDao;
import com.smat.ins.model.entity.Task;

/**
 * Implementation of CompletedTaskDao for retrieving completed tasks
 * based on workflow completion status (step code '03')
 */
public class CompletedTaskDaoImpl extends GenericDaoImpl<Task, Integer> implements CompletedTaskDao {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(CompletedTaskDaoImpl.class);
    
    private static final String COMPLETED_STEP_CODE = "03";

    @Override
    public List<Map<String, Object>> getCompletedTasks(String taskType, Date fromDate, Date toDate, 
                                                       Integer companyId, Short equipmentCategoryId) {
        List<Map<String, Object>> allTasks = new ArrayList<>();
        
        try {
            if ("equipment".equalsIgnoreCase(taskType)) {
                allTasks.addAll(getCompletedEquipmentTasks(fromDate, toDate, companyId, equipmentCategoryId));
            } else if ("employee".equalsIgnoreCase(taskType)) {
                allTasks.addAll(getCompletedEmployeeTasks(fromDate, toDate, companyId));
            } else {
                allTasks.addAll(getCompletedEquipmentTasks(fromDate, toDate, companyId, equipmentCategoryId));
                allTasks.addAll(getCompletedEmployeeTasks(fromDate, toDate, companyId));
            }
        } catch (Exception e) {
            log.error("Error retrieving completed tasks for type: {}", taskType, e);
            throw new RuntimeException("Failed to retrieve completed tasks: " + e.getMessage(), e);
        }
        
        return allTasks;
    }

    @Override
    public List<Map<String, Object>> getCompletedEquipmentTasks(Date fromDate, Date toDate, 
                                                               Integer companyId, Short equipmentCategoryId) {
        Session session = sessionFactory.getCurrentSession();
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT DISTINCT ");
            sql.append("    t.id AS task_id, ");
            sql.append("    t.task_description, ");
            sql.append("    'Equipment Inspection' AS task_type, ");
            sql.append("    eif.report_no, ");
            sql.append("    eif.sticker_no, ");
            sql.append("    t.created_date, ");
            sql.append("    t.completed_date, ");
            sql.append("    c.name AS company_name, ");
            sql.append("    ec.arabic_name AS equipment_category_name, ");
            sql.append("    ec.code AS equipment_category_code, ");
            sql.append("    ua_assigner.en_alias_name AS assigner_name, ");
            sql.append("    su_assigner.user_name AS assigner_user_name, ");
            sql.append("    ua_assignee.en_alias_name AS assignee_name, ");
            sql.append("    su_assignee.user_name AS assignee_user_name, ");
            sql.append("    su_assigner.en_display_name AS user_display_name, ");
            sql.append("    s.arabic_name AS step_name ");
            
            sql.append("FROM task t ");
            sql.append("JOIN inspection_form_workflow ifw ON t.id = ifw.task ");
            sql.append("JOIN workflow_definition wd ON ifw.workflow_definition = wd.id ");
            sql.append("JOIN step s ON wd.step = s.id ");
            sql.append("JOIN equipment_inspection_form eif ON ifw.equipment_inspection_form = eif.id ");
            sql.append("LEFT JOIN company c ON t.company = c.id ");
            sql.append("LEFT JOIN equipment_category ec ON t.equipment_category = ec.id ");
            sql.append("LEFT JOIN user_alias ua_assigner ON t.assigner = ua_assigner.id ");
            sql.append("LEFT JOIN sys_user su_assigner ON ua_assigner.sys_user = su_assigner.id ");
            sql.append("LEFT JOIN user_alias ua_assignee ON t.assign_to = ua_assignee.id ");
            sql.append("LEFT JOIN sys_user su_assignee ON ua_assignee.sys_user = su_assignee.id ");
            sql.append("WHERE s.code = :stepCode ");
            sql.append("AND t.is_active = 1 ");
 

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("stepCode", COMPLETED_STEP_CODE);
            
            if (fromDate != null) {
                sql.append("AND t.created_date >= :fromDate ");
                parameters.put("fromDate", fromDate);
            }
            
            if (toDate != null) {
                sql.append("AND t.created_date <= :toDate ");
                parameters.put("toDate", toDate);
            }
            
            if (companyId != null) {
                sql.append("AND t.company = :companyId ");
                parameters.put("companyId", companyId);
            }
            
            if (equipmentCategoryId != null) {
                sql.append("AND t.equipment_category = :equipmentCategoryId ");
                parameters.put("equipmentCategoryId", equipmentCategoryId);
            }
            
            sql.append("ORDER BY t.created_date DESC");
            
            Query query = session.createNativeQuery(sql.toString());
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }
            
            @SuppressWarnings("unchecked")
            List<Object[]> queryResults = query.getResultList();
            
            for (Object[] row : queryResults) {
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("taskId", row[0]);
                taskMap.put("taskDescription", row[1]);
                taskMap.put("taskType", row[2]);
                taskMap.put("reportNo", row[3]);
                taskMap.put("stickerNo", row[4]);
                taskMap.put("createdDate", row[5]);
                taskMap.put("completedDate", row[6]);
                taskMap.put("companyName", row[7]);
                taskMap.put("equipmentCategoryName", row[8]);
                taskMap.put("equipmentCategoryCode", row[9]);
                taskMap.put("assignerName", row[10]);
                taskMap.put("assignerUserName", row[11]);
                taskMap.put("assigneeName", row[12]);
                taskMap.put("assigneeUserName", row[13]);
                taskMap.put("assignerUserDisplayName", row[14]);
                taskMap.put("stepName", row[15]);
                results.add(taskMap);
            }
            
        } catch (Exception e) {
            log.error("Error retrieving completed equipment tasks for companyId: {}, equipmentCategoryId: {}", 
                      companyId, equipmentCategoryId, e);
            throw new RuntimeException("Failed to retrieve completed equipment tasks: " + e.getMessage(), e);
        }
        
        return results;
    }

    @Override
    public List<Map<String, Object>> getCompletedEmployeeTasks(Date fromDate, Date toDate, Integer companyId) {
        Session session = sessionFactory.getCurrentSession();
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT DISTINCT ");
            sql.append("    t.id AS task_id, ");
            sql.append("    t.task_description, ");
            sql.append("    'Employee Certification' AS task_type, ");
            sql.append("    ec.cert_number, ");
            sql.append("    e.full_name AS employee_name, ");
            sql.append("    t.created_date, ");
            sql.append("    t.completed_date, ");
            sql.append("    c.name AS company_name, ");
            sql.append("    ect.cert_name AS certification_type, ");
            sql.append("    ua_assigner.en_alias_name AS assigner_name, ");
            sql.append("    su_assigner.user_name AS assigner_user_name, ");
            sql.append("    ua_assignee.en_alias_name AS assignee_name, ");
            sql.append("    su_assignee.user_name AS assignee_user_name, ");
            sql.append("    su_assigner.en_display_name AS user_display_name, ");
            sql.append("    ec.id, ");
            sql.append("    s.arabic_name AS step_name ");
            sql.append("FROM task t ");
            sql.append("JOIN emp_certification_workflow ecw ON t.id = ecw.task ");
            sql.append("JOIN workflow_definition wd ON ecw.workflow_definition = wd.id ");
            sql.append("JOIN step s ON wd.step = s.id ");
            sql.append("JOIN emp_certification ec ON ecw.cert_id = ec.id ");
            sql.append("JOIN employee e ON ec.employee_id = e.id ");
            sql.append("LEFT JOIN emp_certification_type ect ON ec.cert_type_id = ect.id ");
            sql.append("LEFT JOIN company c ON t.company = c.id ");
            sql.append("LEFT JOIN user_alias ua_assigner ON t.assigner = ua_assigner.id ");
            sql.append("LEFT JOIN sys_user su_assigner ON ua_assigner.sys_user = su_assigner.id ");
            sql.append("LEFT JOIN user_alias ua_assignee ON t.assign_to = ua_assignee.id ");
            sql.append("LEFT JOIN sys_user su_assignee ON ua_assignee.sys_user = su_assignee.id ");
            sql.append("WHERE s.code = :stepCode ");
            sql.append("AND t.is_active = 1 ");
       

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("stepCode", COMPLETED_STEP_CODE);
            
            if (fromDate != null) {
                sql.append("AND t.created_date >= :fromDate ");
                parameters.put("fromDate", fromDate);
            }
            
            if (toDate != null) {
                sql.append("AND t.created_date <= :toDate ");
                parameters.put("toDate", toDate);
            }
            
            if (companyId != null) {
                sql.append("AND t.company = :companyId ");
                parameters.put("companyId", companyId);
            }
            
            sql.append("ORDER BY t.created_date DESC");
            
            Query query = session.createNativeQuery(sql.toString());
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }
            
            @SuppressWarnings("unchecked")
            List<Object[]> queryResults = query.getResultList();
            
            for (Object[] row : queryResults) {
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("taskId", row[0]);
                taskMap.put("taskDescription", row[1]);
                taskMap.put("taskType", row[2]);
                taskMap.put("certNumber", row[3]);
                taskMap.put("employeeName", row[4]);
                taskMap.put("createdDate", row[5]);
                taskMap.put("completedDate", row[6]);
                taskMap.put("companyName", row[7]);
                taskMap.put("certificationType", row[8]);
                taskMap.put("assignerName", row[9]);
                taskMap.put("assignerUserName", row[10]);
                taskMap.put("assigneeName", row[11]);
                taskMap.put("assigneeUserName", row[12]);
                taskMap.put("assignerUserDisplayName", row[13]);
                taskMap.put("empCertificateId", row[14]);
                taskMap.put("stepName", row[15]);
                results.add(taskMap);
            }
            
        } catch (Exception e) {
            log.error("Error retrieving completed employee tasks for companyId: {}", companyId, e);
            throw new RuntimeException("Failed to retrieve completed employee tasks: " + e.getMessage(), e);
        }
        
        return results;
    }

    @Override
    public Long countCompletedTasks(String taskType, Date fromDate, Date toDate, Integer companyId) {
        try {
            if ("equipment".equalsIgnoreCase(taskType)) {
                return countCompletedEquipmentTasks(fromDate, toDate, companyId, null);
            } else if ("employee".equalsIgnoreCase(taskType)) {
                return countCompletedEmployeeTasks(fromDate, toDate, companyId);
            } else {
                return countCompletedEquipmentTasks(fromDate, toDate, companyId, null) + 
                       countCompletedEmployeeTasks(fromDate, toDate, companyId);
            }
        } catch (Exception e) {
            log.error("Error counting completed tasks for type: {}, companyId: {}", taskType, companyId, e);
            throw new RuntimeException("Failed to count completed tasks: " + e.getMessage(), e);
        }
    }
    
    private Long countCompletedEquipmentTasks(Date fromDate, Date toDate, Integer companyId, Short equipmentCategoryId) {
        Session session = sessionFactory.getCurrentSession();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(DISTINCT t.id) ");
        sql.append("FROM task t ");
        sql.append("JOIN inspection_form_workflow ifw ON t.id = ifw.task ");
        sql.append("JOIN workflow_definition wd ON ifw.workflow_definition = wd.id ");
        sql.append("JOIN step s ON wd.step = s.id ");
        sql.append("WHERE s.code = :stepCode ");
        sql.append("AND t.is_active = 1 ");
   

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("stepCode", COMPLETED_STEP_CODE);
        
        if (fromDate != null) {
            sql.append("AND t.created_date >= :fromDate ");
            parameters.put("fromDate", fromDate);
        }
        
        if (toDate != null) {
            sql.append("AND t.created_date <= :toDate ");
            parameters.put("toDate", toDate);
        }
        
        if (companyId != null) {
            sql.append("AND t.company = :companyId ");
            parameters.put("companyId", companyId);
        }
        
        if (equipmentCategoryId != null) {
            sql.append("AND t.equipment_category = :equipmentCategoryId ");
            parameters.put("equipmentCategoryId", equipmentCategoryId);
        }
        
        Query query = session.createNativeQuery(sql.toString());
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
        
        return ((Number) query.getSingleResult()).longValue();
    }
    
    private Long countCompletedEmployeeTasks(Date fromDate, Date toDate, Integer companyId) {
        Session session = sessionFactory.getCurrentSession();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(DISTINCT t.id) ");
        sql.append("FROM task t ");
        sql.append("JOIN emp_certification_workflow ecw ON t.id = ecw.task ");
        sql.append("JOIN workflow_definition wd ON ecw.workflow_definition = wd.id ");
        sql.append("JOIN step s ON wd.step = s.id ");
        sql.append("WHERE s.code = :stepCode ");
        sql.append("AND t.is_active = 1 ");
     

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("stepCode", COMPLETED_STEP_CODE);
        
        if (fromDate != null) {
            sql.append("AND t.created_date >= :fromDate ");
            parameters.put("fromDate", fromDate);
        }
        
        if (toDate != null) {
            sql.append("AND t.created_date <= :toDate ");
            parameters.put("toDate", toDate);
        }
        
        if (companyId != null) {
            sql.append("AND t.company = :companyId ");
            parameters.put("companyId", companyId);
        }
        
        Query query = session.createNativeQuery(sql.toString());
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
        
        return ((Number) query.getSingleResult()).longValue();
    }
}