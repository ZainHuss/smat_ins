package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.EquipmentInspectionFormItem;
import com.smat.ins.model.entity.InspectionFormWorkflow;
import com.smat.ins.model.entity.InspectionFormWorkflowStep;

public interface EquipmentInspectionFormItemService extends GenericService<EquipmentInspectionFormItem, Long> {
	public List<EquipmentInspectionFormItem> getByEquipmentForm(Long inspectionFormId);

	
}
