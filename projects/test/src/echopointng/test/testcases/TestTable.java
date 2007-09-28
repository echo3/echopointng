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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.CheckBox;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Table;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.layout.TableLayoutData;
import nextapp.echo2.app.list.DefaultListSelectionModel;
import nextapp.echo2.app.list.ListSelectionModel;
import nextapp.echo2.app.table.DefaultTableColumnModel;
import nextapp.echo2.app.table.DefaultTableModel;
import nextapp.echo2.app.table.TableColumn;
import nextapp.echo2.app.table.TableColumnModel;
import nextapp.echo2.app.table.TableModel;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.ContainerEx;
import echopointng.ExtentEx;
import echopointng.LabelEx;
import echopointng.TableEx;
import echopointng.layout.TableLayoutDataEx;
import echopointng.table.AbleTableSelectionModel;
import echopointng.table.ColumnHidingTableModel;
import echopointng.table.DefaultPageableSortableTableModel;
import echopointng.table.DefaultSortableTableModel;
import echopointng.table.DefaultTableCellRendererEx;
import echopointng.table.PageableSortableTable;
import echopointng.table.PageableTableNavigation;
import echopointng.table.SortableTable;
import echopointng.table.SortableTableColumn;
import echopointng.table.SortableTableModel;
import echopointng.table.SortableTableSelectionModel;
import echopointng.table.TableActionEventEx;
import echopointng.table.TableCellRendererEx;
import echopointng.test.controller.TestController;
import echopointng.test.helpers.TestBooleanHelper;
import echopointng.test.helpers.TestButtonHelper;
import echopointng.test.helpers.TestCallback;
import echopointng.test.helpers.TestDisplaySink;
import echopointng.test.helpers.TestHelper;
import echopointng.util.ColorKit;
import echopointng.util.RandKit;
import echopointng.xhtml.XhtmlFragment;

/**
 * <code>TestLabel</code>
 */

public class TestTable extends TestCaseBaseNG {

	public String getTestCategory() {
		return "TableEx";
	}

	private static final int COLUMNS = 7;

	private static final int ROWS = 777;

	public Component testPageableSortableTable() {
		Component container = new Column();

		PageableSortableTable table = new PageableSortableTable();
		TableColumnModel columnModel = new DefaultTableColumnModel();
		DefaultTableModel model = new DefaultTableModel(COLUMNS, ROWS);

		for (int i = 0; i < COLUMNS; i++) {
			SortableTableColumn tableColumn = new SortableTableColumn(i);
			// tableColumn.setComparator(INT_COMPARATOR);
			tableColumn.setComparator(DefaultSortableTableModel.COMPARABLE_COMPARATOR);
			tableColumn.setModelIndex(i);
			tableColumn.setHeaderValue(" Col " + i + " ");
			tableColumn.setWidth(new ExtentEx("60px"));
			columnModel.addColumn(tableColumn);
			for (int j = 0; j < ROWS; j++) {
				model.setValueAt(new Integer(i * j), i, j);
			}
		}
		DefaultPageableSortableTableModel pageableSortableTableModel = new DefaultPageableSortableTableModel(model, columnModel);
		table.setModel(pageableSortableTableModel);
		table.setColumnModel(columnModel);
		table.setBorder(new BorderEx());
		table.setScrollable(true);
		table.setResizeable(true);
		table.setResizeGrowsTable(true);

		PageableTableNavigation tableScroller = new PageableTableNavigation(table);
		container.add(tableScroller);
		container.add(table);

		return container;
	}

	public Component testPageableSortableTableExactNumbers() {
		Component container = new Column();

		int COLUMNS = 7;
		int ROWS = 50;
		PageableSortableTable table = new PageableSortableTable();
		TableColumnModel columnModel = new DefaultTableColumnModel();
		DefaultTableModel model = new DefaultTableModel(COLUMNS, ROWS);

		for (int i = 0; i < COLUMNS; i++) {
			SortableTableColumn tableColumn = new SortableTableColumn(i);
			tableColumn.setComparator(DefaultSortableTableModel.COMPARABLE_COMPARATOR);
			tableColumn.setModelIndex(i);
			tableColumn.setHeaderValue(" Col " + i + " ");
			tableColumn.setWidth(new ExtentEx("60px"));
			columnModel.addColumn(tableColumn);
			for (int j = 0; j < ROWS; j++) {
				model.setValueAt(new Integer(i * j), i, j);
			}
		}
		DefaultPageableSortableTableModel pageableSortableTableModel = new DefaultPageableSortableTableModel(model, columnModel);
		table.setModel(pageableSortableTableModel);
		table.setColumnModel(columnModel);
		table.setBorder(new BorderEx());
		table.setScrollable(true);
		table.setResizeable(true);
		table.setResizeGrowsTable(true);

		PageableTableNavigation tableScroller = new PageableTableNavigation(table);
		container.add(tableScroller);
		container.add(table);

		return container;
	}

	private TableModel genDefaultModel(int columnCount, int rowCount) {
		DefaultTableModel model = new DefaultTableModel(columnCount, rowCount);
		for (int col = 0; col < columnCount; col++) {
			for (int row = 0; row < rowCount; row++) {
				if (col == 0) {
					model.setValueAt("" + row, col, row);
				} else {
					model.setValueAt("C:" + col + " R:" + row, col, row);
				}
			}
		}
		return model;
	}

	private TableModel genDefaultRandomModel(int columnCount, int rowCount) {
		DefaultTableModel model = new DefaultTableModel(columnCount, rowCount) {
			public Class getColumnClass(int column) {
				if (column == 1) {
					return String.class;
				} else if (column == 2) {
					return Integer.class;
				} else if (column == 3) {
					return Float.class;
				} else {
					return Calendar.class;
				}
			};
		};

		String[] strings = { "Alpha", "Beta", "Delta", "Gamma", "Omega", "Theta", "Phi" };
		Integer[] ints = { new Integer(1), new Integer(100), new Integer(1000), new Integer(99), new Integer(999), new Integer(10000) };
		Float[] floats = { new Float(1.1), new Float(1.2), new Float(0.1), new Float(99.9), new Float(99.67) };
		Calendar[] cals = { Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance() };

		for (int col = 0; col < columnCount; col++) {
			for (int row = 0; row < rowCount; row++) {
				Object value = "C:" + col + " R:" + row;
				if (col == 0) {
					value = "" + row;
				}
				if (col == 1) {
					Calendar cal = (Calendar) RandKit.roll(cals);
					if (row > 0) {
						cal.add(Calendar.HOUR, RandKit.rand(1, 23));
						cal.add(Calendar.MINUTE, RandKit.rand(1, 60));
						cal.add(Calendar.SECOND, RandKit.rand(1, 60));
					}
					value = new SimpleDateFormat("hh:mm:ss").format(cal.getTime());
				}
				if (col == 2) {
					value = RandKit.roll(strings);
				}
				if (col == 3) {
					value = RandKit.roll(ints);
				}
				if (col == 4) {
					value = RandKit.roll(floats);
				}
				model.setValueAt(value, col, row);
			}
		}
		return model;
	}

	public Component testSortedTable() {

		TableModel tableModel = genDefaultRandomModel(5, 10);
		final SortableTableModel sortableTableModel = new DefaultSortableTableModel(tableModel);
		final SortableTableSelectionModel sortableTableSelectionModel = new SortableTableSelectionModel(sortableTableModel);

		SortableTable table = new SortableTable(sortableTableModel);
		table.setSelectionModel(sortableTableSelectionModel);
		table.setBorder(BorderEx.DEFAULT);
		table.setSelectionEnabled(true);
		table.setSelectionBackground(Color.YELLOW);
		table.setRolloverBackground(Color.PINK);
		table.setRolloverEnabled(true);
		table.setResizeable(true);
		table.setScrollable(true);

		final DefaultTableModel selectionTableModel = new DefaultTableModel(3, 0);

		TableEx selectionTable = new TableEx(selectionTableModel);
		selectionTable.setHeaderVisible(true);
		selectionTable.setDefaultHeaderRenderer(new DefaultTableCellRendererEx() {
			/**
			 * @see echopointng.table.TableCellRendererEx#getTableCellRendererContent(nextapp.echo2.app.Table,
			 *      java.lang.Object, int, int)
			 */
			public XhtmlFragment getTableCellRendererContent(Table table, Object value, int column, int row) {
				String colName = "";
				if (column == 0) {
					colName = "Selected Row";
				} else if (column == 1) {
					colName = "Unsorted Model Index";
				} else if (column == 2) {
					colName = "Sorted View Index";
				}
				return new XhtmlFragment(colName);
			}

		});
		selectionTable.setBorder(BorderEx.DEFAULT);

		ButtonEx updateSelectionBtn = new ButtonEx("Update Selection Status");

		final Runnable actionRunner = new Runnable() {
			public void run() {
				int rowCount = selectionTableModel.getRowCount();
				for (int i = 0; i < rowCount; i++) {
					selectionTableModel.deleteRow(i);
				}
				for (int row = 0; row < sortableTableModel.getRowCount(); row++) {
					if (sortableTableSelectionModel.isSelectedIndex(row)) {
						//
						// the sortable selection model always reports in terms
						// of
						// the view not the model hence we should not re-covert
						// to the
						// view.
						//
						String rowName = "Row : " + row;
						String modelIndex = "" + sortableTableModel.toUnsortedModelRowIndex(row);
						String viewIndex = "" + row;

						selectionTableModel.addRow(new Object[] { rowName, modelIndex, viewIndex });
					}
				}
			};
		};

		sortableTableSelectionModel.addChangeListener(new ChangeListener() {
			public void stateChanged(nextapp.echo2.app.event.ChangeEvent e) {
				actionRunner.run();
			};
		});

		updateSelectionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionRunner.run();
			}
		});

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("10px"));
		cell.add(table);
		cell.add(updateSelectionBtn);
		cell.add(selectionTable);

		return cell;
	}

	public Component testPageableSortedTableWithSelection() {

		TableModel tableModel = genDefaultRandomModel(5, 100);
		final DefaultPageableSortableTableModel pageableSortableTableModel = new DefaultPageableSortableTableModel(tableModel);
		pageableSortableTableModel.setRowsPerPage(10);
		final AbleTableSelectionModel ableTableSelectionModel = new AbleTableSelectionModel(pageableSortableTableModel);
		ableTableSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_SELECTION);

		PageableSortableTable table = new PageableSortableTable(pageableSortableTableModel);
		table.setSelectionModel(ableTableSelectionModel);
		table.setBorder(BorderEx.DEFAULT);
		table.setSelectionEnabled(true);
		table.setSelectionBackground(Color.YELLOW);
		table.setRolloverBackground(Color.PINK);
		table.setRolloverEnabled(true);
		table.setResizeable(true);
		table.setScrollable(true);

		// inserted by CC
		table.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Point A");
            }
        }); 
		
		DefaultTableModel selectionTableModel = new DefaultTableModel(3, 0);

		final TableEx selectionTable = new TableEx(selectionTableModel);
		selectionTable.setHeaderVisible(true);
		selectionTable.setDefaultHeaderRenderer(new DefaultTableCellRendererEx() {
			/**
			 * @see echopointng.table.TableCellRendererEx#getTableCellRendererContent(nextapp.echo2.app.Table,
			 *      java.lang.Object, int, int)
			 */
			public XhtmlFragment getTableCellRendererContent(Table table, Object value, int column, int row) {
				String colName = "";
				if (column == 0) {
					colName = "Selected Row";
				} else if (column == 1) {
					colName = "Unsorted Model Index";
				} else if (column == 2) {
					colName = "Sorted View Index";
				}
				return new XhtmlFragment(colName);
			}

		});
		selectionTable.setBorder(BorderEx.DEFAULT);

		ButtonEx updateSelectionBtn = new ButtonEx("Update Selection Status");

		final Runnable actionRunner = new Runnable() {
			public void run() {
				DefaultTableModel selectionTableModel = new DefaultTableModel(3, 0);
				int selectedIndices[] = ableTableSelectionModel.getSelectedIndices();
				selectionTableModel.addRow(new Object[] { "View indices", "-", String.valueOf(selectedIndices.length)});
				for (int i = 0; i < selectedIndices.length; i++) {
					int row = selectedIndices[i];
					//
					// the sortable selection model always reports in terms
					// of
					// the view not the model hence we should not re-covert
					// to the
					// view.
					//
					String rowName = "Row : " + row;
					String modelIndex = "" + pageableSortableTableModel.toUnsortedModelRowIndex(row);
					String viewIndex = "" + row;

					selectionTableModel.addRow(new Object[] { rowName, modelIndex, viewIndex });
				}
				selectedIndices = ableTableSelectionModel.getSelectedModelIndices();
				selectionTableModel.addRow(new Object[] { "Model indices", "-", String.valueOf(selectedIndices.length)});
				for (int i = 0; i < selectedIndices.length; i++) {
					int row = selectedIndices[i];

					String rowName = "Row : " + row;
					String modelIndex = "" + row;
					String viewIndex = "" + pageableSortableTableModel.toSortedViewRowIndex(row);;

					selectionTableModel.addRow(new Object[] { rowName, modelIndex, viewIndex });
				}
				selectionTable.setModel(selectionTableModel);
			};
		};

		ableTableSelectionModel.addChangeListener(new ChangeListener() {
			public void stateChanged(nextapp.echo2.app.event.ChangeEvent e) {
				actionRunner.run();
			};
		});

		updateSelectionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionRunner.run();
			}
		});

		PageableTableNavigation tableScroller = new PageableTableNavigation(table);
		
		TestController controller = new TestController(this, table);
		controller.addTest(new TestButtonHelper(), "Multi Selection", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				ableTableSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_SELECTION);
			}
		});
		controller.addTest(new TestButtonHelper(), "Single Selection", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				ableTableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			}
		});
		
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("10px"));
		cell.add(table);
		cell.add(tableScroller);
		cell.add(updateSelectionBtn);
		cell.add(selectionTable);
		cell.add(controller.getUI());

		return cell;
	}

	public Component testTableSimple() {
		TableEx tableEx = new TableEx(genDefaultModel(7, 7));
		tableEx.setSelectionEnabled(true);
		tableEx.setSelectionBackground(ColorKit.clr("#ff00cc"));

		tableEx.setRolloverEnabled(true);
		tableEx.setRolloverBackground(ColorKit.clr("#acacac"));
		return tableEx;
	}

	public Component testTableWithFooter() {
		TableEx tableEx = new TableEx(genDefaultModel(7, 7));
		tableEx.setSelectionEnabled(true);
		tableEx.setSelectionBackground(ColorKit.clr("#ff00cc"));

		tableEx.setRolloverEnabled(true);
		tableEx.setRolloverBackground(ColorKit.clr("#acacac"));

		TestController controller = new TestController(this, tableEx);
		controller.addBooleanTest("footerVisible", "Footer Visible?");
		controller.addColorTest("footerBackground", "Set Footer Background");

		Column cell = new Column();
		cell.add(tableEx);
		cell.add(controller.getUI());
		return cell;
	}

	public Component testSpanning() {

		final FillImage cellBGImage = new FillImage(new HttpImageReference("/images/bg_blue_hue.gif"), null, null, FillImage.REPEAT_HORIZONTAL);
		TableCellRendererEx cellRendererEx = new TableCellRendererEx() {

			public Component getTableCellRendererComponent(Table table, Object value, int column, int row) {
				return null;
			}

			/**
			 * @see echopointng.table.TableCellRendererEx#isActionCausingCells(nextapp.echo2.app.Table,
			 *      int, int)
			 */
			public boolean isActionCausingCell(Table table, int column, int row) {
				return (column == 4 ? false : true);
			}

			/**
			 * @see echopointng.table.TableCellRendererEx#isSelectionCausingCell(nextapp.echo2.app.Table,
			 *      int, int)
			 */
			public boolean isSelectionCausingCell(Table table, int column, int row) {
				return (column == 5 ? false : true);
			}

			public XhtmlFragment getTableCellRendererContent(Table table, Object value, int column, int row) {
				String content = String.valueOf(value);
				TableLayoutDataEx layoutDataEx = new TableLayoutDataEx();

				if ((column * row) % 2 == 0) {
					layoutDataEx.setBackground(ColorKit.clr("#acbcdc"));
				}
				if (column == 1 && (row % 2 == 0)) {
					int colspan = row;
					int rowspan = 2;
					content += " colspan=" + colspan + " rowspan=" + rowspan;

					layoutDataEx.setColSpan(colspan);
					layoutDataEx.setRowSpan(rowspan);
					layoutDataEx.setBackgroundImage(cellBGImage);
					layoutDataEx.setAlignment(new Alignment(Alignment.TRAILING, Alignment.CENTER));
				}
				XhtmlFragment fragment = new XhtmlFragment(content, layoutDataEx);
				return fragment;
			}
		};

		TableEx tableEx = new TableEx(genDefaultModel(8, 8));
		tableEx.setInsets(new Insets(2));
		tableEx.setBorder(BorderEx.DEFAULT);
		tableEx.setSelectionEnabled(true);
		tableEx.setSelectionBackground(ColorKit.clr("#D8BFD8"));

		tableEx.setRolloverEnabled(true);
		tableEx.setRolloverBackground(Color.YELLOW);

		tableEx.createDefaultColumnsFromModel();
		TableColumnModel columnModel = tableEx.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			TableColumn tableColumn = columnModel.getColumn(i);
			tableColumn.setCellRenderer(cellRendererEx);
		}
		return tableEx;
	}

	public Component testResizeableTable() {

		TableCellRendererEx cellRendererEx = new DefaultTableCellRendererEx() {
			public Component getTableCellRendererComponent(Table table, Object value, int column, int row) {
				return null;
			}

			public XhtmlFragment getTableCellRendererContent(Table table, Object value, int column, int row) {
				XhtmlFragment fragment = new XhtmlFragment(String.valueOf(value));
				if (row > 0 && row % 2 != 0) {
					TableLayoutDataEx layoutDataEx = new TableLayoutDataEx();
					layoutDataEx.setBackground(ColorKit.clr("#DCDCDC"));
					if (column == 1 && row == 1) {
						layoutDataEx.setColSpan(2);
						layoutDataEx.setRowSpan(2);
					}
					if (column == 0) {
						layoutDataEx.setToolTipText("Tooltip for " + column + "-" + row);
					}
					fragment.setLayoutData(layoutDataEx);
				}
				return fragment;
			}

		};

		final BorderEx tableBorder = new BorderEx(1, Color.DARKGRAY, BorderEx.STYLE_SOLID);

		final TableModel defaultTableModel = genDefaultModel(4, 50);
		final TableModel smallTableModel = genDefaultModel(4, 5);
		final TableModel emptyTableModel = genDefaultModel(4, 0);

		final TableEx tableEx = new TableEx(defaultTableModel);
		tableEx.setSelectionEnabled(true);
		tableEx.setSelectionBackground(ColorKit.clr("#ff00cc"));

		tableEx.setRolloverEnabled(true);
		tableEx.setRolloverBackground(ColorKit.clr("#acacac"));
		tableEx.setHeaderBackground(ColorKit.clr("#EBEADB"));
		tableEx.setFooterBackground(ColorKit.clr("#abcdef"));

		tableEx.createDefaultColumnsFromModel();
		TableColumnModel columnModel = tableEx.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			TableColumn tableColumn = columnModel.getColumn(i);
			tableColumn.setWidth(new ExtentEx("100px"));
			tableColumn.setCellRenderer(cellRendererEx);
		}

		/* ====================== */
		final CheckBox scrollableCB = new CheckBox("scrollable?");
		final CheckBox resizeableCB = new CheckBox("resizeable?");
		final CheckBox resizeDragBarUsedCB = new CheckBox("resizeDragBarUsed?");
		final CheckBox resizeGrowsTableCB = new CheckBox("resizeGrowsTable?");

		final CheckBox heightSetCB = new CheckBox("200px height?");
		final CheckBox width100SetCB = new CheckBox("100% width?");
		final CheckBox width99SetCB = new CheckBox("99% width?");
		final CheckBox width80SetCB = new CheckBox("80% width?");
		final CheckBox borderdCB = new CheckBox("1px Border?");

		final CheckBox smallModelCB = new CheckBox("Small TableModel?");
		final CheckBox emptyModelCB = new CheckBox("Empty TableModel?");
		final CheckBox hiddenHeaderCB = new CheckBox("Hide Header?");
		final CheckBox showFooterCB = new CheckBox("Show Footer?");
		final CheckBox heightStretchedCB = new CheckBox("Height Stretched?");

		final ButtonEx applyBtn = new ButtonEx("Apply");
		applyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableEx.setScrollable(scrollableCB.isSelected());
				tableEx.setResizeable(resizeableCB.isSelected());
				tableEx.setResizeDragBarUsed(resizeDragBarUsedCB.isSelected());
				tableEx.setResizeGrowsTable(resizeGrowsTableCB.isSelected());
				tableEx.setHeightStretched(heightStretchedCB.isSelected());

				if (borderdCB.isSelected()) {
					tableEx.setBorder(tableBorder);
				} else {
					tableEx.setBorder(null);
				}
				if (heightSetCB.isSelected()) {
					tableEx.setHeight(new ExtentEx("200px"));
				} else {
					tableEx.setHeight(null);
				}
				if (width100SetCB.isSelected()) {
					tableEx.setWidth(new ExtentEx("100%"));
				} else if (width99SetCB.isSelected()) {
					tableEx.setWidth(new ExtentEx("99%"));
				} else if (width80SetCB.isSelected()) {
					tableEx.setWidth(new ExtentEx("80%"));
				} else {
					tableEx.setWidth(null);
				}
				if (emptyModelCB.isSelected()) {
					tableEx.setModel(emptyTableModel);
				} else if (smallModelCB.isSelected()) {
					tableEx.setModel(smallTableModel);
				} else {
					tableEx.setModel(defaultTableModel);
				}
				if (hiddenHeaderCB.isSelected()) {
					tableEx.setHeaderVisible(false);
				} else {
					tableEx.setHeaderVisible(true);
				}
				if (showFooterCB.isSelected()) {
					tableEx.setFooterVisible(true);
				} else {
					tableEx.setFooterVisible(false);
				}
			}
		});
		// controller code
		Grid controllerGrid = new Grid(5);

		controllerGrid.add(scrollableCB);
		controllerGrid.add(resizeableCB);
		controllerGrid.add(resizeDragBarUsedCB);
		controllerGrid.add(resizeGrowsTableCB);
		controllerGrid.add(borderdCB);
		controllerGrid.add(heightSetCB);
		controllerGrid.add(width100SetCB);
		controllerGrid.add(width99SetCB);
		controllerGrid.add(width80SetCB);
		controllerGrid.add(smallModelCB);
		controllerGrid.add(emptyModelCB);
		controllerGrid.add(hiddenHeaderCB);
		controllerGrid.add(showFooterCB);
		controllerGrid.add(heightStretchedCB);
		controllerGrid.add(applyBtn);

		Column column = new Column();
		column.add(controllerGrid);
		column.add(tableEx);

		ContainerEx cell = new ContainerEx();
		cell.setBorder(new BorderEx(1, Color.RED, BorderEx.STYLE_DASHED));
		cell.setInsets(new Insets(2));
		cell.setOutsets(new Insets(5));
		cell.add(column);
		return cell;
	}

	public Component testSelection() {
		final LabelEx actionLabel = new LabelEx();

		final Table table = new Table(genDefaultModel(7, 3));
		table.setSelectionEnabled(true);
		table.setSelectionBackground(ColorKit.clr("#B0C4DE"));
		table.setRolloverEnabled(true);
		table.setRolloverBackground(ColorKit.clr("#FAFAD2"));

		final TableEx tableEx = new TableEx(genDefaultModel(7, 10));
		tableEx.setSelectionEnabled(true);
		tableEx.setSelectionBackground(ColorKit.clr("#B0C4DE"));
		tableEx.setRolloverEnabled(true);
		tableEx.setRolloverBackground(ColorKit.clr("#FAFAD2"));

		final TestController controller = new TestController(this, tableEx);

		// cell renderer
		final TableLayoutData layoutDataAction = new TableLayoutData();
		final TableLayoutData layoutDataSelection = new TableLayoutData();
		final TableLayoutData layoutDataSelected = new TableLayoutData();
		layoutDataAction.setBackground(ColorKit.clr("#F0FFF0"));
		layoutDataSelection.setBackground(ColorKit.clr("#DEB887"));
		layoutDataSelected.setBackground(ColorKit.clr("#F0E68C"));

		TableCellRendererEx cellRendererEx = new DefaultTableCellRendererEx() {
			/**
			 * @see echopointng.table.DefaultTableCellRendererEx#getTableCellRendererContent(nextapp.echo2.app.Table,
			 *      java.lang.Object, int, int)
			 */
			public XhtmlFragment getTableCellRendererContent(Table table, Object value, int column, int row) {
				XhtmlFragment fragment = super.getTableCellRendererContent(table, value, column, row);
				if (row != column) {
					fragment.setLayoutData(layoutDataAction);
				} else {
					if (table.getSelectionModel().isSelectedIndex(row)) {
						fragment.setLayoutData(layoutDataSelected);
					} else {
						fragment.setLayoutData(layoutDataSelection);
					}
				}
				return fragment;
			}

			/**
			 * @see echopointng.table.DefaultTableCellRendererEx#isActionCausingCells(nextapp.echo2.app.Table,
			 *      int, int)
			 */
			public boolean isActionCausingCell(Table table, int column, int row) {
				return column != row;
			}

			/**
			 * @see echopointng.table.DefaultTableCellRendererEx#isSelectionCausingCell(nextapp.echo2.app.Table,
			 *      int, int)
			 */
			public boolean isSelectionCausingCell(Table table, int column, int row) {
				return column == row;
			}
		};
		TableColumnModel columnModel = tableEx.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			TableColumn tableColumn = columnModel.getColumn(i);
			tableColumn.setCellRenderer(cellRendererEx);
		}

		tableEx.setActionCommand("ActionStations");
		tableEx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TableActionEventEx tableActionEventEx = (TableActionEventEx) e;
				int metaKeyInfo = tableActionEventEx.getMetaKeyInfo();
				StringBuffer sb = new StringBuffer();
				sb.append(" metaKeyInfo:");
				sb.append(metaKeyInfo);
				if ((metaKeyInfo & TableActionEventEx.METAKEY_ALT) == TableActionEventEx.METAKEY_ALT) {
					sb.append(" ALT ");
				}
				if ((metaKeyInfo & TableActionEventEx.METAKEY_CONTROL) == TableActionEventEx.METAKEY_CONTROL) {
					sb.append(" CTL ");
				}
				if ((metaKeyInfo & TableActionEventEx.METAKEY_META) == TableActionEventEx.METAKEY_META) {
					sb.append(" META ");
				}
				if ((metaKeyInfo & TableActionEventEx.METAKEY_SHIFT) == TableActionEventEx.METAKEY_SHIFT) {
					sb.append(" SHIFT ");
				}
				if (metaKeyInfo == TableActionEventEx.METAKEY_NONE) {
					sb.append(" NONE ");
				}
				sb.append(" column:");
				sb.append(tableActionEventEx.getColumn());
				sb.append(" row:");
				sb.append(tableActionEventEx.getRow());

				actionLabel.setText(sb.toString());
			}
		});
		//
		// selection model test
		TestBooleanHelper selectionHelper = controller.addBooleanTest(null, "Use Single Selection Model?", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				TestBooleanHelper helper = (TestBooleanHelper) testHelper;
				DefaultListSelectionModel selectionModel1 = new DefaultListSelectionModel();
				DefaultListSelectionModel selectionModel2 = new DefaultListSelectionModel();
				if (helper.booleanValue()) {
					selectionModel1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					selectionModel2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				} else {
					selectionModel1.setSelectionMode(ListSelectionModel.MULTIPLE_SELECTION);
					selectionModel2.setSelectionMode(ListSelectionModel.MULTIPLE_SELECTION);
				}
				table.setSelectionModel(selectionModel1);
				tableEx.setSelectionModel(selectionModel2);
			}
		});
		selectionHelper.setValue(true);
		//
		// ignore meta keys test
		controller.addBooleanTest("ignoreMetaKeys", "Ignore Meta Keys?");

		controller.addTest(new TestButtonHelper(), "Display Selections!", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				ListSelectionModel model;
				int maxIndex, minIndex;
				String tableType;

				Table[] tables = new Table[] { table, tableEx };
				for (int i = 0; i < tables.length; i++) {
					tableType = tables[i].getClass().getName();
					model = tables[i].getSelectionModel();
					maxIndex = model.getMaxSelectedIndex();
					minIndex = model.getMinSelectedIndex();
					if (maxIndex == -1 && minIndex == -1) {
						displaySink.logMessage(tableType + " has no selections at present");
					} else {
						for (int j = minIndex; j <= maxIndex; j++) {
							displaySink.logMessage(tableType + " row[" + j + "] is selected");
						}
					}
				}
			}
		});

		controller.addTest(new TestButtonHelper(), "Use large TableModel", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				TableModel model = genDefaultModel(4, 5000);
				tableEx.setModel(model);
			}
		});

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(7));
		cell.add(actionLabel);

		cell.add(new LabelEx(table.getClass().getName()));
		cell.add(table);
		cell.add(new LabelEx(tableEx.getClass().getName()));
		cell.add(tableEx);
		cell.add(controller.getUI());

		return cell;
	}
	
	public Component testSelectionInOneColumn() {
		final TableEx tableEx = new TableEx(genDefaultModel(7, 10));
		tableEx.setSelectionEnabled(true);
		tableEx.setSelectionBackground(ColorKit.clr("#B0C4DE"));
		tableEx.setRolloverEnabled(true);
		tableEx.setRolloverBackground(ColorKit.clr("#FAFAD2"));
		
		final TableLayoutData layoutDataGrey = new TableLayoutData();
		layoutDataGrey.setBackground(ColorKit.clr("#C0C0C0"));
		
		TableCellRendererEx cellRendererEx = new DefaultTableCellRendererEx() {
			/**
			 * @see echopointng.table.DefaultTableCellRendererEx#getTableCellRendererContent(nextapp.echo2.app.Table, java.lang.Object, int, int)
			 */
			public XhtmlFragment getTableCellRendererContent(Table table, Object value, int column, int row) {
				XhtmlFragment fragment = super.getTableCellRendererContent(table, value, column, row);
				if (column == 0 && row >= 0) {
					fragment.setLayoutData(layoutDataGrey);
				}
				if (row == -1) {
					fragment.setLayoutData(layoutDataGrey);
				}
				return fragment;
			}
			/**
			 * @see echopointng.table.DefaultTableCellRendererEx#isSelectionCausingCell(nextapp.echo2.app.Table, int, int)
			 */
			public boolean isSelectionCausingCell(Table table, int column, int row) {
				return column == 0;
			}
		};
		tableEx.setDefaultRenderer(cellRendererEx);
		tableEx.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_SELECTION);
		tableEx.getColumnModel().getColumn(0).setWidth(new ExtentEx("50px"));
		
		Column cell = new Column();
		cell.add(tableEx);
		return cell;
	}

	public Component testTableRendering() {

		TableModel tableModel = genDefaultRandomModel(5, 5);
		final TableEx tableEx = new TableEx(tableModel);
		tableEx.setFooterVisible(true);
		tableEx.setScrollable(true);
		tableEx.setResizeable(true);

		TestController controller = new TestController(this, tableEx);
		controller.addBooleanTest(TableEx.PROPERTY_SCROLLABLE, "Scrollable?");
		controller.addBooleanTest(TableEx.PROPERTY_RESIZEABLE, "Resizeable?");
		controller.addColorTest(TableEx.PROPERTY_BACKGROUND, "Set table background");
		controller.addColorTest(TableEx.PROPERTY_HEADER_BACKGROUND, "Header background");
		controller.addColorTest(TableEx.PROPERTY_FOOTER_BACKGROUND, "Footer background");
		controller.addTest(new TestButtonHelper(), "Set Height 150px", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				tableEx.setHeight(new ExtentEx("150px"));
			}
		});
		controller.addTest(new TestButtonHelper(), "Set Width 100%", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				tableEx.setWidth(new ExtentEx("100%"));
			}
		});

		Column cell = new Column();
		cell.add(tableEx);
		cell.add(controller.getUI());
		return cell;
	}

	public Component testFillParent() {
		TableModel tableModel = genDefaultRandomModel(5, 50);
		final TableEx tableEx = new TableEx(tableModel);
		tableEx.setHeaderVisible(true);
		tableEx.setHeaderBackground(Color.PINK);
		tableEx.setFooterVisible(true);
		tableEx.setFooterBackground(Color.PINK);
		tableEx.setScrollable(true);
		tableEx.setResizeable(true);
		tableEx.setHeightStretched(true);
		tableEx.setHeight(new ExtentEx("100%"));

		TestController controller = new TestController(this, tableEx);
		controller.addTest(new TestButtonHelper(), "Set Height 150px", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				tableEx.setHeight(new ExtentEx("150px"));
			}
		});
		controller.addTest(new TestButtonHelper(), "Set Height null", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				tableEx.setHeight(null);
			}
		});
		controller.addTest(new TestButtonHelper(), "Set Width 100%", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				tableEx.setWidth(new ExtentEx("100%"));
			}
		});
		controller.addTest(new TestButtonHelper(), "Set Width 80%", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				tableEx.setWidth(new ExtentEx("80%"));
			}
		});
		controller.addTest(new TestButtonHelper(), "Set Width to null", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				tableEx.setWidth(null);
			}
		});
		controller.addTest(new TestButtonHelper(), "Set Width 400px", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				tableEx.setWidth(new ExtentEx("400px"));
			}
		});
		controller.addTest(new TestButtonHelper(), "Set Min Height 100px", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				tableEx.setMinimumStretchedHeight(new ExtentEx("100px"));
			}
		});
		controller.addTest(new TestButtonHelper(), "Set Max Height 200px", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				tableEx.setMaximumStretchedHeight(new ExtentEx("200px"));
			}
		});
		ContainerEx cell = new ContainerEx();
		cell.add(tableEx);
		cell.add(controller.getUI());
		return cell;
	}

	public Component testColumnSizing() {
		return null;
	}

	public Component testTableExRenderingSpeed() {

		TableModel tableModel = genDefaultRandomModel(8, 200);
		final TableEx tableEx = new TableEx(tableModel);
		tableEx.setFooterVisible(true);
		tableEx.setScrollable(true);
		tableEx.setResizeable(true);
		
		TableCellRendererEx cellRendererEx = new DefaultTableCellRendererEx() {
			public XhtmlFragment getTableCellRendererContent(Table table, Object value, int column, int row) {
				XhtmlFragment fragment = super.getTableCellRendererContent(table, value, column, row);
				if (column > 3) {
					fragment.setFragment("<div style=\"color:blue;\">" + fragment.getFragment() + "</div>");
				}
				if (column == 2) {
					fragment.setFragment("<span style=\"color:red;\">" + fragment.getFragment() + "</span>");
				}
				if (row == -2) {
					fragment  = new XhtmlFragment("<div style=\"height:1ex;\"/>");
				}
				return fragment;
			}
		};
		tableEx.setDefaultRenderer(cellRendererEx);

		TestController controller = new TestController(this, tableEx);
		controller.addBooleanTest(TableEx.PROPERTY_SCROLLABLE, "Scrollable?");
		controller.addBooleanTest(TableEx.PROPERTY_RESIZEABLE, "Resizeable?");

		Column cell = new Column();
		cell.add(tableEx);
		cell.add(controller.getUI());
		return cell;
	}

	public Component testColumnHiding() {
		
		DefaultTableModel defaultTableModel = new DefaultTableModel(new Object[][] {
				new Object[] { "R0 C0", "R0 C1", "R0 C2", "R0 C3" },
				new Object[] { "R1 C0", "R1 C1", "R1 C2", "R1 C3" },
		},
		new Object[] {
			"Column 0", "Column 1", "Column 2", "Column 3",	
		});
		

		final ColumnHidingTableModel columnHidingTableModel = new ColumnHidingTableModel(defaultTableModel);

		// TableCellRendererEx cellRendererEx = new DefaultTableCellRendererEx()
		// {
		// };

		final TableEx tableEx = new TableEx(columnHidingTableModel);
		tableEx.setFooterVisible(true);
		tableEx.setScrollable(true);
		tableEx.setResizeable(true);

		TestController controller = new TestController(this, tableEx);
		controller.addBooleanTest(TableEx.PROPERTY_SCROLLABLE, "Scrollable?");
		controller.addBooleanTest(TableEx.PROPERTY_RESIZEABLE, "Resizeable?");
		controller.addTest(new TestButtonHelper(), "Toggle Col 0", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				columnHidingTableModel.toggleColumn(0);
			}
		});
		controller.addTest(new TestButtonHelper(), "Toggle Col 2", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				columnHidingTableModel.toggleColumn(2);
			}
		});

		Column cell = new Column();
		cell.add(tableEx);
		cell.add(controller.getUI());
		return cell;
	}
	
	public Component testIE6CrashWithSortableModel() {
		TableModel myModel = genDefaultModel(5, 10);
        SortableTableModel model = new DefaultSortableTableModel( myModel );
        SortableTable table =  new SortableTable( model );

        // Set size and scrollability
        table.setResizeable( true );
        table.setWidth( new ExtentEx( "100%" ) );
        table.setWidth( new ExtentEx( "400px" ) );
        table.setScrollable( true );

        // Set column width
        table.getColumnModel().getColumn( 0 ). setWidth( new ExtentEx( "100px" ) );
        table.getColumnModel().getColumn( 1 ). setWidth( new ExtentEx( "100px" ) );
        table.getColumnModel().getColumn( 2 ). setWidth( new ExtentEx( "100px" ) );
        table.getColumnModel().getColumn( 3 ). setWidth( new ExtentEx( "100px" ) );
        table.getColumnModel().getColumn( 4 ). setWidth( new ExtentEx( "100px" ) );
        return table;		
	}
}
