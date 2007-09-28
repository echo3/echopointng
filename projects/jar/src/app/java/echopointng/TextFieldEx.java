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

import nextapp.echo2.app.Insets;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.text.Document;
import echopointng.able.AccessKeyable;
import echopointng.able.Attributeable;
import echopointng.able.Borderable;
import echopointng.able.Insetable;
import echopointng.able.MouseCursorable;
import echopointng.able.Sizeable;
import echopointng.able.ToolTipable;
import echopointng.text.StringDocumentEx;

/**
 * <code>TextFieldEx</code> is a <code>TextField</code> that adds extra EPNG
 * support as well as some new event options such as <i>onchange</i>. It also
 * has some extra useful constructors.
 * <p>
 * The <code>setRegex()</code> method can be used to associate a regular
 * expression with the <code>{@link TextFieldEx}</code>. This is evaluated on
 * the client and if it evaluates to false then the regexFailureLook
 * <code>{@link AbleProperties}</code> are used to present a different look
 * and feel to the <code>{@link TextFieldEx}</code> to indicate it may be
 * invalid. 
 * <p>
 * Remember that the regular expression
	 * executes on the client (not the server) and hence you must use a 
	 * regular expression syntax that is supported by the client.
 */
public class TextFieldEx extends TextField implements Attributeable, Borderable, MouseCursorable, Sizeable, Insetable, ToolTipable, AccessKeyable {

	public static final String PROPERTY_ACTION_CAUSED_ON_CHANGE = "actionCausedOnChange";

	public static final String PROPERTY_HIDDEN_FIELD = "hiddenField";

	public static final String PROPERTY_REGEX = "regex";

	public static final String PROPERTY_REGEX_FAILURE_PROPERTIES = "regexFailureProperties";

	private Map attributeMap;

	/**
	 * Creates a new <code>TextFieldEx</code> with an empty
	 * <code>StringDocumentEx</code> as its model, and default width setting.
	 */
	public TextFieldEx() {
		super(new StringDocumentEx());
	}

	/**
	 * Creates a new <code>TextFieldEx</code> with the specified
	 * <code>Document</code> model.
	 * 
	 * @param document
	 *            the document
	 */
	public TextFieldEx(Document document) {
		super(document);
	}

	/**
	 * Creates a new <code>TextFieldEx</code> with the specified
	 * <code>Document</code> model, initial text, and column width.
	 * 
	 * @param document
	 *            the document
	 * @param text
	 *            the initial text (may be null)
	 * @param columns
	 *            the number of columns to display
	 */
	public TextFieldEx(Document document, String text, int columns) {
		super(document, text, columns);
	}

	/**
	 * Creates a new <code>TextFieldEx</code> with the specified text in a
	 * <code>StringDocument</code> model.
	 * 
	 * @param text
	 *            the text to be placed into a <code>StringDocument</code>
	 */
	public TextFieldEx(String text) {
		super(new StringDocumentEx());
		if (text != null) {
			setText(text);
		}
	}

	/**
	 * Creates a new <code>TextFieldEx</code> with the initial text, and
	 * column width.
	 * 
	 * @param text
	 *            the initial text (may be null)
	 * @param columns
	 *            the number of columns to display
	 */
	public TextFieldEx(String text, int columns) {
		this(new StringDocumentEx(), text, columns);
	}

	/**
	 * Creates a new <code>TextFieldEx</code> with the specified column width.
	 * 
	 * @param columns
	 *            the number of columns to display
	 */
	public TextFieldEx(int columns) {
		this(new StringDocumentEx(), null, columns);
	}

	/**
	 * @see echopointng.able.AccessKeyable#getAccessKey()
	 */
	public String getAccessKey() {
		return (String) getProperty(PROPERTY_ACCESS_KEY);
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
	 * @see echopointng.able.AccessKeyable#setAccessKey(java.lang.String)
	 */
	public void setAccessKey(String newValue) {
		setProperty(PROPERTY_ACCESS_KEY, newValue);
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
	 * @return true if any change to <code>TextFieldEx</code>'s content cause
	 *         the <code>ActionListener</code> to be invoked.
	 */
	public boolean getActionCausedOnChange() {
		return ComponentEx.getProperty(this, PROPERTY_ACTION_CAUSED_ON_CHANGE, false);
	}

	/**
	 * When this flag is set to true, then when the <code>TextFieldEx</code>'s
	 * content changes, then any attached action listener will be informed
	 * immediately. Use this flag with caution as it can degrade the users
	 * experience and UI responsiveness.
	 * <p>
	 * Note also you CANNOT tell the difference between onchange events and
	 * pressing enter within the field. Microsoft documentation defines onchange
	 * for text fields as:
	 * 
	 * <pre>
	 *      &quot;...[the] event is fired when the contents are committed and not while the value is 
	 *       changing. [...] this event is notfired while the user is typing, but rather when the user commits the change by 
	 *       leaving the text box that has focus.&quot;
	 * </pre>
	 * 
	 * @param newValue -
	 *            true if any change to <code>TextFieldEx</code>'s content
	 *            cause the <code>ActionListener</code> to be invoked.
	 */
	public void setActionCausedOnChange(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_ACTION_CAUSED_ON_CHANGE, newValue);
	}

	/**
	 * @return true if this <code>TextFieldEx</code> is rendered as a hidden
	 *         input field. This is different to calling setVisible(false),
	 *         which will stop the field from being rendered entirely.
	 */
	public boolean isHiddenField() {
		return ComponentEx.getProperty(this, PROPERTY_HIDDEN_FIELD, false);
	}

	/**
	 * Allows you to set the <code>TextFieldEx</code> to be rendered as a
	 * hidden input field. This is different to calling setVisible(false), which
	 * will stop the field from being rendered entirely.
	 */
	public void setHiddenField(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_HIDDEN_FIELD, newValue);
	}

	/**
	 * @return the regular expresion string that is in play with this
	 *         <code>{@link TextFieldEx}</code>
	 */
	public String getRegex() {
		return (String) ComponentEx.getProperty(this, PROPERTY_REGEX);
	}

	/**
	 * Sets the regular expresion string that is in play with this
	 * <code>{@link TextFieldEx}</code>.  Remember that the regular expression
	 * executes on the client (not the server) and hence you must use a 
	 * regular expression syntax that is supported by the client.
	 * 
	 * @param newValue
	 *            the regular expresion string
	 */
	public void setRegex(String newValue) {
		setProperty(PROPERTY_REGEX, newValue);
	}

	/**
	 * @return the <code>{@link AbleProperties}</code> that are used when the
	 *         regular expression evaluates to false.
	 */
	public AbleProperties getRegexFailureProperties() {
		return (AbleProperties) getProperty(PROPERTY_REGEX_FAILURE_PROPERTIES);
	}

	/**
	 * Sets the <code>{@link AbleProperties}</code> that are used when the
	 * regular expression evaluates to false.
	 * 
	 * @param newValue
	 *            the AbleProperties to use
	 */
	public void setRegexFailureProperties(AbleProperties newValue) {
		setProperty(PROPERTY_REGEX_FAILURE_PROPERTIES, newValue);
	}

}
