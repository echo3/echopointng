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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.EventListener;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.DocumentEvent;
import nextapp.echo2.app.event.DocumentListener;
import nextapp.echo2.app.text.Document;
import nextapp.echo2.app.text.StringDocument;
import echopointng.util.ColorKit;

/**
 * <code>Calculator</code> is a component that acts like a hand held
 * calculator.
 * <p>
 * Its acts a bit like a TextField in that it has a backing
 * <code>Document</code> and it can raise action events when you press the
 * Transfer button, if its present (which by default is not present).
 * <p>
 * The Transfer button can be used to raise an event so that other components
 * can know about the contents of the Calculator.
 * <p>
 * Take care when setting text into the Calculator, it will call isValidNumber()
 * to ensure that the calculator can take the input. if it cant (eg not a
 * decimal number, then it will throw an IllegalArgumentException.
 */

public class Calculator extends AbleComponent {

	public static final String ACTION_LISTENERS_CHANGED_PROPERTY = "actionListeners";

	public static Style DEFAULT_STYLE;

	public static final String DOCUMENT_CHANGED_PROPERTY = "document";

	public static final String INPUT_TRANSFER = "transfer";

	public static final String PROPERTY_ACTION_COMMAND = "actionCommand";

	public static final String PROPERTY_BUTTON_BACKGROUND = "buttonBackground";

	public static final String PROPERTY_BUTTON_OPERATOR_FOREGROUND = "buttonOperatorForeground";

	public static final String PROPERTY_BUTTON_NUMBER_FOREGROUND = "buttonNumberForeground";
	
	public static final String PROPERTY_BUTTON_HEIGHT = "buttonHeight";

	public static final String PROPERTY_BUTTON_WIDTH = "buttonWidth";

	public static final String PROPERTY_DISPLAY_BACKGROUND = "displayBackground";

	public static final String PROPERTY_DISPLAY_FONT = "displayFont";

	public static final String PROPERTY_DISPLAY_FOREGROUND = "displayForeground";

	public static final String PROPERTY_MEMORY = "memory";

	public static final String PROPERTY_TRANSFER = "transfer";

	public static final String TEXT_CHANGED_PROPERTY = "text";
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(PROPERTY_DISPLAY_BACKGROUND, Color.BLACK);
		style.setProperty(PROPERTY_DISPLAY_FOREGROUND, Color.GREEN);
		style.setProperty(PROPERTY_DISPLAY_FONT, new Font(Font.MONOSPACE, 0, new ExtentEx("8pt")));

		style.setProperty(PROPERTY_INSETS, new Insets(2));
		style.setProperty(PROPERTY_BORDER, new BorderEx(1));
		style.setProperty(PROPERTY_BACKGROUND,ColorKit.clr("#ECE9D8"));
		style.setProperty(PROPERTY_FONT, new Font(Font.MONOSPACE, 0, new ExtentEx("8pt")));

		style.setProperty(PROPERTY_BUTTON_NUMBER_FOREGROUND, Color.BLUE);
		style.setProperty(PROPERTY_BUTTON_WIDTH, new ExtentEx("35px"));
		style.setProperty(PROPERTY_BUTTON_HEIGHT, new ExtentEx("35px"));

		DEFAULT_STYLE = style;
	}

	public static final String DEFAULT_NUMBER_MASK = "########################0.0########################";

	private Document document;

	/**
	 * Local listener to monitor changes to document.
	 */
	private DocumentListener documentListener = new DocumentListener() {
		public void documentUpdate(DocumentEvent e) {
			firePropertyChange(TEXT_CHANGED_PROPERTY, null, ((Document) e.getSource()).getText());
		}
	};

	/**
	 * Constructs a <code>Calculator</code>
	 */
	public Calculator() {
		setDocument(new StringDocument());
		setText("0");
	}

	/**
	 * Constructs a <code>Calculator</code> with the specified backing
	 * Document
	 * 
	 * @param document -
	 *            the backing Document
	 */
	public Calculator(Document document) {
		setDocument(document);
	}

	/**
	 * Adds an <code>ActionListener</code> to the <code>Calculator</code>.
	 * The <code>ActionListener</code> will be invoked when the user presses
	 * the Transfer button in the calculator.
	 * 
	 * @param l
	 *            the <code>ActionListener</code> to add
	 */
	public void addActionListener(ActionListener l) {
		getEventListenerList().addListener(ActionListener.class, l);
		firePropertyChange(ACTION_LISTENERS_CHANGED_PROPERTY, null, l);
	}

	/**
	 * Fires an action event to all listeners.
	 */
	private void fireActionEvent() {
		if (!hasEventListenerList()) {
			return;
		}
		EventListener[] listeners = getEventListenerList().getListeners(ActionListener.class);
		ActionEvent e = null;
		for (int i = 0; i < listeners.length; ++i) {
			if (e == null) {
				e = new ActionEvent(this, (String) getRenderProperty(PROPERTY_ACTION_COMMAND));
			}
			((ActionListener) listeners[i]).actionPerformed(e);
		}
	}

	/**
	 * Returns the action command which will be provided in
	 * <code>ActionEvent</code> s fired by this <code>Calculator</code>.
	 * 
	 * @return the action command
	 */
	public String getActionCommand() {
		return (String) getProperty(PROPERTY_ACTION_COMMAND);
	}

	/**
	 * @return the background color of the calculator's buttons
	 */
	public Color getButtonBackground() {
		return (Color) getProperty(PROPERTY_BUTTON_BACKGROUND);
	}

	/**
	 * @return the foregound color of the calculator's buttons that are operators
	 */
	public Color getButtonOperatorForeground() {
		return (Color) getProperty(PROPERTY_BUTTON_OPERATOR_FOREGROUND);
	}

	/**
	 * @return the foregound color of the calculator's buttons that are operators
	 */
	public Color getButtonNumberForeground() {
		return (Color) getProperty(PROPERTY_BUTTON_NUMBER_FOREGROUND);
	}
	
	/**
	 * @return the height of the calculator's buttons
	 */
	public Extent getButtonHeight() {
		return (Extent) getProperty(PROPERTY_BUTTON_HEIGHT);
	}

	/**
	 * @return the width of the calculator's buttons
	 */
	public Extent getButtonWidth() {
		return (Extent) getProperty(PROPERTY_BUTTON_WIDTH);
	}

	/**
	 * @return the background color of the calculator's number display
	 */
	public Color getDisplayBackground() {
		return (Color) getProperty(PROPERTY_DISPLAY_BACKGROUND);
	}

	/**
	 * @return the font of the calculator's number display
	 */
	public Font getDisplayFont() {
		return (Font) getProperty(PROPERTY_DISPLAY_FONT);
	}

	/**
	 * @return the foreground color of the calculator's number display
	 */
	public Color getDisplayForeground() {
		return (Color) getProperty(PROPERTY_DISPLAY_FOREGROUND);
	}

	/**
	 * Returns the model associated with this <code>TextComponent</code>.
	 * 
	 * @return the model
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * @return the state of the calculator's memory
	 */
	public String getMemory() {
		return (String) getProperty(PROPERTY_MEMORY);
	}

	/**
	 * @return the text for the of the calculator's transfer button
	 * 
	 * @see Calculator#setTransfer(String)
	 */
	public String getTransfer() {
		return (String) getProperty(PROPERTY_TRANSFER);
	}

	/**
	 * Returns the text contained in the <code>Document</code> model of this
	 * text component.
	 * 
	 * @return the text contained in the document
	 */
	public String getText() {
		return document.getText();
	}

	/**
	 * Determines the any <code>ActionListener</code> s are registered.
	 * 
	 * @return true if any action listeners are registered
	 */
	public boolean hasActionListeners() {
		return hasEventListenerList() && getEventListenerList().getListenerCount(ActionListener.class) != 0;
	}

	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		super.processInput(inputName, inputValue);
		if (TEXT_CHANGED_PROPERTY.equals(inputName)) {
			setText((String) inputValue);
		}
		if (PROPERTY_MEMORY.equals(inputName)) {
			setMemory((String) inputValue);
		}
		if (INPUT_TRANSFER.equals(inputName)) {
			fireActionEvent();
		}
	}

	/**
	 * Removes an <code>ActionListener</code> from the <code>Calculator</code>.
	 * 
	 * @param l
	 *            the <code>ActionListener</code> to remove
	 */
	public void removeActionListener(ActionListener l) {
		if (!hasEventListenerList()) {
			return;
		}
		getEventListenerList().removeListener(ActionListener.class, l);
		firePropertyChange(ACTION_LISTENERS_CHANGED_PROPERTY, l, null);
	}

	/**
	 * Sets the action command which will be provided in
	 * <code>ActionEvent</code> s fired by this <code>Calculator</code>.
	 * 
	 * @param newValue
	 *            the new action command
	 */
	public void setActionCommand(String newValue) {
		setProperty(PROPERTY_ACTION_COMMAND, newValue);
	}

	/**
	 * Sets the background color of the calculator's buttons
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setButtonBackground(Color newValue) {
		setProperty(PROPERTY_BUTTON_BACKGROUND, newValue);
	}

	/**
	 * Sets the foreground color of the calculator's buttons that are operators
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setButtonOperatorForeground(Color newValue) {
		setProperty(PROPERTY_BUTTON_OPERATOR_FOREGROUND, newValue);
	}

	/**
	 * Sets the foreground color of the calculator's buttons that are numbers
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setButtonNumberForeground(Color newValue) {
		setProperty(PROPERTY_BUTTON_NUMBER_FOREGROUND, newValue);
	}
	
	/**
	 * Sets the height of the calculator's buttons
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setButtonHeight(Extent newValue) {
		setProperty(PROPERTY_BUTTON_HEIGHT, newValue);
	}

	/**
	 * Sets the width of the calculator's buttons
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setButtonWidth(Extent newValue) {
		setProperty(PROPERTY_BUTTON_WIDTH, newValue);
	}

	/**
	 * Sets the background color of the calculator's number display
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setDisplayBackground(Color newValue) {
		setProperty(PROPERTY_DISPLAY_BACKGROUND, newValue);
	}

	/**
	 * Sets the font of the calculator's number display
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setDisplayFont(Font newValue) {
		setProperty(PROPERTY_DISPLAY_FONT, newValue);
	}

	/**
	 * Sets the foreground color of the calculator's number display
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setDisplayForeground(Color newValue) {
		setProperty(PROPERTY_DISPLAY_FOREGROUND, newValue);
	}

	/**
	 * Sets the model associated with this <code>TextComponent</code>.
	 * 
	 * @param newValue
	 *            the new model (may not be null)
	 */
	public void setDocument(Document newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("Document may not be null.");
		}
		Document oldValue = getDocument();
		if (oldValue != null) {
			oldValue.removeDocumentListener(documentListener);
		}
		newValue.addDocumentListener(documentListener);
		document = newValue;
	}

	/**
	 * This method is called to determine wether the text parameter is a valid
	 * number to be placed inside the calculator or its memory.
	 * 
	 * @param numberText -
	 *            the text to check
	 * @return true if its a valid number.
	 */
	public boolean isValidNumber(String numberText) {
		try {
			NumberFormat nf = new DecimalFormat(DEFAULT_NUMBER_MASK);
			nf.parse(numberText);
			return true;
		} catch (ParseException pe) {
			return false;
		}
	}

	/**
	 * Sets the string value into the calculators memory
	 * 
	 * @param newValue
	 */
	public void setMemory(String newValue) {
		if (newValue != null && !isValidNumber(newValue)) {
			throw new IllegalArgumentException("An invalid number " + newValue + " was provided as calculator memory");
		}
		setProperty(PROPERTY_MEMORY, newValue);
	}

	/**
	 * Sets the string value for the Transfer button. If this is non-null then a
	 * Transfer button is drawn and it can be used to raise an action event for
	 * the Calculator. If it is null then it no Transfer button is drawn and no
	 * action event can be raised by the Calulator
	 * 
	 * @param newValue
	 */
	public void setTransfer(String newValue) {
		setProperty(PROPERTY_TRANSFER, newValue);
	}

	/**
	 * Sets the text of the document model of this calculaor. It will call
	 * isValidNumber first and if this returns false then an
	 * IllegalArgumentException will be thrown.
	 * 
	 * @param newValue
	 *            the new text
	 * 
	 * @throws IllegalArgumentException -
	 *             if the text cannot be converted into a number that the
	 *             calculator can handle.
	 */
	public void setText(String newValue) {
		if (newValue != null && !isValidNumber(newValue)) {
			throw new IllegalArgumentException("An invalid number " + newValue + " was provided as calculator value");
		}
		getDocument().setText(newValue);
	}

}
