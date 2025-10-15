package com.smat.ins.model.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.dao.CompletedTaskDao;
import com.smat.ins.model.dao.TaskDao;
import com.smat.ins.model.entity.Task;
import com.smat.ins.model.service.CompletedTaskService;

/**
 * Service implementation for managing completed tasks based on workflow completion status
 */
@Service("completedTaskService")
@Transactional
public class CompletedTaskServiceImpl extends GenericServiceImpl<Task,CompletedTaskDao, Integer> implements CompletedTaskService {

    private static final Logger log = LoggerFactory.getLogger(CompletedTaskServiceImpl.class);
    
  
   

	@Override
    public List<Map<String, Object>> getCompletedTasks(String taskType, Date fromDate, Date toDate, 
                                                       Integer companyId, Short equipmentCategoryId) {
        try {
            log.info("Retrieving completed tasks - Type: {}, From: {}, To: {}, Company: {}, Equipment Category: {}", 
                    taskType, fromDate, toDate, companyId, equipmentCategoryId);
            
            List<Map<String, Object>> results = dao.getCompletedTasks(
                taskType, fromDate, toDate, companyId, equipmentCategoryId);
            
            log.info("Retrieved {} completed tasks", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("Error retrieving completed tasks", e);
            throw new RuntimeException("Error retrieving completed tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getCompletedEquipmentTasks(Date fromDate, Date toDate, 
                                                               Integer companyId, Short equipmentCategoryId) {
        try {
            log.info("Retrieving completed equipment tasks - From: {}, To: {}, Company: {}, Equipment Category: {}", 
                    fromDate, toDate, companyId, equipmentCategoryId);
            
            List<Map<String, Object>> results = dao.getCompletedEquipmentTasks(
                fromDate, toDate, companyId, equipmentCategoryId);
            
            log.info("Retrieved {} completed equipment tasks", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("Error retrieving completed equipment tasks", e);
            throw new RuntimeException("Error retrieving completed equipment tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getCompletedEmployeeTasks(Date fromDate, Date toDate, Integer companyId) {
        try {
            log.info("Retrieving completed employee tasks - From: {}, To: {}, Company: {}", 
                    fromDate, toDate, companyId);
            
            List<Map<String, Object>> results = dao.getCompletedEmployeeTasks(
                fromDate, toDate, companyId);
            
            log.info("Retrieved {} completed employee tasks", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("Error retrieving completed employee tasks", e);
            throw new RuntimeException("Error retrieving completed employee tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public Long countCompletedTasks(String taskType, Date fromDate, Date toDate, Integer companyId) {
        try {
            log.info("Counting completed tasks - Type: {}, From: {}, To: {}, Company: {}", 
                    taskType, fromDate, toDate, companyId);
            
            Long count = dao.countCompletedTasks(taskType, fromDate, toDate, companyId);
            
            log.info("Found {} completed tasks", count);
            return count;
            
        } catch (Exception e) {
            log.error("Error counting completed tasks", e);
            throw new RuntimeException("Error counting completed tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getTaskCompletionStatistics(Date fromDate, Date toDate, Integer companyId) {
        try {
            log.info("Retrieving task completion statistics - From: {}, To: {}, Company: {}", 
                    fromDate, toDate, companyId);
            
            Map<String, Object> statistics = new HashMap<>();
            
            // Get counts for each task type
            Long equipmentTasksCount = dao.countCompletedTasks("equipment", fromDate, toDate, companyId);
            Long employeeTasksCount = dao.countCompletedTasks("employee", fromDate, toDate, companyId);
            Long totalTasksCount = equipmentTasksCount + employeeTasksCount;
            
            statistics.put("equipmentTasksCount", equipmentTasksCount);
            statistics.put("employeeTasksCount", employeeTasksCount);
            statistics.put("totalTasksCount", totalTasksCount);
            
            // Calculate percentages
            if (totalTasksCount > 0) {
                double equipmentPercentage = (equipmentTasksCount.doubleValue() / totalTasksCount.doubleValue()) * 100;
                double employeePercentage = (employeeTasksCount.doubleValue() / totalTasksCount.doubleValue()) * 100;
                
                statistics.put("equipmentTasksPercentage", Math.round(equipmentPercentage * 100.0) / 100.0);
                statistics.put("employeeTasksPercentage", Math.round(employeePercentage * 100.0) / 100.0);
            } else {
                statistics.put("equipmentTasksPercentage", 0.0);
                statistics.put("employeeTasksPercentage", 0.0);
            }
            
            statistics.put("fromDate", fromDate);
            statistics.put("toDate", toDate);
            statistics.put("companyId", companyId);
            
            log.info("Task completion statistics: {}", statistics);
            return statistics;
            
        } catch (Exception e) {
            log.error("Error retrieving task completion statistics", e);
            throw new RuntimeException("Error retrieving task completion statistics: " + e.getMessage(), e);
        }
    }
}

