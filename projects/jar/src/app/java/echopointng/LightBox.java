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
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.Style;

/**
 * The <code>LightBox</code> is a component that covers all visible content
 * with a translucent image. This can be sued to give a visual clue to the user
 * that content cannot be interacted with.
 * <p>
 * This component is design to be used with modal <code>WindowPane</code>s to
 * provide rich feedback to the user that the <code>WindowPane</code> is the
 * only thing they can interact with.
 * 
 */
public class LightBox extends ComponentEx {

	public static final String PROPERTY_TRANSLUCENT_IMAGE = "translucentImage";

	private static final String imagepath = "/echopointng/resource/images/";
	/**
	 * A translucent PNG file with 80% opacity
	 */
	public static final ImageReference TRANSLUCENT_IMAGE_80_PERCENT = new ResourceImageReference(imagepath + "translucent_80_percent.png");

	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(PROPERTY_HIDDEN, false);
		style.setProperty(PROPERTY_TRANSLUCENT_IMAGE, TRANSLUCENT_IMAGE_80_PERCENT);
		DEFAULT_STYLE = style;
	}

	/**
	 * Constructs a <code>LightBox</code>
	 */
	public LightBox() {
		super();
	}
	
	/**
	 * @see nextapp.echo2.app.Component#isValidChild(nextapp.echo2.app.Component)
	 */
	public boolean isValidChild(Component child) {
		return false;
	}
	
	/**
	 * A convenience method for setHidden(false);
	 */
	public void show() {
		setHidden(false);
	}
	
	/**
	 * A convenience method for setHidden(true);
	 */
	public void hide() {
		setHidden(true);
	}
	
	/**
	 * @return the translucent ImageReference to be used as the background of
	 *         the LightBox
	 */
	public ImageReference getTranslucentImage() {
		return (ImageReference) getProperty(PROPERTY_TRANSLUCENT_IMAGE);
	}

	/**
	 * Sets the image to be used as the background of the light box. This image
	 * should be a translucent image such as a PNG and it will be used to cover
	 * any current content on the client when the lightbox is shown.
	 * 
	 * @param newValue -
	 *            the new value of the translucentImage
	 */
	public void setTranslucentImage(ImageReference newValue) {
		setProperty(PROPERTY_TRANSLUCENT_IMAGE, newValue);
	}

}
