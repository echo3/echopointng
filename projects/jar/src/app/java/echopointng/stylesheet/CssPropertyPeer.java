package echopointng.stylesheet;
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

/**
 * <code>CssValuePeer</code> is an interfaced used
 * to convert CSS property value strings into specific
 * Java objects and vice versa.
 */
public interface CssPropertyPeer {

	/**
	 * This method is called to convert the CSS string value into
	 * a specific Java object.
	 * 
	 * @param classLoader - the class loader in use
	 * @param objectClass - the current object class in question
	 * @param propertyValue - the CSS value string
	 * @param lineNo - the line no within the parsed CSS input
	 * @return a specific Object of the correct type
	 * @throws CssInvalidValueException - if the value cannot be converted.
	 */
	public Object getJavaObject(ClassLoader classLoader, Class objectClass, String propertyValue, int lineNo) throws CssInvalidValueException;
	
	/**
	 * This is called to convert a current Java object into a 
	 * CSS property value string.
	 * 
	 * @param classLoader - the class loader in use
	 * @param objectClass - the current object class in question
	 * @param propertyValue - the CSS value string
	 * @return a string in the right Css property value string format
	 */
	public String getStyleString(ClassLoader classLoader, Class objectClass, Object propertyValue);
}
