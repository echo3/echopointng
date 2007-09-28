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

import nextapp.echo2.app.Label;
import nextapp.echo2.app.Style;

/**
 * <code>GroupBox</code> has a simple title and draws a border around its
 * child components.
 */

public class GroupBox extends AbleComponent {

	public static final String PROPERTY_TITLE_LABEL = "titleLabel";

	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		DEFAULT_STYLE = style;
	}
	
	/**
	 * Constructs a <code>GroupBox</code> with no title
	 */
	public GroupBox() {
	}

	/**
	 * Constructs a <code>GroupBox</code> with the specified title.
	 */
	public GroupBox(String title) {
		setTitleLabel(new Label(title));
	}

	/**
	 * Constructs a <code>GroupBox</code> using the specified
	 * <code>Label</code> for the title.
	 */
	public GroupBox(Label titleLabel) {
		setTitleLabel(titleLabel);
	}

	/**
	 * @return the title text of the <code>GroupBox</code>
	 */
	public String getTitle() {
		Label title = getTitleLabel();
		return title == null ? null : title.getText();
	}

	/**
	 * Sets the title text of the <code>GroupBox</code>
	 */
	public void setTitle(String newValue) {
		Label title = getTitleLabel();
		if (title == null) {
			setTitleLabel(new Label(newValue));
		} else {
			title.setText(newValue);
		}
	}

	/**
	 * @return the <code>Label</code> representing the <code>GroupBox</code>
	 *         's title
	 */
	public Label getTitleLabel() {
		return (Label) getProperty(PROPERTY_TITLE_LABEL);
	}

	/**
	 * Sets the <code>Label</code> representing the <code>GroupBox</code>'s
	 * title
	 */
	public void setTitleLabel(Label newValue) {
		Label oldValue = getTitleLabel();
		remove(oldValue);
		setProperty(PROPERTY_TITLE_LABEL, newValue);
	}

	/**
	 * @see nextapp.echo2.app.Component#validate()
	 */
	public void validate() {
		Label title = getTitleLabel();
		if (title != null && !this.equals(title.getParent())) {
			add(title, 0);
		}
//		if (!this.isEnabled()) {
//			ComponentKit.setEnabled(this, false);
//		}
	}

}
