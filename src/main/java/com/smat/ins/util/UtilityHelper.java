package com.smat.ins.util;


import java.io.InputStream;
import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.omnifaces.util.Faces;



public class UtilityHelper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 916144897744051384L;

	public static Locale getLocale() {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getViewRoot().getLocale();
	}

	public static String getParam(String param) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		return params.get(param);
	}

	public static void setRequestAttr(String key, Object value) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> attributes = context.getExternalContext().getRequestMap();
		attributes.put(key, value);
	}

	public static Object getRequestAttr(String key) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> attributes = context.getExternalContext().getRequestMap();
		return attributes.get(key);
	}

	public static Object getSessionAttr(String key) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getExternalContext().getSessionMap().get(key);
	}

	public static void putSessionAttr(String key, Object value) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = ctx.getExternalContext().getSessionMap();
		sessionMap.put(key, value);
	}

	public static void sendErrorMasg(String msg) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
//	    LocaleBean localeBean=(LocaleBean) FacesUtils.getManagedBean("localeBean");
		ResourceBundle bundle = ResourceBundle.getBundle("com.correspondance.model.view.resources.messages",
				getLocale());
		FacesMessage message = new FacesMessage(bundle.getString(msg));
		facesContext.addMessage("", message);

	}

	private final static Logger log = Logger.getLogger(UtilityHelper.class.getName());

	private static Properties buildProperties = null;

	/**
	 * Get servlet context.
	 *
	 * @return the servlet context
	 */
	public static ServletContext getServletContext() {
		return (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	}

	public static ExternalContext getExternalContext() {
		FacesContext fc = FacesContext.getCurrentInstance();
		return fc.getExternalContext();
	}

	public static HttpSession getHttpSession(boolean create) {
		return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(create);
	}

	/**
	 * Get managed bean based on the bean name.
	 *
	 * @param beanName the bean name
	 * @return the managed bean associated with the bean name
	 */
	public static Object getManagedBean(String beanName) {
		FacesContext fc = FacesContext.getCurrentInstance();
		ELContext elc = fc.getELContext();
		ExpressionFactory ef = fc.getApplication().getExpressionFactory();
		ValueExpression ve = ef.createValueExpression(elc, getJsfEl(beanName), Object.class);
		try {
			return ve.getValue(elc);
		} catch (Exception e) {
			log.log(Level.SEVERE, "could not get value for bean " + beanName, e);
		}
		return null;
	}

	/**
	 * Remove the managed bean based on the bean name.
	 *
	 * @param beanName the bean name of the managed bean to be removed
	 */
	public static void resetManagedBean(String beanName) {
		FacesContext fc = FacesContext.getCurrentInstance();
		ELContext elc = fc.getELContext();
		ExpressionFactory ef = fc.getApplication().getExpressionFactory();
		ef.createValueExpression(elc, getJsfEl(beanName), Object.class).setValue(elc, null);
	}

	/**
	 * Store the managed bean inside the session scope.
	 *
	 * @param beanName    the name of the managed bean to be stored
	 * @param managedBean the managed bean to be stored
	 */
	public static void setManagedBeanInSession(String beanName, Object managedBean) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(beanName, managedBean);
	}

	/**
	 * Store the managed bean inside the request scope.
	 *
	 * @param beanName    the name of the managed bean to be stored
	 * @param managedBean the managed bean to be stored
	 */
	public static void setManagedBeanInRequest(String beanName, Object managedBean) {
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(beanName, managedBean);
	}

	/**
	 * Get parameter value from request scope.
	 *
	 * @param name the name of the parameter
	 * @return the parameter value
	 */
	public static String getRequestParameter(String name) {
		FacesContext context = FacesContext.getCurrentInstance();

		Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
		if (paramMap.containsKey(name))
			return paramMap.get(name);
		else
			return null;
	}

	/**
	 * Get named request map object value from request map.
	 *
	 * @param name the name of the key in map
	 * @return the key value if any, null otherwise.
	 */
	public static Object getRequestMapValue(String name) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(name);
	}

	/**
	 * Gets the action attribute value from the specified event for the given name.
	 * Action attributes are specified by &lt;f:attribute /&gt;.
	 *
	 * @param event
	 * @param name
	 * @return
	 */
	public static String getActionAttribute(ActionEvent event, String name) {
		return (String) event.getComponent().getAttributes().get(name);
	}

	public static String getBuildAttribute(String name) {
		if (buildProperties != null)
			return buildProperties.getProperty(name, "unknown");
		InputStream is = null;
		try {
			is = getServletContext().getResourceAsStream("/WEB-INF/buildversion.properties");
			buildProperties = new Properties();
			buildProperties.load(is);
		} catch (Throwable e) {
			is = null;
			buildProperties = null;
			return "unknown";
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Throwable t) {
				}
			}
		}
		return buildProperties.getProperty(name, "unknown");
	}

	/**
	 * Gest parameter value from the the session scope.
	 *
	 * @param name name of the parameter
	 * @return the parameter value if any.
	 */
	@SuppressWarnings("unused")
	public static String getSessionParameter(String name) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest myRequest = (HttpServletRequest) context.getExternalContext().getRequest();
		HttpSession mySession = myRequest.getSession();
		return myRequest.getParameter(name);
	}

	public static String getFacesParameter(String parameter) {
		return getFacesParameter(parameter, null);
	}

	/**
	 * Get parameter value from the web.xml file
	 *
	 * @param parameter name to look up
	 * @return the value of the parameter
	 */
	public static String getFacesParameter(String parameter, String defaultVal) {
		try {
			// Get the servlet context based on the faces context
			ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

			// Return the value read from the parameter
			String toReturn = sc.getInitParameter(parameter);

			return toReturn != null ? toReturn : defaultVal;
		} catch (Exception failedGet) {
		}

		return defaultVal;
	}

	/**
	 * Add information message.
	 *
	 * @param msg the information message
	 */
	public static void addInfoMessage(String msg) {
		addInfoMessage(null, msg);
	}

	/**
	 * Add information message to a specific client.
	 *
	 * @param clientId the client id
	 * @param msg      the information message
	 */
	public static void addInfoMessage(String clientId, String msg) {
		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
	}

	/**
	 * Add information message to a specific client.
	 *
	 * @param clientId the client id
	 * @param msg      the information message
	 */
	public static void addWarnMessage(String clientId, String msg) {
		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg));
	}
	
	/**
	 * Add information message to a specific client.
	 *
	 * @param facesMessage      the information message
	 */
	public static void addWarnMessage( String msg) {
		addWarnMessage( null, msg);
	}


	/**
	 * Add error message.
	 *
	 * @param msg the error message
	 */
	public static void addErrorMessage(String msg) {
		addErrorMessage(null, msg);
	}
	
	/**
	 * Method to return the value specified via an f:attribute component
	 *
	 * @param event of the parent that had the f:attribute
	 * @param name  of the param
	 * @return the Object value, which may be null if the attribute wasn't found
	 */
	public static Object getFAttribute(ActionEvent event, String name) {
		return event.getComponent().getAttributes().get(name);
	}

	/**
	 * Finds component with the given id
	 *
	 * @param c  component check children of.
	 * @param id id of component to search for.
	 * @return found component if any.
	 */
	public static UIComponent findComponent(UIComponent c, String id) {
		if (id.equals(c.getId())) {
			return c;
		}
		Iterator<UIComponent> kids = c.getFacetsAndChildren();
		while (kids.hasNext()) {
			UIComponent found = findComponent(kids.next(), id);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	/**
	 * Finds all component with the given id. Component id's are formed from the
	 * concatination of parent component ids. This search will find all componet in
	 * the component tree with the specified id as it is possible to have the same
	 * id used more then once in the component tree
	 *
	 * @param root            component check children of.
	 * @param id              id of component to search for.
	 * @param foundComponents list of found component with the specified id.
	 * @return found component if any.
	 */
	public static void findAllComponents(UIComponent root, String id, List<UIComponent> foundComponents) {
		if (id.equals(root.getId())) {
			foundComponents.add(root);
		}
		Iterator<UIComponent> kids = root.getFacetsAndChildren();
		while (kids.hasNext()) {
			findAllComponents(kids.next(), id, foundComponents);
		}
	}

	/**
	 * Add error message to a specific client.
	 *
	 * @param clientId the client id
	 * @param msg      the error message
	 */
	public static void addErrorMessage(String clientId, String msg) {
		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
	}


	@SuppressWarnings("el-syntax")
	private static String getJsfEl(String value) {
		return "#{" + value + "}";
	}

	public static boolean isBlank(String check) {
		return ((check == null) || (check.trim().length() == 0));
	}

	public static String join(String[] base) {
		return join(base, ",");
	}

	public static String join(String[] base, String delimiter) {
		// Check if we have enough items to warrant the list logic
		if (base.length > 1) {
			StringBuilder toReturn = new StringBuilder(base.length * 5);

			for (int i = 0; i < base.length; i++) {
				toReturn.append(base[i]);

				// Add a delimiter unless this is the last item
				if ((i + 1) <= (base.length - 1)) {
					toReturn.append(delimiter);
				}
			}
			return toReturn.toString();
		}
		// return a single item without any delimiters
		else if (base.length == 1)
			return base[0];
		// if base is empty - return empty string
		else {
			return "";
		}
	}

	/**
	 * Method to refresh the browser to the specified url by adding a META-REFRESH
	 * tag to the response
	 */
	public static void refreshBrowser(String url) {
		try {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();

			if (response != null) {
				response.setHeader("Refresh", "0; URL=" + response.encodeRedirectURL(url));
			}
		} catch (Exception ignored) {
		}
	}

	/**
	 * Method to redirect the browser to the specified url via the ExternalContext
	 * redirect method
	 */
	public static void redirectBrowser(String url) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			if ((context != null) && (context.getExternalContext() != null)) {
				context.getExternalContext().redirect(url);
			}
		} catch (Exception failedRedirect) {
			failedRedirect.printStackTrace();
		}
	}

	/**
	 * get file from resource as stream
	 * 
	 * @param path path of file
	 * @return input stream of file
	 */
	public static InputStream getResourceAsStream(String path) {
		return getServletContext().getResourceAsStream(path);
	}
	
	
    public static java.util.Date tryParse(Object obj){
    	try{
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    	java.util.Date date= formatter.parse(obj.toString());
    	return date;
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public static Date convertToSqlDate(java.util.Date utileDate) {
		Date sqlDate = null;
		if (utileDate != null)
			sqlDate = new Date(utileDate.getTime());

		return sqlDate;
	}
    
    public static java.util.Date getCurrentDate(){
    	Calendar calendar=Calendar.getInstance();
    	return calendar.getTime();
    }
    
    
    private final static String ALGORITHM = "DES";
    private final static String HEX = "0123456789ABCDEF";
    private final static String secretKey="@D!2#M$@";
    
    /**
     * Encrypt data
     * @param secretKey -   a secret key used for encryption
     * @param data      -   data to encrypt
     * @return  Encrypted data
     * @throws Exception
     */
    public static String cipher(String data) throws Exception {
        // Key has to be of length 8
        if (secretKey == null || secretKey.length() < 8)
            throw new Exception("Invalid key length - 8 bytes key needed!");
         
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
         
        return toHex(cipher.doFinal(data.getBytes()));
    }
     
    /**
     * Decrypt data
     * @param secretKey -   a secret key used for decryption
     * @param data      -   data to decrypt
     * @return  Decrypted data
     * @throws Exception
     */
    public static String decipher(String data) throws Exception {
        // Key has to be of length 8
        if (secretKey == null || secretKey.length() < 8)
            throw new Exception("Invalid key length - 8 bytes key needed!");
         
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
         
        return new String(cipher.doFinal(toByte(data)));
    }
     
    // Helper methods
     
    private static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
         
        byte[] result = new byte[len];
         
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }
 
    public static String toHex(byte[] stringBytes) {
        StringBuffer result = new StringBuffer(2*stringBytes.length);
         
        for (int i = 0; i < stringBytes.length; i++) {
            result.append(HEX.charAt((stringBytes[i]>>4)&0x0f)).append(HEX.charAt(stringBytes[i]&0x0f));
        }
         
        return result.toString();
    }
    
    public static String getBaseURL() {
        return Faces.getRequestBaseURL();
    }
    
   

}
