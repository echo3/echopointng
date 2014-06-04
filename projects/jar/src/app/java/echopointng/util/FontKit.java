package echopointng.util;
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

import echopointng.util.collections.ConcurrentReaderHashMap;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A utility to class to help with Font manipulation
 *
 * @author Brad Baker 
 */
public class FontKit {

	/* our static cache of Font objects by Font String representation */ 
	private static Map fontMap = new ConcurrentReaderHashMap();
	
   private static final Map FONTSTYLE_TEXT_TO_CONSTANT;
    static {
        Map constantMap = new HashMap();
        constantMap.put("PLAIN", new Integer(Font.PLAIN));
        constantMap.put("FONT.PLAIN", new Integer(Font.PLAIN));
        constantMap.put("BOLD", new Integer(Font.BOLD));
        constantMap.put("FONT.BOLD", new Integer(Font.BOLD));
        constantMap.put("ITALIC", new Integer(Font.ITALIC));
        constantMap.put("FONT.ITALIC", new Integer(Font.ITALIC));
        constantMap.put("LINETHROUGH", new Integer(Font.LINE_THROUGH));
        constantMap.put("FONT.LINETHROUGH", new Integer(Font.LINE_THROUGH));
        constantMap.put("OVERLINE", new Integer(Font.OVERLINE));
        constantMap.put("FONT.OVERLINE", new Integer(Font.OVERLINE));
        constantMap.put("UNDERLINE", new Integer(Font.UNDERLINE));
        constantMap.put("FONT.UNDERLINE", new Integer(Font.UNDERLINE));
        FONTSTYLE_TEXT_TO_CONSTANT = Collections.unmodifiableMap(constantMap);
    }
    

    private static final Map TYPEFACE_TEXT_TO_CONSTANT;
    static {
        Map constantMap = new HashMap();
        constantMap.put("HELVETICA", Font.HELVETICA);
        constantMap.put("ARIAL", Font.ARIAL);
        constantMap.put("VERDANA", Font.VERDANA);
        constantMap.put("TIMES", Font.TIMES);
        constantMap.put("TIMES ROMAN", Font.TIMES_ROMAN);
        constantMap.put("TIMES NEW ROMAN", Font.TIMES_NEW_ROMAN);
        constantMap.put("COURIER", Font.COURIER);
        constantMap.put("COURIER NEW", Font.COURIER_NEW);
        TYPEFACE_TEXT_TO_CONSTANT = Collections.unmodifiableMap(constantMap);
    }

    
	/** not instantiable */
	private FontKit() {
	}
	
	/**
	 * Creates an AWT font object from a Echo font object.  This is
	 * useful for drawing on AWT images.
	 * 
	 * @param echoFont - the nextapp.echo2.app.Font object to convert
	 * @param awtDefaultFont - a default AWT font to use if the conversion cant be done.
	 * @return - a new java.awt.Font object base don echoFont.  
	 */
	public static java.awt.Font makeAwtFont(Font echoFont, java.awt.Font awtDefaultFont) {
		if (echoFont == null)
			return awtDefaultFont;
		else {
			int size = echoFont.getSize() == null ? 12 : echoFont.getSize().getValue();
			int style = 0;
			if (echoFont.isBold())
				style |= java.awt.Font.BOLD;
			if (echoFont.isItalic())
				style |= java.awt.Font.ITALIC;
			if (echoFont.isUnderline());
			if (style == 0)
				style = java.awt.Font.PLAIN;

			return new java.awt.Font(echoFont.getTypeface().getName(), style, size);
		}
	}
	
	/**
	 * Makes a W3C CSS font string in the special format
	 * <p>
	 * <i>fontstyle</i>, <i>fontsize</i>, <i>fontnames</i>
	 * <p>
	 * @param font - the font to represent as a W3C CSS string
	 * @return the W3C CSS string represenation
	 */
	public static String makeCSSFont(Font font) {
		StringBuffer sb = new StringBuffer();
		if (font.isBold())
			sb.append("bold ");
		if (font.isItalic())
			sb.append("italic ");
		if (font.isUnderline())
			sb.append("underline ");

		sb.append(font.getSize());
		sb.append(" ");
		
		int i = 0;
		Font.Typeface face = font.getTypeface();
		while (face != null) {
			if (i > 0)
				sb.append(", ");
			sb.append("'");
			sb.append(face.getName());	
			sb.append("'");
			face = face.getAlternate();
			i++;
		}
		return sb.toString();	
	}
	
	/**
	 * Adds bold to a Component's font.
	 * <p>
	 * If the component has no font, a <code>findFont</code> is performed and
	 * the result is set in as the font.
	 * <p>
	 * @see FontKit#addBold(Font)
	 * 
	 * @param c - the component in question
	 */
	public static void addBold(Component c) {
		Font font = findFont(c);
		font = addBold(font);
		c.setFont(font);
	}

	/**
	 * Adds italic to a Component's font.
	 * <p>
	 * If the component has no font, a <code>findFont</code> is performed and
	 * the result is set in as the font.
	 * <p>
	 * @see FontKit#addItalic(Font)
	 * 
	 * @param c - the component in question
	 */
	public static void addItalic(Component c) {
		Font font = findFont(c);
		c.setFont(addItalic(font));	
	}

	/**
	 * Adds underline to a Component's font.
	 * <p>
	 * If the component has no font, a <code>findFont</code> is performed and
	 * the result is set in as the font.
	 * <p>
	 * @see FontKit#addUnderline(Font)
	 * 
	 * @param c - the component in question
	 */
	public static void addUnderline(Component c) {
		Font font = findFont(c);
		c.setFont(addUnderline(font));	
	}

	/**
	 * Adds the specified <code>style</code> to a Component's font.
	 * <p>
	 * If the component has no font, a <code>findFont</code> is performed and
	 * the result is set in as the font.
	 * <p>
	 * @see FontKit#addStyle(Font, int)
	 * 
	 * @param c - the component in question
	 */
	public static void addStyle(Component c, int style) {
		Font font = findFont(c);
		c.setFont(addStyle(font,style));	
	}

	/**
	 * Sets a component's font smaller or large by the specified 
	 * delta amount.
	 * <p>
	 * If the component has no font, a <code>findFont</code> is performed and
	 * the result is set in as the font.
	 * <p>
	 * @see FontKit#addSize(Font, int)
	 * 
	 * @param c - the component in question
	 * @param sizeDelta - a positive or negative delta amount
	 */
	public static void addSize(Component c, int sizeDelta) {
		Font font = findFont(c);
		c.setFont(addSize(font,sizeDelta));
	}

	/**
	 * Sets Component's font to bold.
	 * <p>
	 * If the component has no font, a <code>findFont</code> is performed and
	 * the result is set in as the font.
	 * <p>
	 * @see FontKit#setStyle(Font, int)
	 * 
	 * @param c - the component in question
	 */
	public static void setBold(Component c) {
		Font font = findFont(c);
		c.setFont(setStyle(font, Font.BOLD));	
	}

	/**
	 * Sets Component's font to italic.
	 * <p>
	 * If the component has no font, a <code>findFont</code> is performed and
	 * the result is set in as the font.
	 * <p>
	 * @see FontKit#setStyle(Font, int)
	 * 
	 * @param c - the component in question
	 */
	public static void setItalic(Component c) {
		Font font = findFont(c);
		c.setFont(setStyle(font, Font.ITALIC));	
	}

	/**
	 * Sets Component's font to underline.
	 * <p>
	 * If the component has no font, a <code>findFont</code> is performed and
	 * the result is set in as the font.
	 * <p>
	 * @see FontKit#setStyle(Font, int)
	 * 
	 * @param c - the component in question
	 */
	public static void setUnderline(Component c) {
		Font font = findFont(c);
		c.setFont(setStyle(font, Font.UNDERLINE));	
	}
	

	/**
	 * Sets the size of a Component's font.
	 * <p>
	 * If the component has no font, a <code>findFont</code> is performed and
	 * the result is set in as the font.
	 * <p>
	 * @see FontKit#setSize(Font, int)
	 * 
	 * @param c - the component in question
	 * @param size - the new font size
	 */
	public static void setSize(Component c, int size) {
		Font font = findFont(c);
		c.setFont(addSize(font,size));
	}

	

	/**
	 * Searchs the heirarchy tree of the component and finds the first
	 * non null Font object.  It will return <code>Font(Font.SANS_SERIF,Font.PLAIN,new Extent(10,Extent.PT)</code> 
	 * if no ancestor components have a Font set.
	 * 
	 */
	public static Font findFont(Component comp) {
		while (comp != null) {
			Font fnt = comp.getFont();
			if (fnt != null)
				return fnt;
			comp = comp.getParent();
		}
		return new Font(Font.SANS_SERIF,Font.PLAIN,new Extent(10,Extent.PT));
	}

	/**
	 * Returns the Font value of the given Font string representation.  
	 * <p>
	 * The allowable forms are : <p>
	 * 	 - fontName,fontStyle,fontSize<br>
	 * 	 - font( fontName, fontStyle, fontSize)<br>
	 * <p>
	 * where 
	 * <p>
	 * fontName  - 	eg. Verdana or 'Times New Roman'.
	 * 			   	If multiple font names are specified or there is
	 * 				white space in the font name, then it must be enclosed
	 * 				in single quotes and commas eg. 'Verdana, Times New Roman, Tahoma'.
	 * <br>  
	 * fontStyle - PLAIN|BOLD|ITALIC|UNDERLINE|OVERLINE|LINETHROUGH or nothing! (this is case insenstive)
	 * <br>
	 * fontSize - an integer size value or an Extent value.  (In the case where
	 * 				only an integer is specified, the default Extent units 
	 * 				are points (pt).
	 * <p>
	 * Examples :<br>
	 * <blockquote><pre>
	 * 	Verdana,,9 									- is a legal font string
	 * 	Verdana,PLAIN,9 							- is a legal font string
	 * 	Verdana,plain,9 							- is a legal font string
	 * 	Verdana,plain,9em 							- is a legal font string
	 * 	Verdana,plain,9pt 							- is a legal font string
	 * 	Verdana,bold|italic,9						- is a legal font string
	 * 	'Times New Roman',plain,9 					- is a legal font string
	 * 	'Verdana, Times New Roman',plain,9 			- is a legal font string
	 * 
	 * 	font(Verdana,,9)							- is a legal font string
	 * 	font(Verdana,PLAIN,9pt)						- is a legal font string
	 * 	font(Verdana,plain,9)						- is a legal font string
	 * 	font(Verdana,bold|italic,9em)				- is a legal font string
	 * 	font('Verdana, Times New Roman',plain,9)	- is a legal font string
	 * 
	 * 	Verdana, 3, 9								- is an ILLEGAL font string
	 * 	Verdana, Times New Roman,PLAIN,9			- is an ILLEGAL font string
	 * 	font(Verdana, 3, 9)							- is an ILLEGAL font string
	 * 	font(Verdana, Times New Roman,PLAIN,9)		- is an ILLEGAL font string
	 * </pre></blockquote>
	 * <p>
	 * The results of this operations is cached in a global static
	 * cache, so that Font objects can be re-used.  This is okay 
	 * since Font objects are immutable once created. 
	 * 
	 * @param fontString - the font string in question
	 * @return a new Font object	 
	 * @throws IllegalArgumentException - if the fontString is in an invalid format
	 */	
	public static Font makeFont(String fontString) {
		if (fontString == null)
			throw new IllegalArgumentException("The fontString must be non null");
					
		Font font = _getCachedFont(fontString);
		if (font != null)
			return font;
		
		if (! isFont(fontString))
			throw new IllegalArgumentException("The font string is invalid : " + fontString);
			
		int index = 0;
		String tokens[] = TokenizerKit.tokenize(fontString, "(,)|");
		if (fontString.indexOf("font(") == 0)
			index = 1;
		
		// font names
		String fontName = tokens[index].trim();
		Font.Typeface fontNameVal = makeTypeface(fontName);

		// font styles
		int fontStyleVal = 0;
		String fontStyleString = tokens[++index];
		for (int i = index; i < tokens.length; i++) {
			fontStyleString = tokens[i].trim();
			if (_isInteger(fontStyleString) || ExtentKit.isExtent(fontStyleString))
				break;
			String arr[] = TokenizerKit.tokenize(fontStyleString,"|");
			for (int j = 0; j < arr.length; j++) {
				fontStyleString = arr[j].trim();
				int k = _getFontStyle(fontStyleString);
				if (k >= 0) {
					fontStyleVal |= k;
				}
			}
		}

		// font size
		String fontSize = fontStyleString;
		Font newFont;
		if (ExtentKit.isExtent(fontSize)) {
			newFont = new Font(fontNameVal, fontStyleVal, ExtentKit.makeExtent(fontSize));
		} else {
			int fontSizeVal = Integer.parseInt(fontSize);
			newFont = new Font(fontNameVal, fontStyleVal, new Extent(fontSizeVal,Extent.PT));
		}
		return _putCachedFont(fontString, newFont);
	}
	
	/**
	 * Shortcut synonym for makeFont(fontString);
	 * <p>
	 * @see FontKit#makeFont(String)
	 */
	public static Font font(String fontString) {
		return makeFont(fontString);
	}
	
	/**
	 * Returns true if the <code>fontString</code> is a valid 
	 * representation of a Font value.
	 * <p>
	 * The allowable forms are : <p>
	 * 	 - fontName,fontStyle,fontSize<br>
	 * 	 - font( fontName, fontStyle, fontSize)<br>
	 * <p>
	 * where 
	 * <p>
	 * fontName  - 	eg. Verdana or 'Times New Roman'.
	 * 			   	If multiple font names are specified or there is
	 * 				white space in the font name, then it must be enclosed
	 * 				in single quotes and commas eg. 'Verdana, Times New Roman, Tahoma'.
	 * <br>  
	 * fontStyle - PLAIN|BOLD|ITALIC|UNDERLINE|OVERLINE|LINETHROUGH or nothing! (this is case insenstive)
	 * <br>
	 * fontSize - an integer size value or an Extent value.  (In the case where
	 * 				only an integer is specified, the default Extent units 
	 * 				are points (pt).
	 * <p>
	 * Examples :<br>
	 * <blockquote><pre>
	 * 	Verdana,,9 									- is a legal font string
	 * 	Verdana,PLAIN,9 							- is a legal font string
	 * 	Verdana,plain,9 							- is a legal font string
	 * 	Verdana,bold|italic,9						- is a legal font string
	 * 	'Times New Roman',plain,9 					- is a legal font string
	 * 	'Verdana, Times New Roman',plain,9 			- is a legal font string
	 * 
	 * 	font(Verdana,,9)							- is a legal font string
	 * 	font(Verdana,PLAIN,9)						- is a legal font string
	 * 	font(Verdana,plain,9)						- is a legal font string
	 * 	font(Verdana,bold|italic,9)					- is a legal font string
	 * 	font('Verdana, Times New Roman',plain,9)	- is a legal font string
	 * 
	 * 	Verdana, 3, 9								- is an ILLEGAL font string
	 * 	Verdana, Times New Roman,PLAIN,9			- is an ILLEGAL font string
	 * 	font(Verdana, 3, 9)							- is an ILLEGAL font string
	 * 	font(Verdana, Times New Roman,PLAIN,9)		- is an ILLEGAL font string
	 * </pre></blockquote>
	 *  
	 * @param fontString - the font string representation to check
	 * @return true if the string is in the correct format
	 */
	public static boolean isFont(String fontString) {
		if (fontString == null)
			return false;
		
		int index = 0;
		String tokens[] = TokenizerKit.tokenize(fontString, "(,)|");
		if (fontString.indexOf("font(") == 0)
			index = 1;

		if (tokens.length < 2)
			return false;

		// font name is ok.  Technically anything is OK
		
		// font styles
		String font = tokens[++index];
		for (;index < tokens.length; index++) {
			font = tokens[index].trim();
			if (_isInteger(font) || ExtentKit.isExtent(font))
				break;
			String arr[] = TokenizerKit.tokenize(font,"|");
			for (int i = 0; i < arr.length; i++) {
				font = arr[i].trim();
				if (_getFontStyle(font) == -1) {
					return false;
				}
			}
		}
		// font size
		String fontSize = font;
		if (! ExtentKit.isExtent(fontSize)) {
			if (!_isInteger(fontSize))
				return false;
		}

		if (index != tokens.length-1)
			return false;
			
		return true;
	}
	
	/**
	 * This will return a Font.Typeface object by first rtying to match
	 * the names against System provided ones and failing that it will
	 * create a font.Typeface for you. 
	 * <p>
	 * Multi named type faces must be encluded in spaces.
	 * <p>  
	 * Valid typeface name forms are :
	 * <pre>
	 * 'Font1, Font 2, Font2'
	 * 'Font1'
	 * Font1
	 * <pre>
	 * 
	 * @param typeFaceNames - the name(s) of the intended typeface
	 */
	public static Font.Typeface makeTypeface(String typeFaceNames) {
		if (typeFaceNames.charAt(0) == '\'' && typeFaceNames.charAt(typeFaceNames.length()-1) == '\'') {
			typeFaceNames = typeFaceNames.substring(1,typeFaceNames.length()-1);
		}
		String[] tokens = TokenizerKit.tokenizeStrict(typeFaceNames,",");
		if (tokens.length == 0)
			return null;
		Font.Typeface typeFace;
		typeFace = getSystemTypeface(tokens[0].toUpperCase(Locale.ENGLISH));
		//
		// if we have only one token and it matches a System
		// Typeface, then we will go with it
		if (tokens.length == 1 && typeFace != null) {
			return typeFace;
		} else {
			if (_exactMatchingTypeFace(typeFace,tokens)) {
				return typeFace;
			} else {
				typeFace = null;
				for (int i = tokens.length-1; i >= 0; i--) {
					typeFace = new Font.Typeface(tokens[i],typeFace);
				}
				return typeFace;
			}
		}
	}

	/*
	 * Returns true if the type face provided exactly matches the list of font names
	 */
	private static boolean _exactMatchingTypeFace(Font.Typeface typeFace, String[] fontNames) {
		if (typeFace == null)
			return false;
		int i = 0;
		while (typeFace != null) {
			if (i >= fontNames.length)
				return false;
			if (! fontNames[i].equalsIgnoreCase(typeFace.getName())) {
				return false;
			}
			typeFace = typeFace.getAlternate();
			i++;
		}
		return true;
	}

	/*
	 * Gets a Font from the cache if its matches the font string
	 * It may be null if is not been cached.  The strings are
	 * always stored in lower case for consistency
	 */
	private static Font _getCachedFont(String fontString) {
		fontString = fontString.trim().toLowerCase(Locale.ENGLISH);
		Font font = (Font) fontMap.get(fontString);
		return font;
	}
	
	/*
	 * Puts a Font into the shared cache and then returns it
	 */
	private static Font _putCachedFont(String fontString, Font font) {
		fontString = fontString.trim().toLowerCase(Locale.ENGLISH);
		fontMap.put(fontString, font);
		return font;
	}
	 
	
	/*
	 * Well is it an integer?
	 */
	private static boolean _isInteger(String propertyValue) {
		try {
			Integer.parseInt(propertyValue.trim());
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * Makes a string representation of a font in the format
	 * <p>
	 * 	 font(fontName, fontStyle, fontSize)
	 * <p>
	 * where :
	 * <p>
	 * fontName  - is a font name such as 'Verdana' or 'Times New Roman'.  
	 * fontStyle - PLAIN|BOLD|ITALIC|UNDERLINE
	 * fontSize - an integer size value or an Extent value.  (In the case where
	 * 				only an integer is specified, the default Extent units 
	 * 				are points (pt).
	 * 
	 * @param font - the font to make into a String representation
	 * @return the string representation of the font
	 */
	public static String makeFontString(Font font) {
		return _buildFontString(font.getTypeface(),_getStyle(font),font.getSize());
	}

	/**
	 * Returns one the standard Echo Font.Typefaces if the name string
	 * matches the first Typeface in the chain.
	 * 
	 * @param name - the font typeface name to match.
	 * @return a TypeFace of null if a system one cannot be found
	 */
    public static Font.Typeface getSystemTypeface(String name) {
        if (TYPEFACE_TEXT_TO_CONSTANT.containsKey(name.toUpperCase(Locale.ENGLISH))) {
            return (Font.Typeface) TYPEFACE_TEXT_TO_CONSTANT.get(name.toUpperCase(Locale.ENGLISH));
        } else {
            return null;
        }
    }
	
    
    /*
     * Returns a font style integer value of -1 if it cant be found
     */
    private static int _getFontStyle(String styleName) {
    	Integer styleInt = (Integer) FONTSTYLE_TEXT_TO_CONSTANT.get(styleName.toUpperCase(Locale.ENGLISH));
    	if (styleInt == null)
    		return -1;
    	return styleInt.intValue();
    }
	
	/* 
	 * Builds a Font string from the given font attributes,  This
	 * can then be used for font caching etc.. as well as returned
	 * as a Font String representation  
	 * */
	private static String _buildFontString(Font.Typeface typeFace, int style, Extent sizeExtent) {
		StringBuffer sb = new StringBuffer();
		sb.append("font(");
		
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

		boolean isplain = true;
		boolean needsOr = false;
		sb.append(",");
		if ((style & Font.BOLD) == Font.BOLD) {
			if (needsOr) sb.append("|");
			sb.append("BOLD");
			needsOr = true;
			isplain = false;
		}
		if ((style & Font.ITALIC) == Font.ITALIC) {
			if (needsOr) sb.append("|");
			sb.append("ITALIC");
			needsOr = true;
			isplain = false;
		}
		if ((style & Font.UNDERLINE) == Font.UNDERLINE) {
			if (needsOr) sb.append("|");
			sb.append("UNDERLINE");
			needsOr = true;
			isplain = false;
		}
		if ((style & Font.OVERLINE) == Font.OVERLINE) {
			if (needsOr) sb.append("|");
			sb.append("OVERLINE");
			needsOr = true;
			isplain = false;
		}
		if ((style & Font.LINE_THROUGH) == Font.LINE_THROUGH) {
			if (needsOr) sb.append("|");
			sb.append("LINETHROUGH");
			needsOr = true;
			isplain = false;
		}
		if (isplain)
			sb.append("PLAIN");

		if (sizeExtent != null) {
			sb.append(",");
			sb.append(sizeExtent.toString());
		}
		sb.append(")");
		return sb.toString();	
	}	

	/*
	 * Builds a Font object from a valid font string, which is
	 * then cached in our global Font cache.
	 */
	private static Font _buildFontObj(Font.Typeface typeface, int style, Extent sizeExtent) {
		String fontString = _buildFontString(typeface,style,sizeExtent);
		return makeFont(fontString);
	}


	/* 
	 * Adds style bits to an exisitng font  
	 */
	static private int _addFontStyle(Font font, int newStyle) {
		int style = Font.PLAIN;
		if (font.isBold())
			style |= Font.BOLD;
		if (font.isItalic())
			style |= Font.ITALIC;
		if (font.isUnderline())
			style |= Font.UNDERLINE;

		style |= newStyle;
		return style;
	}

	/*
	 * Returns the style bits of the font.
	 */
	static private int _getStyle(Font font) {
		return _addFontStyle(font,0);
	}

	/**
	 * Adds the Font.BOLD attribute to an exisitng Font object
	 * and returns a new Font.
	 * <p>
	 * @param font - the font in question
	 * @return  - a new Font object
	 */
	public static Font addBold(Font font) {
		return addStyle(font,Font.BOLD);
	}

	/**
	 * Adds the Font.ITALIC attribute to an exisitng Font object
	 * and returns a new Font.
	 * <p>
	 * @param font - the font in question
	 * @return  - a new Font object
	 */
	public static Font addItalic(Font font) {
		if (font == null)
			return null;
		return addStyle(font,Font.ITALIC);
	}

	/**
	 * Adds the Font.UNDERLINE attribute to an exisitng Font object
	 * and returns a new Font.
	 * <p>
	 * @param font - the font in question
	 * @return  - a new Font object
	 */
	public static Font addUnderline(Font font) {
		return addStyle(font,Font.UNDERLINE);
	}

	/**
	 * Adds the specified <code>style</code> to the Font
	 * and returns a new Font.
	 * <p>
	 * @param font - the font in question
	 * @param style - the new font style to added to the font
	 * @return  - a new Font object
	 */
	public static Font addStyle(Font font, int style) {
		if (font == null)
			return null;
		return _buildFontObj(font.getTypeface(),_addFontStyle(font, style),font.getSize());
	}

	/**
	 * Makes a font smaller or larger by adding the specified amount to
	 * its current font size. The Font is made smaller if a negative
	 * value is used.
	 * 
	 * @param font - the font in question
	 * @param sizeDelta - the size delta to add to the current size
	 * @return  - a new Font object
	 *
	 */
	public static Font addSize(Font font, int sizeDelta) {
		if (font == null)
			return null;
		Extent now = font.getSize();
		if (now != null)
			now = new Extent(now.getValue()+sizeDelta, now.getUnits());
		return _buildFontObj(font.getTypeface(),_getStyle(font),now);
	}

	/**
	 * Returns a new Font object, based on <code>font</code>, that
	 * has the specified <code>size</code>.
	 *   
	 * @param font - the font to use as a template
	 * @param size - the new size
	 * @return a new Font object with the new size
	 */
	public static Font setSize(Font font, int size) {
		if (font == null)
			return null;
		Extent now = font.getSize();
		if (now != null)
			now = new Extent(size, now.getUnits());
		return _buildFontObj(font.getTypeface(),_getStyle(font),now);
	}

	/**
	 * Returns a new Font object, based on <code>font</code>, that
	 * has the specified <code>style</code>.
	 *   
	 * @param font - the font to use as a template
	 * @param style - the new style
	 * @return a new Font object with the new style
	 */
	public static Font setStyle(Font font, int style) {
		if (font == null)
			return null;
		return _buildFontObj(font.getTypeface(),style,font.getSize());
	}
	
	/**
	 * Returns a new Font object, based on <code>font</code>, that
	 * has the specified <code>typeFace</code>.
	 *   
	 * @param font - the font to use as a template
	 * @param typeFace - the new TypeFace to use
	 * @return a new Font object with the new font names
	 */
	public static Font setNames(Font font, Font.Typeface typeFace) {
		if (font == null)
			return null;
		return _buildFontObj(typeFace,_getStyle(font),font.getSize());
	}

	/**
	 * Returns a new Font object, based on <code>font</code>, that
	 * has the specified <code>fontName</code>.
	 * <p>
	 * If <code>fontName</code> has whitespace in it, then
	 * you need to surround it in single quotes.
	 *   
	 * @param font - the font to use as a template
	 * @param fontName - the new font name to use
	 * @return a new Font object with the new font name
	 */
	public static Font setName(Font font, String fontName) {
		if (font  == null)
			return null;
		Font.Typeface typeFace = makeTypeface(fontName);
		return _buildFontObj(typeFace,_getStyle(font),font.getSize());
	}
}
