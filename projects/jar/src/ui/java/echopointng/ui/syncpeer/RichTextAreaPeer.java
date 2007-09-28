/*
 * This file is part of the Echo Point Project. This project is a collection of
 * Components that have extended the Echo Web Application Framework.
 * 
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or the
 * GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in which
 * case the provisions of the GPL or the LGPL are applicable instead of those
 * above. If you wish to allow use of your version of this file only under the
 * terms of either the GPL or the LGPL, and not to allow others to use your
 * version of this file under the terms of the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and other
 * provisions required by the GPL or the LGPL. If you do not delete the
 * provisions above, a recipient may use your version of this file under the
 * terms of any one of the MPL, the GPL or the LGPL.
 */
package echopointng.ui.syncpeer;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.text.TextComponent;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerContext;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.ClientProperties;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.servermessage.DomUpdate;
import nextapp.echo2.webrender.service.JavaScriptService;
import nextapp.echo2.webrender.util.DomUtil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.ButtonEx;
import echopointng.EPNG;
import echopointng.RichTextArea;
import echopointng.richtext.RichTextRenderer;
import echopointng.richtext.RichTextSpellChecker;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.HtmlNodeLexer;
import echopointng.ui.util.HtmlTable;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.LayoutStrut;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

public class RichTextAreaPeer extends AbstractEchoPointPeer implements PropertyUpdateProcessor, ActionProcessor
{

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service RICH_TEXT_SERVICE = JavaScriptService.forResource("EPNG.RichTextArea", "/echopointng/ui/resource/js/rta.js");
	static {
		WebRenderServlet.getServiceRegistry().add(RICH_TEXT_SERVICE);
	}

	private static final ImageReference IMAGE_WIGGLY_RED_LINE = new ResourceImageReference("/echopointng/resource/images/richtext/ep_rt_wiggly_redline.gif");
	
     /**
     * Constructs a <code>RichTextAreaPeer</code>
     */
    public RichTextAreaPeer() {
    	super();
		partialUpdateManager.add(RichTextArea.TEXT_CHANGED_PROPERTY, new PartialUpdateParticipant() {
			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}
			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				htmlChanged(rc,update);
			}
		});

		partialUpdateManager.add(RichTextArea.PROPERTY_SPELL_CHECK_IN_PROGRESS, new PartialUpdateParticipant() {
			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}
			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				htmlChanged(rc,update);
			}
		});
    	
    }
    
    /**
     * Called when a RTA property has changed that may require spelling checking to occur.
     */
    private void htmlChanged(RenderContext rc, ServerComponentUpdate update) {
		RichTextArea rta = (RichTextArea) update.getParent();
		String text = rta.getText();

		// an XML message directive please to tell the popup to expand!
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
				"EPRTA.MessageProcessor", "htmlChanged", new String[0], new String[0]);
		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemizedUpdateElement.appendChild(itemElement);
		itemElement.setAttribute("eid", ContainerInstance.getElementId(rta));
		
		boolean spellCheckInProgress = ((Boolean) rta.getRenderProperty(RichTextArea.PROPERTY_SPELL_CHECK_IN_PROGRESS)).booleanValue(); 
		text = spellCheckText(rc,itemElement,rta,text,spellCheckInProgress);
		itemElement.setAttribute("html", text);
		itemElement.setAttribute("spellCheckInProgress", String.valueOf(spellCheckInProgress));
    }

	/**
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	public String getContainerId(Component child) {
		throw new UnsupportedOperationException("RichTextArea does not support children.");
	}

	/**
	 * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
		String propertyName = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
		if (TextComponent.TEXT_CHANGED_PROPERTY.equals(propertyName)) {
			String propertyValue = DomUtil.getElementText(propertyElement);
			propertyValue = removeSpellCheckText(propertyValue);
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, TextComponent.TEXT_CHANGED_PROPERTY, propertyValue);
		}
	}
	
	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance, nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		String actionName = actionElement.getAttribute(ACTION_NAME); 
		String actionValue = actionElement.getAttribute(ACTION_VALUE);
        ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, actionName, actionValue);

	}
	
	/**
	 * This will remove any spell check markup that has been placed in the
	 * RTA HTML text.
	 */
	private String removeSpellCheckText(String htmlText) {
		htmlText = HtmlNodeLexer.lex(htmlText,new HtmlNodeLexer.HtmlLexerCallBack() {
			boolean deleting = false;
			String currentTag = null;
			int depthCount;
			public StringBuffer onCommentNode(StringBuffer comment) {
				return deleting ? null : comment;
			}
			public StringBuffer onTextNode(StringBuffer textNode) {
				if (deleting) {
					if ("span".equalsIgnoreCase(currentTag)) {
						// we want the text inside a spell span but not the span itself
						return textNode;
					}
					return null;
				} else {
					return textNode;
				}
			}
			public StringBuffer onElementNode(StringBuffer element) {
				if (deleting) {
					boolean isTagStart = element.indexOf("<"+currentTag) == 0;
					boolean isTagEnd   = element.indexOf("</"+currentTag) == 0; 
					if (isTagStart) {
						depthCount++;
					}
					if (isTagEnd) {
						if (depthCount == 0) {
							deleting = false;
							return null;
						} else {
							depthCount--;
						}
					}
					return null;
				} else {
					// IE has no class=epspell while Moz has class="epspell"
					if (element.indexOf("class=\"epspell") != -1 || element.indexOf("class=epspell") != -1) {
						deleting = true;
						depthCount = 0;
						currentTag = getTag(element);
						return null;
					} else {
						return element;
					}
				}
			}
			
			private String getTag(StringBuffer element) {
				StringBuffer tagName = new StringBuffer();
				int len = element.length();
				char[] chars = new char[len];
				element.getChars(0,len,chars,0);
				for (int i = 0; i < chars.length; i++) {
					if (chars[i] == '<' || chars[i] == '/')
						continue;
					if (chars[i] == ' ' || chars[i] == '>')
						break;
					tagName.append(chars[i]);
					
					
				}
				return tagName.toString();
			}
			
			
		});
		return htmlText;
	}
	
	/**
	 * If the spelling checking is on, then we need to go through and check each word
	 * in the HTML and "hilight" it with our spell check support.
	 * <p>
	 */
	private String spellCheckText(final RenderContext rc, final Element directiveItem, final RichTextArea rta, String htmlText, boolean spellCheckInProgress) {
		final String elementId = ContainerInstance.getElementId(rta);
		
		final RichTextSpellChecker spellChecker = rta.getSpellChecker();
		if (htmlText == null || htmlText.trim().length() == 0 || spellChecker == null) {
			return htmlText;
		}
		boolean alreadyHasSpellingMarkup = false;
		if (htmlText.indexOf("class=\"epspell\"") != -1) {
			alreadyHasSpellingMarkup = true;
		} else if (htmlText.indexOf("class=epspell") != -1) {
			alreadyHasSpellingMarkup = true;
		}
		if (spellCheckInProgress) {
			if (alreadyHasSpellingMarkup) {
				return htmlText;
			}
			String newHtmlText = HtmlNodeLexer.lex(htmlText, new  HtmlNodeLexer.HtmlLexerCallBack() {
				int spellCount = 0;
				
				/**
				 * @see echopointng.ui.util.HtmlNodeLexer.HtmlLexerCallBack#onCommentNode(java.lang.StringBuffer)
				 */
				public StringBuffer onCommentNode(StringBuffer comment) {
					return comment;
				}
				
				/**
				 * @see echopointng.ui.util.HtmlNodeLexer.HtmlLexerCallBack#onElementNode(java.lang.StringBuffer)
				 */
				public StringBuffer onElementNode(StringBuffer element) {
					return element;
				}
				/**
				 * @see echopointng.ui.util.HtmlNodeLexer.HtmlLexerCallBack#onTextNode(java.lang.StringBuffer)
				 */
				public StringBuffer onTextNode(StringBuffer textNode) {
					StringBuffer newTextNode = new StringBuffer();
					RichTextSpellChecker.SpellCheckerWord[] words = spellChecker.parseWords(textNode.toString());
					int lastStartIndex = 0;
					for (int i = 0; i < words.length; i++) {
						int startIndex = words[i].getStartIndex();
						int endIndex = words[i].getEndIndex();
						// copy the previous text into the new text node
						String prevText = textNode.substring(lastStartIndex,startIndex);
						newTextNode.append(prevText);
						lastStartIndex  = endIndex;
						
						String word = textNode.substring(startIndex, endIndex);
						if (word.length() > 0) {
							String[] alternatives = spellChecker.checkWord(word);
							// is it badly spelt
							if (alternatives != null) {

								StringBuffer specialSpanText = new StringBuffer();
								String spellId = elementId + '|' + spellCount;
								spellCount++;
								specialSpanText.append("<span class=\"epspell\" id=\"");
								specialSpanText.append(spellId);
								specialSpanText.append("\">");
								specialSpanText.append(word);
								specialSpanText.append("</span>");
								
								newTextNode.append(specialSpanText);
							
								//
								// create spelling entry
								StringBuffer spellings = new StringBuffer();
								if (alternatives.length == 0) {
									spellings.append(words);
								} else {
									for (int j = 0; j < alternatives.length; j++) {
										if (j > 0) {
											spellings.append("##");
										}
										spellings.append(alternatives[j]);
									}
								}

								Element spellingItem = rc.getServerMessage().getDocument().createElement("spelling");
								directiveItem.appendChild(spellingItem);
								spellingItem.setAttribute("spellId",spellId);
								spellingItem.setAttribute("spellings",spellings.toString());
							} else {
								// just append the word
								newTextNode.append(word);
							}
						}
					}
					// do we have any residual text
					if (lastStartIndex < textNode.length()) {
						String endText = textNode.substring(lastStartIndex,textNode.length());
						newTextNode.append(endText);
					}
					return newTextNode;
				}
			});
			if (!newHtmlText.equals(htmlText)) {
				return newHtmlText;
			} else {
				return htmlText;
			}
		} else {
			// no spell check in operation so return the text.
			return htmlText;
		}
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc, update, component);
		createDisposeDirective(rc.getServerMessage(), ContainerInstance.getElementId(component));
		// because we have "reparented" the floating ColorChooser divs, we need
		// to remove them
		String boxId = ContainerInstance.getElementId(component) + "CC";
		DomUpdate.renderElementRemove(rc.getServerMessage(), boxId);
		
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(RICH_TEXT_SERVICE.getId());
		rc.getServerMessage().addLibrary(ColorChooserPeer.CC_SERVICE.getId());
		
	}
	
	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointContainerPeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		boolean fullReplace = false;
		if (update.hasUpdatedProperties()) {
			if (partialUpdateManager.canProcess(rc, update)) {
				partialUpdateManager.process(rc, update);
			} else {
				fullReplace = true;
			}
		}
		// because we have "reparented" floating Box divs, we need to remove
		// them as well as ourself from the DOM tree.
		if (fullReplace) {
			// Perform full update.
			String elementId = ContainerInstance.getElementId(update.getParent());
			DomUpdate.renderElementRemove(rc.getServerMessage(), elementId);
			renderAdd(rc, update, targetId, update.getParent());
		}
		return false;
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		String elementId = rc.getElementId();
		RichTextArea rta = (RichTextArea) component;
		RichTextRenderer rtaRenderer = (RichTextRenderer) rc.getRP(RichTextArea.PROPERTY_RENDERER, fallbackStyle);

		ApplicationInstance app = ApplicationInstance.getActive();
		ContainerContext containerContext = (ContainerContext) app.getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
		ClientProperties clientProperties = containerContext.getClientProperties();
		String userAgent = clientProperties.getString(ClientProperties.NAVIGATOR_USER_AGENT);
		////////////////////////////////////////////////////////
		// script support
		////////////////////////////////////////////////////////
		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(RICH_TEXT_SERVICE);
		rc.addLibrary(ColorChooserPeer.CC_SERVICE);
		

		////////////////////////////////////////////////////////
		// styles
		////////////////////////////////////////////////////////
		CssStyleEx style;

		int toolbarAligment = rc.getRP(RichTextArea.PROPERTY_TOOLBAR_ALIGNMENT, fallbackStyle, Alignment.TOP);
		String ifwidth = "100%";
		String ifheight = "100%";

		style = new CssStyleEx();
		style.setAttribute("height", ifheight);
		style.setAttribute("width", ifwidth);
		Render.asColors(style, rta, RichTextArea.PROPERTY_EDITOR_BACKGROUND, RichTextArea.PROPERTY_EDITOR_BACKGROUND, fallbackStyle);
		Render.asFont(style, rta, RichTextArea.PROPERTY_EDITOR_FONT, fallbackStyle);
		Render.asBorder(style, rta, RichTextArea.PROPERTY_EDITOR_BORDER, fallbackStyle);
		String styleIFRAME = style.renderInline();

		style = new CssStyleEx();
		style.setBackground((Color) rc.getRP(RichTextArea.PROPERTY_TOOLBAR_BACKGROUND, fallbackStyle));
		style.setAttribute("text-align", "left");
		String styleToolbarContainer = style.renderInline();

		style = new CssStyleEx();
		style.setAttribute("padding-right", "2px");
		style.setAttribute("vertical-align", "top");
		String rtatbCell = style.renderInline();

		////////////////////////////////////////////////////////
		// iframe for RTA editing
		////////////////////////////////////////////////////////
		Element iframe = rc.createE("iframe");
		iframe.setAttribute("style", styleIFRAME);
		iframe.setAttribute("id", elementId + "IFrame");
		iframe.setAttribute("src", "about:blank");
		//		iframe.setAttribute("frameborder", "0");
		//		iframe.setAttribute("hspace", "1");
		//		iframe.setAttribute("vspace", "0");

		if (!rc.getRP(RichTextArea.PROPERTY_EDITABLE, fallbackStyle, true)) {
			iframe.setAttribute("readonly", "readonly");
		}
		if (!rta.isRenderEnabled()) {
			iframe.setAttribute("disabled", "disabled");
		}

		Element iframeContainer = iframe;
		//iframeContainer.appendChild(iframe);

		////////////////////////////////////////////////////////
		// Toolbar
		////////////////////////////////////////////////////////
		HtmlTable tableToolbarContainer = new HtmlTable(rc.getDocument(), 0, 0, 0);
		tableToolbarContainer.setAttribute("style", styleToolbarContainer);
		Element td = tableToolbarContainer.getTD();
		int cellCount = 0;
		////////////////////////////////////////////////////////
		// Select fields (in their own table)
		////////////////////////////////////////////////////////
		HtmlTable tableSelectFields = new HtmlTable(rc.getDocument(), 0, 0, 0);
		String[][] paragraphStyles = rtaRenderer.getParagraphStyles(rta, userAgent);
		String[][] fontNames = rtaRenderer.getFontNames(rta, userAgent);
		String[][] fontSizes = rtaRenderer.getFontSizes(rta, userAgent);
		if (paragraphStyles != null || fontNames != null || fontSizes != null) {
			if (paragraphStyles != null) {
				tableSelectFields.getTD().setAttribute("style", rtatbCell);
				tableSelectFields.getTD().appendChild(renderSelect(rc, elementId, "formatblock", "Paragraph Style", paragraphStyles));
				cellCount++;
			}

			if (fontNames != null) {
				if (cellCount > 0)
					tableSelectFields.newTD();
				tableSelectFields.getTD().setAttribute("style", rtatbCell);
				tableSelectFields.getTD().appendChild(renderSelect(rc, elementId, "fontname", "Font Name", fontNames));
				cellCount++;
			}

			if (fontSizes != null) {
				if (cellCount > 0)
					tableSelectFields.newTD();
				tableSelectFields.getTD().setAttribute("style", rtatbCell);
				tableSelectFields.getTD().appendChild(renderSelect(rc, elementId, "fontsize", "Font Size", fontSizes));
				cellCount++;
			}
			td.appendChild(tableSelectFields.getTABLE());
			if (toolbarAligment == Alignment.BOTTOM) {
				td.setAttribute("style", "padding-top:2px;padding-bottom:2px;");
			} else {
				td.setAttribute("style", "padding-bottom:2px;");
			}
		}

		////////////////////////////////////////////////////////
		// Buttons
		////////////////////////////////////////////////////////
		renderAllButtons(rc, tableToolbarContainer, rta, elementId, rtatbCell, userAgent);

		////////////////////////////////////////////////////////
		// Editor Table (toolbar + iframe)
		////////////////////////////////////////////////////////
		HtmlTable tableEditor = new HtmlTable(rc.getDocument(), 0, 0, 0);
		tableEditor.setAttribute("width", "100%");
		tableEditor.setAttribute("height", "100%");
		//
		// finally align the toolbar above or below the iframe
		Element tdIframe;
		Element tdToolbar;
		if (toolbarAligment == Alignment.BOTTOM) {
			tdIframe = tableEditor.getTD();
			tdToolbar = tableEditor.newTR();
		} else {
			tdToolbar = tableEditor.getTD();
			tdIframe = tableEditor.newTR();
		}
		tdToolbar.appendChild(tableToolbarContainer.getTABLE());
		tdIframe.appendChild(iframeContainer);
		//tdIframe.setAttribute("height", "100%");
		tdIframe.setAttribute("style","height:100%");

		////////////////////////////////////////////////////////
		// Outer Most Container
		////////////////////////////////////////////////////////
		style = new CssStyleEx(rta,fallbackStyle);
		Render.asFillImage(style, rta, RichTextArea.PROPERTY_BACKGROUND_IMAGE, fallbackStyle, rc);
		if (! rta.isRenderEnabled()) {
			Render.asColors(style,rta,RichTextArea.PROPERTY_DISABLED_BACKGROUND,ButtonEx.PROPERTY_DISABLED_FOREGROUND, fallbackStyle);
			Render.asFont(style,rta,RichTextArea.PROPERTY_DISABLED_FONT, fallbackStyle);
			Render.asBorder(style,rta,RichTextArea.PROPERTY_DISABLED_BORDER, fallbackStyle);
			Render.asFillImage(style,rta,RichTextArea.PROPERTY_DISABLED_BACKGROUND_IMAGE, fallbackStyle,rc);
		}

		Element divOuter = rc.createE("div");
		divOuter.setAttribute("id", elementId);
		divOuter.setAttribute("style", style.renderInline());
		rc.addStandardWebSupport(divOuter);
		
		divOuter.appendChild(tableEditor.getTABLE());
		parent.appendChild(divOuter);

		//renderColorChooser(rc, rta, parent, userAgent);

		createInitDirective(rc, rta, userAgent);
	}

	private Element renderAButton(RenderingContext rc, String rtaId, String cmd, ImageReference imageReference, String tooltip) {
		Element imgE = ImageManager.createImgE(rc,imageReference);
		imgE.setAttribute("id", rtaId + "_" + cmd);
		imgE.setAttribute("title", tooltip);
		return imgE;
	}

	/*
	 * Renders all the supported button commands
	 */
	private void renderAllButtons(RenderingContext rc, HtmlTable tableOut, RichTextArea rta, String rtaId, String rtatbCell, String userAgent) {
		RichTextRenderer rtaRenderer = rta.getRenderer();

		HtmlTable tableIn = new HtmlTable(rc.getDocument());
		tableOut.getTD().appendChild(tableIn.getTABLE());

		Element td = null;
		int cellCount = 0;

		String[][] commands = rtaRenderer.getSupportedCommands(rta, userAgent);
		for (int i = 0; i < commands.length; i++) {
			String cmd = commands[i][0];
			String toolTip = commands[i][1];

			//
			// set up images
			ImageReference image = rtaRenderer.getCommandImage(rta, userAgent, cmd);

			if (cmd == RichTextRenderer.CMD_HINT_NEWLINE) {
				tableIn = new HtmlTable(rc.getDocument());
				tableOut.newTR();
				tableOut.getTD().appendChild(tableIn.getTABLE());
				cellCount = 0;
				continue;
			} else if (cmd == RichTextRenderer.CMD_HINT_SPACER) {
				td = tableIn.newTD();
				td.appendChild(LayoutStrut.createStrut(rc, 5,1));
				continue;
			}
			if (cellCount == 0)
				td = tableIn.getTD();
			else
				td = tableIn.newTD();

			td.setAttribute("style", rtatbCell);
			td.setAttribute("align", "left");
			td.appendChild(renderAButton(rc, rtaId, cmd, image, toolTip));
			cellCount++;
		}
	}

	private boolean isCommandSupported(String command, RichTextArea rta, RichTextRenderer rtaRenderer, String userAgent) {
		String commands[][] = rtaRenderer.getSupportedCommands(rta, userAgent);
		if (commands != null) {
			for (int i = 0; i < commands.length; i++) {
				if (commands[i][0].equals(command))
					return true;
			}
		}
		return false;
	}

	/*
	 * Renders a Select Field with the given values
	 */
	private Element renderSelect(RenderingContext rc, String rtaId, String cmd, String tooltip, String[][] valuesAndNames) {
		Element select = rc.createE("select");
		Element option;
		select.setAttribute("id", rtaId + "_" + cmd);
		select.setAttribute("title", tooltip);
		for (int i = 0; i < valuesAndNames.length; i++) {
			option = rc.createE("option");
			select.appendChild(option);
			option.setAttribute("value", valuesAndNames[i][0]);
			option.appendChild(rc.createText(valuesAndNames[i][1]));
		}
		//
		// add our intdeterminate values in
		option = rc.createE("option");
		select.appendChild(option);
		option.setAttribute("value", "");
		option.appendChild(rc.createText("   "));
		return select;
	}

	/**
	 * Strips new lines from the string writer and quotes any quoteChar
	 * characters for use in JavaScript
	 */
	public static String removeNewLinesAndJSQuote(java.io.StringWriter sw, char quoteChar) {
		return removeNewLinesAndJSQuote(sw.toString(), quoteChar);
	}

	/**
	 * Strips new lines from the string writer and quotes any quoteChar
	 * characters for use in JavaScript
	 */
	public static String removeNewLinesAndJSQuote(String s, char quoteChar) {
		if (s == null)
			return null;

		StringBuffer sbTemp = new StringBuffer(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == quoteChar) {
				sbTemp.append('\\');
				sbTemp.append(quoteChar);
			} else {
				if (c != '\r' && c != '\n')
					sbTemp.append(c);
			}
		}
		return sbTemp.toString();
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * dispose the state of a RichTextItem, performing tasks such as
	 * deregistering event listeners on the client and deleting its JS object.
	 */
	protected void createDisposeDirective(ServerMessage serverMessage, String elementId) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPRTA.MessageProcessor", "dispose",
				new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", elementId);
		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * initialize the state of a RichTextItem, performing tasks such as
	 * registering event listeners on the client and creating the JS object.
	 */
	protected void createInitDirective(RenderingContext rc, RichTextArea rta, String userAgent) {
		RichTextRenderer rtaRenderer = rta.getRenderer();
		RichTextRenderer.CommandAppearance appearance = rtaRenderer.getCommandAppearance(rta, userAgent);
		
		CssStyleEx style;

		style = new CssStyleEx();
		style.setAttribute("left", "0");
		style.setAttribute("top", "0");
		style.setAttribute("position", "relative");
		if (appearance != null) {
			Render.asBorder(style, appearance.getBorder());
			Render.asColor(style, appearance.getBackground(), "background-color");
		} else {
			style.setAttribute("background-color", "#efefef");
			style.setAttribute("border-bottom", "buttonface solid 1px");
			style.setAttribute("border-left", "buttonface solid 1px");
			style.setAttribute("border-right", "buttonface solid 1px");
			style.setAttribute("border-top", "buttonface solid 1px");
		}
		String upItemStyle = style.renderInline();

		style = new CssStyleEx();
		style.setAttribute("left", "0");
		style.setAttribute("top", "0");
		style.setAttribute("position", "relative");
		if (appearance != null) {
			Render.asBorder(style, appearance.getSelectedBorder());
			Render.asColor(style, appearance.getSelectedBackground(), "background-color");
		} else {
			style.setAttribute("background-color", "buttonface");
			style.setAttribute("border-bottom", "buttonhighlight solid 1px");
			style.setAttribute("border-left", "buttonshadow    solid 1px");
			style.setAttribute("border-right", "buttonhighlight solid 1px");
			style.setAttribute("border-top", "buttonshadow    solid 1px");
		}
		String downItemStyle = style.renderInline();

		style = new CssStyleEx();
		style.setAttribute("position", "relative");
		style.setAttribute("left", "0");
		style.setAttribute("top", "0");
		if (appearance != null) {
			Render.asBorder(style, appearance.getRolloverBorder());
			Render.asColor(style, appearance.getRolloverBackground(), "background-color");
		} else {
			style.setAttribute("background-color", "#efefef");
			style.setAttribute("border-bottom", "buttonshadow    solid 1px");
			style.setAttribute("border-left", "buttonhighlight solid 1px");
			style.setAttribute("border-right", "buttonshadow    solid 1px");
			style.setAttribute("border-top", "buttonhighlight solid 1px");
		}
		String upMouseOverStyle = style.renderInline();

		style = new CssStyleEx();
		style.setAttribute("position", "relative");
		style.setAttribute("left", "0");
		style.setAttribute("top", "0");
		if (appearance != null) {
			Render.asBorder(style, appearance.getRolloverBorder());
			Render.asColor(style, appearance.getRolloverBackground(), "background-color");
		} else {
			style.setAttribute("background-color", "#efefef");
			style.setAttribute("border-bottom", "buttonhighlight solid 1px");
			style.setAttribute("border-left", "buttonshadow    solid 1px");
			style.setAttribute("border-right", "buttonhighlight solid 1px");
			style.setAttribute("border-top", "buttonshadow    solid 1px");
		}
		String downMouseOverStyle = style.renderInline();

		style = new CssStyleEx();
		style.setAttribute("font-size", "10pt");
		String selectStyle = style.renderInline();

		//--------------------------------------------------
		// directive creation
		//--------------------------------------------------
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPRTA.MessageProcessor",
				"init", new String[0], new String[0]);

		Element itemElement = rc.createE("item");
		itemElement.setAttribute("eid", rc.getElementId());
		itemizedUpdateElement.appendChild(itemElement);


		style = new CssStyleEx();
		Render.asColors(style, rta, RichTextArea.PROPERTY_EDITOR_BACKGROUND, RichTextArea.PROPERTY_EDITOR_FOREGROUND);
		Render.asFont(style, rta, RichTextArea.PROPERTY_EDITOR_FONT);
		String cssText = "";
		cssText += "body { ";
		cssText += style.renderInline();
		cssText += "}";
		
//		cssText += ".epspell { ";
//		cssText += "	background-color : yellow;";
//		cssText += "	color : red;";
//		cssText += "	text-decoration : line-though;";
//		cssText += "}";

		String wigglyUrl = ImageManager.getURI(rc,IMAGE_WIGGLY_RED_LINE);
		
		cssText += ".epspell { ";
		cssText += "	background : url(" + wigglyUrl + ")  repeat-x bottom";
		cssText += "}";
		
				
		
		
		cssText += ".epspellplain { ";
		cssText += "}";
		
		cssText += ".epspellbox {";
		cssText += "	background-color : #C6DBFF;"; /* #C6DBFF #EFEFDE */
		cssText += "	border-color : EFEFDE; ;"; /* #9CAECE */
		cssText += "	border-width : 2px;";
		cssText += "	border-style : outset;";
		cssText += "	padding : 2px;";
		cssText += "}";

		cssText += ".epspelloption {";
		cssText += "	color : #000000;";
		cssText += "	background-color : #C6DBFF;";
		cssText += "	padding-left : 5px;";
		//cssText += "	width : 100%;";
		cssText += "}";

		cssText += ".epspelloptionhilight {";
		cssText += "	color : #ffffff;";
		cssText += "	background-color : #5271CE;";
		cssText += "	padding-left : 5px;";
		//cssText += "	width : 100%;";
		cssText += "}";

		boolean spellCheckInProgress = ((Boolean) rta.getRenderProperty(RichTextArea.PROPERTY_SPELL_CHECK_IN_PROGRESS)).booleanValue();
		
		String htmlText = rta.getText();
		htmlText = htmlText == null ? "" : htmlText;
		htmlText = spellCheckText(rc,itemElement,rta,htmlText,spellCheckInProgress);
		//
		// build our RTA document which goes inside the editing iframe
		StringBuffer htmlDocument = new StringBuffer();
		htmlDocument.append("<html>");
		htmlDocument.append("<head>");
		htmlDocument.append("<style>" + cssText + "</style>");
		htmlDocument.append("</head>");
		htmlDocument.append("<body>");
		htmlDocument.append(htmlText);
		htmlDocument.append("</body>");
		htmlDocument.append("</html>");

		itemElement.setAttribute("htmlDocument", htmlDocument.toString());
		itemElement.setAttribute("initialText", htmlText);
		itemElement.setAttribute("spellCheckInProgress", String.valueOf(spellCheckInProgress));
		
		//
		// look and feel objects
		Element landfItem;
		landfItem = rc.createE("landf");
		landfItem.setAttribute("tag", "button");
		landfItem.setAttribute("upItemStyle", upItemStyle);
		landfItem.setAttribute("downItemStyle", downItemStyle);
		landfItem.setAttribute("upMouseOverStyle", upMouseOverStyle);
		landfItem.setAttribute("downMouseOverStyle", downMouseOverStyle);
		itemElement.appendChild(landfItem);

		landfItem = rc.createE("landf");
		landfItem.setAttribute("tag", "select");
		landfItem.setAttribute("selectStyle", selectStyle);
		itemElement.appendChild(landfItem);

		//
		// commands
		if (isCommandSupported(RichTextRenderer.CMD_BOLD, rta, rtaRenderer, userAgent)) {
			createButtonCommand(rc, itemElement, "bold", true);
		}
		if (isCommandSupported(RichTextRenderer.CMD_ITALIC, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "italic", true);
		if (isCommandSupported(RichTextRenderer.CMD_UNDERLINE, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "underline", true);
		if (isCommandSupported(RichTextRenderer.CMD_SUBSCRIPT, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "subscript", true);
		if (isCommandSupported(RichTextRenderer.CMD_SUPERSCRIPT, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "superscript", true);
		if (isCommandSupported(RichTextRenderer.CMD_REMOVEFORMAT, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "removeformat", true);

		if (isCommandSupported(RichTextRenderer.CMD_FORECOLOR, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "forecolor", false);
		if (isCommandSupported(RichTextRenderer.CMD_BACKCOLOR, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "hilitecolor", false);

		if (isCommandSupported(RichTextRenderer.CMD_ALIGN_LEFT, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "justifyleft", true);
		if (isCommandSupported(RichTextRenderer.CMD_ALIGN_CENTER, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "justifycenter", true);
		if (isCommandSupported(RichTextRenderer.CMD_ALIGN_RIGHT, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "justifyright", true);
		if (isCommandSupported(RichTextRenderer.CMD_JUSTIFY, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "justifyfull", true);

		if (isCommandSupported(RichTextRenderer.CMD_INDENT, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "indent", false);
		if (isCommandSupported(RichTextRenderer.CMD_OUTDENT, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "outdent", false);

		if (isCommandSupported(RichTextRenderer.CMD_NUMBERS, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "insertorderedlist", true);
		if (isCommandSupported(RichTextRenderer.CMD_BULLETS, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "insertunorderedlist", true);

		if (isCommandSupported(RichTextRenderer.CMD_COPY, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "copy", false);
		if (isCommandSupported(RichTextRenderer.CMD_CUT, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "cut", false);
		if (isCommandSupported(RichTextRenderer.CMD_PASTE, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "paste", false);
		if (isCommandSupported(RichTextRenderer.CMD_SELECTALL, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "selectall", false);

		if (isCommandSupported(RichTextRenderer.CMD_UNDO, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "undo", false);
		if (isCommandSupported(RichTextRenderer.CMD_REDO, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "redo", false);
		
		if (isCommandSupported(RichTextRenderer.CMD_SPELLCHECK, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "spellcheck", false);

		if (isCommandSupported(RichTextRenderer.CMD_INSERTHR, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "inserthorizontalrule", false);
		
		if (isCommandSupported(RichTextRenderer.CMD_CREATELINK, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "createlink", false);
		if (isCommandSupported(RichTextRenderer.CMD_INSERTIMAGE, rta, rtaRenderer, userAgent))
			createButtonCommand(rc, itemElement, "insertimage", false);
		//if (isCommandSupported(rc,rta,RichTextRenderer.CMD_INSERTTABLE))
		//createCommandE(rc,itemElement,"inserttable",false);

		if (rtaRenderer.getFontNames(rta, userAgent) != null)
			createSelectCommand(rc, itemElement, "fontname");
		if (rtaRenderer.getFontSizes(rta, userAgent) != null)
			createSelectCommand(rc, itemElement, "fontsize");
		if (rtaRenderer.getParagraphStyles(rta, userAgent) != null)
			createSelectCommand(rc, itemElement, "formatblock");
	}

	private void createButtonCommand(RenderingContext rc, Element parentE, String cmd, boolean isStateful) {
		createCommandImpl(rc, parentE, "button", cmd, isStateful);
	}

	private void createSelectCommand(RenderingContext rc, Element parentE, String cmd) {
		createCommandImpl(rc, parentE, "select", cmd, false);
	}

	private void createCommandImpl(RenderingContext rc, Element parentE, String tag, String cmd, boolean isStateful) {
		Element cmdE = rc.createE("command");
		cmdE.setAttribute("tag", tag);
		cmdE.setAttribute("cmd", cmd);
		cmdE.setAttribute("isStateful", String.valueOf(isStateful));
		parentE.appendChild(cmdE);
	}
	
}
