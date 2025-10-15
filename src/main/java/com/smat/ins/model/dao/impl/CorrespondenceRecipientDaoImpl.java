package com.smat.ins.model.dao.impl;

import java.util.Date;
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
import com.smat.ins.model.entity.CorrespondenceRecipient;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.entity.UserAlias;
import com.smat.ins.model.dao.CorrespondenceRecipientDao;

public class CorrespondenceRecipientDaoImpl extends GenericDaoImpl<CorrespondenceRecipient, Long>
		implements CorrespondenceRecipientDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1569807350434633688L;

	@Override
	public List<CorrespondenceRecipient> getInboxBySysUser(SysUser sysUserRecipient,
			CorrespondenceState correspondenceState,int offset, int pageSize) {
		// TODO Auto-generated method stub

		Session session = null;
		List<CorrespondenceRecipient> correspondenceRecipients = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CorrespondenceRecipient> criteriaQuery = criteriaBuilder
					.createQuery(CorrespondenceRecipient.class);
			Root<CorrespondenceRecipient> root = criteriaQuery.from(CorrespondenceRecipient.class);

			Join<CorrespondenceRecipient, Correspondence> joinCorrespondence = root.join("correspondence",
					JoinType.LEFT);
			Join<CorrespondenceRecipient, UserAlias> joinUserAliasRecipient = root.join("userAlias", JoinType.LEFT);

			joinUserAliasRecipient.join("sysUserBySysUser", JoinType.LEFT);

			Join<Correspondence, CorrespondenceState> joinCorrespondenceState = joinCorrespondence
					.join("correspondenceState", JoinType.LEFT);
			joinCorrespondence.join("boxType", JoinType.LEFT);
			joinCorrespondence.join("organizationByDivan", JoinType.LEFT);
			joinCorrespondence.join("sysUserByCreatorSysUser", JoinType.LEFT);
			root.join("correspondenceNote", JoinType.LEFT);
			root.fetch("correspondenceNote");
			
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(joinUserAliasRecipient.get("id"), sysUserRecipient.getId()),
					criteriaBuilder.equal(joinCorrespondenceState.get("id"), correspondenceState.getId()),
					criteriaBuilder.or(criteriaBuilder.equal(root.get("isProcessed"), false),criteriaBuilder.isNull(root.get("isProcessed")))
					);

			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("isViewed")));
			

			TypedQuery<CorrespondenceRecipient> typedQuery = session.createQuery(criteriaQuery);
			typedQuery.setFirstResult(offset);
			typedQuery.setMaxResults(pageSize);
			correspondenceRecipients = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondenceRecipients;
	}
	
	

	@Override
	public Long getInboxCountBySysUser(SysUser sysUserRecipient, CorrespondenceState correspondenceState) {
		// TODO Auto-generated method stub
		Session session = null;
		Long correspondenceRecipientsCount = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Long> criteriaQuery = criteriaBuilder
					.createQuery(Long.class);
			Root<CorrespondenceRecipient> root = criteriaQuery.from(CorrespondenceRecipient.class);

			Join<CorrespondenceRecipient, Correspondence> joinCorrespondence = root.join("correspondence",
					JoinType.LEFT);
			Join<CorrespondenceRecipient, UserAlias> joinUserAliasRecipient = root.join("userAlias", JoinType.LEFT);

			joinUserAliasRecipient.join("sysUserBySysUser", JoinType.LEFT);

			Join<Correspondence, CorrespondenceState> joinCorrespondenceState = joinCorrespondence
					.join("correspondenceState", JoinType.LEFT);
			joinCorrespondence.join("boxType", JoinType.LEFT);
			joinCorrespondence.join("organizationByDivan", JoinType.LEFT);
			joinCorrespondence.join("sysUserByCreatorSysUser", JoinType.LEFT);
			root.join("correspondenceNote", JoinType.LEFT);

			criteriaQuery.select(criteriaBuilder.count(root.get("id")));

			criteriaQuery.where(criteriaBuilder.equal(joinUserAliasRecipient.get("id"), sysUserRecipient.getId()),
					criteriaBuilder.equal(joinCorrespondenceState.get("id"), correspondenceState.getId()),
					criteriaBuilder.or(criteriaBuilder.equal(root.get("isProcessed"), false),criteriaBuilder.isNull(root.get("isProcessed")))
					);

			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("isViewed")));
			

			TypedQuery<Long> typedQuery = session.createQuery(criteriaQuery);
		
			correspondenceRecipientsCount = typedQuery.getSingleResult();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondenceRecipientsCount;
	}



	@Override
	public List<CorrespondenceRecipient> searchInMail(SysUser sysUserLogin, SysUser sysUserSender,
			Date correspondenceDate, String title) {
		// TODO Auto-generated method stub
		Session session = null;
		List<CorrespondenceRecipient> correspondenceRecipients = null;
		try {

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondenceRecipients;
	}

	@Override
	public List<CorrespondenceRecipient> getByCorrespondence(Correspondence correspondence) {
		// TODO Auto-generated method stub
		Session session = null;
		List<CorrespondenceRecipient> correspondenceRecipients = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CorrespondenceRecipient> criteriaQuery = criteriaBuilder
					.createQuery(CorrespondenceRecipient.class);
			Root<CorrespondenceRecipient> root = criteriaQuery.from(CorrespondenceRecipient.class);

			Join<CorrespondenceRecipient, Correspondence> joinCorrespondence = root.join("correspondence",
					JoinType.LEFT);
			root.join("userAlias", JoinType.LEFT);

			root.join("purposeType", JoinType.LEFT);
			root.join("priorityType", JoinType.LEFT);
			root.join("correspondenceNote", JoinType.LEFT);

			root.fetch("userAlias");
			root.fetch("correspondenceNote");
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(joinCorrespondence.get("id"), correspondence.getId())

			);

			TypedQuery<CorrespondenceRecipient> typedQuery = session.createQuery(criteriaQuery);
			correspondenceRecipients = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondenceRecipients;
	}



	@Override
	public List<CorrespondenceRecipient> getAllInboxBySysUser(SysUser sysUserRecipient,
			CorrespondenceState correspondenceState, int offset, int pageSize) {
		// TODO Auto-generated method stub
		Session session = null;
		List<CorrespondenceRecipient> correspondenceRecipients = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CorrespondenceRecipient> criteriaQuery = criteriaBuilder
					.createQuery(CorrespondenceRecipient.class);
			Root<CorrespondenceRecipient> root = criteriaQuery.from(CorrespondenceRecipient.class);

			Join<CorrespondenceRecipient, Correspondence> joinCorrespondence = root.join("correspondence",
					JoinType.LEFT);
			Join<CorrespondenceRecipient, UserAlias> joinUserAliasRecipient = root.join("userAlias", JoinType.LEFT);

			joinUserAliasRecipient.join("sysUserBySysUser", JoinType.LEFT);

			Join<Correspondence, CorrespondenceState> joinCorrespondenceState = joinCorrespondence
					.join("correspondenceState", JoinType.LEFT);
			joinCorrespondence.join("boxType", JoinType.LEFT);
			joinCorrespondence.join("organizationByDivan", JoinType.LEFT);
			joinCorrespondence.join("sysUserByCreatorSysUser", JoinType.LEFT);
			root.join("correspondenceNote", JoinType.LEFT);
			root.fetch("correspondenceNote");
			
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(joinUserAliasRecipient.get("id"), sysUserRecipient.getId()),
					criteriaBuilder.equal(joinCorrespondenceState.get("id"), correspondenceState.getId())
					);

			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("isViewed")));
			

			TypedQuery<CorrespondenceRecipient> typedQuery = session.createQuery(criteriaQuery);
			typedQuery.setFirstResult(offset);
			typedQuery.setMaxResults(pageSize);
			correspondenceRecipients = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondenceRecipients;
	}



	@Override
	public Long getAllInboxCountBySysUser(SysUser sysUserRecipient, CorrespondenceState correspondenceState) {
		// TODO Auto-generated method stub
		Session session = null;
		Long correspondenceRecipientsCount = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Long> criteriaQuery = criteriaBuilder
					.createQuery(Long.class);
			Root<CorrespondenceRecipient> root = criteriaQuery.from(CorrespondenceRecipient.class);

			Join<CorrespondenceRecipient, Correspondence> joinCorrespondence = root.join("correspondence",
					JoinType.LEFT);
			Join<CorrespondenceRecipient, UserAlias> joinUserAliasRecipient = root.join("userAlias", JoinType.LEFT);

			joinUserAliasRecipient.join("sysUserBySysUser", JoinType.LEFT);

			Join<Correspondence, CorrespondenceState> joinCorrespondenceState = joinCorrespondence
					.join("correspondenceState", JoinType.LEFT);
			joinCorrespondence.join("boxType", JoinType.LEFT);
			joinCorrespondence.join("organizationByDivan", JoinType.LEFT);
			joinCorrespondence.join("sysUserByCreatorSysUser", JoinType.LEFT);
			root.join("correspondenceNote", JoinType.LEFT);

			criteriaQuery.select(criteriaBuilder.count(root.get("id")));

			criteriaQuery.where(criteriaBuilder.equal(joinUserAliasRecipient.get("id"), sysUserRecipient.getId()),
					criteriaBuilder.equal(joinCorrespondenceState.get("id"), correspondenceState.getId())
					);

			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("isViewed")));
			

			TypedQuery<Long> typedQuery = session.createQuery(criteriaQuery);
		
			correspondenceRecipientsCount = typedQuery.getSingleResult();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondenceRecipientsCount;
	}
	
	

}
