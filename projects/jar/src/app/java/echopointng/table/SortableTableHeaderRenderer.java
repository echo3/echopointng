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

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.Table;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.layout.TableLayoutData;
import nextapp.echo2.app.table.TableCellRenderer;
import echopointng.BorderEx;
import echopointng.ExtentEx;

/**
 * Default renderer for <code>SortableTableModel</code> backed Tables.
 * 
 * @author Jason Dalton
 */
public class SortableTableHeaderRenderer implements TableCellRenderer {

	public static final ImageReference DEFAULT_UP_ARROW_IMAGE = new ResourceImageReference("/echopointng/resource/images/ArrowUp.gif");

	public static final ImageReference DEFAULT_DOWN_ARROW_IMAGE = new ResourceImageReference("/echopointng/resource/images/ArrowDown.gif");

	public static final Color DEFAULT_BACKGROUND = new Color(175, 175, 239);

	public static final BorderEx DEFAULT_BORDER = new BorderEx(DEFAULT_BACKGROUND);

	public static final Insets DEFAULT_INSETS = new Insets(1);

	public static final TableLayoutData DEFAULT_LAYOUT_DATA;
	static {
		DEFAULT_LAYOUT_DATA = new TableLayoutData();
		DEFAULT_LAYOUT_DATA.setBackground(DEFAULT_BACKGROUND);
	}

	/** The up arrow image displayed in the column header */
	private ImageReference upArrowImage = DEFAULT_UP_ARROW_IMAGE;

	/** The down arrow image displayed in the column header */
	private ImageReference downArrowImage = DEFAULT_DOWN_ARROW_IMAGE;

	/** The background displayed in the column header */
	private Color background = DEFAULT_BACKGROUND;

	/** The border displayed in the column header */
	private BorderEx border = DEFAULT_BORDER;

	/** The insets of the button displayed in the column header */
	private Insets insets = DEFAULT_INSETS;

	/** The layout data to be used for each cell */
	private TableLayoutData layoutData = DEFAULT_LAYOUT_DATA;

	public SortableTableHeaderRenderer() {
		layoutData.setBackground(background);
	}

	/**
	 * @see nextapp.echo2.app.table.TableCellRenderer#getTableCellRendererComponent(nextapp.echo2.app.Table,
	 *      java.lang.Object, int, int)
	 */
	public Component getTableCellRendererComponent(Table table, Object value, int column, int row) {
		SortableTableModel model = (SortableTableModel) table.getModel();
		return getSortButton((String) value, column, model);
	}

	/**
	 * Creates and returns the component to render the header cell of the
	 * SortableTable. Derived classes can override this method to further
	 * customize the look and feel of the button being returned or return a
	 * button of their own.
	 * 
	 * @param label
	 *            specifies the label to displyed on the button.
	 * @param column
	 *            the column index of the column being rendered.
	 * @param model
	 *            specifies the model for the SortableTable.
	 * @return a Button which renders the header cell.
	 */
	protected Button getSortButton(String label, int column, SortableTableModel model) {
		Button button = new Button(label);
		button.addActionListener(getSortButtonListener(column, model));
		button.setLayoutData(getLayoutData());
		button.setInsets(getInsets());
		button.setBackground(getBackground());
		button.setBorder(getBorder());
		button.setTextPosition(new Alignment(Alignment.LEFT, Alignment.DEFAULT));
//		button.setWidth(new ExtentEx("100%"));
		ImageReference icon = null;
		if (model.getCurrentSortColumn() == column) {
			int sortDirective = model.getSortDirective(column);
			if (sortDirective == SortableTableModel.ASCENDING) {
				icon = getUpArrowImage();
			} else if (sortDirective == SortableTableModel.DESCENDING) {
				icon = getDownArrowImage();
			} else {
				icon = null;
			}
			button.setIcon(icon);
		}
		return button;
	}

	/**
	 * Creates and returns the ActionListener for the specified column header
	 * button.
	 * 
	 * @param column
	 *            specifies a column index of the SortableTable.
	 * @param model
	 *            specifies the model of the SortableTable.
	 * @return an action listener for the of the button used to render the
	 *         header cell.
	 */
	protected ActionListener getSortButtonListener(final int column, final SortableTableModel model) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int sortDirective = model.getSortDirective(column);
				int newSortDirective;
				if (sortDirective == SortableTableModel.NOT_SORTED) {
					newSortDirective = SortableTableModel.ASCENDING;
				} else if (sortDirective == SortableTableModel.ASCENDING) {
					newSortDirective = SortableTableModel.DESCENDING;
				} else {
					newSortDirective = SortableTableModel.NOT_SORTED;
				}
				model.sortByColumn(column, newSortDirective);
			}
		};
	}

	/**
	 * Returns the up arrow displyed in the column header of the sortable table.
	 * 
	 * @return the up arrow image displayed in the column header.
	 */
	public ImageReference getUpArrowImage() {
		return upArrowImage;
	}

	/**
	 * Sets the up arrow displyed in the column header of the sortable table.
	 * 
	 * @param upArrowImage
	 *            specifies the images to be used as up arrow.
	 */
	public void setUpArrowImage(ImageReference upArrowImage) {
		this.upArrowImage = upArrowImage;
	}

	/**
	 * Returns the down arrow displyed in the column header of the sortable
	 * table.
	 * 
	 * @return the down arrow image displayed in the column header.
	 */
	public ImageReference getDownArrowImage() {
		return downArrowImage;
	}

	/**
	 * Sets the down arrow displyed in the column header of the sortable table.
	 * 
	 * @param downArrowImage
	 *            specifies the images to be used as up arrow.
	 */
	public void setDownArrowImage(ImageReference downArrowImage) {
		this.downArrowImage = downArrowImage;
	}

	/**
	 * @return the background color of the ortable table header.
	 */
	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
		layoutData.setBackground(background);
	}

	/**
	 * Returns the border for the button which is used to render a header cell
	 * of the SortableTable.
	 * 
	 * @return border for the button used to render header cell.
	 */
	public BorderEx getBorder() {
		return border;
	}

	/**
	 * Sets the border of the button which is used to render a header cell of
	 * the SortableTable. The new border is not effective until the table is
	 * redisplayed as a result of call to validate method.
	 * 
	 * @param border
	 *            specifies the border to be set for the cell renderer button.
	 */
	public void setBorder(BorderEx border) {
		this.border = border;
	}

	/**
	 * @return the insets of the button used to render the sortable table
	 *         header.
	 */
	public Insets getInsets() {
		return insets;
	}

	/**
	 * Sets the insets of the button used to render the header cell of the
	 * sortable table header. Note that this method sets the insets for the
	 * button itself not for the TableLayout used to place the button in the
	 * header cell.
	 * 
	 * @param insets
	 *            specifies the insets to be set for the button used render
	 *            header cell.
	 */
	public void setInsets(Insets insets) {
		this.insets = insets;
	}

	/**
	 * Returns the TableLayoutData used to place a button component in the
	 * header cell of the SortableTable. The changes made to the layout will not
	 * be effective until the table is validated.
	 * 
	 * @return the TableLayoutData used for each header cell.
	 */
	public TableLayoutData getLayoutData() {
		return layoutData;
	}

	/**
	 * @param layoutData
	 *            specifies the TableLayoutData to be set for each header cell
	 *            component.
	 */
	public void setLayoutData(TableLayoutData layoutData) {
		this.layoutData = layoutData;
	}
}