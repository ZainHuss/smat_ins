package com.smat.ins.model.service.impl;




import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.dao.CabinetDao;
import com.smat.ins.model.service.CabinetService;

public class CabinetServiceImpl extends
		GenericServiceImpl<Cabinet, CabinetDao, Long> implements   CabinetService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7376912129586530297L;

	@Override
	public Integer getMaxCabinetCode() throws Exception{
		// TODO Auto-generated method stub
		return dao.getMaxCabinetCode();
	}

	@Override
	public List<Cabinet> myCabinets(UserAlias userAlias)throws Exception {
		// TODO Auto-generated method stub
		return dao.myCabinets(userAlias);
	}

}
