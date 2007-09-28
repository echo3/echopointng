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

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.CheckBox;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.CheckBoxEx;
import echopointng.ExtentEx;
import echopointng.LabelEx;
import echopointng.MutableStyleEx;
import echopointng.PushButton;
import echopointng.RadioButtonEx;
import echopointng.model.ActionEventEx;
import echopointng.util.ColorKit;
import echopointng.util.FontKit;
import echopointng.xhtml.XhtmlFragment;

/**
 * <code>TestButton</code>
 */
public class TestButton extends TestCaseBaseNG {
	
	public String getTestCategory() {
		return "ButtonEx";
	}
	
	public Component testButtons() {
		
		Button b;
		Grid cell = new Grid();
		cell.setInsets(new Insets(5));
		
		cell.add(createButton("ButtonEx 1"));
		cell.add(createButton("ButtonEx 2"));
		
		b = createButton("ButtonEx disabled"); b.setEnabled(false);
		cell.add(b);
		
		HttpImageReference bgTile = new HttpImageReference("/images/tile_back.gif",null,new Extent(10));
		FillImage fillImage = new FillImage(bgTile, new Extent(0), new Extent(0), FillImage.REPEAT_HORIZONTAL);
		
		b = new ButtonEx("With Background");
		b.setBackgroundImage(fillImage);
		cell.add(b);
		
		return cell;
	}

	public Component testDefaultButton() {
		Grid cell = new Grid();
		// cell.setInsets(new Insets(5));
		
		ButtonEx b;
		b = new ButtonEx("ButtonEx 1");
		cell.add(b);
		
		b = new ButtonEx("ButtonEx 2");
		cell.add(b);
		
		b = new ButtonEx("Disabled ButtonEx 3");
		b.setEnabled(false);
		cell.add(b);
		return cell;
	}

	public Component testPushButtons() {
		Button b;
		Grid cell = new Grid();
		cell.add(createPushButton("PushButton 1"));
		cell.add(createPushButton("PushButton 2"));
		cell.add(new PushButton("Default PushButton 3"));
		cell.add(new PushButton("Default PushButton 4"));
		cell.add(new PushButton("Default PushButton 5"));
		
		b = createPushButton("PushButton disabled");
		b.setEnabled(false);
		cell.add(b);
		
		HttpImageReference bgTile = new HttpImageReference("/images/tile_back.gif",null,new Extent(10));
		FillImage fillImage = new FillImage(bgTile, new Extent(0), new Extent(0), FillImage.REPEAT);
		b = new PushButton("PushButton with background");
		b.setBackgroundImage(fillImage);
		cell.add(b);
		
		return cell;
	}
	
	public Component testToggleButtons() {
		Grid cell = new Grid();
		CheckBoxEx checkBoxEx = new CheckBoxEx("CheckBox 1");
		cell.add(checkBoxEx);
		
		RadioButtonEx radioButtonEx = new RadioButtonEx("RadioButton 1");
		cell.add(radioButtonEx);
		
		return cell;
	}
	
	private Button createPushButton(String buttonText) {
		ButtonEx b = new PushButton(buttonText);
		b.setBackground(Color.LIGHTGRAY);
		// b.setBorder(new Border(1,Color.RED,Border.STYLE_SOLID));
		b.setOutsets(new Insets(2));
		b.setInsets(new Insets(10));
		b.setIcon(new HttpImageReference("images/ArrowRoundLeft.gif"));

		b.setRolloverEnabled(true);
		b.setRolloverBackground(Color.PINK);
		b.setRolloverBorder(new Border(1,Color.BLUE,Border.STYLE_SOLID));
		b.setRolloverIcon(new HttpImageReference("images/ArrowRoundRight.gif"));

		b.setPressedEnabled(true);
		b.setPressedBackground(Color.YELLOW);
		b.setPressedBorder(new Border(1,Color.GREEN,Border.STYLE_SOLID));
		
		b.setDisabledBackground(Color.ORANGE);
		b.setDisabledBorder(new BorderEx(1,Color.RED, BorderEx.STYLE_DASHED));
		b.setDisabledBackgroundImage(new FillImage(new HttpImageReference("images/t_sunflowers.jpg")));
		b.setDisabledIcon(new HttpImageReference("images/poker_monkey_no.gif",new ExtentEx(25),new ExtentEx(25)));
		
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				e = null;
			}
		});
		return b;
	}

	private Button createButton(String buttonText) {
		ButtonEx b = new ButtonEx(buttonText);
		b.setToolTipText("Tooltip for " + buttonText);
		b.setBackground(Color.LIGHTGRAY);
		b.setBorder(new Border(1,Color.RED,Border.STYLE_SOLID));
		b.setOutsets(new Insets(2));
		b.setInsets(new Insets(10));
		b.setIcon(new HttpImageReference("images/ArrowRoundLeft.gif"));

		b.setRolloverEnabled(true);
		b.setRolloverBackground(Color.PINK);
		b.setRolloverBorder(new Border(1,Color.BLUE,Border.STYLE_SOLID));
		b.setRolloverIcon(new HttpImageReference("images/ArrowRoundRight.gif"));

		b.setPressedEnabled(true);
		b.setPressedBackground(Color.YELLOW);
		b.setPressedBorder(new Border(1,Color.GREEN,Border.STYLE_SOLID));
		
		b.setDisabledBackground(Color.ORANGE);
		b.setDisabledBorder(new BorderEx(1,Color.RED, BorderEx.STYLE_DASHED));
		b.setDisabledBackgroundImage(new FillImage(new HttpImageReference("images/t_sunflowers.jpg")));
		b.setDisabledIcon(new HttpImageReference("images/poker_monkey_no.gif",new ExtentEx(25),new ExtentEx(25)));

		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				e = null;
			}
		});
		return b;
	}
	
	public Component testLookAndFeel() {
	
		Font buttonFont = FontKit.makeFont("'Segoe UI,Corbel,Calibri,Tahoma,Lucida Sans Unicode,sans-serif', plain, 8pt");
		Color bluegrayBG = ColorKit.clr("#EBE9ED");
		Color grayishBorderClr = ColorKit.clr("#A7A6AA");
		BorderEx outsetBorder = new BorderEx(
									new ExtentEx(1), Color.WHITE, Border.STYLE_SOLID,
									new ExtentEx(1), grayishBorderClr, Border.STYLE_SOLID,
									new ExtentEx(1), Color.WHITE, Border.STYLE_SOLID,
									new ExtentEx(1), grayishBorderClr, Border.STYLE_SOLID
									);

		BorderEx insetBorder = new BorderEx(
				new ExtentEx(1), grayishBorderClr, Border.STYLE_SOLID,
				new ExtentEx(1), Color.WHITE, Border.STYLE_SOLID,
				new ExtentEx(1), grayishBorderClr, Border.STYLE_SOLID,
				new ExtentEx(1), Color.WHITE, Border.STYLE_SOLID
				);

		BorderEx flatBorder = new BorderEx(
				new ExtentEx(1), bluegrayBG, Border.STYLE_SOLID,
				new ExtentEx(1), bluegrayBG, Border.STYLE_SOLID,
				new ExtentEx(1), bluegrayBG, Border.STYLE_SOLID,
				new ExtentEx(1), bluegrayBG, Border.STYLE_SOLID
				);

		
		MutableStyleEx outsetStyle = new MutableStyleEx();
		outsetStyle.setProperty(Button.PROPERTY_ROLLOVER_ENABLED,true);
		outsetStyle.setProperty(Button.PROPERTY_PRESSED_ENABLED,true);
		outsetStyle.setProperty(Button.PROPERTY_INSETS,new Insets(0));
		outsetStyle.setProperty(Button.PROPERTY_FONT,buttonFont);
		
		outsetStyle.setProperty(Button.PROPERTY_BACKGROUND,bluegrayBG);
		outsetStyle.setProperty(Button.PROPERTY_BORDER,flatBorder);
		outsetStyle.setProperty(Button.PROPERTY_ROLLOVER_BACKGROUND,bluegrayBG);
		outsetStyle.setProperty(Button.PROPERTY_ROLLOVER_BORDER,outsetBorder);
		
		outsetStyle.setProperty(Button.PROPERTY_PRESSED_BACKGROUND,bluegrayBG);
		outsetStyle.setProperty(Button.PROPERTY_PRESSED_BORDER,insetBorder);


		MutableStyleEx insetStyle = new MutableStyleEx(outsetStyle);
		insetStyle.setProperty(Button.PROPERTY_ROLLOVER_BORDER,insetBorder);
		insetStyle.setProperty(Button.PROPERTY_PRESSED_BORDER,outsetBorder);

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("1em"));
		// cell.setInsets(new Insets(1));

		cell.add(createButtonRow(outsetStyle,bluegrayBG));
		cell.add(createButtonRow(insetStyle,bluegrayBG));
		return cell;
	}
	
	private Component createButtonRow(Style style, Color background) {
		Row cell = new Row();
		cell.setBackground(background);
		cell.setInsets(new Insets(1));
		cell.setCellSpacing(new ExtentEx("1em"));

		ButtonEx btn;
		/*--*/
		btn = new ButtonEx("Copy");
		btn.setIcon(new HttpImageReference("images/copy.png"));
		btn.setStyle(style);
		cell.add(btn);
		/*--*/
		btn = new ButtonEx("Cut");
		btn.setIcon(new HttpImageReference("images/cut.png"));
		btn.setStyle(style);
		cell.add(btn);
		/*--*/
		btn = new PushButton("Paste");
		btn.setIcon(new HttpImageReference("images/paste.png"));
		btn.setStyle(style);
		cell.add(btn);
		
		return cell;
	}
	
	public Component testXhtmlSupport() {
		
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("2em"));
		
		XhtmlFragment fragment = new XhtmlFragment("" +
				"This is <em>XHTML</em>.  " +
				"" +
				"<p>Multi lined paragraphs work?</p>" +
				"" +
				"<h2>As do headers</h2>" +
				"" +
				"and <span style=\"font-size:14pt;color:red\">multi font</span> and " +
				"<span style=\"font-size:16pt;color:blue\">multi colored</span> text" +
				"");
		
		
		ButtonEx buttonEx = new ButtonEx(fragment);
		cell.add(buttonEx);
		buttonEx.setIcon(new HttpImageReference("images/ArrowRoundLeft.gif"));

// HttpImageReference bgTile = new
// HttpImageReference("/images/tile_back.gif",null,new Extent(10));
// FillImage fillImage = new FillImage(bgTile, new Extent(0), new Extent(0),
// FillImage.REPEAT);
// buttonEx.setBackgroundImage(fillImage);

		
		buttonEx = new PushButton(fragment);
		cell.add(buttonEx);
		return cell;
	}
	
	public Component testEnabledNess() {
	
		final CheckBox chkButtonsDisabled = new CheckBox("Disable Buttons");
		
		final PushButton pushButton1 = new PushButton("Push Button 1");
		final PushButton pushButton2 = new PushButton("Push Button 2 (Submit)");
		final ButtonEx buttonEx1 = new ButtonEx("ButtonEx");
		final Button button = new Button("Button");

		pushButton2.setSubmitButton(true);
		
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == chkButtonsDisabled) {
					boolean enabled = true;
					if (chkButtonsDisabled.isSelected()) {
						enabled = false;
					}
					pushButton1.setEnabled(enabled);
					pushButton2.setEnabled(enabled);
					buttonEx1.setEnabled(enabled);
					button.setEnabled(enabled);
				} else {
					((Button) e.getSource()).setText(e.getSource().getClass().getName() + " Button Pressed!");
				}
			};
			
		};
		chkButtonsDisabled.addActionListener(actionListener);
		
		pushButton1.addActionListener(actionListener);
		pushButton2.addActionListener(actionListener);
		buttonEx1.addActionListener(actionListener);
		button.addActionListener(actionListener);
		
		Column cell = new Column();
		
		cell.add(chkButtonsDisabled);
		cell.add(pushButton1);
		cell.add(pushButton2);
		cell.add(buttonEx1);
		cell.add(button);
		
		return cell;
	}
	
	public Component testMetaKeySupport() {
		final LabelEx metaKeyLabel = new LabelEx();
		
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e instanceof ActionEventEx) {
					int metaKeys = ((ActionEventEx)e).getMetaKeyInfo();
					
					String newLabelTest = e.getSource().getClass().getName();
					if ((metaKeys & ActionEventEx.METAKEY_ALT) == ActionEventEx.METAKEY_ALT) {
						newLabelTest += " ALT";
					}
					if ((metaKeys & ActionEventEx.METAKEY_CONTROL) == ActionEventEx.METAKEY_CONTROL) {
						newLabelTest += " CTRL";
					}
					if ((metaKeys & ActionEventEx.METAKEY_META) == ActionEventEx.METAKEY_META) {
						newLabelTest += " META";
					}
					if ((metaKeys & ActionEventEx.METAKEY_SHIFT) == ActionEventEx.METAKEY_SHIFT) {
						newLabelTest += " SHIFT";
					}
					newLabelTest += " CLICKED";
					metaKeyLabel.setText(newLabelTest);
				}
				
			}
		};
		
		ButtonEx buttonEx = new ButtonEx("Press Me!");
		buttonEx.addActionListener(al);
		
		PushButton pushButton = new PushButton("And Me");
		pushButton.addActionListener(al);
		
		Column cell = new Column();
		cell.add(buttonEx);
		cell.add(pushButton);
		cell.add(metaKeyLabel);
		return cell;
	}
}

