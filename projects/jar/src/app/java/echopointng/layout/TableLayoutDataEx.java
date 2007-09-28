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
package echopointng.layout;

import echopointng.table.TableCellRendererEx;
import nextapp.echo2.app.layout.TableLayoutData;

/**
 * <code>TableLayoutDataEx</code> is an extension of
 * <code>TableLayoutData</code> that allows row and column spanning by table
 * cells.
 * 
 * @see TableCellRendererEx
 * @see echopointng.TableEx
 * 
 */
public class TableLayoutDataEx extends TableLayoutData {
	private int colSpan;
	private int rowSpan;
	private String toolTipText;
	
	/**
	 * Constructs a <code>TableLayoutDataEx</code>
	 */
	public TableLayoutDataEx() {
		this(1, 1);
	}

	/**
	 * Constructs a <code>TableLayoutDataEx</code>
	 * 
	 * @param colSpan -
	 *            the number of columns that are spanned by this cell.
	 * @param rowSpan -
	 *            the number of rows that are spanned by this cell.
	 */
	public TableLayoutDataEx(int colSpan, int rowSpan) {
		setColSpan(colSpan);
		setRowSpan(rowSpan);
	}

	/**
	 * @return the number of columns that are spanned by this cell.
	 */
	public int getColSpan() {
		return colSpan;
	}

	/**
	 * @return the number of rows that are spanned by this cell.
	 */
	public int getRowSpan() {
		return rowSpan;
	}

	/**
	 * Sets the number of columns that are spanned by this cell. Less than 1 is
	 * normalised out to 1.
	 * 
	 * @param newValue -
	 *            the number of columns that are spanned by this cell.
	 */
	public void setColSpan(int newValue) {
		this.colSpan = (newValue < 1 ? 1 : newValue);
	}

	/**
	 * Sets the number of rows that are spanned by this cell. Less than 1 is
	 * normalised out to 1.
	 * 
	 * @param newValue -
	 *            the number of rows that are spanned by this cell.
	 */
	public void setRowSpan(int newValue) {
		this.rowSpan = (newValue < 1 ? 1 : newValue);
	}

    /**
     * Returns the tool tip text (displayed when the mouse cursor is hovered 
     * over the area with this layout data).
     * 
     * @return the tool tip text
     */
    public String getToolTipText() {
        return (String) toolTipText;
    }
    
    /**
     * Sets the tool tip text (displayed when the mouse cursor is hovered 
     * over the area with this layout data).
     * 
     * @param newValue the new tool tip text
     */
    public void setToolTipText(String newValue) {
    	toolTipText = newValue;
    }
	
}
