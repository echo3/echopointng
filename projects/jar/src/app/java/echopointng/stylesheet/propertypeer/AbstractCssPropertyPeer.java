package echopointng.stylesheet.propertypeer;
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

import echopointng.stylesheet.CssInvalidValueException;
import echopointng.stylesheet.CssObjectIntrospector;
import echopointng.stylesheet.CssPropertyPeer;
import echopointng.stylesheet.CssPropertyPeerLoader;
import echopointng.util.ExtentKit;
import echopointng.util.TokenizerKit;
import nextapp.echo2.app.Extent;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * <code>AbstractCssPropertyPeer</code> is a base class for
 * PropertyPeers
 */
public abstract class AbstractCssPropertyPeer implements CssPropertyPeer {

	/**
	 * @see echopointng.stylesheet.CssPropertyPeer#getJavaObject(ClassLoader, Class, String, int)
	 */
	public Object getJavaObject(ClassLoader classLoader, Class objectClass, String propertyValue, int lineNo)
			throws CssInvalidValueException {
		if (! canConvert(null, propertyValue)) {
			Class conversionClass = getConversionClass();
			try {
				CssObjectIntrospector ci = CssObjectIntrospector.forName(objectClass.getName(), classLoader);
	            return getConstantValue(ci,conversionClass,propertyValue, lineNo);
	        } catch (Exception ex) {		
				throw new CssInvalidValueException("Invalid property value for class" + conversionClass.getName() + " : " + propertyValue,null,lineNo);
	        }
		}
		if (isNullString(propertyValue))
			return null;
		return getObject(null, propertyValue);
	}
	
	/**
	 * @see echopointng.stylesheet.propertypeer.AbstractCssPropertyPeer#getStyleString(java.lang.ClassLoader, java.lang.Class, java.lang.Object)
	 */
	public String getStyleString(ClassLoader classLoader, Class objectClass, Object propertyValue) {
		if (propertyValue == null)
			return "null";
		if (! isAssignableFrom(propertyValue,getConversionClass()))
			return null;
		return getString(null, propertyValue);
	}
	
	/**
	 * What is an assignable conversion class for this peer
	 * @return - a conversion class
	 */
	protected abstract Class getConversionClass();
	
	/**
	 * Is the propertyValue valid in its current form?
	 * @param ci - The component Intropector
	 * @param propertyValue - the value to inspect
	 * 
	 * @return true if its a valid, convertable value
	 */
	protected abstract boolean canConvert(CssObjectIntrospector ci, String propertyValue);

	/**
	 * Returns an instance of the converted property value.  
	 * canConvert() will have been called before this is called
	 * and hence no sanity checks need be performed again.
	 * 
	 * @param ci - a object intropector for you to use
	 * @param propertyValue - the value to convert into an object
	 *  
	 * @return the converted value
	 */
	protected abstract Object getObject(CssObjectIntrospector ci, String propertyValue);
	
	/**
	 * Get a string representation of the object.  This default 
	 * implementation returns String.valueOf(object) or
	 * "null";
	 * @param ci - the CssObjectIntrospector available
	 * @param object - the object to get a string representation of.
	 * 
	 * @return - the string representation
	 */
	protected String getString(CssObjectIntrospector ci, Object object) {
		return object == null ? "null" : String.valueOf(object);
	}
	
	/**
	 * Converts a property string value into a name class field constant
	 * of the supplied name.
	 * @param ci - the componen
	 * @param conversionClass
	 * @param propertyValue
	 * @param lineNo
	 * 
	 * @return a string contant into a value
	 */
	protected Object getConstantValue(CssObjectIntrospector ci, Class conversionClass, String propertyValue, int lineNo) throws CssInvalidValueException {
        Object constantValue = ci.getConstantValue(propertyValue);
        if (constantValue == null)
            throw new CssInvalidValueException("No class field constant found for " + propertyValue,null,lineNo);

        if (conversionClass.isAssignableFrom(constantValue.getClass()))
            return constantValue;
        constantValue = ci.getConstantValue(propertyValue.toUpperCase(Locale.ENGLISH));
        if (conversionClass.isAssignableFrom(constantValue.getClass()))
            return constantValue;
        constantValue = ci.getConstantValue(propertyValue.toLowerCase(Locale.ENGLISH));
        if (conversionClass.isAssignableFrom(constantValue.getClass()))
        	return constantValue;
        throw new CssInvalidValueException("No suitable class field constant found for " + propertyValue,null,lineNo);
	}
	
	/**
	 * Returns true if value Object is assignable to the
	 * target class.  If the value is null, then it
	 * is always assignable.
	 *  
	 * @param value - the object to test
	 * @param targetClass - the target assignable class
	 * @return true if its assignable or false
	 */
	protected static boolean isAssignableFrom(Object value, Class targetClass) {
		if (value == null)
			return true;
		return targetClass.isAssignableFrom(value.getClass());
	}
	
	/**
	 * Returns TRUE if the propertyValue is the null string 
	 */
	protected static boolean isNullString(String propertyValue) {
		return propertyValue.trim().equalsIgnoreCase("null");
	}
	
	/**
	 * Returns true if the propertyValue is a valid representation of a base 10 Integer
	 * value.
	 */
	protected static boolean isInteger(String propertyValue) {
		try {
			Integer.parseInt(propertyValue.trim());
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	/**
	 * Returns the int value of the given propertyValue string.  The IsInteger
	 * method should be called to check the value beforehand.
	 */
	protected static int getInteger(String propertyValue) {
		int val;
		try {
			val = Integer.parseInt(propertyValue.trim());
		} catch (NumberFormatException nfe) {
			val = Integer.MIN_VALUE;
		}
		return val;
	}
	
	/**
	 * Looks into a Map of constant values to object values and returns
	 * the key that matches the given object value
	 * 
	 * @param constantMap - the Map is assumed to have String keys
	 * @param equalsTo - the object to perform .equals() on
	 * @return the key or a String.valueOf(equalsTo)
	 */
	protected static String getConstantFromMap(Map constantMap, Object equalsTo) {
		for (Iterator iter = constantMap.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			if (constantMap.get(key).equals(equalsTo)) {
				return key;
			}
		}
		return String.valueOf(equalsTo);
	}

	/**
	 * @see AbstractCssPropertyPeer#getConstantFromMap(Map, Object)
	 */
	protected static String getConstantFromMap(Map constantMap, int intValue) {
		return getConstantFromMap(constantMap,new Integer(intValue));
	}

	/**
	 * Can be called to return an int from a Map of Integer objects.  You
	 * will want to be pretty sure the key is in there or BANG!
	 * 
	 * @param constantMap - the Map to look in
	 * @param key - the lookup key
	 * @return and int for that key
	 */
	protected static int getIntFromMap(Map constantMap, Object key) {
		return ((Integer) constantMap.get(key)).intValue();
	}
	
	/**
	 * Tokenizes a given string with the specified delimeters.
	 * 
	 * @param tokenStr - the string to tokenize
	 * @param delims - the delimeters to use
	 * @return an array of string tokens
	 */
	protected static String[] tokenize(String tokenStr, String delims) {
		return TokenizerKit.tokenize(tokenStr,delims);
	}
	
	/**
	 * Returns true of the specified string is an extent string
	 */
	protected static boolean isExtent(String extentStr) {
		return ExtentKit.isExtent(extentStr);
	}
	
	/**
	 * Makes an Extent from a string
	 */
	protected static Extent makeExtent(String extentStr) {
		return ExtentKit.makeExtent(extentStr);
	}
	
	protected static CssPropertyPeer getPeer(Class peerClass) {
		CssPropertyPeerLoader loader = CssPropertyPeerLoader.forClassLoader(peerClass.getClassLoader());
		return loader.getPropertyPeer(peerClass);
	}
	
}
