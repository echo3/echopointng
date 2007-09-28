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

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.Calculator;
import echopointng.EPNG;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.HtmlTable;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

/**
 * <code>CalculatorPeer</code>
 */

public class CalculatorPeer extends AbstractEchoPointPeer implements PropertyUpdateProcessor, ActionProcessor {
	
	private static final String nbsp = "\u00a0";
	

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service CALC_SERVICE = JavaScriptService.forResource("EPNG.Calculator", "/echopointng/ui/resource/js/calculator.js");
	static {
		WebRenderServlet.getServiceRegistry().add(CALC_SERVICE);
	}
	
	/**
	 * 
	 */
	public CalculatorPeer() {
		partialUpdateManager.add(Calculator.TEXT_CHANGED_PROPERTY, new PartialUpdateParticipant() {
			/**
			 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#canRenderProperty(nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate)
			 */
			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}
			
			/**
			 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#renderProperty(nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate)
			 */
			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				Calculator calc = (Calculator) update.getParent();
				String text = calc.getText();

				// an XML message directive please to tell the popup to expand!
				Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
						"EPCalculator.MessageProcessor", "text", new String[0], new String[0]);
				Element itemElement = rc.getServerMessage().getDocument().createElement("item");
				itemElement.setAttribute("eid", ContainerInstance.getElementId(calc));
				itemElement.setAttribute("text", text);
				itemizedUpdateElement.appendChild(itemElement);
			}
		});
		
	}

	/**
	 * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(nextapp.echo2.webcontainer.ContainerInstance, nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
        String propertyName = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
        String propertyValue = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE);
        if (Calculator.TEXT_CHANGED_PROPERTY.equals(propertyName)) {
            if (propertyValue != null && propertyValue.indexOf('.') == propertyValue.length()-1) {
            	propertyValue = propertyValue.substring(0,propertyValue.length()-1);
            }
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
            		Calculator.TEXT_CHANGED_PROPERTY, propertyValue);
        }
        if (Calculator.PROPERTY_MEMORY.equals(propertyName)) {
            if (propertyValue != null && propertyValue.indexOf('.') == propertyValue.length()-1) {
            	propertyValue = propertyValue.substring(0,propertyValue.length()-1);
            }
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
            		Calculator.PROPERTY_MEMORY, propertyValue);
        }
	}
	
	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance, nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
        ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, Calculator.INPUT_TRANSFER, null);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      org.w3c.dom.Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parentNode, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		Calculator calc = (Calculator) component;
		
		createInitDirective(rc, calc, fallbackStyle);

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(CALC_SERVICE);

		String elementId = rc.getElementId();
		CssStyleEx style = new CssStyleEx(component, fallbackStyle);
		style.setAttribute("white-space", "nowrap");

		HtmlTable table = new HtmlTable(rc.getDocument(), 0, 0, 0);
		parentNode.appendChild(table.getTABLE());		

		table.getTABLE().setAttribute("id", elementId);
		table.getTABLE().setAttribute("style", style.renderInline());
		rc.addStandardWebSupport(table.getTABLE());
		
		//
		// render the input=text bit
		CssStyleEx cssInputStyleEx = new CssStyleEx();
		cssInputStyleEx.setBackground((Color) rc.getRP(Calculator.PROPERTY_DISPLAY_BACKGROUND,fallbackStyle));
		cssInputStyleEx.setForeground((Color) rc.getRP(Calculator.PROPERTY_DISPLAY_FOREGROUND,fallbackStyle));
		cssInputStyleEx.setFont((Font) rc.getRP(Calculator.PROPERTY_DISPLAY_FONT,fallbackStyle));
		cssInputStyleEx.setAttribute("width","95%");
        cssInputStyleEx.setAttribute("text-align","right");
		cssInputStyleEx.setAttribute("border","none");
		cssInputStyleEx.setAttribute("padding-right","2px");
		
		Element inputE = rc.createE("input");
		inputE.setAttribute("id",elementId + "Display");
		inputE.setAttribute("type","text");
		inputE.setAttribute("value",calc.getText());
		inputE.setAttribute("style",cssInputStyleEx.renderInline());
        inputE.setAttribute("maxlength", "25");
		
		Element td;
		
		// add the text field part
		td = table.getTD();
		td.setAttribute("colspan","6");
		td.setAttribute("align","center");
		td.setAttribute("style","padding:2px;");
		if (! rc.getComponent().isRenderEnabled()) {
			inputE.setAttribute("disabled","disabled");
		}
		td.appendChild(inputE);
		table.newTR();
		
		
		CssStyleEx cssOperatorStyleEx = new CssStyleEx();
		Render.asColors(cssOperatorStyleEx,component,Calculator.PROPERTY_BUTTON_BACKGROUND,Calculator.PROPERTY_BUTTON_OPERATOR_FOREGROUND,fallbackStyle);
		Render.asFont(cssOperatorStyleEx,component,Calculator.PROPERTY_FONT,fallbackStyle);
		ExtentRender.renderToStyle(cssOperatorStyleEx,"width",(Extent) rc.getRP(Calculator.PROPERTY_BUTTON_WIDTH,fallbackStyle));
		ExtentRender.renderToStyle(cssOperatorStyleEx,"height",(Extent) rc.getRP(Calculator.PROPERTY_BUTTON_HEIGHT,fallbackStyle));
		cssOperatorStyleEx.setAttribute("white-space", "nowrap");

		CssStyleEx cssNumberStyleEx = new CssStyleEx();
		Render.asColors(cssNumberStyleEx,component,Calculator.PROPERTY_BUTTON_BACKGROUND,Calculator.PROPERTY_BUTTON_NUMBER_FOREGROUND,fallbackStyle);
		Render.asFont(cssNumberStyleEx,component,Calculator.PROPERTY_FONT,fallbackStyle);
		ExtentRender.renderToStyle(cssNumberStyleEx,"width",(Extent) rc.getRP(Calculator.PROPERTY_BUTTON_WIDTH,fallbackStyle));
		ExtentRender.renderToStyle(cssNumberStyleEx,"height",(Extent) rc.getRP(Calculator.PROPERTY_BUTTON_HEIGHT,fallbackStyle));
		cssNumberStyleEx.setAttribute("white-space", "nowrap");
		
		
		// MC 7 8 9 / sqrt
		td = table.getTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"mc","MC",false));
		td.appendChild(rc.createText(nbsp));
		td = table.newTD();
		td.appendChild(createButton(rc,cssNumberStyleEx,"7","7",true));
		td = table.newTD();
		td.appendChild(createButton(rc,cssNumberStyleEx,"8","8",true));
		td = table.newTD();
		td.appendChild(createButton(rc,cssNumberStyleEx,"9","9",true));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"/","/",false));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"sqrt","sqrt",false));
		table.newTR();
		
		// MR 4 5 6 * %
		td = table.getTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"mr","MR",false));
		//td.appendChild(rc.createText(nbsp));
		td = table.newTD();
		td.appendChild(createButton(rc,cssNumberStyleEx,"4","4",true));
		td = table.newTD();
		td.appendChild(createButton(rc,cssNumberStyleEx,"5","5",true));
		td = table.newTD();
		td.appendChild(createButton(rc,cssNumberStyleEx,"6","6",true));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"*","*",false));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"%","%",false));
		table.newTR();
		
		// MS 1 2 3 - 1/x
		td = table.getTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"ms","MS",false));
		//td.appendChild(rc.createText(nbsp));
		td = table.newTD();
		td.appendChild(createButton(rc,cssNumberStyleEx,"1","1",true));
		td = table.newTD();
		td.appendChild(createButton(rc,cssNumberStyleEx,"2","2",true));
		td = table.newTD();
		td.appendChild(createButton(rc,cssNumberStyleEx,"3","3",true));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"-","-",false));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"1/x","1/x",false));
		table.newTR();
		
		// M+ 0 +/- . + =
		td = table.getTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"m+","M+",false));
		//td.appendChild(rc.createText(nbsp));
		td = table.newTD();
		td.appendChild(createButton(rc,cssNumberStyleEx,"0","0",true));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"+/-","+/-",false));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,".",".",false));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"+","+",false));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"=","=",true));
		table.newTR();
		
		// C CE BKSP op mem
		td = table.getTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"c","C",false));
		//td.appendChild(rc.createText(nbsp));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"ce","CE",false));
		td = table.newTD();
		td.appendChild(createButton(rc,cssOperatorStyleEx,"bksp","BKSP",false));
		
		td = table.newTD();
		td.setAttribute("align","center");
		td.setAttribute("style","padding:5");
		td.appendChild(createPre(rc,elementId+"Memory",null));
		
		td = table.newTD();
		td.setAttribute("align","center");
		td.setAttribute("style","padding:5");
		td.appendChild(createPre(rc,elementId+"Operation",null));
		
		td = table.newTD();
		if (rc.getRP(Calculator.PROPERTY_TRANSFER) != null) {
			td.appendChild(createButton(rc,cssOperatorStyleEx,"xfer",String.valueOf(rc.getRP(Calculator.PROPERTY_TRANSFER)),true));
		} else {
			td.appendChild(rc.createText(nbsp));
		}
			
		
	}

	private Element createPre(RenderingContext rc, String id, String value) {
		Element pre = rc.createE("pre");
		pre.setAttribute("id",id);
		pre.setAttribute("style","border:1px #f0f0f0 inset;margin-left:2px;margin-right:2px");
		Node textNode = rc.createText((value != null && value.length() > 0) ?  value : nbsp);
		pre.appendChild(textNode);
		return pre;
		
	}

	private Element createButton(RenderingContext rc, CssStyleEx styleEx, String cmd, String text, boolean makeBold) {
		Element button = rc.createE("button");
		if (! rc.getComponent().isRenderEnabled()) {
			button.setAttribute("disabled","disabled");
		}
		button.setAttribute("id",rc.getElementId()+"|"+cmd);
		button.setAttribute("style",styleEx.renderInline());
		
		Node textNode = rc.createText(text);
		if (makeBold) {
			Element boldE = rc.createE("b");
			boldE.appendChild(textNode);
			textNode = boldE;
		}
		button.appendChild(textNode);
		return button;
	}	
	/**
	 * 
	 */
	protected void createInitDirective(RenderingContext rc, Calculator calc, Style fallbackStyle) {
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
				"EPCalculator.MessageProcessor", "init", new String[0], new String[0]);

		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemElement.setAttribute("eid", rc.getElementId());
		itemElement.setAttribute("enabled", String.valueOf(calc.isRenderEnabled()));
		itemElement.setAttribute("serverNotify", String.valueOf(calc.hasActionListeners()));
		itemElement.setAttribute("text", calc.getText());
		itemElement.setAttribute("memory", calc.getMemory());

		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc, update, component);
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(CALC_SERVICE.getId());
		
		createDisposeDirective(rc.getServerMessage(),component);
	}
	
	/**
	 *  
	 */
	protected void createDisposeDirective(ServerMessage serverMessage, Component component) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPCalculator.MessageProcessor",
				"dispose", new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}

}
