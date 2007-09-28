package echopointng.ui.util;

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

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.RenderState;
import nextapp.echo2.webrender.Connection;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.servermessage.DomPropertyStore;
import nextapp.echo2.webrender.servermessage.EventProcessor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import echopointng.ComponentEx;
import echopointng.able.AccessKeyable;
import echopointng.able.Attributeable;
import echopointng.able.ToolTipable;

/**
 * <code>RenderingContext</code> is useful as a wrapper inside a
 * DomUpdateSupport or SynchronizePeer implementation to hold important context
 * such as the component being rendered or the ServerComponentUpdate in use.
 * <p>
 * This allows you to have sub methods that take a RenderingContext as a
 * parameter and yet contain most of the context information you will require.
 * <p>
 * Remember that SynchronizePeer's are component static, ie. they are not meant
 * to contain state information as member variables for a given component.
 * <p>
 * This class helps during the lifetime of a given "render invocation".
 * <p>
 * It also provides a number of helper methods to make writing UI Peer easier
 * and to make the code cleaner.
 *  
 */
public class RenderingContext implements RenderContext {

	Component component;

	RenderContext rc;

	RenderState renderState;

	ServerComponentUpdate serverComponentUpdate;

	/**
	 * Constructs a <code>RenderingContext</code> that wraps the original
	 * RenderContext as well as contains references to the ServerComponentUpdate
	 * and Component being rendered.
	 * 
	 * @param rc -
	 *            the RenderContext in use at this time.
	 * @param serverComponentUpdate -
	 *            the ServerComponentUpdate in use at this time
	 * @param component -
	 *            the Component being rendered
	 */
	public RenderingContext(RenderContext rc, ServerComponentUpdate serverComponentUpdate, Component component) {
		this.rc = rc;
		this.serverComponentUpdate = serverComponentUpdate;
		this.component = component;
	}

	/**
	 * This will add "access key" attributes to an Element if the specified
	 * component in facts implements AccessKeyable.
	 * 
	 * @param component -
	 *            the Component to check for AccessKeyable
	 * @param element -
	 *            the Element to add the access key support to
	 */
	public void addAccessKeySupport(Component component, Element element) {
		if (component instanceof AccessKeyable) {
			if (! component.isRenderEnabled()) {
				return;
			}
			AccessKeyable ak = (AccessKeyable) component;
			if (ak.getRenderProperty(AccessKeyable.PROPERTY_ACCESS_KEY) instanceof String) {
				element.setAttribute("accesskey", (String) ak.getRenderProperty(AccessKeyable.PROPERTY_ACCESS_KEY));
			}
		}
	}

	/**
	 * This will add "title" attributes to an Element if the specified
	 * component in facts implements <code>ToolTipable</code>.
	 * 
	 * @param component -
	 *            the Component to check for <code>ToolTipable</code>
	 * @param element -
	 *            the Element to add the tool tip title support to
	 */
	public void addToolTipSupport(Component component, Element element) {
		if (component instanceof ToolTipable) {
			if (! component.isRenderEnabled()) {
				return;
			}
			ToolTipable tt = (ToolTipable) component;
			if (tt.getRenderProperty(ToolTipable.PROPERTY_TOOL_TIP_TEXT) instanceof String) {
				element.setAttribute("title", (String) tt.getRenderProperty(ToolTipable.PROPERTY_TOOL_TIP_TEXT));
			}
		}
	}
	
	/**
	 * This will add "access key" attributes to an Element if the specified
	 * component in facts implements AccessKeyable
	 * 
	 * @param element -
	 *            the Element to add the access key support to
	 */
	public void addAccessKeySupport(Element element) {
		addDisabledSupport(this.component, element);
	}

	/**
	 * This will add "disabled" attributes to an Element if the specified
	 * component is not enabled.
	 * 
	 * @param component -
	 *            the Component to check for enabledness
	 * @param element -
	 *            the Element to add the disabled attribute to
	 */
	public void addDisabledSupport(Component component, Element element) {
		if (!component.isRenderEnabled()) {
			element.setAttribute("disabled", "disabled");
		}
	}

	/**
	 * This will add "disabled" attributes to an Element if the current
	 * component is not enabled.
	 * 
	 * @param element -
	 *            the Element to add the disabled attribute to
	 */
	public void addDisabledSupport(Element element) {
		addDisabledSupport(this.component, element);
	}

	/**
	 * This will add "custom" attributes to an Element from the
	 * iif the component implements the <code>Attributeable</code> 
	 * interface
	 * 
	 * @param component -
	 *            the Component to check for <code>Attributeable</code> interface
	 * @param element -
	 *            the element to add the attributes to
	 */
	public void addAttributeableSupport(Component component, Element element) {
		if (component instanceof Attributeable) {
			Attributeable attributeable = (Attributeable) component;
			String attributeNames[] = attributeable.getAttributeNames();
			for (int i = 0; i < attributeNames.length; i++) {
				String attributeName = attributeNames[i];
				Object attributeValue = attributeable.getAttribute(attributeName);
				element.setAttribute(attributeName, String.valueOf(attributeValue));
			}
		}
	}
	
	/**
	 * Adds a library and caused the client to wait for it to be loaded.
	 * 
	 * @see ServerMessage#addLibrary(String)
	 */
	public void addLibrary(Service service) {
		this.rc.getServerMessage().addLibrary(service.getId());
	}

	/**
	 * This is a shortcut method for for the addXXXSupport methods
	 * 
	 * @see RenderingContext#addAccessKeySupport(Component, Element)
	 * @see RenderingContext#addDisabledSupport(Component, Element)
	 * @see RenderingContext#addTabIndexSupport(Component, Element)
	 */
	public void addStandardWebSupport(Component component, Element element) {
		addAccessKeySupport(component, element);
		addDisabledSupport(component, element);
		addTabIndexSupport(component, element);
		addToolTipSupport(component,element);
		addAttributeableSupport(component,element);
	}

	/**
	 * @see RenderingContext#addStandardWebSupport(Component, Element)
	 */
	public void addStandardWebSupport(Element element) {
		addStandardWebSupport(this.component, element);
	}

	/**
	 * This method will add tab traversal code into the specified element, if
	 * the component is in fact a focus tab participant and implements
	 * TabStop.
	 * 
	 * @param component
	 *            the component in question
	 * @param element
	 *            the element to add a a tabindex to, typically the highest
	 *            level element.
	 */
	public void addTabIndexSupport(Component component, Element element) {
		if (component.isRenderEnabled()) {
			if (component.isFocusTraversalParticipant()) {
				element.setAttribute("tabindex", Integer.toString(component.getFocusTraversalIndex()));
			} else {
				// this cause an issue on FireFox 1.5 as it introduces
				// an unwanted focus outline
				//element.setAttribute("tabindex", "-1");
			}
		}
	}

	/**
	 * This method will add tab traversal code into the specified element, if
	 * the current component is in fact a focus tab participant.
	 * 
	 * @param element
	 *            the element to add a a tabindex to, typically the highest
	 *            level element.
	 */
	public void addTabIndexSupport(Element element) {
		addTabIndexSupport(this.component, element);
	}

	/**
	 * Creates a DOM element based on the current Document in the
	 * RenderContext's server message. The element is not attached to any parent
	 * Elements after creation.
	 * 
	 * @param elementName -
	 *            the tag name of the element to create
	 * @return - the newly created DOM element.
	 */
	public Element createE(String elementName) {
		return rc.getServerMessage().getDocument().createElement(elementName);
	}

	/**
	 * Creates a <code>Text</code> node given the specified string.
	 * 
	 * @param data
	 *            The data for the node.
	 * @return The new <code>Text</code> object.
	 */
	public Text createText(String text) {
		return rc.getServerMessage().getDocument().createTextNode(text);
	}

	/**
	 * @return Returns the Component associated with this RenderContext.
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * @see nextapp.echo2.webcontainer.RenderContext#getConnection()
	 */
	public Connection getConnection() {
		return rc.getConnection();
	}

	/**
	 * @see nextapp.echo2.webcontainer.RenderContext#getContainerInstance()
	 */
	public ContainerInstance getContainerInstance() {
		return rc.getContainerInstance();
	}

	/**
	 * @return - the Document associated with this the underlying ServerMessage.
	 */
	public Document getDocument() {
		return rc.getServerMessage().getDocument();
	}

	/*
	 * ========================================================== DOM code
	 * ==========================================================
	 */

	/**
	 * Returns the magic Echo component identifier for the current component.
	 */
	public String getElementId() {
		return ContainerInstance.getElementId(component);
	}

	/**
	 * Returns the RenderState associated with this RenderingContext if there is
	 * any.
	 * 
	 * @return the RenderState associated with this RenderingContext if there is
	 *         any.
	 */
	public RenderState getRenderState() {
		return rc.getContainerInstance().getRenderState(component);
	}

	/*
	 * ========================================================== Component
	 * property code ==========================================================
	 */

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component. This is intended to make rendering code more readable through
	 * brevity.
	 */
	public Object getRP(String propertyName) {
		return ComponentEx.getRenderProperty(component, propertyName);
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public boolean getRP(String propertyName, boolean defaultValue) {
		return ComponentEx.getRenderProperty(component, propertyName, defaultValue);
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public boolean getRP(String propertyName, Style defaultStyle, boolean defaultValue) {
		Boolean value = (Boolean) component.getRenderProperty(propertyName);
		if (value == null && defaultStyle != null) {
			value = (Boolean) defaultStyle.getProperty(propertyName);
		}
		return (value == null ? defaultValue : value.booleanValue());
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public byte getRP(String propertyName, byte defaultValue) {
		return ComponentEx.getRenderProperty(component, propertyName, defaultValue);
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public char getRP(String propertyName, char defaultValue) {
		return ComponentEx.getRenderProperty(component, propertyName, defaultValue);
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public double getRP(String propertyName, double defaultValue) {
		return ComponentEx.getRenderProperty(component, propertyName, defaultValue);
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public double getRP(String propertyName, Style defaultStyle, double defaultValue) {
		Double value = (Double) component.getRenderProperty(propertyName);
		if (value == null && defaultStyle != null) {
			value = (Double) defaultStyle.getProperty(propertyName);
		}
		return (value == null ? defaultValue : value.doubleValue());
	}
	
	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public float getRP(String propertyName, float defaultValue) {
		return ComponentEx.getRenderProperty(component, propertyName, defaultValue);
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public int getRP(String propertyName, int defaultValue) {
		return ComponentEx.getRenderProperty(component, propertyName, defaultValue);
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public int getRP(String propertyName, Style defaultStyle, int defaultValue) {
		Integer value = (Integer) component.getRenderProperty(propertyName);
		if (value == null && defaultStyle != null) {
			value = (Integer) defaultStyle.getProperty(propertyName);
		}
		return (value == null ? defaultValue : value.intValue());
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public long getRP(String propertyName, long defaultValue) {
		return ComponentEx.getRenderProperty(component, propertyName, defaultValue);
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public Object getRP(String propertyName, Object defaultValue) {
		Object value = ComponentEx.getRenderProperty(component, propertyName);
		return (value != null ? value : defaultValue);
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public Object getRP(String propertyName, Style defaultStyle, Object defaultValue) {
		Object value = ComponentEx.getRenderProperty(component, propertyName);
		if (value == null && defaultStyle != null) {
			value = defaultStyle.getProperty(propertyName);
		}
		return (value != null ? value : defaultValue);
	}
	
	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public short getRP(String propertyName, short defaultValue) {
		return ComponentEx.getRenderProperty(component, propertyName, defaultValue);
	}

	/**
	 * A short hand method for getting rendered property values from the current
	 * Component, with a default if it is not set. This is intended to make
	 * rendering code more readable through brevity.
	 */
	public Object getRP(String propertyName, Style defaultStyle) {
		Object value = ComponentEx.getRenderProperty(component, propertyName);
		if (value == null && defaultStyle != null) {
			value = defaultStyle.getProperty(propertyName);
		}
		return value;
	}

	/**
	 * @return Returns the ServerComponentUpdate associated with this
	 *         RenderContext.
	 */
	public ServerComponentUpdate getServerComponentUpdate() {
		return serverComponentUpdate;
	}

	/**
	 * @see nextapp.echo2.webcontainer.RenderContext#getServerMessage()
	 */
	public ServerMessage getServerMessage() {
		return rc.getServerMessage();
	}

	/**
	 * This adds a snippet of JavaScript code into the mix via a
	 * <code>JavaScriptSnippetService </code> for the current component. If a
	 * <code>JavaScriptSnippetService </code> already exists for the current
	 * component, then it is re-used.
	 * 
	 * @param jsSnippetCode -
	 *            the javascript code to be executed
	 */
	//	public void addSnippet(String jsSnippetCode) {
	//		JavaScriptSnippetService snippet;
	//		snippet = JavaScriptSnippetService.forComponent(getContainerInstance(),
	// getComponent());
	//		if (snippet == null) {
	//			snippet = new JavaScriptSnippetService(component);
	//			rc.getContainerInstance().getServiceRegistry().add(snippet);
	//		}
	//		snippet.setContent(jsSnippetCode);
	//		addLibrary(snippet);
	//	}
	/**
	 * Creates an <code>eventadd</code> operation to register a client event
	 * listener of a particular type on an HTML element.
	 * 
	 * @param eventType
	 *            the type of event (the "on" prefix should be omitted, e.g.,
	 *            "onmousedown" would be expressed as "mousedown")
	 * @param elementId
	 *            the id of the listened-to DOM element
	 * @param eventHandler
	 *            the name of the handler method to be invoked when the event
	 *            occurs, e.g., "EchoButton.processAction"
	 * 
	 * @see EventUpdate#createEventAdd(ServerMessage, String, String, String)
	 */
	public void renderEventAdd(String eventType, String elementId, String eventHandler) {
		EventProcessor.renderEventAdd(rc.getServerMessage(), eventType, elementId, eventHandler);
	}

	/**
	 * Creates an <code>eventremove</code> operation to unregister a client
	 * event listener of a particular type on an HTML element.
	 * 
	 * @param eventType
	 *            the type of event (the "on" prefix should be omitted, e.g.,
	 *            "onmousedown" would be expressed as "mousedown")
	 * @param elementId
	 *            the id of the listened-to DOM element
	 * 
	 * @see EventUpdate#createEventRemove(ServerMessage, String, String)
	 */
	public void renderEventRemove(String eventType, String elementId) {
		EventProcessor.renderEventRemove(rc.getServerMessage(), eventType, elementId);
	}

	/**
	 * Creates a <code>storeproperty</code> operation to store a non-rendered
	 * named property in an HTMLElement of the client DOM.
	 * 
	 * @param elementId
	 *            the id of the element on which to set the non-rendered
	 *            property
	 * @param propertyName
	 *            the name of the property
	 * @param propertyValue
	 *            the value of the property
	 */
	public void renderSetProperty(String elementId, String propertyName, String propertyValue) {
		DomPropertyStore.renderSetProperty(getServerMessage(), elementId, propertyName, propertyValue);
	}

}
