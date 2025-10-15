package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.FormColumnDao;

import com.smat.ins.model.entity.FormColumn;



public class FormColumnDaoImpl extends
		GenericDaoImpl<FormColumn, Integer> implements   FormColumnDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2854727787273298484L;

	@Override
	public List<FormColumn> getBy(Integer rowId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<FormColumn> formColumns = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<FormColumn> criteriaQuery = criteriaBuilder.createQuery(FormColumn.class);
			Root<FormColumn> root = criteriaQuery.from(FormColumn.class);
			root.fetch("formRow",JoinType.INNER);
			criteriaQuery.select(root);
			if (rowId != null)
				criteriaQuery.where(criteriaBuilder.equal(root.get("formRow").get("id"), rowId));
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("columnOrder")));
			TypedQuery<FormColumn> typedQuery = session.createQuery(criteriaQuery);
			formColumns = typedQuery.getResultList();
			return formColumns;

		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return formColumns;
		}
	}

	@Override
	public Integer getMaxId(Integer rowId) {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxId = null;
		try {
			session = sessionFactory.getCurrentSession();
			
			maxId= (Integer) session.createNativeQuery(
						"SELECT max(fc.id) AS max_id  FROM form_column fc  WHERE fc.form_row=?".toLowerCase())
						.setParameter(1, rowId).addScalar("max_id").uniqueResult();
			if(maxId==null)
				return 0;
			else
				return maxId;

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxId;
		}
	}

}
