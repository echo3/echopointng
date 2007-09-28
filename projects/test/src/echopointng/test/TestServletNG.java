package echopointng.test;

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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.webcontainer.WebContainerServlet;

/**
 * <code>TestServletNG</code>
 */
public class TestServletNG extends WebContainerServlet {

	/**
	 * @see nextapp.echo2.webcontainer.WebContainerServlet#newApplicationInstance()
	 */
	public ApplicationInstance newApplicationInstance() {
		return new TestInstanceNG();
		//return new TestEcho2WithHistoryInstance();
	}

	/**
	 * @see javax.servlet.GenericServlet#log(java.lang.String,
	 *      java.lang.Throwable)
	 */
	public void log(String arg0, Throwable arg1) {
		super.log(arg0, arg1);
		System.out.println(arg0);
		arg1.printStackTrace(System.out);
	}

	/**
	 * @see javax.servlet.GenericServlet#log(java.lang.String)
	 */
	public void log(String arg0) {
		super.log(arg0);
		System.out.println(arg0);
	}

	/**
	 * @see nextapp.echo2.webrender.WebRenderServlet#process(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			super.process(request, response);
		} catch (Throwable t) {
			t.printStackTrace(System.out);
			redirectToErrorServlet(request, response, t);
		}
	}


	/**
	 * Redirects to an error page so the Throwable can be shown
	 * 
	 * @param request -
	 *            the Servlet Http request object
	 * @param response -
	 *            the Servlet Http response object
	 * @param t -
	 *            the Throwable that has been thrown
	 */
	protected void redirectToErrorServlet(HttpServletRequest request, HttpServletResponse response, Throwable t) throws IOException, ServletException {
		// stick the error in the Session so we can get it on the other page
		HttpSession session = request.getSession();
		session.setAttribute("echopointng.servletThrowable", t);

		String uri = request.getRequestURI();
		int lastSlash = uri.lastIndexOf("/");
		uri = uri.substring(0,lastSlash+1);
		uri += "testngerrorpage";
		// 
		// Not sure which is a better way to redirect
		// from this "temporary" page. sendRedirect() works
		// however I have seen Internet comments that
		// question this.
		if (true) {
			response.sendRedirect(uri);
		} else {
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(uri);
			if (dispatcher == null) {
				throw new IOException("No Request Dispatcher for " + uri);
			}
			dispatcher.forward(request, response);
		}
	}

}
