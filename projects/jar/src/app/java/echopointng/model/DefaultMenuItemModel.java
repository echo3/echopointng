package echopointng.model;

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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.EventListener;

import nextapp.echo2.app.button.ButtonGroup;
import nextapp.echo2.app.button.ButtonModel;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.event.EventListenerList;

/**
 * The default implementation of a menu item data model.
 */
public class DefaultMenuItemModel implements ButtonModel, Serializable {

	private EventListenerList listenerList = null;

	private String actionCommand = null;

	private ButtonGroup group = null;

	/**
	 * Creates a new default menu item model.
	 */
	public DefaultMenuItemModel() {
		super();

		listenerList = new EventListenerList();
	}

	/**
	 * Adds an <code>ActionListener</code> to the menu item model.
	 * 
	 * @param l
	 *            The <code>ActionListener</code> to be added.
	 */
	public void addActionListener(ActionListener l) {
		listenerList.addListener(ActionListener.class, l);
	}

	/**
	 * Adds a <code>ChangeListener</code> to the menu item model.
	 * 
	 * @param l
	 *            The <code>ChangeListener</code> to be added.
	 */
	public void addChangeListener(ChangeListener l) {
		listenerList.addListener(ChangeListener.class, l);
	}

	/**
	 * Adds a <code>ItemListener</code> to the menu item model.
	 * 
	 * @param l
	 *            The <code>ItemListener</code> to be added.
	 */
	public void addItemListener(ItemListener l) {
		listenerList.addListener(ItemListener.class, l);
	}

	/**
	 * Notifies all listeners that have registered for this event type.
	 * 
	 * @param e
	 *            The <code>ActionEvent</code> to send.
	 */
	public void fireActionPerformed(ActionEvent e) {
		EventListener[] listeners = listenerList.getListeners(ActionListener.class);
		for (int index = 0; index < listeners.length; ++index) {
			((ActionListener) listeners[index]).actionPerformed(e);
		}
	}

	/**
	 * Notifies all listeners that have registered for this event type.
	 * 
	 * @param e
	 *            The <code>ItemEvent</code> to send.
	 */
	public void fireItemStateChanged(ItemEvent e) {
		EventListener[] listeners = listenerList.getListeners(ItemListener.class);
		for (int index = 0; index < listeners.length; ++index) {
			((ItemListener) listeners[index]).itemStateChanged(e);
		}
	}

	/**
	 * Notifies all listeners that have registered for this event type.
	 */
	public void fireStateChanged() {
		EventListener[] listeners = listenerList.getListeners(ChangeListener.class);
		ChangeEvent e = new ChangeEvent(this);
		for (int index = 0; index < listeners.length; ++index) {
			((ChangeListener) listeners[index]).stateChanged(e);
		}
	}

	/**
	 * Returns the action command for this menu item model.
	 * 
	 * @return The action command for this menu item model.
	 */
	public String getActionCommand() {
		return actionCommand;
	}

	/**
	 * Returns the <code>ButtonGroup</code> to which this menu item model
	 * belongs, if any.
	 * 
	 * @return The group which contains this menu item model.
	 */
	public ButtonGroup getGroup() {
		return group;
	}

	/**
	 * Returns the menu item model's selection state.
	 * 
	 * @return The menu item model's selection state.
	 */
	public boolean isSelected() {
		return false;
	}

	/**
	 * Removes an <code>ActionListener</code> from the menu item model.
	 * 
	 * @param l
	 *            The <code>ActionListener</code> to be removed.
	 */
	public void removeActionListener(ActionListener l) {
		listenerList.removeListener(ActionListener.class, l);
	}

	/**
	 * Removes a <code>ChangeListener</code> from the menu item model.
	 * 
	 * @param l
	 *            The <code>ChangeListener</code> to be removed.
	 */
	public void removeChangeListener(ChangeListener l) {
		listenerList.removeListener(ChangeListener.class, l);
	}

	/**
	 * Removes a <code>ItemListener</code> from the menu item model.
	 * 
	 * @param l
	 *            The <code>ItemListener</code> to be removed.
	 */
	public void removeItemListener(ItemListener l) {
		listenerList.removeListener(ItemListener.class, l);
	}

	/**
	 * Sets the menu item model's action command.
	 * 
	 * @param actionCommand
	 *            The new action command for this menu item model.
	 */
	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}

	/**
	 * Sets the <code>ButtonGroup</code> to which this menu item model
	 * belongs. <br>
	 * 
	 * @param group
	 *            The group to which this menu item model belongs.
	 */
	public void setGroup(ButtonGroup group) {
		this.group = group;
	}

	/**
	 * Sets the menu item model's selection state.
	 * 
	 * @param selected
	 *            The new selection state of the menu item model.
	 */
	public void setSelected(boolean selected) {
	}

	/**
	 * Our default doAction
	 */
	public void doAction() {
		fireActionPerformed(new ActionEvent(this, getActionCommand()));
	}

}
