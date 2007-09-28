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
package echopointng.ui.syncpeer;

import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.partialupdate.BorderUpdate;
import nextapp.echo2.webcontainer.partialupdate.ColorUpdate;
import nextapp.echo2.webcontainer.partialupdate.InsetsUpdate;
import nextapp.echo2.webrender.servermessage.DomUpdate;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.GroupBox;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;

/**
 * <code>GroupBoxPeer</code> is a peer for <code>GroupBox</code>
 */

public class GroupBoxPeer extends AbstractEchoPointContainerPeer {

	/**
	 * Default constructor.
	 */
	public GroupBoxPeer() {
		super();
		partialUpdateManager.add(GroupBox.PROPERTY_BORDER, new BorderUpdate(Column.PROPERTY_BORDER, null, BorderUpdate.CSS_BORDER));
		partialUpdateManager.add(GroupBox.PROPERTY_FOREGROUND, new ColorUpdate(Column.PROPERTY_FOREGROUND, null, ColorUpdate.CSS_COLOR));
		partialUpdateManager.add(GroupBox.PROPERTY_BACKGROUND, new ColorUpdate(Column.PROPERTY_BACKGROUND, null, ColorUpdate.CSS_BACKGROUND_COLOR));
		partialUpdateManager.add(GroupBox.PROPERTY_INSETS, new InsetsUpdate(Column.PROPERTY_INSETS, null, InsetsUpdate.CSS_PADDING));
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      org.w3c.dom.Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parentNode, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);
		CssStyleEx style = new CssStyleEx(component, fallbackStyle);

		boolean hasIEbackgroundPaddingQuirk = false;
		if (Render.isClientIE6(rc)) {
			if (style.getAttribute("background-color") != null) {
				hasIEbackgroundPaddingQuirk = true;
			}
			if (style.getAttribute("margin-top") != null || style.getAttribute("margin") != null) {
				hasIEbackgroundPaddingQuirk = true;
			}
		}

		
		if (hasIEbackgroundPaddingQuirk) {
			// Fixes an IE bug which applies padding-top and the background-colour outside of the border.
			style.setAttribute("position", "relative");
		}
		
		Element fs = rc.createE("fieldset");
		parentNode.appendChild(fs);
		fs.setAttribute("id", rc.getElementId());
		fs.setAttribute("style", style.renderInline());
		rc.addStandardWebSupport(fs);

		Label title = (Label) rc.getRP(GroupBox.PROPERTY_TITLE_LABEL, fallbackStyle);
		if (title != null && title.getText() != null && title.getText().length() > 0) {
			Element eLegend = rc.createE("legend");
			
			if (hasIEbackgroundPaddingQuirk) {
				// Fixes an IE bug which applies padding-top and the background-colour outside of the border.
				eLegend.setAttribute("style", "position:absolute; top:-0.5em; left:0.5em");
			}
			
			fs.appendChild(eLegend);
			renderReplaceableChild(rc, rc.getServerComponentUpdate(), eLegend, title);
		}

		Component children[] = component.getVisibleComponents();
		for (int i = (title == null ? 0 : 1); i < children.length; i++) {
			renderReplaceableChild(rc, rc.getServerComponentUpdate(), fs, children[i]);
		}
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {

		// Determine if fully replacing the component is required.
		boolean fullReplace = false;
		if (update.hasUpdatedLayoutDataChildren()) {
			fullReplace = true;
		} else if (update.hasUpdatedProperties()) {
			if (!partialUpdateManager.canProcess(rc, update)) {
				fullReplace = true;
			}
		}

		if (fullReplace) {
			// Perform full update.
			DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
			renderAdd(rc, update, targetId, update.getParent());
		} else {
			// Perform incremental updates.
			renderUpdateRemoveChildren(rc, update);

			if (update.hasUpdatedProperties()) {
				partialUpdateManager.process(rc, update);
			}

			renderUpdateAddChildren(rc, update);
		}

		return fullReplace;
	}
}
