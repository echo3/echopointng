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

/**
 * The <code>KeyStrokes</code> interface is used to denote all the keystroke combinations
 * available.
 *<p>
 * This can be a a single keystroke combination such as VK_F2 or a combination
 * using a mask such as ALT_MASK | CONTROL_MASK | VK_F2 which is equates to
 * Alt-Ctrl-F2.
 *
 */
public interface KeyStrokes {

	/**
	 * Use this mask to determine if the ALT key is held a key is pressed
	 */
	public static final int ALT_MASK = 0x1000;

	/**
	 * Use this mask to determine if the CONTROL key is held a key is pressed
	 */
	public static final int CONTROL_MASK = 0x2000;

	/**
	 * Use this mask to determine if the SHIFT key is held a key is pressed
	 */
	public static final int SHIFT_MASK = 0x4000;

	/**
	 * Use this mask to determine if the META key is held a key is pressed
	 */
	public static final int META_MASK = 0x8000;

	public static final int VK_CANCEL = 3;

	public static final int VK_HELP = 6;

	public static final int VK_BACK_SPACE = 8;

	public static final int VK_TAB = 9;

	public static final int VK_CLEAR = 12;

	public static final int VK_RETURN = 13;

	public static final int VK_ENTER = 14;

	public static final int VK_SHIFT = 16;

	public static final int VK_CONTROL = 17;

	public static final int VK_ALT = 18;

	public static final int VK_PAUSE = 19;

	public static final int VK_CAPS_LOCK = 20;

	public static final int VK_ESCAPE = 27;

	public static final int VK_SPACE = 32;

	public static final int VK_PAGE_UP = 33;

	public static final int VK_PAGE_DOWN = 34;

	public static final int VK_END = 35;

	public static final int VK_HOME = 36;

	public static final int VK_LEFT = 37;

	public static final int VK_UP = 38;

	public static final int VK_RIGHT = 39;

	public static final int VK_DOWN = 40;

	public static final int VK_PRINTSCREEN = 44;

	public static final int VK_INSERT = 45;

	public static final int VK_DELETE = 46;

	public static final int VK_0 = 48;

	public static final int VK_1 = 49;

	public static final int VK_2 = 50;

	public static final int VK_3 = 51;

	public static final int VK_4 = 52;

	public static final int VK_5 = 53;

	public static final int VK_6 = 54;

	public static final int VK_7 = 55;

	public static final int VK_8 = 56;

	public static final int VK_9 = 57;

	public static final int VK_SEMICOLON = 59;

	public static final int VK_EQUALS = 61;

	public static final int VK_A = 65;

	public static final int VK_B = 66;

	public static final int VK_C = 67;

	public static final int VK_D = 68;

	public static final int VK_E = 69;

	public static final int VK_F = 70;

	public static final int VK_G = 71;

	public static final int VK_H = 72;

	public static final int VK_I = 73;

	public static final int VK_J = 74;

	public static final int VK_K = 75;

	public static final int VK_L = 76;

	public static final int VK_M = 77;

	public static final int VK_N = 78;

	public static final int VK_O = 79;

	public static final int VK_P = 80;

	public static final int VK_Q = 81;

	public static final int VK_R = 82;

	public static final int VK_S = 83;

	public static final int VK_T = 84;

	public static final int VK_U = 85;

	public static final int VK_V = 86;

	public static final int VK_W = 87;

	public static final int VK_X = 88;

	public static final int VK_Y = 89;

	public static final int VK_Z = 90;

	public static final int VK_CONTEXT_MENU = 93;

	public static final int VK_NUMPAD0 = 96;

	public static final int VK_NUMPAD1 = 97;

	public static final int VK_NUMPAD2 = 98;

	public static final int VK_NUMPAD3 = 99;

	public static final int VK_NUMPAD4 = 100;

	public static final int VK_NUMPAD5 = 101;

	public static final int VK_NUMPAD6 = 102;

	public static final int VK_NUMPAD7 = 103;

	public static final int VK_NUMPAD8 = 104;

	public static final int VK_NUMPAD9 = 105;

	public static final int VK_MULTIPLY = 106;

	public static final int VK_ADD = 107;

	public static final int VK_SEPARATOR = 108;

	public static final int VK_SUBTRACT = 109;

	public static final int VK_DECIMAL = 110;

	public static final int VK_DIVIDE = 111;

	public static final int VK_F1 = 112;

	public static final int VK_F2 = 113;

	public static final int VK_F3 = 114;

	public static final int VK_F4 = 115;

	public static final int VK_F5 = 116;

	public static final int VK_F6 = 117;

	public static final int VK_F7 = 118;

	public static final int VK_F8 = 119;

	public static final int VK_F9 = 120;

	public static final int VK_F10 = 121;

	public static final int VK_F11 = 122;

	public static final int VK_F12 = 123;

	public static final int VK_F13 = 124;

	public static final int VK_F14 = 125;

	public static final int VK_F15 = 126;

	public static final int VK_F16 = 127;

	public static final int VK_F17 = 128;

	public static final int VK_F18 = 129;

	public static final int VK_F19 = 130;

	public static final int VK_F20 = 131;

	public static final int VK_F21 = 132;

	public static final int VK_F22 = 133;

	public static final int VK_F23 = 134;

	public static final int VK_F24 = 135;

	public static final int VK_NUM_LOCK = 144;

	public static final int VK_SCROLL_LOCK = 145;

	public static final int VK_COMMA = 188;

	public static final int VK_PERIOD = 190;

	public static final int VK_SLASH = 191;

	public static final int VK_BACK_QUOTE = 192;

	public static final int VK_OPEN_BRACKET = 219;

	public static final int VK_BACK_SLASH = 220;

	public static final int VK_CLOSE_BRACKET = 221;

	public static final int VK_QUOTE = 222;

	public static final int VK_META = 224;

}
