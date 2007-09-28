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
package echopointng.test.testcases;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.table.DefaultTableModel;
import nextapp.echo2.app.table.TableModel;
import nextapp.echo2.app.util.DomUtil;
import nextapp.echo2.webcontainer.ContainerInstance;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.BorderEx;
import echopointng.ExtentEx;
import echopointng.LabelEx;
import echopointng.LiveTable;
import echopointng.table.DefaultLiveTableRenderer;
import echopointng.table.LiveTableRenderer;
import echopointng.util.FontKit;

/** 
 * <code>TestProgressBar</code> 
 */

public class TestLiveTable extends TestCaseBaseNG {
	
	private TableModel genLargeTableModel(final String prefix, int maxRows) {
		TableModel model = new DefaultTableModel(4,maxRows) {
			/**
			 * @see nextapp.echo2.app.table.DefaultTableModel#getValueAt(int, int)
			 */
			public Object getValueAt(int column, int row) {
				StringBuffer sb = new StringBuffer();
				sb.append(prefix);
				sb.append(" row : ");
				sb.append(row);
				sb.append(" col : ");
				sb.append(column);
				return sb;
			}
		};
		return model;
	}
	
	/**
	 * @see echopointng.test.testcases.TestCaseBaseNG#getTestCategory()
	 */
	public String getTestCategory() {
		return "LiveTable";
	}

	public Component testDefaultLiveTable() {
		return new LiveTable();
	}

	public Component testLargeLiveTable() {
		LiveTable liveTable = new LiveTable(genLargeTableModel("large", 10000));
		return liveTable;
	}
	
	public Component testMediumLiveTable() {
		LiveTable liveTable = new LiveTable(genLargeTableModel("medium", 1000));
		return liveTable;
	}

	public Component testSmallLiveTable() {
		LiveTable liveTable = new LiveTable(genLargeTableModel("small", 100));
		liveTable.setRowsPerPage(20);
		return liveTable;
	}

	public Component testRolloverLiveTable() {
		LiveTable liveTable = new LiveTable(genLargeTableModel("rollover", 100));
		liveTable.setRowsPerPage(20);
		liveTable.setFont(FontKit.makeFont("Verdana,plain,8pt"));
		liveTable.setRolloverEnabled(true);
		liveTable.setRolloverBackground(Color.ORANGE);
		
		liveTable.setBorder(new BorderEx(Color.BLUE));
		return liveTable;
	}
	
	public Component testLiveTableWithLinks() {
		
		LiveTableRenderer renderer = new DefaultLiveTableRenderer() {
			/**
			 * @see echopointng.table.DefaultLiveTableRenderer#renderRowCell(echopointng.LiveTable, org.w3c.dom.Element, int, int)
			 */
			protected void renderRowCell(LiveTable liveTable, Element parentTD, int column, int row) {
				Document doc = parentTD.getOwnerDocument();
				Object value = liveTable.getModel().getValueAt(column, row);
				Node newNode = doc.createTextNode(String.valueOf(value));
				if (column % 2 == 0) {
					// add it as an anchor
					String elementId = ContainerInstance.getElementId(liveTable);
					String actionValue = "row : " + row + " column : " + column;
					Element anchor = doc.createElement("a");
					anchor.setAttribute("href","javascript:EPLiveTable.href('" + elementId + "','" + actionValue + "')");
					anchor.setAttribute("class","anchorStyle");
					anchor.appendChild(newNode);
					newNode = anchor;
				}
				parentTD.appendChild(newNode);
			}
			
			/**
			 * @see echopointng.table.DefaultLiveTableRenderer#renderStyles(echopointng.LiveTable, org.w3c.dom.Node)
			 */
			public void renderStyles(LiveTable liveTable, Node parentNode) {
				super.renderStyles(liveTable, parentNode);
				
				Document doc = parentNode.getOwnerDocument();
				
				String styleText = "a.anchorStyle { color : #acbddc; }";
				Element style = doc.createElement("style");
				DomUtil.setElementText(style, styleText);
				parentNode.appendChild(style);
			}
		};
		
		LiveTable liveTable = new LiveTable(genLargeTableModel("Click me", 1000));
		liveTable.setRenderer(renderer);
		liveTable.setFont(new Font(Font.VERDANA,Font.PLAIN,new ExtentEx("8pt")));
		
		final LabelEx  labelEx = new LabelEx("Looking for action!");
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				labelEx.setText("You clicked on " + e.getActionCommand());
			}
		};
		liveTable.addActionListener(al);
		
		Column cell = new Column();
		cell.setCellSpacing(new Extent(20));
		cell.add(liveTable);
		cell.add(labelEx);
		
		return cell;
	}
	
	
}
