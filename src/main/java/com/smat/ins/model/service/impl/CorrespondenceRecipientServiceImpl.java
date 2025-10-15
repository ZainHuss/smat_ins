package com.smat.ins.model.service.impl;

import java.util.List;
import java.util.Date;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.CorrespondenceRecipient;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.dao.CorrespondenceRecipientDao;
import com.smat.ins.model.service.CorrespondenceRecipientService;

public class CorrespondenceRecipientServiceImpl
		extends GenericServiceImpl<CorrespondenceRecipient, CorrespondenceRecipientDao, Long>
		implements CorrespondenceRecipientService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -184073501898103247L;

	@Override
	public List<CorrespondenceRecipient> getInboxBySysUser(SysUser sysUserRecipient,
			CorrespondenceState correspondenceState,int offset, int pageSize) throws Exception{
		// TODO Auto-generated method stub
		return dao.getInboxBySysUser(sysUserRecipient, correspondenceState,offset,pageSize);
	}
	@Override
	public Long getInboxCountBySysUser(SysUser sysUserRecipient, CorrespondenceState correspondenceState)throws Exception {
		return dao.getInboxCountBySysUser(sysUserRecipient, correspondenceState);
	}

	@Override
	public List<CorrespondenceRecipient> searchInMail(SysUser sysUserLogin, SysUser sysUserSender,
			Date correspondenceDate, String title)throws Exception {
		// TODO Auto-generated method stub
		return dao.searchInMail(sysUserLogin, sysUserSender, correspondenceDate, title);
	}

	@Override
	public List<CorrespondenceRecipient> getByCorrespondence(Correspondence correspondence) throws Exception{
		// TODO Auto-generated method stub
		return dao.getByCorrespondence(correspondence);
	}
	@Override
	public List<CorrespondenceRecipient> getAllInboxBySysUser(SysUser sysUserRecipient,
			CorrespondenceState correspondenceState, int offset, int pageSize) throws Exception{
		// TODO Auto-generated method stub
		return dao.getAllInboxBySysUser(sysUserRecipient, correspondenceState, offset, pageSize);
	}
	@Override
	public Long getAllInboxCountBySysUser(SysUser sysUserRecipient, CorrespondenceState correspondenceState) throws Exception{
		// TODO Auto-generated method stub
		return dao.getAllInboxCountBySysUser(sysUserRecipient, correspondenceState);
	}

}
