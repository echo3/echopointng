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

import nextapp.echo2.webcontainer.RenderContext;

import org.w3c.dom.Node;

import echopointng.LiveTable;

/**
 * <code>LiveTableRenderer</code> is used to partially render a TableModel
 * within a LiveTable, via direct XHTML insertion of row data.
 *  
 */
public interface LiveTableRenderer {

	/**
	 * This method is called to get the XHTML for an empty row with the
	 * LiveTable. When a LiveTable row has not been loaded with data, then an
	 * empty row is shown.
	 * <p>
	 * This XHTML is a TR tag representing an empty row.
	 * <p>
	 * Each row must be the same height as all the other rows otherwise the
	 * LiveTable scrolling algorithm wont work.
	 * <p>
	 * You should use the styleNames returned in the getStyles() method on rows
	 * cells to ensure the least amount of data is sent down the client and
	 * hence UI feedback is faster.
	 * 
	 * @param liveTable -
	 *            the LiveTable in question
	 * @param parentNode -
	 *            the parentNode to place the XHTML in
	 */
	public void renderEmptyRow(LiveTable liveTable, Node parentNode);

	/**
	 * This method is called to render the XHTML for the data starting from
	 * rowStart and ending at rowEnd.
	 * <p>
	 * This XHTML is a series of TR tags for the given data.
	 * <p>
	 * Each row must be the same height as all the other rows otherwise the
	 * LiveTable scrolling algorithm wont work.
	 * <p>
	 * You should use the styleNames returned in the getStyles() method on rows
	 * cells to ensure the least amount of data is sent down the client and
	 * hence UI feedback is faster.
	 * 
	 * @param liveTable -
	 *            the LiveTable in question
	 * @param parentNode -
	 *            the parentNode to place the XHTML in
	 * @param rowStart -
	 *            the starting row of data
	 * @param rowEnd -
	 *            the ending row of data
	 */
	public void renderRows(LiveTable liveTable, Node parentNode, int rowStart, int rowEnd);

	/**
	 * This method is called to render a series of XTHML Style Elements (eg.
	 * &lt;style&gt;xxx&lt;/style&gt;) that can be used to "style" the rows of
	 * data in the LiveTable. This allows each row of XHTML to only contain
	 * "class=stylename" entries and hence save on the amount of data that is
	 * sent to the client.
	 * <p>
	 * The style names should perhaps use the renderId() of the LiveTable to
	 * ensure that they are unique.
	 * 
	 * @param liveTable -
	 *            the LiveTable in question
	 * @param parentNode -
	 *            the parentNode to place the XHTML in
	 */
	public void renderStyles(LiveTable liveTable, Node parentNode);

	/**
	 * This method will be called if rollover's are enabled on the LiveTable.
	 * <p>
	 * Th implementor should render a rollover style for a cell inside row data
	 * (eg a TD) and then return the style className that will be applied to the
	 * cell when the mouse moves over the cells in a row of data.
	 * <p>
	 * The rollover style properties should be based on the LiveTable rollover
	 * related properties.
	 * 
	 * @param liveTable -
	 *            the LiveTable in question
	 * @param parentNode -
	 *            the parentNode to place the XHTML in
	 * @param rc -
	 *            the RenderContext in place at the time this call is made
	 * @return the name of the rollover style to be placed in the cell className
	 */
	public String renderRolloverStyle(LiveTable liveTable, Node parentNode, RenderContext rc);

	/**
	 * This method will be called if selection is enabled on the LiveTable.
	 * <p>
	 * Th implementor should render a selection style for a cell inside row data
	 * (eg a TD) and then return the style className that will be applied to the
	 * cell when the selection is made on a row of data. .
	 * <p>
	 * The selection style properties should be based on the LiveTable selection
	 * related properties.
	 * 
	 * @param liveTable -
	 *            the LiveTable in question
	 * @param parentNode -
	 *            the parentNode to place the XHTML in
	 * @param rc -
	 *            the RenderContext in place at the time this call is made
	 * @return the name of the selection style to be placed in the cell
	 *         className
	 */
	public String renderSelectionStyle(LiveTable liveTable, Node parentNode, RenderContext rc);

}
