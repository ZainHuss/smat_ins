package com.smat.ins.model.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.Task;

/**
 * Service interface for managing completed tasks based on workflow completion status
 */
public interface CompletedTaskService extends GenericService<Task, Integer> {
    
    /**
     * Retrieves completed tasks for both equipment inspection and employee certification
     * A task is considered completed if its workflow has reached step code '03'
     * 
     * @param taskType Filter by task type: "equipment", "employee", or null for both
     * @param fromDate Filter tasks created from this date (optional)
     * @param toDate Filter tasks created until this date (optional)
     * @param companyId Filter by company ID (optional)
     * @param equipmentCategoryId Filter by equipment category ID (optional, for equipment tasks only)
     * @return List of completed tasks with additional metadata
     */
    List<Map<String, Object>> getCompletedTasks(String taskType, Date fromDate, Date toDate, 
                                               Integer companyId, Short equipmentCategoryId);
    
    /**
     * Retrieves completed equipment inspection tasks only
     * 
     * @param fromDate Filter tasks created from this date (optional)
     * @param toDate Filter tasks created until this date (optional)
     * @param companyId Filter by company ID (optional)
     * @param equipmentCategoryId Filter by equipment category ID (optional)
     * @return List of completed equipment inspection tasks
     */
    List<Map<String, Object>> getCompletedEquipmentTasks(Date fromDate, Date toDate, 
                                                         Integer companyId, Short equipmentCategoryId);
    
    /**
     * Retrieves completed employee certification tasks only
     * 
     * @param fromDate Filter tasks created from this date (optional)
     * @param toDate Filter tasks created until this date (optional)
     * @param companyId Filter by company ID (optional)
     * @return List of completed employee certification tasks
     */
    List<Map<String, Object>> getCompletedEmployeeTasks(Date fromDate, Date toDate, Integer companyId);
    
    /**
     * Count completed tasks by type
     * 
     * @param taskType Filter by task type: "equipment", "employee", or null for both
     * @param fromDate Filter tasks created from this date (optional)
     * @param toDate Filter tasks created until this date (optional)
     * @param companyId Filter by company ID (optional)
     * @return Count of completed tasks
     */
    Long countCompletedTasks(String taskType, Date fromDate, Date toDate, Integer companyId);
    
    /**
     * Get task completion statistics
     * 
     * @param fromDate Filter tasks created from this date (optional)
     * @param toDate Filter tasks created until this date (optional)
     * @param companyId Filter by company ID (optional)
     * @return Map containing completion statistics
     */
    Map<String, Object> getTaskCompletionStatistics(Date fromDate, Date toDate, Integer companyId);
}

