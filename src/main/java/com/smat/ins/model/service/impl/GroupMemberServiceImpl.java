package com.smat.ins.model.service.impl;

import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.GroupMember;
import com.smat.ins.model.entity.WorkingGroup;
import com.smat.ins.model.dao.GroupMemberDao;
import com.smat.ins.model.service.GroupMemberService;

public class GroupMemberServiceImpl extends
		GenericServiceImpl<GroupMember, GroupMemberDao, Long> implements   GroupMemberService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1836533658135649230L;

	@Override
	public List<GroupMember> getByWorkingGroup(WorkingGroup workingGroup) throws Exception{
		// TODO Auto-generated method stub
		return dao.getByWorkingGroup(workingGroup);
	}

}
