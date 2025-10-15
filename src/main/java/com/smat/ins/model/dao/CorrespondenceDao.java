package com.smat.ins.model.dao;

import java.util.Date;
import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.BoxType;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;

public interface CorrespondenceDao extends GenericDao<Correspondence, Long> {

	public List<Correspondence> getDraftBox(CorrespondenceState correspondenceState, BoxType boxType,
			SysUser sysUserLogin);

	public Long getLastSeq(String boxTypeCode, Long orgID);

	public List<Correspondence> getOutBox(CorrespondenceState correspondenceState, SysUser sysUserLogin);
	
	public List<Correspondence> search(BoxType boxType,Long seq,Date correspondenceDate,String fromNumber,String fromEntity,Date fromDate,String title);

	
}
