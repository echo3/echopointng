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

/**
 * The <code>TreeModel</code> interface defines a suitable data model for a <code>Tree</code>.
 * <p>
 * There is an implied behavioural contract between a Tree and its TreeModel.  The
 * Tree gaurantees not to call any of the <code>getChildXXX()</code> methods until they
 * are absolutely needed (for example a parent node is expanded).  
 * <p>
 * This is to allow children to be lazily created "as needed".  Ths is useful for
 * very large TreeModels or ones where child node creation is an expensive operation
 * <p>
 * Note that the <code>isLeaf() </code> may be called at any time and if
 * it returns true, then the node cannot contain any child nodes.  However the inverse if not
 * necessarily true.  A model may return that a node is NOT a leaf but it may still 
 * have zero child nodes.
 * 
 */
public interface TreeModel {
	
	/**
	 * Adds a listener for the TreeModelEvent posted after the tree changes.
	 *
	 * @param   l       the listener to add
	 */
	public void addTreeModelListener(TreeModelListener l);
	
	/**
	 * Returns the child of <I>parent</I> at index <I>index</I> in the parent's
	 * child array.  <I>parent</I> must be a node previously obtained from
	 * this data source. This should not return null if <i>index</i>
	 * is a valid index for <i>parent</i> (that is <i>index</i> >= 0 &&
	 * <i>index</i> < getChildCount(<i>parent</i>)).
	 *
	 * @param   parent  a node in the tree, obtained from this data source
	 * @return  the child of <I>parent</I> at index <I>index</I>
	 */
	public Object getChild(Object parent, int index);

	/**
	 * Returns the number of children of <I>parent</I>.  Returns 0 if the node
	 * is a leaf or if it has no children.  <I>parent</I> must be a node
	 * previously obtained from this data source.
	 *
	 * @param   parent  a node in the tree, obtained from this data source
	 * @return  the number of children of the node <I>parent</I>
	 */
	public int getChildCount(Object parent);

	/**
	 * Returns the index of child in parent.
	 */
	public int getIndexOfChild(Object parent, Object child);

	/**
	 * Returns the root of the tree.  Returns null only if the tree has
	 * no nodes.
	 *
	 * @return  the root of the tree
	 */
	public Object getRoot();

	/**
	 * Returns true if <I>node</I> is a leaf.  It is possible for this method
	 * to return false even if <I>node</I> has no children.  A directory in a
	 * filesystem, for example, may contain no files; the node representing
	 * the directory is not a leaf, but it also has no children.
	 *
	 * @param   node    a node in the tree, obtained from this data source
	 * @return  true if <I>node</I> is a leaf
	 */
	public boolean isLeaf(Object node);

	/**
	 * Removes a listener previously added with <B>addTreeModelListener()</B>.
	 *
	 */
	public void removeTreeModelListener(TreeModelListener l);

	/**
	  * Messaged when the user has altered the value for the item identified
	  * by <I>path</I> to <I>newValue</I>.  If <I>newValue</I> signifies
	  * a truly new value the model should post a treeNodesChanged
	  * event.
	  *
	  * @param path path to the node that the user has altered.
	  * @param newValue the new value from the Tree.
	  */
	public void valueForPathChanged(TreePath path, Object newValue);
	
	/**
	 * Returns an identifier for a node. The identifier should be assigned 
	 * to the Node when added to the model and thereafter it should not change.
	 *
	 * @return  String - a unique identifier for a specific node
	 */
	public String getNodeId(Object node);
	
	/**
	 * @return a node Object based on its unique identifier (obtained using getNodeId)
	 */
	public Object getNodeById(String id);
	
}
