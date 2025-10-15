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
import com.smat.ins.model.entity.HierarchySystem;
import com.smat.ins.model.entity.HierarchySystemDef;
import com.smat.ins.model.dao.HierarchySystemDefDao;

public class HierarchySystemDefDaoImpl extends GenericDaoImpl<HierarchySystemDef, Integer> implements HierarchySystemDefDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5298102337258367139L;

	@Override
	public List<HierarchySystemDef> getByHierarchySystem(HierarchySystem hierarchySystem) {
		// TODO Auto-generated method stub
		Session session = null;
		List<HierarchySystemDef> hierarchySystemDefs = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<HierarchySystemDef> criteriaQuery = criteriaBuilder.createQuery(HierarchySystemDef.class);
			Root<HierarchySystemDef> root = criteriaQuery.from(HierarchySystemDef.class);

			Join<HierarchySystemDef, HierarchySystem> joinHierarchySystemDef = root.join("hierarchySystem", JoinType.LEFT);

			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(root.get("hierarchySystem").get("id"), hierarchySystem.getId()));

			TypedQuery<HierarchySystemDef> typedQuery = session.createQuery(criteriaQuery);
	        hierarchySystemDefs = typedQuery.getResultList();

		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return hierarchySystemDefs;
	}

}
