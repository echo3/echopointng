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
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.Label;
import echopointng.Tree;
import echopointng.xhtml.XhtmlFragment;

/**
 * Used to display an entry in a tree.  If the tree cell value is a
 * DefaultMutableTreeNode and its user object is a Component
 * then the DefaultTreeCellRenderer will return null on getTreeCellRenderText and
 * return the Component in getTreeCellRendererComponent.
 * <p>
 * Otherwise it will "cast" the cell value to a String via toString() and return a
 * LabelEx via the getTreeCellRenderText method.  It uses the Tree's TreeIcon object to 
 * obtain an icon for "parent" and "leaf" nodes.
 * <p>
 * You can derived your own <code>TreeCellRenderer</code>class from this one and 
 * specify your own icons for each node if you wish.
 * <p>
 * Note that this class also has Style support so you can set appearance 
 * attributes via a Style object or the EchoPoint CSS support.
 * <p>
 * The standard LabelEx background, foreground and font properties are used for non
 * selected tree cell nodes.  The selectedBackground, selectedForeground and 
 * selectedFont properties are used for selected tree cell nodes.
 * 
 */
public class DefaultTreeCellRenderer extends Label implements TreeCellRenderer {

	/** The default Tree Cell Font */
	public static final Font DEFAULT_FONT = new Font(Font.ARIAL, Font.PLAIN, new Extent(8,Extent.PT));

	/** The default Selected Tree Cell Background */
	public static final Color DEFAULT_SELECTED_BACKGROUND = Color.BLACK;

	/** The default Selected Tree Cell FOREGROUND */
	public static final Color DEFAULT_SELECTED_FOREGROUND = Color.WHITE;

	/** The default Selected Tree Cell Font */
	public static final Font DEFAULT_SELECTED_FONT = new Font(Font.ARIAL, Font.BOLD, new Extent(8,Extent.PT));
	
	public static final String PROPERTY_SELECTED_BACKGROUND = "selectedBackground";
	public static final String PROPERTY_SELECTED_FOREGROUND = "selectedForeground";
	public static final String PROPERTY_SELECTED_FONT = "selectedFont";
	
	private Color saveBackground;
	private Color saveForeground;
	private Font  saveFont;
	
	/**
	  * Creates a new instance of DefaultTreeCellRenderer.  
	  */
	public DefaultTreeCellRenderer() {
		super();
		
		saveBackground = super.getBackground();
		saveForeground = super.getForeground();
		saveFont = super.getFont();
		
		//setHorizontalAlignment(EchoConstants.LEFT);
		setSelectedBackground(DEFAULT_SELECTED_BACKGROUND);
		setSelectedForeground(DEFAULT_SELECTED_FOREGROUND);
		setSelectedFont(DEFAULT_FONT);
		
		setBackground(null);
		setFont(DEFAULT_FONT);
	}

	/**
	  * @return the background color to be used for selected tree cell nodes.
	  */
	public Color getSelectedBackground() {
		return (Color) getProperty(PROPERTY_SELECTED_BACKGROUND);
	}
	/**
	  * @return the foreground color to be used for selected tree cell nodes.
	  */
	public Color getSelectedForeground() {
		return (Color) getProperty(PROPERTY_SELECTED_FOREGROUND);
	}
	/**
	  * @return the font to be used for selected tree cell nodes.
	  */
	public Font getSelectedFont() {
		return (Font) getProperty(PROPERTY_SELECTED_FONT);
	}
	
	/**
	 * Sets the background color to be used for selected tree cell nodes.
	 * 
	 * @param newValue -  The newValue to set.
	 */
	public void setSelectedBackground(Color newValue) {
		setProperty(PROPERTY_SELECTED_BACKGROUND,newValue);
	}
	
	/**
	 * Sets the foreground color to be used for selected tree cell nodes.
	 * 
	 * @param newValue -  The newValue to set.
	 */
	public void setSelectedForeground(Color newValue) {
		setProperty(PROPERTY_SELECTED_FOREGROUND,newValue);
	}
	
	/**
	 * Sets the font to be used for selected tree cell nodes.
	 * 
	 * @param newValue -  The newValue to set.
	 */
	public void setSelectedFont(Font newValue) {
		setProperty(PROPERTY_SELECTED_FONT,newValue);
	}
	
	/**
	 * @see nextapp.echo2.app.Component#setBackground(nextapp.echo2.app.Color)
	 */
	public void setBackground(Color newValue) {
		this.saveBackground = newValue;
		super.setBackground(newValue);
	}
	
	/**
	 * @see nextapp.echo2.app.Component#setForeground(nextapp.echo2.app.Color)
	 */
	public void setForeground(Color newValue) {
		this.saveForeground = newValue;
		super.setForeground(newValue);
	}
	
	/**
	 * @see nextapp.echo2.app.Component#setFont(nextapp.echo2.app.Font)
	 */
	public void setFont(Font newValue) {
		this.saveFont = newValue;
		super.setFont(newValue);
	}
	
	/**
	 * @see echopointng.tree.TreeCellRenderer#getTreeCellRendererComponent(Tree, Object, boolean, boolean, boolean)
	 */
	public Component getTreeCellRendererComponent(Tree tree, Object node, boolean selected, boolean expanded, boolean leaf) {
		if (node instanceof DefaultMutableTreeNode) {
			Object value = ((DefaultMutableTreeNode) node).getUserObject();
			if (value instanceof Component) {
				Component c = (Component) value;
				c.setEnabled(tree.isRenderEnabled());
				return c;
			}
		}
		return null;
	}
	
	/**
	 * @see echopointng.tree.TreeCellRenderer#getTreeCellRendererXhtml(echopointng.Tree, java.lang.Object, boolean, boolean, boolean)
	 */
	public XhtmlFragment getTreeCellRendererXhtml(Tree tree, Object node, boolean selected, boolean expanded, boolean leaf) {
		if (node instanceof XhtmlFragment) {
			return (XhtmlFragment) node;
		}
		if (node instanceof DefaultMutableTreeNode) {
			Object value = ((DefaultMutableTreeNode) node).getUserObject();
			if (value instanceof XhtmlFragment) {
				return (XhtmlFragment) value;
			}
		}
		return null;
	}
	/**
	  * Configures the renderer based on the passed in parameters.
	  * <p>
	  * The foreground color of the LabelEx is set based on the selection while
	  * the icon is set based on on <code>leaf</code> and <code>expanded</code>.
	  * <p>
	  * The same LabelEx object is returned for each call to this method.  The
	  * Tree rendering code uses only the public properties of the return 
	  * LabelEx.  In fact since DefaultTreeCellRenderer is derived from LabelEx
	  * it simply returns itself.
	  * 
	  * @see echopointng.tree.TreeCellRenderer#getTreeCellRendererText(Tree, Object, boolean, boolean, boolean)
	  */
	public Label getTreeCellRendererText(Tree tree, Object node, boolean sel, boolean expanded, boolean leaf) {

		String stringValue = "";
		if (node instanceof DefaultMutableTreeNode) {
			Object value = ((DefaultMutableTreeNode) node).getUserObject();
			if (value instanceof Component || value instanceof XhtmlFragment || value == null) {
				return null;
			}
			stringValue = value.toString();
		} else {
			if (node != null) {
				stringValue = node.toString();
			}
		}

		// text 
		setText(stringValue);
		
		// fonts and colors
		if (sel) {
			super.setForeground(getSelectedForeground());
			super.setBackground(getSelectedBackground());
			if (getSelectedFont() == null)
				super.setFont(tree.getFont());
			else
				super.setFont(getSelectedFont());
		} else {
			super.setForeground(saveForeground);
			super.setBackground(saveBackground);
			if (saveFont == null)
				super.setFont(tree.getFont());
			else
				super.setFont(saveFont);
		}

		// icons
		boolean isRoot = false;
		TreeModel model = tree.getModel();
		if (model != null)
			isRoot = (node == model.getRoot());

		setIcon(null);
		TreeIcons icons = tree.getTreeIcons();
		if (icons != null) {
			if (isRoot && expanded) {
				setIcon(icons.getIcon(TreeIcons.ICON_ROOTOPEN));
			} else if (isRoot) {
				setIcon(icons.getIcon(TreeIcons.ICON_ROOT));
			} else if (leaf) {
				setIcon(icons.getIcon(TreeIcons.ICON_LEAF));
			} else if (expanded) {
				setIcon(icons.getIcon(TreeIcons.ICON_FOLDEROPEN));
			} else {
				setIcon(icons.getIcon(TreeIcons.ICON_FOLDER));
			}
		}

		//enabledness
		//setEnabled(tree.isEnabled());
		return this;
	}
}
