package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.dao.CabinetDefinitionDao;

public class CabinetDefinitionDaoImpl extends
		GenericDaoImpl<CabinetDefinition, Long> implements   CabinetDefinitionDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4692375156603903918L;
	@Override
	public List<CabinetDefinition> getByCabinet(Cabinet cabinet) {
		// TODO Auto-generated method stub
		Session session = null;
		List<CabinetDefinition> cabinetDefinitions = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CabinetDefinition> criteriaQuery = criteriaBuilder.createQuery(CabinetDefinition.class);
			Root<CabinetDefinition> root = criteriaQuery.from(CabinetDefinition.class);
			root.join("cabinet", JoinType.LEFT);
			
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("drawerOrder")));
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(root.get("cabinet").get("id"), cabinet.getId()));

			TypedQuery<CabinetDefinition> typedQuery = session.createQuery(criteriaQuery);
			cabinetDefinitions = typedQuery.getResultList();
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return cabinetDefinitions;
	}
	@Override
	public Integer getMaxCabinetDefinitionCode(Cabinet cabinet) {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxCabinetDefinitionCode = null;

		try {
			session = sessionFactory.getCurrentSession();
			if (cabinet == null) {
				return Integer.parseInt((String) session.createNativeQuery(
						"SELECT COALESCE(MAX(SUBSTR(code,-2)),0) as Max_Drawer_Code from CABINET_DEFINITION CAB_DEF where CAB_DEF.CABINET is NULL".toLowerCase())
						.addScalar("Max_Drawer_Code").uniqueResult());

			} else {
				return Integer.parseInt( (String) session.createNativeQuery(
						"SELECT COALESCE(MAX(SUBSTR(code,-2)),0) as Max_Drawer_Code from CABINET_DEFINITION CAB_DEF where CAB_DEF.CABINET = ?".toLowerCase())
						.setParameter(1, cabinet.getId()).addScalar("Max_Drawer_Code").uniqueResult());
			}

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxCabinetDefinitionCode;
		}
	}
	@Override
	public CabinetDefinition getBy(Long cabinetId, String cabinetDefinitionCode) {
		// TODO Auto-generated method stub
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CabinetDefinition> criteriaQuery = criteriaBuilder.createQuery(CabinetDefinition.class);
			Root<CabinetDefinition> root = criteriaQuery.from(CabinetDefinition.class);
			root.join("cabinet", JoinType.LEFT);
			
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("drawerOrder")));
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.equal(root.get("cabinet").get("id"), cabinetId),criteriaBuilder.equal(root.get("code"), cabinetDefinitionCode)));

			TypedQuery<CabinetDefinition> typedQuery = session.createQuery(criteriaQuery);
			return typedQuery.getSingleResult();
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
			return null;
		}
		
	}
}
