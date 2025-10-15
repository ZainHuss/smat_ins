package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.ArchiveDocumentFile;

public interface ArchiveDocumentFileDao extends GenericDao<ArchiveDocumentFile, Long> {
	public Long getMaxArchiveDocumentFileCode(ArchiveDocument archiveDocument);
	
	public List<ArchiveDocumentFile> getBy(ArchiveDocument archiveDocument );
	
	public List<ArchiveDocumentFile> getBy(String fileCode,String docCode,String folderCode,String drawerCode,String cabinetCode );

}
