package echopointng.layout;

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

import java.io.Serializable;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.LayoutData;
import nextapp.echo2.app.MutableStyle;
import echopointng.able.Alignable;
import echopointng.able.BackgroundImageable;
import echopointng.able.Borderable;
import echopointng.able.Floatable;
import echopointng.able.Insetable;
import echopointng.able.MouseCursorable;
import echopointng.able.Positionable;
import echopointng.able.Sizeable;

/**
 * <code>DisplayLayoutData</code> is used to specify special display
 * information for a child component. This class allows its parent container to
 * "position" the child component without the child component having to have to
 * support the various echopointng.able interfaces.
 * <p>
 * <h2>IMPLEMENTATION</h2>
 * 
 * Parent components that support the use of <code>DisplayLayoutData</code>,
 * such as <code>ContainerEx</code>, use it to produce a "mini" container
 * around the component. The properties specified in the
 * <code>DisplayLayoutData</code> are then used to control this "mini"
 * container not the child component itself. So for example the MouseCursorable
 * properties are set on the "mini" container even if the contained child
 * component does not implement the MouseCursorable interface.
 * <p>
 * Remember a single instance of DisplayLayoutData can be used on many
 * components and hence memory can be saved.
 */

public class DisplayLayoutData implements LayoutData, Serializable, Positionable, Sizeable, MouseCursorable, Insetable, Borderable, Alignable,
		BackgroundImageable, Floatable {

	public static final String PROPERTY_TOOL_TIP_TEXT = "toolTipText";
	public static final String PROPERTY_INLINE_LAYOUT = "inlineLayout";
	
	private MutableStyle localStyle = new MutableStyle();
	
	/**
	 * Constructs a empty <code>DisplayLayoutData</code>
	 */
	public DisplayLayoutData() {}
	
	/**
	 * Constructs a <code>DisplayLayoutData</code> with the left and top parameters set and
	 * set the position to <code>Positionable.ABSOLUTE</code>
	 * @param left - the left position value
	 * @param top - the top position value
	 */
	public DisplayLayoutData(Extent left, Extent top) {
		setPosition(Positionable.ABSOLUTE);
		setLeft(left);
		setTop(top);
	}

	/**
	 * @see echopointng.able.Positionable#clear()
	 */
	public void clear() {
		setZIndex(Integer.MIN_VALUE);
		setPosition(STATIC);
		setLeft(null);
		setRight(null);
		setTop(null);
		setBottom(null);
	}

	/**
	 * Returns the default/base background color of the
	 * <code>DisplayLayoutData</code>.
	 * 
	 * @return the background color
	 */
	public Color getBackground() {
		return (Color) localStyle.getProperty(Component.PROPERTY_BACKGROUND);
	}

	/**
	 * 
	 * @see echopointng.able.Borderable#getBorder()
	 */
	public Border getBorder() {
		return (Border) getProperty(PROPERTY_BORDER);
	}

	/**
	 * @see echopointng.able.Positionable#getBottom()
	 */
	public Extent getBottom() {
		return (Extent) getProperty(PROPERTY_BOTTOM);
	}

	/**
	 * @see echopointng.able.Floatable#getClearFloat()
	 */
	public int getClearFloat() {
		return getProperty(PROPERTY_CLEAR_FLOAT, FLOAT_CLEAR_NONE);
	}

	/**
	 * @see echopointng.able.Floatable#getFloat()
	 */
	public int getFloat() {
		return getProperty(PROPERTY_FLOAT, FLOAT_NONE);
	}

	/**
	 * Returns the default/base font of the <code>DisplayLayoutData</code>.
	 * 
	 * @return the font
	 */
	public Font getFont() {
		return (Font) localStyle.getProperty(Component.PROPERTY_FONT);
	}

	/**
	 * Returns the default/base foreground color of the
	 * <code>DisplayLayoutData</code>.
	 * 
	 * @return the foreground color
	 */
	public Color getForeground() {
		return (Color) localStyle.getProperty(Component.PROPERTY_FOREGROUND);
	}

	/**
	 * @see echopointng.able.Heightable#getHeight()
	 */
	public Extent getHeight() {
		return (Extent) getProperty(PROPERTY_HEIGHT);
	}

	/**
	 * @see echopointng.able.Insetable#getInsets()
	 */
	public Insets getInsets() {
		return (Insets) getProperty(PROPERTY_INSETS);
	}

	/**
	 * @see echopointng.able.Positionable#getLeft()
	 */
	public Extent getLeft() {
		return (Extent) getProperty(PROPERTY_LEFT);
	}

	/**
	 * @see echopointng.able.MouseCursorable#getMouseCursor()
	 */
	public int getMouseCursor() {
		return getProperty(PROPERTY_MOUSE_CURSOR, CURSOR_AUTO);
	}

	/**
	 * @see echopointng.able.MouseCursorable#getMouseCursorUri()
	 */
	public String getMouseCursorUri() {
		return (String) getProperty(PROPERTY_MOUSE_CURSOR_URI);
	}

	/**
	 * @see echopointng.able.Insetable#getOutsets()
	 */
	public Insets getOutsets() {
		return (Insets) getProperty(PROPERTY_OUTSETS);
	}

	/**
	 * @see echopointng.able.Positionable#getPosition()
	 */
	public int getPosition() {
		return getProperty(PROPERTY_POSITION, STATIC);
	}

	protected Object getProperty(String propertyName) {
		return localStyle.getProperty(propertyName);
	}

	protected boolean getProperty(String propertyName, boolean defaultValue) {
		Boolean obj = (Boolean) getProperty(propertyName);
		return (obj == null ? defaultValue : obj.booleanValue());
	}

	protected int getProperty(String propertyName, int defaultValue) {
		Integer obj = (Integer) getProperty(propertyName);
		return (obj == null ? defaultValue : obj.intValue());
	}

	/**
	 * @see echopointng.able.Delegateable#getRenderProperty(java.lang.String)
	 */
	public Object getRenderProperty(String propertyName) {
		return getProperty(propertyName);
	}

	/**
	 * @see echopointng.able.Delegateable#getRenderProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public Object getRenderProperty(String propertyName, Object defaultValue) {
		Object obj = getProperty(propertyName);
		return obj == null ? defaultValue : obj;
	}

	/**
	 * @see echopointng.able.Positionable#getRight()
	 */
	public Extent getRight() {
		return (Extent) getProperty(PROPERTY_RIGHT);
	}

	/**
	 * @see echopointng.able.Positionable#getTop()
	 */
	public Extent getTop() {
		return (Extent) getProperty(PROPERTY_TOP);
	}

	/**
	 * @see echopointng.able.Widthable#getWidth()
	 */
	public Extent getWidth() {
		return (Extent) getProperty(PROPERTY_WIDTH);
	}

	/**
	 * @see echopointng.able.Positionable#getZIndex()
	 */
	public int getZIndex() {
		return getProperty(PROPERTY_Z_INDEX, Integer.MIN_VALUE);
	}

	/**
	 * @see echopointng.able.Positionable#isPositioned()
	 */
	public boolean isPositioned() {
		return getPosition() != STATIC;
	}

	/**
	 * Sets the default background color of the <code>DisplayLayoutData</code>.
	 * 
	 * @param newValue
	 *            the new background <code>Color</code>
	 */
	public void setBackground(Color newValue) {
		setProperty(Component.PROPERTY_BACKGROUND, newValue);
	}

	/**
	 * @see echopointng.able.Borderable#setBorder(nextapp.echo2.app.Border)
	 */
	public void setBorder(Border newValue) {
		setProperty(PROPERTY_BORDER, newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setBottom(nextapp.echo2.app.Extent)
	 */
	public void setBottom(Extent newValue) {
		setProperty(PROPERTY_BOTTOM, newValue);
	}

	/**
	 * @see echopointng.able.Floatable#setClearFloat(int)
	 */
	public void setClearFloat(int newValue) {
		switch (newValue) {
			case FLOAT_CLEAR_NONE:
			case FLOAT_CLEAR_LEFT:
			case FLOAT_CLEAR_RIGHT:
			case FLOAT_CLEAR_BOTH:
				setProperty(PROPERTY_CLEAR_FLOAT, newValue);
				break;
			default:
				throw new IllegalArgumentException("Illegal float clearing value");
		}
	}

	/**
	 * @see echopointng.able.Floatable#setFloat(int)
	 */
	public void setFloat(int newValue) {
		switch (newValue) {
			case FLOAT_NONE:
			case FLOAT_LEFT:
			case FLOAT_RIGHT:
				setProperty(PROPERTY_FLOAT, newValue);
				break;
			default:
				throw new IllegalArgumentException("Illegal float value");
		}
	}

	/**
	 * Sets the default text font of the <code>DisplayLayoutData</code>.
	 * 
	 * @param newValue
	 *            the new <code>Font</code>
	 */
	public void setFont(Font newValue) {
		setProperty(Component.PROPERTY_FONT, newValue);
	}

	/**
	 * Sets the default foreground color of the <code>DisplayLayoutData</code>.
	 * 
	 * @param newValue
	 *            the new foreground <code>Color</code>
	 */
	public void setForeground(Color newValue) {
		setProperty(Component.PROPERTY_FOREGROUND, newValue);
	}

	/**
	 * @see echopointng.able.Heightable#setHeight(nextapp.echo2.app.Extent)
	 */
	public void setHeight(Extent newValue) {
		setProperty(PROPERTY_HEIGHT, newValue);
	}

	/**
	 * @see echopointng.able.Insetable#setInsets(nextapp.echo2.app.Insets)
	 */
	public void setInsets(Insets newValue) {
		setProperty(PROPERTY_INSETS, newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setLeft(nextapp.echo2.app.Extent)
	 */
	public void setLeft(Extent newValue) {
		setProperty(PROPERTY_LEFT, newValue);
	}

	/**
	 * @see echopointng.able.MouseCursorable#setMouseCursor(int)
	 */
	public void setMouseCursor(int mouseCursor) {
		setProperty(PROPERTY_MOUSE_CURSOR, mouseCursor);
	}

	/**
	 * @see echopointng.able.MouseCursorable#setMouseCursorUri(java.lang.String)
	 */
	public void setMouseCursorUri(String mouseCursorURI) {
		setProperty(PROPERTY_MOUSE_CURSOR_URI, mouseCursorURI);
	}

	/**
	 * @see echopointng.able.Insetable#setOutsets(nextapp.echo2.app.Insets)
	 */
	public void setOutsets(Insets newValue) {
		setProperty(PROPERTY_OUTSETS, newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setPosition(int)
	 */
	public void setPosition(int newPositioning) {
		setProperty(PROPERTY_POSITION, newPositioning);
	}

	protected void setProperty(String propertyName, boolean newValue) {
		setProperty(propertyName, Boolean.valueOf(newValue));
	}

	protected void setProperty(String propertyName, int newValue) {
		setProperty(propertyName, new Integer(newValue));
	}

	protected void setProperty(String propertyName, Object newValue) {
		localStyle.setProperty(propertyName, newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setRight(nextapp.echo2.app.Extent)
	 */
	public void setRight(Extent newValue) {
		setProperty(PROPERTY_RIGHT, newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setTop(nextapp.echo2.app.Extent)
	 */
	public void setTop(Extent newValue) {
		setProperty(PROPERTY_TOP, newValue);
	}

	/**
	 * @see echopointng.able.Widthable#setWidth(nextapp.echo2.app.Extent)
	 */
	public void setWidth(Extent newValue) {
		setProperty(PROPERTY_WIDTH, newValue);
	}

	/**
	 * @see echopointng.able.Positionable#setZIndex(int)
	 */
	public void setZIndex(int newValue) {
		setProperty(PROPERTY_Z_INDEX, newValue);
	}

	/**
	 * Returns the alignment of the cell.
	 * 
	 * @return the alignment
	 */
	public Alignment getAlignment() {
		return (Alignment) getProperty(PROPERTY_ALIGNMENT);
	}

	/**
	 * Returns the background image of the cell.
	 * 
	 * @return the background image
	 */
	public FillImage getBackgroundImage() {
		return (FillImage) getProperty(PROPERTY_BACKGROUND_IMAGE);
	}

	/**
	 * Sets the alignment of the cell.
	 * 
	 * @param newValue
	 *            the new alignment
	 */
	public void setAlignment(Alignment newValue) {
		setProperty(PROPERTY_ALIGNMENT,newValue);
	}

	/**
	 * Sets the background image of the cell.
	 * 
	 * @param newValue
	 *            the new background image
	 */
	public void setBackgroundImage(FillImage newValue) {
		setProperty(PROPERTY_BACKGROUND_IMAGE,newValue);
	}

    /**
     * Returns the tool tip text (displayed when the mouse cursor is hovered 
     * over the area with this layout data).
     * 
     * @return the tool tip text
     */
    public String getToolTipText() {
        return (String) getProperty(PROPERTY_TOOL_TIP_TEXT);
    }
    
    /**
     * Sets the tool tip text (displayed when the mouse cursor is hovered 
     * over the area with this layout data).
     * 
     * @param newValue the new tool tip text
     */
    public void setToolTipText(String newValue) {
        setProperty(PROPERTY_TOOL_TIP_TEXT, newValue);
    }

    /**
     * Returns true if the layout data should use inline layout or false if it should
     * block layout.
     * 
     * @return true of the layout data should use inline layout or false if it should
     * block layout.
     */
    public boolean isInlineLayout() {
        return getProperty(PROPERTY_INLINE_LAYOUT,false);
    }
    
    /**
     * Set to true if the layout data should use inline layout or false if it should
     * block layout.
     * 
     * 
     * @param newValue true if the layout data should use inline layout or false if it should
     * block layout.
     */
    public void setInlineLayout(boolean newValue) {
        setProperty(PROPERTY_INLINE_LAYOUT, newValue);
    }

}
