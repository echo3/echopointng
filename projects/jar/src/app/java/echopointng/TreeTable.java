package echopointng;

/*
 * This file is part of the Echo Point Project. This project is a collection of
 * Components that have extended the Echo Web Application Framework.
 * 
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in which
 * case the provisions of the GPL or the LGPL are applicable instead of those
 * above. If you wish to allow use of your version of this file only under the
 * terms of either the GPL or the LGPL, and not to allow others to use your
 * version of this file under the terms of the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and other
 * provisions required by the GPL or the LGPL. If you do not delete the
 * provisions above, a recipient may use your version of this file under the
 * terms of any one of the MPL, the GPL or the LGPL.
 */

/*
 * The design paradigm, class name and some code used within have been taken
 * from the java.net JDNC project. This project is licensed under the LGPL and
 * is Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 * 
 * This file was made part of the EchoPoint project on the 15/07/2005.
 *  
 */
import java.util.Enumeration;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.Table;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.list.DefaultListSelectionModel;
import nextapp.echo2.app.list.ListSelectionModel;
import nextapp.echo2.app.table.AbstractTableModel;
import nextapp.echo2.app.table.TableCellRenderer;
import nextapp.echo2.app.table.TableModel;
import echopointng.tree.DefaultTreeCellRenderer;
import echopointng.tree.DefaultTreeSelectionModel;
import echopointng.tree.TreeCellRenderer;
import echopointng.tree.TreeExpansionEvent;
import echopointng.tree.TreeExpansionListener;
import echopointng.tree.TreeModelEvent;
import echopointng.tree.TreeModelListener;
import echopointng.tree.TreePath;
import echopointng.tree.TreeSelectionModel;
import echopointng.treetable.AbstractTreeTableModel;
import echopointng.treetable.DefaultTreeTableModel;
import echopointng.treetable.TreeTableModel;

/**
 * <p>
 * <code>TreeTable</code> is a specialized
 * {@link nextapp.echo2.app.Table table} consisting of a single column in which
 * to display hierarchical data, and any number of other columns in which to
 * display regular data.
 * </p>
 * <p>
 * The interface for the data model used by a <code>TreeTable</code> is
 * {@link echopointng.treetable.TreeTableModel}. It extends the
 * {@link echopointng.tree.TreeModel}interface to allow access to cell data by
 * column indices within each node of the tree hierarchy.
 * </p>
 * <p>
 * The most straightforward way create and use a <code>TreeTable</code>, is
 * to first create a suitable data model for it, and pass that to a
 * <code>TreeTable</code> constructor
 * </p>
 * 
 * <p>
 * A single treetable model instance may be shared among more than one
 * <code>TreeTable</code> instances. To access the treetable model, always
 * call {@link #getTreeTableModel() getTreeTableModel}and
 * {@link #setTreeTableModel(TreeTableModel) setTreeTableModel}.
 * <code>TreeTable</code> wraps the supplied treetable model inside a private
 * adapter class to adapt it to a {@link nextapp.echo2.app.table.TableModel}.
 * Although the model adapter is accessible through the
 * {@link #getModel() getModel}method, you should avoid accessing and
 * manipulating it in any way. In particular, each model adapter instance is
 * tightly bound to a single table instance, and any attempt to share it with
 * another table (for example, by calling {@link #setModel(TableModel) setModel})
 * will throw an <code>IllegalArgumentException</code>!
 * 
 * @author Philip Milne
 * @author Scott Violet
 * @author Ramesh Gupta
 */
public class TreeTable extends TableEx {
	/**
	 * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel to
	 * listen for changes in the ListSelectionModel it maintains. Once a change
	 * in the ListSelectionModel happens, the paths are updated in the
	 * DefaultTreeSelectionModel.
	 */
	class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel implements ChangeListener {
		ChangeListener listChangeListener;

		ListSelectionModel tableSelectionModel;

		/** Set to true when we are updating the ListSelectionModel. */
		boolean updatingListSelectionModel;

		public ListToTreeSelectionModelWrapper() {
			super();
			tableSelectionModel = new DefaultListSelectionModel();
			tableSelectionModel.addChangeListener(this);
		}

		ListSelectionModel getTableSelectionModel() {
			return tableSelectionModel;
		}

		/**
		 * This is overridden to set <code>updatingListSelectionModel</code>
		 * and message super. This is the only place DefaultTreeSelectionModel
		 * alters the ListSelectionModel.
		 */
		public void resetRowSelection() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					super.resetRowSelection();
				} finally {
					updatingListSelectionModel = false;
				}
			}
			// Notice how we don't message super if
			// updatingListSelectionModel is true. If
			// updatingListSelectionModel is true, it implies the
			// ListSelectionModel has already been updated and the
			// paths are the only thing that needs to be updated.
		}

		/**
		 * @see nextapp.echo2.app.event.ChangeListener#stateChanged(nextapp.echo2.app.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			updateSelectedPathsFromSelectedRows();
		}

		/**
		 * If <code>updatingListSelectionModel</code> is false, this will
		 * reset the selected paths from the selected rows in the list selection
		 * model.
		 */
		protected void updateSelectedPathsFromSelectedRows() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					// This is way expensive, ListSelectionModel needs an
					// enumerator for iterating.
					int min = tableSelectionModel.getMinSelectedIndex();
					int max = tableSelectionModel.getMaxSelectedIndex();

					clearSelection();
					if (min != -1 && max != -1) {
						for (int counter = min; counter <= max; counter++) {
							if (tableSelectionModel.isSelectedIndex(counter)) {
								TreePath selPath = treeAndTableCellRenderer.getPathForRow(counter);

								if (selPath != null) {
									addSelectionPath(selPath);
								}
							}
						}
					}
				} finally {
					updatingListSelectionModel = false;
				}
			}
		}

	}

	/**
	 * <code>TreeAndTableCellRenderer</code> is intermediate class of Tree
	 * that allows most of the Tree worked required without rewriting huge
	 * chunks for code.
	 * 
	 */
	static class TreeAndTableCellRenderer extends Tree implements TableCellRenderer {

		static final Style DEFAULT_STYLE;

		static {
			MutableStyleEx style = new MutableStyleEx(Tree.DEFAULT_STYLE);
			style.setProperty(PROPERTY_ROOT_VISIBLE, false);
			style.setProperty(PROPERTY_SHOWS_ROOT_HANDLES, true);

			DEFAULT_STYLE = style;
		}

		private TreeTable treeTable;

		// Force user to specify TreeTableModel instead of more general
		// TreeModel
		public TreeAndTableCellRenderer(TreeTableModel model) {
			super(model);
			// done via default fallback style
			// setRootVisible(false); // superclass default is "true"
			// setShowsRootHandles(true); // superclass default is "false"
			setCellRenderer(new DefaultTreeCellRenderer());
		}

		/**
		 * Immutably binds this TreeTableModelAdapter to the specified
		 * TreeTable. For internal use by TreeTable only.
		 * 
		 * @param treeTable
		 *            the TreeTable instance that this treeAndTableCellRenderer
		 *            is bound to
		 */
		public final void bind(TreeTable treeTable) {
			// Suppress potentially subversive invocation!
			// Prevent clearing out the deck for possible hijack attempt later!
			if (treeTable == null) {
				throw new IllegalArgumentException("TreeTable must not be null");
			}

			if (this.treeTable == null) {
				this.treeTable = treeTable;
			} else {
				throw new IllegalArgumentException("TreeAndTableCellRenderer already bound");
			}
		}

		/**
		 * @see nextapp.echo2.app.table.TableCellRenderer#getTableCellRendererComponent(nextapp.echo2.app.Table,
		 *      java.lang.Object, int, int)
		 */
		public Component getTableCellRendererComponent(Table table, Object value, int column, int row) {
			TreeTableModelAdapter tableModelAdapter = treeTable.getTreeTableModelAdapter();
			Object treeNode = tableModelAdapter.nodeForRow(row);
			TreePath treeNodePath = tableModelAdapter.pathForRow(row);
			String displayValue = (value == null ? null : value.toString());

			TreeTableCell tableCell = new TreeTableCell(treeTable, this, treeNode, treeNodePath, displayValue);
			// TableLayoutData layOutData = new TableLayoutData();
			// layOutData.setAlignment(new Alignment(Alignment.LEFT,
			// Alignment.TOP));
			// tableCell.setLayoutData(layOutData);
			return tableCell;
		}
	}

	/**
	 * code>TreeTableCell</code> is used to provide a component into the Table
	 * cell that can draw a TreePath row. This way the standard Table rendering
	 * can be used but the Tree code can "drawn" a Tree like row cell as a
	 * component.
	 * <p>
	 * Because this class is not made public, it properties are access by its
	 * rendering peer via property strings.
	 */
	static class TreeTableCell extends Component {

		TreeTable treeTable;

		/**
		 * Constructs a <code>TreeTable.TreeTableCell</code>
		 */
		TreeTableCell(TreeTable treeTable, Tree tree, Object treeNode, TreePath treeNodePath, String displayValue) {
			super();
			this.treeTable = treeTable;
			setProperty("treeTable", treeTable);
			setProperty("tree", tree);
			setProperty("treeNode", treeNode);
			setProperty("treeNodePath", treeNodePath);
			setProperty("displayValue", displayValue);
		}

		/**
		 * We forward the processinput directly on to TreeTable because it is
		 * the one who is interested in toggles and selection
		 * 
		 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
		 *      java.lang.Object)
		 */
		public void processInput(String inputName, Object inputValue) {
			treeTable.processInput(inputName, inputValue);
		}
	}

	static class TreeTableModelAdapter extends AbstractTableModel {

		private TreeTableModel model; // immutable

		private final Tree tree; // immutable

		private TreeTable treeTable = null; // logically immutable

		/**
		 * Maintains a TreeTableModel and a Tree as purely implementation
		 * details. Developers can plug in any type of custom TreeTableModel
		 * through a TreeTable constructor or through setTreeTableModel().
		 * 
		 * @param model
		 *            Underlying data model for the TreeTable that will
		 *            ultimately be bound to this TreeTableModelAdapter
		 * @param tree
		 *            TreeAndTableCellRenderer instantiated with the same model
		 *            as specified by the model parameter of this constructor
		 * @throws IllegalArgumentException
		 *             if a null model argument is passed
		 * @throws IllegalArgumentException
		 *             if a null tree argument is passed
		 */
		TreeTableModelAdapter(TreeTableModel model, Tree tree) {
			if (model == null)
				throw new IllegalArgumentException("TreeTableModel model must be non null");
			if (tree == null)
				throw new IllegalArgumentException("TreeTableModel model must be non null");

			this.tree = tree; // need tree to implement getRowCount()
			this.model = model;

			// Install a TreeModelListener that can update the table when
			// tree changes. We use delayedFireTableDataChanged as we can
			// not be guaranteed the tree will have finished processing
			// the event before us.
			model.addTreeModelListener(new TreeModelListener() {
				public void treeNodesChanged(TreeModelEvent e) {
					delayedFireTableDataChanged(e, 0);
				}

				public void treeNodesInserted(TreeModelEvent e) {
					delayedFireTableDataChanged(e, 1);
				}

				public void treeNodesRemoved(TreeModelEvent e) {
					delayedFireTableDataChanged(e, 2);
				}

				public void treeStructureChanged(TreeModelEvent e) {
					delayedFireTableDataChanged();
				}
			});

			tree.addTreeExpansionListener(new TreeExpansionListener() {
				public void treeCollapsed(TreeExpansionEvent event) {
					fireTableDataChanged();
				}

				// Don't use fireTableRowsInserted() here; the selection model
				// would get updated twice.
				public void treeExpanded(TreeExpansionEvent event) {
					fireTableDataChanged();
				}
			});
		}

		/**
		 * Immutably binds this TreeTableModelAdapter to the specified
		 * TreeTable.
		 * 
		 * @param treeTable
		 *            the TreeTable instance that this adapter is bound to.
		 */
		protected final void bind(TreeTable treeTable) {
			// Suppress potentially subversive invocation!
			// Prevent clearing out the deck for possible hijack attempt later!
			if (treeTable == null) {
				throw new IllegalArgumentException("null treeTable");
			}

			if (this.treeTable == null) {
				this.treeTable = treeTable;
			} else {
				throw new IllegalArgumentException("adapter already bound");
			}
		}

		/**
		 * Invokes fireTableDataChanged
		 */
		private void delayedFireTableDataChanged() {
			fireTableDataChanged();
		}

		/**
		 * Invokes fireTableDataChanged after all the pending events have been
		 * processed. SwingUtilities.invokeLater is used to handle this.
		 */
		private void delayedFireTableDataChanged(final TreeModelEvent tme, final int typeChange) {
			int indices[] = tme.getChildIndices();
			TreePath path = tme.getTreePath();
			if (indices != null) {
				if (tree.isExpanded(path)) { // Dont bother to update if
					// the parent
					// node is collapsed
					int startingRow = tree.getRowForPath(path) + 1;
					int min = Integer.MAX_VALUE;
					int max = Integer.MIN_VALUE;
					for (int i = 0; i < indices.length; i++) {
						if (indices[i] < min) {
							min = indices[i];
						}
						if (indices[i] > max) {
							max = indices[i];
						}
					}
					switch (typeChange) {
					case 0:
						fireTableRowsUpdated(startingRow + min, startingRow + max);
						break;
					case 1:
						fireTableRowsInserted(startingRow + min, startingRow + max);
						break;
					case 2:
						fireTableRowsDeleted(startingRow + min, startingRow + max);
						break;
					}
				}
			} else { // case where the event is fired to identify root.
				fireTableDataChanged();
			}
		}

		// Wrappers, implementing TableModel interface.
		// TableModelListener management provided by AbstractTableModel
		// superclass.
		public Class getColumnClass(int column) {
			return model.getColumnClass(column);
		}

		/**
		 * @see nextapp.echo2.app.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return model.getColumnCount();
		}

		/**
		 * @see nextapp.echo2.app.table.TableModel#getColumnName(int)
		 */
		public String getColumnName(int column) {
			return model.getColumnName(column);
		}

		/**
		 * @see nextapp.echo2.app.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return tree.getRowCount();
		}

		/**
		 * Returns the TreeTable instance to which this TreeTableModelAdapter is
		 * permanently and exclusively bound. For use by
		 * {@link echopointng.TreeTable#setModel(TableModel)}.
		 * 
		 * @return TreeTable to which this TreeTableModelAdapter is permanently
		 *         bound
		 */
		protected TreeTable getTreeTable() {
			return treeTable;
		}

		/**
		 * Returns the real TreeTableModel that is wrapped by this
		 * TreeTableModelAdapter.
		 * 
		 * @return the real TreeTableModel that is wrapped by this
		 *         TreeTableModelAdapter
		 */
		public TreeTableModel getTreeTableModel() {
			return model;
		}

		/**
		 * @see nextapp.echo2.app.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int column, int row) {
			//
			// make a call the the TableTreeModel to get
			// the column data in the context of a given
			// tree node.
			//
			Object node = nodeForRow(row);
			return model.getValueAt(node, column);
		}

		Object nodeForRow(int row) {
			TreePath path = pathForRow(row);
			if (path != null) {
				return path.getLastPathComponent();
			}
			return null;
		}

		TreePath pathForRow(int row) {
			return tree.getPathForRow(row);
		}
	}

	private TreeAndTableCellRenderer treeAndTableCellRenderer = null;

	/**
	 * Constructs a TreeTable using a
	 * {@link echopointng.treetable.DefaultTreeTableModel}.
	 */
	public TreeTable() {
		this(new DefaultTreeTableModel());
	}

	/**
	 * Constructs a TreeTable using the specified
	 * {@link echopointng.treetable.TreeTableModel}.
	 * 
	 * @param treeModel
	 *            model for the TreeTable
	 */
	public TreeTable(TreeTableModel treeModel) {
		// Implementation note:
		// Make sure that the SAME instance of treeModel is passed to the
		// constructor for TreeAndTableCellRenderer as is passed in the first
		// argument to the following chained constructor for this TreeTable:
		this(treeModel, new TreeTable.TreeAndTableCellRenderer(treeModel));
	}

	/**
	 * Constructs a <code>TreeTable</code> using the specified
	 * {@link echopointng.treetable.TreeTableModel}and
	 * {@link echopointng.treetable.TreeAndTableCellRenderer}. The
	 * treeAndTableCellRenderer must have been constructed using the same
	 * instance of {@link echopointng.treetable.TreeTableModel}as passed to
	 * this constructor.
	 * 
	 * @param treeModel
	 *            model for the TreeTable
	 * @param treeAndTableCellRenderer
	 *            cell treeAndTableCellRenderer for the tree portion of this
	 *            TreeTable instance.
	 * @throws IllegalArgumentException
	 *             if an attempt is made to instantiate TreeTable and
	 *             TreeAndTableCellRenderer with different instances of
	 *             TreeTableModel.
	 */
	private TreeTable(TreeTableModel treeModel, TreeAndTableCellRenderer renderer) {
		// To avoid unnecessary object creation, such as the construction of a
		// DefaultTableModel, it is better to invoke
		// super(TreeTableModelAdapter)
		// directly, instead of first invoking super() followed by a call to
		// setTreeTableModel(TreeTableModel).

		// Adapt tree model to table model before invoking super()
		super(new TreeTableModelAdapter(treeModel, renderer));

		// Enforce referential integrity; bail on fail
		if (treeModel != renderer.getModel()) { // do not use assert here!
			throw new IllegalArgumentException("Mismatched TreeTableModel");
		}
		init(renderer);
	}

	/**
	 * Collapses all nodes in the treetable.
	 */
	public void collapseAll() {
		treeAndTableCellRenderer.collapseAll();
	}

	/**
	 * Collapses the node at the specified path in the treetable.
	 * 
	 * @param path
	 *            path of the node to collapse
	 */
	public void collapsePath(TreePath path) {
		treeAndTableCellRenderer.collapsePath(path);
	}

	/**
	 * Collapses the row in the treetable. If the specified row index is not
	 * valid, this method will have no effect.
	 */
	public void collapseRow(int row) {
		treeAndTableCellRenderer.collapseRow(row);
	}

	/**
	 * Expands all nodes in the treetable.
	 */
	public void expandAll() {
		treeAndTableCellRenderer.expandAll();
	}

	/**
	 * Expands the the node at the specified path in the treetable.
	 * 
	 * @param path
	 *            path of the node to expand
	 */
	public void expandPath(TreePath path) {
		treeAndTableCellRenderer.expandPath(path);
	}

	/**
	 * Expands the specified row in the treetable. If the specified row index is
	 * not valid, this method will have no effect.
	 */
	public void expandRow(int row) {
		treeAndTableCellRenderer.expandRow(row);
	}

	/**
	 * Returns an <code>Enumeration</code> of the descendants of the path
	 * <code>parent</code> that are currently expanded. If <code>parent</code>
	 * is not currently expanded, this will return <code>null</code>. If you
	 * expand/collapse nodes while iterating over the returned
	 * <code>Enumeration</code> this may not return all the expanded paths, or
	 * may return paths that are no longer expanded.
	 * 
	 * @param parent
	 *            the path which is to be examined
	 * @return an <code>Enumeration</code> of the descendents of
	 *         <code>parent</code>, or <code>null</code> if
	 *         <code>parent</code> is not currently expanded
	 */

	public Enumeration getExpandedDescendants(TreePath parent) {
		return treeAndTableCellRenderer.getExpandedDescendants(parent);
	}

	/**
	 * Returns the TreePath for a given row.
	 * 
	 * @param row
	 * 
	 * @return the <code>TreePath</code> for the given row.
	 */
	public TreePath getPathForRow(int row) {
		return treeAndTableCellRenderer.getPathForRow(row);
	}

	/**
	 * Returns the value of the <code>showsRootHandles</code> property.
	 * 
	 * @return the value of the <code>showsRootHandles</code> property
	 */
	public boolean getShowsRootHandles() {
		return treeAndTableCellRenderer.getShowsRootHandles();
	}

	/**
	 * This method returns the underlying Tree component of the TreeTable. It
	 * allows you to customise specific Tree properties such as th selection
	 * model etc..
	 * 
	 * @return returns the underlying Tree component of the TreeTable.
	 */
	public Tree getTree() {
		return treeAndTableCellRenderer;
	}

	/**
	 * Returns the selection model for the tree portion of the this treetable.
	 * 
	 * @return selection model for the tree portion of the this treetable
	 */
	public TreeSelectionModel getTreeSelectionModel() {
		return treeAndTableCellRenderer.getSelectionModel();
	}

	/**
	 * Returns the underlying TreeTableModel for this TreeTable.
	 * 
	 * @return the underlying TreeTableModel for this TreeTable
	 */
	public TreeTableModel getTreeTableModel() {
		return getTreeTableModelAdapter().getTreeTableModel();
	}

	/**
	 * Returns the underlying TreeTableModelAdapter for this TreeTable, this is
	 * the mode that is intermediate between TableModel and TreeTableModel.
	 * 
	 * @return the underlying TreeTableModelAdapter for this TreeTable
	 */
	TreeTableModelAdapter getTreeTableModelAdapter() {
		return (TreeTableModelAdapter) getModel();
	}

	/**
	 * Initializes this TreeTable and permanently binds the specified
	 * treeAndTableCellRenderer to it.
	 * 
	 * @param treeAndTableCellRenderer
	 *            private tree/treeAndTableCellRenderer permanently and
	 *            exclusively bound to this TreeTable.
	 */
	private final void init(TreeAndTableCellRenderer treeAndTableCellRenderer) {
		this.treeAndTableCellRenderer = treeAndTableCellRenderer;
		this.add(treeAndTableCellRenderer);
		// Force the Table and Tree to share their row selection models.
		ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper();

		if (treeAndTableCellRenderer != null) {
			treeAndTableCellRenderer.bind(this); // IMPORTANT: link back!
			treeAndTableCellRenderer.setSelectionModel(selectionWrapper);
		}

		treeAndTableCellRenderer.setRootVisible(true);
		setSelectionModel(selectionWrapper.getTableSelectionModel());
		setDefaultRenderer(AbstractTreeTableModel.hierarchicalColumnClass, treeAndTableCellRenderer);
	}

	/**
	 * Returns true if the node at the specified display row is collapsed.
	 * 
	 * @param row
	 *            row
	 * @return true, if the node at the specified display row is currently
	 *         collapsed. false, otherwise
	 */
	public boolean isCollapsed(int row) {
		return treeAndTableCellRenderer.isCollapsed(row);
	}

	/**
	 * Returns true if the node identified by path is currently collapsed, this
	 * will return false if any of the values in path are currently not being
	 * displayed.
	 * 
	 * @param path
	 *            path
	 * @return true, if the value identified by path is currently collapsed;
	 *         false, otherwise
	 */
	public boolean isCollapsed(TreePath path) {
		return treeAndTableCellRenderer.isCollapsed(path);
	}

	/**
	 * Returns true if the node at the specified display row is currently
	 * expanded. Otherwise, this method returns false.
	 * 
	 * @param row
	 *            row
	 * @return true, if the node at the specified display row is currently
	 *         expanded. false, otherwise
	 */
	public boolean isExpanded(int row) {
		return treeAndTableCellRenderer.isExpanded(row);
	}

	/**
	 * Returns true if the node identified by path is currently expanded.
	 * Otherwise, this method returns false.
	 * 
	 * @param path
	 *            path
	 * @return true, if the value identified by path is currently expanded;
	 *         false, otherwise
	 */
	public boolean isExpanded(TreePath path) {
		return treeAndTableCellRenderer.isExpanded(path);
	}

	/**
	 * Determines if the specified column contains hierarchical nodes.
	 * 
	 * @param column
	 *            zero-based index of the column
	 * @return true if the class of objects in the specified column implement
	 *         the {@link echopointng.tree.TreeNode}interface; false otherwise.
	 */
	public boolean isHierarchical(int column) {
		Class columnClass = getModel() != null ? getModel().getColumnClass(column) : null;
		return AbstractTreeTableModel.hierarchicalColumnClass.isAssignableFrom(columnClass);
	}

	/**
	 * Returns true if the root node of the tree is expanded when the TreeModel
	 * is first set.
	 * 
	 */
	public boolean isRootAutoExpanded() {
		return treeAndTableCellRenderer.isRootAutoExpanded();
	}

	/**
	 * Returns true if the root node of the tree is displayed.
	 * 
	 * @return true if the root node of the tree is displayed
	 */
	public boolean isRootVisible() {
		return treeAndTableCellRenderer.isRootVisible();
	}

	/**
	 * Returns true if the value identified by path is currently viewable, which
	 * means it is either the root or all of its parents are expanded.
	 * Otherwise, this method returns false.
	 * 
	 * @return true, if the value identified by path is currently viewable;
	 *         false, otherwise
	 */
	public boolean isVisible(TreePath path) {
		return treeAndTableCellRenderer.isVisible(path);
	}

	/**
	 * We forward the TreePath input into out internal Tree.
	 * 
	 * @see nextapp.echo2.app.Table#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		if (inputValue instanceof TreePath) {
			treeAndTableCellRenderer.processInput(inputName, inputValue);
		} else {
			super.processInput(inputName, inputValue);
		}
		if (getModel() == null)
			return;

	}

	/**
	 * Sets the specified TreeCellRenderer
	 * 
	 * @param cellRenderer
	 *            to use for rendering tree cells.
	 */
	public void setCellRenderer(TreeCellRenderer cellRenderer) {
		if (treeAndTableCellRenderer != null) {
			treeAndTableCellRenderer.setCellRenderer(cellRenderer);
		}
	}

	/**
	 * <p>
	 * Overrides superclass version to make sure that the specified
	 * {@link TableModel}is compatible with TreeTable before invoking the
	 * inherited version.
	 * </p>
	 * 
	 * <p>
	 * Because TreeTable internally adapts an
	 * {@link echopointng.treetable.TreeTableModel}to make it a compatible
	 * TableModel, <b>this method should never be called directly </b>. Use
	 * {@link #setTreeTableModel(echopointng.treetable.TreeTableModel) setTreeTableModel}
	 * instead.
	 * </p>
	 * 
	 * <p>
	 * While it is possible to obtain a reference to this adapted version of the
	 * TableModel by calling {@link Table#getModel()}, any attempt to call
	 * setModel() with that adapter will fail because the adapter might have
	 * been bound to a different TreeTable instance. If you want to extract the
	 * underlying TreeTableModel, which, by the way, <em>can</em> be shared,
	 * use {@link #getTreeTableModel() getTreeTableModel}instead
	 * </p>.
	 * 
	 * @param tableModel
	 *            must be a TreeTableModelAdapter, which general calles will not
	 *            have access to.
	 * @throws IllegalArgumentException
	 *             if the specified tableModel is not an instance of
	 *             TreeTableModelAdapter
	 */
	public final void setModel(TableModel tableModel) { // note final keyword
		if (tableModel instanceof TreeTableModelAdapter) {
			if (((TreeTableModelAdapter) tableModel).getTreeTable() == null) {
				// Passing the above test ensures that this method is being
				// invoked either from TreeTable/Table constructor or from
				// setTreeTableModel(TreeTableModel)
				super.setModel(tableModel); // invoke superclass version

				((TreeTableModelAdapter) tableModel).bind(this); // permanently
				// bound
				// Once a TreeTableModelAdapter is bound to any TreeTable
				// instance,
				// invoking TreeTable.setModel() with that adapter will throw an
				// IllegalArgumentException, because we really want to make sure
				// that a TreeTableModelAdapter is NOT shared by another
				// TreeTable.
			} else {
				throw new IllegalArgumentException("model already bound");
			}
		} else {
			throw new IllegalArgumentException("unsupported model type");
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
		treeAndTableCellRenderer.setRootAutoExpanded(rootExpanded);
	}

	/**
	 * Determines whether or not the root node from the TreeModel is visible.
	 * 
	 * @param visible
	 *            true, if the root node is visible; false, otherwise
	 */
	public void setRootVisible(boolean visible) {
		treeAndTableCellRenderer.setRootVisible(visible);
	}

	/* ========================================================================== */
	/* ========================================================================== */
	/* ========================================================================== */
	/* ========================================================================== */

	/**
	 * <p>
	 * Overridden to ensure that private treeAndTableCellRenderer state is kept
	 * in sync with the state of the component. Calls the inherited version
	 * after performing the necessary synchronization. If you override this
	 * method, make sure you call this version from your version of this method.
	 * </p>
	 * 
	 * <p>
	 * This version maps the selection mode used by the treeAndTableCellRenderer
	 * to match the selection mode specified for the table. Specifically, the
	 * modes are mapped as follows:
	 * 
	 * <pre>
	 *  
	 *       ListSelectionModel.SINGLE_SELECTION: TreeSelectionModel.CONTIGUOUS_TREE_SELECTION;
	 *       ListSelectionModel.MULTIPLE__SELECTION: TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION;
	 *       any other (default): TreeSelectionModel.SINGLE_TREE_SELECTION;
	 *   
	 * </pre>
	 * 
	 * @param mode
	 *            any of the table selection modes
	 */
	public void setSelectionMode(int mode) {
		if (treeAndTableCellRenderer != null) {
			switch (mode) {
			case ListSelectionModel.SINGLE_SELECTION: {
				treeAndTableCellRenderer.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
				break;
			}
			case ListSelectionModel.MULTIPLE_SELECTION: {
				treeAndTableCellRenderer.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
				break;
			}
			default: {
				treeAndTableCellRenderer.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				break;
			}
			}
		}
		if (super.getSelectionModel() != null) {
			super.getSelectionModel().setSelectionMode(mode);
		}
	}

	/**
	 * Sets the value of the <code>showsRootHandles</code> property for the
	 * tree part. This property specifies whether the node handles should be
	 * displayed. If handles are not supported by a particular look and feel,
	 * this property may be ignored.
	 * 
	 * @param visible
	 *            true, if root handles should be shown; false, otherwise
	 */
	public void setShowsRootHandles(boolean visible) {
		treeAndTableCellRenderer.setShowsRootHandles(visible);
	}

	/**
	 * Sets the data model for this TreeTable to the specified
	 * {@link echopointng.treetable.TreeTableModel}. The same data model may be
	 * shared by any number of TreeTable instances.
	 * 
	 * @param treeModel
	 *            data model for this TreeTable
	 */
	public void setTreeTableModel(TreeTableModel treeModel) {
		treeAndTableCellRenderer.setModel(treeModel);
		// Adapt tree model to table model before invoking setModel()
		setModel(new TreeTableModelAdapter(treeModel, treeAndTableCellRenderer));
		// Enforce referential integrity; bail on fail
		if (treeModel != treeAndTableCellRenderer.getModel()) { // do not use
																// assert here!
			throw new IllegalArgumentException("Mismatched TreeTableModel");
		}
	}

	/**
	 * @see nextapp.echo2.app.Table#validate()
	 */
	public void validate() {
		super.validate();
	}

}
