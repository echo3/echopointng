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
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.propertyrender.AlignmentRender;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.service.JavaScriptService;
import nextapp.echo2.webrender.util.DomUtil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.TitleBar;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssRolloverStyleEx;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.HtmlTable;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

/**
 * <code>TitleBarPeer</code> is the peer for <code>TitleBarPeer</code>
 */
public class TitleBarPeer extends AbstractEchoPointContainerPeer implements PropertyUpdateProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service TITLEBAR_SERVICE = JavaScriptService.forResource("EPNG.TitleBar", "/echopointng/ui/resource/js/titlebar.js");

	static {
		WebRenderServlet.getServiceRegistry().add(TITLEBAR_SERVICE);
	}


	public TitleBarPeer() {
		super();
		partialUpdateManager.add(TitleBar.EXPANDED_CHANGED_PROPERTY, new PartialUpdateParticipant() {
			/**
			 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#canRenderProperty(nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate)
			 */
			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}
			
			/**
			 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#renderProperty(nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate)
			 */
			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				TitleBar tb = (TitleBar) update.getParent();
				boolean isExpanded = tb.isExpanded();

				// an XML message directive please to tell the popup to expand!
				Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
						"EPTitleBar.MessageProcessor", "expansion", new String[0], new String[0]);
				Element itemElement = rc.getServerMessage().getDocument().createElement("item");
				itemElement.setAttribute("eid", ContainerInstance.getElementId(tb));
				itemElement.setAttribute("expanded", String.valueOf(isExpanded));
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
		if (TitleBar.EXPANDED_CHANGED_PROPERTY.equals(propertyName)) {
			String propertyValue = DomUtil.getElementText(propertyElement);
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, TitleBar.EXPANDED_CHANGED_PROPERTY, Boolean.valueOf(propertyValue));
		}
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointContainerPeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		return renderUpdateBaseImpl(rc,update,targetId,true);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		renderHtml(rc,rc.getServerComponentUpdate(),parent,component,null);
	}
	
	/**
	 * This method will render the TitleBar as usual however it informs the TitleBar of
	 * the id of the partner Component, that wants to be called in JS if the expand
	 * icons are pressed.
	 */
	public void renderHtml(RenderContext rc, ServerComponentUpdate update, Node parent, Component component, String partnerComponentId) {
		renderHtmlImpl(new RenderingContext(rc, update, component),parent,component,partnerComponentId);

	}
	
	private CssRolloverStyleEx createBaseStyle(RenderingContext rc, Component component, Style fallbackStyle) {
		CssRolloverStyleEx baseStyle;
		baseStyle = new CssRolloverStyleEx(component, fallbackStyle);
		baseStyle.setRolloverBackground((Color) rc.getRP(TitleBar.PROPERTY_ROLLOVER_BACKGROUND,fallbackStyle));
		baseStyle.setRolloverForeground((Color) rc.getRP(TitleBar.PROPERTY_ROLLOVER_FOREGROUND,fallbackStyle));
		baseStyle.setRolloverFont((Font) rc.getRP(TitleBar.PROPERTY_ROLLOVER_FONT,fallbackStyle));
		baseStyle.setRolloverBorder((Border) rc.getRP(TitleBar.PROPERTY_ROLLOVER_BORDER,fallbackStyle));
		Render.asFillImage(baseStyle,component,TitleBar.PROPERTY_BACKGROUND_IMAGE, fallbackStyle,rc);
		if (Render.isClientIE(rc)) {
			baseStyle.setAttribute("zoom","1");
		}
		return baseStyle;
	}
	//
	// do the rendering here
	private void renderHtmlImpl(RenderingContext rc, Node parent, Component component, String partnerComponentId) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		createInitDirective(rc,component,partnerComponentId, fallbackStyle);

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(TITLEBAR_SERVICE);

		String elementId = rc.getElementId();

		CssRolloverStyleEx baseStyle = createBaseStyle(rc,component,fallbackStyle);
		
		Element divOuter = rc.createE("div");
		divOuter.setAttribute("id", elementId);
		divOuter.setAttribute("style", baseStyle.renderInline());
		
		rc.addStandardWebSupport(divOuter);
		parent.appendChild(divOuter);

		HtmlTable tableInner = new HtmlTable(rc.getDocument(), 0, 0, 0);
		tableInner.getTABLE().setAttribute("width", "100%");
		divOuter.appendChild(tableInner.getTABLE());

		Element td;
		td = tableInner.getTD();
		
		Component applicationComponent = (Component) rc.getRP(TitleBar.PROPERTY_APPLICATION_COMPONENT, fallbackStyle);
		Component helpComponent = (Component) rc.getRP(TitleBar.PROPERTY_HELP_COMPONENT, fallbackStyle);
		Component closeComponent = (Component) rc.getRP(TitleBar.PROPERTY_CLOSE_COMPONENT, fallbackStyle);
		ImageReference leftIcon = (ImageReference) rc.getRP(TitleBar.PROPERTY_LEFT_ICON, fallbackStyle);
		ImageReference rightIcon = (ImageReference) rc.getRP(TitleBar.PROPERTY_RIGHT_ICON, fallbackStyle);
		
		boolean isExpanded = ((TitleBar) component).isExpanded();
		
		CssStyleEx style;
		String title = getTitle(rc, fallbackStyle);
		if (isExpanded)
			title = getExpandedTitle(rc, fallbackStyle);
		//
		//-- the application component
		//
		if ( applicationComponent != null && applicationComponent.isRenderVisible()) {
			renderReplaceableChild(rc,rc.getServerComponentUpdate(),td,applicationComponent);
			td = tableInner.newTD();
		}
		//
		//-- left icon
		//
		ImageReference icon;
		if (leftIcon != null) {
			style = new CssStyleEx();
			style.setAttribute("cursor","pointer");
			icon = (ImageReference) rc.getRP(isExpanded ? TitleBar.PROPERTY_LEFT_EXPANDED_ICON : TitleBar.PROPERTY_LEFT_ICON, fallbackStyle);
			Element img = ImageManager.createImgE(rc,style,icon);
			img.setAttribute("id",elementId+"|LeftIcon");
			td.appendChild(img);
			
			td = tableInner.newTD();
		}
		//
		//-- title text
		//
		style = new CssStyleEx();
		style.setAttribute("cursor","pointer");
		style.setAttribute("white-space","pre");
		AlignmentRender.renderToStyle(style,(Alignment) rc.getRP(TitleBar.PROPERTY_TITLE_ALIGNMENT, fallbackStyle));
		
		//
		//-- title background
		//
		String titleBackground = isExpanded ? TitleBar.PROPERTY_EXPANDED_TITLE_BACKGROUND_IMAGE : TitleBar.PROPERTY_TITLE_BACKGROUND_IMAGE;
		Render.asFillImage(style,component,titleBackground,fallbackStyle, rc);
		
		td.setAttribute("id",elementId+"|Title");
		td.setAttribute("width","100%");
		td.setAttribute("style",style.renderInline());
		if (title != null ) {
			DomUtil.setElementText(td,title);
		}
		//
		//-- right icon
		//
		if (rightIcon != null) {
			td = tableInner.newTD();
			style = new CssStyleEx();
			style.setAttribute("cursor","pointer");
			icon = (ImageReference) rc.getRP(isExpanded ? TitleBar.PROPERTY_RIGHT_EXPANDED_ICON : TitleBar.PROPERTY_RIGHT_ICON, fallbackStyle);
			Element img = ImageManager.createImgE(rc,style,icon);
			img.setAttribute("id",elementId+"|RightIcon");
			td.appendChild(img);
			
		}
		//
		//-- help component
		//
		if ( helpComponent != null && helpComponent.isRenderVisible()) {
			td = tableInner.newTD();
			renderReplaceableChild(rc,rc.getServerComponentUpdate(),td,helpComponent);
		}
		//
		//-- close component
		//
		if ( closeComponent != null && closeComponent.isRenderVisible()) {
			td = tableInner.newTD();
			renderReplaceableChild(rc,rc.getServerComponentUpdate(),td,closeComponent);
		}
	}
	
	private String getTitle(RenderingContext rc, Style fallbackStyle) {
		String title = (String) rc.getRP(TitleBar.PROPERTY_TITLE, fallbackStyle);
		if (title != null)
			return title;
		title = (String) rc.getRP(TitleBar.PROPERTY_EXPANDED_TITLE, fallbackStyle);
		return (title==null) ? "" : title; 
	}
	
	private String getExpandedTitle(RenderingContext rc, Style fallbackStyle) {
		String title = (String) rc.getRP(TitleBar.PROPERTY_EXPANDED_TITLE, fallbackStyle);
		if (title != null)
			return title;
		title = (String) rc.getRP(TitleBar.PROPERTY_TITLE, fallbackStyle);
		return (title==null) ? "" : title; 
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * initialize the state of a TitleBar, performing tasks such as registering
	 * event listeners on the client and creating the JS object.
	 */
	protected void createInitDirective(RenderingContext rc, Component component, String partnerComponentId, Style fallbackStyle) {
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, 
				"EPTitleBar.MessageProcessor","init", new String[0], new String[0]);

		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemElement.setAttribute("eid", rc.getElementId());

		
		boolean isExpanded = ((TitleBar) component).isExpanded();
		itemElement.setAttribute("expanded", String.valueOf(isExpanded));
		if (partnerComponentId != null) {
			itemElement.setAttribute("partnerComponentId", partnerComponentId);
		}

		itemElement.setAttribute("title", getTitle(rc, fallbackStyle));
		itemElement.setAttribute("expandedTitle", getExpandedTitle(rc, fallbackStyle));
		
		CssRolloverStyleEx baseStyle = createBaseStyle(rc,component,fallbackStyle);
		itemElement.setAttribute("styleDefault", baseStyle.renderInline());
		
		if (component.isRenderEnabled() && rc.getRP(TitleBar.PROPERTY_ROLLOVER_ENABLED,fallbackStyle,true)) {
			itemElement.setAttribute("rolloverEnabled", "true");
			itemElement.setAttribute("styleRollover", baseStyle.renderRolloverSupportInline());
		}
		
		ImageReference leftIconImg = (ImageReference) rc.getRP(TitleBar.PROPERTY_LEFT_ICON, fallbackStyle);
		ImageReference leftExpandedIconImg = (ImageReference) rc.getRP(TitleBar.PROPERTY_LEFT_EXPANDED_ICON, fallbackStyle);
		ImageReference rightIconImg = (ImageReference) rc.getRP(TitleBar.PROPERTY_RIGHT_ICON, fallbackStyle);
		ImageReference rightExpandedIconImg = (ImageReference) rc.getRP(TitleBar.PROPERTY_RIGHT_EXPANDED_ICON, fallbackStyle);

		if (leftIconImg != null) {
			String leftIcon  = ImageManager.getURI(rc, leftIconImg);
			String leftExpandedIcon  = ImageManager.getURI(rc, leftExpandedIconImg);
			leftExpandedIcon = (leftExpandedIcon != null ? leftExpandedIcon : leftIcon);
			itemElement.setAttribute("leftIcon", leftIcon);
			itemElement.setAttribute("leftExpandedIcon", leftExpandedIcon);
		}


		if (rightIconImg != null) {
			String rightIcon  = ImageManager.getURI(rc, rightIconImg);
			String rightExpandedIcon  = ImageManager.getURI(rc, rightExpandedIconImg);
			rightExpandedIcon = (rightExpandedIcon != null ? rightExpandedIcon : rightIcon);
			itemElement.setAttribute("rightIcon", rightIcon);
			itemElement.setAttribute("rightExpandedIcon", rightExpandedIcon);
		}
		itemizedUpdateElement.appendChild(itemElement);
	}
	
	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate, nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc,update,component);
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(TITLEBAR_SERVICE.getId());
		createDisposeDirective(rc.getServerMessage(),component);
	}
	

	/**
	 * 
	 */
	protected void createDisposeDirective(ServerMessage serverMessage, Component component) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, 
				"EPTitleBar.MessageProcessor", "dispose",
				new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}
}
