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
import nextapp.echo2.app.Style;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.ServiceRegistry;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.service.JavaScriptService;
import nextapp.echo2.webrender.service.StaticBinaryService;
import nextapp.echo2.webrender.util.DomUtil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.Slider;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

/**
 * <code>SliderPeer</code> is the peer for <code>SliderPeer</code>
 */
public class SliderPeer extends AbstractEchoPointContainerPeer implements PropertyUpdateProcessor, ActionProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service SLIDER_SERVICE = JavaScriptService.forResource("EPNG.Slider", "/echopointng/ui/resource/js/slider.js");

	private static final Service SLIDER_HORZ_IMAGE = StaticBinaryService.forResource("EPNG.Slider.HorzImage", "image/gif",
			"/echopointng/ui/resource/images/slider_horz_handle.gif");

	private static final Service SLIDER_VERT_IMAGE = StaticBinaryService.forResource("EPNG.Slider.VertImage", "image/gif",
			"/echopointng/ui/resource/images/slider_vert_handle.gif");

	private static final Service SLIDER_HORZ_IMAGE_ROLLOVER = StaticBinaryService.forResource("EPNG.Slider.HorzRolloverImage", "image/gif",
			"/echopointng/ui/resource/images/slider_horz_handle_rollover.gif");

	private static final Service SLIDER_VERT_IMAGE_ROLLOVER = StaticBinaryService.forResource("EPNG.Slider.VertRolloverImage", "image/gif",
			"/echopointng/ui/resource/images/slider_vert_handle_rollover.gif");

	static {
		ServiceRegistry registery = WebRenderServlet.getServiceRegistry();
		registery.add(SLIDER_SERVICE);
		registery.add(SLIDER_HORZ_IMAGE);
		registery.add(SLIDER_VERT_IMAGE);
		registery.add(SLIDER_HORZ_IMAGE_ROLLOVER);
		registery.add(SLIDER_VERT_IMAGE_ROLLOVER);
	}

	public SliderPeer() {
		super();
		partialUpdateManager.add(Slider.VALUE_CHANGED_PROPERTY, new PartialUpdateParticipant() {
			/**
			 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#canRenderProperty(nextapp.echo2.webcontainer.RenderContext,
			 *      nextapp.echo2.app.update.ServerComponentUpdate)
			 */
			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}

			/**
			 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#renderProperty(nextapp.echo2.webcontainer.RenderContext,
			 *      nextapp.echo2.app.update.ServerComponentUpdate)
			 */
			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				Slider slider = (Slider) update.getParent();
				int value = slider.getValue();

				// an XML message directive please to tell the popup to expand!
				Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
						"EPSlider.MessageProcessor", "value", new String[0], new String[0]);
				Element itemElement = rc.getServerMessage().getDocument().createElement("item");
				itemElement.setAttribute("eid", ContainerInstance.getElementId(slider));
				itemElement.setAttribute("value", String.valueOf(value));
				itemizedUpdateElement.appendChild(itemElement);
			}
		});
	}

	/**
	 * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
		String propertyName = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
		if (Slider.VALUE_CHANGED_PROPERTY.equals(propertyName)) {
			String propertyValue = DomUtil.getElementText(propertyElement);
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, Slider.VALUE_CHANGED_PROPERTY,
					Integer.valueOf(propertyValue));
		}
	}
	
	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance, nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
    	String value = actionElement.getAttribute(ActionProcessor.ACTION_VALUE);
        ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component,Slider.VALUE_CHANGED_PROPERTY,Integer.valueOf(value));
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointContainerPeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		return renderUpdateBaseImpl(rc, update, targetId, true);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		Slider slider = (Slider) component;

		createInitDirective(rc, slider, fallbackStyle);

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(Resources.EP_DRAG_SERVICE);
		rc.addLibrary(SLIDER_SERVICE);

		int orientation = rc.getRP(Slider.PROPERTY_ORIENTATION, fallbackStyle, Slider.ORIENTATION_HORIZONTAL);

		String elementId = rc.getElementId();

		//--------------------------
		// Render outer element

		CssStyleEx cssStyle = new CssStyleEx();
		cssStyle.setAttribute("position", "relative");
		// we dont render to standard able stuff as some
		// of it does not apply.
		Render.asWidthable(cssStyle, slider, fallbackStyle);
		Render.asHeightable(cssStyle, slider, fallbackStyle);
		Render.asMouseCursorable(cssStyle, slider, fallbackStyle);

		Element divOuter = rc.createE("div");
		parent.appendChild(divOuter);
		divOuter.setAttribute("id", elementId);
		divOuter.setAttribute("style", cssStyle.renderInline());
		rc.addStandardWebSupport(divOuter);

		//--------------------------
		// Render drag rail
		cssStyle = new CssStyleEx();
		Render.asWidthable(cssStyle, slider, fallbackStyle);
		Render.asHeightable(cssStyle, slider, fallbackStyle);
		Render.asBorderable(cssStyle, slider, fallbackStyle);
		cssStyle.setBackground((Color) rc.getRP(Slider.PROPERTY_BACKGROUND, fallbackStyle));
		cssStyle.setAttribute("position", "absolute");
		if (orientation == Slider.ORIENTATION_HORIZONTAL) {
			cssStyle.setAttribute("bottom", "0px");
		} else {
			cssStyle.setAttribute("left", "0px");
		}
		Render.layoutFix(rc,cssStyle);
		
		Element divRail = rc.createE("div");
		divOuter.appendChild(divRail);
		divRail.setAttribute("id", elementId + "|Rail");
		divRail.setAttribute("style", cssStyle.renderInline());
		// we need some content on IE
		divRail.appendChild(rc.createE("span"));

		//--------------------------
		// Render drag handle
		cssStyle = new CssStyleEx();
		cssStyle.setAttribute("position", "absolute");
		cssStyle.setAttribute("z-index", "1");
		cssStyle.setAttribute("float", "left");
		cssStyle.setAttribute("border", "none");
		String handleURI;
		if (orientation == Slider.ORIENTATION_HORIZONTAL) {
			handleURI = rc.getContainerInstance().getServiceUri(SLIDER_HORZ_IMAGE);
			cssStyle.setAttribute("bottom", "-5px");
			cssStyle.setAttribute("left", "-5px");
			cssStyle.setAttribute("width", "11px");
			cssStyle.setAttribute("height", "21px");
		} else {
			handleURI = rc.getContainerInstance().getServiceUri(SLIDER_VERT_IMAGE);
			cssStyle.setAttribute("left", "-5px");
			cssStyle.setAttribute("top", "-5px");
			cssStyle.setAttribute("width", "21px");
			cssStyle.setAttribute("height", "11px");
		}
		Render.layoutFix(rc,cssStyle);
		
		Element handleE = rc.createE("img");
		divOuter.appendChild(handleE);
		handleE.setAttribute("id", elementId + "|Handle");
		handleE.setAttribute("src", handleURI);
		handleE.setAttribute("style", cssStyle.renderInline());

		//--------------------------
		// Render info area
		cssStyle = new CssStyleEx();
		cssStyle.setAttribute("position", "absolute");
		cssStyle.setAttribute("z-index", "2");
		cssStyle.setAttribute("width", "50px");
		cssStyle.setAttribute("text-align", "center");
		cssStyle.setAttribute("display", "none");
		cssStyle.setAttribute("visibility", "none");
		cssStyle.setAttribute("background-color", "#FFFFE0");
		cssStyle.setAttribute("border", "1px black solid");
		cssStyle.setAttribute("padding", "2px");
		cssStyle.setAttribute("font-size", "8pt");
		Render.layoutFix(rc,cssStyle);

		Element divInfo = rc.createE("div");
		divOuter.appendChild(divInfo);
		divInfo.setAttribute("id", elementId + "|Info");
		divInfo.setAttribute("style", cssStyle.renderInline());
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc, update, component);
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(SLIDER_SERVICE.getId());
		createDisposeDirective(rc.getServerMessage(), component);
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * initialize the state of a ExpandableSection, performing tasks such as
	 * registering event listeners on the client and creating the JS object.
	 */
	protected void createInitDirective(RenderingContext rc, Component component, Style fallbackStyle) {
		Slider slider = (Slider) component;
		int orientation = rc.getRP(Slider.PROPERTY_ORIENTATION, fallbackStyle, Slider.ORIENTATION_HORIZONTAL);

		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPSlider.MessageProcessor",
				"init", new String[0], new String[0]);

		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemElement.setAttribute("eid", rc.getElementId());
		itemElement.setAttribute("enabled", String.valueOf(component.isRenderEnabled()));
		itemElement.setAttribute("min", String.valueOf(slider.getMinimum()));
		itemElement.setAttribute("max", String.valueOf(slider.getMaximum()));

		itemElement.setAttribute("horizontal", String.valueOf(orientation == Slider.ORIENTATION_HORIZONTAL));

		itemElement.setAttribute("value", String.valueOf(slider.getValue()));
		itemElement.setAttribute("valueRatio", String.valueOf(rc.getRP(Slider.PROPERTY_VALUE_RATIO, fallbackStyle, 1.0d)));
		itemElement.setAttribute("valueDecimalPlaces", String.valueOf(rc.getRP(Slider.PROPERTY_VALUE_DECIMAL_PLACES, fallbackStyle, 0)));
		itemElement.setAttribute("immediateNotification", String.valueOf(rc.getRP(Slider.PROPERTY_IMMEDIATE_NOTIFICATION,fallbackStyle,false)));

		String handleURI;
		if (orientation == Slider.ORIENTATION_HORIZONTAL) {
			handleURI = rc.getContainerInstance().getServiceUri(SLIDER_HORZ_IMAGE);
			itemElement.setAttribute("handleURI", handleURI);
			handleURI = rc.getContainerInstance().getServiceUri(SLIDER_HORZ_IMAGE_ROLLOVER);
			itemElement.setAttribute("handleRolloverURI", handleURI);
		} else {
			handleURI = rc.getContainerInstance().getServiceUri(SLIDER_VERT_IMAGE);
			itemElement.setAttribute("handleURI", handleURI);
			handleURI = rc.getContainerInstance().getServiceUri(SLIDER_VERT_IMAGE_ROLLOVER);
			itemElement.setAttribute("handleRolloverURI", handleURI);
		}
		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 *  
	 */
	protected void createDisposeDirective(ServerMessage serverMessage, Component component) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPSlider.MessageProcessor", "dispose",
				new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}
}
