package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.ArchiveDocumentType;
import com.smat.ins.model.entity.Organization;

public interface ArchiveDocumentDao extends GenericDao<ArchiveDocument, Long> {

    public List<ArchiveDocument> getByParent(ArchiveDocument archiveDocument, Long cabinetFolderId, Integer start,
            Integer pageSize);

    public Long getByParentCount(ArchiveDocument archiveDocument, Long cabinetFolderId);

    public List<ArchiveDocument> getByNameOrCode(Long cabinetFolderId, String searchKey);

    public List<ArchiveDocument> getByNameOrCode(Long cabinetFolderId, String arabicName, String code);

    public ArchiveDocument getArchiveDocumentWithDetails(ArchiveDocument archiveDocument, Long cabinetFolderId);

    public List<ArchiveDocument> getAllWithDetails(Long cabinetFolderId);

    public ArchiveDocument getParentArchiveDocument(ArchiveDocument archiveDocument, Long cabinetFolderId);

    public Long getMaxArchiveDocumentCode(ArchiveDocument parentArchiveDocument, Long cabinetFolderId);

    public ArchiveDocument getBy(Long cabinetFolderId, String arabicName, String englishName);

    public List<ArchiveDocument> getBy(Long cabinetFolderId, String arabicName, String englishName, String description,
            ArchiveDocumentType archiveDocumentType, Organization divan, String side, String subject, Short year);

    // -------------------- new flexible searches --------------------
    /**
     * بحث عام بدون قيد على cabinet_folder — يبحث في code أو arabicName أو englishName
     */
    public List<ArchiveDocument> findByCodeOrNameGlobal(String key);

    /**
     * بحث داخل cabinet محدد عن code (باستخدام علاقة cabinet_folder_document)
     */
    public List<ArchiveDocument> getByCodeWithinCabinet(Long cabinetFolderId, String code);

}
