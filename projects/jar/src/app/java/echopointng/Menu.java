package echopointng;

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
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.Style;
import echopointng.able.Borderable;
import echopointng.able.Insetable;
import echopointng.util.ColorKit;

/**
 * The <code>Menu</code> class provides a <code>MenuItem</code> that can be
 * added to a parent <code>Menu</code> or <code>MenuBar</code>.
 * <p>
 * If it is not contained within a parent <code>Menu</code>, then it is
 * rendered just like a <code>ButtonEx</code> but with a drop down box for any
 * of its child components. <br>
 * <p>
 * The styleChildren property can be used to help ensure consistent visual
 * properties between parent and child Menu's. When this is true, the visual
 * properties of the parent Menu will be transfered to the child
 * <code>Menu</code> or <code>MenuItem.</code>
 * 
 * 
 * @author Brad Baker
 */
public class Menu extends MenuItem {

	/** The default sub menu image */
	public static final ImageReference DEFAULT_SUBMENU_IMAGE;

	/**
	 * Open Option that opens the Menu's drop down box when the mouse is placed
	 * over the Menu's button.
	 */
	public final static int OPEN_ON_MOUSEOVER = 0;

	/**
	 * Open Option that opens the Menu's drop down box when the mouse is clicked
	 * anywhere on the Menu's button, including the SubMenu image.
	 */
	public final static int OPEN_ON_CLICK = 1;

	/**
	 * Open Option that opens the Menu's drop down box when the mouse is clicked
	 * only on the Menu's SubMenu image. If the user clicks on the ButtonEx
	 * part, then the button action is fired.
	 */
	public final static int OPEN_ON_SUBMENU_CLICK = 2;

	public static final String PROPERTY_BACKGROUND_IMAGE = "backgroundImage";

	public static final String PROPERTY_DRAW_OUTER_BORDERS = "drawOuterBorders";

	public static final String PROPERTY_HORIZONTAL = "horizontal";

	public static final String PROPERTY_KEEP_ALIVE = "keepAlive";

	public static final String PROPERTY_LEFT_OFFSET = "leftOffset";

	public static final String PROPERTY_MENU_ALWAYS_ON_TOP = "menuAlwaysOnTop";

	public static final String PROPERTY_MENU_BACKGROUND = "menuBackground";

	public static final String PROPERTY_MENU_BACKGROUND_IMAGE = "menuBackgroundImage";

	public static final String PROPERTY_MENU_BORDER = "menuBorder";

	public static final String PROPERTY_MENU_FOREGROUND = "menuForeground";

	public static final String PROPERTY_MENU_INSETS = "menuInsets";

	public static final String PROPERTY_MENU_OUTSETS = "menuOutsets";

	public static final String PROPERTY_OPEN_OPTION = "openOption";

	public static final String PROPERTY_PROPERTY_CHILDREN = "styleChildren";

	/* The properties */
	public static final String PROPERTY_STYLE_CHILDREN = "styleChildren";

	public static final String PROPERTY_SUBMENU_IMAGE = "submenuImage";

	public static final String PROPERTY_SUBMENU_IMAGE_ALIGNMENT = "submenuImageAlignment";

	public static final String PROPERTY_SUBMENU_IMAGE_BORDERED = "submenuImageBordered";

	public static final String PROPERTY_SUBMENU_ROLLOVER_IMAGE = "submenuRolloverImage";

	public static final String PROPERTY_TOP_OFFSET = "topOffset";

	static {
		String imagePath = "/echopointng/resource/images/";
		DEFAULT_SUBMENU_IMAGE = new ResourceImageReference(imagePath + "menu_submenuBlack.gif", new Extent(11), new Extent(11));
	}

	/**
	 * This DEFAULT_STYLE is applied to the Menu to give it a series of borders
	 * and background colors
	 */
	public static final Style DEFAULT_STYLE;
	static {
		MutableStyleEx style = new MutableStyleEx(MenuItem.DEFAULT_STYLE);

		style.setProperty(PROPERTY_MENU_ALWAYS_ON_TOP, false);

		style.setProperty(PROPERTY_MENU_BORDER, new Border(1, ColorKit.makeColor("#000000"), Border.STYLE_SOLID));
		style.setProperty(PROPERTY_MENU_BACKGROUND, ColorKit.makeColor("#FFFFFF"));

		style.setProperty(PROPERTY_SUBMENU_IMAGE, DEFAULT_SUBMENU_IMAGE);
		style.setProperty(PROPERTY_SUBMENU_IMAGE_ALIGNMENT, Alignment.RIGHT);
		style.setProperty(PROPERTY_SUBMENU_IMAGE_BORDERED, true);

		style.setProperty(PROPERTY_MENU_INSETS, new Insets(1));
		style.setProperty(PROPERTY_MENU_OUTSETS, new Insets(0));

		style.setProperty(PROPERTY_TEXT_ALIGNMENT, new Alignment(Alignment.CENTER, Alignment.CENTER));
		style.setProperty(PROPERTY_HORIZONTAL, false);
		style.setProperty(PROPERTY_STYLE_CHILDREN, true);

		DEFAULT_STYLE = style;

	}

	/**
	 * Creates a <code>Menu</code> with no text or icon.
	 */
	public Menu() {
		this(null, null);
	}

	/**
	 * Creates a <code>Menu</code> with no text or icon and adds
	 * menuItem as its first child.
	 * 
	 * @param menuItem - the first child to add to this <code>Menu</code>
	 */
	public Menu(MenuItem menuItem) {
		this(null, null);
	}
	
	/**
	 * Creates a <code>Menu</code> with an icon.
	 * 
	 * @param icon
	 *            An icon to display in the <code>Menu</code>.
	 */
	public Menu(ImageReference icon) {
		this(null, icon);
	}

	/**
	 * Creates a <code>Menu</code> with text.
	 * 
	 * @param text
	 *            A text label to display in the <code>Menu</code>.
	 */
	public Menu(String text) {
		this(text, null);
	}

	/**
	 * Creates a <code>Menu</code> with text and an icon.
	 * 
	 * @param text
	 *            A text label to display in the <code>Menu</code>.
	 * @param icon
	 *            An icon to display in the <code>Menu</code>.
	 */
	public Menu(String text, ImageReference icon) {
		super(text, icon);
	}

	/**
	 * Adds a <code>Component</code> to the end of the <code>Menu</code>.
	 * This could typically be a <code>Separator</code>. If the Component is
	 * a MenuItem then it is styled if styleChildren is set to true.
	 */
	public void add(Component c) {
		if (c instanceof MenuItem) {
			addMenuItem((MenuItem) c);
		} else {
			super.add(c, -1);
		}
	}

	/**
	 * Adds a <code>MenuItem</code> to the end of the <code>Menu</code> and
	 * styles it the same if styleChildren is set to true.
	 * 
	 * @param menuItem -
	 *            the new child MenuItem
	 */
	public void add(MenuItem menuItem) {
		addMenuItem(menuItem);
	}

	/**
	 * Creates and adds a <code>MenuItem</code> to the end of the
	 * <code>Menu</code> with the specified text.
	 */
	public MenuItem add(String text) {
		MenuItem menuItem = new MenuItem(text);
		return addMenuItem(menuItem);
	}

	/* Adds and styles a MenuItem */
	private MenuItem addMenuItem(MenuItem menuItem) {
		if (isStyleChildren()) {
			styleMenuItem(menuItem);
		}
		super.add(menuItem, -1);
		return menuItem;
	}

	/**
	 * @return the left offset of this <code>Menu<code>
	 */
	public int getLeftOffset() {
		return ComponentEx.getProperty(this, PROPERTY_LEFT_OFFSET, 0);
	}
	
	/**
	 * @see Menu#setMenuAlwaysOnTop(boolean)
	 */
	public boolean getMenuAlwaysOnTop() {
		return ComponentEx.getProperty(this, PROPERTY_MENU_ALWAYS_ON_TOP,false);
	}

	/**
	 * @return Returns the background color of the Menu's drop down box.
	 */
	public Color getMenuBackground() {
		return (Color) ComponentEx.getProperty(this, PROPERTY_MENU_BACKGROUND);
	}

	/**
	 * Returns the background image for this Menu's drop down box.
	 * 
	 * @return The background image for this Menu's drop down box.
	 */
	public FillImage getMenuBackgroundImage() {
		return (FillImage) ComponentEx.getProperty(this, PROPERTY_MENU_BACKGROUND_IMAGE);
	}

	/**
	 * @return the border around the Menu drop down box
	 */
	public Border getMenuBorder() {
		return (Border) getProperty(PROPERTY_MENU_BORDER);
	}

	/**
	 * @return Returns the foreground color of the Menu's drop down box.
	 */
	public Color getMenuForeground() {
		return (Color) ComponentEx.getProperty(this, PROPERTY_MENU_FOREGROUND);
	}

	/**
	 * @return the Insets used inside the Menu's drop down box.
	 */
	public Insets getMenuInsets() {
		return (Insets) ComponentEx.getProperty(this, PROPERTY_MENU_INSETS);
	}

	/**
	 * This returns the MenuItem's that are a direct child of this Menu.
	 * 
	 * @return a non null array of MenuItem's
	 */
	public MenuItem[] getMenuItems() {
		Component[] children = getComponents();
		int miCount = 0;
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof MenuItem)
				miCount++;
		}
		MenuItem[] miChildren = new MenuItem[miCount];
		miCount = 0;
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof MenuItem) {
				miChildren[miCount] = (MenuItem) children[i];
				miCount++;
			}
		}
		return miChildren;

	}

	/**
	 * @return the Insets used outside the Menu's drop down box.
	 */
	public Insets getMenuOutsets() {
		return (Insets) ComponentEx.getProperty(this, PROPERTY_MENU_OUTSETS);
	}

	/**
	 * @return Returns the Menu's Drop Down Box opening option.
	 */
	public int getOpenOption() {
		return ComponentEx.getProperty(this, PROPERTY_OPEN_OPTION, OPEN_ON_MOUSEOVER);
	}

	/**
	 * @return The submenu <code>Image Reference</code> of the
	 *         <code>Menu</code>.
	 */
	public ImageReference getSubmenuImage() {
		return (ImageReference) ComponentEx.getProperty(this, PROPERTY_SUBMENU_IMAGE);
	}

	/**
	 * @return The submenu image alignmentof the <code>Menu</code>.
	 */
	public int getSubmenuImageAlignment() {
		return ComponentEx.getProperty(this, PROPERTY_SUBMENU_IMAGE_ALIGNMENT, Alignment.RIGHT);
	}

	/**
	 * @return the submenu image used when the mouse if rolled over the Menu
	 */
	public ImageReference getSubmenuRolloverImage() {
		return (ImageReference) ComponentEx.getProperty(this, PROPERTY_SUBMENU_ROLLOVER_IMAGE);
	}

	/**
	 * Returns the top offset of this <code>Menu<code>
	 * @return int
	 */
	public int getTopOffset() {
		return ComponentEx.getProperty(this, PROPERTY_TOP_OFFSET, 0);
	}

	/**
	 * @return Returns true if the Menu must be clicked on to open is drop down
	 *         box.
	 *         <p>
	 *         This will return true if the open options are set to
	 *         OPEN_ON_CLICK or OPEN_ON_SUBMENU_CLICK.
	 */
	public boolean isClickToOpen() {
		return getOpenOption() == OPEN_ON_CLICK || getOpenOption() == OPEN_ON_SUBMENU_CLICK;
	}

	/**
	 * Returns true if the Menu's outer borders and insets/outsets will be
	 * drawn.
	 * 
	 * @return true if the Menu's outer borders and insets/outsets will be
	 *         drawn.
	 */
	public boolean isDrawOuterBorders() {
		return ComponentEx.getProperty(this, PROPERTY_DRAW_OUTER_BORDERS, true);
	}

	/**
	 * @return true if the Menu is oriented in a horizontal manner
	 */
	public boolean isHorizontal() {
		return ComponentEx.getProperty(this, PROPERTY_HORIZONTAL, false);
	}

	/**
	 * @return true if drop down menu boxes will stay alive (ie visible) when
	 *         the mouse moves out of them
	 */
	public boolean isKeepAlive() {
		return ComponentEx.getProperty(this, PROPERTY_KEEP_ALIVE, false);
	}

	/**
	 * @return true if child Menu's and MenuItems will be styled with the same
	 *         presentation parameters as this Menu when they are added.
	 */
	public boolean isStyleChildren() {
		return ComponentEx.getProperty(this, PROPERTY_STYLE_CHILDREN, false);
	}

	/**
	 * @return true if the submenu image is bordered with the Menu's border
	 *         color / style and size.
	 */
	public boolean isSubmenuImageBordered() {
		return ComponentEx.getProperty(this, PROPERTY_SUBMENU_IMAGE_BORDERED, true);
	}

	/**
	 * Set to true if the Menu must be clicked on to open is drop down box.
	 * <p>
	 * This is the equivalent of calling setOpenOption(OPEN_ON_CLICK);
	 * <p>
	 * If clickToOpen is false, then this is the equivalent of calling
	 * setOpenOption(OPEN_ON_MOUSEOVER);
	 * 
	 * @param clickToOpen
	 *            true ot false
	 */
	public void setClickToOpen(boolean clickToOpen) {
		if (clickToOpen) {
			setOpenOption(OPEN_ON_CLICK);
		} else {
			setOpenOption(OPEN_ON_MOUSEOVER);
		}
	}

	/**
	 * Sets the false to prevent the menu outer borders and insets from being
	 * used.
	 * 
	 * @param newValue -
	 *            The newValue to set.
	 */
	public void setDrawOuterBorders(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_DRAW_OUTER_BORDERS, newValue);
	}

	/**
	 * Sets the whether the <code>Menu</code> is orientated horizontal or
	 * vertical on the screen.
	 */
	public void setHorizontal(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_HORIZONTAL, newValue);
	}

	/**
	 * Set to true if drop down menu boxes will stay alive (ie visible) when the
	 * mouse moves out of them
	 */
	public void setKeepAlive(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_KEEP_ALIVE, newValue);
	}

	/**
	 * Sets the left offset of the drop down box associated with this
	 * <code>Menu<code>
	 * relative to its parent.
	 * 
	 * @param newLeftOffset the left offset of this <code>Menu<code>
	 */
	public void setLeftOffset(int newLeftOffset) {
		ComponentEx.setProperty(this, PROPERTY_LEFT_OFFSET, newLeftOffset);
	}

	/**
	 * The menuAlwaysOnTop property can be used control how the popup menu box
	 * is drawn.
	 * <p>
	 * If this is true, then the Menu box with always take the highest position
	 * on the screen and will disregard parent clipping and scrolling constraints.
	 * <p>
	 * If this is false, then the normal client clipping and scrollling
	 * constraints will apply.  This can cause the popup menu box to be 
	 * shown under other components according to the current hierarchy.
	 * 
	 * @param newValue - the new menuAlwaysOnTop value
	 */
	public void setMenuAlwaysOnTop(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_MENU_ALWAYS_ON_TOP, newValue);
	}

	/**
	 * Sets the background color of the Menu's drop down box.
	 * 
	 * @param menuBackground
	 *            the new background
	 */
	public void setMenuBackground(Color menuBackground) {
		ComponentEx.setProperty(this, PROPERTY_MENU_BACKGROUND, menuBackground);
	}

	/**
	 * Sets the background image for this Menu's drop down box
	 * 
	 * @param newValue
	 *            The new background image.
	 */
	public void setMenuBackgroundImage(FillImage newValue) {
		ComponentEx.setProperty(this, PROPERTY_MENU_BACKGROUND_IMAGE, newValue);
	}

	/**
	 * Sets the border for this Menu's drop down box
	 * 
	 * @param newValue
	 *            The new border
	 */
	public void setMenuBorder(Border newValue) {
		ComponentEx.setProperty(this, PROPERTY_MENU_BORDER, newValue);
	}

	/**
	 * Sets the foreground color of the Menu's drop down box.
	 * 
	 * @param menuForeground -
	 *            the new color
	 */
	public void setMenuForeground(Color menuForeground) {
		ComponentEx.setProperty(this, PROPERTY_MENU_FOREGROUND, menuForeground);
	}

	/**
	 * Sets the Insets used inside the Menu's drop down box. Note that this
	 * differs from the Insets that are used for the Menu's text within its
	 * parent Menu drop down box.
	 * 
	 * @param menuInsets
	 *            the Insets used inside the Menu's drop down box.
	 */
	public void setMenuInsets(Insets menuInsets) {
		ComponentEx.setProperty(this, PROPERTY_MENU_INSETS, menuInsets);
	}

	/**
	 * Sets the Insets used outside the Menu's drop down box. Note that this
	 * differs from the outsets that are used for the Menu's text within its
	 * parent Menu drop down box.
	 * 
	 * @param menuOutsets
	 *            the Insets used outside the Menu's drop down box.
	 */
	public void setMenuOutsets(Insets menuOutsets) {
		ComponentEx.setProperty(this, PROPERTY_MENU_INSETS, menuOutsets);
	}

	/**
	 * Sets the Menu's drop down box opening options. This can be one of the
	 * following values :
	 * <ul>
	 * <li>OPEN_ON_MOUSEOVER - opens the Menu's drop down box when the mouse
	 * moves over the Menu's button part.</li>
	 * <li>OPEN_ON_CLICK - opens the Menu's drop down box when the mouse is
	 * clicked anywhere on the Menu's button part.</li>
	 * <li>OPEN_ON_SUBMENU_CLICK - opens the Menu's drop down box when the
	 * mouse is clicked only on the Menu's sub menu image part.</li>
	 * </ul>
	 * 
	 * @param newValue -
	 *            The newValue to set.
	 */
	public void setOpenOption(int newValue) {
		if (newValue != OPEN_ON_CLICK && newValue != OPEN_ON_MOUSEOVER && newValue != OPEN_ON_SUBMENU_CLICK)
			throw new IllegalArgumentException("The Menu's open option must be either OPEN_ON_CLICK, OPEN_ON_MOUSEOVER or OPEN_ON_SUBMENU_CLICK");
		ComponentEx.setProperty(this, PROPERTY_OPEN_OPTION, newValue);
	}

	/**
	 * Set to true if child Menu's and MenuItems will be styled with the same
	 * presentation parameters as this Menu when they are added.
	 * 
	 * @param newValue -
	 *            whether to style children when added
	 */
	public void setStyleChildren(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_STYLE_CHILDREN, newValue);
	}

	/**
	 * Sets the submenu <code>ImageReference</code> of the <code>Menu</code>.
	 * This submenu image will only be shown if the Menu does in fact have
	 * children
	 */
	public void setSubmenuImage(ImageReference newImageRef) {
		ComponentEx.setProperty(this, PROPERTY_SUBMENU_IMAGE, newImageRef);
	}

	/**
	 * Sets the alignment of the submenu image of the <code>Menu</code>.<br>
	 * This can be one of the following values :
	 * <ul>
	 * <li>Alignment.LEFT</li>
	 * <li>Alignment.RIGHT (the default)</li>
	 * </ul>
	 */
	public void setSubmenuImageAlignment(int newAlignment) {
		if (!(newAlignment == Alignment.LEFT || newAlignment == Alignment.RIGHT))
			throw new IllegalArgumentException("SubmenuImageAlignment must be Alignment.LEFT or Alignment.RIGHT!");
		ComponentEx.setProperty(this, PROPERTY_SUBMENU_IMAGE_ALIGNMENT, newAlignment);
	}

	/**
	 * Set to true if the submenu image is bordered with the Menu's border color /
	 * style and size.
	 * 
	 * @param newValue -
	 *            true of false
	 */
	public void setSubmenuImageBordered(boolean newValue) {
		ComponentEx.setProperty(this, PROPERTY_SUBMENU_IMAGE_BORDERED, newValue);
	}

	/**
	 * Sets the submenu image used when the mouse if rolled over the Menu
	 * 
	 * @param newValue -
	 *            the new image
	 */
	public void setSubmenuRolloverImage(ImageReference newValue) {
		ComponentEx.setProperty(this, PROPERTY_SUBMENU_ROLLOVER_IMAGE, newValue);
	}

	/**
	 * Sets the top offset of the drop down box associated with this
	 * <code>Menu<code>
	 * relative to its parent.
	 * @param newTopOffset the top offset of this <code>Menu<code>
	 */
	public void setTopOffset(int newTopOffset) {
		ComponentEx.setProperty(this, PROPERTY_TOP_OFFSET, newTopOffset);
	}

	/**
	 * This methods will call styleMenuItem() for every child MenuItem in the
	 * Menu. It will recurse down the parent-child relationship tree for any
	 * contained Menu's.
	 * 
	 * @see Menu#styleMenuItem(MenuItem)
	 */
	public void styleAllMenuItems() {
		MenuItem items[] = getMenuItems();
		for (int i = 0; i < items.length; i++) {
			MenuItem item = items[i];
			styleMenuItem(item);
			if (item instanceof Menu)
				((Menu) item).styleAllMenuItems();
		}
	}

	/**
	 * This will style the MenuItem with the same presentation parameters as
	 * this Menu. The following groups of properties are set :
	 * <ul>
	 * <li>foreground, background, font, background image</li>
	 * <li>borders</li>
	 * <li>rollover foreground, background, font, background image</li>
	 * <li>rollover borders</li>
	 * <li>insets, outsets</li>
	 * <li><i>If the MenuItem is in fact a Menu </i></li>
	 * <ul>
	 * <li>menu foreground, background, font</li>
	 * <li>menu borders</li>
	 * <li>menu insets, outsets</li>
	 * <li>menu submenu image, submenu alignment</li>
	 * <li>menu submenu rollover image, submenu bordered</li>
	 * <li>menu background image properties</li>
	 * </ul>
	 * </ul>
	 * <p>
	 * Note only screen presentation parameters are set, other functional
	 * parameters such as isHorizontal() are not set.
	 * <p>
	 * Also the getStyle() value is copied into the MenuItem as well.
	 * <p>
	 * @param menuItem -
	 *            the MenuItem to style the same as this Menu
	 */
	public void styleMenuItem(MenuItem menuItem) {
		
		menuItem.setStyle(this.getStyle());
		
		copyProperty(menuItem, PROPERTY_FONT);
		copyProperty(menuItem, PROPERTY_FOREGROUND);
		copyProperty(menuItem, PROPERTY_BACKGROUND);
		copyProperty(menuItem, PROPERTY_BACKGROUND_IMAGE);

		copyProperty(menuItem, Borderable.PROPERTY_BORDER);

		copyProperty(menuItem, Insetable.PROPERTY_INSETS);
		copyProperty(menuItem, Insetable.PROPERTY_OUTSETS);

		copyProperty(menuItem, PROPERTY_ROLLOVER_BACKGROUND);
		copyProperty(menuItem, PROPERTY_ROLLOVER_BACKGROUND_IMAGE);
		copyProperty(menuItem, PROPERTY_ROLLOVER_BORDER);
		copyProperty(menuItem, PROPERTY_ROLLOVER_ENABLED);
		copyProperty(menuItem, PROPERTY_ROLLOVER_FONT);
		copyProperty(menuItem, PROPERTY_ROLLOVER_FOREGROUND);

		if (menuItem instanceof Menu) {
			Menu menu = (Menu) menuItem;
			copyProperty(menu, Menu.PROPERTY_MENU_BACKGROUND);
			copyProperty(menu, Menu.PROPERTY_MENU_BACKGROUND_IMAGE);
			copyProperty(menu, Menu.PROPERTY_MENU_BORDER);
			copyProperty(menu, Menu.PROPERTY_MENU_FOREGROUND);

			copyProperty(menu, Menu.PROPERTY_MENU_INSETS);
			copyProperty(menu, Menu.PROPERTY_MENU_OUTSETS);

			copyProperty(menu, Menu.PROPERTY_SUBMENU_IMAGE);
			copyProperty(menu, Menu.PROPERTY_SUBMENU_ROLLOVER_IMAGE);
			copyProperty(menu, Menu.PROPERTY_SUBMENU_IMAGE_BORDERED);
			copyProperty(menu, Menu.PROPERTY_SUBMENU_IMAGE_ALIGNMENT);

			copyProperty(menu, Menu.PROPERTY_MENU_ALWAYS_ON_TOP);
			copyProperty(menu, Menu.PROPERTY_OPEN_OPTION);

		}
	}

	private void copyProperty(MenuItem menuItem, String propertyName) {
		menuItem.setProperty(propertyName, this.getProperty(propertyName));
	}

	/**
	 * This method will set the style name via setStyleName() of the each of the
	 * child menus and menu items using topMenu as the top most level Menu. It
	 * calls setStyleName() of the starting Menu as well.
	 * 
	 * @param menuStyleName -
	 *            the style name to use for instances of Menu
	 * @param menuItemStyleName -
	 *            the style name to use for instances of MenuItem
	 */
	public void styleAllMenuItemsByName(String menuStyleName, String menuItemStyleName) {
		this.setStyleName(menuStyleName);
		MenuItem items[] = this.getMenuItems();
		for (int i = 0; i < items.length; i++) {
			MenuItem item = items[i];
			if (item instanceof Menu) {
				item.setStyleName(menuStyleName);
				((Menu) item).styleAllMenuItemsByName(menuStyleName, menuItemStyleName);
			} else {
				item.setStyleName(menuItemStyleName);
			}
		}
	}

	/**
	 * This static method will set the style via setStyle() of the each of the
	 * child menus and menu items using topMenu as the top most level Menu. It
	 * calls setStyle() of the starting Menu as well.
	 * 
	 * @param menuStyle -
	 *            the style to use for instances of Menu
	 * @param menuItemStyle -
	 *            the style to use for instances of MenuItem
	 */
	public void styleAllMenuItems(Style menuStyle, Style menuItemStyle) {
		this.setStyle(menuStyle);
		MenuItem items[] = this.getMenuItems();
		for (int i = 0; i < items.length; i++) {
			MenuItem item = items[i];
			if (item instanceof Menu) {
				item.setStyle(menuStyle);
				((Menu) item).styleAllMenuItems(menuStyle, menuItemStyle);
			} else {
				item.setStyle(menuItemStyle);
			}
		}
	}

	/**
	 * @see nextapp.echo2.app.button.AbstractButton#isValidChild(nextapp.echo2.app.Component)
	 */
	public boolean isValidChild(Component component) {
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("Menu");
		if (getId() != null) {
			sb.append(" - ");
			sb.append(getId());
		}
		if (getText() != null) {
			sb.append(" : ");
			sb.append(getText());
		}
		return sb.toString();
	}
}
