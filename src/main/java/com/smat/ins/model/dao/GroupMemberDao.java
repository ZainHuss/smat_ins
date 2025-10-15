package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.GroupMember;
import com.smat.ins.model.entity.WorkingGroup;

public interface GroupMemberDao extends
		GenericDao<GroupMember, Long> {
	
	public List<GroupMember> getByWorkingGroup(WorkingGroup workingGroup);

}
