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
import java.math.BigDecimal;
import java.util.EventListener;

import nextapp.echo2.app.Style;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import echopointng.model.BoundedRangeModel;
import echopointng.model.DefaultBoundedRangeModel;
import echopointng.util.ColorKit;

/**
 * <code>Slider</code> is a component the presents a handle that can be
 * dragged to move within a range of values.
 * <p>
 * The <code>BoundedRangeModel</code> is used to contain the range of integer
 * values that the Slider can move between.
 * <p>
 * Since this is integer based, you can use a valueRatio to generate a double
 * value from the <code>BoundedRangeModel</code> value, thereby allowing
 * decimal numbers to be used within a Slider.
 * 
 */
public class Slider extends AbleComponent {

	/*
	 * We pass each Change event to the listeners with the the Slider as the
	 * event source. We also update our image reference to reflect the change.
	 */
	private class InternalModelListener implements ChangeListener, Serializable {
		public void stateChanged(ChangeEvent e) {
			// fire an event to allow the property to be captured directly by
			// rendering peer
			Integer newValue = new Integer(getValue());
			firePropertyChange(VALUE_CHANGED_PROPERTY, null, newValue);
			// inform all listeners
			fireStateChanged();
		}
	}

	/**
	 * DEFAULT_HORIZONTAL_STYLE contains a default Style for a horizontal Slider
	 */
	public static final Style DEFAULT_HORIZONTAL_STYLE;

	/**
	 * DEFAULT_VERTICAL_STYLE contains a default Style for a vertical Slider
	 */
	public static final Style DEFAULT_VERTICAL_STYLE;

	/**
	 * DEFAULT_STYLE is equal to DEFAULT_HORIZONTAL_STYLE
	 */
	public static final Style DEFAULT_STYLE;

	public static final int ORIENTATION_HORIZONTAL = 0;

	public static final int ORIENTATION_VERTICAL = 1;

	public static final String PROPERTY_MODEL = "model";

	public static final String PROPERTY_ORIENTATION = "orientation";

	public static final String PROPERTY_VALUE_DECIMAL_PLACES = "valueRatio";

	public static final String PROPERTY_VALUE_RATIO = "valueRatio";

	public static final String PROPERTY_IMMEDIATE_NOTIFICATION = "immediateNotification";

	public static final String VALUE_CHANGED_PROPERTY = "value";
	static {
		MutableStyleEx style = new MutableStyleEx();
		//
		// horizontal
		//
		style.setProperty(PROPERTY_ORIENTATION, ORIENTATION_HORIZONTAL);
		style.setProperty(PROPERTY_HEIGHT, new ExtentEx("5px"));
		style.setProperty(PROPERTY_WIDTH, new ExtentEx("100px"));

		// made to look like a drop shadow area
		BorderEx railBorder = new BorderEx(1, ColorKit.clr("#9d9c99"), BorderEx.STYLE_SOLID, 1, ColorKit.clr("#eeefff"), BorderEx.STYLE_SOLID,

		1, ColorKit.clr("#9d9c99"), BorderEx.STYLE_SOLID, 1, ColorKit.clr("#eeefff"), BorderEx.STYLE_SOLID);
		style.setProperty(PROPERTY_BORDER, railBorder);
		style.setProperty(PROPERTY_BACKGROUND, ColorKit.clr("#f0ede0"));

		DEFAULT_HORIZONTAL_STYLE = style;
		DEFAULT_STYLE = DEFAULT_HORIZONTAL_STYLE;
		//
		// vertical
		//
		style = new MutableStyleEx(DEFAULT_HORIZONTAL_STYLE);

		style.setProperty(PROPERTY_ORIENTATION, ORIENTATION_VERTICAL);
		style.setProperty(PROPERTY_WIDTH, new ExtentEx("5px"));
		style.setProperty(PROPERTY_HEIGHT, new ExtentEx("100px"));

		DEFAULT_VERTICAL_STYLE = style;
	}

	/**
	 * Only one ChangeEvent is needed per instance since the event's only
	 * interesting property is the immutable source, which is the Slider.
	 */
	protected transient ChangeEvent changeEvent = null;

	protected ChangeListener changeListener = null;

	/**
	 * Constructs a <code>Slider</code> with a
	 * <code>DefaultBoundedRangeModel</code> set to zero.
	 */
	public Slider() {
		this(new DefaultBoundedRangeModel());
	}

	/**
	 * Constructs a <code>Slider</code> with the specified
	 * <code>BoundedRangeModel</code>
	 * 
	 * @param model -
	 *            the new <code>BoundedRangeModel</code>
	 */
	public Slider(BoundedRangeModel model) {
		setModel(model);
	}

	/**
	 * Constructs a <code>Slider</code> with a
	 * <code>DefaultBoundedRangeModel</code> and uses the specified Style.
	 * Typically you might use this constructor like this
	 * 
	 * <pre>
	 * Slider slider = new Slider(Slider.DEFAULT_VERTICAL_STYLE);
	 * </pre>
	 * 
	 * @param style -
	 *            the Style to use
	 */
	public Slider(Style style) {
		this();
		setStyle(style);
	}

	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		super.processInput(inputName, inputValue);
		if (VALUE_CHANGED_PROPERTY.equals(inputName)) {
			setValue(((Integer) inputValue).intValue());
		}
	}

	/**
	 * Adds a ChangeListener to the Slider.
	 * 
	 * @param l
	 *            the ChangeListener to add
	 */
	public void addChangeListener(ChangeListener l) {
		getEventListenerList().addListener(ChangeListener.class, l);
	}

	/**
	 * Subclasses that want to handle ChangeEvents differently can override this
	 * to return a subclass of ModelListener or another ChangeListener
	 * implementation.
	 */
	protected ChangeListener createChangeListener() {
		return new InternalModelListener();
	}

	/**
	 * Notify all listeners that have registered interest for notification on
	 * this event type.
	 */
	protected void fireStateChanged() {
		EventListener[] listeners = getEventListenerList().getListeners(ChangeListener.class);
		for (int index = 0; index < listeners.length; ++index) {
			if (changeEvent == null)
				changeEvent = new ChangeEvent(this);

			((ChangeListener) listeners[index]).stateChanged(changeEvent);
		}

	}

	/**
	 * Returns the model's maximum value. By default, this is <code>100</code>.
	 * 
	 * @return an int -- the model's maximum
	 */
	public int getMaximum() {
		return getModel().getMaximum();
	}

	/**
	 * Returns the model's minimum value. By default, this is <code>0</code>.
	 * 
	 */
	public int getMinimum() {
		return getModel().getMinimum();
	}

	/**
	 * Returns the data model used by the <code>Slider</code>.
	 * 
	 */
	public BoundedRangeModel getModel() {
		return (BoundedRangeModel) getProperty(PROPERTY_MODEL);
	}

	/**
	 * Returns <code>Slider.ORIENTATION_VERTICAL</code> or
	 * <code>Slider.ORIENTATION_HORIZONTAL</code>, depending on the
	 * orientation of the Slider. The default orientation is
	 * <code>ORIENTATION_HORIZONTAL</code>.
	 * 
	 */
	public int getOrientation() {
		return getProperty(PROPERTY_ORIENTATION, ORIENTATION_HORIZONTAL);
	}

	/**
	 * Returns the model's current value. The value is always between the
	 * model's minimum and maximum values, inclusive. By default, the value
	 * equals the minimum.
	 * 
	 */
	public int getValue() {
		return getModel().getValue();
	}

	/**
	 * The getDoubleValue() method allows a decimal number to be returned by the
	 * Slider when the backing model only supports integers. The number is the
	 * current getValue() * getValueRatio(), which is then rounded to
	 * getValueDecimalPlaces()
	 * 
	 * @return - getValue() * getValueRatio() rounded to getValueDecimalPlaces()
	 */
	public double getDoubleValue() {
		int value = getModel().getValue();
		double d = value * getValueRatio();
		int powersOfTen = 10 * getValueDecimalPlaces();
		if (powersOfTen != 0) {
			BigDecimal decimal = new BigDecimal(d * powersOfTen);
			BigDecimal powersOfTenDecimal = new BigDecimal(powersOfTen);
			BigDecimal result = decimal.divide(powersOfTenDecimal, BigDecimal.ROUND_HALF_DOWN);
			return result.doubleValue();
		} else {
			return new Double(d).intValue();
		}
	}

	/**
	 * @return - the value decimal places in use, which is by default 0
	 */
	public int getValueDecimalPlaces() {
		return getProperty(PROPERTY_VALUE_DECIMAL_PLACES, 0);
	}

	/**
	 * @return - the value ratio in use, which is by default 1
	 */
	public double getValueRatio() {
		return getProperty(PROPERTY_VALUE_RATIO, 1);
	}

	/**
	 * Sets the number of decimal places that will be returned in the value of
	 * the getDoubleValue() method and when displayed on the client.
	 * 
	 * @param newValue -
	 *            the number of decimal places to use
	 */
	public void setValueDecimalPlaces(int newValue) {
		setProperty(PROPERTY_VALUE_DECIMAL_PLACES, newValue);
	}

	/**
	 * Sets the ratio that will be multiplied by the current model value when
	 * its is returned by the getDoubleValue() and when displayed on the client.
	 * 
	 * @param newValue -
	 *            a new ratio to multiply the value by such as 0.01.
	 */
	public void setValueRatio(double newValue) {
		setProperty(PROPERTY_VALUE_DECIMAL_PLACES, newValue);
	}

	/**
	 * Removes a ChangeListener from the Slider.
	 * 
	 * @param l
	 *            the ChangeListener to remove
	 */
	public void removeChangeListener(ChangeListener l) {
		getEventListenerList().removeListener(ChangeListener.class, l);
	}

	/**
	 * Sets the model's maximum to <I>n </I>.
	 * 
	 * The underlying BoundedRangeModel will handle any mathematical issues
	 * arrising from assigning faulty values.
	 * <p>
	 * Notifies any listeners if the data changes.
	 * 
	 */
	public void setMaximum(int n) {
		getModel().setMaximum(n);
	}

	/**
	 * Sets the model's minimum to <I>n </I>.
	 * <p>
	 * The underlying BoundedRangeModel will handle any mathematical issues
	 * arrising from assigning faulty values.
	 * <p>
	 * Notifies any listeners if the data changes.
	 * 
	 */
	public void setMinimum(int n) {
		getModel().setMinimum(n);
	}

	/**
	 * Sets the data model used by the Slider.
	 * 
	 * @param newModel -
	 *            the new BoundedRangeModel to use
	 * 
	 * @throws IllegalArgumentException
	 *             is the model is null
	 * 
	 */
	public void setModel(BoundedRangeModel newModel) {
		if (newModel == null) {
			throw new IllegalArgumentException("The model must not be null");
		}
		BoundedRangeModel oldModel = getModel();
		setProperty(PROPERTY_MODEL, newModel);
		if (newModel != oldModel) {
			if (oldModel != null) {
				oldModel.removeChangeListener(changeListener);
				changeListener = null;
			}

			if (newModel != null) {
				changeListener = createChangeListener();
				newModel.addChangeListener(changeListener);
			}
			newModel.setExtent(0);
		}
	}

	/**
	 * Sets the Slider's orientation to <I>newOrientation </I>, which must be
	 * <code>Slider.ORIENTATION_VERTICAL</code> or
	 * <code>Slider.ORIENTATION_HORIZONTAL</code>. The default orientation is
	 * <code>ORIENTATION_HORIZONTAL</code>.
	 */
	public void setOrientation(int newOrientation) {
		if (newOrientation != ORIENTATION_HORIZONTAL && newOrientation != ORIENTATION_VERTICAL) {
			throw new IllegalArgumentException("The orientation must be either ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL");
		}
		setProperty(PROPERTY_ORIENTATION, newOrientation);
	}

	/**
	 * Sets the model's value to <I>n </I>.
	 * <p>
	 * The underlying BoundedRangeModel will handle any mathematical issues
	 * arrising from assigning faulty values.
	 * <p>
	 * Notifies any listeners if the data changes.
	 * 
	 */
	public void setValue(int n) {
		getModel().setValue(n);
	}

	/**
	 * @return true if the component will raise a change event immediately once
	 *         the the slider value changes. Set this to false if you want it to
	 *         delay the notification until another server interaction event
	 *         such as a button press.
	 */
	public boolean isImmediateNotification() {
		return getProperty(PROPERTY_IMMEDIATE_NOTIFICATION, false);
	}

	/**
	 * Set to true if the component will raise a change event immediately once
	 * the the slider value changes. Set this to false if you want it to delay
	 * the notification until another server interaction event such as a button
	 * press.
	 * 
	 * @param newValue -
	 *            true if the component will raise a change event immediately
	 *            once the the slider value changes. Set this to false if you
	 *            want it to delay the notification until another server
	 *            interaction event such as a button press.
	 */
	public void setImmediateNotification(boolean newValue) {
		setProperty(PROPERTY_IMMEDIATE_NOTIFICATION, newValue);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("w=");
		sb.append(getWidth());
		sb.append(" h=");
		sb.append(getHeight());
		sb.append(" orient=");
		sb.append((getOrientation() == ORIENTATION_HORIZONTAL ? "horz" : "vert"));
		sb.append(" val=");
		sb.append(getValue());
		sb.append(" dblval=");
		sb.append(getDoubleValue());
		return sb.toString();
	}
}
