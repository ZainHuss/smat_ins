package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.EquipmentInspectionFormItemDao;
import com.smat.ins.model.entity.EquipmentCategory;
import com.smat.ins.model.entity.EquipmentInspectionForm;
import com.smat.ins.model.entity.EquipmentInspectionFormItem;

import com.smat.ins.model.entity.GeneralEquipmentItem;
import com.smat.ins.model.entity.ItemType;

public class EquipmentInspectionFormItemDaoImpl extends
		GenericDaoImpl<EquipmentInspectionFormItem, Long> implements   EquipmentInspectionFormItemDao {

	@Override
	public List<EquipmentInspectionFormItem> getByEquipmentForm(Long inspectionFormId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<EquipmentInspectionFormItem> equipmentInspectionFormItems = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EquipmentInspectionFormItem> criteriaQuery = criteriaBuilder.createQuery(EquipmentInspectionFormItem.class);
			Root<EquipmentInspectionFormItem> root = criteriaQuery.from(EquipmentInspectionFormItem.class);
			Join<EquipmentInspectionFormItem, EquipmentInspectionForm> joinEquipmentInspectionForm = root.join("equipmentInspectionForm",
					JoinType.LEFT);
			Join<EquipmentInspectionFormItem, GeneralEquipmentItem> joinGeneralEquipmentItem = root.join("generalEquipmentItem",
					JoinType.LEFT);
			Join<GeneralEquipmentItem, ItemType> joinItemType = joinGeneralEquipmentItem.join("itemType",
					JoinType.LEFT);
			criteriaQuery.select(root);
			if (inspectionFormId != null )
				criteriaQuery.where(criteriaBuilder.equal(joinEquipmentInspectionForm.get("id"), inspectionFormId));
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("itemOrder")));
			TypedQuery<EquipmentInspectionFormItem> typedQuery = session.createQuery(criteriaQuery);
			equipmentInspectionFormItems = typedQuery.getResultList();
			return equipmentInspectionFormItems;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return equipmentInspectionFormItems;
		}
	}

}
