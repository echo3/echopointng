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
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.SynchronizePeerFactory;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.service.JavaScriptService;
import nextapp.echo2.webrender.util.DomUtil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.ExpandableSection;
import echopointng.TitleBar;
import echopointng.model.ExpansionGroup;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

/**
 * <code>ExpandableSectionPeer</code> is the peer for <code>ExpandableSectionPeer</code>
 */
public class ExpandableSectionPeer extends AbstractEchoPointContainerPeer implements PropertyUpdateProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service ES_SERVICE = JavaScriptService.forResource("EPNG.ExpandableSection", "/echopointng/ui/resource/js/expandablesection.js");

	static {
		WebRenderServlet.getServiceRegistry().add(ES_SERVICE);
	}

	public ExpandableSectionPeer() {
		super();
		partialUpdateManager.add(ExpandableSection.EXPANDED_CHANGED_PROPERTY, new PartialUpdateParticipant() {
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
				ExpandableSection es = (ExpandableSection) update.getParent();
				boolean isExpanded = es.isExpanded();

				// an XML message directive please to tell the popup to expand!
				Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
						"EPExpandableSection.MessageProcessor", "expansion", new String[0], new String[0]);
				Element itemElement = rc.getServerMessage().getDocument().createElement("item");
				itemElement.setAttribute("eid", ContainerInstance.getElementId(es));
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
		if (ExpandableSection.EXPANDED_CHANGED_PROPERTY.equals(propertyName)) {
			String propertyValue = DomUtil.getElementText(propertyElement);
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, ExpandableSection.EXPANDED_CHANGED_PROPERTY, Boolean.valueOf(propertyValue));
		}
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointContainerPeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		return super.renderUpdate(rc,update,targetId);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		ExpandableSection es = (ExpandableSection) component;


		createInitDirective(rc,es, fallbackStyle);

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(ES_SERVICE);

		String elementId = rc.getElementId();
		CssStyleEx style = new CssStyleEx(component, fallbackStyle);

		Element divOuter = rc.createE("div");
		divOuter.setAttribute("id", elementId);
		divOuter.setAttribute("style", style.renderInline());
		rc.addStandardWebSupport(divOuter);
		
		parent.appendChild(divOuter);

		Element divTitleBar = rc.createE("div");
		divOuter.appendChild(divTitleBar);

		TitleBar tb = (TitleBar) rc.getRP(ExpandableSection.PROPERTY_TITLEBAR,fallbackStyle);
		boolean isExpanded = es.isExpanded();
		
		//
		//-- the title component
		//
		if ( tb != null && tb.isRenderVisible()) {
			divTitleBar.setAttribute("id",elementId+"|TitleBar");
			renderTitleBarChild(rc,rc.getServerComponentUpdate(),divTitleBar,tb,elementId);
		}
		//
		//-- expandable content area
		//
		Element divContent = rc.createE("div");
		divOuter.appendChild(divContent);
		divContent.setAttribute("id",elementId+"|Content");
		
		CssStyleEx styleContent = new CssStyleEx();
		if (! isExpanded) {
			styleContent.setAttribute("position","relative");
			styleContent.setAttribute("display","none");
		} else {
			styleContent.setAttribute("position","relative");
			styleContent.setAttribute("display","block");
		}
		Render.layoutFix(rc,styleContent);
		divContent.setAttribute("style",styleContent.renderInline());
		
		Component[] children = component.getVisibleComponents();
		for (int i = 0; i < children.length; i++) {
			if (children[i] != tb) {
				renderReplaceableChild(rc,rc.getServerComponentUpdate(),divContent,children[i]);
			}
		}
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate, nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc,update,component);
		 rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		 rc.getServerMessage().addLibrary(ES_SERVICE.getId());
		 createDisposeDirective(rc.getServerMessage(),component);
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * initialize the state of a ExpandableSection, performing tasks such as registering
	 * event listeners on the client and creating the JS object.
	 */
	protected void createInitDirective(RenderingContext rc, ExpandableSection es, Style fallbackStyle) {
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, 
				"EPExpandableSection.MessageProcessor","init", new String[0], new String[0]);

		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemElement.setAttribute("eid", rc.getElementId());
		itemElement.setAttribute("expanded", String.valueOf(es.isExpanded()));
		ExpansionGroup group = (ExpansionGroup) rc.getRP(ExpandableSection.PROPERTY_EXPANSION_GROUP, fallbackStyle);
		if (group != null) {
			itemElement.setAttribute("groupId", group.getRenderId());
			itemElement.setAttribute("accordionMode", String.valueOf(group.isAccordionMode()));
		}
		TitleBar tb = (TitleBar) rc.getRP(ExpandableSection.PROPERTY_TITLEBAR, fallbackStyle);
		if (tb != null) {
			itemElement.setAttribute("titleBarId", ContainerInstance.getElementId(tb));
		}
		
		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * 
	 */
	protected void createDisposeDirective(ServerMessage serverMessage, Component component) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, 
				"EPExpandableSection.MessageProcessor", "dispose",
				new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}
	
	/**
	 * Renders thr TitleBar as a child of the ExpandableSection, and uses its specialized
	 * form of the SyncPeer.
	 */
	protected void renderTitleBarChild(RenderingContext rc, ServerComponentUpdate update, Node parentNode, TitleBar child, String partnerComponentId) {
		Element containerDivElement = parentNode.getOwnerDocument().createElement(DEFAULT_CONTAINER_TAG);
		String containerId = getContainerId(child);
		containerDivElement.setAttribute("id", containerId);
		parentNode.appendChild(containerDivElement);
		TitleBarPeer tbPeer = (TitleBarPeer) SynchronizePeerFactory.getPeerForComponent(child.getClass());
		tbPeer.renderHtml(rc,rc.getServerComponentUpdate(),containerDivElement,child,partnerComponentId);
	}
	
}
