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
package echopointng.ui.template;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nextapp.echo2.webcontainer.RenderContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import echopointng.template.TemplateCompilerHints;
import echopointng.template.TemplateDataSource;

/**
 * <code>XHTMLTemplateCompiler</code> can compile source XHTML template data
 * into.. well a XHTML DOM Element.
 */

public class XHTMLTemplateCompiler implements TemplateCompiler {

	/**
	 * ThreadLocal cache of validating <code>DocumentBuilder</code> instances.
	 */
	private static final ThreadLocal nonvalidatingDocumentBuilders = new ThreadLocal() {

		/**
		 * @see java.lang.ThreadLocal#initialValue()
		 */
		protected Object initialValue() {
			return buildDocumentBuilder(null);
		}
	};

	/**
	 * Called to return a <code>DocumentBuilder</code> based on the compiler
	 * hints.
	 * 
	 * @param compilerHints -
	 *            the hints for the factory used to build the
	 *            <code>DocumentBuilder</code> which can be null.
	 * @return a <code>DocumentBuilder</code>
	 */
	protected static final DocumentBuilder buildDocumentBuilder(TemplateCompilerHints compilerHints) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			if (compilerHints == null) {
				factory.setNamespaceAware(true);
				factory.setValidating(false);
				factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.valueOf(false));
			} else {
				factory.setCoalescing(compilerHints.isCoalescing());
				factory.setExpandEntityReferences(compilerHints.isExpandEntityReferences());
				factory.setIgnoringComments(compilerHints.isIgnoringComments());
				factory.setIgnoringElementContentWhitespace(compilerHints.isIgnoringElementContentWhitespace());
				factory.setNamespaceAware(compilerHints.isNamespaceAware());
				factory.setValidating(compilerHints.isValidating());
				// custom attributes
				String[] attrNames = compilerHints.getAttributeNames();
				if (attrNames != null) {
					for (int i = 0; i < attrNames.length; i++) {
						Object value = compilerHints.getAttributeValue(attrNames[i]);
						factory.setAttribute(attrNames[i], value);
					}
				}
			}
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
	protected static DocumentBuilder getNonValidatingDocumentBuilder() {
		return (DocumentBuilder) nonvalidatingDocumentBuilders.get();
	}

	/**
	 * @see echopointng.ui.template.TemplateCompiler#compileTemplateDataIntoXHTML(nextapp.echo2.webcontainer.RenderContext,
	 *      echopointng.template.TemplateDataSource)
	 */
	public Element compileTemplateDataIntoXHTML(RenderContext rc, TemplateDataSource tds) throws Exception {
		return compileXHTML(tds.getInputStream(), tds);
	}

	/**
	 * This does the actual compiling of a stream of XHTML into a XHTML DOM
	 * Element.
	 * 
	 * @param inputStream -
	 *            this must be a stream of XHTML data
	 * @param characterEncoding -
	 *            the encoding to use
	 * @return
	 * @throws Exception
	 */
	protected Element compileXHTML(InputStream inputStream, TemplateDataSource tds) throws Exception {
		long startMS = System.currentTimeMillis();

		String characterEncoding = tds.getCharacterEncoding();
		TemplateCompilerHints compilerHints = tds.getCompilerHints();

		InputStreamReader isr;
		if (characterEncoding != null) {
			isr = new InputStreamReader(inputStream, characterEncoding);
		} else {
			isr = new InputStreamReader(inputStream);
		}
		
		InputSource inputSource = new InputSource();
		inputSource.setCharacterStream(isr);
		inputSource.setByteStream(inputStream);
		inputSource.setEncoding(characterEncoding);

		try {
			DocumentBuilder docBuilder = null;
			if (compilerHints == null) {
				docBuilder = getNonValidatingDocumentBuilder();
			} else {
				docBuilder = buildDocumentBuilder(compilerHints);
			}
			Document document = docBuilder.parse(inputSource);
			Element documentElement = document.getDocumentElement();

			long compileMS = System.currentTimeMillis() - startMS;
			if (compileMS == 0)
				;
			return documentElement;
		} catch (RuntimeException rte) {
			throw rte;
		}
	}

}
