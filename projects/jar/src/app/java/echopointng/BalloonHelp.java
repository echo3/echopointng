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

import echopointng.template.StringTemplateDataSource;
import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.Style;

/**
 * <code>BalloonHelp</code> is a <code>PopUp</code> component that appears
 * like classic Balloon Help, inspired from the early Apple Macintosh.
 */
public class BalloonHelp extends PopUp {
	/** The default alignment for the floating box is right/top */
	public static final Alignment DEFAULT_ALIGNMENT = new Alignment(Alignment.LEFT, Alignment.TOP);

	/** The default image used as the toggle image */
	public static final ImageReference DEFAULT_TOGGLE_ICON = new ResourceImageReference("/echopointng/resource/images/balloon/balloon_question.gif",
			new Extent(16), new Extent(16));

	/**
	 * The default Style for the BalloonHelp
	 */
	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(PROPERTY_POPUP_ALIGNMENT, DEFAULT_ALIGNMENT);
		style.setProperty(PROPERTY_POPUP_NEXT_TO_TOGGLE, true);
		style.setProperty(PROPERTY_POPUP_ON_ROLLOVER, true);
		style.setProperty(PROPERTY_TOGGLE_ICON, DEFAULT_TOGGLE_ICON);
		style.setProperty(PROPERTY_TOGGLE_BACKGROUND, null);
		style.setProperty(PROPERTY_BORDER, null);
		style.setProperty(PROPERTY_POPUP_BORDER, null);
		style.setProperty(PROPERTY_ROLLOVER_ENABLED, false);
		style.setProperty(PROPERTY_TOGGLE_PRESSED_ENABLED, null);
		DEFAULT_STYLE = style;
	}

	/**
	 * Constructs a <code>BalloonHelp</code>
	 */
	public BalloonHelp() {
		this(null, null);
	}

	/**
	 * Constructs a <code>BalloonHelp</code> using the XHTML passed in as the
	 * help text.
	 * 
	 * @param xhtmlText -
	 *            must be a fragment of <code>valid</code> XHTML. eg single
	 *            top level tag and all tags propertly closed.
	 */
	public BalloonHelp(String xhtmlText) {
		this(null, null);
		TemplatePanel panel = new TemplatePanel(new StringTemplateDataSource("<bdo>" + xhtmlText + "</bdo>"));
		setPopUp(panel);
	}

	/**
	 * Constructs a <code>BalloonHelp</code> with the specified popupComponent
	 * as help.
	 * 
	 * @param popUpComponent
	 */
	public BalloonHelp(Component popUpComponent) {
		this(null, popUpComponent);
	}

	/**
	 * Constructs a <code>BalloonHelp</code> with the specified target and
	 * popup component
	 * 
	 * @param targetComponent
	 * @param popUpComponent
	 */
	public BalloonHelp(Component targetComponent, Component popUpComponent) {
		super(targetComponent, popUpComponent);
	}
}
