package echopointng.history;

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

import java.util.EventObject;

/**
 * <code>HistoryEvent</code> represents an event that has been raised when the
 * user clicks on the back and forward button.
 */
public class HistoryEvent extends EventObject {

	private HistoryState historyState;

	/**
	 * Creates an HistoryEvent.
	 * 
	 * @param source
	 *            the source of the event
	 * @param historyState
	 *            the history state associated with this event
	 */
	public HistoryEvent(Object source, HistoryState historyState) {
		super(source);
		this.historyState = historyState;
	}

	/**
	 * Returns the <code>HistoryState</code> object associated with this
	 * <code>HistoryEvent</code>. If this is null it could be that a history
	 * event has been raised by the associated state information is no longer
	 * available. This can happen if the maximum number of HistoryState objects
	 * is restricted.
	 * 
	 * @return the <code>HistoryState</code> object associated with this
	 *         <code>HistoryEvent</code>.
	 */
	public HistoryState getHistoryState() {
		return historyState;
	}
}
