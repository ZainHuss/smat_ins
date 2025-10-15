package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.JobPosition;
import com.smat.ins.model.service.JobPositionService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingJobPositionBean implements Serializable {

	// #region "CodingProperties"
	private static final long serialVersionUID = 4600306662046762257L;
	
	private JobPosition jobPosition;

	private List<JobPosition> jobPositionList;

	private List<JobPosition> selectedJobPositionList;

	

	private JobPositionService jobPositionService;
	private LocalizationService localizationService;
	// #endregion
	@PostConstruct
	public void init() {

		try {
			jobPosition = new JobPosition();
			jobPositionList= new ArrayList<JobPosition>();
			selectedJobPositionList= new ArrayList<JobPosition>();
			jobPositionList=jobPositionService.findAll();
			
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
			e.printStackTrace();
		}
	}
	
	public CodingJobPositionBean() {
		jobPositionService = (JobPositionService) BeanUtility.getBean("jobPositionService");
		localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
	}
	
	@PreDestroy
	public void destroy() {

	}

	public void openNew() {

		this.jobPosition = new JobPosition();
	}

	public boolean doValidate() {
		boolean result = true;
		if (jobPosition.getArabicName() == null || jobPosition.getArabicName().trim().isEmpty()) {
			UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("arabicName") + "  "
					+ localizationService.getErrorMessage().getString("validateInput"));
			result = false;
		}

		return result;
	}
	
	public void save() {
		if (doValidate()) {
			try {
				jobPositionService.saveOrUpdate(jobPosition);
				jobPositionList=jobPositionService.findAll();
				UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-jobPosition");
				PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
			} catch (Exception e) {
				UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
				PrimeFaces.current().ajax().update("form:messages", "form:dataTable-jobPosition");
				e.printStackTrace();
			}
		}
	}

	public boolean hasSelectedJobPosition() {
		return this.selectedJobPositionList != null && !this.selectedJobPositionList.isEmpty();
	}
	
	public String getDeleteButtonMessage() {

		if (hasSelectedJobPosition()) {
			int size = this.selectedJobPositionList.size();
			return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("jobPositionsSelectedNum")
					: localizationService.getInterfaceLabel().getString("oneJobPositionSelected");
		}

		return localizationService.getInterfaceLabel().getString("delete");
	}

	public void deleteJobPosition() {
		try {

			jobPositionService.delete(jobPosition);
			this.jobPosition = null;
			jobPositionList = jobPositionService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-jobPosition");
			PrimeFaces.current().executeScript("PF('widgetVarJobPosition').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-jobPosition");
			e.printStackTrace();
		}
	}

	public void deleteSelectedJobPosition() {
		try {   
			jobPositionService.delete(selectedJobPositionList);
			this.selectedJobPositionList = null;
			jobPositionList = jobPositionService.findAll();
			UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-jobPosition");
			PrimeFaces.current().executeScript("PF('dtJobPositions').clearFilters()");
		} catch (Exception e) {
			UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
			PrimeFaces.current().ajax().update("form:messages", "form:dataTable-jobPosition");
			e.printStackTrace();
		}

	}

	public JobPosition getJobPosition() {
		return jobPosition;
	}

	public void setJobPosition(JobPosition jobPosition) {
		this.jobPosition = jobPosition;
	}

	public List<JobPosition> getJobPositionList() {
		return jobPositionList;
	}

	public void setJobPositionList(List<JobPosition> jobPositionList) {
		this.jobPositionList = jobPositionList;
	}

	public List<JobPosition> getSelectedJobPositionList() {
		return selectedJobPositionList;
	}

	public void setSelectedJobPositionList(List<JobPosition> selectedJobPositionList) {
		this.selectedJobPositionList = selectedJobPositionList;
	}



	
	
}
