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
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webrender.output.CssStyle;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.Tree;
import echopointng.tree.TreeModel;
import echopointng.tree.TreePath;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.RenderingContext;
import echopointng.util.TokenizerKit;

/**
 * <code>TreeTableCellPeer</code> is a peer for the scope hidden component
 * TreeTable.TreeTableCell.
 * <p>
 * Its purpose it to render an individual "row" of a Tree, with the intended
 * parent a Table cell. Because it is scope hidden, it uses "named" properties
 * to access its data.
 */

public class TreeTableCellPeer extends AbstractEchoPointPeer implements ActionProcessor {

	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		Tree tree = (Tree) component.getRenderProperty("tree");
		TreeModel model = (TreeModel) tree.getRenderProperty(Tree.PROPERTY_MODEL);
		if (model == null)
			return;
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
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      org.w3c.dom.Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, final Node parentNode, Component component) {
		Tree tree = (Tree) rc.getRP("tree");
		Style fallbackStyle = EPNG.getFallBackStyle(tree);
		Object treeNode = rc.getRP("treeNode");
		TreePath treeNodePath = (TreePath) rc.getRP("treeNodePath");
		
		ImageManager imageManager = (ImageManager) retreiveRenderState(rc, component);
		if (imageManager == null) {
			imageManager = new ImageManager();
			storeRenderState(rc, component,imageManager);
		}
		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(TreePeer.TREE_SCRIPT_SERVICE);

		final Component treeTableCell = component;
		
		TreeRenderer.EventSupportCallBack callback = new TreeRenderer.EventSupportCallBack() {
			/**
			 * @see echopointng.ui.syncpeer.TreeRenderer.EventSupportCallBack#onExpandoCell(nextapp.echo2.webrender.output.CssStyle,
			 *      org.w3c.dom.Element, echopointng.Tree, java.lang.Object)
			 */
			public String onExpandoCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode, TreePath treeNodePath) {
				return addToggleEvents(rc, cssStyle, treeTableCell, tree, treeNode, treeNodePath);
			}

			/**
			 * @see echopointng.ui.syncpeer.TreeRenderer.EventSupportCallBack#onNodeComponentCell(nextapp.echo2.webrender.output.CssStyle,
			 *      org.w3c.dom.Element, echopointng.Tree, java.lang.Object)
			 */
			public String onNodeComponentCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode, TreePath treeNodePath) {
				return null;
			}

			/**
			 * @see echopointng.ui.syncpeer.TreeRenderer.EventSupportCallBack#onNodeIconCell(nextapp.echo2.webrender.output.CssStyle,
			 *      org.w3c.dom.Element, echopointng.Tree, java.lang.Object)
			 */
			public String onNodeIconCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode, TreePath treeNodePath) {
				// Let table handle row selection
				return null;
			}

			/**
			 * @see echopointng.ui.syncpeer.TreeRenderer.EventSupportCallBack#onNodeTextCell(nextapp.echo2.webrender.output.CssStyle,
			 *      org.w3c.dom.Element, echopointng.Tree, java.lang.Object)
			 */
			public String onNodeTextCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode, TreePath treeNodePath) {
				// Let table handle row selection
				return null;
			}

			/**
			 * @see echopointng.ui.syncpeer.TreeRenderer.EventSupportCallBack#onTreeRow(nextapp.echo2.webrender.output.CssStyle,
			 *      org.w3c.dom.Element, echopointng.Tree)
			 */
			public void onTreeRow(RenderingContext rc, CssStyle cssStyle, Element treeElement, Tree tree, TreePath treeNodePath) {
				treeElement.setAttribute("id", rc.getElementId());
				parentNode.appendChild(treeElement);
			}
		};
		TreeRenderer treeRenderer = new TreeRenderer(rc, callback, tree, fallbackStyle,imageManager);
		treeRenderer.renderTreeRow(treeNode,treeNodePath);
	}

	private String addToggleEvents(RenderingContext rc, CssStyle cssStyle, Component component, Tree tree, Object treeNode, TreePath treeNodePath) {
		String eventElementId = createEventId(component, tree, "expando", "toggle", treeNode, treeNodePath);
		if (component.isRenderEnabled()) {
			cssStyle.setAttribute("cursor", "pointer");
			rc.renderEventAdd("click", eventElementId, "EchoPointTree.EventProcessor.onNodeClick");
		}
		return eventElementId;
	}

	/**
	 * Creates a identifier to unique identify a node and its containing Tree.
	 */
	private String createEventId(Component component, Tree tree, String cellType, String baseAction, Object treeNode, TreePath treeNodePath) {
		TreeModel model = (TreeModel) tree.getRenderProperty(Tree.PROPERTY_MODEL);

		String pathId = TreeRenderer.createPathId(treeNodePath,model);
		String treeId = "tree|" + ContainerInstance.getElementId(component) + '|' + cellType;
		String eventElementId = treeId + '|' + baseAction + '|' + pathId;
		return eventElementId;
	}
}
