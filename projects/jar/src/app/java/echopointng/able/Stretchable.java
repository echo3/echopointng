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

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;

/**
 * The <code>{@link Stretchable}</code> interface is used to indicate that a
 * <code>{@link Component}</code> should "stretch" itself to use all left over
 * space inside a parent component.
 * <p>
 * By default most component try to minimize the space they use, but components
 * that implement this interface can be made to maximize the space they use.
 */
public interface Stretchable extends Delegateable {

	public static final String PROPERTY_HEIGHT_STRETCHED = "heightStretched";

	public static final String PROPERTY_MINIMUM_STRETCHED_HEIGHT = "minimumStretchedHeight";

	public static final String PROPERTY_MAXIMUM_STRETCHED_HEIGHT = "maximumStretchedHeight";

	/**
	 * @return true if the height should be stretched to use all available space
	 *         of its parent.
	 */
	public boolean isHeightStretched();

	/**
	 * Set to true if the height should be stretched to use all available space
	 * of its parent.
	 * 
	 * @param newValue -
	 *            a boolean flag indicating whether the height should be
	 *            stretched to use all available space of its parent or not.
	 */
	public void setHeightStretched(boolean newValue);

	/**
	 * @return - the minimum height that the component should stretch itself
	 *         down to in pixels.
	 */
	public Extent getMinimumStretchedHeight();

	/**
	 * Sets the minimum height that the component should stretch itself down to
	 * in pixels.
	 * 
	 * @param newValue -
	 *            a new Extent value that MUST be in pixel units.
	 */
	public void setMinimumStretchedHeight(Extent newValue);

	/**
	 * @return - the maximum height that the component should stretch itself up
	 *         to in pixels.
	 */
	public Extent getMaximumStretchedHeight();

	/**
	 * Sets the maximum height that the component should stretch itself up to in
	 * pixels.
	 * 
	 * @param newValue -
	 *            a new Extent value that MUST be in pixel units.
	 */
	public void setMaximumStretchedHeight(Extent newValue);

}
