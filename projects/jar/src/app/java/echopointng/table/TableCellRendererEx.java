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

import echopointng.xhtml.XhtmlFragment;
import nextapp.echo2.app.Table;
import nextapp.echo2.app.table.TableCellRenderer;

/**
 * <code>TableCellRendererEx</code> is an extension of
 * <code>TableCellRenderer</code> that allows cell content other than
 * <code>Component</code>s to be expressed as well as allowing row and column
 * spanning by table cells.
 * <p>
 * This is acheived by the <code>TableCellRendererEx</code> returning
 * <code>TableLayoutDataEx</code> objects that can describe the content and
 * visual appearance of the table cell.
 * <p>
 * Using strings for cell contents results in less memory being used because a
 * Component need not be stored per table cell.
 * <p>
 * It is expected that uses of this class such as TableEx will call the
 * <code>TableCellRenderer.getTableCellRendererComponent()</code> method first
 * and if it returns null then the
 * <code>TableCellRendererEx.getTableCellRendererLayout()</code> method will
 * then be called.
 * 
 * 
 * @see echopointng.layout.TableLayoutDataEx
 * @see echopointng.TableEx
 * 
 */
public interface TableCellRendererEx extends TableCellRenderer {

	/**
	 * Returns a <code>XhtmlFragment</code> that will be displayed as the
	 * content at the specified co'ordinate in the table.
	 * 
	 * @param table
	 *            the <code>Table</code> for which the rendering is occurring
	 * @param value
	 *            the value retrieved from the <code>TableModel</code> for the
	 *            specified coordinate
	 * @param column
	 *            the column index to render
	 * @param row
	 *            the row index to render
	 * @return a <code>XhtmlFragment</code> representation of the value
	 */
	public XhtmlFragment getTableCellRendererContent(Table table, Object value, int column, int row);

	/**
	 * This method allows you to "restrict" the cells (within a row) that will
	 * cause selection of the row to occur. By default any cell will cause
	 * selection of a row. If this methods returns false then only certain cells
	 * within the row will cause selection when clicked on.
	 * <p>
	 * 
	 * @param table -
	 *            the Table in question
	 * @param column -
	 *            the column in question
	 * @param row -
	 *            the row in quesiton
	 * @return - true if the cell causes selection
	 */
	public boolean isSelectionCausingCell(Table table, int column, int row);

	/**
	 * This method is called to determine which cells within a row can cause an
	 * action to be raised on the server when clicked.
	 * <p>
	 * By default if a Table has attached actionListeners then any click on any
	 * cell wihtin a row will cause the action to fire.
	 * <p>
	 * This method allows this to be overrriden and only certain cells within a
	 * row can cause an action event to be raise.
	 * 
	 * @param table -
	 *            the Table in question
	 * @param column -
	 *            the column in question
	 * @param row -
	 *            the row in quesiton
	 * @return - Return true means that the cell can cause actions while false
	 *         means the cells can not cause action events.
	 */
	public boolean isActionCausingCell(Table table, int column, int row);
}
