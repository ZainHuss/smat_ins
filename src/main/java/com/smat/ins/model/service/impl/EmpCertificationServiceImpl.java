package com.smat.ins.model.service.impl;


import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.dao.EmpCertificationDao;
import com.smat.ins.model.dao.EmpCertificationWorkflowDao;
import com.smat.ins.model.dao.EmpCertificationWorkflowStepDao;
import com.smat.ins.model.dao.EmployeeDao;
import com.smat.ins.model.entity.EmpCertification;
import com.smat.ins.model.entity.EmpCertificationWorkflow;
import com.smat.ins.model.entity.EmpCertificationWorkflowStep;
import com.smat.ins.model.entity.Employee;
import com.smat.ins.model.service.EmpCertificationService;

public class EmpCertificationServiceImpl extends GenericServiceImpl<EmpCertification,EmpCertificationDao, Integer> implements EmpCertificationService {

    private EmployeeDao employeeDao;
    private EmpCertificationWorkflowDao empCertificationWorkflowDao;

    private EmpCertificationWorkflowStepDao empCertificationWorkflowStepDao;



    public EmployeeDao getEmployeeDao() {
        return employeeDao;
    }

    public void setEmployeeDao(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public EmpCertificationWorkflowDao getEmpCertificationWorkflowDao() {
        return empCertificationWorkflowDao;
    }

    public void setEmpCertificationWorkflowDao(EmpCertificationWorkflowDao empCertificationWorkflowDao) {
        this.empCertificationWorkflowDao = empCertificationWorkflowDao;
    }

    public EmpCertificationWorkflowStepDao getEmpCertificationWorkflowStepDao() {
        return empCertificationWorkflowStepDao;
    }

    public void setEmpCertificationWorkflowStepDao(EmpCertificationWorkflowStepDao empCertificationWorkflowStepDao) {
        this.empCertificationWorkflowStepDao = empCertificationWorkflowStepDao;
    }

    @Override
    public Integer getMaxCertNo() {
        // TODO Auto-generated method stub
        return dao.getMaxCertNo();
    }

    @Override
    public Integer getMaxTimeSheetNo() {
        // TODO Auto-generated method stub
        return dao.getMaxTimeSheetNo();
    }

    @Override
    public List<EmpCertification> getForReview() {
        // TODO Auto-generated method stub
        return dao.getForReview();
    }

    @Override
    public EmpCertification getBy(Integer taskId) {
        // TODO Auto-generated method stub
        return dao.getBy(taskId);
    }

    @Override
    public Boolean saveToStep(EmpCertification empCertification,Employee employee, EmpCertificationWorkflow empCertificationWorkflow,
                              EmpCertificationWorkflowStep empCertificationWorkflowStep) throws Exception {
        // TODO Auto-generated method stub

        employeeDao.update(employee);
        dao.update(empCertification);
        empCertificationWorkflowDao.update(empCertificationWorkflow);
        empCertificationWorkflowStepDao.insert(empCertificationWorkflowStep);
        return true;
    }

    @Override
    public Boolean merge(EmpCertification empCertification, Employee employee)throws Exception {
        // TODO Auto-generated method stub

        employeeDao.update(employee);
        dao.merge(empCertification);
        return true;
    }

    @Override
    public Boolean saveOrUpdate(EmpCertification empCertification, Employee employee) throws Exception {
        // TODO Auto-generated method stub
        Employee employeeResult=employeeDao.insert(employee);
        empCertification.setEmployee(employeeResult);
        dao.insert(empCertification);
        return true;
    }

    @Override
    public EmpCertification findBy(Integer certId) {
        // TODO Auto-generated method stub
        return dao.findBy(certId);
    }

    @Override
    public EmpCertification getByCertNumberAndTsNumber(String certNumber, String tsNumber) {
        // TODO Auto-generated method stub
        return dao.getByCertNumberAndTsNumber(certNumber, tsNumber);
    }

}
