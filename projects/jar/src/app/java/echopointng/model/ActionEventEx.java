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

import nextapp.echo2.app.event.ActionEvent;

/**
 * ActionEventEx extends ActionEvent and provides information about what meta
 * key was pressed at the time the user raised this event.
 * 
 */
public class ActionEventEx extends ActionEvent {

	/**
	 * Used to indicate that NO meta key was pressed during the event.
	 */
	public static final int METAKEY_NONE = 0;

	/**
	 * Used to indicate whether the 'alt' key was depressed during the firing of
	 * the event. On some platforms this key may map to an alternative key name.
	 */
	public static final int METAKEY_ALT = 1;

	/**
	 * Used to indicate whether the 'shift' key was depressed during the firing
	 * of the event.
	 */
	public static final int METAKEY_SHIFT = 2;

	/**
	 * Used to indicate whether the 'ctrl' key was depressed during the firing
	 * of the event.
	 */
	public static final int METAKEY_CONTROL = 4;

	/**
	 * Used to indicate whether the 'meta' key was depressed during the firing
	 * of the event. On some platforms this key may map to an alternative key
	 * name.
	 */
	public static final int METAKEY_META = 8;

	private int metaKeyInfo;

	/**
	 * Constructs a <code>ActionEventEx</code>
	 * 
	 * @param source -
	 *            the source of the event
	 * @param command -
	 *            the command associated with the event
	 */
	public ActionEventEx(Object source, String command) {
		this(source, command, 0);
	}

	/**
	 * Constructs a <code>ActionEventEx</code>
	 * 
	 * @param source -
	 *            the source of the event
	 * @param command -
	 *            the command associated with the event
	 * @param metaKeyInfo -
	 *            the metaKey information in play when the event was raised.
	 *            This can be one of the following
	 *            <ul>
	 *            <li>METAKEY_NONE - no meta keys were pressed during the
	 *            action event</li>
	 *            <li>METAKEY_ALT - the 'alt' key was pressed during the action
	 *            event</li>
	 *            <li>METAKEY_SHIFT - the 'shift' key was pressed during the
	 *            action event</li>
	 *            <li>METAKEY_CONTROL - the 'control' key was pressed during
	 *            the action event</li>
	 *            <li>METAKEY_META - the 'meta' key was pressed during the
	 *            action event</li>
	 *            </ul>
	 */
	public ActionEventEx(Object source, String command, int metaKeyInfo) {
		super(source, command);
		this.metaKeyInfo = metaKeyInfo;
	}

	/**
	 * @return the meta keys in play when the action was raised. This can be one
	 *         of the following
	 *         <ul>
	 *         <li>METAKEY_NONE - no meta keys were pressed during the action
	 *         event</li>
	 *         <li>METAKEY_ALT - the 'alt' key was pressed during the action
	 *         event</li>
	 *         <li>METAKEY_SHIFT - the 'shift' key was pressed during the
	 *         action event</li>
	 *         <li>METAKEY_CONTROL - the 'control' key was pressed during the
	 *         action event</li>
	 *         <li>METAKEY_META - the 'meta' key was pressed during the action
	 *         event</li>
	 *         </ul>
	 */
	public int getMetaKeyInfo() {
		return metaKeyInfo;
	}
}
