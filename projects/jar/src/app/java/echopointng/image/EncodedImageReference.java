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

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.StreamImageReference;

/**
 * The <code>EncodedImageReference</code> class is an ImageReference that
 * can render <code>java.awt.Image</code> objects.  The <code>ImageEncoder</code>
 * used to encode the image data is pluggable.  
 * <p>
 * This class allows you to balance memory usage versus processor usage.
 * <p>
 * By default the output of the encoding is not kept in memory, so the encoding 
 * runs every time the image needs to be rendered. However this can be changed so
 * that the encoding results are kept in memory.
 * <p>
 * If the image data is cached, they are kept in a <code>SoftReference</code> so 
 * they may be reclaimed by the GC if memory gets low.
 * <p>
 * The AWT Image object that is renderered is by default kept in memory.  You can
 * change this by deriving a new class from <code>EncodedImageReference</code> and then
 * drawing the AWT image on as needed basis during the call to <code>getImage()</code>.
 * <p>     
 * 
 * @author Brad Baker
 */
public class EncodedImageReference extends StreamImageReference implements Serializable {

	private transient ImageEncoder encoder;
	private transient Image internalImage;
	private 		  boolean keptInMemory;
	private transient SoftReference refEncodedBytes;
	private 		  boolean valid;
	private 		  String id = ApplicationInstance.generateSystemId();


	/**
	 * Constructs an <code>EncodedImageReference</code> without an image.  You
	 * must of course call <code>setImage()</code> before the image is rendered, 
	 * otherwise there will be nothing to render.  
	 * <p> Another alternative would be to derive a new class and override 
	 * <code>getImage()</code> to provide the image as necessary.  This
	 * will use a smallest amount of memory at the expense of more CPU processing. 
	 * 
	 */
	public EncodedImageReference() {
		this(null);
	}

	/**
	 * Constructs an <code>EncodedImageReference</code> with an image.
	 * 
     * @param image A java.awt.Image to be displayed.  If you intend to extend
     *        this class and override the getImage() method to return images
     *        only as they are needed, then you may pass null to this parameter.
	 */
	public EncodedImageReference(Image image) {
		this.internalImage = image;
		if (image != null && ImageKit.hasAlphaChannel(image))
			encoder = new GifEncoder();
		else
			encoder = new PngEncoder();
		refEncodedBytes = new SoftReference(null);
		keptInMemory = false;
		valid = false;
	}

	/**
	 * @see nextapp.echo2.app.RenderIdSupport#getRenderId()
	 */
	public String getRenderId() {
		return id;
	}
	/**
	 * @see nextapp.echo2.app.StreamImageReference#getContentType()
	 */
	public String getContentType() {
		return encoder.getContentType();
	}

	/**
	 * Returns the <code>ImageEncoder</code> in use.
	 * 
	 * @return ImageEncoder
	 */
	public ImageEncoder getEncoder() {
		return encoder;
	}

	/**
	 * @see nextapp.echo2.app.ImageReference#getHeight()
	 */
	public Extent getHeight() {
		Image image = getImage();
		if (image != null)
			return new Extent(image.getHeight(ImageKit.imageObserver));
		return super.getHeight();
	}

	/**
	 * Returns the <code>Image</code> in use.
	 * <p>
     * You may extend this class and override this method such that
     * images are created only when they are needed, thereby 
     * reducing memory usage at the cost of increased processor 
     * workload.
	 * 
	 * @return Image - the AWT image object
	 */
	public Image getImage() {
		return internalImage;
	}

	/**
	 * @see nextapp.echo2.app.ImageReference#getWidth()
	 */
	public Extent getWidth() {
		Image image = getImage();
		if (image != null)
			return new Extent(image.getWidth(ImageKit.imageObserver));
		return super.getWidth();
	}

	/**
	 * Returns true if the encoded image is cached in memory.
	 * 
	 * @return boolean true if the image is kept in memory
	 */
	public boolean isKeptInMemory() {
		return keptInMemory;
	}
	

	/** 
	 * This returns whether the AWT Image data is currently valid.  
	 *    
	 * @return - true if the AWT image is valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @see java.io.Serializable
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();


		Class encoderClass = (Class) in.readObject();
		try {
			encoder = (ImageEncoder) encoderClass.newInstance();
		} catch (Exception e) {
			throw new IOException("Unable to instanstiate the encoder class : " + encoderClass.getName() + " : " + e.toString());
		}
		valid = false;
		refEncodedBytes = new SoftReference(null);
		
		this.internalImage = ImageKit.readSerializedImage(in);
	}

	/**
	 * @see nextapp.echo2.app.StreamImageReference#render(java.io.OutputStream)
	 */
	public void render(OutputStream out) throws IOException {
		if (isKeptInMemory()) {
			byte[] bytes = (byte[]) refEncodedBytes.get();
			//
			// the GC may have reaped out bytes or the image may
			// no longer be valid.  Either way we need to encode.
			//
			if (bytes == null || ! isValid()) {
				Image image = getImage();
				if (image == null)
					return;
				ByteArrayOutputStream ba = new ByteArrayOutputStream();
				encoder.encode(image, ba);
				bytes = ba.toByteArray();
				refEncodedBytes = new SoftReference(bytes);
				setValid(true);
				//
				// tell any imagelisteners that the image has changed
				update();
			}
			out.write(bytes);
		} else {
			Image image = getImage();
			if (image == null)
				return;
			encoder.encode(image, out);
		}
	}

	/**
	 * Sets the <code>ImageEncoder</code> to use for encoding 
	 * 
	 * @param encoder
	 */
	public void setEncoder(ImageEncoder encoder) {
		if (encoder == null)
			throw new IllegalArgumentException("The Encoder must be non null!");
			
		ImageEncoder oldValue = this.encoder;
		this.encoder = encoder;
		if (oldValue != this.encoder) {
			valid = false;
			update();
		}
	}

	/**
	 * Sets the <code>Image</code> to encode.  This can be null.
	 * 
	 * @param image - the AWT Image to encode. 
	 */
	public void setImage(Image image) {
		Image oldValue = this.internalImage;
		this.internalImage = image;
		if (oldValue != image) {
			valid = false;
			update();
		}
	}

	/**
	 * If set to true, the image will be encoded only as necessary and
	 * then cached in memory, otherwise it will be encoded as required.
	 * 
	 * @param b
	 */
	public void setKeptInMemory(boolean b) {
		boolean oldValue = this.keptInMemory;
		keptInMemory = b;
		if (oldValue != this.keptInMemory) {
			valid = false;
			update();
		}
	}

	/** 
	 * This controls whether the AWT Image data is currently valid.
	 * <p>
	 * This is used to determine whether a call to <code>getImage()</code> should be made.  If the
	 * encoded image is <b>kept in memory</b> but the image is invalid, then it will
	 * be encoded again.  If it valid, then the cached encoded image data can be re-used.
	 * <p>
	 * If you derive a new class and override the getImage() method, you should set 
	 * this to false when the image needs to be re-painted.
	 *   
	 * @param valid - true if the AWT image data is valid.
	 */
	public void setValid(boolean valid) {
		boolean oldValue = this.valid;
		this.valid = valid;
		if (valid != oldValue)
			update();
		
	}

	/**
	 * @see java.io.Serializable
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(encoder.getClass());
		ImageKit.writeSerializedImage(out,getImage());
	}
	
	/**
	 * Not supported.
	 */
	public void update() { }
}

