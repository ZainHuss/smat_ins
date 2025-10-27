package com.smat.ins.model.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.dao.CompletedTaskDao;
import com.smat.ins.model.entity.Task;
import com.smat.ins.model.service.CompletedTaskService;

/**
 * Service implementation for managing completed tasks based on workflow completion status
 *
 * Note: this implementation uses getDao() (inherited from GenericServiceImpl)
 * rather than relying on a locally @Autowired dao field, to avoid NullPointerExceptions
 * in environments where the local autowiring may not occur.
 */
@Service("completedTaskService")
@Transactional
public class CompletedTaskServiceImpl extends GenericServiceImpl<Task, CompletedTaskDao, Integer> implements CompletedTaskService {

    private static final Logger log = LoggerFactory.getLogger(CompletedTaskServiceImpl.class);

    public CompletedTaskServiceImpl() {
        super();
    }

    @Override
    public List<Map<String, Object>> getCompletedTasks(String taskType, Date fromDate, Date toDate,
                                                       Integer companyId, Short equipmentCategoryId) {
        try {
            log.info("Retrieving completed tasks - Type: {}, From: {}, To: {}, Company: {}, Equipment Category: {}",
                    taskType, fromDate, toDate, companyId, equipmentCategoryId);

            CompletedTaskDao theDao = getDaoOrThrow();
            List<Map<String, Object>> results = theDao.getCompletedTasks(
                    taskType, fromDate, toDate, companyId, equipmentCategoryId);

            log.info("Retrieved {} completed tasks", results == null ? 0 : results.size());
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

            CompletedTaskDao theDao = getDaoOrThrow();
            List<Map<String, Object>> results = theDao.getCompletedEquipmentTasks(
                    fromDate, toDate, companyId, equipmentCategoryId);

            log.info("Retrieved {} completed equipment tasks", results == null ? 0 : results.size());
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

            CompletedTaskDao theDao = getDaoOrThrow();
            List<Map<String, Object>> results = theDao.getCompletedEmployeeTasks(
                    fromDate, toDate, companyId);

            log.info("Retrieved {} completed employee tasks", results == null ? 0 : results.size());
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

            CompletedTaskDao theDao = getDaoOrThrow();
            Long count = theDao.countCompletedTasks(taskType, fromDate, toDate, companyId);

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

            CompletedTaskDao theDao = getDaoOrThrow();

            // Get counts for each task type
            Long equipmentTasksCount = theDao.countCompletedTasks("equipment", fromDate, toDate, companyId);
            Long employeeTasksCount = theDao.countCompletedTasks("employee", fromDate, toDate, companyId);
            Long totalTasksCount = (equipmentTasksCount == null ? 0L : equipmentTasksCount) + (employeeTasksCount == null ? 0L : employeeTasksCount);

            statistics.put("equipmentTasksCount", equipmentTasksCount == null ? 0L : equipmentTasksCount);
            statistics.put("employeeTasksCount", employeeTasksCount == null ? 0L : employeeTasksCount);
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

    /**
     * Backfill missing completed_date values for tasks whose workflows are at step '03'.
     * Delegates to DAO which performs the necessary updates.
     */
    @Override
    public void backfillMissingCompletedDates(Date fromDate, Date toDate, Integer companyId, Short equipmentCategoryId) {
        try {
            log.info("Backfilling missing completed_date values - From: {}, To: {}, Company: {}, Equipment Category: {}", fromDate, toDate, companyId, equipmentCategoryId);

            CompletedTaskDao theDao = getDaoOrThrow();
            theDao.backfillCompletedDates(fromDate, toDate, companyId, equipmentCategoryId);

        } catch (Exception e) {
            log.error("Error backfilling completed dates", e);
            throw new RuntimeException("Error backfilling completed dates: " + e.getMessage(), e);
        }
    }

    /**
     * Helper to safely obtain the DAO from the superclass and throw with explanatory message if missing.
     */
    private CompletedTaskDao getDaoOrThrow() {
        CompletedTaskDao theDao = null;
        try {
            // GenericServiceImpl typically exposes a protected field or getter.
            // Try getDao() method (common pattern). If not available, attempt to access protected field 'dao'.
            try {
                theDao = this.getClass().getMethod("getDao").invoke(this) == null ? null : (CompletedTaskDao) this.getClass().getMethod("getDao").invoke(this);
            } catch (NoSuchMethodException nsme) {
                // fallback: try direct field access (protected dao in GenericServiceImpl)
                try {
                    java.lang.reflect.Field f = GenericServiceImpl.class.getDeclaredField("dao");
                    f.setAccessible(true);
                    Object val = f.get(this);
                    theDao = (CompletedTaskDao) val;
                } catch (Exception inner) {
                    // ignore; will handle below
                }
            } catch (Exception ex) {
                // ignore here; we'll try reflection field access below
            }

            if (theDao == null) {
                // final attempt using super's generic protected field 'dao'
                try {
                    java.lang.reflect.Field f = GenericServiceImpl.class.getDeclaredField("dao");
                    f.setAccessible(true);
                    Object val = f.get(this);
                    theDao = (CompletedTaskDao) val;
                } catch (Exception e) {
                    // nothing else we can do programmatically
                    log.error("Unable to obtain CompletedTaskDao from GenericServiceImpl; please ensure GenericServiceImpl provides 'getDao()' or protected 'dao' field.", e);
                    throw new IllegalStateException("CompletedTaskDao not available (injection/initialization problem). See logs for details.");
                }
            }

            return theDao;
        } catch (Exception e) {
            // convert any reflection errors into a runtime explanatory exception
            throw new IllegalStateException("Failed to obtain CompletedTaskDao instance: " + e.getMessage(), e);
        }
    }
}
