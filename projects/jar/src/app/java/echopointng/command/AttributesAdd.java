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
package echopointng.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nextapp.echo2.app.Command;
import nextapp.echo2.app.Component;
import echopointng.able.Attributeable;

/**
 * The <code>AttributesAdd</code> command is use to dynamically add
 * attributes on the client side to a component
 *
 */
public class AttributesAdd implements Command, Attributeable {
	Map			attributeMap = new HashMap();		
	Component 	component;
	
	/**
	 * Constructs a <code>AttributesAdd</code> that can be applied to the specified component
	 * 
	 * @param component - the component to apply the attributes to
	 * @param attributeable - an Attributeable containg the attributes to be applied.
	 */
	public AttributesAdd(Component component, Attributeable attributeable) {
		if (component == null) {
			throw new IllegalArgumentException("The Component must be non null");
		}
		if (attributeable == null) {
			throw new IllegalArgumentException("The Attributeable must be non null");
		}
		this.component = component;
		String names[] = attributeable.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			attributeMap.put(names[i],attributeable.getAttribute(names[i]));
		}
	}

	/**
	 * Constructs a <code>AttributesAdd</code> that can be applied to the specified component
	 * 
	 * @param component - the component to apply the attributes to
	 * @param attributes - the attributes in a Map.
	 */
	public AttributesAdd(Component component, Map attributes) {
		if (component == null) {
			throw new IllegalArgumentException("The Component must be non null");
		}
		if (attributes == null) {
			throw new IllegalArgumentException("The Map must be non null");
		}
		this.component = component;
		for (Iterator iter = attributes.keySet().iterator(); iter.hasNext();) {
			String name = String.valueOf(iter.next());
			attributeMap.put(name,attributes.get(name));
		}
	}
	
	/**
	 * @return Returns the component.
	 */
	public Component getComponent() {
		return component;
	}
	
	/**
	 * @see echopointng.able.Attributeable#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String attributeName) {
		return attributeMap.get(attributeName);
	}
	
	/**
	 * @see echopointng.able.Attributeable#getAttributeNames()
	 */
	public String[] getAttributeNames() {
		String names[] = new String[attributeMap.size()];
		int i = 0;
		for (Iterator iter = attributeMap.keySet().iterator() ; iter.hasNext(); i++) {
			names[i] = (String) iter.next();
		}
		return names;
	}
	
	/**
	 * @see echopointng.able.Attributeable#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String attributeName, Object attributeValue) {
		attributeMap.put(attributeName,attributeValue);
	}
}
