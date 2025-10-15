package com.smat.ins.model.service;



import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.UserAlias;

public interface CabinetService extends
		GenericService<Cabinet, Long> {
	public Integer getMaxCabinetCode()throws Exception;
	
	public List<Cabinet> myCabinets(UserAlias userAlias)throws Exception;
}
