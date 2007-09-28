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
import java.net.URL;

/**
 * <code>ResourceTemplateDataSource</code> takes it template data from a Class
 * Resource
 */
public class ResourceTemplateDataSource extends AbstractTemplateDataSource {

	private String resource;

	private String canonicalName;

	private ClassLoader classLoader;

	/**
	 * Constructs a <code>ResourceTemplateDataSource</code> with the specified
	 * class resource as template data.
	 * 
	 * @param resource -
	 *            the name of the class resource to load template data from
	 * @param classLoader -
	 *            the class loader to load resources from
	 */
	public ResourceTemplateDataSource(String resource, ClassLoader classLoader) {
		this.resource = resource;
		this.classLoader = classLoader;
		this.canonicalName = "res:" + resource;
	}

	/**
	 * Constructs a <code>ResourceTemplateDataSource</code> using the named
	 * class resource as template data.
	 * 
	 * @param resource -
	 *            the name of the class resource to load template data from
	 */
	public ResourceTemplateDataSource(String resource) {
		this(resource, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Constructs a <code>ResourceTemplateDataSource</code> with no template
	 * resousrce.
	 *  
	 */
	public ResourceTemplateDataSource() {
		this(null, null);
	}

	/**
	 * @see echopointng.template.TemplateDataSource#getCanonicalName()
	 */
	public String getCanonicalName() {
		return canonicalName;
	}

	/**
	 * @see echopointng.template.TemplateDataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		URL url = classLoader.getResource(resource);
		if (url != null)
			return url.openStream();
		return null;
	}

	/**
	 * 
	 * @return Returns the classLoader.
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * @param classLoader
	 *            The classLoader to set.
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * @return Returns the resource.
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * @param resource
	 *            The resource to set.
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}
}
