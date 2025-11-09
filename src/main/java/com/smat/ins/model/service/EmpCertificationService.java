package com.smat.ins.model.service;


import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.EmpCertification;
import com.smat.ins.model.entity.EmpCertificationWorkflow;
import com.smat.ins.model.entity.EmpCertificationWorkflowStep;
import com.smat.ins.model.entity.Employee;
import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.InspectionFormWorkflow;
import com.smat.ins.model.entity.InspectionFormWorkflowStep;

public interface EmpCertificationService extends GenericService<EmpCertification, Integer> {
    public Integer getMaxCertNo();

    public Integer getMaxTimeSheetNo();

    public List<EmpCertification> getForReview();
    public EmpCertification getBy(Integer taskId);

    public Boolean saveToStep(EmpCertification empCertification,Employee employee,EmpCertificationWorkflow empCertificationWorkflow,
                              EmpCertificationWorkflowStep empCertificationWorkflowStep) throws Exception;
    Integer getNextCertSeq();


    public Boolean merge(EmpCertification empCertification,Employee employee)throws Exception;

    public Boolean saveOrUpdate(EmpCertification empCertification,Employee employee)throws Exception;

    public EmpCertification findBy(Integer certId);

    public EmpCertification getByCertNumberAndTsNumber(String certNumber, String tsNumber);
}
