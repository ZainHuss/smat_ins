package com.smat.ins.view.bean;


import com.itextpdf.text.pdf.*;
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
// iText imports for direct PDF generation
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;

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
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.Serializable;
import java.util.*;
import com.google.gson.Gson;
// imports required
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.faces.event.ActionEvent;
import com.smat.ins.model.entity.TaskDraft;
import com.smat.ins.model.service.TaskDraftService;



@Named
@ViewScoped
public class EmpCertificationBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private TaskDraftService taskDraftService;
    private Gson gson = new Gson();
    private ObjectMapper objectMapper = new ObjectMapper();


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
            taskDraftService = (TaskDraftService) BeanUtility.getBean("taskDraftService");

            String mode = UtilityHelper.getRequestParameter("mode");
            if ("view".equals(mode)) {
                viewOnly = true; // يجعل كل الحقول للعرض فقط
            }

            // Initialize from request parameters
            certIdStr = UtilityHelper.getRequestParameter("cert");
            taskIdStr = UtilityHelper.getRequestParameter("t");
            permission = UtilityHelper.getRequestParameter("p");
            persistentMode = UtilityHelper.getRequestParameter("m");

            // decode permission safely
            if (permission != null) {
                try {
                    permission = UtilityHelper.decipher(permission);
                    disabled = permission.equalsIgnoreCase("readOnly");
                } catch (Exception ex) {
                    // إذا فشلت عملية فك التشفير فاترك القيمة كما هي
                    ex.printStackTrace();
                }
            }

            if (taskIdStr != null) {
                try {
                    task = taskService.findById(Integer.valueOf(UtilityHelper.decipher(taskIdStr)));
                    if (task != null && task.getCompany() != null)
                        company = task.getCompany();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            // decode persistent mode once for safety
            String decodedPersistentMode = null;
            if (persistentMode != null) {
                try {
                    decodedPersistentMode = UtilityHelper.decipher(persistentMode);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (certIdStr != null) {
                // عرض/تعديل شهادة موجودة
                try {
                    Integer certId = Integer.valueOf(UtilityHelper.decipher(certIdStr));
                    empCertification = empCertificationService.findBy(certId);
                    if (empCertification != null) {
                        employee = empCertification.getEmployee();
                        empCertificationType = empCertification.getEmpCertificationType();
                        this.empCertificationWorkflow = empCertificationWorkflowService.getCurrentInspectionFormWorkFlow(empCertification.getId());
                        this.empCertificationWorkflowStep = empCertificationWorkflowStepService.getLastStep(empCertification.getId());
                        if (this.empCertificationWorkflowStep != null) {
                            stepComment = this.empCertificationWorkflowStep.getSysUserComment();
                        }
                        if (this.empCertificationWorkflow != null) {
                            step = this.empCertificationWorkflow.getWorkflowDefinition().getStep().getCode();
                        }
                        // normalize dates immediately after load to avoid timezone shifts when reviewer views them
                        normalizeDatesAfterLoad();
                    } else {
                        // create empty employee to avoid NPE in view
                        employee = new Employee();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    UtilityHelper.addErrorMessage("Error loading certification record");
                }
            } else if ("insert".equals(decodedPersistentMode)) {
                // إنشاء شهادة جديدة - يجب توليد certNumber بطريقة آمنة
                empCertification = new EmpCertification();
                employee = new Employee();

                Integer nextSeq = null;
                try {
                    // If this page is opened for a task, try to reserve a cert number bound to the task
                    try {
                        com.smat.ins.model.service.SeqReservationService seqReservationService = (com.smat.ins.model.service.SeqReservationService) BeanUtility.getBean("seqReservationService");
                        if (task != null && seqReservationService != null) {
                            Integer reserved = seqReservationService.getReservedCertNoForTask(task.getId());
                            if (reserved == null) {
                                Long reservedBy = null;
                                try { if (loginBean != null && loginBean.getUser() != null) reservedBy = loginBean.getUser().getId(); } catch (Exception ignore) {}
                                reserved = seqReservationService.reserveCertNoForTask(task.getId(), reservedBy);
                            }
                            if (reserved != null) {
                                nextSeq = reserved;
                            }
                        }
                    } catch (Exception ex) {
                        // reservation service not available or failed - fallback below
                        ex.printStackTrace();
                    }

                    if (nextSeq == null) {
                        // هذا النداء يجب أن يتم داخل معاملة DB (@Transactional على الـ Service أو الميثود)
                        nextSeq = empCertificationService.getNextCertSeq();
                    }
                } catch (Exception ex) {
                    // لو فشلت لأي سبب (مثلاً عدم تفعيل المعاملات) سنستخدم fallback الآمن بالـ MAX
                    ex.printStackTrace();
                }

                if (nextSeq != null) {
                    empCertification.setCertNumber(buildCertPrefix(task) + String.format("%07d", nextSeq));
                } else {
                    // fallback: الطريقة القديمة بالـ MAX
                    try {
                        Integer maxCertNo = empCertificationService.getMaxCertNo();
                        int fallback = (maxCertNo == null) ? 1 : (maxCertNo + 1);
                        empCertification.setCertNumber(buildCertPrefix(task) + String.format("%07d", fallback));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        // كآخر حل: استخدم 1
                        empCertification.setCertNumber(buildCertPrefix(task) + String.format("%07d", 1));
                    }
                }

                // Timesheet number (كما كان)
                try {
                    Integer tsNo = empCertificationService.getMaxTimeSheetNo();
                    int tsVal = (tsNo == null) ? 1 : (tsNo + 1);
                    empCertification.setTsNumber("TS" + String.format("%05d", tsVal));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    empCertification.setTsNumber("TS" + String.format("%05d", 1));
                }

                // Issue date default
                empCertification.setIssueDate(toNoon(Calendar.getInstance().getTime()));
            } // end create new

            // تحميل القوائم العامة
            try {
                companies = companyService.findAll();
            } catch (Exception ex) {
                companies = new ArrayList<>();
                ex.printStackTrace();
            }
            try {
                employees = employeeService.findAll();
            } catch (Exception ex) {
                employees = new ArrayList<>();
                ex.printStackTrace();
            }
            try {
                empCertificationTypes = empCertificationTypeService.findAll();
            } catch (Exception ex) {
                empCertificationTypes = new ArrayList<>();
                ex.printStackTrace();
            }

            // إعداد قائمة المستقبلين (user aliases) كما في السابق
            userAliasRecipientList = new ArrayList<>();
            selectedUserAliasRecipient = new UserAlias();
            SysUser sysUserLogin = (SysUser) UtilityHelper.getSessionAttr("user");
            try {
                java.util.List<com.smat.ins.model.entity.SysUser> reviewers = sysUserService
                        .listUserHasPersmission("011");
                if (reviewers != null) {
                    for (com.smat.ins.model.entity.SysUser su : reviewers) {
                        try {
                            java.util.List<UserAlias> aliases = userAliasService.getBySysUser(su);
                            if (aliases != null && !aliases.isEmpty()) {
                                userAliasRecipientList.addAll(aliases);
                            }
                        } catch (Exception ignore) {
                            // continue on per-user failures
                        }
                    }
                }
            } catch (Exception e) {
                // fallback to previous behavior (organization-based recipients filtered by permission)
                try {
                    List<UserAlias> myUserAliasList = userAliasService.getBySysUser(sysUserLogin);
                    if (myUserAliasList != null && !myUserAliasList.isEmpty()) {
                        UserAlias userAliasOwner = myUserAliasList.get(0);
                        List<UserAlias> userAliasRecipientListDb = userAliasService.getListRecipients(userAliasOwner);
                        for (UserAlias userAlias : userAliasRecipientListDb) {
                            if (sysUserService.isUserHasPermission(userAlias.getSysUserBySysUser().getId(), "011"))
                                userAliasRecipientList.add(userAlias);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
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

    // Build certification prefix in format
    private String buildCertPrefix(Task task) {
        try {
            int yy = Calendar.getInstance().get(Calendar.YEAR) % 100;
            String year = String.format("%02d", yy);

            String orgShortcut = "XX";

            if (task != null && task.getUserAliasByAssignTo() != null) {
                UserAlias ua = task.getUserAliasByAssignTo();
                Organization selected = null;
                Organization rootCandidate = null;

                // Prefer organizationByOrganization from multiple sources; collect a root candidate but use it only if no specific org found
                try {
                    // 1) Prefer alias' SysUser -> organization (this is the actual inspector's organization)
                    try {
                        SysUser aliasUser = ua.getSysUserBySysUser();
                        if (aliasUser != null) {
                            try { Organization o2 = aliasUser.getOrganizationByOrganization(); if (o2 != null) selected = o2; } catch (Exception ignore) {}
                            try { Organization r2 = aliasUser.getOrganizationByRootOrganization(); if (r2 != null && rootCandidate == null) rootCandidate = r2; } catch (Exception ignore) {}
                        }
                    } catch (Exception ignore) {}

                    // 2) Alias -> organization (specific on alias level) - used only if alias's sysuser has no specific org
                    try { Organization o = ua.getOrganizationByOrganization(); if (o != null && selected == null) selected = o; } catch (Exception ignore) {}
                    // keep root candidate from alias if not set
                    try { Organization r = ua.getOrganizationByRootOrganization(); if (r != null && rootCandidate == null) rootCandidate = r; } catch (Exception ignore) {}

                    // 3) Task's sysUser -> organization
                    try {
                        SysUser taskUser = task.getSysUser();
                        if (taskUser != null && selected == null) {
                            try { Organization o3 = taskUser.getOrganizationByOrganization(); if (o3 != null) selected = o3; } catch (Exception ignore) {}
                            try { Organization r3 = taskUser.getOrganizationByRootOrganization(); if (r3 != null && rootCandidate == null) rootCandidate = r3; } catch (Exception ignore) {}
                        }
                    } catch (Exception ignore) {}

                    // 4) Logged-in user -> organization
                    try {
                        SysUser logged = (loginBean != null) ? loginBean.getUser() : null;
                        if (logged != null && selected == null) {
                            try { Organization o4 = logged.getOrganizationByOrganization(); if (o4 != null) selected = o4; } catch (Exception ignore) {}
                            try { Organization r4 = logged.getOrganizationByRootOrganization(); if (r4 != null && rootCandidate == null) rootCandidate = r4; } catch (Exception ignore) {}
                        }
                    } catch (Exception ignore) {}
                } catch (Exception ignore) {}

                // If we couldn't find a specific org, use the root candidate (last resort)
                Organization org = (selected != null) ? selected : rootCandidate;

                // debug output removed

                if (org != null) {
                    try {
                        // Prefer explicit alphabetic code if available and contains letters
                        String code = org.getCode();
                        if (code != null) code = code.trim();
                        if (code != null && !code.isEmpty() && code.matches(".*[A-Za-z].*")) {
                            // keep letters/numbers but prefer letters; take first two letters if possible
                            String lettersOnly = code.replaceAll("[^A-Za-z]", "").toUpperCase();
                            if (lettersOnly.length() >= 2) orgShortcut = lettersOnly.substring(0,2);
                            else if (lettersOnly.length() == 1) orgShortcut = lettersOnly + "X";
                            else {
                                // fallback to first two alnum
                                String alnum = code.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
                                if (alnum.length() >= 2) orgShortcut = alnum.substring(0,2);
                                else if (alnum.length() == 1) orgShortcut = alnum + "X";
                            }
                        } else {
                            // derive from name (english preferred)
                            String name = org.getEnglishName() != null && !org.getEnglishName().trim().isEmpty() ? org.getEnglishName() : org.getArabicName();
                            if (name != null && !name.trim().isEmpty()) {
                                // First: check explicit branch suffix after '-' and map to provided shortcuts
                                try {
                                    String lower = name.toLowerCase();
                                    String branchKey = null;
                                    if (lower.contains("-")) {
                                        branchKey = lower.substring(lower.lastIndexOf('-') + 1).trim();
                                    }
                                    if (branchKey != null && !branchKey.isEmpty()) {
                                        if (branchKey.contains("khobar")) {
                                            orgShortcut = "KH";
                                        } else if (branchKey.contains("riyadh")) {
                                            orgShortcut = "RD";
                                        } else if (branchKey.contains("jeddah")) {
                                            orgShortcut = "JD";
                                        } else if (branchKey.contains("neom")) {
                                            orgShortcut = "NE";
                                        }
                                    }
                                } catch (Exception ignore) {}

                                // If mapping not applied, fallback to previous heuristic
                                if (orgShortcut == null || orgShortcut.equals("XX")) {
                                    String clean = name.trim().replaceAll("[^A-Za-z ]", "").toUpperCase();
                                    String[] parts = clean.split("\\s+");
                                    if (parts.length >= 2) {
                                        orgShortcut = (parts[0].substring(0,1) + parts[1].substring(0,1)).toUpperCase();
                                    } else {
                                        String s = parts[0];
                                        if (s.length() >= 2) {
                                            orgShortcut = (s.substring(0,1) + s.substring(1,2)).toUpperCase();
                                        } else if (s.length() == 1) {
                                            orgShortcut = (s + s).toUpperCase();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        orgShortcut = "XX";
                    }
                }
            }

            // normalize to 2 alphabetic chars; ensure letters (fallback to XX)
            if (orgShortcut == null) orgShortcut = "XX";
            orgShortcut = orgShortcut.replaceAll("[^A-Z]", "");
            if (orgShortcut.length() >= 2) orgShortcut = orgShortcut.substring(0,2);
            if (orgShortcut.length() == 1) orgShortcut = orgShortcut + "X";
            if (orgShortcut.length() == 0) orgShortcut = "XX";

            return "ID/" + year + orgShortcut + "/";
        } catch (Exception e) {
            return "ID/" + String.format("%02d", Calendar.getInstance().get(Calendar.YEAR) % 100) + "XX/";
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
                empCertificationWorkflowStep.setSysUserComment(comment != null ? comment : "");
                empCertificationWorkflowStep.setStepSeq((short) (maxStepSeq + 1));
                empCertificationWorkflowStep.setWorkflowDefinition(workflowDefinitionFinal);

                // persist step & workflow (this should keep merged changes)
                empCertificationService.saveToStep(empCertification, employee, empCertificationWorkflow, empCertificationWorkflowStep);

                // After reviewer approves, generate the employee certificate PDF and save it
                // into the employee attachments folder (EMP-DEFAULT / drawer 01 / folder by certNumber)
                try {
                    // build Jasper parameters same as doPrint()
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

                    byte[] pdfBytes = null;
                    try {
                        // prefer iText generator implemented in this bean
                        pdfBytes = generateCertificatePdfBytes(parameters);
                    } catch (Exception genEx) {
                        genEx.printStackTrace();
                        // fallback to existing Jasper if generator fails
                        try {
                            String jasperPath = getServletContextPath("views/jasper/emp-certification.jasper");
                            if (jasperPath != null) {
                                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, new JREmptyDataSource());
                                pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
                            }
                        } catch (Exception jex) {
                            jex.printStackTrace();
                        }
                    }

                    if (pdfBytes != null) {
                        // put bytes into session so viewer can display the newly generated PDF
                        try {
                            UtilityHelper.putSessionAttr("certPdfBytes", pdfBytes);
                        } catch (Exception sessEx) {
                            sessEx.printStackTrace();
                        }

                        // store pdfBytes as attachment (reuse upload storage pattern)
                        try {
                            com.smat.ins.model.service.CabinetService cabinetService = (com.smat.ins.model.service.CabinetService) BeanUtility.getBean("cabinetService");
                            com.smat.ins.model.service.CabinetFolderService cabinetFolderService = (com.smat.ins.model.service.CabinetFolderService) BeanUtility.getBean("cabinetFolderService");
                            com.smat.ins.model.service.ArchiveDocumentService archiveDocumentService = (com.smat.ins.model.service.ArchiveDocumentService) BeanUtility.getBean("archiveDocumentService");
                            com.smat.ins.model.service.ArchiveDocumentFileService archiveDocumentFileService = (com.smat.ins.model.service.ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");
                            com.smat.ins.model.service.CabinetFolderDocumentService cabinetFolderDocumentService = (com.smat.ins.model.service.CabinetFolderDocumentService) BeanUtility.getBean("cabinetFolderDocumentService");

                            String targetCabinetCode = "EMP-DEFAULT";
                            com.smat.ins.model.entity.Cabinet targetCabinet = null;
                            for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                                if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
                            }
                            if (targetCabinet == null) {
                                com.smat.ins.util.CabinetDefaultsCreator.ensureDefaultCabinets(loginBean.getUser());
                                for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                                    if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
                                }
                            }
                            if (targetCabinet != null) {
                                com.smat.ins.model.entity.CabinetDefinition def = null;
                                if (targetCabinet.getCabinetDefinitions() != null) {
                                    for (Object od : targetCabinet.getCabinetDefinitions()) {
                                        com.smat.ins.model.entity.CabinetDefinition cd = (com.smat.ins.model.entity.CabinetDefinition) od;
                                        if ("01".equals(cd.getCode())) { def = cd; break; }
                                    }
                                }
                                if (def == null && targetCabinet.getCabinetDefinitions() != null && !targetCabinet.getCabinetDefinitions().isEmpty())
                                    def = (com.smat.ins.model.entity.CabinetDefinition) targetCabinet.getCabinetDefinitions().iterator().next();
                                if (def != null) {
                                    String folderName = empCertification.getCertNumber() != null ? empCertification.getCertNumber().trim() : (empCertification.getId() != null ? "emp_" + empCertification.getId() : "emp_" + System.currentTimeMillis());
                                    com.smat.ins.model.entity.CabinetFolder cabinetFolder = null;
                                    try {
                                        java.util.List<com.smat.ins.model.entity.CabinetFolder> existing = cabinetFolderService.getByCabinetDefinition(def);
                                        if (existing != null) {
                                            for (com.smat.ins.model.entity.CabinetFolder f : existing) {
                                                String fn = folderName == null ? "" : folderName.trim().toLowerCase();
                                                String fa = f.getArabicName() == null ? "" : f.getArabicName().trim().toLowerCase();
                                                String fe = f.getEnglishName() == null ? "" : f.getEnglishName().trim().toLowerCase();
                                                if (fn.equals(fa) || fn.equals(fe)) { cabinetFolder = f; break; }
                                            }
                                        }
                                    } catch (Exception ignore) {}
                                    if (cabinetFolder == null) {
                                        cabinetFolder = new com.smat.ins.model.entity.CabinetFolder();
                                        cabinetFolder.setCabinetDefinition(def);
                                        cabinetFolder.setSysUser(loginBean.getUser());
                                        cabinetFolder.setArabicName(folderName);
                                        cabinetFolder.setEnglishName(folderName);
                                        int nextCode = 1; try { java.util.List<com.smat.ins.model.entity.CabinetFolder> existing2 = cabinetFolderService.getByCabinetDefinition(def); nextCode = existing2 != null ? existing2.size() + 1 : 1; } catch (Exception ignore) {}
                                        cabinetFolder.setCode(String.format("%03d", nextCode));
                                        cabinetFolder.setCreatedDate(new java.util.Date());
                                        cabinetFolderService.saveOrUpdate(cabinetFolder);
                                    }

                                    String mainLocation = com.smat.ins.util.CabinetDefaultsCreator.selectMainLocation(targetCabinet.getCabinetLocation());
                                    java.nio.file.Path folderPath = Paths.get(mainLocation, targetCabinet.getCode(), def.getCode(), cabinetFolder.getCode());
                                    Files.createDirectories(folderPath);

                                    String original = "EmployeeCertificate_" + folderName + ".pdf";
                                    String safe = original.replaceAll("[^a-zA-Z0-9._-]", "_");

                                    com.smat.ins.model.entity.ArchiveDocument archiveDocument = new com.smat.ins.model.entity.ArchiveDocument();
                                    try {
                                        com.smat.ins.model.service.ArchiveDocumentTypeService archiveDocumentTypeService = (com.smat.ins.model.service.ArchiveDocumentTypeService) BeanUtility.getBean("archiveDocumentTypeService");
                                        java.util.List<com.smat.ins.model.entity.ArchiveDocumentType> types = archiveDocumentTypeService.findAll();
                                        if (types != null && !types.isEmpty()) archiveDocument.setArchiveDocumentType(types.get(0));
                                    } catch (Exception ignore) {}
                                    archiveDocument.setArabicName(original); archiveDocument.setEnglishName(original); archiveDocument.setIsDirectory(false);
                                    archiveDocument.setCreatedDate(new java.util.Date()); archiveDocument.setSysUserByCreatorUser(loginBean.getUser());
                                    archiveDocumentService.saveOrUpdate(archiveDocument);

                                    com.smat.ins.model.entity.ArchiveDocumentFile docFile = new com.smat.ins.model.entity.ArchiveDocumentFile();
                                    docFile.setArchiveDocument(archiveDocument);
                                    docFile.setName(original);
                                    String ext = "pdf";
                                    docFile.setExtension(ext);
                                    docFile.setMimeType("application/pdf");
                                    docFile.setUuid(java.util.UUID.randomUUID().toString());
                                    docFile.setFileSize((long) pdfBytes.length);
                                    docFile.setCreatedDate(new java.util.Date());
                                    try {
                                        Long maxCode = archiveDocumentFileService.getMaxArchiveDocumentFileCode(archiveDocument);
                                        int codeLength = 9; String fileCode = String.format("%0" + codeLength + "d", (maxCode == null ? 0L : maxCode) + 1L);
                                        docFile.setCode(fileCode);
                                        // store generated certificate with a friendly fixed filename so the
                                        // zip download contains `employee_certificate.pdf` inside the archive
                                        String storedName = "employee_certificate." + ext;
                                        java.nio.file.Path target = folderPath.resolve(storedName);
                                        Files.write(target, pdfBytes, StandardOpenOption.CREATE_NEW);
                                        String logical = targetCabinet.getCode() + "/" + def.getCode() + "/" + cabinetFolder.getCode() + "/" + storedName;
                                        docFile.setLogicalPath(logical);
                                        docFile.setServerPath(target.toString());
                                    } catch (Exception ex) {
                                        int seq = 1; try { seq += (int) Files.list(folderPath).count(); } catch (Exception ignore) {}
                                        String storedName = String.format("%03d_employee_certificate.%s", seq, ext);
                                        java.nio.file.Path target = folderPath.resolve(storedName);
                                        Files.write(target, pdfBytes, StandardOpenOption.CREATE_NEW);
                                        String logical = targetCabinet.getCode() + "/" + def.getCode() + "/" + cabinetFolder.getCode() + "/" + storedName;
                                        docFile.setLogicalPath(logical);
                                        docFile.setServerPath(target.toString());
                                    }
                                    archiveDocumentFileService.saveOrUpdate(docFile);
                                    com.smat.ins.model.entity.CabinetFolderDocument cfd = new com.smat.ins.model.entity.CabinetFolderDocument();
                                    cfd.setCabinetFolder(cabinetFolder); cfd.setSysUser(loginBean.getUser()); cfd.setArchiveDocument(archiveDocument);
                                    cfd.setCreatedDate(new java.util.Date()); cabinetFolderDocumentService.saveOrUpdate(cfd);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace(); // don't break approval if saving fails
                        }
                    }
                } catch (Exception genEx) {
                    genEx.printStackTrace(); // generation failure should not prevent approval
                }

                // update local state
                step = "03";
                downloadAttach();
                // Show a full-screen blocking overlay on the client and redirect
                try {
                    String ctx = ((javax.faces.context.FacesContext) javax.faces.context.FacesContext.getCurrentInstance()).getExternalContext().getRequestContextPath();
                    String script = "(function(){"
                            + "var existing=document.getElementById('approveBlocker'); if(existing) existing.remove();"
                            + "var div=document.createElement('div');"
                            + "div.id='approveBlocker';"
                            + "div.style.position='fixed';div.style.top='0';div.style.left='0';div.style.width='100%';div.style.height='100%';"
                            + "div.style.background='rgba(0,0,0,0.55)';div.style.zIndex='2147483647';div.style.display='flex';div.style.alignItems='center';div.style.justifyContent='center';"
                            + "div.innerHTML=\"<div style='text-align:center;color:white;font-weight:600'><i class='pi pi-spin pi-spinner' style='font-size:3rem'></i><div style='margin-top:1rem'>Preparing download... Please wait...</div></div>\";"
                            + "document.body.appendChild(div);"
                            + "setTimeout(function(){ window.location.href='" + ctx + "/tasks/my-tasks'; },5000);"
                            + "})();";
                    org.primefaces.PrimeFaces.current().executeScript(script);
                } catch (Exception ex) {
                    // don't break the success path if client script fails
                    ex.printStackTrace();
                }
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
                empCertificationWorkflowStepTwo.setSysUserComment(comment != null ? comment : "");
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
                empCertificationWorkflowStepOne.setSysUserComment(comment != null ? comment : "");
                empCertificationWorkflowStepOne.setStepSeq((short) (maxStepSeq + 1));
                empCertificationWorkflowSteps.add(empCertificationWorkflowStepOne);

                EmpCertificationWorkflowStep empCertificationWorkflowStepTwo = new EmpCertificationWorkflowStep();
                empCertificationWorkflowStepTwo.setEmpCertification(empCertification);
                empCertificationWorkflowStepTwo.setWorkflowDefinition(workflowDefinitionInit.getWorkflowDefinitionByNext());
                empCertificationWorkflowStepTwo.setProcessDate(Calendar.getInstance().getTime());
                empCertificationWorkflowStepTwo.setSysUser(loginBean.getUser());
                empCertificationWorkflowStepTwo.setSysUserComment(comment != null ? comment : "");
                empCertificationWorkflowStepTwo.setStepSeq((short) (empCertificationWorkflowStepOne.getStepSeq() + 1));
                empCertificationWorkflowSteps.add(empCertificationWorkflowStepTwo);

                empCertification.setEmpCertificationWorkflowSteps(empCertificationWorkflowSteps);
                empCertificationService.saveOrUpdate(empCertification, employee);
                // confirm reservation if we reserved a cert for this task
                try {
                    if (task != null) {
                        com.smat.ins.model.service.SeqReservationService seqReservationService = (com.smat.ins.model.service.SeqReservationService) BeanUtility.getBean("seqReservationService");
                        if (seqReservationService != null) {
                            try { seqReservationService.confirmReservedCertNoForTask(task.getId()); } catch (Exception ignore) { }
                        }
                    }
                } catch (Exception ignore) {}
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
            workflowStep.setSysUserComment(comment != null ? comment : "");
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

            // Generate PDF bytes directly (no Jasper)
            byte[] pdfBytes = generateCertificatePdfBytes(parameters);

            // Put bytes in session so the CertificateViewerBean can read them and show viewer dialog
            try {
                UtilityHelper.putSessionAttr("certPdfBytes", pdfBytes);
                // update viewer content and show dialog (works with AJAX)
                try { PrimeFaces.current().ajax().update("form_viewer:manage-viewer-content"); } catch (Exception ignore) {}
                try { PrimeFaces.current().executeScript("PF('viewerWidgetVar').show();"); } catch (Exception ignore) {}
            } catch (Exception ex) {
                ex.printStackTrace();
                UtilityHelper.addErrorMessage("Failed to prepare PDF viewer: " + ex.getMessage());
            }

        } catch (Exception e) {
            UtilityHelper.addErrorMessage("Failed to generate report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Prepare session attributes for ZipDocumentDownloadBean to create/download attachments zip
     */
    public void prepareAttachmentsZipDownload() throws Exception {
        try {
            com.smat.ins.model.service.CabinetService cabinetService = (com.smat.ins.model.service.CabinetService) BeanUtility.getBean("cabinetService");
            com.smat.ins.model.service.CabinetFolderService cabinetFolderService = (com.smat.ins.model.service.CabinetFolderService) BeanUtility.getBean("cabinetFolderService");
            com.smat.ins.model.service.CabinetFolderDocumentService cabinetFolderDocumentService = (com.smat.ins.model.service.CabinetFolderDocumentService) BeanUtility.getBean("cabinetFolderDocumentService");

            String targetCabinetCode = "EMP-DEFAULT";
            com.smat.ins.model.entity.Cabinet targetCabinet = null;
            for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
            }
            if (targetCabinet == null) return;

            com.smat.ins.model.entity.CabinetDefinition def = null;
            if (targetCabinet.getCabinetDefinitions() != null) {
                for (Object od : targetCabinet.getCabinetDefinitions()) {
                    com.smat.ins.model.entity.CabinetDefinition cd = (com.smat.ins.model.entity.CabinetDefinition) od;
                    if ("01".equals(cd.getCode())) { def = cd; break; }
                }
            }
            if (def == null && targetCabinet.getCabinetDefinitions() != null && !targetCabinet.getCabinetDefinitions().isEmpty())
                def = (com.smat.ins.model.entity.CabinetDefinition) targetCabinet.getCabinetDefinitions().iterator().next();
            if (def == null) return;

            String folderName = null;
            try {
                if (empCertification != null && empCertification.getCertNumber() != null && !empCertification.getCertNumber().trim().isEmpty()) {
                    folderName = empCertification.getCertNumber().trim();
                }
            } catch (Exception ignore) {}
            if (folderName == null) {
                folderName = "emp_" + (employee != null && employee.getId() != null ? employee.getId() : "unknown");
            }

            com.smat.ins.model.entity.CabinetFolder cabinetFolder = null;
            try {
                java.util.List<com.smat.ins.model.entity.CabinetFolder> existing = cabinetFolderService.getByCabinetDefinition(def);
                if (existing != null) {
                    for (com.smat.ins.model.entity.CabinetFolder f : existing) {
                        String fn = folderName == null ? "" : folderName.trim().toLowerCase();
                        String fa = f.getArabicName() == null ? "" : f.getArabicName().trim().toLowerCase();
                        String fe = f.getEnglishName() == null ? "" : f.getEnglishName().trim().toLowerCase();
                        if (fn.equals(fa) || fn.equals(fe)) {
                            cabinetFolder = f;
                            break;
                        }
                    }
                }
            } catch (Exception ignore) {}

            if (cabinetFolder == null) return;

            java.util.List<com.smat.ins.model.entity.CabinetFolderDocument> items = cabinetFolderDocumentService.getByCabinetFolder(cabinetFolder);
            if (items == null || items.isEmpty()) return;
            com.smat.ins.model.entity.ArchiveDocument firstDoc = items.get(0).getArchiveDocument();
            if (firstDoc == null) return;

            UtilityHelper.putSessionAttr("archDocId", firstDoc.getId());
            UtilityHelper.putSessionAttr("cabinetFolderId", cabinetFolder.getId());
            UtilityHelper.putSessionAttr("cabinetId", targetCabinet.getId());
            UtilityHelper.putSessionAttr("cabinetDefinitionId", def.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the list of ArchiveDocumentFile objects that are recorded in the
     * cabinet folder for this employee certification. This uses the same
     * cabinet/folder resolution logic as prepareAttachmentsZipDownload but
     * returns the actual file records (empty list on error).
     */
    public java.util.List<com.smat.ins.model.entity.ArchiveDocumentFile> getAttachmentFiles() {
        try {
            com.smat.ins.model.service.CabinetService cabinetService = (com.smat.ins.model.service.CabinetService) BeanUtility.getBean("cabinetService");
            com.smat.ins.model.service.CabinetFolderService cabinetFolderService = (com.smat.ins.model.service.CabinetFolderService) BeanUtility.getBean("cabinetFolderService");
            com.smat.ins.model.service.CabinetFolderDocumentService cabinetFolderDocumentService = (com.smat.ins.model.service.CabinetFolderDocumentService) BeanUtility.getBean("cabinetFolderDocumentService");
            com.smat.ins.model.service.ArchiveDocumentFileService archiveDocumentFileService = (com.smat.ins.model.service.ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");

            String targetCabinetCode = "EMP-DEFAULT";
            com.smat.ins.model.entity.Cabinet targetCabinet = null;
            for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
            }
            if (targetCabinet == null) return java.util.Collections.emptyList();

            com.smat.ins.model.entity.CabinetDefinition def = null;
            if (targetCabinet.getCabinetDefinitions() != null) {
                for (Object od : targetCabinet.getCabinetDefinitions()) {
                    com.smat.ins.model.entity.CabinetDefinition cd = (com.smat.ins.model.entity.CabinetDefinition) od;
                    if ("01".equals(cd.getCode())) { def = cd; break; }
                }
            }
            if (def == null && targetCabinet.getCabinetDefinitions() != null && !targetCabinet.getCabinetDefinitions().isEmpty())
                def = (com.smat.ins.model.entity.CabinetDefinition) targetCabinet.getCabinetDefinitions().iterator().next();
            if (def == null) return java.util.Collections.emptyList();

            String folderName = null;
            try {
                if (empCertification != null && empCertification.getCertNumber() != null && !empCertification.getCertNumber().trim().isEmpty()) {
                    folderName = empCertification.getCertNumber().trim();
                }
            } catch (Exception ignore) {}
            if (folderName == null) {
                folderName = "emp_" + (employee != null && employee.getId() != null ? employee.getId() : "unknown");
            }

            com.smat.ins.model.entity.CabinetFolder cabinetFolder = null;
            try {
                java.util.List<com.smat.ins.model.entity.CabinetFolder> existing = cabinetFolderService.getByCabinetDefinition(def);
                if (existing != null) {
                    for (com.smat.ins.model.entity.CabinetFolder f : existing) {
                        String fn = folderName == null ? "" : folderName.trim().toLowerCase();
                        String fa = f.getArabicName() == null ? "" : f.getArabicName().trim().toLowerCase();
                        String fe = f.getEnglishName() == null ? "" : f.getEnglishName().trim().toLowerCase();
                        if (fn.equals(fa) || fn.equals(fe)) { cabinetFolder = f; break; }
                    }
                }
            } catch (Exception ignore) {}
            if (cabinetFolder == null) return java.util.Collections.emptyList();

            java.util.List<com.smat.ins.model.entity.CabinetFolderDocument> items = cabinetFolderDocumentService.getByCabinetFolder(cabinetFolder);
            if (items == null || items.isEmpty()) return java.util.Collections.emptyList();

            java.util.List<com.smat.ins.model.entity.ArchiveDocumentFile> result = new java.util.ArrayList<>();
            for (com.smat.ins.model.entity.CabinetFolderDocument cfd : items) {
                com.smat.ins.model.entity.ArchiveDocument ad = cfd.getArchiveDocument();
                if (ad == null) continue;
                java.util.List<com.smat.ins.model.entity.ArchiveDocumentFile> files = archiveDocumentFileService.getBy(ad);
                if (files != null && !files.isEmpty()) result.addAll(files);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Delete an attachment by id. Removes DB record, physical file and parent archive doc if empty.
     */
    public void deleteAttachment(java.lang.Long archiveDocumentFileId) {
        try {
            if (archiveDocumentFileId == null) {
                UtilityHelper.addErrorMessage("Invalid attachment id");
                return;
            }

            com.smat.ins.model.service.ArchiveDocumentFileService archiveDocumentFileService = (com.smat.ins.model.service.ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");
            com.smat.ins.model.service.ArchiveDocumentService archiveDocumentService = (com.smat.ins.model.service.ArchiveDocumentService) BeanUtility.getBean("archiveDocumentService");

            com.smat.ins.model.entity.ArchiveDocumentFile docFile = archiveDocumentFileService.findById(archiveDocumentFileId);
            if (docFile == null) {
                UtilityHelper.addErrorMessage("Attachment not found");
                return;
            }

            com.smat.ins.model.entity.ArchiveDocument archiveDocument = docFile.getArchiveDocument();
            String serverPath = docFile.getServerPath();

            // delete DB record
            try { archiveDocumentFileService.delete(docFile); } catch (Exception ex) { ex.printStackTrace(); }

            // delete physical file
            try {
                if (serverPath != null && !serverPath.trim().isEmpty()) {
                    java.nio.file.Path p = java.nio.file.Paths.get(serverPath);
                    java.nio.file.Files.deleteIfExists(p);
                }
            } catch (Exception ex) { ex.printStackTrace(); }

            // delete parent archiveDocument if no files remain
            try {
                if (archiveDocument != null) {
                    java.util.List<com.smat.ins.model.entity.ArchiveDocumentFile> remaining = null;
                    try { remaining = archiveDocumentFileService.getBy(archiveDocument); } catch (Exception ign) {}
                    if (remaining == null || remaining.isEmpty()) {
                        try { archiveDocumentService.deleteArchiveDoc(archiveDocument); } catch (Exception ign) { ign.printStackTrace(); }
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); }

            UtilityHelper.addInfoMessage("Attachment deleted");
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Error deleting attachment: " + e.getMessage());
        } finally {
            try { PrimeFaces.current().ajax().update(":form:emp_attachments_section"); } catch (Exception ignore) {}
        }
    }

    // helper
    private String safeString(Object o) {
        return o == null ? "" : String.valueOf(o);
    }
    /**
     * Generate certificate PDF bytes directly using iText (no Jasper).
     * Uses the prepared parameters map (keys used in jrxml) to layout the PDF.
     */
    private byte[] generateCertificatePdfBytes(Map<String, Object> parameters) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // Colors and fonts
            // refreshed color palette for better visual appeal
            com.itextpdf.text.BaseColor brandBlue = new com.itextpdf.text.BaseColor(10, 102, 204); // brighter blue
            com.itextpdf.text.BaseColor lightGray = new com.itextpdf.text.BaseColor(250, 250, 252);
            com.itextpdf.text.BaseColor darkText = new com.itextpdf.text.BaseColor(38, 50, 56);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, com.itextpdf.text.BaseColor.BLACK);
            Font roleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, com.itextpdf.text.BaseColor.BLACK);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, brandBlue);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, darkText);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, darkText);
            Font urlFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, brandBlue);
            // fonts for certificate and approval labels/values
            Font certLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, brandBlue);
            Font certValueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, darkText);
            Font approvalLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, brandBlue);
            Font approvalValueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, darkText);

            // page metrics
            float pageW = document.getPageSize().getWidth();
            float pageH = document.getPageSize().getHeight();
            float margin = 28f;
            float gap = 18f;

            // Two card panels stacked vertically (front + back)
            float panelW = pageW - margin * 2;
            float panelH = (pageH - margin * 2 - gap) / 2f;
            float cornerRadius = 8f;

            PdfContentByte cb = writer.getDirectContent();

            // helper to draw rounded panel background with subtle drop shadow
            java.util.function.BiConsumer<Float, Float> drawPanelBg = (x, y) -> {
                try {
                    // shadow (slightly offset)
                    cb.saveState();
                    cb.setColorFill(new com.itextpdf.text.BaseColor(220, 220, 224));
                    cb.roundRectangle(x + 3f, y - 3f, panelW, panelH, cornerRadius);
                    cb.fill();
                    cb.restoreState();

                    // panel
                    cb.saveState();
                    cb.setColorFill(lightGray);
                    cb.roundRectangle(x, y, panelW, panelH, cornerRadius);
                    cb.fillStroke();
                    cb.restoreState();
                } catch (Exception ignore) {}
            };

            // FRONT PANEL (top)
            float frontX = margin;
            float frontY = pageH - margin - panelH;
            drawPanelBg.accept(frontX, frontY);

            // header: logos and title
            // increase header height to give more breathing room below the logos/title
            float headerH = 110f;
            try {
                // left logo
                String leftPath = getServletContextPath("views/jasper/images/logo-left.png");
                if (leftPath != null) {
                    byte[] leftBytes = makeImageTransparent(leftPath);
                    if (leftBytes != null) {
                        Image logoL = Image.getInstance(leftBytes);
                        // larger logo for more presence
                        logoL.scaleToFit(90, 90);
                        // raise left logo further up
                        logoL.setAbsolutePosition(frontX + 12, frontY + panelH - 6 - logoL.getScaledHeight());
                        cb.addImage(logoL);
                    }
                }
            } catch (Exception ignore) {}
            try {
                // right logo
                String rightPath = getServletContextPath("views/jasper/images/logo-right.png");
                if (rightPath != null) {
                    byte[] rightBytes = makeImageTransparent(rightPath);
                    if (rightBytes != null) {
                        Image logoR = Image.getInstance(rightBytes);
                        // larger right logo (landscape style)
                        logoR.scaleToFit(160, 80);
                        // raise right logo to align with left
                        logoR.setAbsolutePosition(frontX + panelW - 12 - logoR.getScaledWidth(), frontY + panelH - 6 - logoR.getScaledHeight());
                        cb.addImage(logoR);
                    }
                }
            } catch (Exception ignore) {}


            // thin blue separator
            cb.setLineWidth(3f);
            cb.setColorStroke(brandBlue);
            cb.moveTo(frontX + 12f, frontY + panelH - headerH + 6f);
            cb.lineTo(frontX + panelW - 12f, frontY + panelH - headerH + 6f);
            cb.stroke();

            // Role title (Certified as: <role>) - moved below the blue separator to avoid overlap
            String certType = safeString(parameters.get("CertType"));
            String certParam = safeString(parameters.get("CertNumber"));
            String certDisplay = (certParam != null && certParam.startsWith("ID/")) ? certParam : ("ID/" + certParam);
            // move 'Certified As' a bit higher for better alignment with logos (capitalize)
            // Render 'Certified As' and its value on the same baseline with matching size
            // lower the "Certified As" line slightly for better spacing
            float certifiedY = frontY + panelH - headerH - 22f;
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Certified As:", certLabelFont), frontX + 12f, certifiedY, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(certType, certValueFont), frontX + 150f, certifiedY, 0);

            // left photo area (positioned below header, with stable spacing)
            float photoX = frontX + 18f;
            float photoW = 120f; float photoH = 140f;
            // shift content a bit lower to avoid header overlap
            float contentTop = frontY + panelH - headerH - 40f; // increased gap under header
            float photoY = contentTop - photoH - 12f; // place photo slightly lower

            // draw white rounded backdrop behind photo for consistent look
            try {
                cb.saveState();
                cb.setColorFill(new com.itextpdf.text.BaseColor(255, 255, 255));
                cb.setColorStroke(brandBlue);
                cb.setLineWidth(1f);
                cb.roundRectangle(photoX - 4f, photoY - 4f, photoW + 8f, photoH + 8f, 6f);
                cb.fillStroke();
                cb.restoreState();
            } catch (Exception ignore) {}

            try {
                Object empPhoto = parameters.get("EmployeePhoto");
                Image img = null;
                if (empPhoto instanceof java.io.InputStream) img = Image.getInstance(streamToBytes((java.io.InputStream) empPhoto));
                else if (empPhoto instanceof byte[]) img = Image.getInstance((byte[]) empPhoto);
                if (img != null) {
                    img.scaleToFit(photoW - 8f, photoH - 8f);
                    float imgW = img.getScaledWidth();
                    float imgH = img.getScaledHeight();
                    float imgX = photoX + (photoW - imgW) / 2f;
                    float imgY = photoY + (photoH - imgH) / 2f;
                    img.setAbsolutePosition(imgX, imgY);
                    cb.addImage(img);
                }
            } catch (Exception ignore) {}

            // right info box with rounded header (aligned with photo)
            float infoX = frontX + photoW + 36f; // leave space for photo + gap
            float infoY = photoY + 4f; // nudge info box down a bit to align visually with photo
            float infoW = frontX + panelW - 18f - infoX; // right margin
            float infoH = photoH + 8f; // make info box similar height to photo

            // draw rounded header bar
            cb.saveState();
            cb.setColorFill(brandBlue);
            cb.roundRectangle(infoX, infoY + infoH - 26f, infoW, 26f, 8f);
            cb.fill();
            cb.restoreState();

            // header text
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Company: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, com.itextpdf.text.BaseColor.WHITE)), infoX + 8f, infoY + infoH - 18f, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(safeString(parameters.get("CompanyName")), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, com.itextpdf.text.BaseColor.WHITE)), infoX + 80f, infoY + infoH - 18f, 0);

            // draw rows inside info box with borders and consistent spacing
            float innerPad = 8f;
            float availableH = infoH - 36f; // space below rounded header
            int rowsCount = 4;
            float rowH = availableH / rowsCount;
            float curY = infoY + infoH - 36f; // start below rounded header
            String[][] rows = new String[][] {
                    {"Name:", safeString(parameters.get("EmployeeName"))},
                    {"Id/Iqama No:", safeString(parameters.get("EmployeeId"))},
                    {"Issue on:", formatDateParam(parameters.get("IssueDate"))},
                    {"Valid up to:", formatDateParam(parameters.get("ExpiryDate"))}
            };
            cb.setLineWidth(1f);
            cb.setColorStroke(brandBlue);
            for (int i = 0; i < rows.length; i++) {
                float rY = curY - rowH + 6f;
                // draw row rectangle
                cb.saveState();
                cb.setColorFill(new com.itextpdf.text.BaseColor(255, 255, 255));
                cb.rectangle(infoX, rY, infoW, rowH - 6f);
                cb.fillStroke();
                cb.restoreState();

                // left label
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(rows[i][0], labelFont), infoX + innerPad, rY + (rowH / 2f) - 6f, 0);
                // value (wrap if long) - place at fixed X
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(rows[i][1], valueFont), infoX + 110f, rY + (rowH / 2f) - 6f, 0);
                curY -= rowH;
            }

            // bottom ID and TS line (separated and aligned)
            float bottomY = frontY + 22f;
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("ID No.", labelFont), frontX + 12f, bottomY, 0);
            // bring ID value closer to label
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(certDisplay, approvalValueFont), frontX + 62f, bottomY, 0);
            // TS label and value tightened and closer
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase("TS No.", labelFont), frontX + panelW - 92f, bottomY, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(safeString(parameters.get("TsNumber")), approvalValueFont), frontX + panelW - 12f, bottomY, 0);

            // small footer note
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("Certified to Operate the equipment listed on the back side of the card", smallFont), frontX + panelW / 2f, frontY + 6f, 0);

            // BACK PANEL (bottom)
            float backX = frontX;
            float backY = frontY - gap - panelH;
            drawPanelBg.accept(backX, backY);


            // back header text: keep issuer sentence at top, move company title lower for balance
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("This card is issued by and remains the property of:", smallFont), backX + panelW / 2f, backY + panelH - 16f, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("SMAT for Inspection Company", titleFont), backX + panelW / 2f, backY + panelH - 44f, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("Postal code: 34432, P.O.Box: 19331", smallFont), backX + panelW / 2f, backY + panelH - 62f, 0);

            // big ID
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("ID No. " + certDisplay, valueFont), backX + panelW / 2f, backY + panelH - 74f, 0);

            // approval schedule box with QR
            // make box slightly smaller and place it lower
            float boxX = backX + 28f;
            float boxY = backY + 12f; // lowered
            float boxW = panelW - 56f;
            float boxH = panelH - 180f; // reduced height

            // draw outline
            cb.setLineWidth(1f);
            cb.setColorStroke(brandBlue);
            cb.rectangle(boxX, boxY, boxW, boxH);
            cb.stroke();

            // left text in box (Approval schedule + cert type) - bigger and clearer
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("APPROVAL SCHEDULE", approvalLabelFont), boxX + 12f, boxY + boxH - 34f, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(certType, approvalValueFont), boxX + 12f, boxY + boxH - 58f, 0);

            // QR on right inside box, with URL centered under it
            try {
                byte[] qrBytes = null;
                if (parameters.get("QRCodeImage") instanceof java.io.InputStream) qrBytes = streamToBytes((java.io.InputStream) parameters.get("QRCodeImage"));
                else if (parameters.get("QRCodeImage") instanceof byte[]) qrBytes = (byte[]) parameters.get("QRCodeImage");
                if (qrBytes != null) {
                    Image q = Image.getInstance(qrBytes);
                    // allow a slightly larger QR while keeping margin
                    float qSize = Math.min(140f, boxH - 30f);
                    q.scaleToFit(qSize, qSize);
                    // position QR near top-right of the box
                    float qY = boxY + boxH - 12f - q.getScaledHeight();
                    float qX = boxX + boxW - 12f - q.getScaledWidth();
                    q.setAbsolutePosition(qX, qY);
                    cb.addImage(q);
                    // add URL centered under the QR
                    float urlY = qY - 12f;
                    float urlX = qX + (q.getScaledWidth() / 2f);
                    ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("www.smat-ins.com", urlFont), urlX, urlY, 0);
                }
            } catch (Exception ignore) {}

            document.close();
            return baos.toByteArray();
        } catch (DocumentException de) {
            throw new Exception("PDF generation failed: " + de.getMessage(), de);
        } finally {
            try { document.close(); } catch (Exception ignore) {}
        }
    }

    private byte[] streamToBytes(java.io.InputStream is) throws java.io.IOException {
        if (is == null) return null;
        try (java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int r;
            while ((r = is.read(buffer)) != -1) bos.write(buffer, 0, r);
            return bos.toByteArray();
        }
    }

    /**
     * Load image from disk and convert near-white background pixels to transparent.
     * Returns PNG bytes (ARGB) on success or original file bytes on failure.
     */
    private byte[] makeImageTransparent(String path) {
        if (path == null) return null;
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            BufferedImage src = ImageIO.read(f);
            if (src == null) return java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path));
            int w = src.getWidth();
            int h = src.getHeight();
            BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int rgb = src.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    // treat near-white as transparent
                    if (r > 240 && g > 240 && b > 240) {
                        dst.setRGB(x, y, (0 << 24) | (r << 16) | (g << 8) | b);
                    } else {
                        dst.setRGB(x, y, (255 << 24) | (r << 16) | (g << 8) | b);
                    }
                }
            }
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                ImageIO.write(dst, "png", baos);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            try { return java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path)); } catch (Exception ex) { return null; }
        }
    }

    private PdfPCell makeCell(String text, Font f, int border) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBorder(border);
        c.setPadding(4);
        return c;
    }

    private String formatDateParam(Object o) {
        if (o == null) return "";
        try {
            if (o instanceof java.util.Date) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                return sdf.format((java.util.Date) o);
            } else {
                return String.valueOf(o);
            }
        } catch (Exception e) { return String.valueOf(o); }
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

    // Check whether a cabinet drawer folder contains regular files
    private boolean hasFilesInCabinetFolder(String cabinetCode, String drawerCode, String folderName) {
        try {
            com.smat.ins.model.service.CabinetService cabinetService = (com.smat.ins.model.service.CabinetService) BeanUtility.getBean("cabinetService");
            com.smat.ins.model.service.CabinetFolderService cabinetFolderService = (com.smat.ins.model.service.CabinetFolderService) BeanUtility.getBean("cabinetFolderService");

            com.smat.ins.model.entity.Cabinet targetCabinet = null;
            for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                if (cabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
            }
            if (targetCabinet == null) return false;

            com.smat.ins.model.entity.CabinetDefinition def = null;
            if (targetCabinet.getCabinetDefinitions() != null) {
                for (Object od : targetCabinet.getCabinetDefinitions()) {
                    com.smat.ins.model.entity.CabinetDefinition cd = (com.smat.ins.model.entity.CabinetDefinition) od;
                    if (drawerCode.equals(cd.getCode())) { def = cd; break; }
                }
            }
            if (def == null) return false;

            java.util.List<com.smat.ins.model.entity.CabinetFolder> existing = cabinetFolderService.getByCabinetDefinition(def);
            com.smat.ins.model.entity.CabinetFolder cabinetFolder = null;
            if (existing != null) {
                for (com.smat.ins.model.entity.CabinetFolder f : existing) {
                    String fn = folderName == null ? "" : folderName.trim().toLowerCase();
                    String fa = f.getArabicName() == null ? "" : f.getArabicName().trim().toLowerCase();
                    String fe = f.getEnglishName() == null ? "" : f.getEnglishName().trim().toLowerCase();
                    if (fn.equals(fa) || fn.equals(fe)) { cabinetFolder = f; break; }
                }
            }
            if (cabinetFolder == null) return false;

            String mainLocation = com.smat.ins.util.CabinetDefaultsCreator.selectMainLocation(targetCabinet.getCabinetLocation());
            java.nio.file.Path folderPath = java.nio.file.Paths.get(mainLocation, targetCabinet.getCode(), def.getCode(), cabinetFolder.getCode());
            if (!java.nio.file.Files.exists(folderPath) || !java.nio.file.Files.isDirectory(folderPath)) return false;
            try (java.util.stream.Stream<java.nio.file.Path> s = java.nio.file.Files.list(folderPath)) {
                return s.anyMatch(p -> java.nio.file.Files.isRegularFile(p));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
            // Comment is optional now; do not enforce non-empty comment here.
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

    /**
     * Handle generic attachment upload for employee training form.
     * Stores files on disk under an application-managed attachments folder.
     */
    public void handleAttachmentUpload(org.primefaces.event.FileUploadEvent event) {
        try {
            if (event.getFile() == null || event.getFile().getContent() == null) return;
            if (event.getFile().getSize() > 20L * 1024L * 1024L) {
                UtilityHelper.addErrorMessage("File size exceeds 20MB limit");
                return;
            }

            // services
            com.smat.ins.model.service.CabinetService cabinetService = (com.smat.ins.model.service.CabinetService) BeanUtility.getBean("cabinetService");
            com.smat.ins.model.service.CabinetFolderService cabinetFolderService = (com.smat.ins.model.service.CabinetFolderService) BeanUtility.getBean("cabinetFolderService");
            com.smat.ins.model.service.ArchiveDocumentService archiveDocumentService = (com.smat.ins.model.service.ArchiveDocumentService) BeanUtility.getBean("archiveDocumentService");
            com.smat.ins.model.service.ArchiveDocumentFileService archiveDocumentFileService = (com.smat.ins.model.service.ArchiveDocumentFileService) BeanUtility.getBean("archiveDocumentFileService");
            com.smat.ins.model.service.CabinetFolderDocumentService cabinetFolderDocumentService = (com.smat.ins.model.service.CabinetFolderDocumentService) BeanUtility.getBean("cabinetFolderDocumentService");

            // find employee cabinet
            String targetCabinetCode = "EMP-DEFAULT";
            com.smat.ins.model.entity.Cabinet targetCabinet = null;
            for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
            }
            if (targetCabinet == null) {
                // create defaults and reload
                com.smat.ins.util.CabinetDefaultsCreator.ensureDefaultCabinets(loginBean.getUser());
                for (com.smat.ins.model.entity.Cabinet c : cabinetService.findAll()) {
                    if (targetCabinetCode.equals(c.getCode())) { targetCabinet = c; break; }
                }
            }

            if (targetCabinet == null) {
                UtilityHelper.addErrorMessage("Employee cabinet not available");
                return;
            }

            // choose cabinetDefinition (drawer) code "01"
            com.smat.ins.model.entity.CabinetDefinition def = null;
            if (targetCabinet.getCabinetDefinitions() != null) {
                for (Object od : targetCabinet.getCabinetDefinitions()) {
                    com.smat.ins.model.entity.CabinetDefinition cd = (com.smat.ins.model.entity.CabinetDefinition) od;
                    if ("01".equals(cd.getCode())) { def = cd; break; }
                }
            }
            if (def == null) {
                // fallback to first
                if (targetCabinet.getCabinetDefinitions() != null && !targetCabinet.getCabinetDefinitions().isEmpty())
                    def = (com.smat.ins.model.entity.CabinetDefinition) targetCabinet.getCabinetDefinitions().iterator().next();
            }
            if (def == null) {
                UtilityHelper.addErrorMessage("No cabinet definition found for target cabinet");
                return;
            }

            // find or create folder for this employee/training task
            // Prefer certificate number as folder name; fallback to employee id
            String folderName = null;
            try {
                if (empCertification != null && empCertification.getCertNumber() != null && !empCertification.getCertNumber().trim().isEmpty()) {
                    folderName = empCertification.getCertNumber().trim();
                }
            } catch (Exception ignore) {}
            if (folderName == null) {
                folderName = "emp_" + (employee != null && employee.getId() != null ? employee.getId() : "unknown");
            }

            com.smat.ins.model.entity.CabinetFolder cabinetFolder = null;
            try {
                java.util.List<com.smat.ins.model.entity.CabinetFolder> existing = cabinetFolderService.getByCabinetDefinition(def);
                if (existing != null) {
                    for (com.smat.ins.model.entity.CabinetFolder f : existing) {
                        String fn = folderName == null ? "" : folderName.trim().toLowerCase();
                        String fa = f.getArabicName() == null ? "" : f.getArabicName().trim().toLowerCase();
                        String fe = f.getEnglishName() == null ? "" : f.getEnglishName().trim().toLowerCase();
                        if (fn.equals(fa) || fn.equals(fe)) {
                            cabinetFolder = f;
                            break;
                        }
                    }
                }
            } catch (Exception ignore) {}

            if (cabinetFolder == null) {
                cabinetFolder = new com.smat.ins.model.entity.CabinetFolder();
                cabinetFolder.setCabinetDefinition(def);
                cabinetFolder.setSysUser(loginBean.getUser());
                cabinetFolder.setArabicName(folderName);
                cabinetFolder.setEnglishName(folderName);
                // generate simple code
                int nextCode = 1;
                try {
                    java.util.List<com.smat.ins.model.entity.CabinetFolder> existing2 = cabinetFolderService.getByCabinetDefinition(def);
                    nextCode = existing2 != null ? existing2.size() + 1 : 1;
                } catch (Exception ignore) {}
                cabinetFolder.setCode(String.format("%03d", nextCode));
                cabinetFolder.setCreatedDate(new java.util.Date());
                cabinetFolderService.saveOrUpdate(cabinetFolder);
            }

            // create disk folder
            String mainLocation = com.smat.ins.util.CabinetDefaultsCreator.selectMainLocation(targetCabinet.getCabinetLocation());
            java.nio.file.Path folderPath = Paths.get(mainLocation, targetCabinet.getCode(), def.getCode(), cabinetFolder.getCode());
            Files.createDirectories(folderPath);

            // prepare file metadata
            String original = event.getFile().getFileName();
            String safe = original.replaceAll("[^a-zA-Z0-9._-]", "_");

            // Create ArchiveDocument and ArchiveDocumentFile
            com.smat.ins.model.entity.ArchiveDocument archiveDocument = new com.smat.ins.model.entity.ArchiveDocument();
            // set required archive document type (many parts of the app expect a non-null type)
            try {
                com.smat.ins.model.service.ArchiveDocumentTypeService archiveDocumentTypeService = (com.smat.ins.model.service.ArchiveDocumentTypeService) BeanUtility.getBean("archiveDocumentTypeService");
                java.util.List<com.smat.ins.model.entity.ArchiveDocumentType> types = archiveDocumentTypeService.findAll();
                if (types != null && !types.isEmpty()) {
                    archiveDocument.setArchiveDocumentType(types.get(0));
                }
            } catch (Exception ignore) {}
            archiveDocument.setArabicName(original);
            archiveDocument.setEnglishName(original);
            archiveDocument.setIsDirectory(false);
            archiveDocument.setCreatedDate(new java.util.Date());
            archiveDocument.setSysUserByCreatorUser(loginBean.getUser());
            // set organization and root organization from uploader
            try {
                com.smat.ins.model.service.OrganizationService organizationService = (com.smat.ins.model.service.OrganizationService) BeanUtility.getBean("organizationService");
                com.smat.ins.model.entity.Organization userOrg = null;
                if (loginBean.getUser() != null) userOrg = loginBean.getUser().getOrganizationByOrganization();
                if (userOrg != null) {
                    archiveDocument.setOrganizationByOrganization(userOrg);
                    com.smat.ins.model.entity.Organization rootOrg = userOrg;
                    try {
                        com.smat.ins.model.entity.Organization parentOrg = organizationService.getParentOrganization(rootOrg);
                        while (parentOrg != null && !parentOrg.equals(rootOrg)) {
                            rootOrg = parentOrg;
                            parentOrg = organizationService.getParentOrganization(rootOrg);
                        }
                    } catch (Exception ignore) {}
                    archiveDocument.setOrganizationByRootOrganization(rootOrg);
                }
            } catch (Exception ignore) {}
            archiveDocumentService.saveOrUpdate(archiveDocument);

            com.smat.ins.model.entity.ArchiveDocumentFile docFile = new com.smat.ins.model.entity.ArchiveDocumentFile();
            docFile.setArchiveDocument(archiveDocument);
            docFile.setName(original);
            String ext = org.apache.commons.io.FilenameUtils.getExtension(original);
            docFile.setExtension(ext);
            docFile.setMimeType(event.getFile().getContentType());
            docFile.setUuid(java.util.UUID.randomUUID().toString());
            docFile.setFileSize(event.getFile().getSize());
            docFile.setCreatedDate(new java.util.Date());

            // try to allocate a sequential zero-padded code and use it as filename
            try {
                Long maxCode = archiveDocumentFileService.getMaxArchiveDocumentFileCode(archiveDocument);
                int codeLength = 9; // keep consistent with service defaults
                String fileCode = String.format("%0" + codeLength + "d", (maxCode == null ? 0L : maxCode) + 1L);
                docFile.setCode(fileCode);

                String storedName = fileCode + "." + ext;
                java.nio.file.Path target = folderPath.resolve(storedName);
                Files.write(target, event.getFile().getContent(), java.nio.file.StandardOpenOption.CREATE_NEW);

                String logical = targetCabinet.getCode() + "/" + def.getCode() + "/" + cabinetFolder.getCode() + "/" + storedName;
                docFile.setLogicalPath(logical);
                docFile.setServerPath(target.toString());
            } catch (Exception ex) {
                // fallback to older naming if code allocation fails
                int seq = 1; try { seq += (int) java.nio.file.Files.list(folderPath).count(); } catch (Exception ignore) {}
                String storedName = String.format("%03d_%s", seq, safe);
                java.nio.file.Path target = folderPath.resolve(storedName);
                Files.write(target, event.getFile().getContent(), java.nio.file.StandardOpenOption.CREATE_NEW);
                String logical = targetCabinet.getCode() + "/" + def.getCode() + "/" + cabinetFolder.getCode() + "/" + storedName;
                docFile.setLogicalPath(logical); docFile.setServerPath(target.toString());
            }

            // do not set content to avoid DB bloat
            archiveDocumentFileService.saveOrUpdate(docFile);

            // link via CabinetFolderDocument
            com.smat.ins.model.entity.CabinetFolderDocument cfd = new com.smat.ins.model.entity.CabinetFolderDocument();
            cfd.setCabinetFolder(cabinetFolder);
            cfd.setSysUser(loginBean.getUser());
            cfd.setArchiveDocument(archiveDocument);
            cfd.setCreatedDate(new java.util.Date());
            cabinetFolderDocumentService.saveOrUpdate(cfd);

            UtilityHelper.addInfoMessage("Attachment uploaded to cabinet: " + original);

        } catch (Exception e) {
            UtilityHelper.addErrorMessage("Error uploading attachment: " + e.getMessage());
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
    public void saveDraft() {
        try {
            TaskDraft draft = new TaskDraft();
            if (task != null) draft.setTaskId(task.getId());
            SysUser user = loginBean.getUser();
            if (user != null) {
                draft.setDraftOwnerId(user.getId().intValue());
                draft.setDraftOwnerName(user.getDisplayName());
            }
            // scope this draft to employee certification form
            draft.setTaskType("emp_training");
            // وضع بيانات النموذج في JSON — أفضل من Java serialization
            Map<String, Object> payload = new HashMap<>();
            // convert to safe shallow structures to avoid serializing full entity graphs
            payload.put("empCertification", com.smat.ins.util.DraftUtils.toSafeStructure(this.empCertification));
            payload.put("employee", com.smat.ins.util.DraftUtils.toSafeStructure(this.employee));
            payload.put("selectedUserAliasRecipient", com.smat.ins.util.DraftUtils.toSafeStructure(this.selectedUserAliasRecipient));
            payload.put("persistentMode", this.persistentMode);
            // include some UI state
            payload.put("stepComment", this.stepComment);
            payload.put("disabled", this.disabled);
            // تحويل إلى bytes
            byte[] bytes = objectMapper.writeValueAsBytes(payload);
            draft.setDraftData(bytes);
            draft.setCreatedDate(new Date());
            TaskDraft saved = taskDraftService.saveOrUpdate(draft);
            if (saved != null && saved.getId() != null) {
                UtilityHelper.addInfoMessage("Draft saved successfully (v=" + saved.getVersion() + ")");
            } else {
                UtilityHelper.addErrorMessage("Failed to save draft");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Error saving draft: " + e.getMessage());
        }
    }

    // ActionListener wrapper referenced by xhtml (some templates call saveDraftEmpCertification)
    public void saveDraftEmpCertification(ActionEvent event) {
        saveDraft();
    }
    public void loadDraft() {
        try {
            // حاول جلب مسودة مرتبطة بالمهمة أولاً (إن وُجدت)
            TaskDraft draft = null;
            if (task != null && task.getId() != null) {
                draft = taskDraftService.findByTaskId(task.getId());
            }
            // إن لم توجد مسودة مرتبطة بالمهمة، جلب أحدث مسودة للمالك الحالي
            if (draft == null) {
                SysUser current = loginBean.getUser();
                if (current != null) {
                    Integer ownerId = current.getId().intValue();
                    // try owner+type first so we load the right form
                    draft = taskDraftService.findLatestByOwnerAndType(ownerId, "emp_training");
                    if (draft == null) {
                        draft = taskDraftService.findLatestByOwner(ownerId);
                    }
                }
            }

            if (draft == null) {
                UtilityHelper.addInfoMessage("No draft found.");
                return;
            }

            byte[] data = draft.getDraftData();
            if (data == null || data.length == 0) {
                UtilityHelper.addInfoMessage("Draft is empty.");
                return;
            }

            // فك الـ JSON إلى خريطة وملأ الحقول الأساسية
            Map<String, Object> payload = objectMapper.readValue(data, Map.class);

            // EmpCertification
            if (payload.containsKey("empCertification")) {
                // تحويل الكائن إلى JSON ثم إلى الكلاس لضمان النوع
                EmpCertification e = objectMapper.convertValue(payload.get("empCertification"), EmpCertification.class);
                this.empCertification = e;
            }

            // Employee
            if (payload.containsKey("employee")) {
                Employee emp = objectMapper.convertValue(payload.get("employee"), Employee.class);
                this.employee = emp;
                if (this.empCertification != null) this.empCertification.setEmployee(emp);
            }

            // selected reviewer user alias (قد تحتاج تحويل/بحث حسب id)
            if (payload.containsKey("selectedUserAliasRecipient")) {
                UserAlias ua = objectMapper.convertValue(payload.get("selectedUserAliasRecipient"), UserAlias.class);
                this.selectedUserAliasRecipient = ua;
            }

            // persistentMode إذا خُزّن
            if (payload.containsKey("persistentMode")) {
                Object pm = payload.get("persistentMode");
                if (pm != null) this.persistentMode = pm.toString();
            }

            UtilityHelper.addInfoMessage("Draft loaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            UtilityHelper.addErrorMessage("Error loading draft: " + e.getMessage());
        }
    }
    public void downloadAttach() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            // prepare attachments zip session attributes (so user can download attachments separately)
            try { prepareAttachmentsZipDownload(); } catch (Exception ignore) {}

            // determine folder name: prefer certificate number, fallback to employee id
            String folderName = null;
            try {
                if (empCertification != null && empCertification.getCertNumber() != null
                        && !empCertification.getCertNumber().trim().isEmpty()) {
                    folderName = empCertification.getCertNumber().trim();
                }
            } catch (Exception ignore) {}

            if (folderName == null && employee != null && employee.getId() != null) {
                folderName = "emp_" + employee.getId();
            }

            // if there are files in the cabinet folder, open zip download in a new tab
            if (folderName != null && hasFilesInCabinetFolder("EMP-DEFAULT", "01", folderName)) {
                String ctx = facesContext.getExternalContext().getRequestContextPath();
                String url = ctx + "/attachments/zip?type=emp&certNo=" + java.net.URLEncoder.encode(folderName, "UTF-8");
                PrimeFaces.current().executeScript("window.open('" + url + "', '_blank');");
            } else {
                UtilityHelper.addErrorMessage("No attachments found to download.");
            }

        } catch (Exception e) {
            UtilityHelper.addErrorMessage("Failed to prepare attachments download: " + e.getMessage());
            e.printStackTrace();
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