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

import java.io.IOException;

import nextapp.echo2.app.Component;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webrender.Connection;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.util.JavaScriptCompressor;

/**
 * <code>JavaScriptSnippetService</code> allows for dynamic snippets of
 * JavaScript to be sent to the broweser. And because this class has a setter
 * for the content, you can "re-used" instances of this Service and change the
 * content servered.
 * <p>
 * It is designed to give you a "per component instance" of some JavaScript
 * snippet, as opposed to the standard <code>JavaScriptService</code> which is
 * designed for static JS content.
 * <p>
 * Use the getId() to lookup and place the service into the ContainerInstance
 * service registry.
 */

public class JavaScriptSnippetService implements Service {

	/**
	 * This will look inside the ContainerInstance's service
	 * registry and finds a JavaScriptSnippetService that has
	 * previously been created for the component.
	 * <p>
	 * This is designed to allow re-use of the Service.
	 * 
	 * @param ci - the ContainerInstance which may contain the Service
	 * @param component - the associated component
	 * 
	 * @return a JavaScriptSnippetService or null if one cant be found
	 */
	public static JavaScriptSnippetService forComponent(ContainerInstance ci, Component component) {
		return null;
	}
	
	private String id;

	private String content;

	/**
	 * Constructs a <code>JavaScriptSnippetService</code> that serves the
	 * specified JS content. The component's identifier is used to help name the
	 * Service.
	 * 
	 * @param associatedComponent -
	 *            the associated Component for this snippest of JS.
	 * @param content -
	 *            the code of the JS snippet
	 *  
	 */
	public JavaScriptSnippetService(Component associatedComponent, String content) {
		this.id = makeServiceId(associatedComponent);
		setContent(content);
	}

	/**
	 * Constructs a <code>JavaScriptSnippetService</code> that serves the
	 * NO specified JS content. The component's identifier is used to help name the
	 * Service.
	 * 
	 * @param associatedComponent -
	 *            the associated Component for this snippest of JS.
	 */
	public JavaScriptSnippetService(Component associatedComponent) {
		this(associatedComponent,null);
	}

	private static String makeServiceId(Component component) {
		return "EPJSSS_" + component.getId();
	}
	
	/**
	 * @see nextapp.echo2.webrender.server.Service#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @see nextapp.echo2.webrender.server.Service#getVersion()
	 */
	public int getVersion() {
		return DO_NOT_CACHE;
	}

	/**
	 * @see nextapp.echo2.webrender.server.Service#service(nextapp.echo2.webrender.server.Connection)
	 */
	public void service(Connection conn) throws IOException {
		conn.getResponse().setContentType("text/plain");
		conn.getWriter().print(content);
	}

	/**
	 * Returns the content of the JavaScript snippet, which may have been
	 * compressed.
	 * 
	 * @return the content of the JavaScript snippet
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the content of the JavaScript snippet, which may been compressed.
	 * 
	 * @param content-
	 *            the JavaScript snippest to be sent to the browser.
	 */
	public void setContent(String content) {
		if (content != null)
			content = JavaScriptCompressor.compress(content);
		this.content = content;
	}
}
