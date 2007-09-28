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
import echopointng.stylesheet.CssObjectDeclarationParser;
import echopointng.stylesheet.CssObjectIntrospector;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;

/**
 * <code>ImageReferencePeer</code> maps to ImageReference
 * values of either HttpImageReference or ResourceImageReference
 */
public class ImageReferencePeer extends AbstractCssPropertyPeer {

    /**
	 * @see echopointng.stylesheet.propertypeer.AbstractCssPropertyPeer#getConversionClass()
	 */
	protected Class getConversionClass() {
		return ImageReference.class;
	}
	
	/* --- validity test --- */
	protected boolean canConvert(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return true;
		String[] tokens = CssObjectDeclarationParser.parse(propertyValue);
		if (tokens[0].equalsIgnoreCase("image") || tokens[0].equalsIgnoreCase("img")) {
			if (tokens.length != 2 && tokens.length != 4)
				return false;
			if (tokens.length == 4) {
				return isExtent(tokens[2]) && isExtent(tokens[3]);
			}
			return true;
		}
		// we allow 'resimage(url)' 'resimage(url,contenttype)' and 'resimage(url,contenttype, width, height)'  
		if (tokens[0].indexOf("resimage(") == 0 || tokens[0].indexOf("resimg(") == 0) {
			if (tokens.length != 2 && tokens.length != 3 && tokens.length != 5)
				return false;
			if (tokens.length == 5) {
				return isExtent(tokens[3]) && isExtent(tokens[4]);
			}
			return true;
		}
		return false;
	}
	/* --- object conversion --- */
	protected Object getObject(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return null;

		String[] tokens = CssObjectDeclarationParser.parse(propertyValue);
		if (tokens[0].equalsIgnoreCase("image") || tokens[0].equalsIgnoreCase("img")) {
			// HttpImageRef
			if (tokens.length == 4)
				return getHttpImageRef(tokens[1].trim(), tokens[2].trim(), tokens[3].trim());
			else
				return getHttpImageRef(tokens[1].trim(),null,null);
		} else {
			// ResourceImageRef
			if (tokens.length == 5)
				return getResImageRef(tokens[1].trim(), tokens[2].trim(), tokens[3].trim(),tokens[4].trim());
			else if (tokens.length == 3)
				return getResImageRef(tokens[1].trim(),tokens[2].trim(),null,null);
			else
				return getResImageRef(tokens[1].trim(),null,null,null);
		}
	}

	/* --- string conversion --- */
	protected String getString(CssObjectIntrospector ci, Object object) {
		ImageReference value = (ImageReference) object;
		StringBuffer sb = new StringBuffer();
		if (value instanceof HttpImageReference) {
			sb.append("image(");	
			sb.append("'");
			sb.append(((HttpImageReference) value).getUri());		
			sb.append("'");
		} else if (value instanceof ResourceImageReference) {
			sb.append("resimage('");	
			sb.append("'");
			sb.append(((ResourceImageReference)value).getResource());
			sb.append("'");
			sb.append(",");
			sb.append(((ResourceImageReference)value).getContentType());
		} else {
			sb.append("'");
			sb.append("??? unhandled image reference type");
			sb.append("'");
		}
		if (value.getWidth() != null && value.getHeight() != null) {
			sb.append(",");
			sb.append(value.getWidth());
			sb.append(",");
			sb.append(value.getHeight());
		}
		sb.append(")");	
		return sb.toString();
	}
	
	/**
	 * Returns a HttpImageReference from the given url strings
	 * @param uri - the URI of the HttpImageReference
	 * @param width - the optional width string
	 * @param height - the optional height string
	 * @return an HttpImageReference
	 */
	private static HttpImageReference getHttpImageRef(String uri, String width, String height) {
		String theUri = uri.trim();
		if (isNullString(uri))
			theUri = null;
		
		if (width != null && height != null) {
			Extent w = makeExtent(width);
			Extent h = makeExtent(height);
			return new HttpImageReference(theUri, w, h);
		} else {
			return new HttpImageReference(theUri);
		}
	}

	/**
	 * Returns a ResourceImageReference from a specified parameters
	 * @param uri - the uri to the resource
	 * @param contentType - the optional content type
	 * @param width - the optional width extent
	 * @param height - the optional height extent
	 * @return a built ResourceImageReference
	 */
	private ResourceImageReference getResImageRef(String uri, String contentType, String width, String height) {
		String theUri = uri.trim();
		if (isNullString(uri))
			theUri = null;

		if (width != null && height != null) {
			Extent w = makeExtent(width);
			Extent h = makeExtent(height);
			return new ResourceImageReference(theUri, contentType, w, h);
		} else if (contentType != null){
			return new ResourceImageReference(theUri,contentType);
		} else {
			return new ResourceImageReference(theUri);
		}
	}

}
