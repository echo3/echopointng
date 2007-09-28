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
package echopointng;

import java.io.Serializable;
import java.util.BitSet;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.LayoutData;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.Table;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.list.ListSelectionModel;
import nextapp.echo2.app.table.DefaultTableModel;
import nextapp.echo2.app.table.TableCellRenderer;
import nextapp.echo2.app.table.TableColumn;
import nextapp.echo2.app.table.TableColumnModel;
import nextapp.echo2.app.table.TableModel;
import echopointng.able.Attributeable;
import echopointng.able.Borderable;
import echopointng.able.Stretchable;
import echopointng.command.AttributesAdd;
import echopointng.layout.TableLayoutDataEx;
import echopointng.list.ListSelectionModelEx;
import echopointng.table.DefaultTableCellRendererEx;
import echopointng.table.PageableTableModel;
import echopointng.table.TableActionEventEx;
import echopointng.table.TableCellRendererEx;
import echopointng.table.TableColumnEx;
import echopointng.util.ColorKit;
import echopointng.util.TokenizerKit;
import echopointng.xhtml.XhtmlFragment;

/**
 * <code>TableEx</code> is an implementation of <code>Table</code> thats
 * adds support for light weight table cells and column and row spanning of
 * cells. This is achieved by supporting the <code>TableCellRendererEx</code>
 * interface, the <code>TableLayoutDataEx</code> layout data and the
 * <code>XhtmlFragment</code> object for specifying cell content.
 * 
 * @see echopointng.table.TableCellRendererEx
 * @see echopointng.layout.TableLayoutDataEx
 * @see echopointng.xhtml.XhtmlFragment
 */
public class TableEx extends Table implements Attributeable, Stretchable {

	/**
	 * <code>CellMatrix</code> is used to hold the content for a given sized
	 * set of cells.
	 * <p>
	 * The index of the cell matrix is 0,0 to (rowCount,cellCount) unless there
	 * is a isHeaderVisible in which case it is 0,-1 to ((rowCount*cellCount)
	 * 
	 */
	protected class CellMatrix implements Serializable {

		private Map cellContentMap;

		private Object cellSpanObject;

		private int columnCount;

		private int rowCount;

		private Map propertyMap;

		/**
		 * Constructs a <code>CellMatrix</code>
		 * 
		 * @param columnCount -
		 *            the maximum number of columns
		 * @param rowCount -
		 *            the maximum number of rows
		 * @param cellSpanObject -
		 *            the object to be stored in a cell if its is spanned via
		 *            rowCount and colSpan values.
		 */
		public CellMatrix(int columnCount, int rowCount, Object cellSpanObject) {
			super();
			this.rowCount = rowCount;
			this.columnCount = columnCount;
			this.cellSpanObject = cellSpanObject;
			int cellCount = rowCount * columnCount;
			cellCount += (isHeaderVisible() ? columnCount : 0);
			cellContentMap = new HashMap(cellCount);
		}

		/**
		 * Returns true if the specified value is part of the content in this
		 * <code>CellMatrix</code>
		 * 
		 * @param value -
		 *            what the check for
		 * @return true if the specified value is part of the content in this
		 *         <code>CellMatrix</code>
		 */
		public boolean containsCellContent(Object value) {
			for (Iterator iter = cellContentMap.entrySet().iterator(); iter.hasNext();) {
				Entry entry = (Entry) iter.next();
				if (entry.getValue() == value || entry.getValue().equals(value)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Returns the cell content at the specified column and row co-ordinate.
		 * 
		 * @param column
		 * @param row
		 * @return the cell content at the specified column and row co-ordinate.
		 * @throws IllegalArgumentException
		 *             if the specified column and row co-ordinate is outside
		 *             the bounds of the CellMatrix.
		 */
		public Object getCellContent(int column, int row) {
			validateCoordinate(column, row);
			String key = getColRowKey(column, row);
			Object content = cellContentMap.get(key);
			return content;
		}

		/**
		 * @return Returns the cellSpanObject in use
		 */
		public Object getCellSpanObject() {
			return cellSpanObject;
		}

		/**
		 * @return a key by col/row
		 */
		private String getColRowKey(int column, int row) {
			StringBuffer sb = new StringBuffer("col:");
			sb.append(column);
			sb.append("row:");
			sb.append(row);
			return sb.toString();
		}

		/**
		 * @return Returns the columnCount.
		 */
		public int getColumnCount() {
			return columnCount;
		}

		/**
		 * @return Returns the rowCount.
		 */
		public int getRowCount() {
			return rowCount;
		}

		/**
		 * Sets the content of the cells at the specified column/row
		 * co-ordinate.
		 * 
		 * @param value -
		 *            the content value to be placed in the cell
		 * @param column -
		 *            the column index
		 * @param row -
		 *            the row index
		 * @throws IllegalArgumentException
		 *             if the specified column and row co-ordinate is outside
		 *             the bounds of the CellMatrix.
		 */
		public void setCellContent(Object value, int column, int row) {
			setCellContent(value, column, row, 0, 0);
		}

		/**
		 * Sets the content of the cells at the specified column/row
		 * co-ordinate. The column span and row span can be specified as well.
		 * If this is greater then 1, then cells at the appropriate spanned cell
		 * co-ordinates are set the cellSpanObject.
		 * 
		 * @param value -
		 *            the content value to be placed in the cell
		 * @param column -
		 *            the column index
		 * @param row -
		 *            the row index
		 * @param colSpan -
		 *            the column span
		 * @param rowSpan -
		 *            the row span
		 * @throws IllegalArgumentException
		 *             if the specified column and row co-ordinate is outside
		 *             the bounds of the CellMatrix.
		 */
		public void setCellContent(Object value, int column, int row, int colSpan, int rowSpan) {
			validateCoordinate(column, row);
			String key = getColRowKey(column, row);
			cellContentMap.put(key, value);
			// handle col and row span by setting the content of the
			// cells that are skipped with the cellSpanObject
			if (rowSpan > 1 || colSpan > 1) {
				int maxRows = row;
				if (rowSpan > 1) {
					maxRows = Math.min(row + (rowSpan - 1), rowCount - 1);
				}
				int maxColumns = column;
				for (int rowIndex = row; rowIndex <= maxRows; rowIndex++) {
					if (colSpan > 1) {
						maxColumns = Math.min(column + (colSpan - 1), columnCount - 1);
						for (int columnIndex = column; columnIndex <= maxColumns; columnIndex++) {
							// protected against setting the current cell
							if (rowIndex == row && columnIndex == column) {
								continue;
							}
							setCellContent(cellSpanObject, columnIndex, rowIndex);
						}
					}
					// protected against setting the current cell
					if (rowIndex == row) {
						continue;
					}
					setCellContent(cellSpanObject, column, rowIndex);
				}
			}
		}

		/**
		 * Called to set specific property information for a cell.
		 * 
		 * @param column -
		 *            the column of the cell
		 * @param row -
		 *            the row of the cell
		 * @param propertyName -
		 *            the property name
		 * @param value -
		 *            a value for that property at that cell
		 */
		public void setCellProperties(int column, int row, String propertyName, Object value) {
			if (propertyMap == null) {
				propertyMap = new HashMap();
			}
			String key = makePropertyMapKey(column, row, propertyName);
			propertyMap.put(key, value);
		}

		/**
		 * 
		 */
		public Object getCellProperties(int column, int row, String propertyName) {
			if (propertyMap == null) {
				return null;
			}
			String key = makePropertyMapKey(column, row, propertyName);
			return propertyMap.get(key);
		}

		private String makePropertyMapKey(int column, int row, String propertyName) {
			String key = "r:" + row + "c:" + column + "pn:" + propertyName;
			return key;
		}

		private void validateCoordinate(int column, int row) {
			if (column < 0) {
				throw new IllegalArgumentException("The column index [" + column + "] is less than zero");
			}
			if (column > columnCount) {
				throw new IllegalArgumentException("The column index [" + column + "] is greater than columnCount [" + columnCount + "]");
			}
			if (row > rowCount) {
				throw new IllegalArgumentException("The row index [" + row + "] is greater than rowCount [" + rowCount + "]");
			}
			int minRowIndex = 0;
			if (isHeaderVisible()) {
				minRowIndex = -1;
			}
			if (isFooterVisible()) {
				minRowIndex = -2;
			}
			if (row < minRowIndex) {
				throw new IllegalArgumentException("The row index [" + row + "] is less than the minimum allowed [" + minRowIndex + "]");
			}
		}
	}

	/**
	 * Inidicates that the row index of the footer row.
	 */
	public static final int FOOTER_ROW = -2;

	/**
	 * This object is used to indicate cell content that is currently spanned
	 * (and hence has no content).
	 */
	public static final Serializable CELL_SPANNER =  new Serializable() {};

	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();

		style.setProperty(PROPERTY_SELECTION_BACKGROUND, Color.YELLOW);
		style.setProperty(Borderable.PROPERTY_BORDER, BorderEx.DEFAULT);
		style.setProperty(PROPERTY_ROLLOVER_ENABLED, true);
		style.setProperty(PROPERTY_ROLLOVER_BACKGROUND, ColorKit.makeColor("#DEF3FF"));
		DEFAULT_STYLE = style;
	}

	/**
	 * The default renderer for table cells for TableEx is a
	 * <code>DefaultTableCellRendererEx</code>, this will be used as the last
	 * resort when no TableColumns / Table Column Class and TableEx instances
	 * have a cell renderer in play.
	 */
	public static final TableCellRendererEx DEFAULT_TABLE_CELL_RENDERER = new DefaultTableCellRendererEx();

	public static final String PROPERTY_HEADER_BACKGROUND = "headerBackground";

	public static final String PROPERTY_FOOTER_BACKGROUND = "footerBackground";

	public static final String PROPERTY_FOOTER_VISIBLE = "footerVisible";

	public static final String PROPERTY_HEIGHT = "height";

	public static final String PROPERTY_IGNORE_META_KEYS = "ignoreMetaKeys";

	public static final String PROPERTY_RESIZE_DRAG_BAR_USED = "resizeDragBarUsed";

	public static final String PROPERTY_RESIZE_GROWS_TABLE = "resizeGrowsTable";

	public static final String PROPERTY_RESIZEABLE = "resizeable";

	public static final String PROPERTY_SCROLLABLE = "scrollable";

	public static final String COLUMN_WIDTHS_CHANGED_PROPERTY = "columnWidthsChanged";

	public static final String DEFAULT_FOOTER_RENDERER_CHANGED_PROPERTY = "defaultFooterRenderer";

	private Map attributeMap;

	private TableCellRenderer defaultFooterRenderer;

	private TableCellRenderer defaultRenderer;

	/**
	 * Tracks the content for the cells of the <code>TableEx</code>
	 */
	protected CellMatrix cellMatrix;

	/**
	 * A boolean flag indicating whether the <code>TableEx</code> is valid or
	 * not.
	 */
	protected boolean valid;

	/**
	 * A boolean flag indicating whether the <code>TableEx</code> has a
	 * visible footer or not
	 */
	protected boolean footerVisible;

	/**
	 * Creates a new <code>TableEx</code> with an empty
	 * <code>DefaultTableModel</code>.
	 */
	public TableEx() {
		this(new DefaultTableModel());
	}

	/**
	 * Creates a new <code>TableEx</code> with a new
	 * <code>DefaultTableModel</code> with the specified dimensions.
	 * 
	 * @param columns
	 *            the initial column count
	 * @param rows
	 *            the initial row count
	 */
	public TableEx(int columns, int rows) {
		this(new DefaultTableModel(columns, rows));
	}

	/**
	 * Creates a <code>TableEx</code> using the supplied
	 * <code>TableModel</code>.
	 * 
	 * @param model
	 *            the initial model
	 */
	public TableEx(TableModel model) {
		this(model, null);
	}

	/**
	 * Creates a <code>TableEx</code> with the supplied
	 * <code>TableModel</code> and the specified <code>TableColumnModel</code>.
	 * 
	 * @param model
	 *            the initial model
	 * @param columnModel
	 *            the initial column model
	 */
	public TableEx(TableModel model, TableColumnModel columnModel) {
		super(model, columnModel);
		defaultRenderer = DEFAULT_TABLE_CELL_RENDERER;
		setFocusTraversalParticipant(false);
	}

	/**
	 * @see nextapp.echo2.app.Table#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		if (inputName.equals(SELECTION_CHANGED_PROPERTY)) {
			// are we a special case for selection
		   	if (getModel() instanceof PageableTableModel || (getSelectionModel() instanceof ListSelectionModelEx)) {
		   		setPageableSelectionEx((int[]) inputValue);
		   	} else {
		   		super.processInput(inputName, inputValue);
		   	}
		}
		if (INPUT_ACTION.equals(inputName)) {
			String value = String.valueOf(inputValue);
			String values[] = TokenizerKit.tokenize(value, ";", false);
			int metaKeyInfo = 0;
			int row = -1;
			int column = -1;
			for (int i = 0; i < values.length; i++) {
				if (values[i].startsWith("c:")) {
					column = Integer.parseInt(values[i].substring(2));
				}
				if (values[i].startsWith("r:")) {
					row = Integer.parseInt(values[i].substring(2));
				}
				if (values[i].startsWith("mk:")) {
					metaKeyInfo = Integer.parseInt(values[i].substring(3));
				}
			}
			fireActionEvent(metaKeyInfo, column, row);
		}
		if (inputName.equals(COLUMN_WIDTHS_CHANGED_PROPERTY)) {
			int columnWidths[] = (int[]) inputValue;
			TableColumnModel columnModel = this.getColumnModel();
			if (columnModel != null) {
				for (int i = 0; i < columnWidths.length; i++) {
					TableColumn tableColumn = columnModel.getColumn(i);
					if (tableColumn != null) {
						tableColumn.setWidth(new ExtentEx(columnWidths[i]));
					}
				}
			}
		}
	}
	
    /**
     * Selects only the specified row indices in response to a
     * selection data from the client but only for pageable tables.
     * 
     * @param selectedIndices the indices to select
     */
    protected void setPageableSelectionEx(int[] selectedIndices) {
    	/*
    	 * If the TableModel is a PagedTableModel then we dont want to clear the selection
    	 * on other pages here.  They are not visible to the user but they are still valid
    	 * So we only "clear" the selections on the page that is visible.
    	 */
    	BitSet incomingSelections = new BitSet();
    	for (int i = 0; i < selectedIndices.length; i++) {
			incomingSelections.set(selectedIndices[i]);
		}
    	ListSelectionModel selectionModel = getSelectionModel();
    	if (selectionModel.getMaxSelectedIndex() != -1) {
	    	if (getModel() instanceof PageableTableModel) {
	    		// just a pages worth of data please
	    		int rowCount = getModel().getRowCount();
		     	for (int row = 0; row < rowCount; row++) {
					if (selectionModel.isSelectedIndex(row)) {
						// if its in our incoming selection then dont clear it
						if (! incomingSelections.get(row)) {
							selectionModel.setSelectedIndex(row, false);
						}
					}
				}
	    	} else {
	    		// just clear it directly as it more efficient
	    		selectionModel.clearSelection();
	    	}
    	}
    	/*
    	 * Now do the selection.  If we can do it in one step then great.
    	 */
 		if (selectionModel instanceof ListSelectionModelEx) {
			ListSelectionModelEx listSelectionModelEx = (ListSelectionModelEx) selectionModel;
			listSelectionModelEx.setSelectedIndices(selectedIndices,true);
		} else {
	        for (int i = 0; i < selectedIndices.length; ++i) {
	            selectionModel.setSelectedIndex(selectedIndices[i], true);
	        }
		}
        firePropertyChange(SELECTION_CHANGED_PROPERTY, null, selectedIndices);
    }
	

	/**
	 * Fires an action event to all listeners.
	 */
	protected void fireActionEvent(int metaKeyInfo, int column, int row) {
		if (!hasEventListenerList()) {
			return;
		}
		EventListener[] listeners = getEventListenerList().getListeners(ActionListener.class);
		TableActionEventEx e = null;
		for (int i = 0; i < listeners.length; ++i) {
			if (e == null) {
				e = new TableActionEventEx(this, (String) getRenderProperty(PROPERTY_ACTION_COMMAND), metaKeyInfo, column, row);
			}
			((ActionListener) listeners[i]).actionPerformed(e);
		}
	}

	/**
	 * Re-renders the rows of the <code>TableEx</code>.
	 */
	protected void doRender() {
		TableModel model = getModel();
		TableColumnModel columnModel = getColumnModel();
		TableCellRenderer defaultHeaderRenderer = getDefaultHeaderRenderer();
		TableCellRenderer defaultFooterRenderer = getDefaultFooterRenderer();
		TableCellRenderer defaultRenderer = getDefaultRenderer();
		defaultRenderer = (defaultRenderer == null ? DEFAULT_TABLE_CELL_RENDERER : defaultRenderer);

		int rowCount = model.getRowCount();
		int columnCount = columnModel.getColumnCount();
		boolean isHeaderVisible = isHeaderVisible();
		boolean isFooterVisible = isFooterVisible();

		Map encounteredComponentMap = new HashMap();
		cellMatrix = new CellMatrix(columnCount, rowCount, TableEx.CELL_SPANNER);

		TableColumn[] tableColumns = new TableColumn[columnCount];
		TableCellRenderer[] columnRenderers = new TableCellRenderer[columnCount];

		for (int columnIndex = 0; columnIndex < columnCount; ++columnIndex) {
			tableColumns[columnIndex] = columnModel.getColumn(columnIndex);

			TableCellRenderer renderer = tableColumns[columnIndex].getCellRenderer();
			if (renderer == null) {
				Class columnClass = model.getColumnClass(tableColumns[columnIndex].getModelIndex());
				renderer = getDefaultRenderer(columnClass);
				if (renderer == null) {
					renderer = defaultRenderer;
				}
			}
			columnRenderers[columnIndex] = renderer;
		}

		if (isHeaderVisible) {
			for (int columnIndex = 0; columnIndex < columnCount; ++columnIndex) {
				int modelColumnIndex = tableColumns[columnIndex].getModelIndex();
				Object headerValue = tableColumns[columnIndex].getHeaderValue();
				if (headerValue == null) {
					headerValue = model.getColumnName(modelColumnIndex);
				}
				TableCellRenderer headerRenderer = tableColumns[columnIndex].getHeaderRenderer();
				if (headerRenderer == null) {
					headerRenderer = defaultHeaderRenderer;
					if (headerRenderer == null) {
						headerRenderer = defaultRenderer;
					}
				}
				//
				// get header cell data
				Component renderedComponent = doRenderCellContent(headerRenderer, headerValue, modelColumnIndex, HEADER_ROW);
				if (renderedComponent != null) {
					encounteredComponentMap.put(renderedComponent, renderedComponent);
				}
			}
		}
		if (isFooterVisible) {
			for (int columnIndex = 0; columnIndex < columnCount; ++columnIndex) {

				TableColumn tableColumn = tableColumns[columnIndex];
				int modelColumnIndex = tableColumn.getModelIndex();
				Object footerValue = null;
				if (tableColumn instanceof TableColumnEx) {
					footerValue = ((TableColumnEx) tableColumn).getFooterValue();
				}
				if (footerValue == null) {
					footerValue = model.getColumnName(modelColumnIndex);
				}
				TableCellRenderer footerRenderer = null;
				if (tableColumn instanceof TableColumnEx) {
					footerRenderer = ((TableColumnEx) tableColumn).getFooterRenderer();
				}
				if (footerRenderer == null) {
					footerRenderer = defaultFooterRenderer;
					if (footerRenderer == null) {
						footerRenderer = defaultRenderer;
					}
				}
				//
				// get header cell data
				Component renderedComponent = doRenderCellContent(footerRenderer, footerValue, modelColumnIndex, FOOTER_ROW);
				if (renderedComponent != null) {
					encounteredComponentMap.put(renderedComponent, renderedComponent);
				}
			}
		}
		//
		// data cells next
		for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
			for (int columnIndex = 0; columnIndex < columnCount; ++columnIndex) {
				int modelColumnIndex = tableColumns[columnIndex].getModelIndex();
				Object modelValue = model.getValueAt(modelColumnIndex, rowIndex);

				TableCellRenderer cellRenderer = columnRenderers[columnIndex];
				Component renderedComponent = doRenderCellContent(cellRenderer, modelValue, modelColumnIndex, rowIndex);
				if (renderedComponent != null) {
					encounteredComponentMap.put(renderedComponent, renderedComponent);
				}
			}
		}
		// fix up our children that are no longer cells
		Component[] children = getComponents();
		for (int i = 0; i < children.length; i++) {
			Component child = children[i];
			if (!encounteredComponentMap.containsKey(child)) {
				this.remove(child);
			}
		}
		// we need to fire an event to make sure we have some sort of change to
		// make the TableEx re-paint. Children changing would do it but we cant
		// gaurantee that children will change because they might be xhtml
		// fragments.
		firePropertyChange("repaint", null, model);
	}

	/**
	 * Renders the content of a cell, given a specific
	 * <code>TableCellRenderer</code>. The internal <code>CellMatrix</code>
	 * should be filled with the content in this method. Also it must be careful
	 * to respect cell span parameters that might be in
	 * <code>TableLayoutDataEx</code>.
	 * 
	 * @param cellRenderer -
	 *            the TableCellRenderer in plat
	 * @param modelValue -
	 *            the value from the TableModel
	 * @param column -
	 *            the columnin play
	 * @param row -
	 *            the row in play
	 * @return the Component returned as cell content or null if its not a
	 *         Component to be used as cell content
	 */
	protected Component doRenderCellContent(TableCellRenderer cellRenderer, Object modelValue, int column, int row) {
		if (cellMatrix.getCellContent(column, row) != null) {
			// too bad we already have content in there. It must have been
			// col/row spanned
			return null;
		}
		Object content = null;
		int rowSpan = 0;
		int colSpan = 0;
		TableLayoutDataEx layoutDataEx = null;
		Component renderedComponent = cellRenderer.getTableCellRendererComponent(this, modelValue, column, row);
		if (renderedComponent == null) {
			if (cellRenderer instanceof TableCellRendererEx) {
				TableCellRendererEx cellRendererEx = (TableCellRendererEx) cellRenderer;
				XhtmlFragment fragment = cellRendererEx.getTableCellRendererContent(this, modelValue, column, row);
				content = fragment;
				if (fragment != null) {
					LayoutData layoutData = fragment.getLayoutData();
					if (layoutData instanceof TableLayoutDataEx) {
						layoutDataEx = (TableLayoutDataEx) layoutData;
					}
				}
				boolean isActionCausing = cellRendererEx.isActionCausingCell(this, column, row);
				boolean isSelectionCausing = cellRendererEx.isSelectionCausingCell(this, column, row);
				//
				// we save on memory by only putting in a property value when
				// the cell is NOT action causing
				// or selection causing. The default is that they are action
				// causing.
				if (!isActionCausing) {
					cellMatrix.setCellProperties(column, row, "actionCausing", new Boolean(false));
				}
				if (!isSelectionCausing) {
					cellMatrix.setCellProperties(column, row, "selectionCausing", new Boolean(false));
				}
			} else {
				// we could do what the old table used to do to be backwards
				// compat
				// but its more expensive memory wise so we wont because it
				// gives us
				// nil benefit.
				//
				// renderedComponent = new Label();
				content = null;
			}
		} else if (renderedComponent != null) {
			if (!this.isAncestorOf(renderedComponent)) {
				add(renderedComponent);
			}
			LayoutData layoutData = (LayoutData) renderedComponent.getRenderProperty(Component.PROPERTY_LAYOUT_DATA);
			if (layoutData instanceof TableLayoutDataEx) {
				layoutDataEx = (TableLayoutDataEx) layoutData;
			}
			content = renderedComponent;
		} else {
			content = null;
		}
		if (layoutDataEx != null) {
			colSpan = layoutDataEx.getColSpan();
			rowSpan = layoutDataEx.getRowSpan();
		}
		cellMatrix.setCellContent(content, column, row, colSpan, rowSpan);
		return renderedComponent;
	}

	/**
	 * @see echopointng.able.Attributeable#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String attributeName) {
		if (attributeMap != null) {
			return attributeMap.get(attributeName);
		}
		return null;
	}

	/**
	 * @see echopointng.able.Attributeable#getAttributeNames()
	 */
	public String[] getAttributeNames() {
		if (attributeMap == null) {
			return new String[0];
		}
		int count = 0;
		String[] attributeNames = new String[attributeMap.keySet().size()];
		for (Iterator iter = attributeMap.keySet().iterator(); iter.hasNext();) {
			attributeNames[count++] = (String) iter.next();
		}
		return attributeNames;
	}

	/**
	 * Returns the Component at a specified cell co-ordinate.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified column and row co-ordinate is outside the
	 *             bounds of the TableModel.
	 * 
	 * @see nextapp.echo2.app.Table#getCellComponent(int, int)
	 */
	public Component getCellComponent(int column, int row) {
		Object value = getCellContent(column, row);
		if (value instanceof Component) {
			return (Component) value;
		}
		return null;
	}

	/**
	 * Returns the cell content rendered at the specified cell position. This
	 * can be a Component, TableEx.CELL_SPANNER, <code>XhtmlFragment</code> or
	 * a <code>null</code> value indicating that the cell has no content (but
	 * is still to be shown).
	 * 
	 * @param column
	 *            the column
	 * @param row
	 *            the row
	 * @return the content or null if there is no content there
	 * @throws IllegalArgumentException
	 *             if the specified column and row co-ordinate is outside the
	 *             bounds of the TableModel (including header).
	 */
	public Object getCellContent(int column, int row) {
		if (!valid) {
			validate();
		}
		if (cellMatrix != null) {
			return cellMatrix.getCellContent(column, row);
		}
		return null;
	}

	/**
	 * This method is intended to be called by the TableEx rendering peer.
	 * 
	 * It returns true if the specified cell is an action causing cell, as
	 * specified by a TableCellRendererEx.
	 * 
	 * @param column -
	 *            the column in question
	 * @param row -
	 *            the row in question
	 * @return true if it can cause an action to be raise when clicked.
	 */
	public boolean isActionCausingCell(int column, int row) {
		if (cellMatrix == null) {
			return true;
		}
		Object value = cellMatrix.getCellProperties(column, row, "actionCausing");
		return (value == null ? true : false);
	}

	/**
	 * This method is intended to be called by the TableEx rendering peer.
	 * 
	 * It returns true if the specified cell is an selection causing cell, as
	 * specified by a TableCellRendererEx.
	 * 
	 * @param column -
	 *            the column in question
	 * @param row -
	 *            the row in question
	 * @return true if it can cause an action to be raise when clicked.
	 */
	public boolean isSelectionCausingCell(int column, int row) {
		if (cellMatrix == null) {
			return true;
		}
		Object value = cellMatrix.getCellProperties(column, row, "selectionCausing");
		return (value == null ? true : false);
	}

	/**
	 * 
	 * @return the color to be used as the header background
	 */
	public Color getHeaderBackground() {
		return (Color) getProperty(PROPERTY_HEADER_BACKGROUND);
	}

	/**
	 * 
	 * @return the color to be used as the footer background
	 */
	public Color getFooterBackground() {
		return (Color) getProperty(PROPERTY_FOOTER_BACKGROUND);
	}

	/**
	 * Returns the height of the TableEx.
	 * 
	 * @return the height of the TableEx
	 */
	public Extent getHeight() {
		return (Extent) getProperty(PROPERTY_HEIGHT);
	}

	/**
	 * @see nextapp.echo2.app.Component#init()
	 */
	public void init() {
		// we implement Attributeable via a Command so we dont need peers
		// that are not owned by EPNG.
		super.init();
		getApplicationInstance().enqueueCommand(new AttributesAdd(this, this));
	}

	/**
	 * @see nextapp.echo2.app.Table#invalidate()
	 */
	protected void invalidate() {
		valid = false;
	}

	/**
	 * Determines if the table footer is visible.
	 * 
	 * @return the footer visibility state
	 */
	public boolean isFooterVisible() {
		return ComponentEx.getProperty(this, PROPERTY_FOOTER_VISIBLE, false);
	}

	/**
	 * Determines if meta keys are ignored during selection
	 * 
	 * @return true if if meta keys are ignored during selection
	 * @see TableEx#setIgnoreMetaKeys(boolean)
	 */
	public boolean isIgnoreMetaKeys() {
		return ComponentEx.getProperty(this, PROPERTY_IGNORE_META_KEYS, false);
	}

	/**
	 * @return true if the <code>TableEx</code> is resizeable
	 */
	public boolean isResizeable() {
		return ComponentEx.getProperty(this, PROPERTY_RESIZEABLE, false);
	}

	/**
	 * @return true if a visual bar artefact is used to when resizing columns
	 */
	public boolean isResizeDragBarUsed() {
		return ComponentEx.getProperty(this, PROPERTY_RESIZE_DRAG_BAR_USED, false);
	}

	/**
	 * @return true if a resize operations is allowed to grow the table.
	 */
	public boolean isResizeGrowsTable() {
		return ComponentEx.getProperty(this, PROPERTY_RESIZE_GROWS_TABLE, true);
	}

	/**
	 * @return - true if the <code>TableEx</code> is scrollable
	 */
	public boolean isScrollable() {
		return ComponentEx.getProperty(this, PROPERTY_SCROLLABLE, false);
	}

	/**
	 * @see echopointng.able.Attributeable#setAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setAttribute(String attributeName, Object attributeValue) {
		if (attributeMap == null) {
			attributeMap = new HashMap();
		}
		attributeMap.put(attributeName, attributeValue);
	}

	/**
	 * Sets the color to be used as the header background
	 * 
	 * @param newValue -
	 *            the color to be used as the header background
	 */
	public void setHeaderBackground(Color newValue) {
		setProperty(PROPERTY_HEADER_BACKGROUND, newValue);
	}

	/**
	 * Sets the color to be used as the footer background
	 * 
	 * @param newValue -
	 *            the color to be used as the footer background
	 */
	public void setFooterBackground(Color newValue) {
		setProperty(PROPERTY_FOOTER_BACKGROUND, newValue);
	}

	/**
	 * Sets the height of the TableEx.
	 * 
	 * @param newValue -
	 *            the new height value
	 */
	public void setHeight(Extent newValue) {
		setProperty(PROPERTY_HEIGHT, newValue);
	}

	/**
	 * Controls whether the <code>TableEx</code> columns are resizeable. Other
	 * properties <code>resizeDragBarUsed</code> and
	 * <code>resizeGrowsTable</code> are dependant on <code>resizeable</code>
	 * being set to <code>true</code>.
	 * <p>
	 * Conversely, <code>scrollable</code> must be <code>true</code> in
	 * order for TableEx columns to be <code>resizeable</code>
	 * 
	 * @param newValue -
	 *            whether the <code>TableEx</code> is resizeable
	 */
	public void setResizeable(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_RESIZEABLE, newValue);
	}

	/**
	 * If true, then a visual visual bar artefact is used when resizing columns.
	 * If this is false then the columns themselves are resize in real time.
	 * 
	 * @param newValue -
	 *            the new boolean value
	 */
	public void setResizeDragBarUsed(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_RESIZE_DRAG_BAR_USED, newValue);
	}

	/**
	 * If true, then the table content area will grow to accomidate the new
	 * columns widths when they are resized. If false then the content area will
	 * not grow and the column sizes will be restricted to maximum values.
	 * 
	 * @param newValue -
	 *            the new boolean value
	 */
	public void setResizeGrowsTable(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_RESIZE_GROWS_TABLE, newValue);
	}

	/**
	 * Controls whether the <code>TableEx</code> is scrollable. Other
	 * properties such as <code>resizeable</code> are dependant on
	 * <code>scrollable</code> being set to <code>true</code>.
	 * 
	 * @param newValue -
	 *            whether the <code>TableEx</code> is scrollable
	 */
	public void setScrollable(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_SCROLLABLE, newValue);
	}

	/**
	 * Sets the visibility state of the table footer.
	 * 
	 * @param newValue
	 *            true if the footer should be displayed
	 */
	public void setFooterVisible(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_FOOTER_VISIBLE, newValue);
		invalidate();
	}

	/**
	 * When the Table selection is in multiple selection mode, then the Table
	 * will use the Control/Shift keys to control how selections are made. If
	 * this flag is set to true, then the meta keys are ignored and clicking on
	 * a table row will create a new selection/deselection.
	 * 
	 * @param newValue
	 *            true the meta keys are to be ignored
	 */
	public void setIgnoreMetaKeys(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_IGNORE_META_KEYS, newValue);
	}

	/**
	 * Returns the default <code>TableCellRenderer</code> used to render
	 * footer cells. The default footer renderer will be used in the event that
	 * a <code>TableColumn</code> does not provide a specific footer renderer.
	 * 
	 * @return the <code>TableCellRenderer</code>
	 */
	public TableCellRenderer getDefaultFooterRenderer() {
		return defaultFooterRenderer;
	}

	/**
	 * Sets the default <code>TableCellRenderer</code> used to render footer
	 * cells. The default footer renderer will be used in the event that a
	 * <code>TableColumn</code> does not provide a specific footer renderer.
	 * 
	 * @param newValue
	 *            the <code>TableCellRenderer</code>
	 */
	public void setDefaultFooterRenderer(TableCellRenderer newValue) {
		invalidate();
		TableCellRenderer oldValue = defaultFooterRenderer;
		defaultFooterRenderer = newValue;
		firePropertyChange(DEFAULT_FOOTER_RENDERER_CHANGED_PROPERTY, oldValue, newValue);
	}

	public void setDefaultRenderer(TableCellRenderer newValue) {
		invalidate();
		TableCellRenderer oldValue = defaultRenderer;
		defaultRenderer = newValue;
		firePropertyChange(DEFAULT_RENDERER_CHANGED_PROPERTY, oldValue, newValue);
	}

	/**
	 * @return the default TableCellRenderer that is used after all other
	 *         combinations have been exhausted. It will look in the
	 *         TableColumns first for a cell renderer, followed by the
	 *         getDefaultRenderer(by class) and then finally getDefaultRender(),
	 */
	public TableCellRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	/**
	 * @see nextapp.echo2.app.Table#validate()
	 */
	public void validate() {
		super.validate();
		while (!valid) {
			valid = true;
			doRender();
		}
	}

	/**
	 * @see echopointng.able.Stretchable#getMaximumStretchedHeight()
	 */
	public Extent getMaximumStretchedHeight() {
		return (Extent) getProperty(PROPERTY_MAXIMUM_STRETCHED_HEIGHT);
	}

	/**
	 * @see echopointng.able.Stretchable#getMinimumStretchedHeight()
	 */
	public Extent getMinimumStretchedHeight() {
		return (Extent) getProperty(PROPERTY_MINIMUM_STRETCHED_HEIGHT);
	}

	/**
	 * @see echopointng.able.Stretchable#isHeightStretched()
	 */
	public boolean isHeightStretched() {
		return ComponentEx.getProperty(this, PROPERTY_HEIGHT_STRETCHED, false);
	}

	/**
	 * <code>TableEx</code> puts a particular restriction on
	 * <code>Stretchable</code>. It will never stretch the table content to
	 * fill a space bigger than its natural height. So if you have a small set
	 * of rows in a large area, the table will not be "stretched forwards" to
	 * fill the space, unlike a normal Stretchable.
	 * 
	 * @see echopointng.able.Stretchable#setHeightStretched(boolean)
	 */
	public void setHeightStretched(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_HEIGHT_STRETCHED, newValue);
	}

	/**
	 * @see echopointng.able.Stretchable#setMaximumStretchedHeight(nextapp.echo2.app.Extent)
	 */
	public void setMaximumStretchedHeight(Extent newValue) {
		Extent.validate(newValue, Extent.PX);
		setProperty(PROPERTY_MAXIMUM_STRETCHED_HEIGHT, newValue);
	}

	/**
	 * @see echopointng.able.Stretchable#setMinimumStretchedHeight(nextapp.echo2.app.Extent)
	 */
	public void setMinimumStretchedHeight(Extent newValue) {
		Extent.validate(newValue, Extent.PX);
		setProperty(PROPERTY_MINIMUM_STRETCHED_HEIGHT, newValue);
	}
}
