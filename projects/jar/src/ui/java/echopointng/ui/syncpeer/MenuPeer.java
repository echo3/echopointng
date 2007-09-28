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
import nextapp.echo2.app.button.AbstractButton;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.ClientProperties;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.servermessage.DomUpdate;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.ButtonEx;
import echopointng.EPNG;
import echopointng.Menu;
import echopointng.MenuItem;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.HtmlTable;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

/** 
 * <code>MenuPeer</code> is the peer for <code>Menu</code>
 */
public class MenuPeer extends MenuItemPeer {

	/**
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	public String getContainerId(Component child) {
        String parentId = ContainerInstance.getElementId(child.getParent());
        return parentId + "_contains_" + ContainerInstance.getElementId(child);
	}
	
	
    /**
     * This looks for children with updated layour data or updated properties
     * on the peer's component and if there are any, it removes and
     * redraws the entire peer otherwise it just replaces the children
     * and the redraws them.  
     *  
     * @see nextapp.echo2.webcontainer.SynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        boolean fullReplace = false;
        if (update.hasUpdatedLayoutDataChildren()) {
            fullReplace = true;
        } else if (update.hasUpdatedProperties()) {
            if (partialUpdateManager.canProcess(rc, update)) {
            	partialUpdateManager.process(rc,update);
             } else {
             	fullReplace = true;
             }
        } else if (update.hasAddedChildren()) {
         	fullReplace = true;
        }
        // because we have "reparented" floating Box divs, we need to remove
        // them as well as ourself from the DOM tree.
		if (fullReplace) {
			// Perform full update.
			String elementId = ContainerInstance.getElementId(update.getParent());
			String menuBoxId = elementId + "Box"; 
			DomUpdate.renderElementRemove(rc.getServerMessage(), elementId);
			DomUpdate.renderElementRemove(rc.getServerMessage(), menuBoxId);
			
			renderAdd(rc, update, targetId, update.getParent());
		} else {
			renderUpdateRemoveChildren(rc, update);
			renderUpdateAddChildren(rc, update);
		}
		return fullReplace;
    }
 
	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate, nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc,update,component);
		renderDisposeDirective(rc.getServerMessage(),ContainerInstance.getElementId(component));
	}
    
	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext, Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parentNode, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(MENU_ITEM_SERVICE);
		
		Menu menu = (Menu) component;

		if (menu.getRootMenu() == menu) {
			// we are the root and hence as fixed
			renderRootMenu(rc,parentNode,menu, fallbackStyle);
		} else {
			// we are a menu item with floating drop down box.
			renderMenu(rc,parentNode,menu, fallbackStyle);
		}
	}
	
	/**
	 * This render the Menu that is "fixed" to the page and not floating
	 * like its parent
	 */
	public void renderRootMenu(RenderingContext rc, Node parentNode, Component component, Style fallbackStyle) {
		//-----------------------------------------------
		// talk to our message processor
		//-----------------------------------------------
		createMenuItemInitDirective(rc,component, fallbackStyle);
		
		//-----------------------------------------------
		// render a fixed box of menu items
		//-----------------------------------------------
		CssStyleEx style = new CssStyleEx(component, fallbackStyle);
		Render.asFillImage(style, component,AbstractButton.PROPERTY_BACKGROUND_IMAGE, fallbackStyle,rc);
		if (! component.isRenderEnabled()) {
			Render.asColors(style,component,AbstractButton.PROPERTY_DISABLED_BACKGROUND,ButtonEx.PROPERTY_DISABLED_FOREGROUND, fallbackStyle);
			Render.asFont(style,component,AbstractButton.PROPERTY_DISABLED_FONT, fallbackStyle);
			Render.asBorder(style,component,AbstractButton.PROPERTY_DISABLED_BORDER, fallbackStyle);
			Render.asFillImage(style,component,AbstractButton.PROPERTY_DISABLED_BACKGROUND_IMAGE, fallbackStyle,rc);
		}
		if (rc.getRP(MenuItem.PROPERTY_LINE_WRAP, fallbackStyle, false) == false) {
			style.setAttribute("white-space", "nowrap");
		}
		
		HtmlTable table = new HtmlTable(rc.getDocument(),false);
		table.setAttribute("id",rc.getElementId());
		table.setAttribute("style",style.renderInline());
		rc.addStandardWebSupport(table.getTABLE());
		
		
		int componentBoxCount = 0;
		Component children[] = component.getVisibleComponents();
		for (int i = 0; i < children.length; i++) {
			Component child = children[i];
			
			Element tdContainerE;
			if (rc.getRP(Menu.PROPERTY_HORIZONTAL,fallbackStyle,false)) {
				tdContainerE = table.newTD();
			} else {
				tdContainerE = table.newTR();
			}
			if (child instanceof MenuItem) {
				renderReplaceableChild(rc,rc.getServerComponentUpdate(),tdContainerE,child);
			} else {
				// we need a special component box around the component
				renderInComponentBox(rc, tdContainerE, child, componentBoxCount++);
			}
		}
		parentNode.appendChild(table.getTABLE());
		// the root menu gets an empty menu box
		Element menuBox = renderMenuBox(rc,component,fallbackStyle);
		parentNode.appendChild(menuBox);
	}

	/**
	 * This render the Menu that is "floating" on the page, and acts
	 * as both a menu item and floating menu box
	 */
	public void renderMenu(RenderingContext rc, Node parentNode, Component component, Style fallbackStyle) {
		//----------------------------
		// Render the Menu Item part
		//----------------------------
		Element menuItemContent = renderAsMenuItem(rc,component);
		
		//----------------------------
		// Render the Menu Box part
		//----------------------------
		HtmlTable table = new HtmlTable(rc.getDocument(),false);
	
		int componentBoxCount = 0;
		Component children[] = component.getVisibleComponents();
		int visibleComponentCount = children.length;
		for (int i = 0; i < children.length; i++) {
			Component child = children[i];
			
			Element tdContainerE;
			if (rc.getRP(Menu.PROPERTY_HORIZONTAL,fallbackStyle,false)) {
				tdContainerE = table.newTD();
			} else {
				tdContainerE = table.newTR();
			}
			if (child instanceof MenuItem) {
				renderReplaceableChild(rc,rc.getServerComponentUpdate(),tdContainerE,child);
			} else {
				// we need a special component box around the component
				renderInComponentBox(rc, tdContainerE, child, componentBoxCount++);
			}
		}
		if (visibleComponentCount > 0) {
			Element menuBox = renderMenuBox(rc,component,fallbackStyle);
			menuBox.appendChild(table.getTABLE());
			menuItemContent.appendChild(menuBox);
		}
		// Create Internet Explorer Select-Element blocking IFRAME
		// if needed
		if (rc.getContainerInstance().getClientProperties().getBoolean(ClientProperties.QUIRK_IE_SELECT_Z_INDEX)) {
			addIEIframeTrick(rc, menuItemContent);
		}		
		// the parent contains the menu item 
		parentNode.appendChild(menuItemContent);
	}
	
	protected Element renderMenuBox(RenderingContext rc, Component component, Style fallbackStyle) {
		CssStyleEx style = new CssStyleEx();
		Color color;
		
		color = (Color) rc.getRP(Menu.PROPERTY_MENU_BACKGROUND,fallbackStyle); 
		if (color == null) {
			color = (Color) rc.getRP(Menu.PROPERTY_BACKGROUND,fallbackStyle);
		}
		style.setBackground(color);
		
		color = (Color) rc.getRP(Menu.PROPERTY_MENU_FOREGROUND,fallbackStyle); 
		if (color == null) {
			color = (Color) rc.getRP(Menu.PROPERTY_FOREGROUND,fallbackStyle);
		}
		style.setForeground(color);

		Render.asBorder(style, component, Menu.PROPERTY_MENU_BORDER, fallbackStyle);
		Render.asInsets(style,component,Menu.PROPERTY_MENU_INSETS, "padding", fallbackStyle);
		Render.asInsets(style,component,Menu.PROPERTY_MENU_OUTSETS, "margin", fallbackStyle);
		
		if (rc.getRP(Menu.PROPERTY_MENU_BACKGROUND_IMAGE, fallbackStyle) != null) {
			Render.asFillImage(style,component,Menu.PROPERTY_MENU_BACKGROUND_IMAGE, fallbackStyle,rc);
		}
		style.setAttribute("position","absolute");
		style.setAttribute("visibility","hidden");
		style.setAttribute("display","none");
		Render.layoutFix(rc,style);
		
		Element menuBox = rc.createE("div");
		menuBox.setAttribute("id",rc.getElementId()+"Box");
		menuBox.setAttribute("style",style.renderInline());
		return menuBox;
	}

	/**
	 * Renders a box element around a non MenuItem component child
	 */
	protected void renderInComponentBox(RenderingContext rc, Element parentE, Component child, int componentBoxCount) {
		String componentBoxId = rc.getElementId() + "ComponentBox" + componentBoxCount;
		Element componentBox = rc.createE("div");
		componentBox.setAttribute("id",componentBoxId);
		parentE.appendChild(componentBox);
		renderReplaceableChild(rc,rc.getServerComponentUpdate(),componentBox,child);
		
		createComponentBoxInitDirective(rc,componentBoxId);
	}
	
    /**
     * Renders a directive to the outgoing <code>ServerMessage</code> to 
     * initalising the state of a Menu's component box
     */
	protected void createComponentBoxInitDirective(RenderingContext rc, String componentBoxId) {
        Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
                "EPMI.MessageProcessor", "init",  new String[0], new String[0]);
        
        Element itemElement = rc.getServerMessage().getDocument().createElement("item");
        itemElement.setAttribute("eid", componentBoxId);
        itemElement.setAttribute("etype", "componentBox");
        itemizedUpdateElement.appendChild(itemElement);
	}
	
	private void addIEIframeTrick(RenderingContext rc, Element containerE) {
		String elementId= rc.getElementId();
		Element iframeQuirkIframeElement = rc.createE("iframe");
		iframeQuirkIframeElement.setAttribute("src", "javascript:false;");
		iframeQuirkIframeElement.setAttribute("frameborder", "0");
		iframeQuirkIframeElement.setAttribute("scrolling", "no");
		iframeQuirkIframeElement.setAttribute("width", "100%");
		iframeQuirkIframeElement.setAttribute("height", "100%");
		iframeQuirkIframeElement.setAttribute("style", "filter:progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)");

		CssStyleEx style = new CssStyleEx();
		style.setAttribute("position", "absolute");
		style.setAttribute("visibility", "hidden");
		//style.setAttribute("display", "none");
		style.setAttribute("z-index", "1");
		style.setAttribute("top", "0px");
		style.setAttribute("left", "0px");
		Render.layoutFix(rc,style);

		Element iframeQuirkDiv = rc.createE("div");
		iframeQuirkDiv.appendChild(iframeQuirkIframeElement);
		iframeQuirkDiv.setAttribute("id", elementId + "|IframeQuirk");
		iframeQuirkDiv.setAttribute("style", style.renderInline());

		containerE.appendChild(iframeQuirkDiv);
	}
	
}
