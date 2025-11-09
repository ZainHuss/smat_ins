package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.EquipmentInspectionFormDao;
import com.smat.ins.model.dao.InspectionFormWorkflowDao;
import com.smat.ins.model.dao.InspectionFormWorkflowStepDao;
import com.smat.ins.model.service.EquipmentInspectionFormService;

import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.InspectionFormWorkflow;
import com.smat.ins.model.entity.InspectionFormWorkflowStep;

public class EquipmentInspectionFormServiceImpl extends
        GenericServiceImpl<EquipmentInspectionForm, EquipmentInspectionFormDao, Long> implements   EquipmentInspectionFormService {

    private InspectionFormWorkflowDao inspectionFormWorkflowDao;

    private InspectionFormWorkflowStepDao inspectionFormWorkflowStepDao;




    public InspectionFormWorkflowDao getInspectionFormWorkflowDao() {
        return inspectionFormWorkflowDao;
    }

    public void setInspectionFormWorkflowDao(InspectionFormWorkflowDao inspectionFormWorkflowDao) {
        this.inspectionFormWorkflowDao = inspectionFormWorkflowDao;
    }

    public InspectionFormWorkflowStepDao getInspectionFormWorkflowStepDao() {
        return inspectionFormWorkflowStepDao;
    }

    public void setInspectionFormWorkflowStepDao(InspectionFormWorkflowStepDao inspectionFormWorkflowStepDao) {
        this.inspectionFormWorkflowStepDao = inspectionFormWorkflowStepDao;
    }

    @Override
    public Integer getMaxReportNoCodeByEquipmentCat(String code) {
        // TODO Auto-generated method stub
        return dao.getMaxReportNoCodeByEquipmentCat(code);
    }

    @Override
    public Integer getMaxTimeSheetNoCodeByEquipmentCat(String code) {
        // TODO Auto-generated method stub
        return dao.getMaxTimeSheetNoCodeByEquipmentCat(code);
    }

    @Override
    public Integer getNextReportSeqByEquipmentCat(String code) {
        return dao.getNextReportSeqByEquipmentCat(code);
    }


    @Override
    public Integer getMaxJobNoCodeByEquipmentCat(String code) {
        // TODO Auto-generated method stub
        return dao.getMaxJobNoCodeByEquipmentCat(code);
    }

    @Override
    public Integer getMaxStickerNoCodeByEquipmentCat(String code) {
        // TODO Auto-generated method stub
        return dao.getMaxStickerNoCodeByEquipmentCat(code);
    }

    @Override
    public List<EquipmentInspectionForm> getForReview() {
        // TODO Auto-generated method stub
        return dao.getForReview();
    }

    @Override
    public EquipmentInspectionForm getBy(Integer taskId) {
        // TODO Auto-generated method stub
        return dao.getBy(taskId);
    }

    @Override
    public Boolean saveToStep(EquipmentInspectionForm equipmentInspectionForm, InspectionFormWorkflow inspectionFormWorkflow,
                              InspectionFormWorkflowStep inspectionFormWorkflowStep) throws Exception {
        // TODO Auto-generated method stub
        dao.update(equipmentInspectionForm);
        inspectionFormWorkflowDao.update(inspectionFormWorkflow);
        inspectionFormWorkflowStepDao.insert(inspectionFormWorkflowStep);
        return true;
    }

}
