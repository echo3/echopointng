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
package echopointng.history;

import java.io.Serializable;

/**
 * <code>HistoryState</code> is a interface that represents an application state at a
 * given point of time.
 * <p>
 * The objects that implement this interface will typically contain state
 * information that allows an application to go backwards (ie undo some actions)
 * and go forwards (ie redo some actions).
 * 
 * @see echopointng.HistoryMonitor
 * @see echopointng.history.HistoryEvent
 * @see echopointng.history.HistoryEventListener
 */
public interface HistoryState extends Serializable {

	/**
	 * This is a unique <code>history hash </code> value that can be used to uniquely identify
	 * this particular slice of application state.  This method should follow the same semantics
	 * as <code>Object.hashCode()</code> especially in regards to how it acts when used as the 
	 * key of a <code>java.util.Map</code>.
	 * <p>
	 * The value used be will be display to the user on their browser as indication of where they are 
	 * inside the application.  Therefore there are some size contrictions on the length of this string
	 * which is the maximum size of a URL parameter in the users browser.  To be safe use fairly compact 
	 * strings (< 256 chars say)
	 * <p>
	 * This is method is defined as a String to allow more meaningful state to be displayed
	 * and also to allow possible "encoding" of application state in the hashCode itself.
	 * <p>
	 * The most trivial implementation fo this method would be
	 * <pre>
	 *    public String historyHash() {
	 *       return String.valueOf(this.hashCode());
	 *    }
	 * </pre>
	 * <p>
	 * A more human friendly implementation might be something like this
	 * <pre>
	 * public String historyHash() {
	 *    return whatAreWeDoing + "-" + System.identityHashCode(new Object());
	 * }
	 * </pre>
	 * 
	 * @return - a unique history hash code
	 */
	public String historyHash();

}
