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

import java.util.EventListener;

/**
 * <code>TreeModelListener</code> defines the interface for an object 
 * that listens to changes in a <code>TreeModel</code>.
 *
 */
public interface TreeModelListener extends EventListener {

	/**
	 * <p>Invoked after a node (or a set of siblings) has changed in some
	 * way. The node(s) have not changed locations in the tree or
	 * altered their children arrays, but other attributes have
	 * changed and may affect presentation. Example: the name of a
	 * file has changed, but it is in the same location in the file
	 * system.</p>
	 * <p>To indicate the root has changed, childIndices and children
	 * will be null. </p>
	 * 
	 * <p>e.path() returns the path the parent of the changed node(s).</p>
	 * 
	 * <p>e.childIndices() returns the index(es) of the changed node(s).</p>
	 */
	public void treeNodesChanged(TreeModelEvent e);
	/**
	 * <p>Invoked after nodes have been inserted into the tree.</p>
	 * 
	 * <p>e.path() returns the parent of the new nodes
	 * <p>e.childIndices() returns the indices of the new nodes in
	 * ascending order.
	 */
	public void treeNodesInserted(TreeModelEvent e);
	/**
	 * <p>Invoked after nodes have been removed from the tree.  Note that
	 * if a subtree is removed from the tree, this method may only be
	 * invoked once for the root of the removed subtree, not once for
	 * each individual set of siblings removed.</p>
	 *
	 * <p>e.path() returns the former parent of the deleted nodes.</p>
	 * 
	 * <p>e.childIndices() returns the indices the nodes had before they were deleted in ascending order.</p>
	 */
	public void treeNodesRemoved(TreeModelEvent e);
	/**
	 * <p>Invoked after the tree has drastically changed structure from a
	 * given node down.  If the path returned by e.getPath() is of length
	 * one and the first element does not identify the current root node
	 * the first element should become the new root of the tree.<p>
	 * 
	 * <p>e.path() holds the path to the node.</p>
	 * <p>e.childIndices() returns null.</p>
	 */
	public void treeStructureChanged(TreeModelEvent e);
}
