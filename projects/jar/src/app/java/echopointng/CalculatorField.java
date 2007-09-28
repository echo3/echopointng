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

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.DocumentEvent;
import nextapp.echo2.app.event.DocumentListener;
import nextapp.echo2.app.text.Document;
import nextapp.echo2.app.text.StringDocument;
import echopointng.util.ColorKit;

/**
 * <code>CalculatorField</code> is a drop down component that contains a text
 * field and a drop down Calculator. The text field is updated with the contents
 * of the calculator when the Transfer button is pressed or other server
 * interactions happen.
 * <p>
 * The TextField and the Calculator shared the same underlying Document.
 */
public class CalculatorField extends DropDown {

	/**
	 * The default appearance of the TextField component with the CalculatorField
	 */
	public static final Style DEFAULT_TEXT_FIELD_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(TextField.PROPERTY_BORDER, new BorderEx(0, null, BorderEx.STYLE_NONE));
		DEFAULT_TEXT_FIELD_STYLE = style;
	}

	/**
	 * The default appearance of the Calculator component with the CalculatorField
	 */
	public static final Style DEFAULT_CALCULATOR_FIELD_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx(Calculator.DEFAULT_STYLE);
		style.setProperty(TextField.PROPERTY_BORDER, new BorderEx(0, null, BorderEx.STYLE_NONE));
		DEFAULT_CALCULATOR_FIELD_STYLE = style;
	}
	
	/**
	 * The default appearance of the Calculator 
	 */
	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx(DropDown.DEFAULT_STYLE);
		style.setProperty(PROPERTY_POPUP_BACKGROUND, ColorKit.clr("#ECE9D8"));
		DEFAULT_STYLE = style;
	}
	
	private class InternalListener implements ActionListener, DocumentListener  {
		/**
		 * @see nextapp.echo2.app.event.ActionListener#actionPerformed(nextapp.echo2.app.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			setExpanded(false);
		}
		
		/**
		 * @see nextapp.echo2.app.event.DocumentListener#documentUpdate(nextapp.echo2.app.event.DocumentEvent)
		 */
		public void documentUpdate(DocumentEvent e) {
			firePropertyChange(Calculator.TEXT_CHANGED_PROPERTY,null,null);
		}		
	};
	
	private InternalListener internalListener = new InternalListener();
	
	/**
	 * Constructs a <code>CalculatorField</code> with a
	 * value of zero.
	 */
	public CalculatorField() {
		this(0);
	}

	/**
	 * Constructs a <code>CalculatorField</code> with the
	 * specified value
	 */
	public CalculatorField(double value) {
		StringDocument document = new StringDocument();
		document.addDocumentListener(internalListener);
		
		TextField textField = new TextField(document);
		
		Calculator calculator = new Calculator(document);
		calculator.setTransfer("OK");
		calculator.addActionListener(internalListener);
		
		setTarget(textField);
		setPopUp(calculator);
		
		textField.setStyle(DEFAULT_TEXT_FIELD_STYLE);
		calculator.setStyle(DEFAULT_CALCULATOR_FIELD_STYLE);
		
		setValue(value);
	}
	
	/**
	 * @return returns the value of the <code>CalculatorField</code>.
	 * 
	 * @exception NumberFormatException
	 *                if the string does not contain a parsable
	 *                <code>double</code>.
	 */
	public double getValue() {
		Component popup = getPopUp();
		if (popup instanceof Calculator) {
			String text = ((Calculator) popup).getText();
			return Double.parseDouble(text);
		}
		return Float.NaN;
	}

	/**
	 * Sets the value of the <code>CalculatorField</code> to be the given
	 * double. The Calculator precision rules will apply.
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setValue(double newValue) {
		Component popup = getPopUp();
		if (popup instanceof Calculator) {
			((Calculator) popup).setText(String.valueOf(newValue));
		}
	}

	/**
	 * @return Returns the underlying TextField.
	 */
	public TextField getTextField() {
		return (TextField) getTarget();
	}

	/**
	 * @return Returns the underlying Calculator.
	 */
	public Calculator getCalculator() {
		return (Calculator) getPopUp();
	}

	/**
	 * Sets the underlying TextField
	 * 
	 * @param newValue -
	 *            The textField to set.
	 */
	public void setTextField(TextField newValue) {
		if (newValue == null || newValue.getDocument() == null) {
			throw new IllegalArgumentException("The TextField and its Document must be non null.");
		}
		Document document = newValue.getDocument();
		setTarget(newValue);
		setDocument(document);
	}

	/**
	 * Sets the underlying TextField
	 * 
	 * @param newValue -
	 *            The textField to set.
	 */
	public void setCalculator(Calculator newValue) {
		if (newValue == null || newValue.getDocument() == null) {
			throw new IllegalArgumentException("The Calculator and its Document must be non null.");
		}
		Calculator oldValue = getCalculator();
		if (oldValue != null) {
			oldValue.removeActionListener(internalListener);
		}
		newValue.addActionListener(internalListener);
		
		Document document = newValue.getDocument();
		setPopUp(newValue);
		setDocument(document);
	}

	/**
	 * Returns the underlying Document of the CalculatorField
	 * 
	 * @return the underlying Document of the CalculatorField
	 */
	public Document getDocument() {
		if (getTextField() != null && getTextField().getDocument() != null) {
			return getTextField().getDocument();
		} else if (getCalculator() != null && getCalculator().getDocument() != null) {
			return getCalculator().getDocument();
		}
		return null;
	}

	/**
	 * Sets the the underlying Document of the CalculatorField.  This will change the
	 * document on the underlying TextField and Calculator
	 * 
	 * @param newValue -
	 *            the new underlying Document of the CalculatorField
	 * @throws IllegalArgumentException - if the new Document is null
	 */
	public void setDocument(Document newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("The Document must not be null!");
		}
		Document oldValue = getDocument();
		if (oldValue != null) {
			oldValue.removeDocumentListener(internalListener);
		}
		newValue.addDocumentListener(internalListener);
		if (getTextField() != null) {
			getTextField().setDocument(newValue);
		}
		if (getCalculator() != null) {
			getCalculator().setDocument(newValue);
		}
	}

}
