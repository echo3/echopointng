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

import nextapp.echo2.app.table.TableModel;

/**
 * A <code>TableModel</code> representing sortable data.
 * 
 * @author Jason Dalton
 */
public interface SortableTableModel extends TableModel {

	/**
	 * A sort directive to sort in ascending order
	 */
	public static final int ASCENDING = 1;

	/**
	 * A sort directive to sort in descending order
	 */
	public static final int DESCENDING = -1;

	/**
	 * A sort directive to not sort at all
	 */
	public static final int NOT_SORTED = 0;

	/**
	 * Returns the primarily sorted column number.
	 * 
	 * @return the column number.
	 */
	public int getCurrentSortColumn();

	/**
	 * Returns the sort directive for the specified column
	 * 
	 * @return retutrns on of these values
	 *         <ul>
	 *         <li>SortableTableModel.DESCENDING</li>
	 *         <li>SortableTableModel.NOT_SORTED</li>
	 *         <li>SortableTableModel.ASCEDING</li>
	 *         </ul>
	 */
	public int getSortDirective(int column);

	/**
	 * Sets th sort directive for the given TableModel column.
	 * 
	 * @param column
	 *            the column in question
	 * @param sortDirective
	 *            must be one of :
	 *            <ul>
	 *            <li>SortableTableModel.DESCENDING</li>
	 *            <li>SortableTableModel.NOT_SORTED</li>
	 *            <li>SortableTableModel.ASCEDING</li>
	 *            </ul>
	 */
	public void setSortDirective(int column, int sortDirective);

	/**
	 * Sorts the data backing this model based on the given column and sort
	 * directive
	 * 
	 * @param column
	 *            the column to sort by
	 * @param sortDirective
	 *            must be one of :
	 *            <ul>
	 *            <li>SortableTableModel.DESCENDING</li>
	 *            <li>SortableTableModel.NOT_SORTED</li>
	 *            <li>SortableTableModel.ASCEDING</li>
	 *            </ul>
	 */
	public void sortByColumn(int column, int sortDirective);

	/**
	 * This converts the sorted view row index into the equivalent unsorted model row index.
	 * When the underlying TableModel is being sorted, you can use this method
	 * to map from the sorted View set of row indexes into the underlying unsorted <code>TableModel</code> row
	 * indexes.
	 * 
	 * @param viewRowIndex -
	 *            a row index in terms of the sorted view that you want to convert to a
	 *            unsorted model row index
	 * @return a unsorted model row index
	 */
	public int toUnsortedModelRowIndex(int viewRowIndex);

	/**
	 * This converts the unsorted model row index into the equivalent sorted view row index.
	 * When the underlying TableModel is being sorted, you can use this method
	 * to map from the underlying TableModel set of row indexes into sorted View row
	 * indexes.
	 * 
	 * @param modelRowIndex -
	 *            a row index in terms of the unsorted model that you want to convert to
	 *            a sorted view row index
	 * @return a sorted view row index
	 */
	public int toSortedViewRowIndex(int modelRowIndex);
}
