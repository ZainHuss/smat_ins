package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.entity.CabinetFolderDocument;
import com.smat.ins.model.dao.CabinetFolderDocumentDao;

public class CabinetFolderDocumentDaoImpl extends
		GenericDaoImpl<CabinetFolderDocument, Long> implements   CabinetFolderDocumentDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2792835664547002986L;

	@Override
	public List<CabinetFolderDocument> getByCabinetFolder(CabinetFolder cabinetFolder) {
		// TODO Auto-generated method stub
		Session session = null;
		List<CabinetFolderDocument> cabinetFolderDocuments = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CabinetFolderDocument> criteriaQuery = criteriaBuilder.createQuery(CabinetFolderDocument.class);
			Root<CabinetFolderDocument> root = criteriaQuery.from(CabinetFolderDocument.class);
			root.join("cabinetFolder", JoinType.LEFT);
			root.join("archiveDocument", JoinType.LEFT);
			criteriaQuery.select(root).distinct(true);

			criteriaQuery.where(criteriaBuilder.equal(root.get("cabinetFolder").get("id"), cabinetFolder.getId()));

			TypedQuery<CabinetFolderDocument> typedQuery = session.createQuery(criteriaQuery);
			cabinetFolderDocuments = typedQuery.getResultList();
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return cabinetFolderDocuments;
	}

	@Override
	public Boolean deleteByDocumentId(Long docId) {
		// TODO Auto-generated method stub
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		    CriteriaDelete<CabinetFolderDocument> deleteQuery = criteriaBuilder.createCriteriaDelete(CabinetFolderDocument.class);
		    Root<CabinetFolderDocument> root = deleteQuery.from(CabinetFolderDocument.class); 
		    deleteQuery.where(criteriaBuilder.equal(root.get("archiveDocument").get("id"), docId));
		    session.createQuery(deleteQuery).executeUpdate();
			
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public CabinetFolder getBy(ArchiveDocument archiveDocument) {
		// TODO Auto-generated method stub
		Session session = null;
		CabinetFolderDocument cabinetFolderDocument = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<CabinetFolderDocument> criteriaQuery = criteriaBuilder.createQuery(CabinetFolderDocument.class);
			Root<CabinetFolderDocument> root = criteriaQuery.from(CabinetFolderDocument.class);
			root.join("cabinetFolder", JoinType.LEFT);
			root.join("archiveDocument", JoinType.LEFT);
			root.fetch("cabinetFolder");
			criteriaQuery.select(root);

			criteriaQuery.where(criteriaBuilder.equal(root.get("archiveDocument").get("id"), archiveDocument.getId()));

			TypedQuery<CabinetFolderDocument> typedQuery = session.createQuery(criteriaQuery);
			cabinetFolderDocument = typedQuery.getSingleResult();
			if(cabinetFolderDocument !=null) 
				return cabinetFolderDocument.getCabinetFolder();
			else return null;
		} catch (Exception e) {
			log.error(persistentClass + " can't be persisted in DB because of the following Exception ");
			e.printStackTrace();
		}
		return null;
	}

}
