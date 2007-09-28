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

import java.util.Calendar;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.layout.GridLayoutData;
import echopointng.DateChooser;
import echopointng.util.ColorKit;

/** 
 * <code>TestDateChooser</code> 
 */

public class TestDateChooser extends TestCaseBaseNG {

	
	public String getTestCategory() {
		return "DateChooser";
	}

	public Component testDateChooser() {
		DateChooser dateChooser = new DateChooser();
		dateChooser.setBorder(new Border(1,Color.BLACK,Border.STYLE_SOLID));
		dateChooser.setYearSelectable(true);
		dateChooser.setWeekNumberAbbreviation("Week");
		dateChooser.setWeekNumberForeground(ColorKit.clr("#9400D3"));
		return dateChooser;
	}
	
	public Component testDateChooserDisabled() {
		DateChooser dateChooser = new DateChooser();
		dateChooser.setBorder(new Border(1,Color.BLACK,Border.STYLE_SOLID));
		dateChooser.setYearSelectable(true);
		dateChooser.setEnabled(false);
		return dateChooser;
	}

	public Component testDateChooserRestricted() {
		Column cell = new Column();
		cell.setCellSpacing(new Extent(10));
		
		
		Calendar maxDate = Calendar.getInstance();
		maxDate.set(Calendar.YEAR,2007);
		
		Calendar minDate = Calendar.getInstance();
		minDate.set(Calendar.YEAR,2004);
		
		DateChooser dateChooser;
		
		dateChooser = new DateChooser();
		dateChooser.setBorder(new Border(1,Color.BLACK,Border.STYLE_SOLID));
		dateChooser.setYearSelectable(true);
		dateChooser.setMaximumDate(maxDate);
		dateChooser.setMinimumDate(minDate);
		cell.add(new Label("Navigation Uninhibited"));
		cell.add(dateChooser);

		dateChooser = new DateChooser();
		dateChooser.setBorder(new Border(1,Color.BLACK,Border.STYLE_SOLID));
		dateChooser.setYearSelectable(true);
		dateChooser.setMaximumDate(maxDate);
		dateChooser.setMinimumDate(minDate);
		dateChooser.setNavigationInhibited(true);
		cell.add(new Label("Navigation Inhibited"));
		cell.add(dateChooser);
		
		return cell;
	}

	public Component testFastVersusSlow() {
		Column cell = new Column();
		cell.setCellSpacing(new Extent(10));
		
		
		DateChooser dateChooserFast = new DateChooser();
		DateChooser dateChooserSlow = new DateChooser();
		dateChooserSlow.setFastMode(false);
		
		
		cell.add(new Label("Fast Mode"));
		cell.add(dateChooserFast);
		
		cell.add(new Label("Slow Mode"));
		cell.add(dateChooserSlow);
		
		return cell;
	}
	
	public Component testDateChooserDOW() {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.THURSDAY);
		
		Grid cell = new Grid(4);
		DateChooser dateChooser;
		
		int startDay = Calendar.SUNDAY;
		int endDay = Calendar.SATURDAY;
		
		//DefaultCalendarSelectionModel calModel = new DefaultCalendarSelectionModel(cal);
		
		for (int i = startDay; i <= endDay; i++) {
			dateChooser = new DateChooser();
			//dateChooser.setModel(calModel);
			dateChooser.setFastMode(true);

			dateChooser.setFirstDayOfWeek(i);
			
			GridLayoutData layoutData = new GridLayoutData();
			layoutData.setInsets(new Insets(5));
			dateChooser.setLayoutData(layoutData);
			cell.add(dateChooser);
		}
		return cell;
	}
	

}
