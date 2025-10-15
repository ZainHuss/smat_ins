package com.smat.ins.model.dao;

import java.util.Date;
import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.Task;

public interface TaskDao extends GenericDao<Task, Integer> {

	public List<Task> getListInitialTaskByCorrespondence(Long correspondenceId, Long correspondenceRecipientId);

	public List<Task> getListIntialTaskByRecepient(Long correspondenceRecipientId);
	
	public List<Task> getListReviewdTaskByCorrespondence(Long correspondenceId,Long reviewedUserId);

	public List<Task> getListReviewedTask(Long reviewedUserId);
	
	public List<Task> searchTasks(String description, Boolean isCompleted, Boolean isDone, 
            Date fromDate, Date toDate, Long assigneeId);
	
	public List<Task> getListInitialEmpTaskByCorrespondence(Long correspondenceId, Long correspondenceRecipientId);

	public List<Task> getListIntialEmpTaskByRecepient(Long correspondenceRecipientId);
	
	public List<Task> getListReviewdEmpTaskByCorrespondence(Long correspondenceId,Long reviewedUserId);

	public List<Task> getListReviewedEmpTask(Long reviewedUserId);
    void updateTaskStatus(int taskId, int status);

}
