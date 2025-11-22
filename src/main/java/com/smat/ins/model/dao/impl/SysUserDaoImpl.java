package com.smat.ins.model.dao.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysRole;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.SysUserRole;
import com.smat.ins.util.BCrypt;
import com.smat.ins.model.dao.SysUserDao;

public class SysUserDaoImpl extends GenericDaoImpl<SysUser, Long> implements SysUserDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7372958983115627452L;

	@Override
	public SysUser auth(String userName, String password) throws Exception {
		if (userName == null || password == null) {
			return null;
		}

		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<SysUser> cq = cb.createQuery(SysUser.class);
			Root<SysUser> root = cq.from(SysUser.class);
			cq.select(root);

			// fetch candidates using case-insensitive comparison to account for DB collation
			cq.where(cb.equal(cb.lower(root.get("userName")), userName.toLowerCase()));

			TypedQuery<SysUser> tq = session.createQuery(cq);
			List<SysUser> users = tq.getResultList();
			if (users == null || users.isEmpty()) {
				return null;
			}

			// Require exact username match (case-sensitive) and correct BCrypt password
			for (SysUser u : users) {
				try {
					if (u.getUserName() != null && u.getUserName().equals(userName)
							&& u.getPassword() != null && BCrypt.checkpw(password, u.getPassword())) {
						return u;
					}
				} catch (Exception ex) {
					// ignore individual user password check failures
				}
			}

			if (users.size() > 1) {
				log.warn("Multiple SysUser rows found for userName='" + userName
						+ "'. None matched exact case + password. Database should enforce unique user_name.");
			}

			// No exact-case username + password match found
			return null;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<SysRole> getExceptInUser(SysUser sysUser) {
		// TODO Auto-generated method stub
		Session session = null;
		List<SysRole> sysRoles = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<SysRole> criteriaQuery = criteriaBuilder.createQuery(SysRole.class);
			Subquery<Short> subQuery = criteriaQuery.subquery(Short.class);
			Root<SysRole> root = criteriaQuery.from(SysRole.class);
			Root<SysUserRole> subRoot = subQuery.from(SysUserRole.class);
			Join<SysUserRole, SysUser> joinSysUser = subRoot.join("sysUser", JoinType.INNER);
			subQuery.where(criteriaBuilder.equal(joinSysUser.get("id"), sysUser.getId()));
			subQuery.select(subRoot.get("sysRole").get("id"));
			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.not(root.get("id").in(subQuery)));
			TypedQuery<SysRole> typedQuery = session.createQuery(criteriaQuery);
			sysRoles = typedQuery.getResultList();
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception");
			e.printStackTrace();
		}
		return sysRoles;
	}

	@Override
	public Integer getMaxUserCodeByOrg(Organization organization) {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxUserCode = null;

		try {
			session = sessionFactory.getCurrentSession();
			if (organization != null) {
				Object res = session.createNativeQuery(
						"select COALESCE(MAX(SUBSTR(su.sys_user_code,-6)),0) as max_user_code from sys_user  su where su.organization = ?")
						.setParameter(1, organization.getId()).uniqueResult();
				if (res == null)
					return 0;
				try {
					String s = res.toString();
					java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)$").matcher(s);
					String digits = null;
					if (m.find()) {
						digits = m.group(1);
					} else {
						digits = s.replaceAll("\\D+", "");
					}
					if (digits == null || digits.isEmpty())
						return 0;
					return Integer.parseInt(digits);
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
					return 0;
				}

			} else
				return null;

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxUserCode;
		}
	}

	@Override
	public Integer getMaxUserNameCodeByRootOrg(Organization rootOrganization) {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxUserCode = null;

		try {
			session = sessionFactory.getCurrentSession();
			if (rootOrganization != null) {
				Object res = session.createNativeQuery(
						"select COALESCE(MAX(SUBSTR(su.user_name,-6)),0) as max_user_name from sys_user  su where su.root_organization = ?"
								.toLowerCase())
						.setParameter(1, rootOrganization.getId()).uniqueResult();
				if (res == null)
					return 0;
				try {
					String s = res.toString();
					java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)$").matcher(s);
					String digits = null;
					if (m.find()) {
						digits = m.group(1);
					} else {
						digits = s.replaceAll("\\D+", "");
					}
					if (digits == null || digits.isEmpty())
						return 0;
					return Integer.parseInt(digits);
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
					return 0;
				}

			} else
				return null;

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxUserCode;
		}
	}

	@Override
	public List<SysUser> getPagesByGloblFilter(Integer offset, Integer pageSize, String filterValue, String sortField,
			String sortOrder) {
		// TODO Auto-generated method stub
		Session session = null;
		List<SysUser> result = null;
		session = sessionFactory.getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<SysUser> criteriaQuery = criteriaBuilder.createQuery(SysUser.class);
		Root<SysUser> root = criteriaQuery.from(SysUser.class);
		criteriaQuery.select(root);
		if (filterValue != null && !filterValue.isEmpty()) {
			Predicate firstName = criteriaBuilder.like(root.get("firstName"), "%" + filterValue + "%");
			Predicate fatherName = criteriaBuilder.like(root.get("fatherName"), "%" + filterValue + "%");
			Predicate lastName = criteriaBuilder.like(root.get("lastName"), "%" + filterValue + "%");
			Predicate arDisplayName = criteriaBuilder.like(root.get("arDisplayName"), "%" + filterValue + "%");
			Predicate enDisplayName = criteriaBuilder.like(root.get("enDisplayName"), "%" + filterValue + "%");
			Predicate arOrganizationByRootOrganization = criteriaBuilder
					.like(root.get("organizationByRootOrganization").get("arabicName"), "%" + filterValue + "%");
			Predicate enOrganizationByRootOrganization = criteriaBuilder
					.like(root.get("organizationByRootOrganization").get("englishName"), "%" + filterValue + "%");
			Predicate userName = criteriaBuilder.like(root.get("userName"), "%" + filterValue + "%");

			Predicate finalPredicate = criteriaBuilder.or(firstName, fatherName, lastName, arDisplayName, enDisplayName,
					arOrganizationByRootOrganization, enOrganizationByRootOrganization, userName);
			criteriaQuery.where(finalPredicate);
		}

		if (sortField != null && !sortField.isEmpty()) {
			if (sortOrder.equalsIgnoreCase("ASCENDING")) {
				switch (sortField) {
				case "fullName":
					criteriaQuery.orderBy(criteriaBuilder.asc(root.get("firstName")),
							criteriaBuilder.asc(root.get("fatherName")), criteriaBuilder.asc(root.get("lastName")));
					break;

				case "displayName":
					criteriaQuery.orderBy(criteriaBuilder.asc(root.get("arDisplayName")));
					break;
				case "organizationByRootOrganization.name":
					criteriaQuery
							.orderBy(criteriaBuilder.asc(root.get("organizationByRootOrganization").get("arabicName")));
					break;
				case "organizationByOrganization.name":
					criteriaQuery
							.orderBy(criteriaBuilder.asc(root.get("organizationByOrganization").get("arabicName")));
					break;
				case "userName":
					criteriaQuery.orderBy(criteriaBuilder.asc(root.get("userName")));
					break;
				}
			} else {
				switch (sortField) {
				case "fullName":
					criteriaQuery.orderBy(criteriaBuilder.desc(root.get("firstName")),
							criteriaBuilder.desc(root.get("fatherName")), criteriaBuilder.desc(root.get("lastName")));
					break;
				case "displayName":
					criteriaQuery.orderBy(criteriaBuilder.desc(root.get("arDisplayName")));
					break;
				case "organizationByRootOrganization.name":
					criteriaQuery.orderBy(
							criteriaBuilder.desc(root.get("organizationByRootOrganization").get("arabicName")));
					break;
				case "organizationByOrganization.name":
					criteriaQuery
							.orderBy(criteriaBuilder.desc(root.get("organizationByOrganization").get("arabicName")));
					break;
				case "userName":
					criteriaQuery.orderBy(criteriaBuilder.desc(root.get("userName")));
					break;
				}
			}
		}

		TypedQuery<SysUser> typedQuery = session.createQuery(criteriaQuery);
		if (offset != null && pageSize != null) {
			typedQuery.setFirstResult(offset);
			typedQuery.setMaxResults(pageSize);
		}
		result = typedQuery.getResultList();
		return result;
	}

	@Override
	public Long getCountByGloblFilter(String filterValue) {
		// TODO Auto-generated method stub
		Session session = null;
		Long resultCount = null;
		session = sessionFactory.getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<SysUser> root = criteriaQuery.from(SysUser.class);
		criteriaQuery.select(criteriaBuilder.count(root));
		if (filterValue != null && !filterValue.isEmpty()) {
			Predicate firstName = criteriaBuilder.like(root.get("firstName"), "%" + filterValue + "%");
			Predicate fatherName = criteriaBuilder.like(root.get("fatherName"), "%" + filterValue + "%");
			Predicate lastName = criteriaBuilder.like(root.get("lastName"), "%" + filterValue + "%");
			Predicate arDisplayName = criteriaBuilder.like(root.get("arDisplayName"), "%" + filterValue + "%");
			Predicate enDisplayName = criteriaBuilder.like(root.get("enDisplayName"), "%" + filterValue + "%");
			Predicate arOrganizationByRootOrganization = criteriaBuilder
					.like(root.get("organizationByRootOrganization").get("arabicName"), "%" + filterValue + "%");
			Predicate enOrganizationByRootOrganization = criteriaBuilder
					.like(root.get("organizationByRootOrganization").get("englishName"), "%" + filterValue + "%");
			Predicate userName = criteriaBuilder.like(root.get("userName"), "%" + filterValue + "%");

			Predicate finalPredicate = criteriaBuilder.or(firstName, fatherName, lastName, arDisplayName, enDisplayName,
					arOrganizationByRootOrganization, enOrganizationByRootOrganization, userName);
			criteriaQuery.where(finalPredicate);
		}

		TypedQuery<Long> typedQuery = session.createQuery(criteriaQuery);
		resultCount = typedQuery.getSingleResult();
		return resultCount;
	}

	@Override
	public Boolean isUserHasPermission(Long userId, String permissionCode) {
		// TODO Auto-generated method stub
		Session session = null;
		Boolean result = false;

		try {
			session = sessionFactory.getCurrentSession();
			List<SysUser> users = session
					.createNativeQuery("SELECT * FROM sys_user su \r\n"
							+ " INNER JOIN sys_user_role sur ON su.id = sur.sys_user\r\n"
							+ " INNER JOIN sys_role_permission srp ON sur.sys_role = srp.sys_role\r\n"
							+ " INNER JOIN sys_permission sp ON srp.sys_permission = sp.id\r\n"
							+ " WHERE su.id=? AND sp.code=?", SysUser.class)
					.setParameter(1, userId).setParameter(2, permissionCode).getResultList();

			if (users != null && !users.isEmpty())
				result = true;
			return result;

		} catch (HibernateException e) {
			e.printStackTrace();
			return result;
		}
	}

	@Override
	public List<SysUser> listUserHasPersmission(String permissionCode) {
		// TODO Auto-generated method stub
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			List<SysUser> users = session.createNativeQuery(
					"SELECT * FROM sys_user su \r\n" + " INNER JOIN sys_user_role sur ON su.id = sur.sys_user\r\n"
							+ " INNER JOIN sys_role_permission srp ON sur.sys_role = srp.sys_role\r\n"
							+ " INNER JOIN sys_permission sp ON srp.sys_permission = sp.id\r\n" + " WHERE sp.code=?",
					SysUser.class).setParameter(1, permissionCode).getResultList();

			if (users != null && !users.isEmpty())

				return users;
			else
				return Collections.emptyList();

		} catch (HibernateException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

}
