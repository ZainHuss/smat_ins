package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.entity.CabinetFolder;

public interface CabinetFolderDao extends GenericDao<CabinetFolder, Long> {
	public List<CabinetFolder> getByCabinetDefinition(CabinetDefinition cabinetDefinition);
	
	public Integer getMaxCabinetCode(CabinetDefinition cabinetDefinition);
	
	public CabinetFolder getBy(Long cabinetDefinitionId,String cabinetFolderCode);
}
