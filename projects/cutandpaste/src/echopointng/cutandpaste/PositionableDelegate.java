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
package echopointng.cutandpaste;

import echopointng.able.Positionable;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;

/** 
 * <code>PositionableDelegate</code> implements <code>Positionable</code>
 */

public class PositionableDelegate extends AbstractComponentDelegate implements Positionable {

	public PositionableDelegate(Component delegatee) {
		super(delegatee);
	}
	
	/**
	 * @see echopointng.able.Positionable#clearPositioning()
	 */
	public void clearPositioning() {
		setzIndex(Integer.MIN_VALUE);
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
		return getProperty(PROPERTY_POSITIONING,STATIC);
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
	 * @see echopointng.able.Positionable#getzIndex()
	 */
	public int getzIndex() {
		return getProperty(PROPERTY_ZINDEX,Integer.MIN_VALUE);
	}

	/**
	 * @see echopointng.able.Positionable#isPositioned()
	 */
	public boolean isPositioned() {
		return getProperty(PROPERTY_POSITIONING,false);
	}

	/**
	 * @see echopointng.able.Positionable#setBottom(nextapp.echo2.app.Extent)
	 */
	public void setBottom(Extent newValue) {
		setProperty(PROPERTY_BOTTOM,newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setLeft(nextapp.echo2.app.Extent)
	 */
	public void setLeft(Extent newValue) {
		setProperty(PROPERTY_LEFT,newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setPosition(int)
	 */
	public void setPosition(int newPositioning) {
		setProperty(PROPERTY_POSITIONING,newPositioning);
	}

	/**
	 * @see echopointng.able.Positionable#setRight(nextapp.echo2.app.Extent)
	 */
	public void setRight(Extent newValue) {
		setProperty(PROPERTY_RIGHT,newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setTop(nextapp.echo2.app.Extent)
	 */
	public void setTop(Extent newValue) {
		setProperty(PROPERTY_TOP,newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setzIndex(int)
	 */
	public void setzIndex(int newValue) {
		setProperty(PROPERTY_ZINDEX,newValue);
	}
}
