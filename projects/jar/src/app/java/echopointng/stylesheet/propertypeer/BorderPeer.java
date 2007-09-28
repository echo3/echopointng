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
import java.util.Map;

import nextapp.echo2.app.Border;
import echopointng.stylesheet.CssObjectDeclarationParser;
import echopointng.stylesheet.CssObjectIntrospector;
import echopointng.util.ColorKit;
import echopointng.util.ExtentKit;

/**
 * <code>BorderPeer</code> is used to create Border objects
 */
public class BorderPeer extends AbstractCssPropertyPeer {

    private static final Map STYLE_CONSTANTS;
    private static final Map STYLE_CONSTANT_ALTERNATES;
    static {
        Map constantMap = new HashMap();
        constantMap.put("STYLE_DASHED", new Integer(Border.STYLE_DASHED));
        constantMap.put("STYLE_DOTTED", new Integer(Border.STYLE_DOTTED));
        constantMap.put("STYLE_DOUBLE", new Integer(Border.STYLE_DOUBLE));
        constantMap.put("STYLE_GROOVE", new Integer(Border.STYLE_GROOVE));
        constantMap.put("STYLE_INSET", new Integer(Border.STYLE_INSET));
        constantMap.put("STYLE_NONE", new Integer(Border.STYLE_NONE));
        constantMap.put("STYLE_OUTSET", new Integer(Border.STYLE_OUTSET));
        constantMap.put("STYLE_DASHED", new Integer(Border.STYLE_DASHED));
        constantMap.put("STYLE_RIDGE", new Integer(Border.STYLE_RIDGE));
        constantMap.put("STYLE_SOLID", new Integer(Border.STYLE_SOLID));
        STYLE_CONSTANTS = Collections.unmodifiableMap(constantMap);
        
        constantMap.put("DASHED", new Integer(Border.STYLE_DASHED));
        constantMap.put("DOTTED", new Integer(Border.STYLE_DOTTED));
        constantMap.put("DOUBLE", new Integer(Border.STYLE_DOUBLE));
        constantMap.put("GROOVE", new Integer(Border.STYLE_GROOVE));
        constantMap.put("INSET", new Integer(Border.STYLE_INSET));
        constantMap.put("NONE", new Integer(Border.STYLE_NONE));
        constantMap.put("OUTSET", new Integer(Border.STYLE_OUTSET));
        constantMap.put("DASHED", new Integer(Border.STYLE_DASHED));
        constantMap.put("RIDGE", new Integer(Border.STYLE_RIDGE));
        constantMap.put("SOLID", new Integer(Border.STYLE_SOLID));
        STYLE_CONSTANT_ALTERNATES = Collections.unmodifiableMap(constantMap);
        
    }
	
    /**
	 * @see echopointng.stylesheet.propertypeer.AbstractCssPropertyPeer#getConversionClass()
	 */
	protected Class getConversionClass() {
		return Border.class;
	}
	
	/* --- validity test --- */
	protected boolean canConvert(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return true;
		String[] tokens = CssObjectDeclarationParser.parse(propertyValue);
		if (tokens.length == 4 && tokens[0].equalsIgnoreCase("border")) {
			String size = tokens[1].trim();
			String color = tokens[2].trim();
			String style = tokens[3].trim().toUpperCase();
			
			return ExtentKit.isExtent(size) && ColorKit.isColor(color) && 
				(STYLE_CONSTANTS.containsKey(style) || STYLE_CONSTANT_ALTERNATES.containsKey(style));
		}
		return false;
	}
	/* --- object conversion --- */
	protected Object getObject(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return null;
		String[] tokens = CssObjectDeclarationParser.parse(propertyValue);
		String size = tokens[1].trim();
		String color = tokens[2].trim();
		String style = tokens[3].trim().toUpperCase();
			
		int styleInt = Border.STYLE_NONE;
		if (STYLE_CONSTANTS.containsKey(style))
			styleInt = ((Integer) STYLE_CONSTANTS.get(style)).intValue();
		
		return new Border(ExtentKit.makeExtent(size),ColorKit.makeColor(color),styleInt);
	}
	/* --- string conversion --- */
	protected String getString(CssObjectIntrospector ci, Object object) {
		Border value = (Border) object;

		StringBuffer sb = new StringBuffer();
		sb.append("border(");
		sb.append(String.valueOf(value.getSize()));
		sb.append(",");
		sb.append(ColorKit.makeCSSColor(value.getColor()));
		sb.append(",");
		sb.append(getConstantFromMap(STYLE_CONSTANTS,value.getStyle()));
		sb.append(")");
		return sb.toString();
	}	

}
