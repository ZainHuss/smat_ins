package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.dao.OrganizationDao;

public class OrganizationDaoImpl extends GenericDaoImpl<Organization, Long> implements OrganizationDao {
	/**
	 * 
	 */
	private static final long serialVersionUID = -905201791195798887L;

	@Override
	public List<Organization> getByParent(Organization organization, Integer start, Integer pageSize) {
		// TODO Auto-generated method stub
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Organization> criteriaQuery = criteriaBuilder.createQuery(Organization.class);
			Root<Organization> root = criteriaQuery.from(Organization.class);
			Join<Organization, Organization> parentOrganization = root.join("organization", JoinType.LEFT);
			root.join("hierarchySystemDef", JoinType.LEFT);
			root.join("hierarchySystem",JoinType.LEFT);
			criteriaQuery.select(root);

			if (organization == null)
				criteriaQuery.where(criteriaBuilder.isNull(parentOrganization));
			else
				criteriaQuery.where(criteriaBuilder.equal(parentOrganization.get("id"), organization.getId()));
			TypedQuery<Organization> typedQuery = session.createQuery(criteriaQuery);
			if (start != null && pageSize != null) {
				typedQuery.setFirstResult(start);
				typedQuery.setMaxResults(pageSize);
			}

			return typedQuery.getResultList();

		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Organization> getByNameOrCode(String searchKey) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Organization> organizations = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Organization> criteriaQuery = criteriaBuilder.createQuery(Organization.class);
			Root<Organization> root = criteriaQuery.from(Organization.class);
			root.join("organization", JoinType.LEFT);
			root.join("hierarchySystemDef", JoinType.LEFT);
			root.join("hierarchySystem",JoinType.LEFT);
			criteriaQuery.select(root);
			if (searchKey != null && !searchKey.trim().isEmpty())
				criteriaQuery.where(criteriaBuilder.like(root.get("code"), "%" + searchKey + "%"));

			TypedQuery<Organization> typedQuery = session.createQuery(criteriaQuery);
			organizations = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return organizations;
	}

	@Override
	public List<Organization> getByNameOrCode(String arabicName, String code) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Organization> organizations = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Organization> criteriaQuery = criteriaBuilder.createQuery(Organization.class);
			Root<Organization> root = criteriaQuery.from(Organization.class);
			root.join("organization", JoinType.LEFT);
			root.join("hierarchySystemDef", JoinType.LEFT);
			root.join("hierarchySystem",JoinType.LEFT);
			criteriaQuery.select(root);
			if (arabicName != null && !arabicName.trim().isEmpty())
				criteriaQuery.where(criteriaBuilder.like(root.get("arabicName"), "%" + arabicName + "%"));
			if (code != null && !code.trim().isEmpty())
				criteriaQuery.where(criteriaBuilder.like(root.get("code"), "%" + code + "%"));
			TypedQuery<Organization> typedQuery = session.createQuery(criteriaQuery);
			organizations = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return organizations;

	}

	@Override
	public Long getByParentCount(Organization organization) {
		// TODO Auto-generated method stub
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
			Root<Organization> root = criteriaQuery.from(Organization.class);
			Join<Organization, Organization> parentOrganization = root.join("organization", JoinType.LEFT);
			root.join("hierarchySystemDef", JoinType.LEFT);
			root.join("hierarchySystem",JoinType.LEFT);
			if (organization == null)
				criteriaQuery.select(criteriaBuilder.count(root)).where(criteriaBuilder.isNull(parentOrganization));
			else
				criteriaQuery.select(criteriaBuilder.count(root))
						.where(criteriaBuilder.equal(parentOrganization.get("id"), organization.getId()));
			TypedQuery<Long> typedQuery = session.createQuery(criteriaQuery);
			return typedQuery.getSingleResult();

		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public Organization getOrganizationWithDetails(Organization organization) {
		// TODO Auto-generated method stub
		Session session = null;
		Organization parentOrg = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Organization> criteriaQuery = criteriaBuilder.createQuery(Organization.class);
			Root<Organization> root = criteriaQuery.from(Organization.class);
			root.join("organization", JoinType.LEFT);
			root.join("hierarchySystemDef", JoinType.LEFT);
			root.join("hierarchySystem",JoinType.LEFT);
			if (organization != null) {
				criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), organization.getId()));
				TypedQuery<Organization> typedQuery = session.createQuery(criteriaQuery);
				parentOrg = typedQuery.getSingleResult();
				return parentOrg;
			} else {
				return null;
			}

		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Organization> getAllWithDetails() {
		// TODO Auto-generated method stub
		Session session = null;
		List<Organization> organizations = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Organization> criteriaQuery = criteriaBuilder.createQuery(Organization.class);
			Root<Organization> root = criteriaQuery.from(Organization.class);
			root.join("organization", JoinType.LEFT);
			root.join("hierarchySystemDef", JoinType.LEFT);
			root.join("hierarchySystem",JoinType.LEFT);
			criteriaQuery.select(root);
			TypedQuery<Organization> typedQuery = session.createQuery(criteriaQuery);
			organizations = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return organizations;
	}

	@Override
	public Organization getParentOrganization(Organization organization) {
		// TODO Auto-generated method stub
		Session session = null;
		Organization parentOrg = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Organization> criteriaQuery = criteriaBuilder.createQuery(Organization.class);
			Root<Organization> root = criteriaQuery.from(Organization.class);
			root.join("organization", JoinType.LEFT);
			root.join("hierarchySystemDef", JoinType.LEFT);
			root.join("hierarchySystem",JoinType.LEFT);
			if (organization != null) {
				criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), organization.getId()));
				TypedQuery<Organization> typedQuery = session.createQuery(criteriaQuery);
				parentOrg = typedQuery.getSingleResult();
				if (parentOrg.getOrganization() != null)
					return parentOrg.getOrganization();
				else
					return parentOrg;
			} else {
				return null;
			}

		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Integer getMaxLevelOrgCode(Organization parentOrg) {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxLevelOrgCode = null;

		try {
			session = sessionFactory.getCurrentSession();
			if (parentOrg == null) {
				return Integer.parseInt((String) session.createNativeQuery(
						"select COALESCE(MAX(SUBSTR(code,-3)),0) as max_level_org_code from organization org where org.parent_organization is NULL".toLowerCase())
						.addScalar("max_level_org_code").uniqueResult());

			} else {
				return Integer.parseInt( (String) session.createNativeQuery(
						"select COALESCE(MAX(SUBSTR(code,-3)),0) as max_level_org_code from organization org where org.parent_organization = ?".toLowerCase())
						.setParameter(1, parentOrg.getId()).addScalar("Max_Level_Org_Code").uniqueResult());
			}

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxLevelOrgCode;
		}
	}

	@Override
	public boolean checkIfFound(Organization organization) {
		// TODO Auto-generated method stub
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Organization> criteriaQuery = criteriaBuilder.createQuery(Organization.class);
			Root<Organization> root = criteriaQuery.from(Organization.class);
			root.join("organization", JoinType.LEFT);
			root.join("hierarchySystemDef", JoinType.LEFT);
			root.join("hierarchySystem", JoinType.LEFT);
			criteriaQuery.select(root).where(
					criteriaBuilder.equal(root.get("arabicName"), organization.getArabicName()),
					criteriaBuilder.equal(root.get("englishName"), organization.getEnglishName()));
			TypedQuery<Organization> typedQuery = session.createQuery(criteriaQuery);
			Organization org = typedQuery.getSingleResult();
			if (org != null)
				return true;
			else
				return false;
			
		} catch (HibernateException e) {
			e.printStackTrace();
			return true;
		}
	}
	
	
}
