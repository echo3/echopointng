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
 * the terms of anyone of the MPL, the GPL or the LGPL.
 */
package echopointng.ui.syncpeer;

import java.util.ArrayList;
import java.util.List;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.StyleSheet;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerContext;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.SynchronizePeerFactory;
import nextapp.echo2.webrender.ClientProperties;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.servermessage.DomUpdate;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import echopointng.TemplatePanel;
import echopointng.stylesheet.CssStyleSheetException;
import echopointng.stylesheet.CssStyleSheetLoader;
import echopointng.template.TemplateCachingHints;
import echopointng.template.TemplateDataSource;
import echopointng.template.TemplateTextSubstitution;
import echopointng.ui.resource.Resources;
import echopointng.ui.template.TemplateCompiler;
import echopointng.ui.template.TemplateCompilerLoader;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;
import echopointng.ui.util.io.StringInputStream;
import echopointng.util.HtmlKit;
import echopointng.util.collections.ExpiryCache;
import echopointng.util.reflect.ReflectionKit;

/**
 * <code>TemplatePanelPeer</code> is a peer for <code>TemplatePanel</code>
 */
public class TemplatePanelPeer extends AbstractEchoPointContainerPeer {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service TEMPLATE_JS_SERVICE = JavaScriptService.forResource("EPNG.TemplatePanel",
			"/echopointng/ui/resource/js/templatepanel.js");
	static {
		WebRenderServlet.getServiceRegistry().add(TEMPLATE_JS_SERVICE);
	}

	/**
	 * A cache of templates previously encountered. As the XHTML results are
	 * cached it will save time compiling them again.  <code>SoftReference</code>'s are not
	 * used by default.
	 * <p>
	 * You can change the default "time to live" and "default access timeout"
	 * values by calling
	 * <code>TemplatePanelPeer.COMPILED_TEMPLATE_CACHE.setTimeToLive(xxx)</code>
	 * and
	 * <code>TemplatePanelPeer.COMPILED_TEMPLATE_CACHE.setAccessTimeout(xxx)</code>
	 * and whether <code>SoftReference</code>s are used to cache objects by
	 * using
	 * <code>TemplatePanelPeer.COMPILED_TEMPLATE_CACHE.setSoftReferences(xxx)</code>
	 * <p>
	 * You should however consider using <code>TemplateCachingHints</code> to provide these
	 * caching values on a case by case basis.
	 * 
	 * @see TemplateCachingHints
	 * @see java.lang.ref.SoftReference
	 */
	public static final ExpiryCache COMPILED_TEMPLATE_CACHE;
	static {
		COMPILED_TEMPLATE_CACHE = new ExpiryCache(ExpiryCache.DEFAULT_TIME_TO_LIVE, ExpiryCache.DEFAULT_ACCESS_TIMEOUT, false);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		TemplatePanel templatePanel = (TemplatePanel) component;

		// we now need to ask an appropriate template compiler to return
		// a W3C DOM Element which has been "transmogrified" and we
		// can then send it back as the panels DOM
		TemplateResult compiledTemplate = renderTemplateData(rc, templatePanel);
		if (compiledTemplate != null) {

			createInitDirective(rc, templatePanel, compiledTemplate);

			rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
			rc.addLibrary(TEMPLATE_JS_SERVICE);

			compiledTemplate.markupE.setAttribute("id", ContainerInstance.getElementId(templatePanel));
			compiledTemplate.markupE.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
			rc.addStandardWebSupport(compiledTemplate.markupE);

			if (!compiledTemplate.markupE.hasAttribute("style")) {
				CssStyleEx cssStyle = new CssStyleEx(component);
				compiledTemplate.markupE.setAttribute("style", cssStyle.renderInline());
			}
			parent.appendChild(compiledTemplate.markupE);
		}
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc, update, component);
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(TEMPLATE_JS_SERVICE.getId());
		createDisposeDirective(rc.getServerMessage(), (TemplatePanel) component);
	}

	/**
	 * 
	 */
	protected void createDisposeDirective(ServerMessage serverMessage, TemplatePanel templatePanel) {
		serverMessage.addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		serverMessage.addLibrary(TEMPLATE_JS_SERVICE.getId());

		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPTemplatePanel.MessageProcessor",
				"dispose", new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemizedUpdateElement.appendChild(itemElement);
		itemElement.setAttribute("eid", ContainerInstance.getElementId(templatePanel));
	}

	/**
	 * Has this XML format
	 * 
	 * item {eid} inlineStyleContainer style style style externalStyleContainer
	 * style style style
	 * 
	 */
	protected void createInitDirective(RenderingContext rc, TemplatePanel templatePanel, TemplateResult templateResult) {
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
				"EPTemplatePanel.MessageProcessor", "init", new String[0], new String[0]);

		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemizedUpdateElement.appendChild(itemElement);
		itemElement.setAttribute("eid", ContainerInstance.getElementId(templatePanel));

		ApplicationInstance app = ApplicationInstance.getActive();
		ContainerContext containerContext = (ContainerContext) app.getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
		ClientProperties clientProperties = containerContext.getClientProperties();
		String userAgent = clientProperties.getString(ClientProperties.NAVIGATOR_USER_AGENT);

		// <LINK> fixup
		//
		// The link elements must be sent down to the client and
		// inserted directly via JS. This is because they
		// have come out of the head and need to be replaced there!
		//
		Element externalStyleContainerE = rc.getServerMessage().getDocument().createElement("externalStyleContainer");
		for (int i = 0; i < templateResult.externalStyles.length; i++) {
			externalStyleContainerE.appendChild(templateResult.externalStyles[i]);
		}
		itemElement.appendChild(externalStyleContainerE);

		// <SCRIPT> fixup
		//
		// The script elements must be sent down to the client and
		// inserted directly via JS. This is because they
		// have come out of the head and need to be replaced there!
		//
		Element externalScriptContainerE = rc.getServerMessage().getDocument().createElement("externalScriptContainer");
		for (int i = 0; i < templateResult.externalScripts.length; i++) {
			externalScriptContainerE.appendChild(templateResult.externalScripts[i]);
		}
		itemElement.appendChild(externalScriptContainerE);
		
		//
		// <STYLE> fixup
		//
		// If we are running on the Gecko engine then the <style>
		// elements can be inserted into the markup text itself,
		// because the browser engine DOM import can handle it
		//
		// IE must fix up the styles via JS because it does not
		// support inserting <style> elements via DOM import
		// 
		Element inlineStyleContainerE = rc.getServerMessage().getDocument().createElement("inlineStyleContainer");
		itemElement.appendChild(inlineStyleContainerE);
		if (userAgent.indexOf("Gecko") != -1) {
			Element styleParentE = templateResult.markupE;
			if (getElementsByTag(styleParentE, "body").length > 0) {
				styleParentE = getElementsByTag(styleParentE, "body")[0];
			}
			for (int i = 0; i < templateResult.inlineStyles.length; i++) {
				styleParentE.insertBefore(templateResult.inlineStyles[i], styleParentE.getFirstChild());
			}
		} else {
			for (int i = 0; i < templateResult.inlineStyles.length; i++) {
				inlineStyleContainerE.appendChild(templateResult.inlineStyles[i]);
			}
		}
	}

	// =========================================================================
	// TEMPLATE PARSING CODE BELOW
	// =========================================================================

	private static class CompilationResult {
		private Element compiledXHTML;

		private boolean wasCompiled;

		private long compilationTime;

		CompilationResult(Element compiledXHTML, boolean wasCompiled) {
			this.compiledXHTML = compiledXHTML;
			this.wasCompiled = wasCompiled;
			this.compilationTime = 0;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("Compilation time ms : ");
			sb.append(compilationTime);
			return sb.toString();
		}
	}

	private static class TemplateResult {
		/**
		 * Constructs a <code>TemplateResult</code>
		 * 
		 * @param errorE
		 */
		public TemplateResult(Element markupE) {
			this.markupE = markupE;
			this.externalStyles = new Element[0];
			this.inlineStyles = new Element[0];
			this.externalScripts = new Element[0];
		}

		Element markupE;

		Element[] externalStyles;

		Element[] inlineStyles;

		Element[] externalScripts;
	}

	/**
	 * This is asked to render the tempate data and return a W3C DOM Element
	 * that can be imported into the current TemplatePanel rendering DOM tree.
	 * 
	 * @param templatePanel -
	 *            the templatePanel in question
	 * @return a W3C node or null if it cant be compiled
	 */
	private TemplateResult renderTemplateData(RenderingContext rc, TemplatePanel templatePanel) {
		TemplateDataSource tds = (TemplateDataSource) rc.getRP(TemplatePanel.PROPERTY_TEMPLATE_DATA_SOURCE);
		try {
			CompilationResult compileResult = getCompiledTemplate(rc, tds);
			Element markup = compileResult.compiledXHTML;
			if (markup == null)
				return null;

			boolean doStyleProcessing = compileResult.wasCompiled || (!templatePanel.isStyleApplied());
			//
			// make sure the new markup is now part of the original
			// rendering Document. Otherwise further child rendering
			// issues could occur.
			markup = (Element) rc.getDocument().importNode(markup, true);
			//
			// now find all the <component> tags and replace them with
			// the contents of named components
			Element componentElements[] = getElementsByTags(markup, new String[] { "component", "input", "button", "select" });
			for (int i = 0; i < componentElements.length; i++) {
				Element componentElement = componentElements[i];
				String componentName = componentElement.getAttribute("name");
				String componentId = componentElement.getAttribute("id");
				String componentStyleValue = componentElement.getAttribute("style");
				String componentStyleNameValue = componentElement.getAttribute("styleName");

				//
				// lookup by name first and then by id after that
				if (componentName.length() == 0) {
					componentName = componentId;
				}
				Component child = templatePanel.getNamedComponent(componentName);
				Element newComponentElement;
				if (child != null) {
					// apply any style attribute to the component first
					if (doStyleProcessing) {
						// apply styleName=xxx" by calling setStyleName() if present
						if (componentStyleNameValue != null && componentStyleNameValue.length() > 0) {
							child.setStyleName(componentStyleNameValue);
						}
						// apply style="xxx" if applicable
						applyStyle(templatePanel, child, componentStyleValue);
						templatePanel.setStyleApplied(true);

						//
						// if the component has a public void setText(String s)
						// method and they want
						// component text setting then do it.
						if (rc.getRP(TemplatePanel.PROPERTY_INVOKE_SETTEXT, false)) {
							String textToBeSet = getChildText(componentElement);
							if (textToBeSet.length() == 0) {
								textToBeSet = componentElement.getAttribute("value");
							}
							if (textToBeSet.length() > 0) {
								ReflectionKit
										.invokeIfPresent("setText", new Class[] { String.class }, Void.TYPE, child, new Object[] { textToBeSet });
							}
						}
					}
					// and render the child component into a containing Element
					newComponentElement = renderChild(rc, child, componentName);
				} else {
					newComponentElement = null;
					if (componentName.length() > 0) {
						//
						// we have a component named but not found. This is not
						// kosher. what should we do?
						if (rc.getRP(TemplatePanel.PROPERTY_EXCEPTION_ON_FAILURE, false)) {
							throw new RuntimeException("A component named '" + componentName + "' could not be found in TemplateDataSource "
									+ tds.getCanonicalName() + " could not be compiled.");
						} else {
							newComponentElement = createErrorElement(rc, "Component named '" + componentName + "' could not be found!");
						}
					}
				}
				//
				// if we have new markup, then replace it in
				if (newComponentElement != null) {
					Element componentContainerElement = (Element) componentElement.getParentNode();
					componentContainerElement.replaceChild(newComponentElement, componentElement);
				}
			}

			//
			// now find all the <text> tags and replace them with
			// the text subsitution values
			TemplateTextSubstitution textSubstitution = (TemplateTextSubstitution) rc.getRP(TemplatePanel.PROPERTY_TEMPLATE_TEXT_SUBSTITUTION);
			Element textElements[] = getElementsByTag(markup, "text");
			for (int i = 0; i < textElements.length; i++) {
				Element textMarkerElement = textElements[i];
				String textName = textMarkerElement.getAttribute("name");
				String textValue = (textSubstitution == null ? "" : textSubstitution.getSubstitutionText(textName));

				Element textContainerElement = (Element) textMarkerElement.getParentNode();
				Text newText = rc.createText(textValue);
				textContainerElement.replaceChild(newText, textMarkerElement);
			}
			//
			// now we may have to re-parent the DOM tree as we dont want
			// extra BODY tags turning up in the output if the template is
			// a full XHTML document or a BODY snippet
			//
			TemplateResult transResult = transmogrifyMarkup(markup);
			return transResult;
		} catch (Exception e) {
			String errorStr = "TemplateDataSource " + tds.getCanonicalName() + " could not be compiled : " + e.toString();
			if (rc.getRP(TemplatePanel.PROPERTY_EXCEPTION_ON_FAILURE, false)) {
				throw new RuntimeException(errorStr,e);
			} else {
				Element errorE = createErrorElement(rc, errorStr);
				return new TemplateResult(errorE);
			}
		}
	}

	/**
	 * Looks into the cache to see if we already have the compiled template data
	 * and if not, finds a compiler to compile it.
	 */
	private CompilationResult getCompiledTemplate(final RenderingContext rc, TemplateDataSource tds) throws Exception {
		CompilationResult compilationResult;
		if (tds == null) {
			return new CompilationResult(null, false);
		}
		long startMS = System.currentTimeMillis();

		String canonicalName = tds.getCanonicalName();
		TemplateCachingHints hints = tds.getCachingHints();
		Element compiledXHTML;
		// do we have a copy of the Element in our cache
		if (hints != null) {
			compiledXHTML = (Element) COMPILED_TEMPLATE_CACHE.get(canonicalName, hints.getLastModified());
			if (compiledXHTML != null) {
				compilationResult = new CompilationResult(compiledXHTML, false);
				compilationResult.compilationTime = System.currentTimeMillis() - startMS;
				return compilationResult;
			}
		}
		//
		// find a tempate compiler that will compile the given
		// content type
		//
		TemplateCompilerLoader loader = TemplateCompilerLoader.forClassLoader(Thread.currentThread().getContextClassLoader());
		TemplateCompiler compiler = loader.getTemplateCompiler(tds.getContentType());
		if (compiler == null) {
			throw new IllegalStateException("A TemplateCompiler cannot be found for content type : " + tds.getContentType());
		}
		compiledXHTML = compiler.compileTemplateDataIntoXHTML(rc, tds);

		// we put it into the cache if they say its OK.  We never put it if there are no hints
		if (hints != null) {
			long ato = hints.getAccessTimeout();
			long ttl = hints.getTimeToLive();
			long version = hints.getLastModified();
			COMPILED_TEMPLATE_CACHE.put(canonicalName, compiledXHTML, ttl, ato, version);
		}

		compilationResult = new CompilationResult(compiledXHTML, true);
		compilationResult.compilationTime = System.currentTimeMillis() - startMS;
		return compilationResult;
	}

	/**
	 * This will perform HTML fixups on the markup. This is needed becaue the
	 * document may be a full HTML document and hence you cant have 2 HTML/BODY
	 * tags in the one document.
	 * <p>
	 * Also the STYLE tags in the HEAD need to be transferred to the start of
	 * the markup.
	 * 
	 * @param markup
	 * @return
	 */
	private TemplateResult transmogrifyMarkup(Element markup) {
		Element heads[] = getElementsByTag(markup, "head");
		Element bodys[] = getElementsByTag(markup, "body");
		if (markup.getTagName().equals("body") || markup.getTagName().equals("html") || bodys.length > 0) {
			if (bodys.length > 0)
				markup = bodys[0];
			// markup = reparentElement(markup, DEFAULT_CONTAINER_TAG);
			markup = reparentElement(markup, "div");
		}
		List cssLinksList = new ArrayList();
		List stylesList = new ArrayList();
		List scriptsList = new ArrayList();

		//
		// take all the <style> elements out of the markup
		// in order and save them away
		//
		Element[] styles = getAllStylesFromMarkup(markup, heads);
		for (int j = 0; j < styles.length; j++) {
			stylesList.add(styles[j]);
			styles[j].getParentNode().removeChild(styles[j]);
		}
		//
		// take all the css <link> elements out of the markup
		// and save them away
		//
		for (int i = 0; i < heads.length; i++) {
			Element[] links = getElementsByTag(heads[i], "link");
			for (int j = 0; j < links.length; j++) {
				//
				// is it really a CSS link
				if ("text/css".equalsIgnoreCase(links[j].getAttribute("type"))) {
					cssLinksList.add(links[j]);
					links[j].getParentNode().removeChild(links[j]);
				}
			}
		}

		//
		// take all the  <script> elements out of the markup
		// and save them away
		//
		for (int i = 0; i < heads.length; i++) {
			Element[] scripts = getElementsByTag(heads[i], "script");
			for (int j = 0; j < scripts.length; j++) {
				scriptsList.add(scripts[j]);
				scripts[j].getParentNode().removeChild(scripts[j]);
			}
		}
		
		//
		// add the namespace support for xhtml
		markup.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");

		TemplateResult transResult = new TemplateResult(markup);
		transResult.externalStyles = (Element[]) cssLinksList.toArray(new Element[cssLinksList.size()]);
		transResult.inlineStyles = (Element[]) stylesList.toArray(new Element[stylesList.size()]);
		transResult.externalScripts = (Element[]) scriptsList.toArray(new Element[scriptsList.size()]);

		return transResult;
	}

	private Element[] getAllStylesFromMarkup(Element markupBody, Element heads[]) {
		List styleList = new ArrayList();
		Element[] styles;
		for (int i = 0; i < heads.length; i++) {
			styles = getElementsByTag(heads[0], "style");
			for (int j = 0; j < styles.length; j++) {
				styleList.add(styles[j]);
			}
		}
		styles = getElementsByTag(markupBody, "style");
		for (int j = 0; j < styles.length; j++) {
			styleList.add(styles[j]);
		}
		return (Element[]) styleList.toArray(new Element[styleList.size()]);
	}

	/**
	 * And error mesage to reported back when a template cannot be executed
	 * properly
	 */
	private Element createErrorElement(RenderingContext rc, String errorMessage) {
		Element div = rc.createE("div");
		div.setAttribute("style",
				"background:red;color:white;font-size:8pt;border-width:1; border-style:solid; border-color:black; padding:4; margin:4");

		div.appendChild(rc.createE("b")).appendChild(rc.createE("p")).appendChild(rc.createText("Template Error : " + errorMessage));
		return div;
	}

	/**
	 * Returns all the Element children of parent with the specified tag name
	 */
	private Element[] getElementsByTag(Element parent, String tagName) {
		NodeList nodeList = parent.getElementsByTagName(tagName);
		Element elements[] = new Element[nodeList.getLength()];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = (Element) nodeList.item(i);
		}
		return elements;
	}

	private Element[] getElementsByTags(Element parent, String[] tagNames) {
		List elementsList = new ArrayList();
		for (int i = 0; i < tagNames.length; i++) {
			NodeList nodeList = parent.getElementsByTagName(tagNames[i]);
			int nllen = nodeList.getLength();
			for (int j = 0; j < nllen; j++) {
				elementsList.add(nodeList.item(j));
			}
		}
		return (Element[]) elementsList.toArray(new Element[elementsList.size()]);
	}

	/**
	 * Returns all the top level Node children of parent
	 */
	private Node[] getChildNodes(Element parentE) {
		NodeList nodeList = parentE.getChildNodes();
		int len = nodeList.getLength();
		Node nodes[] = new Node[len];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = nodeList.item(i);
		}
		return nodes;
	}

	private String getChildText(Element parentE) {
		StringBuffer sb = new StringBuffer();
		Node[] nodes = getChildNodes(parentE);
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] instanceof Text) {
				sb.append(nodes[i].getNodeValue());
			}
		}
		return sb.toString();
	}

	/**
	 * Changes the parent of the children of sourceElement into a new element of
	 * type tag.
	 */
	private Element reparentElement(Element sourceElement, String tag) {
		Document doc = sourceElement.getOwnerDocument();
		Element newParent = doc.createElement(tag);
		NamedNodeMap attrs = sourceElement.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attr = (Attr) attrs.item(i);
			
			Attr newAttr = doc.createAttribute(attr.getName());
			newAttr.setValue(attr.getValue());
			newParent.setAttributeNode(newAttr);
		}
		Node[] children = getChildNodes(sourceElement);
		for (int i = 0; i < children.length; i++) {
			newParent.appendChild(children[i]);
		}
		return newParent;
	}

	/**
	 * Applies a style="xxx" atribute to the component if it has not already
	 * been done before.
	 */
	private void applyStyle(TemplatePanel templatePanel, Component child, String styleValue) throws CssStyleSheetException {
		if (styleValue.length() > 0) {
			StringBuffer cssText = new StringBuffer();
			cssText.append(child.getClass().getName());
			cssText.append("{");
			cssText.append(styleValue);
			cssText.append("}");

			// we need to read any style data specified on the component
			StringInputStream sis = new StringInputStream(cssText.toString());
			StyleSheet styleSheet = CssStyleSheetLoader.load(sis, Thread.currentThread().getContextClassLoader());
			Style style = styleSheet.getStyle(child.getClass(), null);
			child.setStyle(style);
		}
	}

	/**
	 * Renders an individual child component of the <code>TemplatePanel</code>
	 * into a double wrapped container element. The double wrapping is needed to
	 * allow direct replacement of components within the template mark up.
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param child
	 *            the child <code>Component</code> to be rendered
	 * @return the containing Element for the rendered component
	 */
	private Element renderChild(RenderingContext rc, Component child, String componentName) {
		Element childContainerE = rc.createE(DEFAULT_CONTAINER_TAG);
		childContainerE.setAttribute("id", getMarkerContainerId(child.getParent(), componentName));
		renderReplaceableChild(rc, rc.getServerComponentUpdate(), childContainerE, child);
		return childContainerE;
	}

	/**
	 * Returns an identifier in the form "parentId_marks_componentName".
	 * 
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	private String getMarkerContainerId(Component parent, String componentName) {
		String parentId = ContainerInstance.getElementId(parent);
		return parentId + "_marks_" + HtmlKit.encode(componentName);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointContainerPeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		boolean fullReplace = false;
		if (update.hasUpdatedLayoutDataChildren()) {
			fullReplace = true;
		} else if (update.hasUpdatedProperties()) {
			if (partialUpdateManager.canProcess(rc, update)) {
				partialUpdateManager.process(rc, update);
			} else {
				fullReplace = true;
			}
		}
		if (update.hasAddedChildren() || update.hasRemovedChildren()) {
			// find out what components have been added and removed and then
			// remove/add them again using the double wrapped mechanism inside
			// our
			// markup
			//
			// removed children first
			renderUpdateRemoveChildren(rc, update);
			//
			// now re-add any new children but only those that are "named"
			// components from
			// the templates point of view
			Element domAddElementDirective = DomUpdate.renderElementAdd(rc.getServerMessage());
			TemplatePanel panel = (TemplatePanel) update.getParent();
			Component[] addedChildren = update.getAddedChildren();
			for (int i = 0; i < addedChildren.length; i++) {
				Component newChild = addedChildren[i];
				String componentNames[] = panel.getNamedComponents();
				for (int j = 0; j < componentNames.length; j++) {
					Component testComponent = panel.getNamedComponent(componentNames[j]);
					if (newChild.equals(testComponent) && newChild.isRenderVisible()) {
						//
						// its okay to render it as a new child addition. The
						// double
						// wrapped markup should be in place because it must
						// have already
						// been rendered.
						//
						String outerElementId = getMarkerContainerId(panel, componentNames[j]);
						DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
						renderReplaceableChild(rc, update, htmlFragment, newChild);
						DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElementDirective, outerElementId, htmlFragment);
					}
				}
			}
			fullReplace = false;
		}
		if (fullReplace) {
			// Perform full update.
			String removeTagetId = ContainerInstance.getElementId(update.getParent());
			DomUpdate.renderElementRemove(rc.getServerMessage(), removeTagetId);
			renderAdd(rc, update, targetId, update.getParent());
		}
		return fullReplace;
	}

	/**
	 * Our special implementation of this method knows about DisplayLayoutData
	 * 
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderReplaceableChild(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, org.w3c.dom.Node,
	 *      nextapp.echo2.app.Component)
	 */
	protected Element renderReplaceableChild(RenderContext rc, ServerComponentUpdate update, Node parentNode, Component child) {
		Document doc = parentNode.getOwnerDocument();
		Element childContainerE = doc.createElement(DEFAULT_CONTAINER_TAG);
		CssStyle containerStyle = Render.itsDisplayLayoutData(rc,child);
		if (containerStyle.hasAttributes()) {
			// divs for display layout data please as positioning and heights
			// must take effect
			childContainerE = doc.createElement("div");
			//
			// set the style on the outer container tag div
			childContainerE.setAttribute("style", containerStyle.renderInline());
			Render.itsDisplayLayoutData(rc,child,childContainerE);			
		}

		String containerId = getContainerId(child);
		childContainerE.setAttribute("id", containerId);
		parentNode.appendChild(childContainerE);
		ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
		if (syncPeer instanceof DomUpdateSupport) {
			((DomUpdateSupport) syncPeer).renderHtml(rc, update, childContainerE, child);
		} else {
			syncPeer.renderAdd(rc, update, containerId, child);
		}
		return childContainerE;
	}

}
