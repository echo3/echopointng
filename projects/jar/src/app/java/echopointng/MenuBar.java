package echopointng;

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

import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Style;
import echopointng.able.Insetable;
import echopointng.able.Positionable;

/**
 * 
 * The <code>MenuBar</code> class provides a <code>Component</code> that
 * contains other <code>Menu</code> objects and <code>MenuItem</code>
 * objects. Its underlying <code>ButtonEx</code> text and icon properties are
 * never rendered.
 * <p>
 * The styleChildren property can be used to help ensure consistent visual
 * properties between parent and child Menu's. When this is true, the visual
 * properties of the parent Menu will be transfered to the child
 * <code>Menu</code> or <code>MenuItem.</code>
 * 
 * @author Brad Baker
 */

public class MenuBar extends Menu implements Positionable {

	/**
	 * This DEFAULT_STYLE is applied to the MenuBar to give it a series of
	 * borders and background colors
	 */
	public static Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx(Menu.DEFAULT_STYLE);
		style.setProperty(PROPERTY_HORIZONTAL, true);

		//style.setProperty(PROPERTY_BACKGROUND,ColorKit.makeColor("#D4D0C8"));

		//style.setProperty(Borderable.PROPERTY_BORDER,new
		// Border(1,ColorKit.makeColor("#CECFCE"),Border.STYLE_SOLID));
		//style.setProperty(PROPERTY_ROLLOVER_BORDER,new
		// Border(1,ColorKit.makeColor("#3169C6"),Border.STYLE_SOLID));

		style.setProperty(Insetable.PROPERTY_INSETS, new Insets(1));
		style.setProperty(Insetable.PROPERTY_OUTSETS, new Insets(1));

		DEFAULT_STYLE = style;
	}

	/**
	 * Constructs a <code>MenuBar</code>.
	 */
	public MenuBar() {
		super();
	}

	/**
	 * Constructs a <code>MenuBar</code> and adds menuItem as its first child
	 * 
	 * @param menuItem - a first child for the MenuBar.
	 */
	public MenuBar(MenuItem menuItem) {
		super();
		add(menuItem);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("MenuBar");
		if (getId() != null) {
			sb.append(" - ");
			sb.append(getId());
		}
		if (getText() != null) {
			sb.append(" : ");
			sb.append(getText());
		}
		return sb.toString();
	}

	/**
	 * @see echopointng.able.Positionable#clear()
	 */
	public void clear() {
		setZIndex(Integer.MIN_VALUE);
		setPosition(STATIC);
		setLeft(null);
		setRight(null);
		setTop(null);
		setBottom(null);

	}

	/**
	 * @see echopointng.able.Positionable#getBottom()
	 */
	public Extent getBottom() {
		return (Extent) getProperty(PROPERTY_BOTTOM);
	}

	/**
	 * @see echopointng.able.Positionable#getLeft()
	 */
	public Extent getLeft() {
		return (Extent) getProperty(PROPERTY_LEFT);
	}

	/**
	 * @see echopointng.able.Positionable#getPosition()
	 */
	public int getPosition() {
		return ComponentEx.getProperty(this, PROPERTY_POSITION, STATIC);
	}

	/**
	 * @see echopointng.able.Positionable#getRight()
	 */
	public Extent getRight() {
		return (Extent) getProperty(PROPERTY_RIGHT);
	}

	/**
	 * @see echopointng.able.Positionable#getTop()
	 */
	public Extent getTop() {
		return (Extent) getProperty(PROPERTY_TOP);
	}

	/**
	 * @see echopointng.able.Positionable#getZIndex()
	 */
	public int getZIndex() {
		return ComponentEx.getProperty(this, PROPERTY_Z_INDEX, Integer.MIN_VALUE);
	}

	/**
	 * @see echopointng.able.Positionable#isPositioned()
	 */
	public boolean isPositioned() {
		return getPosition() != STATIC;
	}

	/**
	 * @see echopointng.able.Positionable#setBottom(nextapp.echo2.app.Extent)
	 */
	public void setBottom(Extent newValue) {
		setProperty(PROPERTY_BOTTOM, newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setLeft(nextapp.echo2.app.Extent)
	 */
	public void setLeft(Extent newValue) {
		setProperty(PROPERTY_LEFT, newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setPosition(int)
	 */
	public void setPosition(int newPositioning) {
		ComponentEx.setProperty(this, PROPERTY_POSITION, newPositioning);
	}

	/**
	 * @see echopointng.able.Positionable#setRight(nextapp.echo2.app.Extent)
	 */
	public void setRight(Extent newValue) {
		setProperty(PROPERTY_RIGHT, newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setTop(nextapp.echo2.app.Extent)
	 */
	public void setTop(Extent newValue) {
		setProperty(PROPERTY_TOP, newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setZIndex(int)
	 */
	public void setZIndex(int newValue) {
		ComponentEx.setProperty(this, PROPERTY_Z_INDEX, newValue);
	}
}
