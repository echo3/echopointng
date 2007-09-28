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

import nextapp.echo2.app.table.TableColumnModel;
import nextapp.echo2.app.table.TableModel;

/**
 * The default implementation for the <code>PageableTableModel</code>
 * interface, this TableModel is an extension of
 * <code>DefaultSortableTableModel</code> and is a decorator
 * <code>TableModel</code> that allows the underlying data to be both <b>paged</b>
 * and <b>sorted</b>.
 * <p>
 * 
 * @author Jason Dalton then modified by Brad Baker
 * 
 */
public class DefaultPageableSortableTableModel extends DefaultSortableTableModel implements PageableTableModel {

	/**
	 * This is the default number of rows per page.
	 */
	public static final int DEFAULT_ROWS_PER_PAGE = 50;

	/**
	 * This is the default initial page index
	 */
	public static final int DEFAULT_INITAL_PAGE_INDEX = 0;

	private int currentPage = DEFAULT_INITAL_PAGE_INDEX;

	private int rowsPerPage = DEFAULT_ROWS_PER_PAGE;

	/**
	 * Constructs a <code>DefaultPageableSortableTableModel</code> with the
	 * specified <code>TableColunModel</code>
	 * 
	 * @param columnModel -
	 *            the <code>TableColunModel</code> to use
	 */
	public DefaultPageableSortableTableModel(TableColumnModel columnModel) {
		super(columnModel);
	}

	/**
	 * Constructs a <code>DefaultPageableSortableTableModel</code> which
	 * decorates the specified <code>TableModel</code> with the paging
	 * capability.
	 * 
	 * @param underlyingTableModel -
	 *            the underlying <code>TableModel</code>
	 * @param columnModel -
	 *            the TableColumModel to use
	 */
	public DefaultPageableSortableTableModel(TableModel underlyingTableModel, TableColumnModel columnModel) {
		super(underlyingTableModel, columnModel);
	}

	/**
	 * Constructs a <code>DefaultPageableSortableTableModel</code> which
	 * decorates the specified <code>TableModel</code> with the paging
	 * capability.
	 * 
	 * @param underlyingTableModel -
	 *            the underlying <code>TableModel</code>
	 */
	public DefaultPageableSortableTableModel(TableModel underlyingTableModel) {
		super(underlyingTableModel);
	}

	/**
	 * @see echopointng.table.PageableTableModel#toUnpagedModelRowIndex(int)
	 */
	public int toUnpagedModelRowIndex(int viewRowIndex) {
		int normalizedRow = currentPage * rowsPerPage + viewRowIndex;
		return normalizedRow;
	}

	/**
	 * @see echopointng.table.PageableTableModel#toPagedViewRowIndex(int)
	 */
	public int toPagedViewRowIndex(int modelRowIndex) {
		int normalizedRow = modelRowIndex - (currentPage * rowsPerPage);
		return normalizedRow;
	}

	/**
	 * This returns a cell value at the specified column and <i>paged</i> row
	 * index. Remember the row index must be in the "paged view" context not the
	 * "unpaged model" index.
	 * 
	 * @see nextapp.echo2.app.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int column, int row) {
		int normalizedRow = toUnpagedModelRowIndex(row);
		// call the sortable getValueAt!
		return super.getValueAt(column, normalizedRow);
	}

	/**
	 * Returns the number of rows in the current <code>paged</code> view of
	 * the underlying <code>TableModel</code>
	 * 
	 * @see nextapp.echo2.app.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		int underlyingRowCount = (underlyingTableModel == null) ? 0 : underlyingTableModel.getRowCount();
		int viewableCount = underlyingRowCount - (currentPage * rowsPerPage);
		if (rowsPerPage > viewableCount) {
			return viewableCount;
		} else {
			return rowsPerPage;
		}
	}

	/**
	 * @see echopointng.table.PageableTableModel#setCurrentPage(int)
	 */
	public void setCurrentPage(int page) {
		int totalPages = getTotalPages();
		if (page < 0 || page >= totalPages) {
			throw new IllegalArgumentException("page must be > 0 and < getTotalPages() : " + page + " totalPages : " + totalPages);
		}
		currentPage = page;
		fireTableDataChanged();
	}

	/**
	 * @see echopointng.table.PageableTableModel#getCurrentPage()
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @see echopointng.table.PageableTableModel#setRowsPerPage(int)
	 */
	public void setRowsPerPage(int rows) {
		if (rows <= 0) {
			throw new IllegalArgumentException("rows must be greater than 0");
		}
		rowsPerPage = rows;
		// check current page again now with this new rows per page as it may
		// no longer be valid. Move it to the higest valid page if its now too
		// big
		int totalPages = getTotalPages();
		if (this.currentPage > totalPages) {
			int newPage = totalPages - 1;
			setCurrentPage(newPage < 0 ? 0 : newPage);
		}
		fireTableDataChanged();
	}

	/**
	 * @see echopointng.table.PageableTableModel#getRowsPerPage()
	 */
	public int getRowsPerPage() {
		return rowsPerPage;
	}

	/**
	 * @see echopointng.table.PageableTableModel#getTotalRows()
	 */
	public int getTotalRows() {
		return (underlyingTableModel == null) ? 0 : underlyingTableModel.getRowCount();
	}

	/**
	 * @see echopointng.table.PageableTableModel#getTotalPages()
	 */
	public int getTotalPages() {
		int totalRows = getTotalRows();
		int rowsPerPage = getRowsPerPage();
		if (rowsPerPage <= 0) {
			return 1; // avoid a divide by zero bug
		}
		int totalPages = totalRows / rowsPerPage;
		if ((totalRows % rowsPerPage != 0) || totalRows == 0) {
			totalPages += 1;
		}
		return totalPages;
	}
}
