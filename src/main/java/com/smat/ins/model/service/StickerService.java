package com.smat.ins.model.service;

import com.generic.model.service.GenericService;
import com.smat.ins.model.entity.Sticker;
import java.util.List;
import javax.persistence.Query;

public interface StickerService extends GenericService<Sticker, Long> {
    public Long getLastSeq();
    public Integer getLastStickerNo();
    public Integer getLastSerialID();
    public List<Sticker> searchStickers(String searchText);
}