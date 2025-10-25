package com.smat.ins.view.bean;


import com.itextpdf.text.pdf.qrcode.WriterException;
import com.smat.ins.model.entity.*;
import com.smat.ins.model.service.*;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.QRCodeGenerator;
import com.smat.ins.util.UtilityHelper;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.PrimeFaces;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class EmpCertificationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // Workflow properties
    private String step = "01";
    private String stepComment;
    private String permission;
    private String persistentMode;
    private String certIdStr;
    private String taskIdStr;

    private boolean disabled;
    private String comment;

    // Main entities
    private EmpCertification empCertification;
    private EmpCertificationWorkflow empCertificationWorkflow;
    private EmpCertificationWorkflowStep empCertificationWorkflowStep;

    // Related entities
    private EmpCertificationType empCertificationType;
    private Company company;
    private Employee employee;
    private List<Company> companies;
    private List<Employee> employees;
    private List<EmpCertificationType> empCertificationTypes;
    private Task task;
    private boolean viewOnly;


    // User Reviewer properties
    private List<UserAlias> userAliasRecipientList;
    private UserAlias selectedUserAliasRecipient;

    // Services (will be initialized via BeanUtility)
    private EmpCertificationService empCertificationService;
    private EmpCertificationWorkflowService empCertificationWorkflowService;
    private EmpCertificationWorkflowStepService empCertificationWorkflowStepService;
    private CompanyService companyService;
    private EmployeeService employeeService;
    private EmpCertificationTypeService empCertificationTypeService;
    private TaskService taskService;
    private UserAliasService userAliasService;
    private SysUserService sysUserService;

    @Inject
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        try {
            // Initialize services using BeanUtility
            empCertificationService = (EmpCertificationService) BeanUtility.getBean("empCertificationService");
            empCertificationWorkflowService = (EmpCertificationWorkflowService) BeanUtility.getBean("empCertificationWorkflowService");
            empCertificationWorkflowStepService = (EmpCertificationWorkflowStepService) BeanUtility.getBean("empCertificationWorkflowStepService");
            companyService = (CompanyService) BeanUtility.getBean("companyService");
            employeeService = (EmployeeService) BeanUtility.getBean("employeeService");
            empCertificationTypeService = (EmpCertificationTypeService) BeanUtility.getBean("empCertificationTypeService");
            taskService = (TaskService) BeanUtility.getBean("taskService");
            userAliasService = (UserAliasService) BeanUtility.getBean("userAliasService");
            sysUserService = (SysUserService) BeanUtility.getBean("sysUserService");
            String mode = UtilityHelper.getRequestParameter("mode");
            if ("view".equals(mode)) {
                viewOnly = true; // يجعل كل الحقول للعرض فقط
            }

            // Initialize from request parameters
            certIdStr = UtilityHelper.getRequestParameter("cert");
            taskIdStr = UtilityHelper.getRequestParameter("t");
            permission = UtilityHelper.getRequestParameter("p");
            persistentMode = UtilityHelper.getRequestParameter("m");

            if (permission != null) {
                permission = UtilityHelper.decipher(permission);
                disabled = permission.equalsIgnoreCase("readOnly");
            }

            if (taskIdStr != null) {
                task = taskService.findById(Integer.valueOf(UtilityHelper.decipher(taskIdStr)));
                if (task != null && task.getCompany() != null)
                    company = task.getCompany();
            }

            if (certIdStr != null) {
                Integer certId = Integer.valueOf(UtilityHelper.decipher(certIdStr));
                empCertification = empCertificationService.findBy(certId);
                employee = empCertification.getEmployee();
                empCertificationType = empCertification.getEmpCertificationType();
                if (empCertification != null) {
                    this.empCertificationWorkflow = empCertificationWorkflowService.getCurrentInspectionFormWorkFlow(empCertification.getId());
                    this.empCertificationWorkflowStep = empCertificationWorkflowStepService.getLastStep(empCertification.getId());
                    if (this.empCertificationWorkflowStep != null) {
                        stepComment = this.empCertificationWorkflowStep.getSysUserComment();
                    }
                    step = this.empCertificationWorkflow.getWorkflowDefinition().getStep().getCode();
                }
                // normalize dates immediately after load to avoid timezone shifts when reviewer views them
                normalizeDatesAfterLoad();
            } else if (persistentMode != null && UtilityHelper.decipher(persistentMode).equals("insert")) {
                empCertification = new EmpCertification();
                employee = new Employee();
                Integer maxCertNo = empCertificationService.getMaxCertNo();
                Integer tsNo = empCertificationService.getMaxTimeSheetNo();
                empCertification.setCertNumber("SMI/24RDIID/" + String.format("%07d", maxCertNo + 1));
                empCertification.setTsNumber("TS" + String.format("%05d", tsNo + 1));
                empCertification.setIssueDate(toNoon(Calendar.getInstance().getTime()));

            }

            companies = companyService.findAll();
            employees = employeeService.findAll();
            empCertificationTypes = empCertificationTypeService.findAll();

            userAliasRecipientList = new ArrayList<>();
            selectedUserAliasRecipient = new UserAlias();
            SysUser sysUserLogin = (SysUser) UtilityHelper.getSessionAttr("user");
            List<UserAlias> myUserAliasList = userAliasService.getBySysUser(sysUserLogin);
            if (myUserAliasList != null && !myUserAliasList.isEmpty()) {
                UserAlias userAliasOwner = myUserAliasList.get(0);
                List<UserAlias> userAliasRecipientListDb = userAliasService.getListRecipients(userAliasOwner);
                for (UserAlias userAlias : userAliasRecipientListDb) {
                    if (sysUserService.isUserHasPermission(userAlias.getSysUserBySysUser().getId(), "011"))
                        userAliasRecipientList.add(userAlias);
                }
            }
        } catch (Exception e) {
            UtilityHelper.addErrorMessage("Error initializing certification form");
            e.printStackTrace();
        }
    }

    public void assignCert(Integer taskId) {
        try {
            taskIdStr = UtilityHelper.cipher(taskId.toString());
            empCertification = empCertificationService.getBy(taskId);
            EmpCertificationWorkflow empCertificationWorkflow = null;
            if (empCertification != null) {
                certIdStr = UtilityHelper.cipher(empCertification.getId().toString());
                if (empCertification.getEmpCertificationWorkflows() != null) {
                    empCertificationWorkflow = (EmpCertificationWorkflow) empCertification.getEmpCertificationWorkflows().iterator().next();
                }
            }
            if (loginBean.hasSysPermission("011") && loginBean.hasSysPermission("010")) {
                if (empCertificationWorkflow == null) {
                    permission = UtilityHelper.cipher("editable");
                    persistentMode = UtilityHelper.cipher("insert");
                } else {
                    if (empCertificationWorkflow.getWorkflowDefinition().getStep().getCode().equals("02")) {
                        permission = UtilityHelper.cipher("readOnly");
                        persistentMode = UtilityHelper.cipher("changeStep");
                    }
                    if (empCertificationWorkflow.getWorkflowDefinition().getStep().getCode().equals("01")) {
                        permission = UtilityHelper.cipher("editable");
                        persistentMode = UtilityHelper.cipher("update");
                    }
                }
            } else if (loginBean.hasSysPermission("011")) {
                permission = UtilityHelper.cipher("readOnly");
                persistentMode = UtilityHelper.cipher("changeStep");
            } else if (loginBean.hasSysPermission("010")) {
                permission = UtilityHelper.cipher("editable");
                if (empCertification == null) {
                    persistentMode = UtilityHelper.cipher("insert");
                } else {
                    if (empCertificationWorkflow != null) {
                        if (empCertificationWorkflow.getWorkflowDefinition().getStep().getCode().equals("01"))
                            persistentMode = UtilityHelper.cipher("update");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String doSave() {
        // normalize before save to ensure DB gets noon-time
        empCertification.setIssueDate(toNoon(empCertification.getIssueDate()));
        empCertification.setExpiryDate(toNoon(empCertification.getExpiryDate()));
        if (empCertification.getEmployee() != null) {
            empCertification.getEmployee().setDateOfBirth(toNoon(empCertification.getEmployee().getDateOfBirth()));
        }

        if (!doValidate())
            return "";
        try {
            WorkflowDefinition workflowDefinitionInit = getInitWorkflowDefinition();
            WorkflowDefinition workflowDefinitionFinal = getFinalWorkflowDefinition();
            Set<EmpCertificationWorkflow> empCertificationWorkflows = new HashSet<>();
            Set<EmpCertificationWorkflowStep> empCertificationWorkflowSteps = new HashSet<>();
            empCertification.setEmpCertificationType(empCertificationType);
            if (task != null && task.getCompany() != null) {
                employee.setCompany(task.getCompany());
            }
            Short maxStepSeq;

            // ----------------- CHANGE STEP (Reviewer approves) - MODIFIED -----------------
            if (UtilityHelper.decipher(persistentMode).equals("changeStep")) {
                // get last seq (protect null -> treat as 0)
                maxStepSeq = empCertificationWorkflowStepService.getLastStepSeq(empCertification.getId());
                if (maxStepSeq == null) {
                    maxStepSeq = 0;
                }

                // mark reviewer on entity
                empCertification.setSysUserByReviewedBy(loginBean.getUser());

                // MERGE current changes first so any edits the reviewer made on bound fields are persisted
                try {
                    empCertificationService.merge(empCertification, employee);
                } catch (Exception mergeEx) {
                    // log and continue - saveToStep may still operate, but bubble up if it fails
                    mergeEx.printStackTrace();
                }

                // prepare workflow object (current)
                EmpCertificationWorkflow empCertificationWorkflow = empCertificationWorkflowService.getCurrentInspectionFormWorkFlow(empCertification.getId());
                if (empCertificationWorkflow == null) {
                    // defensive: create new workflow if somehow missing
                    empCertificationWorkflow = new EmpCertificationWorkflow();
                    empCertificationWorkflow.setEmpCertification(empCertification);
                    empCertificationWorkflow.setTask(task);
                }
                empCertificationWorkflow.setEmpCertification(empCertification);
                empCertificationWorkflow.setWorkflowDefinition(workflowDefinitionFinal);
                empCertificationWorkflow.setTask(task);

                // prepare workflow step
                EmpCertificationWorkflowStep empCertificationWorkflowStep = new EmpCertificationWorkflowStep();
                empCertificationWorkflowStep.setEmpCertification(empCertification);
                empCertificationWorkflowStep.setProcessDate(Calendar.getInstance().getTime());
                empCertificationWorkflowStep.setSysUser(loginBean.getUser());
                empCertificationWorkflowStep.setSysUserComment(comment);
                empCertificationWorkflowStep.setStepSeq((short) (maxStepSeq + 1));
                empCertificationWorkflowStep.setWorkflowDefinition(workflowDefinitionFinal);

                // persist step & workflow (this should keep merged changes)
                empCertificationService.saveToStep(empCertification, employee, empCertificationWorkflow, empCertificationWorkflowStep);

                // update local state
                step = "03";
                UtilityHelper.addInfoMessage("Operation successful");
                return "";
            }
            // ----------------- END changeStep -----------------

            // update existing (inspector -> send to reviewer)
            if (UtilityHelper.decipher(persistentMode).equals("update")) {
                maxStepSeq = empCertificationWorkflowStepService.getLastStepSeq(empCertification.getId());
                if (maxStepSeq == null) maxStepSeq = 0;

                // set reviewer (selected)
                if (selectedUserAliasRecipient != null && selectedUserAliasRecipient.getSysUserBySysUser() != null) {
                    empCertification.setSysUserByReviewedBy(selectedUserAliasRecipient.getSysUserBySysUser());
                }

                // update current workflow entry
                if (empCertification.getEmpCertificationWorkflows() != null && !empCertification.getEmpCertificationWorkflows().isEmpty()) {
                    EmpCertificationWorkflow currentWorkflow = (EmpCertificationWorkflow) empCertification.getEmpCertificationWorkflows().iterator().next();
                    currentWorkflow.setWorkflowDefinition(workflowDefinitionInit.getWorkflowDefinitionByNext());
                    if (selectedUserAliasRecipient != null) {
                        currentWorkflow.setSysUser(selectedUserAliasRecipient.getSysUserBySysUser());
                    }
                }

                EmpCertificationWorkflowStep empCertificationWorkflowStepTwo = new EmpCertificationWorkflowStep();
                empCertificationWorkflowStepTwo.setEmpCertification(empCertification);
                empCertificationWorkflowStepTwo.setWorkflowDefinition(workflowDefinitionInit.getWorkflowDefinitionByNext());
                empCertificationWorkflowStepTwo.setProcessDate(Calendar.getInstance().getTime());
                empCertificationWorkflowStepTwo.setSysUser(loginBean.getUser());
                empCertificationWorkflowStepTwo.setSysUserComment(comment);
                empCertificationWorkflowStepTwo.setStepSeq((short) (maxStepSeq + 1));
                empCertification.getEmpCertificationWorkflowSteps().add(empCertificationWorkflowStepTwo);

                // merge to save changes (including reviewer assignment)
                empCertificationService.merge(empCertification, employee);
                UtilityHelper.addInfoMessage("Operation successful");
                return "pretty:inspection/my-tasks";

            } else if (UtilityHelper.decipher(persistentMode).equals("insert")) {
                maxStepSeq = empCertificationWorkflowStepService.getLastStepSeq(null);
                if (maxStepSeq == null) maxStepSeq = 0;

                empCertification.setSysUserByInspectedBy(loginBean.getUser());

                EmpCertificationWorkflow empCertificationWorkflow = new EmpCertificationWorkflow();
                empCertificationWorkflow.setEmpCertification(empCertification);
                empCertificationWorkflow.setWorkflowDefinition(workflowDefinitionInit.getWorkflowDefinitionByNext());
                empCertificationWorkflow.setTask(task);
                if (selectedUserAliasRecipient != null && selectedUserAliasRecipient.getSysUserBySysUser() != null) {
                    empCertificationWorkflow.setSysUser(selectedUserAliasRecipient.getSysUserBySysUser());
                }
                empCertificationWorkflows.add(empCertificationWorkflow);
                empCertification.setEmpCertificationWorkflows(empCertificationWorkflows);

                EmpCertificationWorkflowStep empCertificationWorkflowStepOne = new EmpCertificationWorkflowStep();
                empCertificationWorkflowStepOne.setEmpCertification(empCertification);
                empCertificationWorkflowStepOne.setWorkflowDefinition(workflowDefinitionInit);
                empCertificationWorkflowStepOne.setProcessDate(Calendar.getInstance().getTime());
                empCertificationWorkflowStepOne.setSysUser(loginBean.getUser());
                empCertificationWorkflowStepOne.setSysUserComment(comment);
                empCertificationWorkflowStepOne.setStepSeq((short) (maxStepSeq + 1));
                empCertificationWorkflowSteps.add(empCertificationWorkflowStepOne);

                EmpCertificationWorkflowStep empCertificationWorkflowStepTwo = new EmpCertificationWorkflowStep();
                empCertificationWorkflowStepTwo.setEmpCertification(empCertification);
                empCertificationWorkflowStepTwo.setWorkflowDefinition(workflowDefinitionInit.getWorkflowDefinitionByNext());
                empCertificationWorkflowStepTwo.setProcessDate(Calendar.getInstance().getTime());
                empCertificationWorkflowStepTwo.setSysUser(loginBean.getUser());
                empCertificationWorkflowStepTwo.setSysUserComment(comment);
                empCertificationWorkflowStepTwo.setStepSeq((short) (empCertificationWorkflowStepOne.getStepSeq() + 1));
                empCertificationWorkflowSteps.add(empCertificationWorkflowStepTwo);

                empCertification.setEmpCertificationWorkflowSteps(empCertificationWorkflowSteps);
                empCertificationService.saveOrUpdate(empCertification, employee);
                UtilityHelper.addInfoMessage("Operation successful");
                return "pretty:inspection/my-tasks";
            }
        } catch (Exception e) {
            UtilityHelper.addErrorMessage("Operation failed");
            e.printStackTrace();
            return "";
        }
        return "";
    }


    public String returnToIssuer() {
    	// normalize before save to ensure DB gets noon-time
    	empCertification.setIssueDate(toNoon(empCertification.getIssueDate()));
    	empCertification.setExpiryDate(toNoon(empCertification.getExpiryDate()));
    	if (empCertification.getEmployee() != null) {
    	    empCertification.getEmployee().setDateOfBirth(toNoon(empCertification.getEmployee().getDateOfBirth()));
    	}

        if (!doValidate()) {
            return "";
        }
        try {
            Short maxStepSeq = empCertificationWorkflowStepService.getLastStepSeq(empCertification.getId());
            empCertification.setSysUserByIssuedBy(loginBean.getUser());
            empCertificationWorkflow.setWorkflowDefinition(getInitWorkflowDefinition());
            EmpCertificationWorkflowStep workflowStep = new EmpCertificationWorkflowStep();
            workflowStep.setEmpCertification(empCertification);
            workflowStep.setProcessDate(new Date());
            workflowStep.setSysUser(loginBean.getUser());
            workflowStep.setSysUserComment(comment);
            workflowStep.setStepSeq((short) (maxStepSeq + 1));
            workflowStep.setWorkflowDefinition(getInitWorkflowDefinition());
            empCertificationService.saveToStep(empCertification, employee, empCertificationWorkflow, workflowStep);
            UtilityHelper.addInfoMessage("Certification returned to issuer");
            return "pretty:inspection/my-tasks";
        } catch (Exception e) {
            UtilityHelper.addErrorMessage("Error returning certification");
            e.printStackTrace();
        }
        return "";
    }

    private String getServletContextPath(String relativePath) {
        return ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(relativePath);
    }

    public void doPrint() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            if (empCertification == null) {
                UtilityHelper.addErrorMessage("Certificate data is not available.");
                return;
            }
            if (empCertification.getEmployee() == null) {
                UtilityHelper.addErrorMessage("Employee data is not available.");
                return;
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CertNumber", safeString(empCertification.getCertNumber()));
            parameters.put("TsNumber", empCertification.getTsNumber());
            parameters.put("IssueDate", empCertification.getIssueDate());
            parameters.put("ExpiryDate", empCertification.getExpiryDate());
            parameters.put("EmployeeName", safeString(empCertification.getEmployee().getFullName()));
            parameters.put("CompanyName", empCertification.getEmployee().getCompany() != null ? safeString(empCertification.getEmployee().getCompany().getName()) : "");
            parameters.put("CertType", empCertification.getEmpCertificationType() != null ? safeString(empCertification.getEmpCertificationType().getCertName()) : "");
            parameters.put("EmployeeId", safeString(empCertification.getEmployee().getIdNumber()));

            if (empCertification.getEmployee().getEmployeePhoto() != null) {
                parameters.put("EmployeePhoto", new ByteArrayInputStream(empCertification.getEmployee().getEmployeePhoto()));
            } else {
                parameters.put("EmployeePhoto", null);
            }
            
        
            parameters.put("logoLeftPath", getServletContextPath("views/jasper/images/logo-left.png"));
            parameters.put("logoRightPath", getServletContextPath("views/jasper/images/logo-right.png"));

            String qrCodeData = UtilityHelper.getBaseURL() + "api/emp-cert/" + safeString(empCertification.getCertNumber()) + "&" + safeString(empCertification.getTsNumber());
            byte[] qrCodeImage = QRCodeGenerator.generateQrCodeImage(qrCodeData, 100, 100);
            if (qrCodeImage != null) {
                parameters.put("QRCodeImage", new ByteArrayInputStream(qrCodeImage));
            }

            String jasperPath = getServletContextPath("views/jasper/emp-certification.jasper");
            if (jasperPath == null) {
                UtilityHelper.addErrorMessage("Report template not found.");
                return;
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, new JREmptyDataSource());
            // Export to bytes
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            // Stream to response safely
            ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            response.reset();
            response.setContentType("application/pdf");
            String fileName = "EmployeeCertification_" + (empCertification.getCertNumber() != null ? empCertification.getCertNumber() : System.currentTimeMillis()) + ".pdf";
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            response.setContentLength(pdfBytes.length);

            try (ServletOutputStream out = response.getOutputStream()) {
                out.write(pdfBytes);
                out.flush();
            }

            facesContext.responseComplete();

        } catch (Exception e) {
            UtilityHelper.addErrorMessage("Failed to generate report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // helper
    private String safeString(Object o) {
        return o == null ? "" : String.valueOf(o);
    }
 // helper to set date time to noon to avoid timezone day-shift issues
    private Date toNoon(Date d) {
        if (d == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // normalize all relevant dates from empCertification and employee after load
    private void normalizeDatesAfterLoad() {
        if (empCertification == null) return;
        empCertification.setIssueDate(toNoon(empCertification.getIssueDate()));
        empCertification.setExpiryDate(toNoon(empCertification.getExpiryDate()));
        if (empCertification.getEmployee() != null) {
            empCertification.getEmployee().setDateOfBirth(toNoon(empCertification.getEmployee().getDateOfBirth()));
        }
    }

    
   


   

    private void exportReport(JasperPrint jasperPrint, String reportName) throws IOException, JRException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        try (ServletOutputStream servletOutputStream = response.getOutputStream()) {
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline; filename=" + reportName + "_" + System.currentTimeMillis() + ".pdf");
            JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void doPrintOld() {
        try {
            InputStream jasperStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/reports/emp-certification.jasper");
            if (jasperStream == null) {
                throw new Exception("Jasper template not found");
            }
            Map<String, Object> data = new HashMap<>();
            data.put("CertNumber", empCertification.getCertNumber());
            data.put("TsNumber", empCertification.getTsNumber());
            data.put("IssueDate", empCertification.getIssueDate());
            data.put("ExpiryDate", empCertification.getExpiryDate());
            data.put("EmployeeName", empCertification.getEmployee().getFullName());
            data.put("CompanyName", empCertification.getEmployee().getCompany().getName());
            data.put("CertType", empCertification.getEmpCertificationType().getCertName());
            data.put("IssuerName", empCertification.getSysUserByIssuedBy().getDisplayName());
            data.put("ReviewedBy", empCertification.getSysUserByReviewedBy() != null ? empCertification.getSysUserByReviewedBy().getDisplayName() : "");
            List<EmpCertification> dataList = Collections.singletonList(empCertification);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, data, dataSource);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            UtilityHelper.putSessionAttr("certPdfBytes", pdfBytes);
            PrimeFaces.current().ajax().update("form_viewer:manage-viewer-content");
            PrimeFaces.current().executeScript("PF('viewerWidgetVar').show();");
        } catch (Exception e) {
            UtilityHelper.addErrorMessage("Error generating certificate");
            e.printStackTrace();
        }
    }

    private WorkflowDefinition getFinalWorkflowDefinition() throws Exception {
        WorkflowDefinitionService workflowDefinitionService = (WorkflowDefinitionService) BeanUtility.getBean("workflowDefinitionService");
        return workflowDefinitionService.getFinalStep((short) 2);
    }

    private WorkflowDefinition getInitWorkflowDefinition() throws Exception {
        WorkflowDefinitionService workflowDefinitionService = (WorkflowDefinitionService) BeanUtility.getBean("workflowDefinitionService");
        return workflowDefinitionService.getInitStep((short) 2);
    }

    private boolean doValidate() {
        try {
            if (comment == null || comment.isEmpty()) {
                UtilityHelper.addErrorMessage("Please enter a comment");
                return false;
            }
            if (UtilityHelper.decipher(persistentMode).equals("insert")) {
                if (selectedUserAliasRecipient == null || selectedUserAliasRecipient.getSysUserBySysUser() == null) {
                    UtilityHelper.addErrorMessage("Please select a reviewer.");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Getters and setters
    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getStepComment() {
        return stepComment;
    }

    public void setStepComment(String stepComment) {
        this.stepComment = stepComment;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPersistentMode() {
        return persistentMode;
    }

    public void setPersistentMode(String persistentMode) {
        this.persistentMode = persistentMode;
    }

    public String getCertIdStr() {
        return certIdStr;
    }

    public void setCertIdStr(String certIdStr) {
        this.certIdStr = certIdStr;
    }

    public String getTaskIdStr() {
        return taskIdStr;
    }

    public void setTaskIdStr(String taskIdStr) {
        this.taskIdStr = taskIdStr;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public EmpCertification getEmpCertification() {
        return empCertification;
    }

    public void setEmpCertification(EmpCertification empCertification) {
        this.empCertification = empCertification;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<EmpCertificationType> getEmpCertificationTypes() {
        return empCertificationTypes;
    }

    public Task getTask() {
        return task;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<UserAlias> getUserAliasRecipientList() {
        return userAliasRecipientList;
    }

    public void setUserAliasRecipientList(List<UserAlias> userAliasRecipientList) {
        this.userAliasRecipientList = userAliasRecipientList;
    }

    public UserAlias getSelectedUserAliasRecipient() {
        return selectedUserAliasRecipient;
    }

    public void setSelectedUserAliasRecipient(UserAlias selectedUserAliasRecipient) {
        this.selectedUserAliasRecipient = selectedUserAliasRecipient;
    }

    public EmpCertificationType getEmpCertificationType() {
        return empCertificationType;
    }

    public void setEmpCertificationType(EmpCertificationType empCertificationType) {
        this.empCertificationType = empCertificationType;
    }

    public void setEmpCertificationTypes(List<EmpCertificationType> empCertificationTypes) {
        this.empCertificationTypes = empCertificationTypes;
    }
    public boolean isViewOnly() {
        return viewOnly;
    }

    private org.primefaces.model.DefaultStreamedContent photoStreamedContent;

    public void handlePhotoUpload(org.primefaces.event.FileUploadEvent event) {
        try {
           
            if (event.getFile() != null && event.getFile().getContent() != null) {
                if (event.getFile().getSize() > 2097152) {
                    UtilityHelper.addErrorMessage("File size exceeds 2MB limit");
                    return;
                }
                String contentType = event.getFile().getContentType();
                if (!contentType.startsWith("image/")) {
                    UtilityHelper.addErrorMessage("Invalid file format. Only images are allowed");
                    return;
                }
                if (employee == null) {
                    employee = new Employee();
                }
                employee.setEmployeePhoto(event.getFile().getContent());
                updatePhotoStreamedContent();
                UtilityHelper.addInfoMessage("Photo uploaded successfully");
            }
        } catch (Exception e) {
            UtilityHelper.addErrorMessage("Error uploading photo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public org.primefaces.model.DefaultStreamedContent getPhotoStreamedContent() {
        if (photoStreamedContent == null) {
            updatePhotoStreamedContent();
        }
        return photoStreamedContent;
    }

    private void updatePhotoStreamedContent() {
        try {
            if (employee != null && employee.getEmployeePhoto() != null) {
                ByteArrayInputStream stream = new ByteArrayInputStream(employee.getEmployeePhoto());
                photoStreamedContent = org.primefaces.model.DefaultStreamedContent.builder()
                        .contentType("image/jpeg")
                        .stream(() -> stream)
                        .build();
            } else {
                photoStreamedContent = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            photoStreamedContent = null;
        }
    }

    public void removePhoto() {
        try {
            if (employee != null) {
                employee.setEmployeePhoto(null);
                photoStreamedContent = null;
                UtilityHelper.addInfoMessage("Photo removed successfully");
            }
        } catch (Exception e) {
            UtilityHelper.addErrorMessage("Error removing photo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}