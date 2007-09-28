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
package echopointng.xhtml;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nextapp.echo2.app.LayoutData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <code>XhtmlFragment</code> is used to represent just that, a fragment of
 * XHTML that can be used inside the Echo2 framework. <code>LayoutData</code>
 * can also be associated with this fragment of XHTML and is used to indicate
 * the layout of this XHTML fragment to its parent container.
 * <p>
 * No validation is performed on this XHTML and hence care must be taken to
 * ensure that the XHTML is valid before use. However remember that the XML
 * fragment is placed inside the context of a parent XHTML element and hence "no
 * top" level element may be required (in other words the fragment need not be
 * XML valid to be used in some cases).
 * 
 * @see nextapp.echo2.app.LayoutData
 */
public class XhtmlFragment implements Serializable {

	/**
	 * ThreadLocal cache of validating <code>DocumentBuilder</code> instances.
	 */
	private static final ThreadLocal nonvalidatingDocumentBuilders = new ThreadLocal() {

		/**
		 * @see java.lang.ThreadLocal#initialValue()
		 */
		protected Object initialValue() {
			return buildDocumentBuilder();
		}
	};

	/**
	 * Called to return a non validating <code>DocumentBuilder</code>
	 */
	private static final DocumentBuilder buildDocumentBuilder() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);
			factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.valueOf(false));
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder;
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Retrieves a thread-specific non validating <code>DocumentBuilder</code>.
	 * 
	 * @return the <code>DocumentBuilder</code> serving the current thread.
	 */
	private static DocumentBuilder getNonValidatingDocumentBuilder() {
		return (DocumentBuilder) nonvalidatingDocumentBuilders.get();
	}

	/**
	 * This can be called to findout if a fragment is valid XML. This is a
	 * costly operation and hence should be used with some design caution. If
	 * the fragment is valid then a null will be rturned, otherwise an Exception
	 * will be returned
	 * 
	 * @param fragment -
	 *            the XML fragment to parse
	 * @return null if its a valid XML fragment or a <code>Exception</code> if
	 *         its ont. These may be a SaxException, a
	 *         <code>ParserConfigurationException</code> or a
	 *         <code>IOException</code>.
	 * 
	 * @see SAXException
	 * @see IOException
	 */
	public static Exception isValidXML(String fragment) {
		try {
			parseFragment(fragment);
			return null;
		} catch (SAXException e) {
			// A parsing error occurred; the xml input is not valid
			return e;
		} catch (IOException e) {
			return e;
		}
	}

	private static Node[] parseFragment(String fragment) throws SAXException, IOException {
		String xhtml = (fragment == null ? "" : fragment);
		// surround with a parent element to make it valid
		xhtml = "<div>" + xhtml + "</div>";
		StringReader sr = new StringReader(xhtml);
		InputSource inputSource = new InputSource(sr);

		Document document = getNonValidatingDocumentBuilder().parse(inputSource);
		Element documentElement = document.getDocumentElement();

		NodeList nodeList = documentElement.getChildNodes();
		Node children[] = new Node[nodeList.getLength()];
		for (int i = 0, l = nodeList.getLength(); i < l; i++) {
			Node child = nodeList.item(i);
			children[i] = child;
		}
		return children;
	}

	private String fragment;

	private LayoutData layoutData;

	/**
	 * Constructs a <code>XhtmlFragment</code> with a null XHTML fragment.
	 */
	public XhtmlFragment() {
		this(null);
	}

	/**
	 * Constructs a <code>XhtmlFragment</code> with the specified fragment of
	 * XHTML.
	 * 
	 * @param fragment -
	 *            the XHTML fragment to use
	 */
	public XhtmlFragment(String fragment) {
		this(fragment, null);
	}

	/**
	 * Constructs a <code>XhtmlFragment</code> with the specified fragment of
	 * XHTML.
	 * 
	 * @param fragment -
	 *            the XHTML fragment to use
	 * @param layoutData -
	 *            the LayoutData to associate with this fragment.
	 */
	public XhtmlFragment(String fragment, LayoutData layoutData) {
		setFragment(fragment);
		this.layoutData = layoutData;
	}

	/**
	 * @return Returns the fragment of XHTML.
	 */
	public String getFragment() {
		return fragment;
	}

	/**
	 * Returns the <code>LayoutData</code> object used to describe how this
	 * <code>XhtmFragment</code> should be laid out within its parent
	 * container.
	 * 
	 * @return the layout data, or null if unset
	 * @see LayoutData
	 */
	public LayoutData getLayoutData() {
		return layoutData;
	}

	/**
	 * Sets the fragment of XHTML. Care must be taken to ensure that it is a
	 * valid peice of XHTML. By default his is not checked because it can be very
	 * costly to "compile" the XHTML to ensure its valid.
	 * 
	 * @param fragment
	 *            The fragment of XHTML to set.
	 */
	public void setFragment(String fragment) {
		this.fragment = fragment == null ? "" : fragment;
	}

	/**
	 * Sets the <code>LayoutData</code> of this <code>XhtmlFragment</code>.
	 * A <code>LayoutData</code> implementation describes how this
	 * <code>XhtmlFragment</code> is laid out within/interacts with its parent
	 * container.
	 * 
	 * @param layoutData
	 *            the new <code>LayoutData</code>
	 * @see LayoutData
	 */
	public void setLayoutData(LayoutData layoutData) {
		this.layoutData = layoutData;
	}

	/**
	 * This returns the XhtmlFragment as a series of DOM <code>Node</code>s.
	 * Note that because a XML parser is invoked here, an exception could be
	 * thrown, especially if the XhtmlFragment does not contain valud XML.
	 * <p>
	 * The Nodes are "imported" into the <code>targetDocument</code>, ready
	 * to be used immediately. They are not "added" to the <code>Document</code>,
	 * that is up to the caller.
	 * <p>
	 * <b>Implementation Note</b> : the XhtmlFragment is wrapped in a top level
	 * parent <code>Element</code> to make doubly sure its well formed (eg has
	 * a top level parent). The immediate child <code>Node</code>s of this
	 * temporay top level <code>Element</code> are them returned. A non
	 * validating XML parser instance is used in this method for speed reasons.
	 * 
	 * @return a w3c DOM <code>DocumentFragment</code> containing the parsed
	 *         XHTML as w3c DOM <code>Nodes</code>
	 * @throws SAXException -
	 *             if its not valid XML
	 * @throws IOException -
	 *             very very unlikely that we will get an IOException when
	 *             reading a String
	 */
	public Node[] toDOM(Document targetDocument) throws SAXException, IOException {
		Node rawNodes[] = parseFragment(this.fragment);
		Node[] xhtmlNodes = new Node[rawNodes.length];
		for (int i = 0; i < rawNodes.length; i++) {
			Node child = targetDocument.importNode(rawNodes[i], true);
			xhtmlNodes[i] = child;
		}
		return xhtmlNodes;
	}

	/**
	 * This method use a simple "heuristic" based test to see if the
	 * <code>XhtmlFragment</code> content is in fact a simple bit of text,
	 * without markup. This can be used in some cases to "optimise" how the
	 * <code>XhtmlFragment</code> is used, since simple text can be inlined
	 * easily without the need to convert it to a set of
	 * <code>{@link Element}</code> nodes.
	 * <p>
	 * The test is very simple.  It looks for the pairs of characters such as < >
	 * and & ; if they are not present it considers it as <b>just text</b>.
	 * 
	 * @return - true if the content is considered "just text"
	 */
	public boolean isJustText() {
		// < and >
		if (this.fragment.indexOf('<') != -1 && this.fragment.indexOf('>') != -1) {
			return false;
		}
		// & and ;
		if (this.fragment.indexOf('&') != -1 && this.fragment.indexOf('>') != -1) {
			return false;
		}
		// ="
		if (this.fragment.indexOf("=\"") != -1) {
			return false;
		}
		return true;
	}

	/**
	 * This version of <code>toString()</code> simply returned the same as
	 * <code>getFragment()</code>.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return fragment;
	}

}
