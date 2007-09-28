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
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * An ServletOutputStream that will capture the content
 * as it otherwise passes into the ether.
 */
public class CapturedServletOutputStream extends ServletOutputStream {

	private ByteArrayOutputStream out;

	public CapturedServletOutputStream(ByteArrayOutputStream out) {
		this.out = out;
	}

	/**
	 * @see java.io.OutputStream#close()
	 */
	public void close() throws IOException {
		out.close();
	}

	/**
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException {
		out.flush();
	}

	/**
	 * @see javax.servlet.ServletOutputStream#print(boolean)
	 */
	public void print(boolean arg0) throws IOException {
		print(String.valueOf(arg0));
	}

	/**
	 * @see javax.servlet.ServletOutputStream#print(char)
	 */
	public void print(char arg0) throws IOException {
		write(arg0);
	}

	/**
	 * @see javax.servlet.ServletOutputStream#print(double)
	 */
	public void print(double arg0) throws IOException {
		print(String.valueOf(arg0));
	}

	/**
	 * @see javax.servlet.ServletOutputStream#print(float)
	 */
	public void print(float arg0) throws IOException {
		print(String.valueOf(arg0));
	}

	/**
	 * @see javax.servlet.ServletOutputStream#print(int)
	 */
	public void print(int arg0) throws IOException {
		print(String.valueOf(arg0));
	}

	/**
	 * @see javax.servlet.ServletOutputStream#print(long)
	 */
	public void print(long arg0) throws IOException {
		print(String.valueOf(arg0));
	}

	/**
	 * @see javax.servlet.ServletOutputStream#println()
	 */
	public void println() throws IOException {
		write('\n');
	}

	/**
	 * @see javax.servlet.ServletOutputStream#println(boolean)
	 */
	public void println(boolean arg0) throws IOException {
		println(String.valueOf(arg0));
	}

	/**
	 * @see javax.servlet.ServletOutputStream#println(char)
	 */
	public void println(char arg0) throws IOException {
		println(String.valueOf(arg0));
	}

	/**
	 * @see javax.servlet.ServletOutputStream#println(double)
	 */
	public void println(double arg0) throws IOException {
		println(String.valueOf(arg0));
	}

	/**
	 * @see javax.servlet.ServletOutputStream#println(float)
	 */
	public void println(float arg0) throws IOException {
		println(String.valueOf(arg0));
	}

	/**
	 * @see javax.servlet.ServletOutputStream#println(int)
	 */
	public void println(int arg0) throws IOException {
		println(String.valueOf(arg0));
	}

	/**
	 * @see javax.servlet.ServletOutputStream#println(long)
	 */
	public void println(long arg0) throws IOException {
		println(String.valueOf(arg0));
	}

	/**
	 * @see javax.servlet.ServletOutputStream#print(java.lang.String)
	 */
	public void print(String arg0) throws IOException {
		int len = arg0.length();
		for (int i = 0; i < len; i++) {
			int c = arg0.charAt(i);
			print(c);
		}
	}

	/**
	 * @see javax.servlet.ServletOutputStream#println(java.lang.String)
	 */
	public void println(String arg0) throws IOException {
		print(arg0);
		print('\n');
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		out.write(b);
	}

	/**
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	/**
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}
}
