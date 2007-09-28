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
package echopointng;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.RadioButton;
import echopointng.able.Attributeable;
import echopointng.command.AttributesAdd;

/** 
 * <code>RadioButtonEx</code> 
 */

public class RadioButtonEx extends RadioButton implements Attributeable {

	private Map attributeMap;

	/**
	 * Creates a radio button with no text or icon.
	 */
	public RadioButtonEx() {
	    this(null, null);
	}

	/**
	 * Creates a radio button with an icon.
	 *
	 * @param icon the icon to be displayed in the radio button
	 */
	public RadioButtonEx(ImageReference icon) {
	    this(null, icon);
	}

	/**
	 * Creates a radio button with text.
	 *
	 * @param text the text to be displayed in the radio button
	 */
	public RadioButtonEx(String text) {
	    this(text, null);
	}

	/**
	 * Creates a radio button with text and an icon.
	 *
	 * @param text the text to be displayed in the radio button
	 * @param icon the icon to be displayed in the radio button
	 */
	public RadioButtonEx(String text, ImageReference icon) {
	    super(text,icon);
	}

	/**
	 * @see nextapp.echo2.app.Component#init()
	 */
	public void init() {
		// we implement Attributeable via a Command so we dont need peers
		// that are not owned by EPNG.
		super.init();
		getApplicationInstance().enqueueCommand(new AttributesAdd(this,this));
	}
	
	/**
	 * @see echopointng.able.Attributeable#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String attributeName) {
		if (attributeMap != null) {
			return attributeMap.get(attributeName);
		}
		return null;
	}

	/**
	 * @see echopointng.able.Attributeable#getAttributeNames()
	 */
	public String[] getAttributeNames() {
		if (attributeMap == null) {
			return new String[0];
		}
		int count = 0;
		String[] attributeNames = new String[attributeMap.keySet().size()];
		for (Iterator iter = attributeMap.keySet().iterator(); iter.hasNext();) {
			attributeNames[count++] = (String) iter.next();
		}
		return attributeNames;
	}

	/**
	 * @see echopointng.able.Attributeable#setAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setAttribute(String attributeName, Object attributeValue) {
		if (attributeMap == null) {
			attributeMap = new HashMap();
		}
		attributeMap.put(attributeName, attributeValue);
	}

}
