package echopointng.able;
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
import nextapp.echo2.app.Color;;

/**
 * A <code>Scrollable</code> is a component that can have a a fixed width 
 * and/or height, and can present scrollbars when the content of 
 * the component is too large to fit inside.
 * <p>
 * <h3>SCROLLBARS</h3>
 *
 * Since a <code>Scrollable</code> can be made a fixed size, by setting 
 * its width and/or height properties, it has support for
 * a scroll bar policy which controls how scroll bars are used if the content 
 * of the component will not fit into the fixed size.
 * <p>
 * If the scroll bar policy is NEVER, then no scroll bars will be shown 
 * and the content inside the component will be clipped to the bounding rectangle.
 * <p>
 * If the scroll bar policy is ALWAYS, then scroll bars will always be 
 * shown, regardless of whether the content is too big for the bounding rectangle, 
 * which allows the user to view all the content.
 * <p>
 * If the scroll bar policy is AUTO, then scroll bars will be 
 * shown when appropriate, ie when the content is too big for the bounding 
 * rectangle.
 */
public interface Scrollable extends Sizeable {

	/**
	 * A scroll bar policy that will cause not cause any scroll bar
	 * policy to be applied at all.  Its as if no scroll bar policy
	 * is in place.
	 */
	public static final int UNDEFINED = 0;

	/**
	 * A scroll bar policy that will cause scroll bars to never appear,
	 * without regard for whether they are required. Content is 
	 * never clipped even if its to large for the components dimensions.
	 */
	public static final int NEVER = 1;
	/**
	  * A scroll bar policy that will cause scroll bars to always appear, 
	  * without regard for whether they are required.
	  */
 	public static final int ALWAYS = 2;
	/**
	 * A scroll bar policy that will cause scroll bars to be visible if they 
	 * are necessary, and invisible if they are not.
	 */
 	public static final int AUTO = 4;
 	
	/**
	 * A scroll bar policy that will cause scroll bars to never appear,
	 * without regard for whether they are required. Content will
	 * always be clipped to the components dimensions and the
	 * scollbars are hidden.
	 */
	public static final int CLIPHIDE = 8;
 	
	

	public static final String PROPERTY_SCROLL_BAR_POLICY = "scrollBarPolicy";
	public static final String PROPERTY_SCROLL_BAR_BASE_COLOR = "scrollBarBaseColor";
	public static final String PROPERTY_SCROLL_BAR_PROPERTIES = "scrollBarProperties";
	
	/**
	 * Returns the ScrollBarPolicy in place
	 * 
	 * This can be one of :
	 * <ul>
	 * <li>NONE</li>
	 * <li>ALWAYS</li>
	 * <li>AUTO</li>
	 * <li>CLIPHIDE</li>
	 * </ul>
	 */
	public int getScrollBarPolicy();

	/**
	 * Returns the base color of the ScrollBarProperties associated with this <code>Scrollable</code>
	 * @return the base color of the ScrollBarProperties associated with this <code>Scrollable</code>
	 */
	public Color getScrollBarBaseColor();
	
	/**
	 * Returns the ScrollBarProperties associated with this <code>Scrollable</code>
	 * @return the ScrollBarProperties associated with this <code>Scrollable</code>
	 */
	public ScrollBarProperties getScrollBarProperties();
	
	/**
	 * Sets the scroll bar policy of the component
	 * 
	 * This can be one of :
	 * <ul>
	 * <li>SCOLLBARS_NONE</li>
	 * <li>SCOLLBARS_ALWAYS</li>
	 * <li>SCOLLBARS_AUTO</li>
	 * <li>CLIPHIDE</li>
	 * </ul>
	 */
	public void setScrollBarPolicy(int newScrollBarPolicy);

	/**
	 * Sets the base color of the ScrollBarProperties associated with this <code>Scrollable</code>.
	 * If no  ScrollBarProperties is available, then a new one should be created.
	 *
	 * @param newValue - the new base color of ScrollBarProperties to use
	 */
	public void setScrollBarBaseColor(Color newValue);
	
	/**
	 * Sets the ScrollBarProperties associated with this <code>Scrollable</code>
	 * @param newValue - the new ScrollBarProperties to use
	 */
	public void setScrollBarProperties(ScrollBarProperties newValue);
	
}
