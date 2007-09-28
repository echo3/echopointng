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

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import echopointng.ButtonEx;
import echopointng.ContainerEx;
import echopointng.DirectHtml;

/**
 * <code>TestDirectHtml</code>
 */

public class TestDirectHtml extends TestCaseBaseNG {

	public String getTestCategory() {
		return "DirectHtml";
	}

	public Component testDirectHtml() {
		Column col = new Column();

		col.add(new DirectHtml("<pre>Some html inside a pre tag\n with whitespace newlines in it</pre>"));

		col.add(new DirectHtml("<div>Some html inside a div tag</div>"));

		col.add(new DirectHtml("<div>Some html inside a div tag with <span style=\"color:red\">a red span in it</span></div>"));

		return col;

	}

	public Component testDefault() {
		return new DirectHtml();
	}

	public Component testRemove() {
		final ContainerEx container = new ContainerEx();
		final DirectHtml directHtml = new DirectHtml("<div>Some html inside a div tag with <span style=\"color:red\">a red span in it</span></div>");
		container.add(directHtml);

		ButtonEx bRemove = new ButtonEx("Remove");
		bRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				container.remove(directHtml);
			}
		});

		ButtonEx bAdd = new ButtonEx("Add");
		bAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				container.add(directHtml);
			}
		});

		Column col = new Column();
		col.add(container);
		col.add(bRemove);
		col.add(bAdd);
		return col;
	}

	public Component testUpdates() {
		Row cell = new Row();

		ContainerEx containerEx1 = new ContainerEx();
		containerEx1.setWidth(new Extent(350, Extent.PX));
		containerEx1.setHeight(new Extent(200, Extent.PX));
		containerEx1.setBorder(new Border(new Extent(1, Extent.PX), new Color(0x808080), Border.STYLE_SOLID));

		cell.add(containerEx1);

		final DirectHtml directHtml = new DirectHtml();
		containerEx1.add(directHtml);

		Row row1 = new Row();
		row1.setCellSpacing(new Extent(8, Extent.PX));
		cell.add(row1);
		final Button button1 = new Button();
		button1.setText("Left button");
		button1.setBackground(new Color(0xc0c0c0));
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				directHtml.setText("Thanks for pushing the left button!");
			}
		});
		row1.add(button1);

		final Button button2 = new Button();
		button2.setText("Right button");
		button2.setBackground(new Color(0xc0c0c0));
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				directHtml.setText("Thanks for pushing the right button!");
			}
		});
		row1.add(button2);

		return cell;
	}

	public Component testNullHandling() {
		Column cell = new Column();

		ContainerEx containerEx1 = new ContainerEx();
		containerEx1.setWidth(new Extent(350, Extent.PX));
		containerEx1.setHeight(new Extent(200, Extent.PX));
		containerEx1.setBorder(new Border(new Extent(1, Extent.PX), new Color(0x808080), Border.STYLE_SOLID));
		cell.add(containerEx1);

		final DirectHtml directHtml = new DirectHtml();
		containerEx1.add(directHtml);

		Row row1 = new Row();
		row1.setCellSpacing(new Extent(8, Extent.PX));
		cell.add(row1);
		Button button1 = new Button();
		button1.setText("Left button");
		button1.setBackground(new Color(0xc0c0c0));
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				directHtml.setText("Thanks for pressing the Left Button!");
			}
		});
		row1.add(button1);
		Button button2 = new Button();
		button2.setText("Right button");
		button2.setBackground(new Color(0xc0c0c0));
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				directHtml.setText("Thanks for pressing the Right Button!");
			}
		});
		row1.add(button2);
		Button button3 = new Button();
		button3.setText("Empty String button");
		button3.setBackground(new Color(0xff8000));
		button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				directHtml.setText("");
			}
		});
		row1.add(button3);
		Button button4 = new Button();
		button4.setText("Crash me!");
		button4.setBackground(Color.RED);
		button4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				directHtml.setText(null);
			}
		});
		row1.add(button4);

		return cell;
	}
	
	public Component testEntities() {
		Column cell = new Column();
		cell.add(new DirectHtml("Is this a greater than &gt; symbol or this \u003E"));
		cell.add(new DirectHtml("What about this - <b>&gt; symbol</b> or this <b>\u003E</b>"));
		cell.add(new DirectHtml("or even this - <b>></b> - (ps this is just accidentally works and is in fact invalid)"));
		
		
		return cell;
	}
}
