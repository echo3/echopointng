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

package echopointng.ui.syncpeer;

import java.util.ArrayList;
import java.util.Stack;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Style;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.SynchronizePeerFactory;
import nextapp.echo2.webcontainer.propertyrender.AlignmentRender;
import nextapp.echo2.webcontainer.propertyrender.ColorRender;
import nextapp.echo2.webcontainer.propertyrender.FontRender;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.util.DomUtil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.Tree;
import echopointng.tree.TreeCellRenderer;
import echopointng.tree.TreeIcons;
import echopointng.tree.TreeModel;
import echopointng.tree.TreePath;
import echopointng.ui.util.HtmlTable;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.RenderingContext;
import echopointng.xhtml.XhtmlFragment;

/**
 * <code>TreeRenderer</code> will render a <code>Tree</code> into a list of 
 * table elements.  This allows the rendering of the individual "tree rows" to
 * be broken out of the main Tree peer.
 * <p>
 * A Tree is rendered as a series of rows (know here as "tree rows").  The rows
 * are to be line up directly on top of one another.  The "TD cells" of the tree
 * rows must line up in order for the Tree to look right.  The effect looks something like this :
 * <pre>
 * |------------|-------------------------------------------------------------
 * |            |                                       
 * |   folder   |  Root Text                               
 * |   icon     |                                          
 * |------------|------------|------------|-----------------------------------
 * |            |    +/-     |            |               
 * |   empty    |  expando   |   folder   |    Child Text
 * |            |   icon     |    icon    |             
 * |------------|------------|------------|-----------------------------------
 * |            |    +/-     |            |               
 * |   empty    |  expando   |   folder   |    Child Text
 * |            |   icon     |    icon    |             
 * |------------|------------|------------|------------|----------------------
 * |            |            |   +/-      |            |
 * |   empty    |   empty    |   expando  |  folder    |  Grandchild Text
 * |            |            |    icon    |   icon     |
 * |------------|------------|------------|------------|----------------------
 * |            |            |   +/-      |            |
 * |   empty    |   empty    |   expando  |  folder    |  Grandchild Text
 * |            |            |    icon    |   icon     |
 * |------------|------------|------------|------------|----------------------
 * </pre>
 * <p>
 * One thing to remember is that the rows are table elements not rows within
 * a single table.  This allows the most flexibility for Tree and TreeTables
 * so it really more like this :
 * 
 * <pre>
 * |------------|------------|------------|------------|----------------------
 * |            |            |   +/-      |            |
 * |   empty    |   empty    |   expando  |  folder    |  Grandchild Text
 * |            |            |    icon    |   icon     |
 * |------------|------------|------------|------------|----------------------
 * 
 * |------------|------------|------------|------------|----------------------
 * |            |            |   +/-      |            |
 * |   empty    |   empty    |   expando  |  folder    |  Grandchild Text
 * |            |            |    icon    |   icon     |
 * |------------|------------|------------|------------|----------------------
 *
 * |------------|------------|------------|------------|----------------------
 * |            |            |   +/-      |            |
 * |   empty    |   empty    |   expando  |  folder    |  Grandchild Text
 * |            |            |    icon    |   icon     |
 * |------------|------------|------------|------------|----------------------
 * </pre>

 * 
 */
public class TreeRenderer  {

	/**
	 * <code>EventSupportCallBack</code> is a callback that is used to
	 * call back to the caller and get it to add "event handling" code to
	 * the renderer elements.  That this render helper does not need to know about 
	 * the gory detaiuls of event creation.
	 */
	public interface EventSupportCallBack {

		/**
		 * This is called to put event support on the "expando" cell, which is
		 * the plus/minus cell for exapnding tree nodes.  You should return the
		 * unique identifier for this element.
		 */
		public String onExpandoCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode, TreePath treeNodePath);

		/**
		 * This is called to put event support on the "node icon" cell, which is
		 * the icon next to the node text.  You should return the
		 * unique identifier for this element.
		 */
		public String onNodeIconCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode, TreePath treeNodePath);

		/**
		 * This is called to put event support on the "node text" cell.  You should return the
		 * unique identifier for this element.
		 */
		public String onNodeTextCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode, TreePath treeNodePath);

		/**
		 * This is called to put event support on the "node component" cell.  this is the node
		 * that contains a component as child (as opposed to an icon/text). You should return the
		 * unique identifier for this element.
		 */
		public String onNodeComponentCell(RenderingContext rc, CssStyle cssStyle, Element cellElement, Tree tree, Object treeNode, TreePath treeNodePath);
		
		/**
		 * This is called to put event support on the "tree row" element.  
		 */
		public void onTreeRow(RenderingContext rc, CssStyle cssStyle, Element treeElement, Tree tree, TreePath treeNodePath);
	}
	
	private Tree tree;
	private TreeModel treeModel;
	private TreeIcons treeIcons;
	private ImageManager imageManager;
	private RenderingContext rc;
	private Style fallbackStyle;
	private EventSupportCallBack eventSupportCallBack;
	private TreeCellRenderer cellRenderer;
	private boolean showLines;
	private boolean isRootVisible;
	private boolean showRootHandles;
	private boolean isCellWidthConstrained;
	private boolean isSelectionIncludesIcon;

	
	private static final Alignment TOP_VERT_ALIGNMENT  = new Alignment(Alignment.DEFAULT,Alignment.TOP);
	private static final Alignment BOTTOM_VERT_ALIGNMENT  = new Alignment(Alignment.DEFAULT,Alignment.BOTTOM);
	// use  for debugging only
	private static final int DEBUG_TABLE_BORDER = 0;
	private static final boolean DEBUG_PATH_IDS = false;
	
	
	/**
	 * This returns a String identifier for a given TreePath.  It simply
	 * consists of nodeIds provided by the TreeModel.  The top most level will
	 * be known as [root].
	 * <p>
	 * An example is : [root][0][43][78]
	 * 
	 * @param treeNodePath - the TreePath to create an PathId for
	 * @param model - the model rom which to work out the child index of the nodes
	 * 
	 * @return a String in the form [root][n][n][n][n]...
	 */
	public static String createPathId(TreePath treeNodePath, TreeModel model) {
		Object[] path = treeNodePath.getPath();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < path.length; i++) {
			sb.append('[');
			if (i == 0) {
				sb.append("root");
			} else {
				sb.append(model.getNodeId(path[i]));
			}
			sb.append(']');
		}		
		return sb.toString();
	}
	
	/**
	 * Constructs a <code>TreeRenderer</code>
	 *
	 * @param rc - the RenderingContext to use
	 * @param eventSupportCallBack - the call back for event handling
	 * @param tree - the Tree in question
	 * @param imageManager - the ImageManager to use to contain images.
	 */
	public TreeRenderer(RenderingContext rc, EventSupportCallBack eventSupportCallBack, Tree tree, Style fallbackStyle, ImageManager imageManager) {
		this.rc = rc;
		this.eventSupportCallBack  = eventSupportCallBack;
		this.tree = tree;
		this.imageManager = imageManager;
		this.fallbackStyle = fallbackStyle;
		this.treeModel = (TreeModel) getRP(Tree.PROPERTY_MODEL,fallbackStyle);
		this.treeIcons = (TreeIcons) getRP(Tree.PROPERTY_TREE_ICONS,fallbackStyle);
		this.cellRenderer = (TreeCellRenderer) getRP(Tree.PROPERTY_CELL_RENDERER,fallbackStyle);
		this.showLines = getRP(Tree.PROPERTY_LINES_DRAWN,true,fallbackStyle);
		this.isRootVisible = getRP(Tree.PROPERTY_ROOT_VISIBLE,true,fallbackStyle);
		this.showRootHandles = getRP(Tree.PROPERTY_SHOWS_ROOT_HANDLES,true,fallbackStyle);
		this.isCellWidthConstrained = getRP(Tree.PROPERTY_CELL_WIDTH_CONTRAINED,false,fallbackStyle);
		this.isSelectionIncludesIcon = getRP(Tree.PROPERTY_SELECTION_INCLUDES_ICON,true,fallbackStyle);
	}
	
	private Object getRP(String propertyName, Style style) {
		Object value = tree.getRenderProperty(propertyName);
		if (value == null && style != null) {
			value = style.getProperty(propertyName);
		}
		return value;
	}

	private boolean getRP(String propertyName, boolean defaultValue, Style style) {
		Object value = tree.getRenderProperty(propertyName);
		if (value == null && style != null) {
			value = style.getProperty(propertyName);
		}
		if (value == null) {
			return defaultValue;
		}
		return ((Boolean) value).booleanValue();
	}

	/**
	 * Returns true if the specified node is the last sibling in its level
	 * of tree nodes.
	 */
	private boolean isLastSibling(TreePath treeNodePath) {
		TreePath parentPath = getParent(treeNodePath);
		if (parentPath == null) {
			return true; // must be root node
		}
		Object parentNode = parentPath.getLastPathComponent();
		Object treeNode = treeNodePath.getLastPathComponent();
		
		int childCount = treeModel.getChildCount(parentNode);
		int childIndex = treeModel.getIndexOfChild(parentNode,treeNode);
		return childIndex == (childCount-1);
	}
	
	/**
	 * Not all TreeModels support getParent().  Therefore if we encounter
	 * one and we dont have the parent easily available, then we search the tree
	 * model from the top and look for the parent that way.  this is not as bad as it
	 * might seem because we only navigate down expanded tree nodes, not every node in 
	 * the TreeMode.
	 * <p>
	 * We really should not be asking for the parent of a node if its not visible
	 * and hence this should work. 
	 */
	private TreePath getParent(TreePath treeNodePath) {
		Object root = treeModel.getRoot();
		Object treeNode = treeNodePath.getLastPathComponent();
		if (treeNode.equals(root)) {
			return null;
		}
		TreePath parentPath = treeNodePath.getParentPath();
		if (parentPath == null) {
			TreePath path = new TreePath(root);
			parentPath = findParentDownwards(root,path,treeNode);
			//
			// this should never happen as the parent must be visible for the child to be visible
			if (parentPath == null) {
				throw new IllegalStateException("Unexpected tree node state.  The node is visible but its parent is not!");
			}
		}
		return parentPath;
	}
	
	/*
	 * A brute force approach to finding the parent of a visible child node.  It
	 * starts from the top and finds all children and then looks for the node
	 * and hence knows it parent.  This should be used as the method of last 
	 * resort!
	 */
	private TreePath findParentDownwards(Object currentParent, TreePath currentTreePath, Object searchNode) {
		if (treeModel.isLeaf(currentParent) || tree.isExpanded(currentTreePath) == false) {
			return null;
		}
		int childCount = treeModel.getChildCount(currentParent);
		for (int i = 0; i < childCount; i++) {
			Object childNode = treeModel.getChild(currentParent,i);
			TreePath childPath = new TreePath(currentTreePath,childNode);
			if (searchNode.equals(childNode)) {
				return currentTreePath;
			}
			TreePath newParentPath = findParentDownwards(childNode,childPath,searchNode);
			if (newParentPath != null) {
				return newParentPath;
			}
		}
		return null;
	}

	
	/**
	 * Recurses up the tree hierachy and creates a "cell command" stack which the number
	 * of spacer cells required for a tree row and whether the cells should be "empty" or
	 * should contain joining "lines".
	 */
	private void createCellCommandsStack(Stack stack, TreePath treeNodePath) {
		Object root = treeModel.getRoot();
		Object treeNode = treeNodePath.getLastPathComponent();
		if (root.equals(treeNode)) {
			return;
		}
		while (true) {
			TreePath parentPath = getParent(treeNodePath);
			if (root.equals(parentPath.getLastPathComponent())) {
				if (isRootVisible && showRootHandles) {
					stack.push("empty");
				}
				return;
			}
			if (isLastSibling(parentPath)) {
				stack.push("empty");
			} else {
				stack.push("line");
			}
			treeNodePath = parentPath;
		}
	}

	/**
	 * Called to create a CssStyle from the properties of the Label.  This will be
	 * the Label returned by the cell renderer.
	 * 
	 */
	private CssStyle createStyleFromLabel(Label cellLabel) {
		if (cellLabel == null) {
			return new CssStyle();
		}
		Color background = (Color) cellLabel.getRenderProperty(Component.PROPERTY_BACKGROUND);
		if (background == null)
			background = (Color) getRP(Component.PROPERTY_BACKGROUND,fallbackStyle);

		Color foreground = (Color) cellLabel.getRenderProperty(Component.PROPERTY_FOREGROUND);
		if (foreground == null)
			foreground = (Color) getRP(Component.PROPERTY_FOREGROUND,fallbackStyle);

		Font font = (Font) cellLabel.getRenderProperty(Component.PROPERTY_FONT);
		if (font == null)
			font = (Font) getRP(Component.PROPERTY_FONT,fallbackStyle);

        CssStyle cssStyle = new CssStyle();
        //
        // some defaults that can be overrridden by the Label
		AlignmentRender.renderToStyle(cssStyle,BOTTOM_VERT_ALIGNMENT);
        
        Alignment cellAlignment = (Alignment) cellLabel.getRenderProperty(Label.PROPERTY_TEXT_ALIGNMENT);
		AlignmentRender.renderToStyle(cssStyle,cellAlignment);
		
        ColorRender.renderToStyle(cssStyle, foreground,background); 
        FontRender.renderToStyle(cssStyle, font);
        return cssStyle;
	}

	private CssStyle createStyleFromTree(Tree tree) {
		Color background = (Color) getRP(Component.PROPERTY_BACKGROUND,fallbackStyle);
		Color foreground = (Color) getRP(Component.PROPERTY_FOREGROUND,fallbackStyle);
		Font font = (Font) getRP(Component.PROPERTY_FONT,fallbackStyle);

        CssStyle cssStyle = new CssStyle();
        
        ColorRender.renderToStyle(cssStyle, foreground,background); 
        FontRender.renderToStyle(cssStyle, font);
        return cssStyle;
	}	
	/**
	 * Renders an image into the cell TD
	 */
	private void renderCellImage(ImageReference cellImageRef, Element cellTD) {
		if (cellImageRef != null) {
			CssStyle cssStyle = new CssStyle();
			AlignmentRender.renderToStyle(cssStyle,TOP_VERT_ALIGNMENT);
			if (cellImageRef.getWidth() != null)
				cssStyle.setAttribute("width", cellImageRef.getWidth().toString());
			if (cellImageRef.getHeight() != null)
				cssStyle.setAttribute("height", cellImageRef.getHeight().toString());
			Element imgE = ImageManager.createImgE(rc, cssStyle,cellImageRef);
			cellTD.appendChild(imgE);
		}
	}

	/**
	 * Renders an image into the cell TD
	 */
	private void renderCellImageSetWidths(CssStyle cssStyle, ImageReference cellImageRef, Element cellTD) {
		if (cellImageRef != null) {
			renderCellImage(cellImageRef,cellTD);
			cssStyle.setAttribute("padding","0");
			cssStyle.setAttribute("margin","0");
			if (cellImageRef.getWidth() != null) {
				cssStyle.setAttribute("width", cellImageRef.getWidth().toString());
			}
//			if (cellImageRef.getHeight() != null)
//				cssStyle.setAttribute("height", cellImageRef.getHeight().toString());
		}
	}
	
	/**
	 * Renders a tree row cell that has the empty image inside it
	 */
	private void renderEmptyCell(Element cellTD) {
		ImageReference icon = treeIcons.getIcon(TreeIcons.ICON_EMPTY);
		if (icon != null) {
			// keep a reference
			imageManager.addImage(icon);
			Element imgE = ImageManager.createImgE(rc,null,icon);
			cellTD.appendChild(imgE);
		}
		CssStyle cssStyle = createStyleFromTree(tree);
		cellTD.setAttribute("style",cssStyle.renderInline());
		cellTD.setAttribute("nowrap","nowrap");
	}

	/**
	 * Renders an image into the background of a style
	 */
	private void renderBackgroundImageIntoStyle(ImageReference cellImageRef, CssStyle cssStyle) {
		if (cellImageRef != null) {
			String iconUri = ImageManager.getURI(rc,cellImageRef);
			cssStyle.setAttribute("background-image","url(" + iconUri + ")");
			cssStyle.setAttribute("background-repeat","repeat-y");
		}
	}
	
	/**
	 * Renders a tree row cell that has the line image inside it as the background
	 */
	private void renderBackgroundLineCell(Element cellTD) {
		ImageReference cellImageRef;
		if (this.showLines) {
			cellImageRef = treeIcons.getIcon(TreeIcons.ICON_LINE);
		} else {
			cellImageRef = treeIcons.getIcon(TreeIcons.ICON_EMPTY);
		}
		CssStyle cssStyle = createStyleFromTree(tree);
		if (cellImageRef != null) {
			imageManager.addImage(cellImageRef);
			
			String iconUri = ImageManager.getURI(rc,cellImageRef);
			cssStyle.setAttribute("background-image","url(" + iconUri + ")");
			if (cellImageRef.getWidth() != null) {
				cssStyle.setAttribute("width", cellImageRef.getWidth().toString());
			}
			//if (cellImageRef.getHeight() != null) {
			//	cssStyle.setAttribute("height", cellImageRef.getHeight().toString());
			//}
			Element imgE = ImageManager.createImgE(rc,null,cellImageRef);
			cellTD.appendChild(imgE);
		}
		if (cssStyle.hasAttributes()) {
			cellTD.setAttribute("style",cssStyle.renderInline());
		}
		// need some content so it has body
		cellTD.setAttribute("nowrap","nowrap");
	}
	
	/**
	 * Renders the expando cell
	 */
	private void renderExpandoCell(Object treeNode, TreePath treeNodePath, Element cellTD) {
		ImageReference icon;
		boolean lastSibling = isLastSibling(treeNodePath);
		boolean isExpanded = tree.isExpanded(treeNodePath);
		boolean isRootNode = treeNode.equals(treeModel.getRoot());
		boolean isLeaf = treeModel.isLeaf(treeNode); 
		boolean needsEventHandling = false;
		// Write out the expando icons if they are not a leaf
		if (! isLeaf) {
			needsEventHandling = true;
			if (lastSibling && showLines && isRootNode == false) {
				if (isExpanded) {
					icon = treeIcons.getIcon(TreeIcons.ICON_MINUSBOTTOM);
				} else {
					icon = treeIcons.getIcon(TreeIcons.ICON_PLUSBOTTOM);
				}
			} else if (isRootNode && ! showRootHandles) {
				icon = treeIcons.getIcon(TreeIcons.ICON_EMPTY);
			} else {
				if (isExpanded) {
					icon = treeIcons.getIcon(TreeIcons.ICON_MINUS);
				} else {
					icon = treeIcons.getIcon(TreeIcons.ICON_PLUS);
				}
			}
		} else {
			needsEventHandling = false;
			if (showLines) {
				if (lastSibling) {
					icon = treeIcons.getIcon(TreeIcons.ICON_JOINBOTTOM);
				} else {
					icon = treeIcons.getIcon(TreeIcons.ICON_JOIN);
				}
			} else {
				icon = treeIcons.getIcon(TreeIcons.ICON_JOINNOLINE);
			}
		}
		// be double sure we have a reference to the icon maintained
		// just in case the TreeIcon implementation dynamically
		// generates them.
		imageManager.addImage(icon);

		CssStyle cellStyle = createStyleFromTree(tree);
		if (showLines && lastSibling == false && isRootNode == false) {
			ImageReference lineIcon = treeIcons.getIcon(TreeIcons.ICON_LINE);
			imageManager.addImage(lineIcon);
			
			renderBackgroundImageIntoStyle(lineIcon,cellStyle);
		}
		renderCellImageSetWidths(cellStyle,icon,cellTD);
		if (needsEventHandling) {
			String eventElementId = eventSupportCallBack.onExpandoCell(rc,cellStyle,cellTD,tree,treeNode,treeNodePath);
			cellTD.setAttribute("id",eventElementId);
		}
		cellTD.setAttribute("style",cellStyle.renderInline());
		cellTD.setAttribute("nowrap","nowrap");
	}
	
	/**
	 * Renders the node cell, which can container am optional "folder" icon or text.  It may also
	 * be a component instead of icon/text in which case its up to the component to style
	 * the tree cell.
	 */
	private void renderNodeCell(Object treeNode, TreePath treeNodePath, HtmlTable treeRowTable) {
		boolean isExpanded = tree.isExpanded(treeNodePath);
		boolean isLeaf = treeModel.isLeaf(treeNode);
		boolean isSelected = tree.isPathSelected(treeNodePath);
		
		Element cellTD;
		Label cellLabel = cellRenderer.getTreeCellRendererText(tree, treeNode, isSelected, isExpanded, isLeaf);
		if (cellLabel != null) {
			CssStyle cssStyleText = createStyleFromLabel(cellLabel);
			ImageReference icon = cellLabel.getIcon();
			if (icon != null) {
				// keep a reference to the icon to prevent
				// it being garbage collected.
				imageManager.addImage(icon);
				
				cellTD = treeRowTable.newTD();
				//
				// if we dont include the selection into the icon area, then we need a new style based on a non
				// selected cell label.
				Label iconCellLabel = cellLabel;
				if (! isSelectionIncludesIcon) {
					iconCellLabel = cellRenderer.getTreeCellRendererText(tree, treeNode, false, isExpanded, isLeaf);
				}
				CssStyle cssStyleIcon = createStyleFromLabel(iconCellLabel);
				renderCellImageSetWidths(cssStyleIcon,icon,cellTD);
				
				String eventElementId = eventSupportCallBack.onNodeIconCell(rc,cssStyleIcon,cellTD,tree,treeNode,treeNodePath);
				cellTD.setAttribute("id",eventElementId);
				cellTD.setAttribute("style",cssStyleIcon.renderInline());
			}
			//
			// render the text
			cssStyleText.setAttribute("white-space","pre");
			if (! this.isCellWidthConstrained) {
				cssStyleText.setAttribute("width","100%");
			}
			
			cellTD = treeRowTable.newTD();
			cellTD.setAttribute("nowrap","nowrap");
			String text = (String) cellLabel.getRenderProperty(Label.PROPERTY_TEXT);			
			if (text != null && text.length() > 0) {
		        DomUtil.setElementText(cellTD, text);
			}
			
			String eventElementId = eventSupportCallBack.onNodeTextCell(rc,cssStyleText,cellTD,tree,treeNode,treeNodePath);
			cellTD.setAttribute("id",eventElementId);
			cellTD.setAttribute("style",cssStyleText.renderInline());
		} else {
			//
			// node may have a Component associated with it
			Component cellComponent = tree.getComponent(treeNode);
			if (cellComponent != null) {
				CssStyle cssStyleComponent = new CssStyle();
				cssStyleComponent.setAttribute("text-align","left");
				cssStyleComponent.setAttribute("vertical-align","top");
				cssStyleComponent.setAttribute("white-space","pre");
				if (! this.isCellWidthConstrained) {
					cssStyleComponent.setAttribute("width","100%");
				}
				
				cellTD = treeRowTable.newTD();
	
				String eventElementId = eventSupportCallBack.onNodeIconCell(rc,cssStyleComponent,cellTD,tree,treeNode,treeNodePath);
				cellTD.setAttribute("id",eventElementId);
				cellTD.setAttribute("style",cssStyleComponent.renderInline());
				
		        ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(cellComponent.getClass());
		        if (syncPeer instanceof DomUpdateSupport) {
		            ((DomUpdateSupport) syncPeer).renderHtml(rc, rc.getServerComponentUpdate(), cellTD, cellComponent);
		        } else {
		            syncPeer.renderAdd(rc, rc.getServerComponentUpdate(), eventElementId, cellComponent);
		        }
		        return;
			}
			// 
			// ok its none of these so lets try XhtmlFragment last of all
			XhtmlFragment fragment = cellRenderer.getTreeCellRendererXhtml(tree, treeNode, isSelected, isExpanded, isLeaf);
			if (fragment != null) {
				CssStyle cssStyleFragment = new CssStyle();
				cssStyleFragment.setAttribute("text-align","left");
				cssStyleFragment.setAttribute("vertical-align","top");
				cssStyleFragment.setAttribute("white-space","pre");
				if (! this.isCellWidthConstrained) {
					cssStyleFragment.setAttribute("width","100%");
				}
				
				cellTD = treeRowTable.newTD();
	
				String eventElementId = eventSupportCallBack.onNodeIconCell(rc,cssStyleFragment,cellTD,tree,treeNode,treeNodePath);
				cellTD.setAttribute("id",eventElementId);
				cellTD.setAttribute("style",cssStyleFragment.renderInline());

				// parse and insert the XhtmlFragment if its complex
				if (fragment.isJustText()) {
					String xhtmlText = fragment.getFragment();
					Node textNode = treeRowTable.getOwnerDocument().createTextNode(xhtmlText);
					cellTD.appendChild(textNode);
				} else {
					Node fragments[] = null;
					try {
						fragments = fragment.toDOM(treeRowTable.getOwnerDocument());
					} catch (Exception e) {
						throw new RuntimeException("The XhtmlFragment is not valid XHTML : " + fragment.getFragment(), e);
					}
					for (int i = 0; i < fragments.length; i++) {
						cellTD.appendChild(fragments[i]);
					}
				}
			}
		}
		// if the cell width is to be constrained, then the previous cell widths will NOT be
		// 100% and we need a final cell to be 100%.
		if (this.isCellWidthConstrained) {
			cellTD = treeRowTable.newTD();
			cellTD.setAttribute("style","width:100%");
		}
	}
	
	/**
	 * Called to render the nodes of The Tree recursively.  It must be careful not to "tocuh" any
	 * nodes that are not expanded or are leafs.  This prevents unnecessary model access.
	 */
	private void renderTreeRecursive(Object parentTreeNode, TreePath parentTreePath) {
		//
		// is it the root node, cause that is special
		boolean renderParent = true;
		if (parentTreeNode.equals(treeModel.getRoot())) {
			if (! isRootVisible) {
				renderParent = false;
			}
		}
		if (renderParent) {
			renderTreeRow(parentTreeNode,parentTreePath);
		}
		
		if (! treeModel.isLeaf(parentTreeNode) && tree.isExpanded(parentTreePath)) {
			int cc =  treeModel.getChildCount(parentTreeNode);
			for (int i = 0; i < cc; i++) {
				Object nodeTreeChild = treeModel.getChild(parentTreeNode,i);
				renderTreeRecursive(nodeTreeChild, new TreePath(parentTreePath,nodeTreeChild));
			}
		}
	}
	
	/**
	 * This is called to render a given Tree as a series of tree rows.  
	 */
	public void renderTree() {
		Object root = treeModel.getRoot();
		renderTreeRecursive(root,new TreePath(root));
	}	
	
	/**
	 * This is called to render a given Tree node as tree row.  It returns the Element
	 * containing the renderer tree row.
	 */
	public Element renderTreeRow(Object treeNode, TreePath treeNodePath) {
		boolean isRootPath = getParent(treeNodePath) == null;

		//
		// we use a table as the tree row container
		//
		HtmlTable treeRowTable = new HtmlTable(rc.getDocument(),false,DEBUG_TABLE_BORDER,0,0);
		treeRowTable.newTRnoTD();
		Element cellTD = null;
		
		// for debugging only
		if (DEBUG_PATH_IDS) {
			String pathId = createPathId(treeNodePath,treeModel);
			cellTD = treeRowTable.newTD();
			cellTD.setAttribute("style","background-color:#FFEFD5");
			DomUtil.setElementText(cellTD,pathId);
		}
		//
		// we need to create a stack of cell commands that tell us how to create
		// the tree row spacer cells.
		Stack cellCommandStack  = new Stack();
		createCellCommandsStack(cellCommandStack,treeNodePath);
		
		while (! cellCommandStack.isEmpty()) {
			cellTD = treeRowTable.newTD();
			
			String cellCommand = (String) cellCommandStack.pop();
			if (cellCommand.equals("line")) {
				renderBackgroundLineCell(cellTD);
			} else {
				renderEmptyCell(cellTD);
			}
		}
		//
		// we need to create the expando cell.  if its the root path
		// then we create an expando only if the root handles are showing
		//
		if ((isRootPath && showRootHandles) || ! isRootPath) {
			cellTD = treeRowTable.newTD();
			renderExpandoCell(treeNode,treeNodePath,cellTD);
		}
		//
		//
		// we then need to render the node cell
		renderNodeCell(treeNode,treeNodePath,treeRowTable);


		CssStyle cssTreeStyle = new CssStyle();
		cssTreeStyle.setAttribute("border-collapse","collapse");
		treeRowTable.getTABLE().setAttribute("border",""+DEBUG_TABLE_BORDER);
		treeRowTable.getTABLE().setAttribute("width","100%");
		eventSupportCallBack.onTreeRow(rc,cssTreeStyle, treeRowTable.getTABLE(), tree, treeNodePath);
		
		treeRowTable.getTABLE().setAttribute("style",cssTreeStyle.renderInline());
		return treeRowTable.getTABLE();
	}

	
	/**
	 * Returns an ordered list of TreePaths. Used when rendering to determine deltas.
	 * 
	 * @return ArrayList of TreePaths in the order they would be rendered.
	 */
	protected ArrayList compileVisiblePaths() {
		Object root = treeModel.getRoot();
		ArrayList paths = new ArrayList();
		compileVisiblePaths(paths, root, new TreePath(root));
		return paths;
	}
	
	private void compileVisiblePaths(ArrayList paths, Object node, TreePath path) {
		
		// Root node has a special flag that determines if it is rendered
		boolean renderNode = !(node.equals(treeModel.getRoot()) && !isRootVisible);
		
		if (renderNode) {
			paths.add(path);
		}
		
		if (!treeModel.isLeaf(node) && tree.isExpanded(path)) {
			
			int cc =  treeModel.getChildCount(node);
			for (int i = 0; i < cc; i++) {
				Object childNode = treeModel.getChild(node, i);
				compileVisiblePaths(paths, childNode, new TreePath(path, childNode));
			}
		}
		
	}
}
