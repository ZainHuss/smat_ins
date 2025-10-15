package com.smat.ins.model.service.impl;




import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.dao.CabinetDefinitionDao;
import com.smat.ins.model.service.CabinetDefinitionService;

public class CabinetDefinitionServiceImpl extends
		GenericServiceImpl<CabinetDefinition, CabinetDefinitionDao, Long> implements   CabinetDefinitionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7942083941500289328L;
	
	@Override
	public List<CabinetDefinition> getByCabinet(Cabinet cabinet) {
		// TODO Auto-generated method stub
		return dao.getByCabinet(cabinet);
	}
	@Override
	public Integer getMaxCabinetDefinitionCode(Cabinet cabinet) {
		// TODO Auto-generated method stub
		return dao.getMaxCabinetDefinitionCode(cabinet);
	}
	@Override
	public CabinetDefinition getBy(Long cabinetId, String cabinetDefinitionCode) {
		// TODO Auto-generated method stub
		return dao.getBy(cabinetId, cabinetDefinitionCode);
	}
}
