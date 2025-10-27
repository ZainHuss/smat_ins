package com.smat.ins.model.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.Task;

/**
 * DAO interface for retrieving completed tasks based on workflow completion status
 */
public interface CompletedTaskDao extends GenericDao<Task, Integer> {

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
     * Backfill completed_date for tasks whose workflows are at the completed step (code '03')
     * but have NULL completed_date in the task table. This updates both equipment and
     * employee workflows where applicable.
     *
     * @param fromDate optional filter (applies to t.created_date >= fromDate)
     * @param toDate optional filter (applies to t.created_date <= toDate)
     * @param companyId optional filter
     * @param equipmentCategoryId optional filter (applies to equipment tasks)
     */
    void backfillCompletedDates(Date fromDate, Date toDate, Integer companyId, Short equipmentCategoryId);
}
