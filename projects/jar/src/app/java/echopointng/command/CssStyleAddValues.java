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

import nextapp.echo2.app.Command;
import nextapp.echo2.app.Component;

/**
 * <code>CssStyleAddValues</code> allows form an arbitary CSS style values to
 * be added to a component on the client browser.
 * <p>
 * If a given CSS value currently exists, it is replaced, otherwise it is
 * appended.
 * <p>
 * For example given the XHTML declaration :
 * 
 * <pre>
 * 
 *  
 *   		&lt;div style=&quot;background-color:red&quot; &gt;...&lt;/div&gt; 
 *   
 *  
 * </pre>
 * 
 * The
 * <code>new CssStyleAddValues("background-color:blue;font-size:8pt",myComponent)</code>
 * will result in the new XHTML declaration of :
 * 
 * <pre>
 * 
 *  
 *   		&lt;div style=&quot;background-color:blue;font-size:8pt;&quot; &gt;...&lt;/div&gt; 
 *   
 *  
 * </pre>
 *  
 */

public class CssStyleAddValues implements Command {

	private String styleText;

	private Component targetComponent;

	/**
	 * Constructs a <code>CssStyleAddValues</code> command with the text of a
	 * CSS style declaration and the component to apply it to. For example given
	 * the XHTML declaration :
	 * 
	 * <pre>
	 * 
	 *  
	 *   		&lt;div style=&quot;background-color:red&quot; &gt;...&lt;/div&gt; 
	 *   
	 *  
	 * </pre>
	 * 
	 * Then
	 * <code>new CssStyleAddValues("background-color:blue;font-size:8pt",myComponent)</code>
	 * will result in the new XHTML declaration of :
	 * 
	 * <pre>
	 * 
	 *  
	 *   		&lt;div style=&quot;background-color:blue;font-size:8pt;&quot; &gt;...&lt;/div&gt; 
	 *   
	 *  
	 * </pre>
	 * 
	 * @param styleText -
	 *            the text of the style declarartion
	 * @param targetComponent -
	 *            the target component to apply the style values to
	 */
	public CssStyleAddValues(String styleText, Component targetComponent) {
		super();
		this.styleText = styleText;
		this.targetComponent = targetComponent;
		if (targetComponent ==  null) {
			throw new IllegalArgumentException("You must provide a target component.");
		}
	}

	/**
	 * @return Returns the CSS style declaration text
	 */
	public String getStyleText() {
		return styleText;
	}

	/**
	 * @return Returns the targetComponent.
	 */
	public Component getTargetComponent() {
		return targetComponent;
	}

}
