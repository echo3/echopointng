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

import java.util.Date;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.ContentPane;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.StyleSheet;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.componentxml.ComponentXmlException;
import nextapp.echo2.app.componentxml.StyleSheetLoader;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.ContainerEx;
import echopointng.DirectHtml;
import echopointng.ExtentEx;
import echopointng.TemplatePanel;
import echopointng.able.MouseCursorable;
import echopointng.able.Positionable;
import echopointng.able.Scrollable;
import echopointng.able.Stretchable;
import echopointng.layout.DisplayLayoutData;
import echopointng.layout.ScrollableDisplayLayoutData;
import echopointng.template.StringTemplateDataSource;
import echopointng.template.TemplateDataSource;
import echopointng.test.controller.TestController;
import echopointng.ui.util.io.StringInputStream;
import echopointng.util.ColorKit;
import echopointng.util.FontKit;
import echopointng.util.RandKit;

/**
 * <code>TestContainerEx</code>
 */
public class TestContainerEx extends TestCaseBaseNG {

	public String getTestCategory() {
		return "ContainerEx";
	}

	public Component testContainerExDirect() {
		TemplatePanel templateInner = new TemplatePanel(new StringTemplateDataSource("<bdo>" + genBackgroundText(5) + "</bdo>"));

		ContainerEx containerExInner = new ContainerEx();
		containerExInner.setWidth(new ExtentEx(400));
		containerExInner.setHeight(new ExtentEx(200));
		containerExInner.setBorder(new BorderEx(1, Color.YELLOW));
		containerExInner.setBackground(Color.ORANGE);
		containerExInner.add(templateInner);

		String templateStr = "<p><i>" + "This is a simple example of a ContainerEx that contains some components "
				+ "as well as another ContainerEx inside it.  " + "</i></p>" + "<p>"
				+ "And here is the extra ContainerEx that is contained inside the outer "
				+ "ContainerEx. <component name=\"inner\" /> So now you can have " + "scrollable and positionable components in Echo2 without "
				+ "having to use WindowPane." + "</p>";
		templateStr += genBackgroundText(5);
		templateStr = "<bdo>" + templateStr + "</bdo>";
		TemplatePanel template = new TemplatePanel(new StringTemplateDataSource(templateStr));
		template.addNamedComponent(containerExInner, "inner");

		ContainerEx containerEx = new ContainerEx();
		containerEx.setWidth(new ExtentEx(500));
		containerEx.setHeight(new ExtentEx(500));
		containerEx.setPosition(ContainerEx.RELATIVE);
		containerEx.setBorder(new BorderEx(1));
		containerEx.add(template);

		return containerEx;
	}

	public Component testDisplayLayoutData() {
		String templateStr = "<p><i>" + "Note that the ContainerEx used in this example has its "
				+ "position set to RELATIVE.  This must be done because HTML " + "positions its child elements relative to the next "
				+ "positioned (ie not STATIC) parent element.  Hence the "
				+ "ContainerEx parent must be positioned as RELATIVE (or ABSOLUTE) in order "
				+ "for its child components to be placed in the expected " + "place." + "</i></p>";
		templateStr += genBackgroundText(1);
		templateStr = "<bdo>" + templateStr + "</bdo>";
		TemplatePanel template = new TemplatePanel(new StringTemplateDataSource(templateStr));

		ContainerEx containerEx = new ContainerEx();
		containerEx.setWidth(new ExtentEx(500));
		containerEx.setHeight(new ExtentEx(500));
		containerEx.setPosition(ContainerEx.RELATIVE);
		containerEx.setBorder(new BorderEx());

		containerEx.add(template);

		// add some positioned objects
		containerEx.add(newLabelAt(20, 120));
		containerEx.add(newLabelAt(200, 200));
		containerEx.add(newLabelAt(40, 140));
		containerEx.add(newLabelAt(60, 160));

		containerEx.add(newLabelSized(100, 50));
		containerEx.add(newLabelSized(400, 50));

		return containerEx;
	}

	private Label newLabelAt(int left, int top) {
		Label l;
		DisplayLayoutData dld;

		dld = new DisplayLayoutData();
		dld.setPosition(Positionable.ABSOLUTE);
		dld.setLeft(new ExtentEx(left));
		dld.setTop(new ExtentEx(top));

		l = new Label("Positioned at (" + left + "," + top + ")");
		l.setLayoutData(dld);
		l.setBackground(Color.ORANGE);
		return l;
	}

	private Label newLabelSized(int width, int height) {
		Label l;
		DisplayLayoutData dld;

		dld = new DisplayLayoutData();
		dld.setWidth(new ExtentEx(width));
		dld.setHeight(new ExtentEx(height));
		dld.setOutsets(new Insets(5));
		dld.setInsets(new Insets(5));
		dld.setBorder(new BorderEx(1, Color.BLACK, BorderEx.STYLE_DASHED));
		dld.setBackground(Color.BLUE);

		l = new Label("Sized as (" + width + "w," + height + "h )");
		l.setLayoutData(dld);
		return l;
	}

	private String genBackgroundText(int howmuch) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < howmuch; i++) {
			sb.append("<p>" + "Aliquam sed pede id justo egestas tempor. " + "Sed sagittis. Sed mollis. Pellentesque vel wisi. Curabitur "
					+ "turpis urna, cursus et, blandit nec, fringilla vitae, nibh. "
					+ "Quisque ornare porttitor diam. Vivamus imperdiet sollicitudin " + "lorem. Nunc semper sapien condimentum urna. Maecenas "
					+ "vulputate dui. Suspendisse in justo. Phasellus a eros. " + "Aliquam lectus. Vivamus viverra arcu ac turpis. Quisque "
					+ "commodo est id quam. Donec vestibulum. Integer euismod " + "lorem ac lectus.</p>" + "<p>"
					+ "Class aptent taciti sociosqu ad litora torquent per conubia "
					+ "nostra, per inceptos hymenaeos. Nam convallis. Donec aliquam " + "blandit purus. Maecenas aliquam, nulla volutpat dictum "
					+ "viverra, elit lacus consectetuer ipsum, in luctus sem dui " + "at lacus. Lorem ipsum dolor sit amet, consectetuer "
					+ "adipiscing elit. Fusce ligula. Mauris et orci. Aenean " + "tempor tellus id tellus. Cras in sapien vel nibh blandit "
					+ "malesuada. In sagittis. Duis turpis tortor, auctor et, " + "sodales nec, dapibus in, erat." + "</p>");
		}
		return sb.toString();
	}

	public Component testWithScrollableDisplayLayoutData() {
		String templateStr = "<p>" + "<i>" + "This is an example of a ScrollableDisplayLayoutData "
				+ "that allows a component that doesnt specifically support " + "scrolling to have a scrolling region around it." + "</i>" + "</p>";
		templateStr += genBackgroundText(1);
		templateStr = "<bdo>" + templateStr + "</bdo>";
		TemplateDataSource dataSource = new StringTemplateDataSource(templateStr);

		ContainerEx containerEx = new ContainerEx();
		containerEx.setWidth(new ExtentEx(500));
		containerEx.setHeight(new ExtentEx(500));
		containerEx.setPosition(ContainerEx.RELATIVE);
		containerEx.setBorder(new BorderEx());

		// add some scrollable regions

		ScrollableDisplayLayoutData sdld = new ScrollableDisplayLayoutData();
		sdld.setScrollBarPolicy(Scrollable.ALWAYS);
		sdld.setBorder(new BorderEx(1, Color.RED));
		sdld.setOutsets(new Insets(5));
		sdld.setWidth(new Extent(400));
		sdld.setHeight(new Extent(200));
		sdld.setMouseCursor(MouseCursorable.CURSOR_HELP);

		TemplatePanel template = new TemplatePanel(dataSource);
		template.setLayoutData(sdld);
		containerEx.add(template);

		sdld = new ScrollableDisplayLayoutData();
		sdld.setScrollBarPolicy(Scrollable.CLIPHIDE);
		sdld.setBorder(new BorderEx(1, Color.BLUE));
		sdld.setOutsets(new Insets(15));
		sdld.setInsets(new Insets(5));
		sdld.setWidth(new Extent(400));
		sdld.setHeight(new Extent(200));
		sdld.setScrollBarBaseColor(Color.PINK);
		sdld.setBackground(Color.MAGENTA);
		sdld.setMouseCursor(MouseCursorable.CURSOR_CROSSHAIR);

		template = new TemplatePanel(dataSource);
		template.setLayoutData(sdld);
		containerEx.add(template);

		return containerEx;
	}

	public Component testFloatedContainers() {
		TemplateDataSource dataSource = new StringTemplateDataSource("<div>" + genBackgroundText(1) + "</div>");

		ContainerEx outerContainer = new ContainerEx();
		outerContainer.setBorder(new BorderEx());
		outerContainer.setInsets(new Insets(10));
		outerContainer.setScrollBarPolicy(Scrollable.AUTO);
		
		Label info = new Label("The containers below should left-align horizontally until there is insufficient room for them to fit, "
							+ "which will cause them to wrap onto the next line as needed. This is called a 'float'. "
							+ "Resize the browser window to check that the float wrapping is working correctly.");
		outerContainer.add(info);
		
		
		// Create four 200x200 containers
		ContainerEx floatedContainer;
		
		DisplayLayoutData dldFloat = new DisplayLayoutData();
		dldFloat.setInsets(new Insets(10));
		dldFloat.setFloat(DisplayLayoutData.FLOAT_LEFT);
		
		for (int i = 0; i < 4; i++) {
			int bg = 255 - (20 * i);
			
			floatedContainer = new ContainerEx();
			floatedContainer.setBackground(new Color(bg, bg, bg));
			floatedContainer.setWidth(new ExtentEx(200));
			floatedContainer.setHeight(new ExtentEx(200));
			floatedContainer.setPosition(ContainerEx.RELATIVE);
			floatedContainer.setBorder(new BorderEx());
			floatedContainer.setScrollBarPolicy(Scrollable.AUTO);
			floatedContainer.setLayoutData(dldFloat);
			floatedContainer.add(new TemplatePanel(dataSource));
			
			outerContainer.add(floatedContainer);
		}

		Label labelInline = new Label("This is a piece of inline text that should wrap around the containers if possible.");
		labelInline.setBackground(Color.YELLOW);
		outerContainer.add(labelInline);
		
		
		// Clear the floats
		DisplayLayoutData dldClear = new DisplayLayoutData();
		dldClear.setInsets(new Insets(10));
		dldClear.setClearFloat(DisplayLayoutData.FLOAT_CLEAR_LEFT);
		
		Label label = new Label("This text should clear the floated containers above it (ie it should never wrap on the same line).");
		label.setLayoutData(dldClear);
		outerContainer.add(label);
		
		
		DisplayLayoutData dldLeft = new DisplayLayoutData();
		dldLeft.setInsets(new Insets(10));
		dldLeft.setFloat(DisplayLayoutData.FLOAT_LEFT);
		
		Label labelLeft = new Label("This label should float to the left.");
		labelLeft.setBackground(Color.CYAN);
		labelLeft.setLayoutData(dldLeft);
		outerContainer.add(labelLeft);
		
		DisplayLayoutData dldRight = new DisplayLayoutData();
		dldRight.setInsets(new Insets(10));
		dldRight.setFloat(DisplayLayoutData.FLOAT_RIGHT);
		
		Label labelRight = new Label("This label should float to the right.");
		labelRight.setBackground(Color.PINK);
		labelRight.setLayoutData(dldRight);
		outerContainer.add(labelRight);
		
		return outerContainer;
	}
	
	public Component testSplitPane() {
		String templateStr = "<p><i>" + "This example shows the use of SplitPanes inside the ContainerEx.  "
				+ "This is posssible because ContainerEx implements PaneContainer." + "</i></p>";
		templateStr += genBackgroundText(2);
		templateStr = "<bdo>" + templateStr + "</bdo>";

		TemplateDataSource dataSource = new StringTemplateDataSource(templateStr);

		TemplatePanel templateTop = new TemplatePanel(dataSource);
		templateTop.setBackground(Color.CYAN);
		TemplatePanel templateBottom = new TemplatePanel(dataSource);
		templateBottom.setBackground(Color.YELLOW);

		SplitPane splitPane = new SplitPane();
		splitPane.add(templateTop);
		splitPane.add(templateBottom);
		splitPane.setSeparatorPosition(new Extent(400));
		splitPane.setSeparatorColor(Color.RED);
		splitPane.setResizable(true);

		DisplayLayoutData layoutData = new DisplayLayoutData();
		layoutData.setHeight(new ExtentEx("100%"));
		splitPane.setLayoutData(layoutData);

		ContainerEx containerEx = new ContainerEx();
		containerEx.setWidth(new ExtentEx(500));
		containerEx.setHeight(new ExtentEx(500));
		containerEx.setBackground(Color.PINK);
		containerEx.setBorder(new BorderEx());

		containerEx.add(splitPane);
		return containerEx;
	}

	public Component testWindowPane() {
		TemplatePanel templateInner = new TemplatePanel(new StringTemplateDataSource("<bdo>" + genBackgroundText(5) + "</bdo>"));

		ContentPane contentPaneInner = new ContentPane();
		contentPaneInner.add(templateInner);

		WindowPane windowPane = new WindowPane();
		windowPane.setModal(true);
		windowPane.setWidth(new Extent(200));
		windowPane.setHeight(new Extent(200));
		windowPane.add(contentPaneInner);

		TemplatePanel templateOuter = new TemplatePanel(new StringTemplateDataSource("<bdo>" + genBackgroundText(5) + "</bdo>"));

		ContentPane contentPane = new ContentPane();
		contentPane.add(windowPane);
		contentPane.add(templateOuter);
		contentPane.setBackground(ColorKit.makeColor("#acbcdc"));

		ContainerEx containerEx = new ContainerEx();
		containerEx.setBorder(new BorderEx());
		containerEx.setWidth(new Extent(500));
		containerEx.setHeight(new Extent(500));
		containerEx.add(contentPane);
		return containerEx;
	}

	public Component testRemoval() {
		final ContainerEx innerContainerEx = new ContainerEx();
		final ContainerEx outerContainerEx = new ContainerEx();

		innerContainerEx.add(new Label(" Remove me!"));
		innerContainerEx.add(new Label(" Remove me!"));
		innerContainerEx.add(new Label(" Remove me!"));
		innerContainerEx.add(new Label(" Remove me!"));
		innerContainerEx.add(new Label(" Remove me!"));

		ButtonEx b = new ButtonEx("Remove Them!");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				innerContainerEx.removeAll();

				innerContainerEx.add(new Label(" Removed at " + new Date()));
				innerContainerEx.add(new Label(" Removed at " + new Date()));
				innerContainerEx.add(new Label(" Removed at " + new Date()));
				innerContainerEx.add(new Label(" Removed at " + new Date()));
				innerContainerEx.add(new Label(" Removed at " + new Date()));
				innerContainerEx.add(new Label(" Removed at " + new Date()));

			}
		});
		outerContainerEx.add(b);
		outerContainerEx.add(innerContainerEx);
		return outerContainerEx;
	}

	public Component testRemoval2() {
		ContainerEx rootContainerEx = new ContainerEx();
		final ContainerEx buttonContainerEx = new ContainerEx();
		final ContainerEx bodyContainerEx = new ContainerEx();
		for (int i = 0; i < 5; i++) {
			Button button = new Button("Button " + i);
			final int index = i;
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					bodyContainerEx.removeAll();
					bodyContainerEx.add(new Label("loaded content from button " + index));
				}
			});
			buttonContainerEx.add(button);
		}
		rootContainerEx.add(buttonContainerEx);
		rootContainerEx.add(bodyContainerEx);
		return rootContainerEx;
	}

	public Component testBorders() {
		Border border = new BorderEx(new Extent(0), Color.BLUE, Border.STYLE_NONE, new Extent(2), Color.CYAN, Border.STYLE_SOLID, new Extent(0),
				Color.WHITE, Border.STYLE_SOLID, new Extent(1), Color.RED, Border.STYLE_SOLID);

		ContainerEx containerEx = new ContainerEx();
		containerEx.setBorder(border);

		containerEx.add(genRandomTextContent());

		Component moreText = genRandomTextContent();
		DisplayLayoutData layoutData = new DisplayLayoutData();
		layoutData.setBorder(border);
		layoutData.setOutsets(new Insets(10));
		moreText.setLayoutData(layoutData);

		containerEx.add(moreText);

		return containerEx;
	}

	public Component testPropertyUpdate() {
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("10em"));
		cell.setBackground(ColorKit.clr("#ECEDEF"));

		final BorderEx[] borders = new BorderEx[] { new BorderEx(Color.BLACK), new BorderEx(Color.BLUE), new BorderEx(Color.DARKGRAY), };

		final Color[] backgrounds = new Color[] { Color.CYAN, Color.WHITE, Color.GREEN, };

		final Color[] foregrounds = new Color[] { Color.BLACK, Color.DARKGRAY, Color.LIGHTGRAY, };

		final Insets[] insets = new Insets[] { new Insets(5), new Insets(15), new Insets(25), };

		final Insets[] outsets = new Insets[] { new Insets(5), new Insets(15), new Insets(25), };

		final int[] cursors = new int[] { MouseCursorable.CURSOR_CROSSHAIR, MouseCursorable.CURSOR_HELP, MouseCursorable.CURSOR_MOVE, };

		final ContainerEx containerEx = new ContainerEx();
		containerEx.add(genTextContent());

		Button button = new ButtonEx("Change properties");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				containerEx.setBorder((BorderEx) RandKit.roll(borders));
				containerEx.setBackground((Color) RandKit.roll(backgrounds));
				containerEx.setForeground((Color) RandKit.roll(foregrounds));
				containerEx.setInsets((Insets) RandKit.roll(insets));
				containerEx.setOutsets((Insets) RandKit.roll(outsets));
				containerEx.setMouseCursor(RandKit.roll(cursors));
			}
		});
		cell.add(button);
		cell.add(containerEx);
		return cell;
	}

	public Component testHidden() {
		Column col = new Column();
		col.setCellSpacing(new Extent(10));

		final ContainerEx containerEx = new ContainerEx(genTextContent());
		final Button button = new ButtonEx("Hide/Show ContainerEx");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean hidden = containerEx.isHidden();
				containerEx.setHidden(!hidden);
			};
		});

		col.add(button);
		col.add(containerEx);
		return col;
	}

	public Component testScroll() {
		Column col = new Column();
		col.setCellSpacing(new Extent(10));

		final Label currentValues = new Label();

		final ContainerEx containerEx = new ContainerEx();
		for (int i = 0; i < 10; i++) {
			containerEx.add(genRandomTextContent());

			ContainerEx wideEx = new ContainerEx();
			wideEx.setWidth(new ExtentEx(500));
			wideEx.setBorder(BorderEx.DEFAULT);
			containerEx.add(wideEx);

		}
		containerEx.setHeight(new ExtentEx("300px"));
		containerEx.setWidth(new ExtentEx("300px"));

		final Button buttonDown = new ButtonEx("Scroll ContainerEx Down");
		buttonDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Extent now = containerEx.getVerticalScroll();
				currentValues.setText("VerticalScroll:" + now);
				now = (now == null ? new Extent(0) : now);
				containerEx.setVerticalScroll(new Extent(now.getValue() + 50));

			};
		});

		final Button buttonDownMax = new ButtonEx("Scroll ContainerEx DownMax");
		buttonDownMax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Extent now = containerEx.getVerticalScroll();
				currentValues.setText("VerticalScroll:" + now);
				containerEx.setVerticalScroll(new Extent(-1));

			};
		});

		final Button buttonAcross = new ButtonEx("Scroll ContainerEx Across");
		buttonAcross.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Extent now = containerEx.getHorizontalScroll();
				currentValues.setText("HorizontalScroll:" + now);
				now = (now == null ? new Extent(0) : now);
				containerEx.setHorizontalScroll(new Extent(now.getValue() + 50));
			};
		});

		final Button buttonAcrossMax = new ButtonEx("Scroll ContainerEx Across Max");
		buttonAcrossMax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Extent now = containerEx.getHorizontalScroll();
				currentValues.setText("HorizontalScroll:" + now);
				containerEx.setHorizontalScroll(new Extent(-1));
			};
		});

		col.add(buttonDown);
		col.add(buttonAcross);
		col.add(containerEx);
		col.add(currentValues);
		col.add(buttonDownMax);
		col.add(buttonAcrossMax);
		return col;
	}

	public Component testBackgroundImage() {
		FillImage fillImage = new FillImage(new HttpImageReference("/images/tile_back.gif"), new Extent(0), new Extent(0), FillImage.REPEAT_HORIZONTAL);
		ContainerEx ex = new ContainerEx();
		ex.add(genRandomTextContent());
		ex.setBackgroundImage(fillImage);
		return ex;
	}
	
	  static final StyleSheet DEFAULT_STYLE_SHEET;
	  static {
	      try {
	    	  
	    	  String stylesheetXML = "" +
"<stylesheet>" +
"<style name=\"cont2Label1\" type=\"nextapp.echo2.app.Label\">" +
"  <properties>" +
"   <property name=\"layoutData\">" +
"    <layout-data type=\"echopointng.layout.DisplayLayoutData\">" +
"     <properties>" +
"      <property name=\"position\" value=\"ABSOLUTE\"/>" +
"      <property name=\"top\" value=\"10px\"/>" +
"      <property name=\"left\" value=\"30px\"/>" +
"     </properties>" +
"    </layout-data>" +
"   </property>" +
"  </properties>" +
"</style>" +

"<style name=\"cont2Label2\" type=\"nextapp.echo2.app.Label\">" +
" <properties>" +
"   <property name=\"layoutData\">" +
"    <layout-data type=\"echopointng.layout.DisplayLayoutData\">" +
"     <properties>" +
"      <property name=\"position\" value=\"ABSOLUTE\"/>" +
"      <property name=\"top\" value=\"10px\"/>" +
"      <property name=\"right\" value=\"30px\"/>" +
"     </properties>" +
"    </layout-data>" +
"   </property>" +
"  </properties>" +
"</style>" +
"</stylesheet>";

	    	  StringInputStream xmlStyleSheetStream = new StringInputStream(stylesheetXML);
	    	  
	    	  DEFAULT_STYLE_SHEET = StyleSheetLoader.load(xmlStyleSheetStream,
	                        Thread.currentThread().getContextClassLoader());
	          } catch(ComponentXmlException cxe) {
	          throw new RuntimeException(cxe);
	          }
	  };
	public Component testDisplayLayoutWithStyleSheets() {
	      ApplicationInstance.getActive().setStyleSheet(DEFAULT_STYLE_SHEET);
	        
	        ContentPane contentPane = new ContentPane();
	        Column column = new Column();
	        contentPane.add(column);
	        
	        ContainerEx cont1 = new ContainerEx();
	        cont1.setHeight(new Extent(50, Extent.PX));
	        cont1.setBackground(Color.LIGHTGRAY);
	        Label cont1Label1 = new Label("No StyleSheet - Label 1");
	        DisplayLayoutData dld = new DisplayLayoutData();
	        dld.setPosition(DisplayLayoutData.ABSOLUTE);
	        dld.setTop(new Extent(10, Extent.PX));
	        dld.setLeft(new Extent(30, Extent.PX));
	        cont1Label1.setLayoutData(dld);

	        Label cont1Label2 = new Label("No StyleSheet - Label 2");
	        dld = new DisplayLayoutData();
	        dld.setPosition(DisplayLayoutData.ABSOLUTE);
	        dld.setTop(new Extent(10, Extent.PX));
	        dld.setRight(new Extent(30, Extent.PX));
	        cont1Label2.setLayoutData(dld);
	        cont1.add(cont1Label1);
	        cont1.add(cont1Label2);
	        
	        ContainerEx cont2 = new ContainerEx();
	        cont2.setHeight(new Extent(50, Extent.PX));
	        cont2.setBackground(Color.CYAN);
	        Label cont2Label1 = new Label("Via StyleSheet - Label 1");
	        cont2Label1.setStyleName("cont2Label1");
	        Label cont2Label2 = new Label("Via StyleSheet - Label 2");
	        cont2Label2.setStyleName("cont2Label2");
	        cont2.add(cont2Label1);
	        cont2.add(cont2Label2);
	        
	        column.add(cont1);
	        column.add(cont2);
	        
	        return contentPane;
	  }
	
	public Component testDynamicPropeties() {
		final ContainerEx containerEx = new ContainerEx();
		final Column status = new Column();
		status.setFont(FontKit.font("'Times New Roman',plain,8pt"));
		final Runnable statusUpdater = new Runnable() {
			public void run() {
				status.removeAll();
				status.add(new Label("width : " + containerEx.getWidth()));
				status.add(new Label("height : " + containerEx.getHeight()));
				status.add(new Label("background : " + containerEx.getBackground()));
				status.add(new Label("foreground : " + containerEx.getForeground()));
			}
		};
		statusUpdater.run();
		
		containerEx.setPosition(Positionable.RELATIVE);
		containerEx.setScrollBarPolicy(Scrollable.CLIPHIDE);
		containerEx.setBorder(BorderEx.DEFAULT);
		ButtonEx buttonEx = new ButtonEx("Jiggle Properties");
		buttonEx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String widths[] = { "200px", "300px", "500px", "100%", "50%" };
				String heights[] = { "100px", "200px", "300px", "100%", "50%" };
				
				String backgrounds[] = { "#FAEBD7", "#FFEBCD", "#DEB887", "#6495ED", "#8FBC8B" };
				String foregrounds[] = { "#4B0082", "#000000", "#800000"};
				
				ExtentEx widthExtentEx = new ExtentEx(RandKit.roll(widths));
				ExtentEx heightExtentEx = new ExtentEx(RandKit.roll(heights));
				containerEx.setWidth(widthExtentEx);
				containerEx.setHeight(heightExtentEx);
				
				containerEx.setBackground(ColorKit.clr(RandKit.roll(backgrounds)));
				containerEx.setForeground(ColorKit.clr(RandKit.roll(foregrounds)));
				statusUpdater.run();
			}
		});
		containerEx.add(buttonEx);
		containerEx.add(status);
		containerEx.add(genRandomTextContent());
		return containerEx;
	}
	
	public Component testInlineSupport() {
		ContainerEx containerEx = new ContainerEx();
		containerEx.setWidth(new Extent(300));
		
		DisplayLayoutData layoutData = new DisplayLayoutData();
		//layoutData.setInlineLayout(true);
		for (int i = 0; i < 20; i++) {
			Label lbl = new Label("Label " + i);
			lbl.setLayoutData(layoutData);
			containerEx.add(lbl);
		}
		return containerEx;
	}
	
	public Component testStretch1() {
		
		ContainerEx containerExStretched = new ContainerEx();
		containerExStretched.setBorder(new BorderEx(Color.RED));
		containerExStretched.setOutsets(new Insets(5));
		containerExStretched.setInsets(new Insets(10));
		containerExStretched.setScrollBarPolicy(Scrollable.AUTO);
		containerExStretched.setHeightStretched(true);
		//containerExStretched.setMaximumStretchedHeight(new ExtentEx(200));
		containerExStretched.setMinimumStretchedHeight(new ExtentEx(100));
		
		containerExStretched.add(new DirectHtml("This ContainerEx should fill its parent" +
				"<br/>and if the browser window resizes it should" +
				"<br/>still take this in account.  It will not get smaller than 100px" +
				"<br/> nor will it larger than 200px in size"));
		

		ContainerEx containerExNormal = new ContainerEx();
		containerExNormal.setBorder(BorderEx.DEFAULT);
		containerExNormal.setHeightStretched(false);
		containerExNormal.setOutsets(new Insets(5));
		containerExNormal.setInsets(new Insets(10));
		containerExNormal.setScrollBarPolicy(Scrollable.AUTO);
		
		containerExNormal.add(new DirectHtml("The ContainerEx should NOT fill" +
				"<br/>but rather do the normal browser behaviour" +
				"<br/>of minimizing its height!"));
		
		Column cell = new Column();
		cell.add(new DirectHtml("The following labels take up some of the parent component" +
				"<br/>and then the ContainerEx that follows will fill the rest" +
				"<br/>of the available space."));
		cell.add(containerExStretched);
		cell.add(containerExNormal);
		
		TestController controller = new TestController(this,containerExStretched);
		controller.addBooleanTest(Stretchable.PROPERTY_HEIGHT_STRETCHED, "Height Maximized?");
		cell.add(controller.getUI());
		return cell;
	}

	public Component testStretch2() {
		
		ContainerEx containerExStretched = new ContainerEx();
		containerExStretched.setBorder(new BorderEx(Color.RED));
		containerExStretched.setOutsets(new Insets(5));
		containerExStretched.setInsets(new Insets(10));
		containerExStretched.setScrollBarPolicy(Scrollable.AUTO);
		containerExStretched.setHeightStretched(true);
		containerExStretched.setMaximumStretchedHeight(new ExtentEx(200));
		containerExStretched.setMinimumStretchedHeight(new ExtentEx(100));
		
		containerExStretched.add(new DirectHtml("This ContainerEx should fill its parent" +
				"<br/>and if the browser window resizes it should" +
				"<br/>still take this in account.  It will not get smaller than 100px" +
				"<br/> nor will it larger than 200px in size"));
		

		ContainerEx containerExNormal = new ContainerEx();
		containerExNormal.setBorder(BorderEx.DEFAULT);
		containerExNormal.setHeightStretched(true);
		containerExNormal.setOutsets(new Insets(5));
		containerExNormal.setInsets(new Insets(10));
		containerExNormal.setScrollBarPolicy(Scrollable.AUTO);
		
		containerExNormal.add(new DirectHtml("The ContainerEx will also try it so" +
				"<br/>what happens if we have 2 height maximising components" +
				"<br/>in a parent!  The first one in the DOM will win!" +
				"<br/>Of course if its restricted it may get a look in!"));
		
		Column cell = new Column();
		cell.add(new DirectHtml("The following labels take up some of the parent component" +
				"<br/>and then the ContainerEx that follows will fill the rest" +
				"<br/>of the available space."));
		cell.add(containerExStretched);
		cell.add(containerExNormal);
		
		return cell;
	}
	
	public Component testStretchLargeContent() {
		
		ContainerEx containerExStretched = new ContainerEx();
		containerExStretched.setBorder(new BorderEx(Color.RED));
		containerExStretched.setOutsets(new Insets(5));
		containerExStretched.setInsets(new Insets(10));
		containerExStretched.setScrollBarPolicy(Scrollable.AUTO);
		containerExStretched.setHeightStretched(true);
		//containerExStretched.setMaximumStretchedHeight(new ExtentEx(200));
		containerExStretched.setMinimumStretchedHeight(new ExtentEx(100));
		
		containerExStretched.add(new DirectHtml("This ContainerEx should fill its parent" +
				"<br/>and if the browser window resizes it should" +
				"<br/>still take this in account.  The content of this" +
				"<br/>container is quite large.  In fact what happens when its really large?" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/><br/><br/>" +
				"<br/>Will it work as expected" +
				"<br/>Will it work as expected" +
				"<br/>Will it work as expected" +
				"<br/>"));
		

		Column cell = new Column();
		cell.add(new DirectHtml("The following labels take up some of the parent component" +
				"<br/>and then the ContainerEx that follows will fill the rest" +
				"<br/>of the available space."));
		cell.add(containerExStretched);
		
		TestController controller = new TestController(this,containerExStretched);
		controller.addBooleanTest(Stretchable.PROPERTY_HEIGHT_STRETCHED, "Height Maximized?");
		cell.add(controller.getUI());
		return cell;
	}
	
	public Component testCentering() {
		ContainerEx container = new ContainerEx();
	    container.setWidth(new Extent(200));
	    container.setHeight(new Extent(200));
	    container.setPosition(ContainerEx.ABSOLUTE);
	    container.setScrollBarPolicy(Scrollable.CLIPHIDE);
	    container.setTop(new ExtentEx("50%"));
	    container.setLeft(new ExtentEx("50%"));
	    container.setOutsets(new Insets(-50, -50, 0, 0));
	    
	    container.setBackground(ColorKit.clr("#acbcdc"));
	    container.add(genRandomTextContent());
	    return container;
	}
}
