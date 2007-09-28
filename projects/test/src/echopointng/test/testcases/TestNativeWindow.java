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

import nextapp.echo2.app.Button;
import nextapp.echo2.app.CheckBox;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import echopointng.ButtonEx;
import echopointng.ExtentEx;
import echopointng.NativeWindow;
import echopointng.util.RandKit;

/**
 * <code>TestNativeWindow</code>
 */
public class TestNativeWindow extends TestCaseBaseNG {

	public String getTestCategory() {
		return "NativeWindow";
	}

	public Component testNativeWindow() {
		Column cell = new Column();

		final NativeWindow nativeWindow = new NativeWindow("http://www.google.com");

		Grid controllerGrid = new Grid(3);

		Button button;

		button = new ButtonEx("Open NativeWindow");
		controllerGrid.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nativeWindow.setOpen(true);
			}
		});

		button = new ButtonEx("Close NativeWindow");
		controllerGrid.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nativeWindow.setOpen(false);
			}
		});

		button = new ButtonEx("Tweak URL");
		controllerGrid.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String urls[] = { "http://www.nextapp.com", "http://www.google.com", "http://wiki.nextapp.com", "http://www.theserverside.com", };
				nativeWindow.setURL(RandKit.roll(urls));
			}
		});

		button = new ButtonEx("moveTo 100,100");
		controllerGrid.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nativeWindow.setLeft(new ExtentEx("100px"));
				nativeWindow.setTop(new ExtentEx("100px"));
			}
		});
		button = new ButtonEx("resizeTo 300,300");
		controllerGrid.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nativeWindow.setWidth(new ExtentEx("300px"));
				nativeWindow.setHeight(new ExtentEx("300px"));
			}
		});
		
		button = new ButtonEx("Toggle Modal");
		controllerGrid.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nativeWindow.setModal(! nativeWindow.isModal());
			}
		});		
		
		final String featureNames[] = new String[] { 
				"FEATURE_LOCATION", "FEATURE_MENUBAR",
				"FEATURE_RESIZEABLE", "FEATURE_SCROLLBARS",
				"FEATURE_STATUSBAR", "FEATURE_TOOLBAR",
		};
		
		final int featureValues[] = new int[] {
				NativeWindow.FEATURE_LOCATION, NativeWindow.FEATURE_MENUBAR,
				NativeWindow.FEATURE_RESIZEABLE, NativeWindow.FEATURE_SCROLLBARS,
				NativeWindow.FEATURE_STATUSBAR, NativeWindow.FEATURE_TOOLBAR,
		};
		ActionListener featureListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = Integer.parseInt(e.getActionCommand());
				int newValue = featureValues[index];
				int currentValue = nativeWindow.getFeatures();
				if ((currentValue & newValue) == newValue) {
					currentValue &= ~newValue;
				} else {
					currentValue |= newValue;
				}
				nativeWindow.setFeatures(currentValue);
			};
		};

		Grid featureGrid = new Grid(2);
		for (int i = 0; i < featureValues.length; i++) {
			CheckBox checkBox = new CheckBox("Toggle " + featureNames[i]);
			checkBox.setActionCommand(String.valueOf(i));
			checkBox.addActionListener(featureListener);
			featureGrid.add(checkBox);
		}
		
		cell.add(nativeWindow);
		cell.add(controllerGrid);
		cell.add(featureGrid);

		return cell;
	}

}
