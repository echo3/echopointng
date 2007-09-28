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
import nextapp.echo2.app.Insets;
import echopointng.stylesheet.CssObjectDeclarationParser;
import echopointng.stylesheet.CssObjectIntrospector;
import echopointng.util.ExtentKit;

/**
 * <code>InsetsPeer</code> is used to create Insets objects
 */
public class InsetsPeer extends AbstractCssPropertyPeer {

    /**
	 * @see echopointng.stylesheet.propertypeer.AbstractCssPropertyPeer#getConversionClass()
	 */
	protected Class getConversionClass() {
		return Insets.class;
	}
	
	/* --- validity test --- */
	protected boolean canConvert(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return true;
		String[] tokens = CssObjectDeclarationParser.parse(propertyValue);
		if (tokens[0].equalsIgnoreCase("insets")) {
			if (tokens.length != 2 && tokens.length != 3 && tokens.length != 5)
				return false;
			if (tokens.length == 5) {
				String l = tokens[1].trim();
				String t = tokens[2].trim();
				String r = tokens[3].trim();
				String b = tokens[4].trim();
				return ExtentKit.isExtent(l) && ExtentKit.isExtent(t) && ExtentKit.isExtent(r) && ExtentKit.isExtent(b);
			}
			if (tokens.length == 3) {
				String w = tokens[1].trim();
				String h = tokens[2].trim();
				return ExtentKit.isExtent(w) && ExtentKit.isExtent(h);
			}
			if (tokens.length == 2) {
				String w = tokens[1].trim();
				return ExtentKit.isExtent(w);
			}
		}
		return false;
	}
	/* --- object conversion --- */
	protected Object getObject(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return null;
		String[] tokens = CssObjectDeclarationParser.parse(propertyValue);
		if (tokens.length == 5) {
			String l = tokens[1].trim();
			String t = tokens[2].trim();
			String r = tokens[3].trim();
			String b = tokens[4].trim();
			return new Insets(ExtentKit.makeExtent(l), ExtentKit.makeExtent(t), ExtentKit.makeExtent(r), ExtentKit.makeExtent(b));
		} else if (tokens.length == 3) {
			String w = tokens[1].trim();
			String h = tokens[2].trim();
			return new Insets(ExtentKit.makeExtent(w), ExtentKit.makeExtent(h));
		} else  {
			String w = tokens[1].trim();
			return new Insets(ExtentKit.makeExtent(w));
		}
	}
	/* --- string conversion --- */
	protected String getString(CssObjectIntrospector ci, Object object) {
		Insets value = (Insets) object;
		StringBuffer sb = new StringBuffer();
		sb.append("insets(");
		sb.append(value.getLeft());
		sb.append(",");
		sb.append(value.getTop());
		sb.append(",");
		sb.append(value.getRight());
		sb.append(",");
		sb.append(value.getBottom());
		sb.append(")");
		return sb.toString();
	}	

}
