package com.smat.ins.model.service.impl;




import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.LinkedCorrespondence;
import com.smat.ins.model.dao.LinkedCorrespondenceDao;
import com.smat.ins.model.service.LinkedCorrespondenceService;

public class LinkedCorrespondenceServiceImpl extends
		GenericServiceImpl<LinkedCorrespondence, LinkedCorrespondenceDao, Long> implements   LinkedCorrespondenceService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8442173305160052188L;

	@Override
	public List<LinkedCorrespondence> getByCorrespondence(Long correspondenceId) throws Exception{
		// TODO Auto-generated method stub
		return dao.getByCorrespondence(correspondenceId);
	}

}
