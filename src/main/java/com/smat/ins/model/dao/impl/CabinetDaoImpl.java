package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetType;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.dao.CabinetDao;

public class CabinetDaoImpl extends GenericDaoImpl<Cabinet, Long> implements CabinetDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3933988703087520563L;

	@Override
	public Integer getMaxCabinetCode() {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxCabinetCode = null;

		try {
			session = sessionFactory.getCurrentSession();
			Object result = session
					.createNativeQuery("SELECT COALESCE(MAX(SUBSTR(code,-6)),0) as Max_Cabinet_Code from CABINET CAB ".toLowerCase())
					.uniqueResult();
			if (result == null) return 0;
			String s = result.toString();
			// strip non-digits to avoid NumberFormatException when codes contain letters
			String digits = s.replaceAll("\\D+", "");
			if (digits == null || digits.isEmpty()) return 0;
			try {
				return Integer.parseInt(digits);
			} catch (NumberFormatException nfe) {
				// fallback
				return 0;
			}
		} catch (HibernateException e) {
			e.printStackTrace();
			return maxCabinetCode;
		}
	}

	@Override
	public List<Cabinet> myCabinets(UserAlias userAlias) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Cabinet> cabinets = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Cabinet> criteriaQuery = criteriaBuilder.createQuery(Cabinet.class);
			Root<Cabinet> root = criteriaQuery.from(Cabinet.class);
			Join<Cabinet, UserAlias> joinUserAlias = root.join("userAlias", JoinType.LEFT);
			Join<Cabinet, Organization> joinOrganization = root.join("organization", JoinType.LEFT);
			Join<Cabinet, CabinetType> joinCabinetType=root.join("cabinetType", JoinType.INNER);
			criteriaQuery.select(root);

			Predicate predicateUserAlias = criteriaBuilder.and(
					criteriaBuilder.equal(joinUserAlias.get("id"), userAlias.getId()),
					criteriaBuilder.equal(joinCabinetType.get("code"), "02"));
			Predicate predicateOrganization = criteriaBuilder.and(
					criteriaBuilder.equal(joinOrganization.get("id"),
							userAlias.getOrganizationByOrganization().getId()),
					criteriaBuilder.equal(joinCabinetType.get("code"), "03"));
			Predicate predicatePublicCabinet = criteriaBuilder.equal(joinCabinetType.get("code"), "01");

			criteriaQuery.where(criteriaBuilder.or(predicateUserAlias, predicateOrganization, predicatePublicCabinet));
			TypedQuery<Cabinet> typedQuery = session.createQuery(criteriaQuery);
			cabinets = typedQuery.getResultList();
			return cabinets;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return cabinets;
		}
	}

}
