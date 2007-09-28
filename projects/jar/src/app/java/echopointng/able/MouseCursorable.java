package echopointng.able;
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


/**
 * The <code>MouseCursorable</code> interface is used to set a 
 * new cursor value when the mouse is moved over a component.
 */
public interface MouseCursorable  extends Delegateable  {

    public static final String PROPERTY_MOUSE_CURSOR = "mouseCursor";
    public static final String PROPERTY_MOUSE_CURSOR_URI = "mouseCursorUri";
	
	/** 
	 * The UA determines the cursor to display based on the current context. 
	 * This is the starting value for MouseCursorables 
	 */
	public static final int CURSOR_AUTO = 0;
	
	/** The platform-dependent default cursor. Often rendered as an arrow.  */
	public static final int CURSOR_DEFAULT = 1;

	/** A simple crosshair (e.g., short line segments resembling a "+" sign). */
	public static final int CURSOR_CROSSHAIR = 2;
	
	/** The cursor is a pointer that indicates a link. */
	public static final int CURSOR_POINTER = 3;
	
	/** Indicates something is to be moved. */
	public static final int CURSOR_MOVE = 4;
	
	/** Used when the movement starts from the east corner of the box. */
	public static final int CURSOR_E_RESIZE = 5;
	
	/** Used when the movement starts from the northeast corner of the box. */
	public static final int CURSOR_NE_RESIZE = 6;
	
	/** Used when the movement starts from the northwest corner of the box. */
	public static final int CURSOR_NW_RESIZE = 7;
	
	/** Used when the movement starts from the north corner of the box. */
	public static final int CURSOR_N_RESIZE = 8;

	/** Used when the movement starts from the southeast corner of the box. */
	public static final int CURSOR_SE_RESIZE = 9;

	/** Used when the movement starts from the southwest corner of the box. */
	public static final int CURSOR_SW_RESIZE = 10;

	/** Used when the movement starts from the south corner of the box. */
	public static final int CURSOR_S_RESIZE = 11;

	/** Used when the movement starts from the west corner of the box. */
	public static final int CURSOR_W_RESIZE = 12;

	/** Indicates text that may be selected. Often rendered as an I-bar. */
	public static final int CURSOR_TEXT = 13;

	/** Indicates that the program is busy and the user should wait. Often rendered as a watch or hourglass. */
	public static final int CURSOR_WAIT = 14;

	/** Help is available for the object under the cursor. Often rendered as a question mark or a balloon. */
	public static final int CURSOR_HELP = 15;

	/** The user agent retrieves the cursor from the resource designated by the URI. */
	public static final int CURSOR_CUSTOM_URI = 16;
	
	/**
	 * Returns the current mouse cursor in use.  This will be one of the following values.
	 * <ul>
	 *	<li>CURSOR_DEFAULT</li>
	 *	<li>CURSOR_AUTO</li>
	 *	<li>CURSOR_CROSSHAIR</li>
	 *	<li>CURSOR_POINTER</li>
	 *	<li>CURSOR_MOVE</li>
	 *	<li>CURSOR_E_RESIZE</li>
	 *	<li>CURSOR_NE_RESIZE</li>
	 *	<li>CURSOR_NW_RESIZE</li>
	 *	<li>CURSOR_N_RESIZE</li>
	 *	<li>CURSOR_SE_RESIZE</li>
	 *	<li>CURSOR_SW_RESIZE</li>
	 *	<li>CURSOR_S_RESIZE</li>
	 *	<li>CURSOR_W_RESIZE</li>
	 *	<li>CURSOR_TEXT</li>
	 *	<li>CURSOR_WAIT</li>
	 *	<li>CURSOR_HELP</li>				
	 *	<li>CURSOR_CUSTOM</li>				
	 * </ul>
	 * @return The current mouse cursor in use.
	 */
	public int getMouseCursor();
	
	/**
	 * @return The custom URI for the mouse cursor or null
	 */
	public String getMouseCursorUri();
	
	/**
	 * Sets the mouse cursor to use.  This will be one of the following values.
	 * <ul>
	 *	<li>CURSOR_DEFAULT</li>
	 *	<li>CURSOR_AUTO</li>
	 *	<li>CURSOR_CROSSHAIR</li>
	 *	<li>CURSOR_POINTER</li>
	 *	<li>CURSOR_MOVE</li>
	 *	<li>CURSOR_E_RESIZE</li>
	 *	<li>CURSOR_NE_RESIZE</li>
	 *	<li>CURSOR_NW_RESIZE</li>
	 *	<li>CURSOR_N_RESIZE</li>
	 *	<li>CURSOR_SE_RESIZE</li>
	 *	<li>CURSOR_SW_RESIZE</li>
	 *	<li>CURSOR_S_RESIZE</li>
	 *	<li>CURSOR_W_RESIZE</li>
	 *	<li>CURSOR_TEXT</li>
	 *	<li>CURSOR_WAIT</li>
	 *	<li>CURSOR_HELP</li>				
	 *	<li>CURSOR_CUSTOM</li>				
	 * </ul>
	 * @param mouseCursor - the mouse cursor to use
	 */
	public void setMouseCursor(int mouseCursor);
	
	/**
	 * Sets the URI to a custom platform mouse cursor.  This will only
	 * be used if the mosue cusor is set to CURSOR_CUSTOM.				
	 *  
	 * @param mouseCursorUri the URI to a custom platform mouse cursor.
	 */
	public void setMouseCursorUri(String mouseCursorUri);
}
