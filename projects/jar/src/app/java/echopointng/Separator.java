package echopointng;
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
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Style;
import echopointng.able.Insetable;


/**
 *
 * The <code>Separator</code> class is a <code>Component</code>
 * that provides a simple separator within menus or between
 * other <code>Components</code>
 * <p>
 * It consists of a top line and bottom line, that can have width
 * and color values.  It also has an Inset value around it.
 * 
 * @author Brad Baker 
 */

public class Separator extends ComponentEx implements Insetable {
	
	public static final String PROPERTY_BOTTOM_SIZE = "bottomSize";
	public static final String PROPERTY_BOTTOM_COLOR = "bottomColor";
	public static final String PROPERTY_TOP_SIZE = "topSize";
	public static final String PROPERTY_TOP_COLOR = "topColor";
	
	/** the default bottom color */
	public static final Color DEFAULT_BOTTOM_COLOR 	= new Color(0xF0,0xF0,0xF0);
	/** the default insets are {4,2} */
	public static final Insets DEFAULT_INSETS 		= new Insets(4,2);
	/** the default top color */
	public static final Color DEFAULT_TOP_COLOR 	= new Color(0x90,0x90,0x90);
	/** the default top size is 1 */
	public static final Extent DEFAULT_TOP_SIZE		= new Extent(1);
	/** the default bottom size is 1 */
	public static final Extent DEFAULT_BOTTOM_SIZE	= new Extent(1);
	
	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx();
		style.setProperty(PROPERTY_BOTTOM_COLOR,DEFAULT_BOTTOM_COLOR);
		style.setProperty(PROPERTY_BOTTOM_SIZE,DEFAULT_BOTTOM_SIZE);
		style.setProperty(PROPERTY_TOP_COLOR,DEFAULT_TOP_COLOR);
		style.setProperty(PROPERTY_TOP_SIZE,DEFAULT_TOP_SIZE);
		style.setProperty(PROPERTY_INSETS,DEFAULT_INSETS);
		style.setProperty(PROPERTY_OUTSETS,DEFAULT_OUTSETS);
		DEFAULT_STYLE = style;
	}
	
	/**
	 * Constructs a <code>Separator</code>.
	 *
	 */
	public Separator() {
		super();
	}
	/** 
	 * The color of the bottom separator line
	 * @return The color of the bottom separator line
	 */
	public Color getBottomColor() {
		return (Color) getProperty(PROPERTY_BOTTOM_COLOR);
	}

	/** 
	 * The size of the bottom separator line
	 * @return The size of the bottom separator line
	 */
	public Extent getBottomSize() {
		return (Extent) getProperty(PROPERTY_BOTTOM_SIZE);
	}

	/** 
	 * The color of the top separator line
	 * @return The color of the top separator line
	 */
	public Color getTopColor() {
		return (Color) getProperty(PROPERTY_TOP_COLOR);
	}

	/** 
	 * The size of the top separator line
	 * @return The color of the top separator line
	 */
	public Extent getTopSize() {
		return (Extent) getProperty(PROPERTY_TOP_SIZE);
	}

	public void setBottomColor(Color color) {
		setProperty(PROPERTY_BOTTOM_COLOR,color);	
	}

	public void setBottomSize(Extent newValue) {
		setProperty(PROPERTY_BOTTOM_SIZE,newValue);	
	}

	public void setTopColor(Color newValue) {
		setProperty(PROPERTY_TOP_COLOR,newValue);	
	}

	public void setTopSize(Extent newValue) {
		setProperty(PROPERTY_TOP_SIZE,newValue);	
	}
	/**
	 * @see echopointng.able.Insetable#getInsets()
	 */
	public Insets getInsets() {
		return (Insets) getProperty(PROPERTY_INSETS);
	}
	/**
	 * @see echopointng.able.Insetable#getOutsets()
	 */
	public Insets getOutsets() {
		return (Insets) getProperty(PROPERTY_OUTSETS);
	}
	/**
	 * @see echopointng.able.Insetable#setInsets(nextapp.echo2.app.Insets)
	 */
	public void setInsets(Insets newValue) {
		setProperty(PROPERTY_INSETS,newValue);
	}
	/**
	 *  @see echopointng.able.Insetable#setOutsets(nextapp.echo2.app.Insets)
	 */
	public void setOutsets(Insets newValue) {
		setProperty(PROPERTY_OUTSETS,newValue);
	}
}
