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

import java.text.SimpleDateFormat;
import java.util.Date;

import nextapp.echo2.app.Button;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.DocumentEvent;
import nextapp.echo2.app.event.DocumentListener;
import nextapp.echo2.app.text.Document;
import echopointng.Calculator;
import echopointng.CalculatorField;
import echopointng.ExtentEx;
import echopointng.PushButton;
import echopointng.util.ColorKit;

/**
 * <code>TestCalculator</code>
 */
public class TestCalculator extends TestCaseBaseNG {

	public String getTestCategory() {
		return "Calculator";
	}

	public Component testCalculator() {
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(10));

		cell.add(new Calculator());

		return cell;
	}

	public Component testDisabledCalculator() {
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(5));

		Calculator calc = new Calculator();
		calc.setEnabled(false);
		cell.add(calc);

		return cell;
	}
	
	public Component testCalculatorWithListeners() {
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(5));

		final Label docLabel = new Label();
		final Label actionLabel = new Label();
		Calculator calc = new Calculator();
		calc.setText("666");
		calc.setMemory("333");
		calc.setTransfer("OK");
		calc.setBackground(ColorKit.clr("#ECE9D8"));
		calc.setButtonNumberForeground(Color.BLUE);
		calc.setActionCommand("actionBaby!");

		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = new SimpleDateFormat("hh:mm:ss ").format(new Date());
				text += "ActionListener fired:" + e.getActionCommand();
				actionLabel.setText(text);
			}
		};
		DocumentListener dl = new DocumentListener() {
			public void documentUpdate(DocumentEvent e) {
				String text = new SimpleDateFormat("hh:mm:ss ").format(new Date());
				text += "DocListener fired:" + ((Document) e.getSource()).getText();
				docLabel.setText(text);
			}
		};

		calc.addActionListener(al);
		calc.getDocument().addDocumentListener(dl);

		cell.add(calc);

		calc = new Calculator();
		calc.setText("999");
		calc.setInsets(new Insets(0));
		calc.addActionListener(al);
		calc.getDocument().addDocumentListener(dl);
		cell.add(calc);

		cell.add(docLabel);
		cell.add(actionLabel);

		return cell;
	}

	public Component testCalculatorField() {
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx(5));

		final Label docLabel = new Label();
		
		final CalculatorField calculatorField = new CalculatorField(777.777);
		calculatorField.setValue(999.999);
		calculatorField.getDocument().addDocumentListener(new DocumentListener() {
			public void documentUpdate(DocumentEvent e) {
				String text = new SimpleDateFormat("hh:mm:ss ").format(new Date());
				text += "DocListener fired:" + ((Document) e.getSource()).getText();
				docLabel.setText(text);
			}
		});
		
		
		
		Button button = new PushButton("Set value to 888.888");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calculatorField.setValue(888.888);
			}
		});

		cell.add(calculatorField);
		cell.add(docLabel);
		cell.add(button);

		return cell;
	}
}
