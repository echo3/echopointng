package echopointng.image;
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GraphicAttribute;
import java.awt.font.ImageGraphicAttribute;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Map;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.Insets;
import echopointng.util.ColorKit;
import echopointng.util.FontKit;

/**
 * The <code>TextImageReference</code> class is an ImageReference
 * implementation that can draw text onto a background image.  This makes the 
 * class perfect for "dynamic" button icons since a "button-like" background 
 * image can be used and then button text can be dynamically drawn on it.
 * <p>
 * If the text to be rendered wont fit within the background image (plus its Insets)
 * then the background image can be "scaled" to fit the text.  The scaling options
 * are :
 * <ul>
 * <li>SCALE_NONE - no scaling is performed</li>
 * <li>SCALE_FAST - a fast scaling algorithm is used. Not always the best look but its fast</li>
 * <li>SCALE_SMOOTH - a better looking scaling algorithm is used at the cost of speed</li>
 * <li>SCALE_SPLICE_V_THEN_H - this will splice the image at its x,y, first in the vertical direction
 *                             and then in the horizontal direction.</li>
 * <li>SCALE_SPLICE_H_THEN_V - this will splice the image at its x,y, first in the horizontal direction
 *                             and then in the vertical direction.</li>
 * </ul>
 * <p>
 * The last two scaling options are very useful for "button-like" background images.  This is because 
 * they will not scale the "outer" edges of the image but rather "splice and fill".  This means that
 * "button-like shadows and outlines" will not be lost or scaled unecesarily. 
 * <p>
 * TextImageReference will also "break" strings on carriage return boundaries, to allow
 * for multi line text.  This may result in the need to "auto-resize" the background image.
 * <p> 
 * The text is drawn at a specific {x,y} position.  If you use a contructor without
 * the {x,y} parameters, then the text is automatically centred within the
 * height and width of the backgroundImage.  You can use the horizontal and
 * vertical alignment properties to control where the text is placed in relation
 * to the {x,y} position.  The {x,y} position will be the base line of the text.
 * <p>
 * <code>AttributedString</code>'s are used to render the image text. These 
 * contain formatting instructions for specific characters.  While the
 * formatting information uses java.awt.* visual classes, a series of static
 * helper methods have been provided to help create java.text.AttributedStrings
 * using Echo color/font objects.  You can also use the <code>ColorKit</code>
 * and <code>FontKit</code> classes.
 * <p>
 * <code>AttributedStrings</code> have a great feature whereby the Unicode 
 * replacement character u'FFFC' can be substituted with another visual
 * element.  This is supported via the replacementImages and the 
 * replacementImageAlignment properties.  The images in the replacementImages
 * will be substituted, in a round robin fashion, for each Unicode replacement
 * character in the AttributedString. 
 * <p>
 * This class by default does not keep the encoded output image bytes in memory.  
 * You can change this by <code>setKeptInMemory(true).</code>  
 * <p>
 * You should think about loading the background images via 
 * <code>ImageKit.loadCachedImage(...)</code> so that these static
 * images are cached even if the dynamic <code>TextImageReference</code>
 * is not.  
 * 
 * @see java.text.AttributedString
 * @see java.awt.font.TextAttribute
 * @see java.awt.Image
 * @see java.awt.Image#SCALE_FAST
 * @see java.awt.Image#SCALE_SMOOTH
 * 
 * @author Brad Baker
 */
public class TextImageReference extends EncodedImageReference implements Serializable {
	
	/** Dont scale the background image to the text size */ 
	public final static int SCALE_NONE = 0;
	 
	/** Choose a scaling algorithm to optimize speed more than smoothness of the scaled image  */
	public final static int SCALE_FAST = 4;
	
	/** Choose a scaling algorithm to optimize smoothness of the scaled image more than speed */ 
	public final static int SCALE_SMOOTH = 8;

	/** Choose a scaling algorithm that splices vertically then horizontally */ 
	public final static int SCALE_SPLICE_V_THEN_H = 16;

	/** Choose a scaling algorithm that splices horizontally then vertically */ 
	public final static int SCALE_SPLICE_H_THEN_V = 32;

	/** The default scaling option is SCALE_SPLICE_V_THEN_H */ 
	public final static int SCALE_DEFAULT = SCALE_SPLICE_V_THEN_H;
	
	/** The default insets between the text and the outer edge of the background image */
	public final static Insets DEFAULT_INSETS = new Insets(3);
	
	private transient AttributedString attributedString;
	private transient Image backgroundImage;
	private transient Image[] replacementImages;
	
	private boolean bestQuality = true;
	private boolean autoCenterAtConstruction = false;
	private int horizontalAlignment = Alignment.CENTER;
	private int replacementImageAlignment = Alignment.CENTER;
	private int textAngle = 0;
	private int verticalAlignment = Alignment.CENTER;
	private int x = Integer.MAX_VALUE;
	private int y = Integer.MAX_VALUE;
	private int origImageW = -1;
	private int origImageH = -1;
	private int scaleOption = SCALE_DEFAULT;
	private Insets insets = DEFAULT_INSETS;
	private TextImageDrawer drawer = new TextImageDrawer();

	/**
	 * Constructs a TextImageReference that will be drawn centered within the 
	 * background image.
	 * 
	 * @param string - the attributed string to draw.  Can be null.
	 * @param backgroundImage - the background image to draw on.  Must not be null.
	 */
	public TextImageReference(AttributedString string, Image backgroundImage) {
		this(string,backgroundImage,SCALE_DEFAULT);
	}

	/**
	 * Constructs a TextImageReference that will be drawn centered within the 
	 * background image and scalled according to the scaling option.
	 * 
	 * @param string - the attributed string to draw.  Can be null.
	 * @param backgroundImage - the background image to draw on.  Must not be null.
	 * @param scaleOption - the scaling option to use
	 */
	public TextImageReference(AttributedString string, Image backgroundImage, int scaleOption) {
		this(string, backgroundImage, scaleOption, new GifEncoder());
	}

	/**
	 * Constructs a TextImageReference that will be drawn centered within the 
	 * background image and scalled according to the scaling option.
	 * 
	 * @param string - the attributed string to draw.  Can be null.
	 * @param backgroundImage - the background image to draw on.  Must not be null.
	 * @param scaleOption - the scaling option to use
	 * @param encoder - the Imageencoder to use for this image
	 */
	public TextImageReference(AttributedString string, Image backgroundImage, int scaleOption, ImageEncoder encoder) {
		constructTextImageRefererence(Integer.MAX_VALUE, Integer.MAX_VALUE, string, backgroundImage, scaleOption, encoder);
	}
	
	/**
	 * Constructs a TextImageReference that will be drawn centered within the 
	 * background image, which is loaded from a file.
	 * 
	 * @param string - the attributed string to draw.  Can be null.
	 * @param imageFileName - the background image file to draw on.  Must not be null.
	 * @param encoder = the ImageEncoder to use
	 */
	public TextImageReference(AttributedString string, String imageFileName,ImageEncoder encoder) {
		this(string,ImageKit.loadImage(imageFileName),SCALE_DEFAULT,encoder);
	}

	/**
	 * Constructs a TextImageReference that will be drawn centered within the 
	 * background image, which is loaded from a file.
	 * 
	 * @param string - the attributed string to draw.  Can be null.
	 * @param imageFileName - the background image file to draw on.  Must not be null.
	 */
	public TextImageReference(AttributedString string, String imageFileName) {
		this(string,ImageKit.loadImage(imageFileName));
	}
	
	/**
	 * Constructs a TextImageReference that will be centered within the 
	 * background image, which is loaded from an URL.
	 * 
	 * @param string - the attributed string to draw.  Can be null.
	 * @param imageURL - the background image URL to draw on.  Must not be null.
	 */
	public TextImageReference(AttributedString string, URL imageURL) {
		this(string,ImageKit.loadImage(imageURL));
	}

	/**
	 * Constructs a TextImageReference that will be centered within the 
	 * background image, which is loaded from an URL.
	 * 
	 * @param string - the attributed string to draw.  Can be null.
	 * @param imageURL - the background image URL to draw on.  Must not be null.
	 * @param encoder = the ImageEncoder to use
	 */
	public TextImageReference(AttributedString string, URL imageURL, ImageEncoder encoder) {
		this(string,ImageKit.loadImage(imageURL), SCALE_DEFAULT,encoder);
	}
	
	/**
	 * Constructs a TextImageReference that will be drawn at the x,y points
	 * within the background image.
	 * 
	 * @param x - where to draw the text in the x direction
	 * @param y - where to draw the text in the y direction
	 * @param string - the attributed string to draw.  Can be null.
	 * @param backgroundImage - the background image to draw on.  Must not be null.
	 */
	public TextImageReference(int x, int y, AttributedString string, Image backgroundImage) {
		this(x, y, string, backgroundImage, new GifEncoder());
	}

	/**
	 * Constructs a TextImageReference that will be drawn at the x,y points
	 * within the background image.
	 * 
	 * @param x - where to draw the text in the x direction
	 * @param y - where to draw the text in the y direction
	 * @param string - the attributed string to draw.  Can be null.
	 * @param backgroundImage - the background image to draw on.  Must not be null.
	 * @param encoder = the ImageEncoder to use
	 */
	public TextImageReference(int x, int y, AttributedString string, Image backgroundImage, ImageEncoder encoder) {
		constructTextImageRefererence(x, y, string, backgroundImage, SCALE_DEFAULT, encoder);
	}
	
	/**
	 * Constructs a TextImageReference that will be drawn at the x,y points
	 * within the background image, which is loaded from a file.
	 * 
	 * @param x - where to draw the text in the x direction
	 * @param y - where to draw the text in the y direction
	 * @param string - the attributed string to draw.  Can be null.
	 * @param imageFileName - the background image to draw on.  Must not be null.
	 */
	public TextImageReference(int x, int y, AttributedString string, String imageFileName) {
		this(x,y,string,ImageKit.loadImage(imageFileName));
	}

	/**
	 * Constructs a TextImageReference that will be drawn at the x,y points
	 * within the background image, which is loaded from a file.
	 * 
	 * @param x - where to draw the text in the x direction
	 * @param y - where to draw the text in the y direction
	 * @param string - the attributed string to draw.  Can be null.
	 * @param imageFileName - the background image to draw on.  Must not be null.
	 * @param encoder = the ImageEncoder to use
	 */
	public TextImageReference(int x, int y, AttributedString string, String imageFileName, ImageEncoder encoder) {
		this(x,y,string,ImageKit.loadImage(imageFileName),encoder);
	}
	
	/**
	 * Constructs a TextImageReference that will be drawn at the x,y points
	 * within the background image, which is loaded from an URL.
	 * 
	 * @param x - where to draw the text in the x direction
	 * @param y - where to draw the text in the y direction
	 * @param string - the attributed string to draw.  Can be null.
	 * @param imageURL - the background image to draw on.  Must not be null.
	 */
	public TextImageReference(int x, int y, AttributedString string, URL imageURL) {
		this(x,y,string,ImageKit.loadImage(imageURL));
	}

	/**
	 * Constructs a TextImageReference that will be drawn at the x,y points
	 * within the background image, which is loaded from an URL.
	 * 
	 * @param x - where to draw the text in the x direction
	 * @param y - where to draw the text in the y direction
	 * @param string - the attributed string to draw.  Can be null.
	 * @param imageURL - the background image to draw on.  Must not be null.
	 * @param encoder = the ImageEncoder to use
	 */
	public TextImageReference(int x, int y, AttributedString string, URL imageURL, ImageEncoder encoder) {
		this(x,y,string,ImageKit.loadImage(imageURL),encoder);
	}
	
	/** Constructs the object */
	private void constructTextImageRefererence(int x, int y, AttributedString string, Image backgroundImage, int scalingOption, ImageEncoder encoder) {
		if (backgroundImage == null)
			throw new IllegalArgumentException("The backgroundImage must be non null");
		if (!ImageKit.waitForImage(backgroundImage))
			throw new IllegalStateException("The backgroundImage could not be loaded");
		
		this.origImageW = backgroundImage.getWidth(ImageKit.imageObserver);
		this.origImageH = backgroundImage.getHeight(ImageKit.imageObserver);
		this.scaleOption = scalingOption;	
		this.attributedString = string;
		this.backgroundImage = backgroundImage;
		if (x == Integer.MAX_VALUE && y == Integer.MAX_VALUE)
			this.autoCenterAtConstruction = true;
			
		if (encoder == null) {
			// we use GIF encoding by default!
			encoder = new GifEncoder();
		}
		super.setEncoder(encoder);
		//
		// initially I thought that keeping the encoded results in memory would
		// be needed because the encoder would be called all the time.  However
		// the CacheableService facility of Echo is excellent and hence render
		// will only be called if the image has changed.  So we dont keep it
		// in memory.
		super.setKeptInMemory(false);
		
		//
		// okay workout background image size, and maybe scale it!
		drawer.reconstructBackgroundImage(x,y);
	}


	/**
	 * Called to paint the text of the TextImageReference.  The
	 * background image is already present when this method is called
	 * <p>
	 * Subclasses are expected to draw the AttributedString, respecting 
	 * the other parameters that are in force such as textAngle 
	 * <p>
	 * This method returns a Rectangle2D that outlines the boundaries
	 * of any text painted, whcih may be larger than the actual 
	 * drawing space.
	 * 
	 * @param g the Graphics2D
	 * @param x the x co-ord to paint the text
	 * @param y the y co-ord to paint the text
	 * @return returns a boundary Rectangle enclosing all text painted
	 */
	protected Rectangle2D paintText(Graphics2D g, int x, int y) {
		return drawer.paintText(g,x,y,this.attributedString);
	}
	
	/**
	 * This methods returns an array of TextLayouts that will be used to
	 * draw the attributed string.  In this default version of the method,
	 * the TextLayouts are broken down on carriage return boundaries.  These
	 * TextLayouts can then be used to determine the height and width
	 * of the attributed string.
	 * <p>
	 * This is called to determine the dimensions of the current text.  This 
	 * is used to decide whether to "scale" the background image
	 * if the text inside it is bigger then these plus the insets.  This
	 * method should return the same dimensions that are used
	 * in the paintText() method.
	 * <p>
	 *  
	 * @param g - the Graphics 2D
	 * @param as - the attributed string
	 * @return an array of TextLayout to be used for painting
	 */
	protected TextLayout[] getTextLayouts(Graphics2D g, AttributedString as) {
		ACI subcit;
		ACI cit = new ACI(attributedString.getIterator());

		FontRenderContext frc = g.getFontRenderContext();
		replaceAttributedImages(g, frc, this.attributedString);

		// run through the string and find all the \n chars indexes.
		// We start a new line from that position when drawing
		ArrayList textLayouts = new ArrayList();
		int current = cit.getBeginIndex();
		int end = cit.getEndIndex();
		int previous = current;
		boolean lastCharWasCR = false;
		for (char c = cit.first(); c != CharacterIterator.DONE; c = cit.next()) {
			if (c == '\n') {
				if (! lastCharWasCR) {
					subcit = new ACI(as.getIterator(null,previous,current));
					textLayouts.add(new TextLayout(subcit.getIterator(), frc));
					previous = current+1;
				} else {
					previous++;
				}
				lastCharWasCR = true;
			} else {
				lastCharWasCR = false;
			}
			current++;
		}
		if (previous < end) {
			subcit = new ACI(as.getIterator(null,previous,end));
			textLayouts.add(new TextLayout(subcit.getIterator(), frc));
		}

		return (TextLayout[]) textLayouts.toArray(new TextLayout[textLayouts.size()]);		
	}
	/**
	* Replaces all Unicode replacement characters u'FFFC' with the images from
	* getReplacementImages() array in a round robin fashion.
	*/
	protected void replaceAttributedImages(Graphics2D g, FontRenderContext frc, AttributedString as) {
		if (as == null)
			return;

		StringBuffer buf = new StringBuffer();
		CharacterIterator cit = as.getIterator();
		for (char c = cit.first(); c != CharacterIterator.DONE; c = cit.next()) {
			buf.append(c);
		}
		String s = buf.toString();

		int ii = 0;
		Image[] images = replacementImages;
		if (images != null && images.length > 0) {

			// the maximum height
			int maxTextHeight = 0;
			for (int i = 0; i < replacementImages.length; i++) {
				maxTextHeight = Math.max(maxTextHeight, images[i].getHeight(ImageKit.imageObserver));
			}
			TextLayout tl = new TextLayout(attributedString.getIterator(), frc);
			Rectangle rectText = tl.getBounds().getBounds();
			maxTextHeight = Math.max(maxTextHeight, rectText.height);

			int pos = s.indexOf("\ufffc");
			while (pos >= 0) {
				ImageKit.waitForImage(images[ii]);

				//int imgW = images[ii].getWidth(ImageKit.imageObserver);
				int imgH = images[ii].getHeight(ImageKit.imageObserver);
				int gX = 0;
				int gY = 0;

				int attrAlignment = GraphicAttribute.TOP_ALIGNMENT;
				if (replacementImageAlignment == Alignment.TOP) {
					attrAlignment = GraphicAttribute.ROMAN_BASELINE;
					gY += rectText.height - tl.getDescent();

				} else if (replacementImageAlignment == Alignment.CENTER) {
					attrAlignment = GraphicAttribute.ROMAN_BASELINE;
					if (imgH < maxTextHeight) {
						gY += (rectText.height - tl.getDescent()) / 2 + (imgH / 2);
					} else {
						gY += (rectText.height / 2) + (imgH / 2);
					}

				} else if (replacementImageAlignment == Alignment.BOTTOM) {
					attrAlignment = GraphicAttribute.ROMAN_BASELINE;
					gY += imgH;
				}
				ImageGraphicAttribute imageGraphic = new ImageGraphicAttribute(images[ii], attrAlignment, gX, gY);

				as.addAttribute(TextAttribute.CHAR_REPLACEMENT, imageGraphic, pos, pos + 1);

				// wrap around the replacement images array.
				ii++;
				if (ii >= images.length)
					ii = 0;

				pos = s.indexOf("\ufffc", pos + 1);
			}
		}
	}
	

	/**
	 * We override getImage() so that the resultant image 
	 * is "painted" every time it is needed.  This is a
	 * computation vs memory tradeoff.
	 * 
	 * @see EncodedImageReference#getImage()
	 */
	public Image getImage() {
		Image paintedImage = drawer.paintImage();
		return paintedImage;
	}
	/**
	 * Returns the backgroundImage in use
	 * 
	 * @return Image - the backgroundImage in use
	 */
	public Image getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * This returns the height of the backgroundImage
	 */
	public Extent getHeight() {
		return new Extent(backgroundImage.getHeight(ImageKit.imageObserver));
	}

	/**
	 * Returns the horizontal alignment of the text relative to the x,y 
	 * drawing point.
	 *
	 * @return The horizontal alignment of the text relative to the x,y 
	 * drawing point,
	 *         one of the following values:
	 *         <ul>
	 *         <li>Alignment.LEFT</li>
	 *         <li>Alignment.CENTER (the default)</li>
	 *         <li>Alignment.RIGHT</li>
	 *         </ul>
	 */
	public int getHorizontalAlignment() {
		return horizontalAlignment;
	}
	/**
	 * Returns the alignment of the replacement images within
	 * the <code>AttributedString</code>.  This can be
	 * Alignment.TOP, Alignment.CENTER or Alignment.BOTTOM
	 * 
	 * @return the replacement image alignment
	 */
	public int getReplacementImageAlignment() {
		return replacementImageAlignment;
	}

	/**
	 * Returns the array of replacement images that will be substituted for
	 * the Unicode replacement character 'u'FFFC'.  These are replaced in a 
	 * round-robin fashion, by circling around the array. This can be a
	 * null array. 
	 * 
	 * @return the array of replacement images
	 */
	public Image[] getReplacementImages() {
		return replacementImages;
	}

	/**
	 * Returns the AttributedString in use.
	 * 
	 * @return AttributedString - the AttributedString in use.
	 */
	public AttributedString getString() {
		return attributedString;
	}

	/**
	 * Returns the current textAngle.
	 * 
	 * @return int -  the current textAngle.
	 */
	public int getTextAngle() {
		return textAngle;
	}

	/**
	 * Returns the vertical alignment of the text relative to the x,y 
	 * drawing point.
	 *
	 * @return The vertical alignment of the text relative to the x,y 
	 * drawing point,
	 *         one of the following values:
	 *         <ul>
	 *         <li>Alignment.TOP</li>
	 *         <li>Alignment.CENTER (the default)</li>
	 *         <li>Alignment.BOTTOM</li>
	 *         </ul>
	 */
	public int getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * This returns the width of the backgroundImage.
	 */
	public Extent getWidth() {
		return new Extent(backgroundImage.getWidth(ImageKit.imageObserver));
	}

	/**
	 * The x position where the etxt will be drawn.
	 * 
	 * @return int - The x position where the etxt will be drawn.
	 */
	public int getX() {
		return x;
	}

	/**
	 * The y position where the etxt will be drawn.
	 * 
	 * @return int - The y position where the etxt will be drawn.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns true if the image produce is of the best quality.
	 * @return boolean - true if the image produce is of the best quality.
	 */
	public boolean isBestQuality() {
		return bestQuality;
	}

	/**
	 * Sets the background image to be used
	 * 
	 * @param image - the background image to be used
	 */
	public void setBackgroundImage(Image image) {
		if (image == null)
			throw new IllegalArgumentException("The background image must be non null");

		Image oldImage = backgroundImage;
		backgroundImage = image;
		if (!oldImage.equals(image)) {
			fireImageChange();
		}

	}

	/**
	 * Sets the background image by loading from an image
	 * file.
	 * 
	 * @param imageFileName - the background image file to be used
	 */
	public void setBackgroundImage(String imageFileName) {
		Image image = ImageKit.loadImage(imageFileName);
		setBackgroundImage(image);
	}

	/**
	 * Sets the background image by loading from an image
	 * URL.
	 * 
	 * @param imageURL - the background image URL to be used
	 */
	public void setBackgroundImage(URL imageURL) {
		Image image = ImageKit.loadImage(imageURL);
		setBackgroundImage(image);
	}

	/**
	 * Set this to true if you want the best quality images to be drawn.
	 * This setting affects the rendering hints used during image drawing. 
	 * 
	 * @param b
	 */
	public void setBestQuality(boolean b) {
		boolean oldValue = bestQuality;
		bestQuality = b;
		if (oldValue != bestQuality) {
			fireImageChange();
		}
	}

	/**
	 * Sets the horizontal alignment of the text relative to the x,y 
	 * drawing point.
	 *
	 * @param i -  The horizontal alignment of the text relative to the x,y 
	 * drawing point,
	 *         one of the following values:
	 *         <ul>
	 *         <li>Alignment.LEFT</li>
	 *         <li>Alignment.CENTER (the default)</li>
	 *         <li>Alignment.RIGHT</li>
	 *         </ul>
	 */
	public void setHorizontalAlignment(int i) {
		if (i != Alignment.LEFT && i != Alignment.CENTER && i != Alignment.RIGHT)
			throw new IllegalArgumentException("setHorizontalAlignment must be one of Alignment.LEFT, Alignment.CENTER, or Alignment.RIGHT");

		int oldValue = horizontalAlignment;
		horizontalAlignment = i;
		if (oldValue != horizontalAlignment) {
			fireImageChange();
		}
	}

	/**
	 * Sets the alignment of the replacement images within
	 * the <code>AttributedString</code>.  This can be
	 * Alignment.TOP, Alignment.CENTER or Alignment.BOTTOM
	 * 
	 * @param i
	 */
	public void setReplacementImageAlignment(int i) {
		int oldValue = replacementImageAlignment;
		replacementImageAlignment = i;
		if (oldValue != replacementImageAlignment) {
			fireImageChange();
		}
	}

	/**
	 * Sets the array of replacement images that will be substituted for
	 * the Unicode replacement character u'FFFC'.  These are replaced in a 
	 * round-robin fashion, for each Unicode replacement character in the
	 * AttribuedtString.  This can be a null array and hence no replacement
	 * will be done. 
	 * @param images
	 * @see AttributedString for more details
	 */
	public void setReplacementImages(Image[] images) {
		Image[] oldValue = replacementImages;
		replacementImages = images;
		if (oldValue != replacementImages) {
			fireImageChange();
		}
	}

	/**
	 * This is the AttributedString to be drawn onto the
	 * image.  This can be null.
	 * 
	 * @param string
	 */
	public void setString(AttributedString string) {
		AttributedString oldValue = this.attributedString;
		this.attributedString = string;
		if (oldValue != null && !oldValue.equals(this.attributedString)) {
			fireImageChange();
		}
	}

	/**
	 * Sets the text angle in degrees to be used.  
	 * 
	 * @param i int - the text angle to use.,
	 */
	public void setTextAngle(int i) {
		int oldValue = textAngle;
		textAngle = i;
		if (oldValue != textAngle) {
			fireImageChange();
		}
	}
	/**
	 * Sets the vertical alignment of the text relative to the x,y 
	 * drawing point.
	 *
	 * @param i -  The vertical alignment of the text relative to the x,y 
	 * drawing point,
	 *         one of the following values:
	 *         <ul>
	 *         <li>Alignment.TOP</li>
	 *         <li>Alignment.CENTER (the default)</li>
	 *         <li>Alignment.BOTTOM</li>
	 *         </ul>
	 */
	public void setVerticalAlignment(int i) {
		if (i != Alignment.TOP && i != Alignment.CENTER && i != Alignment.BOTTOM)
			throw new IllegalArgumentException("setHorizontalAlignment must be one of Alignment.TOP, Alignment.CENTER, or Alignment.BOTTOM");

		int oldValue = verticalAlignment;
		verticalAlignment = i;
		if (oldValue != verticalAlignment) {
			fireImageChange();
		}
	}

	/**
	 * Sets the X position to draw the text on the image.  If this is set to 
	 * Integer.MAX_VALUE then the text will be centered within the 
	 * background image.
	 * 
	 * @param i
	 */
	public void setX(int i) {
		int oldValue = x;
		x = i;
		if (oldValue != x) {
			fireImageChange();
		}
	}

	/**
	 * Sets the y position for drawing the text on the image. If this is set to 
	 * Integer.MAX_VALUE then the text will be centered within the 
	 * background image.
	 * 
	 * @param i
	 */
	public void setY(int i) {
		int oldValue = y;
		y = i;
		if (oldValue != y) {
			this.autoCenterAtConstruction = false;
			fireImageChange();
		}
	}

	/**
	 * @see java.io.Serializable
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.backgroundImage = ImageKit.readSerializedImage(in);
		this.attributedString = ImageKit.readSerializedAttributedString(in);

		int replaceImagesLen = in.readInt();
		replacementImages = new Image[replaceImagesLen];
		for (int i = 0; i < replaceImagesLen; i++) {
			replacementImages[i] = ImageKit.readSerializedImage(in);
		}
		fireImageChange();
	}

	/**
	 * @see java.io.Serializable
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();

		ImageKit.writeSerializedImage(out, backgroundImage);
		ImageKit.writeSerializedAttributedString(out, attributedString);

		int replaceImagesLen = (replacementImages == null ? 0 : replacementImages.length);
		out.writeInt(replaceImagesLen);
		for (int i = 0; i < replaceImagesLen; i++) {
			ImageKit.writeSerializedImage(out, replacementImages[i]);
		}
	}

	/**
	 * Returns an AttributedString with no color or font information
	 * 
	 * @param s
	 * @return AttributedString 
	 */
	public static AttributedString getAttributedString(String s) {
		return getAttributedString(s, null, null);
	}

	/**
	 * Returns an AttributedString with the specified forground color
	 * and font based on nextapp.echo.* visual objects.
	 * 
	 * @param s - must not be null
	 * @param foreground - can be null
	 * @param font - can be null
	 * @return AttributedString
	 */
	public static AttributedString getAttributedString(String s, Color foreground, Font font) {
		AttributedString as = new AttributedString(s);
		//
		// you can add attributes to a zero lengthed string so dont try!
		if (s == null || s.length() == 0)
			return as;

		if (foreground != null) {
			as.addAttribute(TextAttribute.FOREGROUND, ColorKit.makeAwtColor(foreground, null));
		}
		if (font != null) {
			as.addAttribute(TextAttribute.FONT, FontKit.makeAwtFont(font, null));
		}
		return as;
	}

	/** 
	 * Returns the scaling option in effect.
	 * @return the scaling option in effect.
	 */
	public int getScaleOption() {
		return scaleOption;
	}

	/** 
	 * Sets the scaling option that will be used to draw the background
	 * image.  If the text of the TextImageReference is to large for the
	 * background image, then the image will be scaled up to the requireed
	 * size, using this scaling option. 
	 * 
	 * @param scalingOption the new scaling option
	 */
	public void setScaleOption(int scalingOption) {
		int oldValue = this.scaleOption; 
		this.scaleOption = scalingOption;
		if ( oldValue != scalingOption) {
			fireImageChange();
		}
	}

	/**
	 * Returns the Insets in use 
	 * @return the Insets in use
	 */
	public Insets getInsets() {
		return insets;
	}

	/** 
	 * Sets the Insets between the text and the background image outer edge.
	 * @param insets the Insets between the text and the background image outer edge.
	 */
	public void setInsets(Insets insets) {
		Insets oldValue = this.insets; 
		this.insets = insets;
		if (oldValue != insets) {
			fireImageChange();
		}
	}
	
	/**
	 * Called when the image properties have changed and the background
	 * image might have to be scaled. 
	 */
	private void fireImageChange() {
		drawer.reconstructBackgroundImage(this.x,this.y);
		update();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (this.attributedString != null) {
			sb.append(new ACI(this.attributedString.getIterator()).toString());
			sb.append(" origW:");
			sb.append(origImageW);
			sb.append(" origH:");
			sb.append(origImageH);
			return sb.toString();
		} else {
			return "null";
		}
	}
	
	/**
	 * <code>TextImageDrawer</code> holds all the drawing code, just
	 * to help us organise this java source file.
	 */
	private class TextImageDrawer implements Serializable {
		
		/**
		 * This creates a "tall"  attributed string version of the
		 * specified string.  It does this by replacing all 
		 * standard ASCI characters with M and y so that any
		 * scaling will occur as if the string contained a
		 * M and a y.  The reason this is important is that
		 * images heights will be consistent accross different
		 * text in the images.
		 */
		private AttributedString createTallVersion(AttributedString as) {
			if (as == null)
				return as;
			
			AttributedCharacterIterator cit;
			cit = as.getIterator();
			for (char c = cit.first(); c != CharacterIterator.DONE; c = cit.next()) {
				if (c == 'M' || c == 'y')
					return as;
			}

			int count = 0;
			StringBuffer buf = new StringBuffer();
			cit = as.getIterator();
			for (char c = cit.first(); c != CharacterIterator.DONE; c = cit.next()) {
				if (Character.isLetter(c)) {
					c = (count++ % 2 == 0 ? 'y' : 'M');
				}
				buf.append(c);
			}
			
			AttributedString newAS = new AttributedString(buf.toString());
			count = 0;
			cit = as.getIterator();
			for (char c = cit.first(); c != CharacterIterator.DONE; c = cit.next()) {
				Map attributes = cit.getAttributes();
				newAS.addAttributes(attributes,count,count+1);
				count++; 
			}
			return newAS;			
		}
		/**
		 * Calculates the height of a text layout
		 */
		private double calcTextLayoutHeight(TextLayout textLayout) {
			double height = 0;
			height += textLayout.getAscent();
			height += textLayout.getDescent();
			height += textLayout.getLeading();
			return height;
		}
		/**
		 * Gets the bounds of the image with respect to the Insets
		 */
		Rectangle2D getPreferredBoundsRect(int imageW, int imageH) {
			int insetL = (getInsets() == null ? 0 : getInsets().getLeft().getValue());
			int insetT = (getInsets() == null ? 0 : getInsets().getTop().getValue());
			int insetR = (getInsets() == null ? 0 : getInsets().getRight().getValue());
			int insetB = (getInsets() == null ? 0 : getInsets().getBottom().getValue());
			Rectangle2D preferredTB = new Rectangle2D.Float(insetL,insetT,imageW-insetR-insetL,imageH-insetB-insetT);
			return preferredTB;
		}
		/**
		 * Returns the total dimensions of a list of TextLayouts.
		 *  
		 * @return Dimension - the text dimensions
		 */
		private Dimension getTextDimensions(TextLayout[] textLayouts) {
			double height = 0;
			double width = 0;
			double w = 0;
			for (int i = 0; i < textLayouts.length; i++) {
				w = textLayouts[i].getBounds().getWidth();
				if (w > width)
					width = w;
				height += calcTextLayoutHeight(textLayouts[i]);
			}
			return new Dimension((int) width, (int) height);
		}
		/** 
		 * Paints a small cross hair at a point with a little marker to to
		 * indicate which way postive x and postiive y are.
		 */
		void _paintCrossHairs(Graphics2D g, float x1, float y1, java.awt.Color clr) {
			int x = (int) x1;
			int y = (int) y1;
		
			int delta = 15;
			g.setPaint(clr);
			g.drawLine(x, y, x, y - delta);
			g.drawLine(x, y, x + delta, y);
			g.drawLine(x, y, x, y + delta);
			g.drawLine(x, y, x - delta, y);
		
			g.drawLine(x, y, x + (delta / 2), y + (delta / 2));
		}
		/**
		 * Draws a text layout around a point, aligning the text box
		 * around that point according to the given alignment values and
		 * rotating the text are the given angle.
		 * <p>
		 * This returns a bounding Rectangle of where to text was painted. 
		 */
		private Rectangle2D paintRotatedTextCentered(Graphics2D g, TextLayout tl, float rx, float ry, int textAngle, int hTextAlignment, int vTextAlignment) {
			Rectangle rect = tl.getBounds().getBounds();
		
			// debug - help us visually to see the {x,y} position
			//_paintCrossHairs(g, rx, ry, java.awt.Color.RED);
		
			// save the current transform
			AffineTransform atPrev = g.getTransform();
		
			// text is draw from the base line so adjust to its
			// logical bounding box, which is what we want centered
		
			float tx = rx;
			float ty = ry;
			if (hTextAlignment == Alignment.CENTER)
				tx = rx - (rect.width / 2);
			if (hTextAlignment == Alignment.RIGHT)
				tx = rx - (rect.width);
		
			if (vTextAlignment == Alignment.CENTER)
				ty = ry + (rect.height / 2) - (tl.getDescent() / 2);
			if (vTextAlignment == Alignment.TOP)
				ty = ry + (rect.height - tl.getDescent());
				
		
			// and make a new one around of point of interest
			AffineTransform at = new AffineTransform();
			at.setToIdentity();
			at.rotate(Math.toRadians(textAngle), rx, ry);
			at.translate(tx, ty);
			g.setTransform(at);
		
			float dx = 0;
			float dy = 0;
			tl.draw(g, dx, dy);
		
			// debug - this draws a box around the text
			// helps with visualising centering 
			//g.setColor(java.awt.Color.black);
			//g.draw(rect);
		
			g.setTransform(atPrev);
			
			Rectangle2D bounds = new Rectangle2D.Double();
			bounds.setRect(rect.getX()+tx,
							  rect.getY()+ty,
							  rect.getWidth(),
							  rect.getHeight());
			return bounds;
		}
		/** Called to contruct the actual background image and do any stretching as necessary */
		private void reconstructBackgroundImage(int inX, int inY) {		
			if (!ImageKit.waitForImage(backgroundImage))
				throw new IllegalStateException("The backgroundImage could not be scaled");
				
			int imageW = backgroundImage.getWidth(ImageKit.imageObserver);
			int imageH = backgroundImage.getHeight(ImageKit.imageObserver);
			
			//
			// work out if the painted text will be larger then the
			// background image plus its insets.  If so we might have to scale the 
			// background image to fit.  We "draw" from the
			// 
			if (getScaleOption() != SCALE_NONE) {
				BufferedImage bufImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bufImage.createGraphics();
				
				int insetL = (getInsets() == null ? 0 : getInsets().getLeft().getValue());
				int insetT = (getInsets() == null ? 0 : getInsets().getTop().getValue());
				int insetR = (getInsets() == null ? 0 : getInsets().getRight().getValue());
				int insetB = (getInsets() == null ? 0 : getInsets().getBottom().getValue());
				
				Rectangle2D preferredTB = new Rectangle2D.Float(insetL,insetT,imageW-insetR-insetL,imageH-insetB-insetT);
				
				int paintX = (autoCenterAtConstruction || inX == Integer.MAX_VALUE) ? imageW / 2 : inX;
				int paintY = (autoCenterAtConstruction || inY == Integer.MAX_VALUE) ? imageH / 2 : inY;
				
				AttributedString tallAS = createTallVersion(attributedString);
				Rectangle2D actualTB = paintText(g, paintX, paintY, tallAS);
				//
				// if the painted text does not fit in the actual background image arae
				// then lets scale it
				if (! preferredTB.contains(actualTB)) {
					int useScalingOption = getScaleOption();
					int newW = (int) (actualTB.getWidth()+insetL + insetR);
					int newH = (int) (actualTB.getHeight()+insetT + insetB);
					//
					// and scale it
					newW = Math.max(imageW,newW);
					newH = Math.max(imageH,newH);
					
					int spliceX = (autoCenterAtConstruction || inX == Integer.MAX_VALUE) ? imageW / 2 : inX;
					int spliceY = (autoCenterAtConstruction || inY == Integer.MAX_VALUE) ? imageH / 2 : inY;

					
					if (useScalingOption == SCALE_SPLICE_H_THEN_V) {
						backgroundImage = ImageKit.enlargeImageSpliceHoriz(backgroundImage,spliceY,newH);
						backgroundImage = ImageKit.enlargeImageSpliceVert(backgroundImage,spliceX,newW);
					}
					else if (useScalingOption == SCALE_SPLICE_V_THEN_H) {
						backgroundImage = ImageKit.enlargeImageSpliceVert(backgroundImage,spliceX,newW);
						backgroundImage = ImageKit.enlargeImageSpliceHoriz(backgroundImage,spliceY,newH);
					}
					else {
						backgroundImage = backgroundImage.getScaledInstance(newW,newH,useScalingOption);
					}
					imageW = newW;
					imageH = newH;
				}
			}
			x = (autoCenterAtConstruction || inX == Integer.MAX_VALUE) ? imageW / 2 : inX;
			y = (autoCenterAtConstruction || inY == Integer.MAX_VALUE) ? imageH / 2 : inY;
		}
		/** Called when the image may need to be painted */
		private Image paintImage() {
		
			ImageKit.waitForImage(backgroundImage);
			
			int imageW = backgroundImage.getWidth(ImageKit.imageObserver);
			int imageH = backgroundImage.getHeight(ImageKit.imageObserver);
		
			BufferedImage bufImage = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_ARGB);
		
			Graphics2D g = bufImage.createGraphics();
			if (bestQuality) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
				g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
			} else {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		
				g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
			}
		
			/// draw the background image on to the graphics area!
			g.drawImage(backgroundImage, 0, 0, imageW, imageH, ImageKit.imageObserver);
			
			// debug our drawing surface
			//g.setColor(java.awt.Color.ORANGE);
			//g.drawRect(0, 0, imageW-1, imageH-1);

			// call the protected paintText method to allow 
			// others to do the text painting
			paintText(g, x, y, attributedString);
			
			
			// debug - draw our preferred text boundaries
			//g.setColor(java.awt.Color.green);
			//g.draw(getPreferredBoundsRect(imageW,imageH));
		
			g.dispose();
		
			return bufImage;
		}
		
		private Rectangle2D paintText(Graphics2D g, int x, int y, AttributedString attributedString) {

			if (attributedString == null)
				return new Rectangle();
			ACI cit = new ACI(attributedString.getIterator());	
			int start = cit.getBeginIndex();
			int limit = cit.getEndIndex();
			if (start == limit)
				return new Rectangle();
			FontRenderContext frc = g.getFontRenderContext();
			// insert our replacement images
			replaceAttributedImages(g, frc, attributedString);

			TextLayout[] textLayouts = getTextLayouts(g,attributedString);
			Dimension textDimensions = getTextDimensions(textLayouts);
			float textHeight = (float) textDimensions.getHeight(); 
			float textWidth = (float) textDimensions.getWidth();
			if (textWidth <= 0 || textHeight <= 0)
				return new Rectangle();
			 
			float fx = x;
			float fy = y;
			float fydraw = fy;
		
			// debug - help us visually to see the {x,y} position
			//_paintCrossHairs(g, fx, fy, java.awt.Color.RED);
		
			//
			// if we are centered vertically, then we move up to
			// 1/2 the height of the total logical text box.
			//
			if (verticalAlignment == Alignment.CENTER)
				fy = fy - (textHeight / 2.0f);

			Rectangle2D textBounds = null;			
			for (int i = 0; i < textLayouts.length; i++) {
				fydraw = fy;
				//
				// again if we are centered vertically, we need to move
				// down 1/2 the height of the single text box
				if (verticalAlignment == Alignment.CENTER)
					fydraw += calcTextLayoutHeight(textLayouts[i]) / 2.0;
				 
				Rectangle2D singleTextBounds = paintRotatedTextCentered(g, textLayouts[i], fx, fydraw, textAngle, horizontalAlignment, verticalAlignment);
				if (textBounds == null)
					textBounds = singleTextBounds;
				else
					textBounds = textBounds.createUnion(singleTextBounds);
			
				fy += calcTextLayoutHeight(textLayouts[i]);
			}
			// debug - help us visually to see the text boundaries
			//g.setColor(java.awt.Color.orange);
			//g.draw(textBounds);
			return textBounds;
		}	
	};
	
	/**
	 * An AttributedCharacterIteraror delegate.  Used to make debugging easier
	 * in Eclipse!
	 */
	private class ACI implements Serializable {
		AttributedCharacterIterator cit;

		private ACI(AttributedCharacterIterator cit) {
			this.cit = cit;
		}
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			StringBuffer buf = new StringBuffer();
			for (char c = cit.first(); c != CharacterIterator.DONE; c = cit.next()) {
				buf.append(c);
			}
			return buf.toString();
		}

		public AttributedCharacterIterator getIterator() {
			return cit;
		}
		protected Object clone() throws CloneNotSupportedException {
			return cit.clone();
		}
		public char current() {
			return cit.current();
		}
		public boolean equals(Object obj) {
			return cit.equals(obj);
		}
		public char first() {
			return cit.first();
		}
		public int getBeginIndex() {
			return cit.getBeginIndex();
		}
		public int getEndIndex() {
			return cit.getEndIndex();
		}
		public int getIndex() {
			return cit.getIndex();
		}
		public char last() {
			return cit.last();
		}
		public char next() {
			return cit.next();
		}
		public char previous() {
			return cit.previous();
		}
		public char setIndex(int position) {
			return cit.setIndex(position);
		}

	}
}
