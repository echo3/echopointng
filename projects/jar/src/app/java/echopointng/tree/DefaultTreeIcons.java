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
import java.util.HashMap;
import java.util.Map;

import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;

/**
 * This class hold a series of "named" image icons, used by the Tree
 * during rendering, for visuals such as lines between nodes and expansion
 * handles.
 */
public class DefaultTreeIcons implements TreeIcons, java.io.Serializable {

	/** the map of icons images to names */
	protected Map iconMap = new HashMap();

	/** the default width of the icons is 19px */
	public static final Extent DEFAULT_WIDTH = new Extent(19,Extent.PX);

	/** the default height of the icons is 16px */
	public static final Extent DEFAULT_HEIGHT = new Extent(16,Extent.PX);

	/** default tree icons */
	private static final String imagepath = "/echopointng/resource/images/tree/";
	
	public static ImageReference treeEmpty = new ResourceImageReference(imagepath+"tree_empty.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	public static ImageReference treeFolder = new ResourceImageReference(imagepath+"tree_folder.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	public static ImageReference treeFolderOpen = new ResourceImageReference(imagepath+"tree_folderopen.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	public static ImageReference treeLeaf = new ResourceImageReference(imagepath+"tree_leaf.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	public static ImageReference treeJoin = new ResourceImageReference(imagepath+"tree_join.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	public static ImageReference treeJoinBottom = new ResourceImageReference(imagepath+"tree_joinbottom.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	public static ImageReference treeLine = new ResourceImageReference(imagepath+"tree_line.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	public static ImageReference treeMinus = new ResourceImageReference(imagepath+"tree_minus.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	public static ImageReference treeMinusBottom = new ResourceImageReference(imagepath+"tree_minusbottom.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	public static ImageReference treePlus = new ResourceImageReference(imagepath+"tree_plus.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	public static ImageReference treePlusBottom = new ResourceImageReference(imagepath+"tree_plusbottom.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	public static ImageReference treeRoot = new ResourceImageReference(imagepath+"tree_folder.gif", DEFAULT_WIDTH, DEFAULT_HEIGHT);

	/**
	 * Creates the DefaultTreeIcons with the default static default icon images
	 */
	public DefaultTreeIcons() {

		setIcon(ICON_EMPTY, treeEmpty);

		setIcon(ICON_FOLDER, treeFolder);

		setIcon(ICON_FOLDEROPEN, treeFolderOpen);

		setIcon(ICON_JOIN, treeJoin);

		setIcon(ICON_JOINBOTTOM, treeJoinBottom);

		setIcon(ICON_JOINNOLINE, treeEmpty);

		setIcon(ICON_LEAF, treeLeaf);

		setIcon(ICON_LINE, treeLine);

		setIcon(ICON_MINUS, treeMinus);

		setIcon(ICON_MINUSBOTTOM, treeMinusBottom);

		setIcon(ICON_PLUS, treePlus);

		setIcon(ICON_PLUSBOTTOM, treePlusBottom);

		setIcon(ICON_ROOT, treeRoot);

		setIcon(ICON_ROOTOPEN, treeFolderOpen);
	}
	/**
	 * Returns an icon image with the given name
	 */
	public ImageReference getIcon(String iconName) {
		return (ImageReference) iconMap.get(iconName);
	}
	/**
	 * Sets a named icon into the TreeIcon object
	 */
	public void setIcon(String iconName, ImageReference iconImage) {
		iconMap.put(iconName, iconImage);
	}
}
