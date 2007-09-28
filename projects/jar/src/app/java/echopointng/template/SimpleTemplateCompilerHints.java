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
package echopointng.template;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>SimpleTemplateCompilerHints</code> by default sets all boolean
 * properties to false except for <code>namespaceAware</code> which is set to
 * true. There are by default no custom attributes.
 * <p>
 * Some XML parses such as Xerces will automatically go to the Internet to get the XHTML
 * DTD from the W3C if a DOCTYPE is specified in the template data, irrespective of whether
 * validating is true or not.  You can stop this on Xerces by setting a custom property
 * as follows :
 * <pre>
 * setValidating(false);
 * setAttributeValue("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.valueOf(false));
 * </pre>
 */
public class SimpleTemplateCompilerHints implements TemplateCompilerHints {

	Map attributes = null;

	private boolean coalescing = false;

	private boolean expandEntityReferences = false;

	private boolean ignoringComments = false;

	private boolean ignoringElementContentWhitespace = false;

	private boolean namespaceAware = true;

	private boolean validating = false;

	/**
	 * Constructs a <code>SimpleTemplateCompilerHints</code>
	 */
	public SimpleTemplateCompilerHints() {
	}

	/**
	 * @see echopointng.template.TemplateCompilerHints#getAttributeNames()
	 */
	public String[] getAttributeNames() {
		if (attributes == null) {
			return new String[0];
		}
		return (String[]) attributes.keySet().toArray(new String[attributes.keySet().size()]);
	}

	/**
	 * @see echopointng.template.TemplateCompilerHints#getAttributeValue(java.lang.String)
	 */
	public Object getAttributeValue(String attributeName) {
		if (attributes == null) {
			return null;
		}
		return attributes.get(attributeName);
	}

	/**
	 * @see echopointng.template.TemplateCompilerHints#isCoalescing()
	 */
	public boolean isCoalescing() {
		return coalescing;
	}

	/**
	 * @see echopointng.template.TemplateCompilerHints#isExpandEntityReferences()
	 */
	public boolean isExpandEntityReferences() {
		return expandEntityReferences;
	}

	/**
	 * @see echopointng.template.TemplateCompilerHints#isIgnoringComments()
	 */
	public boolean isIgnoringComments() {
		return ignoringComments;
	}

	/**
	 * @see echopointng.template.TemplateCompilerHints#isIgnoringElementContentWhitespace()
	 */
	public boolean isIgnoringElementContentWhitespace() {
		return ignoringElementContentWhitespace;
	}

	/**
	 * @see echopointng.template.TemplateCompilerHints#isNamespaceAware()
	 */
	public boolean isNamespaceAware() {
		return namespaceAware;
	}

	/**
	 * @see echopointng.template.TemplateCompilerHints#isValidating()
	 */
	public boolean isValidating() {
		return validating;
	}

	/**
	 * Sets an attribute value against a given attribute name
	 * 
	 * @param attributeName -
	 *            the name of the attribute
	 * @param attributeValue -
	 *            its value
	 */
	public void setAttributeValue(String attributeName, Object attributeValue) {
		if (attributes == null) {
			attributes = new HashMap();
		}
		attributes.put(attributeName, attributeValue);
	}

	/**
	 * @param coalescing
	 *            The boolean value to set.
	 * @see TemplateCompilerHints#isCoalescing()
	 */
	public void setCoalescing(boolean coalescing) {
		this.coalescing = coalescing;
	}

	/**
	 * @param expandEntityReferences
	 *            The boolean value to set.
	 * @see TemplateCompilerHints#isExpandEntityReferences()
	 */
	public void setExpandEntityReferences(boolean expandEntityReferences) {
		this.expandEntityReferences = expandEntityReferences;
	}

	/**
	 * @param ignoringComments
	 *            The boolean value to set.
	 * @see TemplateCompilerHints#isIgnoringComments()
	 */
	public void setIgnoringComments(boolean ignoringComments) {
		this.ignoringComments = ignoringComments;
	}

	/**
	 * @param ignoringElementContentWhitespace
	 *            The boolean value to set.
	 * @see TemplateCompilerHints#isIgnoringElementContentWhitespace()
	 */
	public void setIgnoringElementContentWhitespace(boolean ignoringElementContentWhitespace) {
		this.ignoringElementContentWhitespace = ignoringElementContentWhitespace;
	}

	/**
	 * @param namespaceAware
	 *            The boolean value to set.
	 * @see TemplateCompilerHints#isNamespaceAware()
	 */
	public void setNamespaceAware(boolean namespaceAware) {
		this.namespaceAware = namespaceAware;
	}

	/**
	 * @param validating
	 *            The boolean value to set.
	 * @see TemplateCompilerHints#isValidating()
	 */
	public void setValidating(boolean validating) {
		this.validating = validating;
	}
}
