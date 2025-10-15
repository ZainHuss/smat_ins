package com.smat.ins.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileCode {
	static int counter = 0;

	public static synchronized String getFileCode() throws Exception {
		String date = "";
		try {
			date = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
		} catch (Exception e) {
			throw e;
		}
		counter++;
		counter %= 10000;
		return date + String.format("%04d", counter);
	}
}
