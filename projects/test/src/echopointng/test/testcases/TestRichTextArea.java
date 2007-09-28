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

import java.text.DateFormat;
import java.util.Calendar;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.ContentPane;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import echopointng.ButtonEx;
import echopointng.ContainerEx;
import echopointng.ExtentEx;
import echopointng.LabelEx;
import echopointng.RichTextArea;
import echopointng.layout.DisplayLayoutData;
import echopointng.richtext.AbstractRichTextSpellChecker;

/** 
 * <code>TestRichTextArea</code> 
 */

public class TestRichTextArea extends TestCaseBaseNG {

	/**
	 * @see echopointng.test.testcases.TestCaseBaseNG#getTestCategory()
	 */
	public String getTestCategory() {
		return "RichTextArea";
	}

	public Component testRichTextArea() {
		
		String htmlText = "This is some HTML inside an EchoPointNG RichTextArea. " +
				"<br/><br/>" +
				"The <b>EchoPointNG</b> RichTextArea has spell checking capabilities and this example is used " +
				"to indicate them.<br/>" +
				"<br/> " +
				"In this example, anywhere the word EchoPointNG is encountered, the " +
				"spell checker implementation presents it as mispelt. " +
				"<br/><br/>" +
				"Even if <i>EchoPointNG</i> is in italics or its in big " +
				"<h3>EchoPointNG</h3> h3 tags is can still be corrected by the spell checker " +
				"as it can parse HTML." +
				"<br/><br/>" +
				"And if you dont like this spell check implementation, you can replace it " +
				"with your own implementation, allowing you to create new parsing rules and new " +
				"spelling laternative rules." +
				"";
		
		
		final RichTextArea rta = new RichTextArea();
		//rta.setBackgroundImage(fillImage);
		rta.setBorder(new Border(1,Color.BLACK,Border.STYLE_DASHED));
		rta.setHeight(new Extent(400));
		//rta.setToolBarAlignment(Alignment.BOTTOM);
		rta.setInsets(new Insets(10));
		rta.setOutsets(new Insets(1));
		rta.setEditorBorder(new Border(1,Color.BLUE,Border.STYLE_DOTTED));
		//rta.setEditorBackground(Color.WHITE);
		
		rta.setText(htmlText);
		
		rta.setSpellCheckInProgress(true);
		rta.setSpellChecker( new AbstractRichTextSpellChecker() {
			
			/**
			 * We have implemented the spelling logic here while
			 * the base class does the parsing.
			 */
			public String[] checkWord(String word) {
				if (word.equalsIgnoreCase("EchoPointNG")) {
					return new String[] {
							"EchoPointNG Is",
							"Amazing",
							"Cool Shit",
							"And I Cant",
							"Beleive It's Not Local",
					};
				}
				return null;
			}
		});
		
		final LabelEx label = new LabelEx();

		final ButtonEx button = new ButtonEx("Show HTML Text");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String currentText = rta.getText();
				label.setText(currentText);
				label.setLineWrap(true);
				
				DateFormat df = DateFormat.getInstance();
				String dt = df.format(Calendar.getInstance().getTime());
				
				currentText = currentText + "<br/><br/> appended new text at " + dt; 
				rta.setText(currentText);
			}
		});

		Column col = new Column();
		col.add(rta);
		col.add(button);
		col.add(label);
		
		return col;
	}
	
	public Component testDefault() {
		return new RichTextArea();
	}

	public Component testSizedRTA() {
		RichTextArea rta = new RichTextArea();
		rta.setHeight(new ExtentEx("444px"));
		rta.setWidth(new ExtentEx("444px"));
		return rta;
	}

	public Component testInsideModelWindow() {
		
		RichTextArea rta = new RichTextArea();
		rta.setText("Some text in the RTA");
		
		WindowPane w = new WindowPane("RichTextAreaTest", new Extent(500),new Extent(500));
		w.add(rta);
		w.setMovable(true);
		w.setModal(true);
		
		ContentPane pane = new ContentPane();
		pane.add(w);
		
		ContainerEx ex = new ContainerEx();
		ex.setBackground(Color.RED);
		ex.setHeight(new ExtentEx("500px"));

		DisplayLayoutData displayLayoutData = new DisplayLayoutData();
		displayLayoutData.setHeight(new ExtentEx("100%"));
		pane.setLayoutData(displayLayoutData);

		ex.add(pane);
		//ex.add(rta);
		return w;
	}
}
