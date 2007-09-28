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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.MutableStyle;
import nextapp.echo2.app.MutableStyleSheet;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.StyleSheet;
import echopointng.util.reflect.ReflectionKit;

/**
 * <code>CssStyleSheetLoader</code> will load a CSS 'like' style sheet source and create
 * a StyleSheet object ready for use.
 */
public class CssStyleSheetLoader {
	
	private static final Style EMPTY_STYLE = new MutableStyle();
	
	/**
	 * <code>ClassEntryComparator</code> responsible for comparing
	 * 2 CssClassDecl objects into an order based on
	 * lowest possible Component class ordering and then within
	 * "extends" style names.
	 */
	private static class CssComponentClassComparator implements Comparator {
		
		/**
		 * Assumes that the class name and class class are known at the time 
		 * comparison.
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			CssClassDecl te1 = (CssClassDecl) o1;
			CssClassDecl te2 = (CssClassDecl) o2;
			if (te1 == te2)
				return 0;
			Class class1 = te1.getComponentClass();
			Class class2 = te2.getComponentClass();
			String styleName1 = te1.getStyleName(); 
			String styleName2 = te2.getStyleName(); 
			
			// if they are the same class then see if the
			// they are logicall derived from each other
			// via "style name" 
			// (style names are always non null by default)
			if (class1.equals(class2)) {
				if (styleName1 == null && styleName2 == null)
					return 0;
				
				if (styleName1 != null && styleName1.equals(te2.getExtendsStyleName())) {
					return -1; // less than
				}
				if (styleName2 != null && styleName2.equals(te1.getExtendsStyleName())) {
					return 1; // greater than
				}
				if (styleName1 != null) {
					if (styleName2 != null) {
						return styleName1.compareTo(styleName2);
					}
					return 1;
				}
				return -1;	// nulls first
			}
			//
			// if the first is assignable from the second then it must be 
			// lower than the second in the class hierarchy
			if (class1.isAssignableFrom(class2)) {
				return -1;
			}
			// and vice vera
			if (class2.isAssignableFrom(class1)) {
				return 1;
			}
			// they are in now way releated from a class point of view
			// we can simply sort on the class name
			return te1.getClassName().compareTo(te2.getClassName());
		}
		
	}
	
	/**
	 * Parses a CSS style sheet and returns a <code>StyleSheet</code>
	 * instance.
	 * 
	 * @param resourceName
	 *            the name of the resource on the classpath containing the CSS
	 *            data
	 * @param classLoader
	 *            the <code>ClassLoader</code> with which to instantiate
	 *            property objects
	 * @return the created <code>StyleSheet</code>
	 * @throws CssStyleSheetException
	 *             if parsing/instantiation errors occur
	 */
	public static StyleSheet load(String resourceName, ClassLoader classLoader)
			throws CssStyleSheetException {
		InputStream in = null;
		try {
			in = classLoader.getResourceAsStream(resourceName);
			return load(in, classLoader);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	/**
	 * Parses a CSS style sheet and returns a <code>StyleSheet</code>
	 * instance.
	 * 
	 * @param in
	 *            the <code>InputStream</code> containing the CSS data
	 * @param classLoader
	 *            the <code>ClassLoader</code> with which to instantiate
	 *            property objects
	 * @return the created <code>StyleSheet</code>
	 * @throws CssStyleSheetException
	 *             if parsing/instantiation errors occur
	 */
	public static StyleSheet load(InputStream in, ClassLoader classLoader)
			throws CssStyleSheetException {
		CssStyleSheetParser parser = new CssStyleSheetParser(in);
		parser.parse();

		CssClassDecl[] classEntries = parser.getClassEntries();
		Map cssClassDeclMap = new HashMap();
		Map componentClassToStyleMap = new HashMap();
		
		// the first check is to make sure we have classes for each of 
		// the named class entries.
		for (int i = 0; i < classEntries.length; i++) {
			CssClassDecl currentCE = classEntries[i];
			int lineNo = currentCE.getLineNo();
			String componentClassName = currentCE.getClassName();
			try {
				Class componentClass = Class.forName(componentClassName, true,classLoader);
				// normalise our values here
				currentCE.setComponentClass(componentClass);
				currentCE.setClassName(componentClass.getName());
				if (! Component.class.isAssignableFrom(componentClass)) {
					throw new CssStyleSheetException(
							"The class is no derived from nextapp.echo2.app.Component: " + componentClassName,
							null, lineNo);
				}
			} catch (ClassNotFoundException ex) {
				throw new CssStyleSheetException(
						"Cannot load class: " + componentClassName,
						ex, lineNo);
			}
		}
		//
		// now sort the array into least specific component class order
		// and within that sort by "extends" style names order so
		// ew always have encountered the least specific decls first
		//
		Arrays.sort(classEntries,new CssComponentClassComparator());
		//for (int i = 0; i < classEntries.length; i++) {
		//	System.out.println(classEntries[i]);
		//}
		//
		// we run through each entry now that its in least specific 
		// component class order.  We can now be sure we will have 
		// all our required class entries and Style built before we need
		// them again
		MutableStyleSheet styleSheet = new MutableStyleSheet();
		for (int i = 0; i < classEntries.length; i++) {
			CssClassDecl currentCE = classEntries[i];
			int lineNo = currentCE.getLineNo();
			String componentClassName = currentCE.getClassName();
			String styleName = currentCE.getStyleName();
			
			String fullStyleName = componentClassName;
			if (styleName!= null)
				fullStyleName += '#' + styleName;
			if (cssClassDeclMap.containsKey(fullStyleName)) {
				// we have a duplicate entry
				throw new CssStyleSheetException(
						"Duplicate style declaration encountered "
								+ fullStyleName, null, lineNo);
			} else {
				cssClassDeclMap.put(fullStyleName,currentCE);
			}
			
			//
			// create a style based on previous component hierarchy styles.
			// This represents the "cascading" of the styles
			MutableStyle style = populateDerivedStyle(currentCE,componentClassToStyleMap);
			
			//
			// if we have extended styles then it can be extended from class declarations
			// of the same type of super types.  For example
			//
			//		ButtonEx#dark extends light with Component#light
			//
			// it can also extend "itself" so its base on on the parent eg
			//
			//		ButtonEx
			//
			//		ButtonEx#dark extends itself
			//
			
			if (currentCE.getExtendsStyleName() != null) {
				String extendsStyleName = currentCE.getExtendsStyleName();
				String fullExtendsStyleName = componentClassName + '#' + extendsStyleName;
				Style extendsStyle = findExtendsStyle(currentCE,componentClassToStyleMap,cssClassDeclMap);
				if (extendsStyle == null) {
					// we have an 'extends' entry but no super class entry
					throw new CssStyleSheetException(
							"Invalid 'extends' style name '"+ fullExtendsStyleName + "' for style name "
									+ fullStyleName, null, lineNo);
				}

				// and we override the current style values with
				// the base extended style.
				style.addStyleContent(extendsStyle);
			}
			//
			// finally get a Style for all the properties in the class
			CssStyleSetter styleSetter = new CssStyleSetter();
			Style propertyStyle = styleSetter.createStyle(currentCE, classLoader);
			style.addStyleContent(propertyStyle);

			componentClassToStyleMap.put(fullStyleName, style);
			styleSheet.addStyle(currentCE.getComponentClass(), styleName, style);
		}
		return styleSheet;
	}

	/**
	 * This will traverse UP the component class hirearchy from least specific and apply all the
	 * base styles that can be found to a new Style object.  In this
	 * way more specific entries will "override" or cascade over 
	 * least specific entries.
	 * 
	 * @param currentCE - the current CssClassDecl entry
	 * @param componentClassToStyleMap - the map of previously encounted Class to style mappings
	 * 
	 * @return a MutableStyle as a starting point for the component
	 */
	private static MutableStyle populateDerivedStyle(CssClassDecl currentCE, Map componentClassToStyleMap) {
		MutableStyle style = new MutableStyle();
		Class classes[] = ReflectionKit.getClassHierarchy(currentCE.getComponentClass(),Component.class);
		for (int i = classes.length-1; i >=0; i--) {
			String componentClassName = classes[i].getName();
			Style classStyle = (Style) componentClassToStyleMap.get(componentClassName);
			if (classStyle != null)
				style.addStyleContent(classStyle);
		}
		return style;
	}

	/**
	 * Returns a Style object by searching DOWN from most specific declarations to find a class
	 * and style name combination that the class declration extends.
	 * 
	 * @param currentCE - the current CssClassDecl entry
	 * @param componentClassToStyleMap - the map of previously encounted Class to style mappings
	 * @return a Style or null
	 */
	private static Style findExtendsStyle(CssClassDecl currentCE, Map componentClassToStyleMap, Map cssComponentClassMap) {
		String extendsStyleName = currentCE.getExtendsStyleName();
		Class classes[] = ReflectionKit.getClassHierarchy(currentCE.getComponentClass(),Component.class);
		for (int i = 0; i < classes.length; i++) {
			String componentClassName = classes[i].getName() ;
			String fullExtendsClassName;
			if ("itself".equals(extendsStyleName)) {
				fullExtendsClassName = componentClassName;
			} else {
				fullExtendsClassName = componentClassName + '#' +  extendsStyleName;				
			}
			
			Style classStyle = (Style) componentClassToStyleMap.get(fullExtendsClassName);
			if (classStyle != null) {
				return classStyle;
			}
		}
		if ("itself".equals(extendsStyleName)) {
			// if we got here and we cant find a Style it means they have declared a 
			// style to cover as a minimum nextapp.echo2.app.Component.  Thats OK because we will
			// imply that there is one, albeit an empty one
			return EMPTY_STYLE;
		}
		return null;
	}


}
