package com.smat.ins.model.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.SysPermission;
import com.smat.ins.model.entity.SysRolePermission;
import com.smat.ins.model.dao.SysPermissionDao;

public class SysPermissionDaoImpl extends
		GenericDaoImpl<SysPermission, Short> implements   SysPermissionDao {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4717713512677202553L;

	@Override
	public List<SysPermission> getBySysUser(Long sysUserID) {
		// TODO Auto-generated method stub
		Session session = null;
		Criteria criteria = null;
		List<SysPermission> SysPermissions = null;
		try {
			session = sessionFactory.getCurrentSession();
			criteria = session.createCriteria(SysPermission.class, "SysPermission");
			criteria.createAlias("SysPermission.sysRolePermissions", "sysRolePermissions");
			criteria.createAlias("sysRolePermissions.sysRole", "sysRole");
			criteria.createAlias("sysRole.sysUserRoles", "sysUserRoles");
			criteria.createAlias("sysUserRoles.sysUser", "sysUser");
			criteria.add(Restrictions.eq("sysUser.id", sysUserID));
			SysPermissions = criteria.list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysPermissions;
	}

	@Override
	public List<SysPermission> getExceptInRole(Short roleId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<SysPermission> SysPermissions = null;
		try {
			session = sessionFactory.getCurrentSession();
			DetachedCriteria detachedCriteria=DetachedCriteria.forClass(SysRolePermission.class, "sysRolePermission");
			detachedCriteria.createAlias("sysRolePermission.sysRole", "sysRole");
			detachedCriteria.createAlias("sysRolePermission.sysPermission", "sysPermission");
			detachedCriteria.add(Restrictions.eq("sysRole.id", roleId));
			detachedCriteria.setProjection(Projections.property(
					  "sysPermission.id"));
			Criteria criteria = session.createCriteria(SysPermission.class, "sysPermissionObj");
			
			criteria.add(Subqueries.propertyNotIn("sysPermissionObj.id",
					  detachedCriteria));
			SysPermissions=criteria.list();
			
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return SysPermissions;
	}

	@Override
	public List<SysPermission> getByCode(String code) {
		// TODO Auto-generated method stub
		Session session = null;
		Criteria criteria = null;
		List<SysPermission> SysPermissions = null;
		try {
			session = sessionFactory.getCurrentSession();
			criteria = session.createCriteria(SysPermission.class, "SysPermission");
			criteria.add(Restrictions.eq("SysPermission.code", code));
			SysPermissions = criteria.list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysPermissions;
	}

}
