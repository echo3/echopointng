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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.RenderState;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.servermessage.DomUpdate;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.Tree;
import echopointng.tree.TreeModel;
import echopointng.tree.TreePath;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.RenderingContext;
import echopointng.util.TokenizerKit;

/**
 * Synchronization peer for <code>echopointng.Tree</code> components.
 */
public class TreePeer extends AbstractEchoPointContainerPeer implements ActionProcessor {

	/**
	 * Service to provide supporting Tree JavaScript library.
	 */
	public static final Service TREE_SCRIPT_SERVICE = JavaScriptService.forResource("Echopoint.Tree", "/echopointng/ui/resource/js/tree.js");

	static {
		WebRenderServlet.getServiceRegistry().add(TREE_SCRIPT_SERVICE);
	}

	/**
	 * <code>TreeRenderState</code> is used to contain render state for
	 * a component.
	 */
	private static class TreeRenderState implements RenderState {

		private Collection lastRenderedPaths;
		private ImageManager imageManager = new ImageManager();
		
		private Collection getLastRenderedPaths() {
			return lastRenderedPaths;
		}
		
		private void setLastRenderedPaths(Collection paths) {
			this.lastRenderedPaths = paths;
		}
	}


	/**
	 * Default constructor.
	 */
	public TreePeer() {
		partialUpdateManager.add(Tree.NODE_CHANGED_PROPERTY, new PartialUpdateParticipant() {

			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}

			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				RenderingContext renderingContext = new RenderingContext(rc, update, update.getParent());
				renderNodeDiffs(renderingContext, (Tree) update.getParent());
			}
		});
	}
	
	
	private TreeRenderState setupTreeImageRenderState(RenderContext rc, Component component) {
		//
		// Tree uses its own special image render state
		//
		TreeRenderState treeImageRenderState;
		RenderState renderState = retreiveRenderState(rc, component);
		if (renderState == null || !(renderState instanceof TreeRenderState)) {
			treeImageRenderState = new TreeRenderState();
			storeRenderState(rc, component, treeImageRenderState);
		} else {
			treeImageRenderState = (TreeRenderState) renderState;
		}
		return treeImageRenderState;
	}

	
	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		renderDisposeDirective(new RenderingContext(rc,update,component), (Tree) component);
	}


	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to dispose the state of a tree, performing
	 * tasks such as unregistering event listeners on the client.
	 * 
	 * @param rc the relevant <code>RenderContext</code>
	 * @param tree the tree
	 */
	private void renderDisposeDirective(RenderingContext rc, Tree tree) {
		ServerMessage serverMessage = rc.getServerMessage();
		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(TREE_SCRIPT_SERVICE);
		
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE,
			"EchoPointTree.MessageProcessor", "dispose", new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(tree));
		itemizedUpdateElement.appendChild(itemElement);
	}
	
	
	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to initialise the state of a tree, performing
	 * tasks such as registering event listeners on the client.
	 * 
	 * @param rc the relevant <code>RenderContext</code>
	 * @param tree the tree
	 */
	private void renderInitDirective(RenderContext rc, Tree tree) {
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
			"EchoPointTree.MessageProcessor", "init", new String[] {}, new String[] {});
		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(tree));
		itemizedUpdateElement.appendChild(itemElement);
	}
	
	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to tell the tree to make a specific
	 * tree node come into view
	 * 
	 * @param rc the relevant <code>RenderContext</code>
	 * @param tree the tree
	 */
	private void renderScrollIntoViewDirective(RenderingContext rc, Tree tree) {
		TreeModel model = tree.getModel();
		Collection lastExpandedPaths = tree.getLastExpandedPaths();
		if (model == null || lastExpandedPaths == null) {
			return;
		}
		TreePath expandedTargetPath = null;
		for (Iterator iter = lastExpandedPaths.iterator(); iter.hasNext();) {
			expandedTargetPath = (TreePath) iter.next();
		}
		// clear it as being rendered now so it wont interfere in the future
		lastExpandedPaths.clear();
		
		Style fallbackStyle = EPNG.getFallBackStyle(tree);
		boolean scrollIntoView = rc.getRP(Tree.PROPERTY_SCROLL_INTO_VIEW_USED,fallbackStyle,true);
		if (! scrollIntoView || expandedTargetPath == null) {
			return;
		}
		
		//
		// dont do this for the root as it could jump down a long way unexpectantly.
		if (model.getRoot() == expandedTargetPath.getLastPathComponent()) {
			return;
		}
		// we try to scroll the next peer into view in the hope that if its visible, its previous
		// peer's children are in view.  Failing that we try the last
		// we need to find the last child node of that last expanded node
		TreePath scrollIntoViewTreePath = null;
		Object parentNode = expandedTargetPath.getParentPath().getLastPathComponent();
		Object expandedTargetNode = expandedTargetPath.getLastPathComponent();
		int cc = model.getChildCount(parentNode);
		int index = model.getIndexOfChild(parentNode,expandedTargetNode);
		if (index+1 < cc) {
			// ok we have a peer
			Object peerNode = model.getChild(parentNode, index+1);
			scrollIntoViewTreePath = new TreePath(expandedTargetPath.getParentPath(),peerNode);
		} else {
			// we dont have a peer so make it the last child if we have one of those
			cc = model.getChildCount(expandedTargetNode);
			if (cc-1 > 0) {
				Object childNode = model.getChild(expandedTargetNode, cc-1);
				scrollIntoViewTreePath = new TreePath(expandedTargetPath,childNode);
			}
		}
		if (scrollIntoViewTreePath != null) {
			Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
				"EchoPointTree.MessageProcessor", "scrollIntoView", new String[] {}, new String[] {});
			Element itemElement = rc.getServerMessage().getDocument().createElement("item");
			itemElement.setAttribute("eid", ContainerInstance.getElementId(tree));
			itemElement.setAttribute("scrollIntoViewPath", TreeRenderer.createPathId(scrollIntoViewTreePath,model));
			itemElement.setAttribute("expandedTargetPath", TreeRenderer.createPathId(expandedTargetPath,model));
			itemizedUpdateElement.appendChild(itemElement);
		}
	}
	
	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		
		Tree tree = (Tree) component;
		Style fallbackStyle = EPNG.getFallBackStyle(tree);

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(TREE_SCRIPT_SERVICE);
		renderInitDirective(rc, tree);
		renderScrollIntoViewDirective(rc, tree);

		TreeRenderState treeRenderState = setupTreeImageRenderState(rc, tree);

		CssStyleEx style = new CssStyleEx(tree, fallbackStyle);
		
		final Element outerTreeDiv = rc.createE("div");
		parent.appendChild(outerTreeDiv);
		rc.addStandardWebSupport(outerTreeDiv);

		outerTreeDiv.setAttribute("id", rc.getElementId());
		outerTreeDiv.setAttribute("style", style.renderInline());
		
		TreeRenderer.EventSupportCallBack callback = new TreeRenderer.EventSupportCallBack() {
			/**
			 * @see echopointng.ui.syncpeer.TreeRenderer.EventSupportCallBack#onExpandoCell(nextapp.echo2.webrender.output.CssStyle,
			 *      org.w3c.dom.Element, echopointng.Tree, java.lang.Object)
			 */
			public String onExpandoCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode, TreePath treeNodePath) {
				return addToggleEventId(rc, cssStyle, tree, treeNode, treeNodePath);
			}

			/**
			 * @see echopointng.ui.syncpeer.TreeRenderer.EventSupportCallBack#onNodeComponentCell(nextapp.echo2.webrender.output.CssStyle,
			 *      org.w3c.dom.Element, echopointng.Tree, java.lang.Object)
			 */
			public String onNodeComponentCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode,
					TreePath treeNodePath) {
				return null;
			}

			/**
			 * @see echopointng.ui.syncpeer.TreeRenderer.EventSupportCallBack#onNodeIconCell(nextapp.echo2.webrender.output.CssStyle,
			 *      org.w3c.dom.Element, echopointng.Tree, java.lang.Object)
			 */
			public String onNodeIconCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode,
					TreePath treeNodePath) {
				return addSelectEventId(rc, cssStyle, tree, treeNode, treeNodePath, "icon");
			}

			/**
			 * @see echopointng.ui.syncpeer.TreeRenderer.EventSupportCallBack#onNodeTextCell(nextapp.echo2.webrender.output.CssStyle,
			 *      org.w3c.dom.Element, echopointng.Tree, java.lang.Object)
			 */
			public String onNodeTextCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode,
					TreePath treeNodePath) {
				return addSelectEventId(rc, cssStyle, tree, treeNode, treeNodePath, "text");
			}

			/**
			 * @see echopointng.ui.syncpeer.TreeRenderer.EventSupportCallBack#onTreeRow(nextapp.echo2.webrender.output.CssStyle,
			 *      org.w3c.dom.Element, echopointng.Tree)
			 */
			public void onTreeRow(RenderingContext rc, CssStyle cssStyle, Element treeElement, Tree tree, TreePath treeNodePath) {
				outerTreeDiv.appendChild(treeElement);
				String eventElementId = createEventId(tree, "treerow", "", null, treeNodePath);
				treeElement.setAttribute("id", eventElementId);
			}
		};
		TreeRenderer treeRenderer = new TreeRenderer(rc, callback, tree, fallbackStyle, treeRenderState.imageManager);
		treeRenderer.renderTree();
		tree.getDirtyPaths().clear();
		
		// save a copy of the newly rendered tree paths
		if (rc.getRP(Tree.PROPERTY_PARTIAL_UPDATE_SUPPORT, fallbackStyle, true)) {
			treeRenderState.setLastRenderedPaths(treeRenderer.compileVisiblePaths());
		}
	}


	/**
	 * The TablePeer now has the ability to update only the tree node rows that
	 * have changed. Therefore to do this is must unwind some of the automatic
	 * rendering support provided by the base classes and rather render some if
	 * it itself.
	 * 
	 * @see echopointng.ui.syncpeer.AbstractEchoPointContainerPeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		
		Tree tree = (Tree) update.getParent();		
		boolean fullReplace = !(Tree.getRenderProperty(tree, Tree.PROPERTY_PARTIAL_UPDATE_SUPPORT, true));
		
		if (!fullReplace) {
			if (update.hasUpdatedLayoutDataChildren()) {
				fullReplace = true;
			} else if (update.hasUpdatedProperties()) {
				if (partialUpdateManager.canProcess(rc, update)) {
					partialUpdateManager.process(rc, update);
				} else {
					fullReplace = true;
				}
			}
		}

		// Only do this if full replace as the partial update takes care of removing 
		// and adding child components if necessary
		if (fullReplace) {
			// do a normal remove of the old DOM and add the new one
			renderUpdateBaseImpl(rc, update, targetId, fullReplace);
		}
		
		return fullReplace;
	}

	
	private void renderNodeDiffs(RenderingContext rc, Tree tree) {
		
		TreeRenderer.EventSupportCallBack callback = new TreeRenderer.EventSupportCallBack() {

			public String onExpandoCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode, TreePath treeNodePath) {
				return addToggleEventId(rc, cssStyle, tree, treeNode, treeNodePath);
			}

			public String onNodeComponentCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode,
					TreePath treeNodePath) {
				return null;
			}

			public String onNodeIconCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode,
					TreePath treeNodePath) {
				return addSelectEventId(rc, cssStyle, tree, treeNode, treeNodePath, "icon");
			}

			public String onNodeTextCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode,
					TreePath treeNodePath) {
				return addSelectEventId(rc, cssStyle, tree, treeNode, treeNodePath, "text");
			}

			public void onTreeRow(RenderingContext rc, CssStyle cssStyle, Element treeElement, Tree tree, TreePath treeNodePath) {
				String eventElementId = createEventId(tree, "treerow", "", null, treeNodePath);
				treeElement.setAttribute("id", eventElementId);
			}
		};
		
		TreeRenderState treeRenderState = setupTreeImageRenderState(rc, tree);
		
		Style fallbackStyle = EPNG.getFallBackStyle(tree);
		
		TreeRenderer treeRenderer = new TreeRenderer(rc, callback, tree, fallbackStyle, treeRenderState.imageManager);
		
		// First get old tree structure
		Collection oldPaths = treeRenderState.getLastRenderedPaths();
		
		// Now get the current tree structure
		ArrayList newPaths = treeRenderer.compileVisiblePaths();
		
		// Added node paths exist in the new structure but not the old
		// Don't use newPaths.removeAll(oldPaths) as we need newPaths later
		ArrayList addedPaths = new ArrayList();
		
		for (Iterator iter = newPaths.iterator(); iter.hasNext();) {
			TreePath path = (TreePath) iter.next();
			if (!oldPaths.contains(path)) {
				addedPaths.add(path);
			}
		}
		
		
		/*
		 * REMOVALS and DIRTY PATHS
		 */
		
		// Removed paths exist in the old structure but not the new
		oldPaths.removeAll(newPaths);
		
		// Paths that are dirty, but are not already in the removed or added paths lists, need to be removed and re-added
		// Dirty paths must be visible in the new structure
		Collection dirtyPaths = tree.getDirtyPaths();
		dirtyPaths.retainAll(newPaths);
		dirtyPaths.removeAll(addedPaths);
		
		
		for (Iterator iter = dirtyPaths.iterator(); iter.hasNext();) {
			TreePath path = (TreePath) iter.next();
			oldPaths.add(path);
			addedPaths.add(path);
		}
		
		// Remove out of date paths
		for (Iterator iter = oldPaths.iterator(); iter.hasNext();) {
			TreePath path = (TreePath) iter.next();
			DomUpdate.renderElementRemove(rc.getServerMessage(), createEventId(tree, "treerow", "", null, path));
		}
		
		
		/*
		 * ADDITIONS
		 */
		if (addedPaths.size() > 0) {
			Document doc = rc.getServerMessage().getDocument();
			Element domAddElement = DomUpdate.renderElementAdd(rc.getServerMessage());
			
			for (Iterator iter = addedPaths.iterator(); iter.hasNext();) {
				
				TreePath path = (TreePath) iter.next();
	
				// Render the node at this path
				DocumentFragment docFrag = doc.createDocumentFragment();
				docFrag.appendChild(treeRenderer.renderTreeRow(path.getLastPathComponent(), path));
				
				
				/*
				 * Echo2 uses the standard DOM insertBefore() semantics. However this does not quite
				 * suit as as we would rather an insertAfter() mechanism.  None the less we need
				 * to move forward to find a "solid" table row to use as the sibling for the
				 * insertBefore() operation.  If we cannot find one going forwards, then it must require a
				 * parent appendChild() operation instead.
				 */
				TreePath siblingPath = null;
				
				for (int k = newPaths.indexOf(path) + 1; k < newPaths.size() && siblingPath == null; k++) {
					
					siblingPath = (TreePath) newPaths.get(k);
					
					// if the sibling is already in the tree then its a spot for the new node to be inserted
					if (!addedPaths.contains(siblingPath)) {
						DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, rc.getElementId(),
							createEventId(tree, "treerow", "", null, siblingPath), docFrag);
					} else {
						siblingPath = null;
					}
					
				}
				
				// If we have navigated forward without finding a spot to put the new row
				// just append it at the end of the Tree.
				if (siblingPath == null) {
					DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, rc.getElementId(), docFrag);
				}
				
				// Remove added node path so it can now be used as a sibling by later additions
				iter.remove();
			}
		}
		
		// keep only one copy of the lastest tree structure
		treeRenderState.setLastRenderedPaths(newPaths);
		tree.getDirtyPaths().clear();
		
		renderScrollIntoViewDirective(rc, tree);
	}
	
	
	/**
	 * The events can be expansion or contraction events as well as selection events
	 * 
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		Tree tree = (Tree) component;
		TreeModel model = (TreeModel) tree.getRenderProperty(Tree.PROPERTY_MODEL);
		if (model == null) {
			return;
		}
		
		String actionName = actionElement.getAttribute(ActionProcessor.ACTION_NAME);
		String actionValue = actionElement.getAttribute(ActionProcessor.ACTION_VALUE);
		// its a tree path
		String[] paths = TokenizerKit.tokenize(actionValue, "[]");
		Object[] nodes = new Object[paths.length];
		for (int i = 0; i < paths.length; i++) {
			if (i == 0) {
				nodes[i] = model.getRoot();
			} else {
				nodes[i] = model.getNodeById(paths[i]);
			}
		}
		TreePath nodePath = new TreePath(nodes);
		ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, actionName, nodePath);
	}


	/**
	 * Adds the event handler id for toggling the nodes expanded or contracted.
	 * The entire tree has a single click handler - an id is needed on a toogle
	 * cell so the tree's click handler knows what to do when it is clicked.
	 */
	private String addToggleEventId(RenderingContext rc, CssStyle cssStyle, Tree tree, Object treeNode, TreePath treeNodePath) {
		String eventElementId = createEventId(tree, "expando", "toggle", treeNode, treeNodePath);
		if (tree.isRenderEnabled()) {
			cssStyle.setAttribute("cursor", "pointer");
		}
		return eventElementId;
	}

	/**
	 * Adds the event handler id for selecting nodes.
	 * The entire tree has a single click handler - an id is needed on a select
	 * cell so the tree's click handler knows what to do when it is clicked.
	 */
	private String addSelectEventId(RenderingContext rc, CssStyle cssStyle, Tree tree, Object treeNode, TreePath treeNodePath, String cellType) {
		String eventElementId = createEventId(tree, cellType, "select", treeNode, treeNodePath);
		if (tree.isRenderEnabled()) {
			cssStyle.setAttribute("cursor", "pointer");
		}
		return eventElementId;
	}

	/**
	 * Creates a identifier to unique identify a node and its containing Tree.
	 */
	private String createEventId(Tree tree, String cellType, String baseAction, Object treeNode, TreePath treeNodePath) {
		TreeModel model = (TreeModel) tree.getRenderProperty(Tree.PROPERTY_MODEL);

		String pathId = TreeRenderer.createPathId(treeNodePath, model);
		String treeId = "tree|" + ContainerInstance.getElementId(tree) + '|' + cellType;
		String eventElementId = treeId + '|' + baseAction + '|' + pathId;
		return eventElementId;
	}

}
