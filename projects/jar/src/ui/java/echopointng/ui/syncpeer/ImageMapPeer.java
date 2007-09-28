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

import java.util.Iterator;
import java.util.Map;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.ImageMap;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.RenderingContext;

/**
 * <code>ImageMapPeer</code>
 */
public class ImageMapPeer extends AbstractEchoPointPeer implements ActionProcessor {

	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		String actionName = actionElement.getAttribute(ActionProcessor.ACTION_NAME);
		String actionValue = actionElement.getAttribute(ActionProcessor.ACTION_VALUE);
		ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, actionName, actionValue);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		ImageReference mapImageRef = (ImageReference) rc.getRP(ImageMap.PROPERTY_IMAGE, fallbackStyle);
		if (mapImageRef == null) {
			return;
		}
		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);

		Element containerE = rc.createE("bdo");
		containerE.setAttribute("id", rc.getElementId());
		parent.appendChild(containerE);

		Element imgE = ImageManager.createImgE(rc, mapImageRef);
		rc.addStandardWebSupport(imgE);
		containerE.appendChild(imgE);

		CssStyleEx style = new CssStyleEx(component, fallbackStyle);
		imgE.setAttribute("style", style.renderInline());
		imgE.setAttribute("usemap", "#" + rc.getElementId() + "Map");

		//
		// now our image map
		Element mapE = rc.createE("map");
		containerE.appendChild(mapE);
		mapE.setAttribute("name", rc.getElementId() + "Map");

		// and an area tag for every co-ordinate set we have
		ImageMap imageMap = (ImageMap) component;
		for (Iterator it = imageMap.getCoords().iterator(); it.hasNext();) {
			Element areaE = rc.createE("area");
			mapE.appendChild(areaE);

			String coordStr = "";
			Map.Entry me = (Map.Entry) it.next();
			ImageMap.Coords coords = (ImageMap.Coords) me.getValue();
			int coordType = coords.getType();
			if (coordType == ImageMap.Coords.CIRCLE) {
				areaE.setAttribute("shape", "circle");
				coordStr = "" + coords.getX() + "," + coords.getY() + "," + coords.getRadius();
			} else if (coordType == ImageMap.Coords.POLYGON) {
				areaE.setAttribute("shape", "poly");
				int[] arr = coords.getPolygonCoords();
				for (int i = 0; i < arr.length; i++) {
					coordStr += arr[i];
					if (i < arr.length - 1)
						coordStr += ",";
				}
			} else {
				areaE.setAttribute("shape", "rect");
				coordStr = "" + coords.getLeft() + "," + coords.getTop() + "," + coords.getRight() + "," + coords.getBottom();
			}
			areaE.setAttribute("coords", coordStr);

			String altText = coords.getAltText();
			if (altText != null) {
				areaE.setAttribute("title", altText);
			}

			String actionCommand = coords.getActionCommand();
			String jsCommand = "javascript:EP.Event.hrefActionHandler('" + rc.getElementId() + "','" + actionCommand + "',null)";

			if (component.isRenderEnabled()) {
				areaE.setAttribute("href", jsCommand);
			} else {
				areaE.setAttribute("nohref", "nohref");
			}
		}
	}
}
