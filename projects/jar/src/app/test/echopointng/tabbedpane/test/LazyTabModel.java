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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Label;
import echopointng.TabbedPane;
import echopointng.tabbedpane.AbstractTabModel;

/**
 * <code>LazyTabModel</code> only creates its children as needed
 */

public class LazyTabModel extends AbstractTabModel {

	private Map tabMap = new HashMap();

	private Map contentMap = new HashMap();

	private int currentIndex = -1;

	public LazyTabModel() {
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#size()
	 */
	public int size() {
		return 10;
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#getTabAt(int, boolean)
	 */
	public Component getTabAt(TabbedPane tabbedPane, int index, boolean isSelected) {
		Component tabComponent = (Component) contentMap.get(new Integer(index));
		if (tabComponent == null) {
			tabComponent = new Label("Tab " + index);
			contentMap.put(new Integer(index), tabComponent);
		}
		if (isSelected) {
			tabComponent.setForeground(Color.ORANGE);
		} else {
			tabComponent.setForeground(Color.BLACK);
		}
		return tabComponent;
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#getTabContentAt(int)
	 */
	public Component getTabContentAt(int index) {
		if (currentIndex != -1 && currentIndex != index) {
			throw new IllegalStateException("getTabContentAt called without a corresponding releaseTabAt");
		}
		Component content = (Component) contentMap.get(new Integer(index));
		if (content == null) {
			content = new Label("Content for Tab" + index);
			contentMap.put(new Integer(index), content);
			currentIndex = index;
		}
		return content;
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#releaseTabAt(int)
	 */
	public void releaseTabAt(int index) {
		// we could release it here
		currentIndex = -1;
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#indexOfTab(nextapp.echo2.app.Component)
	 */
	public int indexOfTab(Component tabComponent) {
		return getIndexOfComponent(tabMap, tabComponent);
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#indexOfTabContent(nextapp.echo2.app.Component)
	 */
	public int indexOfTabContent(Component tabContent) {
		return getIndexOfComponent(contentMap, tabContent);
	}

	private int getIndexOfComponent(Map map, Component component) {
		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
			Integer index = (Integer) iter.next();
			Component componentTest = (Component) tabMap.get(index);
			if (component == componentTest) {
				return index.intValue();
			}
		}
		return -1;
	}
}
