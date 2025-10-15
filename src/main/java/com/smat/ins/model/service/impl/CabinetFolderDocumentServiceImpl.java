package com.smat.ins.model.service.impl;



import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.entity.CabinetFolderDocument;
import com.smat.ins.model.dao.CabinetFolderDocumentDao;
import com.smat.ins.model.service.CabinetFolderDocumentService;

public class CabinetFolderDocumentServiceImpl extends
		GenericServiceImpl<CabinetFolderDocument, CabinetFolderDocumentDao, Long> implements   CabinetFolderDocumentService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -28904168508179488L;

	@Override
	public List<CabinetFolderDocument> getByCabinetFolder(CabinetFolder cabinetFolder) throws Exception {
		// TODO Auto-generated method stub
		return dao.getByCabinetFolder(cabinetFolder);
	}

	@Override
	public Boolean deleteByDocumentId(Long docId)throws Exception {
		// TODO Auto-generated method stub
		return dao.deleteByDocumentId(docId);
	}

	@Override
	public CabinetFolder getBy(ArchiveDocument archiveDocument)throws Exception {
		// TODO Auto-generated method stub
		return dao.getBy(archiveDocument);
	}

}
