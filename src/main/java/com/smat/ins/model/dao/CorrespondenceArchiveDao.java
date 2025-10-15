package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.CorrespondenceArchive;

public interface CorrespondenceArchiveDao extends
		GenericDao<CorrespondenceArchive, Long> {

	public List<CorrespondenceArchive> getByCorrespondence(Long correspondenceId );
}
