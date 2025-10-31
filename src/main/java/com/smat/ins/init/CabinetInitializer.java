package com.smat.ins.init;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.smat.ins.util.CabinetDefaultsCreator;

@Named
@ApplicationScoped
public class CabinetInitializer {

    @PostConstruct
    public void init() {
        try {
            // run once at application start to ensure default cabinets
            CabinetDefaultsCreator.ensureDefaultCabinets(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
