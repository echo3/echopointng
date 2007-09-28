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

import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.Table;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.table.DefaultTableModel;
import nextapp.echo2.app.table.TableCellRenderer;
import nextapp.echo2.app.table.TableColumn;
import nextapp.echo2.app.table.TableColumnModel;
import nextapp.echo2.app.table.TableModel;
import echopointng.able.Attributeable;
import echopointng.able.Borderable;
import echopointng.able.Insetable;
import echopointng.able.MouseCursorable;
import echopointng.able.ScrollBarProperties;
import echopointng.able.Scrollable;
import echopointng.able.Sizeable;
import echopointng.able.ToolTipable;
import echopointng.table.DefaultLiveTableRenderer;
import echopointng.table.LiveTableRenderer;
import echopointng.table.LiveTableService;
import echopointng.util.ColorKit;

/**
 * THIS CLASS IS CURRENTLY EXPIRMENTAL AND HENCE IT AND ALL ASSOCIATED
 * CLASS AND INTERFACES SHOULD BE CONSIDERED NOT IN THEIR FINAL form
 * <p> 
 * <code>LiveTable</code> is a specialised form of Table that provides support
 * for only loading "some of the data" in a large TableModel.
 * 
 * It does this by dividing the TableModel rows into a series of "virtual pages"
 * which can be asked for at any time.
 * 
 * <code>LiveTable</code> assumes that the height of each row is the same.
 * This allows it to know what rows have been scrolled into the viewport and
 * hence what rows to ask for from the TableModel dynamically. It is therefore
 * impertive that the rowHeight property have a value in order for the LiveTable
 * to scroll correctly.
 * <p>
 */

public class LiveTable extends Table implements Sizeable, Scrollable, Borderable, Insetable, MouseCursorable, ToolTipable, Attributeable {

	/**
	 * This DEFAULT_STYLE is applied to the LiveTable to give it a series of
	 * borders and background colors
	 */
	public static final Style DEFAULT_STYLE;

	public static final String PROPERTY_PAGE_FETCH_SIZE = "pageFetchSize";

	public static final String PROPERTY_RENDERER = "renderer";

	public static final String PROPERTY_ROW_HEIGHT = "rowsHeight";

	public static final String PROPERTY_ROWS_PER_PAGE = "rowsPerPage";

	//    public static final String PROPERTY_ROLLOVER_BACKGROUND =
	// "rolloverBackground";
	//    public static final String PROPERTY_ROLLOVER_BACKGROUND_IMAGE =
	// "rolloverBackgroundImage";
	//    public static final String PROPERTY_ROLLOVER_ENABLED = "rolloverEnabled";
	//    public static final String PROPERTY_ROLLOVER_FONT = "rolloverFont";
	//    public static final String PROPERTY_ROLLOVER_FOREGROUND =
	// "rolloverForeground";

	static {
		MutableStyleEx style = new MutableStyleEx();

		style.setProperty(Borderable.PROPERTY_BORDER, new BorderEx(ColorKit.makeColor("#ACBCDC")));

		style.setProperty(PROPERTY_HEIGHT, new Extent(400, Extent.PX));
		style.setProperty(PROPERTY_ROWS_PER_PAGE, 100);
		style.setProperty(PROPERTY_PAGE_FETCH_SIZE, 2);
		style.setProperty(PROPERTY_ROW_HEIGHT, new Extent(25));
		style.setProperty(PROPERTY_RENDERER, new DefaultLiveTableRenderer());
		style.setProperty(PROPERTY_SCROLL_BAR_POLICY, Scrollable.AUTO);
		DEFAULT_STYLE = style;
	}

	private Map attributeMap;

	/**
	 * Creates a new <code>LiveTable</code> with an empty
	 * <code>DefaultTableModel</code>.
	 */
	public LiveTable() {
		this(new DefaultTableModel());
	}

	/**
	 * Creates a new <code>LiveTable</code> with a new
	 * <code>DefaultTableModel</code> with the specified dimensions.
	 * 
	 * @param columns
	 *            the initial column count
	 * @param rows
	 *            the initial row count
	 */
	public LiveTable(int columns, int rows) {
		this(new DefaultTableModel(columns, rows));
	}

	/**
	 * Creates a <code>LiveTable</code> using the supplied
	 * <code>TableModel</code>.
	 * 
	 * @param model
	 *            the initial model
	 */
	public LiveTable(TableModel model) {
		this(model, null);
	}

	/**
	 * Creates a <code>LiveTable</code> with the supplied
	 * <code>TableModel</code> and the specified <code>TableColumnModel</code>.
	 * 
	 * @param model
	 *            the initial model
	 * @param columnModel
	 *            the initial column model
	 */
	public LiveTable(TableModel model, TableColumnModel columnModel) {
		super(model, columnModel);
		setFocusTraversalParticipant(false);
	}

	/**
	 * @see nextapp.echo2.app.Component#dispose()
	 */
	public void dispose() {
		super.dispose();
		LiveTableService.INSTANCE.deregister(this);
	}

	/**
	 * LiveTable implementation only handles headers cells as child components not the
	 * main TableModel data rows.  This allows it much bigger data sets.
	 */
	protected void doRender() {
		TableModel model = getModel();
		TableColumnModel columnModel = getColumnModel();
		removeAll();
		int columnCount = columnModel.getColumnCount();

		TableColumn[] tableColumns = new TableColumn[columnCount];

		for (int columnIndex = 0; columnIndex < columnCount; ++columnIndex) {
			tableColumns[columnIndex] = columnModel.getColumn(columnIndex);
		}

		if (isHeaderVisible()) {
			for (int columnIndex = 0; columnIndex < columnCount; ++columnIndex) {
				int modelColumnIndex = tableColumns[columnIndex].getModelIndex();
				Object headerValue = tableColumns[columnIndex].getHeaderValue();
				if (headerValue == null) {
					headerValue = model.getColumnName(modelColumnIndex);
				}
				TableCellRenderer headerRenderer = tableColumns[columnIndex].getHeaderRenderer();
				if (headerRenderer == null) {
					headerRenderer = getDefaultHeaderRenderer();
					if (headerRenderer == null) {
						headerRenderer = DEFAULT_TABLE_CELL_RENDERER;
					}
				}
				Component renderedComponent = headerRenderer.getTableCellRendererComponent(this, headerValue, modelColumnIndex, HEADER_ROW);
				if (renderedComponent == null) {
					renderedComponent = new Label();
				}
				add(renderedComponent);
			}
		}
	}

	/**
	 * Fires an action event to all listeners.
	 */
	private void fireActionEvent(Object value) {
		if (!hasEventListenerList()) {
			return;
		}
		EventListener[] listeners = getEventListenerList().getListeners(ActionListener.class);
		ActionEvent e = null;
		for (int i = 0; i < listeners.length; ++i) {
			if (e == null) {
				e = new ActionEvent(this, String.valueOf(value));
			}
			((ActionListener) listeners[i]).actionPerformed(e);
		}
	}

	/**
	 * @see echopointng.able.Attributeable#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String attributeName) {
		if (attributeMap != null) {
			return attributeMap.get(attributeName);
		}
		return null;
	}

	/**
	 * @see echopointng.able.Attributeable#getAttributeNames()
	 */
	public String[] getAttributeNames() {
		if (attributeMap == null) {
			return new String[0];
		}
		int count = 0;
		String[] attributeNames = new String[attributeMap.keySet().size()];
		for (Iterator iter = attributeMap.keySet().iterator(); iter.hasNext();) {
			attributeNames[count++] = (String) iter.next();
		}
		return attributeNames;
	}

	/**
	 * @see echopointng.able.Heightable#getHeight()
	 */
	public Extent getHeight() {
		return (Extent) getProperty(PROPERTY_HEIGHT);
	}

	/**
	 * @see echopointng.able.MouseCursorable#getMouseCursor()
	 */
	public int getMouseCursor() {
		return ComponentEx.getProperty(this, PROPERTY_MOUSE_CURSOR, CURSOR_AUTO);
	}

	/**
	 * @see echopointng.able.MouseCursorable#getMouseCursorUri()
	 */
	public String getMouseCursorUri() {
		return (String) getProperty(PROPERTY_MOUSE_CURSOR_URI);
	}

	/**
	 * @see echopointng.able.Insetable#getOutsets()
	 */
	public Insets getOutsets() {
		return (Insets) getProperty(PROPERTY_OUTSETS);
	}

	/**
	 * The pageFetchSize property controls how many pages (and hence rows) are
	 * "fetched" when a TableModel interaction is performed. A higher
	 * pageFetchSize will mean less server interactions but more network
	 * bandwidth.
	 * 
	 * @return - the pageFetchSize
	 */
	public int getPageFetchSize() {
		return ComponentEx.getProperty(this, PROPERTY_PAGE_FETCH_SIZE, 1);
	}

	public LiveTableRenderer getRenderer() {
		return (LiveTableRenderer) getProperty(PROPERTY_RENDERER);
	}

	public Extent getRowHeight() {
		return (Extent) getProperty(PROPERTY_ROW_HEIGHT);
	}

	public int getRowsPerPage() {
		return ComponentEx.getProperty(this, PROPERTY_ROWS_PER_PAGE, 100);
	}

	/**
	 * @see echopointng.able.Scrollable#getScrollBarBaseColor()
	 */
	public Color getScrollBarBaseColor() {
		return (Color) getProperty(PROPERTY_SCROLL_BAR_BASE_COLOR);
	}

	/**
	 * @see echopointng.able.Scrollable#getScrollBarPolicy()
	 */
	public int getScrollBarPolicy() {
		return ComponentEx.getProperty(this, PROPERTY_SCROLL_BAR_POLICY, Scrollable.AUTO);
	}

	/**
	 * @see echopointng.able.Scrollable#getScrollBarProperties()
	 */
	public ScrollBarProperties getScrollBarProperties() {
		return (ScrollBarProperties) getProperty(PROPERTY_SCROLL_BAR_PROPERTIES);
	}

	/**
	 * @see echopointng.able.ToolTipable#getToolTipText()
	 */
	public String getToolTipText() {
		return (String) getProperty(PROPERTY_TOOL_TIP_TEXT);
	}

	/**
	 * @see nextapp.echo2.app.Component#init()
	 */
	public void init() {
		super.init();
		LiveTableService.INSTANCE.register(this);
	}

	/**
	 * @see nextapp.echo2.app.Component#processInput(java.lang.String,
	 *      java.lang.Object)
	 */
	public void processInput(String name, Object value) {
		super.processInput(name, value);
		if ("click".equals(name)) {
			fireActionEvent(value);
		}
	}

	/**
	 * @see echopointng.able.Attributeable#setAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setAttribute(String attributeName, Object attributeValue) {
		if (attributeMap == null) {
			attributeMap = new HashMap();
		}
		attributeMap.put(attributeName, attributeValue);
	}

	/**
	 * The <code>LiveTable</code> must have a height value set.
	 * 
	 * @see echopointng.able.Heightable#setHeight(nextapp.echo2.app.Extent)
	 */
	public void setHeight(Extent newValue) {
		setProperty(PROPERTY_HEIGHT, newValue);
	}

	/**
	 * @see echopointng.able.MouseCursorable#setMouseCursor(int)
	 */
	public void setMouseCursor(int mouseCursor) {
		ComponentEx.setProperty(this, PROPERTY_MOUSE_CURSOR, mouseCursor);
	}

	/**
	 * @see echopointng.able.MouseCursorable#setMouseCursorUri(java.lang.String)
	 */
	public void setMouseCursorUri(String mouseCursorURI) {
		setProperty(PROPERTY_MOUSE_CURSOR_URI, mouseCursorURI);
	}

	/**
	 * @see echopointng.able.Insetable#setOutsets(nextapp.echo2.app.Insets)
	 */
	public void setOutsets(Insets newValue) {
		setProperty(PROPERTY_OUTSETS, newValue);
	}

	/**
	 * The pageFetchSize property controls how many pages (and hence rows) are
	 * "fetched" when a TableModel interaction is performed. A higher
	 * pageFetchSize will mean less server interactions but more network
	 * bandwidth.
	 * 
	 * @param newValue -
	 *            the new pageFetchSize value
	 */
	public void setPageFetchSize(int newValue) {
		ComponentEx.setProperty(this, PROPERTY_PAGE_FETCH_SIZE, newValue);
	}

	public void setRenderer(LiveTableRenderer newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("You must provide a non null LiveTableRenderer to LiveTable!");
		}
		setProperty(PROPERTY_RENDERER, newValue);
	}

	public void setRowHeight(Extent newValue) {
		setProperty(PROPERTY_ROW_HEIGHT, newValue);
	}

	public void setRowsPerPage(int newValue) {
		ComponentEx.setProperty(this, PROPERTY_ROWS_PER_PAGE, newValue);
	}

	/**
	 * @see echopointng.able.Scrollable#setScrollBarBaseColor(nextapp.echo2.app.Color)
	 */
	public void setScrollBarBaseColor(Color newValue) {
		setProperty(PROPERTY_SCROLL_BAR_BASE_COLOR, newValue);
	}

	/**
	 * @see echopointng.able.Scrollable#setScrollBarPolicy(int)
	 */
	public void setScrollBarPolicy(int newScrollBarPolicy) {
		ComponentEx.setProperty(this, PROPERTY_SCROLL_BAR_POLICY, newScrollBarPolicy);
	}

	/**
	 * @see echopointng.able.Scrollable#setScrollBarProperties(echopointng.able.ScrollBarProperties)
	 */
	public void setScrollBarProperties(ScrollBarProperties newValue) {
		setProperty(PROPERTY_SCROLL_BAR_PROPERTIES, newValue);
	}

	/**
	 * @see echopointng.able.ToolTipable#setToolTipText(java.lang.String)
	 */
	public void setToolTipText(String newValue) {
		setProperty(PROPERTY_TOOL_TIP_TEXT, newValue);
	}

	/**
	 * @see nextapp.echo2.app.Table#validate()
	 */
	public void validate() {
		super.validate();
		Style fallbackStyle = EPNG.getFallBackStyle(this);
		Extent heightExtent = (Extent) this.getRenderProperty(PROPERTY_ROW_HEIGHT);
		if (heightExtent == null && fallbackStyle != null) {
			heightExtent = (Extent) fallbackStyle.getProperty(PROPERTY_ROW_HEIGHT);
		}
		if (heightExtent == null) {
			throw new IllegalStateException("The LiveTable must have a row height set in order to function correctly");
		}

		heightExtent = (Extent) this.getRenderProperty(PROPERTY_HEIGHT);
		if (heightExtent == null && fallbackStyle != null) {
			heightExtent = (Extent) fallbackStyle.getProperty(PROPERTY_HEIGHT);
		}
		if (heightExtent == null) {
			throw new IllegalStateException("The LiveTable must have a height set in order to function correctly");
		}

		LiveTableRenderer renderer = (LiveTableRenderer) this.getRenderProperty(PROPERTY_RENDERER);
		if (renderer == null && fallbackStyle != null) {
			renderer = (LiveTableRenderer) fallbackStyle.getProperty(PROPERTY_RENDERER);
		}
		if (renderer == null) {
			throw new IllegalStateException("The LiveTable must have LiveTableRenderer!");
		}
	}
}
