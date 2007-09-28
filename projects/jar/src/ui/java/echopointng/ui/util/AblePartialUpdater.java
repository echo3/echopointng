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
package echopointng.ui.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.PartialUpdateManager;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.servermessage.DomUpdate;
import echopointng.able.Borderable;
import echopointng.able.Heightable;
import echopointng.able.Insetable;
import echopointng.able.MouseCursorable;
import echopointng.able.Positionable;
import echopointng.able.Scrollable;
import echopointng.able.Widthable;

/**
 * <code>AblePartialUpdater</code> is a <code>PartialUpdateParticipant</code>
 * that can handle the basic echopoint.able.* interfaces
 */

public class AblePartialUpdater implements PartialUpdateParticipant {

	private static Set handledPropertyNames;

	private static Map handlers;
	static {
		handledPropertyNames = new HashSet();
		handlers = new HashMap();

		initHandler(Borderable.PROPERTY_BORDER);
		initHandler(Heightable.PROPERTY_HEIGHT);
		initHandler(Insetable.PROPERTY_INSETS);
		initHandler(Insetable.PROPERTY_OUTSETS);
		initHandler(MouseCursorable.PROPERTY_MOUSE_CURSOR);
		initHandler(MouseCursorable.PROPERTY_MOUSE_CURSOR_URI);
		initHandler(Positionable.PROPERTY_BOTTOM);
		initHandler(Positionable.PROPERTY_LEFT);
		initHandler(Positionable.PROPERTY_POSITION);
		initHandler(Positionable.PROPERTY_RIGHT);
		initHandler(Positionable.PROPERTY_TOP);
		initHandler(Positionable.PROPERTY_Z_INDEX);
		initHandler(Widthable.PROPERTY_WIDTH);
		initHandler(Scrollable.PROPERTY_SCROLL_BAR_BASE_COLOR);
		initHandler(Scrollable.PROPERTY_SCROLL_BAR_POLICY);
		initHandler(Scrollable.PROPERTY_SCROLL_BAR_PROPERTIES);

	}

	private static void initHandler(String propertyName) {
		handledPropertyNames.add(propertyName);
		handlers.put(propertyName, new AblePartialUpdater(propertyName));
	}

	private String targetPropertyName;

	/**
	 * Constructs a <code>AblePartialUpdater</code> that handles the specified
	 * property name.
	 * 
	 * @param targetPropertyName -
	 *            the name of the property to handle
	 */
	public AblePartialUpdater(String targetPropertyName) {
		this.targetPropertyName = targetPropertyName;
	}

	/**
	 * This creates a set of <code>AblePartialUpdater</code>'s and adds them
	 * to the <code>PartialUpdateManager</code> for the particular able
	 * properties.
	 * 
	 * @param partialUpdateManager -
	 *            the PartialUpdateManager to add a AblePartialUpdater's to
	 */
	public static void addToUpdateManager(PartialUpdateManager partialUpdateManager) {
		for (Iterator iter = handledPropertyNames.iterator(); iter.hasNext();) {
			String propertyName = (String) iter.next();
			partialUpdateManager.add(propertyName, (PartialUpdateParticipant) handlers.get(propertyName));
		}
	}

	/**
	 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#canRenderProperty(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate)
	 */
	public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
		String[] propertyNames = update.getUpdatedPropertyNames();
		for (int i = 0; i < propertyNames.length; i++) {
			if (propertyNames[i].equals(targetPropertyName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#renderProperty(
	 *      nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate)
	 */
	public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
		Component component = update.getParent();
		String elementId = ContainerInstance.getElementId(component);
		String propertyNames[] = update.getUpdatedPropertyNames();
		for (int i = 0; i < propertyNames.length; i++) {
			String propertyName = propertyNames[i];
			if (propertyName.equals(targetPropertyName)) {
				Object value = update.getParent().getRenderProperty(propertyName);
				if (value == null) {
					renderNullUpdate(rc, elementId, component, propertyName);
				} else {
					renderUpdatesViaCssStyle(rc, elementId, component, propertyName);
				}
			}
		}
	}

	private void removeBorderStyles(RenderContext rc, String elementId) {
		DomUpdate.renderStyleUpdate(rc.getServerMessage(), elementId, "borderWidth", "");
		DomUpdate.renderStyleUpdate(rc.getServerMessage(), elementId, "borderStyle", "");
		DomUpdate.renderStyleUpdate(rc.getServerMessage(), elementId, "borderColor", "");
	}

	/**
	 * Called to render updates when the value has changed to null.
	 */
	private void renderNullUpdate(RenderContext rc, String elementId, Component component, String propertyName) {
		if (component instanceof Borderable && propertyName.equals(Borderable.PROPERTY_BORDER)) {
			// special border support
			removeBorderStyles(rc, elementId);
			//====================================================
			//===
		} else if (component instanceof Insetable) {
			if (propertyName.equals(Insetable.PROPERTY_INSETS)) {
				DomUpdate.renderStyleUpdate(rc.getServerMessage(), elementId, "padding", "");
			}
			if (propertyName.equals(Insetable.PROPERTY_OUTSETS)) {
				DomUpdate.renderStyleUpdate(rc.getServerMessage(), elementId, "margin", "");
			}
			//====================================================
			//===
		} else if (component instanceof MouseCursorable) {
			DomUpdate.renderStyleUpdate(rc.getServerMessage(), elementId, "cursor", "");
			renderUpdatesViaCssStyle(rc,elementId,component,propertyName);
			//====================================================
			//===
		} else {
			DomUpdate.renderStyleUpdate(rc.getServerMessage(), elementId, propertyName, "");
		}
	}

	private void renderUpdatesViaCssStyle(RenderContext rc, String elementId, Component component, String propertyName) {
		CssStyleEx styleEx = new CssStyleEx();
		boolean handled = false;
		if (component instanceof Borderable && propertyName.equals(Borderable.PROPERTY_BORDER)) {
			// special case - remove any previous border value
			removeBorderStyles(rc, elementId);
			Render.asBorderable(styleEx, (Borderable) component);
			handled = true;
		} else if (component instanceof Heightable && propertyName.equals(Heightable.PROPERTY_HEIGHT)) {
			Render.asHeightable(styleEx, (Heightable) component);
			handled = true;
		} else if (component instanceof Insetable && propertyName.equals(Insetable.PROPERTY_INSETS)) {
			Render.asInsetable(styleEx, (Insetable) component);
			handled = true;
		} else if (component instanceof Insetable && propertyName.equals(Insetable.PROPERTY_OUTSETS)) {
			Render.asInsetable(styleEx, (Insetable) component);
			handled = true;
		} else if (component instanceof MouseCursorable && propertyName.equals(MouseCursorable.PROPERTY_MOUSE_CURSOR)) {
			Render.asMouseCursorable(styleEx, (MouseCursorable) component);
			handled = true;
		} else if (component instanceof MouseCursorable && propertyName.equals(MouseCursorable.PROPERTY_MOUSE_CURSOR_URI)) {
			Render.asMouseCursorable(styleEx, (MouseCursorable) component);
			handled = true;
		} else if (component instanceof Widthable && propertyName.equals(Widthable.PROPERTY_WIDTH)) {
			Render.asWidthable(styleEx, (Widthable) component);
			handled = true;
		}
		if (!handled && component instanceof Positionable) {
			boolean ok = false;
			if (propertyName.equals(Positionable.PROPERTY_BOTTOM)) {
				ok = true;
			} else if (propertyName.equals(Positionable.PROPERTY_LEFT)) {
				ok = true;
			} else if (propertyName.equals(Positionable.PROPERTY_POSITION)) {
				ok = true;
			} else if (propertyName.equals(Positionable.PROPERTY_RIGHT)) {
				ok = true;
			} else if (propertyName.equals(Positionable.PROPERTY_TOP)) {
				ok = true;
			} else if (propertyName.equals(Positionable.PROPERTY_Z_INDEX)) {
				ok = true;
			}
			if (ok) {
				Render.asPositionable(styleEx, (Positionable) component);
				handled = true;
			}
		}
		if (!handled && component instanceof Scrollable) {
			boolean ok = false;
			if (propertyName.equals(Scrollable.PROPERTY_SCROLL_BAR_BASE_COLOR)) {
				ok = true;
			} else if (propertyName.equals(Scrollable.PROPERTY_SCROLL_BAR_POLICY)) {
				ok = true;
			} else if (propertyName.equals(Scrollable.PROPERTY_SCROLL_BAR_PROPERTIES)) {
				ok = true;
			}
			if (ok) {
				Render.asScrollable(styleEx, (Scrollable) component);
				handled = true;
			}
		}
		StringTokenizer st1 = new StringTokenizer(styleEx.renderInline(), ":;");
		while (st1.hasMoreTokens()) {
			String cssAttributeName = st1.nextToken();
			if (st1.hasMoreTokens()) {
				String cssAttributeValue = st1.nextToken();
				cssAttributeName = CssKit.cssAttributeNameToPropertyName(cssAttributeName);
				DomUpdate.renderStyleUpdate(rc.getServerMessage(), elementId, cssAttributeName, cssAttributeValue);
			}
		}
	}

}
