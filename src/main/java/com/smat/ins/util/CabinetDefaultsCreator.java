package com.smat.ins.util;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

import com.smat.ins.model.entity.Cabinet;
import com.smat.ins.model.entity.CabinetDefinition;
import com.smat.ins.model.entity.CabinetFolder;
import com.smat.ins.model.entity.CabinetLocation;
import com.smat.ins.model.entity.CabinetType;
import com.smat.ins.model.entity.SysUser;
import com.smat.ins.model.service.CabinetDefinitionService;
import com.smat.ins.model.service.CabinetFolderService;
import com.smat.ins.model.service.CabinetLocationService;
import com.smat.ins.model.service.CabinetService;
import com.smat.ins.model.service.CabinetTypeService;
import com.smat.ins.util.BeanUtility;

/**
 * Utility to create default cabinets (inspection, employee training) when missing.
 */
public class CabinetDefaultsCreator {

    public static void ensureDefaultCabinets(SysUser sysUser) {
        try {
            CabinetService cabinetService = (CabinetService) BeanUtility.getBean("cabinetService");
            CabinetTypeService cabinetTypeService = (CabinetTypeService) BeanUtility.getBean("cabinetTypeService");
            CabinetLocationService cabinetLocationService = (CabinetLocationService) BeanUtility.getBean("cabinetLocationService");
            CabinetDefinitionService cabinetDefinitionService = (CabinetDefinitionService) BeanUtility.getBean("cabinetDefinitionService");
            CabinetFolderService cabinetFolderService = (CabinetFolderService) BeanUtility.getBean("cabinetFolderService");

            // Determine default cabinet type and location
            CabinetType defaultType = null;
            try {
                // CabinetTypeService uses Integer as id type
                defaultType = cabinetTypeService.findById(1);
            } catch (Exception ignore) {
            }
            if (defaultType == null) {
                // fallback: take first available
                try { defaultType = cabinetTypeService.findAll().get(0); } catch (Exception ignore) {}
            }

            CabinetLocation defaultLocation = null;
            try { defaultLocation = cabinetLocationService.findAll().get(0); } catch (Exception ignore) {}

            // 1) Cabinet for Inspection
            String inspectionName = "Cabinet for Inspection";
            String inspectionCode = "INS-DEFAULT";
            Cabinet inspectionCabinet = null;
            try {
                for (Cabinet c : cabinetService.findAll()) {
                    if (inspectionCode.equals(c.getCode())) { inspectionCabinet = c; break; }
                }
            } catch (Exception ignore) {}
            if (inspectionCabinet == null) {
                inspectionCabinet = new Cabinet();
                inspectionCabinet.setCode(inspectionCode);
                // set both Arabic/English names and descriptions
                inspectionCabinet.setArabicName(inspectionName);
                inspectionCabinet.setEnglishName(inspectionName);
                inspectionCabinet.setArabicDescription("Default cabinet for inspection attachments");
                inspectionCabinet.setEnglishDescription("Default cabinet for inspection attachments");
                inspectionCabinet.setCabinetType(defaultType);
                inspectionCabinet.setCabinetLocation(defaultLocation);
                inspectionCabinet.setSysUser(sysUser);
                inspectionCabinet.setCreatedDate(Calendar.getInstance().getTime());

                CabinetDefinition def = new CabinetDefinition();
                def.setCabinet(inspectionCabinet);
                def.setSysUser(sysUser);
                def.setDrawerArabicName("المرفقات");
                def.setDrawerEnglishName("attachment");
                def.setCode("01");
                def.setDrawerOrder(1);
                def.setCreatedDate(Calendar.getInstance().getTime());
                inspectionCabinet.getCabinetDefinitions().add(def);

                cabinetService.saveOrUpdate(inspectionCabinet);

                // create a CabinetFolder under the definition
                try {
                    CabinetFolder folder = new CabinetFolder();
                    folder.setCabinetDefinition(def);
                    folder.setSysUser(sysUser);
                    folder.setArabicName("المرفقات");
                    folder.setEnglishName("attachment");
                    folder.setCode("01");
                    folder.setCreatedDate(Calendar.getInstance().getTime());
                    cabinetFolderService.saveOrUpdate(folder);
                } catch (Exception ignore) {}

                // create directories on disk
                try {
                    if (defaultLocation != null) {
                        String mainLocation = selectMainLocation(defaultLocation);
                        java.nio.file.Path path = Paths.get(mainLocation + FileSystems.getDefault().getSeparator() + inspectionCabinet.getCode());
                        Files.createDirectories(path);
                        Files.createDirectories(path.resolve(def.getCode()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 2) Cabinet for Employee Training
            String empName = "Attachment for Employee Training";
            String empCode = "EMP-DEFAULT";
            Cabinet empCabinet = null;
            try {
                for (Cabinet c : cabinetService.findAll()) {
                    if (empCode.equals(c.getCode())) { empCabinet = c; break; }
                }
            } catch (Exception ignore) {}
            if (empCabinet == null) {
                empCabinet = new Cabinet();
                empCabinet.setCode(empCode);
                // set both Arabic/English names and descriptions
                empCabinet.setArabicName(empName);
                empCabinet.setEnglishName(empName);
                empCabinet.setArabicDescription("Default cabinet for employee training attachments");
                empCabinet.setEnglishDescription("Default cabinet for employee training attachments");
                empCabinet.setCabinetType(defaultType);
                empCabinet.setCabinetLocation(defaultLocation);
                empCabinet.setSysUser(sysUser);
                empCabinet.setCreatedDate(Calendar.getInstance().getTime());

                CabinetDefinition def2 = new CabinetDefinition();
                def2.setCabinet(empCabinet);
                def2.setSysUser(sysUser);
                def2.setDrawerArabicName("المرفقات");
                def2.setDrawerEnglishName("attachment");
                def2.setCode("01");
                def2.setDrawerOrder(1);
                def2.setCreatedDate(Calendar.getInstance().getTime());
                empCabinet.getCabinetDefinitions().add(def2);

                cabinetService.saveOrUpdate(empCabinet);

                // create a CabinetFolder under the definition
                try {
                    CabinetFolder folder2 = new CabinetFolder();
                    folder2.setCabinetDefinition(def2);
                    folder2.setSysUser(sysUser);
                    folder2.setArabicName("المرفقات");
                    folder2.setEnglishName("attachment");
                    folder2.setCode("01");
                    folder2.setCreatedDate(Calendar.getInstance().getTime());
                    cabinetFolderService.saveOrUpdate(folder2);
                } catch (Exception ignore) {}

                // create directories on disk
                try {
                    if (defaultLocation != null) {
                        String mainLocation = selectMainLocation(defaultLocation);
                        java.nio.file.Path path = Paths.get(mainLocation + FileSystems.getDefault().getSeparator() + empCabinet.getCode());
                        Files.createDirectories(path);
                        Files.createDirectories(path.resolve(def2.getCode()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String selectMainLocation(CabinetLocation loc) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win") && loc.getWinLocation() != null)
            return loc.getWinLocation();
        if (os.contains("mac") && loc.getMacLocation() != null)
            return loc.getMacLocation();
        if ((os.contains("nix") || os.contains("nux") || os.contains("aix")) && loc.getLinuxLocation() != null)
            return loc.getLinuxLocation();
        // fallback to winLocation then linux then mac
        if (loc.getWinLocation() != null) return loc.getWinLocation();
        if (loc.getLinuxLocation() != null) return loc.getLinuxLocation();
        if (loc.getMacLocation() != null) return loc.getMacLocation();
        return ".";
    }
}
