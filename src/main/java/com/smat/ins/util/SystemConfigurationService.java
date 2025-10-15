package com.smat.ins.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.smat.ins.model.entity.CabinetLocation;
import com.smat.ins.model.service.CabinetLocationService;

public class SystemConfigurationService {

	private CabinetLocation cabinetLocation;

	public SystemConfigurationService(CabinetLocationService cabinetLocationService) {
		super();
		// TODO Auto-generated constructor stub

		try {
			cabinetLocation = cabinetLocationService.getDefaultLocation();
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				Files.createDirectories(Paths.get(cabinetLocation.getWinLocation()));
			} else if (os.contains("osx")) {
				Files.createDirectories(Paths.get(cabinetLocation.getMacLocation()));
			} else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
				Files.createDirectories(Paths.get(cabinetLocation.getLinuxLocation()));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
