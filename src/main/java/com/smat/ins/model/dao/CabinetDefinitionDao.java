package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetDefinition;

public interface CabinetDefinitionDao extends
		GenericDao<CabinetDefinition, Long> {
	public List<CabinetDefinition> getByCabinet(Cabinet cabinet);
	
	public Integer getMaxCabinetDefinitionCode(Cabinet cabinet);
	
	public CabinetDefinition getBy(Long cabinetId,String cabinetDefinitionCode);
}
