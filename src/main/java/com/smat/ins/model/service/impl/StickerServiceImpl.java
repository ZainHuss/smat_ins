package com.smat.ins.model.service.impl;

import com.generic.model.service.impl.GenericServiceImpl;
import com.smat.ins.model.dao.StickerDao;
import com.smat.ins.model.entity.Sticker;
import com.smat.ins.model.service.StickerService;
import java.util.List;

public class StickerServiceImpl extends GenericServiceImpl<Sticker, StickerDao, Long> implements StickerService {

    /**
     * 
     */
    private static final long serialVersionUID = 578950734771371814L;

    @Override
    public Long getLastSeq() {
        // TODO Auto-generated method stub
        return dao.getLastSeq();
    }

    @Override
    public Integer getLastStickerNo() {
        // TODO Auto-generated method stub
        return dao.getLastStickerNo();
    }

    @Override
    public Integer getLastSerialID() {
        // TODO Auto-generated method stub
        return dao.getLastSerialID();
    }
    
    @Override
    public List<Sticker> searchStickers(String searchText) {
        return dao.searchStickers(searchText);
    }
    
}