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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * The CssStyleSheetParser object implements a parser for 'w3c CSS like' StyleSheet files.  This class is
 * used internally by the CssStyleSheet class to parse any StyleSheet files.  It is not intended for general 
 * use otherwise.
 * <p>
 * If the parse is sucessful, then a list of CssStyleSheetParser.ClassEntries are created, within
 * each one if a zero or more CssStyleSheetParser.AttrEntry objects, one for each atribute.
 * <p>
 * Note that the CssStyleSheetParser does not intepret any of the data nor does it create 
 * eventual objects out of entries such as color objects etc..  
 */
class CssStyleSheetParser {

	private final int STATE_CLASSNAME = 0;
	private final int STATE_LEFTCURLY = 1;
	private final int STATE_RIGHTCURLY = 2;
	private final int STATE_ATTRNAME = 3;
	private final int STATE_ATTRVAL = 4;
	private final int STATE_SEMICOLON = 5;
	private final int STATE_COLON = 6;
	private final int STATE_EXTENDS = 7;
	
	private int state = STATE_RIGHTCURLY;
	private List classEntryList = new ArrayList();
	private Stack propertyStack = new Stack();
	private InputStream input;
	
	/**
	 * StyleSheetParser constructor.
	 */
	public CssStyleSheetParser(InputStream in) {
		super();
		this.input = in;
	}
	/**
	 * Parses the CSS StyleSheet data into a series of StyleSheetEntry objects.
	 * <p>
	 * This will throw a StyleSheetException if the data format 
	 * is invalid or cannot be read.
	 */
	void parse() throws CssStyleSheetException {
		String token = null;
		String currentPropertyName = null;
		String currentPropertyValue = null;
		String className = null;
		CssClassDecl currentCE = null;
		CssClassPropertyDecl currentPE = null;
		int tt = 0;
		int orderNum = 0;
		
		StreamTokenizer st = null;
		try {
			st = new StreamTokenizer(new InputStreamReader(this.input));
			resetParserSyntaxTable(st, false);
			classEntryList.clear();
			//---------------------------------------------------------
			// Parse the input stream
			//---------------------------------------------------------
			tt = st.nextToken();
			while (tt != StreamTokenizer.TT_EOF) {
				token = "";

				boolean isWordToken = true;
				switch (tt) {
					case StreamTokenizer.TT_WORD :
						// A word was found; the value is in sval
						token = st.sval;
						break;
					case '"' :
						// A double-quoted string was found; sval contains the contents
						token = '"' + st.sval + '"';
						break;
					case '\'' :
						// A single-quoted string was found; sval contains the contents
						token = '\'' + st.sval + '\'';
						break;
					case StreamTokenizer.TT_EOF :
						// End of file has been reached
						break;
					default :
						// A regular character was found; the value is the token itself
						token = "" + (char) st.ttype;
						isWordToken = false;
						break;
				}
				
				//---------------------------------------------------------
				// now check which state we are in and determine if the token is valid
				//---------------------------------------------------------
				switch (state) {
					case STATE_RIGHTCURLY :
						{
							resetParserSyntaxTable(st, false);

							if (!isWordToken)
								throwTPE(st.lineno(), token, "class name expected");
							className = token;

							state = STATE_CLASSNAME;
							break;
						}
					case STATE_CLASSNAME :
						{
							if (isWordToken ) {
								if (token.equalsIgnoreCase("extends")) {
									currentCE = new CssClassDecl();
									currentCE.setLineNo(st.lineno());
									currentCE.setClassName(className);
									state = STATE_EXTENDS;
								} else {
									className += " " + token;
								}
							} else if (token.equals(",")) {
								className += " ";
							} else {
								if (token.equals(";")) {
									state = STATE_CLASSNAME;
								} else {
									if (!token.equals("{"))
										throwTPE(st.lineno(), token, "opening brace '{' or comma ',' expected");
									
									// a new class has been encountered
									currentCE = new CssClassDecl();
									currentCE.setLineNo(st.lineno());
									currentCE.setClassName(className);

									resetParserSyntaxTable(st, true);
									state = STATE_LEFTCURLY;
								}
							}
							break;
						}
					case STATE_EXTENDS :
					{
						if (isWordToken ) {
							currentCE.setExtendsStyleName(token);
						} else {
							if (!token.equals("{"))
								throwTPE(st.lineno(), token, "opening brace '{' or comma ',' expected");

							resetParserSyntaxTable(st, true);
							state = STATE_LEFTCURLY;
						}
						break;
					}
						
					case STATE_LEFTCURLY :
						{
							if (!isWordToken) {
								if (token.equals("}")) {
									// they want to close the entry
									resetParserSyntaxTable(st, false);
									state = STATE_RIGHTCURLY;
								} else if (token.equals(";")) {
									// no changed in state
								} else {
									throwTPE(st.lineno(), token, "attribute name expected");
								}
							} else {
								// start of a new Property
								currentPropertyName = token;
								state = STATE_ATTRNAME;
							}
							break;
						}
					case STATE_ATTRNAME :
						{
							if (currentPE == null)
								currentPE = new CssClassPropertyDecl();
							currentPE.setLineNo(st.lineno());
							if (isWordToken) {
								currentPropertyName += token;
								// allow indexed property names
							} else if (token.equals("[") || token.equals("]")) {
								currentPropertyName += token;
							} else {
								if (!token.equals(":"))
									throwTPE(st.lineno(), token, "colon ':' expected");
								state = STATE_COLON;
							}
							break;
						}
					case STATE_COLON :
						{
							if (! token.equals("{") && !isWordToken)
								throwTPE(st.lineno(), token, "attribute value expected");
							currentPE.setPropertyName(currentPropertyName);
							//
							// we have started a sub property with no target class name
							if (token.equals("{")) {
								// are we alredy inside another property
								CssClassPropertyDecl parentPE = peekProperty();
								if (parentPE != null) {
									parentPE.addProperty(currentPE);
								} else {
									currentCE.addProperty(currentPE);
								}
								pushProperty(currentPE);
								currentPE = null;
								state = STATE_LEFTCURLY;
							} else {
								currentPropertyValue = token;
								state = STATE_ATTRVAL;
							}
							break;
						}
					case STATE_ATTRVAL :
						{
							if (isWordToken) {
								currentPropertyValue += " " + token;
							} else {
								if (!token.equals(";") && !token.equals("{") && !token.equals("}"))
									throwTPE(st.lineno(), token, "semi colon ';', opening brace '{' or closing brace '}' expected");

								// we are starting a sub property but with a 
								// target class name
								if (token.equals("{")) {
									currentPE.setTargetClassName(currentPropertyValue);
									// are we alredy inside another property
									CssClassPropertyDecl parentPE = peekProperty();
									if (parentPE != null) {
										parentPE.addProperty(currentPE);
									} else {
										currentCE.addProperty(currentPE);
									}
									pushProperty(currentPE);
									currentPE = null;
									state = STATE_LEFTCURLY;
									
								} else {
									// must be ; or } which ends the property
									// save the attribute values
									currentPE.setPropertyValue(currentPropertyValue);
									
									CssClassPropertyDecl parentPE = peekProperty();
									if (parentPE != null) {
										parentPE.addProperty(currentPE);
									} else {
										currentCE.addProperty(currentPE);
									}
									currentPE = null;
									
									
									if (token.equals(";")) {
										state = STATE_SEMICOLON;
									} else if (token.equals("}")) {
										//
										// if we have a parentPE then we are nested in a property decl
										// otherwise its top level
										if (parentPE != null) {
											popProperty();
											state = STATE_SEMICOLON;
										} else {
											currentCE.setOrder(orderNum++);
											classEntryList.add(currentCE);
											currentCE = new CssClassDecl();
											resetParserSyntaxTable(st, false);
											state = STATE_RIGHTCURLY;
										}
									}
								}
							}
							break;
						}

					case STATE_SEMICOLON :
						{
							if (isWordToken) {
								// they have specified another attrname
								currentPropertyName = token;
								state = STATE_ATTRNAME;
							} else {
								// end of the entry
								if (!token.equals("}") && !token.equals(";"))
									throwTPE(st.lineno(), token, "closing brace '}' or semi colon ';' expected");

								if (token.equals("}")) {
									CssClassPropertyDecl parentPE = peekProperty();
									if (parentPE != null) {
										popProperty();
										state = STATE_SEMICOLON;
									} else {
										currentCE.setOrder(orderNum++);
										classEntryList.add(currentCE);
										currentCE = new CssClassDecl();
										resetParserSyntaxTable(st, false);
										state = STATE_RIGHTCURLY;
									}
								}
							}
							break;
						}
				}
				tt = st.nextToken();
			}
			// if we finished up without being in right curly then its no good
			if (state != STATE_RIGHTCURLY)
				throwTPE(st.lineno(), "", "closing brace '}' expected not end of file");

		} catch (IOException ioe) {
			int lineNo = -1;
			if (st != null)
				lineNo = st.lineno();
			throw new CssStyleSheetException(ioe.toString(), ioe, lineNo);
		}

		// fix up stuff at the end, like duplicate names etc...
		parseFixup();
	}
	
	
	/**
	 * A valid entry in the CSS is class, class, class.
	 * <p>
	 * These result in parsing is one StyleSheetClassEntry object with the 
	 * multiple class names separated by spaces.  We need to create 
	 * instances of StyleSheetClassEntry for each class and copy their 
	 * attributes across.
	 * <p>
	 * Also indexed properties need to be broken out and the names and
	 * indexes seperated
	 *
	 */
	private void parseFixup() {

		for (int j = 0; j < classEntryList.size(); j++) {
			CssClassDecl newEntry = null;
			CssClassDecl entry = (CssClassDecl) classEntryList.get(j);

			String classNames[] = split(entry.getClassName(), " ");
			if (classNames.length > 1) {
				entry.setClassName(classNames[0]);
				for (int i = 1; i < classNames.length; i++) {
					newEntry = (CssClassDecl) entry.clone();
					newEntry.setClassName(classNames[i]);
					classEntryList.add(newEntry);
				}
			}
		}

		// split out # style values if present
		for (int j = 0; j < classEntryList.size(); j++) {
			CssClassDecl entry = (CssClassDecl) classEntryList.get(j);
			String names[] = split(entry.getClassName(), "#");
			entry.setClassName(names[0]);
			if (names.length > 1)
				entry.setStyleName(names[1]);
		}
	}

	
	private void pushProperty(CssClassPropertyDecl attrEntry) {
		propertyStack.push(attrEntry);
	}

	private CssClassPropertyDecl popProperty() {
		return (propertyStack.isEmpty() ? null : (CssClassPropertyDecl) propertyStack.pop());
	}

	private CssClassPropertyDecl peekProperty() {
		return (propertyStack.isEmpty() ? null : (CssClassPropertyDecl) propertyStack.peek());
	}
	
	/**
	 * Returns an array of ClassEntry objects parsed.
	 * <p>
	 * Each one contains zero or more PropertyEntry objects.
	 */
	CssClassDecl[] getClassEntries() {
		return (CssClassDecl[]) classEntryList.toArray(new CssClassDecl[classEntryList.size()]);
	}
	
	/**
	 * Resets the tokenizer to reconize commas as words or not
	 */
	private static void resetParserSyntaxTable(StreamTokenizer st, boolean commasAreWords) {
		st.resetSyntax();

		st.whitespaceChars(0, ' ');
		st.whitespaceChars(160, 160);

		st.wordChars('0', 'z');

		st.ordinaryChars(33, 39);
		st.ordinaryChars(42, 43);
		st.ordinaryChars(45, 47);
		st.ordinaryChars(58, 64);
		st.ordinaryChars(91, 96);
		st.ordinaryChars(123, 127);

		st.wordChars('#', '#');
		st.wordChars('(', ')');
		st.wordChars('.', '.');
		st.wordChars('_', '_');
		st.wordChars('$', '$');
		st.wordChars('-', '-');
		st.wordChars('|', '|');
		st.wordChars('%', '%');
		st.wordChars('!', '!');

		if (commasAreWords)
			st.wordChars(',', ',');
		else
			st.ordinaryChars(',', ',');

		// These calls caused comments to be discarded
		st.eolIsSignificant(false);
		st.slashSlashComments(true);
		st.slashStarComments(true);
		st.quoteChar('"');
		st.quoteChar('\'');

	}
	/**
	 * Splits a string into parts based on a specified character
	 */
	private static String[] split(String s, String delimeters) {
		List list = new ArrayList();
		StringTokenizer st = new StringTokenizer(s, delimeters, false);
		while (st.hasMoreTokens()) {
			list.add(st.nextToken());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	/**
	 * internal function to throw a formatted StyleSheetException
	 */
	private void throwTPE(int lineNo, String token, String message) throws CssStyleSheetException {

		if (input != null) {
			try {
				input.close();
			} catch (IOException ieo) {}
		}
		input = null;

		String msg = "CSS stylesheet parse failure : " + message;
		if (token != null || token.length() > 0)
			msg += " instead found '" + token + "' line : " + lineNo;

		throw new CssStyleSheetException(msg, null, lineNo);
	}
}
