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

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import echopointng.BalloonHelp;
import echopointng.BorderEx;
import echopointng.ButtonEx;
import echopointng.EPNG;
import echopointng.ExtentEx;
import echopointng.LabelEx;
import echopointng.TabbedPane;
import echopointng.able.Stretchable;
import echopointng.tabbedpane.DefaultTabImageRenderer;
import echopointng.tabbedpane.DefaultTabModel;
import echopointng.test.controller.TestController;
import echopointng.util.ColorKit;

/** 
 * <code>TestTabbedPane</code> 
 */
public class TestTabbedPane extends TestCaseBaseNG{
	

	public String getTestCategory() {
		return "TabbedPane";
	}

	private Component genContent(String text) {
		BalloonHelp balloonHelp1 = new BalloonHelp();
		balloonHelp1.setPopUp(new LabelEx("Some ballon help here!"));
		balloonHelp1.setTarget(new LabelEx(text));
		
		Column cell = new Column();
		cell.add(genRandomTextContent());
		cell.add(balloonHelp1);
		
		return  cell;
	}

	private DefaultTabModel genTabModel(String tabPrefix, int tabCount) {
		ImageReference icon = new HttpImageReference("images/ArrowRoundRight.gif");
		
		DefaultTabModel tabModel = new DefaultTabModel();
		for (int i = 0; i < tabCount; i++) {
			tabModel.addTab(tabPrefix + " " + i , icon, genContent(tabPrefix + " " + i + " Content"));
		}
		return tabModel;
	}
	
	private TabbedPane genTabbedPane(String tabPrefix, int tabCount) {
		TabbedPane tabbedPane = new TabbedPane(genTabModel(tabPrefix,tabCount));
		return tabbedPane;
		
	}

	public Component testTabbedPane1() {
		ImageReference icon = new HttpImageReference("images/ArrowRoundRight.gif");
		
		TabbedPane tabbedPane1 = genTabbedPane("Tab 1",5);
		tabbedPane1.getSelectionModel().setSelectedIndex(2);
		tabbedPane1.setBackground(Color.YELLOW);
		tabbedPane1.setTabPlacement(Alignment.TOP);
		//tabbedPane.setTabPlacement(Alignment.BOTTOM);
		tabbedPane1.setInsets(new Insets(20));
		tabbedPane1.setOutsets(new Insets(20));
		tabbedPane1.setBorder(new Border(1,Color.BLACK,Border.STYLE_DOTTED));
		tabbedPane1.setBorder(new Border(1,Color.BLACK,Border.STYLE_SOLID));

		
		DefaultTabModel tabModel2 = new DefaultTabModel();
		tabModel2.addTab("Second 1 Tab 1", icon, new LabelEx("Second Tab 1 Content"));
		tabModel2.addTab("Second Tab 2", icon, new LabelEx("Second Tab 2 Content"));
		tabModel2.addTab("Second Tab 3", icon, new LabelEx("Second Tab 3 Content"));
		tabModel2.addTab("Second Tab 4", icon, new LabelEx("Second Tab 4 Content"));
		
		DefaultTabImageRenderer renderer = DefaultTabImageRenderer.getInstance(Alignment.TOP);
		tabModel2.setTabImageRenderer(renderer);
		
		TabbedPane tabbedPane2 = new TabbedPane(tabModel2);
		renderer.prime(tabbedPane2);
		
		tabbedPane2.setInsets(new Insets(20));
		tabbedPane2.setOutsets(new Insets(20));
		tabbedPane2.setBorder(new Border(1,Color.BLACK,Border.STYLE_SOLID));

		
		Grid grid = new Grid(1);
		grid.add(tabbedPane1);
		grid.add(tabbedPane2);
		
		return grid;
	}

	public Component testTabbedPaneSized() {
		
		TabbedPane tabbedPane = genTabbedPane("Tab",5);
		tabbedPane.setWidth(new ExtentEx(200));
		tabbedPane.setHeight(new ExtentEx(300));

		Grid grid = new Grid(1);
		grid.add(tabbedPane);
		
		return grid;
	}

	public Component testTabbedPaneStretched() {
		
		TabbedPane tabbedPane = genTabbedPane("Tab",5);
		tabbedPane.setHeightStretched(true);
		tabbedPane.setOutsets(new Insets(2,5,2,5));
		
		TestController controller = new TestController(this,tabbedPane);
		controller.addBooleanTest(Stretchable.PROPERTY_HEIGHT_STRETCHED, "Height Maximized?");
		

		Column cell = new Column();
		cell.add(tabbedPane);
		cell.add(controller.getUI());
		
		return cell;
	}
	
	
	public Component testTabbedPaneContainer() {
		SplitPane splitPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL, new ExtentEx("150px"));
		splitPane.setResizable(true);
		splitPane.add(genRandomTextContent());
		splitPane.add(genRandomTextContent());
		
		
		Component tabContent = splitPane;
		//Component tabContent = genRandomTextContent();
		//ContainerEx containerEx = new ContainerEx(splitPane);
		//containerEx.setHeight(new ExtentEx("400px"));
		//
		//Component tabContent = containerEx;
		
		DefaultTabModel tabModel = new DefaultTabModel();
		tabModel.addTab("Tab 1", tabContent);
		tabModel.addTab("Tab 2", genRandomTextContent());
		
		TabbedPane tabbedPane = new TabbedPane(tabModel);
		tabbedPane.setSelectedIndex(0);
		tabbedPane.setHeight(new ExtentEx("300px"));
		return tabbedPane;
	}
	
	public Component testTabbedPaneProperties() {
		final TabbedPane tabbedPane = genTabbedPane("Tab", 5);
	
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("5em"));
		
		ButtonEx b;
		
		/*----*/
		b = new ButtonEx("Set to TAB_STRIP_ONLY");
		cell.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setTabBorderStyle(TabbedPane.TAB_STRIP_ONLY);
			}
		});
		/*----*/
		b = new ButtonEx("Set to TAB_STRIP_AND_CONTENT");
		cell.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setTabBorderStyle(TabbedPane.TAB_STRIP_AND_CONTENT);
			}
		});
		/*----*/
		b = new ButtonEx("Set to LINE_AND_CONTENT");
		cell.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setTabBorderStyle(TabbedPane.TAB_LINE_AND_CONTENT);
			}
		});
		/*----*/
		b = new ButtonEx("Set to TAB_LINE_ONLY");
		cell.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setTabBorderStyle(TabbedPane.TAB_LINE_ONLY);
			}
		});
		/*----*/
		b = new ButtonEx("Set Placement to BOTTOM");
		cell.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setTabPlacement(Alignment.BOTTOM);
			}
		});
		/*----*/
		b = new ButtonEx("Set Placement to TOP");
		cell.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setTabPlacement(Alignment.TOP);
			}
		});

		/*----*/
		b = new ButtonEx("Set LeadInWidth to 50px");
		cell.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setTabLeadInWidth(new ExtentEx(50));
			}
		});

		/*----*/
		b = new ButtonEx("Set LeadInWidth to null");
		cell.add(b);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setTabLeadInWidth(null);
			}
		});

		cell.add(tabbedPane);

		return cell;
	}

	public Component testTabbedPaneTabBorderStyle() {
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("15em"));
		cell.setInsets(new Insets(10));
		cell.setBackground(ColorKit.makeColor("#E5DDFD"));

		TabbedPane tabbedPane;
		ExtentEx tabHeight = new ExtentEx(50);

		tabbedPane = genTabbedPane("TAB_STRIP_AND_CONTENT",2);
		tabbedPane.setHeight(tabHeight);
		tabbedPane.setTabBorderStyle(TabbedPane.TAB_STRIP_AND_CONTENT);
		tabbedPane.setTabLeadInWidth(new ExtentEx(10));
		cell.add(tabbedPane);

		tabbedPane = genTabbedPane("TAB_STRIP_ONLY",2);
		tabbedPane.setHeight(tabHeight);
		tabbedPane.setTabBorderStyle(TabbedPane.TAB_STRIP_ONLY);
		tabbedPane.setTabLeadInWidth(new ExtentEx(20));
		cell.add(tabbedPane);

		
		tabbedPane = genTabbedPane("TAB_LINE_AND_CONTENT",2);
		tabbedPane.setHeight(tabHeight);
		tabbedPane.setTabBorderStyle(TabbedPane.TAB_LINE_AND_CONTENT);
		tabbedPane.setTabLeadInWidth(new ExtentEx(20));
		cell.add(tabbedPane);
		
		tabbedPane = genTabbedPane("TAB_LINE_ONLY",2);
		tabbedPane.setHeight(tabHeight);
		tabbedPane.setTabBorderStyle(TabbedPane.TAB_LINE_ONLY);
		tabbedPane.setTabLeadInWidth(new ExtentEx(30));
		cell.add(tabbedPane);


		return cell;
	}

	public Component testTabbedPaneAppearance() {
		final Color silverishActive = ColorKit.clr("#919B9C");
		final Color blueishNonActive = ColorKit.clr("#91A7B4");
		final Color xporange = ColorKit.makeColor("#FFC73C");
		DefaultTabModel tabModel = new DefaultTabModel() {
			protected void paintTabComponent(TabbedPane tabbedPane, Component tabComponent, boolean isSelected, int tabPlacement) {
				super.paintTabComponent(tabbedPane,tabComponent,isSelected,tabPlacement);
				// now tweak it with our use if TAB_LINE_ONLY
				int borderStyle = Border.STYLE_SOLID;
				Color tabBackground = (Color) EPNG.getRP(tabComponent, Component.PROPERTY_BACKGROUND);
				BorderEx newBorder;
				BorderEx newRolloverBorder;
				if (isSelected) {
					newBorder = new BorderEx(
									/*L*/new ExtentEx(1),silverishActive,	borderStyle,
									/*R*/new ExtentEx(1),silverishActive,	borderStyle,
									/*T*/new ExtentEx(2),xporange,	borderStyle,
									/*B*/new ExtentEx(1),tabBackground,	borderStyle);
									
					newRolloverBorder = new BorderEx(
									/*L*/new ExtentEx(1),silverishActive,	borderStyle,
									/*R*/new ExtentEx(1),silverishActive,	borderStyle,
									/*T*/new ExtentEx(2),xporange,	borderStyle,
									/*B*/new ExtentEx(1),tabBackground,	borderStyle);
				} else {
					newBorder = new BorderEx(
									/*L*/new ExtentEx(1),blueishNonActive,	borderStyle,
									/*R*/new ExtentEx(1),blueishNonActive,	borderStyle,
									/*T*/new ExtentEx(1),blueishNonActive,	borderStyle,
									/*B*/new ExtentEx(1),tabBackground,	borderStyle);
									
					newRolloverBorder = new BorderEx(
									/*L*/new ExtentEx(1),blueishNonActive,	borderStyle,
									/*R*/new ExtentEx(1),blueishNonActive,	borderStyle,
									/*T*/new ExtentEx(2),xporange,	borderStyle,
									/*B*/new ExtentEx(1),tabBackground,	borderStyle);
									
				}
				tabComponent.setProperty(Button.PROPERTY_BORDER,newBorder);
				tabComponent.setProperty(Button.PROPERTY_ROLLOVER_BORDER,newRolloverBorder);
				tabComponent.setProperty(Button.PROPERTY_FONT,new Font(Font.VERDANA,Font.PLAIN,new ExtentEx("6pt")));
			};
		};
		for (int i = 0; i < 5; i++) {
			tabModel.addTab("Tab" + " " + i , null, genContent("Tab" + " " + i + " Content"));
		}
		final TabbedPane tabbedPane = new TabbedPane(tabModel);
		tabbedPane.setTabBorderStyle(TabbedPane.TAB_LINE_ONLY);
		tabbedPane.setTabLeadInWidth(new ExtentEx("3em"));
		tabbedPane.setBorder(new BorderEx(silverishActive));
	
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("5em"));
		
		cell.add(tabbedPane);
	
		return cell;
	}
}

