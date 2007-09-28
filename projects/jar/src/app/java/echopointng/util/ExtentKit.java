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
package echopointng.util;

import nextapp.echo2.app.Extent;

/** 
 * <code>ExtentKit</code> is a utility class to help with Extent
 * manipulation
 */

public class ExtentKit {

	/**
	 * Returns an new Extent object that represents the given Extent string
	 * in the form : <code>  [integer][unit] </code>
	 * where unit is one of <code>[px | % | pt | cm | mm | in | em | ex | pc]</code>
	 * and no spaces are allowed betwwen the integer value and the
	 * unit.  For example '5em', '12pt', '50%' or '22cm'.
	 * 
	 * @param extentString - a valid extent string
	 * @return a new Extent
	 * 
	 * @see ExtentKit#isExtent(String)
	 */
	public static Extent makeExtent(String extentString) {
		if (extentString == null)
			throw new IllegalArgumentException("The extentString must be non null");
	
		if (! isExtent(extentString))
			throw new IllegalArgumentException("The extentString is invalid : " + extentString);
	
		extentString = extentString.trim().toLowerCase();
		String digitBit = _parseIntegerPrefix(extentString);
		String unitBit = _parseIntegerPostfix(extentString);
		
		int size = Integer.parseInt(digitBit);
		int units = Extent.PX;
		if (unitBit.equals("px"))
			units = Extent.PX;
		else if (unitBit.equals("%"))
			units = Extent.PERCENT;
		else if (unitBit.equals("pt"))
			units = Extent.PT;
		else if (unitBit.equals("cm"))
			units = Extent.CM;
		else if (unitBit.equals("mm"))
			units = Extent.MM;
		else if (unitBit.equals("in"))
			units = Extent.IN;
		else if (unitBit.equals("em"))
			units = Extent.EM;
		else if (unitBit.equals("ex"))
			units = Extent.EX;
		else if (unitBit.equals("pc"))
			units = Extent.PC;
		
		return new Extent(size,units);
	}

	/**
	 * Returns true if the string is in an extent in
	 * the form : <code>  [integer][unit] </code>
	 * where unit is one of <code>[px | % | pt | cm | mm | in | em | ex | pc]</code>
	 * and no spaces are allowed betwwen the integer value and the
	 * unit.  For example '5em', '12pt', '50%' or '22cm'.
	 */
	public static boolean isExtent(String extentString) {
		if (extentString == null)
			return false;
		extentString = extentString.trim().toLowerCase();
		String digitBit = _parseIntegerPrefix(extentString);
		String unitBit = _parseIntegerPostfix(extentString);
		if (! _isInteger(digitBit))
			return false;
		if (! _isExtentUnit(unitBit))
			return false;
		
		return true;
	}

	private static boolean _isExtentUnit(String propertyValue) {
		if (propertyValue.equals("px"))
			return true;
		if (propertyValue.equals("%"))
			return true;
		if (propertyValue.equals("pt"))
			return true;
		if (propertyValue.equals("cm"))
			return true;
		if (propertyValue.equals("mm"))
			return true;
		if (propertyValue.equals("in"))
			return true;
		if (propertyValue.equals("em"))
			return true;
		if (propertyValue.equals("ex"))
			return true;
		if (propertyValue.equals("pc"))
			return true;
		return false;	
	}

	/*
	 * Well is it an integer?
	 */
	private static boolean _isInteger(String propertyValue) {
		try {
			Integer.parseInt(propertyValue.trim());
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	//
	// returns the integer part of a string that starts with digits
	//
	private static String _parseIntegerPrefix(String propertyValue) {
		int i = 0;
		while (i < propertyValue.length()) {
			if (! Character.isDigit(propertyValue.charAt(i))) {
				break;
			}
			i++;
		}
		return propertyValue.substring(0,i);
	}

	//
	// returns the string part of a string that starts with digits
	//
	private static String _parseIntegerPostfix(String propertyValue) {
		int i = 0;
		while (i < propertyValue.length()) {
			if (! Character.isDigit(propertyValue.charAt(i))) {
				break;
			}
			i++;
		}
		return propertyValue.substring(i,propertyValue.length());
	}

}
