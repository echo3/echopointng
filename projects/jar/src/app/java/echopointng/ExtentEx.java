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
package echopointng;

import echopointng.util.ExtentKit;

/** 
 * <code>ExtentEx</code> is a version of Extent that allows
 * more convinence methods and construction 
 */

public class ExtentEx extends nextapp.echo2.app.Extent {

    /**
     * Creates a new <code>ExtentEx</code> using a string
     * parameter that is converted into value and units, 	 
     * in the form : <pre>  [integer][unit] </pre>
	 * where [unit] is one of <pre>[px | % | pt | cm | mm | in | em | ex | pc]</pre>
	 * and no spaces are allowed betwwen the integer value and the
	 * unit.  
	 * <p>
	 * For example '5em', '12pt', '50%' or '22cm'.

     * <p>
     * The ExtentKit is used under the covers and an
     * <code>IllegalArgumentException</code> is thrown if the string cant be 
     * converted.
     *  
     * @param extentStr the extent string specifying the extent values and units
     * @throws IllegalArgumentException - if the extent string cannot be converted
     * 
     * @see ExtentKit#makeExtent(String)
     */
	public ExtentEx(String extentStr) {
		super(getStrValue(extentStr),getStrUnits(extentStr));
	}
	
    /**
     * Creates a new <code>ExtentEx</code> with pixel units.
     * 
     * @param value the value of the extent in pixels
     */
	public ExtentEx(int value) {
		super(value);
	}

    /**
     * Creates a new <code>ExtentEx</code>.
     * 
     * @param value the value of the extent
     * @param units the units of the value, one of the following constants:
     *        <ul>
     *         <li><code>PC</code>: Pixels</li>
     *         <li><code>PERCENT</code>: Percent (of size of containing component)</li>
     *         <li><code>PT</code>: Points</li>
     *         <li><code>CM</code>: Centimeters</li>
     *         <li><code>MM</code>: Millimeters</li>
     *         <li><code>IN</code>: Inches</li>
     *         <li><code>EM</code>: Ems (height of 'M' character)</li>
     *         <li><code>EX</code>: Exs (height of 'x' character)</li>
     *         <li><code>PC</code>: Picas</li>
     *        </ul>
     */
	public ExtentEx(int value, int units) {
		super(value, units);
	}

	private static int getStrValue(String extentStr) throws IllegalArgumentException {
		return ExtentKit.makeExtent(extentStr).getValue();
	}
	private static int getStrUnits(String extentStr)throws IllegalArgumentException {
		return ExtentKit.makeExtent(extentStr).getUnits();
	}
}
