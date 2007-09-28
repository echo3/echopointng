/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2005 NextApp, Inc.
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

package echopointng.ui.syncpeer.command;

import nextapp.echo2.app.Command;
import nextapp.echo2.webcontainer.CommandSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Element;

import echopointng.command.CssStyleAdd;
import echopointng.command.CssStyleAddValues;
import echopointng.command.CssStyleApplyTo;
import echopointng.command.CssStyleRemove;
import echopointng.command.CssStyleRemoveFrom;
import echopointng.command.CssStyleSheetAdd;
import echopointng.ui.resource.Resources;

/**
 * A <code>CommandSynchronizePeer</code> implementation for CSS Style
 * commands.
 */
public class CssStyleCommandPeer implements CommandSynchronizePeer {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service CSS_COMMAND_SERVICE = JavaScriptService.forResource("EPNG.CssCommand", "/echopointng/ui/resource/js/csscommand.js");
	static {
		WebRenderServlet.getServiceRegistry().add(CSS_COMMAND_SERVICE);
	}

	/**
	 * @see nextapp.echo2.webcontainer.CommandSynchronizePeer#render(
	 *      nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.Command)
	 */
	public void render(RenderContext rc, Command command) {
		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		serverMessage.addLibrary(CSS_COMMAND_SERVICE.getId());
		if (command instanceof CssStyleAdd) {
			renderCssStyleAdd(rc, (CssStyleAdd) command);
		} else if (command instanceof CssStyleAddValues) {
			renderCssStyleAddValues(rc, (CssStyleAddValues) command);
		} else if (command instanceof CssStyleSheetAdd) {
			renderCssStyleSheetAdd(rc, (CssStyleSheetAdd) command);
		} else if (command instanceof CssStyleRemove) {
			renderCssStyleRemove(rc, (CssStyleRemove) command);
		} else if (command instanceof CssStyleApplyTo) {
			renderCssStyleApplyTo(rc, (CssStyleApplyTo) command);
		} else if (command instanceof CssStyleRemoveFrom) {
			renderCssStyleRemoveFrom(rc, (CssStyleRemoveFrom) command);
		} else {
			throw new IllegalArgumentException("Unhandled Command type : " + String.valueOf(command));
		}
	}

	private void renderCssStyleAdd(RenderContext rc, CssStyleAdd command) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemE = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPCssCommand.MessageProcessor", "addStyle");
		itemE.setAttribute("eid", command.getRenderId());

		Element styleE = serverMessage.getDocument().createElement("style");
		styleE.setAttribute("type", "text/css");
		styleE.setAttribute("media", command.getMedia());
		styleE.appendChild(serverMessage.getDocument().createTextNode(command.getStyleText()));
		itemE.appendChild(styleE);
	}

	private void renderCssStyleAddValues(RenderContext rc, CssStyleAddValues command) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemE = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPCssCommand.MessageProcessor", "addStyleValue");
		itemE.setAttribute("eid", ContainerInstance.getElementId(command.getTargetComponent()));
		itemE.setAttribute("styleText", command.getStyleText());
	}

	private void renderCssStyleSheetAdd(RenderContext rc, CssStyleSheetAdd command) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemE = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPCssCommand.MessageProcessor", "addStyleSheet");
		itemE.setAttribute("eid", command.getRenderId());

		Element linkE = serverMessage.getDocument().createElement("link");
		linkE.setAttribute("media", command.getMedia());
		linkE.setAttribute("href", command.getStyleSheetURI());
		linkE.setAttribute("type", "text/css");
		linkE.setAttribute("rel", "Stylesheet");
		itemE.appendChild(linkE);
	}

	private void renderCssStyleRemove(RenderContext rc, CssStyleRemove command) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemE = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPCssCommand.MessageProcessor", "removeStyle");
		itemE.setAttribute("eid", command.getRenderId());
	}

	private void renderCssStyleApplyTo(RenderContext rc, CssStyleApplyTo command) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemE = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPCssCommand.MessageProcessor", "applyTo");
		itemE.setAttribute("eid", ContainerInstance.getElementId(command.getTargetComponent()));
		itemE.setAttribute("className", command.getClassName());
	}
	
	private void renderCssStyleRemoveFrom(RenderContext rc, CssStyleRemoveFrom command) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemE = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPCssCommand.MessageProcessor", "removeFrom");
		itemE.setAttribute("eid", ContainerInstance.getElementId(command.getTargetComponent()));
		itemE.setAttribute("className", command.getClassName());
	}
	
}
