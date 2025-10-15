package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.ChecklistDetailDataSourceDao;
import com.smat.ins.model.entity.ChecklistDetailDataSource;



public class ChecklistDetailDataSourceDaoImpl extends
		GenericDaoImpl<ChecklistDetailDataSource, Integer> implements   ChecklistDetailDataSourceDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = -861041345090910497L;

	@Override
	public List<ChecklistDetailDataSource> getByDataSource(String code) {
		// TODO Auto-generated method stub
		Session session = null;
		List<ChecklistDetailDataSource> checklistDetailDataSources = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<ChecklistDetailDataSource> criteriaQuery = criteriaBuilder.createQuery(ChecklistDetailDataSource.class);
			Root<ChecklistDetailDataSource> root = criteriaQuery.from(ChecklistDetailDataSource.class);
			root.join("checklistDataSource",JoinType.LEFT);
			
			criteriaQuery.select(root);
			if (code != null && !code.trim().isEmpty())
				criteriaQuery.where(criteriaBuilder.equal(root.get("checklistDataSource").get("code"), code));
			// Add ordering - replace "columnName" with the actual column you want to sort by
		    criteriaQuery.orderBy(criteriaBuilder.desc(root.get("itemValue"))); // For ascending order
		    // OR for descending order:
		
			TypedQuery<ChecklistDetailDataSource> typedQuery = session.createQuery(criteriaQuery);
			checklistDetailDataSources = typedQuery.getResultList();
			return checklistDetailDataSources;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return checklistDetailDataSources;
		}
	}
	
	@Override
	public List<ChecklistDetailDataSource> getByDataSourceId(int id) {
		// TODO Auto-generated method stub
		Session session = null;
		List<ChecklistDetailDataSource> checklistDetailDataSources = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<ChecklistDetailDataSource> criteriaQuery = criteriaBuilder.createQuery(ChecklistDetailDataSource.class);
			Root<ChecklistDetailDataSource> root = criteriaQuery.from(ChecklistDetailDataSource.class);
			root.join("checklistDataSource",JoinType.LEFT);
			
			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(root.get("checklistDataSource").get("id"), id));
			
			TypedQuery<ChecklistDetailDataSource> typedQuery = session.createQuery(criteriaQuery);
			checklistDetailDataSources = typedQuery.getResultList();
			return checklistDetailDataSources;
		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return checklistDetailDataSources;
		}
	}

}
