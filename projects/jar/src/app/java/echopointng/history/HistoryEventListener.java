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

import java.util.EventListener;

/**
 * The <code>HistoryEventListener</code> interface is used to listen for
 * <code>HistoryEvent</code>'s.
 * 
 * @see echopointng.history.HistoryEvent
 * 
 */
public interface HistoryEventListener extends EventListener {

	/**
	 * This is called when an history event has been raised to <b>redo</b> some
	 * application state, eg go forwards in application state.
	 * <p>
	 * NOTE : if the <code>HistoryState</code> (associated with the
	 * HistoryEvent) implements <code>HistoryUndoRedo</code>, then the
	 * <code>redo()</code> method will have already been called before this
	 * event listener method is called.
	 * 
	 * @param historyEvent -
	 *            contains the event parameters
	 * 
	 * @see echopointng.history.HistoryEvent
	 */
	public void onRedo(HistoryEvent historyEvent);

	/**
	 * This is called when an history event has been raised to <b>undo</b> some
	 * application state, eg go backwards in application state.
	 * <p>
	 * NOTE : if the <code>HistoryState</code> (associated with the
	 * HistoryEvent) implements <code>HistoryUndoRedo</code>, then the
	 * <code>undo()</code> method will have already been called before this
	 * event listener method is called.
	 * 
	 * @param historyEvent -
	 *            contains the event parameters
	 * 
	 * @see echopointng.history.HistoryEvent
	 */
	public void onUndo(HistoryEvent historyEvent);

	/**
	 * This is called when an history event has been raised but the
	 * <code>HistoryMonitor</code> could not find any
	 * <code>HistoryState</code> state for that event.
	 * <p>
	 * This can happen because the list of history objects can have an upper
	 * bound or because the history list can be cleared.
	 * <p>
	 * In this case the <code>HistoryEvent.getHistoryState()</code> method
	 * will return null.
	 * 
	 * @param historyEvent -
	 *            contains the event parameters but with a null
	 *            <code>HistoryState</code> object attached.
	 * 
	 * @see echopointng.history.HistoryEvent
	 */
	public void onNoHistoryAvailable(HistoryEvent historyEvent);

}
