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
package echopointng.test.testcases;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Stack;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.ContentPane;
import nextapp.echo2.app.Label;
import echopointng.BorderEx;
import echopointng.ContainerEx;
import echopointng.ContentPaneEx;
import echopointng.ExtentEx;
import echopointng.LabelEx;
import echopointng.StackedPaneEx;
import echopointng.able.Scrollable;
import echopointng.test.controller.TestController;
import echopointng.test.helpers.TestButtonHelper;
import echopointng.test.helpers.TestCallback;
import echopointng.test.helpers.TestDisplaySink;
import echopointng.test.helpers.TestHelper;
import echopointng.util.RandKit;

/** 
 * <code>TestDateChooser</code> 
 */

public class TestStackedPaneEx extends TestCaseBaseNG {

	
	public String getTestCategory() {
		return "StackedPaneEx";
	}

	public Component testStackedPaneEx() {
		ContainerEx positioned = new ContainerEx();
		positioned.setHeight(new ExtentEx("50%"));
		positioned.setBorder(BorderEx.DEFAULT);
		positioned.add(genRandomTextContent());
		positioned.setScrollBarPolicy(Scrollable.ALWAYS);
		
		final Component testChildren[]  = new Component[] {
				new Label("Child A"),
				new Label("Child B"),
				new Label("Child C"),
				positioned,
				new Label("Child D"),
		};
		
		final Stack lastPopped = new Stack();
		
		final StackedPaneEx stackedPaneEx = new StackedPaneEx();
		stackedPaneEx.push(testChildren);
		
		TestController controller = new TestController(this, stackedPaneEx);
		controller.addBooleanTest(StackedPaneEx.PROPERTY_LAZY_RENDER_ENABLED, "lazy rendering?");
		controller.addTest(new TestButtonHelper(), "Pop", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				Component c = stackedPaneEx.pop();
				lastPopped.push(c);
			}
		});
		controller.addTest(new TestButtonHelper(), "Pop to A", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				stackedPaneEx.popUntilTop(testChildren[0]);
			}
		});

		controller.addTest(new TestButtonHelper(), "Pop to A and Remove", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				Component removed[] = stackedPaneEx.popUntilTop(testChildren[0]);
				for (int i = 0; i < removed.length; i++) {
					stackedPaneEx.remove(removed[i]);
				}
			}
		});

		controller.addTest(new TestButtonHelper(), "Push Last Popped", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				stackedPaneEx.push((Component) lastPopped.peek());
			}
		});
		
		controller.addTest(new TestButtonHelper(), "Push All", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				stackedPaneEx.push(testChildren);
			}
		});

		controller.addTest(new TestButtonHelper(), "Remove All", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				stackedPaneEx.removeAll();
			}
		});

		controller.addTest(new TestButtonHelper(), "Push B", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				stackedPaneEx.push(testChildren[1]);
			}
		});
		
		ContentPaneEx contentPaneEx = new ContentPaneEx();
		contentPaneEx.add(stackedPaneEx);
		contentPaneEx.add(controller.getUI());
		
		return contentPaneEx;
	}
	
	public Component testStackedPaneExDefault() {
		StackedPaneEx stackedPaneEx  =new StackedPaneEx();
		return stackedPaneEx;
	}
	
	public Component testStackedPaneExBreadTrail() {
		
		final StackedPaneEx stackedPaneEx  = new StackedPaneEx();
		
		class BreadTrail implements PropertyChangeListener {
			private LabelEx label = new LabelEx();
			public void propertyChange(PropertyChangeEvent evt) {
				StringBuffer sb = new StringBuffer();
				sb.append("Component renderId's : ");
				int size = stackedPaneEx.size();
				for (int i = 0; i < size; i++) {
					Component c = stackedPaneEx.peek(i);
					sb.append(c.getRenderId());
					if (i < size-1) {
						sb.append(">...");
					}
				}
				label.setText(sb.toString());
			}	
			
			public Component getUI() {
				return label;
			}
		};
		
		BreadTrail breadTrail = new BreadTrail();
		stackedPaneEx.addPropertyChangeListener(breadTrail);
		
		TestController controller = new TestController(this, stackedPaneEx);
		controller.addTest(new TestButtonHelper(), "Push", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				Component c = new Column();
				c.setBackground((Color) RandKit.roll(new Color[] {
						Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW,
				}));
				if (RandKit.roll5050()) {
					c.add(new Label("Pane"));
					c.add(genRandomTextContent());
					ContentPane contentPane = new ContentPane();
					contentPane.setBackground(Color.DARKGRAY);
					contentPane.add(c);
					c = contentPane;
				} else {
					c.add(new Label("Non Pane"));
					c.add(genRandomTextContent());
				}
				
				stackedPaneEx.push(c);
			}
		});
		controller.addTest(new TestButtonHelper(), "Pop", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				stackedPaneEx.pop();
			}
		});
		controller.addTest(new TestButtonHelper(), "empty", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				stackedPaneEx.empty();
			}
		});
		
		ContentPaneEx contentPaneEx = new ContentPaneEx();
		contentPaneEx.add(breadTrail.getUI());
		contentPaneEx.add(stackedPaneEx);
		contentPaneEx.add(controller.getUI());
		
		return contentPaneEx;
	}

	public Component testStackedPaneWithPanes() {
		
		final StackedPaneEx stackedPaneEx  = new StackedPaneEx();
		
		TestController controller = new TestController(this, stackedPaneEx);
		controller.addTest(new TestButtonHelper(), "Push", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				Component c = new Column();
				c.setBackground((Color) RandKit.roll(new Color[] {
						Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW,
				}));
				int textCount = RandKit.rand(1,3);
				c.add(new Label("Pane with " + textCount + " lots of random text."));
				for (int i = 1; i <= textCount; i++) {
					c.add(genRandomTextContent());
				}
				ContentPane contentPane = new ContentPane();
				contentPane.setBackground(Color.DARKGRAY);
				contentPane.add(c);
				c = contentPane;
				stackedPaneEx.push(c);
			}
		});
		controller.addTest(new TestButtonHelper(), "Pop", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				stackedPaneEx.pop();
			}
		});
		controller.addTest(new TestButtonHelper(), "empty", new TestCallback() {
			public void runTestCode(TestHelper testHelper, TestDisplaySink displaySink) {
				stackedPaneEx.empty();
			}
		});
		
		ContentPaneEx contentPaneEx = new ContentPaneEx();
		contentPaneEx.add(stackedPaneEx);
		contentPaneEx.add(controller.getUI());
		return contentPaneEx;
	}

}
