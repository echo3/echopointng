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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import echopointng.DateChooser;
import echopointng.DateField;
import echopointng.ExtentEx;
import echopointng.LabelEx;
import echopointng.Strut;
import echopointng.model.CalendarSelectionListener;
import echopointng.model.DefaultCalendarSelectionModel;

/**
 * <code>TestPopUp</code>
 */

public class TestDateField extends TestCaseBaseNG {

	/**
	 * @see echopointng.test.testcases.TestCaseBaseNG#getTestCategory()
	 */
	public String getTestCategory() {
		return "DateField";
	}

	public Component testDateField() {
		Column col = new Column();
		col.setCellSpacing(new ExtentEx("10px"));

		final LabelEx labelEx = new LabelEx();
		final DateField dateField = new DateField();
		dateField.getModel().addListener(new CalendarSelectionListener() {
			public void displayedDateChange(echopointng.model.CalendarEvent calEvent) {
			};

			public void selectedDateChange(echopointng.model.CalendarEvent calEvent) {
				StringBuffer sb = new StringBuffer();
				sb.append("SD : ");
				sb.append(new SimpleDateFormat("dd/MM/yyyy").format(dateField.getSelectedDate().getTime()));
				sb.append("DD : ");
				sb.append(new SimpleDateFormat("dd/MM/yyyy").format(dateField.getDisplayedDate().getTime()));

				labelEx.setText(sb.toString());
			};
		});
		col.add(labelEx);
		col.add(dateField);

		DateField dateFieldDisabled = new DateField();
		dateFieldDisabled.setEnabled(false);
		col.add(dateFieldDisabled);

		return col;
	}

	public Component testDateFieldNewModel() {
		Column col = new Column();
		col.setCellSpacing(new ExtentEx("10px"));

		DateField dateField = new DateField();
		dateField.setWidth(new Extent(135));
		dateField.setBackground(new Color(0xFFFFFF));

		DefaultCalendarSelectionModel model = new DefaultCalendarSelectionModel(Calendar.getInstance());
		dateField.setModel(model);
		col.add(dateField);

		return col;
	}

	public Component testDateFieldChanges() {
		Column col = new Column();
		col.setCellSpacing(new ExtentEx("10px"));

		final DateField dateField = new DateField();
		col.add(dateField);

		return col;
	}

	public Component testDateFieldSet() {
		Column col = new Column();
		col.setCellSpacing(new ExtentEx("10px"));

		Calendar gC = new GregorianCalendar();
		gC.setTime(new Date((long) 0));

		DateField df0 = new DateField();
		df0.setDisplayedDate(gC);

		DateField df1 = new DateField(gC);

		DateField df2 = new DateField();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, (int) (Math.random() * 25));
		cal = DateChooser.calendarCopy(cal);

		df2.setModel(new DefaultCalendarSelectionModel(cal));

		col.add(df0);
		col.add(df1);
		col.add(df2);

		return col;
	}

	public Component testDateFieldEnabled() {
		Column col = new Column();
		col.setCellSpacing(new ExtentEx("10px"));

		final DateField dateField = new DateField();
		final Button enabledButton = new Button("Disable!");
		enabledButton.addActionListener(new ActionListener() {
			public void actionPerformed(nextapp.echo2.app.event.ActionEvent e) {
				boolean enabled = dateField.isEnabled();
				dateField.setEnabled(!enabled);
				if (enabled) {
					enabledButton.setText("Enable");
				} else {
					enabledButton.setText("Disable");
				}
			};
		});

		col.add(dateField);
		col.add(enabledButton);
		return col;
	}

	public Component testStickySelectBug() {
		Row row1 = new Row();
		row1.setCellSpacing(new Extent(10, Extent.PX));
		final DateField mDateField = new DateField();
		mDateField.setPopUpAlignment(new Alignment(Alignment.CENTER, Alignment.BOTTOM));
		Button button1 = new Button();
		button1.setText("Push Here");
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    mDateField.setSelectedDate(Calendar.getInstance());
			    mDateField.setDisplayedDate(Calendar.getInstance());
			  
			}
		});
		row1.add(new Strut(1,300));
		row1.add(mDateField);
		return row1;
	}
	
	public Component testDateFieldFormatting() {
		DateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy");
		
		Column col = new Column();
		col.setCellSpacing(new ExtentEx("10px"));

		final LabelEx labelEx = new LabelEx();
		final DateField dateField = new DateField();
		dateField.setDateFormat(DATE_FORMAT);
		dateField.getModel().addListener(new CalendarSelectionListener() {
			public void displayedDateChange(echopointng.model.CalendarEvent calEvent) {
			};

			public void selectedDateChange(echopointng.model.CalendarEvent calEvent) {
				StringBuffer sb = new StringBuffer();
				sb.append("SD : ");
				sb.append(new SimpleDateFormat("dd/MM/yyyy").format(dateField.getSelectedDate().getTime()));
				sb.append("DD : ");
				sb.append(new SimpleDateFormat("dd/MM/yyyy").format(dateField.getDisplayedDate().getTime()));

				labelEx.setText(sb.toString());
			};
		});
		col.add(labelEx);
		col.add(dateField);
		
		return col;
		
	}
}
