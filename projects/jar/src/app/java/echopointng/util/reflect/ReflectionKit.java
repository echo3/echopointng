package echopointng.util.reflect;

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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * <code>ReflectionKit</code> provides methods that help when using reflection
 * on Java code.
 */
public class ReflectionKit {

	private static abstract class BaseReflectionKitComparator implements Comparator {
		/*
		 * Does the standard nullness checks and returns Integer.MAX_VALUE to
		 * indicate that they are both non null objects and hence further checks
		 * may be needed.
		 */
		int compareForNullness(Object o1, Object o2) {
			if (o1 == null && o2 == null)
				return 0;
			if (o1 == null && o2 != null)
				return -1;
			if (o1 != null && o2 == null)
				return 1;
			return Integer.MAX_VALUE;
		}
		
		/**
		 * Normalises the return code into -1, 0 or 1 as required by {@link Comparator}.
		 * @param rc - the rc in question
		 * @return -1, 0 or 1 as required by {@link Comparator}
		 */
		protected int normaliseRC(int rc) {
			if (rc < 0) {
				return -1;
			} else if (rc > 0) {
				return 1;
			} else {
				return 0;
			}
		}
		

		/**
		 * The compares the two <code>{@link Class}</code> values against
		 * each other in the following order :
		 *
		 * <ol>
		 * <li>c1.equals(c2) return 0</li>
		 * <li>c2 is assignable c1, return -1</li>
		 * <li>c1 is assignable c2, return 1</li>
		 * <li>finally c1.getName() compared to c2.getName()</li>
		 * <li></li>
		 * </ol>
		 */
		protected int compareByClassDerivation(Class c1, Class c2) {
			if (c1 == null || c2 == null) {
				return compareForNullness(c1, c2);
			}
			if (c1.equals(c2))
				return 0;
			if (c2.isAssignableFrom(c1) || c1.isAssignableFrom(c2)) {
				if (c2.isAssignableFrom(c1))
					return -1;
				else if (c1.isAssignableFrom(c2))
					return 1;
			}
			int rc = c1.getName().compareTo(c2.getName());
			return normaliseRC(rc);
			
		}
	}

	/**
	 * A Comparator that can be used when comparing and sorting Class objects by
	 * class name. NOTE: this is only based on class name nor class derivation.
	 * <p>
	 * In short it sorts in "Class Name" order only.
	 */
	public static class ClassNameComparator extends BaseReflectionKitComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (o1 == null || o2 == null) {
				return compareForNullness(o1, o2);
			}
			Class c1 = (Class) o1;
			Class c2 = (Class) o2;
			int rc = c1.getName().compareTo(c2.getName());
			return normaliseRC(rc);
		}
	}

	/**
	 * A Comparator that can be used when comparing and sorting Class objects by
	 * most specific class order. If c2 is derived from c1 then it is sorted
	 * before c1. If the classes are unrelated, then its done by class
	 * alphabetic name.
	 * <p>
	 * In short it sorts in "Class Derivation / Class Name" order.
	 */
	public static class ClassDerivationComparator extends BaseReflectionKitComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (o1 == null || o2 == null) {
				return compareForNullness(o1, o2);
			}
			return compareByClassDerivation((Class) o1, (Class) o2);
		}
	}

	/**
	 * A <code>{@link Comparator}</code> that can be used when comparing and
	 * sorting <code>{@link Member}</code> objects by name, modifier and
	 * finally declaring class order.
	 * 
	 * NOTE : <code>{@link java.lang.reflect.Member}</code> is the base class
	 * for {@link Constructor}, {@link Field} and {@link Method} and hence you
	 * can sort any of these types with this <code>Comparator</code>.
	 * <p>
	 * If the <code>Member</code>'s names are the same they are then sorted
	 * by modifier and parameters and finally by declaring class with the most
	 * specific class first.
	 * <p>
	 * In short it sorts in "Member / Modifiers / Parameters / Declaring Class"
	 * order.
	 */
	public static class MemberClassComparator extends BaseReflectionKitComparator implements Comparator {

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			if (o1 == null || o2 == null) {
				return compareForNullness(o1, o2);
			}
			Member m1 = (Member) o1;
			Member m2 = (Member) o2;

			int rc = compareByModifiers(m1.getModifiers(), m2.getModifiers());
			if (rc == 0) {
				if (m1 instanceof Method && m2 instanceof Method) {
					rc = compareByMethod((Method) m1, (Method) m2);
				}
				if (m1 instanceof Field && m2 instanceof Field) {
					rc = compareByField((Field) m1, (Field) m2);
				}
				if (m1 instanceof Constructor && m2 instanceof Constructor) {
					rc = compareByConstructor((Constructor) m1, (Constructor) m2);
				}
			}
			if (rc == 0) {
				rc = compareByClassDerivation(m1.getDeclaringClass(), m2.getDeclaringClass());
			}
			return normaliseRC(rc);
		}

		/**
		 * The compares the two <code>{@link Modifier}</code> values against
		 * each other in the following order
		 * <ol>
		 * <li>public</li>
		 * <li>protected</li>
		 * <li>abstract</li>
		 * <li>final</li>
		 * <li>native</li>
		 * <li>interface</li>
		 * <li>static</li>
		 * </ol>
		 * 
		 * @param modifier1 -
		 *            the modifier of the first <code>Member</code>
		 * @param modifier2 -
		 *            the modifier of the second <code>Member</code>
		 * @return -1, 0 or 1 as per {@link Comparator}
		 */
		protected int compareByModifiers(int modifier1, int modifier2) {
			int weights[] = new int[2];
			int modifiers[] = new int[] { modifier1, modifier2 };
			for (int i = 0; i < modifiers.length; i++) {
				int mod = modifiers[i];
				if (Modifier.isPublic(mod)) 
					weights[i] |= 0x10000000;
				if (Modifier.isProtected(mod)) 
					weights[i] |= 0x01000000;
				if (Modifier.isProtected(mod)) 
					weights[i] |= 0x00100000;
				if (Modifier.isAbstract(mod)) 
					weights[i] |= 0x00010000;
				if (Modifier.isFinal(mod)) 
					weights[i] |= 0x00001000;
				if (Modifier.isNative(mod)) 
					weights[i] |= 0x00000100;
				if (Modifier.isInterface(mod)) 
					weights[i] |= 0x10000010;
				if (Modifier.isStatic(mod)) 
					weights[i] |= 0x00000001;
				// dont worry about the rest
			}
			int rc = weights[0] - weights[1];
			return normaliseRC(rc);
		}

		/**
		 * The compares the two <code>{@link Method}</code> values against
		 * each other in the following order :
		 *
		 * <ol>
		 * <li>Method name</li>
		 * <li>Method return type</li>
		 * <li>Method parameter count</li>
		 * <li>Method parameter types</li>
		 * </ol>
		 *
		 */
		protected int compareByMethod(Method m1, Method m2) {
			int rc = m1.getName().compareTo(m2.getName());
			if (rc == 0) {
				rc = compareByClassDerivation(m1.getReturnType(), m1.getReturnType());
				if (rc == 0) {
					Class[] m1ParamTypes = m1.getParameterTypes();
					Class[] m2ParamTypes = m2.getParameterTypes();
					rc = m1ParamTypes.length - m2ParamTypes.length;
					if (rc == 0) {
						for (int i = 0; i < m1ParamTypes.length; i++) {
							rc = compareByClassDerivation(m1ParamTypes[i], m1ParamTypes[i]);
							if (rc == 0)
								break;
						}
					}
				}
			}
			return normaliseRC(rc);
		}

		/**
		 * The compares the two <code>{@link Field}</code> values against
		 * each other in the following order :
		 *
		 * <ol>
		 * <li>Field name</li>
		 * <li>Field type</li>
		 * </ol>
		 *
		 */
		protected int compareByField(Field f1, Field f2) {
			if (f1 == null || f2 == null) {
				return compareForNullness(f1, f2);
			}
			int rc = f1.getName().compareTo(f2.getName());
			if (rc == 0) {
				rc = compareByClassDerivation(f1.getType(), f1.getType());
			}
			return normaliseRC(rc);
		}

		/**
		 * The compares the two <code>{@link Constructor}</code> values against
		 * each other in the following order :
		 *
		 * <ol>
		 * <li>The Constructor parameter count</li>
		 * <li>The Constructor parameter type</li>
		 * </ol>
		 *
		 */
		protected int compareByConstructor(Constructor c1, Constructor c2) {
			if (c1 == null || c2 == null) {
				return compareForNullness(c1, c2);
			}
			Class[] c1ParamTypes = c1.getParameterTypes();
			Class[] c2ParamTypes = c2.getParameterTypes();
			int rc = c1ParamTypes.length - c2ParamTypes.length;
			if (rc == 0) {
				for (int i = 0; i < c1ParamTypes.length; i++) {
					rc = compareByClassDerivation(c1ParamTypes[i], c1ParamTypes[i]);
					if (rc == 0)
						break;
				}
			}
			return normaliseRC(rc);
		}

	}

	/**
	 * A <code>{@link Comparator}</code> that can be used when comparing and
	 * sorting <code>{@link Member}</code> objects by most specific declaring
	 * class order, then followed by member name.
	 * <p>
	 * In short it sorts in "Class / Member" order.
	 * 
	 * @see echopointng.util.reflect.ReflectionKit.MemberClassComparator
	 */
	public static class ClassMemberComparator extends MemberClassComparator {
		/**
		 * @see echopointng.util.reflect.ReflectionKit.MemberClassComparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			if (o1 == null || o2 == null) {
				return compareForNullness(o1, o2);
			}
			Member m1 = (Member) o1;
			Member m2 = (Member) o2;

			Class c1 = m1.getDeclaringClass();
			Class c2 = m2.getDeclaringClass();
			if (c1.equals(c2)) {
				int rc = compareByModifiers(m1.getModifiers(), m2.getModifiers());
				if (rc == 0) {
					if (m1 instanceof Method && m2 instanceof Method) {
						rc = compareByMethod((Method) m1, (Method) m2);
					}
					if (m1 instanceof Field && m2 instanceof Field) {
						rc = compareByField((Field) m1, (Field) m2);
					}
					if (m1 instanceof Constructor && m2 instanceof Constructor) {
						rc = compareByConstructor((Constructor) m1, (Constructor) m2);
					}
				}
				return normaliseRC(rc);
			} else {
				return compareByClassDerivation(c1, c2);
			}
		}
	}

	/**
	 * A <code>{@link Comparator}</code> that can be used when comparing and
	 * sorting <code>{@link Method}</code> objects by name, modifier and class
	 * order.
	 * <p>
	 * If the Methods's names are the same they are sorted in declaring class
	 * order with the most specific class first.
	 * <p>
	 * In short it sorts in "Method / Declaring Class" order.
	 */
	public static class MethodClassComparator extends MemberClassComparator {
	}

	/**
	 * A <code>{@link Comparator}</code> that can be used when comparing and
	 * sorting <code>{@link Method}</code> objects by most specific declaring
	 * class order, then followed by method name and parameters.
	 * <p>
	 * In short it sorts in "Class / Method" order.
	 */
	public static class ClassMethodComparator extends ClassMemberComparator {
	}

	/**
	 * A <code>{@link Comparator}</code> that can be used when comparing and
	 * sorting <code>{@link Field}</code> objects by name, modifier and class
	 * order.
	 * <p>
	 * If the Field's names are the same they are sorted in declaring class
	 * order with the most specific class first.
	 * <p>
	 * In short it sorts in "Field / Declaring Class" order.
	 */
	public static class FieldClassComparator extends MemberClassComparator {
	}

	/**
	 * A <code>{@link Comparator}</code> that can be used when comparing and
	 * sorting <code>{@link Field}</code> objects by most specific declaring
	 * class order, then followed by Field name and parameters.
	 * <p>
	 * In short it sorts in "Class / Field" order.
	 */
	public static class ClassFieldComparator extends ClassMemberComparator {
	}

	/**
	 * A <code>{@link Comparator}</code> that can be used when comparing and
	 * sorting <code>{@link Constructor}</code> objects by most specific
	 * declaring class order, then followed by Constructor name and parameters.
	 * <p>
	 * In short it sorts in "Class / Constructor" order.
	 */
	public static class ClassConstructorComparator extends ClassMemberComparator {
	}

	/**
	 * <code>MethodSearchCriteria</code> is an interface used to determine if
	 * a method matches some search criteria.
	 */
	public static interface MethodSearchCriteria {
		public boolean isMethodOK(Class methodClass, Method method);
	}

	/** not instantiable */
	private ReflectionKit() {
	}

	/**
	 * Returns true if the method is in fact a Java Bean getter method. ie is
	 * starts with 'get' or 'is' and takes no parameters and returns a value and
	 * is not static.
	 * <p>
	 * Note that it does NOT check for public access because its valid to have a
	 * getter that isnt public.
	 * 
	 * @param method -
	 *            the method to examine
	 * @return true if the method is a Java Bean getter.
	 */
	public static boolean isGetter(Method method) {
		if (method == null)
			return false;
		if (Modifier.isStatic(method.getModifiers()))
			return false;
		if (method.getReturnType().equals(Void.TYPE))
			return false;
		if (method.getParameterTypes().length > 0)
			return false;

		String name = method.getName();
		if (name.length() > 2 && name.startsWith("is"))
			return true;
		if (name.length() > 3 && name.startsWith("get"))
			return true;
		return false;
	}

	/**
	 * Returns true if the method is in fact a Java Bean setter method. ie is
	 * starts with 'set', takes one parameter and has a return value of void and
	 * is not static.
	 * <p>
	 * Note that it does NOT check for public access because its valid to have a
	 * setter that isnt public.
	 * 
	 * @param method -
	 *            the method to examine
	 * @return true if the method is a Java Bean setter.
	 */
	public static boolean isSetter(Method method) {
		if (method == null)
			return false;
		if (Modifier.isStatic(method.getModifiers()))
			return false;
		if (!method.getReturnType().equals(Void.TYPE))
			return false;
		if (method.getParameterTypes().length != 1)
			return false;

		String name = method.getName();
		if (name.length() > 3 && name.startsWith("set"))
			return true;
		return false;
	}

	/**
	 * Takes a bean property method name and removes any 'get'/'is'/'set' at the
	 * front and then decapitalizes the rest of the name according to the Java
	 * Bean Spec.
	 * 
	 * @param name -
	 *            the name of the method or field name to change
	 * @return - the decapitalized name
	 */
	public static String decapitalize(String name) {
		if (name.startsWith("is"))
			name = name.substring(2);
		else if (name.startsWith("get"))
			name = name.substring(3);
		else if (name.startsWith("set"))
			name = name.substring(3);
		if (name == null || name.length() == 0) {
			return name;
		}
		if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
			return name;
		}
		char chars[] = name.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	/**
	 * This method will returns member methods of the targetClass that meet a
	 * specified search criteria. The targetClass will be search up to and
	 * including the stopClass.
	 * <p>
	 * The results are sorted by method name within derived class. The most
	 * specific class methods will be returned first.
	 * <p>
	 * If stopClass is not a superclass or superinterface of targetClass, then
	 * Object.class is used.
	 * 
	 * 
	 * @param targetClass -
	 *            the class to check for methods
	 * @param stopClass -
	 *            the supertype to stop at.
	 * @param methodSearchCriteria -
	 *            the MethodSearchCirteria to use
	 * @return and array of Methods or Method[0] if there are none that match
	 *         the criteria
	 * 
	 * @see MethodSearchCriteria
	 */
	public static Method[] getMethods(Class targetClass, Class stopClass, MethodSearchCriteria methodSearchCriteria) {
		if (targetClass == null || stopClass == null)
			return new Method[0];

		if (!stopClass.isAssignableFrom(targetClass))
			stopClass = Object.class;

		int methodCount = 0;
		int index = 0;
		Method[] methods;
		Class currentClass = targetClass;
		List methodList = new ArrayList();
		do {
			methods = currentClass.getDeclaredMethods();
			List subMethodList = new ArrayList();
			if (methods.length > 0) {
				for (int i = 0; i < methods.length; i++) {
					Method m = methods[i];
					// does it meet our matching criteria
					if (methodSearchCriteria.isMethodOK(currentClass, m)) {
						subMethodList.add(m);
						methodCount++;
					}
				}
				// sort the methods and add to the master list
				Collections.sort(subMethodList, new ClassMethodComparator());
				for (Iterator iter = subMethodList.iterator(); iter.hasNext();) {
					methodList.add(iter.next());
				}
			}
			if (currentClass == stopClass)
				break;
			currentClass = currentClass.getSuperclass();
		} while (currentClass != null);

		methods = new Method[methodCount];
		for (Iterator iter = methodList.iterator(); iter.hasNext();) {
			Method method = (Method) iter.next();
			methods[index++] = method;
		}
		Arrays.sort(methods,new ClassMethodComparator());
		return methods;
	}

	/**
	 * Returns an array containing Method objects reflecting all the member
	 * methods of the class or interface represented by the targetClass object,
	 * including those declared by the class or interface and and those
	 * inherited from superclasses and superinterfaces up until stopClass.
	 * <p>
	 * All public, protected, default (package) access, and private methods are
	 * returned.
	 * <p>
	 * If stopClass is not a superclass or superinterface of targetClass, then
	 * Object.class is used.
	 * 
	 * @param targetClass -
	 *            the class to check for methods
	 * @param stopClass -
	 *            the supertype to stop at.
	 * @return and array of Methods or Method[0] if there are none
	 */
	public static Method[] getAllDeclaredMethods(Class targetClass, Class stopClass) {
		return getMethods(targetClass, stopClass, new MethodSearchCriteria() {
			public boolean isMethodOK(Class methodClass, Method method) {
				return true;
			}
		});
	}

	/**
	 * Shorthand method for
	 * ReflectionKit.getAllMethods(targetClass,Object.class);
	 * 
	 * @see ReflectionKit#getAllDeclaredMethods(Class, Class)
	 */
	public static Method[] getAllDeclaredMethods(Class targetClass) {
		return getAllDeclaredMethods(targetClass, Object.class);
	}

	/**
	 * Returns an array containing Method objects reflecting all the member
	 * methods of the class or interface represented by the targetClass object,
	 * including those declared by the class or interface and and those
	 * inherited from superclasses and superinterfaces up until stopClass.
	 * <p>
	 * Only public methods are returned.
	 * <p>
	 * If stopClass is not a superclass or superinterface of targetClass, then
	 * Object.class is used.
	 * 
	 * @param targetClass -
	 *            the class to check for methods
	 * @param stopClass -
	 *            the supertype to stop at.
	 * @return and array of Methods or Method[0] if there are none
	 */
	public static Method[] getAllPublicMethods(Class targetClass, Class stopClass) {
		return getMethods(targetClass, stopClass, new MethodSearchCriteria() {
			public boolean isMethodOK(Class methodClass, Method method) {
				return Modifier.isPublic(method.getModifiers());
			}
		});
	}

	/**
	 * Returns an array containing getter Method objects reflecting all the
	 * member methods of the class or interface represented by the targetClass
	 * object, including those declared by the class or interface and and those
	 * inherited from superclasses and superinterfaces up until stopClass.
	 * <p>
	 * Only methods matching the Java Bean specifiction for a getter method are
	 * returned.
	 * <p>
	 * If stopClass is not a superclass or superinterface of targetClass, then
	 * Object.class is used.
	 * <p>
	 * The methods are returned in method name order using the MethodComparator
	 * comparator.
	 * 
	 * @param targetClass -
	 *            the class to check for methods
	 * @param stopClass -
	 *            the supertype to stop at.
	 * @return and array of Methods or Method[0] if there are none
	 */
	public static Method[] getAllBeanGetterMethods(Class targetClass, Class stopClass) {
		Method[] methods = getMethods(targetClass, stopClass, new MethodSearchCriteria() {
			public boolean isMethodOK(Class methodClass, Method method) {
				return isGetter(method);
			}
		});
		Arrays.sort(methods, new ClassMethodComparator());
		return methods;
	}

	/**
	 * Returns an array containing setter Method objects reflecting all the
	 * member methods of the class or interface represented by the targetClass
	 * object, including those declared by the class or interface and and those
	 * inherited from superclasses and superinterfaces up until stopClass.
	 * <p>
	 * Only methods matching the Java Bean specifiction for a setter method are
	 * returned.
	 * <p>
	 * If stopClass is not a superclass or superinterface of targetClass, then
	 * Object.class is used.
	 * 
	 * @param targetClass -
	 *            the class to check for methods
	 * @param stopClass -
	 *            the supertype to stop at.
	 * @return and array of Methods or Method[0] if there are none
	 */
	public static Method[] getAllBeanSetterMethods(Class targetClass, Class stopClass) {
		Method[] methods = getMethods(targetClass, stopClass, new MethodSearchCriteria() {
			public boolean isMethodOK(Class methodClass, Method method) {
				return isSetter(method);
			}
		});
		Arrays.sort(methods, new ClassMethodComparator());
		return methods;
	}

	/**
	 * Returns an array containing getter and setter Method objects reflecting
	 * all the member methods of the class or interface represented by the
	 * targetClass object, including those declared by the class or interface
	 * and and those inherited from superclasses and superinterfaces up until
	 * stopClass.
	 * <p>
	 * Only methods matching the Java Bean specifiction for a getter or setter
	 * method are returned.
	 * <p>
	 * If stopClass is not a superclass or superinterface of targetClass, then
	 * Object.class is used.
	 * 
	 * @param targetClass -
	 *            the class to check for methods
	 * @param stopClass -
	 *            the supertype to stop at.
	 * @return and array of Methods or Method[0] if there are none
	 */
	public static Method[] getAllBeanMethods(Class targetClass, Class stopClass) {
		Method[] methods = getMethods(targetClass, stopClass, new MethodSearchCriteria() {
			public boolean isMethodOK(Class methodClass, Method method) {
				return isSetter(method) || isGetter(method);
			}
		});
		Arrays.sort(methods, new MethodClassComparator());
		return methods;
	}

	/**
	 * Returns the getter method for a given setter method. It is determined by
	 * getting the best matching property name as well as matching return type
	 * to setter parameter type.
	 * 
	 * @param beanSetter -
	 *            a bean setter method of class in question
	 * @return the bean getter method or null if it cant be found
	 * @throws IllegalArgumentException -
	 *             if the method passed in is null
	 */
	public static Method getBeanGetter(Method beanSetter) {
		if (beanSetter == null)
			throw new IllegalArgumentException("beanSetter must be non null");
		String setterName = decapitalize(beanSetter.getName());
		Class setterType = beanSetter.getParameterTypes()[0];

		Method getters[] = getAllBeanGetterMethods(beanSetter.getDeclaringClass(), Object.class);
		for (int i = 0; i < getters.length; i++) {
			Method getter = getters[i];
			String getterName = decapitalize(getter.getName());
			if (getterName.equals(setterName)) {
				if (getter.getReturnType().equals(setterType))
					return getter;
			}
		}
		return null;
	}

	/**
	 * @see ReflectionKit#getClassHierarchy(Class, Class)
	 */
	public static Class[] getClassHierarchy(Class targetClass) {
		return getClassHierarchy(targetClass, Object.class);
	}

	/**
	 * Returns an array containing their hierarchy of class objects for the
	 * given class object. The array is sorted in most specific class order, ie
	 * the first class is the class object itself, followed by its super class,
	 * all the way down to stopClass.
	 * <p>
	 * If stopClass is not a superclass or superinterface of targetClass, then
	 * Object.class is used.
	 * 
	 * @param targetClass -
	 *            the class to start the hierarchial search from
	 * @param stopClass -
	 *            the supertype to stop at.
	 * @return and array of Class or Class[0] if there are none
	 */
	public static Class[] getClassHierarchy(Class targetClass, Class stopClass) {
		if (targetClass == null || stopClass == null)
			return new Class[0];

		if (targetClass == stopClass)
			return new Class[] { targetClass };

		if (!stopClass.isAssignableFrom(targetClass))
			stopClass = Object.class;

		List list = new ArrayList();
		list.add(targetClass);
		do {
			targetClass = targetClass.getSuperclass();
			if (targetClass != null)
				list.add(targetClass);
			if (targetClass.equals(stopClass))
				break;
		} while (targetClass != null);
		return (Class[]) list.toArray(new Class[list.size()]);
	}

	/**
	 * This method can be called to determine whether an object has the specific
	 * named method. This is useful in allowing you to conditionally call a
	 * method that may be present in a future version of a class. For example
	 * when you code is built to Java 1.3, you could call a Java 1.4 API
	 * conditionally if its present.
	 * 
	 * @param methodName -
	 *            the name of the method to invoke
	 * @param paramTypes -
	 *            the types of the methods parameters if this is null then it is
	 *            deemed Class[0]
	 * @param returnType -
	 *            the methods return type, if this is null then it is deemed
	 *            Void.TYPE
	 * @param targetObj -
	 *            the object to invoke the method on
	 * @return - the return value of the method if any or null.
	 * 
	 * @throws a
	 *             RuntimeException if any bad happens, such as invalid
	 *             parameters.
	 */
	public static boolean hasMethod(String methodName, Class[] paramTypes, Class returnType, Object targetObj) {
		if (targetObj == null)
			throw new IllegalArgumentException("You must provide a target Object!");
		if (paramTypes == null)
			paramTypes = new Class[0];
		if (returnType == null)
			returnType = Void.TYPE;

		final String testMethodName = methodName;
		final Class[] testParamTypes = paramTypes;
		final Class testReturnType = returnType;
		Method[] methods = getMethods(targetObj.getClass(), Object.class, new MethodSearchCriteria() {
			/**
			 * @see echopointng.util.reflect.ReflectionKit.MethodSearchCriteria#isMethodOK(java.lang.Class,
			 *      java.lang.reflect.Method)
			 */
			public boolean isMethodOK(Class methodClass, Method method) {
				if (method.getName().equals(testMethodName)) {
					if (method.getReturnType().equals(testReturnType)) {
						Class[] paramTypes = method.getParameterTypes();
						if (paramTypes.length == testParamTypes.length) {
							for (int i = 0; i < paramTypes.length; i++) {
								if (!testParamTypes[i].equals(paramTypes[i]))
									return false;
							}
							return true;
						}
					}
				}
				return false;
			}
		});
		if (methods.length > 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method can be called to invoke a specific named method of an object.
	 * This is useful in allowing you to conditionally call a method that may be
	 * present in a future version of a class. For example when you code is
	 * built to Java 1.3, you could call a Java 1.4 API conditionally if its
	 * present.
	 * 
	 * @param methodName -
	 *            the name of the method to invoke
	 * @param paramTypes -
	 *            the types of the methods parameters if this is null then it is
	 *            deemed Class[0]
	 * @param returnType -
	 *            the methods return type, if this is null then it is deemed
	 *            Void.TYPE
	 * @param targetObj -
	 *            the object to invoke the method on
	 * @param params -
	 *            the parameters for the method if this is null then it is
	 *            deemed Object[0]
	 * @return - the return value of the method if any or null.
	 * 
	 * @throws a
	 *             RuntimeException if any bad happens, such as invalid
	 *             parameters.
	 */
	public static Object invokeIfPresent(String methodName, Class[] paramTypes, Class returnType, Object targetObj, Object[] params) {
		if (targetObj == null)
			throw new IllegalArgumentException("You must provide a target Object!");
		if (paramTypes == null)
			paramTypes = new Class[0];
		if (params == null)
			params = new Object[0];
		if (returnType == null)
			returnType = Void.TYPE;

		final String testMethodName = methodName;
		final Class[] testParamTypes = paramTypes;
		final Class testReturnType = returnType;
		Method[] methods = getMethods(targetObj.getClass(), Object.class, new MethodSearchCriteria() {
			/**
			 * @see echopointng.util.reflect.ReflectionKit.MethodSearchCriteria#isMethodOK(java.lang.Class,
			 *      java.lang.reflect.Method)
			 */
			public boolean isMethodOK(Class methodClass, Method method) {
				if (method.getName().equals(testMethodName)) {
					if (method.getReturnType().equals(testReturnType)) {
						Class[] paramTypes = method.getParameterTypes();
						if (paramTypes.length == testParamTypes.length) {
							for (int i = 0; i < paramTypes.length; i++) {
								if (!testParamTypes[i].equals(paramTypes[i]))
									return false;
							}
							return true;
						}
					}
				}
				return false;
			}
		});
		if (methods.length > 0) {
			try {
				return methods[0].invoke(targetObj, params);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}
}
