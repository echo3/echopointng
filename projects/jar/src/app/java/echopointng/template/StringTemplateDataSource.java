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

import echopointng.ui.util.io.StringInputStream;


/**
 * <code>StringTemplateDataSource</code> takes it template
 * data from a String object.
 */
public class StringTemplateDataSource extends AbstractTemplateDataSource {
	private String string;
	
	/**
	 * Creates a <code>StringTemplateDataSource</code> with 
	 * Unicode character encoding.
	 */
	public StringTemplateDataSource(String s) {
		super(DEFAULT_ENCODING);
		this.string = s;
	}
	
	/**
	 * Constructs a <code>StringTemplateDataSource</code>
	 * with no String template data.
	 */
	public StringTemplateDataSource() {
		this(null);
	}
	
	/**
	 * @see echopointng.template.TemplateDataSource#getCanonicalName()
	 */
	public String getCanonicalName() {
		return "String@" + System.identityHashCode(this.string);
	}
	/**
	 * @see echopointng.template.TemplateDataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return new StringInputStream(this.string);
	}
	/**
	 * @return Returns the string.
	 */
	public String getString() {
		return string;
	}
	/**
	 * @param string The string to set.
	 */
	public void setString(String string) {
		this.string = string;
	}
}
