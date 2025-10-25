package com.smat.ins.model.service;


import java.util.List;

import com.generic.model.service.GenericService;

import com.smat.ins.model.entity.EquipmentCategory;

public interface EquipmentCategoryService extends
        GenericService<EquipmentCategory, Short> {

    public List<EquipmentCategory> getCatWithTemplateCreated();

    // NEW - return only enabled categories
    public List<EquipmentCategory> findAllEnabled();

}
