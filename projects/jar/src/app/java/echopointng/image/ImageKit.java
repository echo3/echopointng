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

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import nextapp.echo2.app.AwtImageReference;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;
import echopointng.util.ColorKit;
import echopointng.util.collections.ConcurrentReaderHashMap;

/**
 * A utility to class to help with ImageReference manipulation
 *
 * @author Brad Baker 
 */
public class ImageKit {

	/** You can use this object as an valid ImageObserver */
	public static final java.awt.Component imageObserver = new java.awt.Component() {
	};

	/** You can use this object as an valid MediaTracker */
	public static final java.awt.MediaTracker imageMediaTracker = new java.awt.MediaTracker(imageObserver);
	
	/* our media tracker ids */
	private static int mediaTrackerIDs = 0;
	
	/* our image cache */
	private static Map imageCache = new ConcurrentReaderHashMap();

	private static Map coloredImageCache = new ConcurrentReaderHashMap(); 

	/** not instantiable */
	private ImageKit() {
	}

	/**
	 * Creates an ImageReference that is width * height in dimensions and has
	 * the specified background color.
	 */
	public static ImageReference createImageRef(Color background, int width, int height) {
		java.awt.Color	clrBackground = ColorKit.makeAwtColor(background,java.awt.Color.white);
	
		//
		// create our Image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();

		g.setColor(clrBackground);
		g.fillRect(0, 0, width, height);
		
		return new AwtImageReference(image);
	}

	/**
	 * Returns an transparent ImageReference that is width * height in dimensions
	 */
	public static ImageReference getTransparentImageRef(int width, int height) {
		return new ResourceImageReference("/echopointng/ui/resource/images/transparent1x1.gif", new Extent(width), new Extent(height));
	}

	/**
	 * Returns an ImageReference that is width * height in dimensions with the specified
	 * background color. The returned ImageReference is cached for fast
	 * retrieval but it uses SoftReferences so it will degrade in low
	 * memory conditions.
	 */
	public static ImageReference getColoredImageRef(Color backgroundColor, int width, int height) {
		String name = "" + backgroundColor.getRgb() + "w:" + width + "h:" + height;
		
		SoftReference softRef = (SoftReference) coloredImageCache.get(name); 
		AwtImageReference awtImageRef = (AwtImageReference) (softRef == null ? null : softRef.get());
		
		if (awtImageRef == null) {
			BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setColor(ColorKit.makeAwtColor(backgroundColor,java.awt.Color.lightGray));
			g.fillRect(0,0,width,height);
			g.dispose();
			
			awtImageRef = new AwtImageReference(image);
			coloredImageCache.put(name,new SoftReference(awtImageRef));
		}
		return awtImageRef;
	}
	
	/**
	 * This method will return copy a source Image and return
	 * a new BufferedImage.  
	 * <p>
	 * This differs from makeBufferedImage() in that it gauruntees 
	 * to make a new copy of the image data whereas makeBufferedImage() 
	 * will return the same object if its already a BufferedImage.  
	 * 
	 * @param srcImage the source Image
	 * @return - a completely new BufferedImage
	 */
	public static BufferedImage copyImage(Image srcImage) {
		if (srcImage == null)
			throw new IllegalArgumentException("The srcImage must be non null");

		if (! waitForImage(srcImage))
			throw new IllegalStateException("The srcImage could not be loaded");

		int width = srcImage.getWidth(imageObserver);	
		int height = srcImage.getHeight(imageObserver);	
			
		boolean hasAlpha = hasAlphaChannel(srcImage);
    
		// Create a buffered image with a format that's compatible with the screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = getColorModel(srcImage).getTransparency();

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(width, height, transparency);
		} catch (Exception e) {
			// The system does not have a screen
		}
		if (bimage == null) {
			int type = (hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
			bimage = new BufferedImage(width,height,type);
		}
		Graphics2D g = bimage.createGraphics();
		g.drawImage(srcImage, 0,0,width,height,imageObserver);
		g.dispose();
		return bimage;
	}

	/**
	 * Private function to load a cached image from disk or the memory cache
	 * for a given String or URL.  Soft references are used.
	 */
	private static Image _loadImageFromCache(Object imageLocation, int width, int height) {
		SoftReference imageRef = (SoftReference) imageCache.get(imageLocation);
		Image image = (Image) ((imageRef == null) ? null : imageRef.get());
		if (image == null) {
			if (imageLocation instanceof URL) {
				if (width >= 0 && height >= 0)
					image = loadImage((URL) imageLocation, width, height);
				else
					image = loadImage((URL) imageLocation);
			}
			if (imageLocation instanceof String) {
				if (width >= 0 && height >= 0)
					image = loadImage((String) imageLocation, width, height);
				else
					image = loadImage((String) imageLocation);
			}
			imageRef = new SoftReference(image);
			imageCache.put(imageLocation,imageRef);						
		}
		return image;
	}
	/**
	 * Loads an image file from disk and returns an java.awt.Image.  The
	 * image is guaranteed to be fully loaded when this method returns and 
	 * is not contained in any internal cache.
	 * 
	 * @param imageFileName - the image file to be loaded
	 * @return Image - the loaded AWT image
	 */
	public static Image loadImage(String imageFileName) {
		Image image = Toolkit.getDefaultToolkit().createImage(imageFileName);
		if (!waitForImage(image))
			return null;
		return image;
	}

	/**
	 * Loads an image file from disk and returns an java.awt.Image, which 
	 * is then cached.  The image is guaranteed to be fully loaded when 
	 * this method returns. Soft references are used so that the image 
	 * cache can be reclaimed in low memory conditions.
	 * 
	 * @param imageFileName - the image file to be loaded
	 * @return Image - the loaded AWT image
	 */
	public static Image loadCachedImage(String imageFileName) {
		return _loadImageFromCache(imageFileName,-1,-1);
	}

	/**
	 * Loads an image file from disk and returns an java.awt.Image.  The
	 * image is guaranteed to be fully loaded when this method returns and 
	 * is not contained in any internal cache.  The image will be
	 * scaled to the specified width and height.
	 * 
	 * @param imageFileName - the image file to be loaded
	 * @param width - the width of the new image
	 * @param height - the height of the new image
	 * @return Image - the loaded AWT image
	 */
	public static Image loadImage(String imageFileName, int width, int height) {
		Image image = Toolkit.getDefaultToolkit().createImage(imageFileName);
		if (!waitForImage(image))
			return null;
		return makeBufferedImage(image, width, height);
	}

	/**
	 * Loads an image file from disk and returns an java.awt.Image, which 
	 * is then cached.  The image is guaranteed to be fully loaded when this 
	 * method returns. Soft references are used so that the image cache can 
	 * be reclaimed in low memory conditions.
	 * 
	 * The image will be scaled to the specified width and height.
	 * 
	 * @param imageFileName - the image file to be loaded
	 * @param width - the width of the new image
	 * @param height - the height of the new image
	 * @return Image - the loaded AWT image
	 */
	public static Image loadCachedImage(String imageFileName, int width, int height) {
		return _loadImageFromCache(imageFileName,width,height);
	}

	/**
	 * Loads an image file from disk and returns an java.awt.Image.  The
	 * image is guaranteed to be fully loaded when this method returns and 
	 * is not contained in any internal cache.  
	 * 
	 * @param imageUrl - the image URL to be loaded
	 * @return Image - the loaded AWT image
	 */
	public static Image loadImage(URL imageUrl) {
		Image image = Toolkit.getDefaultToolkit().createImage(imageUrl);
		if (!waitForImage(image))
			return null;
		return image;
	}

	/**
	 * Loads an image file from disk and returns an java.awt.Image, which 
	 * is then cached.  The image is guaranteed to be fully loaded when 
	 * this method returns. Soft references are used so that the image 
	 * cache can be reclaimed in low memory conditions.  
	 * 
	 * @param imageUrl - the image URL to be loaded
	 * @return Image - the loaded AWT image
	 */
	public static Image loadCachedImage(URL imageUrl) {
		return _loadImageFromCache(imageUrl,-1,-1);
	}

	/**
	 * Loads an image file from disk and returns an java.awt.Image.  The
	 * image is guaranteed to be fully loaded when this method returns and 
	 * is not contained in any internal cache.  The image will be
	 * scaled to the specified width and height.
	 * 
	 * @param imageUrl - the image URL to be loaded
	 * @param width - the width of the new image
	 * @param height - the height of the new image
	 * @return Image - the loaded AWT image
	 */
	public static Image loadImage(URL imageUrl, int width, int height) {
		Image image = Toolkit.getDefaultToolkit().createImage(imageUrl);
		if (!waitForImage(image))
			return null;
		return makeBufferedImage(image, width, height);
	}

	/**
	 * Loads an image file from disk and returns an java.awt.Image, which 
	 * is then cached.  The image is guaranteed to be fully loaded when 
	 * this method returns. Soft references are used so that the image 
	 * cache can be reclaimed in low memory conditions.
	 * 
	 * The image will be scaled to the specified width and height.
	 * 
	 * @param imageUrl - the image URL to be loaded
	 * @param width - the width of the new image
	 * @param height - the height of the new image
	 * @return Image - the loaded AWT image
	 */
	public static Image loadCachedImage(URL imageUrl, int width, int height) {
		return _loadImageFromCache(imageUrl,width,height);
	}
		
	/**
	 * Loads an image file from a named Java resource and returns an 
	 * java.awt.Image.  The image is guaranteed to be fully loaded 
	 * when this method returns and is not contained in any  
	 * internal cache.  
	 * 
	 * @param imageResourceName - the image resource to be loaded
	 * @return Image - the loaded AWT image
	 */
	public static Image loadResourceImage(String imageResourceName) {
		URL url = ImageKit.class.getResource(imageResourceName);
		return loadImage(url);
	}	

	/**
	 * Loads an image file from a named Java resource and returns an 
	 * java.awt.Image which is then cached.  The image is guaranteed to 
	 * be fully loaded when this method returns. Soft references are used
	 * so that the image cache can be reclaimed in low memory conditions.  
	 * 
	 * @param imageResourceName - the image resource to be loaded
	 * @return Image - the loaded AWT image
	 */
	public static Image loadCachedResourceImage(String imageResourceName) {
		URL url = ImageKit.class.getResource(imageResourceName);
		return loadCachedImage(url);
	}	
	
	/**
	 * Loads an image file from a named Java resource and returns an 
	 * java.awt.Image.  The image is guaranteed to be fully loaded 
	 * when this method returns and is not contained in any  
	 * internal cache.  The image will be scaled to the specified 
	 * width and height.
	 * 
	 * @param imageResourceName - the image resource to be loaded
	 * @param width - the width of the new image
	 * @param height - the height of the new image
	 * @return Image - the loaded AWT image
	 */
	public static Image loadResourceImage(String imageResourceName, int width, int height) {
		URL url = ImageKit.class.getResource(imageResourceName);
		return loadImage(url,width,height);
	}
	
	/**
	 * Loads an image file from a named Java resource and returns an 
	 * java.awt.Image which is then cached.  The image is guaranteed to 
	 * be fully loaded when this method returns The image will be scaled 
	 * to the specified width and height.
	 * 
	 * @param imageResourceName - the image resource to be loaded
	 * @param width - the width of the new image
	 * @param height - the height of the new image
	 * @return Image - the loaded AWT image
	 */
	public static Image loadCachedResourceImage(String imageResourceName, int width, int height) {
		URL url = ImageKit.class.getResource(imageResourceName);
		return loadCachedImage(url,width,height);
	}
		
	/**
	 * Returns a BufferedImage, that is same as the original
	 * java.awt.Image.  Its smart about passing in BufferedImage objects
	 * and will simply return them unchanged.  
	 * <p>
	 * The image type will be BufferedImage.TYPE_INT_ARGB if it has 
	 * an alpha channel other wise it will be BufferedImage.TYPE_INT_RGB
	 * 
	 * @param srcImage Image - the image to be converted
	 * @return a BufferedImage
	 */
	public static BufferedImage makeBufferedImage(Image srcImage) {
		if (!waitForImage(srcImage))
			return null;
		
		int iw = srcImage.getWidth(imageObserver);
		int ih = srcImage.getHeight(imageObserver);
		int type = hasAlphaChannel(srcImage) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
		return makeBufferedImage(srcImage, iw, ih, type);
	}

	/**
	 * Returns a BufferedImage, that is same as the original
	 * java.awt.Image.  Its smart about passing in BufferedImage objects
	 * and will simply return them unchanged.  
	 * <p>
	 * The image type will be BufferedImage.TYPE_INT_ARGB if it has 
	 * an alpha channel other wise it will be BufferedImage.TYPE_INT_RGB
	 * type will be BufferedImage.TYPE_INT_ARGB.
	 * <p> 
	 * The image will be scaled to the width/height dimensions.
	 * 
	 * @param srcImage - the image to be converted
	 * @param width - the width of the new image
	 * @param height - the height of the new image
	 * @return a BufferedImage
	 */
	public static BufferedImage makeBufferedImage(Image srcImage, int width, int height) {
		if (!waitForImage(srcImage))
			return null;
		int type = hasAlphaChannel(srcImage) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
		return makeBufferedImage(srcImage, width, height, type);
	}

	/**
	 * Returns a BufferedImage, that is same as the original
	 * java.awt.Image, scaled to width and height.  Its smart about 
	 * passing in BufferedImage objects and will simply return 
	 * them unchanged.
	 * 
	 * @param srcImage - the image to be converted
	 * @param width - the width of the new image
	 * @param height - the height of the new image
	 * @param imageType - can be on of BufferedImage types such as BufferedImage.TYPE_INT_ARGB
	 * @return a BufferedImage
	 */
	public static BufferedImage makeBufferedImage(Image srcImage, int width, int height, int imageType) {

		if (! (srcImage instanceof BufferedImage)) {
			if (!waitForImage(srcImage))
				return null;
		}

		int iw = srcImage.getWidth(imageObserver);
		int ih = srcImage.getHeight(imageObserver);
		if (srcImage instanceof BufferedImage && iw == width && ih == height)
			return (BufferedImage) srcImage;


		BufferedImage bi = new BufferedImage(width, height, imageType);

		Graphics2D g = bi.createGraphics();
		g.drawImage(srcImage, 0, 0, width, height, imageObserver);
		g.dispose();
		return bi;
	}
	
	/**
	 * This method will return the ColorModel of an Image.
	 *  
	 * @param srcImage the source Image
	 * @return - the ColorModel of the image
	 */
	public static ColorModel getColorModel(Image srcImage) {
		// If buffered image, the color model is readily available
		if (srcImage instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage)srcImage;
			return bimage.getColorModel();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(srcImage, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}
		ColorModel cm = pg.getColorModel();
		return cm;
	}	

	/**
	 * This method returns true if the provided Image has an alpha channel
	 * 
	 * @param srcImage - the image to check for an alpha channel
	 * @return true if the provided Image has an alpha channel
	 */
	public static boolean hasAlphaChannel(Image srcImage) {
		ColorModel cm = getColorModel(srcImage);
		if (cm != null)
			return cm.hasAlpha();
		return
			false;
	}

	/** 
	 * Private filter class to examine each pixel and make some color another Color
	 * This leaves the alpha value intact. 
	 */
	private static class ColorSwapImageFilter extends RGBImageFilter {
		private int srcRGB = 0;
		private int destRGB = 0;
		
		private ColorSwapImageFilter(java.awt.Color srcColor, java.awt.Color destColor) {
			srcRGB =((srcColor.getRed() & 0xFF) << 16) |
					((srcColor.getGreen() & 0xFF) << 8)  |
					((srcColor.getBlue() & 0xFF) << 0);

			destRGB =((destColor.getRed() & 0xFF) << 16) |
					((destColor.getGreen() & 0xFF) << 8)  |
					((destColor.getBlue() & 0xFF) << 0);

			canFilterIndexColorModel = true;
		}

		public final int filterRGB(int x, int y, int rgb) {
			int currentColor = (rgb & 0x00FFFFFF); 
			if (currentColor == srcRGB) {
				return ((rgb & 0xFF000000) | destRGB);
			} else {
				return rgb;
			}
		}
	}
	
	/**
	 * This will change all occurences of srcColor to destColor 
	 * in the image.  Any alpha valuews in the image will be 
	 * left intact.
	 * <p>
	 * This method works inline on the provided image.  It does NOT make a copy of 
	 * the source image.  
	 *  
	 * @param srcImage - the image to be transformed
	 * @param srcColor - the source color
	 * @param destColor - the dest color
	 * @return - a same image with the source and dest colors swapped.
	 */	
	public static Image swapImageColors(Image srcImage, Color srcColor, Color destColor) {
		if (srcColor == null)
			throw new IllegalArgumentException("The srcColor must be non null");
		if (destColor == null)
			throw new IllegalArgumentException("The destColor must be non null");
			
		java.awt.Color srcClr = ColorKit.makeAwtColor(srcColor,null);
		java.awt.Color destClr = ColorKit.makeAwtColor(destColor,null);
		ImageProducer ip = new FilteredImageSource(srcImage.getSource(), new ColorSwapImageFilter(srcClr,destClr));
		Image returnImage = Toolkit.getDefaultToolkit().createImage(ip);
		waitForImage(returnImage);
		return returnImage;
	}

	/** private filter class to examine each pixel and make some color transparent */
	private static class TransparencyImageFilter extends RGBImageFilter {
		private int markerRGB = 0;
		private int alphaRGB = 0;
		
		private TransparencyImageFilter(java.awt.Color markerColor, int alpha) {
			java.awt.Color alphaColor = new java.awt.Color(markerColor.getRed(),markerColor.getGreen(),markerColor.getBlue(),alpha);
			alphaRGB = ((alpha & 0xFF) << 24) |
					((markerColor.getRed() & 0xFF) << 16) |
					((markerColor.getGreen() & 0xFF) << 8)  |
					((markerColor.getBlue() & 0xFF) << 0);

			alphaRGB = alphaColor.getRGB();

			markerRGB = markerColor.getRGB() | 0xFF000000;
			canFilterIndexColorModel = true;
		}

		public final int filterRGB(int x, int y, int rgb) {
			int currentColor = (rgb | 0xFF000000); 
			if (currentColor == markerRGB) {
				return alphaRGB;
			} else {
				return rgb;
			}
		}
	}

	/**
	 * This will change the image so that any occurence of the specified color is fully transparent, ie
	 * has an alpha value of 0.  
	 * <p>
	 * This method works inline on the provided image.  It does NOT make a copy of 
	 * the source image.  
	 *  
	 * @param srcImage - the image to be transformed
	 * @param markerColor - the color to be made transparent
	 * @return - a new image with the specified color as fully transparent
	 */	
	public static Image makeImageColorTransparent(Image srcImage, Color markerColor) {
		return makeImageColorTransparent(srcImage,markerColor,0);
	}

	/**
	 * This will change the image so that any occurence of the specified color is changed to 
	 * have an alpha value of 'alpha';
	 * <p>
	 * This method works inline on the provided image.  It does NOT make a copy of 
	 * the source image.  
	 *  
	 * @param srcImage - the image to be transformed
	 * @param markerColor - the color to be made transparent
	 * @param alpha - an alpha value between 0 and 255
	 * @return - a same image with the specified color as having the alpha value
	 */	
	public static Image makeImageColorTransparent(Image srcImage, Color markerColor, int alpha) {
		if (alpha < 0 && alpha > 255)
			throw new IllegalArgumentException("The alpha value must be between 0 and 255");
			
		java.awt.Color awtTransClr = ColorKit.makeAwtColor(markerColor,null);
		ImageProducer ip = new FilteredImageSource(srcImage.getSource(), new TransparencyImageFilter(awtTransClr,alpha));
		Image returnImage = Toolkit.getDefaultToolkit().createImage(ip);
		waitForImage(returnImage);
		return returnImage;
	}
	
	
	public static BufferedImage makeImageGreyScale(Image srcImage) {
		BufferedImage bufImage;
		if (srcImage instanceof BufferedImage) {
			bufImage = (BufferedImage) srcImage;
		} else {
			bufImage = makeBufferedImage(srcImage);
		}
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		return op.filter(bufImage, null);	}


	/** 
	 * Waits for an Image to be loaded by using a MediaTracker internally.
	 * @param srcImage - the source Image
	 * @return false if the image cannot be loaded or true if it is down.
	 */
	public static boolean waitForImage(Image srcImage) {
		if (srcImage == null)
			return false;

		synchronized (imageObserver) {
			mediaTrackerIDs++;
		}
		
		imageMediaTracker.addImage(srcImage, mediaTrackerIDs);
		try {
			imageMediaTracker.waitForID(mediaTrackerIDs);
			if (imageMediaTracker.isErrorID(mediaTrackerIDs))
				return false;
		} catch (Exception e) {
			return false;
		} finally {
			imageMediaTracker.removeImage(srcImage, mediaTrackerIDs);
		}
		
		return true;
	}

	/**
	 * Writes an AttributedString to an object output stream and saves
	 * all Serializable obejcts contained within it.
	 * 
	 * @param out
	 * @param as
	 * @throws IOException
	 */
	public static void writeSerializedAttributedString(ObjectOutputStream out, AttributedString as) throws IOException {
		if (as == null) {
			// write  a null marker
			out.writeObject(null);
			return;
		}

		AttributedCharacterIterator cit = as.getIterator();
		StringBuffer buf = new StringBuffer();
		for (char c = cit.first(); c != AttributedCharacterIterator.DONE; c = cit.next()) {
			buf.append(c);
		}
		out.writeObject("nonnull");
		out.writeObject(buf.toString());

		cit = as.getIterator();
		Set keys = cit.getAllAttributeKeys();
		int keyCount = 0;
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Object key = iter.next();
			if (key instanceof Serializable) {

				AttributedCharacterIterator.Attribute attr = (AttributedCharacterIterator.Attribute) key;
				Object value = cit.getAttribute(attr);
				if (value instanceof Serializable) {
					keyCount++;
				}
			}
		}

		out.writeInt(keyCount);
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Object key = iter.next();
			if (key instanceof Serializable) {

				AttributedCharacterIterator.Attribute attr = (AttributedCharacterIterator.Attribute) key;
				Object value = cit.getAttribute(attr);
				if (value instanceof Serializable) {
					out.writeObject(attr);
					out.writeObject(value);
				}
			}
		}
	}

	/**
	 * Writes an Image out to a serialisation stream.   
	 *
	 * Most of the code was taken from the Echo AwtImageReference, so kudos to
	 * NextApp.
 	 *
	 * @param out the object output stream
	 * @param srcImage - the source Image
	 * @throws IOException if need be
	 */
	public static void writeSerializedImage(ObjectOutputStream out, Image srcImage) throws IOException {
		if (srcImage == null) {
			// has no image data
			out.writeBoolean(false);
		} else {	
			// has image data
			out.writeBoolean(true);
			int width = srcImage.getWidth(ImageKit.imageObserver);
			int height = srcImage.getHeight(ImageKit.imageObserver);
	
			out.writeInt(width);
			out.writeInt(height);
	
			int[] pixels = new int[width * height];
			try {
				PixelGrabber pg = new PixelGrabber(srcImage, 0, 0, width, height, pixels, 0, width);
				pg.grabPixels();
				if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
					throw new IOException("Unable to serialize java.awt.image: PixelGrabber aborted.");
				}
			} catch (InterruptedException ex) {
				throw new IOException("Unable to serialize java.awt.Image: PixelGrabber interrupted.");
			}
			out.writeObject(pixels);
		}
	}
	
	/**
	 * Reads in a serialized AttributeString and restores all Serializable
	 * objects contained within it.
	 * 
	 * @param in
	 * @return AttributedString - the new AttributedString
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static AttributedString readSerializedAttributedString(ObjectInputStream in) throws IOException, ClassNotFoundException {
		Object marker = in.readObject();
		if (marker == null)
			return null;

		String s = (String) in.readObject();

		AttributedString as = new AttributedString(s);

		int keyCount = in.readInt();
		for (int i = 0; i < keyCount; i++) {
			AttributedCharacterIterator.Attribute attr = (AttributedCharacterIterator.Attribute) in.readObject();
			Object value = in.readObject();

			as.addAttribute(attr, value);
		}

		return as;
	}

	/**
	 * Reads an image in from a serilisation stream.  It must have been written with the
	 * <code>writeSerializedImage</code> method.
	 * 
	 * Most of the code was taken from the Echo AwtImageReference, so kudos to
	 * NextApp.
	 * 
	 * @param in
	 * @return Image
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Image readSerializedImage(ObjectInputStream in) throws IOException, ClassNotFoundException {
		boolean hasImageData = in.readBoolean();
		if (hasImageData) {	
			int width = in.readInt();
			int height = in.readInt();

			int[] pixels = (int[]) in.readObject();
			if (pixels != null) {
				ColorModel colorModel = ColorModel.getRGBdefault();
				return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, colorModel, pixels, 0, width));
			}
		}
		return null;
	}
	

	/**
	 * This method mirrows the source image through the vertical
	 * plane, eg. the left become the right.
	 * 
	 * This will work inline on the image if its a BufferedImage
	 * and will create a copy if its not.
	 *    
	 * @param srcImage the source Image
	 * @return the image mirrowed
	 */
	public static Image mirrowImage(Image srcImage) {
		BufferedImage bufferedImage = makeBufferedImage(srcImage);
		
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-bufferedImage.getWidth(imageObserver), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		bufferedImage = op.filter(bufferedImage, null);

		return bufferedImage;
	}
		
	/**
	 * This method flips the source image through the horizontal
	 * plane, eg. the top become the bottom.
	 * 
	 * This will work inline on the image if its a BufferedImage
	 * and will create a copy if its not.
	 *    
	 * @param srcImage the source Image
	 * @return the image flipped
	 */
	public static Image flipImage(Image srcImage) {
		BufferedImage bufferedImage = makeBufferedImage(srcImage);

		// Flip the image horizontally
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -bufferedImage.getHeight(imageObserver));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		bufferedImage = op.filter(bufferedImage, null);
				
		return bufferedImage;
	}

	/**
	 * This method roteates the source image through the horizontal
	 * and vertical plane.  This is the equivalent of rotating it
	 * through 180 degrees. 
	 * 
	 * This will work inline on the image if its a BufferedImage
	 * and will create a copy if its not.
	 *    
	 * @param srcImage the source Image
	 * @return the image rotated 180 degrees.
	 */
	public static Image rotateImage(Image srcImage) {
		BufferedImage bufferedImage = makeBufferedImage(srcImage);

		// Flip the image vertically and horizontally;
		// equivalent to rotating the image 180 degrees
		AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
		tx.translate(-bufferedImage.getWidth(imageObserver), -bufferedImage.getHeight(imageObserver));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		bufferedImage = op.filter(bufferedImage, null);
		return bufferedImage;
	}

	/**
	 * Called to enlarge an image by splicing it vertically (along the X axis) 
	 * at <b>x</b> and filling the new section with a 1 pixel sample at <b>x</b>
	 * <p>
	 * This method will only enlarge the image.  If the new width is less than
	 * or equal to the current width, then the srcImage is returned.
	 * 
	 *    
	 * @param srcImage - the source Image
	 * @param x - the position to splice at
	 * @param newW - the new width of the image
	 * @return an image that is enlarged to the new width via splicing
	 */
	public static Image enlargeImageSpliceVert(Image srcImage, int x, int newW) {
		int imgW = srcImage.getWidth(imageObserver);
		int imgH = srcImage.getHeight(imageObserver);
		if (newW <= imgW)
			return srcImage;
		if (x <= 0 || x >= imgW)
			x = imgW / 2;	
		return _spliceImageInternal(srcImage,x,0,newW,imgH,imgW,imgH,true);
	}

	/**
	 * Called to enlarge an image by splicing it horizontally (along the Y axis) 
	 * at <b>y</b> and filling the new section with a 1 pixel sample at <b>y</b>
	 * <p>
	 * This method will only enlarge the image.  If the new height is less than
	 * or equal to the current height, then the srcImage is returned.
	 *    
	 * @param srcImage - the source Image
	 * @param y - the position to splice at
	 * @param newH - the new height of the image
	 * @return an image that is enlarged to the new height via splicing
	 */
	public static Image enlargeImageSpliceHoriz(Image srcImage, int y, int newH) {
		int imgW = srcImage.getWidth(imageObserver);
		int imgH = srcImage.getHeight(imageObserver);
		if (newH <= imgH)
			return srcImage;
		if (y <= 0 || y >= imgH)
			y = imgH / 2;	
		return _spliceImageInternal(srcImage,0,y,imgW,newH,imgW,imgH,false);
	}
	
	/*
	 * Called internally to splice an image vertically or horizontally.  This
	 * will stretch to the newX, newY sizes if they are different to the 
	 * source image, which is usually not what you want.
	 */
	private static Image _spliceImageInternal(Image srcImage, int x, int y, int newW, int newH, int imgW, int imgH, boolean verticallyFirst) {
		
		x = (x <= 0) ? imgW/2 : x;
		y = (y <= 0) ? imgH/2 : y;
		
		int widthDiff = newW - imgW;
		int heightDiff = newH - imgH;
		
		int sx1 = 0; int sy1 = 0; 
		int sx2 = 0; int sy2 = 0;
		int dx1 = 0; int dy1 = 0;
		int dx2 = 0; int dy2 = 0; 

		BufferedImage bufImg = new BufferedImage(newW,newH,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufImg.createGraphics();
		
		if (verticallyFirst) {
			// --------------------------
			// VERTICAL SPLICE
			// --------------------------
			//
			// draw the repeated part!		
			sx1 = x; 				sy1 = 0; 
			sx2 = x+1; 				sy2 = imgH;
			dx1 = x;				dy1 = 0;
			dx2 = x+1; 				dy2 = newH;
			for (int i = 0; i < widthDiff; i++) {
				g.drawImage(srcImage, dx1,dy1,dx2,dy2, sx1,sy1,sx2,sy2, imageObserver);
				dx1 += 1; 
				dx2 += 1; 
			}
			//		
			// draw the left hand side		
			sx1 = 0; sy1 = 0; 
			sx2 = x; sy2 = imgH;
			dx1 = 0; dy1 = 0;
			dx2 = x; dy2 = newH;
			g.drawImage(srcImage, dx1,dy1,dx2,dy2, sx1,sy1,sx2,sy2, imageObserver);
			//		
			// draw the right hand side		
			sx1 = x; 				sy1 = 0; 
			sx2 = imgW; 			sy2 = imgH;
			dx1 = newW-(imgW-x);	dy1 = 0;
			dx2 = newW; 			dy2 = newH;
			g.drawImage(srcImage, dx1,dy1,dx2,dy2, sx1,sy1,sx2,sy2, imageObserver);
		} else {
			// --------------------------
			// HORIZONTAL SPLICE
			// --------------------------
			//
			// draw the repeated part!		
			sx1 = 0; 				sy1 = y; 
			sx2 = imgW; 			sy2 = y+1;
			dx1 = 0;				dy1 = y;
			dx2 = newW;				dy2 = y+1;
			for (int i = 0; i < heightDiff; i++) {
				g.drawImage(srcImage, dx1,dy1,dx2,dy2, sx1,sy1,sx2,sy2, imageObserver);
				dy1 += 1; 
				dy2 += 1; 
			}
			//		
			// draw the left hand side		
			sx1 = 0; 	sy1 = 0; 
			sx2 = imgW; sy2 = y;
			dx1 = 0; 	dy1 = 0;
			dx2 = newW; dy2 = y;
			g.drawImage(srcImage, dx1,dy1,dx2,dy2, sx1,sy1,sx2,sy2, imageObserver);
			//		
			// draw the right hand side		
			sx1 = 0; 				sy1 = y; 
			sx2 = imgW; 			sy2 = imgH;
			dx1 = 0;				dy1 = newH-(imgH-y);
			dx2 = newW; 			dy2 = newH;
			g.drawImage(srcImage, dx1,dy1,dx2,dy2, sx1,sy1,sx2,sy2, imageObserver);
		}
		g.dispose();
		return bufImg;
	}

}
