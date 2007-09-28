package echopointng;

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
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.WeakHashMap;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import echopointng.tree.DefaultMutableTreeNode;
import echopointng.tree.DefaultTreeCellRenderer;
import echopointng.tree.DefaultTreeIcons;
import echopointng.tree.DefaultTreeModel;
import echopointng.tree.DefaultTreeSelectionModel;
import echopointng.tree.EmptyTreeSelectionModel;
import echopointng.tree.ExpandVetoException;
import echopointng.tree.RowMapper;
import echopointng.tree.TreeActionEventEx;
import echopointng.tree.TreeCellRenderer;
import echopointng.tree.TreeExpansionEvent;
import echopointng.tree.TreeExpansionListener;
import echopointng.tree.TreeIcons;
import echopointng.tree.TreeModel;
import echopointng.tree.TreeModelEvent;
import echopointng.tree.TreeModelListener;
import echopointng.tree.TreeNode;
import echopointng.tree.TreePath;
import echopointng.tree.TreeSelectionEvent;
import echopointng.tree.TreeSelectionListener;
import echopointng.tree.TreeSelectionModel;
import echopointng.tree.TreeWillExpandListener;

/**
 * The <code>Tree</code> component displays data in a hierarchial manner. The
 * API for this Component has based heavily on the Swing JTree class.
 * <p>
 * A <code>Tree</code> only renders the visible nodes and an event is
 * generated to expand or collapse nodes as well as when the user clicks on a
 * node. This allows the application to insert more nodes as necessary or on a
 * "just in time" basis.
 * <p>
 * Tree also supports node selection via its TreeSelectionModel.
 * <p>
 * The <code>Tree</code> component supports "bubbling up" actions.
 * <p>
 * If the objects placed in the Tree implement <code>TreeNode</code> then an
 * action command can be identified with each node. When a node is pressed, the
 * associated action command will be raised with any
 * <code>ActionListeners</code> of the Tree. If no action command is
 * associated with a <code>TreeNode</code> then the path to the ancestors will
 * be followed to look for an action command. If no action command can be found,
 * then the action command of the tree itself will be used.
 * <p>
 * If you call <code>setNullActionCommandsRaiseEvents(false)</code> then
 * action events will not be raised for TreeNodes that have null action
 * commands. It is TRUE by default.
 * <p>
 * 
 * @see echopointng.tree.TreeModel
 * @see echopointng.tree.DefaultTreeModel
 * @see echopointng.tree.TreeCellRenderer
 * @see echopointng.tree.DefaultTreeCellRenderer
 * @see echopointng.tree.TreeNode
 * @see echopointng.tree.MutableTreeNode
 * @see echopointng.tree.DefaultMutableTreeNode
 */
public class Tree extends AbleComponent {

	/**
	 * Used as an INPUT name to indicate that a node is to be toggled, ie
	 * expanded or contracted
	 */
	public static final String INPUT_TOGGLE = "toggle";

	/**
	 * Used as an INPUT name to indicate that a node is to be selected
	 */
	public static final String INPUT_SELECT = "select";

	public final static String PROPERTY_ACTION_COMMAND = "actionCommand";

	public final static String PROPERTY_CELL_RENDERER = "cellRenderer";

	public final static String PROPERTY_LINES_DRAWN = "linesDrawn";

	public final static String PROPERTY_NULL_ACTION_COMMANDS_RAISE_EVENTS = "nullActionCommandsRaiseEvents";

	public final static String PROPERTY_ROOT_VISIBLE = "rootVisible";

	public static final String PROPERTY_ROW_HEIGHT = "rowHeight";

	public final static String PROPERTY_SELECTION_MODEL = "selectionModel";

	public final static String PROPERTY_SHOWS_ROOT_HANDLES = "showsRootHandles";

	public final static String PROPERTY_TREE_ICONS = "treeIcons";

	public final static String PROPERTY_MODEL = "model";

	public final static String PROPERTY_ROOT_AUTO_EXPANDED = "rootAutoExpanded";

	public final static String PROPERTY_PARTIAL_UPDATE_SUPPORT = "partialUpdateSupport";

	public final static String PROPERTY_CELL_WIDTH_CONTRAINED = "cellWidthContrained";

	public final static String PROPERTY_SELECTION_INCLUDES_ICON = "selectionIncludesIcon";

	public final static String PROPERTY_SCROLL_INTO_VIEW_USED = "scrollIntoViewUsed";

	public static final String NODE_CHANGED_PROPERTY = "nodeChanged";

	public static final String MODEL_STRUCTURE_CHANGED_PROPERTY = "modelStructureChanged";

	/**
	 * The <code>TreeExpansionHandler</code> is a private handler for
	 * listening to our own expansion events.
	 */
	private class TreeExpansionHandler implements Serializable, TreeExpansionListener {

		/**
		 * @see echopointng.tree.TreeExpansionListener#treeCollapsed(TreeExpansionEvent)
		 */
		public void treeCollapsed(TreeExpansionEvent e) {
			markPathDirty(e.getPath(), false);
		}

		/**
		 * @see echopointng.tree.TreeExpansionListener#treeExpanded(TreeExpansionEvent)
		 */
		public void treeExpanded(TreeExpansionEvent e) {
			markPathDirty(e.getPath(), false);
			lastExpandedPaths.add(e.getPath());
		}
	}

	/**
	 * Listens to the model and updates the expandedState accordingly when nodes
	 * are removed, or changed.
	 */
	protected class TreeModelHandler implements TreeModelListener, Serializable {

		public void treeNodesChanged(TreeModelEvent e) {
			markPathsDirty(e.getTreePath(), e.getChildren(), false);
		}

		public void treeNodesInserted(TreeModelEvent e) {
			markPathsDirty(e.getTreePath(), e.getChildren(), true);
		}

		public void treeNodesRemoved(TreeModelEvent e) {
			if (e == null) {
				return;
			}

			TreePath parent = e.getTreePath();
			Object[] children = e.getChildren();

			if (children == null) {
				return;
			}

			TreePath rPath;
			Vector toRemove = new Vector(Math.max(1, children.length));

			for (int counter = children.length - 1; counter >= 0; counter--) {
				rPath = parent.pathByAddingChild(children[counter]);
				if (expandedState.get(rPath) != null) {
					toRemove.addElement(rPath);
				}
			}

			if (toRemove.size() > 0) {
				removeDescendantToggledPaths(toRemove.elements());
			}

			TreeModel model = getModel();

			if (model == null || model.isLeaf(parent.getLastPathComponent())) {
				expandedState.remove(parent);
			}
			

			// Don't need to mark the path as dirty - partial render removes all
			// deleted nodes.  An exception is when the parent node
			// has gone from n to 0 children in which case its dirty because its
			// icon must change from -/+ to a straight line
			//
			Object parentNode = parent.getLastPathComponent();
			if (model != null && model.getChildCount(parentNode) == 0) {
				markPathDirty(parent, false);
			}
			invalidate();
			firePropertyChange(NODE_CHANGED_PROPERTY, null, null); 
		}

		public void treeStructureChanged(TreeModelEvent e) {
			if (e == null) {
				return;
			}

			TreePath parent = e.getTreePath();

			if (parent == null) {
				return;
			}

			if (parent.getPathCount() == 1) {
				clearToggledPaths();
				TreeModel model = getModel();
				if (model != null && !model.isLeaf(model.getRoot())) {
					expandedState.put(parent, Boolean.TRUE);
				}
			} else if (expandedState.get(parent) != null) {
				Vector toRemove = new Vector(1);
				boolean isExpanded = isExpanded(parent);

				toRemove.addElement(parent);
				removeDescendantToggledPaths(toRemove.elements());
				if (isExpanded) {
					TreeModel model = getModel();

					if (model == null || model.isLeaf(parent.getLastPathComponent()))
						collapsePath(parent);
					else
						expandedState.put(parent, Boolean.TRUE);
				}
			}

			invalidate();
			firePropertyChange(MODEL_STRUCTURE_CHANGED_PROPERTY, null, null); // Forces
			// full
			// redraw
		}
	}

	/**
	 * Handles the RowMapper interface for the tree
	 * <p>
	 */
	protected class TreeRowMapper implements Serializable, RowMapper {

		/**
		 * @see echopointng.tree.RowMapper#getRowsForPaths(echopointng.tree.TreePath[])
		 */
		public int[] getRowsForPaths(TreePath[] paths) {
			return Tree.this.getRowsForPaths(paths);
		}
	}

	/**
	 * Handles creating a new TreeSelectionEvent with the Tree as the source and
	 * passing it off to all the listeners.
	 * <p>
	 */
	protected class TreeSelectionForwarder implements Serializable, TreeSelectionListener {
		/**
		 * Invoked by the TreeSelectionModel when the selection changes.
		 * 
		 * @param e
		 *            the TreeSelectionEvent generated by the TreeSelectionModel
		 */
		public void valueChanged(TreeSelectionEvent e) {

			// Forces redraw of selected nodes
			TreePath[] paths = e.getPaths();
			for (int i = 0; i < paths.length; i++) {
				markPathDirty(paths[i], false);
			}

			TreeSelectionEvent newE;
			newE = (TreeSelectionEvent) e.cloneWithSource(Tree.this);
			fireValueChanged(newE);
		}
	}

	/* Max number of stacks to keep around. */
	private static int TEMP_STACK_SIZE = 15;

	/* a HashMap of TreeNodes --> Component mappings */
	private transient Map componentMap = new WeakHashMap();

	/* a bit bucket for components */
	// private DevNull devNull;
	/* Used when setExpandedState is invoked, will be a Stack of Stacks. */
	private transient Stack expandedStack;

	/* Maps from TreePath to Boolean indicating whether a path is expanded. */
	private transient Hashtable expandedState;

	/* Records the last tree path that was expanded */
	private transient Collection lastExpandedPaths = new ArrayList();

	/* Internal watch for expansion events */
	private TreeExpansionHandler expansionForwarder;

	/* Creates a new event and passed it off the selectionListeners. */
	private TreeSelectionForwarder selectionForwarder;

	private TreeModelListener treeModelListener;

	/* List of nodes which have changed since the last render */
	private ArrayList dirtyPaths = new ArrayList();

	/* indicates whether the Tree is valid or not */
	private boolean valid;

	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(PROPERTY_ROOT_VISIBLE, true);
		style.setProperty(PROPERTY_LINES_DRAWN, true);
		style.setProperty(PROPERTY_SHOWS_ROOT_HANDLES, true);
		style.setProperty(PROPERTY_SCROLL_INTO_VIEW_USED, true);
		style.setProperty(PROPERTY_NULL_ACTION_COMMANDS_RAISE_EVENTS, true);

		DEFAULT_STYLE = style;
	}

	/**
	 * Returns a <code>Tree</code> with a <code>DefaultMutableTreeNode</code>
	 * as its root, which displays the root node.
	 */
	public Tree() {
		this(new DefaultMutableTreeNode("root"), false);
	}

	/**
	 * Returns a <code>Tree</code> with the specified <code>TreeNode</code>
	 * as its root, which displays the root node.
	 */
	public Tree(TreeNode root) {
		this(root, false);
	}

	/**
	 * Returns a <code>Tree</code> with the specified <code>TreeNode</code>
	 * as its root, which displays the root node and which decides whether a
	 * node is a leaf node in the specified manner.
	 * 
	 */
	public Tree(TreeNode root, boolean asksAllowsChildren) {
		this(new DefaultTreeModel(root, asksAllowsChildren));
	}

	/**
	 * Returns an instance of a <code>Tree</code> which displays the root node
	 * and is created using the specified data model.
	 * 
	 */
	public Tree(TreeModel newModel) {
		super();
		expandedStack = new Stack();
		expandedState = new Hashtable();

		DefaultTreeSelectionModel tempSelectionModel = new DefaultTreeSelectionModel();
		tempSelectionModel.setRowMapper(new TreeRowMapper());
		setSelectionModel(tempSelectionModel);

		expansionForwarder = new TreeExpansionHandler();
		addTreeExpansionListener(expansionForwarder);

		setCellRenderer(new DefaultTreeCellRenderer());
		setTreeIcons(new DefaultTreeIcons());
		setModel(newModel);
	}

	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String name, Object value) {
		super.processInput(name, value);
		if (getModel() == null) {
			return;
		}

		TreePath path = (TreePath) value;

		if (name.startsWith(INPUT_TOGGLE)) {
			// and flip it
			if (isExpanded(path)) {
				collapsePath(path);
			} else {
				expandPath(path);
			}

		} else if (name.startsWith(INPUT_SELECT)) {
			TreeSelectionModel selectionModel = getSelectionModel();
			if (selectionModel != null) {
				if (selectionModel.isPathSelected(path)) {
					selectionModel.removeSelectionPath(path);
				} else {
					selectionModel.addSelectionPath(path);
				}
			}

			Object[] nodes = path.getPath();
			if (nodes.length > 0 && nodes[nodes.length - 1] instanceof TreeNode) {
				Object originalNode = nodes[nodes.length - 1];
				TreeNode node = (TreeNode) originalNode;

				String actionCommand = node.getActionCommand();
				while (actionCommand == null) {
					if (node.getParent() != null) {
						node = node.getParent();
						actionCommand = node.getActionCommand();
					} else {
						break;
					}
				}
				if (actionCommand == null) {
					actionCommand = getActionCommand();
				}
				// TODO - add metakey support
				ActionEvent e = new TreeActionEventEx(this, actionCommand, 0, originalNode);
				fireActionPerformed(e);
			}
		}

		invalidate();
	}

	/**
	 * Adds an <code>ActionListener</code>.
	 * 
	 * @param l
	 *            The <code>ActionListener</code> to be added.
	 */
	public void addActionListener(ActionListener l) {
		getEventListenerList().addListener(ActionListener.class, l);
	}

	/**
	 * Adds the node identified by the specified TreePath to the current
	 * selection. If any component of the path isn't viewable, it is made
	 * viewable.
	 * 
	 * @param path
	 *            the TreePath to add
	 */
	public void addSelectionPath(TreePath path) {
		makeVisible(path);
		getSelectionModel().addSelectionPath(path);
	}

	/**
	 * Adds each path in the array of paths to the current selection. If any
	 * component of any of the paths isn't viewable, it is made viewable.
	 * 
	 * @param paths
	 *            an array of TreePath objects that specifies the nodes to add
	 */
	public void addSelectionPaths(TreePath[] paths) {
		if (paths != null) {
			for (int counter = paths.length - 1; counter >= 0; counter--)
				makeVisible(paths[counter]);
		}
		getSelectionModel().addSelectionPaths(paths);
	}

	/**
	 * Adds a listener for TreeExpansion events.
	 * 
	 * @param tel
	 *            a TreeExpansionListener that will be notified when a tree node
	 *            is expanded or collapsed (a "negative expansion")
	 */
	public void addTreeExpansionListener(TreeExpansionListener tel) {
		getEventListenerList().addListener(TreeExpansionListener.class, tel);
	}

	/**
	 * Adds a listener for TreeSelection events.
	 * 
	 * @param tsl
	 *            the TreeSelectionListener that will be notified when a node is
	 *            selected or deselected (a "negative selection")
	 */
	public void addTreeSelectionListener(TreeSelectionListener tsl) {
		getEventListenerList().addListener(TreeSelectionListener.class, tsl);
		if (getEventListenerList().getListenerCount(TreeSelectionListener.class) != 0 && selectionForwarder == null) {
			selectionForwarder = new TreeSelectionForwarder();
			getSelectionModel().addTreeSelectionListener(selectionForwarder);
		}
	}

	/**
	 * Adds a listener for TreeWillExpand events.
	 * 
	 * @param tel
	 *            a TreeWillExpandListener that will be notified when a tree
	 *            node will be expanded or collapsed (a "negative expansion")
	 */
	public void addTreeWillExpandListener(TreeWillExpandListener tel) {
		getEventListenerList().addListener(TreeWillExpandListener.class, tel);
	}

	/**
	 * Ensures that all the nodes in the tree are collapsed
	 */
	public void collapseAll() {
		TreeModel model = getModel();
		if (model != null) {
			toggleAllNodes(new TreePath(model.getRoot()), false);
		}
	}

	/**
	 * Ensures that the node identified by the specified path is collapsed and
	 * viewable.
	 * 
	 * @param path
	 *            the TreePath identifying a node
	 */
	public void collapsePath(TreePath path) {
		setExpandedState(path, false);
	}

	/**
	 * Ensures that all the nodes are expanded and viewable.
	 */
	public void expandAll() {
		TreeModel model = getModel();
		if (model != null) {
			toggleAllNodes(new TreePath(model.getRoot()), true);
		}
	}

	/**
	 * Ensures that the node identified by the specified path is expanded and
	 * viewable.
	 * 
	 * @param path
	 *            the TreePath identifying a node
	 */
	public void expandPath(TreePath path) {
		TreeModel model = getModel();
		if (path != null && model != null && !model.isLeaf(path.getLastPathComponent())) {
			setExpandedState(path, true);
		}
	}

	/**
	 * Notifies all listeners that have registered for this event type.
	 * 
	 * @param e
	 *            The <code>ActionEvent</code> to send.
	 */
	public void fireActionPerformed(ActionEvent e) {
		EventListener[] listeners = getEventListenerList().getListeners(ActionListener.class);
		for (int index = 0; index < listeners.length; ++index) {
			((ActionListener) listeners[index]).actionPerformed(e);
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param path
	 *            the TreePath indicating the node that was collapsed
	 */
	public void fireTreeCollapsed(TreePath path) {
		Object[] listeners = getEventListenerList().getListeners(TreeExpansionListener.class);
		TreeExpansionEvent e = null;

		for (int index = 0; index < listeners.length; ++index) {
			if (e == null)
				e = new TreeExpansionEvent(this, path);
			((TreeExpansionListener) listeners[index]).treeCollapsed(e);
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param path
	 *            the TreePath indicating the node that was expanded
	 */
	public void fireTreeExpanded(TreePath path) {
		Object[] listeners = getEventListenerList().getListeners(TreeExpansionListener.class);
		TreeExpansionEvent e = null;

		for (int index = 0; index < listeners.length; ++index) {
			if (e == null)
				e = new TreeExpansionEvent(this, path);
			((TreeExpansionListener) listeners[index]).treeExpanded(e);
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param path
	 *            the TreePath indicating the node that was expanded
	 */
	public void fireTreeWillCollapse(TreePath path) throws ExpandVetoException {
		Object[] listeners = getEventListenerList().getListeners(TreeWillExpandListener.class);
		TreeExpansionEvent e = null;

		for (int index = 0; index < listeners.length; ++index) {
			if (e == null)
				e = new TreeExpansionEvent(this, path);
			((TreeWillExpandListener) listeners[index]).treeWillCollapse(e);
		}

	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param path
	 *            the TreePath indicating the node that was expanded
	 */
	public void fireTreeWillExpand(TreePath path) throws ExpandVetoException {
		Object[] listeners = getEventListenerList().getListeners(TreeWillExpandListener.class);
		TreeExpansionEvent e = null;

		for (int index = 0; index < listeners.length; ++index) {
			if (e == null)
				e = new TreeExpansionEvent(this, path);
			((TreeWillExpandListener) listeners[index]).treeWillExpand(e);
		}
	}

	/**
	 * Returns the action command string of the <code>Tree</code>
	 * 
	 * @return java.lang.String
	 */
	public String getActionCommand() {
		return (String) getProperty(PROPERTY_ACTION_COMMAND);
	}

	/**
	 * Returns the current TreeCellRenderer that is rendering each cell.
	 * 
	 * @return the TreeCellRenderer that is rendering each cell
	 * @see echopointng.tree.TreeCellRenderer
	 */
	public TreeCellRenderer getCellRenderer() {
		return (TreeCellRenderer) getProperty(PROPERTY_CELL_RENDERER);
	}

	/**
	 * Returns the Component that will be used to render the provided tree node.
	 * If this method returns null, then no Component has been associated with a
	 * specified tree node and the TreeCellRenderer.getTreeCellRendererText()
	 * method will be used to render the Tree cell.
	 * <p>
	 * This method is primarily for use by the Tree UI peer rendering code. You
	 * should call the "invalidate" and then "validate" methods first before
	 * calling this method to ensure that all child Components are known and are
	 * valid.
	 * 
	 * @param treeNode -
	 *            the tree node in question
	 * @return - a component for the node or null
	 * 
	 * @see TreeCellRenderer#getTreeCellRendererText(Tree, Object, boolean,
	 *      boolean, boolean)
	 */
	public Component getComponent(Object treeNode) {
		WeakReference wr = (WeakReference) componentMap.get(treeNode);
		return wr != null ? (Component) wr.get() : null;
	}

	/**
	 * Returns an Enumeration of the descendants of <code>path</code> that are
	 * currently expanded. If <code>path</code> is not currently expanded,
	 * this will return null. If you expand/collapse nodes while iterating over
	 * the returned Enumeration this may not return all the expanded paths, or
	 * may return paths that are no longer expanded.
	 */
	public Enumeration getExpandedDescendants(TreePath parent) {
		if (!isExpanded(parent))
			return null;

		Enumeration toggledPaths = expandedState.keys();
		Vector elements = new Vector();
		TreePath path;
		Object value;

		if (toggledPaths != null) {
			while (toggledPaths.hasMoreElements()) {
				path = (TreePath) toggledPaths.nextElement();
				value = expandedState.get(path);
				// Add the path if it is expanded, a descendant of parent,
				// and it is visible (all parents expanded).
				if (value != null && ((Boolean) value).booleanValue() && parent.isDescendant(path) && isVisible(path)) {
					elements.addElement(path);
				}
			}
		}
		return elements.elements();
	}

	/**
	 * Returns the last path component in the first node of the current
	 * selection.
	 * 
	 * @return the last Object in the first selected node's TreePath, or null if
	 *         nothing is selected
	 */
	public Object getLastSelectedPathComponent() {
		TreePath selPath = getSelectionModel().getSelectionPath();
		if (selPath != null)
			return selPath.getLastPathComponent();
		return null;
	}

	/**
	 * Returns the path of the last node added to the selection.
	 * 
	 * @return the TreePath of the last node added to the selection.
	 */
	public TreePath getLeadSelectionPath() {
		return getSelectionModel().getLeadSelectionPath();
	}

	/**
	 * Returns the TreeModel that is providing the data.
	 * 
	 * @return the TreeModel that is providing the data
	 */
	public TreeModel getModel() {
		return (TreeModel) getProperty(PROPERTY_MODEL);
	}

	/**
	 * This will return a collection of TresePath that have expanded within the
	 * Tree. This will be automatically cleared each time a Tree is rendered so
	 * dont rely on it.
	 * 
	 * @return a collection of TresePath that have expanded within the Tree.
	 */
	public Collection getLastExpandedPaths() {
		return lastExpandedPaths;
	}

	/**
	 * Returns the row that the TreePath <code>path</code> is being displayed
	 * at. If the TreePath in <code>path</code> is not valid it will be set to
	 * -1.
	 */
	public int getRowForPath(TreePath path) {
		Object[] nodes = null;
		if (getModel() != null) {
			nodes = path.getPath();
		}
		//
		// is it being displayed at all
		if (nodes.length == 0 || !isExpanded(path))
			return -1;

		TreePath rootPath = new TreePath(getModel().getRoot());
		Enumeration enumeration = getDescendantToggledPaths(rootPath);
		Vector pathsVec = orderPathEnumeration(rootPath, enumeration);
		for (int i = 0; i < pathsVec.size(); i++) {
			TreePath testPath = (TreePath) pathsVec.get(i);
			if (testPath.equals(path))
				return i;
		}
		return -1;
	}

	/**
	 * Returns the rows that the TreePath instances in <code>path</code> are
	 * being displayed at. The returned array is an array of the same length as
	 * that passed in, and if one of the TreePaths in <code>path</code> is not
	 * valid its entry in the array should be set to -1.
	 */
	public int[] getRowsForPaths(TreePath[] paths) {
		if (paths == null)
			return null;

		int numPaths = paths.length;
		int[] rows = new int[numPaths];
		for (int counter = 0; counter < numPaths; counter++)
			rows[counter] = getRowForPath(paths[counter]);
		return rows;
	}

	/**
	 * Returns the row height to be used for each row within the Tree. If this
	 * value is 0 or less, then no row height will be used.
	 * 
	 * @return int
	 */
	public int getRowHeight() {
		return getProperty(PROPERTY_ROW_HEIGHT, -1);
	}

	/**
	 * Returns the number of nodes selected.
	 * 
	 * @return the number of nodes selected
	 */
	public int getSelectionCount() {
		return getSelectionModel().getSelectionCount();
	}

	/**
	 * Returns the model for selections. This should always return a non-null
	 * value. If you don't want to allow anything to be selected set the
	 * selection model to null, which forces an empty selection model to be
	 * used.
	 * 
	 */
	public TreeSelectionModel getSelectionModel() {
		return (TreeSelectionModel) getProperty(PROPERTY_SELECTION_MODEL);
	}

	/**
	 * Returns the path to the first selected node.
	 * 
	 * @return the TreePath for the first selected node, or null if nothing is
	 *         currently selected
	 */
	public TreePath getSelectionPath() {
		return getSelectionModel().getSelectionPath();
	}

	/**
	 * Returns the paths of all selected values.
	 * 
	 * @return an array of TreePath objects indicating the selected nodes, or
	 *         null if nothing is currently selected.
	 */
	public TreePath[] getSelectionPaths() {
		return getSelectionModel().getSelectionPaths();
	}

	/**
	 * Returns true if handles for the root nodes are displayed.
	 * 
	 */
	public boolean getShowsRootHandles() {
		return getProperty(PROPERTY_SHOWS_ROOT_HANDLES, true);
	}

	/**
	 * Returns the TreeIcons being used by the Tree
	 * 
	 * @return the TreeIcons being used by the Tree
	 * @see echopointng.tree.TreeIcons
	 */
	public TreeIcons getTreeIcons() {
		return (TreeIcons) getProperty(PROPERTY_TREE_ICONS);
	}

	/**
	 * Returns true if the node identified by the path has ever been expanded.
	 * 
	 * @param path =
	 *            The TreePath in question
	 */
	public boolean hasEverBeenExpanded(TreePath path) {
		return (path != null && expandedState.get(path) != null);
	}

	/**
	 * Marks the Tree as needing to be re-rendered.
	 */
	public void invalidate() {
		valid = false;
		// need this to convince Echo2 that the DOM has changed somehow
		// firePropertyChange(null, null, null);
	}

	/**
	 * Returns true if the value identified by path is currently collapsed, this
	 * will return false if any of the values in path are currently not being
	 * displayed.
	 * 
	 * @param path
	 *            the TreePath to check
	 * @return true if any of the nodes in the node's path are collapsed, false
	 *         if all nodes in the path are expanded
	 */
	public boolean isCollapsed(TreePath path) {
		return !isExpanded(path);
	}

	/**
	 * Returns true if the node identified by the path is currently expanded,
	 * 
	 * @param path
	 *            the TreePath specifying the node to check
	 * @return false if any of the nodes in the node's path are collapsed, true
	 *         if all nodes in the path are expanded
	 */
	public boolean isExpanded(TreePath path) {
		if (path == null)
			return false;

		// Is this node expanded?
		Object value = expandedState.get(path);
		if (value == null || !((Boolean) value).booleanValue())
			return false;

		// It is, make sure its parent is also expanded.
		TreePath parentPath = path.getParentPath();
		if (parentPath != null)
			return isExpanded(parentPath);
		return true;
	}

	/**
	 * Returns the path for the specified row. If <code>row</code> is not
	 * visible, <code>null</code> is returned.
	 * 
	 * @param row
	 *            an integer specifying a row
	 * @return the <code>TreePath</code> to the specified node,
	 *         <code>null</code> if <code>row < 0</code> or
	 *         <code>row >= getRowCount()</code>
	 */
	public TreePath getPathForRow(final int row) {
		if (row < 0) {
			return null;
		}

		// a simply call back implementation that stops at the given row.
		TreePathNavigationListener pathNavigationListener = new TreePathNavigationListener() {
			TreePath targetPath = null;

			public boolean onTreePath(TreePath path, int pathRow) {
				if (pathRow == row) {
					targetPath = path;
					return false;
				}
				return true;
			}

			public TreePath getLastEncounteredPath() {
				return targetPath;
			}
		};
		// call the method with the path listener to find the last encountered
		// path
		findAllVisibleRows(pathNavigationListener);
		return pathNavigationListener.getLastEncounteredPath();
	}

	/**
	 * Returns the number of rows that are currently being displayed in the
	 * Tree.
	 * 
	 * @return the number of rows that are being displayed in the Tree
	 */
	public int getRowCount() {
		int rowCount = 0;
		if (getModel().getRoot() == null) {
			return 0;
		}
		TreePath rootPath = new TreePath(getModel().getRoot());
		if (!isExpanded(rootPath)) {
			return 1;
		}
		rowCount = findAllVisibleRows(null);
		return rowCount;
	}

	/**
	 * <code>TreePathListener</code> is a simply callback interface that is
	 * callde when a TreePath is encountered during a model navigation.
	 */
	private interface TreePathNavigationListener {
		/**
		 * @return false if the navigation is to be stopped at the current
		 *         TreePath.
		 */
		boolean onTreePath(TreePath path, int pathRow);

		/**
		 * @return the TreePath that as last encountered during navigation.
		 */
		TreePath getLastEncounteredPath();
	}

	/**
	 * This will traverse the Tree in a breath first manner and count all the
	 * visible rows. If the pathNavigationListener object is non null then it
	 * will inform it each time it encounters a new TreePath.
	 * <p>
	 * If the pathNavigationListener tells it to stop, then -1 is returned as
	 * the rowCount
	 * 
	 * @param pathNavigationListener -
	 *            an optional callback interface for indicating when a TreePath
	 *            has been encountered.
	 * 
	 * @return a count of all the visible TreePaths or -1 if it is told to stop
	 */
	private int findAllVisibleRows(TreePathNavigationListener pathNavigationListener) {
		TreeModel model = getModel();
		Object rootNode = model.getRoot();
		TreePath rootPath = new TreePath(rootNode);
		int rowCount = 0;
		if (isRootVisible()) {
			rowCount++;
			if (pathNavigationListener != null) {
				if (!pathNavigationListener.onTreePath(rootPath, rowCount - 1)) {
					return -1;
				}
			}
		}
		if (!model.isLeaf(model.getRoot()) && isExpanded(rootPath)) {
			int childRowCount = findAllVisibleRows(rootNode, rootPath, pathNavigationListener, rowCount);
			if (childRowCount == -1) {
				return -1;
			}
			rowCount = childRowCount;
		}
		return rowCount;
	}

	/**
	 * This will navigate down the TreeModel until it is told not to or it finds
	 * all visible nodes. If the pathNavigationListener tells it to stop, then
	 * -1 is returned as the rowCount
	 * 
	 * @see Tree#findAllVisibleRows(TreePathNavigationListener)
	 */
	private int findAllVisibleRows(Object parentNode, TreePath parentPath, TreePathNavigationListener pathNavigationListener, int currentRowCount) {
		TreeModel model = getModel();
		int rowCount = currentRowCount;
		int cc = model.getChildCount(parentNode);
		for (int i = 0; i < cc; i++) {
			Object childNode = model.getChild(parentNode, i);
			TreePath childPath = new TreePath(parentPath, childNode);
			rowCount++;
			if (pathNavigationListener != null) {
				if (!pathNavigationListener.onTreePath(childPath, rowCount - 1)) {
					return -1;
				}
			}
			if (!model.isLeaf(childNode) && isExpanded(childPath)) {
				int childRowCount = findAllVisibleRows(childNode, childPath, pathNavigationListener, rowCount);
				if (childRowCount == -1) {
					return -1;
				}
				rowCount = childRowCount;
			}
		}
		return rowCount;
	}

	/**
	 * Returns true if the node at the specified display row is currently
	 * expanded.
	 * 
	 * @param row
	 *            the row to check, where 0 is the first row in the display
	 * @return true if the node is currently expanded, otherwise false
	 */
	public boolean isExpanded(int row) {
		TreePath path = getPathForRow(row);
		if (path != null) {
			Boolean value = (Boolean) expandedState.get(path);
			return (value != null && value.booleanValue());
		}
		return false;
	}

	/**
	 * Returns true if the node at the specified display row is collapsed.
	 * 
	 * @param row
	 *            the row to check, where 0 is the first row in the display
	 * @return true if the node is currently collapsed, otherwise false
	 */
	public boolean isCollapsed(int row) {
		return !isExpanded(row);
	}

	/**
	 * Ensures that the node in the specified row is expanded and viewable.
	 * <p>
	 * If <code>row</code> is < 0 or >=<code>getRowCount</code> this will
	 * have no effect.
	 * 
	 * @param row
	 *            an integer specifying a display row, where 0 is the first row
	 *            in the display
	 */
	public void expandRow(int row) {
		expandPath(getPathForRow(row));
	}

	/**
	 * Ensures that the node in the specified row is collapsed.
	 * <p>
	 * If <code>row</code> is < 0 or >=<code>getRowCount</code> this will
	 * have no effect.
	 * 
	 * @param row
	 *            an integer specifying a display row, where 0 is the first row
	 *            in the display
	 */
	public void collapseRow(int row) {
		collapsePath(getPathForRow(row));
	}

	/**
	 * Returns true if lines are drawn between tree nodes.
	 * 
	 * @return boolean
	 */
	public boolean isLinesDrawn() {
		return getProperty(PROPERTY_LINES_DRAWN, true);
	}

	/**
	 * @return true if the last expanded node will be scroll into view
	 */
	public boolean isScrollIntoViewUsed() {
		return getProperty(PROPERTY_SCROLL_INTO_VIEW_USED, true);
	}

	/**
	 * Tree has the ability to "scroll" a peer or child into view when a node is
	 * expanded. This provides a more pleasant user experience. If this flag is
	 * ste to true, the Tree will "scroll" the peer of an expanded node into
	 * view and if it has not peer then it will "scroll" the last child of the
	 * expanded node into view.
	 * <p>
	 * If you child node counts are quite high, you may not want this behaviour
	 * and hence you should set it to false.
	 * 
	 * @param newValue
	 *            a new boolean flag value
	 */
	public void setScrollIntoViewUsed(boolean newValue) {
		setProperty(PROPERTY_SCROLL_INTO_VIEW_USED, newValue);
	}

	/**
	 * Returns TRUE of TreeNode's with 'null' action commands will still raise
	 * events by bubbling up the Tree.
	 * 
	 * @return boolean
	 */
	public boolean isNullActionCommandsRaiseEvents() {
		return getProperty(PROPERTY_NULL_ACTION_COMMANDS_RAISE_EVENTS, true);
	}

	/**
	 * Returns true if the item identified by the path is currently selected.
	 * 
	 * @param path
	 *            a TreePath identifying a node
	 * @return true if the node is selected
	 */
	public boolean isPathSelected(TreePath path) {
		return getSelectionModel().isPathSelected(path);
	}

	/**
	 * Returns true if the root node of the tree is displayed.
	 * 
	 */
	public boolean isRootVisible() {
		return getProperty(PROPERTY_ROOT_VISIBLE, true);
	}

	/**
	 * Returns true if the root node of the tree is expanded when the TreeModel
	 * is first set.
	 * 
	 */
	public boolean isRootAutoExpanded() {
		return getProperty(PROPERTY_ROOT_AUTO_EXPANDED, true);
	}

	/**
	 * Returns true if the selection is currently empty.
	 * 
	 * @return true if the selection is currently empty
	 */
	public boolean isSelectionEmpty() {
		return getSelectionModel().isSelectionEmpty();
	}

	/**
	 * Returns true if the Tree cells use as much UI width as possible or false
	 * if the cells try to contrain their UI width.
	 * 
	 * @return true if the Tree cells use as much UI width as possible or false
	 *         if the cells try to contrain their UI width.
	 */
	public boolean isCellWidthConstrained() {
		return getProperty(PROPERTY_CELL_WIDTH_CONTRAINED, false);
	}

	/**
	 * Set to true if the Tree cells use as much UI width as possible or false
	 * if the cells try to contrain their UI width.
	 * 
	 * @param newValue -
	 *            true if the Tree cells use as much UI width as possible or
	 *            false if the cells try to contrain their UI width.
	 */
	public void setCellWidthConstrained(boolean newValue) {
		setProperty(PROPERTY_CELL_WIDTH_CONTRAINED, newValue);
	}

	/**
	 * @return true if the selection of a tree node includes the area around the
	 *         node icon or false if it does not.
	 */
	public boolean isSelectionIncludesIcon() {
		return getProperty(PROPERTY_SELECTION_INCLUDES_ICON, true);
	}

	/**
	 * Set to true if the selection of a tree node includes the area around the
	 * node icon or false if it does not.
	 * 
	 * @param newValue -
	 *            true if the selection of a tree node includes the area around
	 *            the node icon or false if it does not.
	 */
	public void setSelectionIncludesIcon(boolean newValue) {
		setProperty(PROPERTY_SELECTION_INCLUDES_ICON, newValue);
	}

	/**
	 * Returns true if the value identified by path is currently viewable, which
	 * means it is either the root or all of its parents are exapnded ,
	 * Otherwise, this method returns false.
	 * 
	 * @return true if the node is viewable, otherwise false
	 */
	public boolean isVisible(TreePath path) {
		if (path != null) {
			TreePath parentPath = path.getParentPath();
			if (parentPath != null)
				return isExpanded(parentPath);
			return true;
		}
		return false;
	}

	/**
	 * Ensures that the node identified by path is currently viewable.
	 * 
	 * @param path
	 *            the TreePath to make visible
	 */
	public void makeVisible(TreePath path) {
		if (path != null) {
			TreePath parentPath = path.getParentPath();
			if (parentPath != null) {
				expandPath(parentPath);
			}
		}
	}

	/**
	 * Removes an <code>ActionListener</code>.
	 * 
	 * @param l
	 *            The <code>ActionListener</code> to be removed.
	 */
	public void removeActionListener(ActionListener l) {
		getEventListenerList().removeListener(ActionListener.class, l);
	}

	/**
	 * Removes any descendants of the TreePaths in <code>toRemove</code> that
	 * have been expanded.
	 */
	protected void removeDescendantToggledPaths(Enumeration toRemove) {
		if (toRemove != null) {
			while (toRemove.hasMoreElements()) {
				Enumeration descendants = getDescendantToggledPaths((TreePath) toRemove.nextElement());

				if (descendants != null) {
					while (descendants.hasMoreElements()) {
						expandedState.remove(descendants.nextElement());
					}
				}
			}
		}
	}

	/**
	 * Removes the node identified by the specified path from the current
	 * selection.
	 * 
	 * @param path
	 *            the TreePath identifying a node
	 */
	public void removeSelectionPath(TreePath path) {
		this.getSelectionModel().removeSelectionPath(path);
	}

	/**
	 * Removes the nodes identified by the specified paths from the current
	 * selection.
	 * 
	 * @param paths
	 *            an array of TreePath objects that specifies the nodes to
	 *            remove
	 */
	public void removeSelectionPaths(TreePath[] paths) {
		this.getSelectionModel().removeSelectionPaths(paths);
	}

	/**
	 * Removes a listener for TreeExpansion events.
	 * 
	 * @param tel
	 *            the TreeExpansionListener to remove
	 */
	public void removeTreeExpansionListener(TreeExpansionListener tel) {
		getEventListenerList().removeListener(TreeExpansionListener.class, tel);
	}

	/**
	 * Removes a TreeSelection listener.
	 * 
	 * @param tsl
	 *            the TreeSelectionListener to remove
	 */
	public void removeTreeSelectionListener(TreeSelectionListener tsl) {
		getEventListenerList().removeListener(TreeSelectionListener.class, tsl);
		if (getEventListenerList().getListenerCount(TreeSelectionListener.class) == 0 && selectionForwarder != null) {
			getSelectionModel().removeTreeSelectionListener(selectionForwarder);
			selectionForwarder = null;
		}
	}

	/**
	 * Removes a listener for TreeWillExpand events.
	 * 
	 * @param tel
	 *            the TreeWillExpandListener to remove
	 */
	public void removeTreeWillExpandListener(TreeWillExpandListener tel) {
		getEventListenerList().removeListener(TreeWillExpandListener.class, tel);
	}

	/**
	 * Sets the action command string of the Tree. Note that action commands
	 * bubble up from the <code>TreeNode</code>, via their parents to the
	 * Tree itself.
	 * 
	 * @param newActionCommand
	 *            String
	 */
	public void setActionCommand(String newActionCommand) {
		setProperty(PROPERTY_ACTION_COMMAND, newActionCommand);
	}

	/**
	 * Sets the TreeCellRenderer that will be used to draw each cell.
	 * <p>
	 * NOTE : Some TreeCellRender implementations such as
	 * DefaultTreeeCellRenderer are implemented as a Component. While this is
	 * perfectly valid, please note that the implementation is not added a a
	 * child of Tree and hence inheritied styles from the component hierarchy do
	 * not apply. Not adding the implementation as a child component allows for
	 * "static" implementations of TreeCellRender that are derived from
	 * Component.
	 * 
	 * @param newCellRenderer
	 *            the TreeCellRenderer that is to render each cell
	 * @see echopointng.tree.TreeCellRenderer
	 */
	public void setCellRenderer(TreeCellRenderer newCellRenderer) {
		if (newCellRenderer == null) {
			throw new IllegalArgumentException("The TreeCellRenderer must not be null");
		}
		if (!newCellRenderer.equals(getCellRenderer())) {
			setProperty(PROPERTY_CELL_RENDERER, newCellRenderer);
			componentMap.clear(); // must clear this as new Components could
			// be generated
			invalidate();
		}
	}

	/**
	 * @see nextapp.echo2.app.Component#setEnabled(boolean)
	 */
	public void setEnabled(boolean newValue) {
		if (newValue != isEnabled()) {
			super.setEnabled(newValue);
			invalidate();
		}
	}

	/**
	 * Sets whether lines are drawn between tree nodes
	 * 
	 * @param newDrawLines
	 *            boolean
	 */
	public void setLinesDrawn(boolean newDrawLines) {
		setProperty(PROPERTY_LINES_DRAWN, Boolean.valueOf(newDrawLines));
	}

	/**
	 * If this is set to true, the Tree rendering code is given a hint on
	 * whether it can perform partial updates to speed up visual performance. By
	 * default this is true. Partial update support costs more in terms of
	 * server memory because the old state must be tracked. But it can result in
	 * better visual performance.
	 * 
	 * @param newValue -
	 *            a true value cause partial updates to be used by the rendering
	 */
	public void setPartialUpdateSupport(boolean newValue) {
		setProperty(PROPERTY_PARTIAL_UPDATE_SUPPORT, Boolean.valueOf(newValue));
	}

	/**
	 * Returns true if the partial update support is currently enabled.
	 * <p>
	 * If this is set to true, the Tree rendering code is given a hint on
	 * whether it can perform partial updates to speed up visual performance. By
	 * default this is true. Partial update support costs more in terms of
	 * server memory because the old state must be tracked. But it can result in
	 * better visual performance.
	 * 
	 * @return true if the partial update support is currently enabled.
	 */
	public boolean getPartialUpdateSupport() {
		return getProperty(PROPERTY_PARTIAL_UPDATE_SUPPORT, true);
	}

	/**
	 * Sets the TreeModel that will provide the data.
	 * 
	 * @param newTreeModel -
	 *            the TreeModel that is to provide the data
	 */
	public void setModel(TreeModel newTreeModel) {
		TreeModel oldModel = getModel();

		if (newTreeModel == null || !newTreeModel.equals(oldModel)) {

			if (oldModel != null && treeModelListener != null) {
				oldModel.removeTreeModelListener(treeModelListener);
			}

			setProperty(PROPERTY_MODEL, newTreeModel);
			clearToggledPaths();

			if (newTreeModel != null) {
				if (treeModelListener == null) {
					treeModelListener = createTreeModelListener();
				}
				if (treeModelListener != null) {
					newTreeModel.addTreeModelListener(treeModelListener);
				}
				if (newTreeModel.getRoot() != null) {
					if (isRootAutoExpanded() && !newTreeModel.isLeaf(newTreeModel.getRoot())) {
						expandedState.put(new TreePath(newTreeModel.getRoot()), Boolean.TRUE);
					}
				}
			}

			invalidate();
		}
	}

	/**
	 * Sets whether TreeNode null action commands will cause an event to be
	 * raised.
	 * 
	 * @param newValue
	 *            boolean
	 */
	public void setNullActionCommandsRaiseEvents(boolean newValue) {
		setProperty(PROPERTY_NULL_ACTION_COMMANDS_RAISE_EVENTS, newValue);
	}

	/**
	 * Determines whether or not the root node from the TreeModel is visible.
	 * 
	 */
	public void setRootVisible(boolean rootVisible) {
		if (rootVisible != isRootVisible()) {
			setProperty(PROPERTY_ROOT_VISIBLE, rootVisible);
			invalidate();
		}
	}

	/**
	 * Determines whether or not the root node from the TreeModel is
	 * automatically expanded when the model is set into the Tree.
	 * <p>
	 * By default it is and hence the 2nd child level of the TreeModel will be
	 * accessed and shown.
	 * <p>
	 * If this is false, then only the root node will be accessed and displayed
	 * when a TreeModel is first set and the Tree is rendered.
	 * 
	 */
	public void setRootAutoExpanded(boolean rootExpanded) {
		if (rootExpanded != isRootAutoExpanded()) {
			setProperty(PROPERTY_ROOT_AUTO_EXPANDED, rootExpanded);
			invalidate();
		}
	}

	/**
	 * Sets the row height to be used for each row within the Tree. If this
	 * value is 0 or less, then no row height will be used.
	 * 
	 * @param newRowHeight
	 *            int
	 */
	public void setRowHeight(int newRowHeight) {
		setProperty(PROPERTY_ROW_HEIGHT, newRowHeight);
	}

	/**
	 * Sets the tree's selection model. When a null value is specified an empty
	 * electionModel is used, which does not allow selections.
	 * 
	 */
	public void setSelectionModel(TreeSelectionModel newSelectionModel) {
		if (newSelectionModel == null) {
			newSelectionModel = EmptyTreeSelectionModel.getInstance();
		}

		TreeSelectionModel oldValue = getSelectionModel();

		if (!newSelectionModel.equals(oldValue)) {
			if (oldValue != null && selectionForwarder != null) {
				oldValue.removeTreeSelectionListener(selectionForwarder);
			}

			setProperty(PROPERTY_SELECTION_MODEL, newSelectionModel);

			if (selectionForwarder == null) {
				selectionForwarder = new TreeSelectionForwarder();
			}
			newSelectionModel.addTreeSelectionListener(selectionForwarder);

			invalidate();
		}
	}

	/**
	 * Selects the node identified by the specified path. If any component of
	 * the path is hidden (under a collapsed node), it is exposed (made
	 * viewable).
	 * 
	 * @param path
	 *            the TreePath specifying the node to select
	 */
	public void setSelectionPath(TreePath path) {
		makeVisible(path);
		getSelectionModel().setSelectionPath(path);
	}

	/**
	 * Selects the nodes identified by the specified array of paths. If any
	 * component in any of the paths is hidden (under a collapsed node), it is
	 * exposed (made viewable).
	 * 
	 * @param paths
	 *            an array of TreePath objects that specifies the nodes to
	 *            select
	 */
	public void setSelectionPaths(TreePath[] paths) {
		if (paths != null) {
			for (int counter = paths.length - 1; counter >= 0; counter--)
				makeVisible(paths[counter]);
		}
		getSelectionModel().setSelectionPaths(paths);
	}

	/**
	 * Determines whether the node handles are to be displayed.
	 * 
	 */
	public void setShowsRootHandles(boolean newValue) {
		setProperty(PROPERTY_SHOWS_ROOT_HANDLES, newValue);
		TreeModel model = getModel();
		if (model != null) {
			if (model.getRoot() != null) {
				expandPath(new TreePath(model.getRoot()));
			}
		}
	}

	/**
	 * Sets the TreeIcons being used by the Tree
	 * 
	 * @param newTreeIcons -
	 *            the new icons to use
	 * @see TreeIcons
	 */
	public void setTreeIcons(TreeIcons newTreeIcons) {
		if (newTreeIcons == null) {
			throw new IllegalArgumentException("Non null TreeIcons required");
		}
		if (!newTreeIcons.equals(getTreeIcons())) {
			setProperty(PROPERTY_TREE_ICONS, newTreeIcons);
			invalidate();
		}
	}

	/**
	 * @see nextapp.echo2.app.Component#setVisible(boolean)
	 */
	public void setVisible(boolean newValue) {
		if (newValue != isVisible()) {
			super.setVisible(newValue);
			invalidate();
		}
	}

	/**
	 * Toggles all nodes as expanded or collapsed depending on <i>expand </i>
	 * starting from <i>parentPath</i>.
	 */
	public void toggleAllNodes(TreePath parentPath, boolean expand) {
		Object parent = parentPath.getLastPathComponent();
		toggleAllNodesImpl(parent, parentPath, expand);
	}

	/*
	 * And implementation that keeps track of the TreePaths so we dont have to
	 * call getPathToRoot() on the model.
	 */
	private void toggleAllNodesImpl(Object parent, TreePath parentPath, boolean expand) {

		TreeModel model = getModel();
		if (model == null) {
			return;
		}

		if (!model.isLeaf(parent)) {
			int cc = model.getChildCount(parent);

			for (int i = 0; i < cc; i++) {
				Object node = model.getChild(parent, i);
				TreePath childPath = new TreePath(parentPath, node);

				if (!model.isLeaf(node)) {
					toggleAllNodesImpl(node, childPath, expand);
				} else {
					// must be a tip of the trunk so expand/collapse it
					setExpandedState(childPath, expand);
				}
			}

			// If we are collapsing all then we need to collapse each branch
			// node rather than just the leaves
			if (!expand) {
				setExpandedState(parentPath, false);
			}
		}
	}

	/**
	 * Creates and returns an instance of TreeModelListener. The returned object
	 * is responsible for updating the expanded state when the TreeModel
	 * changes.
	 */
	protected TreeModelListener createTreeModelListener() {
		return new TreeModelHandler();
	}

	/**
	 * Clears the cache of toggled tree paths. This does NOT send out any
	 * TreeExpansionListener events.
	 */
	protected void clearToggledPaths() {
		if (expandedState != null)
			expandedState.clear();
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type.
	 * 
	 * @param e
	 *            the TreeSelectionEvent generated by the TreeSelectionModel
	 *            when a node is selected or deselected
	 */
	protected void fireValueChanged(TreeSelectionEvent e) {
		Object[] listeners = getEventListenerList().getListeners(TreeSelectionListener.class);
		for (int i = 0; i < listeners.length; i++) {
			((TreeSelectionListener) listeners[i]).valueChanged(e);
		}
	}

	/**
	 * Returns an Enumeration of TreePaths that have been expanded that are
	 * descendants of <code>parent</code>.
	 */
	protected Enumeration getDescendantToggledPaths(TreePath parent) {
		if (parent == null)
			return null;

		Vector descendants = new Vector();
		Enumeration nodes = expandedState.keys();
		TreePath path;

		while (nodes.hasMoreElements()) {
			path = (TreePath) nodes.nextElement();
			if (parent.isDescendant(path))
				descendants.addElement(path);
		}
		return descendants.elements();
	}

	/**
	 * Sets the expanded state of the path. If <code>state</code> is true, all
	 * parents of <code>path</code> as well as <code>path</code> are marked
	 * as expanded.
	 * <p>
	 * If <code>state</code> is false, all parents of <code>path</code> are
	 * marked EXPANDED, but <code>path</code> itself is marked collapsed.
	 * <p>
	 * This will fail if a TreeWillExpandListener vetos it.
	 */
	protected void setExpandedState(TreePath path, boolean state) {
		if (path != null) {
			// Make sure all parents of path are expanded.
			Stack stack;
			TreePath parentPath = path.getParentPath();
			if (expandedStack.size() == 0) {
				stack = new Stack();
			} else {
				stack = (Stack) expandedStack.pop();
			}
			try {
				while (parentPath != null) {
					if (isExpanded(parentPath)) {
						parentPath = null;
					} else {
						stack.push(parentPath);
						parentPath = parentPath.getParentPath();
					}
				}
				for (int counter = stack.size() - 1; counter >= 0; counter--) {
					parentPath = (TreePath) stack.pop();
					if (!isExpanded(parentPath)) {
						try {
							fireTreeWillExpand(parentPath);
						} catch (ExpandVetoException eve) {
							return;
						}
						expandedState.put(parentPath, Boolean.TRUE);
						fireTreeExpanded(parentPath);
					}
				}
			} finally {
				if (expandedStack.size() < TEMP_STACK_SIZE) {
					stack.removeAllElements();
					expandedStack.push(stack);
				}
			}
			if (!state) {
				// collapse last path.
				Object cValue = expandedState.get(path);
				if (cValue != null && ((Boolean) cValue).booleanValue()) {
					try {
						fireTreeWillCollapse(path);
					} catch (ExpandVetoException eve) {
						return;
					}
					expandedState.put(path, Boolean.FALSE);
					fireTreeCollapsed(path);
				}
			} else {
				// Expand last path.
				Object cValue = expandedState.get(path);

				if (cValue == null || !((Boolean) cValue).booleanValue()) {
					try {
						fireTreeWillExpand(path);
					} catch (ExpandVetoException eve) {
						return;
					}
					expandedState.put(path, Boolean.TRUE);
					fireTreeExpanded(path);
				}
			}
		}
	}

	/**
	 * Returns true if the hash table contains the nominated path or equivalent.
	 */
	private boolean orderContainsTreePath(Hashtable hash, TreePath path) {
		for (Enumeration enumeration = hash.elements(); enumeration.hasMoreElements();) {
			TreePath testPath = (TreePath) enumeration.nextElement();
			if (testPath.equals(path))
				return true;
		}
		return false;
	}

	/**
	 * Orders an enumeration of paths for use within the RowMapper. It makes a
	 * breadth first descent of the tree and finds each node in the Enumeration
	 * and its it in an Vector, ordered from the top down starting from parent
	 * path.
	 */
	private Vector orderPathEnumeration(TreePath parentPath, Enumeration enumeration) {

		Hashtable unsortedSet = new Hashtable();
		Vector sortedSet = new Vector();
		if (getModel() == null)
			return sortedSet;

		for (; enumeration.hasMoreElements();) {
			TreePath path = (TreePath) enumeration.nextElement();
			unsortedSet.put(path.toString(), path);
		}

		// down throught the tree please
		if (orderContainsTreePath(unsortedSet, parentPath))
			sortedSet.add(parentPath);
		orderPathSet(parentPath, unsortedSet, sortedSet);

		return sortedSet;
	}

	/**
	 * Recursive function to travel down the tree and find tree paths within the
	 * unsorted set and put them in breathfirst order with the sorted set.
	 */
	private void orderPathSet(TreePath parentPath, Hashtable unsortedSet, Vector sortedSet) {

		// now run down the whole tree, starting at parent, and
		// add any elements we find as we find them.
		Object node = parentPath.getLastPathComponent();
		if (!getModel().isLeaf(node) && isExpanded(parentPath)) {
			int cc = getModel().getChildCount(node);
			for (int i = 0; i < cc; i++) {
				Object child = getModel().getChild(node, i);

				TreePath targetPath = parentPath.pathByAddingChild(child);
				if (orderContainsTreePath(unsortedSet, targetPath))
					sortedSet.add(targetPath);
				if (!getModel().isLeaf(child)) {
					// recurse
					orderPathSet(targetPath, unsortedSet, sortedSet);
				}
			}
		}
	}

	/**
	 * Checks the given node to see if it contains a Component and adds it as a
	 * child of the Tree. If the cell renderer returns text, then the component
	 * call will not be made. This ensures that the TreeCellrenderer contract is
	 * kept.
	 */
	private void doComponentCheck(TreePath path, TreeModel model, Map referenceMap) {
		Object node = path.getLastPathComponent();

		// Do not get a new component every time if it has already been created,
		// unless the path is dirty
		Component cellComponent = getComponent(node);
		if (cellComponent != null) {

			if (dirtyPaths.contains(path)) {
				// Remove Component if being used to render this node
				componentMap.remove(node);
				remove(cellComponent);

			} else {
				referenceMap.put(cellComponent, cellComponent);
				return;
			}
		}

		boolean isExpanded = isExpanded(path);
		boolean isSelected = isPathSelected(path);
		boolean isLeaf = model.isLeaf(node);

		Label l = getCellRenderer().getTreeCellRendererText(this, node, isSelected, isExpanded, isLeaf);
		if (l == null) {
			cellComponent = getCellRenderer().getTreeCellRendererComponent(this, node, isSelected, isExpanded, isLeaf);
			if (cellComponent != null) {
				if (!this.isAncestorOf(cellComponent)) {
					this.add(cellComponent);
				}

				componentMap.put(node, new WeakReference(cellComponent));
				if (referenceMap != null) {
					referenceMap.put(cellComponent, cellComponent);
				}
			}
		}
	}

	/**
	 * Recursive call to validate a TreePath. All reference components are
	 * placed in the componentMap key by the Tree node and all referenced
	 * components are also placed in the referenceMap keyed by the component.
	 * 
	 * The referenceMap may be null in which case we dont use it.
	 */
	private void doPathValidation(TreePath path, Map referenceMap) {
		if (path == null)
			return;

		TreeModel model = getModel();
		//
		// check if the node has a component that needs to placed in the
		// hierarchy
		//
		doComponentCheck(path, model, referenceMap);
		//
		// find out if the current node is NOT a leaf and hence
		// we can go down to it.
		boolean isExpanded = isExpanded(path);
		Object node = path.getLastPathComponent();
		if (!model.isLeaf(node) && isExpanded) {
			int cc = model.getChildCount(node);
			for (int i = 0; i < cc; i++) {
				Object nodeChild = model.getChild(node, i);
				TreePath childPath = new TreePath(path, nodeChild);
				doComponentCheck(childPath, model, referenceMap);
				//
				// if we have a server side tree, then we dont want to invoke
				// the cell
				// renderer any more than we have to. Therefore we only go down
				// the
				// tree path if the path is visible and hence child components
				// might be
				// needed. We alway descend for a client side tree
				//
				isExpanded = isExpanded(childPath);
				if (isExpanded) {
					doPathValidation(childPath, referenceMap);
				}
			}
		}
	}

	/**
	 * Called just before the tree is rendered. We run through the tree nodes
	 * and try and find any Components that may be rendered by the tree. These
	 * will be the Components that are returned by the
	 * TreeCellRenderer#getTreeCellRendererComponent(..) method.
	 * <p>
	 * We then add them as children of the tree (if they are not already
	 * ancestors) so that a component rendering peer is created properly for
	 * each cell.
	 * 
	 */
	public void validate() {
		super.validate();
		if (!valid) {
			valid = true;

			TreeModel model = getModel();
			if (model == null) {
				return;
			}

			Object root = model.getRoot();
			if (root == null) {
				return;
			}

			TreeCellRenderer renderer = getCellRenderer();
			if (renderer == null) {
				throw new IllegalStateException("The Tree has no TreeCellRenderer");
			}

			HashMap referenceMap = new HashMap();

			//
			// validate the tree path
			//
			TreePath path = new TreePath(root);
			doPathValidation(path, referenceMap);

			//
			// if we have child components that are no longer referenced in the
			// model then we need to get rid of them. We use the referenceMap so
			// that can find components keyed by themselves.
			//
			// componentMap contains WeakReferences to components, keyed by
			// Nodes.
			// If the components are no longer in the referenceMap, remove them
			// from the componentMap
			Iterator iter = componentMap.values().iterator();
			while (iter.hasNext()) {
				WeakReference wr = (WeakReference) iter.next();
				Component component = (Component) wr.get();
				if (!referenceMap.containsKey(component)) {
					iter.remove();
					this.remove(component);
				}
			}

		}
	}

	/**
	 * @return the collection of pending changed paths (needed for partial
	 *         updates when rendering)
	 */
	public Collection getDirtyPaths() {
		return dirtyPaths;
	}

	/**
	 * Marks an array of nodes as being dirty - ie requiring re-rendering.
	 * Should be called whenever the state of a node is changed or a node is
	 * moved within the tree.
	 * 
	 * @param parentPath -
	 *            current path to the parent of the nodes
	 * @param nodes -
	 *            array of nodes to be marked dirty
	 * @param markChildren -
	 *            whether to mark child nodes as dirty too (true when moving a
	 *            node)
	 */
	private void markPathsDirty(TreePath parentPath, Object[] nodes, boolean markChildren) {

		if (nodes != null) {
			for (int i = nodes.length - 1; i >= 0; i--) {
				TreePath path = parentPath.pathByAddingChild(nodes[i]);
				markPathDirty(path, markChildren);
			}
		} else {
			markPathDirty(parentPath, markChildren);
		}
	}

	/**
	 * Registers a node as being dirty - ie requiring re-rendering. Should be
	 * called whenever the state of a node is changed or a node is moved within
	 * the tree.
	 * 
	 * @param path -
	 *            current path to the node
	 * @param markChildren -
	 *            whether to mark child nodes as dirty too (true when moving a
	 *            node)
	 */
	private void markPathDirty(TreePath path, boolean markChildren) {

		invalidate();

		if (!dirtyPaths.contains(path)) {
			dirtyPaths.add(path);
			firePropertyChange(NODE_CHANGED_PROPERTY, null, null); // Forces
			// partial
			// redraw
		}

		Object node = path.getLastPathComponent();

		// Recursively mark child paths as dirty if requested
		TreeModel model = getModel();
		if (markChildren && !model.isLeaf(node)) { // && isExpanded(path)

			int cc = model.getChildCount(node);
			for (int i = 0; i < cc; i++) {
				Object nodeChild = model.getChild(node, i);
				markPathDirty(new TreePath(path, nodeChild), true);
			}
		}

	}

}
