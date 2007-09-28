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

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.SelectField;
import nextapp.echo2.app.WindowPane;
import echopointng.BalloonHelp;
import echopointng.BorderEx;
import echopointng.ContainerEx;
import echopointng.ContentPaneEx;
import echopointng.ExpandableSection;
import echopointng.ExtentEx;
import echopointng.TemplatePanel;
import echopointng.TitleBar;
import echopointng.able.Positionable;
import echopointng.able.Scrollable;
import echopointng.model.ExpansionGroup;
import echopointng.template.StringTemplateDataSource;
import echopointng.template.TemplateDataSource;
import echopointng.util.ColorKit;

/**
 * <code>TestExpandableSection</code>
 */

public class TestExpandableSection extends TestCaseBaseNG {

	/**
	 * @see echopointng.test.testcases.TestCaseBaseNG#getTestCategory()
	 */
	public String getTestCategory() {
		return "ExpandableSection";
	}

	public Component testAccordianMode() {
		Column col = new Column();
		Row row = new Row();

		ExpansionGroup group = new ExpansionGroup();
		group.setAccordionMode(true);
		for (int i = 1; i < 4; i++) {
			ExpandableSection esAcc = new ExpandableSection("Accordion " + i);
			esAcc.getTitleBar().setExpandedTitle("Expanded Title " + i);
			esAcc.setExpansionGroup(group);
			esAcc.add(generateContent());
			col.add(esAcc);
		}
		row.add(col);
		return row;
	}

	public Component testExpandableSection() {
		Column col;
		Row row = new Row();

		ExpandableSection es1 = new ExpandableSection("Expand Me");
		es1.setBorder(new BorderEx(Color.RED));
		es1.getTitleBar().setBackground(Color.RED);
		es1.add(generateContent());
		row.add(es1);

		col = new Column();
		row.add(col);

		ExpansionGroup group = new ExpansionGroup();
		group.setAccordionMode(true);
		for (int i = 1; i < 5; i++) {
			ExpandableSection esAcc = new ExpandableSection("Accordion " + i);
			esAcc.setBorder(new BorderEx(Color.BLUE));
			esAcc.getTitleBar().setBackground(Color.BLUE);
			esAcc.getTitleBar().setExpandedTitle("Expanded Title " + i);
			esAcc.setExpansionGroup(group);
			esAcc.add(generateContent());
			col.add(esAcc);
		}

		col = new Column();
		row.add(col);

		group = new ExpansionGroup();
		group.setAccordionMode(false);
		for (int i = 1; i < 5; i++) {
			ExpandableSection esAcc = new ExpandableSection("Non Accordion " + i);
			esAcc.setBorder(new BorderEx(Color.GREEN));
			esAcc.getTitleBar().setBackground(Color.GREEN);
			esAcc.getTitleBar().setExpandedTitle("Expanded Title " + i);
			esAcc.setExpansionGroup(group);
			esAcc.add(generateContent());
			col.add(esAcc);
		}

		return row;
	}

	public Component testDefaultExpandableSection() {
		ExpandableSection es = new ExpandableSection("ExpandableSection");
		es.add(genTextContent());
		return es;
	}

	public Component testTitleBar() {
		Column col = new Column();
		col.setCellSpacing(new Extent(5));

		TitleBar tb;

		tb = new TitleBar("Default");
		col.add(tb);

		tb = new TitleBar();
		tb.setBorder(new BorderEx(Color.DARKGRAY));
		tb.setHelpComponent(new BalloonHelp());
		col.add(tb);

		tb = new TitleBar("Red and Yellow");
		tb.setBackground(Color.RED);
		tb.setRolloverBackground(Color.YELLOW);
		col.add(tb);

		return col;
	}

	private Component generateContent() {
		String htmlContent = "<div>"
				+ "<p>The Echo Web Application Framework serves as the foundation of our development platform.</p>"
				+ "<p>Echo provides developers with an object-oriented, event-driven architecture for Web-based application development. </p>"
				+ "<p>It removes the developer from having to think in terms of \"page-centric\" applications and enables him/her to develop under a component and application-centric paradigm.</p>"
				+ "<p>Knowledge of HTML, HTTP, and JavaScript is not required.</p>"
				+ "<p>Echo is an open-source product, licensed under the Mozilla Public License (also available under the LGPL), which makes royalty-free development of both commercial and open-source products possible.</p>"
				+ "</div>";

		TemplateDataSource tds = new StringTemplateDataSource(htmlContent);
		return new TemplatePanel(tds);
	}

	public Component testIEGlitch() {
		Column cell = new Column();
		cell.setCellSpacing(new ExtentEx("10em"));

		for (int i = 0; i < 2; i++) {
			final Extent _width = new Extent(200, Extent.PX);
			final Extent _height = new Extent(200, Extent.PX);

			final ContainerEx content;

			final TitleBar titleBar = new TitleBar("test");
			titleBar.setBackground(ColorKit.clr("#acbcdc"));

			final ExpandableSection expSection;

			titleBar.setWidth(_width);
			titleBar.setStyleName("Default");
			titleBar.setHeight(new Extent(20, Extent.PX));

			content = new ContainerEx();
			content.setWidth(_width);
			content.setHeight(_height);
			content.setPosition(Positionable.RELATIVE);
			content.setStyleName("Default");
			content.setScrollBarPolicy(Scrollable.NEVER);

			expSection = new ExpandableSection();
			expSection.setTitleBar(titleBar);
			expSection.setWidth(content.getWidth());
			expSection.add(content);
			expSection.setExpanded(true);

			for (int iTeller = 0; iTeller < 5; iTeller++) {
				Label _label = new Label("test item");
				ContainerEx ex = new ContainerEx();
				ex.add(_label);
				ex.setStyleName("Default");
				content.add(ex);
			}
			ContainerEx outer = new ContainerEx();
			if (i == 0) {
				titleBar.setTitle("Inside a Positionable.STATIC ContainerEx");
				outer.setPosition(Positionable.STATIC);
			} else {
				titleBar.setTitle("Inside a Positionable.RELATIVE ContainerEx");
				outer.setPosition(Positionable.RELATIVE);
			}
			outer.add(expSection);
			cell.add(outer);
		}
		return cell;
	}

	public Component testUserIssue() {
		Column cc = new Column();
		for (int z = 0; z < 15; z++) {
			Row r1 = new Row();
			ExpandableSection ex = new ExpandableSection("Expandable1");
			r1.add(ex);
			cc.add(r1);

			Column c3 = new Column();
			ex.add(c3);

			ExpandableSection ex1 = new ExpandableSection("Expandable11");

			String[] values = { "One", "Two" };
			
			SelectField selectField = new SelectField(values);
			Row rr = new Row();
			rr.add(selectField);
			ex1.add(rr);

			Row r2 = new Row();
			r2.add(ex1);
			c3.add(r2);

			ExpandableSection ex2 = new ExpandableSection("Expandable12");
			Column c = new Column();
			for (int i = 0; i < 10; i++) {
				Label labe2 = new Label("Label" + i);
				c.add(labe2);
			}
			ex2.add(c);
			Row r3 = new Row();
			r3.add(ex2);
			c3.add(r3);
		}
		return cc;
	}
	
	public Component testUserIssueWindowPanes() {
        // create a set of expandable sections  inside a column
        Column column = new Column();
        column.setCellSpacing( new ExtentEx( "2px" ) );

        for ( int i = 1; i <=  10; i++ ){
            ExpandableSection section = new ExpandableSection( "Section" + i );

            Column container = new Column();
            column.setCellSpacing( new ExtentEx( "2px" ) );
            for ( int j = 1; j <=5; j++ ){
                container.add( new Label( "Label" + i + "-" + j ) );
            }
            section.add( container );
            column.add( section );
        }

        // Put the column in a ContaineEx to enable scrolling
        ContainerEx containerEx = new ContainerEx( column );
        containerEx.setWidth( new ExtentEx( "300px" ) );
        containerEx.setHeight( new ExtentEx( "400px" ) );

        // create window pane and add as content
        WindowPane windowPane=  new WindowPane();
        windowPane.setDefaultCloseOperation( WindowPane.DISPOSE_ON_CLOSE );
        windowPane.setTitle( "Exapandable section problem" );
        windowPane.add( containerEx );

        windowPane.setWidth( new ExtentEx( "300px" ) );
        windowPane.setHeight( new ExtentEx( "400px" ) );

        ContentPaneEx mainContentPane = new ContentPaneEx();
        mainContentPane.add( windowPane );		
        return mainContentPane;
	}
}
