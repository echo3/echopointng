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

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.TextArea;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.layout.ColumnLayoutData;
import echopointng.DateField;
import echopointng.EditableLabelEx;
import echopointng.ExtentEx;
import echopointng.LabelEx;
import echopointng.Strut;
import echopointng.model.CalendarSelectionListener;
import echopointng.xhtml.XhtmlFragment;

/**
 * <code>TestLabel</code>
 */

public class TestLabel extends TestCaseBaseNG {

	public String getTestCategory() {
		return "LabelEx";
	}

	public Component testLabel() {
		Column cell = new Column();

		LabelEx label = new LabelEx();
		label.setRolloverEnabled(true);
		label.setOutsets(new Insets(20));
		label.setInsets(new Insets(5));
		label.setForeground(Color.BLACK);
		label.setBackground(Color.WHITE);
		label.setRolloverBackground(Color.ORANGE);
		label.setRolloverForeground(Color.WHITE);
		label.setBorder(new Border(1, Color.RED, Border.STYLE_DASHED));
		label.setIcon(new HttpImageReference("images/ArrowRoundLeft.gif"));
		label.setRolloverIcon(new HttpImageReference("images/ArrowRoundRight.gif"));
		label.setText("LabelEx Here");

		cell.add(label);

		label = new LabelEx();
		label.setOutsets(new Insets(20));
		label.setInsets(new Insets(5));
		label.setForeground(Color.BLACK);
		label.setBackground(Color.WHITE);
		label.setRolloverBackground(Color.ORANGE);
		label.setRolloverForeground(Color.WHITE);
		label.setBorder(new Border(1, Color.RED, Border.STYLE_DASHED));
		label.setIcon(new HttpImageReference("images/ArrowRoundLeft.gif"));
		label.setRolloverIcon(new HttpImageReference("images/ArrowRoundRight.gif"));

		label.setIntepretNewlines(true);
		label.setText("Disabled Label\n\r over multiple\r\nlines using \ntext interpretation");
		label.setEnabled(false);
		cell.add(label);

		label = new LabelEx("Default with Rollover");
		label.setRolloverEnabled(true);
		cell.add(label);

		return cell;

	}

	public Component testDefaultLabel() {
		return new LabelEx("LabelEx");
	}

	public Component testLabelLineWrap() {
		String text = "This is some really long text that should wrap for one Label but no the other."
				+ " Well does it line wrap properly or doesnt it.  Thats the purpose of this test" + " to see if the line wrap works as expected.";

		LabelEx label1 = new LabelEx(text);
		label1.setLineWrap(false);
		LabelEx label2 = new LabelEx(text);
		label2.setLineWrap(true);

		Column cell = new Column();
		cell.add(label1);
		cell.add(label2);

		return cell;
	}

	public Component testAlignment() {
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("5em"));

		Alignment leftAlignment = new Alignment(Alignment.LEFT, Alignment.DEFAULT);
		Alignment centerAlignment = new Alignment(Alignment.CENTER, Alignment.DEFAULT);
		Alignment rightAlignment = new Alignment(Alignment.RIGHT, Alignment.DEFAULT);

		ColumnLayoutData layoutLeft = new ColumnLayoutData();
		layoutLeft.setAlignment(leftAlignment);

		ColumnLayoutData layoutCenter = new ColumnLayoutData();
		layoutCenter.setAlignment(centerAlignment);

		ColumnLayoutData layoutRight = new ColumnLayoutData();
		layoutRight.setAlignment(rightAlignment);

		LabelEx labelLeft = new LabelEx("Left aligned");
		labelLeft.setLayoutData(layoutLeft);

		LabelEx labelCentre = new LabelEx("Center aligned");
		labelCentre.setLayoutData(layoutCenter);

		LabelEx labelRight = new LabelEx("Right aligned");
		labelRight.setLayoutData(layoutRight);

		cell.add(labelLeft);
		cell.add(labelCentre);
		cell.add(labelRight);

		labelLeft = new LabelEx("Left aligned direct");
		labelLeft.setTextAlignment(leftAlignment);
		labelLeft.setWidth(new ExtentEx(250));
		labelLeft.setBackground(Color.PINK);

		labelCentre = new LabelEx("Center aligned direct");
		labelCentre.setTextAlignment(centerAlignment);
		labelCentre.setWidth(new ExtentEx(300));
		labelCentre.setBackground(Color.CYAN);

		labelRight = new LabelEx("Right aligned direct");
		labelRight.setTextAlignment(rightAlignment);
		labelRight.setWidth(new ExtentEx(350));
		labelRight.setBackground(Color.ORANGE);

		cell.add(labelLeft);
		cell.add(labelCentre);
		cell.add(labelRight);

		cell.add(new Strut(200, 10));

		return cell;

	}

	public Component testEditableLabel() {
		Column cell = new Column();

		TextField textField = new TextField();
		textField.setText("We can edit this and it will synchronise and its has an action Listener!");
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(nextapp.echo2.app.event.ActionEvent e) {
				e = null;
			};
		});

		EditableLabelEx editableLabelEx = new EditableLabelEx(textField);
		cell.add(editableLabelEx);

		textField = new TextField();
		textField.setText("We can edit this BUT it will not synchronise!");

		editableLabelEx = new EditableLabelEx(textField);
		editableLabelEx.setText("This wont synch with target");
		editableLabelEx.setSynchronisedWithTarget(false);
		cell.add(editableLabelEx);

		TextArea textArea = new TextArea();
		textArea.setWidth(new ExtentEx("100ex"));
		textArea.setHeight(new ExtentEx("10ex"));

		textArea.setText("A text area over a few lines\n that is well also editiable!");
		editableLabelEx = new EditableLabelEx(textArea);
		editableLabelEx.setIntepretNewlines(true);
		editableLabelEx.setWidth(textArea.getWidth());
		editableLabelEx.setHeight(textArea.getHeight());
		cell.add(editableLabelEx);

		final DateField df = new DateField();
		editableLabelEx = new EditableLabelEx();
		editableLabelEx.add(df);
		editableLabelEx.setTarget(df.getTextField());
		cell.add(editableLabelEx);

		final Label label = new Label();
		cell.add(label);
		df.getModel().addListener(new CalendarSelectionListener() {
			public void displayedDateChange(echopointng.model.CalendarEvent calEvent) {
			};

			public void selectedDateChange(echopointng.model.CalendarEvent calEvent) {
				StringBuffer sb = new StringBuffer();
				sb.append("SD : ");
				sb.append(new SimpleDateFormat("dd/MM/yyyy").format(df.getSelectedDate().getTime()));
				sb.append("DD : ");
				sb.append(new SimpleDateFormat("dd/MM/yyyy").format(df.getDisplayedDate().getTime()));

				label.setText(sb.toString());
			};
		});

		return cell;
	}

	public Component testXhtmlSupport() {
		Column cell = new Column();
		XhtmlFragment fragment = new XhtmlFragment("" + " This <b>LabelEx</b> is example of using an <code>XhtmlFragment</code>"
				+ " as its content.  This allows any <i>XHTML</i> to be used and hence rich presentation"
				+ " features such as <b>bold</b> and <i>italic</i> text can be used." + "<p>"
				
				+ "<p>" 
				+ " You can use <span style=\"font-size:14pt\">CSS</span> inside the markup and hence allow mixed " +
					"<span style=\"font-size:18pt;color:blue\">font sizes</span>" 
				+ " and <span style=\"color:red\">colors</span>."
				+ "</p>" 

				+ "<h2>You can even have headers like this.</h2>"
				
				+ " Because it is XHTML then paragraphs and the like are supported.  However you must make sure its"
				+ " a valid XHTML fragment otherwise it will barf during rendering when it invokes the" + " XML parser." + "</p>"
				
				+"<p>"
				+ " If you look closely at this example you will notice that it does not contain a top level"
				+ " element tag, like all well formed XML should have.  Thats OK because XhtmlFragment handles" + " this for you automatically."
				+ "</p>"
				
				+"<p>"
				+ " All of the XHTML markup you see here is one <code>LabelEx</code>"
				+ "</p>" 
				
				
				+ "<h3>Hey! Doesnt this break the Echo2 design model?</h3>"
				
				+ " Echo2 is designed to abstract the developer from the intricacies of XHTML/CCS/JavaScript"
				+ " and this is a noble goal.  However in order to fully take advantage of the browser"
				+ " EPNG has <i>undone</i> some of these design goals by allowing constructs like "
				+ " <code>XhtmlFragment</code> while still fully embracing the powerful features" + " such as component driven design." + "");
		LabelEx labelEx = new LabelEx();
		labelEx.setText(fragment);
		labelEx.setLineWrap(true);

		cell.add(labelEx);
		return cell;

	}
	
	public Component testTextAndIcon() {
		final ImageReference icon = new HttpImageReference("/images/poker_dogs.gif",new ExtentEx(10),new ExtentEx(10));
		LabelEx labelEx = new LabelEx("Does LabelEx respect ImageReference sizing?",icon);
		Label label = new Label("Does Label respect ImageReference sizing?",icon);
		
		Column cell = new Column();
		cell.add(labelEx);
		cell.add(label);
		
		return cell;
	}

}
