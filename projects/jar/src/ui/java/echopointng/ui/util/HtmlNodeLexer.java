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
package echopointng.ui.util;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
 * <code>HtmlNodeLexer</code> will lex a piece of HTML text and
 * callback out when a TextNode or Element is encountered.  
 * <p>
 * Remember that each text node may contain whitespace and may consist of 
 * multiple words.   Also a Element node consists of 1 or 2 events, the start tag and 
 * the end tag or a end tag only.
 */  
public class HtmlNodeLexer {
	
	/**
	 * The <code>HtmlLexerCallBack</code> interface is used as a callback mechanism
	 * when a specific type of HTML node is encountered.
	 */
	public interface HtmlLexerCallBack {
		/**
		 * Called then a textNode has been fully lexed.  The callee can
		 * modify the text node and pass back a new text node value.
		 * 
		 * @param textNode - the StringBuffer of text
		 * @return the possibly modified text node
		 */
		public StringBuffer onTextNode(StringBuffer textNode);
		/**
		 * Called then an Element has been fully lexed.  The callee can
		 * modify the Element node and pass back a new Element node value.
		 * 
		 * @param element - a StringBuffer of the Element
		 * @return the possibly modified Element
		 */
		public StringBuffer onElementNode(StringBuffer element);
		/**
		 * Called then an Comment has been fully lexed.  The callee can
		 * modify the Comment node and pass back a new Comment node value.
		 * 
		 * @param comment - a StringBuffer of the Comment
		 * @return the possibly modified Comment
		 */
		public StringBuffer onCommentNode(StringBuffer comment);
	}
	
	public static String lex(String htmlText, HtmlLexerCallBack callBack) {
		return doTokenize(htmlText,callBack,"<>",0,0,1,0xFF,false,false);
	}
	
	private static final String STATE_START  	 	= "STATE_START";
	private static final String STATE_ELEMENT 		= "STATE_ELEMENT";
	private static final String STATE_COMMENT		= "STATE_COMMENT";
	private static final String STATE_TEXT			= "STATE_TEXT";
	
	private static String doTokenize(String str, HtmlLexerCallBack callBack, String delims, int loWhiteSpace, int hiWhiteSpace, int loWordChar, int hiWordChar, boolean useQuotes, boolean noCommonWhiteSpace) {
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
		try {
			return doTokenizeInternal(st,callBack);
		} catch (Exception e) {
			return str;
		}
	}
		
	private static String doTokenizeInternal(StreamTokenizer st, HtmlLexerCallBack callBack) throws IOException {
			StringBuffer sbTextNode = null;
			StringBuffer sbElement = null;
			StringBuffer sbComment = null;
			String state = STATE_START;
			String token = "";
			String prevToken = "";
			StringBuffer sbOut = new StringBuffer();


			int tt = st.nextToken();
			while (tt != java.io.StreamTokenizer.TT_EOF) {
				prevToken = token;
				token = "";
				switch (tt) {
					case java.io.StreamTokenizer.TT_WORD :
						// A word was found; the value is in sval
						token = st.sval;
						break;
					case java.io.StreamTokenizer.TT_EOF :
						// End of file has been reached
						break;
					default :
						// A regular character was found; the value is the token itself
						token = new String(new char[] {(char)tt});
						break;
				}
				// ======
				// WHAT STATE ARE WE IN
				// ======
				// STATE_START
				if (state == STATE_START) {
					if (token.equals("<")) {
						state = STATE_ELEMENT;
						sbElement = new StringBuffer(token);
					} else {
						state = STATE_TEXT;
						sbTextNode = new StringBuffer(token);
					}
				//	STATE_ELEMENT
				} else if (state == STATE_ELEMENT) {
					if (prevToken.equals("<") && token.indexOf("!--") == 0) {
						sbComment = new StringBuffer();
						// what we though was an element in in fact a comment
						sbComment.append(sbElement); 
						sbComment.append(token);
						sbElement = null;
						state = STATE_COMMENT; 
					}
					else if (token.equals(">")) {
						sbElement.append(token);
						StringBuffer newElementBuffer = nvl(callBack.onElementNode(sbElement));
						sbOut.append(newElementBuffer);
						sbElement = null;
						
						state = STATE_TEXT;
						sbTextNode = new StringBuffer();
					} else {
						sbElement.append(token);
					}
				//	STATE_COMMENT
				} else if (state == STATE_COMMENT) {
					if (token.equals(">")) {
						sbComment.append(token);
						int endIndex = prevToken.lastIndexOf("--"); 
						if (endIndex == prevToken.length()-2) {
							// callback for a lexed comment
							StringBuffer newCommentBuffer = nvl(callBack.onCommentNode(sbComment));
							sbOut.append(newCommentBuffer);
							sbComment = null;

							state = STATE_TEXT;
							sbTextNode = new StringBuffer();
						}
					} else  {
						sbComment.append(token);
					}

				//	STATE_TEXT
				} else if (state == STATE_TEXT) {
					if (token.equals("<")) {
						// call backout to callback
						if (sbTextNode.length() > 0) {
							StringBuffer newTextBuffer = nvl(callBack.onTextNode(sbTextNode));
							sbOut.append(newTextBuffer);
						}
						sbTextNode = null;
						sbElement = new StringBuffer(token);
						state = STATE_ELEMENT;
					} else  {
						sbTextNode.append(token);
					}
				}
				tt = st.nextToken();
			}
			// just in case we finished on a node
			if (sbTextNode != null && sbTextNode.length() > 0) {
				sbTextNode = nvl(callBack.onTextNode(sbTextNode));
				sbOut.append(sbTextNode);
			}
			if (sbElement != null && sbElement.length() > 0) {
				sbElement = nvl(callBack.onElementNode(sbElement));
				sbOut.append(sbElement);
			}
			if (sbComment != null && sbComment.length() > 0) {
				sbComment = nvl(callBack.onCommentNode(sbComment));
				sbOut.append(sbComment);
			}
			return sbOut.toString();
	}
	
	private static StringBuffer nvl(StringBuffer sb) {
		return (sb == null ? new StringBuffer() : sb);
	}
	
}