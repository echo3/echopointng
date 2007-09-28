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

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Label;
import echopointng.Tree;
import echopointng.xhtml.XhtmlFragment;

/**
 * The <code>TreeCellRender</code> is responsible for returning Components
 * used to render a Tree node. In the most general form the
 * <code>getTreeCellRendererText</code> method will be called to return a
 * <code>LabelEx</code> component that contains the tree node's :
 * <ul>
 * <li>text</li>
 * <li>icon</li>
 * <li>vertical text alignment</li>
 * <li>background</li>
 * <li>foreground</li>
 * <li>font</li>
 * </ul>
 * <p>
 * A more specific case is when a Component is to be returned as the Tree cell.
 * In this case the <code>getTreeCellRendererComponent</code> method will be
 * called to provide the component for the Tree cell.
 * 
 */
public interface TreeCellRenderer {

	/**
	 * Returns a Component that will be rendered as a Tree cell.
	 * <p>
	 * If <code>selected</code> is true, the cell will be drawn as if
	 * selected. If <code>expanded</code> is true the node is currently
	 * expanded and if <code>leaf</code> is true the node represets a leaf
	 * <code>tree</code> is the Tree the receiver is being configured for.
	 * <p>
	 * This method is called second by the tree rendering code, and only after
	 * the getTreeCellRendererText method has returned null.
	 * <p>
	 * The returned Component MUST be a unqiue Component since it will be
	 * rendered as the Tree cell.
	 * <p>
	 * 
	 * @return Component that is used to draw the value.
	 */
	public Component getTreeCellRendererComponent(Tree tree, Object value, boolean selected, boolean expanded, boolean leaf);

	/**
	 * Returns a LabelEx that will be used to render a Tree cell. The following
	 * attributes are used to render the Tree cell.
	 * <ul>
	 * <li>text</li>
	 * <li>icon</li>
	 * <li>vertical text alignment</li>
	 * <li>background</li>
	 * <li>foreground</li>
	 * <li>font</li>
	 * </ul>
	 * <p>
	 * If <code>selected</code> is true, the cell will be drawn as if
	 * selected. If <code>expanded</code> is true the node is currently
	 * expanded and if <code>leaf</code> is true the node represets a leaf
	 * <code>tree</code> is the Tree the receiver is being configured for.
	 * <p>
	 * This method is called first by the tree rendering code. If this returns
	 * null, then the getTreeCellRendererComponent method is then invoked.
	 * <p>
	 * The returned LabelEx does NOT have to be a unique <code>Component</code>
	 * since only the public values of the LabelEx are used by the the Tree
	 * rendering code. Therefore it does NOT have to be a Component in the Echo
	 * hierarchy nor does it have to be a unique LabelEx object.
	 * <p>
	 * You could return the same LabelEx object back each time this method is
	 * called, only changing the background, forground, font and icon as
	 * appropriate.
	 * <p>
	 * Returns the LabelEx that is used to draw the Tree cell.
	 * 
	 * @return LabelEx that is used to draw the Tree cell.
	 */
	public Label getTreeCellRendererText(Tree tree, Object value, boolean selected, boolean expanded, boolean leaf);

	/**
	 * Returns a <code>XhtmlFragment</code> that will be rendered as a Tree
	 * cell.
	 * <p>
	 * If <code>selected</code> is true, the cell will be drawn as if
	 * selected. If <code>expanded</code> is true the node is currently
	 * expanded and if <code>leaf</code> is true the node represets a leaf
	 * <code>tree</code> is the <code>Tree</code> the receiver is being
	 * configured for.
	 * <p>
	 * This method is called third by the Tree rendering code, and only after
	 * the getTreeCellRendererText and getTreeCellRendererComponent method have
	 * returned null.
	 * <p>
	 * 
	 * @return the XhtmlFragment that is used to draw the cell value.
	 */
	public XhtmlFragment getTreeCellRendererXhtml(Tree tree, Object value, boolean selected, boolean expanded, boolean leaf);

}
