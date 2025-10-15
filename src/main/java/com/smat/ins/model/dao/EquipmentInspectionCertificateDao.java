package com.smat.ins.model.dao;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.EquipmentInspectionCertificate;

public interface EquipmentInspectionCertificateDao extends
		GenericDao<EquipmentInspectionCertificate, Long> {

	public EquipmentInspectionCertificate getBy(Long inspectionFormId);
	
	public EquipmentInspectionCertificate getBy(String serialNo,String stickerNo);
}
