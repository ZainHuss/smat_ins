package com.smat.ins.model.service;

import java.util.Date;

import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.CorrespondenceRecipient;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;

public interface CorrespondenceRecipientService extends GenericService<CorrespondenceRecipient, Long> {
	public List<CorrespondenceRecipient> getInboxBySysUser(SysUser sysUserRecipient,
			CorrespondenceState correspondenceState, int offset, int pageSize)throws Exception;

	public Long getInboxCountBySysUser(SysUser sysUserRecipient, CorrespondenceState correspondenceState)throws Exception;
	
	public List<CorrespondenceRecipient> getAllInboxBySysUser(SysUser sysUserRecipient,
			CorrespondenceState correspondenceState, int offset, int pageSize)throws Exception;

	public Long getAllInboxCountBySysUser(SysUser sysUserRecipient, CorrespondenceState correspondenceState)throws Exception;

	public List<CorrespondenceRecipient> searchInMail(SysUser sysUserLogin, SysUser sysUserSender,
			Date correspondenceDate, String title)throws Exception;

	public List<CorrespondenceRecipient> getByCorrespondence(Correspondence correspondence)throws Exception;

}
