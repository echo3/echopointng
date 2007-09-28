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

import nextapp.echo2.app.Style;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import echopointng.able.Expandable;
import echopointng.model.DefaultExpansionModel;
import echopointng.model.ExpansionGroup;
import echopointng.model.ExpansionModel;
import echopointng.util.ColorKit;

/** 
 * <code>ExpandableSection</code> is a container component that 'can expand'
 * to reveal its child component content.
 * <p>
 * An <code>ExpandableSection</code> has two main parts.
 * <ul>
 * <li>The <code>TitleBar</code> area, in which users can click to toggle the expansion of content</li>
 * <li>The content area - consists of all the child components of the <code>ExpandableSection</code>, layed out one 
 * 							after the other.
 * </ul>
 */
public class ExpandableSection extends AbleComponent implements Expandable {

	public static final String PROPERTY_TITLEBAR = "titleBar";
	
	private class InternalExpansionModelListener implements ChangeListener, Serializable {
		
		/**
		 * @see nextapp.echo2.app.event.ChangeListener#stateChanged(nextapp.echo2.app.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			ExpansionModel expansionModel = (ExpansionModel) e.getSource();
			//
			// if an expansion model other than ours, ie the titlebar, tells
			// us of an expansion event, then update out model.
			if (expansionModel != null) {
				boolean isExpanded = expansionModel.isExpanded();
				if (expansionModel != getExpansionModel()) {
					setExpanded(isExpanded);
				} else {
					// set the titlebar to reflect our expansion
					if (getTitleBar() != null && getTitleBar().getExpansionModel() != null) {
						getTitleBar().getExpansionModel().setExpanded(isExpanded);
					}
				}
				Boolean oldValue = Boolean.valueOf(!isExpanded);
				Boolean newValue = Boolean.valueOf(isExpanded);
				firePropertyChange(EXPANDED_CHANGED_PROPERTY,oldValue,newValue);
			}
		}
	}
	private ChangeListener internalExpansionListener = new InternalExpansionModelListener();
	
	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(PROPERTY_BORDER, new BorderEx(ColorKit.makeColor("#ACBCDC")));
		DEFAULT_STYLE = style;
	}
	
	/**
	 * Constructs a <code>ExpandableSection</code> with no content.
	 */
	public ExpandableSection() {
		this((String)null);
	}

	/**
	 * Constructs a <code>ExpandableSection</code> with no content and the 
	 * specified title
	 */
	public ExpandableSection(String title) {
		this(new TitleBar(title));
	}
	/**
	 * Constructs a <code>ExpandableSection</code> with no content and the 
	 * specified <code>TitleBar</code> as the title.
	 */
	public ExpandableSection(TitleBar title) {
		setTitleBar(title);
		setExpansionModel(new DefaultExpansionModel(false));
	}
	/**
	 * @return the TitleBar used at the top of the ExpandableSection
	 */
	public TitleBar getTitleBar() {
		return (TitleBar) getProperty(PROPERTY_TITLEBAR);
	}
	
	/**
	 * Sets the TitleBar used at the top of the ExpandableSection
	 */
	public void setTitleBar(TitleBar newValue) {
		TitleBar oldValue = getTitleBar();
		if (oldValue != null) {
			remove(oldValue);
			if (oldValue.getExpansionModel() != null) {
				oldValue.getExpansionModel().removeChangeListener(internalExpansionListener);
			}
		}
		if (newValue != null) {
			add(newValue);
			if (newValue.getExpansionModel() != null) {
				newValue.getExpansionModel().addChangeListener(internalExpansionListener);
			}
		}
		setProperty(PROPERTY_TITLEBAR,newValue);
	}
	
	/**
	 * @see nextapp.echo2.app.Component#validate()
	 */
	public void validate() {
		if (getExpansionGroup() != null) {
			getExpansionGroup().validate();
		}
		if (getTitleBar() != null) {
			getTitleBar().setExpanded(isExpanded());
		}
	}

	/**
	 * @see echopointng.able.Expandable#getExpansionGroup()
	 */
	public ExpansionGroup getExpansionGroup() {
		return (ExpansionGroup) getProperty(PROPERTY_EXPANSION_GROUP);
	}

	/**
	 * @see echopointng.able.Expandable#getExpansionModel()
	 */
	public ExpansionModel getExpansionModel() {
		return (ExpansionModel) getProperty(PROPERTY_EXPANSION_MODEL);
		
	}

	/**
	 * @see echopointng.able.Expandable#isExpanded()
	 */
	public boolean isExpanded() {
		return (getExpansionModel() == null ? false : getExpansionModel().isExpanded());
	}

	/**
	 * @see echopointng.able.Expandable#setExpanded(boolean)
	 */
	public void setExpanded(boolean isExpanded) {
		ExpansionModel model = getExpansionModel(); 
		if(model != null) {
			model.setExpanded(isExpanded);
		}
	}

	/**
	 * @see echopointng.able.Expandable#setExpansionGroup(echopointng.model.ExpansionGroup)
	 */
	public void setExpansionGroup(ExpansionGroup newExpansionGroup) {
		ExpansionGroup oldValue = getExpansionGroup();
		if (oldValue != null) {
			oldValue.removeExpandable(this);
		}
		if (newExpansionGroup != null) {
			newExpansionGroup.addExpandable(this);
		}
		setProperty(PROPERTY_EXPANSION_GROUP,newExpansionGroup);
	}

	/**
	 * @see echopointng.able.Expandable#setExpansionModel(echopointng.model.ExpansionModel)
	 */
	public void setExpansionModel(ExpansionModel newExpansionModel) {
		setProperty(PROPERTY_EXPANSION_MODEL,newExpansionModel);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getTitleBar());
		sb.append(" : ");
		sb.append(super.toString());
		return sb.toString();
	}
}
