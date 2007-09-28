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
import echopointng.TabbedPane;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.event.ChangeListener;

/**
 * <code>TabModel</code> is used to provide information on a collection of
 * tabs, including their title, icons and the components that represent the
 * tab's content.
 * <p>
 * This is designed to support "lazy" loading of the tabs content and TabbedPane
 * will not call getTabContentAt(index) until absolutely needed.
 * <p>
 * From a design point of view, this class is partly a model of the tabs and
 * content and partly a tab renderer responsible for showing the right UI
 * updates given the selection state of tabs etc..
 */

public interface TabModel {

	/**
	 * This is called to return a <code>TabImageRenderer</code> which can be
	 * used to render the tabs as images. This can be null, in which case tabs
	 * cannot be rendered as images.
	 * 
	 * @return a <code>TabImageRenderer</code> or null
	 */
	public TabImageRenderer getTabImageRenderer();

	/**
	 * This is called to return how many tabs are returned by this
	 * <code>TabModel</code>.
	 * 
	 * @return an int greater than 0
	 */
	public int size();

	/**
	 * This is called to retrieve a <code>Component</code> that represents the
	 * title of the tab at the specified <code>index</code>.
	 * <p>
	 * The returned Component must be unique since it will become a child of the
	 * TabbedPane so it can be fully rendered.
	 * <p>
	 * If the returned Component has ActionListeners, such as a ButtonEx, then
	 * the parent TabbedPane wil attach itself so it can know when the Tab is
	 * pressed and hence has become selected. If not then it is the responbility
	 * of the component to inform that TabbedPane of the events that may affect
	 * tab selection.
	 * <p>
	 * This method may be called at any time and does not support lazy loading
	 * of the tabs (since it makes no sense as they must all be visible in order
	 * to select one).
	 * 
	 * @param tabbedPane -
	 *            a TabbedPane that this model applies to
	 * @param index -
	 *            the index of the tab
	 * @param isSelected -
	 *            true if the index is the current selected tab index.
	 * 
	 * @return - the component of the tab at <code>index</code> or null if
	 *         there isnt a component at that index
	 */
	public Component getTabAt(TabbedPane tabbedPane, int index, boolean isSelected);

	/**
	 * This is called to retrieve the <code>Component</code> that represents
	 * the contents of the tab at the specified <code>index</code>. This is
	 * designed to be lazy loaded, and hence will not be called until absolutely
	 * needed.
	 * <p>
	 * However once it has been called it may be called multiples times again so
	 * take care about how content <code>Component</code> s are dynamically
	 * created.
	 * 
	 * @param index -
	 *            the index of the tab
	 * 
	 * @return - the component which makes up the tabs content at
	 *         <code>index</code> or null if there isnt a component at the
	 *         index
	 */
	public Component getTabContentAt(int index);

	/**
	 * This is called when the content of a tab is no longer selected and hence
	 * resources associated with it may be released by the underlying
	 * <code>TabModel</code>. The content Component will have been released
	 * from the TabbedPane when this call is made.
	 * 
	 * @param index -
	 *            the index of the tab to be released
	 */
	public void releaseTabAt(int index);

	/**
	 * This is called to return the index of the component that makes up a tab.
	 * 
	 * @param tabComponent -
	 *            the component to return and index for
	 * 
	 * @return the index of the component or -1 if it cant be found
	 */
	public int indexOfTab(Component tabComponent);

	/**
	 * This is called to return the index of the tab that would show the
	 * provided content component.
	 * 
	 * @param tabContent
	 *            the component that makes up the content
	 * @return - the index of the tab that would show the provided content
	 *         component.
	 */
	public int indexOfTabContent(Component tabContent);

	/**
	 * Adds <I>listener </I> as a listener to changes in the model.
	 */
	public void addChangeListener(ChangeListener listener);

	/**
	 * Removes <I>listener </I> as a listener to changes in the model.
	 */
	public void removeChangeListener(ChangeListener listener);
}
