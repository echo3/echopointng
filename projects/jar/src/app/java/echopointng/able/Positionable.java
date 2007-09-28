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

import nextapp.echo2.app.Extent;

/**
 * A <code>Positionable</code> is one that can be postioned anywhere on the
 * screen, regardless of the flow layout of other components.
 * <p>
 * By default the it acts like a normal component, and will be rendered with the
 * flow of its parent and siblings. The component has its Positioning property
 * set to POSITIONING_FLOW.
 * <p>
 * <h3>POSITIONING</h3>
 * 
 * However if the Positioning property is POSITIONING_ABSOLUTE or
 * POSITIONING_RELATIVE then the component will break out of the normal flow
 * layout and position themselves directly on the screen.
 * <p>
 * If the Positioning is POSITIONING_RELATIVE, then the component is positioned
 * at an at a point on the screen relative to its first positioned parent
 * component. If it has no parents that are positioned, then it will be
 * positioned relative to the origins of the client window.
 * <p>
 * If the Positioning is POSITIONING_ABSOLUTE, then the component is positioned
 * at an absolute point outside the normal flow of layout. The left, top, right
 * and bottom properties can be used to position the component.
 * <p>
 * If the Positioning is POSITIONING_FIXED, then the component is positioned at
 * an absolute point from the origin of the client window. The left, top, right
 * and bottom properties can be used to position the component.
 * <p>
 * <h3>Left, Top, Right, Bottom</h3>
 * Typically you would set the Left and Top properties in order to get a
 * Positionable to a specified location. However you can also use the Right and
 * Bottom properties.
 * <p>
 * For example you could position a component to 100 pixels in from the bottom
 * and 10 pixels if from the right by only settting the bottom and right
 * properties to 100 and 10 respectively. The width of the component will be
 * determined by the content.
 * <p>
 * A convenience method called <i>clearPositioning() </i> is provided to clear
 * all positioning and have the component acts like a normal flow component.
 * <p>
 * <h3>Z-INDEX</h3>
 * 
 * A <code>Positionable</code> also supports a z-idex, which controls how it
 * is layered over other components, especially other <code>Positionable</code>
 * 's.
 * <p>
 * If no zIndex is to apply then the Integer.MIN_VALUE can be used in which case
 * no zIndex will be set.
 */
public interface Positionable extends Delegateable {
	/**
	 * The Positionable is a normal Positionable, laid out according to the
	 * normal flow.
	 */
	public static final int STATIC = 1;

	/**
	 * The Positionable's position (and possibly size) is specified with the
	 * 'top', 'right', 'bottom', and 'left' properties. These properties specify
	 * offsets with respect to the Positionable's containing Positionable.
	 * Absolutely positioned Positionables are taken out of the normal flow.
	 * This means they have no impact on the layout of later siblings.
	 */
	public static final int ABSOLUTE = 2;

	/**
	 * The Positionable's position is calculated according to the normal flow.
	 * Then the Positionable is offset relative to its normal position.
	 */
	public static final int RELATIVE = 4;

	/**
	 * The Positionable's position is calculated according to the 'absolute'
	 * model, but in addition, the Positionable is fixed with respect to the
	 * viewport and doesn't move when scrolled.
	 */
	public static final int FIXED = 8;

	public static final String PROPERTY_BOTTOM = "bottom";

	public static final String PROPERTY_LEFT = "left";

	public static final String PROPERTY_POSITION = "position";

	public static final String PROPERTY_RIGHT = "right";

	public static final String PROPERTY_TOP = "top";

	public static final String PROPERTY_Z_INDEX = "zIndex";

	/**
	 * This sets all the positioning attributes (left,top,right,bottom,z-index)
	 * to null or zero.
	 */
	public void clear();

	/**
	 * Returns the bottom Y position of the component
	 */
	public Extent getBottom();

	/**
	 * Returns the left X position of the component
	 */
	public Extent getLeft();

	/**
	 * This can be one of :
	 * <ul>
	 * <li>POSITIONING_STATIC</li>
	 * <li>POSITIONING_RELATIVE</li>
	 * <li>POSITIONING_ABSOLUTE</li>
	 * <li>POSITIONING_FIXED</li>
	 * </ul>
	 */
	public int getPosition();

	/**
	 * Returns the right X position of the component
	 */
	public Extent getRight();

	/**
	 * Returns the top Y position of the component
	 */
	public Extent getTop();

	/**
	 * Returns the z-index of the component
	 */
	public int getZIndex();

	/**
	 * This returns true if any positioning is in place other than 
	 * normal flow ie. STATIC.
	 *  
	 */
	public boolean isPositioned();

	/**
	 * Sets the bottom Y position of the component
	 */
	public void setBottom(Extent newValue);

	/**
	 * Set the left X position of the component
	 */
	public void setLeft(Extent newValue);

	/**
	 * Sets the position of the component
	 * 
	 * This can be one of :
	 * <ul>
	 * <li>POSITIONING_STATIC</li>
	 * <li>POSITIONING_RELATIVE</li>
	 * <li>POSITIONING_ABSOLUTE</li>
	 * <li>POSITIONING_FIXED</li>
	 * </ul>
	 */
	public void setPosition(int newPositioning);

	/**
	 * Sets the right X position of the component
	 */
	public void setRight(Extent newValue);

	/**
	 * Sets the top Y position of the component
	 */
	public void setTop(Extent newValue);

	/**
	 * Sets the z-index of the component
	 */
	public void setZIndex(int newValue);
}
