package com.smat.ins.model.service;



import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.ArchiveDocument;
import com.smat.ins.model.entity.ArchiveDocumentFile;

public interface ArchiveDocumentFileService extends GenericService<ArchiveDocumentFile, Long> {
	
	public Long getMaxArchiveDocumentFileCode(ArchiveDocument archiveDocument) throws Exception;

	public List<ArchiveDocumentFile> getBy(ArchiveDocument archiveDocument) throws Exception;

	public List<ArchiveDocumentFile> getBy(String fileCode, String docCode, String folderCode, String drawerCode,
			String cabinetCode) throws Exception;

}
