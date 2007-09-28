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
package echopointng.util.reflect;

import java.lang.reflect.Method;

/**
 * The BeanKit provides methods to help when working with Java Bean compliant
 * objects and their properties.
 * 
 */
public class BeanKit {

	/**
	 * A wrapper Exception that can be throw during BeanKit operations
	 * 
	 */
	public static class BeanKitException extends java.lang.Exception {

		public BeanKitException(String message) {
			super(message);
		}

		public BeanKitException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	private static void assertOK(boolean condition, String message) throws BeanKitException {
		if (!condition) {
			throw new BeanKitException(message);
		}
	}

	private static void assertPropertyType(String propertyName, Class paramType, Method getterSetterMethod) throws BeanKitException {
		String message = propertyName + " is not of type - " + paramType.getName() + " in " + getterSetterMethod.getDeclaringClass().getName();
		if (ReflectionKit.isGetter(getterSetterMethod)) {
			assertOK(getterSetterMethod.getReturnType() == paramType, message);
		} else {
			assertOK(getterSetterMethod.getParameterTypes()[0] == paramType, message);
		}
	}

	private static Method findGetter(Object obj, String propertyName) throws BeanKitException {
		Method[] methods = ReflectionKit.getAllBeanGetterMethods(obj.getClass(), Object.class);
		for (int i = 0; i < methods.length; i++) {
			String getterPropertyName = ReflectionKit.decapitalize(methods[i].getName());
			if (getterPropertyName.equals(propertyName)) {
				return methods[i];
			}
		}
		throw new BeanKitException("No getter found for propertyName - " + propertyName + " within " + obj.getClass().getName());
	}

	private static Method findSetter(Object obj, String propertyName) throws BeanKitException {
		Method[] methods = ReflectionKit.getAllBeanSetterMethods(obj.getClass(), Object.class);
		for (int i = 0; i < methods.length; i++) {
			if (ReflectionKit.decapitalize(methods[i].getName()).equals(propertyName)) {
				return methods[i];
			}
		}
		throw new BeanKitException("No setter found for propertyName - " + propertyName + " within " + obj.getClass().getName());
	}

	private static Object invokeGetter(Object obj, Method getter) throws BeanKitException {
		try {
			Object rc = getter.invoke(obj, new Object[] {});
			return rc;
		} catch (java.lang.Exception e) {
			throw new BeanKitException("Could not invoke getter - " + getter.getName(), e);
		}
	}

	private static void invokeSetter(Object obj, Method setter, Object value) throws BeanKitException {
		try {
			setter.invoke(obj, new Object[] { value });
		} catch (java.lang.Exception e) {
			throw new BeanKitException("Could not invoke setter - " + setter.getName(), e);
		}
	}

	private static Object findAndInvokeGetter(String propertyName, Object obj, Class desiredType) throws BeanKitException {
		Method getter = findGetter(obj, propertyName);
		assertPropertyType(propertyName, desiredType, getter);
		Object objReturn = invokeGetter(obj, getter);
		return objReturn;
	}

	private static void findAndInvokeSetter(String propertyName, Object obj, Class desiredType, Object value) throws BeanKitException {
		Method setter = findSetter(obj, propertyName);
		assertPropertyType(propertyName, desiredType, setter);
		invokeSetter(obj, setter, value);
	}

	/**
	 * Gets the value of a given Java Bean property who has the specified
	 * name and type.
	 * 
	 * @throws BeanKitException -
	 *             if its not a Java Bean property with the right name and type
	 */
	public static Object getProperty(String propertyName, Object obj, Class propertyType) throws BeanKitException {
		return findAndInvokeGetter(propertyName, obj, propertyType);
	}

	/**
	 * Sets the value of a given Java Bean property who has the specified
	 * name and type.
	 * 
	 * @throws BeanKitException -
	 *             if its not a Java Bean property with the right name and type
	 */
	public static void setProperty(String propertyName, Object obj, Class propertyType, Object value) throws BeanKitException {
		findAndInvokeSetter(propertyName, obj, propertyType, value);
	}
	
	/**
	 * Gets the <code>boolean</code> value of a given Java Bean property.
	 * 
	 * @throws BeanKitException -
	 *             if its not a <code>boolean</code> Java Bean property
	 */
	public static boolean getBoolean(String propertyName, Object obj) throws BeanKitException {
		return ((Boolean) findAndInvokeGetter(propertyName, obj, Boolean.TYPE)).booleanValue();
	}

	/**
	 * Sets the <code>boolean</code> value of a given Java Bean property.
	 * 
	 * @throws BeanKitException -
	 *             if its not a <code>boolean</code> Java Bean property
	 */
	public static void setBoolean(String propertyName, Object obj, boolean value) throws BeanKitException {
		findAndInvokeSetter(propertyName, obj, Boolean.TYPE, new Boolean(value));
	}

	/**
	 * Gets the int value of a given Java Bean property.
	 * 
	 * @throws BeanKitException -
	 *             if its not a int Java Bean property
	 */
	public static int getInteger(String propertyName, Object obj) throws BeanKitException {
		return ((Integer) findAndInvokeGetter(propertyName, obj, Integer.TYPE)).intValue();
	}

	/**
	 * Sets the <code>int</code> value of a given Java Bean property.
	 * 
	 * @throws BeanKitException -
	 *             if its not a <code>int</code> Java Bean property
	 */
	public static void setInteger(String propertyName, Object obj, int value) throws BeanKitException {
		findAndInvokeSetter(propertyName, obj, Integer.TYPE, new Integer(value));
	}

	/**
	 * Gets the <code>String</code> value of a given Java Bean property.
	 * 
	 * @throws BeanKitException -
	 *             if its not a <code>String</code> Java Bean property
	 */
	public static String getString(String propertyName, Object obj) throws BeanKitException {
		return (String) findAndInvokeGetter(propertyName, obj, String.class);
	}

	/**
	 * Sets the <code>String</code> value of a given Java Bean property.
	 * 
	 * @throws BeanKitException -
	 *             if its not a <code>String</code> Java Bean property
	 */
	public static void setString(String propertyName, Object obj, String value) throws BeanKitException {
		findAndInvokeSetter(propertyName, obj, String.class, value);
	}

}
