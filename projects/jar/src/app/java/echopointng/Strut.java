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
import nextapp.echo2.app.Extent;

/** 
 * <code>Strut</code> is a very simple component that can have
 * a given size and height.  It can contain now child components
 * and only servers to put space between components.
 */

public class Strut extends AbleComponent {

	/**
	 * Constructs a <code>Strut</code> that is
	 * 10px wide by 10px high.
	 */
	public Strut() {
		this(null,null);
	}
	
	/**
	 * Constructs a <code>Strut</code> that is
	 * <code>width</code> pixels wide by <code>height</code> pixels high.
	 */
	public Strut(int width, int height) {
		this(new Extent(width), new Extent(height));
	}
	
	/**
	 * Constructs a <code>Strut</code>
	 *
	 * @param width - the width of the Strut
	 * @param height - the height of the Strut
	 */
	public Strut(Extent width, Extent height) {
		setWidth(width);
		setHeight(height);
	}
	
	/**
	 * <code>Strut</code> is <i>NOT</i> allowed to have any children.
	 * 
	 * @see nextapp.echo2.app.Component#isValidChild(nextapp.echo2.app.Component)
	 */
	public boolean isValidChild(Component child) {
		return false;
	}
}
