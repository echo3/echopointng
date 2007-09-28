package echopointng.externalevent;
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

import java.util.EventObject;
import java.util.Iterator;
import java.util.Map;

/**
 * <code>ExternalEvent</code> represents an event that
 * has been raised externally to the Echo 
 * web application.
 */
public class ExternalEvent extends EventObject {
	
	private Map eventParameters;
	/** 
	 * Creates an ExternalEvent.
	 *
	 * @param source the source of the event
	 */
	public ExternalEvent(Object source, Map eventParameters) {
		super(source);
		this.eventParameters = eventParameters;
	}

	/** 
	 * Returns the Map of web request parameters that where 
	 * encountered during this ExternalEvent.
	 * 
	 * @return - the Map of parameters that where encountered during 
	 * this external event.
	 */
	public Map getParameterMap() {
		return eventParameters;
	}
	
	/**
	 * Returns the value of an ExternalEvent request parameter as a String, or null 
	 * if the parameter does not exist. You should only use this 
	 * method when you are sure the parameter has only one value.
	 * <p> 
	 * If the parameter might have more than one value, use 
	 * getParameterValues(String).
	 * <p> 
	 * If you use this method with a multivalued parameter, the value returned 
	 * is equal to the first value in the array returned by getParameterValues(). 
     *
	 * @param paramName - the name of the parameter to retrieve
	 * @return the first parameter value or null
	 */
	public String getParameter(String paramName) {
		String[] values = getParameterValues(paramName);
		if (values == null || values.length == 0)	
			return null;
		return values[0];
	}
	
	/**
	 * Returns an array of String objects containing all of the values the given 
	 * ExternalEvent parameter has, or null if the parameter does not exist.
	 * <p>
	 * If the parameter has a single value, the array has a length of 1.
	 * 
	 * @param paramName - the name of the parameter to retrieve
	 * @return the all the parameter values or null
	 */
	public String[] getParameterValues(String paramName) {
		return (String[]) eventParameters.get(paramName); 
	}

	/**
	 * Returns a String[] containing the names of the parameters contained in this 
	 * ExternalEvent. If the external web request has no parameters, 
	 * the method returns a zero length array.
	 * <p>
	 * @return a String[] containing the names of the parameters contained in this 
	 * ExternalEvent.
	 */
	public String[] getParameterNames() {
		int nameCount = 0;
		for (Iterator iter = eventParameters.keySet().iterator(); iter.hasNext();) {
			iter.next(); nameCount++;
		}
		String[] nameArray = new String[nameCount];
		nameCount=0;
		for (Iterator iter = eventParameters.keySet().iterator(); iter.hasNext();) {
			nameArray[nameCount] = (String)iter.next();; 
			nameCount++;
		}
		return nameArray;		
	}
}
