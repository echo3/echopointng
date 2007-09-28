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

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Style;

/**
 * <code>DropDown</code> is a type of <code>PopUp</code> that (by default)
 * shows its floating box below and to the left of the target component. The
 * user must "click" on the toggle to show the drop down box.
 * <p>
 * <code>DropDown</code> is a convenience class for creating a popup that
 * "drops down" below its associated target component, much like a standard
 * SelectField.
 */
public class DropDown extends PopUp {

	/** The default alignment for the floating box is left/bottom */
	public static final Alignment DEFAULT_ALIGNMENT = new Alignment(Alignment.LEFT, Alignment.BOTTOM);

	/**
	 * This DEFAULT_STYLE is applied to the popup to give it a series of borders and background colors
	 */
	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx(PopUp.DEFAULT_STYLE);
		style.setProperty(PROPERTY_POPUP_ALIGNMENT, DEFAULT_ALIGNMENT);
		style.setProperty(PROPERTY_TOGGLE_ICON,DEFAULT_TOGGLE_ICON);
		style.setProperty(PROPERTY_POPUP_NEXT_TO_TOGGLE,false);
		style.setProperty(PROPERTY_POPUP_ON_ROLLOVER,false);
		DEFAULT_STYLE = style;
	}
	
	/**
	 * Constructs a <code>DropDown</code> with no target component and no drop
	 * down component
	 */
	public DropDown() {
		this(null, null);
	}

	/**
	 * Constructs a <code>DropDown</code> with the specified target and popup
	 * component in place.
	 * 
	 * @param targetComponent -
	 *            the target component of the drop down
	 * @param dropDownComponent -
	 *            the component to be shown in the drop down box
	 */
	public DropDown(Component targetComponent, Component dropDownComponent) {
		super(targetComponent, dropDownComponent);
	}
}
