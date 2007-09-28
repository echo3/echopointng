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
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;
import echopointng.model.DefaultMenuItemModel;

/**
 * The <code>MenuItem</code> class provides a <code>ButtonEx</code>
 * that can be added to a <code>Menu</code>.  If it is not contained
 * within a <code>Menu</code>, then it is rendered just like
 * a <code>ButtonEx</code> 
 * <br>
 * @author Brad Baker 
 */
public class MenuItem extends ButtonEx  {

	/**
	 * This DEFAULT_STYLE is applied to the MenuItem to give it a series of borders and background colors
	 */
	public static Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx(ButtonEx.DEFAULT_STYLE);
		style.setProperty(PROPERTY_FOREGROUND,Color.BLACK);
		DEFAULT_STYLE  = style;
	}	
	
	/**
	 * Creates a <code>MenuItem</code> with no text or icon.
	 */
	public MenuItem() {
		this(null, null);
	}
	/**
	 * Creates a <code>MenuItem</code> with text.
	 *
	 * @param text A text label to display in the <code>MenuItem</code>.
	 */
	public MenuItem(String text) {
		this(text, null);
	}
	/**
	 * Creates a <code>MenuItem</code> with an icon.
	 *
	 * @param icon An icon to display in the <code>MenuItem</code>.
	 */	
	public MenuItem(ImageReference icon) {
		this(null, icon);
	}
	/**
	 * Creates a <code>MenuItem</code> with text and an icon.
	 *
	 * @param text A text label to display in the <code>MenuItem</code>.
	 * @param icon An icon to display in the <code>MenuItem</code>.
	 */
	public MenuItem(String text, ImageReference icon) {
		super(text,icon);
		setModel(new DefaultMenuItemModel());
	}
	
	/**
	 * This will traverse up the component hierarchy and return the top
	 * most Menu that contains this MenuItem.  It will return null
	 * if one cant be found.  The top most Menu is considered the
	 * root of the MenuItem.  This can technically be this object.
	 * 
	 * @return the top most Menu or null if it cant be found
	 */
	public Menu getRootMenu() {
		Component p = this;
		Menu topMost = null;
		while (p != null) {
			if (p instanceof Menu) 
				topMost = (Menu) p;
			p = p.getParent();
		}
		return topMost;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("MenuItem");
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
}
