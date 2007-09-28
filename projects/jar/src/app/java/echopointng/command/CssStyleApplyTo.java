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
 * <code>CssStyleApplyTo</code> is used to apply a given CSS className
 * to the XHTML of a specified <code>targetComponent</code>.
 * <p>
 * You must add the CSS style declaration to the application instance first
 * using something like a <code>CssStyleAdd</code> or
 * <code>CssStyleSheetAdd</code> command.
 * <p>
 * For example:
 * <pre>
 *     ContainerEx containerEx = new ContainerEx();
 *     parent.add(containerEx);
 *     ...
 *     ...
 *     appInstance.enqueueCommand(
 *          new CssStyleAdd(" div.whizoStyle { color : red; } "));
 *     
 *     appInstance.enqueueCommand(
 *          new CssStyleApplyTo("whizoStyle",containerEx);
 * <pre>
 * 
 */
public class CssStyleApplyTo implements Command {

	private String className;

	private Component targetComponent;

	/**
	 * Constructs a <code>CssStyleApplyTo</code> Command that will
	 * apply a the given className to the components top level XHTML.
	 * <p>
	 * The className setting is appended to the client elements' current className 
	 * (its not replaced).
	 * 
	 * So if the specified className is "whizoStyle" and the current className
	 * on the component XHTML is "swishoStyle" you will end up with a combined
	 * class name of "whizoStyle swishoStyle".
	 * 
	 * @param className -
	 *            the CSS className to use
	 * @param targetComponent -
	 *            the targetComponent to apply this to on the client
	 */
	public CssStyleApplyTo(String className, Component targetComponent) {
		this.className = className;
		this.targetComponent = targetComponent;
	}

	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return Returns the targetComponent.
	 */
	public Component getTargetComponent() {
		return targetComponent;
	}
}
