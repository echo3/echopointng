/* 
 * This file is part of the Echo2 Table Extension (hereinafter "ETE").
 * Copyright (C) 2002-2005 NextApp, Inc.
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
package echopointng.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nextapp.echo2.app.event.TableModelEvent;
import nextapp.echo2.app.event.TableModelListener;
import nextapp.echo2.app.table.AbstractTableModel;
import nextapp.echo2.app.table.TableColumn;
import nextapp.echo2.app.table.TableColumnModel;
import nextapp.echo2.app.table.TableModel;

/**
 * <code>DefaultSortableTableModel</code> is a decorator for
 * <code>TableModel</code>'s; adding sorting functionality.
 * <p>
 * <code>DefaultSortableTableModel</code> does not store or copy the data in
 * its <code>TableModel</code>; instead it maintains a map from the row
 * indexes of the view to the row indexes of the model.
 * <p>
 * As requests are made of the sorter (like getValueAt(col, row)) they are
 * passed to the underlying model after the row numbers have been translated via
 * the internal mapping array.
 * <p>
 * This way, the <code>DefaultSortableTableModel</code> appears to hold
 * another copy of the table with the rows in a different order.
 * <p>
 * This also makes this decorator <code>TableModel</code> very light weight
 * and if no sorting is specified then no extra sorting information is kept.
 * <p>
 * This code has been adapted from
 * <pre>
 * http://java.sun.com/docs/books/tutorial/uiswing/components/table.html#sorting
 * http://java.sun.com/docs/books/tutorial/uiswing/components/examples/TableSorter.java
 * </pre>
 * @author David Nedde
 */
public class DefaultSortableTableModel extends AbstractTableModel implements SortableTableModel {

	/**
	 * This is the underlying TableModel being decorated by this class
	 */
	protected TableModel underlyingTableModel;

	/**
	 * You can use this comparator if the column objects impment Comparable
	 */
	public static final Comparator COMPARABLE_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			return ((Comparable) o1).compareTo(o2);
		}
	};

	/**
	 * This comparator converts the objects into Strings first and then compares
	 * them based on lexical order. It is used by default if no specific sorting
	 * Comprator can be found for a table column.
	 */
	public static final Comparator LEXICAL_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			return o1.toString().compareTo(o2.toString());
		}
	};

	private TableColumnModel columnModel;

	private SortedRow[] viewToModel;

	private int[] modelToView;

	private TableModelListener tableModelListener = new TableModelHandler();

	private Map columnComparators = new HashMap();

	/** Sorted columns status. The last element is the most recently sorted. */
	private List sortingColumns = new ArrayList();

	/**
	 * Contructs a <code>DefaultSortableTableModel</code> based on the
	 * underlying <code>TableModel</code>
	 * 
	 * @param underlyingTableModel -
	 *            the underlying and unsorted <code>TableModel</code>
	 */
	public DefaultSortableTableModel(TableModel underlyingTableModel) {
		this(underlyingTableModel, null);
	}

	/**
	 * Contructs a <code>DefaultSortableTableModel</code> based on the
	 * underlying <code>TableModel</code> and the provided
	 * <code>TableColumnModel</code>
	 * 
	 * @param underlyingTableModel -
	 *            the underlying and unsorted <code>TableModel</code>
	 * @param columnModel -
	 *            the <code>TableColumnModel</code> to use
	 */
	public DefaultSortableTableModel(TableModel underlyingTableModel, TableColumnModel columnModel) {
		setUnderlyingTableModel(underlyingTableModel);
		this.columnModel = columnModel;
	}

	public DefaultSortableTableModel(TableColumnModel columnModel) {
		this.columnModel = columnModel;
	}

	private void clearSortingState() {
		viewToModel = null;
		modelToView = null;
	}

	/**
	 * @return the underlying <code>TableModel</code>
	 */
	public TableModel getUnderlyingTableModel() {
		return underlyingTableModel;
	}

	/**
	 * Sets the underlying TableModel to use for this
	 * <code>DefaultSortableTableModel</code>.
	 * 
	 * @param newTableModel -
	 *            the new underlying <code>TableModel</code>
	 */
	public void setUnderlyingTableModel(TableModel newTableModel) {
		if (underlyingTableModel != null) {
			underlyingTableModel.removeTableModelListener(tableModelListener);
		}

		underlyingTableModel = newTableModel;
		if (underlyingTableModel != null) {
			underlyingTableModel.addTableModelListener(tableModelListener);
		}

		clearSortingState();
		fireTableStructureChanged();
	}

	private boolean isSorting() {
		return sortingColumns.size() != 0;
	}

	private Directive getDirective(int column) {
		for (Iterator iter = sortingColumns.iterator(); iter.hasNext();) {
			Directive directive = (Directive) iter.next();
			if (directive.column == column) {
				return directive;
			}
		}
		return EMPTY_DIRECTIVE;
	}

	/**
	 * @see echopointng.table.SortableTableModel#getSortDirective(int)
	 */
	public int getSortDirective(int column) {
		return getDirective(column).direction;
	}

	private void sortingStatusChanged() {
		clearSortingState();
		fireTableDataChanged();
	}

	/**
	 * @see echopointng.table.SortableTableModel#setSortDirective(int, int)
	 */
	public void setSortDirective(int column, int sortDirective) {
		Directive directive = getDirective(column);
		if (directive != EMPTY_DIRECTIVE) {
			sortingColumns.remove(directive);
		}

		if (sortDirective != NOT_SORTED) {
			sortingColumns.add(new Directive(column, sortDirective));
		}

		sortingStatusChanged();
	}

	private void cancelSorting() {
		sortingColumns.clear();
		sortingStatusChanged();
	}

	/**
	 * Colums can be sorted by Class by setting this method. If the column is a
	 * SortableTableColumn, then that classes getComparator() will take
	 * precedence. If neither are set, the values are compared using Comparable
	 * if possible, or toString() values as a last resort.
	 */
	public void setColumnComparator(Class type, Comparator comparator) {
		if (comparator == null) {
			columnComparators.remove(type);
		} else {
			columnComparators.put(type, comparator);
		}
	}

	/**
	 * Called to get a Comparator for a given column. A default algorithmn is
	 * used that looks for a SortableTableColumn to specify the Comprator and
	 * failing that if the TableModel column class is assignable from
	 * Comparable, that is used and faling that the LEXICAL_COMPRATOR is used.
	 * 
	 * @param column -
	 *            the column in question
	 * @return a <code>Comparator</code> to use to sort the column
	 */
	protected Comparator getComparator(int column) {
		Class columnType = null;
		if (underlyingTableModel != null) {
			columnType = underlyingTableModel.getColumnClass(column);
		}
		Comparator comparator = (Comparator) columnComparators.get(columnType);

		if (comparator == null && columnModel != null) {
			TableColumn tableColumn = columnModel.getColumn(column);
			if (tableColumn instanceof SortableTableColumn) {
				comparator = ((SortableTableColumn) tableColumn).getComparator();
				if (comparator.equals(SortableTableColumn.DEFAULT_COMPARATOR))
					comparator = null; // we want a more sophisticated default
			}
		}

		if (comparator == null) {
			if (Comparable.class.isAssignableFrom(columnType))
				comparator = COMPARABLE_COMPARATOR;
			else
				comparator = LEXICAL_COMPARATOR;
		}
		return comparator;
	}

	private SortedRow[] getViewToModelSortedRows() {
		if (viewToModel == null) {
			int tableModelRowCount = 0;
			if (underlyingTableModel != null) {
				tableModelRowCount = underlyingTableModel.getRowCount();
			}
			viewToModel = new SortedRow[tableModelRowCount];
			for (int row = 0; row < tableModelRowCount; row++) {
				viewToModel[row] = new SortedRow(row);
			}

			if (isSorting()) {
				Arrays.sort(viewToModel);
			}
		}
		return viewToModel;
	}

	/**
	 * @see echopointng.table.SortableTableModel#toUnsortedModelRowIndex(int)
	 */
	public int toUnsortedModelRowIndex(int viewRowIndex) {
		if (viewRowIndex == -1) {
			return -1; // ROW_OBJECT
		} else {
			SortedRow viewToModel[] = getViewToModelSortedRows();
			if (viewToModel != null && viewRowIndex < viewToModel.length) {
				return viewToModel[viewRowIndex].modelIndex;
			}
			return viewRowIndex;
		}
	}

	private int[] getModelToViewSortedRows() {
		if (modelToView == null) {
			int n = getViewToModelSortedRows().length;
			modelToView = new int[n];
			for (int i = 0; i < n; i++) {
				modelToView[toUnsortedModelRowIndex(i)] = i;
			}
		}
		return modelToView;
	}

	/**
	 * @see echopointng.table.SortableTableModel#toSortedViewRowIndex(int)
	 */
	public int toSortedViewRowIndex(int modelRowIndex) {
		int modelToView[] = getModelToViewSortedRows();
		if (modelToView != null && modelRowIndex < modelToView.length) {
			return modelToView[modelRowIndex];
		}
		return modelRowIndex;
	}

	// TableModel interface methods

	public int getRowCount() {
		return (underlyingTableModel == null) ? 0 : underlyingTableModel.getRowCount();
	}

	public int getColumnCount() {
		return (underlyingTableModel == null) ? 0 : underlyingTableModel.getColumnCount();
	}

	public String getColumnName(int column) {
		return (underlyingTableModel == null) ? null : underlyingTableModel.getColumnName(column);
	}

	public Class getColumnClass(int column) {
		return (underlyingTableModel == null) ? null : underlyingTableModel.getColumnClass(column);
	}

	public Object getValueAt(int column, int row) {
		return (underlyingTableModel == null) ? null : underlyingTableModel.getValueAt(column, toUnsortedModelRowIndex(row));
	}

	// SortableTableModel interface methods
	/**
	 * @see echopointng.table.SortableTableModel#sortByColumn(int, int)
	 */
	public void sortByColumn(int column, int sortDirective) {
		setSortDirective(column, sortDirective);
	}

	/**
	 * @see echopointng.table.SortableTableModel#getCurrentSortColumn()
	 */
	public int getCurrentSortColumn() {
		int sortColumn = -1;

		if (sortingColumns.size() > 0) {
			Directive directive = (Directive) sortingColumns.get(sortingColumns.size() - 1);
			sortColumn = directive.column;
		}

		return sortColumn;
	}

	// Helper classes

	private class SortedRow implements Comparable {
		private int modelIndex;

		public SortedRow(int index) {
			this.modelIndex = index;
		}

		public int compareTo(Object o) {
			int row1 = modelIndex;
			int row2 = ((SortedRow) o).modelIndex;

			int tableColCount = (underlyingTableModel == null) ? -1 : underlyingTableModel.getColumnCount();
			for (int i = sortingColumns.size() - 1; i >= 0; i--) {
				Directive directive = (Directive) sortingColumns.get(i);

				int column = directive.column;
				if (column >= 0 && column < tableColCount) {
					Object o1 = underlyingTableModel.getValueAt(column, row1);
					Object o2 = underlyingTableModel.getValueAt(column, row2);

					int comparison = 0;
					// Define null less than everything, except null.
					if (o1 == null && o2 == null) {
						comparison = 0;
					} else if (o1 == null) {
						comparison = -1;
					} else if (o2 == null) {
						comparison = 1;
					} else {
						comparison = getComparator(column).compare(o1, o2);
					}

					if (comparison != 0) {
						return directive.direction == DESCENDING ? -comparison : comparison;
					}
				}
			}
			return 0;
		}
	}

	private class TableModelHandler implements TableModelListener {
		public void tableChanged(TableModelEvent e) {
			// If we're not sorting by anything, just pass the event along.
			if (!isSorting()) {
				clearSortingState();
				fireTableChanged(e);
				return;
			}

			// If the table structure has changed, cancel the sorting; the
			// sorting columns may have been either moved or deleted from
			// the model.
			if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
				cancelSorting();
				fireTableChanged(e);
				return;
			}

			// We can map a cell event through to the view without widening
			// when the following conditions apply:
			//
			// a) all the changes are on one row (e.getFirstRow() ==
			// e.getLastRow()) and,
			// b) all the changes are in one column (column !=
			// TableModelEvent.ALL_COLUMNS) and,
			// c) we are not sorting on that column (getSortingStatus(column) ==
			// NOT_SORTED) and,
			// d) a reverse lookup will not trigger a sort (modelToView != null)
			//
			// Note: INSERT and DELETE events fail this test as they have column
			// == ALL_COLUMNS.
			//
			// The last check, for (modelToView != null) is to see if
			// modelToView
			// is already allocated. If we don't do this check; sorting can
			// become
			// a performance bottleneck for applications where cells
			// change rapidly in different parts of the table. If cells
			// change alternately in the sorting column and then outside of
			// it this class can end up re-sorting on alternate cell updates -
			// which can be a performance problem for large tables. The last
			// clause avoids this problem.
			int column = e.getColumn();
			int sortStatus = getSortDirective(column);
			if (e.getFirstRow() == e.getLastRow() && column != TableModelEvent.ALL_COLUMNS && sortStatus == NOT_SORTED && modelToView != null) {
				int viewIndex = getModelToViewSortedRows()[e.getFirstRow()];
				fireTableChanged(new TableModelEvent(DefaultSortableTableModel.this, viewIndex, viewIndex, column, e.getType()));
				return;
			}

			// Something has happened to the data that may have invalidated the
			// row order.
			clearSortingState();
			fireTableDataChanged();
			return;
		}
	}

	private static Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);

	private static class Directive {
		private int column;

		private int direction;

		public Directive(int column, int direction) {
			this.column = column;
			this.direction = direction;
		}
	}
}