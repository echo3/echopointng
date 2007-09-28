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

/*
 * The design paradigm, class name and some code used within have been taken from
 * the java.net JDNC project.  This project is licensed under the LGPL and is
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This file was made part of the EchoPoint project on the 15/07/2005.
 *
 */

package echopointng.treetable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import nextapp.echo2.app.event.EventListenerList;
import echopointng.tree.TreeModelEvent;
import echopointng.tree.TreeModelListener;
import echopointng.tree.TreeNode;
import echopointng.tree.TreePath;

/**
 * AbstractTreeTableModel provides an implementation of
 * {@link echopointng.treetable.TreeTableModel}as a convenient starting point
 * in defining custom data models for {@link echopointng.TreeTable}.
 */
public abstract class AbstractTreeTableModel implements TreeTableModel {
	/**
	 * Value returned by
	 * {@link TreeTableModel#getColumnClass(int) getColumnClass}for the
	 * {@link echopointng.TreeTable#isHierarchical(int) hierarchical}column.
	 */
	public final static Class hierarchicalColumnClass = TreeTableModel.class;

	/**
	 * Root node of the model
	 */
	protected Object root;

	/**
	 * Event listener list
	 */
	protected EventListenerList listenerList = new EventListenerList();

	/** Map of Node identifiers keyed by the Nodes themselves */
	protected HashMap nodeIds = new HashMap();
	private int nextNodeId = 0;
	
	
	/**
	 * Constructs an <code>AbstractTreeTableModel</code> with a null root node
	 */
	public AbstractTreeTableModel() {
		this(null);
	}

	/**
	 * Constructs an <code>AbstractTreeTableModel</code> with the specified
	 * node as the root node.
	 * 
	 * @param root
	 *            root node
	 */
	public AbstractTreeTableModel(Object root) {
		this.root = root;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class getColumnClass(int column) {
		// Assume that the first column will contain hierarchical nodes.
		return column == 0 ? hierarchicalColumnClass : Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getColumnName(int column) {
		return "Column " + column; // Cheap implementation
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getRoot() { // From the TreeNode interface
		return root;
	}

	/**
	 * Returns the child of <I>parent </I> at index <I>index </I> in the
	 * parent's child array. <I>parent </I> must be a node previously obtained
	 * from this data source. This should not return null if <i>index </i> is a
	 * valid index for <i>parent </i> (that is <i>index </i>>= 0 && <i>index
	 * </i>< getChildCount( <i>parent </i>)).
	 * 
	 * @param parent
	 *            a node in the tree, obtained from this data source
	 * @return the child of <I>parent </I> at index <I>index </I>, or null if
	 *         the specified parent node is not a <code>TreeNode</code>.
	 */
	public Object getChild(Object parent, int index) {
		// meant to be overridden
		try {
			return ((TreeNode) parent).getChildAt(index);
		} catch (ClassCastException ex) { // not a TreeNode?
			return null;
		}
	}
	
	/**
	 * Returns the parent of <I>child </I>. Returns null if the node has no
	 * parent or the tree model does not support traversing from a child to a
	 * parent node.
	 * 
	 * @param child
	 *            a node in the tree
	 * @return the parent of the node <I>child </I>
	 */
	public Object getParent(Object child) {
		return null;
	}
	

	/**
	 * Returns the number of children in the specified parent node.
	 * 
	 * @param parent
	 *            node whose child count is being requested
	 * @return the number of children in the specified parent node
	 */
	public int getChildCount(Object parent) {
		try {
			return ((TreeNode) parent).getChildCount();
		} catch (ClassCastException ex) { // not a TreeNode?
			return 0;
		}
	}

	/**
	 * This is one of the methods you must override as it simply returns 1.
	 * 
	 * @see TreeTableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 1;
	}

	/**
	 * Returns the index of child in parent. If either the parent or child is
	 * <code>null</code>, returns -1.
	 * 
	 * @param parent
	 *            a note in the tree, obtained from this data source
	 * @param child
	 *            the node we are interested in
	 * @return the index of the child in the parent, or -1 if either the parent
	 *         or the child is <code>null</code>
	 */
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return -1;

		try {
			int index =  ((TreeNode) parent).getIndex((TreeNode) child);
			if (index >= 0) {
				return index;
			}
		} catch (ClassCastException ex) { }// not a TreeNode?
		// This is not called in the JTree's default mode.
		// Use a naive implementation.
		for (int i = 0; i < getChildCount(parent); i++) {
			if (getChild(parent, i).equals(child)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns true if the specified node is a leaf node; false otherwise.
	 * 
	 * @param node
	 *            node to test
	 * @return true if the specified node is a leaf node; false otherwise
	 */
	public boolean isLeaf(Object node) {
		if (node == null)
			return false;
		try {
			return ((TreeNode) node).isLeaf();
		} catch (ClassCastException ex) { // not a TreeNode?
			return getChildCount(node) == 0;
		}
	}

	/**
	 * Builds the parents of node up to and including the root node,
	 * where the original node is the last element in the returned array.
	 * The length of the returned array gives the node's depth in the
	 * tree.
	 * 
	 * @param aNode  the TreeNode to get the path for
	 * @param depth  an int giving the number of steps already taken towards
	 *        the root (on recursive calls), used to size the returned array
	 * @return an array of TreeNodes giving the path from the root to the
	 *         specified node 
	 */
	protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
		TreeNode[] retNodes;
		// This method recurses, traversing towards the root in order
		// size the array. On the way back, it fills in the nodes,
		// starting from the root and working back to the original node.

		/* Check for null, in case someone passed in a null node, or
		   they passed in an element that isn't rooted at root. */
		if (aNode == null) {
			if (depth == 0)
				return null;
			else
				retNodes = new TreeNode[depth];
		} else {
			depth++;
			if (aNode == root)
				retNodes = new TreeNode[depth];
			else
				retNodes = getPathToRoot(aNode.getParent(), depth);
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}
	/**
	 * Builds the parents of node up to and including the root node,
	 * where the original node is the last element in the returned array.
	 * The length of the returned array gives the node's depth in the
	 * tree.
	 * 
	 * @param node the TreeNode to get the path for
	 */
	public Object[] getPathToRoot(Object node) {
		return getPathToRoot((TreeNode) node, 0);
	}
	
	/**
	 * Called when value for the item identified by path has been changed. If
	 * newValue signifies a truly new value the model should post a
	 * <code>treeNodesChanged</code> event.
	 * 
	 * @param path
	 *            path to the node that has changed
	 * @param newValue
	 *            the new value
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	public void addTreeModelListener(echopointng.tree.TreeModelListener l) {
		listenerList.addListener(TreeModelListener.class, l);
	}

	public void removeTreeModelListener(echopointng.tree.TreeModelListener l) {
		listenerList.removeListener(TreeModelListener.class, l);
	}

	public echopointng.tree.TreeModelListener[] getTreeModelListeners() {
		return (TreeModelListener[]) listenerList.getListeners(TreeModelListener.class);
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListeners(TreeModelListener.class);
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 1; i >= 0; i--) {
			// Lazily create the event:
			if (e == null) {
				e = new TreeModelEvent(source, path, childIndices, children);
			}
			((TreeModelListener) listeners[i]).treeNodesChanged(e);
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListeners(TreeModelListener.class);
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 1; i >= 0; i--) {
			// Lazily create the event:
			if (e == null) {
				e = new TreeModelEvent(source, path, childIndices, children);
			}
			((TreeModelListener) listeners[i]).treeNodesInserted(e);
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListeners(TreeModelListener.class);
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 1; i >= 0; i--) {
			// Lazily create the event:
			if (e == null) {
				e = new TreeModelEvent(source, path, childIndices, children);
			}
			((TreeModelListener) listeners[i]).treeNodesRemoved(e);
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListeners(TreeModelListener.class);
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 1; i >= 0; i--) {
			// Lazily create the event:
			if (e == null) {
				e = new TreeModelEvent(source, path, childIndices, children);
			}
			((TreeModelListener) listeners[i]).treeStructureChanged(e);
		}
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
	 * @see echopointng.tree.TreeModel#getNodeById(java.lang.String)
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
	
	
	/**
	 * The following are left to be implemented in the concrete subclass :
	 * <p>
	 * public Object getChild(Object parent, int index) <br>
	 * public int getChildCount(Object parent) <br>
	 * public int getColumnCount() public String getColumnName(int column) <br>
	 * public Object getValueAt(Object node, int column) <br>
	 */
}
