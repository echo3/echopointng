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
package echopointng.able;

/** 
 * <code>Attributeable</code> is an interface that allows a Component to have
 * a series of objects associated with attribute names.  This attribute setting
 * follows standard HashMap semantics.
 * <p>
 * The rendering peer code for the Component will also use these attributes
 * to render out this attribute information on the client.
 */

public interface Attributeable {
	
	/**
	 * Returns a String array with the names of the the attributes that have neen set.
	 * 
	 * @return a String array with the names of the the attributes that have neen set.
	 */
	public String[] getAttributeNames();

	/**
	 * Returns a value for the given attribute name or null if one cannot be found.
	 * 
	 * @param attributeName - the name of the attribute to return
	 * 
	 * @return a value for the given attribute name or null if one cannot be found.
	 */
	public Object getAttribute(String attributeName);

	/**
	 * Sets a value for a given attribute name.
	 * 
	 * @param attributeName - the name of the attribute to set
	 * @param attributeValue - the value for the attribute
	 * 
	 */
	public void setAttribute(String attributeName, Object attributeValue);
}

