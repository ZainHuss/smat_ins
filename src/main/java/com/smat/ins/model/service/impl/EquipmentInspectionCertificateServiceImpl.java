package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;

import com.smat.ins.model.dao.EquipmentInspectionCertificateDao;

import com.smat.ins.model.service.EquipmentInspectionCertificateService;

import com.smat.ins.model.entity.EquipmentInspectionCertificate;

public class EquipmentInspectionCertificateServiceImpl extends
		GenericServiceImpl<EquipmentInspectionCertificate, EquipmentInspectionCertificateDao, Long> implements   EquipmentInspectionCertificateService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6627783528077664042L;

	@Override
	public EquipmentInspectionCertificate getBy(Long inspectionFormId) {
		// TODO Auto-generated method stub
		return dao.getBy(inspectionFormId);
	}

	@Override
	public EquipmentInspectionCertificate getBy(String serialNo, String stickerNo) {
		// TODO Auto-generated method stub
		return dao.getBy(serialNo, stickerNo);
	}

}
