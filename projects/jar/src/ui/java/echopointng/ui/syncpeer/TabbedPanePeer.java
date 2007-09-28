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

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Pane;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.SynchronizePeerFactory;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webcontainer.propertyrender.InsetsRender;
import nextapp.echo2.webrender.ServerMessage;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.TabbedPane;
import echopointng.able.Stretchable;
import echopointng.tabbedpane.TabImageRenderer;
import echopointng.tabbedpane.TabModel;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.ImageManager;
import echopointng.ui.util.LayoutStrut;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;
import echopointng.util.ColorKit;

/**
 * <code>TabbedPanePeer</code> is the peer for <code>TabbedPane</code>
 */
public class TabbedPanePeer extends AbstractEchoPointContainerPeer implements ActionProcessor {

	private final static String IMAGE_PREFIX = "tabIcon";

	private final static String LEADIN_IMAGE_PREFIX = "leadIn";

	private final static String LEADOUT_IMAGE_PREFIX = "leadOut";

	/**
	 * Returns a style name for an active Tab Line TD.
	 */
	private String createTabActiveLineStyle(RenderingContext rc, TabbedPane tp, Color borderColor, int borderWidth, int borderStyle,
			int tabBorderStyle, Color tabBackgroundColor) {
		CssStyleEx style = new CssStyleEx();

		if (tp.getModel().getTabImageRenderer() != null) {
			borderWidth = tp.getModel().getTabImageRenderer().getImageBorderWidth();
			borderColor = tp.getModel().getTabImageRenderer().getImageBorderColor();
		}
		style.setAttribute("height", String.valueOf(borderWidth) + "px");

		if (borderColor != null && borderWidth > 0) {
			_setBorderStyle(style, borderColor, borderStyle, /* B */0, /* T */0, /* L */borderWidth, /* R */borderWidth);
		}
		Component tabComponent = tp.getModel().getTabAt(tp,tp.getSelectedIndex(), true);
		Color background;
		if (tp.getModel().getTabImageRenderer() != null) {
			// 
			// if the TabbedPane has been primed right, it will
			// have a background color all set up to go with
			// the image renderer.
			//
			style.setBackground(tabBackgroundColor);
		} else if (tabComponent != null) {
			//
			// make the background the same as the tab component so its bleeds
			// into the color of the tab TD
			//
			background = (Color) tabComponent.getRenderProperty(TabbedPane.PROPERTY_BACKGROUND);
			style.setBackground(background);
		}

		return style.renderInline();
	}

	/**
	 * Returns a style for the active Tab TD
	 */
	private String createTabActiveTDStyle(RenderingContext rc, TabbedPane tp, int tabPlacement, Color borderColor, int borderWidth, int borderStyle,
			int tabBorderStyle) {
		CssStyleEx style = new CssStyleEx();
		if (tabBorderStyle == TabbedPane.TAB_STRIP_ONLY || tabBorderStyle == TabbedPane.TAB_STRIP_AND_CONTENT) {
			if (borderWidth > 0 && borderColor != null) {
				int topWidth = borderWidth;
				int leftWidth = borderWidth;
				int rightWidth = borderWidth;
				int bottomWidth = borderWidth;
				if (tabPlacement == Alignment.BOTTOM) {
					topWidth = 0;
				} else {
					bottomWidth = 0;
				}
				_setBorderStyle(style, borderColor, borderStyle, /* T */topWidth, /* B */bottomWidth, /* L */leftWidth, /* R */rightWidth);
			}
		}
		return style.renderInline();
	}

	/**
	 * Returns a style name for an inactive Tab Line TD.
	 */
	private String createTabInActiveLineStyle(RenderingContext rc, TabbedPane tp, Color borderColor, int borderWidth, int borderStyle,
			int tabBorderStyle) {
		CssStyleEx style = new CssStyleEx();
		if (tp.getModel().getTabImageRenderer() != null) {
			borderColor = tp.getModel().getTabImageRenderer().getImageBorderColor();
		}
		style.setAttribute("height", String.valueOf(borderWidth) + "px");
		if (borderColor != null) {
			style.setBackground(borderColor);
		}
		return style.renderInline();
	}

	/**
	 * Returns a styleName for a normal non selected tab
	 */
	private String createTabInactiveTDStyle(RenderingContext rc, TabbedPane tp, int tabPlacement, Color borderColor, int borderWidth,
			int borderStyle, int tabBorderStyle) {
		// actually the same as ActiveTD as all paremters are the same
		return createTabActiveTDStyle(rc, tp, tabPlacement, borderColor, borderWidth, borderStyle, tabBorderStyle);
	}

	/**
	 * Sets the border Style based on all the border parameters
	 */
	private void _setBorderStyle(CssStyleEx style, Color borderColor, int borderStyle, int topWidth, int bottomWidth, int leftWidth, int rightWidth) {
		if (borderColor == null || leftWidth == 0 || rightWidth == 0)
			return;
		String bStyle = getBorderStyleString(borderStyle);
		String bColor = ColorKit.makeCSSColor(borderColor);

		if (leftWidth > 0) {
			style.setAttribute("border-left-width", String.valueOf(leftWidth) + "px");
			style.setAttribute("border-left-color", bColor);
			style.setAttribute("border-left-style", bStyle);
		}

		if (rightWidth > 0) {
			style.setAttribute("border-right-width", String.valueOf(rightWidth) + "px");
			style.setAttribute("border-right-color", bColor);
			style.setAttribute("border-right-style", bStyle);
		}
		if (topWidth > 0) {
			style.setAttribute("border-top-width", String.valueOf(topWidth) + "px");
			style.setAttribute("border-top-color", bColor);
			style.setAttribute("border-top-style", bStyle);
		}
		if (bottomWidth > 0) {
			style.setAttribute("border-bottom-width", String.valueOf(bottomWidth) + "px");
			style.setAttribute("border-bottom-color", bColor);
			style.setAttribute("border-bottom-style", bStyle);
		}
	}

	/**
	 * Sets the border properties if we have some border values, into the
	 * CssStyle.
	 */
	private void setContentBorderStyle(CssStyleEx style, TabbedPane tp, int tabPlacement, Color borderColor, int borderWidth, int borderStyle, int tabBorderStyle) {
		if (borderColor == null || borderWidth < 0)
			return;
		if (tabBorderStyle == TabbedPane.TAB_STRIP_AND_CONTENT || tabBorderStyle == TabbedPane.TAB_LINE_AND_CONTENT) {
			if (borderWidth > 0 && borderColor != null) {
				int topWidth = borderWidth;
				int leftWidth = borderWidth;
				int rightWidth = borderWidth;
				int bottomWidth = borderWidth;
				if (tabPlacement == Alignment.BOTTOM) {
					bottomWidth = 0;
				} else {
					topWidth = 0;
				}
				_setBorderStyle(style, borderColor, borderStyle, /* T */topWidth, /* B */bottomWidth, /* L */leftWidth, /* R */rightWidth);
			}
		}
		
	}

	/**
	 * Draws a child component
	 */
	private void drawChild(RenderingContext rc, Component child, Element parent, String containerId) {
		ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
		if (syncPeer instanceof DomUpdateSupport) {
			((DomUpdateSupport) syncPeer).renderHtml(rc, rc.getServerComponentUpdate(), parent, child);
		} else {
			syncPeer.renderAdd(rc, rc.getServerComponentUpdate(), containerId, child);
		}
	}

	private String getBorderStyleString(int borderStyle) {
		switch (borderStyle) {
		case Border.STYLE_DASHED:
			return "dashed";
		case Border.STYLE_DOTTED:
			return "dotted";
		case Border.STYLE_DOUBLE:
			return "double";
		case Border.STYLE_GROOVE:
			return "groove";
		case Border.STYLE_INSET:
			return "inset";
		case Border.STYLE_NONE:
			return "none";
		case Border.STYLE_OUTSET:
			return "outset";
		case Border.STYLE_RIDGE:
			return "ridge";
		case Border.STYLE_SOLID:
			return "solid";
		default:
			return "";
		}
	}

	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		String actionName = actionElement.getAttribute(ActionProcessor.ACTION_NAME);
		String actionValue = actionElement.getAttribute(ActionProcessor.ACTION_VALUE);

		ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, actionName, actionValue);
	}

	/**
	 * Called to register all images with the ImageManager
	 */
	private void registerAllImages(TabbedPane tp, ImageManager iman) {
		int len = tp.size();
		for (int i = 0; i < len; i++) {
			registerSingleImage(tp, i, iman);
		}
		ImageReference lead;
		TabModel model = tp.getModel();
		lead = (model.getTabImageRenderer() == null ? null : model.getTabImageRenderer().getLeadInImage(tp));
		if (lead != null)
			iman.setImage(LEADIN_IMAGE_PREFIX, lead);
		lead = (model.getTabImageRenderer() == null ? null : model.getTabImageRenderer().getLeadOutImage(tp));
		if (lead != null)
			iman.setImage(LEADOUT_IMAGE_PREFIX, lead);
	}

	/**
	 * Called to register and image with the ImageManager
	 */
	private void registerSingleImage(TabbedPane tp, int index, ImageManager iman) {
		if (tp.getModel().getTabImageRenderer() != null) {
			int selectedIndex = tp.getSelectedIndex();
			if (selectedIndex == -1)
				selectedIndex = 0;
			boolean isSelected = index == selectedIndex;

			Component tabComponent = tp.getModel().getTabAt(tp,index, isSelected);
			ImageReference imageRef = tp.getModel().getTabImageRenderer().getTabImage(tp, index, tabComponent, isSelected);
			if (imageRef == null)
				throw new IllegalStateException("The TabImageRenderer must return a non null active image at index : " + index);

			iman.setImage(IMAGE_PREFIX + index, imageRef);
		}
	}

	/**
	 * Renders the actual contents of the TabbedPane
	 */
	private void renderComplexTabContents(RenderingContext rc, TabbedPane tp, Element contentParent, Color borderColor, int borderWidth, int borderStyle) {
		Style fallbackStyle = EPNG.getFallBackStyle(tp);
		int tabBorderStyle = rc.getRP(TabbedPane.PROPERTY_TAB_BORDER_STYLE, fallbackStyle, TabbedPane.TAB_STRIP_AND_CONTENT);

		TabImageRenderer renderer = tp.getModel().getTabImageRenderer();
		int index = tp.getSelectedIndex();
		Component tabContent = tp.getModel().getTabContentAt(index);
		Component tabComponent = tp.getModel().getTabAt(tp,index, true);

		CssStyleEx styleContentParent = new CssStyleEx();
		Color background = (Color) rc.getRP(TabbedPane.PROPERTY_BACKGROUND, fallbackStyle);
		if (renderer != null) {
			// 
			// if the TabbedPane has been primed right, it will
			// have a background color all set up to go with
			// the image renderer.
			//
			styleContentParent.setBackground(background);
		} else if (tabComponent != null) {
			//
			// make the background the same as the tab component so its bleeds
			// into the color of the tab TD
			//
			background = (Color) tabComponent.getRenderProperty(TabbedPane.PROPERTY_BACKGROUND);
			styleContentParent.setBackground(background);
		}
		int tabPlacement = rc.getRP(TabbedPane.PROPERTY_TAB_PLACEMENT, fallbackStyle, Alignment.TOP);

		setContentBorderStyle(styleContentParent, tp, tabPlacement, borderColor, borderWidth, borderStyle, tabBorderStyle);
		//
		// special attributes for the content parent
		InsetsRender.renderToStyle(styleContentParent, "padding", (Insets) rc.getRP(TabbedPane.PROPERTY_INSETS, fallbackStyle));
		// the height of the TabbedPane is set via the content parent
		Extent heightExtent = (Extent) rc.getRP(TabbedPane.PROPERTY_HEIGHT, fallbackStyle);
		if (heightExtent != null) {
			ExtentRender.renderToStyle(styleContentParent, "height", heightExtent);
		}
		styleContentParent.setAttribute("vertical-align", "top");
		styleContentParent.setAttribute("overflow", "hidden");
		
		contentParent.setAttribute("style", styleContentParent.renderInline());
		if (tabContent != null) {
			CssStyleEx styleContentInner = new CssStyleEx();
			CssStyleEx styleContentOuter = new CssStyleEx();
			Render.asScrollable(styleContentOuter, tp, fallbackStyle);

			if (tabContent instanceof Pane) {
				//
				// because we are a PaneContainer, we must have a height in
				// order for the SplitPanes and so to work and we must be
				// position:relative
				styleContentInner.setAttribute("height", "400px");
				styleContentInner.setAttribute("position", "relative");
			}
			if (heightExtent != null) {
				ExtentRender.renderToStyle(styleContentInner, "height", heightExtent);
			}

			String outerContentId = rc.getElementId() + "OuterContent";
			String contentContainerId = getContainerId(tabContent);
			
			//
			// we have an outer content div that we can stretch if need be
			Element outerContentDiv = rc.createE("div");
			outerContentDiv.setAttribute("id", outerContentId);
			outerContentDiv.setAttribute("style", styleContentOuter.renderInline());

			//
			// then we have a content div that has the x_contains_y id as required by Echo2 
			Element innerContentDiv = rc.createE("div");
			innerContentDiv.setAttribute("id", contentContainerId);
			innerContentDiv.setAttribute("style", styleContentInner.renderInline());
			//
			// put the content children into the content div
			drawChild(rc, tabContent, innerContentDiv, contentContainerId);

			// whose parent is whose 
			contentParent.appendChild(outerContentDiv);
			outerContentDiv.appendChild(innerContentDiv);

			boolean heightStretched = rc.getRP(Stretchable.PROPERTY_HEIGHT_STRETCHED, fallbackStyle, false);
			if (heightStretched) {
				renderInitDirective(rc, tabComponent, outerContentId);
			}

		}
	}

	
	/**
	 * Renders a complex tab interface.
	 */
	private void renderComplexTabs(RenderingContext rc, TabbedPane tp, Node parentNode, ImageManager imageManager) {
		Style fallbackStyle = EPNG.getFallBackStyle(tp);
		TabImageRenderer renderer = tp.getModel().getTabImageRenderer();
		boolean tabImageOnly = (renderer != null);

		int tabCount = tp.size();
		int selectedIndex = tp.getSelectedIndex();

		Border border = (Border) rc.getRP(TabbedPane.PROPERTY_BORDER, fallbackStyle);
		Color borderColor = (border == null) ? null : border.getColor();
		int borderWidth = (border == null) ? 0 : (border.getSize() == null ? 0 : border.getSize().getValue());
		int borderStyle = (border == null) ? 0 : border.getStyle();
		if (renderer != null) {
			borderColor = renderer.getImageBorderColor();
			borderWidth = renderer.getImageBorderWidth();
		}

		int tabSpacing = rc.getRP(TabbedPane.PROPERTY_TAB_SPACING, fallbackStyle, 5);
		int tabPlacement = rc.getRP(TabbedPane.PROPERTY_TAB_PLACEMENT, fallbackStyle, Alignment.TOP);
		int tabBorderStyle = rc.getRP(TabbedPane.PROPERTY_TAB_BORDER_STYLE, fallbackStyle, TabbedPane.TAB_STRIP_AND_CONTENT);
		Extent tabLeadInWidth = (Extent) rc.getRP(TabbedPane.PROPERTY_TAB_LEAD_IN_WIDTH, fallbackStyle);
		Color tabBackground = (Color) rc.getRP(TabbedPane.PROPERTY_BACKGROUND, fallbackStyle);

		String tabActiveLineStyle = createTabActiveLineStyle(rc, tp, borderColor, borderWidth, borderStyle, tabBorderStyle, tabBackground);
		String tabActiveTabTDBorderStyle = createTabActiveTDStyle(rc, tp, tabPlacement, borderColor, borderWidth, borderStyle, tabBorderStyle);
		String tabInactiveLineStyle = createTabInActiveLineStyle(rc, tp, borderColor, borderWidth, borderStyle, tabBorderStyle);
		String tabInActiveTabTDBorderStyle = createTabInactiveTDStyle(rc, tp, tabPlacement, borderColor, borderWidth, borderStyle, tabBorderStyle);

		Element td;
		Element imgE;
		ImageReference icon;

		// -------------------------------------
		// Tab Contents Creation
		// -------------------------------------
		Element trContents = rc.createE("tr");
		td = rc.createE("td");
		trContents.appendChild(td);
		renderComplexTabContents(rc, tp, td, borderColor, borderWidth, borderStyle);

		// -------------------------------------
		// Tab Item Creation
		// -------------------------------------
		Element trTabItems = rc.createE("tr");
		td = rc.createE("td");
		trTabItems.appendChild(td);

		//
		// table for the tab items themselves
		Element tableTabs = rc.createE("table");
		td.appendChild(tableTabs);
		tableTabs.setAttribute("border", "0");
		tableTabs.setAttribute("cellpadding", "0");
		tableTabs.setAttribute("cellspacing", "0");
		tableTabs.setAttribute("width", "100%");

		Element tbodyTabs = rc.createE("tbody");
		tableTabs.appendChild(tbodyTabs);

		Element trItems = rc.createE("tr");

		//
		// Lead in Image if available
		ImageReference leadInIcon = imageManager.getImage(LEADIN_IMAGE_PREFIX);
		icon = leadInIcon;
		if (icon != null) {
			td = rc.createE("td");
			trItems.appendChild(td);
			td.setAttribute("valign", tabPlacement == Alignment.TOP ? "bottom" : "top");

			imgE = ImageManager.createImgE(rc, null, icon);
			td.appendChild(imgE);
		}
		// a lead in width may be specified
		if (tabLeadInWidth != null && tabLeadInWidth.getValue() > 0) {
			td = rc.createE("td");
			trItems.appendChild(td);
			td.appendChild(LayoutStrut.createStrut(rc, tabLeadInWidth, new Extent(0)));
		}

		for (int index = 0; index < tabCount; index++) {
			boolean isSelected = (selectedIndex == index);

			// -------------------------------------
			// Tab Cell Creation
			// -------------------------------------

			td = rc.createE("td");
			trItems.appendChild(td);

			Component tabComponent = tp.getModel().getTabAt(tp,index, isSelected);
			String tabCellId = getContainerId(tabComponent);
			if (tabImageOnly) {
				//
				// from the tabimagerenderer icon handling
				String imagePrefix = IMAGE_PREFIX + index;
				icon = imageManager.getImage(imagePrefix);

				imgE = ImageManager.createImgE(rc, null, icon);

				Element anchorE = rc.createE("a");
				if (tp.isRenderEnabled()) {
					anchorE.setAttribute("href", "javascript:EP.Event.hrefActionHandler('" + rc.getElementId() + "','click','"
							+ String.valueOf(index) + "')");
				} else {
					anchorE.setAttribute("href", "javascript:void");
				}
				anchorE.appendChild(imgE);
				td.appendChild(anchorE);

				// if we are a tab as an image, then
				Element noDisplayE = rc.createE("bdo");
				noDisplayE.setAttribute("id", tabCellId);
				noDisplayE.setAttribute("style", "display:none");
				td.appendChild(noDisplayE);
			} else {
				//
				// we have a child component that needs rendering
				td.setAttribute("id", tabCellId);
				drawChild(rc, tabComponent, td, tabCellId);
			}
			//
			// does it require a style inside the TD
			if (!tabImageOnly) {
				if (isSelected) {
					td.setAttribute("style", tabActiveTabTDBorderStyle);
				} else {
					td.setAttribute("style", tabInActiveTabTDBorderStyle);
				}
			}
			td.setAttribute("align", "center");
			if (tabPlacement == Alignment.TOP) {
				td.setAttribute("valign", "bottom");
			} else {
				td.setAttribute("valign", "top");
			}
			//
			// spacer please
			if (index < tabCount - 1) {
				td = rc.createE("td");
				trItems.appendChild(td);
				td.appendChild(LayoutStrut.createStrut(rc, tabSpacing, 1));
			}
		}

		//
		// Lead Out Image if available
		ImageReference leadOutIcon = imageManager.getImage(LEADOUT_IMAGE_PREFIX);
		icon = leadOutIcon;
		if (icon != null) {
			td = rc.createE("td");
			trItems.appendChild(td);
			td.setAttribute("valign", tabPlacement == Alignment.TOP ? "bottom" : "top");

			imgE = ImageManager.createImgE(rc, null, icon);
			td.appendChild(imgE);
		}

		//
		// wide TD to force a spring fill of the rest of the box
		td = rc.createE("td");
		trItems.appendChild(td);
		td.setAttribute("width", "100%");
		td.appendChild(LayoutStrut.createStrut(rc, 1, 1));

		// -------------------------------------
		// Tab Line Row Creation
		// -------------------------------------
		if (borderWidth > 0 && borderColor != null) {
			Element trLines = rc.createE("tr");
			

			int colspanPrev = selectedIndex * 2;
			int colspanPost = ((tabCount - selectedIndex) * 2);
			if (leadInIcon != null) {
				colspanPrev += 1;
			}
			if (leadOutIcon != null)
				colspanPost += 1;
			
			// a lead in width may be specified
			if (tabLeadInWidth != null && tabLeadInWidth.getValue() > 0) {
				td = rc.createE("td");
				td.setAttribute("style", tabInactiveLineStyle);
				trLines.appendChild(td);
				td.appendChild(LayoutStrut.createStrut(rc, tabLeadInWidth, new Extent(0)));
			}
			
			if (selectedIndex > 0 || leadInIcon != null) {
				// inactive tab pre part
				td = rc.createE("td");
				trLines.appendChild(td);
				td.setAttribute("style", tabInactiveLineStyle);
				td.setAttribute("colspan", "" + colspanPrev);
				// td.appendChild(LayoutStrut.createStrut(rc, 1, borderWidth));
			}
			// active tab part
			td = rc.createE("td");
			trLines.appendChild(td);
			td.setAttribute("style", tabActiveLineStyle);
			td.setAttribute("colspan", "1");
			// td.appendChild(LayoutStrut.createStrut(rc, 1, borderWidth));

			// inactive tab post part
			td = rc.createE("td");
			trLines.appendChild(td);
			td.setAttribute("style", tabInactiveLineStyle);
			td.setAttribute("colspan", "" + colspanPost);
			// td.appendChild(LayoutStrut.createStrut(rc, 1, borderWidth));

			if (tabPlacement == Alignment.TOP) {
				tbodyTabs.appendChild(trItems);
				tbodyTabs.appendChild(trLines);
			} else {
				tbodyTabs.appendChild(trLines);
				tbodyTabs.appendChild(trItems);
			}
		} else {
			tbodyTabs.appendChild(trItems);
		}

		// -------------------------------------
		// Tab Outer Table Creation
		// -------------------------------------
		Element table = rc.createE("table");
		table.setAttribute("border", "0");
		table.setAttribute("cellpadding", "0");
		table.setAttribute("cellspacing", "0");

		Extent width = (Extent) rc.getRP(TabbedPane.PROPERTY_WIDTH, fallbackStyle);
		if (width != null) {
			table.setAttribute("width", width.toString());
		} else {
			table.setAttribute("width", "100%");
		}
		Element tbody = rc.createE("tbody");
		table.appendChild(tbody);

		if (tabPlacement == Alignment.TOP) {
			tbody.appendChild(trTabItems);
			tbody.appendChild(trContents);
		} else {
			tbody.appendChild(trContents);
			tbody.appendChild(trTabItems);
		}
		//
		// out outer most element is a DIV
		Element div = rc.createE("div");
		CssStyleEx style = new CssStyleEx();
		InsetsRender.renderToStyle(style, "margin", (Insets) rc.getRP(TabbedPane.PROPERTY_OUTSETS, fallbackStyle));

		div.setAttribute("style", style.renderInline());
		div.setAttribute("id", rc.getElementId());
		rc.addStandardWebSupport(div);
		div.appendChild(table);
		parentNode.appendChild(div);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(echopointng.ui.util.RenderingContext,
	 *      Node, nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		TabbedPane tp = (TabbedPane) component;

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(Resources.EP_STRETCH_SERVICE);

		ImageManager imageManager = (ImageManager) retreiveRenderState(rc,component);
		if (imageManager == null) {
			imageManager = new ImageManager();
			storeRenderState(rc, component, imageManager);
		}
		registerAllImages(tp, imageManager);
		renderComplexTabs(rc, tp, parent, imageManager);
	}

	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rcOrig, ServerComponentUpdate update, String targetId) {
		RenderingContext rc = new RenderingContext(rcOrig,update,update.getParent());
		renderDisposeDirective(rc, update.getParent());
		return renderUpdateBaseImpl(rc, update, targetId, true);
	}
	
	/**
	 * @see echopointng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate, nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rcOrig, ServerComponentUpdate update, Component component) {
		RenderingContext rc = new RenderingContext(rcOrig,update,component);
		super.renderDispose(rc, update, component);
		renderDisposeDirective(rc, component);
	}

	private void renderDisposeDirective(RenderingContext rc, Component component) {
		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(Resources.EP_STRETCH_SERVICE);

		ServerMessage serverMessage = rc.getServerMessage();
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EP.Stretch.MessageProcessor",
				"dispose", new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		
		String outerContentId = rc.getElementId() + "OuterContent";
		itemElement.setAttribute("eid", outerContentId);
		itemizedUpdateElement.appendChild(itemElement);
	}

	private void renderInitDirective(RenderingContext rc, Component component, String contentContainerId) {
		//
		// render a directive to stretch the tab content area if necessary
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EP.Stretch.MessageProcessor",
				"init", new String[0], new String[0]);
		Element itemElement = rc.getDocument().createElement("item");
		itemElement.setAttribute("eid", contentContainerId);
		itemElement.setAttribute("heightStretched", String.valueOf(rc.getRP(Stretchable.PROPERTY_HEIGHT_STRETCHED, false)));
		itemElement.setAttribute("minimumStretchedHeight", getExtentPixels(rc.getRP(Stretchable.PROPERTY_MINIMUM_STRETCHED_HEIGHT, null)));
		itemElement.setAttribute("maximumStretchedHeight", getExtentPixels(rc.getRP(Stretchable.PROPERTY_MAXIMUM_STRETCHED_HEIGHT, null)));
				
		itemizedUpdateElement.appendChild(itemElement);
	}

	private String getExtentPixels(Object extent) {
		if (extent instanceof Extent) {
			return String.valueOf(((Extent)extent).getValue());
		}
		return null;
	}

}
