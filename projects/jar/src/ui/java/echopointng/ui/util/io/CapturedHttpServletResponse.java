package echopointng.ui.util.io;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import echopointng.util.reflect.ReflectionKit;

/**
 * An HttpServletResponse that will capture the content
 * as it otherwise passes into the ether.
 */
public class CapturedHttpServletResponse implements HttpServletResponse {
	private HttpServletResponse wrappedResponse;
	private CapturedServletOutputStream noopServletOutputStream;
	private CapturedPrintWriter noopPrintWriter;
	private String contentType;
	private ByteArrayOutputStream out;

	public CapturedHttpServletResponse(HttpServletResponse wrappedResponse, String characterEncoding) {
		this.wrappedResponse = wrappedResponse;
		out = new ByteArrayOutputStream();
		noopServletOutputStream = new CapturedServletOutputStream(out);
		noopPrintWriter = new CapturedPrintWriter(out,characterEncoding);
		if (characterEncoding != null) {
			setCharacterEncoding(characterEncoding);
		}
	}
	
	/**
	 * Returns the captured output as an InputStream ready for reading
	 * @return the captured output as an InputStream ready for reading
	 */
	public InputStream getCapturedInputStream() {
		byte[] byteArray = out.toByteArray();
		ByteArrayInputStream is = new ByteArrayInputStream(byteArray);
		return is;
	}	
	/**
	 * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
	 */
	public void addCookie(Cookie arg0) {
		wrappedResponse.addCookie(arg0);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
	 */
	public void addDateHeader(String arg0, long arg1) {
		wrappedResponse.addDateHeader(arg0, arg1);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
	 */
	public void addHeader(String arg0, String arg1) {
		wrappedResponse.addHeader(arg0, arg1);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
	 */
	public void addIntHeader(String arg0, int arg1) {
		wrappedResponse.addIntHeader(arg0, arg1);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
	 */
	public boolean containsHeader(String arg0) {
		return wrappedResponse.containsHeader(arg0);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
	 * @deprecated
	 */
	public String encodeRedirectUrl(String arg0) {
		return wrappedResponse.encodeRedirectUrl(arg0);
	}

	/** 
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
	 */
	public String encodeRedirectURL(String arg0) {
		return wrappedResponse.encodeRedirectURL(arg0);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
	 * @deprecated
	 */
	public String encodeUrl(String arg0) {
		return wrappedResponse.encodeUrl(arg0);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
	 */
	public String encodeURL(String arg0) {
		return wrappedResponse.encodeURL(arg0);
	}

	/**
	 * @see javax.servlet.ServletResponse#flushBuffer()
	 */
	public void flushBuffer() throws IOException {
		wrappedResponse.flushBuffer();
	}

	/**
	 * @see javax.servlet.ServletResponse#getBufferSize()
	 */
	public int getBufferSize() {
		return wrappedResponse.getBufferSize();
	}

	/**
	 * @see javax.servlet.ServletResponse#getCharacterEncoding()
	 */
	public String getCharacterEncoding() {
		return wrappedResponse.getCharacterEncoding();
	}

	/**
	 * @see javax.servlet.ServletResponse#getLocale()
	 */
	public Locale getLocale() {
		return wrappedResponse.getLocale();
	}

	/**
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	public ServletOutputStream getOutputStream() throws IOException {
		return noopServletOutputStream;
	}

	/**
	 * @see javax.servlet.ServletResponse#getWriter()
	 */
	public PrintWriter getWriter() throws IOException {
		return noopPrintWriter;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return wrappedResponse.hashCode();
	}

	/**
	 * @see javax.servlet.ServletResponse#isCommitted()
	 */
	public boolean isCommitted() {
		return wrappedResponse.isCommitted();
	}

	/**
	 * @see javax.servlet.ServletResponse#reset()
	 */
	public void reset() {
		wrappedResponse.reset();
	}

	/**
	 * @see javax.servlet.ServletResponse#resetBuffer()
	 */
	public void resetBuffer() {
		wrappedResponse.resetBuffer();
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#sendError(int)
	 */
	public void sendError(int arg0) throws IOException {
		wrappedResponse.sendError(arg0);
	}

	/**
	 *  @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
	 */
	public void sendError(int arg0, String arg1) throws IOException {
		wrappedResponse.sendError(arg0, arg1);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
	 */
	public void sendRedirect(String arg0) throws IOException {
		wrappedResponse.sendRedirect(arg0);
	}

	/**
	 * @see javax.servlet.ServletResponse#setBufferSize(int)
	 */
	public void setBufferSize(int arg0) {
		wrappedResponse.setBufferSize(arg0);
	}

	/**
	 * @see javax.servlet.ServletResponse#setContentLength(int)
	 */
	public void setContentLength(int arg0) {
		wrappedResponse.setContentLength(arg0);
	}

	/**
	 * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
	 */
	public void setContentType(String newValue) {
		this.contentType = newValue;
		wrappedResponse.setContentType(newValue);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
	 */
	public void setDateHeader(String arg0, long arg1) {
		wrappedResponse.setDateHeader(arg0, arg1);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
	 */
	public void setHeader(String arg0, String arg1) {
		wrappedResponse.setHeader(arg0, arg1);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
	 */
	public void setIntHeader(String arg0, int arg1) {
		wrappedResponse.setIntHeader(arg0, arg1);
	}

	/**
	 * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale arg0) {
		wrappedResponse.setLocale(arg0);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int)
	 */
	public void setStatus(int arg0) {
		wrappedResponse.setStatus(arg0);
	}

	/**
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
	 * @deprecated
	 */
	public void setStatus(int arg0, String arg1) {
		wrappedResponse.setStatus(arg0, arg1);
	}
	/**
	 * Added for Servlet 2.4 support
	 */
	public String getContentType() {
		return this.contentType;
	}
	/**
	 * Added for Servlet 2.4 support
	 */
	public void setCharacterEncoding(String newValue) {
		ReflectionKit.invokeIfPresent("setCharacterEncoding",
				new Class[]{String.class}, Void.TYPE,
				wrappedResponse, new Object[] { newValue });
	}
}
