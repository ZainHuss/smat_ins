package com.smat.ins.model.service;

import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.ArchiveDocumentFile;
import com.smat.ins.model.entity.ArchiveDocumentType;
import com.smat.ins.model.entity.CabinetFolderDocument;
import com.smat.ins.model.entity.Organization;

public interface ArchiveDocumentService extends GenericService<ArchiveDocument, Long> {

    public List<ArchiveDocument> getByParent(ArchiveDocument archiveDocument, Long cabinetFolderId, Integer start,
            Integer pageSize) throws Exception;

    public Long getByParentCount(ArchiveDocument archiveDocument, Long cabinetFolderId) throws Exception;

    public List<ArchiveDocument> getByNameOrCode(Long cabinetFolderId, String searchKey) throws Exception;

    public List<ArchiveDocument> getByNameOrCode(Long cabinetFolderId, String arabicName, String code) throws Exception;

    public ArchiveDocument getArchiveDocumentWithDetails(ArchiveDocument archiveDocument, Long cabinetFolderId)
            throws Exception;

    public List<ArchiveDocument> getAllWithDetails(Long cabinetFolderId) throws Exception;

    public ArchiveDocument getParentArchiveDocument(ArchiveDocument archiveDocument, Long cabinetFolderId)
            throws Exception;

    public Long getMaxArchiveDocumentCode(ArchiveDocument parentArchiveDocument, Long cabinetFolderId) throws Exception;

    public ArchiveDocument getBy(Long cabinetFolderId, String arabicName, String englishName) throws Exception;

    public List<ArchiveDocument> getBy(Long cabinetFolderId, String arabicName, String englishName, String description,
            ArchiveDocumentType archiveDocumentType, Organization divan, String side, String subject, Short year)
            throws Exception;

    public ArchiveDocument savewithDoc(ArchiveDocument archiveDocument, CabinetFolderDocument cabinetFolderDocument,
            List<ArchiveDocumentFile> archiveDocumentFiles, String dirPath) throws Exception;

    public Boolean updateArchiveDoc(ArchiveDocument archiveDocument, List<ArchiveDocumentFile> archiveDocumentFiles,
            String dirPath) throws Exception;

    public Boolean saveArchiveDocument(ArchiveDocument archiveDocument, List<ArchiveDocumentFile> archiveDocumentFiles,
            String dirPath) throws Exception;

    public Boolean deleteArchiveDoc(ArchiveDocument archiveDocument) throws Exception;

    // -------------------- new service methods --------------------

    /**
     * Find a document by its code within a cabinet folder. Fallback to global if not found in cabinet.
     */
    public ArchiveDocument getByCode(Long cabinetFolderId, String code) throws Exception;

    /**
     * Return files attached to a given archive document.
     */
    public List<ArchiveDocumentFile> getFilesByDocument(ArchiveDocument archiveDocument) throws Exception;

    /**
     * Global search (no cabinet filter) by code or name.
     */
    public List<ArchiveDocument> findByCodeOrNameGlobal(String key) throws Exception;

}
