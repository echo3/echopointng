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
package echopointng;

import java.io.Serializable;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.PaneContainer;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.button.AbstractButton;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import echopointng.able.ScrollBarProperties;
import echopointng.able.Scrollable;
import echopointng.able.Stretchable;
import echopointng.model.DefaultSingleSelectionModel;
import echopointng.model.SingleSelectionModel;
import echopointng.tabbedpane.DefaultTabModel;
import echopointng.tabbedpane.TabModel;
import echopointng.util.ComponentTracker;
import echopointng.util.reflect.ReflectionKit;

/**
 * <code>TabbedPane</code> is used to provide a tabbed interface to a
 * collection of <code>Component</code>s.
 * <p>
 * A <code>TabModel</code> is used to provide the collections of tabs and the
 * content that should be shown when a given tab is selected.
 * <p>
 * A <code>SingleSelectionModel</code> is used to track which tab is currently
 * selected.
 * <p>
 * This component is a <code>PaneContainer</code> and hence can have
 * components that implement <code>Pane</code> as a child. However many
 * <code>Pane</code>s, such as <code>SplitPane</code>, require a definite
 * height to be set in order to work properly. So make sure you call setHeight()
 * if one of the children implements <code>Pane</code>
 * 
 */
public class TabbedPane extends AbleComponent implements PaneContainer, Stretchable, Scrollable {

	/**
	 * <code>TabActionListener</code> listens to any Tab components for action
	 * events and then sets the tabs selection based on whcih one raised the
	 * event.
	 */
	private class TabActionListener implements ActionListener, Serializable {
		/**
		 * @see nextapp.echo2.app.event.ActionListener#actionPerformed(nextapp.echo2.app.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof Component) {
				Component tabComponent = (Component) e.getSource();
				int index = getModel().indexOfTab(tabComponent);
				if (index != -1) {
					getSelectionModel().setSelectedIndex(index);
				}
				firePropertyChange(null, null, null);
			}
		}
	}

	/**
	 * <code>TabModelListener</code> is used to track changes in the TabModel
	 * and reflect these back in the
	 */
	private class TabModelListener implements ChangeListener, Serializable {
		/**
		 * @see nextapp.echo2.app.event.ChangeListener#stateChanged(nextapp.echo2.app.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			TabModel model = getModel();
			SingleSelectionModel selectionModel = getSelectionModel();

			int selectedIndex = getSelectionModel().getSelectedIndex();
			//
			// remove all children in the TabbedPane cause things have changed
			// in the underlying model
			removeAllTabContent();
			removeAllTabComponents();

			int newTabCount = model.size();
			if (newTabCount <= 0) {
				// we have no atbs and hence no selection
				selectionModel.setSelectedIndex(-1);
			} else {
				if (selectedIndex >= newTabCount) {
					// we have some tabs but the selection could still be after
					// its new length
					selectedIndex = newTabCount - 1;
				} else if (selectedIndex < 0) {
					// or not selected at all
					selectedIndex = 0;
				}
				selectionModel.setSelectedIndex(selectedIndex);
				//
				// now find the component at this index and add it
				// if it not already a child
				Component tabContent = model.getTabContentAt(selectedIndex);
				if (!isAncestorOf(tabContent)) {
					contentTracker.add(tabContent);
				}
				firePropertyChange(null, null, null);
			}
		}
	}

	/**
	 * <code>TabSelectionListener</code> is used to track component selection
	 * and hence tell the model when old content can be released and add new
	 * content into the TabbedPane.
	 */
	private class TabSelectionListener implements ChangeListener, Serializable {
		/**
		 * @see nextapp.echo2.app.event.ChangeListener#stateChanged(nextapp.echo2.app.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			SingleSelectionModel srcSelectionModel = (SingleSelectionModel) e.getSource();
			TabModel model = getModel();

			int newSelectedIndex = srcSelectionModel.getSelectedIndex();
			//
			// remove old content
			removeAllTabContent();
			if (currentlySelectedIndex >= 0) {
				model.releaseTabAt(currentlySelectedIndex);
			}

			//
			// add the new content
			if (newSelectedIndex >= 0) {
				Component newContent = model.getTabContentAt(newSelectedIndex);
				if (newContent != null) {
					contentTracker.add(newContent);
				}
			}
			currentlySelectedIndex = newSelectedIndex;
			firePropertyChange(null, null, null);
		}
	}

	// ---------------------------------------------------------------
	// ---------------------------------------------------------------

	public static final Style DEFAULT_STYLE;

	public static final String PROPERTY_MODEL = "model";

	public static final String PROPERTY_SELECTION_MODEL = "selectionModel";

	public static final String PROPERTY_TAB_BORDER_STYLE = "tabBorderStyle";

	public static final String PROPERTY_TAB_LEAD_IN_WIDTH = "tabLeadInWidth";

	public static final String PROPERTY_TAB_PLACEMENT = "tabPlacement";

	public static final String PROPERTY_TAB_SPACING = "tabSpacing";

	/**
	 * TAB_LINE_AND_CONTENT - shows a border around the tabs and a line betweent
	 * the tabs and the content
	 * <p>
	 * It looks a bit like this (where ==== is the border and --- is the
	 * non-border area
	 * 
	 * <pre>
	 *  -----------   -----------  -----------  -----------
	 *  -         -   -         -  -         -  -         -
	 *  -         -   -         -  -         -  -         -
	 *  ============================         ==========================
	 *  =                                                             =
	 *  =                                                             =
	 *  =                                                             =
	 *  =                                                             =
	 *  =                                                             =
	 *  =                                                             =
	 *  ===============================================================
	 * </pre>
	 */
	public static final int TAB_LINE_AND_CONTENT = 1;

	/**
	 * TAB_LINE_ONLY - shows no border around the tabs and a line betweent the
	 * tabs and the content
	 * <p>
	 * It looks a bit like this (where ==== is the border and --- is the
	 * non-border area
	 * 
	 * <pre>
	 *  -----------   -----------  -----------  -----------
	 *  -         -   -         -  -         -  -         -
	 *  -         -   -         -  -         -  -         -
	 *  ============================         ==========================
	 *  -                                                             -
	 *  -                                                             -
	 *  -                                                             -
	 *  -                                                             -
	 *  -                                                             -
	 *  -                                                             -
	 *  ---------------------------------------------------------------
	 * </pre>
	 */
	public static final int TAB_LINE_ONLY = 2;

	/**
	 * TAB_STRIP_AND_CONTENT - shows the border around the tabs and content
	 * <p>
	 * It looks a bit like this (where ==== is the border and --- is the
	 * non-border area
	 * 
	 * <pre>
	 *  ===========   ===========  ===========  ===========
	 *  =         =   =         =  =         =  =         =
	 *  =         =   =         =  =         =  =         =
	 *  ============================         ==========================
	 *  =                                                             =
	 *  =                                                             =
	 *  =                                                             =
	 *  =                                                             =
	 *  =                                                             =
	 *  =                                                             =
	 *  ===============================================================
	 * </pre>
	 */
	public static final int TAB_STRIP_AND_CONTENT = 3;

	/**
	 * TAB_STRIP_ONLY - shows a border around the tabs and a line betweent the
	 * tabs and the content
	 * <p>
	 * It looks a bit like this (where ==== is the border and --- is the
	 * non-border area
	 * 
	 * <pre>
	 *  ===========   ===========  ===========  ===========
	 *  =         =   =         =  =         =  =         =
	 *  =         =   =         =  =         =  =         =
	 *  ============================         ==========================
	 *  -                                                             -
	 *  -                                                             -
	 *  -                                                             -
	 *  -                                                             -
	 *  -                                                             -
	 *  -                                                             -
	 *  ---------------------------------------------------------------
	 * </pre>
	 * 
	 */
	public static final int TAB_STRIP_ONLY = 4;

	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(PROPERTY_TAB_PLACEMENT, Alignment.TOP);
		style.setProperty(PROPERTY_TAB_SPACING, 5);
		style.setProperty(PROPERTY_BORDER, new BorderEx(1));
		style.setProperty(PROPERTY_TAB_BORDER_STYLE, TAB_STRIP_AND_CONTENT);
		DEFAULT_STYLE = style;
	}

	/** used to contain the tab content components */
	private ComponentTracker contentTracker = new ComponentTracker(this);

	/** our currently selected index */
	private int currentlySelectedIndex = -1;

	private TabActionListener internalActionListener = new TabActionListener();

	private TabModelListener internalModelListener = new TabModelListener();

	private TabSelectionListener internalSelectionListener = new TabSelectionListener();

	/** used to contain the tab components */
	private ComponentTracker tabsTracker = new ComponentTracker(this);

	/**
	 * Constructs a <code>TabbedPane</code> with the a default empty
	 * <code>DefaultTabModel</code> as the TabbedPane's model.
	 * <p>
	 * You can retrieve this as follows to add content to the TabbedPane:
	 * 
	 * <pre>
	 * TabbedPane tabbedPane = new TabbedPane();
	 * DefaultTabModel model = (DefaultTabModel) tabbedPane.getModel();
	 * model.addTab(&quot;Tab 1&quot;, new ContentThingy());
	 * model.addTab(&quot;Tab 2&quot;, new ContentThingo());
	 * </pre>
	 * 
	 * 
	 */
	public TabbedPane() {
		this(new DefaultTabModel());
	}

	/**
	 * Constructs a <code>TabbedPane</code> with the specified
	 * <code>TabModel</code>.
	 * 
	 * @param tabModel -
	 *            the <code>TabModel</code> to use for this
	 *            <code>TabbedPane</code>
	 */
	public TabbedPane(TabModel tabModel) {
		setSelectionModel(new DefaultSingleSelectionModel());
		setModel(tabModel);
		// if we have some content
		// then we always have a selection
		if (tabModel.size() > 0) {
			getSelectionModel().setSelectedIndex(0);
		}
	}

	/**
	 * @return returns the <code>TabModel</code> used to provide the
	 *         TabbedPane's tabs and content.
	 */
	public TabModel getModel() {
		return (TabModel) getProperty(PROPERTY_MODEL);
	}

	/**
	 * @return the currently selected index in the TabbedPane's selection model
	 */
	public int getSelectedIndex() {
		return getSelectionModel().getSelectedIndex();
	}

	/**
	 * @return the <code>SingleSelectionModel</code> used to track the
	 *         selected index of the TabbedPane
	 */
	public SingleSelectionModel getSelectionModel() {
		return (SingleSelectionModel) getProperty(PROPERTY_SELECTION_MODEL);
	}

	/**
	 * This can be one of the following values :
	 * <ul>
	 * <li>TAB_STRIP_ONLY - shows a border around the tabs and a line betweent
	 * the tabs and the content</li>
	 * <li>TAB_STRIP_AND_CONTENT - shows the border around the tabs and content</li>
	 * <li>TAB_LINE_ONLY - shows no border around the tabs and a line betweent
	 * the tabs and the content</li>
	 * <li>TAB_LINE_AND_CONTENT - shows a border around the tabs and a line
	 * betweent the tabs and the content
	 * </ul>
	 * 
	 * @return - the current tab border style.
	 */
	public int getTabBorderStyle() {
		return getProperty(PROPERTY_TAB_BORDER_STYLE, TAB_STRIP_AND_CONTENT);
	}

	/**
	 * @return lead in region before the first tab placement
	 */
	public Extent getTabLeadInWidth() {
		return (Extent) getProperty(PROPERTY_TAB_LEAD_IN_WIDTH);
	}

	/**
	 * Returns where the tabs are placed on the TabbedPane. This can be one of
	 * the following values :
	 * <ul>
	 * <li><code>Alignment.TOP</code>- the bottom of the TabbedPane
	 * (default)</li>
	 * <li><code>Alignment.BOTTOM</code>- the bottom of the TabbedPane</li>
	 * </ul>
	 * 
	 * @return - the current placement of the tabs
	 */
	public int getTabPlacement() {
		return getProperty(PROPERTY_TAB_PLACEMENT, Alignment.TOP);
	}

	/**
	 * @return - the spacing (in pixels) between tabs, the default is 5
	 */
	public int getTabSpacing() {
		return getProperty(PROPERTY_TAB_SPACING, 5);
	}

	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		super.processInput(inputName, inputValue);
		if ("click".equals(inputName)) {
			int selectedIndex = Integer.parseInt((String) inputValue);
			setSelectedIndex(selectedIndex);
			firePropertyChange(null, null, null);
		}
	}

	/**
	 * Removes all tab children and takes away the ActionListener if in place
	 */
	private void removeAllTabComponents() {
		Component[] children = tabsTracker.getComponents();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof AbstractButton) {
				((AbstractButton) children[i]).removeActionListener(internalActionListener);
			}
		}
		tabsTracker.removeAll();
	}

	private void removeAllTabContent() {
		contentTracker.removeAll();
	}

	/**
	 * Sets the <code>TabModel</code> used to provide the TabbedPane's tabs
	 * and content.
	 * 
	 * @param model -
	 *            the <code>TabModel</code> used to provide the TabbedPane's
	 *            tabs and content.
	 */
	public void setModel(TabModel model) {
		if (model == null)
			throw new IllegalArgumentException("The TabbedPane model must not be null!");

		TabModel oldValue = getModel();
		if (oldValue != model) {
			if (oldValue != null)
				oldValue.removeChangeListener(this.internalModelListener);
		}
		model.addChangeListener(this.internalModelListener);

		setProperty(PROPERTY_MODEL, model);
	}

	/**
	 * Sets the selected index of the TabbedPane.
	 * <p>
	 * Shortcurt method to getSelectionModel().setSelectedIndex(index).
	 * 
	 * @param index -
	 *            the index to select
	 */
	public void setSelectedIndex(int index) {
		getSelectionModel().setSelectedIndex(index);
	}

	/**
	 * Sets the <code>SingleSelectionModel</code> used to track the selected
	 * index of the TabbedPane
	 * 
	 * @param selectionModel -
	 *            the <code>SingleSelectionModel</code> used to track the
	 *            selected index of the TabbedPane
	 */
	public void setSelectionModel(SingleSelectionModel selectionModel) {
		if (selectionModel == null)
			throw new IllegalArgumentException("The TabbedPane selectionModel must not be null!");

		SingleSelectionModel oldValue = getSelectionModel();
		if (oldValue != selectionModel) {
			if (oldValue != null)
				oldValue.removeChangeListener(this.internalSelectionListener);
		}
		selectionModel.addChangeListener(this.internalSelectionListener);

		setProperty(PROPERTY_SELECTION_MODEL, selectionModel);
	}

	/**
	 * Sets the tab border style to use. This controls how the border is drawn
	 * around the tabs and content.
	 * 
	 * This can be one of the following values :
	 * <ul>
	 * <li>TAB_STRIP_ONLY - shows a border around the tabs and a line betweent
	 * the tabs and the content</li>
	 * <li>TAB_STRIP_AND_CONTENT - shows the border around the tabs and content</li>
	 * <li>TAB_LINE_ONLY - shows no border around the tabs and a line betweent
	 * the tabs and the content</li>
	 * <li>TAB_LINE_AND_CONTENT - shows a border around the tabs and a line
	 * betweent the tabs and the content
	 * </ul>
	 * 
	 * @see TabbedPane#TAB_STRIP_ONLY
	 * @see TabbedPane#TAB_STRIP_AND_CONTENT
	 * @see TabbedPane#TAB_LINE_ONLY
	 * @see TabbedPane#TAB_LINE_AND_CONTENT
	 * 
	 * @param newValue
	 */
	public void setTabBorderStyle(int newValue) {
		switch (newValue) {
		case TAB_LINE_AND_CONTENT:
		case TAB_LINE_ONLY:
		case TAB_STRIP_AND_CONTENT:
		case TAB_STRIP_ONLY:
			break;
		default:
			throw new IllegalArgumentException(
					"tabBorderStyle must one of TAB_LINE_AND_CONTENT, TAB_LINE_ONLY, TAB_STRIP_AND_CONTENT, TAB_STRIP_ONLY");
		}
		setProperty(PROPERTY_TAB_BORDER_STYLE, newValue);
	}

	/**
	 * Sets the width of a lead in region before the first tab placement
	 * 
	 * @param newValue -
	 *            lead in region before the first tab placement
	 */
	public void setTabLeadInWidth(Extent newValue) {
		setProperty(PROPERTY_TAB_LEAD_IN_WIDTH, newValue);
	}

	/**
	 * Sets where the tabs are placed on the TabbedPane. This can be one of the
	 * following values :
	 * <ul>
	 * <li><code>Alignment.TOP</code>- the bottom of the TabbedPane
	 * (default)</li>
	 * <li><code>Alignment.BOTTOM</code>- the bottom of the TabbedPane</li>
	 * </ul>
	 * 
	 * @param tabPlacement
	 */
	public void setTabPlacement(int tabPlacement) {
		switch (tabPlacement) {
		case Alignment.TOP:
		case Alignment.BOTTOM:
			break;
		default:
			throw new IllegalArgumentException("The TabbedPane tabPlacement value is invalid : " + tabPlacement);
		}
		setProperty(PROPERTY_TAB_PLACEMENT, tabPlacement);
	}

	/**
	 * Sets the spacing (in pixels) between tabs
	 * 
	 * @param tabSpacing -
	 *            the spacing (in pixels) between tabs
	 */
	public void setTabSpacing(int tabSpacing) {
		setProperty(PROPERTY_TAB_SPACING, tabSpacing);
	}

	/**
	 * @return the size on the TabbedPane's <code>TabModel</code>
	 */
	public int size() {
		return getModel().size();
	}

	/**
	 * @see nextapp.echo2.app.Component#validate()
	 */
	public void validate() {
		super.validate();

		TabModel model = getModel();

		int selectedIndex = getSelectionModel().getSelectedIndex();
		int tabCount = model.size();
		for (int i = 0; i < tabCount; i++) {
			Component tabComponent = model.getTabAt(this,i, i == selectedIndex);
			if (tabComponent == null) {
				throw new IllegalStateException("There must be a non null tab at index : " + i);
			}
			//
			// ok they must be a child of ours
			if (tabComponent != null && !this.isAncestorOf(tabComponent)) {
				tabsTracker.add(tabComponent);
			}
			// are they an actionEvent producer?
			ReflectionKit.invokeIfPresent("removeActionListener", new Class[] { ActionListener.class }, null, tabComponent,
					new Object[] { internalActionListener });
			ReflectionKit.invokeIfPresent("addActionListener", new Class[] { ActionListener.class }, null, tabComponent,
					new Object[] { internalActionListener });

			if (tabComponent instanceof AbstractButton) {
				AbstractButton btn = (AbstractButton) tabComponent;
				btn.removeActionListener(internalActionListener);
				btn.addActionListener(internalActionListener);
			}
		}
	}

	/**
	 * @see echopointng.able.Stretchable#getMaximumStretchedHeight()
	 */
	public Extent getMaximumStretchedHeight() {
		return (Extent) getProperty(PROPERTY_MAXIMUM_STRETCHED_HEIGHT) ;
	}

	/**
	 * @see echopointng.able.Stretchable#getMinimumStretchedHeight()
	 */
	public Extent getMinimumStretchedHeight() {
		return (Extent) getProperty(PROPERTY_MINIMUM_STRETCHED_HEIGHT) ;
	}

	/**
	 * @see echopointng.able.Stretchable#isHeightStretched()
	 */
	public boolean isHeightStretched() {
		return getProperty(PROPERTY_HEIGHT_STRETCHED, false);
	}

	/**
	 * @see echopointng.able.Stretchable#setHeightStretched(boolean)
	 */
	public void setHeightStretched(boolean newValue) {
		setProperty(PROPERTY_HEIGHT_STRETCHED, newValue);
	}

	/**
	 * @see echopointng.able.Stretchable#setMaximumStretchedHeight(nextapp.echo2.app.Extent)
	 */
	public void setMaximumStretchedHeight(Extent newValue) {
        Extent.validate(newValue, Extent.PX);
		setProperty(PROPERTY_MAXIMUM_STRETCHED_HEIGHT, newValue);
	}

	/**
	 * @see echopointng.able.Stretchable#setMinimumStretchedHeight(nextapp.echo2.app.Extent)
	 */
	public void setMinimumStretchedHeight(Extent newValue) {
        Extent.validate(newValue, Extent.PX);
		setProperty(PROPERTY_MINIMUM_STRETCHED_HEIGHT, newValue);
	}

	/**
	 * @see echopointng.able.Scrollable#getScrollBarBaseColor()
	 */
	public Color getScrollBarBaseColor() {
		return (Color) getProperty(Scrollable.PROPERTY_SCROLL_BAR_BASE_COLOR);
	}

	/**
	 * @see echopointng.able.Scrollable#getScrollBarPolicy()
	 */
	public int getScrollBarPolicy() {
		return getProperty(Scrollable.PROPERTY_SCROLL_BAR_POLICY,Scrollable.AUTO);
	}

	/**
	 * @see echopointng.able.Scrollable#getScrollBarProperties()
	 */
	public ScrollBarProperties getScrollBarProperties() {
		return (ScrollBarProperties) getProperty(Scrollable.PROPERTY_SCROLL_BAR_PROPERTIES);
	}

	/**
	 * @see echopointng.able.Scrollable#setScrollBarBaseColor(nextapp.echo2.app.Color)
	 */
	public void setScrollBarBaseColor(Color newValue) {
		setProperty(Scrollable.PROPERTY_SCROLL_BAR_BASE_COLOR,newValue);
	}

	/**
	 * @see echopointng.able.Scrollable#setScrollBarPolicy(int)
	 */
	public void setScrollBarPolicy(int newScrollBarPolicy) {
		setProperty(Scrollable.PROPERTY_SCROLL_BAR_POLICY,newScrollBarPolicy);
	}

	/**
	 * @see echopointng.able.Scrollable#setScrollBarProperties(echopointng.able.ScrollBarProperties)
	 */
	public void setScrollBarProperties(ScrollBarProperties newValue) {
		setProperty(Scrollable.PROPERTY_SCROLL_BAR_PROPERTIES,newValue);
	}	
}
