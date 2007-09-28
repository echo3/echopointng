package echopointng.model;

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

import java.io.Serializable;
import java.util.EventListener;

import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.event.EventListenerList;

/**
 * A generic implementation of SingleSelectionModel.
 * <p>
 * This class will perform an optimisation check when the selected index is set
 * and will only fire a ChangeEvent if the index actually changes from the
 * previous value.
 *  
 */
public class DefaultSingleSelectionModel implements SingleSelectionModel, Serializable {

	/**
	 * Only one ChangeEvent is needed per model instance since the event's only
	 * (read-only) state is the source property. The source of events generated
	 * here is always "this".
	 */
	protected transient ChangeEvent changeEvent = null;

	protected EventListenerList listenerList = new EventListenerList();

	private int selectedIndex = -1;

	/**
	 * Initializes selectedIndex to <code>-1</code>
	 */
	public DefaultSingleSelectionModel() {
		selectedIndex = -1;
	}

	/**
	 * Initializes selectedIndex to <I>index </I>
	 */
	public DefaultSingleSelectionModel(int index) {
		selectedIndex = index;
	}

	/**
	 * @see echopointng.model.SingleSelectionModel#addChangeListener(nextapp.echo2.app.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener l) {
		listenerList.addListener(ChangeListener.class, l);
	}

	/**
	 * @see echopointng.model.SingleSelectionModel#clearSelection()
	 */
	public void clearSelection() {
		setSelectedIndex(-1);
	}

	/**
	 * Notifies all listeners that have registered for this event type.
	 */
	public void fireStateChanged() {
		EventListener[] listeners = listenerList.getListeners(ChangeListener.class);
		if (changeEvent == null) {
			changeEvent = new ChangeEvent(this);
		}
		for (int index = 0; index < listeners.length; ++index) {
			((ChangeListener) listeners[index]).stateChanged(changeEvent);
		}
	}

	/**
	 * @see echopointng.model.SingleSelectionModel#getSelectedIndex()
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * @see echopointng.model.SingleSelectionModel#isSelected()
	 */
	public boolean isSelected() {
		boolean ret = false;
		if (getSelectedIndex() != -1) {
			ret = true;
		}
		return ret;
	}

	/**
	 * @see echopointng.model.SingleSelectionModel#removeChangeListener(nextapp.echo2.app.event.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener l) {
		listenerList.removeListener(ChangeListener.class, l);
	}

	/**
	 * @see echopointng.model.SingleSelectionModel#setSelectedIndex(int)
	 */
	public void setSelectedIndex(int index) {
		if (selectedIndex != index) {
			selectedIndex = index;
			fireStateChanged();
		}
	}
}
