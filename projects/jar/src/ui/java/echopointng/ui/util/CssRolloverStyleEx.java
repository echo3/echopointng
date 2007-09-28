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
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.Style;
import nextapp.echo2.webcontainer.RenderContext;
import echopointng.util.ColorKit;

/**
 * <code>CssRolloverStyle</code> helps with creating componentsthat have rollover
 * support.  
 */
public class CssRolloverStyleEx extends CssStyleEx {

	Color rolloverBackground;
	Border rolloverBorder;
	Font rolloverFont;
	Color rolloverForeground;
	FillImage rolloverBackgroundImage;
	
	public CssRolloverStyleEx() {
		super();
	}

	/**
	 * @see CssStyleEx#CssStyleEx(Component)
	 */
	public CssRolloverStyleEx(Component component) {
		super(component);
	}

	/**
	 * @see CssStyleEx#CssStyleEx(Component, Style)
	 */
	public CssRolloverStyleEx(Component component, Style defaultStyle) {
		super(component,defaultStyle);
	}

	/**
	 * @return a style string for ready for rollover.
	 */
	public String renderRolloverSupportInline() {
		return renderRolloverSupportInline(null);
	}
	
	/**
	 * @return a style string for ready for rollover using a RenderContext
	 * for when a FillImage is to be used
	 */
	public String renderRolloverSupportInline(RenderContext rc) {
		StringBuffer buf = new StringBuffer();
		if (rolloverForeground != null) {
			buf.append("color:");
			buf.append(ColorKit.makeCSSColor(rolloverForeground));
			buf.append(';');
		}
		if (rolloverBackground != null) {
			buf.append("background-color:");
			buf.append(ColorKit.makeCSSColor(rolloverBackground));
			buf.append(';');
		}
		CssStyleEx tempStyle = new CssStyleEx();
		if (rolloverFont != null) {
			tempStyle.setFont(rolloverFont);
		}
		if (rolloverBorder != null) {
			Render.asBorder(tempStyle, rolloverBorder);
		}
		if (rolloverBackgroundImage != null && rc != null) {
			Render.asBackgroundImage(rc, tempStyle, this.rolloverBackgroundImage);
		}
		if (tempStyle.hasAttributes()) {
			buf.append(tempStyle.renderInline());
		}
		return buf.toString();
	}

	public void setRolloverBackground(Color rolloverBackground) {
		this.rolloverBackground = rolloverBackground;
	}

	public void setRolloverBorder(Border rolloverBorder) {
		this.rolloverBorder = rolloverBorder;
	}

	public void setRolloverFont(Font rolloverFont) {
		this.rolloverFont = rolloverFont;
	}

	public void setRolloverForeground(Color rolloverForeground) {
		this.rolloverForeground = rolloverForeground;
	}

	public void setRolloverBackgroundImage(FillImage backgroundImage) {
		this.rolloverBackgroundImage = backgroundImage;
	}
	
	/**
	 * @see nextapp.echo2.webrender.output.CssStyle#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append("rollover {");
		buf.append(renderRolloverSupportInline());
		buf.append("}");
		return buf.toString();
	}

}