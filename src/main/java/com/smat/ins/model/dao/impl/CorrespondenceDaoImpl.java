package com.smat.ins.model.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.BoxType;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.dao.CorrespondenceDao;

public class CorrespondenceDaoImpl extends GenericDaoImpl<Correspondence, Long> implements CorrespondenceDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = -908064815854943960L;

	@Override
	public List<Correspondence> getDraftBox(CorrespondenceState correspondenceState, BoxType boxType,
			SysUser sysUserLogin) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Correspondence> correspondences = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Correspondence> criteriaQuery = criteriaBuilder.createQuery(Correspondence.class);
			Root<Correspondence> root = criteriaQuery.from(Correspondence.class);

			Join<Correspondence, BoxType> joinBoxType = root.join("boxType", JoinType.LEFT);
			
			Join<Correspondence, CorrespondenceState> joinCorrespondenceState = root.join("correspondenceState",
					JoinType.LEFT);

			

			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(root.get("sysUserByCreatorSysUser").get("id"), sysUserLogin.getId()),
					criteriaBuilder.equal(joinCorrespondenceState.get("id"), correspondenceState.getId()),
					criteriaBuilder.equal(joinBoxType.get("id"), boxType.getId()),
					criteriaBuilder.isNull(root.get("sysUserByDeleteBySysUser")));

			TypedQuery<Correspondence> typedQuery = session.createQuery(criteriaQuery);
			correspondences = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondences;
	}

	@Override
	public Long getLastSeq(String boxTypeCode, Long orgID) {
		// TODO Auto-generated method stub

		Session session = null;
		Long sequence = Long.valueOf(0);
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
			Root<Correspondence> root = criteriaQuery.from(Correspondence.class);

			Join<Correspondence, BoxType> joinBoxType = root.join("boxType", JoinType.LEFT);
			Join<Correspondence, Organization> joinOrganizationByDivan = root.join("organizationByDivan",
					JoinType.LEFT);
			criteriaQuery.where(criteriaBuilder.equal(joinBoxType.get("enCode"), boxTypeCode),
					criteriaBuilder.equal(joinOrganizationByDivan.get("id"), orgID));

			criteriaQuery.select(criteriaBuilder.max(root.get("seq")));

			TypedQuery<Long> typedQuery = session.createQuery(criteriaQuery);
			sequence = typedQuery.getSingleResult();
			if (sequence == null)
				return Long.valueOf(1);
			else
				return sequence + 1;

		}

		catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return sequence;

	}

	@Override
	public List<Correspondence> getOutBox(CorrespondenceState correspondenceState, SysUser sysUserLogin) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Correspondence> correspondences = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Correspondence> criteriaQuery = criteriaBuilder.createQuery(Correspondence.class);
			Root<Correspondence> root = criteriaQuery.from(Correspondence.class);

			Join<Correspondence, BoxType> joinBoxType = root.join("boxType", JoinType.LEFT);
			Join<Correspondence, CorrespondenceState> joinCorrespondenceState = root.join("correspondenceState",
					JoinType.LEFT);
			Join<Correspondence, SysUser> joinsysUser = root.join("sysUserByCreatorSysUser", JoinType.LEFT);
			Join<Correspondence, Organization> joinOrganizationByDivan = root.join("organizationByDivan", JoinType.LEFT);
			criteriaQuery.select(root);
			criteriaQuery.where(criteriaBuilder.equal(root.get("sysUserByCreatorSysUser").get("id"), sysUserLogin.getId()),
					criteriaBuilder.equal(joinCorrespondenceState.get("id"), correspondenceState.getId()));
		
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdDate")));

			
			TypedQuery<Correspondence> typedQuery = session.createQuery(criteriaQuery);
			correspondences = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondences;
	}

	@Override
	public List<Correspondence> search(BoxType boxType, Long seq, Date correspondenceDate, String fromNumber,
			String fromEntity, Date fromDate, String title) {
		// TODO Auto-generated method stub
		Session session = null;
		List<Correspondence> correspondences = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<Correspondence> criteriaQuery = criteriaBuilder.createQuery(Correspondence.class);
			Root<Correspondence> root = criteriaQuery.from(Correspondence.class);

			Join<Correspondence, BoxType> joinBoxType = root.join("boxType", JoinType.LEFT);
			criteriaQuery.select(root);
			List<Predicate> predicates = new ArrayList<Predicate>();
			if (boxType != null) {
				predicates.add(criteriaBuilder.equal(joinBoxType.get("id"), boxType.getId()));
			}

			if (seq != null) {
				predicates.add(criteriaBuilder.equal(root.get("seq"), seq));
			}

			if (correspondenceDate != null) {
				predicates.add(criteriaBuilder.equal(root.get("correspondenceDate"), correspondenceDate));
			}

			if (fromNumber != null && fromNumber.isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("fromNumber"), fromNumber));
			}

			if (fromEntity != null && fromEntity.isEmpty()) {
				predicates.add(criteriaBuilder.like(root.get("fromEntity"), "%" + fromEntity + "%"));
			}

			if (fromDate != null) {
				predicates.add(criteriaBuilder.equal(root.get("fromDate"), fromDate));
			}

			if (title != null && title.isEmpty()) {
				predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
			}
			
			
			criteriaQuery.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
			TypedQuery<Correspondence> typedQuery = session.createQuery(criteriaQuery);
			correspondences = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return correspondences;
	}

	

}
