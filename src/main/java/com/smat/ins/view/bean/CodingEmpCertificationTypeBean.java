package com.smat.ins.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.smat.ins.model.entity.EmpCertificationType;
import com.smat.ins.model.service.EmpCertificationTypeService;
import com.smat.ins.util.BeanUtility;
import com.smat.ins.util.LocalizationService;
import com.smat.ins.util.UtilityHelper;


@Named
@ViewScoped
public class CodingEmpCertificationTypeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private EmpCertificationType empCertificationType;
    private List<EmpCertificationType> empCertificationTypeList;
    private List<EmpCertificationType> selectedEmpCertificationTypeList;

    private EmpCertificationTypeService empCertificationTypeService;
    private LocalizationService localizationService;

    public CodingEmpCertificationTypeBean() {
        empCertificationTypeService = (EmpCertificationTypeService) BeanUtility.getBean("empCertificationTypeService");
        localizationService = (LocalizationService) BeanUtility.getBean("localizationService");
    }

    @PostConstruct
    public void init() {
        try {
            empCertificationType = new EmpCertificationType();
            empCertificationTypeList = new ArrayList<>();
            selectedEmpCertificationTypeList = new ArrayList<>();
            empCertificationTypeList = empCertificationTypeService.findAll();
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("errorDuringGetData"));
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() {

    }

    public void openNew() {
        this.empCertificationType = new EmpCertificationType();
    }

    public boolean doValidate() {
        boolean result = true;
        if (empCertificationType.getCertName() == null || empCertificationType.getCertName().trim().isEmpty()) {
            UtilityHelper.addWarnMessage(localizationService.getInterfaceLabel().getString("certName") + "  "
                    + localizationService.getErrorMessage().getString("validateInput"));
            result = false;
        }

        return result;
    }

    public void save() {
        if (doValidate()) {
            try {
                empCertificationTypeService.saveOrUpdate(empCertificationType);
                empCertificationTypeList = empCertificationTypeService.findAll();
                UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
                PrimeFaces.current().ajax().update("form:messages", "form:dataTable-empCertificationType");
                PrimeFaces.current().executeScript("PF('widgetVarDetailDialog').hide()");
            } catch (Exception e) {
                UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
                PrimeFaces.current().ajax().update("form:messages", "form:dataTable-empCertificationType");
                e.printStackTrace();
            }
        }
    }

    public boolean hasSelectedEmpCertificationType() {
        return this.selectedEmpCertificationTypeList != null && !this.selectedEmpCertificationTypeList.isEmpty();
    }

    public String getDeleteButtonMessage() {
        if (hasSelectedEmpCertificationType()) {
            int size = this.selectedEmpCertificationTypeList.size();
            return size > 1 ? size + " " + localizationService.getInterfaceLabel().getString("certificationTypesSelectedNum")
                    : localizationService.getInterfaceLabel().getString("oneCertificationTypeSelected");
        }

        return localizationService.getInterfaceLabel().getString("delete");
    }

    public void deleteEmpCertificationType() {
        try {
            empCertificationTypeService.delete(empCertificationType);
            this.empCertificationType = null;
            empCertificationTypeList = empCertificationTypeService.findAll();
            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
            PrimeFaces.current().ajax().update("form:messages", "form:dataTable-empCertificationType");
            PrimeFaces.current().executeScript("PF('widgetVarEmpCertificationType').clearFilters()");
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
            PrimeFaces.current().ajax().update("form:messages", "form:dataTable-empCertificationType");
            e.printStackTrace();
        }
    }

    public void deleteSelectedEmpCertificationType() {
        try {
            empCertificationTypeService.delete(selectedEmpCertificationTypeList);
            this.selectedEmpCertificationTypeList = null;
            empCertificationTypeList = empCertificationTypeService.findAll();
            UtilityHelper.addInfoMessage(localizationService.getInfoMessage().getString("operationSuccess"));
            PrimeFaces.current().ajax().update("form:messages", "form:dataTable-empCertificationType");
            PrimeFaces.current().executeScript("PF('widgetVarEmpCertificationType').clearFilters()");
        } catch (Exception e) {
            UtilityHelper.addErrorMessage(localizationService.getErrorMessage().getString("operationFaild"));
            PrimeFaces.current().ajax().update("form:messages", "form:dataTable-empCertificationType");
            e.printStackTrace();
        }
    }

    public EmpCertificationType getEmpCertificationType() {
        return empCertificationType;
    }

    public void setEmpCertificationType(EmpCertificationType empCertificationType) {
        this.empCertificationType = empCertificationType;
    }

    public List<EmpCertificationType> getEmpCertificationTypeList() {
        return empCertificationTypeList;
    }

    public void setEmpCertificationTypeList(List<EmpCertificationType> empCertificationTypeList) {
        this.empCertificationTypeList = empCertificationTypeList;
    }

    public List<EmpCertificationType> getSelectedEmpCertificationTypeList() {
        return selectedEmpCertificationTypeList;
    }

    public void setSelectedEmpCertificationTypeList(List<EmpCertificationType> selectedEmpCertificationTypeList) {
        this.selectedEmpCertificationTypeList = selectedEmpCertificationTypeList;
    }

}
