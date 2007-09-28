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

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.util.DomUtil;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.propertyrender.BorderRender;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webrender.output.CssStyle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.LiveTable;
import echopointng.able.Borderable;
import echopointng.ui.util.Render;

/**
 * <code>DefaultLiveTableRenderer</code> is a default implementation for
 * rendering the rows in LiveTable.
 * <p>
 * This class uses the folllowing styles names when rendering
 * the styles for the LiveTable data.  The names are intentionally
 * kept short to minmise the traffic between the server and client.
 * <ul>
 * <li>"s" + liveTable.getRenderId() - for a row and cell of table data  - eg. s1234</li>
 * <li>"se" + liveTable.getRenderId() - for a row and cell of empty table data  - eg. se1234</li>
 * <li>"sr" + liveTable.getRenderId() - for a cell rollover style  - eg. sr1234</li>
 * <li>"ss" + liveTable.getRenderId() - for a cell selection style  - eg. ss1234</li>
 * </ul>
 * <p>
 * A <code>tr.sNNNN</code> CSS rule and a <code>tr.seNNNN</code> CSS rule is generated for rows of
 * table data and the empty row of table data.
 * <p>
 * A <code>td.sNNNN</code> CSS rule and a <code>td.seNNNN</code> CSS rule is generated for each cell within
 * a row of data.
 * <p>
 * A <code>td.srNNNN</code> CSS rule is generated to perform rollovers.
 * <p>
 * A <code>td.ssNNNN</code> CSS rule is generated to perform selection.
 * 
 * @see echopointng.table.LiveTableRenderer
 */

public class DefaultLiveTableRenderer implements LiveTableRenderer {

	/**
	 * This contains the entityt numeric value for &nbsp;
	 */
	protected static String NBSP = "\u00a0";

	/**
	 * @see echopointng.table.LiveTableRenderer#renderEmptyRow(echopointng.LiveTable,
	 *      org.w3c.dom.Node)
	 */
	public void renderEmptyRow(LiveTable liveTable, Node parentNode) {
		Document doc = parentNode.getOwnerDocument();

		Element tr = doc.createElement("tr");
		parentNode.appendChild(tr);
		tr.setAttribute("class", "se" + liveTable.getRenderId());

		int cellCount = liveTable.getModel().getColumnCount();
		for (int i = 0; i < cellCount; i++) {
			Element td = doc.createElement("td");
			tr.appendChild(td);
			td.setAttribute("class", "se" + liveTable.getRenderId());
			td.appendChild(doc.createTextNode(NBSP));
		}
	}

	/**
	 * 
	 */
	public void renderRows(LiveTable liveTable, Node parentNode, int rowStart, int rowEnd) {
		Document doc = parentNode.getOwnerDocument();

		for (int row = rowStart; row < rowEnd; row++) {
			Element trE = doc.createElement("tr");
			parentNode.appendChild(trE);
			trE.setAttribute("class", "s" + liveTable.getRenderId());

			int cellCount = liveTable.getModel().getColumnCount();
			for (int column = 0; column < cellCount; column++) {
				Element tdE = doc.createElement("td");
				trE.appendChild(tdE);
				tdE.setAttribute("class", "s" + liveTable.getRenderId());
				renderRowCell(liveTable,tdE,column,row);
			}
		}
	}

	/**
	 * This method is called to render a cell within a given row. You can
	 * override this method to provide more customised output than the
	 * DefaultLiveTableRenderer, which simply gets the String.valueOf() the
	 * TableModel value.
	 * <p>
	 * One thing you could possibly do is put &lt;a&gt; tags into the output,
	 * which would allow the user to click on some given row data. You can use a
	 * construct like this to raise action events back on the LiveTable.
	 * <pre>
	 *    		&lt;a href=&quot;javascript:EPLiveTable.href('c_1234','actionValue')&quot;&gt;Click Me To Raise An ActionEvent&lt;/a&gt;	
	 * </pre>
	 * 
	 * @param liveTable -
	 *            the LiveTable in question
	 * @param parentTD -
	 *            the parent TD element to place the XHTML in
	 * @param column -
	 *            the current column
	 * @param row -
	 *            the current row
	 */
	protected void renderRowCell(LiveTable liveTable, Element parentTD, int column, int row) {
		Document doc = parentTD.getOwnerDocument();
		Object value = liveTable.getModel().getValueAt(column, row);
		Node textNode = doc.createTextNode(String.valueOf(value));
		parentTD.appendChild(textNode);
	}

	/**
	 * @see echopointng.table.LiveTableRenderer#renderStyles(echopointng.LiveTable,
	 *      org.w3c.dom.Node)
	 */
	public void renderStyles(LiveTable liveTable, Node parentNode) {
		Document doc = parentNode.getOwnerDocument();
		Element styleE;
		String styleText;
		String styleName;

		Extent rowHeight = (Extent) EPNG.getRP(liveTable,LiveTable.PROPERTY_ROW_HEIGHT);
		Border border = (Border) EPNG.getRP(liveTable,Borderable.PROPERTY_BORDER);
		
		String heightCss = "height:" + ExtentRender.renderCssAttributeValue(rowHeight) + ";";
		String borderCss = "";
		if (border != null && border.getColor() != null) {
			borderCss = "border:" + BorderRender.renderCssAttributeValue(border) + ";";
		}
		String extraCss = "white-space:nowrap;";
		//
		// NOTE : we use very small style names to minmize the bytes down the wire
		// since each TR and TD will have a className="xxx" on it and the more we
		// can reduce this the better.
		//
		
		// -----------------------
		// row style
		styleName = "tr.s" + liveTable.getRenderId();
		styleText = styleName + " {" + heightCss + "}";
		styleE = doc.createElement("style");
		DomUtil.setElementText(styleE, styleText);
		parentNode.appendChild(styleE);

		// -----------------------
		// row cell style
		styleName = "td.s" + liveTable.getRenderId();
		styleText = styleName + " {" + heightCss + borderCss + extraCss + "}";
		styleE = doc.createElement("style");
		DomUtil.setElementText(styleE, styleText);
		parentNode.appendChild(styleE);

		// -----------------------
		// empty row style
		styleName = "tr.se" + liveTable.getRenderId();
		styleText = styleName + " {" + heightCss + "}";
		styleE = doc.createElement("style");
		DomUtil.setElementText(styleE, styleText);
		parentNode.appendChild(styleE);

		// -----------------------
		// empty row cell style
		styleName = "td.se" + liveTable.getRenderId();
		styleText = styleName + " {" + heightCss + borderCss + extraCss + "}";
		styleE = doc.createElement("style");
		DomUtil.setElementText(styleE, styleText);
		parentNode.appendChild(styleE);
	}
	
	/**
	 * @see echopointng.table.LiveTableRenderer#renderRolloverStyle(LiveTable, Node, RenderContext)
	 */
	public String renderRolloverStyle(LiveTable liveTable, Node parentNode, RenderContext rc) {
		Document doc = parentNode.getOwnerDocument();

		Style fallbackStyle = EPNG.getFallBackStyle(liveTable);
		
        CssStyle rolloverCssStyle = new CssStyle();
        Render.asColors(rolloverCssStyle,liveTable, LiveTable.PROPERTY_ROLLOVER_BACKGROUND, LiveTable.PROPERTY_ROLLOVER_FOREGROUND,fallbackStyle);
        Render.asFont(rolloverCssStyle, liveTable, LiveTable.PROPERTY_ROLLOVER_FONT, fallbackStyle);
        Render.asFillImage(rolloverCssStyle, liveTable, LiveTable.PROPERTY_ROLLOVER_BACKGROUND_IMAGE, fallbackStyle, rc);
      		
		String styleName = "sr" + liveTable.getRenderId();
		String styleText = "td." + styleName + " {" + rolloverCssStyle.renderInline() + "}";
		Element styleE = doc.createElement("style");
		DomUtil.setElementText(styleE, styleText);
		parentNode.appendChild(styleE);
		return styleName;
	}
	
	/**
	 * @see echopointng.table.LiveTableRenderer#renderSelectionStyle(LiveTable, Node, RenderContext)
	 */
	public String renderSelectionStyle(LiveTable liveTable, Node parentNode, RenderContext rc) {
		Document doc = parentNode.getOwnerDocument();

		Style fallbackStyle = EPNG.getFallBackStyle(liveTable);
		
        CssStyle rolloverCssStyle = new CssStyle();
        Render.asColors(rolloverCssStyle,liveTable, LiveTable.PROPERTY_SELECTION_BACKGROUND, LiveTable.PROPERTY_SELECTION_FOREGROUND,fallbackStyle);
        Render.asFont(rolloverCssStyle, liveTable, LiveTable.PROPERTY_SELECTION_FONT, fallbackStyle);
        Render.asFillImage(rolloverCssStyle, liveTable, LiveTable.PROPERTY_SELECTION_BACKGROUND_IMAGE, fallbackStyle, rc);
      		
		String styleName = "ss" + liveTable.getRenderId();
		String styleText = "td." + styleName + " {" + rolloverCssStyle.renderInline() + "}";
		Element styleE = doc.createElement("style");
		DomUtil.setElementText(styleE, styleText);
		parentNode.appendChild(styleE);
		return styleName;
	}
}
