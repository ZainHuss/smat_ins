package com.smat.ins.model.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.EquipmentInspectionCertificateDao;
import com.smat.ins.model.entity.EquipmentInspectionCertificate;
import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.Sticker;


public class EquipmentInspectionCertificateDaoImpl extends
		GenericDaoImpl<EquipmentInspectionCertificate, Long> implements   EquipmentInspectionCertificateDao {

	@Override
	public EquipmentInspectionCertificate getBy(Long inspectionFormId) {
		// TODO Auto-generated method stub
		Session session = null;
		EquipmentInspectionCertificate equipmentInspectionCertificate = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EquipmentInspectionCertificate> criteriaQuery = criteriaBuilder.createQuery(EquipmentInspectionCertificate.class);
			Root<EquipmentInspectionCertificate> root = criteriaQuery.from(EquipmentInspectionCertificate.class);
			root.fetch("company");
			Join<EquipmentInspectionCertificate, EquipmentInspectionForm>joinEquipmentInspectionForm = root.join("equipmentInspectionForm",JoinType.INNER);
		
			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(joinEquipmentInspectionForm.get("id"), inspectionFormId));
			TypedQuery<EquipmentInspectionCertificate> typedQuery = session.createQuery(criteriaQuery);
			equipmentInspectionCertificate = typedQuery.getSingleResult();
			return equipmentInspectionCertificate;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return equipmentInspectionCertificate;
		}
	}

	@Override
	public EquipmentInspectionCertificate getBy(String serialNo, String stickerNo) {
		// TODO Auto-generated method stub
		Session session = null;
		EquipmentInspectionCertificate equipmentInspectionCertificate = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EquipmentInspectionCertificate> criteriaQuery = criteriaBuilder.createQuery(EquipmentInspectionCertificate.class);
			Root<EquipmentInspectionCertificate> root = criteriaQuery.from(EquipmentInspectionCertificate.class);
			root.fetch("company");
			Join<EquipmentInspectionCertificate, EquipmentInspectionForm>joinEquipmentInspectionForm = root.join("equipmentInspectionForm",JoinType.INNER);
			Join<EquipmentInspectionForm, Sticker>joinSticker = joinEquipmentInspectionForm.join("sticker",JoinType.INNER);
			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(joinSticker.get("serialNo"), serialNo),criteriaBuilder.equal(joinSticker.get("stickerNo"), stickerNo));
			TypedQuery<EquipmentInspectionCertificate> typedQuery = session.createQuery(criteriaQuery);
			equipmentInspectionCertificate = typedQuery.getSingleResult();
			return equipmentInspectionCertificate;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return equipmentInspectionCertificate;
		}
	}

}
