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

import nextapp.echo2.app.Component;

/**
 * <code>DirectHtml</code> is a very lightweight component that will insert
 * HTML text directly onto the client. Unlike TemplatePanel, this component will
 * <b>NOT</b> validate this HTML text nor will it parse the source HTML and perform
 * component or text substiutions.
 * <p>
 * This makes it more <i>lightweight</i> than TemplatePanel but far <i>less functional</i>
 * <p>
 * NOTE : The base <code>nextapp.echo2.app.Component</code> properties such as
 * background, foreground and font are NOT applied to this component.  You
 * must implement this in the HTML text itself.
 * <p>
 * Also be careful of your use of <i>id</i> attributes as they may clash with
 * Echo2 generated ones.
 */
public class DirectHtml extends Component {

	public static final String PROPERTY_TEXT = "text";

	/**
	 * Constructs a <code>DirectHtml</code>
	 */
	public DirectHtml() {
		this("");
	}

	/**
	 * Constructs a <code>DirectHtml</code> using the given htmlText.
	 * 
	 * @param htmlText -
	 *            the HTML to use
	 */
	public DirectHtml(String htmlText) {
		setText(htmlText);
	}

	/**
	 * Returns the HTML text to be sent down to the client browser
	 * 
	 * @return the HTML text to be sent down to the client browser
	 */
	public String getText() {
		return (String) getProperty(PROPERTY_TEXT);
	}

	/**
	 * Sets the HTML text to be sent down to the client browser
	 * 
	 * @param newValue -
	 *            the HTML text to be sent down to the client browser
	 */
	public void setText(String newValue) {
		setProperty(PROPERTY_TEXT, newValue);
	}
}
