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
/*
 * This file was made part of the EchoPointNG project on the 8th of June 2006.
 * It was taken from the NextApp Echo2 project and hence....
 */
/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2005 NextApp, Inc.
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

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import echopointng.EPNG;
import echopointng.TableEx;
import echopointng.able.Stretchable;
import echopointng.layout.TableLayoutDataEx;
import echopointng.ui.resource.Resources;
import echopointng.ui.util.CssStyleEx;
import echopointng.ui.util.Render;
import echopointng.ui.util.RenderingContext;
import echopointng.xhtml.XhtmlFragment;

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.LayoutData;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.Table;
import nextapp.echo2.app.layout.TableLayoutData;
import nextapp.echo2.app.list.ListSelectionModel;
import nextapp.echo2.app.table.TableColumnModel;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.PartialUpdateManager;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.SynchronizePeerFactory;
import nextapp.echo2.webcontainer.image.ImageRenderSupport;
import nextapp.echo2.webcontainer.propertyrender.BorderRender;
import nextapp.echo2.webcontainer.propertyrender.CellLayoutDataRender;
import nextapp.echo2.webcontainer.propertyrender.ColorRender;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webcontainer.propertyrender.FillImageRender;
import nextapp.echo2.webcontainer.propertyrender.FontRender;
import nextapp.echo2.webcontainer.propertyrender.InsetsRender;
import nextapp.echo2.webrender.ClientProperties;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.servermessage.DomUpdate;
import nextapp.echo2.webrender.service.JavaScriptService;
import nextapp.echo2.webrender.util.DomUtil;

/**
 * Synchronization peer for <code>echopoint.TableEx</code> components.
 * 
 */
public class TableExPeer implements ActionProcessor, ComponentSynchronizePeer, DomUpdateSupport, PropertyUpdateProcessor, ImageRenderSupport {

	private static final String[] TABLE_INIT_KEYS = new String[] { "rollover-style", "selection-style" };

	private static final String PROPERTY_SELECTION = "selection";

	private static final String IMAGE_ID_ROLLOVER_BACKGROUND = "rolloverBackground";

	private static final String IMAGE_ID_SELECTION_BACKGROUND = "selectionBackground";

	/**
	 * Service to provide supporting JavaScript library.
	 */
	private static final Service TABLEEX_SERVICE = JavaScriptService.forResource("EPNG.TableEx", "/echopointng/ui/resource/js/tableex.js");

	static {
		WebRenderServlet.getServiceRegistry().add(TABLEEX_SERVICE);
	}

	protected PartialUpdateManager propertyRenderRegistry;

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#getContainerId(nextapp.echo2.app.Component)
	 */
	public String getContainerId(Component child) {
		return ContainerInstance.getElementId(child.getParent()) + "_cell_" + child.getRenderId();
	}

	/**
	 * @see nextapp.echo2.webcontainer.image.ImageRenderSupport#getImage(nextapp.echo2.app.Component,
	 *      java.lang.String)
	 * 
	 * TODO - fix up and use ImageManager
	 */
	public ImageReference getImage(Component component, String imageId) {
		if (IMAGE_ID_ROLLOVER_BACKGROUND.equals(imageId)) {
			FillImage backgroundImage = (FillImage) component.getRenderProperty(Table.PROPERTY_ROLLOVER_BACKGROUND_IMAGE);
			if (backgroundImage == null) {
				return null;
			} else {
				return backgroundImage.getImage();
			}
		} else if (IMAGE_ID_SELECTION_BACKGROUND.equals(imageId)) {
			FillImage backgroundImage = (FillImage) component.getRenderProperty(Table.PROPERTY_SELECTION_BACKGROUND_IMAGE);
			if (backgroundImage == null) {
				return null;
			} else {
				return backgroundImage.getImage();
			}
		} else {
			// Retrieve CellLayoutData background image if applicable.
			return CellLayoutDataRender.getCellLayoutDataBackgroundImage(component, imageId);
		}
	}

	/**
	 * Returns the <code>TableLayoutData</code> of the given child, or null if
	 * it does not provide layout data.
	 * 
	 * @param child
	 *            the child component
	 * @return the layout data
	 * @throws java.lang.RuntimeException
	 *             if the the provided <code>LayoutData</code> is not a
	 *             <code>TableLayoutData</code>
	 */
	private TableLayoutData getLayoutData(Component child) {
		LayoutData layoutData = (LayoutData) child.getRenderProperty(Component.PROPERTY_LAYOUT_DATA);
		if (layoutData == null) {
			return null;
		} else if (layoutData instanceof TableLayoutData) {
			return (TableLayoutData) layoutData;
		} else {
			throw new RuntimeException("Invalid LayoutData for Table Child: " + layoutData.getClass().getName());
		}
	}

	/*
	 * See above
	 */
	private TableLayoutData getLayoutData(XhtmlFragment fragment) {
		LayoutData layoutData = fragment.getLayoutData();
		if (layoutData == null) {
			return null;
		} else if (layoutData instanceof TableLayoutData) {
			return (TableLayoutData) layoutData;
		} else {
			throw new RuntimeException("Invalid LayoutData for Table: " + layoutData.getClass().getName());
		}
	}

	/**
	 * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		String value = actionElement.getAttribute(ActionProcessor.ACTION_VALUE);
		ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, Table.INPUT_ACTION, value);
	}

	/**
	 * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(
	 *      nextapp.echo2.webcontainer.ContainerInstance,
	 *      nextapp.echo2.app.Component, org.w3c.dom.Element)
	 */
	public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
		String propertyName = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
		if (PROPERTY_SELECTION.equals(propertyName)) {
			Element[] optionElements = DomUtil.getChildElementsByTagName(propertyElement, "row");
			int[] selectedIndices = new int[optionElements.length];
			for (int i = 0; i < optionElements.length; ++i) {
				selectedIndices[i] = Integer.parseInt(optionElements[i].getAttribute("index"));
			}
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, Table.SELECTION_CHANGED_PROPERTY, selectedIndices);
		}
		if ("columnWidths".equals(propertyName)) {
			Element[] widthElements = DomUtil.getChildElementsByTagName(propertyElement, "columnWidth");
			int[] widths = new int[widthElements.length];
			for (int i = 0; i < widthElements.length; ++i) {
				widths[i] = Integer.parseInt(widthElements[i].getAttribute("width"));
			}
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, TableEx.COLUMN_WIDTHS_CHANGED_PROPERTY, widths);
		}
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderAdd(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
		Element domAddElement = DomUpdate.renderElementAdd(rc.getServerMessage());
		DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
		renderHtml(rc, update, htmlFragment, component);
		DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, targetId, htmlFragment);
	}

	/**
	 * Renders a child component.
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param update
	 *            the update
	 * @param parentElement
	 *            the HTML element which should contain the child
	 * @param child
	 *            the child component to render
	 */
	private void renderAddChild(RenderingContext rc, Element parentElement, Component child) {
		if (!child.isVisible()) {
			// Do nothing.
			return;
		}
		ServerComponentUpdate update = rc.getServerComponentUpdate();
		ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
		if (syncPeer instanceof DomUpdateSupport) {
			((DomUpdateSupport) syncPeer).renderHtml(rc, update, parentElement, child);
		} else {
			syncPeer.renderAdd(rc, update, getContainerId(child), child);
		}
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(Resources.EP_DRAG_SERVICE.getId());
		rc.getServerMessage().addLibrary(TABLEEX_SERVICE.getId());
		renderDisposeDirective(rc, (Table) component);
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * dispose the state of a table, performing tasks such as unregistering
	 * event listeners on the client.
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param table
	 *            the table
	 */
	private void renderDisposeDirective(RenderContext rc, Table table) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPTableEx.MessageProcessor", "dispose",
				new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(table));
		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		Table table = (Table) update.getParent();
		renderDisposeDirective(rc, table);
		DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(table));
		renderAdd(rc, update, targetId, table);
		return true;
	}

	/**
	 * @see nextapp.echo2.webcontainer.DomUpdateSupport#renderHtml(nextapp.echo2.webcontainer.RenderContext,
	 *      nextapp.echo2.app.update.ServerComponentUpdate, org.w3c.dom.Node,
	 *      nextapp.echo2.app.Component)
	 */
	public void renderHtml(RenderContext rcOrig, ServerComponentUpdate update, Node parentNode, Component component) {
		RenderingContext rc = new RenderingContext(rcOrig, update, component);
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		serverMessage.addLibrary(Resources.EP_DRAG_SERVICE.getId());
		serverMessage.addLibrary(TABLEEX_SERVICE.getId());
		TableEx table = (TableEx) component;

		String elementId = ContainerInstance.getElementId(table);

		Element itemXML = renderInitDirective(rc, table, fallbackStyle);
		Document document = parentNode.getOwnerDocument();

		Border border = (Border) rc.getRP(Table.PROPERTY_BORDER, fallbackStyle);
		Extent borderSize = border == null ? null : border.getSize();
		Extent width = (Extent) rc.getRP(Table.PROPERTY_WIDTH, fallbackStyle);
		Extent height = (Extent) rc.getRP(TableEx.PROPERTY_HEIGHT, fallbackStyle);

		boolean selectionEnabled = rc.getRP(Table.PROPERTY_SELECTION_ENABLED, fallbackStyle, false);
		boolean headerVisible = table.isHeaderVisible();
		boolean footerVisible = table.isFooterVisible();
		int rowCount = table.getModel().getRowCount();

		Insets tableInsets = (Insets) rc.getRP(Table.PROPERTY_INSETS, fallbackStyle);
		String defaultInsetsAttributeValue = tableInsets == null ? "0px" : InsetsRender.renderCssAttributeValue(tableInsets);

		if (rc.getRP(TableEx.PROPERTY_SCROLLABLE, fallbackStyle, false)) {
			// --------------------------------------------------------------
			// 2 Div / 2 Table Strategy Rendering
			// --------------------------------------------------------------
			CssStyleEx style;
			Element outerDivE = document.createElement("div");
			outerDivE.setAttribute("id", elementId);
			outerDivE.setAttribute("style", "padding:0px;margin:0px");
			rc.addStandardWebSupport(component, outerDivE);

			// //////////////////
			// header
			// //////////////////
			if (headerVisible) {
				Element headerDivE = renderResizeableHeaderFooterAreas(rc, table, fallbackStyle, border, defaultInsetsAttributeValue, itemXML,
						TableEx.HEADER_ROW, headerVisible, "header", rowCount);
				outerDivE.appendChild(headerDivE);
			}

			// //////////////////
			// content
			// //////////////////
			Element contentDivE = document.createElement("div");
			style = new CssStyleEx(component, fallbackStyle);
			style.setAttribute("overflow", "auto");
			style.setAttribute("padding", "0px");
			style.setAttribute("margin", "0px");
			ExtentRender.renderToStyle(style, "width", width); // the width of
			// the content
			// div controls
			// everything
			if (rowCount > 0) {
				ExtentRender.renderToStyle(style, "height", height); // the
				// height
				// of
				// the
				// content
				// div
				// controls
				// scrolling
				Render.asBorder(style, border);
				if (headerVisible) {
					style.setAttribute("border-top-width", "0px"); // never set
					// since its
					// done in
					// the
					// header
				}
			}
			contentDivE.setAttribute("style", style.renderInline());
			contentDivE.setAttribute("id", elementId + "_contentDiv");

			Element contentTableE = document.createElement("table");
			style = new CssStyleEx();
			style.setAttribute("table-layout", "fixed");
			style.setAttribute("width", "100%");
			// style.setAttribute("height","100%");
			if (selectionEnabled && rowCount > 0) {
				style.setAttribute("cursor", "pointer");
			}
			contentTableE.setAttribute("style", style.renderInline());
			contentTableE.setAttribute("id", elementId + "_contentTable");
			contentTableE.setAttribute("cellpadding", "0");
			contentTableE.setAttribute("cellspacing", "0");
			contentTableE.setAttribute("border", "0");

			Element colgroupE = renderColGroup(table, document);
			if (colgroupE != null) {
				contentTableE.appendChild(colgroupE);
			}
			Element contentTbodyElement = document.createElement("tbody");

			for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
				renderRow(rc, contentTbodyElement, table, rowIndex, defaultInsetsAttributeValue, itemXML, fallbackStyle);
			}
			contentTableE.appendChild(contentTbodyElement);
			contentDivE.appendChild(contentTableE);
			outerDivE.appendChild(contentDivE);

			// //////////////////
			// footer
			// //////////////////
			if (footerVisible) {
				Element footerDivE = renderResizeableHeaderFooterAreas(rc, table, fallbackStyle, border, defaultInsetsAttributeValue, itemXML,
						TableEx.FOOTER_ROW, footerVisible, "footer", rowCount);
				outerDivE.appendChild(footerDivE);
			}

			parentNode.appendChild(outerDivE);
		} else {
			// --------------------------------------------------------------
			// Single Table Strategy
			// --------------------------------------------------------------
			Element tableElement = document.createElement("table");
			tableElement.setAttribute("id", elementId);
			rc.addStandardWebSupport(component, tableElement);

			CssStyleEx tableCssStyle = new CssStyleEx(component, fallbackStyle);
			tableCssStyle.setAttribute("border-collapse", "collapse");

			if (selectionEnabled) {
				tableCssStyle.setAttribute("cursor", "pointer");
			}
			ColorRender.renderToStyle(tableCssStyle, component);
			FontRender.renderToStyle(tableCssStyle, component);
			BorderRender.renderToStyle(tableCssStyle, border);
			if (borderSize != null) {
				if (!rc.getContainerInstance().getClientProperties().getBoolean(ClientProperties.QUIRK_CSS_BORDER_COLLAPSE_INSIDE)) {
					tableCssStyle.setAttribute("margin", ExtentRender.renderCssAttributeValueHalf(borderSize));
				}
			}

			if (rc.getContainerInstance().getClientProperties().getBoolean(ClientProperties.QUIRK_IE_TABLE_PERCENT_WIDTH_SCROLLBAR_ERROR)) {
				if (width != null && width.getUnits() == Extent.PERCENT && width.getValue() > 95) {
					width = new Extent(95, Extent.PERCENT);
				}
			}
			ExtentRender.renderToStyle(tableCssStyle, "width", width);

			tableElement.setAttribute("style", tableCssStyle.renderInline());

			parentNode.appendChild(tableElement);

			Element colgroupE = renderColGroup(table, document);
			if (colgroupE != null) {
				tableElement.appendChild(colgroupE);
			}
			Element tbodyElement = document.createElement("tbody");
			tbodyElement.setAttribute("id", elementId + "_tbody");
			tableElement.appendChild(tbodyElement);

			// header row
			if (table.isHeaderVisible()) {
				renderRow(rc, tbodyElement, table, TableEx.HEADER_ROW, defaultInsetsAttributeValue, itemXML, fallbackStyle);
			}

			// content rows
			int rows = table.getModel().getRowCount();
			for (int rowIndex = 0; rowIndex < rows; ++rowIndex) {
				renderRow(rc, tbodyElement, table, rowIndex, defaultInsetsAttributeValue, itemXML, fallbackStyle);
			}
			// footer row
			if (table.isFooterVisible()) {
				renderRow(rc, tbodyElement, table, TableEx.FOOTER_ROW, defaultInsetsAttributeValue, itemXML, fallbackStyle);
			}
		}
	}

	private Element renderResizeableHeaderFooterAreas(RenderingContext rc, TableEx table, Style fallbackStyle, Border border,
			String defaultInsetsAttributeValue, Element itemXML, int rowIndex, boolean isVisible, String prefixName, int rowCount) {
		CssStyleEx style;
		Document document = rc.getDocument();

		Element headerFooterDivE = document.createElement("div");
		style = new CssStyleEx(table, fallbackStyle);
		style.setAttribute("width", "100%");
		style.setAttribute("overflow", "hidden");
		style.setAttribute("position", "relative");
		style.setAttribute("padding", "0px");
		style.setAttribute("margin", "0px");

		if (isVisible) {
			if (rowIndex == TableEx.HEADER_ROW) {
				Render.asColor(style, (Color) rc.getRP(TableEx.PROPERTY_HEADER_BACKGROUND, fallbackStyle), "background");
			}
			if (rowIndex == TableEx.FOOTER_ROW) {
				Render.asColor(style, (Color) rc.getRP(TableEx.PROPERTY_FOOTER_BACKGROUND, fallbackStyle), "background");
			}
			if (isVisible && rowIndex == TableEx.HEADER_ROW) {
				//
				// fix up the bottom of the header so it uses the top color not
				// the bottom one
				// the bottom one is for the bottom of the content area
				Render.asBorder(style, border);
				if (rowCount > 0) {
					style.setAttribute("border-bottom-width", style.getAttribute("border-top-width"));
					style.setAttribute("border-bottom-color", style.getAttribute("border-top-color"));
					style.setAttribute("border-bottom-style", style.getAttribute("border-top-style"));
				}
			}
			if (isVisible && rowIndex == TableEx.FOOTER_ROW) {
				//
				// fix up the top of the footer so it has no top border. This is
				// done by the content area
				Render.asBorder(style, border);
				if (rowCount > 0) {
					style.setAttribute("border-top-width", "0px");
				}
			}
		}
		headerFooterDivE.setAttribute("style", style.renderInline());
		headerFooterDivE.setAttribute("id", rc.getElementId() + "_" + prefixName + "Div");

		Element headerFooterScrollerDivE = document.createElement("div");
		style = new CssStyleEx();
		style.setAttribute("width", "100%");
		style.setAttribute("position", "relative");
		style.setAttribute("padding", "0px");
		style.setAttribute("margin", "0px");

		headerFooterScrollerDivE.setAttribute("style", style.renderInline());
		headerFooterScrollerDivE.setAttribute("id", rc.getElementId() + "_" + prefixName + "ScrollerDiv");

		Element headerFooterTableE = document.createElement("table");
		style = new CssStyleEx();
		style.setAttribute("table-layout", "fixed");
		style.setAttribute("width", "100%");
		headerFooterTableE.setAttribute("style", style.renderInline());
		headerFooterTableE.setAttribute("id", rc.getElementId() + "_" + prefixName + "Table");
		headerFooterTableE.setAttribute("cellpadding", "0");
		headerFooterTableE.setAttribute("cellspacing", "0");
		headerFooterTableE.setAttribute("border", "0");

		Element headerFooterTbodyElement = document.createElement("tbody");
		headerFooterTableE.appendChild(headerFooterTbodyElement);

		if (isVisible) {
			renderRow(rc, headerFooterTbodyElement, table, rowIndex, defaultInsetsAttributeValue, itemXML, fallbackStyle);
		}

		headerFooterTableE.appendChild(headerFooterTbodyElement);
		headerFooterScrollerDivE.appendChild(headerFooterTableE);
		headerFooterDivE.appendChild(headerFooterScrollerDivE);

		return headerFooterDivE;
	}

	/**
	 * Called to render the COLGROUP for Table. Done if some of the columns have
	 * widths specified in the column models.
	 * 
	 * @return a Element COLGROUP or null if its not needed
	 */
	protected Element renderColGroup(TableEx table, Document document) {
		TableColumnModel columnModel = table.getColumnModel();
		int columnCount = columnModel.getColumnCount();

		boolean someColumnsHaveWidths = false;
		for (int i = 0; i < columnCount; ++i) {
			if (columnModel.getColumn(i).getWidth() != null) {
				someColumnsHaveWidths = true;
			}
		}
		Element colGroupElement = null;
		if (someColumnsHaveWidths) {
			colGroupElement = document.createElement("colgroup");
			for (int i = 0; i < columnCount; ++i) {
				Element colElement = document.createElement("col");
				Extent columnWidth = columnModel.getColumn(i).getWidth();
				if (columnWidth != null) {
					colElement.setAttribute("width", ExtentRender.renderCssAttributeValue(columnWidth));
				}
				colGroupElement.appendChild(colElement);
			}
		}
		return colGroupElement;
	}

	/**
	 * Renders a single row of a table.
	 * 
	 * @param rc
	 *            the relevant <code>RenderingContext</code>
	 * @param update
	 *            the <code>ServerComponentUpdate</code> being processed
	 * @param tbodyElement
	 *            the <code>tbody</code> element to which to append the
	 *            rendered content
	 * @param table
	 *            the <code>Table</code> being rendered
	 * @param rowIndex
	 *            the row to render
	 * @param defaultInsetsAttributeValue
	 *            the default CSS padding attribute value
	 * @param itemXML -
	 *            the messaging directive XML element
	 */

	protected void renderRow(RenderingContext rc, Element tbodyElement, TableEx table, int rowIndex, String defaultInsetsAttributeValue,
			Element itemXML, Style fallbackStyle) {
		Document document = tbodyElement.getOwnerDocument();
		String elementId = ContainerInstance.getElementId(table);
		boolean isScrollable = rc.getRP(TableEx.PROPERTY_SCROLLABLE, fallbackStyle, false);

		Element trElement = document.createElement("tr");
		if (rowIndex == TableEx.HEADER_ROW) {
			trElement.setAttribute("id", elementId + "_tr_header");
		} else if (rowIndex == TableEx.FOOTER_ROW) {
			trElement.setAttribute("id", elementId + "_tr_footer");
		} else {
			trElement.setAttribute("id", elementId + "_tr_" + rowIndex);
		}
		tbodyElement.appendChild(trElement);

		int columns = table.getColumnModel().getColumnCount();
		int rowCount = table.getModel().getRowCount();
		for (int columnIndex = 0; columnIndex < columns; ++columnIndex) {
			Object cellContent = table.getCellContent(columnIndex, rowIndex);
			if (cellContent == null || cellContent == TableEx.CELL_SPANNER) {
				continue;
			}
			Element tdCellE = document.createElement("td");
			tdCellE.setAttribute("c", String.valueOf(columnIndex));
			trElement.appendChild(tdCellE);

			Element cellContentE = tdCellE;

			CssStyle style = new CssStyle();
			if (!isScrollable) {
                Render.asBorder(style, (Border) rc.getRP(Table.PROPERTY_BORDER, fallbackStyle));
			} else {
				Element cellDivE = document.createElement("div");
				tdCellE.appendChild(cellDivE);
				cellContentE = cellDivE;
			}

			String cellId = null;
			Component renderComponent = table;
			Component childComponent = null;
			TableLayoutData tableLayoutData = null;
			TableLayoutDataEx tableLayoutDataEx = null;
			if (cellContent instanceof XhtmlFragment) {
				XhtmlFragment fragment = (XhtmlFragment) cellContent;
				cellId = elementId + "_cell_col" + columnIndex + "row" + rowIndex;

				tableLayoutData = getLayoutData(fragment);
				if (tableLayoutData instanceof TableLayoutDataEx) {
					tableLayoutDataEx = (TableLayoutDataEx) tableLayoutData;
				}
				// tell the client about the xhtml and let it handle it
				renderXhtmlFragment(rc, cellContentE, itemXML, cellId, fragment, rowCount);
			}
			if (cellContent instanceof Component) {
				childComponent = (Component) cellContent;
				renderComponent = childComponent;
				cellId = elementId + "_cell_" + childComponent.getRenderId();

				tableLayoutData = getLayoutData(childComponent);
				if (tableLayoutData instanceof TableLayoutDataEx) {
					tableLayoutDataEx = (TableLayoutDataEx) tableLayoutData;
				}
			}
			if (childComponent != null) {
				renderAddChild(rc, cellContentE, childComponent);
			}

			//
			// render its UI visual appearance
			if (rowIndex == TableEx.HEADER_ROW) {
				Render.asColor(style, (Color) rc.getRP(TableEx.PROPERTY_HEADER_BACKGROUND, fallbackStyle), "background");
			}
			if (rowIndex == TableEx.FOOTER_ROW) {
				Render.asColor(style, (Color) rc.getRP(TableEx.PROPERTY_FOOTER_BACKGROUND, fallbackStyle), "background");
			}
			CellLayoutDataRender.renderToElementAndStyle(cellContentE, style, renderComponent, tableLayoutData, defaultInsetsAttributeValue);
			if (tableLayoutData != null && tableLayoutData.getBackgroundImage() != null) {
				Render.asBackgroundImage(rc, style, tableLayoutData.getBackgroundImage());
			}
			if (isScrollable) {
				// style.setAttribute("height","100%");
				style.setAttribute("width", "100%");
				style.setAttribute("overflow", "hidden");
				style.setAttribute("white-space", "nowrap");
			}
			cellContentE.setAttribute("style", style.renderInline());
			cellContentE.setAttribute("id", cellId);

			//
			// set colspan/rowspan values if there! Must be done on TD
			if (tableLayoutDataEx != null) {
				if (tableLayoutDataEx.getToolTipText() != null) {
					cellContentE.setAttribute("title", tableLayoutDataEx.getToolTipText());
				}
				int colSpan = tableLayoutDataEx.getColSpan();
				int rowSpan = tableLayoutDataEx.getRowSpan();
				if (colSpan > 1) {
					tdCellE.setAttribute("colspan", String.valueOf(colSpan));
				}
				if (rowSpan > 1) {
					tdCellE.setAttribute("rowspan", String.valueOf(rowSpan));
				}
			}
		}

		//
		// handle action causing and selection causing cells in this row. If all
		// cells are action
		// causing then then there is no need for message to be sent. This is
		// the default
		boolean statusQuo = true;
		for (int columnIndex = 0; columnIndex < columns; ++columnIndex) {
			if (!table.isActionCausingCell(columnIndex, rowIndex) || !table.isSelectionCausingCell(columnIndex, rowIndex)) {
				statusQuo = false;
				break;
			}
		}
		if (!statusQuo) {
			// okay build an actual message for the action/selection causing
			// settings
			//
			// Will look something like c:0;c:7;
			// Note that "true" values are omitted, as we assume "true" is the
			// most
			// common use case and hence some memory / XHR transfer savings can
			// be made
			// by not sending it down.
			//
			StringBuffer actionCausingStr = new StringBuffer();
			StringBuffer selectionCausingStr = new StringBuffer();
			for (int columnIndex = 0; columnIndex < columns; ++columnIndex) {
				if (!table.isActionCausingCell(columnIndex, rowIndex)) {
					actionCausingStr.append("c:");
					actionCausingStr.append(columnIndex);
					actionCausingStr.append(";");
				}
				if (!table.isSelectionCausingCell(columnIndex, rowIndex)) {
					selectionCausingStr.append("c:");
					selectionCausingStr.append(columnIndex);
					selectionCausingStr.append(";");
				}
			}
			Document documentXML = itemXML.getOwnerDocument();
			if (actionCausingStr.length() > 0) {
				Element actionElement = documentXML.createElement("actionCausingCell");
				itemXML.appendChild(actionElement);
				actionElement.setAttribute("row", String.valueOf(rowIndex));
				actionElement.setAttribute("cells", actionCausingStr.toString());
			}

			if (selectionCausingStr.length() > 0) {
				Element selectionElement = documentXML.createElement("selectionCausingCell");
				itemXML.appendChild(selectionElement);
				selectionElement.setAttribute("row", String.valueOf(rowIndex));
				selectionElement.setAttribute("cells", selectionCausingStr.toString());
			}
		}
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * initialize the state of a <code>TableEx</code>, performing tasks such
	 * as registering event listeners on the client.
	 * 
	 * @param rc
	 *            the relevant <code>RenderContext</code>
	 * @param table
	 *            the table
	 * @return - the item messaging Element
	 */
	protected Element renderInitDirective(RenderingContext rc, TableEx table, Style fallbackStyle) {
		String elementId = ContainerInstance.getElementId(table);
		ServerMessage serverMessage = rc.getServerMessage();
		Document document = serverMessage.getDocument();

		boolean rolloverEnabled = rc.getRP(Table.PROPERTY_ROLLOVER_ENABLED, fallbackStyle, false);
		boolean selectionEnabled = rc.getRP(Table.PROPERTY_SELECTION_ENABLED, fallbackStyle, false);
		boolean scrollable = rc.getRP(TableEx.PROPERTY_SCROLLABLE, fallbackStyle, false);
		boolean resizeable = rc.getRP(TableEx.PROPERTY_RESIZEABLE, fallbackStyle, false);
		boolean resizeDragBarUsed = rc.getRP(TableEx.PROPERTY_RESIZE_DRAG_BAR_USED, fallbackStyle, false);
		boolean resizeGrowsTable = rc.getRP(TableEx.PROPERTY_RESIZE_GROWS_TABLE, fallbackStyle, true);
		boolean isFooterVisible = rc.getRP(TableEx.PROPERTY_FOOTER_VISIBLE, fallbackStyle, false);
		boolean ignoreMetaKeys = rc.getRP(TableEx.PROPERTY_IGNORE_META_KEYS, fallbackStyle, false);

		String rolloverStyle = "";
		if (rolloverEnabled) {
			CssStyle rolloverCssStyle = new CssStyle();
			ColorRender.renderToStyle(rolloverCssStyle, (Color) rc.getRP(Table.PROPERTY_ROLLOVER_FOREGROUND, fallbackStyle), (Color) rc.getRP(
					Table.PROPERTY_ROLLOVER_BACKGROUND, fallbackStyle));
			FontRender.renderToStyle(rolloverCssStyle, (Font) rc.getRP(Table.PROPERTY_ROLLOVER_FONT, fallbackStyle));
			FillImageRender.renderToStyle(rolloverCssStyle, rc, this, table, IMAGE_ID_ROLLOVER_BACKGROUND, (FillImage) rc.getRP(
					Table.PROPERTY_ROLLOVER_BACKGROUND_IMAGE, fallbackStyle), FillImageRender.FLAG_DISABLE_FIXED_MODE);
			if (rolloverCssStyle.hasAttributes()) {
				rolloverStyle = rolloverCssStyle.renderInline();
			}
		}

		String selectionStyle = "";
		if (selectionEnabled) {
			CssStyle selectionCssStyle = new CssStyle();
			ColorRender.renderToStyle(selectionCssStyle, (Color) rc.getRP(Table.PROPERTY_SELECTION_FOREGROUND, fallbackStyle), (Color) rc.getRP(
					Table.PROPERTY_SELECTION_BACKGROUND, fallbackStyle));
			FontRender.renderToStyle(selectionCssStyle, (Font) rc.getRP(Table.PROPERTY_SELECTION_FONT, fallbackStyle));
			FillImageRender.renderToStyle(selectionCssStyle, rc, this, table, IMAGE_ID_SELECTION_BACKGROUND, (FillImage) rc.getRP(
					Table.PROPERTY_SELECTION_BACKGROUND_IMAGE, fallbackStyle), FillImageRender.FLAG_DISABLE_FIXED_MODE);
			if (selectionCssStyle.hasAttributes()) {
				selectionStyle = selectionCssStyle.renderInline();
			}
		}

		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPTableEx.MessageProcessor", "init",
				TABLE_INIT_KEYS, new String[] { rolloverStyle, selectionStyle });
		Element itemElement = document.createElement("item");
		itemElement.setAttribute("eid", elementId);
		if (table.isHeaderVisible()) {
			itemElement.setAttribute("header-visible", "true");
		}

		if (table.hasActionListeners()) {
			itemElement.setAttribute("server-notify", "true");
		}

		if (rolloverEnabled) {
			itemElement.setAttribute("rollover-enabled", "true");
		}

		if (selectionEnabled) {
			itemElement.setAttribute("selection-enabled", "true");
			ListSelectionModel selectionModel = table.getSelectionModel();
			if (selectionModel.getSelectionMode() == ListSelectionModel.MULTIPLE_SELECTION) {
				itemElement.setAttribute("selection-mode", "multiple");
			}
			if (selectionModel.getMinSelectedIndex() != -1) {
				Element selectionElement = document.createElement("selection");
				int minimumIndex = selectionModel.getMinSelectedIndex();
				int maximumIndex = selectionModel.getMaxSelectedIndex();
				if (maximumIndex > table.getModel().getRowCount() - 1) {
					maximumIndex = table.getModel().getRowCount() - 1;
				}
				for (int i = minimumIndex; i <= maximumIndex; ++i) {
					if (selectionModel.isSelectedIndex(i)) {
						Element rowElement = document.createElement("row");
						rowElement.setAttribute("index", Integer.toString(i));
						selectionElement.appendChild(rowElement);
					}
				}
				itemElement.appendChild(selectionElement);
			}
		}

		if (!table.isRenderEnabled()) {
			itemElement.setAttribute("enabled", "false");
		}

		itemElement.setAttribute("rowCount", String.valueOf(table.getModel().getRowCount()));

		// Scrollable render properties
		itemElement.setAttribute("scrollable", String.valueOf(scrollable));
		itemElement.setAttribute("resizeable", String.valueOf(resizeable));
		itemElement.setAttribute("resizeDragBarUsed", String.valueOf(resizeDragBarUsed));
		itemElement.setAttribute("resizeGrowsTable", String.valueOf(resizeGrowsTable));
		itemElement.setAttribute("ignoreMetaKeys", String.valueOf(ignoreMetaKeys));
		itemElement.setAttribute("footer-visible", String.valueOf(isFooterVisible));

		// Stretchable properties
		itemElement.setAttribute("heightStretched", String.valueOf(rc.getRP(Stretchable.PROPERTY_HEIGHT_STRETCHED, false)));
		itemElement.setAttribute("minimumStretchedHeight", getExtentPixels(rc.getRP(Stretchable.PROPERTY_MINIMUM_STRETCHED_HEIGHT, null)));
		itemElement.setAttribute("maximumStretchedHeight", getExtentPixels(rc.getRP(Stretchable.PROPERTY_MAXIMUM_STRETCHED_HEIGHT, null)));

		itemizedUpdateElement.appendChild(itemElement);
		return itemElement;
	}

	private String getExtentPixels(Object extent) {
		if (extent instanceof Extent) {
			return String.valueOf(((Extent) extent).getValue());
		}
		return null;
	}

	/**
	 * Creates a messaging fixup "xhtmlfragment" object to allow the XHTML of a
	 * cell to be set at the client via innerHTML.
	 * 
	 * @param rc -
	 *            the RenderingContext in play
	 * @param cellContentE -
	 *            the parent of the XhtmlFragement in play
	 * @param itemXML -
	 *            the messaging item to place the "xhtmlfragment" fixup into
	 * @param cellId -
	 *            the id of the cell that the xhtml belongs to
	 * @param fragment -
	 *            the xhtml to use
	 * @param rowCount -
	 *            the number of rows in the Table
	 */
	protected void renderXhtmlFragment(RenderingContext rc, Element cellContentE, Element itemXML, String cellId, XhtmlFragment fragment, int rowCount) {
		//
		// There is a performance issue related to the user of XhtmlFragments
		// and TableEx. Its initially cheaper
		// to do "client side" fixup via e.innerHTML rather than compile it on
		// the server. However once the number of
		// rows goes beyond a threshold, the cost of locationing elements (via
		// document.getElementById()) ends up to
		// great and it can then take seconds to fixup the Xhtml fragments.
		//
		// What should we do. Compile everything and take a hit on the server
		// regardless? This is still more
		// lightweight than using components as cell content.
		// 
		// Only do client side fixup?
		//
		// Use some sort of heuristic to decide when to do client side fixup and
		// when to do server side compilation?  How many rows is too many?
		//
		String xhtml = fragment.getFragment();
		xhtml = (xhtml == null ? "" : xhtml);
		if (fragment.isJustText()) {
			// this works out to be much faster for large tables if is just text
			cellContentE.appendChild(cellContentE.getOwnerDocument().createTextNode(xhtml));
		} else {
			//
			// our magic limit of rows is..... 0.  It works out faster on IE and to a slight degree FireFox
			if (rowCount > 0) {
				Node fragments[] = null;
				try {
					fragments = fragment.toDOM(cellContentE.getOwnerDocument());
				} catch (Exception e) {
					throw new RuntimeException("The XhtmlFragment is not valid XHTML : " + fragment.getFragment(), e);
				}
				for (int i = 0; i < fragments.length; i++) {
					cellContentE.appendChild(fragments[i]);
				}
			} else {
				Document document = itemXML.getOwnerDocument();
				CDATASection cdata = document.createCDATASection(xhtml);
				Element xhtmlFragment = document.createElement("xhtmlfragment");
				xhtmlFragment.appendChild(cdata);
				xhtmlFragment.setAttribute("cellid", cellId);
				itemXML.appendChild(xhtmlFragment);
			}
		}
	}
}
