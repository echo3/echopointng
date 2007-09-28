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

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>CssObjectDeclarationParser</code> takes CSS object declarations
 * and parse them into their value parts.  A CSS object declaration
 * is defined as
 * <pre>
 * cssobjectdecl  ::= &lt;identifier&gt; '(' &lt;cssobjectvaleq&gt; ')'
 * 
 * cssobjectvaleq ::=	&lt;cssobjectval&gt; { "," cssobjectval }
 * 
 * cssobjectval   ::=   &lt;cssobjectdecl&gt; ||
 *                      &lt;anystring&gt;  
 * </pre>
 * 
 * An CSS object declaration can contain child declarations as in
 * <pre>
 *       fillimage(image('imageurl',12px,12px),repeat);
 * </pre>
 * This parser only returns the top level values of the outer
 * CSS object declarations.  You will need to "reparse" child
 * declarations as required.  So in the above example the parser
 * will return the strings :
 * <pre>
 *       "fillimage" "image('imageurl',12px,12px)" "repeat"
 * </pre>
 * The object type name will always be in the first array element.  In 
 * the above example "fillimage" is the object type name.
 *  
 */
public class CssObjectDeclarationParser {
	
	/**
	 * This will parse an CSS object declaration into its top level
	 * string components.  Any child CSS object declarations will be left
	 * in place as a string value, ready for further parsing.
	 * 
	 * If it cannot be parsed, then an empty String[] will be returned.  A
	 * valid CSS object declaration will always have at least one token
	 * which is the object type name.
	 *  
	 * @param objDecl - the CSS object declaration string to be parsed.
	 * @return an array of the values that make up the CSS object declaration
	 * 		or an empty String[] if its cant be parsed.
	 */
	public static String[] parse(String objDecl) {
		return tokenize(objDecl);
	}
	
	/**
	 * Returns true if the objDecl string is in fact in the correct
	 * for an CSS object declaration.
	 * 
	 * @param objDecl - the string to test
	 * @return true if its a CSS object declaration
	 */
	public static boolean isCssObjectDeclaration(String objDecl) {
		return tokenize(objDecl).length > 0;
	}
	
	private static boolean isIdentifier(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (i == 0) {
				if (! Character.isJavaIdentifierStart(c))
					return false;
			} else {
				if (! Character.isJavaIdentifierPart(c))
					return false;
			}
		}
		return true;
	}
	
	private static String[] tokenize(String str) {
		return doTokenize(str,"(),",0,32,33,0xFF,true, false);
	}

	private static final String STATE_START   = "STATE_START";
	private static final String STATE_END   = "STATE_END";
	private static final String STATE_OBJNAME = "STATE_OBJNAME";
	private static final String STATE_OB = "STATE_OB";
	private static final String STATE_VAL = "STATE_VAL";
	private static final String STATE_INNEROBJ = "STATE_INNEROBJ";
	
	private static String[] doTokenize(String str, String delims, int loWhiteSpace, int hiWhiteSpace, int loWordChar, int hiWordChar, boolean useQuotes, boolean noCommonWhiteSpace) {
		try {
			List tokenList = new ArrayList();
			StringReader sr = new StringReader(str);
			StreamTokenizer st = new StreamTokenizer(sr);

			st.resetSyntax();
			st.whitespaceChars(loWhiteSpace, hiWhiteSpace);
			st.wordChars(loWordChar, hiWordChar);
			for (int i = 0; i < delims.length(); i++) {
				st.ordinaryChar(delims.charAt(i));
			}
			if (useQuotes) {
				st.quoteChar('\'');
				st.quoteChar('"');
			}
			if (noCommonWhiteSpace) {
				st.wordChars(' ',' ');
				st.wordChars('\t','\t');
				st.wordChars('\n','\n');
				st.wordChars('\r','\r');
			}
			
			int tt = st.nextToken();
			int braceCount = 0;
			String state = STATE_START;
			String prevState = STATE_START;
			String currentVal = "";
			String token = "";
			while (tt != java.io.StreamTokenizer.TT_EOF) {
				token = null;

				boolean isWordToken = true;
				switch (tt) {
					case java.io.StreamTokenizer.TT_WORD :
						// A word was found; the value is in sval
						token = st.sval;
						break;
					case '"' :
						// A double-quoted string was found; sval contains the contents
						if (state == STATE_INNEROBJ) {
							// we dont want quoting when in an inner object
							token = "\"" + st.sval + "\"" ;
						} else {
							token = st.sval;
						}
						break;
					case '\'' :
						// A single-quoted string was found; sval contains the contents
						if (state == STATE_INNEROBJ) {
							// we dont want quoting when in an inner object
							token = "'" + st.sval + "'" ;
						} else {
							token = st.sval;
						}
						break;
					case java.io.StreamTokenizer.TT_EOF :
						// End of file has been reached
						break;
					default :
						// A regular character was found; the value is the token itself
						token = new String(new char[] {(char)tt});
						isWordToken = false;
						break;
				}
				// ======
				// WHAT STATE ARE WE IN
				// ======
				//	STATE_OBJNAME
				if (state == STATE_START) {
					
					if (! isWordToken )
						throw new RuntimeException("Expecting obj decl name");
					if (! isIdentifier(token))
						throw new RuntimeException("obj name must be a java identifier");
						
					tokenList.add(token);
					currentVal = "";
					prevState = state;
					state = STATE_OBJNAME;
					
				//	STATE_OBJNAME
				} else if (state == STATE_OBJNAME) {
					if (! token.equals("("))
						throw new RuntimeException("Expecting ( after obj decl name");
					prevState = state;
					state = STATE_OB;
					
				//	STATE_OB
				} else if (state == STATE_OB) {
					if (token.equals(")")) {
						if (prevState != STATE_OBJNAME)
							tokenList.add(currentVal);
						prevState = state;
						state = STATE_END;
						continue;
					} else if (token.equals(",")) {
						// empty value
						tokenList.add("");
						currentVal = "";
						prevState = state;
						state = STATE_OB;
					} else {
						if (! isWordToken)
							throw new RuntimeException("Expecting a obj value here");
						currentVal = token;
						prevState = state;
						state = STATE_VAL;
					}
				
				//	STATE_VAL
				} else if (state == STATE_VAL) {
					if (token.equals("(")) {
						// start of an inner object
						currentVal += token;
						braceCount++;
						prevState = state;
						state = STATE_INNEROBJ;
					} else if (token.equals(",")) {
						tokenList.add(currentVal);
						currentVal = "";
						prevState = state;
						state = STATE_OB;
					} else if (token.equals(")")) {
						// the logical end of stream
						tokenList.add(currentVal);
						prevState = state;
						state = STATE_END;
					} else {
						throw new RuntimeException("Unexpected input" + token);
					}
				//	STATE_INNEROBJ
				} else if (state == STATE_INNEROBJ) {
					//	spin around till we hit ')'
					if (token.equals(")")) {
						braceCount--;
						currentVal += token;
						if (braceCount == 0) {
							// end of an inner object
							prevState = state;
							state = STATE_VAL;
						}
					} else {
						// ooh another embedded bracket obj
						if (token.equals("(")) {
							braceCount++;
						}
						currentVal += token;
					}
				// STATE_END
				} else if (state == STATE_END) {
					break;
				}
				tt = st.nextToken();
			}
			if (state != STATE_END)
				return new String[0];
			else
				return (String[]) tokenList.toArray(new String[tokenList.size()]);

		} catch (Exception e) {
			return new String[0];
		}
	}
}