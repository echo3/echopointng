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
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.PartialUpdateManager;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import echopointng.EPNG;
import echopointng.NativeWindow;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.RenderingContext;

/**
 * The rendering peer for NativeWindow
 * 
 */
public class NativeWindowPeer implements ComponentSynchronizePeer, PropertyUpdateProcessor, ActionProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service NATIVE_WINDOW_SERVICE = JavaScriptService.forResource("EPNG.NativeWindow",
			"/echopointng/ui/resource/js/nativewindow.js");
	static {
		WebRenderServlet.getServiceRegistry().add(NATIVE_WINDOW_SERVICE);
	}

	private PartialUpdateManager partialUpdateManager;

	/**
	 * Constructs a <code>NativeWindowPeer</code>
	 */
	public NativeWindowPeer() {
		partialUpdateManager = new PartialUpdateManager();

		PartialUpdateParticipant updateParticipant = new PartialUpdateParticipant() {
			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}

			public void renderProperty(RenderContext rcIn, ServerComponentUpdate update) {
				Component component = update.getParent();
				RenderingContext rc = new RenderingContext(rcIn, update, component);
				Style fallbackStyle = EPNG.getFallBackStyle(component);
				Document doc = rc.getServerMessage().getDocument();

				Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
						"EPNativeWindow.MessageProcessor", "update", new String[0], new String[0]);

				Element itemElement = doc.createElement("item");
				itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
				String propertyNames[] = update.getUpdatedPropertyNames();
				for (int i = 0; i < propertyNames.length; i++) {
					String propertyName = propertyNames[i];
					Object newValue = rc.getRP(propertyName, fallbackStyle, null);
					if (newValue != null) {
						itemElement.setAttribute(propertyName, String.valueOf(newValue));
					}
				}
				itemizedUpdateElement.appendChild(itemElement);
			}
		};

		partialUpdateManager.add(NativeWindow.PROPERTY_DEPENDENT, updateParticipant);
		partialUpdateManager.add(NativeWindow.PROPERTY_MODAL, updateParticipant);
		partialUpdateManager.add(NativeWindow.PROPERTY_OPEN, updateParticipant);
		partialUpdateManager.add(NativeWindow.PROPERTY_URL, updateParticipant);
		partialUpdateManager.add(NativeWindow.PROPERTY_LEFT, updateParticipant);
		partialUpdateManager.add(NativeWindow.PROPERTY_TOP, updateParticipant);
		partialUpdateManager.add(NativeWindow.PROPERTY_HEIGHT, updateParticipant);
		partialUpdateManager.add(NativeWindow.PROPERTY_WIDTH, updateParticipant);
		partialUpdateManager.add(NativeWindow.PROPERTY_CENTERED, updateParticipant);
		partialUpdateManager.add(NativeWindow.PROPERTY_FEATURES, updateParticipant);

	}

	/**
	 * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
		if (NativeWindow.PROPERTY_OPEN.equals(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME))) {
			String newValue = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE);
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, NativeWindow.PROPERTY_OPEN, new Boolean(newValue));
		}
	}

	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		String actionName = actionElement.getAttribute(ActionProcessor.ACTION_NAME);
		String actionValue = actionElement.getAttribute(ActionProcessor.ACTION_VALUE);
		ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, actionName, actionValue);
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	public String getContainerId(Component child) {
		throw new IllegalStateException("NativeWindowPeer does not support children directly.  Use AbstractEchoPointContainerPeer instead");
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderAdd(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
		createInitDirective(new RenderingContext(rc, update, component), component);
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		if (update.hasUpdatedProperties()) {
			if (partialUpdateManager.canProcess(rc, update)) {
				partialUpdateManager.process(rc, update);
			}
		}
		return true;
	}

	private void createInitDirective(RenderingContext rc, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		serverMessage.addLibrary(NATIVE_WINDOW_SERVICE.getId());

		// Create the element containing initialisation parameters for the
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPNativeWindow.MessageProcessor",
				"init", new String[0], new String[0]);

		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));

		itemElement.setAttribute("dependent", rc.getRP(NativeWindow.PROPERTY_DEPENDENT, fallbackStyle, false) ? "true" : "false");
		itemElement.setAttribute("modal", rc.getRP(NativeWindow.PROPERTY_MODAL, fallbackStyle, false) ? "true" : "false");
		itemElement.setAttribute("open", rc.getRP(NativeWindow.PROPERTY_OPEN, fallbackStyle, false) ? "true" : "false");
		itemElement.setAttribute("centered", rc.getRP(NativeWindow.PROPERTY_CENTERED, fallbackStyle, false) ? "true" : "false");
		itemElement.setAttribute("features", String.valueOf(rc.getRP(NativeWindow.PROPERTY_FEATURES,fallbackStyle,0)));

		itemElement.setAttribute("url", (String) rc.getRP(NativeWindow.PROPERTY_URL, ""));

		if (rc.getRP(NativeWindow.PROPERTY_LEFT) != null) {
			itemElement.setAttribute("left", (String) rc.getRP(NativeWindow.PROPERTY_LEFT));
		}
		if (rc.getRP(NativeWindow.PROPERTY_TOP) != null) {
			itemElement.setAttribute("top", (String) rc.getRP(NativeWindow.PROPERTY_TOP));
		}
		if (rc.getRP(NativeWindow.PROPERTY_HEIGHT) != null) {
			itemElement.setAttribute("height", (String) rc.getRP(NativeWindow.PROPERTY_HEIGHT));
		}
		if (rc.getRP(NativeWindow.PROPERTY_WIDTH) != null) {
			itemElement.setAttribute("width", (String) rc.getRP(NativeWindow.PROPERTY_WIDTH));
		}

		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(NATIVE_WINDOW_SERVICE.getId());
		createDisposeDirective(rc.getServerMessage(), component);
	}

	private void createDisposeDirective(ServerMessage serverMessage, Component component) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPNativeWindow.MessageProcessor",
				"dispose", new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}
}
