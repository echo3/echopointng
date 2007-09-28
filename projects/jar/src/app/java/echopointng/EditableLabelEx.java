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
package echopointng;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Style;
import echopointng.able.MouseCursorable;
import echopointng.util.ColorKit;
import echopointng.util.reflect.ReflectionKit;

/**
 * <code>EditableLabelEx</code> is a Label component that can contain other
 * child components and when the user clicks on the Label, the underlying (and
 * hidden) child components will be shown.
 * <p>
 * So for example imagine a <code>EditableLabelEx</code> that contained a
 * <code>TextField</code> as a child. When the user clicks on the Label, the
 * TextField will be shown and the user can edit its contents.
 * <p>
 * This component uses the concept of a <code>target</code> component to allow
 * it to source its Text from. Any number of components can be added to the
 * <code>EditableLabelEx</code> however only the text of the
 * <code>target</code> component will be synchronised.  
 * <p>
 * And this <code>target</code> can be a grand child of the <code>EditableLabelEx</code>.  
 * For example if you added a <code>DateField</code> to the <code>EditableLabelEx</code> then
 * you would set the target to be the <code>TextField</code> part of the
 * <code>DateField</code>, since this is the bit you want to "edit" not the
 * drop down part.
 * <p>
 * The editing function is "ended" when the user moves away from the editing target or clicks
 * some where else on the page.  If the target is a TextComponent, then pressing escape can
 * restore the value back to what it was before editing proceeded.
 * 
 */
public class EditableLabelEx extends LabelEx {
	
	public static final int ACTIVATE_ON_DBLCLICK = 0;
	public static final int ACTIVATE_ON_DELAYEDCLICK = 1;
	

	public static final String PROPERTY_SYNCHRONIZED_WITH_TARGET = "synchronizedWithTarget";
	public static final String PROPERTY_ACTIVATION_METHOD = "activationMethod";

	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx(LabelEx.DEFAULT_STYLE);
		style.setProperty(PROPERTY_ROLLOVER_ENABLED, true);
		style.setProperty(PROPERTY_ROLLOVER_BACKGROUND, ColorKit.makeColor("#DEF3FF"));
		style.setProperty(PROPERTY_ROLLOVER_BORDER, new Border(1, ColorKit.makeColor("#3169C6"), Border.STYLE_DASHED));
		
		style.setProperty(PROPERTY_MOUSE_CURSOR, MouseCursorable.CURSOR_POINTER);
		DEFAULT_STYLE = style;
	}
	
	private Component target;

	/**
	 * Constructs a <code>EditableLabelEx</code> with no target component.
	 */
	public EditableLabelEx() {
		this(null);
	}

	/**
	 * Constructs a <code>EditableLabelEx</code> and sets the specified
	 * component as its target.
	 * 
	 * @param target -
	 *            the target of the <code>EditableLabelEx</code>
	 */
	public EditableLabelEx(Component target) {
		setTarget(target);
	}

	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		super.processInput(inputName, inputValue);
	}

	/**
	 * @return Returns the target of the <code>EditableLabelEx</code>.
	 */
	public Component getTarget() {
		return target;
	}

	/**
	 * Sets the current target of the <code>EditableLabelEx</code>. if this
	 * is not already a child of the <code>EditableLabelEx</code>, then it
	 * will be added as one.
	 * 
	 * @param target
	 *            The target to set.
	 */
	public void setTarget(Component target) {
		this.target = target;
		if (target != null) {
			if (!this.isAncestorOf(target)) {
				add(target);
			}

		}
	}

	/**
	 * @see nextapp.echo2.app.Label#isValidChild(nextapp.echo2.app.Component)
	 */
	public boolean isValidChild(Component component) {
		return true;
	}

	/**
	 * This method is called internally to "synchronise" the value of the
	 * <code>EditableLabelEx</code> with its target component.
	 * <p>
	 * The default implementation will look to see if the component has a String
	 * getText() method and use that to set the text of the Label to be the same
	 * as its target.
	 * <p>
	 * You can overrride this method to synchronise the Label with any type of
	 * component. This method may be called multiple times and the target may be
	 * null so take this into account.
	 */
	protected void synchroniseWithTarget() {
		Component target = getTarget();
		if (target != null) {
			String oldValue = super.getText();
			String newValue = (String) ReflectionKit.invokeIfPresent("getText", new Class[0], String.class, target, new Object[0]);
			if (newValue != null && (!newValue.equals(oldValue))) {
				setText(newValue);
			}
		}
	}

	/**
	 * @return true if the Label's value will be syncrhnonised with its
	 * target component.
	 */
	public boolean isSynchronisedWithTarget() {
		return ComponentEx.getProperty(this, PROPERTY_SYNCHRONIZED_WITH_TARGET, true);
	}
	
	/**
	 * @see nextapp.echo2.app.Label#getText()
	 */
	public String getText() {
		if (isSynchronisedWithTarget()) {
			synchroniseWithTarget();
		}
		return super.getText();
	}

	/**
	 * If this is true, then the Label's value will be syncrhnonised with its
	 * target component.
	 * 
	 * @param newValue -
	 *            the new state of the boolean flag
	 */
	public void setSynchronisedWithTarget(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_SYNCHRONIZED_WITH_TARGET, newValue);
	}
	
	public int getActivationMethod() {
		return ComponentEx.getProperty(this,PROPERTY_ACTIVATION_METHOD,ACTIVATE_ON_DBLCLICK);
	}
	/**
	 * Sets the activation method for editing.  This can be one of the following values
	 * <ul>
	 * <li>ACTIVATE_ON_DBLCLICK - double click to start editing</li>
	 * <li>ACTIVATE_ON_DELAYEDCLICK - click with a delay to start editing</li>
	 *  <ul/>
	 * @param newValue - the new activation method
	 */
	public void setActivationMethod(int newValue) {
		ComponentEx.setProperty(this,PROPERTY_ACTIVATION_METHOD,newValue);
	}
	
	/**
	 * @see nextapp.echo2.app.Component#validate()
	 */
	public void validate() {
		super.validate();
		if (isSynchronisedWithTarget()) {
			synchroniseWithTarget();
		}
	}
}
