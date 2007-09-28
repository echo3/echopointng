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

import java.util.Iterator;
import java.util.List;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.PartialUpdateManager;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import echopointng.HistoryMonitor;
import echopointng.history.HistoryState;
import echopointng.ui.resource.Resources;

/**
 * The rendering peer for HistoryMonitor
 * 
 */
public class HistoryMonitorPeer implements ComponentSynchronizePeer, ActionProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service HISTORY_MONITOR_SERVICE = JavaScriptService.forResource("EPNG.HistoryMonitor",
			"/echopointng/ui/resource/js/historymonitor.js");
	static {
		WebRenderServlet.getServiceRegistry().add(HISTORY_MONITOR_SERVICE);
	}

	private PartialUpdateManager partialUpdateManager;

	/**
	 * Constructs a <code>HistoryMonitorPeer</code>
	 */
	public HistoryMonitorPeer() {
		partialUpdateManager = new PartialUpdateManager();
		partialUpdateManager.add(HistoryMonitor.HISTORY_CHANGED_PROPERTY, new PartialUpdateParticipant() {

			/**
			 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#canRenderProperty(nextapp.echo2.webcontainer.RenderContext,
			 *      nextapp.echo2.app.update.ServerComponentUpdate)
			 */
			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}

			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				Document doc = rc.getServerMessage().getDocument();

				HistoryMonitor historyMonitor = (HistoryMonitor) update.getParent();
				List pendingHistory = historyMonitor.getPendingHistory();
				if (pendingHistory.isEmpty()) {
					return;
				}
				Element itemElement = doc.createElement("item");
				itemElement.setAttribute("eid", ContainerInstance.getElementId(historyMonitor));
				for (Iterator iter = pendingHistory.iterator(); iter.hasNext();) {
					HistoryState undoRedo = (HistoryState) iter.next();
					Element historyElement = doc.createElement("history");
					historyElement.setAttribute("historyHash", undoRedo.historyHash());
					itemElement.appendChild(historyElement);
				}

				Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
						"EPHistoryMonitor.MessageProcessor", "addHistory", new String[0], new String[0]);
				itemizedUpdateElement.appendChild(itemElement);
			}
		});

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
		throw new IllegalStateException("HistoryMonitorPeer does not support children directly.  Use AbstractEchoPointContainerPeer instead");
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderAdd(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
		createInitDirective(rc, component);
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

	protected void createInitDirective(RenderContext rc, Component component) {
		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		serverMessage.addLibrary(HISTORY_MONITOR_SERVICE.getId());

		// Create the element containing initialisation parameters for the
		// ComboBox
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPHistoryMonitor.MessageProcessor",
				"init", new String[0], new String[0]);

		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));

		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(HISTORY_MONITOR_SERVICE.getId());
		createDisposeDirective(rc.getServerMessage(), component);
	}

	protected void createDisposeDirective(ServerMessage serverMessage, Component component) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPHistoryMonitor.MessageProcessor",
				"dispose", new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}
}
