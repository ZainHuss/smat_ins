package com.smat.ins.model.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.SysRole;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.SysUserRole;
import com.smat.ins.model.dao.SysUserRoleDao;


public class SysUserRoleDaoImpl extends
		GenericDaoImpl<SysUserRole, Long> implements   SysUserRoleDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4242656525699526882L;

	@Override
	public List<SysUserRole> getAllWithDetails() {
		// TODO Auto-generated method stub
		Session session = null;
		List<SysUserRole> sysUserRoles = null;
		try {
			session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(SysUserRole.class, "sysUserRole");
			criteria.createAlias("sysUserRole.sysRole", "sysRole");
			criteria.createAlias("sysUserRole.sysUser", "sysUser");	
			
			sysUserRoles = criteria.list();
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return sysUserRoles;
	}

	@Override
	public List<SysUserRole> getRoleBySysUser(SysUser sysUser) {
		// TODO Auto-generated method stub
		Session session = null;
		List<SysUserRole> sysUserRoles = null;
		try {
			session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(SysUserRole.class, "sysUserRole");
			criteria.createAlias("sysUserRole.sysRole", "sysRole");
			criteria.createAlias("sysUserRole.sysUser", "sysUser");	
			
			if (sysUser != null) {
				criteria.add(Restrictions.eq("sysUser.id", sysUser.getId()));

			}
			sysUserRoles = criteria.list();
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return sysUserRoles;
	}

	@Override
	public List<SysRole> getBySysUser(SysUser sysUser) {
		// TODO Auto-generated method stub
		Session session=null;
		Criteria criteria=null;
		List<SysRole> sysRoles=null;
		try{
			session=sessionFactory.getCurrentSession();
			criteria=session.createCriteria(SysRole.class, "sysRole");
			criteria.createAlias("sysRole.sysUserRoles", "sysUserRoles");
			criteria.createAlias("sysUserRoles.sysUser", "sysUser");
			criteria.add(Restrictions.eq("sysUser.id", sysUser.getId()));
			sysRoles=criteria.list();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return sysRoles;
	}

	

}
