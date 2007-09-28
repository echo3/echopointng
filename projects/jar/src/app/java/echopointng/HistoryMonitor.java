package echopointng;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.event.EventListenerList;
import echopointng.history.HistoryEvent;
import echopointng.history.HistoryEventListener;
import echopointng.history.HistoryState;
import echopointng.history.HistoryUndoRedo;

/**
 * A <code>HistoryMonitor</code> can be used to monitor history events that
 * are generated when the user presses the back and forward browser buttons.
 * <p>
 * History is represented as a series of <code>HistoryState</code> objects
 * that represent what is needed to undo/redo the current application state.
 * They must implement the <code>HistoryState</code> interface.
 * <p>
 * If the <code>HistoryState</code> object also implements
 * <code>HistoryUndoRedo</code>, then the appropriate <code>undo()</code>
 * or <code>redo()</code> method will be called when a history event occurs.
 * <p>
 * NOTE : These methods will be called before the
 * <code>HistoryEventListener</code>s are invoked.
 * <p>
 * This component keeps track of the <b>current</b> <code>HistoryState</code>,
 * which is null at the start and then moves along with each
 * <code>HistoryState</code> that is added. When the user presses the back
 * button the current <code>HistoryState</code> will move backwards in time.
 * Conversely it can move forward in time when the user presses the forward
 * button.
 * <p>
 * This class is EXPERIMENTAL and no gaurantees are made about its make up or
 * class signature in the future.
 */
public class HistoryMonitor extends Component {

	public static final String HISTORY_CHANGED_PROPERTY = "historyChanged";

	/**
	 * The EventListenerList containing <code>HistoryEventListener</code>'s
	 */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * The list of <code>HistoryState</code> objects
	 */
	protected List historyStateList;

	/**
	 * The pending list of <code>HistoryState</code> objects not yet processed
	 * by the rendering peer.
	 */
	protected List pendingHistoryStateList;

	private int currentHistoryIndex;

	/**
	 * Constructs an <code>HistoryMonitor</code>
	 */
	public HistoryMonitor() {
		historyStateList = generateHistoryList();
		pendingHistoryStateList = new ArrayList();
		currentHistoryIndex = -1;
	}

	/**
	 * This is called to instantiate this List to be used to hold
	 * <code>HistoryState</code> information. The default implenetation is an
	 * unbounded <code>List</code> but subclasses could use another bounded
	 * <code>List</code> implementation and hence keep a cap on how much
	 * history information is kept.
	 * <p>
	 * Note that if a bounded <code>List</code> is used, then it is possible
	 * for the user to go back in history past a point where the application has
	 * history.
	 * 
	 * @return a <code>List</code> to place <code>HistoryState</code>
	 *         information
	 */
	protected List generateHistoryList() {
		return new ArrayList();
	}

	/**
	 * Adds an <code>HistoryEventListener</code> to the
	 * <code>HistoryMonitor</code>.
	 * 
	 * @param l
	 *            the HistoryEventListener to add
	 */
	public void addHistoryEventListener(echopointng.history.HistoryEventListener l) {
		listenerList.addListener(HistoryEventListener.class, l);
	}

	/**
	 * Removes an <code>HistoryEventListener</code> from the
	 * <code>HistoryMonitor</code>
	 * 
	 * @param l
	 *            the HistoryEventListener to remove
	 */
	public void removeHistoryEventListener(HistoryEventListener l) {
		listenerList.removeListener(HistoryEventListener.class, l);
	}

	private void validate(HistoryState historyState) {
		if (historyState == null) {
			throw new IllegalArgumentException("The HistoryState must be not be null!");
		}
		String historyHash = historyState.historyHash();
		if (historyHash == null) {
			throw new IllegalArgumentException("The HistoryState historyHash must be non null");
		}
		int index = getIndexOfHistory(historyHash);
		if (index != -1) {
			throw new IllegalStateException("A HistoryState object is already in the history cache with the historyHash : "
					+ historyState.historyHash());
		}
	}

	/**
	 * This adds a new <code>HistoryState</code> object. When the user presses
	 * the forward or back button then this <code>HistoryState</code> object
	 * can be presented back inside a HistoryEvent.
	 * 
	 * @param historyState -
	 *            the <code>HistoryState</code> to add
	 * 
	 * @throws IllegalArgumentException -
	 *             if the historyState is null or its historyHash is null
	 * 
	 * @throws IllegalStateException -
	 *             if the underRedo has a historyHash that has been previosuly
	 *             added (this must be unique)
	 * 
	 */
	public void addHistory(HistoryState historyState) {
		validate(historyState);
		synchronized (pendingHistoryStateList) {
			historyStateList.add(historyState);
			pendingHistoryStateList.add(historyState);
			//
			// if our current history undoredo is at the end of the list
			// already then increment otherwise leave it alone
			if (currentHistoryIndex == (historyStateList.size() - 2)) {
				currentHistoryIndex = historyStateList.size() - 1;
			}
		}
		firePropertyChange(HISTORY_CHANGED_PROPERTY, null, null);
	}

	/**
	 * @return returns an unmodifiable List of the HistoryState objects in the
	 *         <code>HistoryMonitor</code>
	 */
	public List getHistory() {
		return Collections.unmodifiableList(historyStateList);
	}

	/**
	 * Clears away any history from the HistoryMonitor
	 */
	public void clearHistory() {
		synchronized (pendingHistoryStateList) {
			historyStateList.clear();
			pendingHistoryStateList.clear();
			currentHistoryIndex = -1;
		}
	}

	/**
	 * Tihs will clear any history from that is in front of the given history
	 * state. if the current history state was in the cleared list, then the
	 * specified historyState will become the current state.
	 * 
	 * @param historyState -
	 *            the history to clear in front of
	 */
	public void clearHistoryInFrontOf(HistoryState historyState) {
		validate(historyState);

		int index = getIndexOfHistory(historyState.historyHash());
		synchronized (pendingHistoryStateList) {
			for (int i = historyStateList.size() - 1; i > index; i--) {
				HistoryState state = (HistoryState) historyStateList.get(i);
				if (pendingHistoryStateList.contains(state)) {
					pendingHistoryStateList.remove(state);
				}
				historyStateList.remove(i);
			}
			if (currentHistoryIndex > index) {
				currentHistoryIndex = index;
			}
		}
	}

	/**
	 * @return the current HistoryState object or null if there isnt one
	 */
	public HistoryState getCurrent() {
		if (currentHistoryIndex >= 0) {
			return (HistoryState) historyStateList.get(currentHistoryIndex);
		}
		return null;
	}

	/**
	 * This returns the list of <code>HistoryState</code> objects that have
	 * been added to the <code>HistoryMonitor</code> but that have not been
	 * processed by its rendering peer. This method is only designed to be
	 * called by the <code>HistoryMonitor</code> rendering peer and not by
	 * code in general.
	 * 
	 * @return - the pending list of history objects
	 */
	public List getPendingHistory() {
		synchronized (pendingHistoryStateList) {
			List newPendingList = new ArrayList(pendingHistoryStateList);
			pendingHistoryStateList.clear();
			return newPendingList;
		}
	}

	/**
	 * Returns the index of the given <code>HistoryState</code> in the history
	 * list with the specified historyHash.
	 * 
	 * @param historyHash -
	 *            the one to search for
	 * @return the index or -1 if it cant be found
	 */
	protected int getIndexOfHistory(String historyHash) {
		for (int i = historyStateList.size() - 1; i >= 0; i--) {
			HistoryState testUndoRedo = (HistoryState) historyStateList.get(i);
			if (testUndoRedo.historyHash().equals(historyHash)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns an array of history between the given startIndex and the
	 * endIndex.
	 * <p>
	 * If endIndex is before startIndex (ie an undo), then the array returned is
	 * from endIndex backwards excluding startIndex.
	 * <p>
	 * Conversely if endIndex is after startIndex (ie a redo), then the array
	 * returned is from startIndex forward exclusing startIndex.
	 * 
	 * @return the array of history objects between indexes.
	 */
	protected HistoryState[] getHistoryBetween(int startIndex, int endIndex) {
		List tempList = new ArrayList();
		if (endIndex < startIndex) {
			// undo
			for (int i = startIndex; i > endIndex; i--) {
				HistoryState historyState = (HistoryState) historyStateList.get(i);
				tempList.add(historyState);
			}
		} else {
			// redo
			for (int i = startIndex+1; i <= endIndex; i++) {
				HistoryState historyState = (HistoryState) historyStateList.get(i);
				tempList.add(historyState);
			}
		}
		return (HistoryState[]) tempList.toArray(new HistoryState[tempList.size()]);
	}

	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		super.processInput(inputName, inputValue);
		String historyHash = String.valueOf(inputValue);
		//
		// So we are going to come in with a historyHash of the state we want to
		// go
		// to ==>. So depending on the direction (undo or redo) we need to
		// "play" every
		// history object we find between where we are (eg the current one) and
		// where we want
		// to go to (eg the one for the specific hash)
		int startIndex = currentHistoryIndex;
		int endIndex = getIndexOfHistory(historyHash);
		boolean isRedo;
		if (endIndex < startIndex) {
			isRedo = false;
		} else {
			isRedo = true;
		}
		//
		// this is where we are from now on
		currentHistoryIndex = endIndex;
		//
		// now for every event between where we are and where we want to go
		// (which should only be one object but lets cater for more than 1)
		//
		HistoryState historyStates[] = getHistoryBetween(startIndex, endIndex);
		for (int i = 0; i < historyStates.length; i++) {
			HistoryState historyState = historyStates[i];
			//
			// call the redo/undo methods if they are available
			if (historyState instanceof HistoryUndoRedo) {
				HistoryUndoRedo undoRedo = (HistoryUndoRedo) historyState;
				if (isRedo) {
					undoRedo.redo();
				} else {
					undoRedo.undo();
				}
			}
			fireEventListeners(new HistoryEvent(this, historyState), true, isRedo);
		}
		//
		// ok did we find anything for where we want to go
		if (endIndex == -1) {
			fireEventListeners(new HistoryEvent(this, null), false, false /*
																			 * <<==
																			 * irrelevant
																			 */);
		}
	}

	private void fireEventListeners(HistoryEvent historyEvent, boolean foundHistory, boolean isRedo) {
		EventListener[] listeners = listenerList.getListeners(HistoryEventListener.class);
		for (int index = 0; index < listeners.length; ++index) {
			HistoryEventListener listener = (HistoryEventListener) listeners[index];
			if (foundHistory) {
				if (isRedo) {
					listener.onRedo(historyEvent);
				} else {
					listener.onUndo(historyEvent);
				}
			} else {
				listener.onNoHistoryAvailable(historyEvent);
			}
		}
	}
}
