package com.smat.ins.model.service.impl;


import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.CabinetLocation;
import com.smat.ins.model.dao.CabinetLocationDao;
import com.smat.ins.model.service.CabinetLocationService;

public class CabinetLocationServiceImpl extends
		GenericServiceImpl<CabinetLocation, CabinetLocationDao, Long> implements   CabinetLocationService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5621917848192425331L;

	@Override
	public CabinetLocation getDefaultLocation() throws Exception{
		// TODO Auto-generated method stub
		return dao.getDefaultLocation();
	}

	@Override
	public List<CabinetLocation> getCabinetLocationExceptDefault() throws Exception{
		// TODO Auto-generated method stub
		return dao.getCabinetLocationExceptDefault();
	}

}
