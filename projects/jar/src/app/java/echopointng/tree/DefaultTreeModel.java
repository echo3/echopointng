package echopointng.tree;

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
 * The design paradigm and class name used within have been taken directly from
 * the java.swing package has been retro-fitted to work with the NextApp Echo web framework.
 *
 * This file was made part of the EchoPoint project on the 25/07/2002.
 *
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import nextapp.echo2.app.event.EventListenerList;

/**
 * A simple tree data model that uses TreeNodes.
 */
public class DefaultTreeModel implements TreeModel, java.io.Serializable {
	/** Root of the tree. */
	protected TreeNode root;

	/** Listeners. */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Determines how the <code>isLeaf</code> method figures out if a node is
	 * a leaf node. If true, a node is a leaf node if it does not allow
	 * children. (If it allows children, it is not a leaf node, even if no
	 * children are present.) That lets you distinguish between <i>folder </i>
	 * nodes and <i>file </i> nodes in a file system, for example.
	 * <p>
	 * If this value is false, then any node which has no children is a leaf
	 * node, and any node may acquire children.
	 *  
	 */
	protected boolean asksAllowsChildren;

	/** Map of Node identifiers keyed by the Nodes themselves */
	protected HashMap nodeIds = new HashMap();
	private int nextNodeId = 0;
	
	/**
	 * Creates a tree in which any node can have children.
	 * 
	 * @param root
	 *            a TreeNode object that is the root of the tree
	 */
	public DefaultTreeModel(TreeNode root) {
		this(root, false);
	}

	/**
	 * Creates a tree specifying whether any node can have children, or whether
	 * only certain nodes can have children.
	 * 
	 * @param root
	 *            a TreeNode object that is the root of the tree
	 * @param asksAllowsChildren
	 *            a boolean, false if any node can have children, true if each
	 *            node is asked to see if it can have children
	 */
	public DefaultTreeModel(TreeNode root, boolean asksAllowsChildren) {
		super();
		if (root == null) {
			throw new IllegalArgumentException("root is null");
		}
		this.root = root;
		this.asksAllowsChildren = asksAllowsChildren;
	}

	/**
	 * Adds a TreeModelListener to the model
	 */
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.addListener(TreeModelListener.class, l);
	}

	/**
	 * Tells how leaf nodes are determined.
	 * 
	 * @return true if only nodes which do not allow children are leaf nodes,
	 *         false if nodes which have no children (even if allowed) are leaf
	 *         nodes
	 */
	public boolean asksAllowsChildren() {
		return asksAllowsChildren;
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 */
	protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		Object[] listeners = listenerList.getListeners(TreeModelListener.class);
		TreeModelEvent e = null;

		for (int index = 0; index < listeners.length; ++index) {
			// Lazily create the event:
			if (e == null)
				e = new TreeModelEvent(source, path, childIndices, children);

			((TreeModelListener) listeners[index]).treeNodesChanged(e);
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 */
	protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
		Object[] listeners = listenerList.getListeners(TreeModelListener.class);
		TreeModelEvent e = null;

		for (int index = 0; index < listeners.length; ++index) {
			// Lazily create the event:
			if (e == null)
				e = new TreeModelEvent(source, path, childIndices, children);

			((TreeModelListener) listeners[index]).treeNodesInserted(e);
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 */
	protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
		Object[] listeners = listenerList.getListeners(TreeModelListener.class);
		TreeModelEvent e = null;

		for (int index = 0; index < listeners.length; ++index) {
			// Lazily create the event:
			if (e == null)
				e = new TreeModelEvent(source, path, childIndices, children);

			((TreeModelListener) listeners[index]).treeNodesRemoved(e);
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 */
	protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		Object[] listeners = listenerList.getListeners(TreeModelListener.class);
		TreeModelEvent e = null;

		for (int index = 0; index < listeners.length; ++index) {
			// Lazily create the event:
			if (e == null)
				e = new TreeModelEvent(source, path, childIndices, children);

			((TreeModelListener) listeners[index]).treeStructureChanged(e);
		}
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
	 * @return the child of <I>parent </I> at index <I>index </I>
	 */
	public Object getChild(Object parent, int index) {
		return ((TreeNode) parent).getChildAt(index);
	}

	/**
	 * Returns the number of children of <I>parent </I>. Returns 0 if the node
	 * is a leaf or if it has no children. <I>parent </I> must be a node
	 * previously obtained from this data source.
	 * 
	 * @param parent
	 *            a node in the tree, obtained from this data source
	 * @return the number of children of the node <I>parent </I>
	 */
	public int getChildCount(Object parent) {
		return ((TreeNode) parent).getChildCount();
	}

	/**
	 * Returns the index of child in parent.
	 */
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return 0;
		return ((TreeNode) parent).getIndex((TreeNode) child);
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
		if (child instanceof TreeNode) {
			return ((TreeNode) child).getParent();
		}
		return null;
	}

	/**
	 * Builds the parents of node up to and including the root node, where the
	 * original node is the last element in the returned array. The length of
	 * the returned array gives the node's depth in the tree.
	 * 
	 * @param aNode
	 *            the TreeNode to get the path for
	 * @param depth
	 *            an int giving the number of steps already taken towards the
	 *            root (on recursive calls), used to size the returned array
	 * @return an array of TreeNodes giving the path from the root to the
	 *         specified node
	 */
	protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
		TreeNode[] retNodes;
		// This method recurses, traversing towards the root in order
		// size the array. On the way back, it fills in the nodes,
		// starting from the root and working back to the original node.

		/*
		 * Check for null, in case someone passed in a null node, or they passed
		 * in an element that isn't rooted at root.
		 */
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
	 * Builds the parents of node up to and including the root node, where the
	 * original node is the last element in the returned array. The length of
	 * the returned array gives the node's depth in the tree.
	 * 
	 * @param node
	 *            the TreeNode to get the path for
	 */
	public Object[] getPathToRoot(Object node) {
		return getPathToRoot((TreeNode) node, 0);
	}

	/**
	 * Returns the root of the tree. Returns null only if the tree has no nodes.
	 * 
	 * @return the root of the tree
	 */
	public Object getRoot() {
		return root;
	}

	/**
	 * Invoked this to insert newChild at location index in parents children.
	 * This will then message nodesWereInserted to create the appropriate event.
	 * This is the preferred way to add children as it will create the
	 * appropriate event.
	 */
	public void insertNodeInto(MutableTreeNode newChild, MutableTreeNode parent, int index) {
		
		// Remove new node from parent first
		if (newChild.getParent() != null) {
			removeNodeFromParent(newChild);
		}
		
		parent.insert(newChild, index);

		int[] newIndexs = new int[1];

		newIndexs[0] = index;
		nodesWereInserted(parent, newIndexs);
	}

	/**
	 * Returns whether the specified node is a leaf node. The way the test is
	 * performed depends on the <code>askAllowsChildren</code> setting.
	 */
	public boolean isLeaf(Object node) {
		if (asksAllowsChildren)
			return !((TreeNode) node).getAllowsChildren();
		return ((TreeNode) node).isLeaf();
	}

	/**
	 * Invoke this method after you've changed how node is to be represented in
	 * the tree.
	 */
	public void nodeChanged(TreeNode node) {
		if (listenerList != null && node != null) {
			TreeNode parent = node.getParent();

			if (parent != null) {
				int anIndex = parent.getIndex(node);
				if (anIndex != -1) {
					int[] cIndexs = new int[1];

					cIndexs[0] = anIndex;
					nodesChanged(parent, cIndexs);
				}
			} else if (node == getRoot()) {
				nodesChanged(node, null);
			}
		}
	}

	/**
	 * Invoke this method after you've changed how the children identified by
	 * childIndicies are to be represented in the tree.
	 */
	public void nodesChanged(TreeNode node, int[] childIndices) {
		if (node != null) {
			if (childIndices != null) {
				int cCount = childIndices.length;

				if (cCount > 0) {
					Object[] cChildren = new Object[cCount];

					for (int counter = 0; counter < cCount; counter++) {
						cChildren[counter] = node.getChildAt(childIndices[counter]);
					}
					
					fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
				}
				
			} else if (node == getRoot()) {
				fireTreeNodesChanged(this, getPathToRoot(node), null, null);
			}
		}
	}

	/**
	 * Invoke this method if you've totally changed the children of node and its
	 * childrens children... This will post a treeStructureChanged event.
	 */
	public void nodeStructureChanged(TreeNode node) {
		if (node != null) {
			fireTreeStructureChanged(this, getPathToRoot(node), null, null);
		}
	}

	/**
	 * Invoke this method after you've inserted some TreeNodes into node.
	 * childIndices should be the index of the new elements and must be sorted
	 * in ascending order.
	 */
	public void nodesWereInserted(TreeNode node, int[] childIndices) {
		if (listenerList != null && node != null && childIndices != null && childIndices.length > 0) {
			int cCount = childIndices.length;
			Object[] newChildren = new Object[cCount];

			for (int counter = 0; counter < cCount; counter++) {
				newChildren[counter] = node.getChildAt(childIndices[counter]);
			}
			
			fireTreeNodesInserted(this, getPathToRoot(node), childIndices, newChildren);
		}
	}

	/**
	 * Invoke this method after you've removed some TreeNodes from node.
	 * childIndices should be the index of the removed elements and must be
	 * sorted in ascending order. And removedChildren should be the array of the
	 * children objects that were removed.
	 */
	public void nodesWereRemoved(TreeNode node, int[] childIndices, Object[] removedChildren) {
		if (node != null && childIndices != null) {
			fireTreeNodesRemoved(this, getPathToRoot(node), childIndices, removedChildren);
		}
	}

	/**
	 * Invoke this method if you've modified the TreeNodes upon which this model
	 * depends. The model will notify all of its listeners that the model has
	 * changed.
	 */
	public void reload() {
		reload(root);
	}

	/**
	 * Invoke this method if you've modified the TreeNodes upon which this model
	 * depends. The model will notify all of its listeners that the model has
	 * changed below the <code>node</code>.
	 */
	public void reload(TreeNode node) {
		nodeStructureChanged(node);
	}

	/**
	 * Message this to remove node from its parent. This will message
	 * nodesWereRemoved to create the appropriate event. This is the preferred
	 * way to remove a node as it handles the event creation for you.
	 */
	public void removeNodeFromParent(MutableTreeNode node) {
		MutableTreeNode parent = (MutableTreeNode) node.getParent();

		if (parent == null) {
			throw new IllegalArgumentException("node does not have a parent.");
		}

		int[] childIndex = new int[1];
		Object[] removedArray = new Object[1];

		childIndex[0] = parent.getIndex(node);
		parent.remove(childIndex[0]);
		removedArray[0] = node;
		nodesWereRemoved(parent, childIndex, removedArray);
	}

	/**
	 * Removes a TreeModelListener from the model
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.removeListener(TreeModelListener.class, l);
	}

	/**
	 * Sets whether or not to test leafness by asking getAllowsChildren() or
	 * isLeaf() to the TreeNodes. If newvalue is true, getAllowsChildren() is
	 * messaged, otherwise isLeaf() is messaged.
	 */
	public void setAsksAllowsChildren(boolean newValue) {
		asksAllowsChildren = newValue;
	}

	/**
	 * Sets the root to <code>root</code>. This will throw an
	 * IllegalArgumentException if <code>root</code> is null.
	 */
	public void setRoot(TreeNode root) {
		if (root == null)
			throw new IllegalArgumentException("Root of tree is not allowed to be null");
		this.root = root;
		nodeStructureChanged(root);
	}

	/**
	 * This sets the user object of the TreeNode identified by path and posts a
	 * node changed. If you use custom user objects in the TreeModel you're
	 * going to need to subclass this and set the user object of the changed
	 * node to something meaningful.
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		MutableTreeNode aNode = (MutableTreeNode) path.getLastPathComponent();

		aNode.setUserObject(newValue);
		nodeChanged(aNode);
	}

	/**
	 * @see echopointng.tree.TreeModel#getNodeId(java.lang.Object)
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

}
