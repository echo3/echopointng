package echopointng.stylesheet.propertypeer;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.ImageReference;
import echopointng.stylesheet.CssObjectIntrospector;
import echopointng.stylesheet.CssObjectDeclarationParser;
import echopointng.util.ExtentKit;

/**
 * <code>FillImagePeer</code> is used to create FillImage objects
 */
public class FillImagePeer extends AbstractCssPropertyPeer {

	private static final Map REPEAT_CONSTANTS;
	static {
		Map constantMap = new HashMap();
		constantMap.put("NO_REPEAT", new Integer(FillImage.NO_REPEAT));
		constantMap.put("REPEAT", new Integer(FillImage.REPEAT));
		constantMap.put("REPEAT_HORIZONTAL", new Integer(FillImage.REPEAT_HORIZONTAL));
		constantMap.put("REPEAT_VERTICAL", new Integer(FillImage.REPEAT_VERTICAL));
		REPEAT_CONSTANTS = Collections.unmodifiableMap(constantMap);
	}

	/**
	 * @see echopointng.stylesheet.propertypeer.AbstractCssPropertyPeer#getConversionClass()
	 */
	protected Class getConversionClass() {
		return FillImage.class;
	}

	/* --- validity test --- */
protected boolean canConvert(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return true;
		String tokens[] = CssObjectDeclarationParser.parse(propertyValue);
		if (! tokens[0].equalsIgnoreCase("fillimage"))
			return false;
		
		ImageReferencePeer imagePeer = new ImageReferencePeer();
		if (tokens.length == 2) {
			String objType = tokens[0]; 
			String imageRef = tokens[1];
			
			return objType.toLowerCase(Locale.ENGLISH).equalsIgnoreCase("fillimage") &&
					imagePeer.canConvert(ci,imageRef);
			
		} else if (tokens.length == 5) {
			String objType = tokens[0]; 
			String imageRef = tokens[1];
			String horizontalOffset = tokens[2];
			String verticalOffset = tokens[3];
			
			return objType.toLowerCase(Locale.ENGLISH).equals("fillimage") &&
					imagePeer.canConvert(ci,imageRef) &&
					ExtentKit.isExtent(horizontalOffset) &&
					ExtentKit.isExtent(verticalOffset);
		} else {
			return false;
		}
	}
	/* --- object conversion --- */
	protected Object getObject(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return null;
		String tokens[] = CssObjectDeclarationParser.parse(propertyValue);
		ImageReferencePeer imagePeer = (ImageReferencePeer) getPeer(ImageReference.class);
		if (tokens.length == 2) {
			String imageRef = tokens[1];
			ImageReference imageRefObj = (ImageReference) imagePeer.getObject(ci, imageRef);
			return new FillImage(imageRefObj);

		} else if (tokens.length == 5) {
			String imageRef = tokens[1];
			Extent horizontalOffset = ExtentKit.makeExtent(tokens[2]);
			Extent verticalOffset = ExtentKit.makeExtent(tokens[3]);
			int repeat = getIntFromMap(REPEAT_CONSTANTS, tokens[4].toUpperCase(Locale.ENGLISH));

			ImageReference imageRefObj = (ImageReference) imagePeer.getObject(ci, imageRef);
			return new FillImage(imageRefObj, horizontalOffset, verticalOffset, repeat);

		} else {
			return null;
		}
	}

	/* --- string conversion --- */
	protected String getString(CssObjectIntrospector ci, Object object) {
		FillImage value = (FillImage) object;
		ImageReferencePeer imagePeer = (ImageReferencePeer) getPeer(ImageReference.class);

		StringBuffer sb = new StringBuffer();
		sb.append("fillimage(");
		sb.append(imagePeer.getString(ci, value.getImage()));
		sb.append(",");
		sb.append(String.valueOf(value.getHorizontalOffset()));
		sb.append(",");
		sb.append(String.valueOf(value.getVerticalOffset()));
		sb.append(",");
		sb.append(getConstantFromMap(REPEAT_CONSTANTS, value.getRepeat()));
		sb.append(")");
		return sb.toString();
	}

}
