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

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;

/**
 * <code>MutableBorderEx</code> is extension of BorderEx is no longer
 * immutable.
 * <p>
 * The more specific left, top, right and bottom border properties take
 * precedence over the general border properties, in that they are applied last
 * during rendering and are drawn so they override anything previously set by
 * the general border properties.
 * 
 * @see echopointng.BorderEx
 */

public class MutableBorderEx extends BorderEx {

	/**
	 * @see BorderEx#BorderEx()
	 */
	public MutableBorderEx() {
		super();
	}
	/**
	 * @see BorderEx#BorderEx(Color)
	 */
	public MutableBorderEx(Color color) {
		super(color);
	}
	/**
	 * @see BorderEx#BorderEx(Extent, Color, int)
	 */
	public MutableBorderEx(Extent size, Color color, int style) {
		super(size, color, style);
	}
	/**
	 * @see BorderEx#BorderEx(Extent, Color, int, Extent, Color, int, Extent, Color, int, Extent, Color, int)
	 */
	public MutableBorderEx(Extent leftSize, Color leftColor, int leftStyle, Extent rightSize, Color rightColor, int rightStyle, Extent topSize,
			Color topColor, int topStyle, Extent bottomSize, Color bottomColor, int bottomStyle) {
		super(leftSize, leftColor, leftStyle, rightSize, rightColor, rightStyle, topSize, topColor, topStyle, bottomSize, bottomColor, bottomStyle);
	}
	/**
	 * @see BorderEx#BorderEx(int)
	 */
	public MutableBorderEx(int size) {
		super(size);
	}
	/**
	 * @see BorderEx#BorderEx(int, Color)
	 */
	public MutableBorderEx(int size, Color color) {
		super(size, color);
	}
	/**
	 * @see BorderEx#BorderEx(int, Color, int)
	 */
	public MutableBorderEx(int size, Color color, int style) {
		super(size, color, style);
	}
	/**
	 * @see BorderEx#BorderEx(int, Color, int, int, Color, int, int, Color, int, int, Color, int)
	 */
	public MutableBorderEx(int leftSize, Color leftColor, int leftStyle, int rightSize, Color rightColor, int rightStyle, int topSize,
			Color topColor, int topStyle, int bottomSize, Color bottomColor, int bottomStyle) {
		super(leftSize, leftColor, leftStyle, rightSize, rightColor, rightStyle, topSize, topColor, topStyle, bottomSize, bottomColor, bottomStyle);
	}
	/**
	 * Sets all <code>MutableBorderEx</code> properties.
	 * 
	 * @param size - is the size of the border 
	 * @param color - is the color of the border
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setAll(Extent size, Color color, int style) {
		super.setAll(size, color, style);
	}
	/**
	 * Sets all <code>MutableBorderEx</code> properties.
	 * 
	 * @param size - is the size of the border in pixels
	 * @param color - is the color of the border
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setAll(int size, Color color, int style) {
		super.setAll(size, color, style);
	}
	/**
	 * Sets the bottom <code>MutableBorderEx</code> properties.
	 * 
	 * @param size - is the size of the border 
	 * @param color - is the color of the border
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setBottom(Extent size, Color color, int style) {
		super.setBottom(size, color, style);
	}
	/**
	 * Sets the bottom <code>MutableBorderEx</code> properties.
	 * 
	 * @param size - is the size of the border in pixels
	 * @param color - is the color of the border
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setBottom(int size, Color color, int style) {
		super.setBottom(size, color, style);
	}
	/**
	 * Sets the bottom <code>MutableBorderEx</code> color property.
	 * 
	 * @param color - is the color of the border
	 */
	public void setBottomColor(Color color) {
		super.setBottomColor(color);
	}
	/**
	 * Sets the bottom <code>MutableBorderEx</code> size property.
	 * 
	 * @param size - is the size of the border 
	 */
	public void setBottomSize(Extent size) {
		super.setBottomSize(size);
	}
	/**
	 * Sets the bottom <code>MutableBorderEx</code> style property.
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setBottomStyle(int style) {
		super.setBottomStyle(style);
	}
	/**
	 * Sets the color <code>MutableBorderEx</code> properties.
	 * 
	 * @param color - is the color of the border
	 */
	public void setColor(Color color) {
		super.setColor(color);
	}
	/**
	 * Sets the left <code>MutableBorderEx</code> properties.
	 * 
	 * @param size - is the size of the border 
	 * @param color - is the color of the border
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setLeft(Extent size, Color color, int style) {
		super.setLeft(size, color, style);
	}
	/**
	 * Sets the left <code>MutableBorderEx</code> properties.
	 * 
	 * @param size - is the size of the border in pixels
	 * @param color - is the color of the border
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setLeft(int size, Color color, int style) {
		super.setLeft(size, color, style);
	}
	/**
	 * Sets the left <code>MutableBorderEx</code> color property.
	 * 
	 * @param color - is the color of the border
	 */
	public void setLeftColor(Color color) {
		super.setLeftColor(color);
	}
	/**
	 * Sets the left <code>MutableBorderEx</code> size property.
	 * 
	 * @param size - is the size of the border 
	 */
	public void setLeftSize(Extent size) {
		super.setLeftSize(size);
	}
	/**
	 * Sets the left <code>MutableBorderEx</code> style property.
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setLeftStyle(int style) {
		super.setLeftStyle(style);
	}
	/**
	 * Sets the right <code>MutableBorderEx</code> properties.
	 * 
	 * @param size - is the size of the border 
	 * @param color - is the color of the border
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setRight(Extent size, Color color, int style) {
		super.setRight(size, color, style);
	}
	/**
	 * Sets the right <code>MutableBorderEx</code> properties.
	 * 
	 * @param size - is the size of the border in pixels
	 * @param color - is the color of the border
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setRight(int size, Color color, int style) {
		super.setRight(size, color, style);
	}
	/**
	 * Sets the right <code>MutableBorderEx</code> color property.
	 * @param color - is the color of the border
	 */
	public void setRightColor(Color color) {
		super.setRightColor(color);
	}
	/**
	 * Sets the right <code>MutableBorderEx</code> right property.
	 * 
	 * @param size - is the size of the border 
	 */
	public void setRightSize(Extent size) {
		super.setRightSize(size);
	}
	/**
	 * Sets the right <code>MutableBorderEx</code> style property.
	 * 
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setRightStyle(int style) {
		super.setRightStyle(style);
	}
	/**
	 * Sets the <code>MutableBorderEx</code> size properties.
	 * 
	 * @param size - is the size of the border 
	 */
	public void setSize(Extent size) {
		super.setSize(size);
	}
	/**
	 * Sets the <code>MutableBorderEx</code> style properties.
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setStyle(int style) {
		super.setStyle(style);
	}
	/**
	 * Sets the top <code>MutableBorderEx</code> properties.
	 * 
	 * @param size - is the size of the border 
	 * @param color - is the color of the border
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setTop(Extent size, Color color, int style) {
		super.setTop(size, color, style);
	}
	/**
	 * Sets the top <code>MutableBorderEx</code> properties.
	 * 
	 * @param size - is the size of the border in pixels
	 * @param color - is the color of the border
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setTop(int size, Color color, int style) {
		super.setTop(size, color, style);
	}
	/**
	 * Sets the top <code>MutableBorderEx</code> color property.
	 * 
	 * @param color - is the color of the border
	 */
	public void setTopColor(Color color) {
		super.setTopColor(color);
	}
	/**
	 * Sets the top <code>MutableBorderEx</code> size property.
	 * 
	 * @param size - is the size of the border 
	 */
	public void setTopSize(Extent size) {
		super.setTopSize(size);
	}
	/**
	 * Sets the top <code>MutableBorderEx</code> style property.
	 * 
	 * @param style -
	 *            the style, one of the following values:
	 *            <ul>
	 *            <li><code>STYLE_NONE</code></li>
	 *            <li><code>STYLE_SOLID</code></li>
	 *            <li><code>STYLE_INSET</code></li>
	 *            <li><code>STYLE_OUTSET</code></li>
	 *            <li><code>STYLE_GROOVE</code></li>
	 *            <li><code>STYLE_RIDGE</code></li>
	 *            <li><code>STYLE_DOUBLE</code></li>
	 *            <li><code>STYLE_DOTTED</code></li>
	 *            <li><code>STYLE_DASHED</code></li>
	 *            </ul>
	 * 
	 */
	public void setTopStyle(int style) {
		super.setTopStyle(style);
	}
}
