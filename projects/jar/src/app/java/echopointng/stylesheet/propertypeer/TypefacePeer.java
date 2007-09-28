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
import nextapp.echo2.app.Font;
import echopointng.stylesheet.CssObjectDeclarationParser;
import echopointng.stylesheet.CssObjectIntrospector;
import echopointng.util.FontKit;

/**
 * <code>TypefacePeerPeer</code> is used to create Font.Typeface objects
 */
public class TypefacePeer extends AbstractCssPropertyPeer {

	
    /**
	 * @see echopointng.stylesheet.propertypeer.AbstractCssPropertyPeer#getConversionClass()
	 */
	protected Class getConversionClass() {
		return Font.Typeface.class;
	}
	
	/* --- validity test --- */
	protected boolean canConvert(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return true;
		if (CssObjectDeclarationParser.isCssObjectDeclaration(propertyValue)) {
			String[] tokens = CssObjectDeclarationParser.parse(propertyValue);
			if (tokens.length == 2) {
				// anything here is okay
				return tokens[0].equals("typeface");
			}
			return false;
		} else {
			// any type face name is okay really
			return true;
		}
	}
	/* --- object conversion --- */
	protected Object getObject(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return null;
		if (CssObjectDeclarationParser.isCssObjectDeclaration(propertyValue)) {
			String[] tokens = CssObjectDeclarationParser.parse(propertyValue);
			Font.Typeface typeface = FontKit.getSystemTypeface(tokens[1]);
			if (typeface == null)
				typeface = FontKit.makeTypeface(tokens[1]);
			return typeface;
		} else {
			Font.Typeface typeface = FontKit.getSystemTypeface(propertyValue.trim());
			if (typeface == null)
				typeface = FontKit.makeTypeface(propertyValue.trim());
			return typeface;
		}
	}
	/* --- string conversion --- */
	protected String getString(CssObjectIntrospector ci, Object object) {
		Font.Typeface typeFace = (Font.Typeface) object;

		StringBuffer sb = new StringBuffer();
		sb.append("typeface(");
		
		int i = 0;
		while (typeFace != null) {
			if (i > 0)
				sb.append(", ");
			else	
				sb.append("'");
				
			sb.append(typeFace.getName());	
			i++;
			typeFace = typeFace.getAlternate();
			if (typeFace == null)
				sb.append("'");
		}
		sb.append(")");
		return sb.toString();
	}	

}
