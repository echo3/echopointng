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
/*
 * The design paradigm and class name used within have been taken directly from
 * the java.swing package has been retro-fitted to work with the NextApp Echo web framework.
 *
 * This file was made part of the EchoPoint project on the 25/07/2002.
 *
 */

import java.io.Serializable;
import java.util.EventListener;

import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.event.EventListenerList;

/**
 * A generic implementation of BoundedRangeModel.
 */
public class DefaultBoundedRangeModel implements BoundedRangeModel, Serializable {
	/**
	 * Only one ChangeEvent is needed per model instance since the event's only
	 * (read-only) state is the source property. The source of events generated
	 * here is always "this".
	 */
	protected transient ChangeEvent changeEvent = null;

	/** The listeners waiting for model changes. */
	protected EventListenerList listenerList = new EventListenerList();

	private int value = 0;

	private int extent = 0;

	private int min = 0;

	private int max = 100;

	private boolean isAdjusting = false;

	/**
	 * Initializes all of the properties with default values. Those values are:
	 * <ul>
	 * <li><code>value</code>= 0
	 * <li><code>extent</code>= 0
	 * <li><code>minimum</code>= 0
	 * <li><code>maximum</code>= 100
	 * <li><code>adjusting</code>= false
	 * </ul>
	 */
	public DefaultBoundedRangeModel() {
	}

	/**
	 * Initializes value, extent, minimum and maximum. Adjusting is false.
	 * Throws an IllegalArgumentException if the following constraints aren't
	 * satisfied: <blockquote>
	 * 
	 * <pre>
	 * min &lt;= value &lt;= value + extent &lt;= max
	 * </pre>
	 * 
	 * </blockquote>
	 */
	public DefaultBoundedRangeModel(int value, int extent, int min, int max) {
		if ((max >= min) && (value >= min) && ((value + extent) >= value) && ((value + extent) <= max)) {
			this.value = value;
			this.extent = extent;
			this.min = min;
			this.max = max;
		} else {
			throw new IllegalArgumentException("invalid range properties");
		}
	}

	/**
	 * Adds a <code>ChangeListener</code> to the button.
	 * 
	 * @param l
	 *            The <code>ChangeListener</code> to be added.
	 */
	public void addChangeListener(ChangeListener l) {
		listenerList.addListener(ChangeListener.class, l);
	}

	/**
	 * Notifies all listeners that have registered for this event type.
	 */
	public void fireStateChanged() {
		EventListener[] listeners = listenerList.getListeners(ChangeListener.class);
		for (int index = 0; index < listeners.length; ++index) {
			if (changeEvent == null) {
				changeEvent = new ChangeEvent(this);
			}
			((ChangeListener) listeners[index]).stateChanged(changeEvent);
		}
	}

	/**
	 * Return the model's extent.
	 * 
	 * @return the model's extent
	 */
	public int getExtent() {
		return extent;
	}

	/**
	 * Return the model's maximum.
	 * 
	 * @return the model's maximum
	 */
	public int getMaximum() {
		return max;
	}

	/**
	 * Return the model's minimum.
	 */
	public int getMinimum() {
		return min;
	}

	/**
	 * Return the model's current value.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns true if the value is in the process of changing as a result of
	 * actions being taken by the user.
	 *  
	 */
	public boolean getValueIsAdjusting() {
		return isAdjusting;
	}

	/**
	 * Removes a <code>ChangeListener</code> from the button.
	 * 
	 * @param l
	 *            The <code>ChangeListener</code> to be removed.
	 */
	public void removeChangeListener(ChangeListener l) {
		listenerList.removeListener(ChangeListener.class, l);
	}

	/**
	 * Sets the extent to <I>n </I> after ensuring that <I>n </I> is greater
	 * than or equal to zero and falls within the model's constraints:
	 * <blockquote>
	 * 
	 * <pre>
	 * minimum &lt;= value &lt;= value + extent &lt;= maximum
	 * </pre>
	 * 
	 * </blockquote>
	 */
	public void setExtent(int n) {
		int newExtent = Math.max(0, n);
		if (value + newExtent > max) {
			newExtent = max - value;
		}
		setRangeProperties(value, newExtent, min, max, isAdjusting);
	}

	/**
	 * Sets the maximum to <I>n </I> after ensuring that <I>n </I> that the
	 * other three properties obey the model's constraints: <blockquote>
	 * 
	 * <pre>
	 * minimum &lt;= value &lt;= value + extent &lt;= maximum
	 * </pre>
	 * 
	 * </blockquote>
	 */
	public void setMaximum(int n) {
		int newMin = Math.min(n, min);
		int newValue = Math.min(n, value);
		int newExtent = Math.min(n - newValue, extent);

		setRangeProperties(newValue, newExtent, newMin, n, isAdjusting);
	}

	/**
	 * Sets the minimum to <I>n </I> after ensuring that <I>n </I> that the
	 * other three properties obey the model's constraints: <blockquote>
	 * 
	 * <pre>
	 * minimum &lt;= value &lt;= value + extent &lt;= maximum
	 * </pre>
	 * 
	 * </blockquote>
	 */
	public void setMinimum(int n) {
		int newMax = Math.max(n, max);
		int newValue = Math.max(n, value);
		int newExtent = Math.min(newMax - newValue, extent);
		setRangeProperties(newValue, newExtent, n, newMax, isAdjusting);
	}

	/**
	 * Sets all of the BoundedRangeModel properties after forcing the arguments
	 * to obey the usual constraints: <blockquote>
	 * 
	 * <pre>
	 * minimum &lt;= value &lt;= value + extent &lt;= maximum
	 * </pre>
	 * 
	 * </blockquote>
	 * <p>
	 * At most, one ChangeEvent is generated.
	 *  
	 */
	public void setRangeProperties(int newValue, int newExtent, int newMin, int newMax, boolean adjusting) {
		if (newMin > newMax) {
			newMin = newMax;
		}
		if (newValue > newMax) {
			newMax = newValue;
		}
		if (newValue < newMin) {
			newMin = newValue;
		}
		if (((long) newExtent + (long) newValue) > newMax) {
			newExtent = newMax - newValue;
		}

		if (newExtent < 0) {
			newExtent = 0;
		}

		boolean isChange = (newValue != value) || (newExtent != extent) || (newMin != min) || (newMax != max) || (adjusting != isAdjusting);

		if (isChange) {
			value = newValue;
			extent = newExtent;
			min = newMin;
			max = newMax;
			isAdjusting = adjusting;

			fireStateChanged();
		}
	}

	/**
	 * Sets the current value of the model. For a slider, that determines where
	 * the knob appears. Ensures that the new value, <I>n </I> falls within the
	 * model's constraints: <blockquote>
	 * 
	 * <pre>
	 * minimum &lt;= value &lt;= value + extent &lt;= maximum
	 * </pre>
	 * 
	 * </blockquote>
	 *  
	 */
	public void setValue(int n) {
		int newValue = Math.max(n, min);
		if (newValue + extent > max) {
			newValue = max - extent;
		}
		setRangeProperties(newValue, extent, min, max, isAdjusting);
	}

	/**
	 * Sets the valueIsAdjusting property.
	 *  
	 */
	public void setValueIsAdjusting(boolean b) {
		setRangeProperties(value, extent, min, max, b);
	}

	/**
	 * Returns a string that displays all of the BoundedRangeModel properties.
	 */
	public String toString() {
		String modelString = "value=" + getValue() + ", " + "extent=" + getExtent() + ", " + "min=" + getMinimum() + ", " + "max=" + getMaximum()
				+ ", " + "adj=" + getValueIsAdjusting();

		return getClass().getName() + "[" + modelString + "]";
	}
}
