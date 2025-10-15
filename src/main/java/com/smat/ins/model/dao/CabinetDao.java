package com.smat.ins.model.dao;



import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.UserAlias;

public interface CabinetDao extends
		GenericDao<Cabinet, Long> {
	
	public Integer getMaxCabinetCode();
	
	public List<Cabinet> myCabinets(UserAlias userAlias);


}
