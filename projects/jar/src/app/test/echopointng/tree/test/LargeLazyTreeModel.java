package echopointng.tree.test;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import nextapp.echo2.app.event.EventListenerList;
import echopointng.tree.TreeModel;
import echopointng.tree.TreeModelListener;
import echopointng.tree.TreePath;
import echopointng.util.RandKit;

/**
 * <code>LargeLazyTreeModel</code> will contain an "infinite" number of nodes.  It does 
 * this because it always returns false to <code>isLeaf()</code>.  However it only
 * creates the children of a node when they are accessed via one of the <code>getChildXXX()</code> methods.
 * <p>
 * This class is used to ensure that a Tree is not creating child nodes when it does have to and hence
 * it can contain very large TreeModels or TreeModels that have expensive child node creation times. 
 */
public class LargeLazyTreeModel implements TreeModel {
	
	EventListenerList listenerList = new EventListenerList();
	private LargeLazyTreeNode root;
	private int depth;
	private int childCreates;
	
	/** Map of Node identifiers keyed by the Nodes themselves */
	protected HashMap nodeIds = new HashMap();
	private int nextNodeId = 0;
	
	
	public LargeLazyTreeModel() {
		depth = 1;
		childCreates = 0;
		root = new LargeLazyTreeNode(null,this.toString());
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LargeLazyTreeModel [" + depth + " deep] [" + childCreates + " child creates]";
	}
	
	/**
	 * How many dep has the TreeModel gone?
	 * @return
	 */
	public int getDepth() {
		return depth;
	}
	
	/**
	 * How many child times have child nodes had to be created.
	 * @return
	 */
	public int getChildCreates() {
		return childCreates;
	}
	
	/**
	 * works out the max depth of the Tree has incurred
	 */
	private void calcDepth(LargeLazyTreeNode node) {
		int tempDepth = getPathToRoot(node).length;
		if (tempDepth > depth) {
			depth = tempDepth;
			root.setName(this.toString());
		}
	}

	/**
	 * We only create the children of the parent when accessed.
	 * @param parentNode - the parent node
	 */
	private void createChildren(LargeLazyTreeNode parentNode) {
		if (parentNode.children == null) {
			childCreates++;
			parentNode.children = new LargeLazyTreeNode[RandKit.rand(3,10)];
			for (int i = 0; i < parentNode.children.length; i++) {
				parentNode.children[i] = new LargeLazyTreeNode(parentNode,"Child-" + i);
				if (i == 0)
					calcDepth(parentNode.children[i]);
			}
		}
	}
	
	
	
	/**
	 * @see echopointng.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		createChildren((LargeLazyTreeNode)parent);
		return ((LargeLazyTreeNode) parent).getChild(index);
	}

	/**
	 * @see echopointng.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		createChildren((LargeLazyTreeNode)parent);
		return ((LargeLazyTreeNode) parent).getChildren().length;
	}

	/**
	 * @see echopointng.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object child) {
		createChildren((LargeLazyTreeNode)parent);
		LargeLazyTreeNode[] children = ((LargeLazyTreeNode) parent).getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i].equals(child))
				return i;
		}
		return 0;
	}

	/**
	 * @see echopointng.tree.TreeModel#getParent(java.lang.Object)
	 */
	public Object getParent(Object child) {
		return ((LargeLazyTreeNode) child).getParent();
	}

	/**
	 * @see echopointng.tree.TreeModel#getPathToRoot(java.lang.Object)
	 */
	public Object[] getPathToRoot(Object node) {
		return getPathToRoot((LargeLazyTreeNode)node,0);
	}
	
	public LargeLazyTreeNode[] getPathToRoot(LargeLazyTreeNode aNode, int depth) {
		LargeLazyTreeNode[] retNodes;
		if (aNode == null) {
			if (depth == 0)
				return null;
			else
				retNodes = new LargeLazyTreeNode[depth];
		} else {
			depth++;
			if (aNode.getParent() == null)
				retNodes = new LargeLazyTreeNode[depth];
			else
				retNodes = getPathToRoot(aNode.getParent(), depth);
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}		

	/**
	 * @see echopointng.tree.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return root;
	}

	/**
	 * We always return false to isLeaf and hence this TreeModel can have infinitely large
	 * child node levels.
	 * 
	 * @see echopointng.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		return false;
	}

	/**
	 * @see echopointng.tree.TreeModel#removeTreeModelListener(echopointng.tree.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.removeListener(TreeModelListener.class,l);
	}

	/**
	 * @see echopointng.tree.TreeModel#addTreeModelListener(echopointng.tree.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.addListener(TreeModelListener.class,l);
	}
	
	/**
	 * @see echopointng.tree.TreeModel#valueForPathChanged(echopointng.tree.TreePath, java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
	}
	
	
	/**
	 * Returns an identifier for a node. The identifier is assigned to the
	 * node the first time this is called for each node and does not change thereafter.
	 *
	 * @return  immutable identifier for a specific node
	 */
	public String getNodeId(Object node) {
		Integer nodeId = (Integer) nodeIds.get(node);
		if (nodeId == null) {
			nodeId = new Integer(nextNodeId++);
			nodeIds.put(node, nodeId);
		}
		return String.valueOf(nodeId);
	}
	
	/**
	 * @returns a node Object based on its unique identifier
	 */
	public Object getNodeById(String id) {
		Iterator iter = nodeIds.entrySet().iterator();
		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			if (String.valueOf(entry.getValue()).equals(id)) {
				return entry.getKey();
			}
		}
		return null;
	}
}