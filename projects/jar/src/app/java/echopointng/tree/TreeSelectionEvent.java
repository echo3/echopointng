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
import java.util.EventObject;

/**
 * An event that characterizes a change in the current
 * selection.  The change is based on any number of paths.
 * TreeSelectionListeners will generally query the source of
 * the event for the new selected status of each potentially
 * changed row.
 * <p>
 *
 */
public class TreeSelectionEvent extends EventObject {
    /** Paths this event represents. */
    protected TreePath[] paths;
    /** For each path identifies if that is path is in fact new. */
    protected boolean[] areNew;
    /** leadSelectionPath before the paths changed, may be null. */
    protected TreePath oldLeadSelectionPath;
    /** leadSelectionPath after the paths changed, may be null. */
    protected TreePath newLeadSelectionPath;

    /**
      * Represents a change in the selection of a TreeSelectionModel.
      * paths identifies the paths that have been either added or
      * removed from the selection.
      *
      * @param source source of event
      * @param paths the paths that have changed in the selection
      */
    public TreeSelectionEvent(
        Object source,
        TreePath[] paths,
        boolean[] areNew,
        TreePath oldLeadSelectionPath,
        TreePath newLeadSelectionPath) {
        super(source);
        this.paths = paths;
        this.areNew = areNew;
        this.oldLeadSelectionPath = oldLeadSelectionPath;
        this.newLeadSelectionPath = newLeadSelectionPath;
    }
    /**
      * Represents a change in the selection of a TreeSelectionModel.
      * path identifies the path that have been either added or
      * removed from the selection.
      *
      * @param source source of event
      * @param path the path that has changed in the selection
      * @param isNew whether or not the path is new to the selection, false
      * means path was removed from the selection.
      */
    public TreeSelectionEvent(
        Object source,
        TreePath path,
        boolean isNew,
        TreePath oldLeadSelectionPath,
        TreePath newLeadSelectionPath) {
        super(source);
        paths = new TreePath[1];
        paths[0] = path;
        areNew = new boolean[1];
        areNew[0] = isNew;
        this.oldLeadSelectionPath = oldLeadSelectionPath;
        this.newLeadSelectionPath = newLeadSelectionPath;
    }
    /**
     * Returns a copy of the receiver, but with the source being newSource.
     */
    public Object cloneWithSource(Object newSource) {
        // Fix for IE bug - crashing
        return new TreeSelectionEvent(newSource, paths, areNew, oldLeadSelectionPath, newLeadSelectionPath);
    }
    /**
     * Returns the current lead path.
     */
    public TreePath getNewLeadSelectionPath() {
        return newLeadSelectionPath;
    }
    /**
     * Returns the path that was previously the lead path.
     */
    public TreePath getOldLeadSelectionPath() {
        return oldLeadSelectionPath;
    }
    /**
      * Returns the first path element.
      */
    public TreePath getPath() {
        return paths[0];
    }
    /**
      * Returns the paths that have been added or removed from the
      * selection.
      */
    public TreePath[] getPaths() {
        int numPaths;
        TreePath[] retPaths;

        numPaths = paths.length;
        retPaths = new TreePath[numPaths];
        System.arraycopy(paths, 0, retPaths, 0, numPaths);
        return retPaths;
    }
    /**
     * Returns true if the first path element has been added to the
     * selection, a return value of false means the first path has been
     * removed from the selection.
     */
    public boolean isAddedPath() {
        return areNew[0];
    }
    /**
     * Returns true if the path identified by path was added to the
     * selection. A return value of false means the path was in the
     * selection but is no longer in the selection. This will raise if
     * path is not one of the paths identified by this event.
     */
    public boolean isAddedPath(TreePath path) {
        for (int counter = paths.length - 1; counter >= 0; counter--)
            if (paths[counter].equals(path))
                return areNew[counter];
        throw new IllegalArgumentException("path is not a path identified by the TreeSelectionEvent");
    }
}
