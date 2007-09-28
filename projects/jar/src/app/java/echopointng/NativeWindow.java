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

import echopointng.able.Sizeable;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;

/**
 * <code>NativeWindow</code> represents a component that can show cotent
 * outside the Echo2 framework via a URL. It performs the same function as
 * <code>BrowserOpenWindowCommand</code> except it does it in a component managed way as well
 * as providing dependency and modality.
 * 
 */
public class NativeWindow extends Component implements Sizeable {

	/**
	 * Shows a web addresslocation feature when the <code>NativeWindow</code>
	 * is first opened
	 */
	public static final int FEATURE_LOCATION = 1;

	/**
	 * Shows allows scrollbars when the <code>NativeWindow</code> is first
	 * opened
	 */
	public static final int FEATURE_SCROLLBARS = 2;

	/**
	 * Shows a status bar area feature when the <code>NativeWindow</code> is
	 * first opened
	 */
	public static final int FEATURE_STATUSBAR = 4;

	/**
	 * Shows a toolbar feature when the <code>NativeWindow</code> is first
	 * opened
	 */
	public static final int FEATURE_TOOLBAR = 8;

	/**
	 * Determines of the window is resizeable <code>NativeWindow</code> when
	 * it is first opened
	 */
	public static final int FEATURE_RESIZEABLE = 16;

	/**
	 * Shows a menu bar area feature when the <code>NativeWindow</code> is
	 * first opened
	 */
	public static final int FEATURE_MENUBAR = 32;

	public static final String PROPERTY_CENTERED = "centered";

	public static final String PROPERTY_DEPENDENT = "dependent";

	public static final String PROPERTY_FEATURES = "features";

	public static final String PROPERTY_LEFT = "left";

	public static final String PROPERTY_MODAL = "modal";

	public static final String PROPERTY_OPEN = "open";

	public static final String PROPERTY_TOP = "top";

	public static final String PROPERTY_URL = "URL";

	/**
	 * Constructs a <code>NativeWindow</code> that is closed by default
	 */
	public NativeWindow() {
	}

	/**
	 * Constructs a <code>NativeWindow</code> with the specified URL. It is
	 * not open by default.
	 * 
	 * @param url -
	 *            the URL to use
	 */
	public NativeWindow(String url) {
		setURL(url);
	}

	/**
	 * Constructs a <code>NativeWindow</code> with the specified URL and open
	 * flag.
	 * 
	 * @param url -
	 *            the URL to use
	 * @param isOpen -
	 *            whether the <code>NativeWindow</code> is shown as opened or
	 *            not.
	 */
	public NativeWindow(String url, boolean isOpen) {
		setOpen(isOpen);
		setURL(url);
	}

	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		super.processInput(inputName, inputValue);
		if (PROPERTY_OPEN.equals(inputName)) {
			setOpen("true".equals(inputValue));
		}
	}

	public int getFeatures() {
		return ComponentEx.getProperty(this, PROPERTY_FEATURES, 0);
	}

	/**
	 * @see echopointng.able.Heightable#getHeight()
	 */
	public Extent getHeight() {
		return (Extent) getProperty(PROPERTY_HEIGHT);
	}

	public Extent getLeft() {
		return (Extent) getProperty(PROPERTY_LEFT);
	}

	public Extent getTop() {
		return (Extent) getProperty(PROPERTY_TOP);
	}

	public String getURL() {
		return (String) ComponentEx.getProperty(this, PROPERTY_URL);
	}

	/**
	 * @see echopointng.able.Widthable#getWidth()
	 */
	public Extent getWidth() {
		return (Extent) getProperty(PROPERTY_WIDTH);
	}

	public boolean isDependent() {
		return ComponentEx.getProperty(this, PROPERTY_DEPENDENT, false);
	}

	public boolean isModal() {
		return ComponentEx.getProperty(this, PROPERTY_MODAL, false);
	}

	public boolean isOpen() {
		return ComponentEx.getProperty(this, PROPERTY_OPEN, false);
	}

	public boolean isCentered() {
		return ComponentEx.getProperty(this, PROPERTY_CENTERED, true);
	}

	/**
	 * Always returns false. Children are not allowed.
	 * 
	 * @see nextapp.echo2.app.Component#isValidChild(nextapp.echo2.app.Component)
	 */
	public boolean isValidChild(Component child) {
		return false;
	}

	public void setDependent(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_DEPENDENT, newValue);
	}

	/**
	 * This specifies a series of <code>NativeWindow</code> features, that
	 * will be used when the window is opened.
	 * 
	 * @param newValue -
	 *            the new set of features which can be a combination of the
	 *            following values
	 *            <ul>
	 *            <li>TODO - fill this in</li>
	 *            </ul>
	 * 
	 */
	public void setFeatures(int newValue) {
		ComponentEx.setProperty(this, PROPERTY_FEATURES, newValue);
	}

	/**
	 * @see echopointng.able.Heightable#setHeight(nextapp.echo2.app.Extent)
	 */
	public void setHeight(Extent newValue) {
		Extent.validate(newValue, Extent.PX);
		setProperty(PROPERTY_HEIGHT, newValue);
	}

	public void setLeft(Extent newValue) {
		Extent.validate(newValue, Extent.PX);
		setProperty(PROPERTY_LEFT, newValue);
	}

	public void setModal(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_MODAL, newValue);
	}

	public void setCentered(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_CENTERED, newValue);
	}

	public void setOpen(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_OPEN, newValue);
	}

	public void setTop(Extent newValue) {
		Extent.validate(newValue, Extent.PX);
		setProperty(PROPERTY_TOP, newValue);
	}

	public void setURL(String newValue) {
		ComponentEx.setProperty(this, PROPERTY_URL, newValue);
	}

	/**
	 * @see echopointng.able.Widthable#setWidth(nextapp.echo2.app.Extent)
	 */
	public void setWidth(Extent newValue) {
		Extent.validate(newValue, Extent.PX);
		setProperty(PROPERTY_WIDTH, newValue);
	}

}
