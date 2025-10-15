package com.smat.ins.model.service;



import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.CabinetLocation;

public interface CabinetLocationService extends
		GenericService<CabinetLocation, Long> {
	public CabinetLocation getDefaultLocation()throws Exception;
	public List<CabinetLocation> getCabinetLocationExceptDefault()throws Exception;

}
