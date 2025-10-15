package com.smat.ins.model.service.impl;



import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.CorrespondenceArchive;
import com.smat.ins.model.dao.CorrespondenceArchiveDao;
import com.smat.ins.model.service.CorrespondenceArchiveService;

public class CorrespondenceArchiveServiceImpl extends
		GenericServiceImpl<CorrespondenceArchive, CorrespondenceArchiveDao, Long> implements   CorrespondenceArchiveService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6043193539359758239L;

	@Override
	public List<CorrespondenceArchive> getByCorrespondence(Long correspondenceId) throws Exception{
		// TODO Auto-generated method stub
		return dao.getByCorrespondence(correspondenceId);
	}

}
