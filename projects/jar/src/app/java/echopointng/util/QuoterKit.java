package echopointng.util;

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
 * This class will quote string data
 */
public class QuoterKit {

	/** not instantiable */	
	private QuoterKit() {
	}
	
	/**
	 * This method will apply a "Java quote" (ie double slashes) 
	 * to a string with the given quote char
	 */
	public static String quoteJ(String s, char quoteChar) {
		StringBuffer sb = new StringBuffer(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == quoteChar) {
				sb.append("\\");
				sb.append(quoteChar);
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Quortes a string into HTML characters.  
	 * @param s - the string to quote into safe HTML
	 * @return - safe HTML or null if s is null
	 */
	public static String quoteHTML(String s) {
		if (s == null)
			return s;
		int slen = s.length();
		StringBuffer sb = new StringBuffer((int) (slen*1.5));
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
		        case '<':
		            sb.append("&lt;");
		            break;
		        case '>':
		            sb.append("&gt;");
		            break;
		        case '"':
		            sb.append("&quot;");
		            break;
		        case '&': {
		        	// if the next N chars are &xxx; then dont replace it
		        	if (checkForEntities(i,slen,s)) {
		        		sb.append(c);
		        		continue;
		        	}
	        		sb.append("&amp;");
		            break;
		        }
				default :
					sb.append(c);
					break;
			}
		}
		return sb.toString();
	}
	
	/**
	 * All the valid HTML entities according to W3CSchools :
	 * http://www.w3schools.com/html/html_entitiesref.asp
	 */
	private static String[] htmlEntities = {
			"&quot;",  	"&#34;",
			"&apos;", 	"&#39;",
			"&&amp;", 	"&#38;",
			"&lt;", 	"&#60;",
			"&gt;", 	"&#62;",
			"&nbsp;", 	"&#160;",
			"&iexcl;", 	"&#161;",
			"&curren;", "&#164;",
			"&cent;", 	"&#162;",
			"&pound;", 	"&#163;",
			"&yen;", 	"&#165;",
			"&brvbar;", "&#166;",
			"&sect;", 	"&#167;",
			"&uml;", 	"&#168;",
			"&copy;", 	"&#169;",
			"&ordf;", 	"&#170;",
			"&laquo;", 	"&#171;",
			"&not;", 	"&#172;",
			"&shy;", 	"&#173;",
			"&reg;", 	"&#174;",
			"&macr;", 	"&#175;",
			"&deg;", 	"&#176;",
			"&plusmn;", "&#177;",
			"&sup2;", 	"&#178;",
			"&sup3;", 	"&#179;",
			"&acute;", 	"&#180;",
			"&micro;", 	"&#181;",
			"&para;", 	"&#182;",
			"&middot;", "&#183;",
			"&cedil;", 	"&#184;",
			"&sup1;", 	"&#185;",
			"&ordm;", 	"&#186;",
			"&raquo;", 	"&#187;",
			"&frac14;", "&#188;",
			"&frac12;", "&#189;",
			"&frac34;", "&#190;",
			"&iquest;", "&#191;",
			"&times;", 	"&#215;",
			"&divide;", "&#247;",
			"&Agrave;", "&#192;",
			"&Aacute;", "&#193;",
			"&Acirc;", 	"&#194;",
			"&Atilde;", "&#195;",
			"&Auml;", 	"&#196;",
			"&Aring;", 	"&#197;",
			"&AElig;", 	"&#198;",
			"&Ccedil;", "&#199;",
			"&Egrave;", "&#200;",
			"&Eacute;", "&#201;",
			"&Ecirc;", 	"&#202;",
			"&Euml;", 	"&#203;",
			"&Igrave;", "&#204;",
			"&Iacute;", "&#205;",
			"&Icirc;", 	"&#206;",
			"&Iuml;", 	"&#207;",
			"&ETH;", 	"&#208;",
			"&Ntilde;", "&#209;",
			"&Ograve;", "&#210;",
			"&Oacute;", "&#211;",
			"&Ocirc;", 	"&#212;",
			"&Otilde;", "&#213;",
			"&Ouml;", 	"&#214;",
			"&Oslash;", "&#216;",
			"&Ugrave;", "&#217;",
			"&Uacute;", "&#218;",
			"&Ucirc;", 	"&#219;",
			"&Uuml;", 	"&#220;",
			"&Yacute;", "&#221;",
			"&THORN;", 	"&#222;",
			"&szlig;", 	"&#223;",
			"&agrave;", "&#224;",
			"&aacute;", "&#225;",
			"&acirc;", 	"&#226;",
			"&atilde;", "&#227;",
			"&auml;", 	"&#228;",
			"&aring;", 	"&#229;",
			"&aelig;", 	"&#230;",
			"&ccedil;", "&#231;",
			"&egrave;", "&#232;",
			"&eacute;", "&#233;",
			"&ecirc;", 	"&#234;",
			"&euml;", 	"&#235;",
			"&igrave;", "&#236;",
			"&iacute;", "&#237;",
			"&icirc;", 	"&#238;",
			"&iuml;", 	"&#239;",
			"&eth;", 	"&#240;",
			"&ntilde;", "&#241;",
			"&ograve;", "&#242;",
			"&oacute;", "&#243;",
			"&ocirc;", 	"&#244;",
			"&otilde;", "&#245;",
			"&ouml;", 	"&#246;",
			"&oslash;", "&#248;",
			"&ugrave;", "&#249;",
			"&uacute;", "&#250;",
			"&ucirc;", 	"&#251;",
			"&uuml;", 	"&#252;",
			"&yacute;", "&#253;",
			"&thorn;", 	"&#254;",
			"&yuml;", 	"&#255;",
			"&OElig;", 	"&#338;",
			"&oelig;", 	"&#339;",
			"&Scaron;", "&#352;",
			"&scaron;", "&#353;",
			"&Yuml;", 	"&#376;",
			"&circ;", 	"&#710;",
			"&tilde;", 	"&#732;",
			"&ensp;", 	"&#8194;",
			"&emsp;", 	"&#8195;",
			"&thinsp;", "&#8201;",
			"&zwnj;", 	"&#8204;",
			"&zwj;", 	"&#8205;",
			"&lrm;", 	"&#8206;",
			"&rlm;", 	"&#8207;",
			"&ndash;", 	"&#8211;",
			"&mdash;", 	"&#8212;",
			"&lsquo;", 	"&#8216;",
			"&rsquo;", 	"&#8217;",
			"&sbquo;", 	"&#8218;",
			"&ldquo;", 	"&#8220;",
			"&rdquo;", 	"&#8221;",
			"&bdquo;", 	"&#8222;",
			"&dagger;", "&#8224;",
			"&Dagger;", "&#8225;",
			"&hellip;", "&#8230;",
			"&permil;", "&#8240;",
			"&lsaquo;", "&#8249;",
			"&rsaquo;", "&#8250;",
			"&euro;", 	"&#8364;",
			"&trade;",	"&#8482;",
		};
	
	/*
	 * Check that the string does have any HTML entities
	 */
	private static boolean checkForEntities(int i, int slen, String s) {
		for (int j = 0; j < htmlEntities.length; j++) {
			if (checkFor(i,slen,s,htmlEntities[j]))
				return true;
		}
		return false;
	}
	
	/*
	 * Check that the string does have the HTML entity
	 */
	private static boolean checkFor(int i, int slen, String s, String checkFor) {
		int cflen = checkFor.length();
    	if (i <= (slen - cflen)) {
    		if (s.substring(i,i+cflen).equalsIgnoreCase(checkFor)) {
    			return true;
    		}
    	}
		return false;
	}
}
