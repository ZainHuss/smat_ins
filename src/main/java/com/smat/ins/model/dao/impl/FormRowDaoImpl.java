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
import com.smat.ins.model.dao.FormRowDao;

import com.smat.ins.model.entity.FormRow;


public class FormRowDaoImpl extends GenericDaoImpl<FormRow, Integer> implements FormRowDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2375841125530426154L;

	@Override
	public List<FormRow> getBy(Integer formId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<FormRow> formRows = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<FormRow> criteriaQuery = criteriaBuilder.createQuery(FormRow.class);
			Root<FormRow> root = criteriaQuery.from(FormRow.class);
			root.fetch("formTemplate",JoinType.INNER);
			criteriaQuery.select(root);
			if (formId != null)
				criteriaQuery.where(criteriaBuilder.equal(root.get("formTemplate").get("id"), formId));
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("rowNum")));
			TypedQuery<FormRow> typedQuery = session.createQuery(criteriaQuery);
			formRows = typedQuery.getResultList();
			return formRows;

		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return formRows;
		}
	}

	@Override
	public Integer getMaxId(Integer formId) {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxId = null;

		try {
			session = sessionFactory.getCurrentSession();
			
				maxId= (Integer) session.createNativeQuery(
						"SELECT max(fr.id) AS max_id  FROM form_row fr WHERE fr.form_template=?".toLowerCase())
						.setParameter(1, formId).addScalar("max_id").uniqueResult();
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
