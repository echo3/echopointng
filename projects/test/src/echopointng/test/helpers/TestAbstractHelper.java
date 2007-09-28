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

import echopointng.util.reflect.BeanKit;
import echopointng.util.reflect.BeanKit.BeanKitException;

/**
 * An abstract base class for TestHelper implementations
 *
 */
public abstract class TestAbstractHelper implements TestHelper {
	
	private Object testTarget;
	private String propertyName;
	private String testDescription;
	private boolean directUpdate;
	private TestDisplaySink displaySink;
	private TestInvoker testInvoker;
	

	/**
	 * @see echopointng.test.helpers.TestHelper#getPropertyName()
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @see echopointng.test.helpers.TestHelper#getTestDescription()
	 */
	public String getTestDescription() {
		return testDescription;
	}

	/**
	 * @see echopointng.test.helpers.TestHelper#getTestTarget()
	 */
	public Object getTestTarget() {
		return testTarget;
	}

	/**
	 * @see echopointng.test.helpers.TestHelper#isDirectUpdate()
	 */
	public boolean isDirectUpdate() {
		return directUpdate;
	}

	/**
	 * @see echopointng.test.helpers.TestHelper#setDirectUpdate(boolean)
	 */
	public void setDirectUpdate(boolean newValue) {
		directUpdate = newValue;
	}

	/**
	 * @see echopointng.test.helpers.TestHelper#setPropertyName(java.lang.String)
	 */
	public void setPropertyName(String newValue) {
		propertyName = newValue;
	}

	/**
	 * @see echopointng.test.helpers.TestHelper#setTestDescription(java.lang.String)
	 */
	public void setTestDescription(String newValue) {
		testDescription = newValue;
	}

	/**
	 * @see echopointng.test.helpers.TestHelper#setTestTarget(java.lang.Object)
	 */
	public void setTestTarget(Object newValue) {
		testTarget = newValue;
	}

	/**
	 * @see echopointng.test.helpers.TestHelper#getDisplaySink()
	 */
	public TestDisplaySink getDisplaySink() {
		return displaySink;
	}

	/**
	 * @see echopointng.test.helpers.TestHelper#setDisplaySink(echopointng.test.helpers.TestDisplaySink)
	 */
	public void setDisplaySink(TestDisplaySink newValue) {
		displaySink = newValue;
	}
	
	protected void reportException(Exception e) {
		displaySink.reportException(e);
	}
	
	/**
	 * Called to get a property back from the testTarget.
	 */
	protected Object getTestProperty(Class valueType) {
		if (getPropertyName() == null) {
			return null;
		}
		try {
			Object value = BeanKit.getProperty(propertyName, testTarget, valueType);
			return value;
		} catch (BeanKitException e1) {
			reportException(e1);
			return null;
		}
		
	}
	/**
	 * Called to set a property back into the testTarget.  If the current propertyName
	 * is null, then no set will take place.
	 */
	protected void setTestProperty(Class valueType,Object newValue) {
		if (getPropertyName() == null) {
			return;
		}
		try {
			BeanKit.setProperty(propertyName, testTarget, valueType, newValue);
		} catch (BeanKitException e1) {
			reportException(e1);
		}
	}

	/**
	 * @return the testInvoker
	 */
	public TestInvoker getTestInvoker() {
		return testInvoker;
	}

	/**
	 * @param testInvoker the testInvoker to set
	 */
	public void setTestInvoker(TestInvoker testInvoker) {
		this.testInvoker = testInvoker;
	}
	
	
}
