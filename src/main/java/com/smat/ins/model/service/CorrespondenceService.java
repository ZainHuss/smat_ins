package com.smat.ins.model.service;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.BoxType;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.CorrespondenceRecipient;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;

import java.util.Date;
import java.util.List;

public interface CorrespondenceService extends
		GenericService<Correspondence, Long> {
	public List<Correspondence> getDraftBox(CorrespondenceState correspondenceState, BoxType boxType,
			SysUser sysUserLogin)throws Exception;

	
	public Long getLastSeq(String boxTypeCode, Long orgID)throws Exception;
	
	public List<Correspondence> getOutBox(CorrespondenceState correspondenceState, SysUser sysUserLogin)throws Exception;
	
	public List<Correspondence> search(BoxType boxType, Long seq, Date correspondenceDate, String fromNumber,
			String fromEntity, Date fromDate, String title)throws Exception;
	public Boolean updateCorrespondenceWithRecipient(Correspondence correspondence,CorrespondenceRecipient correspondenceRecipient)throws Exception;
}
