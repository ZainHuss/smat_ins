package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;

import com.smat.ins.model.entity.EquipmentInspectionForm;

public interface EquipmentInspectionFormDao extends GenericDao<EquipmentInspectionForm, Long> {
	public Integer getMaxReportNoCodeByEquipmentCat(String code);
    Integer getNextReportSeqByEquipmentCat(String code);


    public Integer getMaxTimeSheetNoCodeByEquipmentCat(String code);

	public Integer getMaxJobNoCodeByEquipmentCat(String code);

	public Integer getMaxStickerNoCodeByEquipmentCat(String code);

	public List<EquipmentInspectionForm> getForReview();
	
	public EquipmentInspectionForm getBy(Integer taskId);
}
