package echopointng;

/*
 * This file is part of the Echo Point Project. This project is a collection of Components that have extended the Echo
 * Web Application Framework.
 * 
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version 1.1 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the License for the specific language governing rights and limitations under the License.
 * 
 * Alternatively, the contents of this file may be used under the terms of either the GNU General Public License Version
 * 2 or later (the "GPL"), or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in which case the
 * provisions of the GPL or the LGPL are applicable instead of those above. If you wish to allow use of your version of
 * this file only under the terms of either the GPL or the LGPL, and not to allow others to use your version of this
 * file under the terms of the MPL, indicate your decision by deleting the provisions above and replace them with the
 * notice and other provisions required by the GPL or the LGPL. If you do not delete the provisions above, a recipient
 * may use your version of this file under the terms of any one of the MPL, the GPL or the LGPL.
 */

import java.io.Serializable;
import java.util.EventListener;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.DelegateFocusSupport;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.DocumentEvent;
import nextapp.echo2.app.event.DocumentListener;
import nextapp.echo2.app.event.ListDataEvent;
import nextapp.echo2.app.event.ListDataListener;
import nextapp.echo2.app.list.AbstractListComponent;
import nextapp.echo2.app.list.DefaultListModel;
import nextapp.echo2.app.list.ListCellRenderer;
import nextapp.echo2.app.list.ListModel;
import nextapp.echo2.app.text.StringDocument;
import nextapp.echo2.app.text.TextComponent;

/**
 * The <code>ComboBox</code> component is a TextField that acts like a SelectField as well. It will present a list of
 * choices to the user (like a SelectField) that can be auto-matched as the user types in key strokes.
 * <p>
 * The <code>ComboBox</code> has a drop down button that when pressed will toggle the display of a list of choices,
 * which the user can then select from.
 * <p>
 * The <code>ComboBox</code> can be set to remember which values have been entered and add them to its internal
 * ListModel. This can happen if the ListModel is an instanceof DefaultListModel and hence has an add() method. This
 * behaviour is off by default, but can be switched on via the setAutoRecall() method.
 */
public class ComboBox extends DropDown implements DelegateFocusSupport {

	public static final String PROPERTY_ACTION_ON_SELECTION = "actionOnSelection";
	public static final String PROPERTY_AUTO_RECALL = "autoRecall";
	public static final String PROPERTY_AUTO_RECALL_LIMIT = "autoRecallLimit";
	public static final String PROPERTY_CASE_SENSITIVE = "caseSensitive";
	public static final String PROPERTY_LIST_ROWCOUNT = "listRowCount";
	public static final String PROPERTY_TEXT_MATCHING = "textMatching";
	public static final String PROPERTY_TEXTFIELD_DOCUMENT = "textFieldDocument";
	public static final String PROPERTY_TEXTFIELD_EDITABLE = "textFieldEditable";

	private static final int DEFAULT_AUTO_RECALL_LIMIT = 20;
	private static final int DEFAULT_TEXT_FIELD_COLUMNS = 20;
	private static final int DEFAULT_LIST_ROWCOUNT = 5;

	/**
	 * The default appearance of the TextField component within the ComboBox
	 * Borderless so that the text field appears part of the larger control
	 * */
	public static final Style DEFAULT_TEXT_FIELD_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(TextField.PROPERTY_BORDER, new BorderEx(0, null, BorderEx.STYLE_NONE));
		DEFAULT_TEXT_FIELD_STYLE = style;
	}

	private TextField textField;
	private ListModel listModel;
	private ListCellRenderer listCellRenderer = AbstractListComponent.DEFAULT_LIST_CELL_RENDERER;
	private boolean selectionPending;
	

	/**
	 * Local handler for text field action events.
	 */
	private class TextFieldListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			updateListModel();
			fireActionEvent();
			setExpanded(false);
			selectionPending = true;
		}
	}

	private TextFieldListener textFieldListener = new TextFieldListener();


	/**
	 * Local handler for text field document events.
	 */
	private class DocListener implements DocumentListener {

		public void documentUpdate(DocumentEvent e) {
			firePropertyChange(PROPERTY_TEXTFIELD_DOCUMENT, null, null);
		}
	}

	private DocListener docListener = new DocListener();


	/**
	 * Local handler for list data change events.
	 */
	private class ListDataHandler implements ListDataListener, Serializable {

		public void contentsChanged(ListDataEvent e) {
			firePropertyChange(AbstractListComponent.LIST_DATA_CHANGED_PROPERTY, null, null);
		}

		public void intervalAdded(ListDataEvent e) {
			firePropertyChange(AbstractListComponent.LIST_DATA_CHANGED_PROPERTY, null, null);
		}

		public void intervalRemoved(ListDataEvent e) {
			firePropertyChange(AbstractListComponent.LIST_DATA_CHANGED_PROPERTY, null, null);
		}
	}

	private ListDataHandler listListener = new ListDataHandler();


	/**
	 * Creates a <code>ComboBox</code>
	 */
	public ComboBox() {
		this(null, DEFAULT_TEXT_FIELD_COLUMNS, new Object[0]);
	}

	/**
	 * Creates a <code>ComboBox</code> that is <i>columns </i> wide.
	 */
	public ComboBox(int columns) {
		this(null, columns, new Object[0]);
	}

	/**
	 * Creates a <code>ComboBox</code> with <code>text</code> as the initial value.
	 * 
	 */
	public ComboBox(String text) {
		this(text, DEFAULT_TEXT_FIELD_COLUMNS, new Object[0]);
	}

	/**
	 * Creates a <code>ComboBox</code> with <code>list</code> as the initial list contents.
	 * 
	 */
	public ComboBox(Object[] list) {
		this(null, DEFAULT_TEXT_FIELD_COLUMNS, list);
	}

	/**
	 * Creates a <code>ComboBox</code> with <code>text</code> as the initial value and <code>list</code> as the
	 * initial list contents.
	 * 
	 */
	public ComboBox(String text, Object[] list) {
		this(text, DEFAULT_TEXT_FIELD_COLUMNS, list);
	}

	/**
	 * Creates a <code>ComboBox</code> with <code>text</code> as the initial value and that is <code>columns</code>
	 * wide and <code>list</code> as the initial list contents.
	 * 
	 */
	public ComboBox(String text, int columns, Object[] list) {
		TextField tf = new TextField(new StringDocument(), text, columns);
		tf.setStyle(DEFAULT_TEXT_FIELD_STYLE);
		setTextField(tf);
		setListModel(new DefaultListModel(list));
	}

	
    /**
     * Returns the <code>Component</code> that should receive the focus when 
     * this <code>Component</code>is focused, enabling composite components to 
     * specify a specific child to be focused.
     * 
     * @return the <code>Component</code> that should receive the focus when 
     *         this <code>Component</code>is focused 
     */
	public Component getFocusComponent() {
		return getTextField();
	}
	
	/**
	 * Adds an <code>ActionListener</code> to the ComboBox component. The <code>ActionListener</code> will be
	 * invoked when the user selects an item.
	 * 
	 * @param l the <code>ActionListener</code> to add
	 */
	public void addActionListener(ActionListener l) {
		getEventListenerList().addListener(ActionListener.class, l);
		// Notification of action listener changes is provided due to
		// existance of hasActionListeners() method.
		firePropertyChange(AbstractListComponent.ACTION_LISTENERS_CHANGED_PROPERTY, null, l);
		textField.addActionListener(textFieldListener);
	}

	/**
	 * Removes an <code>ActionListener</code> from the <code>TextField</code>.
	 * 
	 * @param l the <code>ActionListener</code> to remove
	 */
	public void removeActionListener(ActionListener l) {
		getEventListenerList().removeListener(ActionListener.class, l);
		// Notification of action listener changes is provided due to
		// existance of hasActionListeners() method.
		firePropertyChange(AbstractListComponent.ACTION_LISTENERS_CHANGED_PROPERTY, l, null);
		textField.removeActionListener(textFieldListener);
	}

	/**
	 * @see nextapp.echo2.app.Component#validate()
	 */
	public void validate() {
		super.validate();
		// popup list always invisible on redraw
		if (selectionPending) {
			setExpanded(false);
			selectionPending = false;
		}
	}

	/**
	 * Fires an action event to all listeners.
	 */
	private void fireActionEvent() {
		EventListener[] listeners = getEventListenerList().getListeners(ActionListener.class);
		ActionEvent e = null;
		for (int i = 0; i < listeners.length; ++i) {
			if (e == null) {
				e = new ActionEvent(this, (String) getRenderProperty(TextComponent.PROPERTY_ACTION_COMMAND));
			}
			((ActionListener) listeners[i]).actionPerformed(e);
		}
	}

	/**
	 * Tries to add the current text to the internal list model. isAutoRecall must be true.
	 */
	private void updateListModel() {
		if (isAutoRecall()) {
			String text = getText();
			if (listModel instanceof DefaultListModel && text.length() > 0) {

				int modelSize = listModel.size();
				DefaultListModel dm = (DefaultListModel) listModel;
				boolean found = false;

				for (int i = 0; i < modelSize; i++) {
					if (text.equals(listModel.get(i))) {
						found = true;
						break;
					}
				}

				if (!found && modelSize < getAutoRecallLimit()) {
					dm.add(text);
				}
			}
		}
	}

	/**
	 * Returns the auto recall limit
	 * 
	 * @return the auto recall limit
	 */
	public int getAutoRecallLimit() {
		return getProperty(this, PROPERTY_AUTO_RECALL_LIMIT, DEFAULT_AUTO_RECALL_LIMIT);
	}

	/**
	 * Returns the renderer for items.
	 * 
	 * @return The renderer for items.
	 */
	public ListCellRenderer getListCellRenderer() {
		return listCellRenderer;
	}

	/**
	 * Returns the list model for items.
	 * 
	 * @return The list model for items.
	 */
	public ListModel getListModel() {
		return listModel;
	}

	/**
	 * @see nextapp.echo2.app.text.TextComponent#getText()
	 */
	public String getText() {
		return textField.getText();
	}

	/**
	 * Returns the number of visible rows in the drop-down list
	 * 
	 * @return the number of rows
	 */
	public int getListRowCount() {
		return getProperty(this, PROPERTY_LIST_ROWCOUNT, DEFAULT_LIST_ROWCOUNT);
	}

	/**
	 * Returns the text field component of the combobox
	 * 
	 * @return the textField.
	 */
	public TextField getTextField() {
		return textField;
	}

	/**
	 * Determines the any <code>ActionListener</code>s are registered.
	 * 
	 * @return true if any action listeners are registered
	 */
	public boolean hasActionListeners() {
		return getEventListenerList().getListenerCount(ActionListener.class) != 0;
	}

	/**
	 * Indicates whether a server action is done when the list selection changes.
	 * 
	 * @return boolean
	 */
	public boolean isActionOnSelection() {
		return getProperty(this, PROPERTY_ACTION_ON_SELECTION, false);
	}

	/**
	 * Returns true if auto recall is in effect
	 * 
	 * @return boolean
	 */
	public boolean isAutoRecall() {
		return getProperty(this, PROPERTY_AUTO_RECALL, false);
	}

	/**
	 * Indicates whether text matching is case sensitive.
	 * 
	 * @return boolean
	 */
	public boolean isCaseSensitive() {
		return getProperty(this, PROPERTY_CASE_SENSITIVE, false);
	}

	/**
	 * Returns true if text matching is performed
	 * 
	 * @return boolean
	 */
	public boolean isTextMatchingPerformed() {
		return getProperty(this, PROPERTY_TEXT_MATCHING, true);
	}

	/**
	 * Sets how many entries will be automatically recalled. If this value is &lt; 0 then no entries will be remembered.
	 * 
	 * @param newValue - the new limit
	 */
	public void setAutoRecallLimit(int newValue) {
		setProperty(this, PROPERTY_AUTO_RECALL_LIMIT, newValue);
	}

	/**
	 * Controls whether auto recall is in effect. If it is, new items typed into the text box will be added to the
	 * ListModel.
	 * 
	 * @param newValue - True to turn on AutoRecall
	 */
	public void setAutoRecall(boolean newValue) {
		setProperty(this, PROPERTY_AUTO_RECALL, newValue);
	}

	/**
	 * Controls whether a server action is done when the list selection changes. You still need to have an action
	 * listener attached, for this to occur.
	 * 
	 * @param newValue - true or false
	 */
	public void setActionOnSelection(boolean newValue) {
		setProperty(this, PROPERTY_ACTION_ON_SELECTION, newValue);
	}

	/**
	 * Controls whether text matching is case sensitive.
	 * 
	 * @param newValue - True if text matching should be case sensitive
	 */
	public void setCaseSensitive(boolean newValue) {
		setProperty(this, PROPERTY_CASE_SENSITIVE, newValue);
	}

	/**
	 * Sets the visible row count for the drop-down list.
	 * 
	 * @param newValue - number of rows to show in the drop-down list
	 */
	public void setListRowCount(int newValue) {
		setProperty(this, PROPERTY_LIST_ROWCOUNT, newValue);
	}

	/**
	 * Sets the renderer for items.
	 * 
	 * @param newValue The new renderer for items.
	 */
	public void setListCellRenderer(ListCellRenderer newValue) {
		if (newValue == null) throw new IllegalArgumentException("The list cell renderer must be non null");

		ListCellRenderer oldValue = this.listCellRenderer;
		this.listCellRenderer = newValue;
		firePropertyChange(AbstractListComponent.LIST_CELL_RENDERER_CHANGED_PROPERTY, oldValue, newValue);
	}

	/**
	 * Sets the list model.
	 * 
	 * @param newValue The new model.
	 */
	public void setListModel(ListModel newValue) {
		if (newValue == null) throw new IllegalArgumentException("The list model must be non null");
	
		ListModel oldValue = this.listModel;
		if (oldValue != null) oldValue.removeListDataListener(listListener);
		newValue.addListDataListener(listListener);
		this.listModel = newValue;
		firePropertyChange(AbstractListComponent.LIST_MODEL_CHANGED_PROPERTY, oldValue, newValue);
	}

	/**
	 * @see nextapp.echo2.app.text.TextComponent#setText(java.lang.String)
	 */
	public void setText(String newValue) {
		textField.setText(newValue);
	}

	/**
	 * Assigns a new text field to the combo box
	 * 
	 * @param newValue - The textField to set.
	 */
	public void setTextField(TextField newValue) {
		if (newValue == null || newValue.getDocument() == null) {
			throw new IllegalArgumentException("The TextField and its Document must be non null.");
		}
		TextField oldValue = this.textField;
		if (oldValue != null) oldValue.getDocument().removeDocumentListener(docListener);
		newValue.getDocument().addDocumentListener(docListener);
		this.textField = newValue;

		// Set the textField to be the component to have a popup toggle button added to it
		setTarget(newValue);
	}

	/**
	 * Controls whether text matching is performed as the user types in the text field. If true, then the options in the
	 * drop down list will be restricted to those the prefix match the current text field value.
	 * 
	 * @param newValue - True if text matching is to be performed
	 */
	public void setTextMatchingPerformed(boolean newValue) {
		setProperty(this, PROPERTY_TEXT_MATCHING, newValue);
	}

}