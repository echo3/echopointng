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
package echopointng.test.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.event.ActionListener;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.ContainerEx;
import echopointng.DirectHtml;
import echopointng.ExtentEx;
import echopointng.GroupBox;
import echopointng.test.helpers.TestBooleanHelper;
import echopointng.test.helpers.TestCallback;
import echopointng.test.helpers.TestCodeOnlyHelper;
import echopointng.test.helpers.TestColorPopup;
import echopointng.test.helpers.TestDisplaySink;
import echopointng.test.helpers.TestHelper;
import echopointng.test.helpers.TestInvoker;
import echopointng.test.testcases.TestCaseBaseNG;
import echopointng.util.TokenizerKit;

/**
 * A TestController allowed properties to be set on a target component
 * 
 */
public class TestController implements TestDisplaySink, TestInvoker {

	/**
	 * What is contained wihin a test
	 */
	private class TestEntry {

		private Component component;

		private TestCallback callback;

		private TestHelper testHelper;

		private TestEntry(Component component, TestHelper testHelper, TestCallback callback) {
			this.component = component;
			this.testHelper = testHelper;
			this.callback = callback;
		}
	}

	private TestCaseBaseNG testCase;
	
	private Component testTarget;

	private List errorList = new ArrayList();

	private List logList = new ArrayList();

	private List testList = new ArrayList();

	private Grid testGrid;

	private ButtonEx applyBtn;
	private DirectHtml exceptionArea = new DirectHtml();
	private DirectHtml messageArea = new DirectHtml();
	private GroupBox groupBox;
	
	private ContainerEx containerEx = new ContainerEx() {
		public void init() {
			groupBox = new GroupBox() {
				public void init() {
					groupBox.setOutsets(new Insets(1, 2, 1, 1));
					groupBox.setBorder(new BorderEx(1, Color.BLUE, BorderEx.STYLE_DASHED));

					// have an apply button
					applyBtn = new ButtonEx("Apply");
					applyBtn.setWidth(new ExtentEx("6ex"));
					applyBtn.addActionListener(new ActionListener() {
						public void actionPerformed(nextapp.echo2.app.event.ActionEvent e) {
							//
							// go through all the entrys
							//
							for (Iterator iter = testList.iterator(); iter.hasNext();) {
								TestEntry entry = (TestEntry) iter.next();
								if (entry.testHelper instanceof TestCodeOnlyHelper) {
									runTestEntry(entry, false);
								} else {
									runTestEntry(entry, true);
								}
							}
							displayErrors();
						};
					});
					//
					// fill the grid
					//
					for (Iterator iter = testList.iterator(); iter.hasNext();) {
						TestEntry entry = (TestEntry) iter.next();
						testGrid.add(entry.component);
					}
					;
					displayErrors();
					groupBox.add(applyBtn);
					groupBox.add(testGrid);
					groupBox.add(exceptionArea);
					groupBox.add(messageArea);
				}
			};
			WindowPane windowPane = new WindowPane();
			windowPane.setHeight(new ExtentEx("400px"));
			windowPane.setWidth(new ExtentEx("600px"));
			windowPane.setMovable(true);
			windowPane.setResizable(true);
			windowPane.setTitle("Testing - " + testTarget.getClass().getName());
			testCase.getTestingAreaPane().add(groupBox);
		}
	};
	
	public TestController(TestCaseBaseNG testCase, Component testTarget) {
		this(testCase,testTarget, 6);
	}
	
	public TestController(TestCaseBaseNG testCase, Component testTarget, int columns) {
		if (testCase == null) {
			throw new IllegalArgumentException("You mmust provide a TestCaseBaseNG");
		}
		if (testTarget == null) {
			throw new IllegalArgumentException("You mmust provide a testTarget");
		}
		this.testTarget = testTarget;
		this.testCase = testCase;
		testGrid = new Grid(columns);
	}

	public TestBooleanHelper addBooleanTest(String propertyName, String testDesc) {
		return addBooleanTest(propertyName, testDesc, null);
	}
	
	public TestBooleanHelper addBooleanTest(final String propertyName, String testDesc, TestCallback testCallback) {
		TestBooleanHelper testHelper = new TestBooleanHelper();
		addTest(testHelper, propertyName, testDesc, testCallback);
		return testHelper;
	}

	public TestColorPopup addColorTest(final String propertyName, String testDesc) {
		return addColorTest(propertyName, testDesc, null);
	}

	public TestColorPopup addColorTest(final String propertyName, String testDesc, TestCallback testCallback) {
		TestColorPopup testHelper = new TestColorPopup();
		addTest(testHelper, propertyName, testDesc, testCallback);
		return testHelper;
	}

	/**
	 * You can use this method to add your own TestHelper implementations. It
	 * will initialise it with the appropriate parameters etc. for you.
	 * 
	 * @param testHelper -
	 *            the TestHelper to use
	 * @param propertyName -
	 *            the propertyName that the TestHelper will use (can be null)
	 * @param testDesc -
	 *            the description of the test
	 * @param testCallback -
	 *            an optional callback mechanism for testing
	 * @return the passed in TestHelper
	 */
	public TestHelper addTest(TestHelper testHelper, String propertyName, String testDesc, TestCallback testCallback) {
		testHelper.setPropertyName(propertyName);
		testHelper.setTestDescription(testDesc);
		testHelper.setTestTarget(getTestTarget());
		testHelper.setDisplaySink(this);
		testHelper.setTestInvoker(this);
		testHelper.setDirectUpdate(false);
		testHelper.init();
		Component helperComponent = testHelper.getUI();
		if (propertyName != null) {
			testHelper.applyInitialPropertyValue();
		}
		TestEntry entry = new TestEntry(helperComponent, testHelper, testCallback);
		testList.add(entry);
		return testHelper;
	}

	/**
	 * You can use this method to add your own TestHelper implementations. It
	 * will initialise it with the appropriate parameters etc. for you.
	 * 
	 * @param testHelper -
	 *            the TestHelper to use
	 * @param testDesc -
	 *            the description of the test
	 * @param testCallback -
	 *            an optional callback mechanism for testing
	 * @return the passed in TestHelper
	 */
	public TestHelper addTest(TestHelper testHelper, String testDesc, TestCallback testCallback) {
		return addTest(testHelper, null, testDesc, testCallback);
	}

	private void displayErrors() {
		//
		// exceptions
		String xhtml;
		exceptionArea.setText("");
		if (errorList.size() > 0) {
			xhtml = "<div style=\"font-size:8pt;border:1px dashed red;margin-top:5px;height:110px;overflow:scroll;\">";
			for (Iterator iter = errorList.iterator(); iter.hasNext();) {
				Throwable e = (Throwable) iter.next();
	
				xhtml += "<span style=\"color:red;font-weight:bold\">";
				xhtml += e.getMessage();
				xhtml += "</span>";
				// print stack trace
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
	
				String stackTrace = getLines(sw.toString(), 7).trim();
				if (stackTrace.length() > 0) {
					xhtml += "<pre>";
					xhtml += stackTrace;
					xhtml += "</pre>";
				}
			}
			xhtml += "</div>";
			exceptionArea.setText(xhtml);
			errorList.clear();
		}
		//
		// and then log messages
		messageArea.setText("");
		if (logList.size() > 0) {
			xhtml = "<div style=\"color:green;font-size:8pt;border:1px dotted black;margin-top:5px;height:110px;overflow:scroll;\">";
			xhtml += "<pre>";
			for (Iterator iter = logList.iterator(); iter.hasNext();) {
				String msg = (String) iter.next();
				xhtml += msg;
				xhtml += "<br/>";
			}
			xhtml += "</pre>";
			messageArea.setText(xhtml);
			logList.clear();
		}
		
	}

	private String getLines(String str, int lineCount) {
		StringBuffer out = new StringBuffer();
		String lines[] = TokenizerKit.splitIntoLines(str);
		for (int i = 0; i < lineCount && i < lines.length; i++) {
			out.append(lines[i]);
			out.append("\n");
		}
		return out.toString();
	}

	/**
	 * @return the testTarget
	 */
	public Component getTestTarget() {
		return testTarget;
	}

	/**
	 * Called to get the UI interface of this component
	 */
	public Component getUI() {
		return containerEx;
	}

	/**
	 * @see echopointng.test.helpers.TestInvoker#invokeTest(echopointng.test.helpers.TestHelper)
	 */
	public void invokeTest(TestHelper testHelper) {
		boolean ranSomething = false;
		for (Iterator iter = testList.iterator(); iter.hasNext();) {
			TestEntry entry = (TestEntry) iter.next();
			if (entry.testHelper == testHelper) {
				runTestEntry(entry,true);
				ranSomething = true;
			}
		}
		if (ranSomething) {
			displayErrors();		
		}
	}

	/**
	 * @see echopointng.test.helpers.TestDisplaySink#logMessage(java.lang.String)
	 */
	public void logMessage(String message) {
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sb.append(sdf.format(new Date()));
		sb.append(" - ");
		sb.append(message = (message == null) ? "" : message.trim());
		logList.add(sb.toString());
	}

	/**
	 * @see echopointng.test.controller.TestThrowableDisplaySink#reportException(java.lang.Throwable)
	 */
	public void reportException(Throwable t) {
		errorList.add(t);
	}

	private void runTestEntry(TestEntry entry, boolean runCallbackCode) {
		//
		// If the test helper doesnt do direct updates then we
		// need
		// to do them, but ony if we have a propertyName
		if (!entry.testHelper.isDirectUpdate()) {
			if (entry.testHelper.getPropertyName() != null) {
				try {
					entry.testHelper.applyAsynchPropertyValue();
				} catch (RuntimeException rte) {
					reportException(rte);
				}
			}
		}
		//
		// if we have a callback then invoke it
		if (runCallbackCode) {
			if (entry.callback != null) {
				try {
					entry.callback.runTestCode(entry.testHelper, TestController.this);
				} catch (RuntimeException rte) {
					reportException(rte);
				}
			}
		}
	}

}
