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

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import echopointng.util.ColorKit;

/**
 * <code>BorderEx</code> is extension of Border that allows you to set left,
 * right, top and bottom border values.
 * <p>
 * The more specific left, top, right and bottom border properties take
 * precedence over the general border properties, in that they are applied last
 * during rendering and are drawn so they override anything previously set by
 * the general border properties.
 */

public class BorderEx extends Border {

	/**
	 * A static BorderEx that is 1px wide, BLACK and STYLE_SOLID.
	 */
	public static final BorderEx DEFAULT = new BorderEx(1, Color.BLACK, STYLE_SOLID);

	/**
	 * A static BorderEx that is zero sized and STYLE_NONE.
	 */
	public static final BorderEx NONE = new BorderEx(0, Color.BLACK, STYLE_NONE);

	private Color bottomColor;

	private Extent bottomSize;

	private int bottomStyle;

	private Color color;

	private Color leftColor;

	private Extent leftSize;

	private int leftStyle;

	private Color rightColor;

	private Extent rightSize;

	private int rightStyle;

	private Extent size;

	private int style;

	private Color topColor;

	private Extent topSize;

	private int topStyle;

	/**
	 * Creates a new <code>BorderEx</code> with 1px wide, STYLE_SOLID style
	 * and Color.BLACK.
	 */
	public BorderEx() {
		this(1, Color.BLACK, STYLE_SOLID);
	}

	/**
	 * Creates a new <code>BorderEx</code> with a 1px wide STYLE_SOLID style.
	 * 
	 * @param color
	 *            the color of the border
	 */
	public BorderEx(Color color) {
		this(1, color, STYLE_SOLID);
	}

	/**
	 * Creates a new <code>BorderEx</code>.
	 * 
	 * @param size
	 *            the size of the border
	 * @param color
	 *            the color of the border
	 * @param style
	 *            the style of the border, one of the following constant values:
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
	 */
	public BorderEx(Extent size, Color color, int style) {
		super(null, null, 0);
		setSize(size);
		setColor(color);
		setStyle(style);
	}

	/**
	 * Creates a new <code>BorderEx</code> with the specified left, right, top
	 * an bottom properties.
	 * 
	 * @param leftSize -
	 *            the size of the left side of the border
	 * @param leftColor -
	 *            the color of the left side of the border
	 * @param leftStyle -
	 *            the style of the left side of the border
	 * @param rightSize -
	 *            the size of the right side of the border
	 * @param rightColor -
	 *            the color of the right side of the border
	 * @param rightStyle -
	 *            the style of the right side of the border
	 * @param topSize -
	 *            the size of the top side of the border
	 * @param topColor -
	 *            the color of the top side of the border
	 * @param topStyle -
	 *            the style of the top side of the border
	 * @param bottomSize -
	 *            the size of the bottom side of the border
	 * @param bottomColor -
	 *            the color of the bottom side of the border
	 * @param bottomStyle -
	 *            the style of the bottom side of the border
	 * 
	 * <p>
	 * Styles can be one of the following constant values:
	 * <ul>
	 * <li><code>STYLE_NONE</code></li>
	 * <li><code>STYLE_SOLID</code></li>
	 * <li><code>STYLE_INSET</code></li>
	 * <li><code>STYLE_OUTSET</code></li>
	 * <li><code>STYLE_GROOVE</code></li>
	 * <li><code>STYLE_RIDGE</code></li>
	 * <li><code>STYLE_DOUBLE</code></li>
	 * <li><code>STYLE_DOTTED</code></li>
	 * <li><code>STYLE_DASHED</code></li>
	 * </ul>
	 */
	public BorderEx(Extent leftSize, Color leftColor, int leftStyle, Extent rightSize, Color rightColor, int rightStyle, Extent topSize,
			Color topColor, int topStyle, Extent bottomSize, Color bottomColor, int bottomStyle) {
		super(null, null, 0);

		setLeftColor(leftColor);
		setLeftSize(leftSize);
		setLeftStyle(leftStyle);

		setRightColor(rightColor);
		setRightSize(rightSize);
		setRightStyle(rightStyle);

		setTopColor(topColor);
		setTopSize(topSize);
		setTopStyle(topStyle);

		setBottomColor(bottomColor);
		setBottomSize(bottomSize);
		setBottomStyle(bottomStyle);
	}

	/**
	 * Creates a new <code>BorderEx</code> with a STYLE_SOLID style and
	 * Color.BLACK color.
	 * 
	 * @param size
	 *            the size of the border in pixels
	 */
	public BorderEx(int size) {
		this(size, Color.BLACK, STYLE_SOLID);
	}

	/**
	 * Creates a new <code>BorderEx</code> with a STYLE_SOLID style.
	 * 
	 * @param size
	 *            the size of the border in pixels
	 * @param color
	 *            the color of the border
	 */
	public BorderEx(int size, Color color) {
		this(size, color, STYLE_SOLID);
	}

	/**
	 * Creates a new <code>BorderEx</code>.
	 * 
	 * @param size
	 *            the size of the border in pixels
	 * @param color
	 *            the color of the border
	 * @param style
	 *            the style of the border, one of the following constant values:
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
	 */
	public BorderEx(int size, Color color, int style) {
		this(new Extent(size), color, style);
	}

	/**
	 * Creates a new <code>BorderEx</code> with the specified left, right, top
	 * an bottom properties..
	 * 
	 * @param leftSize -
	 *            the size of the left side of the border
	 * @param leftColor -
	 *            the color of the left side of the border
	 * @param leftStyle -
	 *            the style of the left side of the border
	 * @param rightSize -
	 *            the size of the right side of the border
	 * @param rightColor -
	 *            the color of the right side of the border
	 * @param rightStyle -
	 *            the style of the right side of the border
	 * @param topSize -
	 *            the size of the top side of the border
	 * @param topColor -
	 *            the color of the top side of the border
	 * @param topStyle -
	 *            the style of the top side of the border
	 * @param bottomSize -
	 *            the size of the bottom side of the border
	 * @param bottomColor -
	 *            the color of the bottom side of the border
	 * @param bottomStyle -
	 *            the style of the bottom side of the border
	 * 
	 * <p>
	 * Styles can be one of the following constant values:
	 * <ul>
	 * <li><code>STYLE_NONE</code></li>
	 * <li><code>STYLE_SOLID</code></li>
	 * <li><code>STYLE_INSET</code></li>
	 * <li><code>STYLE_OUTSET</code></li>
	 * <li><code>STYLE_GROOVE</code></li>
	 * <li><code>STYLE_RIDGE</code></li>
	 * <li><code>STYLE_DOUBLE</code></li>
	 * <li><code>STYLE_DOTTED</code></li>
	 * <li><code>STYLE_DASHED</code></li>
	 * </ul>
	 */

	public BorderEx(int leftSize, Color leftColor, int leftStyle, int rightSize, Color rightColor, int rightStyle, int topSize, Color topColor,
			int topStyle, int bottomSize, Color bottomColor, int bottomStyle) {
		this(new Extent(leftSize), leftColor, leftStyle, new Extent(rightSize), rightColor, rightStyle, new Extent(topSize), topColor, topStyle,
				new Extent(bottomSize), bottomColor, bottomStyle);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Border)) {
			return false;
		}
		boolean same = false;
		if (o instanceof BorderEx) {
			BorderEx that = (BorderEx) o;
			same = equals(this.leftColor, that.leftColor) && equals(this.rightColor, that.rightColor) && equals(this.topColor, that.topColor)
					&& equals(this.bottomColor, that.bottomColor) &&

					equals(this.leftSize, that.leftSize) && equals(this.rightSize, that.rightSize) && equals(this.topSize, that.topSize)
					&& equals(this.bottomSize, that.bottomSize) &&

					this.leftStyle == that.leftStyle && this.rightStyle == that.rightStyle && this.topStyle == that.topStyle
					&& this.bottomStyle == that.bottomStyle;

		} else {
			Border that = (Border) o;
			//
			// this BorderEx must have all the same l,r,t,b values in
			// order to be compared
			Color clr = this.leftColor;
			Extent size = this.leftSize;
			int style = this.leftStyle;

			if (equals(clr, this.rightColor) && equals(clr, this.topColor) && equals(clr, this.bottomColor) && equals(size, this.rightSize)
					&& equals(size, this.topSize) && equals(size, this.bottomSize) && style == this.rightStyle && style == this.topStyle
					&& style == this.bottomStyle) {

				same = equals(this.getColor(), that.getColor()) && equals(this.getSize(), that.getSize()) && this.getStyle() == that.getStyle();
			}
		}
		return same;
	}

	private boolean equals(Object o1, Object o2) {
		if (o1 == o2)
			return true;
		if (o1 != null)
			return o1.equals(o2);
		return false;
	}

	/* ======================================== */

	public Color getBottomColor() {
		return bottomColor;
	}

	public Extent getBottomSize() {
		return bottomSize;
	}

	public int getBottomStyle() {
		return bottomStyle;
	}

	/* ======================================== */
	/**
	 * @see Border#getColor()
	 */
	public Color getColor() {
		return color;
	}

	/* ======================================== */

	public Color getLeftColor() {
		return leftColor;
	}

	public Extent getLeftSize() {
		return leftSize;
	}

	public int getLeftStyle() {
		return leftStyle;
	}

	/* ======================================== */

	public Color getRightColor() {
		return rightColor;
	}

	public Extent getRightSize() {
		return rightSize;
	}

	public int getRightStyle() {
		return rightStyle;
	}

	/**
	 * @see Border#getSize()()()
	 */
	public Extent getSize() {
		return size;
	}

	/**
	 * @see Border#getStyle()()
	 */
	public int getStyle() {
		return style;
	}

	/**
	 * Returns the string representation of the current border style eg.
	 * "solid", "dotted" etc.
	 * 
	 * @return the string representation of the current border style
	 */
	public String getStyleString() {
		switch (style) {
		case Border.STYLE_NONE:
			return "none";
		case Border.STYLE_INSET:
			return "inset";
		case Border.STYLE_OUTSET:
			return "outset";
		case Border.STYLE_SOLID:
			return "solid";
		case Border.STYLE_DOTTED:
			return "dotted";
		case Border.STYLE_DASHED:
			return "dashed";
		case Border.STYLE_GROOVE:
			return "groove";
		case Border.STYLE_RIDGE:
			return "ridge";
		case Border.STYLE_DOUBLE:
			return "double";
		default:
			return "none";
		}
	}

	/* ======================================== */

	public Color getTopColor() {
		return topColor;
	}

	public Extent getTopSize() {
		return topSize;
	}

	public int getTopStyle() {
		return topStyle;
	}

	void setAll(Extent size, Color color, int style) {
		setLeft(size, color, style);
		setRight(size, color, style);
		setTop(size, color, style);
		setBottom(size, color, style);
	}

	void setAll(int size, Color color, int style) {
		setAll(new Extent(size), color, style);
	}

	void setBottom(Extent size, Color color, int style) {
		setBottomSize(size);
		setBottomColor(color);
		setBottomStyle(style);
	}

	void setBottom(int size, Color color, int style) {
		setBottom(new Extent(size), color, style);
	}

	void setBottomColor(Color bottomColor) {
		this.bottomColor = bottomColor;
	}

	void setBottomSize(Extent bottomSize) {
		this.bottomSize = bottomSize;
	}

	void setBottomStyle(int bottomStyle) {
		this.bottomStyle = bottomStyle;
	}

	void setColor(Color color) {
		this.color = color;
		setLeftColor(color);
		setRightColor(color);
		setTopColor(color);
		setBottomColor(color);
	}

	void setLeft(Extent size, Color color, int style) {
		setLeftSize(size);
		setLeftColor(color);
		setLeftStyle(style);
	}

	/* ======================================== */

	void setLeft(int size, Color color, int style) {
		setLeft(new Extent(size), color, style);
	}

	void setLeftColor(Color leftColor) {
		this.leftColor = leftColor;
	}

	void setLeftSize(Extent leftSize) {
		this.leftSize = leftSize;
	}

	void setLeftStyle(int leftStyle) {
		this.leftStyle = leftStyle;
	}

	void setRight(Extent size, Color color, int style) {
		setRightSize(size);
		setRightColor(color);
		setRightStyle(style);
	}

	void setRight(int size, Color color, int style) {
		setRight(new Extent(size), color, style);
	}

	void setRightColor(Color rightColor) {
		this.rightColor = rightColor;
	}

	void setRightSize(Extent rightSize) {
		this.rightSize = rightSize;
	}

	void setRightStyle(int rightStyle) {
		this.rightStyle = rightStyle;
	}

	void setSize(Extent size) {
		this.size = size;
		setLeftSize(size);
		setRightSize(size);
		setTopSize(size);
		setBottomSize(size);
	}

	void setStyle(int style) {
		this.style = style;
		setLeftStyle(style);
		setRightStyle(style);
		setTopStyle(style);
		setBottomStyle(style);
	}

	void setTop(Extent size, Color color, int style) {
		setTopSize(size);
		setTopColor(color);
		setTopStyle(style);
	}

	void setTop(int size, Color color, int style) {
		setTop(new Extent(size), color, style);
	}

	void setTopColor(Color topColor) {
		this.topColor = topColor;
	}

	void setTopSize(Extent topSize) {
		this.topSize = topSize;
	}

	void setTopStyle(int topStyle) {
		this.topStyle = topStyle;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("border-left:");
		sb.append(leftSize == null ? "null" : leftSize.toString());
		sb.append(" ");
		sb.append(getStyleString());
		sb.append(" ");
		sb.append(leftColor == null ? "null" : ColorKit.makeColorString(leftColor));
		sb.append("; ");

		sb.append("border-right:");
		sb.append(rightSize == null ? "null" : rightSize.toString());
		sb.append(" ");
		sb.append(getStyleString());
		sb.append(" ");
		sb.append(rightColor == null ? "null" : ColorKit.makeColorString(rightColor));
		sb.append("; ");

		sb.append("border-top:");
		sb.append(topSize == null ? "null" : topSize.toString());
		sb.append(" ");
		sb.append(getStyleString());
		sb.append(" ");
		sb.append(topColor == null ? "null" : ColorKit.makeColorString(topColor));
		sb.append("; ");

		sb.append("border-bottom:");
		sb.append(bottomSize == null ? "null" : bottomSize.toString());
		sb.append(" ");
		sb.append(getStyleString());
		sb.append(" ");
		sb.append(bottomColor == null ? "null" : ColorKit.makeColorString(bottomColor));
		sb.append("; ");

		sb.append("border:");
		sb.append(size == null ? "null" : size.toString());
		sb.append(" ");
		sb.append(getStyleString());
		sb.append(" ");
		sb.append(color == null ? "null" : ColorKit.makeColorString(color));
		sb.append(";");

		return sb.toString();
	}
}
