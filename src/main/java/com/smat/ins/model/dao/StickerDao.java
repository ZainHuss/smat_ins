package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;

import com.smat.ins.model.entity.Sticker;

public interface StickerDao extends GenericDao<Sticker, Long> {
	public Long getLastSeq();

	public Integer getLastStickerNo();

	public Integer getLastSerialID();

	List<Sticker> searchStickers(String searchText);

}
