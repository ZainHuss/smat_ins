package com.smat.ins.model.dao.impl;

import java.util.ArrayList;
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
import com.smat.ins.model.entity.ArchiveDocumentType;
import com.smat.ins.model.entity.CabinetFolderDocument;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.dao.ArchiveDocumentDao;

public class ArchiveDocumentDaoImpl extends GenericDaoImpl<ArchiveDocument, Long> implements ArchiveDocumentDao {

    private static final long serialVersionUID = -1535950420053758478L;

    @Override
    public Long getMaxArchiveDocumentCode(ArchiveDocument parentArchiveDocument, Long cabinetFolderId) {
        Session session = null;
        Long maxArchiveDocumentCode = null;

        try {
            session = sessionFactory.getCurrentSession();
            if (parentArchiveDocument == null) {
                return Long.parseLong((String) session.createNativeQuery(
                        "SELECT COALESCE(MAX(SUBSTR(code, -9)), 0) AS max_archive_document_code FROM archive_document arch_doc "
                                + "INNER JOIN cabinet_folder_document cfd ON arch_doc.id = cfd.archive_document "
                                + "WHERE arch_doc.parent_archive_document IS NULL AND cfd.cabinet_folder = ?")
                        .setParameter(1, cabinetFolderId).addScalar("max_archive_document_code").uniqueResult());

            } else {
                return Long.parseLong((String) session.createNativeQuery(
                        "SELECT COALESCE(MAX(SUBSTR(code, -9)), 0) AS max_archive_document_code FROM archive_document arch_doc "
                                + "INNER JOIN cabinet_folder_document cfd ON arch_doc.id = cfd.archive_document "
                                + "WHERE arch_doc.parent_archive_document = ? AND cfd.cabinet_folder = ?")
                        .setParameter(1, parentArchiveDocument.getId()).setParameter(2, cabinetFolderId)
                        .addScalar("max_archive_document_code").uniqueResult());
            }

        } catch (HibernateException e) {
            e.printStackTrace();
            return maxArchiveDocumentCode;
        }
    }

    @Override
    public List<ArchiveDocument> getByParent(ArchiveDocument archiveDocument, Long cabinetFolderId, Integer start,
            Integer pageSize) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<ArchiveDocument> criteriaQuery = criteriaBuilder.createQuery(ArchiveDocument.class);
            Root<ArchiveDocument> root = criteriaQuery.from(ArchiveDocument.class);
            Join<ArchiveDocument, ArchiveDocument> parentArchiveDocument = root.join("archiveDocument", JoinType.LEFT);
            Join<ArchiveDocument, CabinetFolderDocument> joinCabinetFolderDoc = root.join("cabinetFolderDocuments",
                    JoinType.INNER);
            root.join("archiveDocumentType", JoinType.LEFT);
            root.join("archiveExtraProperty", JoinType.LEFT);
            root.join("sysUserByDeletedBySysUser", JoinType.LEFT);
            root.join("organizationByRootOrganization", JoinType.LEFT);
            root.join("organizationByDivan", JoinType.LEFT);
            root.join("sysUserByLockedSysUser", JoinType.LEFT);
            root.join("organizationByOrganization", JoinType.LEFT);
            root.join("userAlias", JoinType.LEFT);
            root.join("sysUserByCreatorUser", JoinType.LEFT);
            criteriaQuery.select(root);

            if (archiveDocument == null)
                criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.isNull(parentArchiveDocument),
                        criteriaBuilder.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId)));
            else
                criteriaQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(parentArchiveDocument.get("id"), archiveDocument.getId()),
                        criteriaBuilder.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId)));
            TypedQuery<ArchiveDocument> typedQuery = session.createQuery(criteriaQuery);
            if (start != null && pageSize != null) {
                typedQuery.setFirstResult(start);
                typedQuery.setMaxResults(pageSize);
            }

            return typedQuery.getResultList();

        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Long getByParentCount(ArchiveDocument archiveDocument, Long cabinetFolderId) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<ArchiveDocument> root = criteriaQuery.from(ArchiveDocument.class);
            Join<ArchiveDocument, ArchiveDocument> parentArchiveDocument = root.join("archiveDocument", JoinType.LEFT);
            Join<ArchiveDocument, CabinetFolderDocument> joinCabinetFolderDoc = root.join("cabinetFolderDocuments",
                    JoinType.INNER);
            root.join("archiveDocumentType", JoinType.LEFT);
            root.join("archiveExtraProperty", JoinType.LEFT);
            root.join("sysUserByDeletedBySysUser", JoinType.LEFT);
            root.join("organizationByRootOrganization", JoinType.LEFT);
            root.join("organizationByDivan", JoinType.LEFT);
            root.join("sysUserByLockedSysUser", JoinType.LEFT);
            root.join("organizationByOrganization", JoinType.LEFT);
            root.join("userAlias", JoinType.LEFT);
            root.join("sysUserByCreatorUser", JoinType.LEFT);

            if (archiveDocument == null)
                criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.isNull(parentArchiveDocument),
                        criteriaBuilder.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId)));
            else
                criteriaQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(parentArchiveDocument.get("id"), archiveDocument.getId()),
                        criteriaBuilder.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId)));
            TypedQuery<Long> typedQuery = session.createQuery(criteriaQuery);
            return typedQuery.getSingleResult();

        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ArchiveDocument> getByNameOrCode(Long cabinetFolderId, String searchKey) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<ArchiveDocument> criteriaQuery = criteriaBuilder.createQuery(ArchiveDocument.class);
            Root<ArchiveDocument> root = criteriaQuery.from(ArchiveDocument.class);
            Join<ArchiveDocument, CabinetFolderDocument> joinCabinetFolderDoc = root.join("cabinetFolderDocuments",
                    JoinType.INNER);
            root.join("archiveDocumentType", JoinType.LEFT);
            root.join("archiveExtraProperty", JoinType.LEFT);
            root.join("sysUserByDeletedBySysUser", JoinType.LEFT);
            root.join("organizationByRootOrganization", JoinType.LEFT);
            root.join("organizationByDivan", JoinType.LEFT);
            root.join("sysUserByLockedSysUser", JoinType.LEFT);
            root.join("organizationByOrganization", JoinType.LEFT);
            root.join("userAlias", JoinType.LEFT);
            root.join("sysUserByCreatorUser", JoinType.LEFT);
            criteriaQuery.select(root);

            if (searchKey != null && !searchKey.trim().isEmpty())
                criteriaQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId),
                        criteriaBuilder.like(root.get("code"), "%" + searchKey + "%")));
            TypedQuery<ArchiveDocument> typedQuery = session.createQuery(criteriaQuery);

            return typedQuery.getResultList();

        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ArchiveDocument> getByNameOrCode(Long cabinetFolderId, String arabicName, String code) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<ArchiveDocument> criteriaQuery = criteriaBuilder.createQuery(ArchiveDocument.class);
            Root<ArchiveDocument> root = criteriaQuery.from(ArchiveDocument.class);
            Join<ArchiveDocument, CabinetFolderDocument> joinCabinetFolderDoc = root.join("cabinetFolderDocuments",
                    JoinType.INNER);
            root.join("archiveDocumentType", JoinType.LEFT);
            root.join("archiveExtraProperty", JoinType.LEFT);
            root.join("sysUserByDeletedBySysUser", JoinType.LEFT);
            root.join("organizationByRootOrganization", JoinType.LEFT);
            root.join("organizationByDivan", JoinType.LEFT);
            root.join("sysUserByLockedSysUser", JoinType.LEFT);
            root.join("organizationByOrganization", JoinType.LEFT);
            root.join("userAlias", JoinType.LEFT);
            root.join("sysUserByCreatorUser", JoinType.LEFT);
            criteriaQuery.select(root);

            if (arabicName != null && !arabicName.trim().isEmpty() && code != null && !code.trim().isEmpty())
                criteriaQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId),
                        criteriaBuilder.like(root.get("arabicName"), "%" + arabicName + "%"),
                        criteriaBuilder.like(root.get("code"), "%" + code + "%")));

            TypedQuery<ArchiveDocument> typedQuery = session.createQuery(criteriaQuery);

            return typedQuery.getResultList();

        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArchiveDocument getArchiveDocumentWithDetails(ArchiveDocument archiveDocument, Long cabinetFolderId) {
        Session session = null;
        ArchiveDocument parentArchiveDocument = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<ArchiveDocument> criteriaQuery = criteriaBuilder.createQuery(ArchiveDocument.class);
            Root<ArchiveDocument> root = criteriaQuery.from(ArchiveDocument.class);
            Join<ArchiveDocument, CabinetFolderDocument> joinCabinetFolderDoc = root.join("cabinetFolderDocuments",
                    JoinType.INNER);
            root.join("archiveDocumentType", JoinType.LEFT);
            root.join("archiveExtraProperty", JoinType.LEFT);
            root.join("sysUserByDeletedBySysUser", JoinType.LEFT);
            root.join("organizationByRootOrganization", JoinType.LEFT);
            root.join("organizationByDivan", JoinType.LEFT);
            root.join("sysUserByLockedSysUser", JoinType.LEFT);
            root.join("organizationByOrganization", JoinType.LEFT);
            root.join("userAlias", JoinType.LEFT);
            root.join("sysUserByCreatorUser", JoinType.LEFT);
            criteriaQuery.select(root);

            if (archiveDocument != null) {
                criteriaQuery.select(root).where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("id"), archiveDocument.getId()),
                        criteriaBuilder.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId)));
                TypedQuery<ArchiveDocument> typedQuery = session.createQuery(criteriaQuery);
                parentArchiveDocument = typedQuery.getSingleResult();
                return parentArchiveDocument;
            } else {
                return null;
            }

        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ArchiveDocument> getAllWithDetails(Long cabinetFolderId) {
        Session session = null;
        List<ArchiveDocument> archiveDocuments = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<ArchiveDocument> criteriaQuery = criteriaBuilder.createQuery(ArchiveDocument.class);
            Root<ArchiveDocument> root = criteriaQuery.from(ArchiveDocument.class);
            Join<ArchiveDocument, CabinetFolderDocument> joinCabinetFolderDoc = root.join("cabinetFolderDocuments",
                    JoinType.INNER);
            root.join("archiveDocumentType", JoinType.LEFT);
            root.join("archiveExtraProperty", JoinType.LEFT);
            root.join("sysUserByDeletedBySysUser", JoinType.LEFT);
            root.join("organizationByRootOrganization", JoinType.LEFT);
            root.join("organizationByDivan", JoinType.LEFT);
            root.join("sysUserByLockedSysUser", JoinType.LEFT);
            root.join("organizationByOrganization", JoinType.LEFT);
            root.join("userAlias", JoinType.LEFT);
            root.join("sysUserByCreatorUser", JoinType.LEFT);
            criteriaQuery.select(root);
            criteriaQuery
                    .where(criteriaBuilder.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId));
            TypedQuery<ArchiveDocument> typedQuery = session.createQuery(criteriaQuery);
            archiveDocuments = typedQuery.getResultList();

        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }

        return archiveDocuments;
    }

    @Override
    public ArchiveDocument getParentArchiveDocument(ArchiveDocument archiveDocument, Long cabinetFolderId) {
        Session session = null;
        ArchiveDocument parentArchiveDocument = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<ArchiveDocument> criteriaQuery = criteriaBuilder.createQuery(ArchiveDocument.class);
            Root<ArchiveDocument> root = criteriaQuery.from(ArchiveDocument.class);
            Join<ArchiveDocument, CabinetFolderDocument> joinCabinetFolderDoc = root.join("cabinetFolderDocuments",
                    JoinType.INNER);
            root.join("archiveDocumentType", JoinType.LEFT);
            root.join("archiveExtraProperty", JoinType.LEFT);
            root.join("sysUserByDeletedBySysUser", JoinType.LEFT);
            root.join("organizationByRootOrganization", JoinType.LEFT);
            root.join("organizationByDivan", JoinType.LEFT);
            root.join("sysUserByLockedSysUser", JoinType.LEFT);
            root.join("organizationByOrganization", JoinType.LEFT);
            root.join("userAlias", JoinType.LEFT);
            root.join("sysUserByCreatorUser", JoinType.LEFT);
            criteriaQuery.select(root);
            if (archiveDocument != null) {
                criteriaQuery.select(root).where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("id"), archiveDocument.getId()),
                        criteriaBuilder.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId)));
                TypedQuery<ArchiveDocument> typedQuery = session.createQuery(criteriaQuery);
                parentArchiveDocument = typedQuery.getSingleResult();
                if (parentArchiveDocument.getArchiveDocument() != null)
                    return parentArchiveDocument.getArchiveDocument();
                else
                    return parentArchiveDocument;
            } else {
                return null;
            }

        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArchiveDocument getBy(Long cabinetFolderId, String arabicName, String englishName) {
        Session session = null;
        ArchiveDocument result = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<ArchiveDocument> criteriaQuery = criteriaBuilder.createQuery(ArchiveDocument.class);
            Root<ArchiveDocument> root = criteriaQuery.from(ArchiveDocument.class);
            Join<ArchiveDocument, CabinetFolderDocument> joinCabinetFolderDoc = root.join("cabinetFolderDocuments",
                    JoinType.INNER);
            root.join("archiveDocumentType", JoinType.LEFT);
            root.join("archiveExtraProperty", JoinType.LEFT);
            root.join("sysUserByDeletedBySysUser", JoinType.LEFT);
            root.join("organizationByRootOrganization", JoinType.LEFT);
            root.join("organizationByDivan", JoinType.LEFT);
            root.join("sysUserByLockedSysUser", JoinType.LEFT);
            root.join("organizationByOrganization", JoinType.LEFT);
            root.join("userAlias", JoinType.LEFT);
            root.join("sysUserByCreatorUser", JoinType.LEFT);
            criteriaQuery.select(root);

            // تم التصحيح هنا لشرط englishName
            if (cabinetFolderId != null && arabicName != null && !arabicName.isEmpty() && englishName != null
                    && !englishName.isEmpty()) {
                criteriaQuery.select(root).where(criteriaBuilder.and(
                        criteriaBuilder.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId),
                        criteriaBuilder.equal(root.get("arabicName"), arabicName.trim()),
                        criteriaBuilder.equal(root.get("englishName"), englishName.trim())));
                TypedQuery<ArchiveDocument> typedQuery = session.createQuery(criteriaQuery);
                result = typedQuery.getSingleResult();
                return result;
            } else {
                return null;
            }

        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ArchiveDocument> getBy(Long cabinetFolderId, String arabicName, String englishName, String description,
            ArchiveDocumentType archiveDocumentType, Organization divan, String side, String subject, Short year) {
        Session session = null;
        List<ArchiveDocument> result = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<ArchiveDocument> criteriaQuery = criteriaBuilder.createQuery(ArchiveDocument.class);
            Root<ArchiveDocument> root = criteriaQuery.from(ArchiveDocument.class);
            Join<ArchiveDocument, CabinetFolderDocument> joinCabinetFolderDoc = root.join("cabinetFolderDocuments",
                    JoinType.INNER);
            root.join("archiveDocumentType", JoinType.LEFT);
            root.join("archiveExtraProperty", JoinType.LEFT);
            root.join("sysUserByDeletedBySysUser", JoinType.LEFT);
            root.join("organizationByRootOrganization", JoinType.LEFT);
            root.join("organizationByDivan", JoinType.LEFT);
            root.join("sysUserByLockedSysUser", JoinType.LEFT);
            root.join("organizationByOrganization", JoinType.LEFT);
            root.join("userAlias", JoinType.LEFT);
            root.join("sysUserByCreatorUser", JoinType.LEFT);
            criteriaQuery.select(root);
            List<Predicate> predicates = new ArrayList<Predicate>();
            if (cabinetFolderId != null) {
                predicates.add(criteriaBuilder.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId));
            }
            if (arabicName != null && !arabicName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("arabicName"), "%" + arabicName.trim() + "%"));
            }

            if (englishName != null && !englishName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("englishName"), "%" + englishName.trim() + "%"));
            }

            if (description != null && !description.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + description.trim() + "%"));
            }

            if (archiveDocumentType != null) {
                predicates.add(criteriaBuilder.equal(root.get("archiveDocumentType").get("id"), archiveDocumentType.getId()));
            }

            if (divan != null) {
                predicates.add(criteriaBuilder.equal(root.get("organizationByDivan").get("id"), divan.getId()));
            }

            if (side != null && !side.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("fromEntity"), "%" + side + "%"));
            }

            if (subject != null && !subject.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("subject"), "%" + subject + "%"));
            }

            if (year != null) {
                predicates.add(criteriaBuilder.equal(root.get("year"), year));
            }

            criteriaQuery.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<ArchiveDocument> typedQuery = session.createQuery(criteriaQuery);
            result = typedQuery.getResultList();
            return result;

        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    // -------------------- new methods implementations --------------------

    @Override
    public List<ArchiveDocument> findByCodeOrNameGlobal(String key) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ArchiveDocument> cq = cb.createQuery(ArchiveDocument.class);
            Root<ArchiveDocument> root = cq.from(ArchiveDocument.class);

            // join minimal relations if needed
            root.join("archiveDocumentType", JoinType.LEFT);
            root.join("archiveExtraProperty", JoinType.LEFT);

            String k = key == null ? "" : "%" + key.trim().toLowerCase() + "%";

            Predicate pCode = cb.like(cb.lower(root.get("code")), k);
            Predicate par = cb.like(cb.lower(root.get("arabicName")), k);
            Predicate peng = cb.like(cb.lower(root.get("englishName")), k);

            cq.select(root).where(cb.or(pCode, par, peng)).orderBy(cb.desc(root.get("id")));
            TypedQuery<ArchiveDocument> tq = session.createQuery(cq);
            return tq.getResultList();
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ArchiveDocument> getByCodeWithinCabinet(Long cabinetFolderId, String code) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ArchiveDocument> cq = cb.createQuery(ArchiveDocument.class);
            Root<ArchiveDocument> root = cq.from(ArchiveDocument.class);
            Join<ArchiveDocument, CabinetFolderDocument> joinCabinetFolderDoc = root.join("cabinetFolderDocuments", JoinType.INNER);

            root.join("archiveDocumentType", JoinType.LEFT);
            root.join("archiveExtraProperty", JoinType.LEFT);

            String k = code == null ? "" : "%" + code.trim() + "%";
            cq.select(root).where(cb.and(cb.equal(joinCabinetFolderDoc.get("cabinetFolder").get("id"), cabinetFolderId),
                                        cb.like(root.get("code"), k)));
            TypedQuery<ArchiveDocument> tq = session.createQuery(cq);
            return tq.getResultList();
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

}
