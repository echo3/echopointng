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
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.ClientProperties;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.servermessage.DomUpdate;
import nextapp.echo2.webrender.service.JavaScriptService;
import nextapp.echo2.webrender.util.DomUtil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.PopUp;
import echopointng.able.Sizeable;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.HtmlTable;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;
import echopointng.ui.util.StaticImageService;

/**
 * <code>PopUpPeer</code> is the peer for <code>PopUpPeer</code>
 */
public class PopUpPeer extends AbstractEchoPointContainerPeer implements PropertyUpdateProcessor {
	public static final Service POPUP_DEFAULT_TOGGLE_IMAGE = StaticImageService.forResource("EPNG.PopUpDefaultToggleImage",
			"/echopointng/resource/images/dropdown_arrow.gif", new Extent(7), new Extent(7));

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service POPUP_SERVICE = JavaScriptService.forResource("EPNG.PopUp", "/echopointng/ui/resource/js/popup.js");

	static {
		WebRenderServlet.getServiceRegistry().add(POPUP_SERVICE);
		WebRenderServlet.getServiceRegistry().add(POPUP_DEFAULT_TOGGLE_IMAGE);
	}

	public PopUpPeer() {
		super();
		partialUpdateManager.add(PopUp.EXPANDED_CHANGED_PROPERTY, new PartialUpdateParticipant() {
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
				PopUp popUp = (PopUp) update.getParent();
				boolean isExpanded = popUp.isExpanded();

				// an XML message directive please to tell the popup to expand!
				Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
						"EPPU.MessageProcessor", "expansion", new String[0], new String[0]);
				Element itemElement = rc.getServerMessage().getDocument().createElement("item");
				itemElement.setAttribute("eid", ContainerInstance.getElementId(popUp));
				itemElement.setAttribute("expanded", String.valueOf(isExpanded));
				itemizedUpdateElement.appendChild(itemElement);
			}
		});
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * dispose the state of a PopUp, performing tasks such as deregistering
	 * event listeners on the client and deleting its JS object.
	 */
	protected void createDisposeDirective(ServerMessage serverMessage, Component component) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPPU.MessageProcessor", "dispose",
				new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemElement.setAttribute("targetId", findKeyTargetId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * initialize the state of a PopUp, performing tasks such as registering
	 * event listeners on the client and creating the EPMI JS object.
	 */
	protected void createInitDirective(RenderingContext rc, Component component, Style fallbackStyle) {
		CssStyleEx defaultCssStyle = new CssStyleEx(component, fallbackStyle);

		CssStyleEx defaultToggleCssStyle = new CssStyleEx();
		Render.asColors(defaultToggleCssStyle, component, PopUp.PROPERTY_TOGGLE_BACKGROUND, null, fallbackStyle);
		Render.asBorder(defaultToggleCssStyle, component, PopUp.PROPERTY_TOGGLE_BORDER, fallbackStyle);
		Render.asInsets(defaultToggleCssStyle, component, PopUp.PROPERTY_TOGGLE_INSETS, "padding", fallbackStyle);

		CssStyleEx defaultTargetCssStyle = new CssStyleEx();
		Render.asColors(defaultTargetCssStyle, component, PopUp.PROPERTY_TARGET_BACKGROUND, null, fallbackStyle);
		Render.asBorder(defaultTargetCssStyle, component, PopUp.PROPERTY_TARGET_BORDER, fallbackStyle);
		Render.asInsets(defaultTargetCssStyle, component, PopUp.PROPERTY_TARGET_INSETS, "padding", fallbackStyle);

		boolean rolloverEnabled = rc.getRP(PopUp.PROPERTY_ROLLOVER_ENABLED, fallbackStyle, false);
		boolean pressedEnabled = rc.getRP(PopUp.PROPERTY_TOGGLE_PRESSED_ENABLED, fallbackStyle, false);

		ImageReference icon = (ImageReference) rc.getRP(PopUp.PROPERTY_TOGGLE_ICON, fallbackStyle);
		boolean hasIcon = icon != null;

		String defaultStyle = null;
		String rolloverStyle = null;

		String toggleDefaultStyle = null;
		String toggleDefaultIconUri = null;
		String toggleRolloverStyle = null;
		String toggleRolloverIconUri = null;
		String togglePressedStyle = null;
		String toggledPressedIconUri = null;

		String targetDefaultStyle = null;
		String targetRolloverStyle = null;

		ImageReference imageRef;

		defaultStyle = defaultCssStyle.renderInline();
		toggleDefaultStyle = defaultToggleCssStyle.renderInline();
		if (hasIcon) {
			toggleDefaultIconUri = ImageManager.getURI(rc, icon);
		}

		if (rolloverEnabled || pressedEnabled) {
			if (rolloverEnabled) {
				CssStyle rolloverCssStyle = new CssStyle();
				Render.asComponent(rolloverCssStyle, component, fallbackStyle);
				Render.asBorder(rolloverCssStyle, component, PopUp.PROPERTY_ROLLOVER_BORDER, fallbackStyle);
				Render.asColors(rolloverCssStyle, component, PopUp.PROPERTY_ROLLOVER_BACKGROUND, null, fallbackStyle);
				if (rolloverCssStyle.hasAttributes()) {
					rolloverStyle = rolloverCssStyle.renderInline();
				}

				CssStyle toggleRolloverCssStyle = new CssStyle();
				Render.asBorder(toggleRolloverCssStyle, component, PopUp.PROPERTY_TOGGLE_ROLLOVER_BORDER, fallbackStyle);
				Render.asColors(toggleRolloverCssStyle, component, PopUp.PROPERTY_TOGGLE_ROLLOVER_BACKGROUND, null, fallbackStyle);
				Render.asInsets(toggleRolloverCssStyle, component, PopUp.PROPERTY_TOGGLE_INSETS, "padding", fallbackStyle);
				if (toggleRolloverCssStyle.hasAttributes()) {
					toggleRolloverStyle = toggleRolloverCssStyle.renderInline();
				}
				if (hasIcon) {
					imageRef = (ImageReference) rc.getRP(PopUp.PROPERTY_TOGGLE_ROLLOVER_ICON, fallbackStyle);
					if (imageRef != null) {
						toggleRolloverIconUri = ImageManager.getURI(rc, imageRef);
					}
				}

				CssStyle targetRolloverCssStyle = new CssStyle();
				Render.asBorder(targetRolloverCssStyle, component, PopUp.PROPERTY_TARGET_ROLLOVER_BORDER, fallbackStyle);
				Render.asColors(targetRolloverCssStyle, component, PopUp.PROPERTY_TARGET_ROLLOVER_BACKGROUND, null, fallbackStyle);
				Render.asInsets(targetRolloverCssStyle, component, PopUp.PROPERTY_TARGET_INSETS, "padding", fallbackStyle);
				if (targetRolloverCssStyle.hasAttributes()) {
					targetRolloverStyle = targetRolloverCssStyle.renderInline();
				}

			}
			if (pressedEnabled) {
				CssStyle togglePressedCssStyle = new CssStyle();
				Render.asBorder(togglePressedCssStyle, component, PopUp.PROPERTY_TOGGLE_PRESSED_BORDER);
				Render.asColors(togglePressedCssStyle, component, PopUp.PROPERTY_TOGGLE_PRESSED_BACKGROUND, null);
				Render.asInsets(togglePressedCssStyle, component, PopUp.PROPERTY_TOGGLE_INSETS, "padding", fallbackStyle);
				if (togglePressedCssStyle.hasAttributes()) {
					togglePressedStyle = togglePressedCssStyle.renderInline();
				}
				if (hasIcon) {
					imageRef = (ImageReference) rc.getRP(PopUp.PROPERTY_TOGGLE_PRESSED_ICON, fallbackStyle);
					if (imageRef != null) {
						toggledPressedIconUri = ImageManager.getURI(rc, imageRef);
					}
				}
			}
		}

		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPPU.MessageProcessor",
				"init", new String[0], new String[0]);

		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemElement.setAttribute("eid", rc.getElementId());

		boolean isExpanded = ((PopUp) component).isExpanded();
		itemElement.setAttribute("expanded", String.valueOf(isExpanded));

		itemElement.setAttribute("enabled", String.valueOf(component.isRenderEnabled()));
		itemElement.setAttribute("parentId", findParentId(component));
		itemElement.setAttribute("keyTargetId", findKeyTargetId(component));

		Alignment align = (Alignment) rc.getRP(PopUp.PROPERTY_POPUP_ALIGNMENT, fallbackStyle);
		if (align != null) {
			itemElement.setAttribute("vAlign", String.valueOf(align.getVertical()));
			itemElement.setAttribute("hAlign", String.valueOf(align.getHorizontal()));
		}
		itemElement.setAttribute("popUpAlwaysOnTop", String.valueOf(rc.getRP(PopUp.PROPERTY_POPUP_ALWAYS_ON_TOP, fallbackStyle, false)));
		itemElement.setAttribute("nextToToggle", String.valueOf(rc.getRP(PopUp.PROPERTY_POPUP_NEXT_TO_TOGGLE, fallbackStyle, true)));
		itemElement.setAttribute("onRolloverShowBox", String.valueOf(rc.getRP(PopUp.PROPERTY_POPUP_ON_ROLLOVER, fallbackStyle, true)));
		itemElement.setAttribute("leftOffset", String.valueOf(rc.getRP(PopUp.PROPERTY_POPUP_LEFT_OFFSET, fallbackStyle)));
		itemElement.setAttribute("topOffset", String.valueOf(rc.getRP(PopUp.PROPERTY_POPUP_TOP_OFFSET, fallbackStyle)));
		itemElement.setAttribute("focusOnExpand", String.valueOf(rc.getRP(PopUp.PROPERTY_FOCUS_ON_EXPAND, fallbackStyle,false)));
		
		Component focusedComponent = (Component)rc.getRP(PopUp.PROPERTY_FOCUS_COMPONENT);
		if (focusedComponent != null) {
			itemElement.setAttribute("focusComponent", ContainerInstance.getElementId(focusedComponent));
		}

		if (defaultStyle != null) {
			itemElement.setAttribute("defaultStyle", defaultStyle);
		}
		if (rolloverStyle != null) {
			itemElement.setAttribute("rolloverStyle", rolloverStyle);
		}

		if (toggleDefaultStyle != null) {
			itemElement.setAttribute("toggleDefaultStyle", toggleDefaultStyle);
		}
		if (toggleDefaultIconUri != null) {
			itemElement.setAttribute("toggleDefaultIcon", toggleDefaultIconUri);
		}
		if (toggleRolloverStyle != null) {
			itemElement.setAttribute("toggleRolloverStyle", toggleRolloverStyle);
		}
		if (toggleRolloverIconUri != null) {
			itemElement.setAttribute("toggleRolloverIcon", toggleRolloverIconUri);
		}
		if (togglePressedStyle != null) {
			itemElement.setAttribute("togglePressedStyle", togglePressedStyle);
		}
		if (toggledPressedIconUri != null) {
			itemElement.setAttribute("togglePressedIcon", toggledPressedIconUri);
		}

		if (targetDefaultStyle != null) {
			itemElement.setAttribute("targetDefaultStyle", targetDefaultStyle);
		}
		if (targetRolloverStyle != null) {
			itemElement.setAttribute("targetRolloverStyle", targetRolloverStyle);
		}

		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * Returns the component id of the target component. Note it is slightly
	 * different beast to the target component "containing" box.
	 */
	private String findKeyTargetId(Component component) {
		Component target = (Component) component.getRenderProperty(PopUp.PROPERTY_TARGET);
		if (target != null) {
			return ContainerInstance.getElementId(target);
		}
		return null;
	}

	/**
	 * This traverses up the hierarchy until it finds a parent popup. If one can
	 * be found then "" is returned.
	 */
	private String findParentId(Component component) {
		Component parent = component.getParent();
		while (parent != null) {
			if (parent instanceof PopUp) {
				return ContainerInstance.getElementId(parent);
			}
			parent = parent.getParent();
		}
		return "";
	}

	/**
	 * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
		String propertyName = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
		if (PopUp.EXPANDED_CHANGED_PROPERTY.equals(propertyName)) {
			String propertyValue = DomUtil.getElementText(propertyElement);
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, PopUp.EXPANDED_CHANGED_PROPERTY,
					Boolean.valueOf(propertyValue));
		}
	}

	/**
	 * This is called to render the popup floating box. The name of the returned
	 * box Element must be elementId + "Box" for JavaScript reasons.
	 * 
	 * @return a new top level Element for the floating box
	 */
	protected Element renderBox(RenderingContext rc, Component component, Style fallbackStyle) {
		CssStyleEx style = new CssStyleEx();
		style.setAttribute("position", "absolute");
		style.setAttribute("visibility", "hidden");
		style.setAttribute("display", "none");
		style.setAttribute("z-index", "2");
		Render.layoutFix(rc,style);
		Render.asColors(style, component, PopUp.PROPERTY_POPUP_BACKGROUND, null, fallbackStyle);
		Render.asBorder(style, component, PopUp.PROPERTY_POPUP_BORDER, fallbackStyle);
		Render.asInsets(style, component, PopUp.PROPERTY_POPUP_INSETS, "padding", fallbackStyle);
		Render.asInsets(style, component, PopUp.PROPERTY_POPUP_OUTSETS, "margin", fallbackStyle);

		Element divBox = rc.createE("div");
		divBox.setAttribute("id", rc.getElementId() + "|Box");
		divBox.setAttribute("style", style.renderInline());

		Component popUpComponent = (Component) rc.getRP(PopUp.PROPERTY_POPUP);
		if (popUpComponent != null) {
			renderReplaceableChild(rc, rc.getServerComponentUpdate(), divBox, popUpComponent);
		}
		return divBox;
	}

	/**
	 * This can be called to insert the content popup BOX with an iframe
	 * 
	 * @param rc -
	 *            The RenderingContext in use
	 * @param containerE -
	 *            the top level Element of the content Box
	 */
	private void addIEIframeTrick(RenderingContext rc, Element containerE) {
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
		style.setAttribute("z-index", "1");
		style.setAttribute("top", "0px");
		style.setAttribute("left", "0px");
		Render.layoutFix(rc,style);
		
		Element iframeQuirkDiv = rc.createE("div");
		iframeQuirkDiv.appendChild(iframeQuirkIframeElement);
		iframeQuirkDiv.setAttribute("id", rc.getElementId() + "|IframeQuirk");
		iframeQuirkDiv.setAttribute("style", style.renderInline());

		containerE.appendChild(iframeQuirkDiv);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {

		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(POPUP_SERVICE.getId());

		PopUp popUp = (PopUp) component;
		createDisposeDirective(rc.getServerMessage(), popUp);
		// because we have "reparented" floating Box divs, we need to remove
		// them
		String menuBoxId = ContainerInstance.getElementId(popUp) + "|Box";
		DomUpdate.renderElementRemove(rc.getServerMessage(), menuBoxId);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);
		//-----------------------------------------------
		// talk to our message processor
		//-----------------------------------------------
		createInitDirective(rc, component, fallbackStyle);

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(POPUP_SERVICE);

		CssStyleEx style;
		String elementId = rc.getElementId();

		//
		// inner html table
		//
		style = new CssStyleEx();
		style.setAttribute("border-collapse", "collapse");
		style.setAttribute("border", "0px");

		HtmlTable innerTable = new HtmlTable(rc.getDocument(), 0, 0, 0);
		innerTable.setAttribute("id", elementId + "Inner");
		innerTable.setAttribute("style", style.renderInline());

		//
		//-- the target part
		//
		style = new CssStyleEx();
		style.setAttribute("width", "100%");
		Render.asColors(style, component, PopUp.PROPERTY_TARGET_BACKGROUND, null, fallbackStyle);
		Render.asBorder(style, component, PopUp.PROPERTY_TARGET_BORDER, fallbackStyle);
		Render.asInsets(style, component, PopUp.PROPERTY_TARGET_INSETS, "padding", fallbackStyle);

		Element td;
		td = innerTable.getTD();
		td.setAttribute("id", elementId + "|Target");
		td.setAttribute("style", style.renderInline());
		Component targetComponent = (Component) rc.getRP(PopUp.PROPERTY_TARGET);
		if (targetComponent != null) {
			renderReplaceableChild(rc, rc.getServerComponentUpdate(), td, targetComponent);
		}

		//
		//-- toggle part
		//
		ImageReference toggleImageRef = (ImageReference) rc.getRP(PopUp.PROPERTY_TOGGLE_ICON, fallbackStyle);

		style = new CssStyleEx();
		Render.asColors(style, component, PopUp.PROPERTY_TOGGLE_BACKGROUND, null, fallbackStyle);
		Render.asBorder(style, component, PopUp.PROPERTY_TOGGLE_BORDER, fallbackStyle);
		Render.asInsets(style, component, PopUp.PROPERTY_TOGGLE_INSETS, "padding", fallbackStyle);

		td = innerTable.newTD();
		td.setAttribute("id", elementId + "|Toggle");
		td.setAttribute("style", style.renderInline());

		// we default to a standard icon if one is not set
		style = new CssStyleEx();
		style.setAttribute("vertical-align", "text-top");
		Element toggleImg = ImageManager.createImgE(rc, style, toggleImageRef);
		toggleImg.setAttribute("id", elementId + "|Icon");
		td.appendChild(toggleImg);

		//
		//-- popup box part
		//
		Element divBox = renderBox(rc, component, fallbackStyle);
		if (rc.getRP(PopUp.PROPERTY_POPUP_ALWAYS_ON_TOP,fallbackStyle,false) == false) {
			td = innerTable.newTR();
			td.setAttribute("id", elementId + "|BoxContainer");
			td.setAttribute("colspan", "2");
			td.setAttribute("style", "display:block;position:relative;padding:0px;margin:0px;");
		}
		td.appendChild(divBox);

		// Create Internet Explorer Select-Element blocking IFRAME
		// if needed
		if (rc.getContainerInstance().getClientProperties().getBoolean(ClientProperties.QUIRK_IE_SELECT_Z_INDEX)) {
			addIEIframeTrick(rc, td);
		}

		//
		// outer div
		style = new CssStyleEx(component, fallbackStyle);

		Element outerDiv = rc.createE("div");
		outerDiv.appendChild(innerTable.getTABLE());
		outerDiv.setAttribute("id", elementId + "|Outer");
		outerDiv.setAttribute("style", style.renderInline());
		rc.addStandardWebSupport(outerDiv);

		//
		// outer table
		style = new CssStyleEx();
		style.setAttribute("border-collapse", "collapse");
		style.setAttribute("border", "0px");
		Render.asSizeable(style, (Sizeable) component, fallbackStyle);

		HtmlTable outerTable = new HtmlTable(rc.getDocument(), 0, 0, 0);
		outerTable.getTD().appendChild(outerDiv);
		outerTable.setAttribute("id", elementId);
		outerTable.setAttribute("style", style.renderInline());

		parent.appendChild(outerTable.getTABLE());
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointContainerPeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		boolean fullReplace = false;
		if (update.hasUpdatedLayoutDataChildren()) {
			fullReplace = true;
		} else if (update.hasUpdatedProperties()) {
			if (!partialUpdateManager.canProcess(rc, update)) {
				fullReplace = true;
			} else {
				partialUpdateManager.process(rc, update);
				return false;
			}
		}
		// because we have "reparented" floating Box divs, we need to remove
		// them as well as ourself from the DOM tree.
		if (fullReplace) {
			// Perform full update.
			String elementId = ContainerInstance.getElementId(update.getParent());
			String menuBoxId = elementId + "|Box";
			DomUpdate.renderElementRemove(rc.getServerMessage(), elementId);
			DomUpdate.renderElementRemove(rc.getServerMessage(), menuBoxId);
			renderAdd(rc, update, targetId, update.getParent());
		} else {
			renderUpdateRemoveChildren(rc, update);
			renderUpdateAddChildren(rc, update);
		}
		return fullReplace;
	}
}
