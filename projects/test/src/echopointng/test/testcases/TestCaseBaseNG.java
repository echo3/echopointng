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

import nextapp.echo2.app.Component;
import nextapp.echo2.app.ContentPane;
import echopointng.TemplatePanel;
import echopointng.template.StringTemplateDataSource;
import echopointng.template.TemplateDataSource;
import echopointng.test.TestCaseNG;
import echopointng.util.RandKit;

/**
 * <code>TestCaseBaseNG</code> is a JUnit like class that can provide a series
 * of tests as Components.
 * <p>
 * If there is public method that take no parameters and returns a Component,
 * then it can be "reflectively" run
 * <p>
 * Some support methods have been added to help with testing
 */

public abstract class TestCaseBaseNG extends TestCaseNG {

	private ContentPane containingPane;
	private ContentPane testingAreaPane;

	/**
	 * @return - the containing pane into which WindowPanes and the like can be
	 *         added
	 */
	public ContentPane getContainingPane() {
		return containingPane;
	}

	/**
	 * Sets the containing pane into which WindowPanes and the like can be added
	 * 
	 * @param containingPane -
	 *            the ContentPane
	 */
	public void setContainingPane(ContentPane containingPane) {
		this.containingPane = containingPane;
	}

	private String htmlContent = "<div>"
			+ "<p>The Echo Web Application Framework serves as the foundation of our development platform.</p>"
			+ "<p>Echo provides developers with an object-oriented, event-driven architecture for Web-based application development. </p>"
			+ "<p>It removes the developer from having to think in terms of \"page-centric\" applications and enables him/her to develop under a component and application-centric paradigm.</p>"
			+ "<p>Knowledge of HTML, HTTP, and JavaScript is not required.</p>"
			+ "<p>Echo is an open-source product, licensed under the Mozilla Public License (also available under the LGPL), which makes royalty-free development of both commercial and open-source products possible.</p>"
			+ "</div>";

	String[] texts = new String[] {
			"<p>"
					+ "Integer pede. Fusce lacus dui, dapibus vitae, placerat non, tempus ac, mi. Duis turpis leo, adipiscing vitae, dapibus nec, dictum a, risus. In tempus wisi eu felis. Vivamus vehicula interdum ligula. Quisque ut tellus a mauris fringilla commodo. Maecenas congue dui gravida turpis. Aliquam dictum nunc sit amet nunc. Ut eu nunc non ante tincidunt sodales. Sed malesuada tellus et eros. Morbi aliquet enim a pede. Morbi sed nulla. Suspendisse felis sem, viverra fringilla, blandit vitae, scelerisque at, ante. Donec semper sagittis urna. Ut laoreet mi et ante. Etiam nunc. Praesent faucibus elementum nulla."
					+ "</p>",

			"<p>"
					+ "Nulla tincidunt mauris quis tortor. Vivamus imperdiet eros eu est. Fusce sollicitudin, est vel aliquet pharetra, mauris erat pellentesque tortor, id elementum eros mauris ut massa. Fusce egestas. Cras nonummy arcu. Phasellus eu mi. Vivamus nec dolor. Duis lacinia. Suspendisse semper consectetuer sem. Suspendisse mollis velit vel nibh. Nam a leo. Duis vitae enim id purus tincidunt porta. Nunc eget elit ut ante ornare eleifend. Donec eget quam sit amet erat feugiat tincidunt. Mauris luctus eleifend lacus."
					+ "</p>",

			"<p>"
					+ "Aliquam sed pede id justo egestas tempor. Sed sagittis. Sed mollis. Pellentesque vel wisi. Curabitur turpis urna, cursus et, blandit nec, fringilla vitae, nibh. Quisque ornare porttitor diam. Vivamus imperdiet sollicitudin lorem. Nunc semper sapien condimentum urna. Maecenas vulputate dui. Suspendisse in justo. Phasellus a eros. Aliquam lectus. Vivamus viverra arcu ac turpis. Quisque commodo est id quam. Donec vestibulum. Integer euismod lorem ac lectus."
					+ "</p>",

			"<p>"
					+ "Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. Nam convallis. Donec aliquam blandit purus. Maecenas aliquam, nulla volutpat dictum viverra, elit lacus consectetuer ipsum, in luctus sem dui at lacus. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Fusce ligula. Mauris et orci. Aenean tempor tellus id tellus. Cras in sapien vel nibh blandit malesuada. In sagittis. Duis turpis tortor, auctor et, sodales nec, dapibus in, erat."
					+ "</p>",

			"<p>"
					+ "Nunc eget ligula quis lectus convallis vulputate. Aenean lobortis justo eu diam. Nam congue iaculis dolor. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum venenatis felis vitae risus. Praesent eu sapien eget ipsum vestibulum lacinia. Etiam dolor ligula, pharetra et, accumsan at, pellentesque a, erat. Aliquam erat volutpat. Vestibulum vulputate, lorem sed euismod pulvinar, pede pede rhoncus turpis, sit amet aliquam velit leo non metus. Praesent sed pede. Aenean sed sapien in mauris blandit pellentesque. Vivamus metus nulla, interdum vitae, faucibus id, laoreet nec, orci."
					+ "</p>", };

	/**
	 * @return some text in a component
	 */
	protected Component genTextContent() {
		return genTextImpl(htmlContent);
	}

	/**
	 * @return some random text in a component
	 */
	protected Component genRandomTextContent() {
		StringBuffer buffer = new StringBuffer("<bdo>");
		int max = RandKit.rand(1, 3);
		for (int i = 0; i < max; i++) {
			buffer.append(RandKit.roll(texts));
		}
		buffer.append("</bdo>");
		return genTextImpl(buffer.toString());
	}

	private Component genTextImpl(String text) {
		TemplateDataSource tds = new StringTemplateDataSource(text);
		return new TemplatePanel(tds);
	}

	/**
	 * @return the testingAreaPane
	 */
	public ContentPane getTestingAreaPane() {
		return testingAreaPane;
	}

	/**
	 * @param testingAreaPane the testingAreaPane to set
	 */
	public void setTestingAreaPane(ContentPane testingAreaPane) {
		this.testingAreaPane = testingAreaPane;
	}
}
