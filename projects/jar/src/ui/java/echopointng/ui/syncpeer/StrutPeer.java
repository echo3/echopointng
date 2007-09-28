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

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Style;
import nextapp.echo2.webrender.output.CssStyle;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.ui.util.LayoutStrut;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

/** 
 * <code>StrutPeer</code> is the peer class for <code>Strut</code>
 */

public class StrutPeer extends AbstractEchoPointPeer {
	
	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext, org.w3c.dom.Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parentNode, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);
		
	   	String uri = rc.getContainerInstance().getServiceUri(LayoutStrut.PIXEL_SERVICE);
    	Element imgE = rc.getServerMessage().getDocument().createElement("img");
        imgE.setAttribute("src", uri);
        
        CssStyle cssStyle = new CssStyle();
        Render.asComponent(cssStyle,component, fallbackStyle);
        
		imgE.setAttribute("id",rc.getElementId());
        imgE.setAttribute("style", cssStyle.renderInline());
        rc.addStandardWebSupport(imgE);
    	parentNode.appendChild(imgE);
	}

}
