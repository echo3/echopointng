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

import nextapp.echo2.app.Insets;

/**
 * The <code>Insetable</code> interface is used to set extra space inside and
 * outside a component. The 'Insets' is the extra space around the content of a
 * component, while the 'Outsets' is the extra space around the outside of a
 * component.
 */
public interface Insetable extends Delegateable {

	/**
	 * A default insets object that has zero-pixel sizes.
	 */
	public static final Insets DEFAULT_INSETS = new Insets(0);

	/**
	 * A default outsets object that has zero-pixel sizes.
	 */
	public static final Insets DEFAULT_OUTSETS = new Insets(0);

	public static final String PROPERTY_INSETS = "insets";

	public static final String PROPERTY_OUTSETS = "outsets";

	/**
	 * @return the Insets in use or null if here are none
	 */
	public Insets getInsets();

	/**
	 * @return the Outsets in use or null if here are none
	 */
	public Insets getOutsets();

	/**
	 * Sets the Insets in play. The Insets control the extra space around the
	 * content of a container.
	 * 
	 * @param newValue -
	 *            the Insets to use
	 */
	public void setInsets(Insets newValue);

	/**
	 * Sets the Outsets in play. The Outsets control the extra space around the
	 * outside of a container.
	 * 
	 * @param newValue -
	 *            the Ousets to use
	 */
	public void setOutsets(Insets newValue);
}
