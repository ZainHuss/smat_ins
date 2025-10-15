package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.EquipmentInspectionCertificate;

public interface EquipmentInspectionCertificateService extends
		GenericService<EquipmentInspectionCertificate, Long> {
	public EquipmentInspectionCertificate getBy(Long inspectionFormId);
	public EquipmentInspectionCertificate getBy(String serialNo, String stickerNo);
}
