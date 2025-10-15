package com.smat.ins.util;

import java.io.Serializable;


import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;






public class BeanUtility implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9068630872240546746L;

    
	public static Object getBean(ServletContext servletContext,String beanName){
		ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		return appContext.getBean(beanName);
	}
	
	public static Object getBean(String beanName){
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		return appContext.getBean(beanName);
	}
	
    

}
