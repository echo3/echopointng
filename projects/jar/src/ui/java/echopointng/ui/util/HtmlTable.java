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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * The <code>HtmlTable</code> class is a helper for creating
 * HTML TABLE elements, TBODY element, TR elements and TD elements in less 
 * code that usual.  It auto creates a TBODY and then TR and TD elements since a 
 * table cannot have content without TBODY, TR and TD elements.
 * <p>
 * It keeps a logical pointer to the current TR and TD so you can add
 * more content as you go, more easily. 
 */
public class HtmlTable  {

	private Element table;
	private Element tbody;
	private Element td;
	private Element tr;
	private Document document;

	/**
	 * Constructs a <code>HtmlTable</code> with a zero sized
	 * border, cellpadding and cellspacing.  A TR and TD are also 
	 * created.
	 *
	 * @param document - the Document to create elements in 
	 */
	public HtmlTable(Document document) {
		this(document,0, 0, 0);
	}

	/**
	 * Constructs a <code>HtmlTable</code> with a zero sized
	 * border, cellpadding and cellspacing and may optionally
	 * create TR and TD combination.
	 *
	 * @param document - the Document to create elements in 
	 * @param autoAddTRTD - wheter a child Tr and TD should be added to the Table
	 */
	public HtmlTable(Document document, boolean autoAddTRTD) {
		this(document,autoAddTRTD,0,0,0);
	}
	
	/**
	 * Constructs a <code>HtmlTable</code> with a zero sized
	 * border, and the specified cellpadding and cellspacing.  
	 * A TR and TD are also created.
	 * 
	 * @param document - the Document to create elements in 
	 * @param cellspacing - the cellspacing of the table
	 * @param cellpadding - the cellpadding of the table
	 */
	public HtmlTable(Document document, int cellspacing, int cellpadding) {
		this(document,0, cellspacing, cellpadding);
	}

	/**
	 * Constructs a <code>HtmlTable</code> with the specified
	 * border, cellpadding and cellspacing.  A TR and TD are also 
	 * created.
	 * 
	 * @param document - the Document to create elements in 
	 * @param border - the border of the table
	 * @param cellspacing - the cellspacing of the table
	 * @param cellpadding - the cellpadding of the table
	 */
	public HtmlTable(Document document, int border, int cellspacing, int cellpadding) {
		this(document,true,border,cellspacing,cellpadding);
	}
	
	/**
	 * Constructs a <code>HtmlTable</code> with the specified
	 * border, cellpadding and cellspacing.  A TR and TD are also 
	 * created if the autoAddTRTD is true.
	 * @param document - the Document to create elements in 
	 * @param autoAddTRTD - wheter a child Tr and TD should be added to the Table
	 * @param border - the border of the table
	 * @param cellspacing - the cellspacing of the table
	 * @param cellpadding - the cellpadding of the table
	 */
	public HtmlTable(Document document, boolean autoAddTRTD, int border, int cellspacing, int cellpadding) {
		
		this.document = document;
		table = document.createElement("table");
		setAttribute("border", border);
		setAttribute("cellspacing", cellspacing);
		setAttribute("cellpadding", cellpadding);
		
		tbody = document.createElement("tbody");
		table.appendChild(this.tbody);

		if (autoAddTRTD) {
			tr = document.createElement("tr");
			this.tbody.appendChild(tr);
	
			td = document.createElement("td");
			tr.appendChild(td);
		}
	}
	
	/** 
	 * @return the TABLE element
	 */
	public Element getTABLE() {
		return table;
	}

	/** 
	 * @return the TBODY element
	 */
	public Element getTBODY() {
		return tbody;
	}
	
	/** 
	 * @return the current TD element
	 */
	public Element getTD() {
		return td;
	}

	/** 
	 * @return the current TR element
	 */
	public Element getTR() {
		return tr;
	}
	
	/**
	 * Adds a new TD element to the current TR and returns the
	 * new TD element. The current TD becomes this new
	 * TD element.  
	 *  
	 * @return a new TD element
	 */
	public Element newTD() {
		Element newTD = document.createElement("td");
		if (tr == null) {
			tr = newTR(false);
		}
		tr.appendChild(newTD);
		td = newTD;
		return newTD;
	}

	/**
	 * Adds a new TR element to the TABLE and returns 
	 * the new TR element.  It also adds a new TD element
	 * to this TR.  The current TR becomes this new
	 * TR element.  
	 *   
	 * @return the new child TD element created under the TR
	 */
	public Element newTR() {
		return newTR(true);
	}

	/**
	 * Adds a new TR element to the TABLE and returns 
	 * the new TR element.  It may also create a new TD element
	 * to this TR if autoCreateTD is true.  The current TR becomes this new
	 * TR element.  
	 * 
	 * @param autoCreateTD - whether to automatically create a child TD
	 * for the TR.
	 *   
	 * @return a new child TD element created under the TR or the TR if a TD is not 
	 * create.
	 */
	private Element newTR(boolean autoCreateTD) {
		Element newTR = document.createElement("tr");		
		this.tbody.appendChild(newTR);
		tr = newTR;
		if (autoCreateTD)
			return newTD();
		else
			return newTR;
	}

	/**
	 * Adds a new TR element to the TABLE and returns 
	 * the new TR element.  It DOES NOT create a new TD element
	 * under the TR.  The current TR becomes this new TR element.  
	 * 
	 * @return a new child TE element 
	 */
	public Element newTRnoTD() {
		return newTR(false);
	}
	
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
	
	public Node appendChild(Node newChild) throws DOMException {
		return table.appendChild(newChild);
	}
	public Node cloneNode(boolean deep) {
		return table.cloneNode(deep);
	}
	public boolean equals(Object obj) {
		return table.equals(obj);
	}
	public String getAttribute(String name) {
		return table.getAttribute(name);
	}
	public Attr getAttributeNode(String name) {
		return table.getAttributeNode(name);
	}
	public Attr getAttributeNodeNS(String namespaceURI, String localName) {
		return table.getAttributeNodeNS(namespaceURI, localName);
	}
	public String getAttributeNS(String namespaceURI, String localName) {
		return table.getAttributeNS(namespaceURI, localName);
	}
	public NamedNodeMap getAttributes() {
		return table.getAttributes();
	}
	public NodeList getChildNodes() {
		return table.getChildNodes();
	}
	public NodeList getElementsByTagName(String name) {
		return table.getElementsByTagName(name);
	}
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
		return table.getElementsByTagNameNS(namespaceURI, localName);
	}
	public Node getFirstChild() {
		return table.getFirstChild();
	}
	public Node getLastChild() {
		return table.getLastChild();
	}
	public String getLocalName() {
		return table.getLocalName();
	}
	public String getNamespaceURI() {
		return table.getNamespaceURI();
	}
	public Node getNextSibling() {
		return table.getNextSibling();
	}
	public String getNodeName() {
		return table.getNodeName();
	}
	public short getNodeType() {
		return table.getNodeType();
	}
	public String getNodeValue() throws DOMException {
		return table.getNodeValue();
	}
	public Document getOwnerDocument() {
		return table.getOwnerDocument();
	}
	public Node getParentNode() {
		return table.getParentNode();
	}
	public String getPrefix() {
		return table.getPrefix();
	}
	public Node getPreviousSibling() {
		return table.getPreviousSibling();
	}
	public String getTagName() {
		return table.getTagName();
	}
	public boolean hasAttribute(String name) {
		return table.hasAttribute(name);
	}
	public boolean hasAttributeNS(String namespaceURI, String localName) {
		return table.hasAttributeNS(namespaceURI, localName);
	}
	public boolean hasAttributes() {
		return table.hasAttributes();
	}
	public boolean hasChildNodes() {
		return table.hasChildNodes();
	}
	public int hashCode() {
		return table.hashCode();
	}
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return table.insertBefore(newChild, refChild);
	}
	public boolean isSupported(String feature, String version) {
		return table.isSupported(feature, version);
	}
	public void normalize() {
		table.normalize();
	}
	public void removeAttribute(String name) throws DOMException {
		table.removeAttribute(name);
	}
	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		return table.removeAttributeNode(oldAttr);
	}
	public void removeAttributeNS(String namespaceURI, String localName)
			throws DOMException {
		table.removeAttributeNS(namespaceURI, localName);
	}
	public Node removeChild(Node oldChild) throws DOMException {
		return table.removeChild(oldChild);
	}
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return table.replaceChild(newChild, oldChild);
	}
	public void setAttribute(String name, String value) throws DOMException {
		table.setAttribute(name, value);
	}
	public void setAttribute(String name, int value) throws DOMException {
		table.setAttribute(name, String.valueOf(value));
	}
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		return table.setAttributeNode(newAttr);
	}
	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		return table.setAttributeNodeNS(newAttr);
	}
	public void setAttributeNS(String namespaceURI, String qualifiedName,
			String value) throws DOMException {
		table.setAttributeNS(namespaceURI, qualifiedName, value);
	}
	public void setNodeValue(String nodeValue) throws DOMException {
		table.setNodeValue(nodeValue);
	}
	public void setPrefix(String prefix) throws DOMException {
		table.setPrefix(prefix);
	}
	public String toString() {
		return table.toString();
	}
}