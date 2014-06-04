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
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;

import java.util.Locale;
import java.util.Map;

/**
 * A utility to class to help with Color manipulation
 *
 * @author Brad Baker 
 */
public class ColorKit {

	/** Array for acceptable Color Constant strings*/
	public static String ColorConstantStrings[] = {
			"Color.BLACK",		"BLACK",
			"Color.BLUE",		"BLUE",
			"Color.CYAN",		"CYAN",
			"Color.DARKGRAY",	"DARKGRAY",
			"Color.GREEN",		"GREEN",
			"Color.LIGHTGRAY",	"LIGHTGRAY",
			"Color.MAGENTA",	"MAGENTA",
			"Color.ORANGE",		"ORANGE",
			"Color.PINK",		"PINK",
			"Color.RED",		"RED",
			"Color.WHITE",		"WHITE",
			"Color.YELLOW",		"YELLOW",
			};

	/** An array for Color values matching ColorConstantStrings*/
	public static Color ColorConstantValues[] =
		{
			Color.BLACK,		Color.BLACK,
			Color.BLUE,			Color.BLUE,
			Color.CYAN,			Color.CYAN,
			Color.DARKGRAY,		Color.DARKGRAY,
			Color.GREEN,		Color.GREEN,
			Color.LIGHTGRAY,	Color.LIGHTGRAY,
			Color.MAGENTA,		Color.MAGENTA,
			Color.ORANGE,		Color.ORANGE,
			Color.PINK,			Color.PINK,
			Color.RED,			Color.RED,
			Color.WHITE,		Color.WHITE,
			Color.YELLOW,		Color.YELLOW,
			};
	
	/** 
	 * The default scale factor applied to the brighter and darker functions
	 */
	public static final double COLOR_FACTOR = 0.7;

	private static Map colorMap = new ConcurrentReaderHashMap();

	/** not instantiable */
	private ColorKit() {
	}
	
	/*
	 * POarse a string in the form #99999
	 */
	private static Color _parseHashHexString(String hasHexString) {
		StringBuffer sb = new StringBuffer(hasHexString.substring(1));
		while (sb.length() < 6) {
			sb.append("0");
		}
		String hex = sb.substring(0,6);
		int rgb = Integer.parseInt(hex,16);
		return new Color(rgb);
	}
	

	/*
	 * Create a zero fronted hex string
	 */
	private static String _toHexString(int i) {
		String hex = Integer.toHexString(i);
		if (hex.length() < 2)
			hex = "0" + hex;
		return hex.toUpperCase(Locale.ENGLISH);
	}
	
	/*
	 * Returns true if the propertyValue is a valid representation of a base 10 Integer
	 * value.
	 */
	private static boolean _isInteger(String propertyValue) {
		try {
			Integer.parseInt(propertyValue.trim());
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	/*
	 * Returns true if the propertyValue is a valid representation of a base 16 Integer
	 * value.
	 */
	private static boolean _isHexInteger(String propertyValue) {
		propertyValue = propertyValue.trim().toLowerCase(Locale.ENGLISH);
		if (propertyValue.indexOf("0x") == 0)
			propertyValue = propertyValue.substring(2);

		try {
			Integer.parseInt(propertyValue, 16);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	/*
	 * Returns the index of the string in an array of strings, case insensitive
	 * or -1 if its not found
	 */
	private static int _arrayIndexOf(String s, String[] array) {
		if (array == null)
			return -1;
		for (int i = 0; i < array.length; i++) {
			if (s.equalsIgnoreCase(array[i]))
				return i;
		}
		return -1;
	}

	/**
	 * Creates a brighter version of this color.
	 * <p>
	 * This method applies a 0.7 scale factor to each of the three RGB 
	 * components of the color to create a brighter version of the same 
	 * color. Although <code>brighter</code> and <code>darker</code> are 
	 * inverse operations, the results of a series of invocations of 
	 * these two methods may be inconsistent because of rounding errors.
	 *
	 * @param  color - the color to make brighter
	 * @return     a new <code>Color</code> object, a brighter version of the color.
	 *
	 */
	public static Color brighter(Color color) {
		return brighter(color,COLOR_FACTOR);
	}
	
	/**
	 * Creates a brighter version of this color, according to the provided
	 * factor.
	 * <p>
	 * This method applies an the scale factor to each of the three RGB 
	 * components of the color to create a brighter version of the same 
	 * color. Although <code>brighter</code> and <code>darker</code> are 
	 * inverse operations, the results of a series of invocations of 
	 * these two methods may be inconsistent because of rounding errors.
	 *
	 * @param  color - the color to make brighter
	 * @param factor - the factor by which to make it brighter.
	 * 
	 * @return     a new <code>Color</code> object, a brighter version of the color.
	 *
	 */
	public static Color brighter(Color color, double factor) {

		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		/* 
		 * 1. black.brighter() should return grey
		 * 2. applying brighter to blue will always return blue, brighter
		 * 3. non pure color (non zero rgb) will eventually return white
		 */
		int i = (int) (1.0 / (1.0 - factor));
		if (r == 0 && g == 0 && b == 0) {
			return new Color(i, i, i);
		}
		if (r > 0 && r < i)
			r = i;
		if (g > 0 && g < i)
			g = i;
		if (b > 0 && b < i)
			b = i;

		return makeColor(Math.min((int) (r / factor), 255), Math.min((int) (g / factor), 255), Math.min((int) (b / factor), 255));
	}
	
	/**
	 * Creates a darker version of this color.
	 * <p>
	 * This method applies an arbitrary 0.7 scale factor to each of the three RGB 
	 * components of the color to create a darker version of the same 
	 * color. Although <code>brighter</code> and <code>darker</code> are 
	 * inverse operations, the results of a series of invocations of 
	 * these two methods may be inconsistent because of rounding errors.
	 *  
	 * @param  color - the color to make darker
	 * @return  a new <code>Color</code> object,a darker version of this color.
	 *
	 */
	public static Color darker(Color color) {
		return darker(color,COLOR_FACTOR);
	}
	
	/**
	 * Creates a darker version of this color, according to the provided
	 * factor.
	 * <p>
	 * This method applies an the scale factor to each of the three RGB 
	 * components of the color to create a darker version of the same 
	 * color. Although <code>brighter</code> and <code>darker</code> are 
	 * inverse operations, the results of a series of invocations of 
	 * these two methods may be inconsistent because of rounding errors.
	 * 
	 * @param  color - the color to make darker
	 * @param factor - the factor by whcih to make it darker.
	 * @return a new <code>Color</code> object,a darker version of the color.
	 *
	 */
	public static Color darker(Color color, double factor) {
		return makeColor(Math.max((int) (color.getRed() * factor), 0), Math.max((int) (color.getGreen() * factor), 0), Math.max((int) (color.getBlue() * factor), 0));
	}

	/**
	 * Searchs the heirarchy tree of the component and finds the first
	 * non null background Color object.  It will return 
	 * <code>Color.WHITE</code> if no ancestor components have 
	 * a background set, but in practice this is unlikely to be the case.
	 * 
	 * @param comp - the component to start searching at
	 * @return - the background color of the component or its parents
	 * 
	 */
	public static Color findBackground(Component comp) {
		while (comp != null) {
			Color clr = comp.getBackground();
			if (clr != null)
				return clr;
			comp = comp.getParent();
		}
		return Color.WHITE;
	}

	/**
	 * Searchs the heirarchy tree of the component and finds the first
	 * non null foreground Color object.  It will return 
	 * <code>Color.BLACK.</code> if no ancestor components have 
	 * a background set, but in practice this is unlikely to be the case.
	 * 
	 * @param comp - the component to start searching at
	 * @return - the foreground color of the component or its parents
	 */
	public static Color findForeground(Component comp) {
		while (comp != null) {
			Color clr = comp.getForeground();
			if (clr != null)
				return clr;
			comp = comp.getParent();
		}
		return Color.BLACK;
	}
	
	/**
	 * Returns the inversion of a color.
	 * 
	 * @param color - the color to invert
	 * @return the inverted color 
	 */
	public static Color invertColor(Color color) {
		int r = Math.abs(255 - color.getRed());
		int g = Math.abs(255 - color.getGreen());
		int b = Math.abs(255 - color.getBlue());

		return makeColor(r, g, b);
	}
 
	/**
	 * Creates an java.awt.Color object from a nextapp.echo.Color object, or 
	 * uses the default AWT if the <code>echoColor</code> object is null
	 * 
	 * @param echoColor - the nextapp.echo2.app.Color object to convert
	 * @param defaultAwtColor - the AWT color to use if the echoColor is null
	 * @return a new AWT color object
	 * 
	 */
	public static java.awt.Color makeAwtColor(Color echoColor, java.awt.Color defaultAwtColor) {
		if (echoColor == null)
			return defaultAwtColor;
		else
			return new java.awt.Color(echoColor.getRgb(), false);
	}

	/**
	 * Returns the Hex W3C CSS color string for a given color 
	 * ie #rrggbb
	 * 
	 * @param color - the color to convert to a W3C hex CSS string value
	 * @return the W3C hex CSS string value
	 */
	public static String makeCSSColor(Color color) {
		StringBuffer b = new StringBuffer();

		b.append("#");
		b.append(makeHexColor(color));
		return b.toString().toUpperCase(Locale.ENGLISH);
	}

	/**
	 * Returns the Color in the form : color(r,g,b); 
	 * 
	 * @param color - the color to convert to a color string representation
	 * @return the color string representation
	 */
	public static String makeColorString(Color color) {
		StringBuffer b = new StringBuffer();

		b.append("color(");
		b.append(color.getRed());
		b.append(",");
		b.append(color.getGreen());
		b.append(",");
		b.append(color.getBlue());
		b.append(")");
		return b.toString();
	}

	/**
	 * Returns the Hex string for a given color for example 'rrggbb'
	 * 
	 * Note it does NOT have the # character at the front
	 * 
	 * @param color the color in question
	 * @return the string in the form rrggbb
	 */
	public static String makeHexColor(Color color) {
		StringBuffer b = new StringBuffer();

		b.append(_toHexString(color.getRed()));
		b.append(_toHexString(color.getGreen()));
		b.append(_toHexString(color.getBlue()));

		return b.toString().toLowerCase(Locale.ENGLISH);
	}
	
	/**
	 * Returns a Color object from the string representation
	 * <p>
	 * The color string must be in the format :<br>
	 * 	 - #rrggbb			where rr, gg, bb are hexidecimal integer values<br>
	 * 	 - rgb( r, g, b)	where r,g,b are integer values<br>
	 * 	 - color( r, g, b) 	where r,g,b are integer values<br>
	 *   - null				will return a null color
	 * <p>
	 * otherwise an IllegalArgumentException is thrown. 
	 * <p>
	 * The results of these operations are cached in a static
	 * cache, so that Color objects can be re-used.  This is okay 
	 * since Color objects are immutable. 
	 * 
	 * @param colorString - the color string representation
	 * @return a new Color object
	 * 
	 * @throws IllegalArgumentException - if the string cannot be converted
	 * to a color
	 */
	public static Color makeColor(String colorString) {
		if (colorString == null)
			throw new IllegalArgumentException("Illegal null color string");
		
		colorString = colorString.trim().toLowerCase(Locale.ENGLISH);
		Color color = (Color) colorMap.get(colorString);
		if (color != null)
			return color;
		
		if (! isColor(colorString))
			throw new IllegalArgumentException("Illegal color string" + colorString);

		String tokens[] = TokenizerKit.tokenize(colorString, "(,)");
		if (tokens.length == 1) {
			int index = _arrayIndexOf(colorString, ColorConstantStrings);
			if (index != -1)
				color = ColorConstantValues[index];
		}
		if (color == null) {
			if (colorString.indexOf('#') == 0) {
				color  = _parseHashHexString(colorString);
			} else {
				int r = Integer.parseInt(tokens[1].trim());
				int g = Integer.parseInt(tokens[2].trim());
				int b = Integer.parseInt(tokens[3].trim());

				color = new Color(r,g,b);
			}
		}
		colorMap.put(colorString, color);
		return color;
	}
	
	/**
	 * Returns true if the propertyValue is a valid representation of a Color
	 * value.
	 * <p>
	 * The allowable forms are : <p>
	 *   - colorconstant	where colorconstant in (red,blue,green...)
	 * 	 - #rrggbb			where rr, gg, bb are hexidecimal integer values<br>
	 * 	 - rgb( r, g, b)	where r,g,b are integer values<br>
	 * 	 - color( r, g, b) 	where r,g,b are integer values<br>
	 */
	public static boolean isColor(String colorString) {
		if (colorString == null)
			return false;
			
		colorString = colorString.trim();
		String tokens[] = TokenizerKit.tokenize(colorString, "(,)");

		if (tokens.length == 1) {
			if (_arrayIndexOf(colorString, ColorConstantStrings) != -1)
				return true;
		}

		if (colorString.indexOf('#') == 0) {
			return _isHexInteger(colorString.substring(1).trim());
		} else if (colorString.indexOf("rgb(") == 0 || colorString.indexOf("color(") == 0) {
			if (tokens.length != 4)
				return false;
			String r = tokens[1].trim();
			String g = tokens[2].trim();
			String b = tokens[3].trim();

			return _isInteger(r) && _isInteger(g) && _isInteger(b);
		}
		return false;
	}

	/**
	 * Shortcut synonym for makeColor(colorString);
	 * <p>
	 * @see ColorKit#makeColor(String)
	 */
	public static Color clr(String colorString) {
		return makeColor(colorString);
	}

	/**
	 * Shortcut synonym for makeColor(r,g,b);
	 * <p>
	 * @see ColorKit#makeColor(int, int, int)
	 */
	public static Color clr(int r, int g, int b) {
		return makeColor(r,g,b);
	}

	/**
	 * Shortcut synonym for makeColor(rgb);
	 * <p>
	 * @see ColorKit#makeColor(int)
	 */
	public static Color clr(int rgb) {
		return makeColor(rgb);
	}
	
	/**
	 * Returns a Color object from the red, green and blue integer values.
	 * <p>
	 * The results of these operations are cached in a static
	 * cache, so that Color objects can be re-used.  This is okay 
	 * since Color objects are immutable. 
	 * 
	 * @param r - the red color component
	 * @param g - the green color component
	 * @param b - the blue color component
	 * @return a new Color object
	 */
	public static Color makeColor(int r, int g, int b) {
		StringBuffer buf = new StringBuffer();
		buf.append('#');
		buf.append(_toHexString(r));
		buf.append(_toHexString(g));
		buf.append(_toHexString(b));
		//
		// since we know we have produced a correct
		// color string we can go direct to the cache!
		//
		String colorString = buf.toString().toLowerCase(Locale.ENGLISH);
		Color color = (Color) colorMap.get(colorString);
		if (color == null) {
			color = new Color(r,g,b);
			colorMap.put(colorString,color);		
		}
		return color;
	}	

	/**
	 * Returns a Color object from the red, green and blue integer values.
	 * <p>
	 * The results of these operations are cached in a static
	 * cache, so that Color objects can be re-used.  This is okay 
	 * since Color objects are immutable. 
	 * 
	 * @param rgb - the red/blue/green integer color value
	 * @return a new Color object
	 */
	public static Color makeColor(int rgb) {
		StringBuffer buf = new StringBuffer();
		buf.append('#');
		buf.append(_toHexString(rgb));
		//
		// since we know we have produced a correct
		// color string we can go direct to the cache!
		//
		String colorString = buf.toString().toLowerCase(Locale.ENGLISH);
		Color color = (Color) colorMap.get(colorString);
		if (color == null) {
			color = new Color(rgb);
			colorMap.put(colorString,color);		
		}
		return color;
	}	
	
	/**
	 * Tints a given color by a factor given in red, blue and green.
	 * <p>
	 * The red, green and blue arguments should be around 1.0 
	 */
	public static Color tint(Color clr, double red, double green, double blue)
	{
		double newRed = clr.getRed() * red;
		double newGreen = clr.getGreen() * green;
		double newBlue = clr.getBlue() * blue;
		
		double normFactor = Math.max(newRed, Math.max(newGreen, newBlue)) / 255;
		
		newRed /= normFactor;
		newGreen /= normFactor;
		newBlue /= normFactor;
		
		return makeColor((int)newRed, (int)newGreen, (int)newBlue);
	}

	/** swapRGB Operation - R = B, G = G, B = R */
	public static final int SWAP_OP_BGR = 0;
	/** swapRGB Operation - R = B, G = R, B = G */
	public static final int SWAP_OP_BRG = 1;
	/** swapRGB Operation - R = G, G = B, B = R */
	public static final int SWAP_OP_GBR = 2;
	/** swapRGB Operation - R = G, G = R, B = B */
	public static final int SWAP_OP_GRB = 3;
	/** swapRGB Operation - R = R, G = B, B = G */
	public static final int SWAP_OP_RBG = 4;

	/**
	 * This will swap the red/green/blue elements of a color
	 * according to the swapOperation, which may be one of :
	 * <ul>
	 * <li>SWAP_OP_RBG</li> 
	 * <li>SWAP_OP_BGR</li> 
	 * <li>SWAP_OP_BRG</li> 
	 * <li>SWAP_OP_GRB</li> 
	 * <li>SWAP_OP_GBR</li> 
	 * <ul>
	 *  
	 * @param swapColor - the color to swap the RGB elements 
	 * @param swapOperation - the operation to perform
	 * @return - a new swapped Color or null if the swapColor was null.
	 */
	public static Color swapRGB(Color swapColor, int swapOperation) {
		if (swapColor == null)	
			return swapColor;
			
		int r = swapColor.getRed();
		int g = swapColor.getGreen();
		int b = swapColor.getBlue();
		switch (swapOperation) {
			case SWAP_OP_RBG:
				return makeColor(r,b,g);
			case SWAP_OP_BGR:
				return makeColor(b,g,r);
			case SWAP_OP_BRG:
				return makeColor(b,r,g);
			case SWAP_OP_GBR:
				return makeColor(g,b,r);
			case SWAP_OP_GRB:
				return makeColor(g,r,b);
			default :
				return swapColor;
		}
	}
}
