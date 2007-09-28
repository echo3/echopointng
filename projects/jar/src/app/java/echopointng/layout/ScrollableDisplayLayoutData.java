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
package echopointng.layout;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import echopointng.able.ScrollBarProperties;
import echopointng.able.Scrollable;

/** 
 * <code>ScrollableDisplayLayoutData</code> is a specialised form
 * of <code>DisplayLayoutData</code> that allows the scroll bar
 * properties to be specified for a child component.
 * 
 * @see echopointng.layout.DisplayLayoutData
 */

public class ScrollableDisplayLayoutData extends DisplayLayoutData implements Scrollable {

	/**
	 * Constructs a empty <code>ScrollableDisplayLayoutData</code>
	 */
	public ScrollableDisplayLayoutData() {}
	
	/**
	 * Constructs a <code>ScrollableDisplayLayoutData</code> with the left and top parameters set and
	 * set the position to <code>Positionable.ABSOLUTE</code>
	 * @param left - the left position value
	 * @param top - the top position value
	 */
	public ScrollableDisplayLayoutData(Extent left, Extent top) {
		super(left,top);
	}
	
	/**
	 * @see echopointng.able.Scrollable#getScrollBarBaseColor()
	 */
	public Color getScrollBarBaseColor() {
		return (Color) getProperty(PROPERTY_SCROLL_BAR_BASE_COLOR);
	}

	/**
	 * @see echopointng.able.Scrollable#getScrollBarPolicy()
	 */
	public int getScrollBarPolicy() {
		return getProperty(PROPERTY_SCROLL_BAR_POLICY,Scrollable.AUTO);
	}

	/**
	 * @see echopointng.able.Scrollable#getScrollBarProperties()
	 */
	public ScrollBarProperties getScrollBarProperties() {
		return (ScrollBarProperties) getProperty(PROPERTY_SCROLL_BAR_PROPERTIES);
	}

	/**
	 * @see echopointng.able.Scrollable#setScrollBarBaseColor(nextapp.echo2.app.Color)
	 */
	public void setScrollBarBaseColor(Color newValue) {
		setProperty(PROPERTY_SCROLL_BAR_BASE_COLOR,newValue);
	}

	/**
	 * @see echopointng.able.Scrollable#setScrollBarPolicy(int)
	 */
	public void setScrollBarPolicy(int newScrollBarPolicy) {
		setProperty(PROPERTY_SCROLL_BAR_POLICY,newScrollBarPolicy);
	}

	/**
	 * @see echopointng.able.Scrollable#setScrollBarProperties(echopointng.able.ScrollBarProperties)
	 */
	public void setScrollBarProperties(ScrollBarProperties newValue) {
		setProperty(PROPERTY_SCROLL_BAR_PROPERTIES,newValue);
	}

}
