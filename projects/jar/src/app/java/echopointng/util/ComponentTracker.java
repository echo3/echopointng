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
package echopointng.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nextapp.echo2.app.Component;

/** 
 * <code>ComponentTracker</code> keeps track of components that have been
 * added and removed from it and its associated component.  This class allows
 * you to add/remove child components while keeping them in a known 
 * bucket (the tracker). 
 */

public class ComponentTracker implements Serializable {
	Map componentMap;
	Component trackee;
	
	/**
	 * Constructs a <code>ComponentTracker</code> that will keep track 
	 * of components on behalf of <code>trackeeComponent</code>
	 *
	 * @param trackeeComponent - the component for which child components
	 * 	will be tracked.
	 */
	public ComponentTracker(Component trackeeComponent) {
		this.trackee = trackeeComponent;
	}
	
	/**
	 * @see Component#add(Component)
	 */
	public void add(Component child) {
		add(child,-1);
	}
	
	/**
	 * @see Component#add(Component, int)
	 */
	public void add(Component child, int index) {
		if (componentMap == null)
			componentMap = new HashMap();
		componentMap.put(child,child);
		trackee.add(child,index);
	}
	
	/**
	 * @see Component#remove(Component)
	 */
	public void remove(Component child) {
		if (componentMap != null) {
			componentMap.remove(child);
		}
		trackee.remove(child);
	}

	/**
	 * @see Component#remove(int)
	 */
	public void remove(int index) {
		Component child = trackee.getComponent(index);
		if (componentMap != null) {
			componentMap.remove(child);
		}
		trackee.remove(index);
	}
	
	/**
	 * @see Component#removeAll()
	 */
	public void removeAll() {
		if (componentMap != null) {
			for (Iterator iter = componentMap.keySet().iterator(); iter.hasNext();) {
				Component child = (Component) iter.next();
				trackee.remove(child);
				iter.remove();
			}
		}
	}
	
	/**
	 * @see Component#getComponents()
	 */
	public Component[] getComponents() {
		if (componentMap == null) {
			return new Component[0];
		}
		Component children[] = new Component[componentMap.size()];
		int i = 0;
		for (Iterator iter = componentMap.keySet().iterator(); iter.hasNext(); i++) {
			Component child = (Component) iter.next();
			children[i] = child;
		}
		return children;
	}
	
	/**
	 * @see Component#getComponentCount()
	 */
	public int getComponentCount() {
		if (componentMap == null) {
			return 0;
		}
		return componentMap.size();
	}
	
	
}
