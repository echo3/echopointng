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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import nextapp.echo2.webcontainer.RenderContext;

import org.w3c.dom.Element;

import echopointng.template.JspTemplateDataSource;
import echopointng.template.TemplateDataSource;
import echopointng.ui.util.io.CapturedHttpServletResponse;
import echopointng.util.throwable.ThrowableKit;

/** 
 * <code>JspXHTMLTemplateCompiler</code> can compile
 * XHTML source template data from JSP page
 * into a XHTML DO Element.
 */

public class JspXHTMLTemplateCompiler extends XHTMLTemplateCompiler {

	/**
	 * @see echopointng.ui.template.TemplateCompiler#compileTemplateDataIntoXHTML(nextapp.echo2.webcontainer.RenderContext, echopointng.template.TemplateDataSource)
	 */
	public Element compileTemplateDataIntoXHTML(RenderContext rc, TemplateDataSource tds) throws Exception {
		// we dont use the TDS input stream because it does know 
		// where to get the stream
		InputStream inputStream = jspExecute(rc,(JspTemplateDataSource) tds);
        return compileXHTML(inputStream,tds);
	}
	
	/**
	 * Executes a JSP page for a given JspTemplateDataSource.  The content
	 * of the JSP page is captured and is ready for XHTML parsing afterwards
	 * 
	 * @param rc - the RenderContext in play
	 * @param jspPath - the path to the JSP file 
	 * @return an InputStream of the captured JSP output
	 */
	private InputStream jspExecute(RenderContext rc, JspTemplateDataSource jspTemplateDataSource) {
		String jspPath  = jspTemplateDataSource.getJspPath();
		Servlet servlet = rc.getConnection().getServlet();
		HttpServletRequest request = rc.getConnection().getRequest();
		HttpServletResponse wrappedResponse = rc.getConnection().getResponse();
		CapturedHttpServletResponse response = new CapturedHttpServletResponse(wrappedResponse,jspTemplateDataSource.getCharacterEncoding());
		
		String requestAttributes[] = jspTemplateDataSource.getAttributeNames();
		try {
			//
			// setup Request variables from the JspTemplateDataSource
			for (int i = 0; i < requestAttributes.length; i++) {
				String name = requestAttributes[i];
				request.setAttribute(name,jspTemplateDataSource.getAttribute(name));
			}
			//
			// and get the content into out captured response
			jspInclude(true,jspPath,servlet,request,response);
		} finally {
			//
			// tear down Request variables 
			for (int i = 0; i < requestAttributes.length; i++) {
				String name = requestAttributes[i];
				request.removeAttribute(name);
			}
		}
		
		//
		// now we have captured the JSP output, we need to reverse that
		// and return it as an InputStream.
		return response.getCapturedInputStream();
	}
	
	/**
	 * Include the JSP template content by using the PageContext.include mechanism
	 */
	private void jspInclude(boolean isLoudErrorsUsed, String jspPath, Servlet servlet, HttpServletRequest request, HttpServletResponse response) {
		JspFactory factory = null;
		PageContext pc = null;
		PrintWriter out = null;
		try {
			out = response.getWriter();
			factory = JspFactory.getDefaultFactory();

			pc = factory.getPageContext(servlet, request, response, null, true, JspWriter.NO_BUFFER, true);

			if (pc != null) {
				///////////////////////////////////////////////
				// include the page
				///////////////////////////////////////////////
				pc.include(jspPath);

				factory.releasePageContext(pc);
				pc = null;
				
			} else {
				///////////////////////////////////////////////
				// we have no page context, it cant be output
				///////////////////////////////////////////////
				throw new ServletException("Unable to obtain JSP PageContext object");
			}
		} catch (ServletException e) {
			if (isLoudErrorsUsed) {
				out.println("<div style=\"background:red;color:white;font-size:12;border-width:1; border-style:solid; border-color:black; padding:4; margin:4\" >");
				out.println("<blink><b>");
				out.println("<p>JSP Exception : '<em>" + e + "</em>'</p>");
				out.println("</b></blink></div>");
			} else {
				out.println("<!-- ");
				out.println("JSP Exception : '" + e + "'");
				out.println(" -->");
			}
		} catch (IOException e) {
			throw ThrowableKit.makeRuntimeException(e);
		} finally {
			// cleanup the factory and page context
			if (factory != null && pc != null)
				factory.releasePageContext(pc);
		}
	}
		

}
