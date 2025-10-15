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
import com.smat.ins.model.entity.JobPosition;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.dao.UserAliasDao;

public class UserAliasDaoImpl extends GenericDaoImpl<UserAlias, Long> implements UserAliasDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6737384202384871188L;

	@Override
	public List<UserAlias> getBySysUser(SysUser sysUser) {
		// TODO Auto-generated method stub
		Session session = null;
		List<UserAlias> usList = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<UserAlias> criteriaQuery = criteriaBuilder.createQuery(UserAlias.class);
			Root<UserAlias> root = criteriaQuery.from(UserAlias.class);
			root.join("organizationByRootOrganization", JoinType.LEFT);
			root.join("organizationByDivan", JoinType.LEFT);
			root.join("organizationByOrganization", JoinType.LEFT);
			root.join("jobPosition", JoinType.LEFT);
			root.join("sysUserBySysUser", JoinType.LEFT);
			criteriaQuery.select(root).distinct(true);

			criteriaQuery.where(criteriaBuilder.equal(root.get("sysUserBySysUser").get("id"), sysUser.getId()));

			TypedQuery<UserAlias> typedQuery = session.createQuery(criteriaQuery);
			usList = typedQuery.getResultList();
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return usList;
	}

	@Override
	public List<UserAlias> getAllWithDetails() {
		// TODO Auto-generated method stub
		Session session = null;
		List<UserAlias> usList = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<UserAlias> criteriaQuery = criteriaBuilder.createQuery(UserAlias.class);
			Root<UserAlias> root = criteriaQuery.from(UserAlias.class);
			root.join("organizationByRootOrganization", JoinType.LEFT);
			root.join("organizationByDivan", JoinType.LEFT);
			root.join("organizationByOrganization", JoinType.LEFT);
			root.join("jobPosition", JoinType.LEFT);
			root.join("sysUserBySysUser", JoinType.LEFT);
			criteriaQuery.select(root);
			TypedQuery<UserAlias> typedQuery = session.createQuery(criteriaQuery);
			usList = typedQuery.getResultList();
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return usList;
	}

	@Override
	public Integer getMaxUserAliasCodeByOrg(Organization organization) {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxUserAliasCode = null;

		try {
			session = sessionFactory.getCurrentSession();
			if (organization != null) {
				return Integer.parseInt((String) session.createNativeQuery(
						"select COALESCE(MAX(SUBSTR(ua.user_code,-5)),0) as max_user_alias_code from user_alias ua where ua.organization = ?".toLowerCase())
						.setParameter(1, organization.getId()).addScalar("Max_User_Alias_Code").uniqueResult());

			} else
				return null;

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxUserAliasCode;
		}
	}

	@Override
	public List<Organization> getListDivanBySysUser(SysUser sysUser) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Organization> divanList = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Organization> criteriaQuery = criteriaBuilder.createQuery(Organization.class);
			Root<UserAlias> root = criteriaQuery.from(UserAlias.class);
			root.join("organizationByRootOrganization", JoinType.LEFT);
			root.join("organizationByDivan", JoinType.LEFT);
			root.join("organizationByOrganization", JoinType.LEFT);
			root.join("jobPosition", JoinType.LEFT);
			root.join("sysUserBySysUser", JoinType.LEFT);
			criteriaQuery.select(root.get("organizationByDivan")).distinct(true);

			criteriaQuery.where(criteriaBuilder.equal(root.get("sysUserBySysUser").get("id"), sysUser.getId()));

			TypedQuery<Organization> typedQuery = session.createQuery(criteriaQuery);
			divanList = typedQuery.getResultList();
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return divanList;
	}

	@Override
	public List<UserAlias> getListRecipients(UserAlias userAliasSender) {
		// TODO Auto-generated method stub
		Session session = null;
		List<UserAlias> userAliasList = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<UserAlias> criteriaQuery = criteriaBuilder.createQuery(UserAlias.class);
			Root<UserAlias> root = criteriaQuery.from(UserAlias.class);
			Join<UserAlias, JobPosition> joinJobPosition = root.join("jobPosition", JoinType.LEFT);
			joinJobPosition.join("effectType", JoinType.LEFT);
			Join<UserAlias, Organization> joinUserOrg = root.join("organizationByOrganization", JoinType.LEFT);
			root.fetch("sysUserBySysUser");
			criteriaQuery.select(root);
			/***
			 * this predicate to find user alias who has same organization;
			 */

			Predicate sameOrganization = criteriaBuilder.equal(root.get("organizationByOrganization").get("id"),
					userAliasSender.getOrganizationByOrganization().getId());
			/**
			 * this predicate to find user alias who belong to same job position level and
			 * in the same parent organization but not in the same organization
			 */
			Predicate sameParentOrg = null;
			if (userAliasSender.getOrganizationByOrganization().getOrganization() == null)
				sameParentOrg = criteriaBuilder.isNull(joinUserOrg.get("organization").get("id"));
			else
				sameParentOrg = criteriaBuilder.equal(joinUserOrg.get("organization").get("id"),
						userAliasSender.getOrganizationByOrganization().getOrganization().getId());
			Predicate sameJobPositionLevel = criteriaBuilder.equal(root.get("jobPosition").get("definedLevel"),
					userAliasSender.getJobPosition().getDefinedLevel());
			Predicate notSameOrganization = criteriaBuilder.notEqual(root.get("organizationByOrganization").get("id"),
					userAliasSender.getOrganizationByOrganization().getId());
			Predicate sameLevelAndParentOrg = null;
			if (userAliasSender.getJobPosition().getEffectType().getCode().trim().equals("01"))
				sameLevelAndParentOrg = criteriaBuilder.and(sameJobPositionLevel, sameParentOrg, sameOrganization);
			else
				sameLevelAndParentOrg = criteriaBuilder.and(sameJobPositionLevel, sameParentOrg, notSameOrganization);
			/**
			 * this predicate to find user alias who defined_level >= low_send_level and
			 * defined_level <= user_alias_defined_level and his parent_organization equal
			 * to organization to specific user alias
			 */

			Predicate lowLevelSend = criteriaBuilder.and(
					criteriaBuilder.ge(root.get("jobPosition").get("definedLevel"), userAliasSender.getLowSendLevel()),
					criteriaBuilder.le(root.get("jobPosition").get("definedLevel"), userAliasSender.getDefinedLevel()),
					criteriaBuilder.equal(joinUserOrg.get("organization").get("id"),
							userAliasSender.getOrganizationByOrganization().getId()));

			/**
			 * this predicate to find user alias who defined_level <= high_send_level and
			 * defined_level >= user_alias_defined_level and his organization equal to
			 * parent organization to specific user alias
			 */
			Predicate highLevelSend = criteriaBuilder.and(
					criteriaBuilder.ge(root.get("jobPosition").get("definedLevel"), userAliasSender.getDefinedLevel()),
					criteriaBuilder.le(root.get("jobPosition").get("definedLevel"),
							userAliasSender.getHighSendLevel()));
			Predicate highLevelSendOrg = null;
			if (userAliasSender.getOrganizationByOrganization().getOrganization() == null)
				highLevelSendOrg = criteriaBuilder.equal(root.get("organizationByOrganization").get("id"),
						userAliasSender.getOrganizationByOrganization().getId());
			else
				highLevelSendOrg = criteriaBuilder.equal(root.get("organizationByOrganization").get("id"),
						userAliasSender.getOrganizationByOrganization().getOrganization().getId());

			Predicate highLevelSendAndOrg = criteriaBuilder.and(highLevelSend, highLevelSendOrg);

			/**
			 * this is define final predicate or
			 */
			Predicate finalPredicate = criteriaBuilder.or(sameOrganization, sameLevelAndParentOrg, lowLevelSend,
					highLevelSendAndOrg);

			criteriaQuery.where(finalPredicate);
			TypedQuery<UserAlias> typedQuery = session.createQuery(criteriaQuery);
			userAliasList = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return userAliasList;
	}

}
