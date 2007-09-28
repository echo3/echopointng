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
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.list.ListCellRenderer;
import nextapp.echo2.app.list.ListModel;
import nextapp.echo2.app.list.StyledListCell;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.propertyrender.ColorRender;
import nextapp.echo2.webcontainer.propertyrender.FontRender;
import nextapp.echo2.webcontainer.propertyrender.InsetsRender;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Element;

import echopointng.ComboBox;
import echopointng.able.Widthable;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;
import echopointng.util.QuoterKit;

/**
 * <code>ComboBoxPeer</code> is the peer for the <code>ComboBox</code>
 */
public class ComboBoxPeer extends PopUpPeer {

	protected static final Insets DEFAULT_ITEM_INSETS = new Insets(new Extent(2), new Extent(1));
	
	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service COMBOBOX_SERVICE = JavaScriptService.forResource("EPNG.ComboBox", "/echopointng/ui/resource/js/combobox.js");

	static {
		WebRenderServlet.getServiceRegistry().add(COMBOBOX_SERVICE);
	}
    
	/**
	 * This is called to render the popup floating box.  Overrides the
	 * base implementation.
	 * 
	 * @return a new top level Element for the floating box
	 */
	protected Element renderBox(RenderingContext rc, Component component, Style fallbackStyle) {

		ComboBox combo = (ComboBox) component;
		rc.addLibrary(COMBOBOX_SERVICE);
		
		createInitDirective(rc, combo);
		
		CssStyle cssStyle = createListBoxStyle(rc, combo, fallbackStyle);

		Element divBox = rc.createE("div");
		divBox.setAttribute("id", rc.getElementId() + "|Box");
		divBox.setAttribute("style", cssStyle.renderInline());
		
		return divBox;
	}
	
	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * initialize the state of a ComboBox, performing tasks such as registering
	 * event listeners on the client and creating the EPComboBox JS object.
	 * <p>
	 * Not that this does not overrride the base createInitDirective() but rather
	 * augments it by assuming it has run first.
	 */
	protected void createInitDirective(RenderingContext rc, ComboBox combo) {
		ServerMessage serverMessage = rc.getServerMessage();
		String elementId = rc.getElementId();

		// Create the element containing initialisation parameters for the ComboBox
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPComboBox.MessageProcessor",
				"init", new String[0], new String[0]);
		
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", elementId);
		itemElement.setAttribute("defaultItemStyle", createDefaultListItemStyle().renderInline());
		itemElement.setAttribute("defaultHoverStyle", createDefaultListItemHoverStyle().renderInline());
		itemElement.setAttribute("textMatching", String.valueOf(combo.isTextMatchingPerformed()));
		itemElement.setAttribute("visibleRows", Integer.toString(combo.getListRowCount()));
		itemElement.setAttribute("actionOnSelection", String.valueOf(combo.isActionOnSelection() && combo.hasActionListeners()));
		itemElement.setAttribute("caseSensitive", String.valueOf(combo.isCaseSensitive()));
		
		itemizedUpdateElement.appendChild(itemElement);
		
		// Create an element for each option
		Element optionsElement = serverMessage.getDocument().createElement("options");
		ListModel model = combo.getListModel();
		ListCellRenderer renderer = combo.getListCellRenderer();
		
        for (int i = 0; i < model.size(); i++) {
			Object value = combo.getListModel().get(i);
			Object renderedValue = renderer.getListCellRendererComponent(combo, value, i);
			String itemValue = QuoterKit.quoteJ(renderedValue.toString(), '\'');
			CssStyle itemStyle = null;
        	
			if (renderedValue instanceof StyledListCell) {
	        	StyledListCell styledListCell = (StyledListCell) renderedValue;
	        	itemStyle = createDefaultListItemStyle();
	        	ColorRender.renderToStyle(itemStyle, styledListCell.getForeground(), styledListCell.getBackground());
                FontRender.renderToStyle(itemStyle, styledListCell.getFont());
			}
			
        	Element optionElement = serverMessage.getDocument().createElement("option");
        	optionElement.setAttribute("value", itemValue);
        	optionElement.setAttribute("style", itemStyle == null ? "" : itemStyle.renderInline());
        	optionsElement.appendChild(optionElement);
        }
	
        if (optionsElement.hasChildNodes()) itemElement.appendChild(optionsElement);
	}
	
    /**
     * Creates the default style for each item of the drop-down list.
     * 
     * @return the style
     */
    private static CssStyle createDefaultListItemStyle() {
        CssStyle style = new CssStyle();
        InsetsRender.renderToStyle(style, "padding", DEFAULT_ITEM_INSETS);
        style.setAttribute("white-space", "nowrap");
        return style;
    }
    
    /**
     * Creates the default hover style for each item of the drop-down list.
     * 
     * @return the style
     */
    private static CssStyle createDefaultListItemHoverStyle() {
        CssStyle style = createDefaultListItemStyle();
		style.setAttribute("background-color", "Highlight");
		style.setAttribute("color", "HighlightText");
        return style;
    }

	/**
	 * Creates the style for the outer div of the drop-down list.
	 * 
	 * @param rc the relevant <code>RenderContext</code>
	 * @param component the <code>ComboBox</code> instance
	 * 
	 * @return the style
	 */
	private CssStyle createListBoxStyle(RenderContext rc, Component component, Style fallbackStyle) {
		
		CssStyle style = new CssStyle();
		
		Render.asBorder(style, component, ComboBox.PROPERTY_POPUP_BORDER, fallbackStyle);
		Render.asInsets(style, component, ComboBox.PROPERTY_POPUP_INSETS, "padding", fallbackStyle);
		Render.asInsets(style, component, ComboBox.PROPERTY_POPUP_OUTSETS, "margin", fallbackStyle);
	    Render.asColors(style, component, ComboBox.PROPERTY_POPUP_BACKGROUND, null, fallbackStyle);
		Render.asFont(style, component, fallbackStyle);
    	Render.asWidthable(style, (Widthable) component);
	    
		style.setAttribute("position", "absolute");
		style.setAttribute("visibility", "hidden");
		style.setAttribute("left", "0px");
		style.setAttribute("top", "0px");
		style.setAttribute("overflow", "auto");
	    style.setAttribute("cursor", "default");
	    Render.layoutFix(rc,style);
	
	    return style;
	}
    
}
