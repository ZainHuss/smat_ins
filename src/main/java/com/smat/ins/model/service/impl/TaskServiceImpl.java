package com.smat.ins.model.service.impl;

import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.dao.TaskDao;
import com.smat.ins.model.service.TaskService;
import com.smat.ins.model.entity.Task;
import org.springframework.transaction.annotation.Transactional;

public class TaskServiceImpl extends GenericServiceImpl<Task, TaskDao, Integer> implements TaskService {

    private static final long serialVersionUID = -6798528431618549773L;

    /**
     * منشئ مع معامل لـ TaskDao
     * @param dao كائن TaskDao
     */
    public TaskServiceImpl(TaskDao dao) {
        super();
        this.dao = dao; 
    }
    
    /**
     * منشئ افتراضي
     */
    public TaskServiceImpl() {
        super();
    }

    /**
     * تحديث حالة المهمة (جعلها فعالة أو غير فعالة)
     * @param taskId معرّف المهمة
     * @param status الحالة الجديدة (1 = فعالة، 0 = غير فعالة)
     */
   
 // TaskServiceImpl.java - Keep this implementation
    @Override
    @Transactional
    public void updateTaskStatus(int taskId, int status) {
        try {
            System.out.println("Service: Updating task " + taskId + " to active: " + (status == 1));
            Task task = dao.getById(taskId);
            
            if (task != null) {
                task.setIs_active(status == 1);
                System.out.println("Before update - isActive: " + task.getIs_active());
                dao.updateTaskStatus(taskId, status); // تأكد من أن هذا السطر غير معلق
                System.out.println("After update - isActive: " + task.getIs_active());
            } else {
                System.out.println("Task not found: " + taskId);
                throw new RuntimeException("Task not found with ID: " + taskId);
            }
        } catch (Exception e) {
            System.out.println("Service error: " + e.getMessage());
            throw new RuntimeException("Error updating task status: " + e.getMessage(), e);
        }
    }

    private void init() {
		// TODO Auto-generated method stub
		
	}

	/**
     * إلغاء مهمة (جعلها غير فعالة) - للحفاظ على التوافقية
     * @param taskId معرّف المهمة
     */
    
    public List<Task> getListInitialTaskByCorrespondence(Long correspondenceId, Long correspondenceRecipientId) {
        return dao.getListInitialTaskByCorrespondence(correspondenceId, correspondenceRecipientId);
    }

    @Override
    public List<Task> getListIntialTaskByRecepient(Long correspondenceRecipientId) {
        return dao.getListIntialTaskByRecepient(correspondenceRecipientId);
    }

    @Override
    public List<Task> getListReviewdTaskByCorrespondence(Long correspondenceId, Long reviewedUserId) {
        return dao.getListReviewdTaskByCorrespondence(correspondenceId, reviewedUserId);
    }

    @Override
    public List<Task> getListReviewedTask(Long reviewedUserId) {
        return dao.getListReviewedTask(reviewedUserId);
    }

    @Override
    public List<Task> searchTasks(String description, Boolean isCompleted, Boolean isDone, Date fromDate, Date toDate,
            Long assigneeId) {
        return dao.searchTasks(description, isCompleted, isDone, fromDate, toDate, assigneeId);
    }

    @Override
    public List<Task> getListInitialEmpTaskByCorrespondence(Long correspondenceId, Long correspondenceRecipientId) {
        return dao.getListInitialEmpTaskByCorrespondence(correspondenceId, correspondenceRecipientId);
    }

    @Override
    public List<Task> getListIntialEmpTaskByRecepient(Long correspondenceRecipientId) {
        return dao.getListIntialEmpTaskByRecepient(correspondenceRecipientId);
    }

    @Override
    public List<Task> getListReviewdEmpTaskByCorrespondence(Long correspondenceId, Long reviewedUserId) {
        return dao.getListReviewdEmpTaskByCorrespondence(correspondenceId, reviewedUserId);
    }

    @Override
    public List<Task> getListReviewedEmpTask(Long reviewedUserId) {
        return dao.getListReviewedEmpTask(reviewedUserId);
    }
}