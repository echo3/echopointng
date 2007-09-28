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
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.Style;


/**
 * MenuButton is a specialised form of Menu that
 * looks like a stand alone button but has the drop down
 * Menu Box that can contain other MenuItems and other
 * components.
 */
public class MenuButton extends Menu {

	/** The default MenuButton sub menu image */
	public static final ImageReference DEFAULT_SUBMENU_IMAGE;
	static {
		String imagePath = "/echopointng/resource/images/";
		DEFAULT_SUBMENU_IMAGE = new ResourceImageReference(imagePath + "menu_submenuButton.gif", new Extent(11), new Extent(11));
	}
	
	/**
	 * This DEFAULT_STYLE is applied to the MenuButton to give it a series of borders and background colors
	 */
	public static Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx(Menu.DEFAULT_STYLE);
		style.setProperty(PROPERTY_OUTSETS,null);
		style.setProperty(PROPERTY_DRAW_OUTER_BORDERS,false);
		style.setProperty(PROPERTY_OPEN_OPTION,OPEN_ON_SUBMENU_CLICK);
		style.setProperty(PROPERTY_SUBMENU_IMAGE_BORDERED,false);
		style.setProperty(PROPERTY_SUBMENU_IMAGE,DEFAULT_SUBMENU_IMAGE);
		
		DEFAULT_STYLE = style;
			
	}
	
	/**
	 * Creates a <code>MenuButton</code> with no text or icon.
	 */
	public MenuButton() {
		this(null, null);
	}
	/**
	 * Creates a <code>MenuButton</code> with text.
	 *
	 * @param text A text label to display in the <code>MenuButton</code>.
	 */
	public MenuButton(String text) {
		this(text, null);
	}
	/**
	 * Creates a <code>MenuButton</code> with an icon.
	 *
	 * @param icon An icon to display in the <code>MenuButton</code>.
	 */
	public MenuButton(ImageReference icon) {
		this(null, icon);
	}
	/**
	 * Creates a <code>MenuButton</code> with text and an icon.
	 *
	 * @param text A text label to display in the <code>MenuButton</code>.
	 * @param icon An icon to display in the <code>MenuButton</code>.
	 */
	public MenuButton(String text, ImageReference icon) {
		super(text, icon);
	}
}
