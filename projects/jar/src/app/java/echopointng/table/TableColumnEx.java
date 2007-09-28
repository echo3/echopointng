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

import nextapp.echo2.app.Extent;
import nextapp.echo2.app.table.TableCellRenderer;
import nextapp.echo2.app.table.TableColumn;

/**
 * The TableColumnEx uses TableCellRendererEx by default and provides extra
 * construction objects as well as footer support.
 * 
 */
public class TableColumnEx extends TableColumn {

	/**
	 * This is the default <code>TableCellRendererEx</code> that is used by
	 * this class.
	 */
	public static final TableCellRendererEx DEFAULT_CELL_RENDERER = new DefaultTableCellRendererEx();

    public static final String FOOTER_RENDERER_CHANGED_PROPERTY = "footerRenderer";
    public static final String FOOTER_VALUE_CHANGED_PROPERTY = "footerValue";
    
    protected TableCellRenderer footerRenderer;
    protected Object footerValue;
	
	/**
	 * @see TableColumn#TableColumn(int)
	 */
	public TableColumnEx(int modelIndex) {
		this(modelIndex, null);
	}

	/**
	 * Constructs a <code>TableColumnEx</code> with the specified model index
	 * and the specified Header value.
	 * 
	 * @param modelIndex -
	 *            the model index to use
	 * @param headerValue -
	 *            the header value
	 */
	public TableColumnEx(int modelIndex, Object headerValue) {
		this(modelIndex, null, headerValue, DEFAULT_CELL_RENDERER, DEFAULT_CELL_RENDERER);
	}

	/**
	 * Constructs a <code>TableColumnEx</code> with the specified model index
	 * and the specified Header and Footer value.
	 * 
	 * @param modelIndex -
	 *            the model index to use
	 * @param headerValue -
	 *            the header value
	 * @param footerValue -
	 *            the footer value
	 */
	public TableColumnEx(int modelIndex, Object headerValue, Object footerValue) {
		this(modelIndex, null, headerValue, footerValue, DEFAULT_CELL_RENDERER, DEFAULT_CELL_RENDERER);
	}
	
	/**
	 * @see TableColumn#TableColumn(int, Extent)
	 */
	public TableColumnEx(int modelIndex, Extent width) {
		this(modelIndex, width, DEFAULT_CELL_RENDERER, DEFAULT_CELL_RENDERER);
	}

	/**
	 * @see TableColumn#TableColumn(int, Extent, TableCellRenderer,
	 *      TableCellRenderer)
	 */
	public TableColumnEx(int modelIndex, Extent width, TableCellRenderer cellRenderer, TableCellRenderer headerRenderer) {
		this(modelIndex, width, null, cellRenderer, headerRenderer);
	}

	/**
	 * Creates a <code>TableColumnEx</code> with the specified model index,
	 * width, headervalue, and cell and header renderers.
	 * 
	 * @param modelIndex
	 *            the column index of model data visualized by this column
	 * @param width
	 *            the column width
	 * @param headerValue -
	 *            the headerValue to use for this column
	 * @param cellRenderer
	 *            the renderer to use for rendering model values
	 * @param headerRenderer
	 *            the renderer to use for rendering the header cell
	 */
	public TableColumnEx(int modelIndex, Extent width, Object headerValue, TableCellRenderer cellRenderer, TableCellRenderer headerRenderer) {
		this(modelIndex, width, headerValue, null, cellRenderer, headerRenderer);
	}

	/**
	 * Creates a <code>TableColumnEx</code> with the specified model index,
	 * width, headervalue, footervalue, and cell and header renderers.
	 * 
	 * @param modelIndex
	 *            the column index of model data visualized by this column
	 * @param width
	 *            the column width
	 * @param headerValue -
	 *            the headerValue to use for this column
	 * @param footerValue -
	 *            the footerValue to use for this column
	 * @param cellRenderer
	 *            the renderer to use for rendering model values
	 * @param headerRenderer
	 *            the renderer to use for rendering the header cell
	 */
	public TableColumnEx(int modelIndex, Extent width, Object headerValue, Object footerValue, TableCellRenderer cellRenderer, TableCellRenderer headerRenderer) {
		super(modelIndex, width, cellRenderer, headerRenderer);
		setHeaderValue(headerValue);
		setFooterValue(footerValue);
	}
	
	/**
	 * Returns the <code>TableCellRenderer</code> used to render the
	 * footer cell of this column.  The value of this property may be null,
	 * in which case the table should revert to using its default cell
	 * renderer.
	 *
	 * @return the footer cell renderer for this column
	 */
	public TableCellRenderer getFooterRenderer() {
	    return footerRenderer;
	}

	/** 
	 * Returns the footer value for this column.  The footer value is the 
	 * object that will be provided to the footer renderer to produce
	 * a component that will be used as the table footer for this column.
	 *
	 * @return the footer value for this column
	 */
	public Object getFooterValue() {
	    return footerValue;
	}

	/** 
	 * Sets the <code>TableCellRenderer</code> used to render the
	 * footer cell of this column.  The value of this property may be null,
	 * in which case the table should revert to using its default cell
	 * renderer.
	 *
	 * @param newValue the new footer cell renderer
	 */
	public void setFooterRenderer(TableCellRenderer newValue) {
	    //TableCellRenderer oldValue = footerRenderer;
	    footerRenderer = newValue;
	    //pcs.firePropertyChange(FOOTER_RENDERER_CHANGED_PROPERTY, oldValue, newValue);
	}

	/** 
	 * Sets the footer value for this column.  The footer value is the 
	 * object that will be provided to the footer renderer to produce
	 * a component that will be used as the table footer for this column.
	 *
	 * @param newValue the new footer value
	 */
	public void setFooterValue(Object newValue) {
	    //Object oldValue = footerValue;
	    footerValue = newValue;
	    //pcs.firePropertyChange(FOOTER_VALUE_CHANGED_PROPERTY, oldValue, newValue);
	}

}
