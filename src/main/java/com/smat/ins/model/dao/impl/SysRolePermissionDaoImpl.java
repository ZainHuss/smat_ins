package com.smat.ins.model.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.SysRolePermission;
import com.smat.ins.model.dao.SysRolePermissionDao;

public class SysRolePermissionDaoImpl extends
		GenericDaoImpl<SysRolePermission, Long> implements   SysRolePermissionDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2610969136570919956L;

	@Override
	public List<SysRolePermission> getBySysRoleWithDetails(Short sysRoleID) {
		// TODO Auto-generated method stub
				Session session = null;
				List<SysRolePermission> rolePermissions = null;
				try {
					session = sessionFactory.getCurrentSession();
					Criteria criteria = session.createCriteria(SysRolePermission.class, "sysRolePermission");
					criteria.createAlias("sysRolePermission.sysRole", "sysRole");
					criteria.createAlias("sysRolePermission.sysPermission", "sysPermission");	
					criteria.add(Restrictions.eq("sysRole.id", sysRoleID));
					rolePermissions = criteria.list();
				} catch (Exception e) {
					log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
					e.printStackTrace();
				}
				return rolePermissions;
	}

}
