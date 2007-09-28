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
package echopointng.test.helpers;

import nextapp.echo2.app.Component;

/**
 * A TestHelper provides UI and other services to help with the testing of
 * (primarily object properties) and other code.
 * 
 */
public interface TestHelper {

	/**
	 * This is called fater all the various properties have been set into the
	 * TestHelper. This allows the UI to be build ready for inital values and
	 * ready to provide test values.
	 * 
	 */
	public void init();

	/**
	 * This is called (after init()) and is used to set the current
	 * <code>TestHelper</code> value into the testTarget, but only if we have
	 * a propertyName in play. This allows for property values to be applied to
	 * the testTarget in an asynchronous manner from when the user "selects" a
	 * new property value.
	 * 
	 */
	public void applyAsynchPropertyValue();
	
	/**
	 * Called to indicate that the TestHelper should "apply" the value of the
	 * testTarget property and reflect it in its UI.  
	 *
	 */
	public void applyInitialPropertyValue();

	/**
	 * This is the place that exceptions that occur during
	 * <code>TestHelper</code> operations should be reported to and not throw.
	 */
	public TestDisplaySink getDisplaySink();

	/**
	 * If this is non null then the property name should be used to set and get
	 * values from the testTarget.
	 */
	public String getPropertyName();

	/**
	 * A description of the test in question
	 */
	public String getTestDescription();

	/**
	 * The target object for the <code>TestHelper</code>
	 */
	public Object getTestTarget();

	/**
	 * @return the UI component of the <code>TestHelper</code>.
	 */
	public Component getUI();

	/**
	 * Called to return the current value of the <code>TestHelper</code>
	 */
	public Object getTestValue();
	
	/**
	 * Called to set the current value of the <code>TestHelper</code>
	 */
	public void setTestValue(Object value);

	/**
	 * If this is true, then the <code>TestHelper</code> should directly
	 * update the test target via the propertyName. If it false, then is should
	 * not do direct update but rather provide a value via getValue();
	 */
	public boolean isDirectUpdate();

	/**
	 * If this is true, then the <code>TestHelper</code> should directly
	 * update the test target via the propertyName. If it false, then is should
	 * not do direct update but rather provide a value via getValue();
	 */
	public void setDirectUpdate(boolean newValue);

	/**
	 * This is the place that exceptions that occur during test operations
	 * should be reported to and not throw.
	 */
	public void setDisplaySink(TestDisplaySink newValue);

	/**
	 * If this is non null then the property name should be used to set and get
	 * values from the testTarget.
	 */
	public void setPropertyName(String newValue);

	/**
	 * A description of the test in question
	 */
	public void setTestDescription(String newValue);

	/**
	 * The target object for the <code>TestHelper</code>
	 */
	public void setTestTarget(Object newValue);

	public TestInvoker getTestInvoker();
	
	public void setTestInvoker(TestInvoker testInvoker);
}
