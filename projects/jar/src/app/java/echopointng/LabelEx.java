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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Style;
import echopointng.able.AccessKeyable;
import echopointng.able.Attributeable;
import echopointng.able.Borderable;
import echopointng.able.Insetable;
import echopointng.able.MouseCursorable;
import echopointng.able.Sizeable;
import echopointng.able.ToolTipable;
import echopointng.util.ColorKit;
import echopointng.xhtml.XhtmlFragment;

/**
 * <code>LabelEx</code> is a LabelEx with extras such as rollovers and
 * borders.
 */
public class LabelEx extends Label implements Borderable, Sizeable, Insetable, AccessKeyable, MouseCursorable, ToolTipable, Attributeable {

	public static final String PROPERTY_BACKGROUND_IMAGE = "backgroundImage";

	public static final String PROPERTY_DISABLED_BACKGROUND = "disabledBackground";

	public static final String PROPERTY_DISABLED_BACKGROUND_IMAGE = "disabledBackgroundImage";

	public static final String PROPERTY_DISABLED_BORDER = "disabledBorder";

	public static final String PROPERTY_DISABLED_FONT = "disabledFont";

	public static final String PROPERTY_DISABLED_FOREGROUND = "disabledForeground";

	public static final String PROPERTY_DISABLED_ICON = "disabledIcon";

	public static final String PROPERTY_LINE_WRAP = "lineWrap";

	public static final String PROPERTY_ROLLOVER_BACKGROUND = "rolloverBackground";

	public static final String PROPERTY_ROLLOVER_BACKGROUND_IMAGE = "rolloverBackgroundImage";

	public static final String PROPERTY_ROLLOVER_BORDER = "rolloverBorder";

	public static final String PROPERTY_ROLLOVER_ENABLED = "rolloverEnabled";

	public static final String PROPERTY_ROLLOVER_FONT = "rolloverFont";

	public static final String PROPERTY_ROLLOVER_FOREGROUND = "rolloverForeground";

	public static final String PROPERTY_ROLLOVER_ICON = "rolloverIcon";

	public static final String PROPERTY_INTERPRET_NEWLINES = "interpretNewlines";

	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(PROPERTY_LINE_WRAP, false);

//		style.setProperty(PROPERTY_BACKGROUND, ColorKit.makeColor("#FFFFFF"));
//		style.setProperty(Borderable.PROPERTY_BORDER, new Border(1, ColorKit.makeColor("#FFFFFF"), Border.STYLE_NONE));

		style.setProperty(PROPERTY_ROLLOVER_ENABLED, false);
		style.setProperty(PROPERTY_ROLLOVER_BACKGROUND, ColorKit.makeColor("#DEF3FF"));
//		style.setProperty(PROPERTY_ROLLOVER_BORDER, new Border(1, ColorKit.makeColor("#3169C6"), Border.STYLE_SOLID));

		style.setProperty(PROPERTY_TEXT_ALIGNMENT, new Alignment(Alignment.TRAILING, Alignment.DEFAULT));
		style.setProperty(PROPERTY_ICON_TEXT_MARGIN, new Extent(3));
		style.setProperty(PROPERTY_MOUSE_CURSOR, MouseCursorable.CURSOR_TEXT);
		DEFAULT_STYLE = style;
	}

	private Map attributeMap;

	/**
	 * Creates a <code>LabelEx</code> with no text or icon.
	 */
	public LabelEx() {
		this((String)null,null);
	}

	/**
	 * Creates a <code>LabelEx</code> with an icon.
	 * 
	 * @param icon
	 *            the icon to be displayed
	 */
	public LabelEx(ImageReference icon) {
		this((String) null, icon);
	}

	/**
	 * Creates a <code>LabelEx</code> with the specified text.
	 * 
	 * @param text
	 *            the text to be displayed
	 */
	public LabelEx(String text) {
		this(text, null);
	}

	/**
	 * Creates a <code>LabelEx</code> with the specified text and an icon.
	 * 
	 * @param text
	 *            the text to be displayed
	 * @param icon
	 *            the icon to be displayed
	 */
	public LabelEx(String text, ImageReference icon) {
		super(text, icon);
		setFocusTraversalParticipant(false);
	}

	/**
	 * Creates a label with the specified <code>XhtmlFragment</code>.
	 * 
	 * @param text
	 *            the <code>XhtmlFragment</code> to be displayed
	 */
	public LabelEx(XhtmlFragment text) {
		this(text,null);
	}

	/**
	 * Creates a label with the specified <code>XhtmlFragment</code> and an icon.
	 * 
	 * @param text
	 *            the <code>XhtmlFragment</code> to be displayed
	 * @param icon
	 *            the icon to be displayed
	 */
	public LabelEx(XhtmlFragment text, ImageReference icon) {
		super(null, icon);
		setText(text);
		setFocusTraversalParticipant(false);
	}
	
	/**
	 * Returns the background image of the LabelEx.
	 * 
	 * @return the background image
	 */
	public FillImage getBackgroundImage() {
		return (FillImage) getProperty(PROPERTY_BACKGROUND_IMAGE);
	}

	/**
	 * Returns the background color of the LabelEx when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @return the color
	 */
	public Color getRolloverBackground() {
		return (Color) getProperty(PROPERTY_ROLLOVER_BACKGROUND);
	}

	/**
	 * Returns the background image displayed when the mouse cursor is inside
	 * the LabelEx's bounds.
	 * 
	 * @return the background image
	 */
	public FillImage getRolloverBackgroundImage() {
		return (FillImage) getProperty(PROPERTY_ROLLOVER_BACKGROUND_IMAGE);
	}

	/**
	 * Returns the border displayed around the LabelEx when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @return the border
	 */
	public Border getRolloverBorder() {
		return (Border) getProperty(PROPERTY_ROLLOVER_BORDER);
	}

	/**
	 * Returns the font of the LabelEx when the mouse cursor is inside its
	 * bounds.
	 * 
	 * @return the font
	 */
	public Font getRolloverFont() {
		return (Font) getProperty(PROPERTY_ROLLOVER_FONT);
	}

	/**
	 * Returns the foreground color of the LabelEx when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @return the color
	 */
	public Color getRolloverForeground() {
		return (Color) getProperty(PROPERTY_ROLLOVER_FOREGROUND);
	}

	/**
	 * Returns the icon of the LabelEx that is displayed when the mouse cursor
	 * is inside its bounds.
	 * 
	 * @return the icon
	 */
	public ImageReference getRolloverIcon() {
		return (ImageReference) getProperty(PROPERTY_ROLLOVER_ICON);
	}

	/**
	 * Returns whether text wrapping is allowed or not.
	 * <p>
	 * If this property is true then the contents of the button MAY be wrapped
	 * to fit in the smallest possible space.
	 * 
	 * @return boolean
	 */
	public boolean isLineWrap() {
		return ComponentEx.getProperty(this, PROPERTY_LINE_WRAP, false);
	}

	/**
	 * Determines if rollover effects are enabled.
	 * 
	 * @return true if rollover effects are enabled
	 * @see #setRolloverEnabled(boolean)
	 */
	public boolean isRolloverEnabled() {
		return ComponentEx.getProperty(this, PROPERTY_ROLLOVER_ENABLED, true);
	}

	/**
	 * Sets the background image of the LabelEx.
	 * 
	 * @param newValue
	 *            the new background image
	 */
	public void setBackgroundImage(FillImage newValue) {
		setProperty(PROPERTY_BACKGROUND_IMAGE, newValue);
	}

	/**
	 * Returns whether line wrapping is in place.
	 * <p>
	 * If this property is true then the contents of the text MAY be wrapped to
	 * fit in the smallest possible space.
	 * 
	 * @param newValue
	 *            boolean - the wrap flag
	 */
	public void setLineWrap(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_LINE_WRAP, newValue);
	}

	/**
	 * Sets the background color of the LabelEx when the mouse cursor is inside
	 * its bounds.
	 * 
	 * @param newValue
	 *            the new <code>Color</code>
	 */
	public void setRolloverBackground(Color newValue) {
		setProperty(PROPERTY_ROLLOVER_BACKGROUND, newValue);
	}

	/**
	 * Sets the background image displayed when the mouse cursor is inside the
	 * LabelEx's bounds
	 * 
	 * @param newValue
	 *            the new background image
	 */
	public void setRolloverBackgroundImage(FillImage newValue) {
		setProperty(PROPERTY_ROLLOVER_BACKGROUND_IMAGE, newValue);
	}

	/**
	 * Sets the border displayed around the LabelEx when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @param newValue
	 *            the new border
	 */
	public void setRolloverBorder(Border newValue) {
		setProperty(PROPERTY_ROLLOVER_BORDER, newValue);
	}

	/**
	 * Sets whether rollover effects are enabled when the mouse cursor is inside
	 * the LabelEx's bounds. Rollover properties have no effect unless this
	 * property is set to true. The default value is false.
	 * 
	 * @param newValue
	 *            true if rollover effects should be enabled
	 */
	public void setRolloverEnabled(boolean newValue) {
		setProperty(PROPERTY_ROLLOVER_ENABLED, Boolean.valueOf(newValue));
	}

	/**
	 * Sets the font of the LabelEx when the mouse cursor is inside its bounds.
	 * 
	 * @param newValue
	 *            the new <code>Font</code>
	 */
	public void setRolloverFont(Font newValue) {
		setProperty(PROPERTY_ROLLOVER_FONT, newValue);
	}

	/**
	 * Sets the foreground color of the LabelEx when the mouse cursor is inside
	 * its bounds.
	 * 
	 * @param newValue
	 *            the new <code>Color</code>
	 */
	public void setRolloverForeground(Color newValue) {
		setProperty(PROPERTY_ROLLOVER_FOREGROUND, newValue);
	}

	/**
	 * Sets the icon of the LabelEx that is displayed when the mouse cursor is
	 * inside its bounds.
	 * 
	 * @param newValue
	 *            the new icon
	 */
	public void setRolloverIcon(ImageReference newValue) {
		setProperty(PROPERTY_ROLLOVER_ICON, newValue);
	}

	/**
	 * @see echopointng.able.AccessKeyable#getAccessKey()
	 */
	public String getAccessKey() {
		return (String) getProperty(PROPERTY_ACCESS_KEY);
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
	 * @see echopointng.able.MouseCursorable#getMouseCursor()
	 */
	public int getMouseCursor() {
		return ComponentEx.getProperty(this, PROPERTY_MOUSE_CURSOR, CURSOR_AUTO);
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
	 * @see echopointng.able.Widthable#getWidth()
	 */
	public Extent getWidth() {
		return (Extent) getProperty(PROPERTY_WIDTH);
	}

	/**
	 * @see echopointng.able.AccessKeyable#setAccessKey(java.lang.String)
	 */
	public void setAccessKey(String newValue) {
		setProperty(PROPERTY_ACCESS_KEY, newValue);
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
	 * @see echopointng.able.MouseCursorable#setMouseCursor(int)
	 */
	public void setMouseCursor(int mouseCursor) {
		ComponentEx.setProperty(this, PROPERTY_MOUSE_CURSOR, mouseCursor);
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
	 * @see echopointng.able.Widthable#setWidth(nextapp.echo2.app.Extent)
	 */
	public void setWidth(Extent newValue) {
		setProperty(PROPERTY_WIDTH, newValue);
	}

	/**
	 * 
	 * @see echopointng.able.Borderable#getBorder()
	 */
	public Border getBorder() {
		return (Border) getProperty(PROPERTY_BORDER);
	}

	/**
	 * @see echopointng.able.Borderable#setBorder(nextapp.echo2.app.Border)
	 */
	public void setBorder(Border newValue) {
		setProperty(PROPERTY_BORDER, newValue);
	}

	/**
	 * @see echopointng.able.Attributeable#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String attributeName) {
		if (attributeMap != null) {
			return attributeMap.get(attributeName);
		}
		return null;
	}

	/**
	 * @see echopointng.able.Attributeable#setAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setAttribute(String attributeName, Object attributeValue) {
		if (attributeMap == null) {
			attributeMap = new HashMap();
		}
		attributeMap.put(attributeName, attributeValue);
	}

	/**
	 * @see echopointng.able.Attributeable#getAttributeNames()
	 */
	public String[] getAttributeNames() {
		if (attributeMap == null) {
			return new String[0];
		}
		int count = 0;
		String[] attributeNames = new String[attributeMap.keySet().size()];
		for (Iterator iter = attributeMap.keySet().iterator(); iter.hasNext();) {
			attributeNames[count++] = (String) iter.next();
		}
		return attributeNames;
	}

	/**
	 * Returns the background color of the <code>LabelEx</code> when the
	 * <code>LabelEx</code> is disabled.
	 * 
	 * @return the color
	 */
	public Color getDisabledBackground() {
		return (Color) getProperty(PROPERTY_DISABLED_BACKGROUND);
	}

	/**
	 * Returns the background image displayed when the <code>LabelEx</code> is
	 * disabled.
	 * 
	 * @return the background image
	 */
	public FillImage getDisabledBackgroundImage() {
		return (FillImage) getProperty(PROPERTY_DISABLED_BACKGROUND_IMAGE);
	}

	/**
	 * Returns the border displayed around the <code>LabelEx</code> when the
	 * <code>LabelEx</code> is disabled.
	 * 
	 * @return the border
	 */
	public Border getDisabledBorder() {
		return (Border) getProperty(PROPERTY_DISABLED_BORDER);
	}

	/**
	 * Returns the font of the <code>LabelEx</code> when the
	 * <code>LabelEx</code> is disabled.
	 * 
	 * @return the font
	 */
	public Font getDisabledFont() {
		return (Font) getProperty(PROPERTY_DISABLED_FONT);
	}

	/**
	 * Returns the foreground color of the <code>LabelEx</code> when the
	 * <code>LabelEx</code> is disabled.
	 * 
	 * @return the color
	 */
	public Color getDisabledForeground() {
		return (Color) getProperty(PROPERTY_DISABLED_FOREGROUND);
	}

	/**
	 * Returns the icon of the <code>LabelEx</code> that is displayed when the
	 * <code>LabelEx</code> is disabled.
	 * 
	 * @return the icon
	 */
	public ImageReference getDisabledIcon() {
		return (ImageReference) getProperty(PROPERTY_DISABLED_ICON);
	}

	/**
	 * Sets the background color of the <code>LabelEx</code> when the
	 * <code>LabelEx</code> is disabled.
	 * 
	 * @param newValue
	 *            the new <code>Color</code>
	 */
	public void setDisabledBackground(Color newValue) {
		setProperty(PROPERTY_DISABLED_BACKGROUND, newValue);
	}

	/**
	 * Sets the background image displayed when the <code>LabelEx</code> is
	 * disabled.
	 * 
	 * @param newValue
	 *            the new background image
	 */
	public void setDisabledBackgroundImage(FillImage newValue) {
		setProperty(PROPERTY_DISABLED_BACKGROUND_IMAGE, newValue);
	}

	/**
	 * Sets the border displayed around the <code>LabelEx</code> when the
	 * <code>LabelEx</code> is disabled.
	 * 
	 * @param newValue
	 *            the new border
	 */
	public void setDisabledBorder(Border newValue) {
		setProperty(PROPERTY_DISABLED_BORDER, newValue);
	}

	/**
	 * Sets the font of the <code>LabelEx</code> when the <code>LabelEx</code>
	 * is disabled.
	 * 
	 * @param newValue
	 *            the new <code>Font</code>
	 */
	public void setDisabledFont(Font newValue) {
		setProperty(PROPERTY_DISABLED_FONT, newValue);
	}

	/**
	 * Sets the foreground color of the <code>LabelEx</code> when the
	 * <code>LabelEx</code> is disabled.
	 * 
	 * @param newValue
	 *            the new <code>Color</code>
	 */
	public void setDisabledForeground(Color newValue) {
		setProperty(PROPERTY_DISABLED_FOREGROUND, newValue);
	}

	/**
	 * Sets the icon of the <code>LabelEx</code> that is displayed when the
	 * <code>LabelEx</code> is disabled.
	 * 
	 * @param newValue
	 *            the new icon
	 */
	public void setDisabledIcon(ImageReference newValue) {
		setProperty(PROPERTY_DISABLED_ICON, newValue);
	}

	/**
	 * @return true if new lines in the <code>LabelEx</code> text are
	 *         interpreted. This is off by default.
	 */
	public boolean isIntepretNewlines() {
		return ComponentEx.getProperty(this, PROPERTY_INTERPRET_NEWLINES, false);
	}

	/**
	 * Sets whether newlines in the text of the <code>LabelEx</code> are
	 * intepreted or not. For example if this is <code>true</code> on a
	 * browser client, the new lines will be displayed using &lt;br/&gt; tags.
	 * 
	 * @param newValue -
	 *            the new value of the flag
	 */
	public void setIntepretNewlines(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_INTERPRET_NEWLINES, newValue);
	}

	/**
	 * If the value in <code>PROPERTY_TEXT</code> is a XhtmlFragment, then
	 * the <code>XhtmlFragment.toString()</code> is returned, otherwise a
	 * <code>String</code> value is returned.
	 * <p>
	 * If you you want access to the underlying XhtmlFragment object itself, then use:
	 * <pre>XhtmlFragment fragment = (XhtmlFragment) getProperty(PROPERTY_TEXT);</pre>
	 * 
	 * @see nextapp.echo2.app.Label#getText()
	 */
	public String getText() {
		Object text = getProperty(PROPERTY_TEXT);
		if (text instanceof XhtmlFragment) {
			return text.toString();
		}
		return (String) text;
	}

	/**
	 * This version of <code>setText()</code> will set an XhtmlFragment inside
	 * the Label
	 * 
	 * @param fragment -
	 *            the XhtmlFragment to use
	 * @see XhtmlFragment
	 */
	public void setText(XhtmlFragment fragment) {
		setProperty(PROPERTY_TEXT, fragment);
	}

}
