package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.CabinetLocation;

public interface CabinetLocationDao extends
		GenericDao<CabinetLocation, Long> {
	
	public CabinetLocation getDefaultLocation();
	public List<CabinetLocation> getCabinetLocationExceptDefault();

}
