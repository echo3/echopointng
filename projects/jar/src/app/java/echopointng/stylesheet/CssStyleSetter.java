package echopointng.stylesheet;

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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nextapp.echo2.app.MutableStyle;
import nextapp.echo2.app.Style;

/**
 * <code>CssStyleSetter</code> is responsible for taking a
 * CssClassDecl object and rendering all the property values into 
 * a Style.
 */
class CssStyleSetter {
	
	/**
	 * Creates a Style for all the properties in the CssClassDecl.
	 * 
	 * @param currentCE -
	 *            the current css class entry
	 * @return a Style object
	 * @throws CssStyleSheetException
	 */
	Style createStyle(CssClassDecl currentCE, ClassLoader classLoader) 
		throws CssStyleSheetException
	{
		MutableStyle propertyStyle = new MutableStyle();

		CssClassPropertyDecl[] entries = currentCE.getProperties();
		if (entries.length == 0) {
			return propertyStyle;
		}

		CssObjectIntrospector ci;
		try {
			ci = CssObjectIntrospector.forName(currentCE.getClassName(),classLoader);
		} catch (ClassNotFoundException ex) {
			throw new CssStyleSheetException(
					"Unable to instrospect component class: "
							+ currentCE.getClassName(), ex, currentCE
							.getLineNo());
		}

		for (int i = 0; i < entries.length; i++) {
			CssClassPropertyDecl pe = entries[i];
			String propertyName = pe.getPropertyName();
			Object propertyValue = getPropertyValue(classLoader, ci, pe);
			if (ci.isIndexedProperty(propertyName)) {
				propertyStyle.setIndexedProperty(propertyName, pe.getIndex(),
						propertyValue);
			} else {
				propertyStyle.setProperty(propertyName, propertyValue);
			}
		}

		return propertyStyle;
	}

	/**
	 * Creates a property value for a given property class and property entry.
	 * It may end up navigating down a CssClassPropertyDecl tree to create Java
	 * objects as required.
	 */
	private Object getPropertyValue(ClassLoader classLoader, CssObjectIntrospector ci, CssClassPropertyDecl pe) throws CssStyleSheetException {
		int lineNo = pe.getLineNo();
		String parentClassName = ci.getIntrospectedClass().getName();
        String propertyName = pe.getPropertyName();
        String propertyTargetClassName = pe.getTargetClassName();
        Class propertyClass;
        if (propertyTargetClassName != null) {
            try {
            	//
            	// We scope nextapp.echo2.app objects in by default
            	//
            	if (propertyTargetClassName.indexOf('.') == -1) {
            		propertyTargetClassName = "nextapp.echo2.app." + propertyTargetClassName;
            	}
                propertyClass = Class.forName(propertyTargetClassName,true,classLoader);
            } catch (ClassNotFoundException ex) {
            	throw new CssStyleSheetException("Target property class not found: " + propertyTargetClassName,ex,lineNo); 
            }
        } else {
            propertyClass = ci.getPropertyClass(propertyName);
        }
        if (propertyClass == null) {
        	throw new CssStyleSheetException("Property does not exist: " + propertyName + " in class: " + parentClassName,null,lineNo);
        }
		
		if (pe.isSingleValue()) {
			String propertyValue = pe.getPropertyValue();
			// find a Css Property Peer that can handle this type of value
			Class componentClass = ci.getIntrospectedClass();
			CssPropertyPeerLoader peerLoader = CssPropertyPeerLoader.forClassLoader(classLoader); 
			return peerLoader.getObject(classLoader, componentClass, propertyClass,propertyValue,pe.getLineNo());
		} else {
			// its a multi level property and hence we need instantiate it and
			// then set all is sub properties first before setting it
			// it into the current property
			CssObjectIntrospector propertyCI;
			try {
				propertyCI = CssObjectIntrospector.forName(propertyClass.getName(), classLoader);
			} catch (ClassNotFoundException e) {
	            throw new CssStyleSheetException("Unable to instrospect class: " + propertyClass.getName(),e,lineNo);
			}
			//
			// get all the child property objdcts first and place them into an Object array
			CssClassPropertyDecl[] entries = pe.getProperties();	
			Object childObjectValues[] = new Object[entries.length];
			for (int i = 0; i < entries.length; i++) {
				CssClassPropertyDecl childPE = entries[i];
		    	
		    	Object propertyObjValue = getPropertyValue(classLoader,propertyCI,childPE);
		    	childObjectValues[i] = propertyObjValue; 
			}
			Object objectInstance = noArgsContruction(propertyCI,propertyClass, entries,childObjectValues,lineNo);
			if (objectInstance == null) {
				//
				// ok try via a contructor with just the right
				// combination of parameters
				objectInstance = constructorContruction(propertyCI,propertyClass, entries,childObjectValues,lineNo);
				
			}
			if (objectInstance == null) {
				throw new CssStyleSheetException("Unable to instantiate object instance for class: " + propertyClass.getName(),null,lineNo);
			}
			return objectInstance;
		}
	}	
	
	private Object noArgsContruction(CssObjectIntrospector ci, Class propertyClass, CssClassPropertyDecl[] entries, Object childObjectValues[], int lineNo) throws CssStyleSheetException {
		//
		// try to instantiate via the no args constructor
        Object propertyInstance;
		try {
			propertyInstance = propertyClass.newInstance();
		} catch (Exception e1) {
            return null;
		}
		for (int i = 0; i < entries.length; i++) {
			CssClassPropertyDecl childPE = entries[i];
	   		String childPropertyName = childPE.getPropertyName();
			String childParentClassName = ci.getIntrospectedClass().getName();
			Object objectValue = childObjectValues[i];
	    	Method writeMethod = ci.getWriteMethod(childPropertyName);
	        try {
				writeMethod.invoke(propertyInstance, new Object[]{objectValue});
			} catch (Exception e2) {
	            throw new CssStyleSheetException("Unable to set property :" + childPropertyName + " into object class: " + childParentClassName,e2,lineNo);
			}
		}
		return propertyInstance;
	}
	
	private Object constructorContruction(CssObjectIntrospector ci, Class propertyClass, CssClassPropertyDecl[] entries, Object childObjectValues[], int lineNo) {
		try {
			Constructor constructor = ci.getConstructor(childObjectValues);
			if (constructor == null)
				return null;
			return constructor.newInstance(childObjectValues);
		} catch (SecurityException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}
	
}
