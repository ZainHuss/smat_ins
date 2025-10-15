package com.smat.ins.model.service.impl;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.dao.ArchiveDocumentDao;
import com.smat.ins.model.dao.ArchiveDocumentFileDao;
import com.smat.ins.model.dao.CabinetFolderDocumentDao;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.ArchiveDocumentFile;
import com.smat.ins.model.entity.ArchiveDocumentType;
import com.smat.ins.model.entity.CabinetFolderDocument;
import com.smat.ins.model.entity.Organization;
import com.smat.ins.model.service.ArchiveDocumentService;
import com.smat.ins.util.UtilityHelper;

public class ArchiveDocumentServiceImpl extends GenericServiceImpl<ArchiveDocument, ArchiveDocumentDao, Long>
        implements ArchiveDocumentService {

    private static final long serialVersionUID = -6302455039820402629L;

    private ArchiveDocumentFileDao archiveDocumentFileDao;

    private CabinetFolderDocumentDao cabinetFolderDocumentDao;

    public ArchiveDocumentFileDao getArchiveDocumentFileDao() {
        return archiveDocumentFileDao;
    }

    public void setArchiveDocumentFileDao(ArchiveDocumentFileDao archiveDocumentFileDao) {
        this.archiveDocumentFileDao = archiveDocumentFileDao;
    }

    public CabinetFolderDocumentDao getCabinetFolderDocumentDao() {
        return cabinetFolderDocumentDao;
    }

    public void setCabinetFolderDocumentDao(CabinetFolderDocumentDao cabinetFolderDocumentDao) {
        this.cabinetFolderDocumentDao = cabinetFolderDocumentDao;
    }

    @Override
    public Long getMaxArchiveDocumentCode(ArchiveDocument parentArchiveDocument, Long cabinetFolderId) throws Exception {
        return dao.getMaxArchiveDocumentCode(parentArchiveDocument, cabinetFolderId);
    }

    @Override
    public List<ArchiveDocument> getByParent(ArchiveDocument archiveDocument, Long cabinetFolderId, Integer start,
            Integer pageSize) throws Exception {
        return dao.getByParent(archiveDocument, cabinetFolderId, start, pageSize);
    }

    @Override
    public Long getByParentCount(ArchiveDocument archiveDocument, Long cabinetFolderId) throws Exception {
        return dao.getByParentCount(archiveDocument, cabinetFolderId);
    }

    @Override
    public List<ArchiveDocument> getByNameOrCode(Long cabinetFolderId, String searchKey) throws Exception {
        return dao.getByNameOrCode(cabinetFolderId, searchKey);
    }

    @Override
    public List<ArchiveDocument> getByNameOrCode(Long cabinetFolderId, String arabicName, String code) throws Exception {
        return dao.getByNameOrCode(cabinetFolderId, arabicName, code);
    }

    @Override
    public ArchiveDocument getArchiveDocumentWithDetails(ArchiveDocument archiveDocument, Long cabinetFolderId)
            throws Exception {
        return dao.getArchiveDocumentWithDetails(archiveDocument, cabinetFolderId);
    }

    @Override
    public List<ArchiveDocument> getAllWithDetails(Long cabinetFolderId) throws Exception {
        return dao.getAllWithDetails(cabinetFolderId);
    }

    @Override
    public ArchiveDocument getParentArchiveDocument(ArchiveDocument archiveDocument, Long cabinetFolderId)
            throws Exception {
        return dao.getParentArchiveDocument(archiveDocument, cabinetFolderId);
    }

    @Override
    public ArchiveDocument getBy(Long cabinetFolderId, String arabicName, String englishName) throws Exception {
        return dao.getBy(cabinetFolderId, arabicName, englishName);
    }

    @Override
    public List<ArchiveDocument> getBy(Long cabinetFolderId, String arabicName, String englishName, String description,
            ArchiveDocumentType archiveDocumentType, Organization divan, String side, String subject, Short year)
            throws Exception {
        return dao.getBy(cabinetFolderId, arabicName, englishName, description, archiveDocumentType, divan, side,
                subject, year);
    }

    @Override
    public ArchiveDocument savewithDoc(ArchiveDocument archiveDocument, CabinetFolderDocument cabinetFolderDocument,
            List<ArchiveDocumentFile> archiveDocumentFiles, String dirPath) throws Exception {
        ArchiveDocument insertedArchDoc = null;
        ResourceBundle applicationSetting = ResourceBundle.getBundle("com.smat.ins.view.resources.applicationSetting");
        Locale currentLocale = UtilityHelper.getLocale();
        ResourceBundle msg = ResourceBundle.getBundle("com.smat.ins.view.resources.interface", currentLocale);
        Integer documentCodeLength = null;
        insertedArchDoc = dao.insert(archiveDocument);

        cabinetFolderDocumentDao.insert(cabinetFolderDocument);
        if (applicationSetting.containsKey("maxDocumentCodeLength")
                && applicationSetting.getString("maxDocumentCodeLength") != null) {
            documentCodeLength = Integer.parseInt(applicationSetting.getString("maxDocumentCodeLength"));

        } else {
            documentCodeLength = 9;
        }
        Long maxArchiveDocumentFileCodeLenght = archiveDocumentFileDao.getMaxArchiveDocumentFileCode(insertedArchDoc);
        List<ArchiveDocumentFile> documentFiles = archiveDocumentFileDao.getBy(archiveDocument);
        if (archiveDocumentFiles != null && !archiveDocumentFiles.isEmpty()) {
            for (ArchiveDocumentFile archiveDocumentFile : archiveDocumentFiles) {
                if (checkIfFoundInList(documentFiles, archiveDocumentFile)) {
                    UtilityHelper.addWarnMessage(msg.getString("theFile") + " ( " + archiveDocumentFile.getName().trim()
                            + " ) " + msg.getString("hadNotBeenAdded"));
                    continue;
                }
                if (maxArchiveDocumentFileCodeLenght != null) {
                    archiveDocumentFile.setCode(
                            String.format("%0" + documentCodeLength + "d", maxArchiveDocumentFileCodeLenght + 1));
                    archiveDocumentFile.setLogicalPath(archiveDocumentFile.getLogicalPath()
                            + archiveDocumentFile.getCode() + '.' + archiveDocumentFile.getExtension());
                    archiveDocumentFile.setServerPath(archiveDocumentFile.getServerPath()
                            + archiveDocumentFile.getCode() + '.' + archiveDocumentFile.getExtension());
                }
                FileUtils.writeByteArrayToFile(new File(dirPath + FileSystems.getDefault().getSeparator()
                        + archiveDocumentFile.getCode() + '.' + archiveDocumentFile.getExtension()),
                        archiveDocumentFile.getContent());
                archiveDocumentFile.setCreatedDate(Calendar.getInstance().getTime());
                archiveDocumentFileDao.insert(archiveDocumentFile);
                maxArchiveDocumentFileCodeLenght += 1;
            }

        }

        return insertedArchDoc;
    }

    @Override
    public Boolean saveArchiveDocument(ArchiveDocument archiveDocument, List<ArchiveDocumentFile> archiveDocumentFiles,
            String dirPath) throws Exception {
        ResourceBundle applicationSetting = ResourceBundle.getBundle("com.smat.ins.view.resources.applicationSetting");
        Locale currentLocale = UtilityHelper.getLocale();
        ResourceBundle msg = ResourceBundle.getBundle("com.smat.ins.view.resources.interface", currentLocale);

        Integer documentCodeLength = null;
        if (applicationSetting.containsKey("maxDocumentCodeLength")
                && applicationSetting.getString("maxDocumentCodeLength") != null) {
            documentCodeLength = Integer.parseInt(applicationSetting.getString("maxDocumentCodeLength"));

        } else {
            documentCodeLength = 9;
        }
        Long maxArchiveDocumentFileCodeLenght = archiveDocumentFileDao.getMaxArchiveDocumentFileCode(archiveDocument);
        List<ArchiveDocumentFile> documentFiles = archiveDocumentFileDao.getBy(archiveDocument);
        if (archiveDocumentFiles != null && !archiveDocumentFiles.isEmpty()) {
            for (ArchiveDocumentFile archiveDocumentFile : archiveDocumentFiles) {
                if (checkIfFoundInList(documentFiles, archiveDocumentFile)) {
                    UtilityHelper.addWarnMessage(msg.getString("theFile") + " ( " + archiveDocumentFile.getName().trim()
                            + " ) " + msg.getString("hadNotBeenAdded"));
                    continue;
                }
                if (maxArchiveDocumentFileCodeLenght != null) {
                    archiveDocumentFile.setCode(
                            String.format("%0" + documentCodeLength + "d", maxArchiveDocumentFileCodeLenght + 1));
                    archiveDocumentFile.setLogicalPath(archiveDocumentFile.getLogicalPath()
                            + archiveDocumentFile.getCode() + '.' + archiveDocumentFile.getExtension());
                    archiveDocumentFile.setServerPath(archiveDocumentFile.getServerPath()
                            + archiveDocumentFile.getCode() + '.' + archiveDocumentFile.getExtension());
                }
                FileUtils.writeByteArrayToFile(new File(dirPath + FileSystems.getDefault().getSeparator()
                        + archiveDocumentFile.getCode() + '.' + archiveDocumentFile.getExtension()),
                        archiveDocumentFile.getContent());
                archiveDocumentFile.setCreatedDate(Calendar.getInstance().getTime());
                archiveDocumentFileDao.insert(archiveDocumentFile);
                maxArchiveDocumentFileCodeLenght += 1;
            }

        }
        return true;
    }

    @Override
    public Boolean updateArchiveDoc(ArchiveDocument archiveDocument, List<ArchiveDocumentFile> archiveDocumentFiles,
            String dirPath) throws Exception {
        ResourceBundle applicationSetting = ResourceBundle.getBundle("com.smat.ins.view.resources.applicationSetting");
        Locale currentLocale = UtilityHelper.getLocale();
        ResourceBundle msg = ResourceBundle.getBundle("com.smat.ins.view.resources.interface", currentLocale);
        Integer documentCodeLength = null;
        dao.update(archiveDocument);

        if (applicationSetting.containsKey("maxDocumentCodeLength")
                && applicationSetting.getString("maxDocumentCodeLength") != null) {
            documentCodeLength = Integer.parseInt(applicationSetting.getString("maxDocumentCodeLength"));

        } else {
            documentCodeLength = 9;
        }
        Long maxArchiveDocumentFileCodeLenght = archiveDocumentFileDao.getMaxArchiveDocumentFileCode(archiveDocument);
        List<ArchiveDocumentFile> documentFiles = archiveDocumentFileDao.getBy(archiveDocument);
        for (ArchiveDocumentFile archiveDocumentFileVar : documentFiles) {
            if (!checkIfFoundInList(archiveDocumentFiles, archiveDocumentFileVar)) {
                archiveDocumentFileDao.delete(archiveDocumentFileVar);
                String filePath = dirPath + FileSystems.getDefault().getSeparator() + archiveDocumentFileVar.getCode()
                        + "." + archiveDocumentFileVar.getExtension();
                if (Files.exists(Paths.get(filePath)))
                    Files.delete(Paths.get(filePath));
            }
        }

        if (archiveDocumentFiles != null && !archiveDocumentFiles.isEmpty()) {
            for (ArchiveDocumentFile archiveDocumentFile : archiveDocumentFiles) {
                if (checkIfFoundInList(documentFiles, archiveDocumentFile)) {
                    UtilityHelper.addWarnMessage(msg.getString("theFile") + " ( " + archiveDocumentFile.getName().trim()
                            + " ) " + msg.getString("hadNotBeenAdded"));
                    continue;
                }
                if (maxArchiveDocumentFileCodeLenght != null) {
                    archiveDocumentFile.setCode(
                            String.format("%0" + documentCodeLength + "d", maxArchiveDocumentFileCodeLenght + 1));
                    archiveDocumentFile.setLogicalPath(archiveDocumentFile.getLogicalPath()
                            + archiveDocumentFile.getCode() + '.' + archiveDocumentFile.getExtension());
                    archiveDocumentFile.setServerPath(archiveDocumentFile.getServerPath()
                            + archiveDocumentFile.getCode() + '.' + archiveDocumentFile.getExtension());
                }
                FileUtils.writeByteArrayToFile(new File(dirPath + FileSystems.getDefault().getSeparator()
                        + archiveDocumentFile.getCode() + '.' + archiveDocumentFile.getExtension()),
                        archiveDocumentFile.getContent());
                archiveDocumentFile.setCreatedDate(Calendar.getInstance().getTime());
                archiveDocumentFileDao.insert(archiveDocumentFile);
                maxArchiveDocumentFileCodeLenght += 1;
            }

        }
        return true;
    }

    private boolean checkIfFoundInList(List<ArchiveDocumentFile> archiveDocumentFiles,
            ArchiveDocumentFile archiveDocumentFile) {
        boolean result = false;
        for (ArchiveDocumentFile file : archiveDocumentFiles)
            if (file.getName().equalsIgnoreCase(archiveDocumentFile.getName())) {
                result = true;
                break;
            }
        return result;
    }

    @Override
    public Boolean deleteArchiveDoc(ArchiveDocument archiveDocument) throws Exception {
        cabinetFolderDocumentDao.deleteByDocumentId(archiveDocument.getId());
        dao.delete(archiveDocument);
        return true;
    }

    // -------------------- new service implementations --------------------

    @Override
    public ArchiveDocument getByCode(Long cabinetFolderId, String code) throws Exception {
        if (code == null) return null;

        // 1) لو فيه cabinetFolderId → حاول البحث داخل الـ cabinet أولًا
        if (cabinetFolderId != null) {
            List<ArchiveDocument> list = dao.getByCodeWithinCabinet(cabinetFolderId, code);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }

        // 2) fallback: بحث عالمي
        List<ArchiveDocument> global = dao.findByCodeOrNameGlobal(code);
        if (global != null && !global.isEmpty()) {
            return global.get(0);
        }

        return null;
    }

    @Override
    public List<ArchiveDocumentFile> getFilesByDocument(ArchiveDocument archiveDocument) throws Exception {
        if (archiveDocument == null) return null;
        return archiveDocumentFileDao.getBy(archiveDocument);
    }

    @Override
    public List<ArchiveDocument> findByCodeOrNameGlobal(String key) throws Exception {
        return dao.findByCodeOrNameGlobal(key);
    }

}
