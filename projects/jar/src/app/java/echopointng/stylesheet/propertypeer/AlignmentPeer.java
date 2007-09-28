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

import nextapp.echo2.app.Alignment;
import echopointng.stylesheet.CssObjectDeclarationParser;
import echopointng.stylesheet.CssObjectIntrospector;

/**
 * <code>AlignmentPeer</code> converts property value strings
 * into Alignment objects
 */
public class AlignmentPeer extends AbstractCssPropertyPeer {
	
    private static final Map HORIZONTAL_CONSTANTS;
    static {
        Map constantMap = new HashMap();
        constantMap.put("LEADING", new Integer(Alignment.LEADING));
        constantMap.put("TRAILING", new Integer(Alignment.TRAILING));
        constantMap.put("LEFT", new Integer(Alignment.LEFT));
        constantMap.put("CENTER", new Integer(Alignment.CENTER));
        constantMap.put("RIGHT", new Integer(Alignment.RIGHT));
        HORIZONTAL_CONSTANTS = Collections.unmodifiableMap(constantMap);
    }
    
    private static final Map VERTICAL_CONSTANTS;
    static {
        Map constantMap = new HashMap();
        constantMap.put("TOP", new Integer(Alignment.TOP));
        constantMap.put("CENTER", new Integer(Alignment.CENTER));
        constantMap.put("BOTTOM", new Integer(Alignment.BOTTOM));
        VERTICAL_CONSTANTS = Collections.unmodifiableMap(constantMap);
    }
    
    /**
	 * @see echopointng.stylesheet.propertypeer.AbstractCssPropertyPeer#getConversionClass()
	 */
	protected Class getConversionClass() {
		return Alignment.class;
	}

	/* --- validity test --- */
	protected boolean canConvert(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return true;
		String[] tokens = CssObjectDeclarationParser.parse(propertyValue);
		if (tokens.length == 3 && tokens[0].equalsIgnoreCase("alignment")) {
			String h = tokens[1].trim().toUpperCase();
			String v = tokens[2].trim().toUpperCase();
			return HORIZONTAL_CONSTANTS.containsKey(h) && VERTICAL_CONSTANTS.containsKey(v);
		}
		return false;
	}
	/* --- object conversion --- */
	protected Object getObject(CssObjectIntrospector ci, String propertyValue) {
		if (isNullString(propertyValue))
			return null;
		String[] tokens = CssObjectDeclarationParser.parse(propertyValue);
		String hs = tokens[1].trim().toUpperCase();
		String vs = tokens[2].trim().toUpperCase();
		int ha = Alignment.DEFAULT;
		int va = Alignment.DEFAULT;
		if (HORIZONTAL_CONSTANTS.containsKey(hs))
			ha = ((Integer) HORIZONTAL_CONSTANTS.get(hs)).intValue();
		if (VERTICAL_CONSTANTS.containsKey(vs))
			va = ((Integer) VERTICAL_CONSTANTS.get(vs)).intValue();
		return new Alignment(ha,va);
	}
	/* --- string conversion --- */
	protected String getString(CssObjectIntrospector ci, Object object) {
		Alignment value = (Alignment) object;
		StringBuffer sb = new StringBuffer();
		sb.append("alignment(");
		sb.append(getConstantFromMap(HORIZONTAL_CONSTANTS,value.getHorizontal()));
		sb.append(",");
		sb.append(getConstantFromMap(VERTICAL_CONSTANTS,value.getVertical()));
		sb.append(")");
		return sb.toString();
	}	
}
