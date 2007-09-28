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
package echopointng.tabbedpane.test;

import junit.framework.TestCase;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Label;
import echopointng.TabbedPane;
import echopointng.tabbedpane.DefaultTabModel;
import echopointng.util.RandKit;

/** 
 * <code>TabbedPaneTest</code> 
 */

public class TabbedPaneTest extends TestCase {
	
	public void testTabModels() {
		
		TabbedPane tabbedPane;
		
		LazyTabModel lazyTabModel = new LazyTabModel();
		tabbedPane = new TabbedPane(lazyTabModel);
		
		Component content = null;
		Component tab = null;

		int tabCount = tabbedPane.getModel().size();
		for (int i = 0; i < 20; i++) {
			int index = RandKit.rand(0,tabCount-1);
			tabbedPane.getSelectionModel().setSelectedIndex(index);
			
			content = tabbedPane.getModel().getTabContentAt(index);
			assertNotNull(content);
			assertEquals(tabbedPane.getSelectionModel().getSelectedIndex(),index);

			tab = tabbedPane.getModel().getTabAt(tabbedPane,index,true);
			assertNotNull(tab);
			assertEquals(tab.getForeground(),Color.ORANGE);

			tabbedPane.validate();
			
			content = tabbedPane.getModel().getTabContentAt(index);
			assertNotNull(content);
			assertTrue(tabbedPane.isAncestorOf(content));
			assertEquals(tabbedPane.getSelectionModel().getSelectedIndex(),index);
		}
		
		lazyTabModel.fireStateChanged();
		assertTrue(tabbedPane.isAncestorOf(content));
	}
	
	public void testDefaultTabModel() {
		
		DefaultTabModel model = new DefaultTabModel();
		
		model.addTab("Tab 1",null,new Label("Content 1"));
		assertTrue(model.size() == 1);
		
		
		model.addTab("Tab 2",null,new Label("Content 2"));
		assertTrue(model.size() == 2);
		
		model.releaseTabAt(0);
		assertTrue(model.size() == 2);
		
		model.removeTabAt(0);
		assertTrue(model.size() == 1);
		
	}

}
