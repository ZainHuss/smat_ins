package com.smat.ins.model.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.GeneralEquipmentItemDao;
import com.smat.ins.model.entity.GeneralEquipmentItem;

public class GeneralEquipmentItemDaoImpl extends
		GenericDaoImpl<GeneralEquipmentItem, Integer> implements   GeneralEquipmentItemDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4465368470824603936L;

	@Override
	public Integer getMaxGeneralEquipmentCode() {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxFormCode = null;
		try {
			session = sessionFactory.getCurrentSession();
			return Integer.parseInt((String) session
					.createNativeQuery(
							"select coalesce(max(substr(item_code,-3)),0) as Max_Code from general_equipment_item")
					.addScalar("Max_Code").uniqueResult());

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxFormCode;
		}
	}

}
