package com.smat.ins.model.service.impl;

import java.util.List;
import java.util.Date;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.BoxType;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.CorrespondenceRecipient;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.dao.CorrespondenceDao;
import com.smat.ins.model.dao.CorrespondenceRecipientDao;
import com.smat.ins.model.service.CorrespondenceService;

public class CorrespondenceServiceImpl extends GenericServiceImpl<Correspondence, CorrespondenceDao, Long>
		implements CorrespondenceService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4994149266621763278L;

	private CorrespondenceRecipientDao correspondenceRecipientDao;

	public CorrespondenceRecipientDao getCorrespondenceRecipientDao() {
		return correspondenceRecipientDao;
	}

	public void setCorrespondenceRecipientDao(CorrespondenceRecipientDao correspondenceRecipientDao) {
		this.correspondenceRecipientDao = correspondenceRecipientDao;
	}

	@Override
	public List<Correspondence> getDraftBox(CorrespondenceState correspondenceState, BoxType boxType,
			SysUser sysUserLogin) throws Exception {
		// TODO Auto-generated method stub
		return dao.getDraftBox(correspondenceState, boxType, sysUserLogin);
	}

	@Override
	public Long getLastSeq(String boxTypeCode, Long orgID) throws Exception {
		// TODO Auto-generated method stub
		return dao.getLastSeq(boxTypeCode, orgID);
	}

	@Override
	public List<Correspondence> getOutBox(CorrespondenceState correspondenceState, SysUser sysUserLogin)
			throws Exception {
		// TODO Auto-generated method stub
		return dao.getOutBox(correspondenceState, sysUserLogin);
	}

	@Override
	public List<Correspondence> search(BoxType boxType, Long seq, Date correspondenceDate, String fromNumber,
			String fromEntity, Date fromDate, String title) throws Exception {
		// TODO Auto-generated method stub
		return dao.search(boxType, seq, correspondenceDate, fromNumber, fromEntity, fromDate, title);
	}

	@Override
	public Boolean updateCorrespondenceWithRecipient(Correspondence correspondence,
			CorrespondenceRecipient correspondenceRecipient) throws Exception {
		// TODO Auto-generated method stub
		dao.update(correspondence);
		correspondenceRecipientDao.update(correspondenceRecipient);
		return true;

	}

}
