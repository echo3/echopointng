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
package echopointng.model;

import java.util.EventListener;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.event.EventListenerList;

/**
 * A default implementation of <code>ColorSelectionModel</code>
 */
public class DefaultColorSelectionModel implements ColorSelectionModel {

	private Color selectedColor;

	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * @see echopointng.model.ColorSelectionModel#addListener(nextapp.echo2.app.event.ChangeListener)
	 */
	public void addListener(ChangeListener listener) {
		listenerList.addListener(CalendarSelectionListener.class, listener);
	}

	/**
	 * @see echopointng.model.ColorSelectionModel#getSelectedColor()
	 */
	public Color getSelectedColor() {
		return selectedColor;
	}

	/**
	 * @see echopointng.model.ColorSelectionModel#removeListener(nextapp.echo2.app.event.ChangeListener)
	 */
	public void removeListener(ChangeListener listener) {
		listenerList.removeListener(CalendarSelectionListener.class, listener);
	}

	/**
	 * @see echopointng.model.ColorSelectionModel#setSelectedColor(nextapp.echo2.app.Color)
	 */
	public void setSelectedColor(Color newValue) {
		Color oldValue = this.selectedColor;
		this.selectedColor = newValue;
		if (oldValue != newValue) {
			if (oldValue != null && !oldValue.equals(newValue)) {
				fireStateChanged();
			} else if (newValue != null && !newValue.equals(oldValue)) {
				fireStateChanged();
			}
		}
	}

	private void fireStateChanged() {
		EventListener[] listeners = listenerList
				.getListeners(CalendarSelectionListener.class);
		ChangeEvent changeEvent = new ChangeEvent(this);
		for (int index = 0; index < listeners.length; ++index) {
			((ChangeListener) listeners[index]).stateChanged(changeEvent);
		}
	}

}
