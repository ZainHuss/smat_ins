package com.smat.ins.model.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.GroupMember;
import com.smat.ins.model.entity.WorkingGroup;
import com.smat.ins.model.dao.GroupMemberDao;

public class GroupMemberDaoImpl extends
		GenericDaoImpl<GroupMember, Long> implements   GroupMemberDao {

	@Override
	public List<GroupMember> getByWorkingGroup(WorkingGroup workingGroup) {
		// TODO Auto-generated method stub
		Session session = null;
		List<GroupMember> groupMembers = null;
		try {
			session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(GroupMember.class, "groupMember");
			criteria.createAlias("groupMember.workingGroup", "workingGroup");
			criteria.createAlias("groupMember.sysUser", "sysUserCreator");	
			criteria.createAlias("groupMember.userAlias", "userAlias");
			
			criteria.createAlias("userAlias.organization", "organization");
			criteria.createAlias("userAlias.jobPosition", "jobPosition");
			criteria.createAlias("userAlias.sysUser", "sysUser");
			
			criteria.add(Restrictions.eq("workingGroup.id", workingGroup.getId()));
			groupMembers = criteria.list();
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return groupMembers;
	}

}
