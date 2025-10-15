package com.smat.ins.model.service;


import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.GroupMember;
import com.smat.ins.model.entity.WorkingGroup;

public interface GroupMemberService extends
		GenericService<GroupMember, Long> {
	public List<GroupMember> getByWorkingGroup(WorkingGroup workingGroup)throws Exception;

}
