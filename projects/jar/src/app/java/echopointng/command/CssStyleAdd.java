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
package echopointng.command;

/**
 * <code>CssStyleAdd</code> allows form an arbitary style declaration to be
 * added to the current application.
 * <p>
 * This <code>Command</code> is generated with a unique identifier that can be
 * used later to remove the style declaration via the
 * <code>CssStyleRemove</code> Command.
 * <p>
 * The style declrations are added as &lt;STYLE&gt;&lt;/STYLE&gt; into the &lt;HEAD&gt; of
 * the client browser doucment.
 */

public class CssStyleAdd extends CssStyleDecl {

	private String styleText;

	/**
	 * Constructs a <code>CssStyleAdd</code> command with the text of a CSS
	 * style declaration such as:
	 * 
	 * <pre>
	 * 
	 *  
	 *    div.whizoStyle {
	 *   		background-color : #acbdcd;
	 *    }
	 *  
	 *    div.frizoStyle {
	 *   		background-color : #c0ffee;
	 *    }
	 *  
	 *  
	 * </pre>
	 * 
	 * @param styleText -
	 *            the text of the style declarartion
	 */
	public CssStyleAdd(String styleText) {
		super();
		this.styleText = styleText;
	}

	/**
	 * Constructs a <code>CssStyleAdd</code> command with the text of a CSS
	 * style declaration and the media it applies to such as:
	 * 
	 * <pre>
	 * 
	 *  
	 *    div.whizoStyle {
	 *   		background-color : #acbdcd;
	 *    }
	 *  
	 *    div.frizoStyle {
	 *   		background-color : #c0ffee;
	 *    }
	 *  
	 *  
	 * </pre>
	 * 
	 * @param styleText -
	 *            the text of the style declarartion
	 * @param media -
	 *            the media the style declration applies to.
	 * 
	 * @see CssStyleDecl#setMedia(String)
	 */
	public CssStyleAdd(String styleText, String media) {
		super();
		this.styleText = styleText;
		setMedia(media);
	}

	/**
	 * @return Returns the CSS style declaration text
	 */
	public String getStyleText() {
		return styleText;
	}

}
