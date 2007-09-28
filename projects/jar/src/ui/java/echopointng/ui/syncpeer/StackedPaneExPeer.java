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
/**
 * This file was based on the TabPanePeer source code found in the
 * Echo2 Extras Project and hence is Copyright (C) 2005-2006 NextApp, Inc.
 */
/* 
 * This file is part of the Echo2 Extras Project.
 * Copyright (C) 2005-2006 NextApp, Inc.
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

import java.util.HashSet;
import java.util.Set;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Pane;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.LazyRenderContainer;
import nextapp.echo2.webcontainer.PartialUpdateManager;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.RenderState;
import nextapp.echo2.webcontainer.SynchronizePeerFactory;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.servermessage.DomUpdate;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Element;

import echopointng.EPNG;
import echopointng.StackedPaneEx;
import echopointng.able.BackgroundImageable;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

/**
 * <code>ComponentSynchronizePeer</code> implementation for synchronizing
 * <code>StackedPaneEx</code> components.
 */
public class StackedPaneExPeer implements ComponentSynchronizePeer, LazyRenderContainer, PropertyUpdateProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service STACKED_PANE_SERVICE = JavaScriptService.forResource("EP.StackedPaneEx",
			"/echopointng/ui/resource/js/stackedpaneex.js");

	static {
		WebRenderServlet.getServiceRegistry().add(STACKED_PANE_SERVICE);
	}

	/**
	 * <code>RenderState</code> implementation to store data on whether child
	 * components have been lazily rendered to client.
	 */
	private static class StackedPaneExRenderState implements RenderState {

		/**
		 * Render id of currently active pane.
		 */
		private String activePaneId;

		/**
		 * Set of rendered child components.
		 */
		private Set renderedChildren = new HashSet();
	}

	/**
	 * <code>PartialUpdateParticipant</code> to update active pane.
	 */
	private PartialUpdateParticipant activePaneUpdateParticipant = new PartialUpdateParticipant() {

		/**
		 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#renderProperty(nextapp.echo2.webcontainer.RenderContext,
		 *      nextapp.echo2.app.update.ServerComponentUpdate)
		 */
		public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
			renderSetActivePane(rc, update, (StackedPaneEx) update.getParent());
		}

		/**
		 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#canRenderProperty(nextapp.echo2.webcontainer.RenderContext,
		 *      nextapp.echo2.app.update.ServerComponentUpdate)
		 */
		public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
			return true;
		}
	};

	/**
	 * The <code>PartialUpdateManager</code> for this synchronization peer.
	 */
	private PartialUpdateManager partialUpdateManager;

	/**
	 * Default constructor.
	 */
	public StackedPaneExPeer() {
		partialUpdateManager = new PartialUpdateManager();
		partialUpdateManager.add(StackedPaneEx.STACK_CHANGED_PROPERTY, activePaneUpdateParticipant);
	}

	private String getRenderedActivePaneId(ContainerInstance ci, StackedPaneEx stackedPane) {
		StackedPaneExRenderState renderState = (StackedPaneExRenderState) ci.getRenderState(stackedPane);
		return renderState.activePaneId;
	}

	/**
	 * Performs configuration tasks related to the active pane of a
	 * <code>StackedPaneEx</code>.
	 * 
	 * @param ci
	 *            the relevant <code>ContainerInstance</code>
	 * @param stackedPane
	 *            the rendering <code>StackedPaneEx</code>
	 * @return true if the active pane requires rendering
	 */
	private boolean configureActivePane(ContainerInstance ci, StackedPaneEx stackedPane) {
		StackedPaneExRenderState renderState = (StackedPaneExRenderState) ci.getRenderState(stackedPane);

		int componentCount = stackedPane.getVisibleComponentCount();

		// Retrieve currently active component according to StackedPaneEx.
		Component activePane = stackedPane.peek();

		// If StackedPaneEx component does not specify a valid active pane,
		// then dont return anything.
		if (activePane == null && renderState.activePaneId != null) {
			//activePane = getChildByRenderId(stackedPane, renderState.activePaneId);
		}

		// If neither component nor render state have active pane information,
		// pick a pane to be active.
		if (activePane == null) {
			if (componentCount == 0) {
				// No panes available, return false indicating that active pane
				// DOES not require rendering.
				return false;
			}
		}

		// Store active pane in render state.
		if (activePane != null) {
			renderState.activePaneId = activePane.getRenderId();
		} else {
			renderState.activePaneId = null;
		}

		if (isLazyRenderEnabled(stackedPane)) {
			// Determine if active pane is rendered or not. If it is not
			// rendered, mark its state rendered and
			// return true to indicate that it should be rendered.
			if (!isRendered(ci, stackedPane, activePane)) {
				setRendered(ci, stackedPane, activePane);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	public String getContainerId(Component child) {
		return ContainerInstance.getElementId(child.getParent()) + "_content_" + child.getRenderId();
	}

	/**
	 * Returns the child component with the specified render id.
	 * 
	 * @param stackedPane
	 *            the <code>StackedPaneEx</code>
	 * @param renderId
	 *            the render identifier
	 * @return the child component, or null if no child exists with the
	 *         specified id.
	 */
	private Component getChildByRenderId(StackedPaneEx stackedPane, String renderId) {
		Component[] children = stackedPane.getVisibleComponents();
		for (int i = 0; i < children.length; ++i) {
			if (children[i].getRenderId().equals(renderId)) {
				return children[i];
			}
		}
		return null;
	}

	/**
	 * Determines if a <code>StackedPaneEx</code> should be lazy-rendered.
	 * 
	 * @param stackedPane
	 *            the <code>StackedPaneEx</code> to query
	 * @return true if lazy-rendering should be enabled
	 */
	private boolean isLazyRenderEnabled(StackedPaneEx stackedPane) {
		Boolean lazyRenderEnabled = (Boolean) stackedPane.getRenderProperty(StackedPaneEx.PROPERTY_LAZY_RENDER_ENABLED);
		return lazyRenderEnabled == null ? true : lazyRenderEnabled.booleanValue();
	}

	/**
	 * @see nextapp.echo2.webcontainer.LazyRenderContainer#isRendered(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, nextapp.echo2.app.Component)
	 */
	public boolean isRendered(ContainerInstance ci, Component parent, Component child) {
		if (!isLazyRenderEnabled((StackedPaneEx) parent)) {
			return true;
		}
		StackedPaneExRenderState renderState = (StackedPaneExRenderState) ci.getRenderState(parent);
		if (renderState == null) {
			// Entire component has not been rendered, thus child has not been
			// rendered.
			return false;
		}

		return renderState.renderedChildren.contains(child);
	}

	/**
	 * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
		// String propertyName =
		// propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderAdd(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderAdd(RenderContext rcIn, ServerComponentUpdate update, String targetId, Component component) {
		RenderingContext rc = new RenderingContext(rcIn, update, component);

		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(STACKED_PANE_SERVICE.getId());
		StackedPaneEx stackedPane = (StackedPaneEx) component;

		ContainerInstance ci = rc.getContainerInstance();
		resetRenderState(ci, stackedPane);

		configureActivePane(ci, stackedPane);

		renderInitDirective(rc, stackedPane, targetId);

		Component[] children = stackedPane.getVisibleComponents();
		for (int i = 0; i < children.length; ++i) {
			renderAddPaneDirective(rc, update, stackedPane, children[i]);
		}
		boolean lazyRenderEnabled = isLazyRenderEnabled(stackedPane);
		for (int i = 0; i < children.length; ++i) {
			if (!lazyRenderEnabled || isRendered(ci, stackedPane, children[i])) {
				renderChild(rc, update, stackedPane, children[i]);
			}
		}
	}

	private void renderAddChildren(RenderContext rc, ServerComponentUpdate update, boolean activePaneRenderRequired) {
		StackedPaneEx stackedPane = (StackedPaneEx) update.getParent();
		ContainerInstance ci = rc.getContainerInstance();

		Component activePane = null;
		if (activePaneRenderRequired) {
			StackedPaneExRenderState renderState = (StackedPaneExRenderState) ci.getRenderState(stackedPane);
			activePane = getChildByRenderId(stackedPane, renderState.activePaneId);
			if (activePane == null) {
				activePaneRenderRequired = false;
			}
		}

		if (update.hasAddedChildren()) {
			Component[] addedChildren = update.getAddedChildren();
			Component[] children = stackedPane.getVisibleComponents();

			// Iterating through arrays and checking for reference equality is
			// used here (versus loading daddedChildren
			// into a hashpanele) because we'll be dealing with very small array
			// lengths, typically less than 10.
			for (int i = 0; i < children.length; ++i) {
				for (int j = 0; j < addedChildren.length; ++j) {
					if (children[i] == addedChildren[j]) {
						renderAddPaneDirective(rc, update, stackedPane, children[i]);
						break;
					}
				}
			}

			boolean lazyRenderEnabled = isLazyRenderEnabled(stackedPane);

			// Add children.
			for (int i = 0; i < addedChildren.length; ++i) {
				if (!lazyRenderEnabled || isRendered(ci, stackedPane, addedChildren[i])) {
					renderChild(rc, update, stackedPane, addedChildren[i]);
					if (addedChildren[i] == activePane) {
						activePaneRenderRequired = false;
					}
				}
			}
		}

		if (activePaneRenderRequired) {
			renderChild(rc, update, stackedPane, activePane);
		}
	}

	private void renderAddPaneDirective(RenderContext rc, ServerComponentUpdate update, StackedPaneEx stackedPane, Component child) {
		ContainerInstance ci = rc.getContainerInstance();
		boolean rendered = !isLazyRenderEnabled(stackedPane) || isRendered(ci, stackedPane, child);

		String elementId = ContainerInstance.getElementId(stackedPane);
		Element addPartElement = rc.getServerMessage().appendPartDirective(ServerMessage.GROUP_ID_UPDATE, "EPStackedPaneEx.MessageProcessor",
				"add-pane");
		addPartElement.setAttribute("eid", elementId);
		addPartElement.setAttribute("pane-id", child.getRenderId());
		addPartElement.setAttribute("pane-index", Integer.toString(stackedPane.indexOf(child)));
		if (rendered) {
			addPartElement.setAttribute("rendered", "true");
		}
		if (child instanceof Pane) {
			addPartElement.setAttribute("pane", "true");
		}
	}

	/**
	 * Renders an individual child component of the <code>StackedPaneEx</code>.
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param update
	 *            the <code>ServerComponentUpdate</code> being performed
	 * @param child
	 *            The child <code>Component</code> to be rendered
	 */
	private void renderChild(RenderContext rc, ServerComponentUpdate update, StackedPaneEx stackedPane, Component child) {
		ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
		syncPeer.renderAdd(rc, update, getContainerId(child), child);
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(
	 *      nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(STACKED_PANE_SERVICE.getId());
		renderDisposeDirective(rc, (StackedPaneEx) component);
	}

	/**
	 * Renders a dispose directive.
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param stackedPane
	 *            the <code>StackedPaneEx</code> being rendered
	 */
	private void renderDisposeDirective(RenderContext rc, StackedPaneEx stackedPane) {
		String elementId = ContainerInstance.getElementId(stackedPane);
		ServerMessage serverMessage = rc.getServerMessage();
		Element initElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPStackedPaneEx.MessageProcessor", "dispose");
		initElement.setAttribute("eid", elementId);
	}

	/**
	 * Renders an initialization directive.
	 * 
	 * @param rc
	 *            the relevant <code>RenderingContext</code>
	 * @param stackedPane
	 *            the <code>StackedPaneEx</code> being rendered
	 */
	private void renderInitDirective(RenderingContext rc, StackedPaneEx stackedPane, String targetId) {
		String elementId = ContainerInstance.getElementId(stackedPane);
		ServerMessage serverMessage = rc.getServerMessage();
		Element partElement = serverMessage.addPart(ServerMessage.GROUP_ID_UPDATE, "EPStackedPaneEx.MessageProcessor");
		Element initElement = serverMessage.getDocument().createElement("init");
		initElement.setAttribute("container-eid", targetId);
		initElement.setAttribute("eid", elementId);

		if (!stackedPane.isRenderEnabled()) {
			initElement.setAttribute("enabled", "false");
		}

		Style fallbackStyle = EPNG.getFallBackStyle(stackedPane);

		// render a style for the component via the normal mechanism and send it
		// down
		CssStyleEx style = new CssStyleEx(stackedPane, fallbackStyle);
		Render.asBackgroundImageable(rc, style, (BackgroundImageable) stackedPane, fallbackStyle);
		Render.layoutFix(rc, style);

		initElement.setAttribute("style", style.renderInline());

		Element standardWebSupportE = rc.getDocument().createElement("standardWebSupport");
		rc.addStandardWebSupport(standardWebSupportE);
		initElement.appendChild(standardWebSupportE);

		String activePaneId = getRenderedActivePaneId(rc.getContainerInstance(), stackedPane);
		if (activePaneId != null) {
			initElement.setAttribute("active-pane", activePaneId);
		}

		partElement.appendChild(initElement);
	}

	/**
	 * Renders directives to remove any children from the client that were
	 * removed in the specified <code>ServerComponentUpdate</code>.
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param update
	 *            the <code>ServerComponentUpdate</code> to process
	 */
	private void renderRemoveChildren(RenderContext rc, ServerComponentUpdate update) {
		StackedPaneEx stackedPane = (StackedPaneEx) update.getParent();
		Component[] removedChildren = update.getRemovedChildren();
		for (int i = 0; i < removedChildren.length; ++i) {
			renderRemovePaneDirective(rc, update, stackedPane, removedChildren[i]);
		}
	}

	/**
	 * Renders a directive to the <code>ServerMessage</code> to remove a pane
	 * from a <code>StackedPaneEx</code>.
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param update
	 *            the <code>ServerComponentUpdate</code> describing the change
	 * @param stackedPane
	 *            the <code>StackedPaneEx</code> being updated
	 * @param child
	 *            the child <code>Component</code> being removed form the
	 *            <code>StackedPaneEx</code>
	 */
	private void renderRemovePaneDirective(RenderContext rc, ServerComponentUpdate update, StackedPaneEx stackedPane, Component child) {
		String elementId = ContainerInstance.getElementId(stackedPane);
		Element removePaneElement = rc.getServerMessage().appendPartDirective(ServerMessage.GROUP_ID_REMOVE, "EPStackedPaneEx.MessageProcessor",
				"remove-pane");
		removePaneElement.setAttribute("eid", elementId);
		removePaneElement.setAttribute("pane-id", child.getRenderId());
	}

	/**
	 * Updates the active pane of a pre-existing <code>StackedPaneEx</code> on
	 * the client. This method will render the pane's component hierarchy to the
	 * client if it has not yet been loaded, and then render a set-active-pane
	 * directive to select the pane.
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param update
	 *            the <code>ServerComponentUpdate</code> describing the change
	 * @param stackedPane
	 *            the <code>StackedPaneEx</code> being updated
	 */
	private void renderSetActivePane(RenderContext rc, ServerComponentUpdate update, StackedPaneEx stackedPane) {
		ContainerInstance ci = rc.getContainerInstance();

		boolean activePaneRenderRequired = configureActivePane(ci, stackedPane);

		Component activePane = null;
		if (activePaneRenderRequired) {
			StackedPaneExRenderState renderState = (StackedPaneExRenderState) ci.getRenderState(stackedPane);
			activePane = getChildByRenderId(stackedPane, renderState.activePaneId);
			if (activePane != null) {
				renderChild(rc, update, stackedPane, activePane);
			}
		}

		renderSetActivePaneDirective(rc, update, stackedPane);
	}

	/**
	 * Renders a directive to the <code>ServerMessage</code> to set the active
	 * pane of a pre-exisiting <code>StackedPaneEx</code>
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param update
	 *            the <code>ServerComponentUpdate</code> describing the change
	 * @param stackedPane
	 *            the <code>StackedPaneEx</code> being updated
	 */
	private void renderSetActivePaneDirective(RenderContext rc, ServerComponentUpdate update, StackedPaneEx stackedPane) {
		Component activePane = null;
		StackedPaneExRenderState renderState = (StackedPaneExRenderState) rc.getContainerInstance().getRenderState(stackedPane);
		activePane = getChildByRenderId(stackedPane, renderState.activePaneId);

		String elementId = ContainerInstance.getElementId(stackedPane);
		Element setActivePaneElement = rc.getServerMessage().appendPartDirective(ServerMessage.GROUP_ID_UPDATE, "EPStackedPaneEx.MessageProcessor",
				"set-active-pane");
		setActivePaneElement.setAttribute("eid", elementId);
		if (activePane != null) {
			setActivePaneElement.setAttribute("active-pane", activePane.getRenderId());
		}
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderUpdate(
	 *      nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		ContainerInstance ci = rc.getContainerInstance();
		StackedPaneEx stackedPane = (StackedPaneEx) update.getParent();

		// Determine if fully replacing the component is required.
		boolean fullReplace = false;
		if (update.hasUpdatedLayoutDataChildren()) {
			fullReplace = true;
		} else if (update.hasUpdatedProperties()) {
			if (!partialUpdateManager.canProcess(rc, update)) {
				fullReplace = true;
			}
		}

		if (fullReplace) {
			// Perform full update.
			DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
			renderAdd(rc, update, targetId, update.getParent());
		} else {

			// Perform incremental updates.
			if (update.hasRemovedChildren() || update.hasAddedChildren()) {

				boolean activePaneRenderRequired = configureActivePane(ci, stackedPane);
				if (update.hasRemovedChildren()) {
					renderRemoveChildren(rc, update);
				}
				if (update.hasAddedChildren() || activePaneRenderRequired) {
					renderAddChildren(rc, update, activePaneRenderRequired);
				}
				renderSetActivePaneDirective(rc, update, stackedPane);
			}

			if (update.hasUpdatedProperties()) {
				partialUpdateManager.process(rc, update);
			}

		}

		return fullReplace;
	}

	/**
	 * Resets the <code>RenderState</code> of a <code>StackedPaneEx</code>
	 * in the <code>ContainerInstance</code>. Invoked when a
	 * <code>StackedPaneEx</code> is initially rendered to the client.
	 * 
	 * @param ci
	 *            the relevant <code>ContainerInstance</code>
	 * @param stackedPane
	 *            the <code>StackedPaneEx</code> being rendered
	 */
	private void resetRenderState(ContainerInstance ci, StackedPaneEx stackedPane) {
		StackedPaneExRenderState renderState = new StackedPaneExRenderState();
		ci.setRenderState(stackedPane, renderState);
	}

	/**
	 * Sets a flag in the <code>RenderState</code> to indicate that a
	 * particular pane of a <code>StackedPaneEx</code> has been/is being
	 * rendered to the client. This method is used to facilitate lazy-rendering,
	 * ensuring each pane of a <code>StackedPaneEx</code> is rendered tot he
	 * client only once.
	 * 
	 * @param ci
	 *            the relevant <code>ContainerInstance</code>
	 * @param stackedPane
	 *            the <code>StackedPaneEx</code> being rendered
	 * @param child
	 *            the child pane component
	 */
	private void setRendered(ContainerInstance ci, StackedPaneEx stackedPane, Component child) {
		StackedPaneExRenderState renderState = (StackedPaneExRenderState) ci.getRenderState(stackedPane);
		if (renderState == null) {
			renderState = new StackedPaneExRenderState();
			ci.setRenderState(stackedPane, renderState);
		}
		renderState.renderedChildren.add(child);
	}
}
