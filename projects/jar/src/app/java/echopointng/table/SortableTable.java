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

import nextapp.echo2.app.table.DefaultTableColumnModel;
import nextapp.echo2.app.table.TableColumnModel;
import nextapp.echo2.app.table.TableModel;
import echopointng.TableEx;

/**
 * A convenience Table class for <code>SortableTableModel</code> backed
 * tables.
 * 
 * @author Jason Dalton
 */
public class SortableTable extends TableEx {

	/**
	 * Constructs a <code>SortableTable</code> with a simple
	 * DefaultSortableTableModel. DefaultTableColumnModel and
	 * SortableTableSelectionModel.
	 */
	public SortableTable() {
		setDefaultHeaderRenderer(new SortableTableHeaderRenderer());
		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		DefaultSortableTableModel model = new DefaultSortableTableModel(columnModel);
		setModel(model);
		setColumnModel(columnModel);
		setSelectionModel(new SortableTableSelectionModel(model));
	}

	/**
	 * Constructs a <code>SortableTable</code> with the specified
	 * <code>TableModel</code> as its model. This TableModel MUST be an
	 * instance of SortableTableModel
	 * 
	 * @param model -
	 *            an instance of SortableTableModel must be used.
	 * @throws IllegalArgumentException -
	 *             if the tabel model is not an instance of SortableTableModel
	 */
	public SortableTable(TableModel model) {
		this(model, null);
	}

	/**
	 * Constructs a <code>SortableTable</code> with the specified
	 * <code>TableModel</code> as its model. This TableModel MUST be an
	 * instance of SortableTableModel
	 * 
	 * @param model -
	 *            an instance of SortableTableModel must be used.
	 * @param columnModel -
	 *            the TableColumnModel to use
	 * @throws IllegalArgumentException -
	 *             if the tabel model is not an instance of SortableTableModel
	 */
	public SortableTable(TableModel model, TableColumnModel columnModel) {
		super(model, columnModel);
		if ((model instanceof SortableTableModel) == false) {
			throw new IllegalArgumentException("Model must be of type SortableTableModel");
		}
		setDefaultHeaderRenderer(new SortableTableHeaderRenderer());
		setSelectionModel(new SortableTableSelectionModel((SortableTableModel) model));
	}
}
