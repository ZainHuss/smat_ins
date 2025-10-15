package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.dao.ColumnContentDao;
import com.smat.ins.model.entity.ColumnContent;
import com.smat.ins.model.entity.FormColumn;

public class ColumnContentDaoImpl extends GenericDaoImpl<ColumnContent, Integer> implements ColumnContentDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2220801927274115197L;

	@Override
	public List<ColumnContent> getBy(Integer columnId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<ColumnContent> columnContents = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<ColumnContent> criteriaQuery = criteriaBuilder.createQuery(ColumnContent.class);
			Root<ColumnContent> root = criteriaQuery.from(ColumnContent.class);
			root.fetch("formColumn", JoinType.INNER);
			criteriaQuery.select(root);
			if (columnId != null)
				criteriaQuery.where(criteriaBuilder.equal(root.get("formColumn").get("id"), columnId));
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("contentOrder")));
			TypedQuery<ColumnContent> typedQuery = session.createQuery(criteriaQuery);
			columnContents = typedQuery.getResultList();
			return columnContents;

		} catch (Exception e) {
			log.error(persistentClass + " can't be fetched from DB because of the following Exception ");
			e.printStackTrace();
			return columnContents;
		}
	}

	@Override
	public Integer getMaxAliasNameCodeByCat(String templatePrefix) {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxFormCode = null;
		try {
			session = sessionFactory.getCurrentSession();
			return Integer.parseInt((String) session.createNativeQuery(
					"SELECT COALESCE(MAX(SUBSTR(SUBSTRING_INDEX(alias_name, '.', -1),-3)),0) as Max_Template_Code from column_content where SUBSTRING_INDEX(alias_name, '.', 1) = ?".toLowerCase())
					.setParameter(1, templatePrefix).addScalar("Max_Template_Code").uniqueResult());

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxFormCode;
		}
	}

	@Override
	public Integer getMaxId(Integer columnId) {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxId = null;

		try {
			session = sessionFactory.getCurrentSession();
			
				maxId= (Integer) session.createNativeQuery(
						"SELECT max(cc.id) AS max_id  FROM column_content cc   WHERE cc.form_column=?".toLowerCase())
						.setParameter(1, columnId).addScalar("max_id").uniqueResult();
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
