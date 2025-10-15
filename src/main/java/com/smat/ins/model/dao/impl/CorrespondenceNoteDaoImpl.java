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
import com.smat.ins.model.entity.CorrespondenceNote;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.dao.CorrespondenceNoteDao;

public class CorrespondenceNoteDaoImpl extends
		GenericDaoImpl<CorrespondenceNote, Long> implements   CorrespondenceNoteDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 503793760710618721L;

	@Override
	public List<CorrespondenceNote> getCorrespondenceNotes(Correspondence correspondence) {
		// TODO Auto-generated method stub
		Session session = null;
		List<CorrespondenceNote> correspondenceNotes = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CorrespondenceNote> criteriaQuery = criteriaBuilder
					.createQuery(CorrespondenceNote.class);
			Root<CorrespondenceNote> root = criteriaQuery.from(CorrespondenceNote.class);

			Join<CorrespondenceNote, Correspondence> joinCorrespondence = root.join("correspondence",
					JoinType.LEFT);
			
			joinCorrespondence.join("boxType", JoinType.LEFT);
			joinCorrespondence.join("organizationByDivan", JoinType.LEFT);

			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(joinCorrespondence.get("id"), correspondence.getId()));

			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("noteDate")));
			

			TypedQuery<CorrespondenceNote> typedQuery = session.createQuery(criteriaQuery);
		
			correspondenceNotes = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondenceNotes;
	}

	@Override
	public List<CorrespondenceNote> getOutBoxBySysUser(CorrespondenceState correspondenceState, SysUser sysUserSender,int offset, int pageSize) {
		// TODO Auto-generated method stub
		Session session = null;
		List<CorrespondenceNote> correspondenceNotes = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CorrespondenceNote> criteriaQuery = criteriaBuilder
					.createQuery(CorrespondenceNote.class);
			Root<CorrespondenceNote> root = criteriaQuery.from(CorrespondenceNote.class);

			Join<CorrespondenceNote, Correspondence> joinCorrespondence = root.join("correspondence",
					JoinType.LEFT);
			Join<CorrespondenceNote, UserAlias> joinUserAliasSender = root.join("userAlias", JoinType.LEFT);

			joinUserAliasSender.join("sysUserBySysUser", JoinType.LEFT);

			Join<Correspondence, CorrespondenceState> joinCorrespondenceState = joinCorrespondence
					.join("correspondenceState", JoinType.LEFT);
			joinCorrespondence.join("boxType", JoinType.LEFT);
			joinCorrespondence.join("organizationByDivan", JoinType.LEFT);
			joinCorrespondence.join("sysUserByCreatorSysUser", JoinType.LEFT);
			root.join("correspondenceRecipients", JoinType.INNER);
			
			
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(joinUserAliasSender.get("id"), sysUserSender.getId()),
					criteriaBuilder.equal(joinCorrespondenceState.get("id"), correspondenceState.getId())
					);

			criteriaQuery.orderBy(criteriaBuilder.desc(joinCorrespondence.get("createdDate")));
			

			TypedQuery<CorrespondenceNote> typedQuery = session.createQuery(criteriaQuery);
			typedQuery.setFirstResult(offset);
			typedQuery.setMaxResults(pageSize);
			correspondenceNotes = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondenceNotes;
	}

	@Override
	public Long getOutBoxCountBySysUser(CorrespondenceState correspondenceState, SysUser sysUserSender) {
		// TODO Auto-generated method stub
		Session session = null;
		Long correspondenceNotesCount = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Long> criteriaQuery = criteriaBuilder
					.createQuery(Long.class);
			Root<CorrespondenceNote> root = criteriaQuery.from(CorrespondenceNote.class);

			Join<CorrespondenceNote, Correspondence> joinCorrespondence = root.join("correspondence",
					JoinType.LEFT);
			Join<CorrespondenceNote, UserAlias> joinUserAliasSender = root.join("userAlias", JoinType.LEFT);

			joinUserAliasSender.join("sysUserBySysUser", JoinType.LEFT);

			Join<Correspondence, CorrespondenceState> joinCorrespondenceState = joinCorrespondence
					.join("correspondenceState", JoinType.LEFT);
			joinCorrespondence.join("boxType", JoinType.LEFT);
			joinCorrespondence.join("organizationByDivan", JoinType.LEFT);
			joinCorrespondence.join("sysUserByCreatorSysUser", JoinType.LEFT);
			root.join("correspondenceRecipients", JoinType.INNER);
			
			
			criteriaQuery.select(criteriaBuilder.count(root.get("id")));

			criteriaQuery.where(criteriaBuilder.equal(joinUserAliasSender.get("id"), sysUserSender.getId()),
					criteriaBuilder.equal(joinCorrespondenceState.get("id"), correspondenceState.getId())
					);

			criteriaQuery.orderBy(criteriaBuilder.desc(joinCorrespondence.get("createdDate")));
			

			TypedQuery<Long> typedQuery = session.createQuery(criteriaQuery);
			
		
			correspondenceNotesCount = typedQuery.getSingleResult();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondenceNotesCount;
	}

}
