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
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.dao.CabinetFolderDao;

public class CabinetFolderDaoImpl extends
		GenericDaoImpl<CabinetFolder, Long> implements   CabinetFolderDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2521227475459425207L;

	@Override
	public List<CabinetFolder> getByCabinetDefinition(CabinetDefinition cabinetDefinition) {
		// TODO Auto-generated method stub
		Session session = null;
		List<CabinetFolder> cabinetFolders = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CabinetFolder> criteriaQuery = criteriaBuilder.createQuery(CabinetFolder.class);
			Root<CabinetFolder> root = criteriaQuery.from(CabinetFolder.class);
			root.join("cabinetDefinition", JoinType.LEFT);
			
			criteriaQuery.select(root).distinct(true);

			criteriaQuery.where(criteriaBuilder.equal(root.get("cabinetDefinition").get("id"), cabinetDefinition.getId()));

			TypedQuery<CabinetFolder> typedQuery = session.createQuery(criteriaQuery);
			cabinetFolders = typedQuery.getResultList();
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return cabinetFolders;
	}

	@Override
	public Integer getMaxCabinetCode(CabinetDefinition cabinetDefinition) {
		// TODO Auto-generated method stub
		Session session = null;
		Integer maxCabinetFolderCode = null;

		try {
			session = sessionFactory.getCurrentSession();
			if (cabinetDefinition == null) {
				return Integer.parseInt((String) session.createNativeQuery(
						"SELECT COALESCE(MAX(SUBSTR(code,-3)),0) as Max_Folder_Code from CABINET_FOLDER CAB_Fold where CAB_Fold.CABINET_DEFINITION is NULL".toLowerCase())
						.addScalar("Max_Folder_Code").uniqueResult());

			} else {
				return Integer.parseInt( (String) session.createNativeQuery(
						"SELECT COALESCE(MAX(SUBSTR(code,-3)),0) as Max_Folder_Code from CABINET_FOLDER CAB_Fold where CAB_Fold.CABINET_DEFINITION = ?".toLowerCase())
						.setParameter(1, cabinetDefinition.getId()).addScalar("Max_Folder_Code").uniqueResult());
			}

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxCabinetFolderCode;
		}
	}

	@Override
	public CabinetFolder getBy(Long cabinetDefinitionId, String cabinetFolderCode) {
		// TODO Auto-generated method stub
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CabinetFolder> criteriaQuery = criteriaBuilder.createQuery(CabinetFolder.class);
			Root<CabinetFolder> root = criteriaQuery.from(CabinetFolder.class);
			root.join("cabinetDefinition", JoinType.LEFT);
			
			criteriaQuery.select(root).distinct(true);

			criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.equal(root.get("cabinetDefinition").get("id"), cabinetDefinitionId),criteriaBuilder.equal(root.get("code"), cabinetFolderCode)));

			TypedQuery<CabinetFolder> typedQuery = session.createQuery(criteriaQuery);
			return typedQuery.getSingleResult();
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
			return null;
		}
	}

}
