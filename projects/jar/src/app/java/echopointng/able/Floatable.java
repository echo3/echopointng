package echopointng.able;

/*
 * This file is part of the Echo Point Project. This project is a collection of Components that have extended the Echo
 * Web Application Framework.
 * 
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version 1.1 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for the specific language governing rights and limitations under the License.
 * 
 * Alternatively, the contents of this file may be used under the terms of either the GNU General Public License Version
 * 2 or later (the "GPL"), or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in which case the
 * provisions of the GPL or the LGPL are applicable instead of those above. If you wish to allow use of your version of
 * this file only under the terms of either the GPL or the LGPL, and not to allow others to use your version of this
 * file under the terms of the MPL, indicate your decision by deleting the provisions above and replace them with the
 * notice and other provisions required by the GPL or the LGPL. If you do not delete the provisions above, a recipient
 * may use your version of this file under the terms of any one of the MPL, the GPL or the LGPL.
 */

/**
 * A <code>Floatable</code> is a component that can be "floated" to the left or right within another component.
 * Adjacent components will flow down the right side of a left-floated box and down the left side of a right-floated
 * box.
 * <p>
 * A Floatable may also "clear" adjacent floated Components. Setting the clearFloat property will ensure that the
 * Component will be rendered on a new line from adjacent floated Components.
 * <p>
 * @see "http://www.w3.org/TR/CSS21/visuren.html#floats"
 */
public interface Floatable
		extends Delegateable {

	public static final String PROPERTY_FLOAT = "float";
	public static final String PROPERTY_CLEAR_FLOAT = "clearfloat";

	public static final int FLOAT_NONE = 0;
	public static final int FLOAT_LEFT = 1;
	public static final int FLOAT_RIGHT = 2;

	public static final int FLOAT_CLEAR_NONE = 10;
	public static final int FLOAT_CLEAR_LEFT = 11;
	public static final int FLOAT_CLEAR_RIGHT = 12;
	public static final int FLOAT_CLEAR_BOTH = 13;


	/**
	 * @return the float direction of the <code>Floatable</code>.
	 */
	public int getFloat();


	/**
	 * Sets the float direction of the <code>Floatable</code>.
	 * 
	 * @param newValue - the new float direction. Must be one of:
	 *            <ul>
	 *            <li>FLOAT_NONE</li>
	 *            <li>FLOAT_LEFT</li>
	 *            <li>FLOAT_RIGHT</li>
	 *            </ul>
	 */
	public void setFloat(int newValue);


	/**
	 * Returns the float clearing direction of the <code>Floatable</code>. Clearing a float will ensure that the
	 * Component will start on a new line from adjacent floated Components.
	 * 
	 * @return the float clearing direction
	 */
	public int getClearFloat();


	/**
	 * Sets the float clearing direction of the <code>Floatable</code>. Clearing a float will ensure that the
	 * Component will start on a new line from adjacent floated Components.
	 * 
	 * @param newValue - the new float clearing direction. Must be one of:
	 *            <ul>
	 *            <li>FLOAT_CLEAR_NONE</li>
	 *            <li>FLOAT_CLEAR_LEFT</li>
	 *            <li>FLOAT_CLEAR_RIGHT</li>
	 *            <li>FLOAT_CLEAR_BOTH</li>
	 *            </ul>
	 */
	public void setClearFloat(int newValue);
	
}
