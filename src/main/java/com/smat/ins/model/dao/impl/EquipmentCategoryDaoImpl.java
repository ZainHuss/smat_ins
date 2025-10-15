package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.EquipmentCategoryDao;
import com.smat.ins.model.entity.EquipmentCategory;


public class EquipmentCategoryDaoImpl extends
		GenericDaoImpl<EquipmentCategory, Short> implements   EquipmentCategoryDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4855965011652892504L;

	@Override
	public List<EquipmentCategory> getCatWithTemplateCreated() {
		// TODO Auto-generated method stub
		Session session = null;
		List<EquipmentCategory> equipmentCategories=null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<EquipmentCategory> criteriaQuery = criteriaBuilder
					.createQuery(EquipmentCategory.class);
			Root<EquipmentCategory> root = criteriaQuery.from(EquipmentCategory.class);
			
			root.join("formTemplates",
					JoinType.INNER);

			criteriaQuery.select(root);
			

			TypedQuery<EquipmentCategory> typedQuery = session.createQuery(criteriaQuery);
			equipmentCategories = typedQuery.getResultList();
			return equipmentCategories;

		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return equipmentCategories;
		}
	}

}
