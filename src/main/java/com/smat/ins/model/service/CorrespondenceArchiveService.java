package com.smat.ins.model.service;




import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.CorrespondenceArchive;

public interface CorrespondenceArchiveService extends
		GenericService<CorrespondenceArchive, Long> {
	public List<CorrespondenceArchive> getByCorrespondence(Long correspondenceId )throws Exception;
}
