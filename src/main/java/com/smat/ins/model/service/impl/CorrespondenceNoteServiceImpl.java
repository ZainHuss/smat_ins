package com.smat.ins.model.service.impl;




import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.CorrespondenceNote;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.dao.CorrespondenceNoteDao;
import com.smat.ins.model.service.CorrespondenceNoteService;

public class CorrespondenceNoteServiceImpl extends
		GenericServiceImpl<CorrespondenceNote, CorrespondenceNoteDao, Long> implements   CorrespondenceNoteService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2983441405827114645L;

	@Override
	public List<CorrespondenceNote> getCorrespondenceNotes(Correspondence correspondence)throws Exception {
		// TODO Auto-generated method stub
		return dao.getCorrespondenceNotes(correspondence);
	}

	@Override
	public List<CorrespondenceNote> getOutBoxBySysUser(CorrespondenceState correspondenceState, SysUser sysUserSender,
			int offset, int pageSize) throws Exception{
		// TODO Auto-generated method stub
		return dao.getOutBoxBySysUser(correspondenceState, sysUserSender, offset, pageSize);
	}

	@Override
	public Long getOutBoxCountBySysUser(CorrespondenceState correspondenceState, SysUser sysUserSender) throws Exception{
		// TODO Auto-generated method stub
		return dao.getOutBoxCountBySysUser(correspondenceState, sysUserSender);
	}

}
