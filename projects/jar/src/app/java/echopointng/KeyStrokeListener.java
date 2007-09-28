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

import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;

/**
 * <code>KeyStrokeListener</code> is a component that listens for key stroke
 * combinations and raises action events if any one of those key combinations
 * have been pressed.
 * <p>
 * This can be a a single keystroke combination such as VK_F2 or a combination
 * using a mask such as ALT_MASK | CONTROL_MASK | VK_F2 which is equates to
 * Alt-Ctrl-F2.
 * <p>
 * Some components "EAT" all of the key strokes that go to them and hence only
 * the key combinations that bubble up all the way to the base level will be
 * able to be caught by the key stroke listener.
 */
public class KeyStrokeListener extends ComponentEx implements KeyStrokes {

	public static final String ACTION_LISTENERS_CHANGED_PROPERTY = "actionListeners";

	public static final String KEYSTROKE_CHANGED_PROPERTY = "keyStroke";

	public static final String PROPERTY_CANCEL_MODE = "cancelMode";

	public static final String PROPERTY_TARGET = "target";

	private Map keyCombos = new HashMap();

	/**
	 * Constructs a <code>KeyStrokeListener</code>
	 */
	public KeyStrokeListener() {
		super();
	}

	/**
	 * This component does not support children.
	 * 
	 * @see nextapp.echo2.app.Component#isValidChild(nextapp.echo2.app.Component)
	 */
	public boolean isValidChild(Component child) {
		return false;
	}

	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
		super.processInput(inputName, inputValue);
		if (KEYSTROKE_CHANGED_PROPERTY.equals(inputName)) {
			Integer key = (Integer) inputValue;
			if (key != null && hasKeyCombination(key.intValue())) {
				if (!hasEventListenerList()) {
					return;
				}
				String command = getKeyCombinationCommand(key.intValue());
				ActionEvent ae = new ActionEvent(this, command);
				EventListener[] listeners = getEventListenerList().getListeners(ActionListener.class);
				for (int index = 0; index < listeners.length; ++index) {
					((ActionListener) listeners[index]).actionPerformed(ae);
				}
			}
		}
	}

	/**
	 * This adds a key combination to the <code>KeyStrokeListener</code>.
	 * This can be a single keystroke combination such as VK_F2 or a combination
	 * using a mask such as ALT_MASK | CONTROL_MASK | VK_F2 which is
	 * Alt-Ctrl-F2.
	 * <p>
	 * If this key combination is pressed, then the command will be used to
	 * raise an ActionEvent.
	 * 
	 * @param keyCombination -
	 *            the new key combination to add.
	 * @param command -
	 *            the command to be used with the ActionEvent
	 */
	public void addKeyCombination(int keyCombination, String command) {
		Integer key = new Integer(keyCombination);
		keyCombos.put(key, command);
		firePropertyChange(KEYSTROKE_CHANGED_PROPERTY, key, null);
	}

	/**
	 * This adds a key combination to the <code>KeyStrokeListener</code> using
	 * string returned from getMnemonic as the command string.
	 * 
	 * @see KeyStrokeListener#addKeyCombination(int, String)
	 */
	public void addKeyCombination(int keyCombination) {
		addKeyCombination(keyCombination, getMnemonic(keyCombination));
	}

	/**
	 * Returns true if the given key combination has been previously added to
	 * this <code>KeyStrokeListener</code>.
	 * 
	 * @param keyCombination -
	 *            the key combination to test for
	 * @return true if the given key combination has been previously added to
	 *         this <code>KeyStrokeListener</code>.
	 */
	public boolean hasKeyCombination(int keyCombination) {
		Integer key = new Integer(keyCombination);
		return keyCombos.containsKey(key);
	}

	/**
	 * Removes a key combination from this <code>KeyStrokeListener</code>.
	 * 
	 * @param keyCombination -
	 *            the key combination to remove.
	 */
	public void removeKeyCombination(int keyCombination) {
		Integer key = new Integer(keyCombination);
		keyCombos.remove(key);
		firePropertyChange(KEYSTROKE_CHANGED_PROPERTY, key, null);
	}

	/**
	 * Returns all the key combinations that currently in this
	 * <code>KeyStrokeListener</code>.
	 * 
	 * @return all the key combinations that currently in this
	 *         <code>KeyStrokeListener</code>.
	 */
	public int[] getKeyCombinations() {
		int len = keyCombos.size();
		int keyArray[] = new int[len];
		len = 0;
		for (Iterator iter = keyCombos.keySet().iterator(); iter.hasNext();) {
			Integer key = (Integer) iter.next();
			keyArray[len] = key.intValue();
			len++;
		}
		Arrays.sort(keyArray);
		return keyArray;
	}

	/**
	 * Returns the command that is currently associated with the given key
	 * combination.
	 * 
	 * @param keyCombination -
	 *            the key combination to look for.
	 * @return the command that is currently associated with the given key
	 *         combination.
	 */
	public String getKeyCombinationCommand(int keyCombination) {
		Integer key = new Integer(keyCombination);
		return (String) keyCombos.get(key);
	}

	/**
	 * Adds an <code>ActionListener</code> to receive notification of user
	 * actions, i.e., key presses.
	 * 
	 * @param l
	 *            the listener to add
	 */
	public void addActionListener(ActionListener l) {
		getEventListenerList().addListener(ActionListener.class, l);
		firePropertyChange(ACTION_LISTENERS_CHANGED_PROPERTY, null, l);
	}

	/**
	 * Determines if there are any <code>ActionListener</code> s registered.
	 * 
	 * @return true if any action listeners are registered
	 */
	public boolean hasActionListeners() {
		return hasEventListenerList() && getEventListenerList().getListenerCount(ActionListener.class) != 0;
	}

	/**
	 * Removes an <code>ActionListener</code> from being notified of user
	 * actions, i.e., key presses.
	 * 
	 * @param l
	 *            the listener to remove
	 */
	public void removeActionListener(ActionListener l) {
		if (!hasEventListenerList()) {
			return;
		}
		getEventListenerList().removeListener(ActionListener.class, l);
		firePropertyChange(ACTION_LISTENERS_CHANGED_PROPERTY, l, null);
	}

	/**
	 * @return true if cancel mode is currently in place.
	 * @see KeyStrokeListener#setCancelMode(boolean)
	 */
	public boolean getCancelMode() {
		return getProperty(PROPERTY_CANCEL_MODE, false);
	}

	/**
	 * When cancelMode is true, then the key stroke is cancel and prevented from
	 * bubling further up the hierarchy. Not all key conbinations can be
	 * cancelled and not all user agents allow event cancelling at this level.
	 * 
	 * @param newValue -
	 *            the new cancel moed value
	 */
	public void setCancelMode(boolean newValue) {
		setProperty(PROPERTY_CANCEL_MODE, newValue);
	}

	/**
	 * Returns the component that is the target of this
	 * <code>KeyStrokeListener</code>. If this is null, then the underlying
	 * Window is targeted.
	 * 
	 * @return - component that is the target of this
	 *         <code>KeyStrokeListener</code> or null.
	 */
	public Component getTarget() {
		return (Component) getProperty(PROPERTY_TARGET);
	}

	/**
	 * Set the component that is the target of this
	 * <code>KeyStrokeListener</code>. If this is null, then the underlying
	 * Window is targeted.
	 * 
	 * @param newValue -
	 *            component that is the target of this
	 *            <code>KeyStrokeListener</code> or null.
	 */
	public void setTarget(Component newValue) {
		setProperty(PROPERTY_TARGET, newValue);
	}

	/**
	 * This returns a mnemonic string representation of the given key
	 * combination for example 'F1' or 'Ctrl-F1' or 'A'.
	 * 
	 * @param keyCombination -
	 *            the key combination to look for.
	 * @return returns a mnemonic string representation of the given key
	 *         combination
	 */
	public static String getMnemonic(int keyCombination) {
		StringBuffer sb = new StringBuffer();
		getMnemonicBase(keyCombination, sb);
		if ((keyCombination & SHIFT_MASK) == SHIFT_MASK) {
			sb.insert(0, "Shift-");
		}
		if ((keyCombination & CONTROL_MASK) == CONTROL_MASK) {
			sb.insert(0, "Ctrl-");
		}
		if ((keyCombination & ALT_MASK) == ALT_MASK) {
			sb.insert(0, "Alt-");
		}
		return sb.toString();
	}

	private static void getMnemonicBase(int keyCombination, StringBuffer sb) {
		keyCombination = keyCombination & ~ALT_MASK;
		keyCombination = keyCombination & ~CONTROL_MASK;
		keyCombination = keyCombination & ~SHIFT_MASK;
		keyCombination = keyCombination & ~META_MASK;

		switch (keyCombination) {
		case VK_CANCEL:
			sb.append("Cancel");
			break;

		case VK_HELP:
			sb.append("Help");
			break;

		case VK_BACK_SPACE:
			sb.append("Backspace");
			break;

		case VK_TAB:
			sb.append("Tab");
			break;

		case VK_CLEAR:
			sb.append("Clear");
			break;

		case VK_RETURN:
			sb.append("Return");
			break;

		case VK_ENTER:
			sb.append("Enter");
			break;

		case VK_SHIFT:
			sb.append("Shift");
			break;

		case VK_CONTROL:
			sb.append("Control");
			break;

		case VK_ALT:
			sb.append("Alt");
			break;

		case VK_PAUSE:
			sb.append("Pause");
			break;

		case VK_CAPS_LOCK:
			sb.append("Capslock");
			break;

		case VK_ESCAPE:
			sb.append("Escape");
			break;

		case VK_SPACE:
			sb.append("Space");
			break;

		case VK_PAGE_UP:
			sb.append("PgUp");
			break;

		case VK_PAGE_DOWN:
			sb.append("PgDown");
			break;

		case VK_END:
			sb.append("End");
			break;

		case VK_HOME:
			sb.append("Home");
			break;

		case VK_LEFT:
			sb.append("Left");
			break;

		case VK_UP:
			sb.append("Up");
			break;

		case VK_RIGHT:
			sb.append("Right");
			break;

		case VK_DOWN:
			sb.append("Down");
			break;

		case VK_PRINTSCREEN:
			sb.append("Printscreen");
			break;

		case VK_INSERT:
			sb.append("Insert");
			break;

		case VK_DELETE:
			sb.append("Delete");
			break;

		case VK_0:
			sb.append('0');
			break;

		case VK_1:
			sb.append('1');
			break;

		case VK_2:
			sb.append('2');
			break;

		case VK_3:
			sb.append('3');
			break;

		case VK_4:
			sb.append('4');
			break;

		case VK_5:
			sb.append('5');
			break;

		case VK_6:
			sb.append('6');
			break;

		case VK_7:
			sb.append('7');
			break;

		case VK_8:
			sb.append('8');
			break;

		case VK_9:
			sb.append('9');
			break;

		case VK_SEMICOLON:
			sb.append(';');
			break;

		case VK_EQUALS:
			sb.append('=');
			break;

		case VK_A:
			sb.append('A');
			break;

		case VK_B:
			sb.append('B');
			break;

		case VK_C:
			sb.append('C');
			break;

		case VK_D:
			sb.append('D');
			break;

		case VK_E:
			sb.append('E');
			break;

		case VK_F:
			sb.append('F');
			break;

		case VK_G:
			sb.append('G');
			break;

		case VK_H:
			sb.append('H');
			break;

		case VK_I:
			sb.append('I');
			break;

		case VK_J:
			sb.append('J');
			break;

		case VK_K:
			sb.append('K');
			break;

		case VK_L:
			sb.append('L');
			break;

		case VK_M:
			sb.append('M');
			break;

		case VK_N:
			sb.append('N');
			break;

		case VK_O:
			sb.append('O');
			break;

		case VK_P:
			sb.append('P');
			break;

		case VK_Q:
			sb.append('Q');
			break;

		case VK_R:
			sb.append('R');
			break;

		case VK_S:
			sb.append('S');
			break;

		case VK_T:
			sb.append('T');
			break;

		case VK_U:
			sb.append('U');
			break;

		case VK_V:
			sb.append('V');
			break;

		case VK_W:
			sb.append('W');
			break;

		case VK_X:
			sb.append('X');
			break;

		case VK_Y:
			sb.append('Y');
			break;

		case VK_Z:
			sb.append('Z');
			break;

		case VK_CONTEXT_MENU:
			sb.append("Contextmenu");
			break;

		case VK_NUMPAD0:
			sb.append("Numpad0");
			break;

		case VK_NUMPAD1:
			sb.append("Numpad1");
			break;

		case VK_NUMPAD2:
			sb.append("Numpad2");
			break;

		case VK_NUMPAD3:
			sb.append("Numpad3");
			break;

		case VK_NUMPAD4:
			sb.append("Numpad4");
			break;

		case VK_NUMPAD5:
			sb.append("Numpad5");
			break;

		case VK_NUMPAD6:
			sb.append("Numpad6");
			break;

		case VK_NUMPAD7:
			sb.append("Numpad7");
			break;

		case VK_NUMPAD8:
			sb.append("Numpad8");
			break;

		case VK_NUMPAD9:
			sb.append("Numpad9");
			break;

		case VK_MULTIPLY:
			sb.append("Multiply");
			break;

		case VK_ADD:
			sb.append("Add");
			break;

		case VK_SEPARATOR:
			sb.append("Separator");
			break;

		case VK_SUBTRACT:
			sb.append("Subtract");
			break;

		case VK_DECIMAL:
			sb.append("Decimal");
			break;

		case VK_DIVIDE:
			sb.append("Divide");
			break;

		case VK_F1:
			sb.append("F1");
			break;

		case VK_F2:
			sb.append("F2");
			break;

		case VK_F3:
			sb.append("F3");
			break;

		case VK_F4:
			sb.append("F4");
			break;

		case VK_F5:
			sb.append("F5");
			break;

		case VK_F6:
			sb.append("F6");
			break;

		case VK_F7:
			sb.append("F7");
			break;

		case VK_F8:
			sb.append("F8");
			break;

		case VK_F9:
			sb.append("F9");
			break;

		case VK_F10:
			sb.append("F10");
			break;

		case VK_F11:
			sb.append("F11");
			break;

		case VK_F12:
			sb.append("F12");
			break;

		case VK_F13:
			sb.append("F13");
			break;

		case VK_F14:
			sb.append("F14");
			break;

		case VK_F15:
			sb.append("F15");
			break;

		case VK_F16:
			sb.append("F16");
			break;

		case VK_F17:
			sb.append("F17");
			break;

		case VK_F18:
			sb.append("F18");
			break;

		case VK_F19:
			sb.append("F19");
			break;

		case VK_F20:
			sb.append("F20");
			break;

		case VK_F21:
			sb.append("F21");
			break;

		case VK_F22:
			sb.append("F22");
			break;

		case VK_F23:
			sb.append("F23");
			break;

		case VK_F24:
			sb.append("F24");
			break;

		case VK_NUM_LOCK:
			sb.append("Numlock");
			break;

		case VK_SCROLL_LOCK:
			sb.append("Scrolllock");
			break;

		case VK_COMMA:
			sb.append(",");
			break;

		case VK_PERIOD:
			sb.append(".");
			break;

		case VK_SLASH:
			sb.append("/");
			break;

		case VK_BACK_QUOTE:
			sb.append("`");
			break;

		case VK_OPEN_BRACKET:
			sb.append("[");
			break;

		case VK_BACK_SLASH:
			sb.append("\\");
			break;

		case VK_CLOSE_BRACKET:
			sb.append("]");
			break;

		case VK_QUOTE:
			sb.append("'");
			break;

		case VK_META:
			sb.append("Meta");
			break;
		}
	}
}
