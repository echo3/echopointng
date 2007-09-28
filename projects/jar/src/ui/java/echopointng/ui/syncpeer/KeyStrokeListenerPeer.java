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
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.KeyStrokeListener;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.RenderingContext;

/** 
 * <code>KeyStrokeListenerPeer</code> is a peer for <code>KeyStrokeListener</code> 
 */

public class KeyStrokeListenerPeer extends AbstractEchoPointPeer implements  ActionProcessor {
	
	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service KEYSTROKE_SERVICE = JavaScriptService.forResource("EPNG.KeyStroke", "/echopointng/ui/resource/js/keystroke.js");
	static {
		WebRenderServlet.getServiceRegistry().add(KEYSTROKE_SERVICE);
	}

	/**
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	public String getContainerId(Component child) {
		throw new UnsupportedOperationException("Component does not support children.");
	}
	
	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance, nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		String actionValue = actionElement.getAttribute(ActionProcessor.ACTION_VALUE);
		Integer key = Integer.valueOf(actionValue);
        ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, KeyStrokeListener.KEYSTROKE_CHANGED_PROPERTY, key);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext, org.w3c.dom.Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parentNode, Component component) {
		KeyStrokeListener ks = (KeyStrokeListener) component;

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(KEYSTROKE_SERVICE);
		
		Element bdo = rc.createE("bdo");
		bdo.setAttribute("id",rc.getElementId());
		parentNode.appendChild(bdo);
		
		createInitDirective(rc,ks);
	}
	
	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate, nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc, update, component);
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(KEYSTROKE_SERVICE.getId());
		createDisposeDirective(rc.getServerMessage(),component);
	}

	/**
	 * 
	 */
	protected void createDisposeDirective(ServerMessage serverMessage, Component component) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, 
				"EPKeyStroke.MessageProcessor", "dispose",new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
		
		serverMessage.addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		serverMessage.addLibrary(KEYSTROKE_SERVICE.getId());
		
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> 
	 */
	protected void createInitDirective(RenderingContext rc, KeyStrokeListener keyStrokeListener) {
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, 
				"EPKeyStroke.MessageProcessor","init", new String[0], new String[0]);
	
		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemizedUpdateElement.appendChild(itemElement);

		itemElement.setAttribute("eid", rc.getElementId());
		itemElement.setAttribute("enabled", String.valueOf(keyStrokeListener.isRenderEnabled()));
		itemElement.setAttribute("serverNotify", String.valueOf(keyStrokeListener.hasActionListeners()));
		itemElement.setAttribute("cancelMode", String.valueOf(rc.getRP(KeyStrokeListener.PROPERTY_CANCEL_MODE,false)));
		
		Component target = (Component) rc.getRP(KeyStrokeListener.PROPERTY_TARGET);
		if (target != null) {
			itemElement.setAttribute("targetid", ContainerInstance.getElementId(target));
		}
		
		StringBuffer keyCombinations = new StringBuffer();
		int keys[] = keyStrokeListener.getKeyCombinations();
		for (int i = 0; i < keys.length; i++) {
			int key = keys[i];
			keyCombinations.append(key);
			if (i < keys.length-1) {
				keyCombinations.append('|');
			}
		} 
		itemElement.setAttribute("keyCombinations", keyCombinations.toString());
	}
	
	

}
