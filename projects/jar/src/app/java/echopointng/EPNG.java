package echopointng;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.MutableStyleSheet;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.StyleSheet;

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

/**
 * <code>EPNG</code> contains static members that help control the EPNG
 * component library.
 * 
 */
public class EPNG {
	/**
	 * <code>EPNG</code> cannot be instantiated
	 */
	private EPNG() {
	}

	/**
	 * This <code>StyleSheet</code> is used as the fall back stylesheet by the
	 * EPNG rendering peers. The EPNG rendering peers use the following lookup
	 * algorythmn for find <b>non null </b> property values.
	 * <p>
	 * <ul>
	 * <li>Look in the component's local properties</li>
	 * <li>Failing that, look in the component's local style</li>
	 * <li>Failing that, look in the application instance stylesheet</li>
	 * <li>Failing that, look in the fallback stylesheet</li>
	 * </ul>
	 * </p>
	 * Hence this fall back stylesheet is the place of last resort for property
	 * values and you can override them by setting values in the component,
	 * component style or application instance stylesheet.
	 * <p>
	 * You are free to change this field or remove it by setting it to null. You
	 * can also set a change the Style for a specific component class using the
	 * addStyle() methods of the stylsheet.
	 * <p>
	 * However remember it is a static field and hence will affect all EPNG
	 * components in the same JVM.
	 */
	public static StyleSheet FALLBACK_STYLESHEET;

	/**
	 * This static helper method can be used to quickly get the Style for a
	 * given component from the FALLBACK_STYLESHEET. If any of these objects is
	 * null, then null is returned.
	 * 
	 * @param component -
	 *            the component to get a fallback style for
	 * 
	 * @return a fallback style for the component
	 */
	public static Style getFallBackStyle(Component component) {
		Style style = null;
		if (EPNG.FALLBACK_STYLESHEET != null && component != null) {
			style = EPNG.FALLBACK_STYLESHEET.getStyle(component.getClass(), null);
		}
		return style;
	}

	/**
	 * This <code>MutableStyleSheet</code> contains the default visual
	 * representation of the EPNG components.
	 */
	public static MutableStyleSheet DEFAULT_STYLESHEET = new MutableStyleSheet();
	static {
		// keep these in alphabetic order
		DEFAULT_STYLESHEET.addStyle(AutoLookupTextFieldEx.class, null, AutoLookupTextFieldEx.DEFAULT_STYLE);

		DEFAULT_STYLESHEET.addStyle(BalloonHelp.class, null, BalloonHelp.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(ButtonEx.class, null, ButtonEx.DEFAULT_STYLE);

		DEFAULT_STYLESHEET.addStyle(Calculator.class, null, Calculator.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(CalculatorField.class, null, CalculatorField.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(ColorChooser.class, null, ColorChooser.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(ContainerEx.class, null, ContainerEx.DEFAULT_STYLE);

		DEFAULT_STYLESHEET.addStyle(DateChooser.class, null, DateChooser.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(DropDown.class, null, DropDown.DEFAULT_STYLE);

		DEFAULT_STYLESHEET.addStyle(EditableLabelEx.class, null, EditableLabelEx.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(ExpandableSection.class, null, ExpandableSection.DEFAULT_STYLE);

		DEFAULT_STYLESHEET.addStyle(GroupBox.class, null, GroupBox.DEFAULT_STYLE);

		DEFAULT_STYLESHEET.addStyle(ImageIcon.class, null, ImageIcon.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(ImageMap.class, null, ImageMap.DEFAULT_STYLE);

		DEFAULT_STYLESHEET.addStyle(LabelEx.class, null, LabelEx.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(LightBox.class, null, LightBox.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(LiveTable.class, null, LiveTable.DEFAULT_STYLE);

		DEFAULT_STYLESHEET.addStyle(Menu.class, null, Menu.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(MenuBar.class, null, MenuBar.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(MenuButton.class, null, MenuButton.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(MenuItem.class, null, MenuItem.DEFAULT_STYLE);

		DEFAULT_STYLESHEET.addStyle(PopUp.class, null, PopUp.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(ProgressBar.class, null, ProgressBar.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(PushButton.class, null, PushButton.DEFAULT_STYLE);

		DEFAULT_STYLESHEET.addStyle(Separator.class, null, Separator.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(Slider.class, null, Slider.DEFAULT_STYLE);

		DEFAULT_STYLESHEET.addStyle(TabbedPane.class, null, TabbedPane.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(TableEx.class, null, TableEx.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(TitleBar.class, null, TitleBar.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(Tree.class, null, Tree.DEFAULT_STYLE);
		DEFAULT_STYLESHEET.addStyle(TreeTable.TreeAndTableCellRenderer.class, null, TreeTable.TreeAndTableCellRenderer.DEFAULT_STYLE);

		FALLBACK_STYLESHEET = DEFAULT_STYLESHEET;
	}

	/**
	 * This helper method will look for rendered properties inside the component
	 * (via getRenderProperty()) and then if it finds null, then it will look in
	 * the fallback style for that component class.
	 * 
	 * @param component -
	 *            the component in question
	 * @param propertyName -
	 *            the name of the property to find a value for
	 * @return - the render value in question
	 */
	public static Object getRP(Component component, String propertyName) {
		return getRP(component,propertyName,null);
	}

	/**
	 * This helper method will look for rendered properties inside the component
	 * (via getRenderProperty()) and then if it finds null, then it will look in
	 * the fallback style for that component class and if that is null it will
	 * return the <code>defaultValue</code>.
	 * 
	 * @param component -
	 *            the component in question
	 * @param propertyName -
	 *            the name of the property to find a value for
	 * @param defaultValue -
	 *            the final default value to return
	 * 
	 * @return - the render value in question
	 */
	public static Object getRP(Component component, String propertyName, Object defaultValue) {
		Object value = component.getRenderProperty(propertyName);
		if (value == null) {
			Style style = getFallBackStyle(component);
			if (style != null) {
				value = style.getProperty(propertyName);
				if (value == null) {
					value = defaultValue;
				}
			}
		}
		return value;
	}

	/**
	 * This helper method will look for rendered properties inside the component
	 * (via getRenderProperty()) and then if it finds null, then it will look in
	 * the fallback style for that component class and if that is null it will
	 * return the <code>defaultValue</code>.
	 * 
	 * @param component -
	 *            the component in question
	 * @param propertyName -
	 *            the name of the property to find a value for
	 * @param defaultValue -
	 *            the final default value to return
	 * 
	 * @return - the render value in question
	 */
	public static int getRP(Component component, String propertyName, int defaultValue) {
		Object value = getRP(component,propertyName,new Integer(defaultValue));
		return ((Integer) value).intValue();
	}	
	
	/**
	 * This helper method will look for rendered properties inside the component
	 * (via getRenderProperty()) and then if it finds null, then it will look in
	 * the fallback style for that component class and if that is null it will
	 * return the <code>defaultValue</code>.
	 * 
	 * @param component -
	 *            the component in question
	 * @param propertyName -
	 *            the name of the property to find a value for
	 * @param defaultValue -
	 *            the final default value to return
	 * 
	 * @return - the render value in question
	 */
	public static boolean getRP(Component component, String propertyName, boolean defaultValue) {
		Object value = getRP(component,propertyName,Boolean.valueOf(defaultValue));
		return ((Boolean) value).booleanValue();
	}		
	
	/**
	 * This helper method will look for rendered properties inside the component
	 * (via getRenderProperty()) and then if it finds null, then it will look in
	 * the fallback style for that component class and if that is null it will
	 * return the <code>defaultValue</code>.
	 * 
	 * @param component -
	 *            the component in question
	 * @param propertyName -
	 *            the name of the property to find a value for
	 * @param defaultValue -
	 *            the final default value to return
	 * 
	 * @return - the render value in question
	 */
	public static double getRP(Component component, String propertyName, double defaultValue) {
		Object value = getRP(component,propertyName,new Double(defaultValue));
		return ((Double) value).doubleValue();
	}		
	
}
