package echopointng.stylesheet.test;

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

import java.util.Locale;

import junit.framework.TestCase;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.SelectField;
import nextapp.echo2.app.Style;
import nextapp.echo2.app.StyleSheet;
import nextapp.echo2.app.TextField;
import echopointng.TemplatePanel;
import echopointng.Tree;
import echopointng.stylesheet.CssStyleSheetException;
import echopointng.stylesheet.CssStyleSheetLoader;
import echopointng.template.JspTemplateDataSource;
import echopointng.template.TemplateCachingHints;
import echopointng.template.TemplateDataSource;

/**
 * <code>CssStyleSheetTest</code>
 */
public class CssStyleSheetTest extends TestCase {

	private StyleSheet loadSS(String sheetName) {
		try {
			return CssStyleSheetLoader
					.load("echopointng/stylesheet/test/" + sheetName, Thread
							.currentThread().getContextClassLoader());
		} catch (CssStyleSheetException sse) {
			System.out.println(sse);
			sse.printStackTrace(System.out);
			return null;
		}
	}
	
	
	public void testStyleSheetCompound2() {
		StyleSheet styleSheet = loadSS("stylesheetCompound2.css");
		assertNotNull(styleSheet);

		Style style;
		
		style = styleSheet.getStyle(Tree.class, null);
		assertNotNull(style);
		
		assertEquals(style.getProperty("foreground"), Color.WHITE);
		assertEquals(style.getProperty("background"), new Color(255));
		
		style = styleSheet.getStyle(Label.class, null);
		assertNotNull(style);
		assertNotNull(style.getProperty("icon"));
		
		ImageReference imageRef = (ImageReference) style.getProperty("icon");
		assertTrue(imageRef instanceof HttpImageReference);
		HttpImageReference httpImageRef = (HttpImageReference) imageRef;
		assertEquals(httpImageRef.getUri(),"/image/testGif.png");
		assertEquals(httpImageRef.getWidth().toString(),"9px");
		assertEquals(httpImageRef.getHeight().toString(),"12pt");
		
		
	}
	
	public void testStyleSheetCompound1() {
		StyleSheet styleSheet = loadSS("stylesheetCompound1.css");
		assertNotNull(styleSheet);

		Style style;
		
		style = styleSheet.getStyle(TemplatePanel.class, null);
		assertNull(style);

		style = styleSheet.getStyle(TemplatePanel.class, "inst1");
		assertNotNull(style);

		assertEquals(style.getProperty("foreground"), Color.RED);
		assertTrue(style.getProperty("templateDataSource") instanceof JspTemplateDataSource);

		TemplateDataSource tds = (TemplateDataSource) style
				.getProperty("templateDataSource");
		assertTrue(tds.getCanonicalName().indexOf("/jsp/test1.jsp") != -1);

		TemplateCachingHints hints = tds.getCachingHints();
		assertNotNull(hints);
		assertTrue(hints.getAccessTimeout() == 10000);
		assertTrue(hints.getTimeToLive() == 20000);

	}

	public void testStyleSheet1() {
		StyleSheet styleSheet = loadSS("stylesheet1.css");
		assertNotNull(styleSheet);
	}

	public void testStyleSheet2() {
		StyleSheet styleSheet = loadSS("stylesheet2.css");
		assertNotNull(styleSheet);
		Style style = styleSheet.getStyle(Button.class, "modern");
		assertNotNull(style);
		assertEquals(style.getProperty("foreground"), Color.RED);
		assertEquals(style.getProperty("rolloverEnabled"), Boolean.FALSE);
		assertEquals(style.getProperty("locale"), Locale.US);

		style = styleSheet.getStyle(Component.class, "light");
		assertNotNull(style);
		assertEquals(style.getProperty("background"), Color.WHITE);
		assertEquals(style.getProperty("visible"), Boolean.FALSE);

		style = styleSheet.getStyle(Component.class, "dark");
		assertNotNull(style);
		assertEquals(style.getProperty("background"), Color.BLACK);
		assertEquals(style.getProperty("visible"), Boolean.FALSE);

		style = styleSheet.getStyle(Component.class, null);
		assertNotNull(style);
		assertEquals(style.getProperty("background"), Color.ORANGE);
		assertEquals(style.getProperty("visible"), Boolean.FALSE);
		
		style = styleSheet.getStyle(Component.class, "extraInfo");
		assertNotNull(style);
		assertEquals(style.getProperty("background"), Color.WHITE);
		assertEquals(style.getProperty("visible"), Boolean.FALSE);
		assertEquals(style.getProperty("foreground"), Color.BLACK);

		style = styleSheet.getStyle(SelectField.class, "noBaseClass");
		assertNotNull(style);
		assertEquals(style.getProperty("foreground"), Color.BLACK);
		
	}
	
	public void testStyleSheet3() {
		StyleSheet styleSheet = loadSS("stylesheet3.css");
		assertNotNull(styleSheet);
		Style style = styleSheet.getStyle(SelectField.class, "noBaseClass");
		assertNotNull(style);
		assertEquals(style.getProperty("foreground"), Color.BLACK);
		
		style = styleSheet.getStyle(TextField.class, "useInheritance");
		assertNotNull(style);
		assertEquals(style.getProperty("foreground"), Color.BLACK);		
		assertEquals(style.getProperty("background"), Color.RED);		
	}
	

}
