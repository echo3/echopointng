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
package echopointng.richtext;

/** 
 * <code>RichTextSpellChecker</code> is an interface used by a <code>RichTextArea</code> to 
 * spell check the words in it. 
 */
public interface RichTextSpellChecker {
	
	/**
	 * <code>SpellCheckerWord</code> is used to indicate the words contained
	 * within a block of text.  This allows different rules for punctuation etc..
	 * <p>
	 * The start and end index follow the same sematics as <code>String.substring(start,end);</code>
	 */
	public interface SpellCheckerWord {
		
		/**
		 * The starting index of the word within the text buffer, inclusive.
		 * 
		 * @return The starting index of the word within the text buffer.
		 */
		public int getStartIndex();
		
		/**
		 * The ending index of the word within the text buffer, exclusive
		 * 
		 * @return The ending index of the word within the text buffer.
		 */
		public int getEndIndex();
	}

	/**
	 * This method is called to break a text buffer into a series of words.  Characters
	 * within the text buffer can be overlook for spelling purposes for example.  This
	 * method allows different rules to be applied by the spell check on what consitutues a 
	 * word to be spell checked.
	 * <p>
	 * This method may be called with all whitespace or punctuation.  The implementor must take care 
	 * to ensure its word parsing takes tis into account.
	 * 
	 * @param textBuffer - the text buffer that may contain zero or more words.
	 * @return a non null buffer of SpellCheckWords for the passed in text buffer
	 */
	public SpellCheckerWord[] parseWords(String textBuffer);
	
	/**
	 * This method is called for every word in the <code>RichTextArea</code>.  The implementor
	 * is expected to return the following :
	 * <ul>
	 * <li>If the word is spelled correctly it should return <code>null</code>.</li> 
	 * <li>If the word is spelled incorrectly, but no alternative words are known, then it
	 * 			should return a zero length array.</li> 
	 * <li>If the word is spelled incorrectly and alternative words are known, then it
	 * 			should return an array of alternative words.</li> 
	 * </ul>
	 * 
	 * @param 
	 * 		word - the word to check.  Garunteed to be non null, and length > 0
	 * 
	 * @return 
	 * 		an array of spell checking alternatives for the word, a zero length array if the
	 * 		word is not known but no alternatives are available and null if the word
	 * 		is not incorrect. 
	 */
	public String[] checkWord(String word);
}
