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
package echopointng.ui.syncpeer;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Style;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.Separator;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.LayoutStrut;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

/** 
 * <code>SeparatorPeer</code> 
 */

public class SeparatorPeer extends AbstractEchoPointPeer {

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext, Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);
		
		CssStyleEx style;
		
		Element outer = rc.createE("div");
		parent.appendChild(outer);
		outer.setAttribute("id",rc.getElementId());
		rc.addStandardWebSupport(outer);
		
		style = new CssStyleEx();
		Render.asComponent(style,component,fallbackStyle);
		outer.setAttribute("style",style.renderInline());
		
		Extent extent;
		//
		// top part of separator
		Element topDiv = rc.createE(("div"));
		outer.appendChild(topDiv);
		
		style = new CssStyleEx();
		style.setBackground((Color) rc.getRP(Separator.PROPERTY_TOP_COLOR, fallbackStyle));
		extent = (Extent) rc.getRP(Separator.PROPERTY_TOP_SIZE, fallbackStyle);
		if (extent != null) {
			style.setAttribute("height",String.valueOf(extent));
			topDiv.appendChild(LayoutStrut.createStrut(rc,null,extent));
		}
		topDiv.setAttribute("style",style.renderInline());
		//
		// bottom part of separator
		Element bottomDiv = rc.createE(("div"));
		outer.appendChild(bottomDiv);
		style = new CssStyleEx();
		style.setBackground((Color) rc.getRP(Separator.PROPERTY_BOTTOM_COLOR, fallbackStyle));
		extent = (Extent) rc.getRP(Separator.PROPERTY_BOTTOM_SIZE, fallbackStyle);
		if (extent != null) {
			style.setAttribute("height",String.valueOf(extent));
			bottomDiv.appendChild(LayoutStrut.createStrut(rc,null,extent));
		}
		bottomDiv.setAttribute("style",style.renderInline());
	}
}
