package com.smat.ins.model.service;



import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.entity.CabinetFolder;

public interface CabinetFolderService extends
		GenericService<CabinetFolder, Long> {
	public List<CabinetFolder> getByCabinetDefinition(CabinetDefinition cabinetDefinition)throws Exception;

	public Integer getMaxCabinetCode(CabinetDefinition cabinetDefinition)throws Exception;

	public CabinetFolder getBy(Long cabinetDefinitionId, String cabinetFolderCode)throws Exception;
}
