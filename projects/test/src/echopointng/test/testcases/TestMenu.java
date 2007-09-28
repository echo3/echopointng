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
package echopointng.test.testcases;

import java.util.Stack;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.SelectField;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.layout.ColumnLayoutData;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.ContainerEx;
import echopointng.DateChooser;
import echopointng.ExtentEx;
import echopointng.Menu;
import echopointng.MenuBar;
import echopointng.MenuButton;
import echopointng.MenuItem;
import echopointng.Separator;

/**
 * <code>TestMenu</code>
 */

public class TestMenu extends TestCaseBaseNG {

	/**
	 * @see echopointng.test.testcases.TestCaseBaseNG#getTestCategory()
	 */
	public String getTestCategory() {
		return "Menu";
	}

	public Component testMenuItemByItself() {
		final ImageReference icon = new HttpImageReference("images/ArrowRoundRight.gif");
		final ImageReference disabledIcon = new HttpImageReference("images/poker_monkey_no.gif", new Extent(30), new Extent(30));
		final Label pressedLabel = new Label();

		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressedLabel.setText("Menu pressed - " + String.valueOf(e.getSource()));
			}
		};

		Column cell = new Column();
		cell.add(pressedLabel);

		MenuItem mi;

		mi = new MenuItem("A Single Menu Item - (Button like isnt it!)", icon);
		mi.addActionListener(al);
		cell.add(mi);

		mi = new MenuItem("Disabled Menu Item", icon);
		mi.addActionListener(al);
		mi.setDisabledIcon(disabledIcon);
		mi.setEnabled(false);
		cell.add(mi);

		Object[] menuDefs = { "Mail", new Object[] { "Check Mail", "Compose", "Folders", "Advanced Search", "Options", "Help", } };
		MenuBar mb = createMenuBar(menuDefs, al);
		mb.setInsets(new Insets(0));
		mb.setOutsets(new Insets(0));
		mb.setBorder(BorderEx.NONE);

		cell.add(mb);

		return cell;
	}

	public Component testDropDownMenu() {
		final ImageReference downArrow = new HttpImageReference("images/down_arrow.png", new Extent(22), new Extent(22));
		final Label pressedLabel = new Label();

		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressedLabel.setText("Menu pressed - " + String.valueOf(e.getSource()));
			}
		};

		Column cell = new Column();
		cell.add(pressedLabel);

		Object[] menuDefs = { "Mail", new Object[] { "Check Mail", "Compose", "Folders", "Advanced Search", "Options", "Help", } };
		MenuBar mb = createMenuBar(menuDefs, al);
		mb.setInsets(new Insets(0));
		mb.setOutsets(new Insets(0));
		mb.setBorder(BorderEx.NONE);

		Menu topLevel = (Menu) mb.getComponent(0);
		topLevel.setSubmenuImage(downArrow);
		topLevel.setOpenOption(Menu.OPEN_ON_SUBMENU_CLICK);

		cell.add(mb);
		return cell;
	}

	public Component testMenu() {
		return createMenuBar("MB", false);
	}

	public Component testMenuWithinWindowPane() {

		MenuBar menuBar1 = createMenuBar("WP NOT", false);
		MenuBar menuBar2 = createMenuBar("WP AOT", false);
		setAlwaysOnTop(menuBar2);

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("50px"));

		cell.add(menuBar1);
		cell.add(menuBar2);

		WindowPane windowPane = new WindowPane();
		windowPane.setWidth(new ExtentEx(500));
		windowPane.setHeight(new ExtentEx(500));
		windowPane.setModal(true);
		windowPane.add(cell);

		return windowPane;
	}

	public Component testTwoMenus() {
		MenuBar menuBar1 = createMenuBar("MB1", false);
		MenuBar menuBar2 = createMenuBar("MB2", true);
		menuBar2.styleAllMenuItems();

		Column col = new Column();
		col.add(menuBar1);
		col.add(menuBar2);

		return col;
	}

	private void setAlwaysOnTop(Menu menu) {
		menu.setMenuAlwaysOnTop(true);
		Component[] children = menu.getComponents();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Menu) {
				setAlwaysOnTop((Menu) children[i]);
			}
		}
	}

	private MenuBar createMenuBar(Object[] menuDefs, ActionListener al) {
		MenuBar menuBar = new MenuBar();
		createMenus(menuDefs, menuBar, al);
		return menuBar;
	}

	private void createMenus(Object[] menuDefs, Menu parentMenu, ActionListener al) {
		Menu currentMenu = null;
		for (int i = 0; i < menuDefs.length; i++) {
			Object menuObj = menuDefs[i];
			if (menuObj.getClass().isArray()) {
				if (currentMenu == null) {
					currentMenu = new Menu("Unspecified");
					currentMenu.addActionListener(al);
					parentMenu.add(currentMenu);
				}
				Object[] menuObjs = (Object[]) menuObj;
				createMenus(menuObjs, currentMenu, al);
				currentMenu = null;
			} else {
				String menuName = String.valueOf(menuObj);
				currentMenu = new Menu(menuName);
				currentMenu.addActionListener(al);
				parentMenu.add(currentMenu);
			}
		}
	}

	private MenuBar createMenuBar(String prefix, boolean isClickToOpen) {
		MenuItem mi, menu, parent;
		MenuBar menuBar;

		menuBar = new MenuBar();
		Stack stack = new Stack();

		menu = menuBar;
		stack.push(menu);
		parent = menu;

		menu = new Menu(prefix + "Menu 1");
		parent.add(menu);
		stack.push(parent);
		parent = menu;
		menu = new MenuItem(prefix + "Menu 1.1");
		parent.add(menu);
		menu = new Menu(prefix + "Menu 1.2");
		parent.add(menu);
		stack.push(parent);
		parent = menu;
		menu = new MenuItem(prefix + "Menu 1.2.1");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 1.2.2");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 1.2.3");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 1.2.4");
		parent.add(menu);
		parent = (Menu) stack.pop();

		menu = new MenuItem(prefix + "Menu 1.3 - Disabled");
		menu.setEnabled(false);
		parent.add(menu);
		parent = (Menu) stack.pop();

		menu = new Menu(prefix + "Menu 2");
		parent.add(menu);
		stack.push(parent);
		parent = menu;
		menu = new Menu(prefix + "Menu 2.1");
		parent.add(menu);
		stack.push(parent);
		parent = menu;
		menu = new Menu(prefix + "Menu 2.1.1");
		parent.add(menu);
		stack.push(parent);
		parent = menu;
		menu = new MenuItem(prefix + "Menu 2.1.1");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 2.1.2");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 2.1.3");
		parent.add(menu);
		menu = new Menu(prefix + "Menu 2.1.4 - Disabled");
		menu.setEnabled(false);
		parent.add(menu);
		stack.push(parent);
		parent = menu;
		menu = new MenuItem(prefix + "Menu 2.1.4.1");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 2.1.4.2");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 2.1.4.3");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 2.1.4.4");
		parent.add(menu);
		parent = (Menu) stack.pop();
		parent = (Menu) stack.pop();

		menu = new MenuItem(prefix + "Menu 2.1.2");
		parent.add(menu);
		parent.add(new Separator());
		menu = new MenuItem(prefix + "Menu 2.1.3");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 2.1.4");
		parent.add(menu);
		parent = (Menu) stack.pop();

		// a sep
		parent.add(new Separator());
		
		menu = new MenuItem(prefix + "Menu 2.2");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 2.3");
		parent.add(menu);
		parent = (Menu) stack.pop();
		menu = new Menu(prefix + "Menu 3");
		parent.add(menu);
		stack.push(parent);
		parent = menu;
		menu = new MenuItem(prefix + "Menu 3.1");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 3.2");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 3.3");
		parent.add(menu);
		parent = (Menu) stack.pop();
		menu = new Menu(prefix + "Menu 4");
		parent.add(menu);
		((Menu) menu).setHorizontal(true);
		stack.push(parent);
		parent = menu;
		menu = new MenuItem(prefix + "Menu 4.1");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 4.2");
		parent.add(menu);
		menu = new Menu(prefix + "Menu 4.3");
		parent.add(menu);
		stack.push(parent);
		parent = menu;
		menu = new MenuItem(prefix + "Menu 4.3.1");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 4.3.2");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 4.3.3");
		parent.add(menu);

		DateChooser dc = new DateChooser();
		parent.add(dc);
		// ImageIcon ii = new ImageIcon(EchoPointIcons.icon32("Camera"));
		// parent.add(ii);
		parent = (Menu) stack.pop();

		parent = (Menu) stack.pop();
		menu = new MenuItem(prefix + "Menu 5");
		parent.add(menu);
		menu = new MenuItem(prefix + "Menu 6");
		parent.add(menu);

		ActionListener actionL = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MenuItem b = (MenuItem) e.getSource();
				b.setIcon(new HttpImageReference("images/poker_monkey_no.gif", new Extent(5), new Extent(5)));
			}
		};

		int count = 0;
		stack = new Stack();
		stack.push(menuBar);
		while (!stack.isEmpty()) {
			count++;
			mi = (MenuItem) stack.pop();
			styleButton(mi);
			mi.addActionListener(actionL);

			if (mi instanceof Menu) {
				((Menu) mi).setClickToOpen(isClickToOpen);
			}

			Component[] children = mi.getComponents();
			for (int i = 0; i < children.length; i++) {
				if (children[i] instanceof MenuItem)
					stack.push(children[i]);

			}
		}
		return menuBar;
	}

	private ButtonEx styleButton(ButtonEx btn) {

		// btn.setIcon(EchoPointIcons.icon20(iconname));
		// btn.setRolloverIcon(EchoPointIcons.icon20(RandKit.roll(icons)));

		btn.setFont(new Font(Font.VERDANA, Font.PLAIN, new Extent(8, Extent.PT)));
		btn.setRolloverFont(new Font(Font.VERDANA, Font.PLAIN, new Extent(8, Extent.PT)));

		btn.setIcon(new HttpImageReference("images/ArrowRoundLeft.gif", new Extent(10), new Extent(10)));
		btn.setRolloverIcon(new HttpImageReference("images/ArrowRoundRight.gif", new Extent(10), new Extent(10)));

		/**
		 * btn.setOutsets(new Insets(2,2,5,2)); btn.setRolloverEnabled(true);
		 * 
		 * btn.setRolloverBackground(ColorKit.makeColor("#C6D3EF"));
		 * btn.setRolloverForeground(ColorKit.makeColor("#ffffff"));
		 * 
		 * btn.setRolloverBorderColor(ColorKit.makeColor("#3169C6"));
		 * btn.setRolloverBorderStyle(Borderable.BORDER_SOLID);
		 * btn.setRolloverBorderSize(1); btn.setRolloverFont(new
		 * Font(Font.VERDANA,Font.PLAIN,10));
		 * 
		 * btn.setBackground(ColorKit.makeColor("#EFEFDE"));
		 * btn.setForeground(ColorKit.makeColor("#000000"));
		 * 
		 * btn.setBorderColor(ColorKit.makeColor("#CECFCE"));
		 * btn.setBorderStyle(Borderable.BORDER_SOLID); btn.setFont(new
		 * Font(Font.VERDANA,Font.BOLD,10));
		 * 
		 * btn.setLineWrap(false);
		 */

		if (btn instanceof Menu) {
			Menu menu = (Menu) btn;

			if (echopointng.util.RandKit.roll5050()) {
				menu.setIcon(null);
				menu.setRolloverIcon(null);
				menu.setSubmenuImageAlignment(Alignment.LEFT);
			}
			menu.setSubmenuImageBordered(true);
			// menu.setKeepAlive(true);

			// menu.setSubmenuImage(EchoPointIcons.icon20("ArrowRight"));
			// menu.setSubmenuImageAlignment(RandKit.roll(new int[] {
			// EchoConstants.LEFT, EchoConstants.RIGHT}));
			// menu.setSubmenuRolloverImage(EchoPointIcons.icon20("ArrowRight"));
			// menu.setSubmenuImageBordered(true);

			if (menu.getMenuItems().length > 0) {
				// menu.setOpenOption(Menu.OPEN_ON_CLICK);
			}
		}
		return btn;
	}

	public Component testMenuWithSetProperties() {
		Column cell = new Column();

		MenuBar topMenu = new MenuBar();
		topMenu.setStyleChildren(false);
		topMenu.setMenuBackground(Color.BLUE);
		topMenu.setDrawOuterBorders(false);
		topMenu.setMenuBorder(new Border(0, Color.WHITE, Border.STYLE_NONE));
		topMenu.setMenuInsets(new Insets(10));
		topMenu.setMenuOutsets(new Insets(10));

		ColumnLayoutData topMenuLayout = new ColumnLayoutData();
		topMenuLayout.setAlignment(new Alignment(Alignment.RIGHT, Alignment.BOTTOM));
		topMenu.setLayoutData(topMenuLayout);
		cell.add(topMenu);

		MenuItem item = new MenuItem("My Menu Item");
		item.setBackground(Color.RED);
		item.setForeground(Color.MAGENTA);
		topMenu.add(item);

		return cell;
	}

	public Component testMenuWithSelects() {
		String[] options = new String[] { "Bright", "Lights", "City", "Gonna", "Set", "My", "Soul", "On", "Fire" };
		Column cell = new Column();
		cell.add(createMenuBar("MB", false));
		cell.add(new SelectField(options));
		cell.add(new SelectField(options));
		cell.add(new SelectField(options));
		return cell;
	}

	public Component testMenuWithSplitPane() {
		MenuBar menuBarAlwaysOnTop = createMenuBar("AlwaysOnTop", true);
		setAlwaysOnTop(menuBarAlwaysOnTop);

		MenuBar menuBarDefault = createMenuBar("Default", true);

		Column column = new Column();
		column.setCellSpacing(new ExtentEx("10px"));
		column.add(menuBarAlwaysOnTop);
		column.add(menuBarDefault);

		SplitPane splitPane = new SplitPane();
		splitPane.setOrientation(SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM);
		splitPane.add(column);
		splitPane.add(genRandomTextContent());
		splitPane.setSeparatorPosition(new Extent(120));

		ContainerEx containerEx = new ContainerEx();
		containerEx.setWidth(new ExtentEx(500));
		containerEx.setHeight(new ExtentEx(500));
		containerEx.setPosition(ContainerEx.RELATIVE);
		containerEx.setBorder(new BorderEx());

		containerEx.add(splitPane);
		return containerEx;
	}

	public Component testAddChildMenuItems() {

		final MenuBar bar = new MenuBar();

		bar.add(new MenuItem("File"));
		bar.add(new MenuItem("Edit"));
		bar.add(new MenuItem("View"));

		final MenuItem addMore = new MenuItem("Add More");
		bar.add(addMore);
		addMore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bar.add(new MenuItem("Source"));
				bar.add(new MenuItem("Navigate"));
				bar.add(new MenuItem("Help"));
			}
		});
		final MenuItem rmeoveLastThree = new MenuItem("Remove Last Three");
		bar.add(rmeoveLastThree);
		rmeoveLastThree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int len = bar.getComponentCount();
				bar.remove(len - 1);
				bar.remove(len - 2);
				bar.remove(len - 3);
			}
		});
		return bar;
	}

	public Component testActionPerformed() {
		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("a menu");

		MenuItem test = new MenuItem("test");
		test.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("blah");
			}
		});

		menu.add(test);
		menuBar.add(menu);
		return menuBar;
	}

	public Component testMenuBorders() {
		Column cell = new Column();

		MenuBar rootbar = new MenuBar();
		rootbar.setBorder(BorderEx.NONE);
		rootbar.setWidth(new Extent(150));

		MenuButton mb = new MenuButton("Label.New");
		mb.setBackground(Color.RED);
		mb.setClickToOpen(true);
		mb.setAlignment(new Alignment(Alignment.CENTER, Alignment.CENTER));
		mb.setWidth(new Extent(150));
		mb.setStyleChildren(true);
		mb.setMenuBorder(new Border(3, Color.PINK, Border.STYLE_SOLID));

		// mb.setBorder(new Border(1,Color.YELLOW, Border.STYLE_SOLID));
		mb.setBorder(BorderEx.NONE);

		MenuItem nr = new MenuItem("Label.New.Report");
		MenuItem af = new MenuItem("Label.New.Format");

		mb.add(nr);
		mb.add(af);
		rootbar.add(mb);

		cell.add(rootbar);
		return cell;
	}
}
