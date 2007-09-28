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

import nextapp.echo2.app.Component;

/**
 * <code>HttpPaneEx</code> is a Pane that can show other HTTP content inside
 * itself.
 * <p>
 * Like <code>ContentPane</code>, this component fills 100% of its parent and
 * its parent must implement <code>PaneContainer</code>.
 * <p>
 * Tihs component does NOT allow any child components to be added to it.
 */
public class HttpPaneEx extends ContentPaneEx {

	public static final String PROPERTY_URI = "uri";

	/**
	 * Constructs a <code>HttpPaneEx</code>
	 */
	public HttpPaneEx() {
		this("javascript:void");
	}

	/**
	 * Constructs a <code>HttpPaneEx</code> pointed to the specified URI
	 * 
	 * @param uri -
	 *            the URI to use.
	 */
	public HttpPaneEx(String uri) {
		super();
		setURI(uri);
	}

	/**
	 * Gets the URI being displayed
	 * 
	 * @return the URI being displayed
	 */
	public String getURI() {
		return (String) getProperty(PROPERTY_URI);
	}

	/**
	 * Sets the URI to display
	 * 
	 * @param newValue -
	 *            the new URI to use
	 */
	public void setURI(String newValue) {
		setProperty(PROPERTY_URI, newValue);
	}

	/**
	 * No children are allowed to be added to <code>HttpPane</code>
	 * 
	 * @see echopointng.ContentPaneEx#isValidChild(nextapp.echo2.app.Component)
	 */
	public boolean isValidChild(Component child) {
		return false;
	}
}
