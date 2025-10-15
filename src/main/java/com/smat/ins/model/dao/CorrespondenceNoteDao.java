package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.Correspondence;
import com.smat.ins.model.entity.CorrespondenceNote;
import com.smat.ins.model.entity.CorrespondenceState;
import com.smat.ins.model.entity.SysUser;

public interface CorrespondenceNoteDao extends
		GenericDao<CorrespondenceNote, Long> {
   public List<CorrespondenceNote> getCorrespondenceNotes(Correspondence correspondence);
   
   public List<CorrespondenceNote> getOutBoxBySysUser(CorrespondenceState correspondenceState, SysUser sysUserSender,int offset, int pageSize);
  
   public Long getOutBoxCountBySysUser(CorrespondenceState correspondenceState, SysUser sysUserSender);

}
