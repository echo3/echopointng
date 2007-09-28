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

import java.io.Serializable;

/**
 * A base class for <code>TemplateDataSource</code> implementations.
 * <p>
 * Its uses a SimpleTemplateCachingHints object as its TemplateCachingHints
 * implementation.
 */
public abstract class AbstractTemplateDataSource implements TemplateDataSource, Serializable {
	/** the default encoding is iso-8859-1 */
	public static String DEFAULT_ENCODING = "ISO-8859-1";

	/** the default encoding is text/xhtml */
	public static String DEFAULT_CONTENT_TYPE = "text/xhtml";

	private String encoding = DEFAULT_ENCODING;

	private TemplateCachingHints cachingHints;

	private String contentType = DEFAULT_CONTENT_TYPE;

	private TemplateCompilerHints compilerHints = null;

	/**
	 * AbstractTemplateDataSource constructor with default encoding.
	 */
	public AbstractTemplateDataSource() {
		this(DEFAULT_ENCODING);
	}

	/**
	 * AbstractTemplateDataSource constructor with encoding.
	 */
	public AbstractTemplateDataSource(String encoding) {
		super();
		this.cachingHints = new SimpleTemplateCachingHints();
		this.encoding = encoding;
	}

	/**
	 * @see echopointng.template.TemplateDataSource#getCharacterEncoding()
	 */
	public java.lang.String getCharacterEncoding() {
		return encoding;
	}

	/**
	 * Sets the character encoding to be used with this TemplateDataSource
	 * 
	 * @param newEncoding -
	 *            the new encoding to use
	 * 
	 * @see TemplateDataSource#getCharacterEncoding()
	 */
	public void setCharacterEncoding(String newEncoding) {
		encoding = newEncoding;
	}

	/**
	 * @see echopointng.template.TemplateDataSource#getCachingHints()
	 */
	public TemplateCachingHints getCachingHints() {
		return cachingHints;
	}

	/**
	 * Sets the caching hints to use for this template data source
	 * 
	 * @param newValue -
	 *            the new hints
	 */
	public void setCachingHints(TemplateCachingHints newValue) {
		this.cachingHints = newValue;
	}

	/**
	 * @see echopointng.template.TemplateDataSource#getContentType()
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type of the template data
	 * 
	 * @param contentType
	 *            The contentType to set.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @see echopointng.template.TemplateDataSource#getCompilerHints()
	 */
	public TemplateCompilerHints getCompilerHints() {
		return compilerHints;
	}

	/**
	 * Sets the <code>TemplateCompilerHints</code> to use for this
	 * <code>TemplateDataSource</code>.
	 * 
	 * @param compilerHints -
	 *            the <code>TemplateCompilerHints</code> to use
	 */
	public void setCompilerHints(TemplateCompilerHints compilerHints) {
		this.compilerHints = compilerHints;
	}
}
