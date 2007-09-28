package echopointng.template;

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

/**
 * <code>TemplateCompilerHints</code> is used to indicate to the underlying
 * templating compiler mechanism information about how it should compile the
 * templaet data.
 * <p>
 * Most of the <code>TemplateCompilerHints</code> properties are really aimed
 * as JAXP XML parser implementations but you can provide generic values via the
 * getAttribute() mechanism.
 * 
 */
public interface TemplateCompilerHints {

	/**
	 * @return Indicates whether or not the factory is configured to produce
	 *         parsers which converts CDATA nodes to Text nodes and appends it
	 *         to the adjacent (if any) Text node.
	 */
	public boolean isCoalescing();

	/**
	 * @return Indicates whether or not to use XHTML parsers which expand entity
	 *         reference nodes.
	 */
	public boolean isExpandEntityReferences();

	/**
	 * @return Indicates whether or not to use XHTML parsers which ignores
	 *         comments.
	 */
	public boolean isIgnoringComments();

	/**
	 * @return Indicates whether or not to use XHTML parsers which ignore
	 *         ignorable whitespace in element content.
	 */
	public boolean isIgnoringElementContentWhitespace();

	/**
	 * @return Indicates whether or not to use XHTML parsers which are namespace
	 *         aware.
	 */
	public boolean isNamespaceAware();

	/**
	 * @return Indicates whether or not to use XHTML parsers which validate the
	 *         XHTML content during parse.
	 */
	public boolean isValidating();

	/**
	 * @return Allows the system to retrieve all the specific attributes to be
	 *         set into the underlying XHTML parser implementation. if there are
	 *         no attributes is should return a String[0] array as oppsoed to null.
	 */
	public String[] getAttributeNames();

	/**
	 * Allows the system to retrieve specific attributes on the underlying XHTML
	 * parser implementation by attribute name. The name should have been
	 * provided in the <code>getAttributeNames()</code> method.
	 * 
	 * @param attributeName -
	 *            the name of the specific attribute
	 * @return the value for that attribute name
	 */
	public Object getAttributeValue(String attributeName);
}