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

import nextapp.echo2.app.CheckBox;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import echopointng.ExtentEx;
import echopointng.LabelEx;
import echopointng.Slider;

/**
 * <code>TestSlider</code>
 */

public class TestSlider extends TestCaseBaseNG {

	public String getTestCategory() {
		return "Slider";
	}

	public Component testSlider() {
		final Column col = new Column();
		col.setInsets(new Insets(30));
		
		final LabelEx valuesLabel = new LabelEx("What are the slider values? Press the Server Interaction button to find out!");

		final Slider sliderHorz = new Slider();
		
		final Slider sliderVert = new Slider(Slider.DEFAULT_VERTICAL_STYLE);

		col.setCellSpacing(new ExtentEx(25));
		
		ChangeListener changeListener = new ChangeListener() {
			/**
			 * @see nextapp.echo2.app.event.ChangeListener#stateChanged(nextapp.echo2.app.event.ChangeEvent)
			 */
			public void stateChanged(ChangeEvent e) {
				String text = "horz slider:" + sliderHorz.getDoubleValue() + " " + 
							  "vert slider:" + sliderVert.getDoubleValue();
				valuesLabel.setText(text);

			}
		};
		sliderHorz.addChangeListener(changeListener);
		sliderVert.addChangeListener(changeListener);
		
		final CheckBox checkBoxIN = new CheckBox("Immediate Notification");
		checkBoxIN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean newValue = checkBoxIN.isSelected();
				sliderHorz.setImmediateNotification(newValue);
				sliderVert.setImmediateNotification(newValue);
			}
		});
		
		
		col.add(sliderHorz);
		col.add(sliderVert);
		col.add(valuesLabel);
		col.add(checkBoxIN);
		return col;
	}

	public Component testDefault() {
		return new Slider();
	}
	
}
