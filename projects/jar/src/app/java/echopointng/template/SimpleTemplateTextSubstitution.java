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

import java.io.Serializable;
import java.util.HashMap;
/**
 * The class <code>SimpleTemplateTextSubstitution</code> is a simple implementation 
 * of <code>TemplateTextSubstitution</code> that uses a HashMap to substitute named
 * string values.
 * <p>
 * Note it uses the toString() method rather than casting associated value objects 
 * to Strings and hence you can technically use objects other than Strings.
 */
public class SimpleTemplateTextSubstitution implements TemplateTextSubstitution,Serializable {
	HashMap stringMap;
	/**
	 * SimpleTemplateTextSubstitution constructor that creates its own empty HashMap.
	 */
	public SimpleTemplateTextSubstitution() {
		super();
		stringMap = new HashMap();
	}
	/**
	 * SimpleTemplateTextSubstitution constructor that takes a primed HashMap of string mappings.
	 */
	public SimpleTemplateTextSubstitution(HashMap newStringMap) {
		super();
		stringMap = newStringMap;
	}
	/**
	 * Returns the underlying HashMap containing named string.
	 * 
	 * @return java.util.HashMap
	 */
	public java.util.HashMap getStringMap() {
		return stringMap;
	}
	/**
	 * This method is called to return a String that has been associated with the 
	 * given substitution name.
	 */
	public String getSubstitutionText(String substitutionName) {
		Object o = stringMap.get(substitutionName);
		if (o != null)
			return o.toString();
		else
			return null;
	}
	/**
	 * This method associated the given substitution name with a string value.
	 */
	public void put(String substitutionName, Object substitutionValue) {
		stringMap.put(substitutionName, substitutionValue);
	}
	/**
	 * Sets the underlying HashMap of named String values.  99% of the time there is 
	 * no need to call this method.
	 * 
	 * @param newStringMap java.util.HashMap
	 */
	public void setStringMap(java.util.HashMap newStringMap) {
		stringMap = newStringMap;
	}
}
