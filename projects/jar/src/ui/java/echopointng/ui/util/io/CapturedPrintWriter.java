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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

/**
 * An PrintWriter that will capture the content as it otherwise passes into the
 * ether.
 */
public class CapturedPrintWriter extends PrintWriter {

	private ByteArrayOutputStream out;

	private String characterEncoding;

	public CapturedPrintWriter(ByteArrayOutputStream out, String characterEncoding) {
		super(new StringWriter());
		this.out = out;
		this.characterEncoding = characterEncoding;
	}

	/**
	 * @see java.io.PrintWriter#checkError()
	 */
	public boolean checkError() {
		return false;
	}

	/**
	 * @see java.io.Writer#close()
	 */
	public void close() {
		try {
			out.close();
		} catch (IOException e) {
		}
	}

	/**
	 * @see java.io.Writer#flush()
	 */
	public void flush() {
		try {
			out.flush();
		} catch (IOException e) {
		}
	}

	/**
	 * @see java.io.PrintWriter#print(boolean)
	 */
	public void print(boolean b) {
		print(String.valueOf(b));
	}

	/**
	 * @see java.io.PrintWriter#print(char)
	 */
	public void print(char c) {
		write(c);
	}

	/**
	 * @see java.io.PrintWriter#print(char[])
	 */
	public void print(char[] arr) {
		write(arr);
	}

	/**
	 * @see java.io.PrintWriter#print(double)
	 */
	public void print(double d) {
		print(String.valueOf(d));
	}

	/**
	 * @see java.io.PrintWriter#print(float)
	 */
	public void print(float f) {
		print(String.valueOf(f));
	}

	/**
	 * @see java.io.PrintWriter#print(int)
	 */
	public void print(int i) {
		print(String.valueOf(i));
	}

	/**
	 * @see java.io.PrintWriter#print(long)
	 */
	public void print(long l) {
		print(String.valueOf(l));
	}

	/**
	 * @see java.io.PrintWriter#print(java.lang.Object)
	 */
	public void print(Object obj) {
		print(String.valueOf(obj));
	}

	/**
	 * @see java.io.PrintWriter#print(java.lang.String)
	 */
	public void print(String s) {
		writeBytes(toEncodedBytes(s));
	}

	/**
	 * @see java.io.PrintWriter#println()
	 */
	public void println() {
		write('\n');
	}

	/**
	 * @see java.io.PrintWriter#println(boolean)
	 */
	public void println(boolean x) {
		println(String.valueOf(x));
	}

	/**
	 * @see java.io.PrintWriter#println(char)
	 */
	public void println(char x) {
		println(String.valueOf(x));
	}

	/**
	 * @see java.io.PrintWriter#println(char[])
	 */
	public void println(char[] x) {
		write(x);
		write('\n');
	}

	/**
	 * @see java.io.PrintWriter#println(double)
	 */
	public void println(double x) {
		println(String.valueOf(x));
	}

	/**
	 * @see java.io.PrintWriter#println(float)
	 */
	public void println(float x) {
		println(String.valueOf(x));
	}

	/**
	 * @see java.io.PrintWriter#println(int)
	 */
	public void println(int x) {
		println(String.valueOf(x));
	}

	/**
	 * @see java.io.PrintWriter#println(long)
	 */
	public void println(long x) {
		println(String.valueOf(x));
	}

	/**
	 * @see java.io.PrintWriter#println(java.lang.Object)
	 */
	public void println(Object x) {
		println(String.valueOf(x));
	}

	/**
	 * @see java.io.PrintWriter#println(java.lang.String)
	 */
	public void println(String x) {
		println(x.toCharArray());
	}

	/**
	 * @see java.io.PrintWriter#setError()
	 */
	protected void setError() {
		; // do nothing
	}

	/**
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] buf, int off, int len) {
		for (int i = off; i < len; i++) {
			write(buf[i]);
		}
	}

	/**
	 * @see java.io.Writer#write(char[])
	 */
	public void write(char[] buf) {
		write(buf, 0, buf.length);
	}

	/**
	 * @see java.io.Writer#write(int)
	 */
	public void write(int c) {
		out.write(c);
	}

	/**
	 * @see java.io.Writer#write(java.lang.String, int, int)
	 */
	public void write(String s, int off, int len) {
		writeBytes(toEncodedBytes(s), off, len);
	}

	/**
	 * @see java.io.Writer#write(java.lang.String)
	 */
	public void write(String s) {
		writeBytes(toEncodedBytes(s));
	}

	private void writeBytes(byte[] buf) {
		writeBytes(buf,0,buf.length);
	}

	private void writeBytes(byte[] buf, int off, int len) {
		for (int i = 0; i < len; i++) {
			write((int) buf[i + off]);
		}
	}

	private byte[] toEncodedBytes(String s) {
		if (this.characterEncoding != null) {
			try {
				return s.getBytes(this.characterEncoding);
			} catch (UnsupportedEncodingException e) {
				return s.getBytes();
			}
		} else {
			return s.getBytes();
		}
	}

}
