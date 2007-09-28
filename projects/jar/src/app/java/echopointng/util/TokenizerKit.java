package echopointng.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The TokenizerKit class is useful for break a String down 
 * into an array of "Token" strings. This class is more powerful 
 * than the standard java.util.StringTokenizer.
 */
public class TokenizerKit {

	/** not instantiable */
	private TokenizerKit() {
	}

	/**
	 * Tokenizes a string into an array of Strings based on the delimeters while 
	 * respecting the quote characters ' and " as well defining 
	 * whitespace characters 0 to 32 as delimeters.
	 * 
	 * @param str - the string to tokenize
	 * @param delims - the string of allowable delimeter characters
	 * @return a String[] of tokens
	 */
	public static String[] tokenize(String str, String delims) {
		return doTokenize(str,delims,0,32,33,0xFF,true, false, false);
	}

	/**
	 * Tokenizes a string into an array of Strings based on the delimeters while 
	 * respecting the quote characters ' and " but the common whitespace characters 
	 * space, tab, carriage return and newline are no longer whitespace.
	 * 
	 * @param str - the string to tokenize
	 * @param delims - the string of allowable delimeter characters
	 * @return a String[] of tokens
	 */
	public static String[] tokenizeStrict(String str, String delims) {
		return doTokenize(str,delims,0,32,33,0xFF,true,true,false);
	}
	
	/**
	 * Tokenizes a string into an array of Strings based on the delimeters while 
	 * respecting the quote characters ' and " as well defining 
	 * whitespace characters 0 to 32 as delimeters.
	 * 
	 * @param str - the string to tokenize
	 * @param delims - the string of allowable delimeter characters
	 * @param returnDelimeters - if true then delimeters will be returned in the tokens array
	 * @return a String[] of tokens
	 */
	public static String[] tokenize(String str, String delims, boolean returnDelimeters) {
		return doTokenize(str,delims,0,32,33,0xFF,true, false, returnDelimeters);
	}

	/**
	 * Tokenizes a string into an array of Strings based on the delimeters while 
	 * respecting the quote characters ' and " but the common whitespace characters 
	 * space, tab, carriage return and newline are no longer whitespace.
	 * 
	 * @param str - the string to tokenize
	 * @param delims - the string of allowable delimeter characters
	 * @param returnDelimeters - if true then delimeters will be returned in the tokens array
	 * @return a String[] of tokens
	 */
	public static String[] tokenizeStrict(String str, String delims, boolean returnDelimeters) {
		return doTokenize(str,delims,0,32,33,0xFF,true,true,returnDelimeters);
	}	
	
	/**
	 * This will split the given string into a series of lines (with no delimeters in the lines themselves)
	 * 
	 * @param str - the String to split into lines
	 * @return a String[] of lines
	 */
	public static String[] splitIntoLines(String str) {
		List strList = new ArrayList();
		StringReader sr = new StringReader(str);
		BufferedReader br = new BufferedReader(sr);
		try {
			String line = br.readLine();
			while (line != null) {
				strList.add(line);
				line = br.readLine();
			}
		} catch (IOException e) {
			// cant happen on StringWritter
		}
		return (String[]) strList.toArray(new String[strList.size()]);
		
	}
	
	/** Do our tokenisation */
	private static String[] doTokenize(String str, String delims, int loWhiteSpace, int hiWhiteSpace, int loWordChar, int hiWordChar, boolean useQuotes, boolean noCommonWhiteSpace, boolean returnDelims) {
		try {
			java.util.ArrayList tokenList = new java.util.ArrayList();
			java.io.StringReader sr = new java.io.StringReader(str);
			java.io.StreamTokenizer st = new java.io.StreamTokenizer(sr);

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

			String token = "";
			int tt = st.nextToken();
			while (tt != java.io.StreamTokenizer.TT_EOF) {
				token = null;

				switch (tt) {
					case java.io.StreamTokenizer.TT_WORD :
						// A word was found; the value is in sval
						token = st.sval;
						break;
					case '"' :
						// A double-quoted string was found; sval contains the contents
						token = st.sval;
						break;
					case '\'' :
						// A single-quoted string was found; sval contains the contents
						token = st.sval;
						break;
					case java.io.StreamTokenizer.TT_EOF :
						// End of file has been reached
						break;
					default :
						// A regular character was found; the value is the token itself
						if (returnDelims) {
							token = new String(new char[] {(char)tt});
						} else {
							token = null;
						}
							
						break;
				}
				if (token != null)
					tokenList.add(token);
				tt = st.nextToken();
			}
			return (String[]) tokenList.toArray(new String[tokenList.size()]);

		} catch (java.io.IOException ieo) {
			return new String[0];
		}
	}
}
