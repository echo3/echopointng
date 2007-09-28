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
 * Defines the requirements for a tree node object that can change --
 * by adding or removing child nodes, or by changing the contents
 * of a user object stored in the node.
 *
 */

public interface MutableTreeNode extends TreeNode {
	/**
	 * Adds <code>child</code> to the receiver at <code>index</code>.
	 * <code>child</code> will be messaged with <code>setParent</code>.
	 */
	public void insert(MutableTreeNode child, int index);
	/**
	 * Removes the child at <code>index</code> from the receiver.
	 */
	public void remove(int index);
	/**
	 * Removes <code>node</code> from the receiver. <code>setParent</code>
	 * will be messaged on <code>node</code>.
	 */
	public void remove(MutableTreeNode node);
	/**
	 * Removes the receiver from its parent.
	 */
	public void removeFromParent();
	/**
	 * Sets the parent of the receiver to <code>newParent</code>.
	 */
	public void setParent(MutableTreeNode newParent);
	/**
	 * Resets the user object of the receiver to <code>object</code>.
	 */
	public void setUserObject(Object object);
}
