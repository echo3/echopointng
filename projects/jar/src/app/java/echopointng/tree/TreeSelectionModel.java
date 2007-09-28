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
import java.beans.PropertyChangeListener;

/**
  * This interface represents the current state of the selection for
  * the <code>Tree</code> component.  
  */
public interface TreeSelectionModel {

	/** Selection can only be contiguous. This will only be enforced if
	 * a RowMapper instance is provided. */
	public static final int CONTIGUOUS_TREE_SELECTION = 2;

	/** Selection can contain any number of items that are not necessarily
	 * contiguous. */
	public static final int DISCONTIGUOUS_TREE_SELECTION = 4;

	/** Selection can only contain one path at a time. */
	public static final int SINGLE_TREE_SELECTION = 1;

	/**
	 * Add a PropertyChangeListener to the listener list.
	 * The listener is registered for all properties.
	 * <p>
	 * @param listener  The PropertyChangeListener to be added
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);
	/**
	  * Adds path to the current selection.  If path is not currently
	  * in the selection the TreeSelectionListeners are notified.
	  *
	  * @param path the new path to add to the current selection.
	  */
	public void addSelectionPath(TreePath path);
	/**
	  * Adds paths to the current selection.  If any of the paths in
	  * paths are not currently in the selection the TreeSelectionListeners
	  * are notified.
	  *
	  * @param paths the new paths to add to the current selection.
	  */
	public void addSelectionPaths(TreePath[] paths);
	/**
	  * Adds x to the list of listeners that are notified each time the
	  * selection changes.
	  *
	  * @param x the new listener to be added.
	  */
	public void addTreeSelectionListener(TreeSelectionListener x);
	/**
	  * Empties the current selection.  If this represents a change in the
	  * current selection, the selection listeners are notified.
	  */
	public void clearSelection();
	/**
	 * Returns the last path that was added.
	 */
	public TreePath getLeadSelectionPath();
	/**
	 * Returns the number of paths that are selected.
	 */
	public int getSelectionCount();
	/**
	 * Returns the selection mode.
	 */
	public int getSelectionMode();
	/**
	  * Returns the first path in the selection.
	  */
	public TreePath getSelectionPath();
	/**
	  * Returns the paths in the selection.
	  */
	public TreePath[] getSelectionPaths();
	/**
	  * Returns true if the path, path, is in the current selection.
	  */
	public boolean isPathSelected(TreePath path);
	/**
	  * Returns true if the selection is currently empty.
	  */
	public boolean isSelectionEmpty();
	/**
	 * Remove a PropertyChangeListener from the listener list.
	 * This removes a PropertyChangeListener that was registered
	 * for all properties.
	 *
	 * @param listener  The PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);
	/**
	  * Removes path from the selection.  If path is in the selection
	  * The TreeSelectionListeners are notified.
	  *
	  * @param path the path to remove from the selection.
	  */
	public void removeSelectionPath(TreePath path);
	/**
	  * Removes paths from the selection.  If any of the paths in paths
	  * are in the selection the TreeSelectionListeners are notified.
	  *
	  * @param paths the paths to remove from the selection.
	  */
	public void removeSelectionPaths(TreePath[] paths);
	/**
	  * Removes x from the list of listeners that are notified each time
	  * the selection changes.
	  *
	  * @param x the listener to remove.
	  */
	public void removeTreeSelectionListener(TreeSelectionListener x);
	/**
	 * Sets the selection model, which must be one of SINGLE_TREE_SELECTION,
	 * CONTIGUOUS_TREE_SELECTION or DISCONTIGUOUS_TREE_SELECTION.
	 */
	public void setSelectionMode(int mode);
	/**
	  * Sets the selection to path.  If this represents a change, then
	  * the TreeSelectionListeners are notified.
	  *
	  * @param path new path to select
	  */
	public void setSelectionPath(TreePath path);
	/**
	  * Sets the selection to the the paths.  If this represents a
	  * change the TreeSelectionListeners are notified.
	  *
	  * @param paths new selection.
	  */
	public void setSelectionPaths(TreePath[] paths);
}
