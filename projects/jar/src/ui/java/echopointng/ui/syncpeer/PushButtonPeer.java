package echopointng.ui.syncpeer;

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

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Style;

import org.w3c.dom.Element;

import echopointng.ButtonEx;
import echopointng.PushButton;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

/**
 * <code>ButtonExPeer</code> is a peer for <code>PushButton</code>
 */

public class PushButtonPeer extends ButtonExPeer {

	/**
	 * @see nextapp.echo2.webcontainer.SynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	public String getContainerId(Component child) {
		throw new UnsupportedOperationException("Component does not support children.");
	}

	/**
	 * We use the BUTTON tag instead of DIV
	 * 
	 * @see echopointng.ui.syncpeer.ButtonExPeer#getTopLevelButtonElement(echopointng.ui.util.RenderingContext,
	 *      echopointng.ButtonEx)
	 */
	protected Element getTopLevelButtonElement(RenderingContext rc, ButtonEx button, Style fallbackStyle) {
		Element buttonE = rc.createE("button");
		if (! Render.isClientIE(rc)) {
			buttonE.setAttribute("type", "button");
		}
		if (rc.getRP(PushButton.PROPERTY_SUBMIT_BUTTON, fallbackStyle, false)) {
			if (! Render.isClientIE(rc)) {
				buttonE.setAttribute("type", "submit");
			}
		}
		if (!button.isRenderEnabled()) {
			buttonE.setAttribute("disabled", "disabled");
		}
		return buttonE;
	}

	/**
	 * Our event support is a little different to the base ButtonEx code
	 * 
	 * @see echopointng.ui.syncpeer.ButtonExPeer#renderEventSupport(RenderingContext,
	 *      Element, ButtonEx, boolean)
	 */
	protected void renderEventSupport(RenderingContext rc, Element itemXML, ButtonEx button, boolean addRolloverListener) {
		boolean pressedEnabled = rc.getRP(ButtonEx.PROPERTY_PRESSED_ENABLED, false);
		itemXML.setAttribute("event-click", String.valueOf(true));
		if (addRolloverListener) {
			itemXML.setAttribute("event-mouseover", String.valueOf(true));
			itemXML.setAttribute("event-mouseout", String.valueOf(true));
		}
		if (pressedEnabled) {
			itemXML.setAttribute("event-mousedown", String.valueOf(true));
			itemXML.setAttribute("event-mouseup", String.valueOf(true));
		}
		// needed for type fixup
		if (Render.isClientIE(rc)) {
			if (rc.getRP(PushButton.PROPERTY_SUBMIT_BUTTON, false)) {
				itemXML.setAttribute("type", "submit");
			}
		}
	}
}
