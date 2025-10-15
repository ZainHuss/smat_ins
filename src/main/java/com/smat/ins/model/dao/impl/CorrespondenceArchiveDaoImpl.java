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
import com.smat.ins.model.entity.CorrespondenceArchive;
import com.smat.ins.model.dao.CorrespondenceArchiveDao;

public class CorrespondenceArchiveDaoImpl extends
		GenericDaoImpl<CorrespondenceArchive, Long> implements   CorrespondenceArchiveDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5456228482618735743L;

	@Override
	public List<CorrespondenceArchive> getByCorrespondence(Long correspondenceId) {
		// TODO Auto-generated method stub
		Session session = null;
		List<CorrespondenceArchive> correspondenceArchives = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CorrespondenceArchive> criteriaQuery = criteriaBuilder
					.createQuery(CorrespondenceArchive.class);
			Root<CorrespondenceArchive> root = criteriaQuery.from(CorrespondenceArchive.class);

			Join<CorrespondenceArchive, Correspondence> joinCorrespondence = root.join("correspondence",
					JoinType.INNER);
			root.join("archiveDocument", JoinType.INNER);


			root.fetch("correspondence");
			root.fetch("archiveDocument");
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(joinCorrespondence.get("id"), correspondenceId));

			TypedQuery<CorrespondenceArchive> typedQuery = session.createQuery(criteriaQuery);
			correspondenceArchives = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondenceArchives;

	}

}
