package com.smat.ins.model.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.generic.model.dao.impl.GenericDaoImpl;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.ArchiveDocumentFile;
import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.entity.CabinetFolderDocument;
import com.smat.ins.model.dao.ArchiveDocumentFileDao;

public class ArchiveDocumentFileDaoImpl extends GenericDaoImpl<ArchiveDocumentFile, Long>
		implements ArchiveDocumentFileDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7451015580484749904L;

	@Override
	public Long getMaxArchiveDocumentFileCode(ArchiveDocument archiveDocument) {
		// TODO Auto-generated method stub
		Session session = null;
		Long maxArchiveDocFile = null;

		try {
			session = sessionFactory.getCurrentSession();
			if (archiveDocument == null) {
				return Long.parseLong((String) session.createNativeQuery(
						"SELECT\r\n"
						+ "  COALESCE(MAX(SUBSTR(code, -9)), 0) AS Max_Doc_File_Code\r\n"
						+ "FROM archive_document_file adf\r\n"
						+ "WHERE adf.archive_document IS NULL".toLowerCase())
						.addScalar("Max_Doc_File_Code").uniqueResult());

			} else {
				return Long.parseLong((String) session.createNativeQuery(
						"SELECT\r\n"
						+ "  COALESCE(MAX(SUBSTR(code, -9)), 0) AS Max_Doc_File_Code\r\n"
						+ "FROM archive_document_file adf\r\n"
						+ "WHERE adf.archive_document =?".toLowerCase())
						.setParameter(1, archiveDocument.getId()).addScalar("Max_Doc_File_Code").uniqueResult());
			}

		} catch (HibernateException e) {
			e.printStackTrace();
			return maxArchiveDocFile;
		}
	}

	@Override
	public List<ArchiveDocumentFile> getBy(ArchiveDocument archiveDocument) {
		// TODO Auto-generated method stub
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<ArchiveDocumentFile> criteriaQuery = criteriaBuilder.createQuery(ArchiveDocumentFile.class);
			Root<ArchiveDocumentFile> root = criteriaQuery.from(ArchiveDocumentFile.class);	
			root.join("archiveDocument", JoinType.INNER);
			
			criteriaQuery.select(root);

			criteriaQuery.where(
						criteriaBuilder.equal(root.get("archiveDocument").get("id"),archiveDocument.getId()));
			
			TypedQuery<ArchiveDocumentFile> typedQuery = session.createQuery(criteriaQuery);
			
			return typedQuery.getResultList();

		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ArchiveDocumentFile> getBy(String fileCode, String docCode, String folderCode, String drawerCode,
			String cabinetCode) {
		// TODO Auto-generated method stub
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<ArchiveDocumentFile> criteriaQuery = criteriaBuilder.createQuery(ArchiveDocumentFile.class);
			Root<ArchiveDocumentFile> root = criteriaQuery.from(ArchiveDocumentFile.class);	
			Join<ArchiveDocumentFile, ArchiveDocument> joinArchiveDocument= root.join("archiveDocument", JoinType.INNER);
			Join<ArchiveDocument, CabinetFolderDocument> joinCabinetFolderDoc = joinArchiveDocument.join("cabinetFolderDocuments",
					JoinType.INNER);
			Join<CabinetFolderDocument, CabinetFolder> joinCabinetFolder = joinCabinetFolderDoc.join("cabinetFolder",
					JoinType.INNER);
			Join<CabinetFolder, CabinetDefinition> joinCabinetDefinition = joinCabinetFolder.join("cabinetDefinition",
					JoinType.INNER);
			Join<CabinetDefinition, Cabinet> joinCabinet = joinCabinetDefinition.join("cabinet",
					JoinType.INNER);
			
			criteriaQuery.select(root);
			Predicate predicate1= criteriaBuilder.equal(root.get("code"),fileCode.trim());
			Predicate predicate2= criteriaBuilder.equal(joinArchiveDocument.get("code"),docCode.trim());
			Predicate predicate3= criteriaBuilder.equal(joinCabinetFolder.get("code"),folderCode.trim());
			Predicate predicate4= criteriaBuilder.equal(joinCabinetDefinition.get("code"),drawerCode.trim());
			Predicate predicate5= criteriaBuilder.equal(joinCabinet.get("code"),cabinetCode.trim());
			Predicate wholePredicate=criteriaBuilder.and(predicate1,predicate2,predicate3,predicate4,predicate5);
			criteriaQuery.where(wholePredicate);
			
			TypedQuery<ArchiveDocumentFile> typedQuery = session.createQuery(criteriaQuery);
			
			return typedQuery.getResultList();

		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		}
	}

}
