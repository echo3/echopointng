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
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nextapp.echo2.app.Style;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.event.DocumentEvent;
import nextapp.echo2.app.event.DocumentListener;
import echopointng.model.CalendarEvent;
import echopointng.model.CalendarSelectionListener;
import echopointng.model.CalendarSelectionModel;

/**
 * <code>DateField</code> is a drop down component that contains a text field
 * and a drop down calendar. The text field is updated with the contents of the
 * DateField calendar.
 */

public class DateField extends DropDown {

	/**
	 * <code>InternalListener</code> is used to listen to our underlying
	 * models
	 */
	private class InternalListener implements CalendarSelectionListener,
			DocumentListener, Serializable {
		public void displayedDateChange(CalendarEvent calEvent) {
			// do Nada on display date change!
		}

		public void documentUpdate(DocumentEvent e) {
			if (selectionPending) {
				return;
			}
			updateDateFromText();
			// make absolutely sure it moves to closed!
			expansionOverride = true;
			setExpanded(true);
			setExpanded(false);
		}

		public void selectedDateChange(CalendarEvent calEvent) {
			if (selectionPending) {
				return;
			}
			if (!updateFromTextField) {
				return;
			}
			updateTextFromDate();
			// make absolutely sure it moves to closed!
			expansionOverride = true;
			setExpanded(true);
			setExpanded(false);
		}
	}

	/**
	 * The default appearance of the TextField component with the DateField
	 */
	public static final Style DEFAULT_TEXT_FIELD_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(TextField.PROPERTY_BORDER, new BorderEx(0, null,
				BorderEx.STYLE_NONE));
		DEFAULT_TEXT_FIELD_STYLE = style;
	}

	private DateFormat dateFormat;

	private InternalListener internalListener = new InternalListener();

	private boolean selectionPending = false;

	private boolean expansionOverride = false;

	private boolean updateFromTextField = true;

	/**
	 * Constructs a <code>DateField</code> with the date format as
	 * <code>DateFormat.getDateInstance()</code> and the current date via
	 * <code>Calendar.getInstance()</code>.
	 */
	public DateField() {
		this(Calendar.getInstance());
	}

	/**
	 * Constructs a <code>DateField</code> with the date format as
	 * <code>DateFormat.getDateInstance()</code> and the specified
	 * <code>startDate</code>.
	 */
	public DateField(Calendar startDate) {
		// a default for the current locale
		setDateFormat(DateFormat.getDateInstance());

		TextField tf = new TextField();
		tf.setStyle(DEFAULT_TEXT_FIELD_STYLE);
		setTextField(tf);

		DateChooser dc = new DateChooser();
		setDateChooser(dc);
		dc.setSelectedDate(startDate);
	}

	/**
	 * @see echopointng.PopUp#processInput(java.lang.String, java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		if (!expansionOverride) {
			super.processInput(inputName, inputValue);
		}
		expansionOverride = false;
	}

	/**
	 * @return Returns the dateChooser.
	 */
	public DateChooser getDateChooser() {
		return (DateChooser) getPopUp();
	}

	/**
	 * @return Returns the dateFormat.
	 */
	public DateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * A shortcurt method to the underlying DateChooser's model
	 * getDisplayedDate() method.
	 * 
	 * @return The currently displayed date.
	 */
	public Calendar getDisplayedDate() {
		if (getModel() != null) {
			return getModel().getDisplayedDate();
		}
		return null;
	}

	/**
	 * Short hand method for <code>getDateChooser().getModel().</code>
	 * 
	 * @return the CalendarSelectionModel used by the underlying DateChooser.
	 */
	public CalendarSelectionModel getModel() {
		if (getDateChooser() != null) {
			return getDateChooser().getModel();
		}
		return null;
	}

	/**
	 * A shortcurt method to the underlying DateChooser's model
	 * getSelectedDate() method.
	 * 
	 * @return The currently selected date
	 */
	public Calendar getSelectedDate() {
		if (getModel() != null) {
			return getModel().getSelectedDate();
		}
		return null;
	}

	/**
	 * @return Returns the textField.
	 */
	public TextField getTextField() {
		return (TextField) getTarget();
	}
	
	/**
	 * Returns the text of the <code>DateField</code>'s TextField.
	 * 
	 * @return the text of the <code>DateField</code>'s TextField.
	 */
	public String getText() {
		TextField textField = getTextField();
		if (textField != null) {
			return textField.getText();
		}
		return null;
	}

	/**
	 * @return true if the Datefield will update the CalendarSelectionModel when
	 *         changes are made to the TextField.
	 */
	public boolean isUpdateFromTextField() {
		return updateFromTextField;
	}

	/**
	 * @param newValue -
	 *            The new DateChooser to use.
	 */
	public void setDateChooser(DateChooser newValue) {
		if (newValue == null || newValue.getModel() == null)
			throw new IllegalArgumentException(
					"The DateChooser and its CalendarSelectionModel must be non null.");

		DateChooser oldValue = getDateChooser();
		if (oldValue != null) {
			oldValue.getModel().removeListener(internalListener);
		}
		newValue.getModel().addListener(internalListener);
		setPopUp(newValue);
	}

	/**
	 * @param newValue -
	 *            The dateFormat to set.
	 */
	public void setDateFormat(DateFormat newValue) {
		if (newValue == null)
			throw new IllegalArgumentException(
					"The DateFormat must be non null.");
		this.dateFormat = newValue;
	}

	/**
	 * Sets the displayed date within the <code>DateChooser</code>'s model.
	 * 
	 * @param newDisplayedDate -
	 *            the new displayed date
	 */
	public void setDisplayedDate(Calendar newDisplayedDate) {
		getModel().setDisplayedDate(newDisplayedDate);
	}

	/**
	 * Sets the CalendarSelectionModel to be used on the underlying DateChooser
	 * component.
	 * 
	 * @param newValue -
	 *            The new CalendarSelectionModel to use
	 * 
	 */
	public void setModel(CalendarSelectionModel newValue) {
		CalendarSelectionModel oldValue = getModel();
		if (newValue != oldValue) {
			oldValue.removeListener(internalListener);
			if (newValue != null) {
				newValue.addListener(internalListener);
			}
			if (getDateChooser() != null) {
				getDateChooser().setModel(newValue);
			}
			updateTextFromDate();
		}

	}

	/**
	 * Sets the selected date within the <code>DateChooser</code>'s model.
	 * <p>
	 * This will also have the effect og setting the text of the
	 * <code>TextField</code> since the DateField is listening to the
	 * udnerlying <code>CalendarSelectionModel</code>.
	 * 
	 * @param newSelectedDate -
	 *            the new selected date
	 */
	public void setSelectedDate(Calendar newSelectedDate) {
		getModel().setSelectedDate(newSelectedDate);
	}

	/**
	 * @param newValue -
	 *            The textField to set.
	 */
	public void setTextField(TextField newValue) {
		if (newValue == null || newValue.getDocument() == null)
			throw new IllegalArgumentException(
					"The TextField and its Document must be non null.");

		TextField oldValue = getTextField();
		if (oldValue != null) {
			oldValue.getDocument().removeDocumentListener(internalListener);
		}
		newValue.getDocument().addDocumentListener(internalListener);
		setTarget(newValue);
	}

	/**
	 * Set to true, the Datefield will update the CalendarSelectionModel when
	 * changes are made to the TextField.
	 * 
	 * @param newValue -
	 *            true or false
	 */
	public void setUpdateFromTextField(boolean newValue) {
		this.updateFromTextField = newValue;
	}

	/**
	 * Called to update the calendar selection model from the current text field
	 * contents. Only works if <code>isUpdateFromTextField()</code> currently
	 * returns true.
	 */
	protected void updateDateFromText() {
		if (!isUpdateFromTextField()) {
			return;
		}
		selectionPending = true;
		String text = getTextField().getText();
		if (dateFormat != null) {
			try {
				Date date = dateFormat.parse(text);
				Locale locale = getLocale();
				if (locale == null) {
					locale = Locale.getDefault();
				}
				Calendar cal = Calendar.getInstance(locale);
				cal.setTime(date);
				getModel().setDates(cal, cal);
			} catch (ParseException pe) {
				// do nothing if we cant parse it!
			}
		}
		selectionPending = false;
	}

	/**
	 * Called to update the text field from the current calendar selection
	 * model.  This uses the current date format to process the models
	 * selected date.
	 */
	protected void updateTextFromDate() {
		selectionPending = true;
		CalendarSelectionModel model = getModel();
		String text = "";
		if (model.getSelectedDate() != null) {
			text = getDateFormat().format(model.getSelectedDate().getTime());
		}
		getTextField().setText(text);
		selectionPending = false;
	}

	/**
	 * @see nextapp.echo2.app.Component#validate()
	 */
	public void validate() {
		super.validate();
		selectionPending = false;
		expansionOverride = false;
	}
}