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
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.servermessage.DomUpdate;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Element;

import echopointng.ColorChooser;
import echopointng.EPNG;
import echopointng.model.ColorSwatchModel;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.RenderingContext;
import echopointng.util.ColorKit;

/**
 * <code>ComponentSynchronizePeer</code> implementation for the
 * <code>ColorChooser</code> component.  ColorChooser is a pure client side
 * implemented component.
 */
public class ColorChooserPeer implements ComponentSynchronizePeer, ActionProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service CC_SERVICE = JavaScriptService.forResource("EPNG.ColorChooser", "/echopointng/ui/resource/js/colorchooser.js");

	static {
		WebRenderServlet.getServiceRegistry().add(CC_SERVICE);
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	public String getContainerId(Component component) {
		throw new UnsupportedOperationException("Component does not support children.");
	}
	
	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance, nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
    	String name = actionElement.getAttribute(ActionProcessor.ACTION_NAME);
    	String value = actionElement.getAttribute(ActionProcessor.ACTION_VALUE);
        ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component,name,value);
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderAdd(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(CC_SERVICE.getId());
		renderInitDirective(new RenderingContext(rc,update,component), targetId, component);
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(CC_SERVICE.getId());
		renderDisposeDirective(rc, component);
	}

	/**
	 * Renders a dispose directive.
	 */
	private void renderDisposeDirective(RenderContext rc, Component component) {
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPColorChooser.MessageProcessor",
				"dispose", new String[0], new String[0]);
		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}
	
	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		// Perform full update.
		DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
		renderAdd(rc, update, targetId, update.getParent());
		return true;
	}
	
	/**
	 * Renders an initialization directive.
	 */
	private void renderInitDirective(RenderingContext rc, String containerId, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);
		
		String elementId = ContainerInstance.getElementId(component);
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemizedUpdateElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_UPDATE, "EPColorChooser.MessageProcessor", "init");
		
		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemizedUpdateElement.appendChild(itemElement);
		
		itemElement.setAttribute("eid", elementId);
		itemElement.setAttribute("container-eid", containerId);
		itemElement.setAttribute("swatchesPerRow", String.valueOf(rc.getRP(ColorChooser.PROPERTY_SWATCHES_PER_ROW,fallbackStyle,17)));
		itemElement.setAttribute("enabled", String.valueOf(component.isRenderEnabled()));
		itemElement.setAttribute("serverNotify", String.valueOf(true));
		
		ColorChooser chooser = (ColorChooser) component;
		Color currentColor = chooser.getSelectedColor();
		if (currentColor != null) {
			itemElement.setAttribute("currentColorSelection", ColorKit.makeCSSColor(currentColor));
		}
		itemElement.setAttribute("showCurrentColorSelectionSwatch", String.valueOf(true));
		itemElement.setAttribute("currentColorSelectionText", "Current Selection : ");
		
		
		// tell em the colors to use
		ColorSwatchModel swatchModel = (ColorSwatchModel) rc.getRP(ColorChooser.PROPERTY_SWATCH_MODEL);
		if (swatchModel != null) {
			Color[] colors = swatchModel.getColorSwatches();
			if (colors == null) {
				colors = new Color[0];
			}
			StringBuffer sbColors = new StringBuffer();
			StringBuffer sbColorTitles = new StringBuffer();
			for (int i = 0; i < colors.length; i++) {
				Color color = colors[i];
				String colorStr = ColorKit.makeCSSColor(color);
				sbColors.append(colorStr);
				String desc = swatchModel.getColorDescription(color);
				sbColorTitles.append((desc != null ? desc : colorStr));
				if (i < colors.length-1) {
					sbColors.append('|');
					sbColorTitles.append('|');
				}
			}
			itemElement.setAttribute("colors",sbColors.toString());
			itemElement.setAttribute("colorTitles",sbColorTitles.toString());
		} else {
			itemElement.setAttribute("colors","");
			itemElement.setAttribute("colorTitles","");
		}
		
		
				
		
		// default style
		CssStyleEx style = new CssStyleEx(component,fallbackStyle);
		itemElement.setAttribute("styleDefault",style.renderInline());
		
		// swatch style
		style = new CssStyleEx();
		ExtentRender.renderToStyle(style,"width",(Extent) rc.getRP(ColorChooser.PROPERTY_SWATCH_WIDTH,fallbackStyle));
		ExtentRender.renderToStyle(style,"height",(Extent) rc.getRP(ColorChooser.PROPERTY_SWATCH_HEIGHT,fallbackStyle));
		style.setAttribute("border","1px solid #000000;");
		itemElement.setAttribute("styleSwatch",style.renderInline());
		
		
	}

}