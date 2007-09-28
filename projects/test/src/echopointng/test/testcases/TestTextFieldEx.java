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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.CheckBox;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.SelectField;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.DocumentEvent;
import nextapp.echo2.app.event.DocumentListener;
import echopointng.AbleProperties;
import echopointng.AutoLookupTextFieldEx;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.ExtentEx;
import echopointng.Strut;
import echopointng.TextFieldEx;
import echopointng.able.MouseCursorable;
import echopointng.text.AutoLookupModel;
import echopointng.util.ColorKit;
import echopointng.util.FontKit;
import echopointng.util.RandKit;
import echopointng.util.TokenizerKit;

/**
 * <code>TestTextFieldEx</code>
 */

public class TestTextFieldEx extends TestCaseBaseNG {

	public String getTestCategory() {
		return "TextFieldEx";
	}

	public Component testAutoLookupTextFieldEx() {
		
		AutoLookupModel autoLookupModel = new AutoLookupModel() {

			String[] strValues = { 
					"Brad Baker", "Bruce Baker", "Brian Baker", "Brodie Baker", "Allen Brodie", "Allen Brooks", "Acmed Brohumdal",
					"Geoff Baker", "Noel Baker", "Kerrie Baker", "Merv Baker", "Amanda Baker", "Maise Baker", "Matilda Baker",
					"Anthony Aaardvark", "Anthony Wiggle", "Anthony Field", "Allen Allenson",
					"Chris Kristofferson", "Chris Christofferson", "Chris Maclean", "Peter Chris",
					"Tommy Tom Tommahawk", "Tommy Radonikis", "Tommy Pinball Wizard", "Tommy Gun",
				};

			public int getMatchOptions() {
				return 0;
			}

			public int getMaximumCacheAge() {
				return -1;
			}

			public int getMaximumCacheSize() {
				return -1;
			}
			
			private Entry makeEntry(String s) {
				String[] words = TokenizerKit.tokenize(s, " ");
				String value = s;
				String sortValue = s;
				String xhtml = "";
				xhtml += "\"";
				for (int i = 0; i < words.length; i++) {
					xhtml += words[i];
					if (i < words.length -1) {
						xhtml += " ";
					}
				}
				xhtml += "\" <";
				for (int i = 0; i < words.length; i++) {
					xhtml += words[i];
					if (i < words.length - 1) {
						xhtml += ".";
					} else {
						xhtml += "@" + RandKit.roll(new String[] { "ibm.com", "nextapp.com", "epng.org.au", "spam.org" });
					}
				}
				xhtml += ">";
				
				return new DefaultEntry(value,sortValue,xhtml);
			}

			public Entry[] prePopulate() {

				List entries = new ArrayList();
				for (int i = 0; i < strValues.length; i++) {
					String value = strValues[i];
					if (value.indexOf("Br") != -1) {
						entries.add(makeEntry(value));
					}
				}
				return (Entry[]) entries.toArray(new Entry[entries.size()]);
			}

			public Entry[] searchEntries(String partialSearchValue, int matchOptions) {
				boolean caseSensitive = (matchOptions & AutoLookupModel.MATCH_IS_CASE_SENSITIVE) == AutoLookupModel.MATCH_IS_CASE_SENSITIVE;
				boolean matchFromStart = (matchOptions & AutoLookupModel.MATCH_ONLY_FROM_START) == AutoLookupModel.MATCH_ONLY_FROM_START;
				List matches = new ArrayList();
				
				if (! caseSensitive) {
					partialSearchValue = partialSearchValue.toUpperCase();
				}
				for (int i = 0; i < strValues.length; i++) {
					String value = strValues[i];
					if (! caseSensitive) {
						value = value.toUpperCase();
					}
					if (matchFromStart) {
						if (value.indexOf(partialSearchValue) == 0) {
							matches.add(makeEntry(value));
						}
					} else {
						if (value.indexOf(partialSearchValue) != -1) {
							matches.add(makeEntry(strValues[i]));
						}
					}
				}
				// and for good measure add something that always matchs to grow the list
				String now = new SimpleDateFormat("HH:mm:ss").format(new Date());
				matches.add(makeEntry(partialSearchValue + " search done at " + now));
				
				
				// take some time
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				return (Entry[]) matches.toArray(new Entry[matches.size()]);
			}
		};
		
		final Column col = new Column();
		col.setInsets(new Insets(30));
		col.setCellSpacing(new ExtentEx("10px"));

		final AutoLookupTextFieldEx textFieldEx1 = new AutoLookupTextFieldEx();
		textFieldEx1.setAutoLookupModel(autoLookupModel);

		final AutoLookupTextFieldEx textFieldEx2 = new AutoLookupTextFieldEx();
		textFieldEx2.setAutoLookupModel(autoLookupModel);
		textFieldEx2.setNoMatchingOptionText("No stuff found");
		textFieldEx2.setSearchBarShown(false);
		
		col.add(textFieldEx1);
		col.add(new SelectField(new String[] {"Some log text that needs to be hidden when popped over" }));
		col.add(new Strut(1,80));
		col.add(textFieldEx2);
		return col;
	}
	
	public Component testTextFieldEx() {
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("5em"));
		cell.setInsets(new Insets(20));

		final CheckBox onChangeCB  = new CheckBox("Invoke ActionListener on change?");
		final CheckBox onBorderCB  = new CheckBox("Use colorful borders?");
		final CheckBox onAccessKeyCB  = new CheckBox("Use Alt G as access key?");
		final CheckBox onInsetsOutsetCB  = new CheckBox("Use Inset (10px) / Outsets (5px)?");
		final CheckBox onMouseCursorCB  = new CheckBox("Use Cross Hair Mouse Cursor?");
		
		final TextFieldEx textFieldEx = new TextFieldEx("",40);

		final Border COLORFUL_BORDER = new BorderEx(
				1,Color.RED,BorderEx.STYLE_DASHED,
				1,Color.BLUE,BorderEx.STYLE_DOTTED,
				1,Color.GREEN,BorderEx.STYLE_SOLID,
				2,Color.YELLOW,BorderEx.STYLE_GROOVE);
		
		final ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == onChangeCB) {
					textFieldEx.setActionCausedOnChange(onChangeCB.isSelected());
					//-------------------	
				} else if (e.getSource() == onBorderCB) {
					Border border = (onBorderCB.isSelected() ? COLORFUL_BORDER : null);
					textFieldEx.setBorder(border);
					//-------------------	
				} else if (e.getSource() == onAccessKeyCB) {
					String accesskey = (onAccessKeyCB.isSelected() ? "G" : null);
					textFieldEx.setAccessKey(accesskey);	
					//-------------------	
					
				} else if (e.getSource() == onInsetsOutsetCB) {
					Insets insets = (onInsetsOutsetCB.isSelected() ? new Insets(10) : null);
					Insets outsets = (onInsetsOutsetCB.isSelected() ? new Insets(5) : null);
					textFieldEx.setInsets(insets);
					textFieldEx.setOutsets(outsets);
					//-------------------	
					
				} else if (e.getSource() == onMouseCursorCB) {
					int mouseCursor  = (onMouseCursorCB.isSelected() ? MouseCursorable.CURSOR_CROSSHAIR : MouseCursorable.CURSOR_DEFAULT);
					textFieldEx.setMouseCursor(mouseCursor);	
					//-------------------	
				} else {
					textFieldEx.setText("The ActionListener was invoked! " + RandKit.roll(
							new String[] { "Steve", "Harmison", "Andrew", "Flintoff"}));
					//-------------------	
				}
			}
		};
		textFieldEx.addActionListener(actionListener);
		onChangeCB.addActionListener(actionListener);
		onBorderCB.addActionListener(actionListener);
		onAccessKeyCB.addActionListener(actionListener);
		onInsetsOutsetCB.addActionListener(actionListener);
		onMouseCursorCB.addActionListener(actionListener);

		cell.add(textFieldEx);
		cell.add(onChangeCB);
		cell.add(onBorderCB);
		cell.add(onAccessKeyCB);
		cell.add(onInsetsOutsetCB);
		cell.add(onMouseCursorCB);
		cell.add(new TextFieldEx("Some other text box"));
		cell.add(new TextFieldEx("To where we can tab"));
		return cell;
	}

	public Component testDefault() {
		return new TextFieldEx();
	}

	private int i = 0;
	
	public Component testDocumentListeners() {
		Column colTest = new Column();
        final Label lblTest = new Label();
        final TextFieldEx txtTest  = new TextFieldEx();
        txtTest.setWidth(new Extent(100));
        txtTest.setMaximumLength(16);
        txtTest.setStyleName("Default");
        txtTest.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void documentUpdate(DocumentEvent e) {
                        lblTest.setText("Click : " + Integer.toString(++i));
                    }
        });
        colTest.add(txtTest);
        colTest.add(lblTest);
        ButtonEx btnTest = new ButtonEx("Test");
        btnTest.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            }
        });
        colTest.add(btnTest);		
        return colTest;
	}
	
	/**
	 * Called to create regex examples
	 */
	private void createRegex(Component cell, String regex, String initialValue, String descr) {
		AbleProperties failureProperties = new AbleProperties();
		failureProperties.setBackground(ColorKit.clr("#F1C9C9"));
		failureProperties.setBorder(new BorderEx(Color.RED));

		TextFieldEx textFieldEx  = new TextFieldEx(initialValue);
		textFieldEx.setBorder(new BorderEx(Color.BLUE));
		
		textFieldEx.setRegex(regex);
		textFieldEx.setRegexFailureProperties(failureProperties);
		
		descr = "regex " + (descr == null ? "" : " : " + descr) + " : ";
		
		Label info = new Label(descr + textFieldEx.getRegex());
		info.setFont(FontKit.font("Arial,plain,8pt"));
		cell.add(textFieldEx); 
		cell.add(info);
	}
	
	public Component testRegexSupport() {

		Grid cell = new Grid(2);
		cell.setInsets(new Insets(5));
		
		//-------------------
		createRegex(cell,"^[0-9]*$","abcedef", "numbers");
		//-------------------
		createRegex(cell,"^[0-9]*$","12345", "numbers");
		//-------------------
		createRegex(cell,"^[-+]?([0-9]*\\.[0-9]+|[0-9]+)$","12345", "floating point");
		//-------------------
		createRegex(cell,"^[-+]?([0-9]*\\.[0-9]+|[0-9]+)$","12345.567", "floating point");
		//-------------------
		createRegex(cell,"^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$","12345.567e24", "floating point with exponent");
		//-------------------
		createRegex(cell,"^\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b$","12345", "ip address");
		//-------------------
		createRegex(cell,"^\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b$","123.345.78.95", "ip address");
		//-------------------
		createRegex(cell,"^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$","bbakerman@there.com", "email address");

		
		return cell;
	}
}
