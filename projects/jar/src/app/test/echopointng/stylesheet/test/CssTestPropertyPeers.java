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
package echopointng.stylesheet.test;

import java.util.Locale;

import junit.framework.TestCase;
import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.Insets;
import echopointng.stylesheet.CssInvalidValueException;
import echopointng.stylesheet.CssPropertyPeer;
import echopointng.stylesheet.CssPropertyPeerLoader;
import echopointng.stylesheet.propertypeer.AlignmentPeer;
import echopointng.stylesheet.propertypeer.BooleanPeer;
import echopointng.stylesheet.propertypeer.BorderPeer;
import echopointng.stylesheet.propertypeer.BytePeer;
import echopointng.stylesheet.propertypeer.CharacterPeer;
import echopointng.stylesheet.propertypeer.ColorPeer;
import echopointng.stylesheet.propertypeer.DoublePeer;
import echopointng.stylesheet.propertypeer.ExtentPeer;
import echopointng.stylesheet.propertypeer.FloatPeer;
import echopointng.stylesheet.propertypeer.FontPeer;
import echopointng.stylesheet.propertypeer.InsetsPeer;
import echopointng.stylesheet.propertypeer.IntegerPeer;
import echopointng.stylesheet.propertypeer.LocalePeer;
import echopointng.stylesheet.propertypeer.LongPeer;
import echopointng.stylesheet.propertypeer.ShortPeer;
import echopointng.stylesheet.propertypeer.StringPeer;
import echopointng.stylesheet.propertypeer.TypefacePeer;

/**
 * <code>CssTestPropertyPeers</code>
 */

public class CssTestPropertyPeers extends TestCase {
	private Extent pt8 = new Extent(8, Extent.PT);

	private Extent px12 = new Extent(12, Extent.PX);

	public void testPropertyPeers() {
		testReversability(AlignmentPeer.class, new Alignment(Alignment.LEFT, Alignment.TOP), "alignment(LEFT,TOP)");

		testReversability(BooleanPeer.class, Boolean.valueOf(true), "true");

		testReversability(BorderPeer.class, new Border(pt8, Color.RED, Border.STYLE_INSET), "border(8pt,#FF0000,STYLE_INSET)");

		testReversability(BytePeer.class, new Byte(Byte.MAX_VALUE), "127");

		testReversability(CharacterPeer.class, new Character('z'), "z");

		testReversability(ColorPeer.class, Color.BLUE, "#0000FF");
		testReversability(DoublePeer.class, new Double(66.66), "66.66");

		testReversability(ExtentPeer.class, pt8, "8pt");
		testReversability(FloatPeer.class, new Float(0.1), "0.1");
		testReversability(FontPeer.class, new Font(Font.ARIAL, Font.BOLD, pt8), "font('Arial, Helvetica, Sans-Serif',BOLD,8pt)");

		testReversability(InsetsPeer.class, new Insets(1, 2, 3, 4), "insets(1px,2px,3px,4px)");

		testReversability(IntegerPeer.class, new Integer(234), "234");

		testReversability(LocalePeer.class, Locale.US, "en_US");
		testReversability(LongPeer.class, new Long(9988), "9988");

		testReversability(ShortPeer.class, new Short(Short.MAX_VALUE), "32767");

		testReversability(StringPeer.class, "identity", "identity");

		testReversability(TypefacePeer.class, Font.ARIAL, "typeface('Arial, Helvetica, Sans-Serif')");

		/*
		 * Can do because HttpImageReference has no .equals
		 * testReversability(ImageReferencePeer.class, new
		 * HttpImageReference("imageUrl.gif",pt8,px12),
		 * "image('imageUrl.gif',8pt,12px)");
		 */

		/*
		 * Can do because FillImage has no .equals
		 * 
		 * testReversability(FillImagePeer.class, new FillImage(new
		 * HttpImageReference("imageUrl.gif",pt8,px12) ,pt8,px12,
		 * FillImage.REPEAT_HORIZONTAL,FillImage.ATTACHMENT_FIXED), "fillimage(" +
		 * "image('imageUrl.gif',8pt,12px)" + ",8pt,12px," +
		 * "REPEAT_HORIZONTAL,ATTACHMENT_FIXED)");
		 */
	}

	/**
	 * This test objects that do not have .equals methods
	 */
public void testImagePropertyPeers() {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		CssPropertyPeer peer;
		
		peer = getPeer(FillImage.class);
		assertNotNull(peer);
		
		FillImage startVal = new FillImage(
				new HttpImageReference("imageUrl.gif",pt8,px12),pt8,px12, FillImage.REPEAT_HORIZONTAL);
		
		String startStr = "fillimage(" +
		"image('imageUrl.gif',8pt,12px)" +
		",8pt,12px," +
		"REPEAT_HORIZONTAL,ATTACHMENT_FIXED)";
		
		try {
			FillImage convertedValue = (FillImage) peer.getJavaObject(classLoader,FillImage.class,startStr,0);
			String convertedString = peer.getStyleString(classLoader,FillImage.class,startVal);

			assertEquals(startStr,convertedString);
			
			assertEquals(startVal.getHorizontalOffset(),convertedValue.getHorizontalOffset());
			assertEquals(startVal.getRepeat(),convertedValue.getRepeat());
			assertEquals(startVal.getVerticalOffset(),convertedValue.getVerticalOffset());
			
			
		} catch (CssInvalidValueException e) {
			assertTrue(false);
		}

		peer = getPeer(HttpImageReference.class);
		assertNotNull(peer);
		
		HttpImageReference startImage = new HttpImageReference("urlImage.gif",pt8,px12);
		String startImageStr = "image('urlImage.gif',8pt,12px)";
		
		try {
			HttpImageReference convertedValue = (HttpImageReference) peer.getJavaObject(classLoader,HttpImageReference.class,startImageStr,0);
			String convertedString = peer.getStyleString(classLoader,HttpImageReference.class,startImage);

			assertEquals(startImageStr,convertedString);
			
			assertEquals(startImage.getUri(),convertedValue.getUri());
			assertEquals(startImage.getHeight(),convertedValue.getHeight());
			assertEquals(startImage.getWidth(),convertedValue.getWidth());
			
			
		} catch (CssInvalidValueException e) {
			assertTrue(false);
		}
 	}
	/**
	 * Can an object and property stirng be converted in an interchangeable
	 * manner
	 */
	private void testReversability(Class propertyPeerClass, Object realValue, String propertyValue) {
		Class objectClass = realValue.getClass();
		ClassLoader classLoader = objectClass.getClassLoader();
		CssPropertyPeer peer = getPeer(objectClass);
		assertNotNull(peer);
		try {
			Object testValue = peer.getJavaObject(classLoader, objectClass, propertyValue, 0);
			assertEquals(testValue, realValue);

			String testString = peer.getStyleString(classLoader, objectClass, realValue);
			assertEquals(testString, propertyValue);

			//  and can it be done in reverse
			realValue = peer.getJavaObject(classLoader, objectClass, testString, 0);
			assertEquals(testValue, realValue);

			propertyValue = peer.getStyleString(classLoader, objectClass, testValue);
			assertEquals(testString, propertyValue);

		} catch (CssInvalidValueException e) {
			assertEquals("caused", e.getMessage());
		}
	}

	/**
	 */
	private CssPropertyPeer getPeer(Class objectClass) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		CssPropertyPeerLoader loader = CssPropertyPeerLoader.forClassLoader(classLoader);
		CssPropertyPeer peer = loader.getPropertyPeer(objectClass);
		return peer;
	}

}
