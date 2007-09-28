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

import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;

import echopointng.command.AttributesAdd;
import echopointng.command.JavaScriptEval;
import echopointng.command.JavaScriptInclude;
import echopointng.command.Print;
import echopointng.ui.resource.Resources;

/**
 * A <code>CommandSynchronizePeer</code> implementation for general
 * EPNG commands.
 */
public class GeneralCommandPeer implements CommandSynchronizePeer {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service COMMAND_SERVICE = JavaScriptService.forResource("EPNG.Command", "/echopointng/ui/resource/js/command.js");
	static {
		WebRenderServlet.getServiceRegistry().add(COMMAND_SERVICE);
	}

	/**
	 * @see nextapp.echo2.webcontainer.CommandSynchronizePeer#render(
	 *      nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.Command)
	 */
	public void render(RenderContext rc, Command command) {
		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		serverMessage.addLibrary(COMMAND_SERVICE.getId());
		if (command instanceof Print) {
			renderPrintCommand(rc, (Print) command);
		} else if (command instanceof AttributesAdd) {
			renderAttributesAdd(rc,(AttributesAdd) command);
		} else if (command instanceof JavaScriptEval) {
			renderEvalJS(rc,(JavaScriptEval) command);
		} else if (command instanceof JavaScriptInclude) {
			renderIncludeJS(rc,(JavaScriptInclude) command);
		} else {
			throw new IllegalArgumentException("Unhandled Command type : " + String.valueOf(command));
		}
	}

	private void renderAttributesAdd(RenderContext rc, AttributesAdd command) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemE = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPCommand.MessageProcessor", "attributesAdd");
		itemE.setAttribute("eid",ContainerInstance.getElementId(command.getComponent()));
		
		String[] names = command.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			Element attrE = serverMessage.getDocument().createElement("attribute");
			itemE.appendChild(attrE);
			attrE.setAttribute("name",names[i]);
			attrE.setAttribute("value",String.valueOf(command.getAttribute(names[i])));
		}
		
	}
	
	private void renderPrintCommand(RenderContext rc, Print command) {
		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPCommand.MessageProcessor", "print");
	}
	
	private void renderEvalJS(RenderContext rc, JavaScriptEval command) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemE = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPCommand.MessageProcessor", "evalJS");
		CDATASection cdata = serverMessage.getDocument().createCDATASection(command.getJavaScript());
		itemE.appendChild(cdata);
	}
	
	private void renderIncludeJS(RenderContext rc, JavaScriptInclude command) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemE = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPCommand.MessageProcessor", "includeJS");
		itemE.setAttribute("javascriptURI",command.getJavaScriptURI());
	}
	
}
