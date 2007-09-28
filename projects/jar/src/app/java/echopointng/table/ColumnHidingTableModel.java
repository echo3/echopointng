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
package echopointng.table;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nextapp.echo2.app.Table;
import nextapp.echo2.app.event.TableModelEvent;
import nextapp.echo2.app.event.TableModelListener;
import nextapp.echo2.app.table.AbstractTableModel;
import nextapp.echo2.app.table.TableModel;

/**
 * <code>ColumnHidingTableModel</code> is a wrapper {@link TableModel} that
 * can allow individual columns to be hidden and hence logical removed from the
 * {@link TableModel}.
 * <p>
 * Each time a column is hidden, the getColumnCount() will return 1 less to
 * account for the hidden column.
 * <p>
 * The user of this {@link TableModel} (eg the {@link Table}) will not be aware
 * that any extra columns have been hidden.
 * 
 */
public class ColumnHidingTableModel extends AbstractTableModel {
	/**
	 * Used to forward table model events upwards and onwards to the users of
	 * this TableModel. That way we dont "mask" out events that might occur in
	 * the underlying {@link TableModel}
	 */
	private TableModelListener forwarderModelListener = new TableModelListener() {
		public void tableChanged(TableModelEvent e) {
			TableModelEvent newEvent = new TableModelEvent(ColumnHidingTableModel.this, mapColumnToUnderlyingModel(e.getColumn()), e.getFirstRow(), e
					.getLastRow(), e.getType());
			fireTableChanged(newEvent);
		};
	};

	private TableModel underlyingTableModel;

	private Set hiddenColumnsSet;

	/**
	 * Constructs a <code>TestTable.ColumnHidingTableModel</code>
	 */
	public ColumnHidingTableModel(TableModel underlyingTableModel) {
		if (underlyingTableModel == null) {
			throw new IllegalArgumentException("The underlying TableModel must not be null");
		}
		this.underlyingTableModel = underlyingTableModel;
		underlyingTableModel.addTableModelListener(forwarderModelListener);
		this.hiddenColumnsSet = new HashSet();
	}

	/**
	 * @return the underlying TableModel
	 */
	public TableModel getUnderlyingTableModel() {
		return underlyingTableModel;
	}

	/**
	 * @see nextapp.echo2.app.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		int baseColumnCount = underlyingTableModel.getColumnCount();
		int hiddenColumnsCount = hiddenColumnsSet.size();
		return baseColumnCount - hiddenColumnsCount;
	}

	/**
	 * @see nextapp.echo2.app.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int column, int row) {
		int mappedColumn = mapColumnToUnderlyingModel(column);
		return underlyingTableModel.getValueAt(mappedColumn, row);
	}

	/**
	 * @see nextapp.echo2.app.table.AbstractTableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int column) {
		int mappedColumn = mapColumnToUnderlyingModel(column);
		return super.getColumnClass(mappedColumn);
	}

	/**
	 * @see nextapp.echo2.app.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		int mappedColumn = mapColumnToUnderlyingModel(column);
		return super.getColumnName(mappedColumn);
	}

	/**
	 * Called to map a column from hidden space into wrapped model space.
	 */
	private int mapColumnToUnderlyingModel(int column) {
		// we need to map the column into a column on the wrapped model, We
		// can work this out
		// by counting the number of hidden columns up to the specified
		// column.
		int offset = 0;
		int matchColumn = 0;
		int baseColumnCount = underlyingTableModel.getColumnCount();
		for (int i = 0; i < baseColumnCount; i++) {
			if (isHiddenColumn(i)) {
				offset++;
			} else {
				matchColumn++;
				if (matchColumn > column) {
					break;
				}
			}
		}
		int mappedColumn = column + offset;
		return mappedColumn;
	}

	/**
	 * @see nextapp.echo2.app.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return underlyingTableModel.getRowCount();
	}

	/**
	 * @return an array of hidden columns. The column values here are in terms
	 *         of the underlying {@link TableModel}.
	 */
	public int[] getHiddenColumns() {
		int[] hiddenColumnsArr = new int[hiddenColumnsSet.size()];
		int index = 0;
		for (Iterator iter = hiddenColumnsSet.iterator(); iter.hasNext();) {
			hiddenColumnsArr[index++] = ((Integer) iter.next()).intValue();
		}
		Arrays.sort(hiddenColumnsArr);
		return hiddenColumnsArr;
	}

	/**
	 * Returns true if the specified column is hidden. The column value here is
	 * in terms of the underlying {@link TableModel}.
	 * 
	 * @param column -
	 *            the column in question (in terms of the underlying TableModel)
	 * @return true if the specified column is hidden.
	 */
	public boolean isHiddenColumn(int column) {
		return hiddenColumnsSet.contains(new Integer(column));
	}

	/**
	 * Hides the specified column. The column value here is in terms of the
	 * underlying {@link TableModel}.
	 * 
	 * @param column -
	 *            the column in question (in terms of the underlying TableModel)
	 */
	public void hideColumn(int column) {
		hideColumnImpl(column, true);
	}

	/**
	 * Shows the specified column. The column value here is in terms of the
	 * underlying {@link TableModel}.
	 * 
	 * @param column -
	 *            the column in question (in terms of the underlying TableModel)
	 */

	public void showColumn(int column) {
		hideColumnImpl(column, false);
	}

	/**
	 * Toggles the specified column, ie if its hidden it is shown and vice
	 * versa. The column value here is in terms of the underlying
	 * {@link TableModel}.
	 * 
	 * @param column -
	 *            the column in question (in terms of the underlying TableModel)
	 */

	public void toggleColumn(int column) {
		hideColumnImpl(column, !isHiddenColumn(column));
	}

	/*
	 * Does the actual hiding and showing
	 */
	private void hideColumnImpl(int column, boolean hideFlag) {
		int maxColumn = underlyingTableModel.getColumnCount();
		if (column < 0 || column >= maxColumn) {
			throw new IllegalArgumentException("The column index must be >= 0 || < " + maxColumn);
		}
		if (hideFlag) {
			hiddenColumnsSet.add(new Integer(column));
		} else {
			hiddenColumnsSet.remove(new Integer(column));
		}
		fireTableStructureChanged();
	}

	/**
	 * This clears shows all columns in the underlying {@link TableModel}
	 */
	public void showAllColumns() {
		this.hiddenColumnsSet = new HashSet();
		fireTableStructureChanged();
	}
}
