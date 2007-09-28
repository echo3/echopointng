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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.EventListener;
import java.util.List;

import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.event.EventListenerList;
import nextapp.echo2.app.table.TableModel;
import echopointng.list.ListSelectionModelEx;

/**
 * Use this <code>ListSelectionModel</code> on a <code>Table</code> that
 * uses a <code>{@link SortableTableModel}</code> and or a
 * <code>{@link PageableTableModel}</code> to allow selections to be preserved
 * when the table is dynamically sorted or possibly paged. Selection indices are
 * retrieved and set based on the table model view.
 * <p>
 * NOTE : It is still possible to use plain TableModel implementations with this
 * ListSelectionModel, as no mapping will need to be done between model space
 * and view space.
 * 
 * Usage:
 * 
 * <pre>
 * Table table = new Table();
 * SortableTableModel sortedTableModel = new SortableTableModel(unsortedTableModel);
 * table.setSelectionModel(new AbleTableSelectionModel(sortedTableModel));
 * </pre>
 * 
 * @author David Nedde
 */
public class AbleTableSelectionModel implements ListSelectionModelEx, Serializable {

	private EventListenerList listenerList = new EventListenerList();

	private int selectionMode = SINGLE_SELECTION;

	private BitSet selection = new BitSet();

	private int minSelectedUnsortedIndex = -1;

	private TableModel tableModel;

	private boolean suppressChangeNotifications = false;

	public AbleTableSelectionModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	/**
	 * @see nextapp.echo2.app.list.ListSelectionModel#addChangeListener(
	 *      nextapp.echo2.app.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener l) {
		listenerList.addListener(ChangeListener.class, l);
	}

	/**
	 * @see nextapp.echo2.app.list.ListSelectionModel#clearSelection()
	 */
	public void clearSelection() {
		selection = new BitSet();
		minSelectedUnsortedIndex = -1;
		fireValueChanged();
	}

	/**
	 * Notifies <code>ChangeListener</code>s that the selection has changed.
	 */
	protected void fireValueChanged() {
		if (suppressChangeNotifications) {
			return;
		}
		ChangeEvent e = new ChangeEvent(this);
		EventListener[] listeners = listenerList.getListeners(ChangeListener.class);
		for (int index = 0; index < listeners.length; ++index) {
			((ChangeListener) listeners[index]).stateChanged(e);
		}
	}

	/*
	 * These method only remap if the underlying TableModel supports the
	 * PageableTableModel or SortableTableModel interfaces
	 */

	private int toSortedViewRowIndex(int modelRowIndex) {
		if (tableModel instanceof SortableTableModel) {
			return ((SortableTableModel) tableModel).toSortedViewRowIndex(modelRowIndex);
		}
		return modelRowIndex;
	}

	private int toUnsortedModelRowIndex(int viewRowIndex) {
		if (tableModel instanceof SortableTableModel) {
			return ((SortableTableModel) tableModel).toUnsortedModelRowIndex(viewRowIndex);
		}
		return viewRowIndex;
	}

	private int toPagedViewRowIndex(int modelRowIndex) {
		if (tableModel instanceof PageableTableModel) {
			return ((PageableTableModel) tableModel).toPagedViewRowIndex(modelRowIndex);
		}
		return modelRowIndex;
	}

	private int toUnpagedModelRowIndex(int viewRowIndex) {
		if (tableModel instanceof PageableTableModel) {
			return ((PageableTableModel) tableModel).toUnpagedModelRowIndex(viewRowIndex);
		}
		return viewRowIndex;
	}

	/**
	 * @see nextapp.echo2.app.list.ListSelectionModel#getMaxSelectedIndex()
	 */
	public int getMaxSelectedIndex() {
		int maxSelectedSortedIndex = selection.length() == 0 ? -1 : Integer.MIN_VALUE;
		int selectedViewIndices[] = getSelectedIndices();
		for (int i = 0; i < selectedViewIndices.length; i++) {
			int viewRowIndex = selectedViewIndices[i];
			if (viewRowIndex > maxSelectedSortedIndex) {
				maxSelectedSortedIndex = viewRowIndex;
			}
		}
		maxSelectedSortedIndex = (maxSelectedSortedIndex == Integer.MIN_VALUE ? -1 : maxSelectedSortedIndex);
		return maxSelectedSortedIndex;
	}

	/**
	 * @see nextapp.echo2.app.list.ListSelectionModel#getMinSelectedIndex()
	 */
	public int getMinSelectedIndex() {
		int minSelectedSortedIndex = selection.length() == 0 ? -1 : Integer.MAX_VALUE;
		int selectedViewIndices[] = getSelectedIndices();
		for (int i = 0; i < selectedViewIndices.length; i++) {
			int viewRowIndex = selectedViewIndices[i];
			if (viewRowIndex < minSelectedSortedIndex) {
				minSelectedSortedIndex = viewRowIndex;
			}
		}
		minSelectedSortedIndex = (minSelectedSortedIndex == Integer.MAX_VALUE ? -1 : minSelectedSortedIndex);
		return minSelectedSortedIndex;
	}

	/**
	 * @see nextapp.echo2.app.list.ListSelectionModel#getSelectionMode()
	 */
	public int getSelectionMode() {
		return selectionMode;
	}

	/**
	 * Returns the selection state of the given row. Note that the row index is
	 * given in terms of the View.
	 * 
	 * @param viewRowIndex -
	 *            the row index in terms of the View
	 * @return the selected state
	 * 
	 * @see nextapp.echo2.app.list.ListSelectionModel#isSelectedIndex(int)
	 */
	public boolean isSelectedIndex(int viewRowIndex) {
		try {
			int rowIndex = toUnpagedModelRowIndex(viewRowIndex);
			rowIndex = toUnsortedModelRowIndex(rowIndex);
			boolean selected = selection.get(rowIndex);
			return selected;
		} catch (ArrayIndexOutOfBoundsException e) {
			// table size changed
			return false;
		}
	}

	/**
	 * Returns the selection state of the given row. Note that the row index is
	 * given in terms of the underlying Model.
	 * 
	 * @param modelRowIndex -
	 *            the row index in terms of the underlying Model
	 * @return the selected state
	 * 
	 * 
	 */
	public boolean isSelectedModelIndex(int modelRowIndex) {
		try {
			boolean selected = selection.get(modelRowIndex);
			return selected;
		} catch (ArrayIndexOutOfBoundsException e) {
			// table size changed
			return false;
		}
	}

	/**
	 * @see nextapp.echo2.app.list.ListSelectionModel#isSelectionEmpty()
	 */
	public boolean isSelectionEmpty() {
		return selection.length() == 0;
	}

	/**
	 * @see nextapp.echo2.app.list.ListSelectionModel#removeChangeListener(nextapp.echo2.app.event.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener l) {
		listenerList.removeListener(ChangeListener.class, l);
	}

	/**
	 * This sets the selection state of the given row to newValue. Note that the
	 * row index is given in terms of the sorted View.
	 * 
	 * @param viewRowIndex -
	 *            the row index in terms of the sorted View
	 * @param newValue -
	 *            the new selection state
	 * 
	 * @see nextapp.echo2.app.list.ListSelectionModel#setSelectedIndex(int,
	 *      boolean)
	 */
	public void setSelectedIndex(int viewRowIndex, boolean newValue) {
		try {
			int rowIndex = toUnpagedModelRowIndex(viewRowIndex);
			rowIndex = toUnsortedModelRowIndex(rowIndex);
			setSelectedUnsortedIndex(rowIndex, newValue);
		} catch (ArrayIndexOutOfBoundsException e) {
			// table size changed - ignore
		}
	}

	/**
	 * @see echopointng.list.ListSelectionModelEx#setSelectedIndices(int[], boolean)
	 */
	public void setSelectedIndices(int[] selectedIndices, boolean selected) {
		suppressChangeNotifications = true;
		for (int i = 0; i < selectedIndices.length; ++i) {
			this.setSelectedIndex(selectedIndices[i], selected);
		}
		// End temporary suppression.
		suppressChangeNotifications = false;
		fireValueChanged();
	}

	/**
	 * @see nextapp.echo2.app.list.ListSelectionModel#setSelectedIndex(int,
	 *      boolean)
	 */
	private void setSelectedUnsortedIndex(int unsortedIndex, boolean newValue) {
		boolean oldValue = selection.get(unsortedIndex);

		if (newValue ^ oldValue) {
			if (newValue) {
				if (selectionMode == SINGLE_SELECTION && getMinSelectedIndex() != -1) {
					setSelectedUnsortedIndex(minSelectedUnsortedIndex, false);
				}
				selection.set(unsortedIndex);
				if (unsortedIndex < minSelectedUnsortedIndex || minSelectedUnsortedIndex == -1) {
					minSelectedUnsortedIndex = unsortedIndex;
				}
			} else {
				selection.clear(unsortedIndex);
				if (unsortedIndex == minSelectedUnsortedIndex) {
					// Minimum selected index has been deselected, find new
					// minimum selected index.
					int max = selection.length() - 1;
					minSelectedUnsortedIndex = -1;
					for (int i = 0; i <= max; ++i) {
						if (selection.get(i)) {
							minSelectedUnsortedIndex = i;
							break;
						}
					}
				}
			}
			fireValueChanged();
		}
	}

	/**
	 * @see nextapp.echo2.app.list.ListSelectionModel#setSelectionMode(int)
	 */
	public void setSelectionMode(int selectionMode) {
		if (selectionMode != MULTIPLE_SELECTION && this.selectionMode == MULTIPLE_SELECTION) {
			// deselect all but first selected element.
			int maxSelectedIndex = selection.length() - 1;
			for (int i = minSelectedUnsortedIndex + 1; i <= maxSelectedIndex; ++i) {
				setSelectedUnsortedIndex(i, false);
			}
		}
		this.selectionMode = selectionMode;
		fireValueChanged();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer("(in model space) ");
		if (this.tableModel != null) {
			for (int i = 0, len = this.tableModel.getRowCount(); i < len; i++) {
				if (selection.get(i)) {
					buf.append(i);
					buf.append(", ");
				}
			}
		}
		return buf.toString();
	}

	/**
	 * @see echopointng.list.ListSelectionModelEx#getSelectedIndices()
	 */
	public int[] getSelectedIndices() {
		int rowCount = tableModel.getRowCount();
		List selectedIndexList = new ArrayList();
		for (int modelRowIndex = 0; modelRowIndex < selection.length(); ++modelRowIndex) {
			try {
				int viewRowIndex = toSortedViewRowIndex(modelRowIndex);
				viewRowIndex = toPagedViewRowIndex(viewRowIndex);
				// now double check that the view index is viable, eg >= 0 <
				// rowCount
				if (viewRowIndex >= 0 && viewRowIndex < rowCount) {
					if (selection.get(modelRowIndex)) {
						selectedIndexList.add(new Integer(viewRowIndex));
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			} // table size changed - ignore
		}
		return toIntArrayFromIntegerList(selectedIndexList);
	}

	/**
	 * @see echopointng.list.ListSelectionModelEx#getSelectedModelIndices()
	 */
	public int[] getSelectedModelIndices() {
		List selectedIndexList = new ArrayList();
		for (int modelRowIndex = 0; modelRowIndex < selection.length(); ++modelRowIndex) {
			try {
				if (selection.get(modelRowIndex)) {
					selectedIndexList.add(new Integer(modelRowIndex));
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			} // table size changed - ignore
		}
		return toIntArrayFromIntegerList(selectedIndexList);
	}

	private int[] toIntArrayFromIntegerList(List integerList) {
		if (integerList == null) {
			return new int[0];
		}
		int ints[] = new int[integerList.size()];
		for (int i = 0; i < integerList.size(); i++) {
			ints[i] = ((Integer) integerList.get(i)).intValue();
		}
		return ints;
	}
}