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
import nextapp.echo2.app.Row;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.text.StringDocument;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.ContainerEx;
import echopointng.ContentPaneEx;
import echopointng.ExtentEx;
import echopointng.HttpPaneEx;
import echopointng.LabelEx;
import echopointng.TemplatePanel;
import echopointng.TextFieldEx;
import echopointng.able.MouseCursorable;
import echopointng.able.Positionable;
import echopointng.able.Scrollable;
import echopointng.layout.DisplayLayoutData;
import echopointng.layout.ScrollableDisplayLayoutData;
import echopointng.template.StringTemplateDataSource;
import echopointng.template.TemplateDataSource;
import echopointng.util.ColorKit;

/**
 * <code>TestContentEx</code>
 */
public class TestContentPaneEx extends TestCaseBaseNG {

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

	public String getTestCategory() {
		return "ContentPaneEx";
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

	public Component testBackgroundImage() {
		FillImage fillImage = new FillImage(new HttpImageReference("/images/tile_back.gif"), new Extent(0), new Extent(0),
				FillImage.REPEAT_HORIZONTAL);
		ContentPaneEx ex = new ContentPaneEx();
		ex.add(genRandomTextContent());
		ex.setBackgroundImage(fillImage);
		return ex;
	}

	public Component testDisplayLayoutData() {
		ContentPaneEx contentPaneEx = new ContentPaneEx();

		// add some positioned objects
		contentPaneEx.add(newLabelAt(220, 220));
		contentPaneEx.add(newLabelAt(240, 240));
		contentPaneEx.add(newLabelAt(260, 260));
		contentPaneEx.add(newLabelAt(400, 400));

		contentPaneEx.add(newLabelSized(100, 50));
		contentPaneEx.add(newLabelSized(400, 50));

		for (int i = 0; i < 100; i++) {
			contentPaneEx.add(genRandomTextContent());
		}

		return contentPaneEx;
	}

	public Component testRemoval() {
		final ContentPaneEx innerContentPaneEx = new ContentPaneEx();
		final ContentPaneEx outerContentPaneEx = new ContentPaneEx();

		innerContentPaneEx.add(new Label(" Remove me!"));
		innerContentPaneEx.add(new Label(" Remove me!"));
		innerContentPaneEx.add(new Label(" Remove me!"));
		innerContentPaneEx.add(new Label(" Remove me!"));
		innerContentPaneEx.add(new Label(" Remove me!"));

		ButtonEx b = new ButtonEx("Remove Them!");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				innerContentPaneEx.removeAll();

				innerContentPaneEx.add(new Label(" Removed at " + new Date()));
				innerContentPaneEx.add(new Label(" Removed at " + new Date()));
				innerContentPaneEx.add(new Label(" Removed at " + new Date()));
				innerContentPaneEx.add(new Label(" Removed at " + new Date()));
				innerContentPaneEx.add(new Label(" Removed at " + new Date()));
				innerContentPaneEx.add(new Label(" Removed at " + new Date()));

			}
		});
		outerContentPaneEx.add(b);
		outerContentPaneEx.add(innerContentPaneEx);
		return outerContentPaneEx;
	}

	public Component testRemoval2() {
		ContentPaneEx rootContentPaneEx = new ContentPaneEx();
		final ContentPaneEx buttonContentPaneEx = new ContentPaneEx();
		final ContentPaneEx bodyContentPaneEx = new ContentPaneEx();
		for (int i = 0; i < 5; i++) {
			Button button = new Button("Button " + i);
			final int index = i;
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					bodyContentPaneEx.removeAll();
					bodyContentPaneEx.add(new Label("loaded content from button " + index));
				}
			});
			buttonContentPaneEx.add(button);
		}
		rootContentPaneEx.add(buttonContentPaneEx);
		rootContentPaneEx.add(bodyContentPaneEx);
		return rootContentPaneEx;
	}

	public Component testScroll() {
		Column col = new Column();
		col.setCellSpacing(new Extent(10));

		final Label currentValues = new Label();

		final ContentPaneEx contentPaneEx = new ContentPaneEx();
		for (int i = 0; i < 100; i++) {
			contentPaneEx.add(genRandomTextContent());
		}

		final Button buttonDown = new ButtonEx("Scroll ContentPaneEx Down");
		buttonDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Extent now = contentPaneEx.getVerticalScroll();
				currentValues.setText("VerticalScroll:" + now);
				now = (now == null ? new Extent(0) : now);
				contentPaneEx.setVerticalScroll(new Extent(now.getValue() + 50));

			};
		});

		final Button buttonDownMax = new ButtonEx("Scroll ContentPaneEx DownMax");
		buttonDownMax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Extent now = contentPaneEx.getVerticalScroll();
				currentValues.setText("VerticalScroll:" + now);
				contentPaneEx.setVerticalScroll(new Extent(-1));

			};
		});

		final Button buttonAcross = new ButtonEx("Scroll ContentPaneEx Across");
		buttonAcross.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Extent now = contentPaneEx.getHorizontalScroll();
				currentValues.setText("HorizontalScroll:" + now);
				now = (now == null ? new Extent(0) : now);
				contentPaneEx.setHorizontalScroll(new Extent(now.getValue() + 50));
			};
		});

		final Button buttonAcrossMax = new ButtonEx("Scroll ContentPaneEx Across Max");
		buttonAcrossMax.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Extent now = contentPaneEx.getHorizontalScroll();
				currentValues.setText("HorizontalScroll:" + now);
				contentPaneEx.setHorizontalScroll(new Extent(-1));
			};
		});

		ContainerEx sizedContainer = new ContainerEx(contentPaneEx);
		sizedContainer.setHeight(new ExtentEx("300px"));
		sizedContainer.setWidth(new ExtentEx("300px"));

		col.add(buttonDown);
		col.add(buttonAcross);
		col.add(sizedContainer);
		col.add(currentValues);
		col.add(buttonDownMax);
		col.add(buttonAcrossMax);
		return col;
	}

	public Component testSplitPane() {
		String templateStr = "<p><i>" + "This example shows the use of SplitPanes inside the ContentPaneEx.  "
				+ "This is posssible because ContentPaneEx implements PaneContainer." + "</i></p>";
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

		ContentPaneEx contentPaneEx = new ContentPaneEx();
		contentPaneEx.setBackground(Color.PINK);

		contentPaneEx.add(splitPane);
		return contentPaneEx;
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

		ContentPaneEx contentPaneEx = new ContentPaneEx();
		contentPaneEx.add(contentPane);
		return contentPaneEx;
	}

	public Component testWithScrollableDisplayLayoutData() {
		String templateStr = "<p>" + "<i>" + "This is an example of a ScrollableDisplayLayoutData "
				+ "that allows a component that doesnt specifically support " + "scrolling to have a scrolling region around it." + "</i>" + "</p>";
		templateStr += genBackgroundText(1);
		templateStr = "<bdo>" + templateStr + "</bdo>";
		TemplateDataSource dataSource = new StringTemplateDataSource(templateStr);

		ContentPaneEx contentPaneEx = new ContentPaneEx();

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
		contentPaneEx.add(template);

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
		contentPaneEx.add(template);

		return contentPaneEx;
	}
	
	public Component testVisualAppearance() {
		final ContentPaneEx contentPaneEx = new ContentPaneEx();
		for (int i = 0; i < 100; i++) {
			contentPaneEx.add(genRandomTextContent());
		}
		return contentPaneEx; 
	}

	public Component testHttpPaneEx() {

		final HttpPaneEx httpPaneEx = new HttpPaneEx("http://www.nextapp.com");
		
		
		
		SplitPane parentSplitPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM,new ExtentEx(70));
		parentSplitPane.setResizable(true);
		parentSplitPane.setSeparatorColor(ColorKit.clr("#abbcdc"));

		Column httpController = new Column();
		
		
		Row httpControllerTop = new Row();
		httpController.setInsets(new Insets(5));
		httpControllerTop.setCellSpacing(new ExtentEx("3em"));
		httpController.add(httpControllerTop);

		Row httpControllerBottom = new Row();
		httpControllerBottom.setCellSpacing(new ExtentEx("3em"));
		httpController.add(httpControllerBottom);
		
		
		final TextFieldEx customURL = new TextFieldEx(new StringDocument(),"",50);
		final ButtonEx customURLButton = new ButtonEx("Go");
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String URI = null;
				if (e.getSource() == customURLButton) {
					URI = customURL.getText();
					if (URI.indexOf("http://") == -1) {
						URI = "http://" + URI;
					}
				} else {
					URI = e.getActionCommand();
				}
				httpPaneEx.setURI(URI);
			};
		};
		customURLButton.addActionListener(listener);
		
		httpControllerBottom.add(new LabelEx("Enter your own URL : "));
		httpControllerBottom.add(customURL);
		httpControllerBottom.add(customURLButton);
		
		String URIS[] = new String[] { 
				"http://www.nextapp.com",
				"http://www.google.com",
				"http://wiki.nextapp.com",
				"http://www.theserverside.com",
		};
		for (int i = 0; i < URIS.length; i++) {
			ButtonEx buttonEx = new ButtonEx(URIS[i].substring(7));
			buttonEx.setActionCommand(URIS[i]);
			buttonEx.addActionListener(listener);
			httpControllerTop.add(buttonEx);
		}
		parentSplitPane.add(httpController);
		parentSplitPane.add(httpPaneEx);
		
		return parentSplitPane; 
	}
	
}
