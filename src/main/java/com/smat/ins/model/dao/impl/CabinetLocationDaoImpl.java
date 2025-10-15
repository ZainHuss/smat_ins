package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.CabinetLocation;
import com.smat.ins.model.dao.CabinetLocationDao;



public class CabinetLocationDaoImpl extends
		GenericDaoImpl<CabinetLocation, Long> implements   CabinetLocationDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3487755827690707488L;

	@Override
	public CabinetLocation getDefaultLocation() {
		// TODO Auto-generated method stub
		Session session = null;
		CabinetLocation cabinetLocation = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CabinetLocation> criteriaQuery = criteriaBuilder.createQuery(CabinetLocation.class);
			Root<CabinetLocation> root = criteriaQuery.from(CabinetLocation.class);
			criteriaQuery.select(root);
			
			criteriaQuery.where(criteriaBuilder.equal(root.get("isDefault"), true));
			TypedQuery<CabinetLocation> typedQuery = session.createQuery(criteriaQuery);
			cabinetLocation = typedQuery.getSingleResult();
			return cabinetLocation;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return cabinetLocation;
		}
	}

	@Override
	public List<CabinetLocation> getCabinetLocationExceptDefault() {
		// TODO Auto-generated method stub
		Session session = null;
		List<CabinetLocation> cabinetLocations = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CabinetLocation> criteriaQuery = criteriaBuilder.createQuery(CabinetLocation.class);
			Root<CabinetLocation> root = criteriaQuery.from(CabinetLocation.class);
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.or(criteriaBuilder.equal(root.get("isDefault"), false),criteriaBuilder.isNull(root.get("isDefault"))));

			TypedQuery<CabinetLocation> typedQuery = session.createQuery(criteriaQuery);
			cabinetLocations = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return cabinetLocations;
	}

}
