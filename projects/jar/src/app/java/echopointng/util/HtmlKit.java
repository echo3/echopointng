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

/*
 * This file was taken from the Echo 1.x code base on the 23rd Jan 2006 and made part of
 * EchoPointNG.  It is therefore Copyright (C) 2002-2004 NextApp, Inc.
 */

/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2004 NextApp, Inc.
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

package echopointng.util;

import java.util.ArrayList;
import java.util.List;

import nextapp.echo2.app.ImageReference;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import echopointng.ui.util.ImageManager;

/**
 * Encodes text strings into HTML.
 */
public class HtmlKit {

	public static final int NEWLINE_REMOVE = 3;

	public static final int NEWLINE_TO_BR = 2;

	public static final int NEWLINE_TO_SPACE = 1;

	public static final int NEWLINE_TRANSPARENT = 0;

	/**
	 * Encodes a string of text into HTML by replacing less than, greater than,
	 * ampersand, and quotation mark characters with their HTML escaped
	 * equivalents. Groups of spaces are replaced with alternating spaces and
	 * non-breaking space characters.
	 * 
	 * @param text
	 *            The text to be encoded into HTML.
	 * @return The text, converted to HTML. Newlines will be left intact, NOT
	 *         translated to &lt;br&gt; tags.
	 */
	public static String encode(String text) {
		return encode(text, NEWLINE_TRANSPARENT);
	}
	/**
	 * Encodes a string of text into HTML by replacing less than, greater than,
	 * ampersand, and quotation mark characters with their HTML escaped
	 * equivalents. Groups of spaces are replaced with alternating spaces and
	 * non-breaking space characters.
	 * 
	 * @param text
	 *            The text to be encoded into HTML.
	 * @param newLinePolicy
	 *            The policy for handling newline characters, one of the
	 *            following values:
	 *            <ul>
	 *            <li>NEWLINE_TRANSPARENT - Leave newline characters intact.</li>
	 *            <li>NEWLINE_TO_SPACE - Convert newlines to spaces.</li>
	 *            <li>NEWLINE_TO_BR - Convert newlines to &lt;br&gt; tags.</li>
	 *            <li>NEWLINE_REMOVE - Remove newlines entirely.</li>
	 *            </ul>
	 * @return The text, converted to HTML.
	 */
	public static String encode(String text, int newLinePolicy) {
		StringBuffer sb = new StringBuffer();
		
		final String quot 	= "&quot;"; 	// String.valueOf((char) 0x0022); // why can I do "\u0022"
		final String amp 	= "&amp;";  	//"\u0026"; 
		final String lt 	= "&lt;"; 		//"\u003C";
		final String gt 	= "&gt;"; 		//"\u003E";
		final String nbsp 	= "&nbsp;"; 	//"\u00a0"; 
		

		if (text != null) {
			int length = text.length();
			int startIndex = 0;
			int index = 0;

			while (index < length) {
				switch (text.charAt(index)) {
				case ' ':
					if (index == 0 || index == length - 1) {
						// Convert spaces at beginning or end of text string to
						// NBSPs.
						if (startIndex < index) {
							sb.append(text.substring(startIndex, index));
						}
						sb.append(nbsp);
						startIndex = index + 1;
					} else if (index < length - 1 && text.charAt(index + 1) == ' ') {
						// Break down blocks of spaces in text so that text is
						// formatted correctly.
						// This code, while moderately confusing is necessary.
						// The rule is that non-breaking space (NBSP)
						// characters should replace every other space in a
						// continued string of spaces. The exception is
						// that a NBSP should never immediately precede a
						// character.
						if (startIndex < index) {
							sb.append(text.substring(startIndex, index));
						}
						if (index < length - 3 && text.charAt(index + 2) == ' ' && text.charAt(index + 3) != ' ') {
							sb.append(nbsp);
							sb.append(nbsp);
							sb.append(" ");
							index += 2; // skip second and third space.
						} else {
							sb.append(nbsp);
							sb.append(" ");
							++index; // skip second space.
						}
						startIndex = index + 1;
					}
					break;
				case '\r':
				case '\n':
					if (newLinePolicy != NEWLINE_TRANSPARENT) {
						if (startIndex < index) {
							sb.append(text.substring(startIndex, index));
						}
						if (index < length - 1
								&& ((text.charAt(index) == '\r' && text.charAt(index + 1) == '\n') || (text.charAt(index) == '\n' && text
										.charAt(index + 1) == '\r'))) {
							++index; // skip second character of newline
							// sequence.
						}
						switch (newLinePolicy) {
						case NEWLINE_TO_SPACE:
							sb.append(" ");
							break;
						case NEWLINE_TO_BR:
							sb.append("<br/>\n");
							break;
						// NEWLINE_REMOVE will not add any characters to output.
						}
						startIndex = index + 1;
					}
					break;
				case '<':
					if (startIndex < index) {
						sb.append(text.substring(startIndex, index));
					}
					sb.append(lt);
					startIndex = index + 1;
					break;
				case '>':
					if (startIndex < index) {
						sb.append(text.substring(startIndex, index));
					}
					sb.append(gt);
					startIndex = index + 1;
					break;
				case '"':
					if (startIndex < index) {
						sb.append(text.substring(startIndex, index));
					}
					sb.append(quot);
					startIndex = index + 1;
					break;
				case '&':
					if (startIndex < index) {
						sb.append(text.substring(startIndex, index));
					}
					sb.append(amp);
					startIndex = index + 1;
					break;
				}

				++index;
			}

			if (startIndex < index) {
				sb.append(text.substring(startIndex));
			}
		}

		return sb.toString();
	}

	/**
	 * Encodes a string of text into DOM Nodes by taking new lines and replacing
	 * them aoccording to the policy provided.
	 * 
	 * @param text
	 *            The text to be encoded into DOM Nodes.
	 * @param newLinePolicy
	 *            The policy for handling newline characters, one of the
	 *            following values:
	 *            <ul>
	 *            <li>NEWLINE_TRANSPARENT - Leave newline characters intact.</li>
	 *            <li>NEWLINE_TO_SPACE - Convert newlines to spaces.</li>
	 *            <li>NEWLINE_TO_BR - Convert newlines to &lt;br&gt; tags.</li>
	 *            <li>NEWLINE_REMOVE - Remove newlines entirely.</li>
	 *            </ul>
	 * @return The text, converted to an array of DOM Nodes.
	 */
	public static Node[] encodeNewLines(Document document, String text, int newLinePolicy) {
		List nodeList = new ArrayList();
		StringBuffer sb = new StringBuffer();
		int length = text.length();
		int startIndex = 0;
		int index = 0;

		while (index < length) {
			switch (text.charAt(index)) {
			case '\r':
			case '\n':
				if (newLinePolicy != NEWLINE_TRANSPARENT) {
					if (startIndex < index) {
						sb.append(text.substring(startIndex, index));
					}
					if (index < length - 1
							&& ((text.charAt(index) == '\r' && text.charAt(index + 1) == '\n') || (text.charAt(index) == '\n' && text
									.charAt(index + 1) == '\r'))) {
						++index; // skip second character of newline
						// sequence.
					}
					switch (newLinePolicy) {
					case NEWLINE_TO_SPACE:
						sb.append(" ");
						break;
					case NEWLINE_TO_BR:
						Node node = document.createTextNode(sb.toString());
						nodeList.add(node);
						node = document.createElement("br");
						nodeList.add(node);
						sb.setLength(0);
						break;
					// NEWLINE_REMOVE will not add any characters to output.
					}
					startIndex = index + 1;
				}
				break;
			}
			++index;
		}
		if (startIndex < index) {
			sb.append(text.substring(startIndex));
			Node node = document.createTextNode(sb.toString());
			nodeList.add(node);
		}
		return (Node[]) nodeList.toArray(new Node[nodeList.size()]);
	}

	/**
	 * This returns the URI for the given <code>ImageReference</code>
	 * 
	 * @param imageReference -
	 *            the <code>ImageReference</code> to get a URI for
	 * @return the URI for the given <code>ImageReference</code>
	 * @throws IllegalArgumentException -
	 *             if the <code>ImageReference</code> is null
	 */
	public static String getImageURI(ImageReference imageReference) {
		if (imageReference == null) {
			throw new IllegalArgumentException("The imageReference must not be null!");
		}
		return ImageManager.getURI(imageReference);
	}

	/** Non-instantiable class. */
	private HtmlKit() {
	}
}
