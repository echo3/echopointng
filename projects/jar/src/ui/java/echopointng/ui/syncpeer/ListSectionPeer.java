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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.SelectField;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.button.AbstractButton;
import nextapp.echo2.app.list.ListModel;
import nextapp.echo2.app.text.TextComponent;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.SynchronizePeerFactory;
import nextapp.echo2.webrender.output.CssStyle;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.LabelEx;
import echopointng.ListSection;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;
import echopointng.xhtml.XhtmlFragment;

public class ListSectionPeer extends AbstractEchoPointContainerPeer implements Comparator {

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		
		Style fallbackStyle = EPNG.getFallBackStyle(component);
		CssStyleEx styleList = new CssStyleEx(component, fallbackStyle);
		createListStyle(rc, styleList, fallbackStyle);

		
		/*
		 * Determine list type and ordering
		 */
		String listOrdering = null;
		ListModel model = (ListModel) rc.getRP(ListSection.PROPERTY_MODEL);
		if (model == null) {
			return;
		}
		int listSize = model.size();
		List theList = new ArrayList(listSize);
		for (int i = 0; i < listSize; i++) {
			theList.add(model.get(i));
		}
		if (rc.getRP(ListSection.PROPERTY_ORDERING, fallbackStyle, ListSection.UNORDERED ) == ListSection.UNORDERED) {
			listOrdering = "ul";
		} else {
			listOrdering = "ol";

			// we need to sort the list since the brower doesnt do it
			Collections.sort(theList, this);
		}

		
		/*
		 * Main list element
		 */
		Element listE = rc.createE(listOrdering);
		parent.appendChild(listE);
		listE.setAttribute("id", rc.getElementId());
		listE.setAttribute("style", styleList.renderInline());
		rc.addStandardWebSupport(listE);

		
		/*
		 * Render children as list items
		 */
		Element listItemE;
		for (int i = 0; i < theList.size(); i++) {
			Object listObject = theList.get(i);
			
			//
			// handle components
			if (listObject instanceof Component) {
				Component child = (Component) listObject;
				if (! child.isRenderVisible()) {
					continue;
				}
				renderReplaceableChild(rc, rc.getServerComponentUpdate(), listE, child);

			//	
			// handle XhtmlFragments or plain old Objects
			} else {	
				listItemE = rc.createE("li");
				listE.appendChild(listItemE);
				
				if (listObject instanceof XhtmlFragment) {
					XhtmlFragment fragment = (XhtmlFragment) listObject;
					
					CssStyle style = Render.itsDisplayLayoutData(rc, fragment);
					
					// Nested Lists seem to require the IE6 zoom hack to render correctly
					if (Render.isClientIE6(rc)) {
						style.setAttribute("zoom", "1");
					}
					
					try {
						Node nodes[] = fragment.toDOM(rc.getDocument());
						for (int j = 0; j < nodes.length; j++) {
							listItemE.appendChild(nodes[j]);
						}
					} catch (Exception e) {
						throw new RuntimeException("The XhtmlFragment is not valid XHTML : " + fragment.getFragment(), e);
					}
					
				} else {
					listItemE.appendChild(rc.createText(String.valueOf(listObject)));
				}
			}
		}
	}

	/**
	 * we are a comparator on the List Section list
	 */
	public int compare(Object o1, Object o2) {
		String s1 = getObjectText(o1);
		String s2 = getObjectText(o2);

		return s1.compareTo(s2);
	}

	/**
	 * Appends list specific styles to an existing style.
	 * 
	 * Should be called only once per render
	 */
	private void createListStyle(RenderingContext rc, CssStyleEx style, Style fallbackStyle) {
		
		ImageReference image = (ImageReference) rc.getRP(ListSection.PROPERTY_BULLETS_IMAGE, fallbackStyle);
		if (image != null) {
			String imgUri = ImageManager.getURI(rc,image);
			style.setAttribute("list-style-image", "url(\"" + imgUri + "\")");
			style.setAttribute("list-style-type", "none");
			
		} else {
			String bullets = "";
			int bulletsValue = rc.getRP(ListSection.PROPERTY_BULLETS, fallbackStyle, ListSection.BULLETS_NONE);
			
			switch (bulletsValue) {
				case ListSection.BULLETS_DISC: {
					bullets = "disc";
					break;
				}
				case ListSection.BULLETS_CIRCLE: {
					bullets = "circle";
					break;
				}
				case ListSection.BULLETS_SQUARE: {
					bullets = "square";
					break;
				}
				case ListSection.BULLETS_DECIMAL: {
					bullets = "decimal";
					break;
				}
				case ListSection.BULLETS_DECIMAL_LEADING_ZERO: {
					bullets = "decimal-leading-zero";
					break;
				}
				case ListSection.BULLETS_LOWER_ROMAN: {
					bullets = "lower-roman";
					break;
				}
				case ListSection.BULLETS_UPPER_ROMAN: {
					bullets = "upper-roman";
					break;
				}
				case ListSection.BULLETS_UPPER_LATIN: {
					bullets = "upper-latin";
					break;
				}
				case ListSection.BULLETS_LOWER_LATIN: {
					bullets = "lower-latin";
					break;
				}
				case ListSection.BULLETS_LOWER_GREEK: {
					bullets = "lower-greek";
					break;
				}
				case ListSection.BULLETS_UPPER_ALPHA: {
					bullets = "upper-alpha";
					break;
				}
				case ListSection.BULLETS_LOWER_ALPHA: {
					bullets = "lower-alpha";
					break;
				}
				case ListSection.BULLETS_NONE:
				default: {
					bullets = "none";
				}
			}
			
			style.setAttribute("list-style-type", bullets);
		}
		
	}


	/**
	 * Returns the meaningful text for a given object when sorting our list
	 */
	private String getObjectText(Object o) {
		if (o instanceof AbstractButton) {
			return ((AbstractButton) o).getText();
		}
		if (o instanceof TextComponent) {
			return ((TextComponent) o).getText();
		}
		if (o instanceof LabelEx) {
			return ((LabelEx) o).getText();
		}
		if (o instanceof SelectField) {
			return ((SelectField) o).getSelectedItem().toString();
		}
		if (o == null) {
			return "null";
		}
		return o.toString();
	}

	
	/**
	 * a replacement that uses LI instead of BDO for containership
	 * 
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderReplaceableChild(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, org.w3c.dom.Node,
	 *      nextapp.echo2.app.Component)
	 */
	protected Element renderReplaceableChild(RenderContext rc, ServerComponentUpdate update, Node parentNode, Component child) {
		
		Element containerTagElement = parentNode.getOwnerDocument().createElement("li");
		String containerId = getContainerId(child);
		containerTagElement.setAttribute("id", containerId);
		parentNode.appendChild(containerTagElement);
		ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
		if (syncPeer instanceof DomUpdateSupport) {
			((DomUpdateSupport) syncPeer).renderHtml(rc, update, containerTagElement, child);
		} else {
			syncPeer.renderAdd(rc, update, containerId, child);
		}
		
		CssStyle style = Render.itsDisplayLayoutData(rc, child);
		Render.itsDisplayLayoutData(rc, child, containerTagElement);

		// Nested Lists seem to require the IE6 zoom hack to render correctly
		if (child instanceof ListSection && Render.isClientIE6(rc)) {
			style.setAttribute("zoom", "1");
		}
		
		if (style.hasAttributes()) {
			// set the style on the outer container tag
			containerTagElement.setAttribute("style", style.renderInline());
		}

		return containerTagElement;
	}

}
