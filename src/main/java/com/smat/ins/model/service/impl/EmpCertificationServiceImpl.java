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

// Import transactional if available in your stack
// For Java EE / Jakarta: import javax.transaction.Transactional;
// For Spring: import org.springframework.transaction.annotation.Transactional;

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
        return dao.getMaxCertNo();
    }

    @Override
    public Integer getMaxTimeSheetNo() {
        return dao.getMaxTimeSheetNo();
    }

    @Override
    public List<EmpCertification> getForReview() {
        return dao.getForReview();
    }

    @Override
    public EmpCertification getBy(Integer taskId) {
        return dao.getBy(taskId);
    }

    @Override
    public Boolean saveToStep(EmpCertification empCertification,Employee employee, EmpCertificationWorkflow empCertificationWorkflow,
                              EmpCertificationWorkflowStep empCertificationWorkflowStep) throws Exception {
        employeeDao.update(employee);
        dao.update(empCertification);
        empCertificationWorkflowDao.update(empCertificationWorkflow);
        empCertificationWorkflowStepDao.insert(empCertificationWorkflowStep);
        return true;
    }

    @Override
    public Boolean merge(EmpCertification empCertification, Employee employee)throws Exception {
        employeeDao.update(employee);
        dao.merge(empCertification);
        return true;
    }

    @Override
    public Boolean saveOrUpdate(EmpCertification empCertification, Employee employee) throws Exception {
        Employee employeeResult = employeeDao.insert(employee);
        empCertification.setEmployee(employeeResult);
        dao.insert(empCertification);
        return true;
    }

    @Override
    public EmpCertification findBy(Integer certId) {
        return dao.findBy(certId);
    }

    @Override
    public EmpCertification getByCertNumberAndTsNumber(String certNumber, String tsNumber) {
        return dao.getByCertNumberAndTsNumber(certNumber, tsNumber);
    }

    /**
     * NEW: wrapper to obtain the next cert sequence.
     * IMPORTANT: This method must be executed within a DB transaction.
     *
     * If you use Spring, annotate the method (or the class) with:
     *   @org.springframework.transaction.annotation.Transactional
     *
     * If you use Java EE / Jakarta, annotate with:
     *   @javax.transaction.Transactional
     *
     * Or configure transaction via EJB: @TransactionAttribute(REQUIRED)
     */
    @Override
    public Integer getNextCertSeq() {
        return dao.getNextCertSeq();
    }
}
