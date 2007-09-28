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
package echopointng.tabbedpane;

import java.util.EventListener;

import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.event.EventListenerList;

/** 
 * <code>AbstractTabModel</code> is an abstract base class from which 
 * to build a <code>TabModel</code>. 
 */

public abstract class AbstractTabModel implements TabModel {

	/**
	 * Only one ChangeEvent is needed per model instance since the event's only
	 * (read-only) state is the source property. The source of events generated
	 * here is always "this".
	 */
	protected transient ChangeEvent changeEvent = null;

	/** our list of event listeners */
	protected EventListenerList listenerList = new EventListenerList();
	
	/** our TabImageRenderer field */
	protected TabImageRenderer tabImageRenderer;
	
	/**
	 * @see echopointng.tabbedpane.TabModel#addChangeListener(nextapp.echo2.app.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener listener) {
		listenerList.addListener(ChangeListener.class, listener);
	}

	/**
	 * Notifies all listeners that have registered that the model has changed
	 */
	public void fireStateChanged() {
		EventListener[] listeners = listenerList.getListeners(ChangeListener.class);
		if (changeEvent == null) {
			changeEvent = new ChangeEvent(this);
		}
		for (int index = 0; index < listeners.length; ++index) {
			((ChangeListener) listeners[index]).stateChanged(changeEvent);
		}
	}

	/**
	 * By default there is no TabImageRenderer
	 * 
	 * @see echopointng.tabbedpane.TabModel#getTabImageRenderer()
	 */
	public TabImageRenderer getTabImageRenderer() {
		return tabImageRenderer;
	}

	/**
	 * @see echopointng.tabbedpane.TabModel#removeChangeListener(nextapp.echo2.app.event.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener listener) {
		listenerList.removeListener(ChangeListener.class, listener);
	}

	/**
	 * Sets the TabImageRenderer to be used by this <code>TabModel</code>
	 * 
	 * @param tabImageRenderer -
	 *            the TabImageRenderer to be used by this <code>TabModel</code>
	 */
	public void setTabImageRenderer(TabImageRenderer tabImageRenderer) {
		this.tabImageRenderer = tabImageRenderer;
	}
}
