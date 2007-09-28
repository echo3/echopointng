package echopointng.tabbedpane;

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

import echopointng.TabbedPane;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.ImageReference;

/**
 * <code>TabImageRenderer</code> is an interface for producing "tab images"
 * within a TabbedPane.
 */
public interface TabImageRenderer {
	/**
	 * This returns a "lead in" image that will be place to the left of all tab
	 * images. If there is no lead in image, then this method should return
	 * null.
	 * 
	 * @param tp
	 *            the TabbedPane in question
	 * @return the lead in image or null if there isnt one
	 */
	public ImageReference getLeadInImage(TabbedPane tp);

	/**
	 * This returns a "lead out" image that will be place to the right of all
	 * tab images. If there is no lead out image, then this method should return
	 * null.
	 * 
	 * @param tp
	 *            the TabbedPane in question
	 * @return the lead out image or null if there isnt one
	 */
	public ImageReference getLeadOutImage(TabbedPane tp);

	/**
	 * This returns an ImageReference to be used as the tab's representation.
	 * <p>
	 * This MUST NOT be null since the TabbedPane requires an image if a
	 * TabImageRenderer is in use.
	 * 
	 * @param tp
	 *            the TabbedPane in question
	 * @param index -
	 *            the index of the tab
	 * 
	 * @param tabComponent -
	 *            the component that represents the tab at <code>index</code>
	 * 
	 * @param isSelected -
	 *            true if the tab is the selected one
	 * 
	 * @return the <code>ImageReference</code> to use as the tab image. This
	 *         must NOT be null.
	 */
	public ImageReference getTabImage(TabbedPane tp, int index, Component tabComponent, boolean isSelected);

	/**
	 * This is called the indicate the width of any border around the image.
	 * This is used by the TabbedPane to decide whether to place a 3 sided
	 * border under the provided images.
	 * 
	 * @return the width of any border around the images. Can be zero!
	 */
	public int getImageBorderWidth();

	/**
	 * This is called the indicate the color of any border around the image.
	 * This is used by the TabbedPane to decide whether to place a 3 sided
	 * border under the provided images.
	 * 
	 * @return the color of any border around the images. Can be null!
	 */
	public Color getImageBorderColor();
}
