package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.LinkedCorrespondence;
import com.smat.ins.model.dao.LinkedCorrespondenceDao;

public class LinkedCorrespondenceDaoImpl extends
		GenericDaoImpl<LinkedCorrespondence, Long> implements   LinkedCorrespondenceDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2310835011225394408L;

	@Override
	public List<LinkedCorrespondence> getByCorrespondence(Long correspondenceId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<LinkedCorrespondence> linkedCorrespondences = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<LinkedCorrespondence> criteriaQuery = criteriaBuilder
					.createQuery(LinkedCorrespondence.class);
			Root<LinkedCorrespondence> root = criteriaQuery.from(LinkedCorrespondence.class);

			Join<LinkedCorrespondence, Correspondence> joinCorrespondence = root.join("correspondenceByMasterCorrespondence",
					JoinType.INNER);
			root.join("correspondenceByLinkedCorrespondence", JoinType.INNER);


			root.fetch("correspondenceByMasterCorrespondence");
			root.fetch("correspondenceByLinkedCorrespondence");
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(joinCorrespondence.get("id"), correspondenceId));

			TypedQuery<LinkedCorrespondence> typedQuery = session.createQuery(criteriaQuery);
			linkedCorrespondences = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return linkedCorrespondences;
	}

}
