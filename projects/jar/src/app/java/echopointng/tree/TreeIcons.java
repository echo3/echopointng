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
import nextapp.echo2.app.ImageReference;

/**
 * This interface is called by the Tree rendering code to retrieve named
 * icon images.  These icons are sued to provide visuals such as the lines 
 * between tree nodes and expnasion handles. The <code>TreeIcons</code> interface
 * can be used by <code>TreeCellRenderer</code> objects to obtain default 
 * icons for "parent" and "leaf" nodes in the Tree.
 * <p>
 * The tree will ask for nodes with the following names :
 * <p>
 * ICON_PLUS			- the expanding + for normal nodes with children <br>
 * ICON_PLUSBOTTOM		- the expanding + for the last node of a parent node<br>
 * ICON_MINUS			- the contracting - for normal nodes with children<br>
 * ICON_MINUSBOTTOM 	- the contracting - for the last node of a parent node<br>
 * ICON_FOLDER			- a folder icon for closed nodes with children<br>
 * ICON_FOLDEROPEN		- a folder icon for opened nodes with children<br>
 * ICON_JOIN			- a joiner line icon for a normal nodes<br>
 * ICON_JOINEBOTTOM		- a joiner line icon for the last node of a parent node<br> 
 * ICON_JOINENOLINE		- a joiner icon for normal nodes when no lines awre required<br>
 * ICON_LEAF			- a leaf icon for nodes with no children<br>
 * ICON_LINE			- a line icon between nodes<br>
 * ICON_EMPTY			- a empty icon between nodes<br>
 * ICON_ROOT			- a root icon<br>
 * ICON_ROOTOPEN		- a root open icon<br>
 */
public interface TreeIcons {
	public static final String ICON_PLUS = "plus";
	public static final String ICON_PLUSBOTTOM = "plusbottom";
	public static final String ICON_MINUS = "minus";
	public static final String ICON_MINUSBOTTOM = "minusbottom";
	public static final String ICON_FOLDER = "folder";
	public static final String ICON_FOLDEROPEN = "folderopen";
	public static final String ICON_JOIN = "join";
	public static final String ICON_JOINBOTTOM = "joinbottom";
	public static final String ICON_JOINNOLINE = "joinnoline";
	public static final String ICON_LEAF = "leaf";
	public static final String ICON_LINE = "line";
	public static final String ICON_EMPTY = "empty";
	public static final String ICON_ROOT = "root";
	public static final String ICON_ROOTOPEN = "rootopen";
	
/**
 * Returns an icon image with the given name
 */
public ImageReference getIcon(String iconName);
/**
 * Sets a named icon image into the TreeIcon object
 */
public void setIcon(String iconName, ImageReference iconImage);
}
