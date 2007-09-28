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
 * decision by deletoing the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */
package echopointng.ui.syncpeer;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.PartialUpdateManager;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.RenderState;
import nextapp.echo2.webcontainer.SynchronizePeerFactory;
import nextapp.echo2.webcontainer.partialupdate.ColorUpdate;
import nextapp.echo2.webrender.servermessage.DomUpdate;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.ComponentEx;
import echopointng.ui.util.AblePartialUpdater;
import echopointng.ui.util.RenderingContext;

/**
 * <code>AbstractEchoPointPeer</code> is a base class for peers that dont
 * necessarily adhere to container semantics
 */
public abstract class AbstractEchoPointPeer implements DomUpdateSupport, ComponentSynchronizePeer {

	/**
	 * This is the default tag used as a HTML container for child elements.
	 */
	protected static final String DEFAULT_CONTAINER_TAG = "bdo";

	/**
	 * There is a <code>PartialUpdateManager</code> available for subclass
	 * peers to use to for partial update support
	 * <p>
	 * By default it has an AblePartialUpdater added to it to handle
	 * echopoint.able.* interfaces as well as a background and foreground
	 * color partial update support.
	 *  
	 */
	protected PartialUpdateManager partialUpdateManager;

	/**
	 * Constructs a <code>AbstractEchoPointPeer</code>
	 */
	public AbstractEchoPointPeer() {
		partialUpdateManager = new PartialUpdateManager();
		AblePartialUpdater.addToUpdateManager(partialUpdateManager);
		partialUpdateManager.add(Component.PROPERTY_FOREGROUND, new ColorUpdate(Component.PROPERTY_FOREGROUND, null, ColorUpdate.CSS_COLOR));
		partialUpdateManager.add(Component.PROPERTY_BACKGROUND,
				new ColorUpdate(Component.PROPERTY_BACKGROUND, null, ColorUpdate.CSS_BACKGROUND_COLOR));
		partialUpdateManager.add(ComponentEx.PROPERTY_HIDDEN,new PartialUpdateParticipant() {
			
			private Boolean getHiddenFlag(ServerComponentUpdate update) {
				Object hidden = update.getParent().getRenderProperty(ComponentEx.PROPERTY_HIDDEN, Boolean.valueOf(false));
				if (hidden instanceof Boolean) {
					return (Boolean) hidden;
				}
				return null;
			}
			/**
			 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#canRenderProperty(nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate)
			 */
			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return getHiddenFlag(update) != null;
			}
			
			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
		        String elementId = ContainerInstance.getElementId(update.getParent());
				Boolean hidden = getHiddenFlag(update);
				if (hidden.booleanValue()) {
					DomUpdate.renderStyleUpdate(rc.getServerMessage(), elementId, "display", "none");
				} else {
					DomUpdate.renderStyleUpdate(rc.getServerMessage(), elementId, "display", "");
				}
			}
		});

	}

	/**
	 * Returns an identifier in the form "parentId_contains_childId".
	 * 
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	public String getContainerId(Component child) {
		throw new IllegalStateException("AbstractEchoPointPeer does not support children directly.  Use AbstractEchoPointContainerPeer instead");
	}

	/**
	 * Removes any render state that is associated with the component.
	 * <p>
	 * Remember to call <code>super.renderDispose()</code> if you overrride
	 * this method.
	 * 
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		disposeRenderState(rc,component);
	}

	/**
	 * This calls renderHtml() to render the entire peer output.
	 * 
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#renderAdd(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
		Element domAddElementDirective = DomUpdate.renderElementAdd(rc.getServerMessage());
		DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
		renderHtml(rc, update, htmlFragment, component);
		DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElementDirective, targetId, htmlFragment);
	}

	/**
	 * This method ends up calling
	 * <code>renderUpdateBaseImpl(rc, update, targetId, fullReplace)</code>
	 * method with <code>fullReplace</code> as true. In other words by default
	 * it does coarse grain replacement of children.
	 * 
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
       boolean fullReplace = false;
		if (update.hasUpdatedProperties()) {
			if (partialUpdateManager.canProcess(rc, update)) {
				partialUpdateManager.process(rc, update);
			} else {
				fullReplace = true;
			}
		}
		return renderUpdateBaseImpl(rc, update, targetId, fullReplace);
	}

	/**
	 * If a full replace is to be done then this will remove itself from the
	 * parent and redraw everything otherwise it will replace just the changed
	 * children.
	 * 
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	protected boolean renderUpdateBaseImpl(RenderContext rc, ServerComponentUpdate update, String targetId, boolean fullReplace) {
		if (fullReplace) {
			// Perform full update.
			String removeTagetId = ContainerInstance.getElementId(update.getParent());
			DomUpdate.renderElementRemove(rc.getServerMessage(), removeTagetId);
			renderAdd(rc, update, targetId, update.getParent());
		} else {
			renderUpdateRemoveChildren(rc, update);
			renderUpdateAddChildren(rc, update);
		}
		return fullReplace;
	}

	/**
	 * Renders child components which were added to a
	 * <code>AbstractEchoPointPeer</code>, as described in the provided
	 * <code>ServerComponentUpdate</code>.
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param update
	 *            the update
	 */
	protected void renderUpdateAddChildren(RenderContext rc, ServerComponentUpdate update) {
		Element domAddElementDirective = DomUpdate.renderElementAdd(rc.getServerMessage());
		Component parentComponent = update.getParent();
		String elementId = ContainerInstance.getElementId(parentComponent);
		Component[] components = parentComponent.getVisibleComponents();
		Component[] addedChildren = update.getAddedChildren();

		for (int componentIndex = components.length - 1; componentIndex >= 0; --componentIndex) {
			boolean childFound = false;
			for (int addedChildrenIndex = 0; !childFound && addedChildrenIndex < addedChildren.length; ++addedChildrenIndex) {
				if (addedChildren[addedChildrenIndex] == components[componentIndex]) {
					DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
					renderReplaceableChild(rc, update, htmlFragment, components[componentIndex]);
					if (componentIndex == components.length - 1) {
						DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElementDirective, elementId, htmlFragment);
					} else {
						DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElementDirective, elementId, elementId + "_contains_"
								+ ContainerInstance.getElementId(components[componentIndex + 1]), htmlFragment);
					}
					childFound = true;
				}
			}
		}

	}

	/**
	 * Renders removal operations for child components which were removed from a
	 * <code>AbstractEchoPointPeer</code>, as described in the provided
	 * <code>ServerComponentUpdate</code>.
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param update
	 *            the update
	 */
	protected void renderUpdateRemoveChildren(RenderContext rc, ServerComponentUpdate update) {
		Component[] removedChildren = update.getRemovedChildren();
		for (int i = 0; i < removedChildren.length; ++i) {
			String containerId = ContainerInstance.getElementId(update.getParent()) + "_contains_"
					+ ContainerInstance.getElementId(removedChildren[i]);
			DomUpdate.renderElementRemove(rc.getServerMessage(), containerId);
		}
	}

	/**
	 * Renders an individual child component of the
	 * <code>AbstractEchoPointPeer</code>. It is deemed replaceable in the
	 * sense it gets enveloped in a tag container and hence can be DOM replaced
	 * directly via the Echo2 framework.
	 * <p>
	 * Each child is wrapped in an HTML element tag which can be specified by 
	 * overriding getContainerTag().
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param update
	 *            the <code>ServerComponentUpdate</code> being performed
	 * @param parentNode
	 *            a document fragment to hold the new XHTML
	 * @param child
	 *            the child <code>Component</code> to be rendered
	 * @return - the new Element that has been used to surround the replaceable
	 *         child component.
	 */
	protected Element renderReplaceableChild(RenderContext rc, ServerComponentUpdate update, Node parentNode, Component child) {
		Element containerTagElement = parentNode.getOwnerDocument().createElement(getContainerTag());
		String containerId = getContainerId(child);
		containerTagElement.setAttribute("id", containerId);
		parentNode.appendChild(containerTagElement);
		ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
		if (syncPeer instanceof DomUpdateSupport) {
			((DomUpdateSupport) syncPeer).renderHtml(rc, update, containerTagElement, child);
		} else {
			syncPeer.renderAdd(rc, update, containerId, child);
		}
		return containerTagElement;
	}

	/**
	 * Returns a String to be used as the tag name for the container element wrapping child component elements.
	 * <p>
	 * DEFAULT_CONTAINER_TAG is returned unless overridden
	 * 
	 * @return a String tag name  
	 */
	protected String getContainerTag() {
		return DEFAULT_CONTAINER_TAG;
	}
	
	/**
	 * This sets up the EchoPoint RenderingContext helper and then calls the
	 * abstract renderHtml method.
	 * 
	 * @see nextapp.echo2.webcontainer.DomUpdateSupport#renderHtml(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, org.w3c.dom.Node,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderContext rc, ServerComponentUpdate update, Node parentNode, Component component) {
		if (!component.isRenderVisible()) {
			// we really should never be called if we are invisble but to be
			// really sure
			Element blank = rc.getServerMessage().getDocument().createElement("bdo");
			blank.setAttribute("id", ContainerInstance.getElementId(component));
			parentNode.appendChild(blank);
			return;
		}
		RenderingContext renderingContext = new RenderingContext(rc, update, component);
		renderHtml(renderingContext, parentNode, component);
	}

	/**
	 * This is the rendering method you must implement if you are to use
	 * <code>AbstractEchoPointPeer</code>.
	 * <p>
	 * Its a simpler wrapper for the standard one but provides some neato setup
	 * services that would otherwise be repeated over and over again.
	 * <p>
	 * Note the use of RenderingContext object instead of the standard Echo
	 * RenderContext as this provides more rich code facilities.
	 * 
	 * @param rc -
	 *            the RenderingContext to use
	 * @param parentNode -
	 *            the parent Node to put content into
	 * @param component -
	 *            the component in question
	 * 
	 * @see nextapp.echo2.webcontainer.DomUpdateSupport#renderHtml(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, org.w3c.dom.Element,
	 *      nextapp.echo2.app.Component)
	 */
	public abstract void renderHtml(RenderingContext rc, Node parentNode, Component component);

	/**
	 * This stores some RenderState 'weakly' against a a Component and also
	 * updates the RenderingContext to point the new rendering state.
	 * 
	 * @param rc - the RenderContext in play
	 * @param component -
	 *            the component to use as a key
	 * @param renderState -
	 *            the RenderState to store
	 */
	protected void storeRenderState(RenderContext rc, Component component, RenderState renderState) {
		rc.getContainerInstance().setRenderState(component, renderState);
	}

	/**
	 * This retreives some RenderState using the Component as 'weak' key. This
	 * may return null if the component has gone out of JVM scope.
	 * 
	 * @param rc - the RenderContext in play
	 * @param component -
	 *            the component to use as a key
	 * @return - the RenderState previously stored via storeRenderState()
	 */
	protected RenderState retreiveRenderState(RenderContext rc, Component component) {
		return rc.getContainerInstance().getRenderState(component);
	}

	/**
	 * Called to dispose of any render state for a given component.
	 * 
	 * @param rc - the RenderContext in play
	 * @param component -
	 *            the component to dispose render state for
	 */
	protected void disposeRenderState(RenderContext rc, Component component) {
		rc.getContainerInstance().setRenderState(component,null);
	}

}
