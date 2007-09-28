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

import java.util.Calendar;

/**
 * <code>CalendarSelectionModel</code> is a model interface for calendar selection. The displayed date is the date
 * currently displayed by the UI as the user browses around a calendar. The
 * selected date is the date they may have "clicked" onm that is selected.
 */
public interface CalendarSelectionModel {
	/**
	 * Adds <I>listener</I> as a listener to changes in the model.
	 */
	public void addListener(CalendarSelectionListener listener);

	/**
	 * @return the model's displayed date. This is the date that is "displayed"
	 *         by the component using this model. As the user browses through
	 *         the Calendar UI, the model's displayed date can change without
	 *         the model's selected date changing per se.
	 */
	public Calendar getDisplayedDate();

	/**
	 * @return the model's selected date. This is the date that is "selected" by
	 *         the component using this model. When the user clicks on a date in
	 *         the Calendar UI the selected date is changed.
	 */
	public Calendar getSelectedDate();

	/**
	 * Removes <I>listener</I> as a listener to changes in the model.
	 */
	public void removeListener(CalendarSelectionListener listener);

	/**
	 * Sets the model's displayed date to <I>cal</I>.
	 * 
	 * Notifies any listeners if the model changes
	 * 
	 */
	public void setDisplayedDate(Calendar cal);

	/**
	 * Sets the model's selected date to <I>cal</I>.
	 * 
	 * Notifies any listeners if the model changes
	 * 
	 */
	public void setSelectedDate(Calendar cal);

	/**
	 * This is the equivalent of calling setSelectedDate() and
	 * setDisplayedDate() except it only raises change events after the both
	 * have been set.
	 * 
	 */
	public void setDates(Calendar selectedDate, Calendar displayedDate);
}
