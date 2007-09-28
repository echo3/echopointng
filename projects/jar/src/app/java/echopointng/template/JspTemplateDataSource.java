package echopointng.template;
/* 
 * This file is part of the Echo Point Project.  This project is a collection
 * of Components that have extended the Echo Web Application Framework.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * <code>JspTemplateDataSource</code> takes it template
 * data from a JSP include path.
 * <p>
 * By default there is no caching of JSP template dtaa.  This is 
 * because the JSP engines are excellent at deciding when JSP
 * content needs recompiling.
 * <p>
 * Also since a Java class is compiled out of JSP content, then
 * the speed at which they operate menas caching may not have a 
 * dramatic effect.
 */
public class JspTemplateDataSource extends AbstractTemplateDataSource {
	
	private String jspPath;
	private Map requestAttributeMap = new HashMap();

	
	/**
	 * Creates a <code>JspTemplateDataSource</code> with no 
	 * JSP path.
	 */
	public JspTemplateDataSource() {
		this("");
	}
	
	/**
	 * Creates a <code>JspTemplateDataSource</code> that
	 * takes its template data from a JSP path 
	 */
	public JspTemplateDataSource(String jspPath) {
		this.jspPath = jspPath;
		setCachingHints(null);
		setContentType("jsp/xhtml");
	}
	
	/**
	 * @return Returns the JSP Path in use.
	 */
	public String getJspPath() {
		return jspPath;
	}
	/**
	 * Sets the JSP path to use for template data.
	 * 
	 * @param jspPath -  The newValue to set.
	 */
	public void setJspPath(String jspPath) {
		this.jspPath = jspPath;
	}
	
	/**
	 * @see echopointng.template.TemplateDataSource#getCanonicalName()
	 */
	public String getCanonicalName() {
		return "jsp:" + jspPath;
	}
	/**
	 * This always returns null because the InputStream of JSP data
	 * can only be obtained by the backend rendering engine.  It does
	 * this by exuting the JSP path and capturing the output data.
	 *  
	 * @see echopointng.template.TemplateDataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return null;
	}
	
	/**
	 * Returns the value of the named attribute as an Object, or 
	 * null if no attribute of the given name exists.
	 * <p>
	 * Attribute names should follow the same conventions 
	 * as package names. Names beginning with 
	 * java.*, javax.*, and com.sun.*, are reserved for use 
	 * by Sun Microsystems.
	 *  
	 * @param name a <code>String </code> specifying the name of the attribute
	 * @return an Object containing the value of the attribute, or null if the 
	 * attribute does not exist
	 */
	public Object getAttribute(String name) {
		return requestAttributeMap.get(name);
	}
	
	/**
	 * Returns an array of String containing the names of the attributes 
	 * available to this JSP request. This method returns an zero length array 
	 * if there are no attributes available to it.
	 * 
	 * @return an array of String containing the names of the attributes 
	 */
	public String[] getAttributeNames() {
		return (String[]) requestAttributeMap.keySet().toArray(new String[requestAttributeMap.keySet().size()]);
	}
	
	/**
	 * Associates a Java bean with a specified name.  This will be inserted
	 * into the ServletRequest (with Request scope) before the JSP is called
	 * <p>
	 * Bean names should follow the same conventions as package names. Names 
	 * beginning with java.*, javax.*, and com.sun.*, are reserved for use by 
	 * Sun Microsystems.
	 * <p>
	 * If the value passed in is null, the effect is the same as 
	 * calling <code>removeAttribute(java.lang.String)</code>.
	 * 
	 * @param name - the name to associated with the java Bean.  This will
	 * 					become a Request scope attribute within the JSP. 
	 * @param javaBean - the object to be associated with the name
	 */
	public void setAttribute(String name, Object javaBean) {
		requestAttributeMap.put(name,javaBean);
	}
	
	/**
	 * Removes an attribute. 
	 * <p>
	 * Attribute names should follow the same conventions 
	 * as package names. Names beginning with 
	 * java.*, javax.*, and com.sun.*, are reserved for use 
	 * by Sun Microsystems.
	 *
	 * @param name - the name associated with the attribute value.   
	 */
	public void removeAttribute(String name) {
		requestAttributeMap.remove(name);
	}
}
