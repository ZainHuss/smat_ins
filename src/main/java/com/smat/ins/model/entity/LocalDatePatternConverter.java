package com.smat.ins.model.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import com.smat.ins.util.UtilityHelper;



public class LocalDatePatternConverter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 633806647606591220L;
	
	public String getLocalDatePattern(Date localDate) {
		if(localDate==null)
			return null;
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		if (UtilityHelper.getLocale().getLanguage().contains("ar")) {
			dateFormatter=new SimpleDateFormat("yyyy/MM/dd");
			
		}else {
			dateFormatter=new SimpleDateFormat("dd/MM/yyyy");
		}
		return dateFormatter.format(localDate); 
	}
	
	public Date getLocalDate(Date localDate) {
		if(localDate==null)
			return null;
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		if (UtilityHelper.getLocale().getLanguage().contains("ar")) {
			dateFormatter=new SimpleDateFormat("yyyy/MM/dd");
			
		}else {
			dateFormatter=new SimpleDateFormat("dd/MM/yyyy");
		}
		try {
			return dateFormatter.parse(localDate.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String getLocalDateTimePattern(Date localDate) {
		if(localDate==null)
			return null;
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		if (UtilityHelper.getLocale().getLanguage().contains("ar")) {
			dateFormatter=new SimpleDateFormat("a hh:mm:ss yyyy/MM/dd");
			
		}else {
			dateFormatter=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		}
		return dateFormatter.format(localDate); 
	}
	
	public Date getLocalDateTime(Date localDate) {
		if(localDate==null)
			return null;
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		if (UtilityHelper.getLocale().getLanguage().contains("ar")) {
			dateFormatter=new SimpleDateFormat("a hh:mm:ss yyyy/MM/dd");
			
		}else {
			dateFormatter=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		}
		try {
			return dateFormatter.parse(localDate.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
