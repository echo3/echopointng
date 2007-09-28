package echopointng.ui.syncpeer;

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

/*
 * Much of this code was taken from the base Echo2 project on the 16/5/05 and
 * hence is Copyright (C) 2002-2005 NextApp, Inc..  This was done to re-use the 
 * base ButtonEx code but with our EchoPoint differences  
 */

/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2005 NextApp, Inc.
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

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.FocusSupport;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.propertyrender.AlignmentRender;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.servermessage.WindowUpdate;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.ButtonEx;
import echopointng.EPNG;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;
import echopointng.ui.util.TriCellTable;
import echopointng.util.HtmlKit;
import echopointng.xhtml.XhtmlFragment;

/**
 * <code>ButtonExPeer</code> is a peer for <code>ButtonEx</code>
 */

public class ButtonExPeer extends AbstractEchoPointPeer implements ActionProcessor, FocusSupport {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service BUTTON_SERVICE = JavaScriptService.forResource("EPNG.ButtonEx", "/echopointng/ui/resource/js/button.js");

	static {
		WebRenderServlet.getServiceRegistry().add(BUTTON_SERVICE);
	}

	/**
	 * @see nextapp.echo2.webcontainer.FocusSupport#renderSetFocus(nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.Component)
	 */
	public void renderSetFocus(RenderContext rc, Component component) {
		WindowUpdate.renderSetFocus(rc.getServerMessage(), ContainerInstance.getElementId(component));
	}

	/**
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	public String getContainerId(Component child) {
		throw new UnsupportedOperationException("Component does not support children.");
	}

	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		String value = actionElement.getAttribute(ActionProcessor.ACTION_VALUE);
		ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, ButtonEx.INPUT_CLICK, Integer.valueOf(value));
	}

	/**
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc, update, component);

		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		serverMessage.addLibrary(BUTTON_SERVICE.getId());

		createDisposeDirective(serverMessage, component);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		// we always replace every thing
		return super.renderUpdateBaseImpl(rc, update, targetId, true);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(BUTTON_SERVICE);
		ButtonEx button = (ButtonEx) component;

		String id = ContainerInstance.getElementId(button);

		Element topElement = getTopLevelButtonElement(rc, button, fallbackStyle);
		topElement.setAttribute("id", id);
		rc.addStandardWebSupport(topElement);

		CssStyle cssStyle = renderTopLevelStyleSupport(rc, button, fallbackStyle);
		topElement.setAttribute("style", cssStyle.renderInline());

		renderButtonContent(rc, topElement, button, fallbackStyle);

		parent.appendChild(topElement);

		boolean addRolloverListener = false;
		if (button.isRenderEnabled()) {
			addRolloverListener = renderRolloverStyleSupport(rc, button, fallbackStyle);
		}
		createInitDirective(rc, button, fallbackStyle, addRolloverListener);
	}

	/**
	 * Called to return the top level Element to be used around the rendered
	 * ButtonEx
	 */
	protected Element getTopLevelButtonElement(RenderingContext rc, ButtonEx button, Style fallbackStyle) {
		return rc.createE("div");
	}

	/**
	 * Called to render the CssStyle for the top level ButtonEx element
	 */
	protected CssStyle renderTopLevelStyleSupport(RenderingContext rc, ButtonEx button, Style fallbackStyle) {
		CssStyleEx cssStyle = new CssStyleEx(button, fallbackStyle);
		Render.asFillImage(cssStyle, button, ButtonEx.PROPERTY_BACKGROUND_IMAGE, fallbackStyle, rc);
		if (!button.isRenderEnabled()) {
			Render.asColors(cssStyle, button, ButtonEx.PROPERTY_DISABLED_BACKGROUND, ButtonEx.PROPERTY_DISABLED_FOREGROUND, fallbackStyle);
			Render.asFont(cssStyle, button, ButtonEx.PROPERTY_DISABLED_FONT, fallbackStyle);
			Render.asBorder(cssStyle, button, ButtonEx.PROPERTY_DISABLED_BORDER, fallbackStyle);
			Render.asFillImage(cssStyle, button, ButtonEx.PROPERTY_DISABLED_BACKGROUND_IMAGE, fallbackStyle, rc);
		}
		if (rc.getRP(ButtonEx.PROPERTY_LINE_WRAP, fallbackStyle, false) == false) {
			cssStyle.setAttribute("white-space", "nowrap");
		}
		return cssStyle;
	}

	/**
	 * Called to render the rollover style support required for this ButtonEx.
	 * It should return true if rollover support of some kind was added.
	 * 
	 */
	protected boolean renderRolloverStyleSupport(RenderingContext rc, ButtonEx button, Style fallbackStyle) {
		String id = ContainerInstance.getElementId(button);
		boolean enabled = button.isRenderEnabled();

		FillImage backgroundImage = (FillImage) rc.getRP(enabled ? ButtonEx.PROPERTY_BACKGROUND_IMAGE : ButtonEx.PROPERTY_DISABLED_BACKGROUND_IMAGE,
				fallbackStyle);

		boolean rolloverEnabled = rc.getRP(ButtonEx.PROPERTY_ROLLOVER_ENABLED, fallbackStyle, false);
		boolean pressedEnabled = rc.getRP(ButtonEx.PROPERTY_PRESSED_ENABLED, fallbackStyle, false);
		boolean addRolloverListener = false;

		if (enabled && (rolloverEnabled || pressedEnabled)) {
			ImageReference icon = (ImageReference) rc.getRP(enabled ? ButtonEx.PROPERTY_ICON : ButtonEx.PROPERTY_DISABLED_ICON, fallbackStyle);
			boolean hasIcon = icon != null;
			CssStyle baseCssStyle = renderTopLevelStyleSupport(rc, button, fallbackStyle);
			rc.renderSetProperty(id, "defaultStyle", baseCssStyle.renderInline());

			if (hasIcon) {
				rc.renderSetProperty(id, "defaultIcon", ImageManager.getURI(rc, icon));
			}

			if (rolloverEnabled) {
				CssStyle rolloverCssStyle = new CssStyle();

				Render.asBorder(rolloverCssStyle, button, ButtonEx.PROPERTY_ROLLOVER_BORDER, fallbackStyle);
				Render
						.asColors(rolloverCssStyle, button, ButtonEx.PROPERTY_ROLLOVER_BACKGROUND, ButtonEx.PROPERTY_ROLLOVER_FOREGROUND,
								fallbackStyle);
				Render.asFont(rolloverCssStyle, button, ButtonEx.PROPERTY_ROLLOVER_FONT, fallbackStyle);
				if (rc.getRP(ButtonEx.PROPERTY_LINE_WRAP, fallbackStyle, false) == false) {
					rolloverCssStyle.setAttribute("white-space", "nowrap");
				}
				if (backgroundImage != null) {
					Render.asFillImage(rolloverCssStyle, button, ButtonEx.PROPERTY_ROLLOVER_BACKGROUND_IMAGE, fallbackStyle, rc);
				}
				if (rolloverCssStyle.hasAttributes()) {
					rc.renderSetProperty(id, "rolloverStyle", rolloverCssStyle.renderInline());
					addRolloverListener = true;
				}
				if (hasIcon) {
					ImageReference rolloverIcon = (ImageReference) rc.getRP(ButtonEx.PROPERTY_ROLLOVER_ICON, fallbackStyle);
					if (rolloverIcon != null) {
						rc.renderSetProperty(id, "rolloverIcon", ImageManager.getURI(rc, rolloverIcon));
					}
				}

			}
			if (pressedEnabled) {
				CssStyle pressedCssStyle = new CssStyle();
				Render.asBorder(pressedCssStyle, button, ButtonEx.PROPERTY_PRESSED_BORDER, fallbackStyle);
				Render.asColors(pressedCssStyle, button, ButtonEx.PROPERTY_PRESSED_BACKGROUND, ButtonEx.PROPERTY_PRESSED_FOREGROUND, fallbackStyle);
				Render.asFont(pressedCssStyle, button, ButtonEx.PROPERTY_PRESSED_FONT, fallbackStyle);
				if (rc.getRP(ButtonEx.PROPERTY_LINE_WRAP, fallbackStyle, false) == false) {
					pressedCssStyle.setAttribute("white-space", "nowrap");
				}
				if (backgroundImage != null) {
					Render.asFillImage(pressedCssStyle, button, ButtonEx.PROPERTY_PRESSED_BACKGROUND_IMAGE, fallbackStyle, rc);
				}
				if (pressedCssStyle.hasAttributes()) {
					rc.renderSetProperty(id, "pressedStyle", pressedCssStyle.renderInline());
				}
				if (hasIcon) {
					ImageReference pressedIcon = (ImageReference) rc.getRP(ButtonEx.PROPERTY_PRESSED_ICON, fallbackStyle);
					if (pressedIcon != null) {
						rc.renderSetProperty(id, "pressedIcon", ImageManager.getURI(rc, pressedIcon));
					}
				}
			}
		}
		return addRolloverListener;
	}

	/**
	 * 
	 */
	protected void createDisposeDirective(ServerMessage serverMessage, Component component) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPButton.MessageProcessor", "dispose",
				new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * We have messaging in ButtonEx
	 */
	protected void createInitDirective(RenderingContext rc, ButtonEx buttonEx, Style fallbackStyle, boolean addRolloverListener) {
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPButton.MessageProcessor",
				"init", new String[0], new String[0]);

		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemElement.setAttribute("eid", rc.getElementId());
		itemElement.setAttribute("enabled", String.valueOf(buttonEx.isRenderEnabled()));
		// do we need this
		itemElement.setAttribute("serverNotify", String.valueOf(buttonEx.hasActionListeners()));
		if (buttonEx.isRenderEnabled()) {
			renderEventSupport(rc, itemElement, buttonEx, addRolloverListener);
		}
		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * Called to add the event support to the ButtonEx. If addRolloverListener
	 * is true then rollover support has been previously added.
	 * 
	 * @param rc -
	 *            the RenderingContext in place
	 * @param itemXML -
	 *            the messaging XML container element
	 * @param button -
	 *            the current button
	 * @param addRolloverListener -
	 *            the result of the renderRolloverStyleSupport() method call
	 */
	protected void renderEventSupport(RenderingContext rc, Element itemXML, ButtonEx button, boolean addRolloverListener) {
		boolean pressedEnabled = rc.getRP(ButtonEx.PROPERTY_PRESSED_ENABLED, false);
		itemXML.setAttribute("event-click", String.valueOf(true));
		if (addRolloverListener) {
			itemXML.setAttribute("event-mouseover", String.valueOf(true));
			itemXML.setAttribute("event-mouseout", String.valueOf(true));
		}
		if (pressedEnabled) {
			itemXML.setAttribute("event-mousedown", String.valueOf(true));
			itemXML.setAttribute("event-mouseup", String.valueOf(true));
		}
	}

	/**
	 * Renders the content of the button, i.e., its text, icon, and/or state
	 * icon.
	 * 
	 * @param rc
	 *            the relevant <code>RenderingContext</code>
	 * @param buttonContainerElement
	 *            the <code>Element</code> which will contain the content
	 * @param button
	 *            the <code>ButtonEx</code> being rendered
	 */
	protected void renderButtonContent(RenderingContext rc, Element buttonContainerElement, ButtonEx button, Style fallbackStyle) {
		Node contentNode = null;
		String elementId = ContainerInstance.getElementId(button);

		ImageReference icon = (ImageReference) rc.getRP(button.isRenderEnabled() ? ButtonEx.PROPERTY_ICON : ButtonEx.PROPERTY_DISABLED_ICON,
				fallbackStyle);
		Object text = rc.getRP(ButtonEx.PROPERTY_TEXT, fallbackStyle);

		// Create entities.
		Node textNodes[] = null;

		if (text instanceof String) {
			int newLinePolicy = HtmlKit.NEWLINE_TRANSPARENT;
			if (rc.getRP(ButtonEx.PROPERTY_INTERPRET_NEWLINES, fallbackStyle, false)) {
				newLinePolicy = HtmlKit.NEWLINE_TO_BR;
			}
			textNodes = HtmlKit.encodeNewLines(rc.getDocument(), (String) text, newLinePolicy);
		} else if (text instanceof XhtmlFragment) {
			//
			// parse the XhtmlFramement into
			XhtmlFragment fragment = (XhtmlFragment) text;
			try {
				textNodes = fragment.toDOM(rc.getDocument());
			} catch (Exception e) {
				throw new RuntimeException("The XhtmlFragment is not valid XHTML : " + fragment.getFragment(), e);
			}
		}

		Element iconElement;
		if (icon == null) {
			iconElement = null;
		} else {
			iconElement = ImageManager.createImgE(rc, icon);
			iconElement.setAttribute("id", elementId + "_icon");
		}

		int entityCount = (textNodes == null ? 0 : 1) + (iconElement == null ? 0 : 1);

		Extent iconTextMargin;
		Alignment textPosition;

		switch (entityCount) {
		case 1:
			if (textNodes != null) {
				contentNode = rc.getDocument().createDocumentFragment();
				for (int i = 0; i < textNodes.length; i++) {
					contentNode.appendChild(textNodes[i]);
				}
			} else {
				contentNode = iconElement;
			}
			break;
		case 2:

			iconTextMargin = (Extent) rc.getRP(ButtonEx.PROPERTY_ICON_TEXT_MARGIN, fallbackStyle);
			TriCellTable tct;
			textPosition = (Alignment) rc.getRP(ButtonEx.PROPERTY_TEXT_POSITION, fallbackStyle);
			int orientation = convertIconTextPositionToOrientation(textPosition, button);

			tct = new TriCellTable(rc.getDocument(), elementId, orientation, iconTextMargin);

			renderCellText(rc, tct, textNodes, button, fallbackStyle);
			renderCellIcon(rc, tct, iconElement, 1, fallbackStyle);

			Element tableElement = tct.getTableElement();
			tableElement.setAttribute("id", elementId + "_table");
			tableElement.setAttribute("style", "width:100%;height:100%");
			contentNode = tableElement;
			break;
		default:
			// 0 element button.
			contentNode = null;
		}

		if (contentNode != null) {
			buttonContainerElement.appendChild(contentNode);
		}
	}

	/**
	 * Renders the content of the <code>TriCellTable</code> cell which
	 * contains the button's icon.
	 * 
	 * @param rc - the RenderingContext
	 * @param tct
	 *            the <code>TriCellTable</code> to update
	 * @param textNode
	 *            the text
	 * @param cellIndex
	 *            the index of the cell in the <code>TriCellTable</code> that
	 *            should contain the icon
	 */
	protected void renderCellIcon(RenderingContext rc, TriCellTable tct, Element iconElement, int cellIndex, Style fallbackStyle) {
		Element iconTdElement = tct.getTdElement(cellIndex);
		 //-- BEGIN UPDATE --
		Element textTdElement = tct.getTdElement(cellIndex);
		CssStyle textTdCssStyle = new CssStyle();
		textTdCssStyle.setAttribute("padding", "0px");
		textTdCssStyle.setAttribute("width", "100%");
		AlignmentRender.renderToStyle(textTdCssStyle, (Alignment) rc.getRP(ButtonEx.PROPERTY_TEXT_ALIGNMENT, fallbackStyle));
		textTdElement.setAttribute("style", textTdCssStyle.renderInline());
//		--- END UPDATE ---
		iconTdElement.appendChild(iconElement);
	}

	/**
	 * Renders the content of the <code>TriCellTable</code> cell which
	 * contains the button's text. Text is always rendered in cell #0 of the
	 * table.
	 * 
	 * @param tct
	 *            the <code>TriCellTable</code> to update
	 * @param textNodes
	 *            the text as a series of DOM nodes
	 * @param button
	 *            the <code>ButtonEx</code> being rendered
	 */
	protected void renderCellText(RenderingContext rc, TriCellTable tct, Node textNodes[], ButtonEx button, Style fallbackStyle) {
		Element textTdElement = tct.getTdElement(0);
		CssStyle textTdCssStyle = new CssStyle();
		textTdCssStyle.setAttribute("padding", "0px");
		textTdCssStyle.setAttribute("width", "100%"); // push the text to fill
														// the space available
		AlignmentRender.renderToStyle(textTdCssStyle, (Alignment) rc.getRP(ButtonEx.PROPERTY_TEXT_ALIGNMENT, fallbackStyle));
		if (rc.getRP(ButtonEx.PROPERTY_LINE_WRAP, fallbackStyle, false) == false) {
			textTdCssStyle.setAttribute("white-space", "nowrap");
		}
		textTdElement.setAttribute("style", textTdCssStyle.renderInline());
		for (int i = 0; i < textNodes.length; i++) {
			textTdElement.appendChild(textNodes[i]);
		}
	}

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
}
