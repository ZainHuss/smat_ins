package com.smat.ins.model.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.CompanyDao;
import com.smat.ins.model.entity.Company;

public class CompanyDaoImpl extends
		GenericDaoImpl<Company, Integer> implements   CompanyDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4720828212724336654L;

	@Override
	public Integer getMaxCompanyCode() {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxCabinetDefinitionCode = null;

		try {
			session = sessionFactory.getCurrentSession();
			
				return Integer.parseInt((String) session.createNativeQuery(
						"SELECT COALESCE(MAX(SUBSTR(code,-3)),0) as Max_Comp_Code from company c".toLowerCase())
						.addScalar("Max_Comp_Code").uniqueResult());

			

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxCabinetDefinitionCode;
		}
	}

}
