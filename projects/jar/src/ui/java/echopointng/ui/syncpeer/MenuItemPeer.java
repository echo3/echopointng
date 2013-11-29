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

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.button.AbstractButton;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.propertyrender.AlignmentRender;
import nextapp.echo2.webcontainer.propertyrender.BorderRender;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import echopointng.ButtonEx;
import echopointng.EPNG;
import echopointng.Menu;
import echopointng.MenuItem;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;
import echopointng.ui.util.TriCellTable;

/**
 * <code>MenuItemPeer</code> is the peer for <code>MenuItem</code>
 */
public class MenuItemPeer extends AbstractEchoPointContainerPeer implements ActionProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service MENU_ITEM_SERVICE = JavaScriptService.forResource("EPNG.MenuItem", "/echopointng/ui/resource/js/menu.js");

	static {
		WebRenderServlet.getServiceRegistry().add(MENU_ITEM_SERVICE);
	}

	/**
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	public String getContainerId(Component child) {
		throw new UnsupportedOperationException("MenuItem does not support children.");
	}

	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, MenuItem.INPUT_CLICK, null);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc,update,component);
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(MENU_ITEM_SERVICE.getId());
		
		renderDisposeDirective(rc.getServerMessage(), ContainerInstance.getElementId(component));
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parentNode, Component component) {
		Element menuItemContent = renderAsMenuItem(rc, component);
		parentNode.appendChild(menuItemContent);
	}

	/**
	 * This will render a MenuItem as, well a MenuItem. It is designed to be
	 * called when rendering a Menu as well as a standard MenuItem.
	 * <p>
	 * This returns the top level element that is rendered
	 */
	public Element renderAsMenuItem(RenderingContext rc, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(MENU_ITEM_SERVICE);

		//-----------------------------------------------
		// build our MenuItem button part
		//-----------------------------------------------
		String elementId = rc.getElementId();
		Node contentNode = null;
		String text = (String) rc.getRP(MenuItem.PROPERTY_TEXT,fallbackStyle);

		Text textNode = text == null ? null : rc.getServerMessage().getDocument().createTextNode(text);
		Element iconElement = null;
		String iconProperyName = component.isRenderEnabled() ?  MenuItem.PROPERTY_ICON : MenuItem.PROPERTY_DISABLED_ICON;
		ImageReference icon = (ImageReference) rc.getRP(iconProperyName,fallbackStyle);
		if (icon != null) {
			iconElement = ImageManager.createImgE(rc,new CssStyle(),icon);
			iconElement.setAttribute("id", elementId + "_Icon");
		}
		int visibleComponentCount = component.getVisibleComponentCount();
		Element subMenuImageElement = null;
		int subMenuImageAlignment = Alignment.RIGHT;
		if (component instanceof Menu) {
			if (visibleComponentCount > 0) {
				icon = (ImageReference) rc.getRP(Menu.PROPERTY_SUBMENU_IMAGE,fallbackStyle);
				if (icon != null) {
					subMenuImageAlignment = rc.getRP(Menu.PROPERTY_SUBMENU_IMAGE_ALIGNMENT, subMenuImageAlignment);
					CssStyleEx submenuStyle = null;
					Border subMenuImageBorder = null;
					if (rc.getRP(Menu.PROPERTY_SUBMENU_IMAGE_BORDERED,fallbackStyle,true)) {
						subMenuImageBorder = (Border) rc.getRP(Menu.PROPERTY_MENU_BORDER,fallbackStyle);
					}
					if (subMenuImageBorder != null) {
						submenuStyle = new CssStyleEx();
						BorderRender.renderToStyle(submenuStyle, subMenuImageBorder);
					}
					subMenuImageElement = ImageManager.createImgE(rc,null,icon);
				}
			}
		}


		Extent iconTextMargin = (Extent) rc.getRP(MenuItem.PROPERTY_ICON_TEXT_MARGIN, fallbackStyle);
		Alignment textPosition = (Alignment) rc.getRP(MenuItem.PROPERTY_TEXT_POSITION, fallbackStyle);
		int triTableOrientation = convertIconTextPositionToOrientation(textPosition, component);

		TriCellTable tct;
		if (subMenuImageElement == null) {
			tct = new TriCellTable(rc.getDocument(), elementId, triTableOrientation, iconTextMargin);
		} else {
			int subMenuOrientation = TriCellTable.LEFT_RIGHT;
			if (subMenuImageAlignment == Alignment.LEFT) {
				subMenuOrientation = TriCellTable.RIGHT_LEFT;
			}
			tct = new TriCellTable(rc.getDocument(), elementId, triTableOrientation, iconTextMargin, subMenuOrientation, iconTextMargin);
		}
		if (textNode != null) {
			renderCellText(rc, tct, textNode, component, 0, fallbackStyle);
		}
		if (iconElement != null) {
			iconElement.setAttribute("id", elementId + "Icon");
			renderCellIcon(tct, iconElement, 1);
		}
		if (subMenuImageElement != null) {
			subMenuImageElement.setAttribute("id", elementId + "SubMenuIMG");
			renderCellIcon(tct, subMenuImageElement, 2);
		}
		contentNode = tct.getTableElement();

		//-----------------------------------------------
		// The Menu Item bit goes into a DIV, which in turn is inside a relative DIV but only if its
		// Menu and not just a MenuItem.
		//-----------------------------------------------
		CssStyleEx baseCssStyle = new CssStyleEx(component, fallbackStyle);
		Render.asFillImage(baseCssStyle, component,AbstractButton.PROPERTY_BACKGROUND_IMAGE, fallbackStyle,rc);
		if (!component.isRenderEnabled()) {
			Render.asColors(baseCssStyle,component,AbstractButton.PROPERTY_DISABLED_BACKGROUND,ButtonEx.PROPERTY_DISABLED_FOREGROUND, fallbackStyle);
			Render.asFont(baseCssStyle,component,AbstractButton.PROPERTY_DISABLED_FONT, fallbackStyle);
			Render.asBorder(baseCssStyle,component,AbstractButton.PROPERTY_DISABLED_BORDER, fallbackStyle);
			Render.asFillImage(baseCssStyle,component,AbstractButton.PROPERTY_DISABLED_BACKGROUND_IMAGE, fallbackStyle,rc);
		}
		if (rc.getRP(MenuItem.PROPERTY_LINE_WRAP, fallbackStyle, false) == false) {
			baseCssStyle.setAttribute("white-space", "nowrap");
		}

		Element menuItemOuterE = rc.createE("div");
		menuItemOuterE.setAttribute("style", baseCssStyle.renderInline());
		AlignmentRender.renderToElement(menuItemOuterE, (Alignment) rc.getRP(MenuItem.PROPERTY_ALIGNMENT));
		rc.addStandardWebSupport(menuItemOuterE);
		
		if (contentNode != null) {
			menuItemOuterE.appendChild(contentNode);
		}
		//-----------------------------------------------
		// talk to our message processor
		//-----------------------------------------------
		createMenuItemInitDirective(rc, component, fallbackStyle);

		if (component instanceof Menu) {
			menuItemOuterE.setAttribute("id", elementId + "MenuItem");

			Element relativeDiv = rc.createE("div");
			relativeDiv.setAttribute("id", elementId);
			relativeDiv.setAttribute("style", "position:relative;padding:0px;margin:0px;");
			relativeDiv.appendChild(menuItemOuterE);

			return relativeDiv;
		} else {
			menuItemOuterE.setAttribute("id", elementId);
			return menuItemOuterE;
		}
	}

	/**
	 * Renders the content of the <code>TriCellTable</code> cell which
	 * contains the menuItem's icon.
	 */
	protected void renderCellIcon(TriCellTable tct, Element iconElement, int cellIndex) {
		Element iconTdElement = tct.getTdElement(cellIndex);
		CssStyle style = new CssStyle();
		style.setAttribute("padding", "0px");
		iconTdElement.setAttribute("style", style.renderInline());
		iconTdElement.appendChild(iconElement);
	}

	/**
	 * Renders the content of the <code>TriCellTable</code> cell which
	 * contains the menuItem's text. Text is always rendered in cell #0 of the
	 * table.
	 */
	protected void renderCellText(RenderingContext rc, TriCellTable tct, Text textNode, Component component, int cellIndex, Style fallbackStyle) {
		Element textTdElement = tct.getTdElement(cellIndex);
		CssStyle textTdCssStyle = new CssStyle();
		textTdCssStyle.setAttribute("padding", "0px");
		AlignmentRender.renderToStyle(textTdCssStyle, (Alignment) rc.getRP(ButtonEx.PROPERTY_TEXT_ALIGNMENT));
		if (rc.getRP(ButtonEx.PROPERTY_LINE_WRAP, fallbackStyle, false) == false) {
			textTdCssStyle.setAttribute("white-space", "nowrap");
		}
		textTdElement.setAttribute("style", textTdCssStyle.renderInline());
		textTdElement.appendChild(textNode);
	}

	/**
	 * Converts Alignment values into TriCellTable values
	 */
	protected int convertIconTextPositionToOrientation(Alignment alignment, Component component) {
		if (alignment == null) {
			return TriCellTable.RIGHT_LEFT;
		}
		if (alignment.getVertical() == Alignment.DEFAULT) {
			if (alignment.getHorizontal() == Alignment.LEFT) {
				return TriCellTable.LEFT_RIGHT;
			} else {
				return TriCellTable.RIGHT_LEFT;
			}
		} else {
			if (alignment.getVertical() == Alignment.TOP) {
				return TriCellTable.TOP_BOTTOM;
			} else {
				return TriCellTable.BOTTOM_TOP;
			}
		}

	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * initialize the state of a MenuItem, performing tasks such as registering
	 * event listeners on the client and creating the EPMI JS object.
	 */
	protected void createMenuItemInitDirective(RenderingContext rc, Component component, Style fallbackStyle) {
		boolean enabled = component.isRenderEnabled();
		CssStyleEx baseCssStyle = new CssStyleEx(component, fallbackStyle);
		Render.asFillImage(baseCssStyle, component,MenuItem.PROPERTY_BACKGROUND_IMAGE, fallbackStyle,rc);
		if (! enabled) {
			Render.asColors(baseCssStyle,component,AbstractButton.PROPERTY_DISABLED_BACKGROUND,ButtonEx.PROPERTY_DISABLED_FOREGROUND, fallbackStyle);
			Render.asFont(baseCssStyle,component,AbstractButton.PROPERTY_DISABLED_FONT, fallbackStyle);
			Render.asBorder(baseCssStyle,component,AbstractButton.PROPERTY_DISABLED_BORDER, fallbackStyle);
			Render.asFillImage(baseCssStyle,component,AbstractButton.PROPERTY_DISABLED_BACKGROUND_IMAGE, fallbackStyle,rc);
		}
		if (rc.getRP(MenuItem.PROPERTY_LINE_WRAP, fallbackStyle, false) == false) {
			baseCssStyle.setAttribute("white-space", "nowrap");
		}
		

		FillImage backgroundImage = (FillImage) rc.getRP(MenuItem.PROPERTY_BACKGROUND_IMAGE, fallbackStyle);

		boolean rolloverEnabled = rc.getRP(MenuItem.PROPERTY_ROLLOVER_ENABLED, fallbackStyle, false);
		boolean pressedEnabled = rc.getRP(MenuItem.PROPERTY_PRESSED_ENABLED, fallbackStyle, false);

		ImageReference icon;
		String defaultStyle = null;
		String defaultIconUri = null;
		String rolloverStyle = null;
		String rolloverIconUri = null;
		String pressedStyle = null;
		String pressedIconUri = null;

		if (enabled && (rolloverEnabled || pressedEnabled)) {
			String iconProperyName = component.isRenderEnabled() ?  MenuItem.PROPERTY_ICON : MenuItem.PROPERTY_DISABLED_ICON;
			icon = (ImageReference) rc.getRP(iconProperyName, fallbackStyle);
			boolean hasIcon = icon != null;
			defaultStyle = baseCssStyle.renderInline();
			if (hasIcon) {
				defaultIconUri = ImageManager.getURI(rc, icon);
			}
			if (rolloverEnabled) {
				CssStyle rolloverCssStyle = new CssStyle();
				Render.asComponent(rolloverCssStyle, component, fallbackStyle);
				Render.asBorder(rolloverCssStyle, component, MenuItem.PROPERTY_ROLLOVER_BORDER, fallbackStyle);
				Render.asColors(rolloverCssStyle,component, MenuItem.PROPERTY_ROLLOVER_BACKGROUND, MenuItem.PROPERTY_ROLLOVER_FOREGROUND, fallbackStyle);
				Render.asFont(rolloverCssStyle, component, MenuItem.PROPERTY_ROLLOVER_FONT, fallbackStyle);
				if (rc.getRP(MenuItem.PROPERTY_LINE_WRAP, fallbackStyle, false) == false) {
					rolloverCssStyle.setAttribute("white-space", "nowrap");
				}
				if (backgroundImage != null) {
					Render.asFillImage(rolloverCssStyle, component, MenuItem.PROPERTY_ROLLOVER_BACKGROUND_IMAGE, fallbackStyle, rc);
				}
				if (rolloverCssStyle.hasAttributes()) {
					rolloverStyle = rolloverCssStyle.renderInline();
				}
				if (hasIcon) {
					icon = (ImageReference) rc.getRP(MenuItem.PROPERTY_ROLLOVER_ICON, fallbackStyle);
					if (icon != null) {
						rolloverIconUri = ImageManager.getURI(rc, icon);
					}
				}
			}
			if (pressedEnabled) {
				CssStyle pressedCssStyle = new CssStyle();
				Render.asBorder(pressedCssStyle,component,MenuItem.PROPERTY_PRESSED_BORDER, fallbackStyle);
				Render.asColors(pressedCssStyle,component,MenuItem.PROPERTY_PRESSED_BACKGROUND,MenuItem.PROPERTY_PRESSED_FOREGROUND, fallbackStyle);
				Render.asFont(pressedCssStyle,component,MenuItem.PROPERTY_PRESSED_FONT, fallbackStyle);
				if (rc.getRP(MenuItem.PROPERTY_LINE_WRAP, fallbackStyle, false) == false) {
					pressedCssStyle.setAttribute("white-space", "nowrap");
				}
				if (backgroundImage != null) {
					Render.asFillImage(pressedCssStyle, component, MenuItem.PROPERTY_PRESSED_BACKGROUND_IMAGE, fallbackStyle, rc);
				}
				if (pressedCssStyle.hasAttributes()) {
					pressedStyle = pressedCssStyle.renderInline();
				}
				if (hasIcon) {
					icon = (ImageReference) rc.getRP(MenuItem.PROPERTY_PRESSED_ICON, fallbackStyle);
					if (icon != null) {
						pressedIconUri = ImageManager.getURI(rc, icon);
					}
				}
			}
		}

		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPMI.MessageProcessor",
				"init", new String[0], new String[0]);

		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemElement.setAttribute("eid", rc.getElementId());
		itemElement.setAttribute("etype", component.getClass().getName());
		itemElement.setAttribute("parentMenuId", ContainerInstance.getElementId(component.getParent()));
		// only for debugging
		//itemElement.setAttribute("text", menuItem.getText());
		
		itemElement.setAttribute("enabled", String.valueOf(enabled));
		itemElement.setAttribute("visibleComponentCount", String.valueOf(component.getVisibleComponentCount()));

		if (defaultStyle != null) {
			itemElement.setAttribute("defaultStyle", defaultStyle);
		}
		if (defaultIconUri != null) {
			itemElement.setAttribute("defaultIcon", defaultIconUri);
		}
		if (rolloverStyle != null) {
			itemElement.setAttribute("rolloverStyle", rolloverStyle);
		}
		if (rolloverIconUri != null) {
			itemElement.setAttribute("rolloverIcon", rolloverIconUri);
		}
		if (pressedStyle != null) {
			itemElement.setAttribute("pressedStyle", pressedStyle);
		}
		if (pressedIconUri != null) {
			itemElement.setAttribute("pressedicon", pressedIconUri);
		}
		MenuItem menuItem = (MenuItem) component;
		if (menuItem.hasActionListeners()) {
			itemElement.setAttribute("serverNotify", "true");
		}
		if (menuItem.getRootMenu() == component) {
			itemElement.setAttribute("isRootMenu", "true");
		}
		if (component instanceof Menu) {
			int openOption = rc.getRP(Menu.PROPERTY_OPEN_OPTION,Menu.OPEN_ON_MOUSEOVER);
			boolean clickToOpen = (openOption == Menu.OPEN_ON_CLICK || openOption == Menu.OPEN_ON_SUBMENU_CLICK);
			boolean submenuClickToOpen = openOption == Menu.OPEN_ON_SUBMENU_CLICK;

			itemElement.setAttribute("menuAlwaysOnTop", String.valueOf(rc.getRP(Menu.PROPERTY_MENU_ALWAYS_ON_TOP, fallbackStyle)));
			itemElement.setAttribute("leftOffset", String.valueOf(rc.getRP(Menu.PROPERTY_LEFT_OFFSET, fallbackStyle)));
			itemElement.setAttribute("topOffset", String.valueOf(rc.getRP(Menu.PROPERTY_TOP_OFFSET, fallbackStyle)));
			itemElement.setAttribute("horizontal", String.valueOf(rc.getRP(Menu.PROPERTY_HORIZONTAL, fallbackStyle)));
            itemElement.setAttribute("flip", String.valueOf(rc.getRP(Menu.PROPERTY_FLIP, fallbackStyle)));
			itemElement.setAttribute("keepAlive", String.valueOf(rc.getRP(Menu.PROPERTY_KEEP_ALIVE, fallbackStyle)));
			itemElement.setAttribute("submenuImageBordered", String.valueOf(rc.getRP(Menu.PROPERTY_SUBMENU_IMAGE_BORDERED, fallbackStyle)));
			itemElement.setAttribute("clickToOpen", String.valueOf(clickToOpen));
			itemElement.setAttribute("submenuClickToOpen", String.valueOf(submenuClickToOpen));
			
			icon = (ImageReference) rc.getRP(Menu.PROPERTY_SUBMENU_IMAGE, fallbackStyle);
			if (icon != null) {
				itemElement.setAttribute("defaultSubmenuImage", ImageManager.getURI(rc, icon));
			}
			icon = (ImageReference) rc.getRP(Menu.PROPERTY_SUBMENU_ROLLOVER_IMAGE, fallbackStyle);
			if (icon != null) {
				itemElement.setAttribute("rolloverSubmenuImage", ImageManager.getURI(rc, icon));
			}
		}
		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * dispose the state of a MenuItem, performing tasks such as deregistering
	 * event listeners on the client and deleting its JS object.
	 */
	protected void renderDisposeDirective(ServerMessage serverMessage, String elementId) {
		// we still need the JS even if we are disposing
		serverMessage.addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		serverMessage.addLibrary(MENU_ITEM_SERVICE.getId());
		
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPMI.MessageProcessor", "dispose",
				new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", elementId);
		itemizedUpdateElement.appendChild(itemElement);
	}

}
