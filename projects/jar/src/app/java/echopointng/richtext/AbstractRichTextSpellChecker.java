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

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>AbstractRichTextSpellChecker</code> can be used as basis for building
 * more complex spell checkers. It provides a parsing mechanbism for break text
 * buffers into a series of words.
 * <p>
 * The derived class must then provide the checkWords() functionality.
 */

public abstract class AbstractRichTextSpellChecker implements RichTextSpellChecker {

	private class CountedReader extends CharArrayReader {

		public CountedReader(char buf[]) {
			super(buf);
		}

		public int getPos() {
			return pos;
		}
	}

	private class SpellCheckerWordImpl implements SpellCheckerWord {
		private int startIndex;
		private int endIndex;
		private String word;
		
		private SpellCheckerWordImpl(String textBuffer, int startIndex, int endIndex) {
			this.endIndex = endIndex;
			this.startIndex = startIndex;
			this.word = textBuffer.substring(startIndex,endIndex);
		}
		public int getStartIndex() {
			return startIndex;
		}
		public int getEndIndex() {
			return endIndex;
		}
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer(word);
			sb.append(" @ si:");
			sb.append(startIndex);
			sb.append(" ei:");
			sb.append(endIndex);
			return sb.toString();
			
}
	}
	/**
	 * @see echopointng.richtext.RichTextSpellChecker#parseWords(java.lang.String)
	 */
	public SpellCheckerWord[] parseWords(String textBuffer) {
		List wordList = new ArrayList();
		final CountedReader cr = new CountedReader(textBuffer.toCharArray());
		StreamTokenizer st = new StreamTokenizer(cr);

		int lastPos = 0;
		int tt = 0;
		try {
			tt = st.nextToken();
			while (tt != java.io.StreamTokenizer.TT_EOF) {
				String token = "";
				switch (tt) {
				case java.io.StreamTokenizer.TT_WORD:
					// A word was found; the value is in sval
					token = st.sval;
					break;
				case java.io.StreamTokenizer.TT_EOF:
					// End of file has been reached
					break;
				default:
					// A regular character was found; the value is the token
					// itself
					token = new String(new char[] { (char) tt });
					break;
				}
				String generalWord = textBuffer.substring(lastPos,cr.getPos());
				
				final int startIndex = lastPos + generalWord.indexOf(token);
				final int endIndex = startIndex + token.length();
				
				SpellCheckerWord word = new SpellCheckerWordImpl(textBuffer,startIndex,endIndex);
				wordList.add(word);
				
				// bump our position
				lastPos = cr.getPos();
				
				tt = st.nextToken();				
			}
		} catch (IOException e) {
		}
		return (SpellCheckerWord[]) wordList.toArray(new SpellCheckerWord[wordList.size()]);
	}
}
