package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.LinkedCorrespondence;

public interface LinkedCorrespondenceDao extends
		GenericDao<LinkedCorrespondence, Long> {
	public List<LinkedCorrespondence> getByCorrespondence(Long correspondenceId );
}
