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

import nextapp.echo2.app.event.ChangeListener;

/**
 * Defines the data model used by components like ProgressBars and Sliders.
 * <p>
 * Defines four interrelated integer properties: minimum, maximum, extent and
 * value. These four integers define two nested ranges like this: <blockquote>
 * 
 * <pre>
 * minimum &lt;= value &lt;= value + extent &lt;= maximum
 * </pre>
 * 
 * </blockquote> The outer range is <code>minimum,maximum</code> and the inner
 * range is <code>value,value+extent</code>. The inner range must lie within
 * the outer one, i.e. <code>value</code> must be less than or equal to
 * <code>maximum</code> and <code>value+extent</code> must greater than or
 * equal to <code>minimum</code>, and <code>maximum</code> must be greater
 * than or equal to <code>minimum</code>.
 * 
 * <ul>
 * <li>The minimum and maximum set methods "correct" the other three properties
 * to acommodate their new value argument. For example setting the model's
 * minimum may change its maximum, value, and extent properties (in that order),
 * to maintain the constraints specified above.
 * 
 * <li>The value and extent set methods "correct" their argument to fit within
 * the limits defined by the other three properties. For example if
 * <code>value == maximum</code>,<code>setExtent(10)</code> would change
 * the extent (back) to zero.
 * 
 * </ul>
 *  
 */
public interface BoundedRangeModel {
	/**
	 * Adds a ChangeListener to the model's listener list.
	 * 
	 * @param cl
	 *            the ChangeListener to add
	 */
	public void addChangeListener(ChangeListener cl);

	/**
	 * Returns the model's extent, the length of the inner range that begins at
	 * the model's value.
	 * 
	 * @return the value of the model's extent property
	 */
	public int getExtent();

	/**
	 * Returns the model's maximum. Note that the upper limit on the model's
	 * value is (maximum - extent).
	 * 
	 * @return the value of the maximum property.
	 */
	public int getMaximum();

	/**
	 * Returns the minimum acceptable value.
	 * 
	 * @return the value of the minimum property
	 */
	public int getMinimum();

	/**
	 * Returns the model's current value. Note that the upper limit on the
	 * model's value is <code>maximum - extent</code> and the lower limit is
	 * <code>minimum</code>.
	 *  
	 */
	public int getValue();

	/**
	 * Returns true if the current changes to the value property are part of a
	 * series of changes.
	 *  
	 */
	public boolean getValueIsAdjusting();

	/**
	 * Removes a ChangeListener from the model's listener list.
	 *  
	 */
	public void removeChangeListener(ChangeListener cl);

	/**
	 * Sets the model's extent. The <I>newExtent </I> is forced to be greater
	 * than or equal to zero and less than or equal to maximum - value.
	 * <p>
	 * When a BoundedRange model is used with a scrollbar the extent defines the
	 * length of the scrollbar knob (aka the "thumb" or "elevator"). The extent
	 * usually represents how much of the object being scrolled is visible. When
	 * used with a slider, the extent determines how much the value can "jump",
	 * for example when the user presses PgUp or PgDn.
	 * <p>
	 * Notifies any listeners if the model changes.
	 *  
	 */
	public void setExtent(int newExtent);

	/**
	 * Sets the model's maximum to <I>newMaximum </I>. The other three
	 * properties may be changed as well, to ensure that <blockquote>
	 * 
	 * <pre>
	 * minimum &lt;= value &lt;= value + extent &lt;= maximum
	 * </pre>
	 * 
	 * </blockquote>
	 * <p>
	 * Notifies any listeners if the model changes.
	 *  
	 */
	public void setMaximum(int newMaximum);

	/**
	 * Sets the model's minimum to <I>newMinimum </I>. The other three
	 * properties may be changed as well, to ensure that: <blockquote>
	 * 
	 * <pre>
	 * minimum &lt;= value &lt;= value + extent &lt;= maximum
	 * </pre>
	 * 
	 * </blockquote>
	 * <p>
	 * Notifies any listeners if the model changes.
	 *  
	 */
	public void setMinimum(int newMinimum);

	/**
	 * This method sets all of the model's data with a single method call. The
	 * method results in a single change event being generated. This is
	 * convenient when you need to adjust all the model data simulaneously and
	 * do not want individual change events to occur.
	 *  
	 */
	public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting);

	/**
	 * Sets the model's current value to <code>newValue</code> if
	 * <code>newValue</code> satisfies the model's constraints. Those
	 * constraints are: <blockquote>
	 * 
	 * <pre>
	 * minimum &lt;= value &lt;= value + extent &lt;= maximum
	 * </pre>
	 * 
	 * </blockquote> Otherwise, if <code>newValue</code> is less than
	 * <code>minimum</code> it's set to <code>minimum</code>, if its
	 * greater than <code>maximum</code> then it's set to <code>maximum</code>,
	 * and if it's greater than <code>value+extent</code> then it's set to
	 * <code>value+extent</code>.
	 * <p>
	 * When a BoundedRange model is used with a scrollbar the value specifies
	 * the origin of the scrollbar knob (aka the "thumb" or "elevator"). The
	 * value usually represents the origin of the visible part of the object
	 * being scrolled.
	 * <p>
	 * Notifies any listeners if the model changes.
	 *  
	 */
	public void setValue(int newValue);

	/**
	 * This attribute indicates that any upcoming changes to the value of the
	 * model should be considered a single event. This attribute will be set to
	 * true at the start of a series of changes to the value, and will be set to
	 * false when the value has finished changing. Normally this allows a
	 * listener to only take action when the final value change in committed,
	 * instead of having to do updates for all intermediate values.
	 * <p>
	 * 
	 * @param b
	 *            true if the upcoming changes to the value property are part of
	 *            a series
	 */
	public void setValueIsAdjusting(boolean b);
}
