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
package echopointng.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.RenderIdSupport;
import echopointng.able.Expandable;

/**
 * <code>ExpansionGroup</code> is a collection of <code>Expandable</code>'s which allows 
 * the expansion of only one <code>Expandable</code> at a time.
 * <p>
 * The accordion mode is a more restrictive form of this in that
 * it allows on only one <code>Expandable</code> to be expanded at one time and it
 * also requires at least one <code>Expandable</code> to be expanded. 
 */
public class ExpansionGroup implements Serializable, RenderIdSupport {

    private static final Expandable[] EMPTY = new Expandable[0];
    private String id = ApplicationInstance.generateSystemId();
    private List members;
    private boolean accordionMode;

	/**
	 * Constructs an <code>ExpansionGroup</code>
	 */
	public ExpansionGroup() {
		super();
	}
	
    /**
     * Adds a <code>Expandable</code> to the group.
     * 
     * @param expandable the <code>Expandable</code> to add
     */
	public void addExpandable(Expandable expandable) {
        if (members == null) {
            members = new ArrayList();
        }
        if (! members.contains(expandable)) {
        	members.add(expandable);
        }
	}
	
    /**
     * Returns all <code>Expandable</code>s in the group.
     * 
     * @return all <code>Expandable</code>s in the group.
     */
	public Expandable[] getExpandables() {
        if (members == null) {
            return EMPTY;
        } else {
            return (Expandable[]) members.toArray(new Expandable[members.size()]);
        }
	}

   /**
     * @see nextapp.echo2.app.RenderIdSupport#getRenderId()
     */
    public String getRenderId() {
        return id;
    }

    /**
     * Removes a <code>Expandable</code> from the group.
     * 
     * @param expandable the <code>Expandable</code> to remove
     */
    public void removeExpandable(Expandable expandable) {
        if (members != null) {
            members.remove(expandable);
        }
    }

	/**
	 * @return true if the <code>ExpansionGroup</code> is in accordion mode.
	 */
	public boolean isAccordionMode() {
		return accordionMode;
	}

	/**
	 * Controls whether the <code>ExpansionGroup</code> is in accordion mode.  If it is
	 * in accordion mode, the at least one of the Expanables is required to be
	 * opened at all times.
	 */
	public void setAccordionMode(boolean newValue) {
		accordionMode = newValue;
	}
	
	/**
	 * This will validate that the Expandables in the in the right
	 * state.  If multiple Expandables in the group are expanded
	 * then the first one will be left expanded and all the rest 
	 * will be collapsed.
	 */
	public void validate() {
		Expandable firstExpanded = null;
		Expandable[] expandables = getExpandables();
		if (expandables.length == 0) {
			return;
		}
		for (int i = 0; i < expandables.length; i++) {
			if (expandables[i].isExpanded()) {
				firstExpanded = expandables[i];
				break;
			}
		}
		if (isAccordionMode()) {
			if (firstExpanded == null) {
				// none are expanded.  We must have one 
				firstExpanded = expandables[0];
				firstExpanded.setExpanded(true);
			}
		}
		// only one may be opened at a time, however they
		// may all be 
		for (int i = 0; i < expandables.length; i++) {
			if (expandables[i] != firstExpanded) {
				if (expandables[i].isExpanded()) {
					expandables[i].setExpanded(false);
				}
			}
		}
	}
}
