package com.smat.ins.model.service.impl;



import java.util.List;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.ArchiveDocumentFile;
import com.smat.ins.model.dao.ArchiveDocumentFileDao;
import com.smat.ins.model.service.ArchiveDocumentFileService;


public class ArchiveDocumentFileServiceImpl extends
		GenericServiceImpl<ArchiveDocumentFile, ArchiveDocumentFileDao, Long> implements   ArchiveDocumentFileService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2011420391996606581L;

	@Override
	public Long getMaxArchiveDocumentFileCode(ArchiveDocument archiveDocument)throws Exception {
		// TODO Auto-generated method stub
		return dao.getMaxArchiveDocumentFileCode(archiveDocument);
	}

	@Override
	public List<ArchiveDocumentFile> getBy(ArchiveDocument archiveDocument)throws Exception {
		// TODO Auto-generated method stub
		return dao.getBy(archiveDocument);
	}

	@Override
	public List<ArchiveDocumentFile> getBy(String fileCode, String docCode, String folderCode, String drawerCode,
			String cabinetCode)throws Exception {
		// TODO Auto-generated method stub
		return dao.getBy(fileCode, docCode, folderCode, drawerCode, cabinetCode);
	}

}
