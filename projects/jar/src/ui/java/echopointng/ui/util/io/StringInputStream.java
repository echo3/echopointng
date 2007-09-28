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
package echopointng.ui.util.io;

import java.io.InputStream;

/**
 * <code>StringInputStream</code> can read a String and return it as an
 * <code>InputStream.</code>
 */

public class StringInputStream extends InputStream {
	private String buffer;

	private int pos;

	private int count;

	/**
	 * Constructs a <code>StringInputStream</code> from the specified
	 * <code>String</code>
	 *
	 * @param s - the string to read
	 */
	public StringInputStream(String s) {
		this.buffer = s;
		count = s.length();
	}

	/**
	 * @see java.io.InputStream#read()
	 */
	public synchronized int read() {
		return (pos < count) ? (buffer.charAt(pos++) & 0xFF) : -1;
	}

	/**
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public synchronized int read(byte b[], int off, int len) {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		}
		if (pos >= count) {
			return -1;
		}
		if (pos + len > count) {
			len = count - pos;
		}
		if (len <= 0) {
			return 0;
		}
		String s = buffer;
		int cnt = len;
		while (--cnt >= 0) {
			b[off++] = (byte) s.charAt(pos++);
		}

		return len;
	}

	/**
	 * @see java.io.InputStream#skip(long)
	 */
	public synchronized long skip(long n) {
		if (n < 0) {
			return 0;
		}
		if (n > count - pos) {
			n = count - pos;
		}
		pos += n;
		return n;
	}

	/**
	 * @see java.io.InputStream#available()
	 */
	public synchronized int available() {
		return count - pos;
	}

	/**
	 * @see java.io.InputStream#reset()
	 */
	public synchronized void reset() {
		pos = 0;
	}
}
