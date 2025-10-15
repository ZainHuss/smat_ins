package com.smat.ins.model.service;




import java.util.List;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.CorrespondenceNote;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;

public interface CorrespondenceNoteService extends
		GenericService<CorrespondenceNote, Long> {
	public List<CorrespondenceNote> getCorrespondenceNotes(Correspondence correspondence)throws Exception;
	public List<CorrespondenceNote> getOutBoxBySysUser(CorrespondenceState correspondenceState, SysUser sysUserSender,int offset, int pageSize)throws Exception;  
    public Long getOutBoxCountBySysUser(CorrespondenceState correspondenceState, SysUser sysUserSender)throws Exception;
}
