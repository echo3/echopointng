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

import nextapp.echo2.app.Component;
import nextapp.echo2.app.ContentPane;
import echopointng.able.Attributeable;
import echopointng.able.BackgroundImageable;
import echopointng.able.MouseCursorable;
import echopointng.able.ToolTipable;

/**
 * <code>ContentPaneEx</code> is an extension of <code>ContentPane</code>
 * that allows for <code>DisplayLayoutData</code> and
 * <code>ScrollableDisplayLayoutData</code> be specified by its child
 * components as well as supoprting many of the common EPNG able interfaces
 * such as MouseCursorable and ToolTippable.
 * <p>
 * Like <code>ContentPane</code>, this component fills 100% of its parent and its parent must 
 * implement <code>PaneContainer</code>.
 * <p>
 * This component is useful in that certain Echo2 components such as WindowPane
 * require their parents to be <code>ContentPane</code>'s but
 * <code>ContentPane</code> does not offer as rich functionality as this
 * component.
 * <p>
 * This version of <code>ContentPane</code> also relaxes restriction on having one
 * non <code>FloatingPane</code> child.  Now any number of chilren are allowed.
 * 
 * @see nextapp.echo2.app.ContentPane
 * @see echopointng.layout.DisplayLayoutData
 * @see echopointng.layout.ScrollableDisplayLayoutData
 * @see nextapp.echo2.app.FloatingPane
 */
public class ContentPaneEx extends ContentPane implements BackgroundImageable, MouseCursorable, ToolTipable, Attributeable {

	private Map attributeMap;

	/**
	 * Constructs a <code>ContentPaneEx</code>
	 */
	public ContentPaneEx() {
		super();
		setFocusTraversalParticipant(false);
	}
	
	/**
	 * This relaxes the ContentPane rule about allowing only one non-<code>FloatingPane</code> child.  Any number 
	 * of children are now allowed!
	 * 
	 * @see nextapp.echo2.app.ContentPane#isValidChild(nextapp.echo2.app.Component)
	 */
	public boolean isValidChild(Component child) {
        // relax the rule about having on one non FloatingPane child
        return true;
	}

	/**
	 * @see echopointng.able.MouseCursorable#getMouseCursor()
	 */
	public int getMouseCursor() {
		return ComponentEx.getProperty(this, PROPERTY_MOUSE_CURSOR, CURSOR_AUTO);
	}

	/**
	 * @see echopointng.able.MouseCursorable#getMouseCursorUri()
	 */
	public String getMouseCursorUri() {
		return (String) getProperty(PROPERTY_MOUSE_CURSOR_URI);
	}

	/**
	 * @see echopointng.able.ToolTipable#getToolTipText()
	 */
	public String getToolTipText() {
		return (String) getProperty(PROPERTY_TOOL_TIP_TEXT);
	}

	/**
	 * @see echopointng.able.MouseCursorable#setMouseCursor(int)
	 */
	public void setMouseCursor(int mouseCursor) {
		ComponentEx.setProperty(this, PROPERTY_MOUSE_CURSOR, mouseCursor);
	}

	/**
	 * @see echopointng.able.MouseCursorable#setMouseCursorUri(java.lang.String)
	 */
	public void setMouseCursorUri(String mouseCursorURI) {
		setProperty(PROPERTY_MOUSE_CURSOR_URI, mouseCursorURI);
	}

	/**
	 * @see echopointng.able.ToolTipable#setToolTipText(java.lang.String)
	 */
	public void setToolTipText(String newValue) {
		setProperty(PROPERTY_TOOL_TIP_TEXT, newValue);

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
