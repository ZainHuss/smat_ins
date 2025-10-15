package com.smat.ins.model.dao;

import java.util.Date;
import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.CorrespondenceRecipient;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;

public interface CorrespondenceRecipientDao extends GenericDao<CorrespondenceRecipient, Long> {
	public List<CorrespondenceRecipient> getInboxBySysUser(SysUser sysUserRecipient,
			CorrespondenceState correspondenceState, int offset, int pageSize);

	public Long getInboxCountBySysUser(SysUser sysUserRecipient, CorrespondenceState correspondenceState);
	
	public List<CorrespondenceRecipient> getAllInboxBySysUser(SysUser sysUserRecipient,
			CorrespondenceState correspondenceState, int offset, int pageSize);

	public Long getAllInboxCountBySysUser(SysUser sysUserRecipient, CorrespondenceState correspondenceState);

	public List<CorrespondenceRecipient> searchInMail(SysUser sysUserLogin, SysUser sysUserSender,
			Date correspondenceDate, String title);

	public List<CorrespondenceRecipient> getByCorrespondence(Correspondence correspondence);

}
