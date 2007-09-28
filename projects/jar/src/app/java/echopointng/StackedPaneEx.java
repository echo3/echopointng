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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.IllegalChildException;
import nextapp.echo2.app.Pane;

/**
 * <code>StackedContentPaneEx</code> is a <code>{@link Pane}</code> that is allowed to have
 * multiple children with only one child component is show at a time, the one
 * that is on top of the '<b>stack</b>'. Think of it like a deck of cards,
 * only the top card is visible at the one time, even though there can be many
 * cards in the deck.
 * <p>
 * When <code>StackedContentPaneEx</code> is "pushed", a new child is placed
 * on top of the display "stack" and made visible. When the
 * <code>StackedContentPaneEx</code> is "popped" the previous child in the
 * 'stack' is displayed.
 * <p>
 * The StackedPane by default does "lazy" rendering. This means that the
 * components are only rendered on the client as required. This means less
 * client memory and network usage but also means the render time for moving
 * between displayed child components is higher. You can turn this off to 
 * improve the "transition" time between components.
 * 
 * @see Stack
 * @see ContentPaneEx
 */
public class StackedPaneEx extends ContentPaneEx {

	public static final String STACK_CHANGED_PROPERTY = "stack";

	public static final String PROPERTY_LAZY_RENDER_ENABLED = "lazyRenderEnabled";

	private List stack = new ArrayList();

	/**
	 * Constructs a <code>StackedPaneEx</code>, with nothing in the stack
	 */
	public StackedPaneEx() {
		this(new Component[0]);
	}

	/**
	 * Constructs a <code>StackedPaneEx</code> with the specified component on
	 * the stack.
	 * 
	 * @param c -
	 *            the Component to push onto the stack
	 */
	public StackedPaneEx(Component c) {
		this(new Component[] { c });
	}

	/**
	 * Constructs a <code>StackedPaneEx</code> with the specified aray of
	 * components on the stack, with the last component int the array being made
	 * the top component of the stack.
	 * 
	 * @param components -
	 *            an array of Component to push onto the stack
	 */
	public StackedPaneEx(Component[] components) {
		super();
		setFocusTraversalParticipant(false);
		if (components != null) {
			for (int i = 0; i < components.length; i++) {
				push(components[i]);
			}
		}
	}

	/**
	 * Called to indicate that the stack has been changed.
	 * 
	 */
	protected void fireStackChanged() {
		firePropertyChange(STACK_CHANGED_PROPERTY, null, null);
	}

	/**
	 * This will <b>push</b> a component onto the <b>top</b> of the stack and
	 * hence make it the visible. If the component is not already a child of the
	 * StackedPaneEx, it is made one.
	 * <p>
	 * If the component is already somewhere in the stack, it is removed from
	 * its current position and moved to the top of the stack.
	 * 
	 * @param c -
	 *            the component to push onto the top of the stack.
	 */
	public void push(Component c) {
		if (c == null) {
			throw new IllegalArgumentException("The Component c must not be null");
		}
		Component peek = peek();
		if (peek != c) {
			if (stack.contains(c)) {
				stack.remove(c);
			}
			stack.add(c);
			if (!this.isAncestorOf(c)) {
				super.add(c, -1);
			}
			fireStackChanged();
		}
	}

	/**
	 * This will <b>push</b> an array of component onto the <b>top</b> of the
	 * stack and hence make it the visible. If the components are not already
	 * chilreb of the StackedPaneEx, then they are made one. The last component
	 * in the array will be <b>push</b>ed to the top of the stack.
	 * 
	 * @param components
	 *            the components to push onto the top of the stack.
	 */
	public void push(Component[] components) {
		if (components != null) {
			for (int i = 0; i < components.length; i++) {
				push(components[i]);
			}
		}
	}

	/**
	 * This now has the same meaning as the push(Component c) method
	 * 
	 * @see StackedPaneEx#push(Component)
	 */
	public void add(Component c) {
		push(c);
	}

	/**
	 * This will <b>pop</b> the current <code>Component</code> of the top of
	 * the stack. This method DOES NOT remove the component as a child however.
	 * 
	 * @return - the Component that was previously on top of the stack or null
	 *         if there was none.
	 */
	public Component pop() {
		if (stack.size() == 0) {
			return null;
		}
		int oldSize = stack.size();
		Component popped = peek();
		stack.remove(popped);
		if (oldSize != stack.size()) {
			fireStackChanged();
		}
		return popped;
	}

	/**
	 * This will <b>pop</b> the current <code>Component</code> of the top of
	 * the stack and remove it as a child of the StackedPaneEx.
	 * 
	 * @return - the Component that was previously on top of the stack or null
	 *         if there was none.
	 */
	public Component popAndRemove() {
		Component popped = pop();
		super.remove(popped);
		return popped;
	}

	/**
	 * This will <b>pop</b> the stack of components until the specified
	 * component becomes the top of the stack. If the component is not already
	 * on the stack of components, the the stack will be emptied.
	 * 
	 * @param c -
	 *            the component to make into the top of the stack
	 * @return - the array of Components that were <b>popped</b> of the stack
	 */
	public Component[] popUntilTop(Component c) {
		List poppedList = new ArrayList();
		Component peekComponent = peek();
		while (peekComponent != null && peekComponent != c) {
			Component popped = pop();
			if (popped != null) {
				poppedList.add(popped);
			}
			peekComponent = peek();
		}
		return (Component[]) poppedList.toArray(new Component[poppedList.size()]);
	}

	/**
	 * This returns the Component that is currently on top of the stack or null
	 * if there isnt one.
	 * 
	 * @return - the Component that is currently on top of the stack or null if
	 *         there is one.
	 */
	public Component peek() {
		if (stack.size() == 0) {
			return null;
		}
		return (Component) stack.get(stack.size() - 1);
	}

	/**
	 * This returns the Component that is at the specified index within the
	 * stack or null if there isnt one.
	 * 
	 * @param index -
	 *            the index into the stack
	 * @return - The Component at that index
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range (index &lt; 0 || index &gt;=
	 *             size()).
	 */
	public Component peek(int index) {
		if (stack.size() == 0) {
			return null;
		}
		return (Component) stack.get(index);
	}

	/**
	 * @return - the size of the stack associated wih this StackedPaneEx
	 */
	public int size() {
		return stack.size();
	}

	/**
	 * Insets the {@link Component} <code>c</code> into the
	 * <code>StackedPaneEx</code>. The component will become the top of the
	 * stack if its index is greater than the current stack size.
	 * 
	 * @param c -
	 *            the {@link Component} to insert
	 * @param index -
	 *            the index into the stack
	 */
	public void insert(Component c, int index) {
		if (index >= stack.size()) {
			push(c);
		} else {
			stack.add(index, c);
			if (!this.isAncestorOf(c)) {
				super.add(c);
			}
			fireStackChanged();
		}
	}

	/**
	 * This is now has the same meaning as insert(Component c, int index)
	 * 
	 * @see StackedPaneEx#insert(Component, int)
	 */
	public void add(Component c, int index) throws IllegalChildException {
		insert(c, index);
	}

	/**
	 * @see nextapp.echo2.app.Component#remove(nextapp.echo2.app.Component)
	 */
	public void remove(Component c) {
		super.remove(c);
		if (stack.contains(c)) {
			stack.remove(c);
			fireStackChanged();
		}
	}

	/**
	 * @see nextapp.echo2.app.Component#remove(int)
	 */
	public void remove(int index) {
		int oldSize = stack.size();
		Component c = (Component) stack.get(index);
		stack.remove(c);
		super.remove(c);
		if (oldSize != stack.size()) {
			fireStackChanged();
		}
	}

	/**
	 * @see nextapp.echo2.app.Component#removeAll()
	 */
	public void removeAll() {
		int oldSize = stack.size();
		stack.clear();
		super.removeAll();
		if (oldSize != stack.size()) {
			fireStackChanged();
		}
	}
	
	/**
	 * Synonym for removeAll().
	 */
	public void empty() {
		removeAll();
	}

	/**
	 * @return true when child components on the stack, other than the top one,
	 *         are rendered in a lazy fashion, that is only when needed. This is
	 *         true by default.
	 */
	public boolean isLazyRenderEnabled() {
		return ComponentEx.getProperty(this, PROPERTY_LAZY_RENDER_ENABLED, true);
	}

	/**
	 * This controls whether child components on the stack, other than the top
	 * one, are rendered in a lazy fashion, that is only when needed. This is
	 * true by default.
	 * 
	 * @param newValue -
	 *            the new value
	 */
	public void setLazyRenderEnabled(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_LAZY_RENDER_ENABLED, newValue);
	}
}
