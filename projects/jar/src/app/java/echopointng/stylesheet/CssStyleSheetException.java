package echopointng.stylesheet;
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

/**
 *
 * A <code>CssStyleSheetException</code> is thrown if the Css StyleSheet data 
 * cannot be read or parsed properly.
 * <p>
 * If another exception is the cause of this exception, for example
 * an IOException during parsing, then it can be contained with
 * the getCauseException() method.
 * <p>
 */
public class CssStyleSheetException extends Exception {
	Throwable causeException;
	int lineNo;
	/**
	 * StyleSheetException constructor
	 */
	public CssStyleSheetException(String s, Throwable causeException, int lineNo) {
		super(s);
		this.causeException = causeException;
		this.lineNo = lineNo;
	}
	/**
	 * Returns the base cause exception of the StyleSheetException or null.
	 */
	public Throwable getCauseException() {
		return causeException;
	}
	/**
	 * The line number at which the exception occurred or -1.
	 */
	public int getLineNo() {
		return lineNo;
	}
	/**
	 * 
	 */
	public String toString() {
		String cause = "";
		if (causeException != null)
			cause = causeException.toString();
		else
			cause = super.toString();
		return cause + " - line " + lineNo + "";
	}
}
