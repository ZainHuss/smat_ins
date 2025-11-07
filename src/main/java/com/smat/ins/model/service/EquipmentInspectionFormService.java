package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.InspectionFormWorkflow;
import com.smat.ins.model.entity.InspectionFormWorkflowStep;

public interface EquipmentInspectionFormService extends
        GenericService<EquipmentInspectionForm, Long> {
    public Integer getMaxReportNoCodeByEquipmentCat(String code);
    public Integer getMaxTimeSheetNoCodeByEquipmentCat(String code);
    public Integer getMaxJobNoCodeByEquipmentCat(String code);
    public Integer getMaxStickerNoCodeByEquipmentCat(String code);
    public List<EquipmentInspectionForm> getForReview();
    public EquipmentInspectionForm getBy(Integer taskId);

    public Boolean saveToStep(EquipmentInspectionForm equipmentInspectionForm,InspectionFormWorkflow inspectionFormWorkflow,
                              InspectionFormWorkflowStep inspectionFormWorkflowStep) throws Exception;
}
