package echopointng.util;
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
import java.lang.reflect.Method;
import java.util.*;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Window;;

/**
 * A utility to class to help with Component manipulation
 *
 * @author Brad Baker 
 */
public class ComponentKit {
	
	/**
	 * <code>ComponentOperationCallBack</code> is an interface
	 * used during the traverseComponent() method.  The onComponent()
	 * method is called for each component in the component 
	 * hierarchy.
	 */
	public static interface ComponentTraversalCallBack {
		/**
		 * Called for each component in a component hierarchy.
		 *  
		 * @param c - the component in a hierarchy
		 */		
		public void onComponent(Component c); 
	}

	/** not instantiable */
	private ComponentKit() {
	}

	/**
	 * This method will remove a child component from anywhere in the
	 * component hierarchy from parent downwards.
	 *  
	 * @param parent - an ancestor of child
	 * @param child - the component to remove
	 * @return true if the component was actually removed, or false if
	 *        the parent is not an ancestor of child.
	 */
	public static boolean remove(Component parent, Component child) {
		if (parent == null || child == null)
			return false;
		boolean isAncestor = parent.isAncestorOf(child);
		if (isAncestor) {
			parent = child.getParent();
			parent.remove(child);
			return true;
		}
		return false;
	}
	
	/**
	 * This method will return an array of Components that indicates the path from
	 * parent to child.  The parent Component (ie the root of the path) is the first 
	 * element in the array, while the child will be the last.  If the parent is not
	 * an ancestor of the child, then a zero length array will be returned.
	 * 
	 * @param parent the parent component to be searched
	 * @param child - the child component to find.
	 * @return an array of Components between parent and child or a zero length
	 *  	   array if there is no path.
	 */
	public static Component[] getComponentPath(Component parent, Component child) {
		if (parent == null || child == null)
			return new Component[0];
		if (! parent.isAncestorOf(child))
			return new Component[0];

		List compList = new ArrayList();
		while (child.getParent() != parent) {
			compList.add(child);
			child = child.getParent();
		}
		compList.add(child);
		compList.add(parent);
		Collections.reverse(compList);
		
		return (Component[]) compList.toArray(new Component[compList.size()]);
	}
	
	/**
	 * Returns the highest Component parent starting from child.  This is in
	 * fact ths parent that has no parent.
	 *  
	 * @param child - the component to start searching at
	 * @return - the higest parent Component
	 */
	public static Component getComponentRoot(Component child) {
		Component parent = null;
		do {
			parent = child.getParent();
			if (parent != null)
				child = parent;
		}
		while (parent != null);
		return child;
	}

	/**
	 * Returns the Window contaning the child or null if the child
	 * is not contained within a Window.
	 *  
	 * @param child - the component to start searching at
	 * @return - the Window contaning the child or null if the child
	 * is not contained within a Window.
	 */
	public static Window getComponentWindow(Component child) {
		Component parent = getComponentRoot(child);
		if (parent instanceof Window)
			return (Window) parent;
		else
			return null;
	}
	
	/**
	 * This method makes the component, and all its children, visible or invisible.
	 *  
	 * @param component - the component inquestion
	 * @param visible - true to make all components visible or 
	 * 					false to make them invisible
	 */	
	public static void setVisible(Component component, boolean visible) {
		if (component == null)
			return;
		component.setVisible(visible);
		Component[] children = component.getComponents();
		for (int i = 0; i < children.length; i++) {
			setVisible(children[i],visible);
		} 	
	}
	
	/**
	 * This method makes the component, and all its children, enabled or disabled.
	 *  
	 * @param component - the component inquestion
	 * @param enabled - true to make all components enabled or 
	 * 					false to make them disabled
	 */	
	public static void setEnabled(Component component, boolean enabled) {
		if (component == null)
			return;
		component.setEnabled(enabled);
		Component[] children = component.getComponents();
		for (int i = 0; i < children.length; i++) {
			setEnabled(children[i],enabled);
		} 	
	}
	
	/**
	 * This method will traverse the current component and all its children,
	 * in a depth first manner, and call the ComponentTraversalCallBack
	 * object for each component found. 
	 *  
	 * @param startComponent - the component to start at.
	 * @param traverseCallBack - the ComponentTraversalCallBack to call when a component is found
	 */
	public static void traverseComponent(Component startComponent, ComponentTraversalCallBack traverseCallBack) {
		if (startComponent != null) {
			Stack stack = new Stack();
			stack.push(startComponent);
			while (! stack.isEmpty()) {
				Component c = (Component) stack.pop();
				traverseCallBack.onComponent(c);
				Component[] children = c.getComponents();
				for (int i = 0; i < children.length; i++) {
					stack.push(children[i]);
				}
			}
		}
	}

	/**
	 * This method will traverse the EchoInstance and all its child
	 * components, in a depth first manner, and call the ComponentTraversalCallBack
	 * object for each component found. 
	 *  
	 * @param instance - the ApplicationInstance to traverse.
	 * @param traverseCallBack - the ComponentTraversalCallBack to call when a component is found
	 */
	public static void traverseInstance(ApplicationInstance instance, ComponentTraversalCallBack traverseCallBack) {
		if (instance != null) {
//			for (int i = 0; i < instance.getWindows().length; i++) {
//				traverseComponent(instance.getWindows()[i],traverseCallBack);
//			}
			if (instance.getDefaultWindow() != null) {
				traverseComponent(instance.getDefaultWindow(),traverseCallBack);
			}
		}
	}


	/**
	 * This method will use reflection to set a property on a given component, and 
	 * all of its children.  For example you could set the component's font via :
	 * <p>
	 * <blockquote><pre>
	 * 		ComponentKit.setProperty(myComponent, "font", new Font(Font.VERDANA,Font.BOLD,8));
	 * </pre></blockquote>
	 * <p>
	 * This method uses JavaBean rules for setting properties, that is it will look
	 * for a method called setXxx that takes one parameter, where Xxx is the setter method of the property to
	 * be set.  Failing that it will look for setXX and finally just XX.
	 * <p>
	 * This method guarantees not to throw any exceptions, but rather return false if
	 * it cannot set the properties on an eligble object.
	 *  
	 * @param component - the component in question
	 * @param propertyName - then name of the property to set
	 * @param propertyValue - the new value
	 * @return true if the property could be set.
	 */
	public static boolean setProperty(Component component, String propertyName, Object propertyValue) {
		return setProperty(component, propertyName, propertyValue, null);
	}
			
	/**
	 * This method will use reflection to set a property on a given component, and 
	 * all of its children.  For example you could set the component's font via :
	 * <p>
	 * <blockquote><pre>
	 * 		ComponentKit.setProperty(myComponent, "font", new Font(Font.VERDANA,Font.BOLD,8), RadionButton.class);
	 * </pre></blockquote>
	 * <p>
	 * This method uses JavaBean rules for setting properties, that is it will look
	 * for a method called setXxx that takes one parameter, where Xxx is the setter method of the property to
	 * be set.  Failing that it will look for setXX and finally just XX.
	 * <p>
	 * If a non null <code>filterClass</code> is provided then it will only set properties on components
	 * that are assignable from the filterClass.  If the filterClass is null, then all
	 * components will be matched.
	 * <p>
	 * This method guarantees not to throw any exceptions, but rather return false if
	 * it cannot set the properties on an eligble object.
	 *  
	 * @param component - 		the component in question
	 * @param propertyName - 	then name of the property to set
	 * @param propertyValue - 	the new value
	 * @param filterClass - 	only sets component derived from the filter class or matches
	 * 							all components if its null.
	 * @return true if the property could be set.
	 */
	public static boolean setProperty(Component component, String propertyName, Object propertyValue, Class filterClass) {
		if (component == null || propertyName == null || propertyName.length() <= 0)
			return false;

		boolean setOK = true;			
		Class componentClass = component.getClass();
		if (filterClass == null || filterClass.isAssignableFrom(componentClass)) {
			Method setter = _getSetter(componentClass,propertyName,propertyValue);
			if (setter != null) {
				try {
					setter.invoke(component,new Object[] { propertyValue });
					setOK = true;
				} catch (Exception e) {
					setOK = false;
				}
			}
		}
		Component[] children = component.getComponents();
		for (int i = 0; i < children.length; i++) {
			boolean wasOK = setProperty(children[i],propertyName, propertyValue, filterClass);
			if (! wasOK)
				setOK = false;
		} 	
		return setOK;
	}
	
	/**
	 * Private method to find the setter of a given propertyName. You must
	 * provide a 1 length string or better.  Trys setXxx, then setXXX and finally
	 * just XXXX.
	 */
	private static Method _getSetter(Class clazz, String propertyName, Object propertyValue) {
		StringBuffer sb;
		String methodName;
		Class[] params = (propertyValue == null) ? null : new Class[] { propertyValue.getClass()};
		Method setter = null;

		// try setXxxxXxxx
		sb  = new StringBuffer("set");
		sb.append(propertyName.substring(0,1).toUpperCase(Locale.ENGLISH));
		sb.append(propertyName.substring(1));
		methodName = sb.toString();
		try {
			setter = _findSetterMethod(clazz,methodName,params);
			return setter;
		} catch (Exception e) {
			// no good
		}
		// try setXxxxxx
		sb  = new StringBuffer("set");
		sb.append(propertyName.substring(0,1).toUpperCase(Locale.ENGLISH));
		if (propertyName.length() >= 2) {
			sb.append(propertyName.substring(1).toLowerCase(Locale.ENGLISH));
		}
		methodName = sb.toString();
		try {
			setter = _findSetterMethod(clazz,methodName,params);
		} catch (Exception e) {
			// no good
		}
		// try setXXXXX
		sb  = new StringBuffer("set");
		sb.append(propertyName);
		methodName = sb.toString();
		try {
			setter = _findSetterMethod(clazz,methodName,params);
		} catch (Exception e) {
			// no good
		}
		try {
			setter = _findSetterMethod(clazz,propertyName,params);
		} catch (Exception e) {
			// no good
		}
		return setter;			
	}

	/** Find a matching setter method */	
	private static Method _findSetterMethod(Class clazz, String methodName, Class[] searchParams) {
		Method[] methods = clazz.getMethods();
		for (int i=0; i< methods.length; i++) {
			Class returnType = methods[i].getReturnType();
			Class[] paramTypes = methods[i].getParameterTypes();
			Method method = methods[i]; 
			if (method.getName().equals(methodName) && paramTypes.length == 1) {
				if (returnType.equals(Void.TYPE)) {
					if (searchParams == null || searchParams.length == 0) {
						//in this case we dont know the class of the parameters 
						// probably because the param to be set is null and hence
						// we take the first one
						return method;
					}
					Class searchParam = searchParams[0];
					if (paramTypes[0].isAssignableFrom(searchParam))	
						return method;
					if (searchParam == null)
						return method;
				}
			}
		}
		return null;	
	}
	
	
}
