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
			Object result = session
					.createNativeQuery(
							"select coalesce(max(cast(item_code as unsigned)),0) as Max_Code from general_equipment_item")
					.uniqueResult();

			if (result == null) {
				return 0;
			}

			if (result instanceof Number) {
				return ((Number) result).intValue();
			}

			try {
				return Integer.parseInt(result.toString());
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
				return 0;
			}

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxFormCode;
		}
	}

}
