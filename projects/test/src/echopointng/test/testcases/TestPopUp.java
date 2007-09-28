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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.CheckBox;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.SelectField;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.list.DefaultListModel;
import nextapp.echo2.app.text.StringDocument;
import echopointng.BalloonHelp;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.Calculator;
import echopointng.CalculatorField;
import echopointng.ComboBox;
import echopointng.ContainerEx;
import echopointng.DateChooser;
import echopointng.DateField;
import echopointng.DropDown;
import echopointng.ExtentEx;
import echopointng.ImageIcon;
import echopointng.LabelEx;
import echopointng.PopUp;
import echopointng.Strut;
import echopointng.TemplatePanel;
import echopointng.layout.DisplayLayoutData;
import echopointng.model.DefaultCalendarSelectionModel;
import echopointng.template.StringTemplateDataSource;

/**
 * <code>TestPopUp</code>
 */

public class TestPopUp extends TestCaseBaseNG {

	private Component createPopUpContent() {
		final LabelEx pressedLabel = new LabelEx("What was pressed?");
		ImageIcon icon = new ImageIcon(new HttpImageReference("images/t_cake.jpg", new Extent(20), new Extent(20)));
		icon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressedLabel.setText(e.getSource().toString() + " was pressed");
			}
		});
		Grid g = new Grid(2);
		g.setBorder(new BorderEx(1));
		g.add(icon);
		g.add(pressedLabel);
		g.add(new DateChooser());
		return g;
	}

	private Component createPopUpContent(PopUp childPopUp) {
		Grid g = new Grid(1);
		g.add(createPopUpContent());
		g.add(childPopUp);
		return g;
	}

	private TextField createTF() {
		return createTF("Target Here");
	}

	private TextField createTF(String text) {
		TextField tf = new TextField(new StringDocument(), text, 20);
		tf.setBorder(BorderEx.NONE);
		return tf;
	}

	/**
	 * @see echopointng.test.testcases.TestCaseBaseNG#getTestCategory()
	 */
	public String getTestCategory() {
		return "PopUp";
	}

	public Component testBalloonHelp() {

		String html = "<bdo>This is some " + "<b>XHTML</b> text inside a " + "<code>TemplatePanel</code><br/> and hence "
				+ "it can be used as the contents of <br/>" + "this <strong>BallonHelp</strong> component.</bdo>";

		TemplatePanel helpText1 = new TemplatePanel(new StringTemplateDataSource(html));
		TemplatePanel helpText2 = new TemplatePanel(new StringTemplateDataSource(html));

		BalloonHelp balloonHelp1 = new BalloonHelp();
		balloonHelp1.setPopUp(helpText1);
		balloonHelp1.setTarget(new LabelEx("Rollover the icon to open==>"));

		BalloonHelp balloonHelp2 = new BalloonHelp();
		balloonHelp2.setPopUp(helpText2);
		balloonHelp2.setTarget(new LabelEx("Click the icon to open==>"));
		balloonHelp2.setPopUpOnRollover(false);

		Grid row = new Grid(3);
		row.setInsets(new Insets(0, 200, 0, 0));
		row.add(balloonHelp1);
		row.add(new LabelEx(" A simple label "));
		row.add(balloonHelp2);
		return row;
	}

	public Component testComboBox() {

		final LabelEx comboLabel = new LabelEx("");
		String[] items = { "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight, the number eight, try a longer option" };
		final ComboBox comboBox = new ComboBox("testing", items);

		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Calendar cal = Calendar.getInstance(TimeZone.getDefault());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				comboLabel.setText(e.getSource().toString() + " was selected at " + sdf.format(cal.getTime()));
			}
		});

		final CheckBox checkAutoRecall = new CheckBox("AutoRecall");
		checkAutoRecall.setSelected(comboBox.isAutoRecall());
		checkAutoRecall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox.setAutoRecall(checkAutoRecall.isSelected());
			}
		});

		final CheckBox checkActionOnSelection = new CheckBox("ActionOnSelection");
		checkActionOnSelection.setSelected(comboBox.isActionOnSelection());
		checkActionOnSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox.setActionOnSelection(checkActionOnSelection.isSelected());
			}
		});

		final CheckBox checkTextMatching = new CheckBox("TextMatching");
		checkTextMatching.setSelected(comboBox.isTextMatchingPerformed());
		checkTextMatching.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox.setTextMatchingPerformed(checkTextMatching.isSelected());
			}
		});

		final CheckBox checkCaseSensitive = new CheckBox("CaseSensitive");
		checkCaseSensitive.setSelected(comboBox.isCaseSensitive());
		checkCaseSensitive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox.setCaseSensitive(checkCaseSensitive.isSelected());
			}
		});

		final TextField textListRowCount = new TextField(new StringDocument(), Integer.toString(comboBox.getListRowCount()), 3);
		textListRowCount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBox.setListRowCount(Integer.parseInt(textListRowCount.getText()));
			}
		});

		Column column = new Column();
		column.add(comboBox);
		column.add(checkAutoRecall);
		column.add(checkActionOnSelection);
		column.add(checkTextMatching);
		column.add(checkCaseSensitive);

		Row row = new Row();
		row.add(textListRowCount);
		row.add(new LabelEx(" visible rows in the popup list"));

		column.add(row);
		column.add(comboLabel);

		ComboBox comboBox2 = new ComboBox(new Object[] { "Alpha", "Beta", "Delta", "Omega" });
		comboBox2.setEnabled(false);
		comboBox2.getTextField().setText("Disabled");
		column.add(comboBox2);

		return column;
	}

	public Component testDateField() {
		Column col = new Column();

		DateField dateField = new DateField();
		col.add(dateField);

		dateField = new DateField();
		dateField.setEnabled(false);
		col.add(dateField);

		dateField = new DateField();
		dateField.setWidth(new Extent(135));
		dateField.setBackground(new Color(0xFFFFFF));
		dateField.getDateChooser().setModel(new DefaultCalendarSelectionModel(Calendar.getInstance()));
		col.add(dateField);

		return col;
	}

	public Component testPopUp() {
		Row row = new Row();

		PopUp popup;

		popup = new PopUp();
		popup.setTarget(createTF());
		popup.setPopUp(new Calculator());

		row.add(popup);
		row.add(new Strut(100, 0));
		row.add(new LabelEx("Well how is it?"));

		ContainerEx containerEx = new ContainerEx();
		containerEx.setInsets(new Insets(100, 300, 0, 0));
		containerEx.add(row);
		return containerEx;
	}

	public Component testPopUpAlignments() {
		Row row = new Row();

		PopUp popup;
		LabelEx label;

		label = new LabelEx("This will popup");
		label.setWidth(new ExtentEx("200px"));
		label.setHeight(new ExtentEx("200px"));

		// TOP
		popup = new PopUp();
		popup.setTarget(createTF("LEFT,TOP"));
		popup.setPopUp(createPopUpContent());
		popup.setPopUpAlignment(new Alignment(Alignment.LEFT, Alignment.TOP));
		row.add(popup);
		row.add(new Strut(30, 0));

		popup = new PopUp();
		popup.setTarget(createTF("CENTER,TOP"));
		popup.setPopUp(createPopUpContent());
		popup.setPopUpAlignment(new Alignment(Alignment.CENTER, Alignment.TOP));
		row.add(popup);
		row.add(new Strut(30, 0));

		popup = new PopUp();
		popup.setTarget(createTF("RIGHT,TOP"));
		popup.setPopUp(createPopUpContent());
		popup.setPopUpAlignment(new Alignment(Alignment.RIGHT, Alignment.TOP));
		row.add(popup);
		row.add(new Strut(30, 0));

		// BOTTOM
		popup = new PopUp();
		popup.setTarget(createTF("LEFT,BOTTOM"));
		popup.setPopUp(createPopUpContent());
		popup.setPopUpAlignment(new Alignment(Alignment.LEFT, Alignment.BOTTOM));
		row.add(popup);
		row.add(new Strut(30, 0));

		popup = new PopUp();
		popup.setTarget(createTF("CENTER,BOTTOM"));
		popup.setPopUp(createPopUpContent());
		popup.setPopUpAlignment(new Alignment(Alignment.CENTER, Alignment.BOTTOM));
		row.add(popup);
		row.add(new Strut(30, 0));

		popup = new PopUp();
		popup.setTarget(createTF("RIGHT,BOTTOM"));
		popup.setPopUp(createPopUpContent());
		popup.setPopUpAlignment(new Alignment(Alignment.RIGHT, Alignment.BOTTOM));
		row.add(popup);
		row.add(new Strut(30, 0));

		// CENTER
		popup = new PopUp();
		popup.setTarget(createTF("LEFT,CENTER"));
		popup.setPopUp(createPopUpContent());
		popup.setPopUpAlignment(new Alignment(Alignment.LEFT, Alignment.CENTER));
		row.add(popup);
		row.add(new Strut(30, 0));

		popup = new PopUp();
		popup.setTarget(createTF("CENTER,CENTER"));
		popup.setPopUp(createPopUpContent());
		popup.setPopUpAlignment(new Alignment(Alignment.CENTER, Alignment.CENTER));
		row.add(popup);
		row.add(new Strut(30, 0));

		popup = new PopUp();
		popup.setTarget(createTF("RIGHT,CENTER"));
		popup.setPopUp(createPopUpContent());
		popup.setPopUpAlignment(new Alignment(Alignment.RIGHT, Alignment.CENTER));
		row.add(popup);
		row.add(new Strut(30, 0));
		row.add(new LabelEx("Well how is it?"));

		ContainerEx containerEx = new ContainerEx();
		containerEx.setInsets(new Insets(100, 300, 0, 0));
		containerEx.setHeight(new ExtentEx(400));
		containerEx.add(row);
		return containerEx;
	}

	public Component testPopUpVersusSelectField() {

		String[] listItems = new String[] { "Alpha", "Beta", "Gamma" };

		PopUp popUp = new PopUp();
		styleIt(popUp);
		popUp.setPopUp(genRandomTextContent());
		popUp.setTarget(createTF("PopUp"));

		DropDown dropDown = new DropDown();
		styleIt(dropDown);
		dropDown.setPopUp(genRandomTextContent());
		dropDown.setTarget(createTF("DropDown"));

		ComboBox comboBox = new ComboBox("ComboBox");
		comboBox.setListModel(new DefaultListModel(listItems));

		SelectField selectField = new SelectField(listItems);

		Column cell = new Column();
		cell.setCellSpacing(new Extent(25));
		cell.add(selectField);
		cell.add(popUp);
		cell.add(dropDown);

		return cell;
	}

	private void styleIt(PopUp popUp) {
	}

	public Component testPopUpWithinPopup() {
		PopUp popupChild = new PopUp();
		popupChild.setTarget(new ImageIcon(new HttpImageReference("images/t_cake.jpg", new Extent(10), new Extent(10))));
		popupChild.setPopUp(createPopUpContent());

		final DropDown dropDownParent = new DropDown();
		dropDownParent.setTarget(createTF());
		dropDownParent.setPopUp(createPopUpContent(popupChild));
		dropDownParent.setPopUpNextToToggle(false);

		PopUp dropDownParent2 = new PopUp();
		dropDownParent2.setTarget(createTF());
		dropDownParent2.setPopUp(createPopUpContent());

		ButtonEx button = new ButtonEx("Toggle Parent");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dropDownParent.setExpanded(!dropDownParent.isExpanded());
			}
		});

		Grid grid = new Grid();
		grid.setSize(3);
		grid.setInsets(new Insets(0, 100, 0, 0));
		grid.add(dropDownParent);
		grid.add(dropDownParent2);
		grid.add(button);
		return grid;
	}

	public Component testPopUpWithinWindowPane() {

		
		CalculatorField calculatorField1 = new CalculatorField(1);
		calculatorField1.setPopUpAlwaysOnTop(true);

		CalculatorField calculatorField2 = new CalculatorField(0);
		calculatorField2.setPopUpAlwaysOnTop(false);
		
		Column cell = new Column();
		cell.add(calculatorField1);
		cell.add(calculatorField2);
		
		WindowPane windowPane = new WindowPane();
		windowPane.setWidth(new ExtentEx(180));
		windowPane.setHeight(new ExtentEx(500));
		windowPane.add(cell);
		windowPane.setModal(true);

		return windowPane;
	}

	public Component testPopupOverSelectField() {
		
		String selectData[] = new String[] { "AlphaRomeo", "BetaFoxTrot", "DeltaVictor", "OmegaBravo" };

		Column cell = new Column();
		cell.add(new DateField());
		for (int i = 0; i < 8; i++) {
			Row row = new Row();
			row.add(new SelectField(selectData));
			row.add(new LabelEx("<<== Can we hover over these on IE?"));
			cell.add(row);
		}
		
		cell.add(new ComboBox(selectData));
		for (int i = 0; i < 8; i++) {
			Row row = new Row();
			row.add(new SelectField(selectData));
			row.add(new LabelEx("<<== Can we hover over these on IE?"));
			cell.add(row);
		}

		cell.add(new BalloonHelp("This is some ballon help.  See it hover!"));

		cell.add(new DateField());
		cell.add(new DateField());
		cell.add(new DateField());

		return cell;
	}
	
	public Component testWithSplitPane() {
		PopUp popUpAlwaysOnTop = new DateField();
		popUpAlwaysOnTop.setPopUpAlwaysOnTop(true);
		
		PopUp popUpDefault = new DateField();
		
		Column column = new Column();
		column.setCellSpacing(new ExtentEx("10px"));
		column.add(popUpAlwaysOnTop);
		column.add(popUpDefault);

		SplitPane splitPane = new SplitPane();
		splitPane.setOrientation(SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM);
		splitPane.add(column);
		splitPane.add(genRandomTextContent());
		splitPane.setSeparatorPosition(new Extent(70));

		ContainerEx containerEx = new ContainerEx();
		containerEx.setWidth(new ExtentEx(500));
		containerEx.setHeight(new ExtentEx(500));
		containerEx.setPosition(ContainerEx.RELATIVE);
		containerEx.setBorder(new BorderEx());

		containerEx.add(splitPane);
		return containerEx;
	}
	
	public Component testWithLargeDOM() {
		Column cell  = new Column();
		PopUp popUp = new DateField();
		cell.add(popUp);
		
		ContainerEx containerEx = new ContainerEx();
		containerEx.setBorder(BorderEx.DEFAULT);
		containerEx.setHeight(new ExtentEx("500px"));
		cell.add(containerEx);
		for (int i = 0; i < 100; i++) {
			containerEx.add(genForm());
			containerEx.add(genRandomTextContent());
		}
		return cell;
	}
	
	private Component genForm() {
		ContainerEx containerEx = new ContainerEx();
		containerEx.setHeight(new ExtentEx(150));
		containerEx.setBorder(BorderEx.DEFAULT);
		containerEx.setOutsets(new Insets(10));
		TextField textField;

		int col = 0;
		int row = 0;

		for (int i = 0; i < 10; i++) {
			textField = new TextField();
			textField.setText("TF"+col);
			textField.setLayoutData(new DisplayLayoutData(new ExtentEx((row*150)+10),new ExtentEx(((col*30)+10))));
			containerEx.add(textField);
			row++;
			if (row % 3 == 0) {
				row = 0;
				col++;
			}
		}
		return containerEx;
	}
	
	public Component testCaret() {
		PopUp pop = new PopUp();
	    pop.setPopUp(new TextField());
	    pop.setPopUpOnRollover(false);
	    pop.setPopUpAlwaysOnTop(true);
	    pop.setPopUpAlignment(new Alignment(Alignment.RIGHT, Alignment.BOTTOM));
	    return pop;
	}
		
	public Component testDispose() {

		final PopUp pop = new PopUp();
	    pop.setPopUp(new TextField());
	    pop.setTarget(new TextField());
	    pop.setPopUpOnRollover(false);
	    pop.setPopUpAlwaysOnTop(true);
	    pop.setPopUpAlignment(new Alignment(Alignment.RIGHT, Alignment.BOTTOM));
	    
	    final ButtonEx btnGo = new ButtonEx("Change Popup");
	    btnGo.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		pop.setPopUpAlwaysOnTop( ! pop.getPopUpAlwaysOnTop());
	    		((TextField)pop.getTarget()).setText(String.valueOf(new Date()));
	    	};
	    });
	    Column cell = new Column();
	    cell.add(btnGo);
	    cell.add(pop);
	    return cell;
		
	}
		
	
}
