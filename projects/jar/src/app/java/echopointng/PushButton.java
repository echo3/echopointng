package echopointng;

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
import echopointng.able.Insetable;
import echopointng.util.ColorKit;
import echopointng.xhtml.XhtmlFragment;
import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Style;

/**
 * The <code>PushButton</code> class is another implementation of the basic
 * <code>nextapp.echo2.app.ButtonEx</code> class that has a more conventional
 * visual appearance of a PushButton.
 * <p>
 * 
 * @author Brad Baker
 */
public class PushButton extends ButtonEx {

	public static final String PROPERTY_SUBMIT_BUTTON = "submitButton";

	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(PROPERTY_ROLLOVER_ENABLED, false);
		style.setProperty(PROPERTY_ROLLOVER_BACKGROUND, ColorKit.makeColor("#DEF3FF"));
		style.setProperty(PROPERTY_ROLLOVER_BORDER, new Border(1, ColorKit.makeColor("#3169C6"), Border.STYLE_SOLID));

		style.setProperty(Insetable.PROPERTY_INSETS, DEFAULT_INSETS);
		style.setProperty(Insetable.PROPERTY_OUTSETS, DEFAULT_OUTSETS);

		style.setProperty(PROPERTY_TEXT_ALIGNMENT, new Alignment(Alignment.TRAILING, Alignment.DEFAULT));
		style.setProperty(PROPERTY_ICON_TEXT_MARGIN, new Extent(3));
		style.setProperty(PROPERTY_MOUSE_CURSOR, CURSOR_POINTER);
		DEFAULT_STYLE = style;
	}

	/**
	 * Creates a <code>PushButton</code> with no text or icon.
	 */
	public PushButton() {
		this((String) null, null);
	}

	/**
	 * Creates a <code>PushButton</code> with text.
	 * 
	 * @param text
	 *            A text label to display in the button.
	 */
	public PushButton(String text) {
		this(text, null);
	}

	/**
	 * Creates a <code>PushButton</code> with an icon.
	 * 
	 * @param icon
	 *            An icon to display in the button.
	 */
	public PushButton(ImageReference icon) {
		this((String) null, icon);
	}

	/**
	 * Creates a <code>PushButton</code> with text and an icon.
	 * 
	 * @param text
	 *            A text label to display in the button.
	 * @param icon
	 *            An icon to display in the button.
	 */
	public PushButton(String text, ImageReference icon) {
		super(text, icon);
	}

	/**
	 * Creates a <code>PushButton</code> with an <code>XhtmlFragment</code>.
	 * 
	 * @param text
	 *            A <code>XhtmlFragment</code> to display in the
	 *            <code>PushButton</code> .
	 */
	public PushButton(XhtmlFragment text) {
		this(text, null);
	}

	/**
	 * Creates a <code>PushButton</code> with an <code>XhtmlFragment</code>
	 * and an icon.
	 * 
	 * @param text
	 *            A <code>XhtmlFragment</code> to display in the
	 *            <code>PushButton</code> .
	 * @param icon
	 *            An icon to display in the <code>PushButton</code> .
	 */
	public PushButton(XhtmlFragment text, ImageReference icon) {
		super();
		setIcon(icon);
		setText(text);
	}

	/**
	 * @return true if the button is a submitting button
	 */
	public boolean isSubmitButton() {
		return ComponentEx.getProperty(this, PROPERTY_SUBMIT_BUTTON, false);
	}

	/**
	 * When <code>newValue</code> is true, then when the user presses ENTER,
	 * the button's action event is fired, even if the user is in another
	 * component. This behavior is indicated by a border surrounding the button.
	 * The border appears when any control receives the focus, other than
	 * another button.
	 * 
	 * @param newValue -
	 *            true or false
	 */
	public void setSubmitButton(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_SUBMIT_BUTTON, newValue);
	}

}
