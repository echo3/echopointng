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

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Command;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.webcontainer.ContainerInstance;
import echopointng.ButtonEx;
import echopointng.ContainerEx;
import echopointng.ExtentEx;
import echopointng.LabelEx;
import echopointng.SelectFieldEx;
import echopointng.command.CssStyleAdd;
import echopointng.command.CssStyleAddDontPrint;
import echopointng.command.CssStyleAddValues;
import echopointng.command.CssStyleApplyTo;
import echopointng.command.CssStyleRemove;
import echopointng.command.CssStyleRemoveFrom;
import echopointng.command.CssStyleSheetAdd;
import echopointng.command.JavaScriptEval;
import echopointng.command.JavaScriptInclude;
import echopointng.command.Print;

/**
 * <code>TestCommands</code>
 */

public class TestCommands extends TestCaseBaseNG {

	/**
	 * @see echopointng.test.testcases.TestCaseBaseNG#getTestCategory()
	 */
	public String getTestCategory() {
		return "Commands";
	}

	private Component genSomeDivText() {
		ContainerEx containerEx = new ContainerEx();
		containerEx.add(genRandomTextContent());
		return containerEx;
	}

	public Component testCssStyleAdd() {
		Component targetC1 = genSomeDivText();
		Component targetC2 = genSomeDivText();

		Command[] commands = new Command[] {
				new CssStyleAdd("" + ".whizbang { " + "   background-color:#acacdc;" + "   border : thin orange solid;" + "}" + "\n" + ".swisho {"
						+ "   background-color:#A88E7E;" + "   border : thin red dashed;" + "}" + ""),

				new CssStyleApplyTo("whizbang", targetC1), new CssStyleApplyTo("swisho", targetC2), };

		ApplicationInstance instance = ApplicationInstance.getActive();
		for (int i = 0; i < commands.length; i++) {
			instance.enqueueCommand(commands[i]);
		}

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(5));
		cell.add(targetC1);
		cell.add(targetC2);
		return cell;
	}

	public Component testCssStyleRemove() {
		Component targetC1 = genSomeDivText();
		Component targetC2 = genSomeDivText();

		final CssStyleAdd[] addCommands = new CssStyleAdd[] { new CssStyleAdd("" + ".whizbangRemove { " + "   background-color:#acacdc;"
				+ "   border : thin orange solid;" + "}" + "\n" + ".swishoRemove {" + "   background-color:#A88E7E;" + "   border : thin red dashed;"
				+ "}" + ""),

		};

		ApplicationInstance instance = ApplicationInstance.getActive();
		for (int i = 0; i < addCommands.length; i++) {
			instance.enqueueCommand(addCommands[i]);
		}
		instance.enqueueCommand(new CssStyleApplyTo("whizbangRemove", targetC1));
		instance.enqueueCommand(new CssStyleApplyTo("swishoRemove", targetC2));

		ButtonEx button = new ButtonEx("Remove Styles");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ApplicationInstance instance = ApplicationInstance.getActive();
				for (int i = 0; i < addCommands.length; i++) {
					String id = addCommands[i].getRenderId();
					instance.enqueueCommand(new CssStyleRemove(id));
				}
			}
		});
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(5));
		cell.add(button);
		cell.add(targetC2);
		cell.add(targetC2);
		return cell;
	}

	public Component testDontPrint() {
		Component targetC1 = new LabelEx("This wont print!");
		targetC1.setBackground(Color.YELLOW);
		Component targetC2 = new LabelEx("But this will print.  Select Print Preview in your browser to demonstate this!");

		CssStyleAddDontPrint dontprintCommand = new CssStyleAddDontPrint();
		CssStyleApplyTo applyCommand1 = new CssStyleApplyTo("dontprint", targetC1);

		ApplicationInstance instance = ApplicationInstance.getActive();
		instance.enqueueCommand(dontprintCommand);
		instance.enqueueCommand(applyCommand1);

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(5));
		cell.add(targetC1);
		cell.add(targetC2);
		return cell;
	}

	public Component testCssStyleRemoveFrom() {
		final Component targetC1 = genSomeDivText();
		final Component targetC2 = genSomeDivText();

		final CssStyleAdd[] addCommands = new CssStyleAdd[] { new CssStyleAdd("" + ".whizbangRemoveFrom { " + "   background-color:#acacdc;"
				+ "   border : thin orange solid;" + "}" + "\n" + ".swishoRemoveFrom {" + "   background-color:#A88E7E;"
				+ "   border : thin red dashed;" + "}" + ""),

		};

		ApplicationInstance instance = ApplicationInstance.getActive();
		for (int i = 0; i < addCommands.length; i++) {
			instance.enqueueCommand(addCommands[i]);
		}
		instance.enqueueCommand(new CssStyleApplyTo("whizbangRemoveFrom", targetC1));
		instance.enqueueCommand(new CssStyleApplyTo("swishoRemoveFrom", targetC2));

		ButtonEx button1 = new ButtonEx("Remove From Component 1");
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ApplicationInstance instance = ApplicationInstance.getActive();
				instance.enqueueCommand(new CssStyleRemoveFrom("whizbangRemoveFrom", targetC1));
			}
		});
		ButtonEx button2 = new ButtonEx("Remove From Component 2");
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ApplicationInstance instance = ApplicationInstance.getActive();
				instance.enqueueCommand(new CssStyleRemoveFrom("swishoRemoveFrom", targetC2));
			}
		});

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(5));
		cell.add(button1);
		cell.add(button2);
		cell.add(targetC1);
		cell.add(targetC2);
		return cell;
	}

	public Component testExternalCSS() {
		final Component c1 = genSomeDivText();
		final CssStyleSheetAdd addCmd = new CssStyleSheetAdd("css/test1.css");
		final CssStyleApplyTo applyTo = new CssStyleApplyTo("externalClass", c1);

		ApplicationInstance instance = ApplicationInstance.getActive();
		instance.enqueueCommand(addCmd);
		instance.enqueueCommand(applyTo);

		ButtonEx buttonEx1 = new ButtonEx("RemoveFrom");
		buttonEx1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ApplicationInstance.getActive().enqueueCommand(new CssStyleRemoveFrom("externalClass", c1));
			}
		});

		ButtonEx buttonEx2 = new ButtonEx("Remove Style Completely");
		buttonEx2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ApplicationInstance.getActive().enqueueCommand(new CssStyleRemove(addCmd.getRenderId()));
			}
		});

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(5));
		cell.add(buttonEx1);
		cell.add(buttonEx2);
		cell.add(c1);
		cell.add(genSomeDivText());

		return cell;
	}

	public Component testPrint() {
		ApplicationInstance instance = ApplicationInstance.getActive();
		instance.enqueueCommand(new Print());

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(5));
		cell.add(genRandomTextContent());
		cell.add(genRandomTextContent());
		cell.add(genRandomTextContent());

		return cell;
	}

	public Component testJavaScriptEval() {
		ApplicationInstance instance = ApplicationInstance.getActive();
		instance.enqueueCommand(new JavaScriptEval("alert('I told you it would work!');"));

		Column cell = new Column();
		cell.add(new LabelEx("Well did it work?"));

		return cell;
	}

	public Component testJavaScriptInclude() {
		ApplicationInstance instance = ApplicationInstance.getActive();
		instance.enqueueCommand(new JavaScriptInclude("js/jsinclude.js"));
		instance.enqueueCommand(new JavaScriptEval("jsincludeOK();"));

		Column cell = new Column();
		cell.add(new LabelEx("Well did it work?  Use a DOM inspector to find out!"));

		return cell;
	}

	public Component testStyleAddValues() {
		String s = "This is a paragraph that spans multiple lines\n" + 
					"with new lines within it.\n\r" +
					"Will this display as we expect?\n" +
					"Of course it will!\n";

		Component targetC1 = new Label(s);

		Command[] commands = new Command[] { 
				new CssStyleAddValues("color:red;white-space:pre;",targetC1),
		};

		ApplicationInstance instance = ApplicationInstance.getActive();
		for (int i = 0; i < commands.length; i++) {
			instance.enqueueCommand(commands[i]);
		}

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(5));
		cell.add(targetC1);
		return cell;
	}
	
	public Component testAttributeAdd() {
		
		final SelectFieldEx sf = new SelectFieldEx(new String[] { "Does","This","Have","Attributes"});
		
		sf.setAttribute("attr1","value1");
		sf.setAttribute("attr2","value2");
		sf.setAttribute("attr3","value3");
		
		Button b = new ButtonEx("Inspect attributes");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String js = "showAttr = function()";
				
				js = "alert('gattr : ' + document.getElementById('"+ContainerInstance.getElementId(sf) + "').getAttribute('attr1'));";
				ApplicationInstance.getActive().enqueueCommand(new JavaScriptEval(js));
				js = "alert('direct : ' + document.getElementById('"+ContainerInstance.getElementId(sf) + "').attr1);";
				ApplicationInstance.getActive().enqueueCommand(new JavaScriptEval(js));

				js = "alert('gattr : ' + document.getElementById('"+ContainerInstance.getElementId(sf) + "').getAttribute('id'));";
				ApplicationInstance.getActive().enqueueCommand(new JavaScriptEval(js));
				js = "alert('direct : ' + document.getElementById('"+ContainerInstance.getElementId(sf) + "').id);";
				ApplicationInstance.getActive().enqueueCommand(new JavaScriptEval(js));
			}
		});
		
		//ApplicationInstance.getActive().enqueueCommand(new AttributesAdd(sf,sf));
		
		
		Column cell = new Column();
		cell.add(sf);
		cell.add(b);
		return cell;
	
	}


}
