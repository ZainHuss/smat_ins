package com.smat.ins.model.service.impl;




import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.dao.CabinetFolderDao;
import com.smat.ins.model.service.CabinetFolderService;

public class CabinetFolderServiceImpl extends
		GenericServiceImpl<CabinetFolder, CabinetFolderDao, Long> implements   CabinetFolderService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6626083000638113156L;

	@Override
	public List<CabinetFolder> getByCabinetDefinition(CabinetDefinition cabinetDefinition) throws Exception{
		// TODO Auto-generated method stub
		return dao.getByCabinetDefinition(cabinetDefinition);
	}

	@Override
	public Integer getMaxCabinetCode(CabinetDefinition cabinetDefinition) throws Exception{
		// TODO Auto-generated method stub
		return dao.getMaxCabinetCode(cabinetDefinition);
	}

	@Override
	public CabinetFolder getBy(Long cabinetDefinitionId, String cabinetFolderCode) throws Exception{
		// TODO Auto-generated method stub
		return dao.getBy(cabinetDefinitionId, cabinetFolderCode);
	}

}
