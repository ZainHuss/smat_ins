package com.smat.ins.model.dao;

import java.util.List;

import com.generic.model.dao.GenericDao;
import com.smat.ins.model.entity.EquipmentCategory;

public interface EquipmentCategoryDao extends
        GenericDao<EquipmentCategory, Short> {

    public List<EquipmentCategory> getCatWithTemplateCreated();

    // NEW - return only enabled (not disabled) categories
    public List<EquipmentCategory> findAllEnabled();

}
