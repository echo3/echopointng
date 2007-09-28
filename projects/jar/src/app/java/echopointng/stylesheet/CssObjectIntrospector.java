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
package echopointng.stylesheet;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import nextapp.echo2.app.componentxml.ComponentIntrospector;

/**
 * <code>CssObjectIntrospector</code> is a replacement for
 * <code>ComponentIntrospector</code> that reports JavaBeans that have
 * getters without setters as properties.
 */

public class CssObjectIntrospector {

	private static final Map classLoaderCache = new WeakHashMap();

	/**
	 * Creates a new <code>CssObjectIntrospector</code> for a type of Object
	 * 
	 * @param typeName
	 *            the type name of the Object
	 * @param classLoader
	 *            the class loader from which the type class may be retrieved
	 */

	public static CssObjectIntrospector forName(String typeName,ClassLoader classLoader) 
		throws ClassNotFoundException {
		
		Map ciStore;
		synchronized (classLoaderCache) {
			ciStore = (Map) classLoaderCache.get(classLoader);
			if (ciStore == null) {
				ciStore = new HashMap();
				classLoaderCache.put(classLoader, ciStore);
			}
		}
		CssObjectIntrospector ci;
		synchronized (ciStore) {
			ci = (CssObjectIntrospector) ciStore.get(typeName);
			if (ci == null) {
				ci = new CssObjectIntrospector(typeName, classLoader);
				ciStore.put(typeName, ci);
			}
		}
		return ci;
	}

	private Map propertyDescriptorMap = new HashMap();
	private Class introspectedClass;
	private Map constants;

	/**
	 * Creates a new <code>CssObjectIntrospector</code> for the specified
	 * object type.
	 * 
	 * @param typeName
	 *            the object type name fto introspect
	 */
	private CssObjectIntrospector(String typeName, ClassLoader classLoader)	throws ClassNotFoundException {
		introspectedClass = Class.forName(typeName, true, classLoader);
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(introspectedClass,Introspector.IGNORE_ALL_BEANINFO);
			loadPropertyData(beanInfo);
		} catch (IntrospectionException ex) {
			// Should not occur.
			throw new RuntimeException("Introspection Error", ex);
		}
	}
	
	/**
	 * @return the class of the object being introspected.
	 */
	public Class getIntrospectedClass() {
		return introspectedClass;
	}
 	
	
    /**
     * Retrieves the names of all constants.
     * A constant is defined to be any public static final variable
     * declared in the introspected class.
     * 
     * @return an iterator over the constant names
     */
    public Iterator getConstantNames() {
        return constants.keySet().iterator();
    }
    
    /**
     * Retrieves the value of the constant with the specified name.
     * 
     * @param constantName the name of the constant (unqualified)
     * @return the constant value, or null if no such constant exists
     * @see #getConstantNames()
     */
    public Object getConstantValue(String constantName) {
        return constants.get(constantName);
    }
    
	/**
	 *  @see ComponentIntrospector#getPropertyClass(String)
	 */
	public Class getPropertyClass(String propertyName) {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        if (propertyDescriptor == null) {
            return null;
        } else if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
            return ((IndexedPropertyDescriptor) propertyDescriptor).getIndexedPropertyType();
        } else {
            return propertyDescriptor.getPropertyType();
        }        
	}

	/**
	 *  @see ComponentIntrospector#getPropertyDescriptor(String)
	 */
	public PropertyDescriptor getPropertyDescriptor(String propertyName) {
        return (PropertyDescriptor) propertyDescriptorMap.get(propertyName);
	}

	/**
	 *  @see ComponentIntrospector#getWriteMethod(String)
	 */
	public Method getWriteMethod(String propertyName) {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        if (propertyDescriptor == null) {
            return null;
        } else {
            if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
                return ((IndexedPropertyDescriptor) propertyDescriptor).getIndexedWriteMethod();
            } else {
                return propertyDescriptor.getWriteMethod();
            }
        }
	}

	/**
	 *  @see ComponentIntrospector#isIndexedProperty(String)
	 */
	public boolean isIndexedProperty(String propertyName) {
        return propertyDescriptorMap.get(propertyName) instanceof IndexedPropertyDescriptor;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer("CssObjectIntrospector : [");
		buf.append(introspectedClass.getName());
		buf.append(']');
		return buf.toString();
	}
	
   /**
     * Initialization method to load data related to style constants.
    private void loadConstants() {
    	int CONSTANT_MODIFERS = Modifier.STATIC | Modifier.PUBLIC | Modifier.FINAL;
        constants = new HashMap();
        Field[] fields = introspectedClass.getFields();
        for (int index = 0; index < fields.length; ++index) {
            if ((fields[index].getModifiers() & CONSTANT_MODIFERS) != 0) {
                String constantName = fields[index].getName();
                try {
                    Object constantValue = fields[index].get(null);         
                    constants.put(constantName, constantValue);
                } catch (IllegalAccessException ex) {
                    // Should not occur.
                }
            }
        }         
    }
     */
	

	/**
	 * Initialization method to load PropertyDescriptor information.  This version
	 * will keep getters wihtout setters (eg non muttable objects as well as mutable ones)
	 */
	private void loadPropertyData(BeanInfo beanInfo) {
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int index = 0; index < propertyDescriptors.length; ++index) {
			// We are no longer limited to mutable properties only.

			if (propertyDescriptors[index] instanceof IndexedPropertyDescriptor) {
				if (((IndexedPropertyDescriptor) propertyDescriptors[index])
						.getIndexedWriteMethod() != null) {
					String name = propertyDescriptors[index].getName();

					// Store JavaBean PropertyDescriptor.
					propertyDescriptorMap.put(name, propertyDescriptors[index]);
				}
			} else {
				// getters and setters are okay in this version
				if (propertyDescriptors[index].getWriteMethod() != null || propertyDescriptors[index].getReadMethod() != null) {
					String name = propertyDescriptors[index].getName();
					if (name.equals("class"))
						continue;
					propertyDescriptorMap.put(name, propertyDescriptors[index]);
				}
			}
		}
	}
	
	/**
	 * This will return a <code>Constructor</code> that can be called 
	 * with the specified array of properties as 
	 * parameters.  Its returns null if it can
	 * find an appopriate <code>Contructor</code>.
	 * <p>
	 * 
	 * @param propertyValues - an array of objects.  The types
	 * 	of these objects will be used to find an appropriate Constructor
	 * 
	 * @return a <code>Contructor</code> or null if one cant be found
	 */
	public Constructor getConstructor(Object propertyValues[]) {
		Class[] targetTypes = new Class[propertyValues.length];
		for (int i = 0; i < propertyValues.length; i++) {
			if (propertyValues[i] == null)
				targetTypes[i] = null;
			else
				targetTypes[i] = propertyValues[i].getClass();
		}
		try {
			// throws 
			return introspectedClass.getConstructor(targetTypes);
		} catch (Exception e) {
			// okay that didnt work
		}
		Constructor[] constructors = introspectedClass.getConstructors();
		for (int i = 0; i < constructors.length; i++) {
			Class parameterTypes[] = constructors[i].getParameterTypes();
			// start the matching process
			if (targetTypes.length != parameterTypes.length)
				continue;
			boolean matches = true;
			for (int j = 0; j < parameterTypes.length; j++) {
				// handle special case of null parameters
				if (targetTypes[j] == null) {
					// if the param is an object then it can be null
					if (parameterTypes[j].isPrimitive()) {
						matches = false;
						break;
					}
					// otherwise it might be okay
				} else {
					// can we assign ourselves to the specified type
					if (! parameterTypes[j].isAssignableFrom(targetTypes[j])) {
						//
						// do a special check for primitive classes
						if (matchesPrimiteClass(parameterTypes[j],targetTypes[j])) {
							continue;
						}
						matches = false;
						break;
					}
				}
			}
			// did we get all the way through on the parameters?
			if (matches) {
				return constructors[i];
			}
		}
		return null;
	}
	
	private boolean matchesPrimiteClass(Class parameterType, Class targetType) {
		if (! parameterType.isPrimitive())
			return false;
		
		if (parameterType.equals(Character.TYPE))
			return targetType.equals(Character.class);
		if (parameterType.equals(Byte.TYPE))
			return targetType.equals(Byte.class);
		if (parameterType.equals(Short.TYPE))
			return targetType.equals(Short.class);
		if (parameterType.equals(Integer.TYPE))
			return targetType.equals(Integer.class);
		if (parameterType.equals(Long.TYPE))
			return targetType.equals(Long.class);
		if (parameterType.equals(Float.TYPE))
			return targetType.equals(Float.class);
		if (parameterType.equals(Double.TYPE))
			return targetType.equals(Double.class);
		return false;
	}

}
