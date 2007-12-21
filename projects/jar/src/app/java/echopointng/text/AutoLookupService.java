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
package echopointng.text;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Style;
import nextapp.echo2.webrender.Connection;
import nextapp.echo2.webrender.ContentType;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.ServiceRegistry;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.HtmlDocument;
import nextapp.echo2.webrender.output.XmlDocument;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import echopointng.AutoLookupTextFieldEx;
import echopointng.EPNG;
import echopointng.text.AutoLookupModel.Entry;
import echopointng.util.HtmlKit;
import echopointng.xhtml.XhtmlFragment;

/**
 * <code>AutoLookupService</code> is used to provide data to a
 * <code>AutoLookupTextFieldEx</code> component in an asynchronous manner.
 */

public class AutoLookupService implements Service {

	/**
	 * The singleton <code>AutoLookupService</code>
	 */
	public static final AutoLookupService INSTANCE;
	static {
		INSTANCE = new AutoLookupService();
		ServiceRegistry serviceRegistry = WebRenderServlet.getServiceRegistry();
		serviceRegistry.add(AutoLookupService.INSTANCE);
	}

	/**
	 * @see nextapp.echo2.webrender.Service#getId()
	 */
	public String getId() {
		return "EPNG.AutoLookup";
	}

	/**
	 * @see nextapp.echo2.webrender.Service#getVersion()
	 */
	public int getVersion() {
		return DO_NOT_CACHE;
	}

	private Map interestedParties = new HashMap();

	/**
	 * Registers the <code>AutoLookupTextFieldEx</code> with the service
	 * 
	 * @param textFieldEx -
	 *            a <code>AutoLookupTextFieldEx</code> to be notified of
	 *            external events.
	 */
	public synchronized void register(AutoLookupTextFieldEx textFieldEx) {
		interestedParties.put("c_" + textFieldEx.getRenderId(), textFieldEx);
	}

	/**
	 * Deregisters the <code>AutoLookupTextFieldEx</code> with the service
	 * 
	 * @param textFieldEx -
	 *            an <code>AutoLookupTextFieldEx</code> to be removed from
	 *            being notified of external events.
	 */
	public synchronized void deregister(AutoLookupTextFieldEx textFieldEx) {
		interestedParties.remove("c_" + textFieldEx.getRenderId());
	}

	/**
	 * @see nextapp.echo2.webrender.Service#service(nextapp.echo2.webrender.Connection)
	 */
	public void service(Connection conn) throws IOException {
		HttpServletRequest request = conn.getRequest();

		String elementId = request.getParameter("elementId");
		String searchValue = request.getParameter("searchValue");
		AutoLookupTextFieldEx textFieldEx = (AutoLookupTextFieldEx) interestedParties.get(elementId);
		if (textFieldEx == null) {
			throw new IllegalStateException("The TextFieldEx " + elementId + " could not be found.");
		}
		AutoLookupModel autoLookupModel = (AutoLookupModel) getRP(textFieldEx, AutoLookupTextFieldEx.PROPERTY_AUTO_LOOKUP_MODEL);
		if (autoLookupModel == null) {
			return; // nothing to do
		}

		XmlDocument xmlDocument = new XmlDocument("data", null, null, HtmlDocument.XHTML_1_0_NAMESPACE_URI);
		Document doc = xmlDocument.getDocument();
		Element dataElement = doc.getDocumentElement();
		Element autoLookupModelE = doc.createElement("autoLookupModel");

		Entry[] entries = autoLookupModel.searchEntries(searchValue, autoLookupModel.getMatchOptions());
		if (entries != null) {
			for (int i = 0; i < entries.length; i++) {
				Element entryE = createEntryXml(doc,entries[i]);
				autoLookupModelE.appendChild(entryE);
			}
		}
		dataElement.appendChild(autoLookupModelE);

		conn.setContentType(ContentType.TEXT_XML);
		PrintWriter out = conn.getWriter();
		xmlDocument.render(out);
	}

	/**
	 * Creates the "entry" XML message that represents this Entry
	 * 
	 * @param document -
	 *            The owning DOM document
	 * @param entry -
	 *            the lookup entry
	 * @return a DOM Element with the name 'entry'
	 */
	public static Element createEntryXml(Document document, Entry entry) {

		String value = entry.getValue();
		String sortValue = entry.getSortValue();
		XhtmlFragment xhtmlFrag = entry.getXhtmlFragment();
		String xhtml = xhtmlFrag == null ? value : xhtmlFrag.toString();

		sortValue = (sortValue != null ? sortValue : value);

		value = HtmlKit.encode(value);
		sortValue = HtmlKit.encode(sortValue);
		xhtml = HtmlKit.encode(xhtml);

		Element valueE = document.createElement("value");
		valueE.appendChild(document.createCDATASection(value));
		Element sortValueE = document.createElement("sortValue");
		sortValueE.appendChild(document.createCDATASection(sortValue));
		Element xhtmlE = document.createElement("xhtml");
		xhtmlE.appendChild(document.createCDATASection(xhtml.toString()));

		Element entryE = document.createElement("entry");
		entryE.appendChild(valueE);
		entryE.appendChild(sortValueE);
		entryE.appendChild(xhtmlE);
		return entryE;
	}

	private static Object getRP(Component component, String propertyName) {
		Object value = component.getRenderProperty(propertyName);
		if (value == null) {
			Style style = EPNG.getFallBackStyle(component);
			if (style != null) {
				value = style.getProperty(propertyName);
			}
		}
		return value;
	}
}
