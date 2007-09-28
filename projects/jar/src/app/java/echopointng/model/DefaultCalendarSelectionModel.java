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
import java.util.Calendar;
import java.util.EventListener;

import nextapp.echo2.app.event.EventListenerList;
import echopointng.DateChooser;

/**
 * A generic implementation of <code>CalendarSelectionModel</code>.
 * 
 */
public class DefaultCalendarSelectionModel implements CalendarSelectionModel, Serializable {

	private Calendar displayedDate;

	protected EventListenerList listenerList = new EventListenerList();

	private Calendar selectedDate;

	/*
	 * Copies the provided calendar and hence is another object
	 */
	private Calendar copyCal(Calendar cal) {
		return DateChooser.calendarCopy(cal);
	}

	/**
	 * The creates a CalendarSelectionModel where <b>selectedDate</b> to set to
	 * <code>cal</code> and <b>displayedDate</b> is set to the first day of
	 * the month of <code>cal</code>.
	 * 
	 * @param cal -
	 *            the Calendar to use.
	 */
	public DefaultCalendarSelectionModel(Calendar cal) {
		super();
		selectedDate = copyCal(cal);
		displayedDate = copyCal(cal);
		displayedDate.set(Calendar.DAY_OF_MONTH, 1);
	}

	/**
	 * Adds a <code>ChangeListener</code> to the model.
	 * 
	 * @param l
	 *            The <code>ChangeListener</code> to be added.
	 */
	public void addListener(CalendarSelectionListener l) {
		listenerList.addListener(CalendarSelectionListener.class, l);
	}

	private void fireSelectedChanged() {
		EventListener[] listeners = listenerList.getListeners(CalendarSelectionListener.class);
		CalendarEvent calEvent = new CalendarEvent(this, this.selectedDate);
		for (int index = 0; index < listeners.length; ++index) {
			((CalendarSelectionListener) listeners[index]).selectedDateChange(calEvent);
		}
	}

	private void fireDisplayedChanged() {
		EventListener[] listeners = listenerList.getListeners(CalendarSelectionListener.class);
		CalendarEvent calEvent = new CalendarEvent(this, this.displayedDate);
		for (int index = 0; index < listeners.length; ++index) {
			((CalendarSelectionListener) listeners[index]).displayedDateChange(calEvent);
		}
	}

	/**
	 * @see echopointng.model.CalendarSelectionModel#getDisplayedDate()
	 */
	public Calendar getDisplayedDate() {
		return displayedDate;
	}

	/**
	 * @see echopointng.model.CalendarSelectionModel#getSelectedDate()
	 */
	public Calendar getSelectedDate() {
		return selectedDate;
	}

	/**
	 * Removes a <code>CalendarSelectionListener</code> from the model.
	 * 
	 * @param l
	 *            The <code>CalendarSelectionListener</code> to be removed.
	 */
	public void removeListener(CalendarSelectionListener l) {
		listenerList.removeListener(CalendarSelectionListener.class, l);
	}

	/**
	 * @see echopointng.model.CalendarSelectionModel#setDisplayedDate(java.util.Calendar)
	 */
	public void setDisplayedDate(Calendar cal) {
		if (!displayedDate.equals(cal)) {
			displayedDate = copyCal(cal);
			fireDisplayedChanged();
		}
	}

	/**
	 * @see echopointng.model.CalendarSelectionModel#setSelectedDate(java.util.Calendar)
	 */
	public void setSelectedDate(Calendar cal) {
		if ((cal != null && selectedDate == null) || (cal == null && selectedDate != null) || (selectedDate != null && !selectedDate.equals(cal))) {
			selectedDate = copyCal(cal);
			fireSelectedChanged();
		}
	}

	/**
	 * @see echopointng.model.CalendarSelectionModel#setDates(java.util.Calendar,
	 *      java.util.Calendar)
	 */
	public void setDates(Calendar newSelectedDate, Calendar newDisplayedDate) {
		boolean selectedSet = false;
		boolean displayedSet = false;
		if ((newSelectedDate != null && selectedDate == null) || !selectedDate.equals(newSelectedDate)) {
			selectedDate = copyCal(newSelectedDate);
			selectedSet = true;
		}
		if ((newDisplayedDate != null && displayedDate == null) || !displayedDate.equals(newDisplayedDate)) {
			displayedDate = copyCal(newDisplayedDate);
			displayedSet = true;
		}
		if (selectedSet) {
			fireSelectedChanged();
		}
		if (displayedSet) {
			fireDisplayedChanged();
		}

	}
}
