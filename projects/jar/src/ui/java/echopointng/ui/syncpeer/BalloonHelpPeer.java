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
package echopointng.ui.syncpeer;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.webrender.output.CssStyle;

import org.w3c.dom.Element;

import echopointng.PopUp;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.HtmlTable;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

/** 
 * <code>BalloonHelpPeer</code> 
 */

public class BalloonHelpPeer extends PopUpPeer {
	
	private static final String img = "/echopointng/resource/images/balloon/balloon_";
	private static final ImageReference B_BB = new ResourceImageReference(img+"bb.gif");
	private static final ImageReference B_BL = new ResourceImageReference(img+"bl.gif");
	private static final ImageReference B_BR = new ResourceImageReference(img+"br.gif");
	private static final ImageReference B_QBWD = new ResourceImageReference(img+"qbwd.gif");
	//private static final ImageReference B_QFWD = new ResourceImageReference(img+"qfwd.gif");
	private static final ImageReference B_SL = new ResourceImageReference(img+"sl.gif");
	private static final ImageReference B_SR = new ResourceImageReference(img+"sr.gif");
	private static final ImageReference B_TL = new ResourceImageReference(img+"tl.gif");
	private static final ImageReference B_TR = new ResourceImageReference(img+"tr.gif");
	private static final ImageReference B_TT = new ResourceImageReference(img+"tt.gif");

	
	/**
	 * @see echopointng.ui.syncpeer.PopUpPeer#renderBox(echopointng.ui.util.RenderingContext, nextapp.echo2.app.Component, nextapp.echo2.app.Style)
	 */
	protected Element renderBox(RenderingContext rc, Component component, Style fallbackStyle) {
		
		Element content;
		Element td = null;
		String imgUri = null;
		
		HtmlTable table = new HtmlTable(rc.getDocument(),0,0,0);
		// top row
		td = table.getTD(); 
		td.setAttribute("style","width:10px;height:7px");
		td.appendChild(createIMG(rc,B_TL,10,7));

		td = table.newTD(); 
		imgUri = ImageManager.getURI(rc,B_TT);
		td.setAttribute("style","width:20px;height:7px;background-repeat:repeat-x;background-image:url(" + imgUri + ")");
		td.appendChild(createIMG(rc,B_TT,20,7));

		td = table.newTD(); 
		imgUri = ImageManager.getURI(rc,B_TT);
		td.setAttribute("style","height:7px;background-repeat:repeat-x;background-image:url(" + imgUri + ")");
		td.appendChild(createIMG(rc,B_TT,0,7));

		td = table.newTD(); 
		imgUri = ImageManager.getURI(rc,B_TT);
		td.setAttribute("style","width:20px;height:7px;background-repeat:repeat-x;background-image:url(" + imgUri + ")");
		td.appendChild(createIMG(rc,B_TT,20,7));
		
		td = table.newTD(); 
		td.setAttribute("style","width:10px;height:7px");
		td.appendChild(createIMG(rc,B_TR,10,7));
		
		// content row
		table.newTR();
		
		td = table.getTD(); 
		imgUri = ImageManager.getURI(rc,B_SL);
		td.setAttribute("style","width:10px;background-repeat:repeat-t;background-image:url(" + imgUri + ")");
		td.appendChild(createIMG(rc,B_SL,10,0));

		td = table.newTD(); 
		td.setAttribute("colspan","3");
		td.setAttribute("nowrap","nowrap");
		td.setAttribute("style","background:rgb(255,255,231)");
		content = td;

		td = table.newTD(); 
		imgUri = ImageManager.getURI(rc,B_SR);
		td.setAttribute("style","width:10px;background-repeat:repeat-t;background-image:url(" + imgUri + ")");
		td.appendChild(createIMG(rc,B_SR,10,0));
		
		// bottom row
		table.newTR();
		
		td = table.getTD(); 
		td.setAttribute("style","width:10px;height:25px");
		td.appendChild(createIMG(rc,B_BL,10,25));

		td = table.newTD(); 
		imgUri = ImageManager.getURI(rc,B_BB);
		td.setAttribute("style","width:20px;height:25px;background-repeat:repeat-x;background-image:url(" + imgUri + ")");
		td.appendChild(createIMG(rc,B_QBWD,20,25));

		td = table.newTD(); 
		imgUri = ImageManager.getURI(rc,B_BB);
		td.setAttribute("style","height:25px;background-repeat:repeat-x;background-image:url(" + imgUri + ")");
		td.appendChild(createIMG(rc,B_BB,0,25));

		td = table.newTD(); 
		imgUri = ImageManager.getURI(rc,B_BB);
		td.setAttribute("style","width:20px;height:25px;background-repeat:repeat-x;background-image:url(" + imgUri + ")");
		td.appendChild(createIMG(rc,B_BB,20,25));
		
		td = table.newTD(); 
		td.setAttribute("style","width:10px;height:25px");
		td.appendChild(createIMG(rc,B_BR,10,25));		
		
		
		CssStyleEx style = new CssStyleEx();
		style.setAttribute("position", "absolute");
		style.setAttribute("visibility", "hidden");
		style.setAttribute("display", "none");
		Render.layoutFix(rc,style);
		Render.asColors(style, component, PopUp.PROPERTY_POPUP_BACKGROUND, null, fallbackStyle);
		Render.asBorder(style, component, PopUp.PROPERTY_POPUP_BORDER, fallbackStyle);
		Render.asInsets(style, component, PopUp.PROPERTY_POPUP_INSETS, "padding", fallbackStyle);
		Render.asInsets(style, component, PopUp.PROPERTY_POPUP_OUTSETS, "margin", fallbackStyle);

		Element divBox = rc.createE("div");
		divBox.setAttribute("id", rc.getElementId() + "|Box");
		divBox.setAttribute("style", style.renderInline());
		divBox.appendChild(table.getTABLE());
		Component popUpComponent = (Component) rc.getRP(PopUp.PROPERTY_POPUP);
		if (popUpComponent != null) {
			renderReplaceableChild(rc, rc.getServerComponentUpdate(), content, popUpComponent);
		}
		return divBox;
	}

	/**
	 */
	private Element createIMG(RenderingContext rc, ImageReference imageRef, int w, int h) {
		CssStyle style = new CssStyle();
		style.setAttribute("width",w+"px");
		style.setAttribute("height",h+"px");
		Element img  = ImageManager.createImgE(rc,style,imageRef);
		return img;
	}
	
}
