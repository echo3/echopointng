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

import nextapp.echo2.webcontainer.RenderContext;

import org.w3c.dom.Element;

import echopointng.template.TemplateDataSource;

/** 
 * <code>TemplateCompiler</code> are responsible for
 * compiling template data into a XHTML DOM element, ready
 * to be inserted into the rendering output.
 * <p>
 * A peer lookup mechanism is used to find the 
 * TemplateCompiler for a given content type.  This
 * is done via the <code>TemplateCompilerLoader</code>
 * class.
 * <p>
 * The source data need not be in XHTML format however
 * you must compile it into a XHTML DOM Element.  For 
 * example you could right your own custom XML/XSLT 
 * implementation that produced XHTML. 
 * <p>
 * The resultant XHTML DOM tree can have special marker Elements for 
 * component and string replacement.
 * <p>
 * <code>&lt;component name="xxx" style="xxx" /&gt;</code> markers will be replaced
 * with the named component and a CSS style will be created for the style
 * attribute and applied to the component.
 * <p>
 * <code>&lt;text name="xxx" /&gt;</code> markers will be replaced
 * with the named string data.
 * 
 * @see echopointng.ui.template.TemplateCompilerLoader
 */
public interface TemplateCompiler {

	/**
	 * This method is called to compile a template data source
	 * of template data into a DOM XHTML Element.
	 * 
	 * @param rc - the RenderContext is play at the time of compilation
	 * @param tds - the template data source
	 * 
	 * @return a DOM Element in XHTML or null if it cant be compiled
	 * 
	 * @throws Exception - can Throw exceptions such as parse exceptions etc..
	 */
	public Element compileTemplateDataIntoXHTML(RenderContext rc, TemplateDataSource tds) throws Exception;
	
}
