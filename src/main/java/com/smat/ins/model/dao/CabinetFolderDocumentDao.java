package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.entity.CabinetFolderDocument;

public interface CabinetFolderDocumentDao extends
		GenericDao<CabinetFolderDocument, Long> {
	public List<CabinetFolderDocument> getByCabinetFolder(CabinetFolder cabinetFolder);
	
	public Boolean deleteByDocumentId(Long docId);
	
	public CabinetFolder getBy(ArchiveDocument archiveDocument);

}
