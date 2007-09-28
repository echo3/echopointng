package echopointng.tabbedpane;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Style;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.EPNG;
import echopointng.MutableStyleEx;
import echopointng.TabbedPane;
import echopointng.able.Insetable;
import echopointng.util.ColorKit;

/**
 * <code>DefaultTabModel</code> holds all its content components in memory for
 * each possible tab. (and hence does no lazy loading of tab content).
 * <p>
 * It also does not release tab information unless instructed to do so by the
 * <code>removeTabAt()</code> method.
 * <p>
 * You can tweak the components used by this TabModel by overrriding the
 * <code>createTabComponent()</code> method and the visual appearance of these 
 * tab components by overriding the <code>paintTabComponent()</code> method.
 */

public class DefaultTabModel extends AbstractTabModel implements Serializable {

	private static final Color XP_ORANGE = ColorKit.makeColor("#FFC73C");

	private static final Color XP_BLUEISH_WHITE = ColorKit.makeColor("#FAFAF9");

	private static final Color DEFAULT_SELECTED_BACKGROUND = Color.WHITE;

	private static final Color DEFAULT_SELECTED_FOREGROUND = Color.BLACK;

	private static final Color DEFAULT_SELECTED_ROLLOVER_BACKGROUND = Color.WHITE;

	public static final Color DEFAULT_SELECTED_ROLLOVER_FOREGROUND = Color.BLACK;

	private static final Color DEFAULT_BACKGROUND = XP_BLUEISH_WHITE;

	private static final Color DEFAULT_FOREGROUND = Color.BLACK;

	private static final Color DEFAULT_ROLLOVER_BACKGROUND = XP_BLUEISH_WHITE;

	private static final Color DEFAULT_ROLLOVER_FOREGROUND = Color.BLACK;

	/**
	 * This <code>Style</code> is used by the tab
	 * components to style themselves. This one is used if the
	 * <code>TabbedPane</code> has a tabPlacement of
	 * <code>Alignment.TOP</code> and the tab is not selected.
	 */
	public static Style DEFAULT_TOP_ALIGNED_STYLE;

	/**
	 * This <code>Style</code> is used by the tab
	 * components to style themselves. This one is used if the
	 * <code>TabbedPane</code> has a tabPlacement of
	 * <code>Alignment.TOP</code> and the tab is selected.
	 */
	public static Style DEFAULT_TOP_ALIGNED_SELECTED_STYLE;

	/**
	 * This <code>Style</code> is used by the tab
	 * components to style themselves. This one is used if the
	 * <code>TabbedPane</code> has a tabPlacement of
	 * <code>Alignment.BOTTOM</code> and the tab is not selected.
	 */
	public static Style DEFAULT_BOTTOM_ALIGNED_STYLE;

	/**
	 * This <code>Style</code> is used by the tab
	 * components to style themselves. This one is used if the
	 * <code>TabbedPane</code> has a tabPlacement of
	 * <code>Alignment.TOP</code> and the tab is selected.
	 */
	public static Style DEFAULT_BOTTOM_ALIGNED_SELECTED_STYLE;
	static {
		MutableStyleEx baseStyle = new MutableStyleEx();
		baseStyle.setProperty(Insetable.PROPERTY_INSETS, new Insets(3));
		baseStyle.setProperty(Insetable.PROPERTY_OUTSETS, new Insets(0));

		MutableStyleEx style;
		//
		// top aligned
		style = new MutableStyleEx(baseStyle);
		style.setProperty(Button.PROPERTY_BACKGROUND, DEFAULT_BACKGROUND);
		style.setProperty(Button.PROPERTY_FOREGROUND, DEFAULT_FOREGROUND);
		style.setProperty(Button.PROPERTY_BORDER, new BorderEx(new Extent(1), DEFAULT_BACKGROUND, Border.STYLE_SOLID, new Extent(1),
				DEFAULT_BACKGROUND, Border.STYLE_SOLID, new Extent(2), DEFAULT_BACKGROUND, Border.STYLE_SOLID, new Extent(1), DEFAULT_BACKGROUND,
				Border.STYLE_SOLID));

		style.setProperty(Button.PROPERTY_ROLLOVER_BACKGROUND, DEFAULT_ROLLOVER_BACKGROUND);
		style.setProperty(Button.PROPERTY_ROLLOVER_FOREGROUND, DEFAULT_ROLLOVER_FOREGROUND);
		style.setProperty(Button.PROPERTY_ROLLOVER_BORDER, new BorderEx(new Extent(1), DEFAULT_ROLLOVER_BACKGROUND, Border.STYLE_SOLID,
				new Extent(1), DEFAULT_ROLLOVER_BACKGROUND, Border.STYLE_SOLID, new Extent(2), XP_ORANGE, Border.STYLE_SOLID, new Extent(1),
				DEFAULT_ROLLOVER_BACKGROUND, Border.STYLE_SOLID));

		DEFAULT_TOP_ALIGNED_STYLE = style;
		//
		// top aligned SELECTED
		style = new MutableStyleEx(baseStyle);
		style.setProperty(Button.PROPERTY_BACKGROUND, DEFAULT_SELECTED_BACKGROUND);
		style.setProperty(Button.PROPERTY_FOREGROUND, DEFAULT_SELECTED_FOREGROUND);
		style.setProperty(Button.PROPERTY_BORDER, new BorderEx(new Extent(1), DEFAULT_SELECTED_BACKGROUND, Border.STYLE_SOLID, new Extent(1),
				DEFAULT_SELECTED_BACKGROUND, Border.STYLE_SOLID, new Extent(2), XP_ORANGE, Border.STYLE_SOLID, new Extent(1),
				DEFAULT_SELECTED_BACKGROUND, Border.STYLE_SOLID));

		style.setProperty(Button.PROPERTY_ROLLOVER_BACKGROUND, DEFAULT_SELECTED_ROLLOVER_BACKGROUND);
		style.setProperty(Button.PROPERTY_ROLLOVER_FOREGROUND, DEFAULT_SELECTED_ROLLOVER_FOREGROUND);
		style.setProperty(Button.PROPERTY_ROLLOVER_BORDER, new BorderEx(new Extent(1), DEFAULT_SELECTED_ROLLOVER_BACKGROUND, Border.STYLE_SOLID,
				new Extent(1), DEFAULT_SELECTED_ROLLOVER_BACKGROUND, Border.STYLE_SOLID, new Extent(2), XP_ORANGE, Border.STYLE_SOLID, new Extent(1),
				DEFAULT_SELECTED_ROLLOVER_BACKGROUND, Border.STYLE_SOLID));

		DEFAULT_TOP_ALIGNED_SELECTED_STYLE = style;
		//
		// bottom aligned
		style = new MutableStyleEx(baseStyle);
		style.setProperty(Button.PROPERTY_BACKGROUND, DEFAULT_BACKGROUND);
		style.setProperty(Button.PROPERTY_FOREGROUND, DEFAULT_FOREGROUND);
		style.setProperty(Button.PROPERTY_BORDER, new BorderEx(new Extent(1), DEFAULT_BACKGROUND, Border.STYLE_SOLID, new Extent(1),
				DEFAULT_BACKGROUND, Border.STYLE_SOLID, new Extent(1), DEFAULT_BACKGROUND, Border.STYLE_SOLID, new Extent(2), DEFAULT_BACKGROUND,
				Border.STYLE_SOLID));

		style.setProperty(Button.PROPERTY_ROLLOVER_BACKGROUND, DEFAULT_ROLLOVER_BACKGROUND);
		style.setProperty(Button.PROPERTY_ROLLOVER_FOREGROUND, DEFAULT_ROLLOVER_FOREGROUND);
		style.setProperty(Button.PROPERTY_ROLLOVER_BORDER, new BorderEx(new Extent(1), DEFAULT_BACKGROUND, Border.STYLE_SOLID, new Extent(1),
				DEFAULT_BACKGROUND, Border.STYLE_SOLID, new Extent(1), DEFAULT_BACKGROUND, Border.STYLE_SOLID, new Extent(2), XP_ORANGE,
				Border.STYLE_SOLID));

		DEFAULT_BOTTOM_ALIGNED_STYLE = style;
		//
		// bottom aligned SELECTED
		style = new MutableStyleEx(baseStyle);
		style.setProperty(Button.PROPERTY_BACKGROUND, DEFAULT_SELECTED_BACKGROUND);
		style.setProperty(Button.PROPERTY_FOREGROUND, DEFAULT_SELECTED_FOREGROUND);
		style.setProperty(Button.PROPERTY_BORDER, new BorderEx(new Extent(1), DEFAULT_SELECTED_BACKGROUND, Border.STYLE_SOLID, new Extent(1),
				DEFAULT_SELECTED_BACKGROUND, Border.STYLE_SOLID, new Extent(1), DEFAULT_SELECTED_BACKGROUND, Border.STYLE_SOLID, new Extent(2),
				XP_ORANGE, Border.STYLE_SOLID));

		style.setProperty(Button.PROPERTY_ROLLOVER_BACKGROUND, DEFAULT_SELECTED_ROLLOVER_BACKGROUND);
		style.setProperty(Button.PROPERTY_ROLLOVER_FOREGROUND, DEFAULT_SELECTED_ROLLOVER_FOREGROUND);
		style.setProperty(Button.PROPERTY_ROLLOVER_BORDER, new BorderEx(new Extent(1), DEFAULT_SELECTED_ROLLOVER_BACKGROUND, Border.STYLE_SOLID,
				new Extent(1), DEFAULT_SELECTED_ROLLOVER_BACKGROUND, Border.STYLE_SOLID, new Extent(1), DEFAULT_SELECTED_ROLLOVER_BACKGROUND,
				Border.STYLE_SOLID, new Extent(2), XP_ORANGE, Border.STYLE_SOLID));
		DEFAULT_BOTTOM_ALIGNED_SELECTED_STYLE = style;
	}

	/**
	 * <code>TabEntry</code> contains out tab data at given index
	 */
	protected class TabEntry implements Serializable {
		protected Component tabComponent = null;
		protected Component tabContent = null;

		public TabEntry(Component tabComponent, Component tabContent) {
			this.tabComponent = tabComponent;
			this.tabContent = tabContent;
		}

		/**
		 * @return Returns the tabComponent.
		 */
		public Component getTabComponent() {
			return tabComponent;
		}

		/**
		 * @return Returns the tabContent.
		 */
		public Component getTabContent() {
			return tabContent;
		}
	}

	private List tabEntryList;


	/**
	 * Constructs a <code>DefaultTabModel</code>
	 */
	public DefaultTabModel() {
		this.tabEntryList = createTabEntryList();
	};

	/**
	 * This allows subclasses to create the List used to contain TabEntry
	 * objects
	 * 
	 * @return the List used to contain TabEntry objects
	 */
	protected List createTabEntryList() {
		return new ArrayList();
	}

	/**
	 * This allows subclass to get to the list that contains the TabEntry
	 * objects
	 * 
	 * @return - the list that contains the TabEntry objects
	 */
	protected List getTabEntryList() {
		return tabEntryList;
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#size()
	 */
	public int size() {
		return getTabEntryList().size();
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#getTabAt(TabbedPane, int, boolean)
	 */
	public Component getTabAt(TabbedPane tabbedPane, int index, boolean isSelected) {
		List tabList = getTabEntryList();
		if (index < 0 || index > tabList.size())
			return null;
		TabEntry entry = (TabEntry) tabList.get(index);
		Component tabComponent = entry.getTabComponent();
		if (tabComponent != null) {
			int tabPlacement = EPNG.getRP(tabbedPane, TabbedPane.PROPERTY_TAB_PLACEMENT, tabbedPane.getTabPlacement());
			paintTabComponent(tabbedPane,tabComponent,isSelected,tabPlacement);
		}
		return tabComponent;
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#getTabContentAt(int)
	 */
	public Component getTabContentAt(int index) {
		List tabList = getTabEntryList();
		if (index < 0 || index > tabList.size())
			return null;
		TabEntry entry = (TabEntry) tabList.get(index);
		return entry.getTabContent();
	}

	/**
	 * Does nothing as all tabs are kept in memory once added to this
	 * <code>TabModel</code> implementation.
	 * 
	 * @see echopointng.tabbedpane.TabModel#releaseTabAt(int)
	 */
	public void releaseTabAt(int index) {
		// we do nothing
	}

	/**
	 * This method is called to create a Tab component with the specified text
	 * and icon. The default behaviour creates ButtonEx instances. Subclasses
	 * can overrride this method to modify what components are returned.
	 * 
	 * @param tabTitle -
	 *            the title of the tab
	 * @param tabIcon -
	 *            the icon for the tab
	 * @return a component that will be used as the Tab. This will most likely
	 *         be a Button.
	 */
	protected Component createTabComponent(String tabTitle, ImageReference tabIcon) {
		ButtonEx tabComponent = new ButtonEx(tabTitle, tabIcon);
		tabComponent.setStyle(DEFAULT_TOP_ALIGNED_STYLE);
		return tabComponent;
	}

	/**
	 * Called to insert a new tab at the specified index. It will grow the
	 * collection of tabs to fit the new tab.
	 * 
	 * @param index -
	 *            the index to insert at, growing the collection as required
	 * 
	 * @param tabComponent -
	 *            the component to use as the tab for the given
	 *            <code>index</code>
	 * 
	 * @param tabContent -
	 *            the content to use for the given <code>index</code>
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range (index &lt; 0 || index &gt;
	 *             size()).
	 */
	public void insertTab(int index, Component tabComponent, Component tabContent) {
		TabEntry entry = new TabEntry(tabComponent, tabContent);
		getTabEntryList().add(index, entry);
		fireStateChanged();
	}

	/**
	 * Called to insert a new tab at the specified index. It will grow the
	 * collection of tabs to fit the new tab.
	 * <p>
	 * This will use the default colors and fonts defined by this
	 * <code>TabModel</code>
	 * 
	 * @param index -
	 *            the index to insert at, growing the collection as required
	 * 
	 * @param tabTitle -
	 *            the title of the tab
	 * 
	 * @param tabIcon -
	 *            the icon to use on the tab
	 * 
	 * @param tabContent -
	 *            the content to use for the given <code>index</code>
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range (index &lt; 0 || index &gt;
	 *             size()).
	 */
	public void insertTab(int index, String tabTitle, ImageReference tabIcon, Component tabContent) {
		Component tabComponent = createTabComponent(tabTitle, tabIcon);
		insertTab(index, tabComponent, tabContent);
	}

	/**
	 * Adds a new tab to the end of the collection of tabs.
	 * <p>
	 * This will use the default colors and fonts defined by this
	 * <code>TabModel</code> if no color and font properties are set on the
	 * LabelEx at the time it was passed to the model.
	 * 
	 * @param tabComponent -
	 *            the component to use as the tab for the given
	 *            <code>index</code>
	 * 
	 * @param tabContent -
	 *            the component to use as the tab content.
	 */
	public void addTab(Component tabComponent, Component tabContent) {
		insertTab(getTabEntryList().size(), tabComponent, tabContent);
	}

	/**
	 * Adds a new tab with the specified title. This will use the default colors
	 * and fonts defined by this <code>TabModel</code>
	 * 
	 * @param tabTitle -
	 *            the title of the new tab
	 * @param tabContent -
	 *            the component to use as the tab content.
	 */
	public void addTab(String tabTitle, Component tabContent) {
		addTab(tabTitle, null, tabContent);
	}

	/**
	 * Adds a new tab with the specified icon. This will use the default colors
	 * and fonts defined by this <code>TabModel</code>
	 * 
	 * @param icon -
	 *            the icon of rhe new tab
	 * @param tabContent -
	 *            the component to use as the tab content.
	 */
	public void addTab(ImageReference icon, Component tabContent) {
		addTab(null, icon, tabContent);
	}

	/**
	 * Adds a new tab with the specified title and icon. This will use the
	 * default colors and fonts defined by this <code>TabModel</code>
	 * 
	 * @param tabTitle -
	 *            the title of the new tab
	 * @param icon -
	 *            the icon of rhe new tab
	 * @param tabContent -
	 *            the component to use as the tab content.
	 */
	public void addTab(String tabTitle, ImageReference icon, Component tabContent) {
		insertTab(getTabEntryList().size(), tabTitle, icon, tabContent);
	}

	/**
	 * Removes the tab details at the specified index
	 * 
	 * @param index -
	 *            the index of the tab details
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range (index &lt; 0 || index &gt;=
	 *             size()).
	 */
	public void removeTabAt(int index) {
		getTabEntryList().remove(index);
		fireStateChanged();
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#indexOfTab(nextapp.echo2.app.Component)
	 */
	public int indexOfTab(Component tabComponent) {
		List tabList = getTabEntryList();
		for (int i = 0; i < tabList.size(); i++) {
			TabEntry entry = (TabEntry) tabList.get(i);
			if (entry.getTabComponent() == tabComponent) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#indexOfTabContent(nextapp.echo2.app.Component)
	 */
	public int indexOfTabContent(Component tabContent) {
		List tabList = getTabEntryList();
		for (int i = 0; i < tabList.size(); i++) {
			TabEntry entry = (TabEntry) tabList.get(i);
			if (entry.getTabContent() == tabContent) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * This method is called to "paint" the so it reflects the current
	 * TabbedPane selection and tab placemement. This default implementation
	 * will use the default styles of this class and call setStyle() on the
	 * <code>tabComponent</code>.
	 * 
	 * @param tabbedPane -
	 *            the TabbedPane that the component belongs to
	 * @param tabComponent -
	 *            the current tab component
	 * @param isSelected -
	 *            whether the tabComponent is the selected tab.
	 * @param tabPlacement -
	 *            whether the tabs are at the top or bottom of thje TabbedPane.
	 */
	protected void paintTabComponent(TabbedPane tabbedPane, Component tabComponent, boolean isSelected, int tabPlacement) {
		if (isSelected) {
			if (tabPlacement == Alignment.TOP) {
				tabComponent.setStyle(DEFAULT_TOP_ALIGNED_SELECTED_STYLE);
			} else {
				tabComponent.setStyle(DEFAULT_BOTTOM_ALIGNED_SELECTED_STYLE);
			}
		} else {
			if (tabPlacement == Alignment.TOP) {
				tabComponent.setStyle(DEFAULT_TOP_ALIGNED_STYLE);
			} else {
				tabComponent.setStyle(DEFAULT_BOTTOM_ALIGNED_STYLE);
			}
		}
	}
}
