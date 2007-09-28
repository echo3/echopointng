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
package echopointng.ui.util;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.Style;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webrender.output.CssStyle;
import echopointng.util.ColorKit;

/**
 * <code>CssStyleEx</code> adds extra setter functions to the CssStyle class.
 * It supports pseudo attributes as well.
 */
public class CssStyleEx extends CssStyle {

	/**
	 * Constructs a <code>CssStyleEx</code>
	 */
	public CssStyleEx() {
	}

	/**
	 * Constructs a <code>CssStyleEx</code> and sets the "discoverable" style
	 * attributes from component by calling
	 * <code>Render.asComponent(this,component);</code>
	 * 
	 * @param component -
	 *            the component o get style attributes from
	 */
	public CssStyleEx(Component component) {
		Render.asComponent(this, component);
	}

	/**
	 * Constructs a <code>CssStyleEx</code> and sets the "discoverable" style
	 * attributes from component by calling
	 * <code>Render.asComponent(this,component);</code>
	 * 
	 * @param component -
	 *            the component o get style attributes from
	 * @param defaultStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the component.
	 */
	public CssStyleEx(Component component, Style defaultStyle) {
		Render.asComponent(this, component, defaultStyle);
	}

	/**
	 * Renders a font into the Style but sets text-decoration to none if it has
	 * no other decoration. This prevents anchors having underlines when
	 * underline is not set.
	 * 
	 * @param font -
	 *            the font to render into this <code>CssStyleEx</code>.
	 */
	private void renderFont(Font font) {
		if (font == null) {
			// stops anchors having underlines
			setAttribute("text-decoration", "none");
		} else {
			Font.Typeface typeFace = font.getTypeface();
			if (typeFace != null) {
				StringBuffer out = new StringBuffer(typeFace.getName());
				typeFace = typeFace.getAlternate();
				while (typeFace != null) {
					out.append(",");
					out.append(typeFace.getName());
					typeFace = typeFace.getAlternate();
				}
				setAttribute("font-family", out.toString());
			}
			if (font.getSize() != null) {
				setAttribute("font-size", ExtentRender.renderCssAttributeValue(font.getSize()));
			}
			boolean setTextDecoration = false;
			if (!font.isPlain()) {
				if (font.isBold()) {
					setAttribute("font-weight", "bold");
				}
				if (font.isItalic()) {
					setAttribute("font-style", "italic");
				}
				if (font.isUnderline() || font.isLineThrough() || font.isItalic()) {
					StringBuffer out = new StringBuffer();
					if (font.isUnderline()) {
						out.append("underline");
						setTextDecoration = true;
					}
					if (font.isOverline()) {
						if (out.length() > 0) {
							out.append(" ");
						}
						out.append("overline");
						setTextDecoration = true;
					}
					if (font.isLineThrough()) {
						if (out.length() > 0) {
							out.append(" ");
						}
						out.append("line-through");
						setTextDecoration = true;
					}
					if (!setTextDecoration) {
						// stops anchors having underlines
						out.append("none");
					}
					setAttribute("text-decoration", out.toString());
				}
			} else {
				// stops anchors having underlines
				setAttribute("text-decoration", "none");
			}
		}
	}

	/**
	 * Renders the background color as a CSS "background-color" value
	 * 
	 * @param foreground -
	 *            the foregound <code>Color</code>
	 */
	public void setBackground(Color background) {
		if (background != null)
			setAttribute("background-color", ColorKit.makeCSSColor(background));
	}

	/**
	 * Renders the font color as a seris of CSS values
	 * 
	 * @param font -
	 *            the <code>Font</code>
	 */
	public void setFont(Font font) {
		renderFont(font);
	}

	/**
	 * Renders the foregound color as a CSS "color" value
	 * 
	 * @param foreground -
	 *            the foregound <code>Color</code>
	 */
	public void setForeground(Color foreground) {
		if (foreground != null)
			setAttribute("color", ColorKit.makeCSSColor(foreground));
	}

	/**
	 * Sets "white-space" to "nowrap"
	 */
	public void setNoWrap() {
		setAttribute("white-space", "nowrap");
	}
	
	/**
	 * Sets the border for this style
	 * 
	 * @param border -  the <code>Border</code> in question
	 */
	public void setBorder(Border border) {
		if (border != null) {
			Render.asBorder(this,border);
		}
	}

	/**
	 * @see nextapp.echo2.webrender.output.CssStyle#toString()
	 */
	public String toString() {
		return "{" + renderInline() + "}";
	}
}
