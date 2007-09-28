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

import echopointng.ButtonEx;
import echopointng.ExtentEx;
import echopointng.ProgressBar;
import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.TaskQueueHandle;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;

/** 
 * <code>TestProgressBar</code> 
 */

public class TestProgressBar extends TestCaseBaseNG {
	
	/**
	 * @see echopointng.test.testcases.TestCaseBaseNG#getTestCategory()
	 */
	public String getTestCategory() {
		return "ProgressBar";
	}

	public Component testProgressBar() {
		
		ButtonEx buttonEx;
		Column cell = new Column();
		cell.setCellSpacing(new Extent(10));
		
		final ProgressBar pb1 = new ProgressBar();
		final ProgressBar pb2 = new ProgressBar();
		
		pb2.setOrientation(ProgressBar.ORIENTATION_VERTICAL);
		pb2.setHeight(new ExtentEx(146));
		pb2.setWidth(new ExtentEx(30));
		
		cell.add(pb1);
		cell.add(pb2);
		
		buttonEx = new ButtonEx("Increase");
		cell.add(buttonEx);
		buttonEx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pb1.setValue(pb1.getValue()+10);
				pb2.setValue(pb2.getValue()+10);
			}
		}) ;

		buttonEx = new ButtonEx("Decrease");
		cell.add(buttonEx);
		buttonEx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pb1.setValue(pb1.getValue()-10);
				pb2.setValue(pb2.getValue()-10);
			}
		}) ;
		
		return cell;
	}
	
	public Component testDefaultProgressBar() {
		return new ProgressBar();
	}

	public Component testAsynchProgressBar() {
		final ApplicationInstance appInstance  = ApplicationInstance.getActive();
		final TaskQueueHandle handle = appInstance.createTaskQueue();
		final ProgressBar bar = new ProgressBar();

		Runnable task = new Runnable() {
			boolean forwards = true; 
			public void run() {
				if (bar.getValue() >= bar.getMaximum()) {
					forwards = false;
				}
				if (bar.getValue() <= bar.getMinimum()) {
					forwards = true;
				}
				if (forwards) {
					bar.setValue(bar.getValue()+10);
				} else {
					bar.setValue(bar.getValue()-10);
				}
				
				// reschedule ourselves
				appInstance.enqueueTask(handle, this);
				
			}
		};
		
		appInstance.enqueueTask(handle, task);
		
		return bar;
	}
	
}
