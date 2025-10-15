package com.smat.ins.model.service;



import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.entity.CabinetFolderDocument;

public interface CabinetFolderDocumentService extends
		GenericService<CabinetFolderDocument, Long> {
	public List<CabinetFolderDocument> getByCabinetFolder(CabinetFolder cabinetFolder)throws Exception;
	
	public Boolean deleteByDocumentId(Long docId)throws Exception;
	
	public CabinetFolder getBy(ArchiveDocument archiveDocument)throws Exception;

}
