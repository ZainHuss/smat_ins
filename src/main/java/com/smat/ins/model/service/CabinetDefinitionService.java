package com.smat.ins.model.service;



import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetDefinition;

public interface CabinetDefinitionService extends
		GenericService<CabinetDefinition, Long> {
	public List<CabinetDefinition> getByCabinet(Cabinet cabinet)throws Exception;
	
	public Integer getMaxCabinetDefinitionCode(Cabinet cabinet)throws Exception;
	
	public CabinetDefinition getBy(Long cabinetId, String cabinetDefinitionCode)throws Exception;

}
