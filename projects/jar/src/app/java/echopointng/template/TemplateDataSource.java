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
import java.io.IOException;
import java.io.InputStream;

/**
 * <code>TemplateDataSource</code> is used to return template source data and
 * encoding information for this data.
 * <p>
 * A single <code>TemplateDataSource</code> can be used as the source for
 * multiple TemplatePanels. This helps reduce the memory footprint of the
 * template data. An implementation of this interface must keep the requirement
 * in mind.
 */
public interface TemplateDataSource {
	/**
	 * Returns a canonical name of this <code>TemplateDataSource</code>.
	 * <p>
	 * The name returned here is used to look up the parsing result of the
	 * internal caching, so it should differ for all different
	 * <code>TemplateDataSource</code> objects :-)
	 * <p>
	 * May return <code>null</code> if this TemplateDataSource is supposed to
	 * be parsed each time. The canonical name would be something like a
	 * filename or an URL.
	 * 
	 * @return a unique name of the <code>TemplateDataSource</code>
	 */
	public String getCanonicalName();

	/**
	 * This content type of the template data is used by the rendering framework
	 * to find an appropriate template compiler.
	 * <p>
	 * An example content type is text/xhtml and cause a template compiler to be
	 * found for XHTML template data.
	 * 
	 * @return the content type of the template data
	 */
	public String getContentType();

	/**
	 * This returns the character encoding of the
	 * <code>TemplateDataSource</code>.
	 * <p>
	 * This will be used to create a
	 * <code>new InputStreamReader(stream,characterEncoding)</code> from the
	 * <code>InputStream</code> return by <code>getInputStream()</code>.
	 * 
	 * @return the character encoding of the <code>TemplateDataSource</code>
	 *         as defined by the <code>java.io.InputStreamReader()</code>
	 *         specification.
	 * 
	 * @see java.io.InputStreamReader#InputStreamReader(java.io.InputStream,
	 *      java.lang.String)
	 */
	public String getCharacterEncoding();

	/**
	 * Gets an <code>InputStream</code> of this
	 * <code>TemplateDataSource</code>.
	 * <p>
	 * <em>Note</em> that this method may be called multiple times in the life
	 * of the <code>TemplateDataSource</code>. So you probably have to
	 * implement a buffer if your underlying data source is transient ..
	 * 
	 * @return a InputStream containing the template data
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * This returns a hint to the template rendering mechanism as to whether
	 * this template data can be cached. If <code>null</code> is returned then
	 * the template data will never be cached.
	 * <p>
	 * However the inverse is not necessarily the case. You may return a
	 * <code>TemplateCachingHints</code>, however this does not mean the
	 * rendering mechanism will cache the template data.
	 * <p>
	 * You might return <code>null</code> if the template data is especially
	 * large and you dont want it retained in cache memory.
	 * 
	 * @return null if the template data should not be cached or a
	 *         <code>TemplateCachingHints</code> to say how it <i>might</i>
	 *         be cached.
	 */
	public TemplateCachingHints getCachingHints();

	/**
	 * This returns a hint to the underlying template compiler mechanism as to
	 * how the template data should be compiled into XHTML.
	 * <p>
	 * Most of the <code>TemplateCompilerHints</code> properties are really
	 * aimed as JAXP XML parser implementations but you can provide generic
	 * values via the getAttribute() mechanism.
	 * 
	 * @return a <code>TemplateCompilerHints</code> implementation or null
	 * if there are no compiler hints
	 */
	public TemplateCompilerHints getCompilerHints();
}
