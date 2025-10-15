package com.smat.ins.model.service;




import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.LinkedCorrespondence;

public interface LinkedCorrespondenceService extends
		GenericService<LinkedCorrespondence, Long> {
	public List<LinkedCorrespondence> getByCorrespondence(Long correspondenceId )throws Exception;
}
