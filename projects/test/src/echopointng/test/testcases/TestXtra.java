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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.WindowPaneListener;
import nextapp.echo2.app.layout.RowLayoutData;
import nextapp.echo2.app.text.StringDocument;
import nextapp.echo2.webcontainer.ContainerContext;
import nextapp.echo2.webrender.Connection;
import nextapp.echo2.webrender.WebRenderServlet;
import echopointng.BalloonHelp;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.ComboBox;
import echopointng.ContainerEx;
import echopointng.ContentPaneEx;
import echopointng.DateChooser;
import echopointng.DateField;
import echopointng.DropDown;
import echopointng.ExpandableSection;
import echopointng.ExtentEx;
import echopointng.ExternalEventMonitor;
import echopointng.GroupBox;
import echopointng.ImageIcon;
import echopointng.ImageMap;
import echopointng.KeyStrokeListener;
import echopointng.LabelEx;
import echopointng.LightBox;
import echopointng.ListBoxEx;
import echopointng.ListSection;
import echopointng.Menu;
import echopointng.MenuBar;
import echopointng.MenuButton;
import echopointng.MenuItem;
import echopointng.PasswordFieldEx;
import echopointng.PopUp;
import echopointng.PushButton;
import echopointng.RichTextArea;
import echopointng.SelectFieldEx;
import echopointng.Separator;
import echopointng.TabbedPane;
import echopointng.TemplatePanel;
import echopointng.TextAreaEx;
import echopointng.TextFieldEx;
import echopointng.TitleBar;
import echopointng.Tree;
import echopointng.able.Attributeable;
import echopointng.able.Positionable;
import echopointng.externalevent.ExternalEvent;
import echopointng.externalevent.ExternalEventListener;
import echopointng.template.StringTemplateDataSource;
import echopointng.util.ColorKit;
import echopointng.util.HtmlKit;
import echopointng.util.RandKit;
import echopointng.xhtml.XhtmlFragment;

/**
 * <code>TestXTra</code>
 */

public class TestXtra extends TestCaseBaseNG {

	private Component addExampleComponent(Component exampleComponent) {
		Row r = new Row();
		RowLayoutData rld = new RowLayoutData();
		rld.setWidth(new ExtentEx("250px"));
		LabelEx lbl = new LabelEx(exampleComponent.getClass().getName());
		lbl.setLayoutData(rld);

		r.setCellSpacing(new ExtentEx("2em"));
		r.add(lbl);
		r.add(exampleComponent);
		return r;

	}

	private void addKeyCombinations(KeyStrokeListener ks, int keyCombo) {
		String keyName = KeyStrokeListener.getMnemonic(keyCombo);

		final int ALT = KeyStrokeListener.ALT_MASK;
		final int SHIFT = KeyStrokeListener.SHIFT_MASK;
		final int CTRL = KeyStrokeListener.CONTROL_MASK;

		ks.addKeyCombination(keyCombo, keyName);
		ks.addKeyCombination(SHIFT | keyCombo);
		ks.addKeyCombination(ALT | keyCombo);
		ks.addKeyCombination(CTRL | keyCombo);
		ks.addKeyCombination(SHIFT | CTRL | keyCombo);
		ks.addKeyCombination(ALT | SHIFT | CTRL | keyCombo);
		ks.addKeyCombination(ALT | CTRL | keyCombo);
	}

	private void addTestAttributes(Component parent) {
		Component[] children = parent.getComponents();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Attributeable) {
				Attributeable attributeable = (Attributeable) children[i];
				attributeable.setAttribute("componentClass", children[i].getClass());
				attributeable.setAttribute("childCount", String.valueOf(i));
			}
		}
	}

	private KeyStrokeListener createKSL() {

		KeyStrokeListener ks = new KeyStrokeListener();
		for (int i = KeyStrokeListener.VK_A; i <= KeyStrokeListener.VK_Z; i++) {
			addKeyCombinations(ks, i);
		}
		for (int i = KeyStrokeListener.VK_F1; i <= KeyStrokeListener.VK_F19; i++) {
			addKeyCombinations(ks, i);
		}
		for (int i = KeyStrokeListener.VK_0; i <= KeyStrokeListener.VK_9; i++) {
			addKeyCombinations(ks, i);
		}
		ks.addKeyCombination(KeyStrokeListener.VK_NUM_LOCK);
		ks.addKeyCombination(KeyStrokeListener.VK_ESCAPE);
		ks.addKeyCombination(KeyStrokeListener.VK_ALT);
		ks.addKeyCombination(KeyStrokeListener.VK_CONTROL);
		ks.addKeyCombination(KeyStrokeListener.VK_SHIFT);
		ks.addKeyCombination(KeyStrokeListener.VK_TAB);
		ks.addKeyCombination(KeyStrokeListener.VK_BACK_SPACE);
		ks.addKeyCombination(KeyStrokeListener.VK_BACK_QUOTE);
		ks.addKeyCombination(KeyStrokeListener.VK_BACK_SLASH);
		ks.addKeyCombination(KeyStrokeListener.VK_SPACE);
		ks.addKeyCombination(KeyStrokeListener.VK_END);
		ks.addKeyCombination(KeyStrokeListener.VK_HOME);
		ks.addKeyCombination(KeyStrokeListener.VK_PAGE_UP);
		ks.addKeyCombination(KeyStrokeListener.VK_PAGE_DOWN);
		ks.addKeyCombination(KeyStrokeListener.VK_INSERT);
		ks.addKeyCombination(KeyStrokeListener.VK_DELETE);
		ks.addKeyCombination(KeyStrokeListener.VK_ENTER);
		ks.addKeyCombination(KeyStrokeListener.VK_RETURN);
		ks.addKeyCombination(KeyStrokeListener.VK_UP);
		ks.addKeyCombination(KeyStrokeListener.VK_DOWN);
		ks.addKeyCombination(KeyStrokeListener.VK_LEFT);
		ks.addKeyCombination(KeyStrokeListener.VK_RIGHT);
		// ks.setCancelMode(true);
		return ks;
	}

	/**
	 * @see echopointng.test.testcases.TestCaseBaseNG#getTestCategory()
	 */
	public String getTestCategory() {
		return "Xtra Tests";
	}

	private void setupImageIcon(final ImageIcon imageIcon, final Label currentSizeLabel) {
		final ImageReference image1 = new HttpImageReference("images/t_astronaut.jpg");
		final ImageReference image2 = new HttpImageReference("images/t_crane.jpg");
		imageIcon.setIcon(image1);
		imageIcon.setHeight(new Extent(64));
		imageIcon.setWidth(new Extent(64));
		imageIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (imageIcon.getIcon() == image1) {
					imageIcon.setIcon(image2);
				} else {
					imageIcon.setIcon(image1);
				}
				int newSize = RandKit.rand(64, 200);
				imageIcon.setHeight(new Extent(newSize));
				imageIcon.setWidth(new Extent(newSize));

				currentSizeLabel.setText("(" + newSize + "px by " + newSize + "px)");

			}
		});
	}

	private void setupImageMap(ImageMap imageMap, final Label whichBit) {
		ImageReference image = new HttpImageReference("images/imagemap_planets.gif");
		imageMap.setImage(image);
		imageMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whichBit.setText("You clicked on " + e.getActionCommand());
			}
		});

		ImageMap.Coords coords = new ImageMap.Coords(0, 0, 82, 126, "the Sun");
		coords.setAltText("Click here for the Sun");
		imageMap.addCoord(coords);

		coords = new ImageMap.Coords(90, 58, 3, "Mercury");
		coords.setAltText("Click here for Mercury");
		imageMap.addCoord(coords);

		coords = new ImageMap.Coords(124, 58, 8, "Venus");
		coords.setAltText("Click here for Venus");
		imageMap.addCoord(coords);

		coords = new ImageMap.Coords(new int[] { 86, 72, 136, 77, 106, 98, 86, 103, 139, 117 }, "bottom space");
		imageMap.addCoord(coords);

		coords = new ImageMap.Coords(new int[] { 88, 12, 136, 12, 136, 46, 88, 47, 118, 27 }, "top space");
		imageMap.addCoord(coords);
	}

	public Component testContainerEx() {
		Column col = new Column();
		ContainerEx containerEx;

		containerEx = new ContainerEx(new TextField());
		containerEx.setPosition(Positionable.ABSOLUTE);
		containerEx.setLeft(new ExtentEx(150));
		containerEx.setTop(new ExtentEx(150));
		col.add(containerEx);

		containerEx = new ContainerEx(new DateChooser());
		containerEx.setPosition(Positionable.ABSOLUTE);
		containerEx.setLeft(new ExtentEx(250));
		containerEx.setTop(new ExtentEx(250));
		col.add(containerEx);

		containerEx = new ContainerEx(new LabelEx("Relative 1"));
		containerEx.setBackground(Color.RED);
		containerEx.setPosition(Positionable.RELATIVE);
		containerEx.setLeft(new ExtentEx(10));
		containerEx.setTop(new ExtentEx(10));
		col.add(containerEx);

		containerEx = new ContainerEx(new LabelEx("Relative 2"));
		containerEx.setBackground(Color.YELLOW);
		containerEx.setPosition(Positionable.RELATIVE);
		containerEx.setLeft(new ExtentEx(20));
		containerEx.setTop(new ExtentEx(20));
		col.add(containerEx);

		containerEx = new ContainerEx(new LabelEx("Relative 3"));
		containerEx.setBackground(Color.MAGENTA);
		containerEx.setPosition(Positionable.RELATIVE);
		containerEx.setLeft(new ExtentEx(30));
		containerEx.setTop(new ExtentEx(30));
		col.add(containerEx);

		for (int i = 0; i < 10; i++) {
			Component text = genTextContent();
			text.setBackground(ColorKit.makeColor(0xACBCDC));
			col.add(text);
		}
		return col;
	}

	public Component testDefaultComponentContructors() {

		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("5em"));

		cell.add(addExampleComponent(new BalloonHelp()));
		cell.add(addExampleComponent(new ButtonEx()));
		cell.add(addExampleComponent(new ComboBox()));
		cell.add(addExampleComponent(new DateChooser()));
		cell.add(addExampleComponent(new DateField()));
		cell.add(addExampleComponent(new DropDown()));
		cell.add(addExampleComponent(new ExpandableSection()));
		cell.add(addExampleComponent(new GroupBox()));
		cell.add(addExampleComponent(new Menu()));
		cell.add(addExampleComponent(new MenuBar()));
		cell.add(addExampleComponent(new MenuButton()));
		cell.add(addExampleComponent(new MenuItem()));
		cell.add(addExampleComponent(new PopUp()));
		cell.add(addExampleComponent(new PushButton()));
		cell.add(addExampleComponent(new RichTextArea()));
		cell.add(addExampleComponent(new TabbedPane()));
		cell.add(addExampleComponent(new TemplatePanel()));
		cell.add(addExampleComponent(new TitleBar()));
		cell.add(addExampleComponent(new Tree()));

		return cell;
	}

	public Component testGroupBox() {
		Component container = new Column();
		GroupBox gb;

		gb = new GroupBox();
		container.add(gb);
		gb.setTitle("A Simple GroupBox");

		gb.add(new Label("A Label"));
		gb.add(new TextField(new StringDocument(), "A TextField", 8));
		gb.add(new Label("A Label"));
		gb.add(new TextField(new StringDocument(), "A TextField", 12));
		gb.add(new Label("A Label"));
		gb.add(new TextField(new StringDocument(), "A TextField", 10));

		gb = new GroupBox();
		container.add(gb);
		gb.setTitle("A Simple GroupBox Thats is Disabled");
		gb.setEnabled(false);

		gb.add(new Label("A Label"));
		gb.add(new TextField(new StringDocument(), "A TextField", 8));
		gb.add(new Label("A Label"));
		gb.add(new TextField(new StringDocument(), "A TextField", 12));
		gb.add(new Label("A Label"));
		gb.add(new TextField(new StringDocument(), "A TextField", 10));

		gb = new GroupBox();
		container.add(gb);
		gb.setBorder(new BorderEx(2, Color.BLACK, BorderEx.STYLE_INSET));
		gb.setTitle("Another GroupBox With Background and Outsets (Padding)");
		gb.setBackground(Color.YELLOW);
		gb.setOutsets(new Insets(10));
		gb.add(genTextContent());

		gb = new GroupBox();
		container.add(gb);
		gb.setBorder(new BorderEx(1, Color.BLUE, BorderEx.STYLE_DASHED));
		gb.setInsets(new Insets(20));
		gb.setTitle("Yet Another GroupBox");

		gb.add(genTextContent());

		// font issue
		gb = new GroupBox();
		container.add(gb);
		gb.setFont(new Font(Font.VERDANA,Font.BOLD,new ExtentEx("10pt")));
		gb.setInsets(new Insets(20));
		gb.setTitle("A Bolded GroupBox");

		Label label;
		label= new LabelEx("A plain fonted child");
		label.setFont(new Font(Font.VERDANA,Font.PLAIN,new ExtentEx("8pt")));
		gb.add(label);
		label= new LabelEx("A underlined fonted child");
		label.setFont(new Font(Font.VERDANA,Font.UNDERLINE,new ExtentEx("8pt")));
		gb.add(label);
		label= new LabelEx("A intalicized fonted child");
		label.setFont(new Font(Font.VERDANA,Font.ITALIC,new ExtentEx("8pt")));
		gb.add(label);

		return container;
	}

	public Component testImageIcon() {
		final LabelEx currentSizeLabel = new LabelEx("Click me to resize and change image");

		ImageIcon imageIcon;

		Column col = new Column();
		col.setCellSpacing(new ExtentEx("10mm"));
		col.add(currentSizeLabel);

		imageIcon = new ImageIcon();
		setupImageIcon(imageIcon, currentSizeLabel);
		col.add(imageIcon);

		imageIcon = new ImageIcon();
		setupImageIcon(imageIcon, currentSizeLabel);
		imageIcon.setEnabled(false);
		col.add(imageIcon);

		return col;
	}

	public Component testImageMap() {
		final LabelEx whichBit = new LabelEx("Click on some part of the image.");

		ImageMap imageMap;

		Column col = new Column();
		col.add(whichBit);

		imageMap = new ImageMap();
		setupImageMap(imageMap, whichBit);
		col.add(imageMap);

		imageMap = new ImageMap();
		setupImageMap(imageMap, whichBit);
		imageMap.setBorder(new BorderEx(2, Color.ORANGE, BorderEx.STYLE_DOTTED));
		col.add(imageMap);

		imageMap = new ImageMap();
		imageMap.setEnabled(false);
		setupImageMap(imageMap, whichBit);
		col.add(imageMap);

		return col;
	}

	public Component testKeyStrokeListener() {
		final LabelEx textLabel = new LabelEx("Go ahead start pressing keys.  If they bubble up to higher levels "
				+ "then our KeyStrokeListener will catch them and raise an event!");
		textLabel.setLineWrap(true);

		final LabelEx whichKey = new LabelEx("");

		final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
		final ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whichKey.setText(sdf.format(new Date()) + " The " + e.getActionCommand() + " key combination was pressed. ");
			}
		};

		final ContainerEx parentContainer = new ContainerEx();

		final TextField targetTextField = new TextField();
		targetTextField.setBackground(Color.RED);

		final KeyStrokeListener ks = createKSL();
		ks.addActionListener(al);
		parentContainer.add(ks);

		ButtonEx button = new ButtonEx("Remove and Add KeyStrokeListener");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whichKey.setText("KSL removed and added");
				parentContainer.removeAll();
				KeyStrokeListener newKSL = createKSL();
				newKSL.addActionListener(al);
				parentContainer.add(newKSL);
			}
		});

		final ButtonEx buttonTarget = new ButtonEx("Target Red TextField");
		buttonTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ks.getTarget() != null) {
					ks.setTarget(null);
					buttonTarget.setText("Target Red TextField");
				} else {
					ks.setTarget(targetTextField);
					buttonTarget.setText("Dont Target Red TextField");
				}
			}
		});

		Column col = new Column();
		col.add(textLabel);
		col.add(targetTextField);
		col.add(new TextField());
		col.add(button);
		col.add(buttonTarget);
		col.add(whichKey);
		col.add(parentContainer);
		return col;
	}

	public Component testListComponents() {

		String[] listBoxText = new String[] { "Alpha", "Beta", "Delta", "Gamma", "Omega" };

		Grid cell = new Grid(2);
		cell.setInsets(new Insets(5));

		cell.add(new LabelEx("ListBoxEx"));
		cell.add(new ListBoxEx(listBoxText));

		cell.add(new LabelEx("SelectFieldEx"));
		cell.add(new SelectFieldEx(listBoxText));

		addTestAttributes(cell);

		return cell;
	}

	public Component testListSection() {
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("2em"));
		
		ListSection listSection;

		Object[] listOfObjs = { "Alpha", "Delta", "Gamma", "Beta", "Omega", "Foxtrot", "Tango", "Whizzo" };

		ImageReference bullet = new HttpImageReference("images/ArrowRoundLeft.gif");

		// multi object ones
		listSection = new ListSection(ListSection.UNORDERED);
		listSection.setBullets(ListSection.BULLETS_SQUARE);
		listSection.add(new Label("Item 1 as a Label"));
		listSection.add(new XhtmlFragment("Item 2 as an XhtmlFragment <i>(with cool markup)</i>"));
		listSection.add(new String("Item 3 as a String"));
		listSection.add(new Label("Item 4 as a Label"),3);

			ListSection childListSection = new ListSection(ListSection.UNORDERED);
			childListSection.setBullets(ListSection.BULLETS_CIRCLE);
			childListSection.add("Child Item 1");
			childListSection.add("Child Item 2");
			childListSection.add("Child Item 3");
			
				ListSection grandchildListSection = new ListSection(ListSection.UNORDERED);
				grandchildListSection.setBullets(ListSection.BULLETS_LOWER_GREEK);
				grandchildListSection.add("Grand Child Item 1");
				grandchildListSection.add("Grand Child Item 2");
				grandchildListSection.add("Grand Child Item 3");			
			
			childListSection.add(grandchildListSection);
		listSection.add(childListSection);
		listSection.add(new XhtmlFragment("What do you think of the <u>lists within lists</u> shown above?"));
		
		cell.add(listSection);
		cell.add(new Separator());

		// default case
		listSection = new ListSection(ListSection.ORDERED, listOfObjs);
		cell.add(listSection);
		cell.add(new Separator());

		// different bullets and UI
		listSection = new ListSection(ListSection.UNORDERED, listOfObjs);
		listSection.setBullets(ListSection.BULLETS_SQUARE);
		listSection.setBorder(new Border(2, Color.LIGHTGRAY, Border.STYLE_GROOVE));
		listSection.setInsets(new Insets(0));
		listSection.setOutsets(new Insets(0));
		cell.add(listSection);
		cell.add(new Separator());

		// user defined bullets
		listSection = new ListSection(ListSection.UNORDERED, listOfObjs);
		listSection.setBulletsImage(bullet);
		cell.add(listSection);
		cell.add(new Separator());
		
		return cell;
	}

	public Component testSeparator() {
		Separator separator;
		Column col = new Column();
		col.setInsets(new Insets(20));
		col.setBorder(new Border(1, Color.BLACK, Border.STYLE_RIDGE));

		separator = new Separator();
		col.add(separator);

		separator = new Separator();
		separator.setTopSize(new Extent(5));
		col.add(separator);

		separator = new Separator();
		separator.setBottomSize(new Extent(3));
		separator.setBottomColor(Color.ORANGE);
		col.add(separator);

		separator = new Separator();
		separator.setTopSize(new Extent(10));
		separator.setTopColor(Color.YELLOW);
		separator.setBottomColor(Color.PINK);
		col.add(separator);

		separator = new Separator();
		separator.setTopSize(new Extent(1));
		separator.setTopColor(Color.BLUE);
		separator.setBottomSize(null);
		col.add(separator);

		return col;
	}

	public Component testTextFields() {
		Grid cell = new Grid(2);
		cell.setInsets(new Insets(5));

		cell.add(new LabelEx("TextFieldEx"));
		cell.add(new TextFieldEx("TextFieldEx"));

		cell.add(new LabelEx("TextAreaEx"));
		cell.add(new TextAreaEx("TextAreaEx"));

		cell.add(new LabelEx("PasswordFieldEx"));
		cell.add(new PasswordFieldEx());

		addTestAttributes(cell);

		return cell;
	}

	public Component testExternalEventMonitor() {

		Font smallfont = new Font(Font.ARIAL, Font.PLAIN, new ExtentEx("8pt"));

		ApplicationInstance app = ApplicationInstance.getActive();
		ContainerContext containerContext = (ContainerContext) app.getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
		Map initialParameters = containerContext.getInitialRequestParameterMap();
		StringBuffer sb = new StringBuffer();
		if (initialParameters != null) {
			for (Iterator iter = initialParameters.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String[] values = (String[]) initialParameters.get(key);
				for (int i = 0; i < values.length; i++) {
					sb.append("&");
					sb.append(key);
					sb.append("=");
					sb.append(values[i]);
				}
			}
		}
		if (sb.length() == 0) {
			sb.append("No initial parameters where provided when the Echo2 application started.");
		} else {
			sb.insert(0, "Initial parameters where provided when the Echo2 application started : ");
		}
		Label initParamsLabel = new Label(sb.toString());

		StringBuffer externalEventURI = new StringBuffer();
		Connection conn = WebRenderServlet.getActiveConnection();
		if (conn != null) {
			externalEventURI.append(conn.getRequest().getRequestURI());
		}
		externalEventURI.append("?serviceId=ExternalEvent&param1=value1&parm2=value2&dateTime=" + new Date());

		sb = new StringBuffer("Cause an external event via the URI :  ");
		sb.append(externalEventURI);

		final Label eventLabel = new Label(sb.toString());

		String htmlText = "<div>You can <a href=\"" + HtmlKit.encode(externalEventURI.toString()) + "\">click this link to cause "
				+ "an external event.</a></div>";
		StringTemplateDataSource htmlDS = new StringTemplateDataSource(htmlText);
		final TemplatePanel panel = new TemplatePanel(htmlDS);

		ExternalEventMonitor eventMonitor = new ExternalEventMonitor();
		eventMonitor.addExternalEventListener(new ExternalEventListener() {
			/**
			 * @see echopointng.externalevent.ExternalEventListener#externalEvent(echopointng.externalevent.ExternalEvent)
			 */
			public void externalEvent(ExternalEvent externalEvent) {
				StringBuffer sb = new StringBuffer("An external event happened via URI : ");
				String[] names = externalEvent.getParameterNames();
				for (int i = 0; i < names.length; i++) {
					String name = names[i];
					String values[] = externalEvent.getParameterValues(name);
					for (int j = 0; j < values.length; j++) {
						if (i == 0) {
							sb.append('?');
						} else {
							sb.append('&');
						}
						sb.append(name);
						sb.append('=');
						sb.append(values[j]);
					}
				}
				eventLabel.setText(sb.toString());

				StringBuffer externalEventURI = new StringBuffer();
				Connection conn = WebRenderServlet.getActiveConnection();
				if (conn != null) {
					externalEventURI.append(conn.getRequest().getRequestURI());
				}
				externalEventURI.append("?serviceId=ExternalEvent&param1=value1&parm2=value2&dateTime=" + new Date());
				String htmlText = "<div>You can <a href=\"" + HtmlKit.encode(externalEventURI.toString()) + "\">click this link to cause "
						+ "an external event.</a></div>";
				StringTemplateDataSource htmlDS = new StringTemplateDataSource(htmlText);
				panel.setTemplateDataSource(htmlDS);

			}
		});
		Column cell = new Column();
		cell.setCellSpacing(new Extent(20));
		cell.setFont(smallfont);

		cell.add(initParamsLabel);
		cell.add(eventLabel);
		cell.add(panel);

		cell.add(eventMonitor);
		return cell;
	}

	public Component testShowJavaProperties() {
		Grid grid = new Grid(2);
		grid.setInsets(new Insets(new ExtentEx(5)));
		grid.setBorder(new BorderEx());

		Properties properties = System.getProperties();
		for (Iterator iter = properties.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();

			grid.add(new Label(String.valueOf(key)));
			grid.add(new Label(String.valueOf(properties.get(key))));
		}
		return grid;
	}

	public Component testExceptionHandling() {
		if (true) {
			throw new UnknownError("An exception has been deliberately thrown here!");
		}
		return new Label("Wont get here!");
	}

	public Component testTextFieldPerformance() {
		Column column = new Column();

		for (int i = 0; i < 200; i++) {
			Row row = new Row();
			row.setCellSpacing(new Extent(5));
			for (int j = 0; j < 10; j++) {
				row.add(new TextField());
			}
			column.add(row);
		}
		return column;
	}

	public Component testLightBox() {
		final WindowPane windowPane = new WindowPane("A LightBox Test Window", new Extent(500), new Extent(500));
		windowPane.setModal(true);
		Column cell = new Column();
		windowPane.add(cell);
		for (int i = 0; i < 10; i++) {
			cell.add(genRandomTextContent());
		}

		final ContentPaneEx paneEx = new ContentPaneEx();

		final LightBox lightBox = new LightBox();
		lightBox.setHidden(true);
		paneEx.add(lightBox);

		WindowPaneListener listener = new WindowPaneListener() {
			public void windowPaneClosing(nextapp.echo2.app.event.WindowPaneEvent e) {
				lightBox.hide();
			};
		};
		windowPane.addWindowPaneListener(listener);

		cell = new Column();
		paneEx.add(cell);
		cell.setCellSpacing(new ExtentEx("5em"));

		ButtonEx b;
		b = new ButtonEx("Toggle WindowPane With LightBox");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (windowPane.getParent() == null) {
					lightBox.show();
					paneEx.add(windowPane);
				} else {
					lightBox.hide();
					paneEx.add(windowPane);
				}

			}
		});
		cell.add(b);

		b = new ButtonEx("Toggle WindowPane Without LightBox");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lightBox.hide();
				if (windowPane.getParent() == null) {
					paneEx.add(windowPane);
				} else {
					paneEx.add(windowPane);
				}
			}
		});
		cell.add(b);
		for (int i = 0; i < 20; i++) {
			cell.add(genRandomTextContent());
		}

		return paneEx;
	}

}
