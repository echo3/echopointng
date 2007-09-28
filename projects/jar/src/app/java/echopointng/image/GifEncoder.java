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

/*
 * This source file is based on the popular ACME GIF image encoder.  
 * It was incorporated as part of the EchoPoint project on
 * 13/04/2003.
 */

/*
 GifEncoder - write out an image as a GIF

 Transparency handling and variable bit size courtesy of Jack Palevich.

 Copyright (C)1996,1998 by Jef Poskanzer <jef@acme.com>. All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 Visit the ACME Labs Java page for up-to-date versions of this and other
 fine Java utilities: http://www.acme.com/java/
*/
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

/**
 * The <code>BaseImageEncoder</code> class is the ImageConsumer  
 */
abstract class BaseImageEncoder implements ImageConsumer {
	private static final ColorModel rgbModel = ColorModel.getRGBdefault();

	private boolean accumulate = false;
	private int[] accumulator;
	private boolean encoding;
	private int height = -1;
	private int hintflags = 0;
	private IOException iox;

	protected OutputStream out;

	private ImageProducer producer;
	private boolean started = false;
	private int width = -1;

	public BaseImageEncoder(Image img, OutputStream out) {
		this(img.getSource(), out);
	}

	public BaseImageEncoder(ImageProducer producer, OutputStream out) {
		this.producer = producer;
		this.out = out;
	}

	public synchronized void encode() throws IOException {
		encoding = true;
		iox = null;
		producer.startProduction(this);
		while (encoding)
			try {
				wait();
			} catch (InterruptedException e) {
			}
		if (iox != null)
			throw iox;
	}

	/// Subclasses implement this to finish an encoding.
	abstract void encodeDone() throws IOException;

	private void encodeFinish() throws IOException {
		if (accumulate) {
			encodePixels(0, 0, width, height, accumulator, 0, width);
			accumulator = null;
			accumulate = false;
		}
	}

	/// Subclasses implement this to actually write out some bits.  They
	// are guaranteed to be delivered in top-down-left-right order.
	// One int per pixel, index is row * scansize + off + col,
	// RGBdefault (AARRGGBB) color model.
	abstract void encodePixels(int x, int y, int w, int h, int[] rgbPixels, int off, int scansize) throws IOException;

	private void encodePixelsWrapper(int x, int y, int w, int h, int[] rgbPixels, int off, int scansize) throws IOException {
		if (!started) {
			started = true;
			encodeStart(width, height);
			if ((hintflags & TOPDOWNLEFTRIGHT) == 0) {
				accumulate = true;
				accumulator = new int[width * height];
			}
		}
		if (accumulate)
			for (int row = 0; row < h; ++row)
				System.arraycopy(rgbPixels, row * scansize + off, accumulator, (y + row) * width + x, w);
		else
			encodePixels(x, y, w, h, rgbPixels, off, scansize);
	}

	// Methods that subclasses implement.

	/// Subclasses implement this to initialize an encoding.
	abstract void encodeStart(int w, int h) throws IOException;

	public void imageComplete(int status) {
		producer.removeConsumer(this);
		if (status == ImageConsumer.IMAGEABORTED)
			iox = new IOException("image aborted");
		else {
			try {
				encodeFinish();
				encodeDone();
			} catch (IOException e) {
				iox = e;
			}
		}
		stop();
	}

	public void setColorModel(ColorModel model) {
		// Ignore.
	}

	// Methods from ImageConsumer.

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setHints(int hintflags) {
		this.hintflags = hintflags;
	}

	public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize) {
		int[] rgbPixels = new int[w];
		for (int row = 0; row < h; ++row) {
			int rowOff = off + row * scansize;
			for (int col = 0; col < w; ++col)
				rgbPixels[col] = model.getRGB(pixels[rowOff + col] & 0xff);
			try {
				encodePixelsWrapper(x, y + row, w, 1, rgbPixels, 0, w);
			} catch (IOException e) {
				iox = e;
				stop();
				return;
			}
		}
	}

	public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
		if (model == rgbModel) {
			try {
				encodePixelsWrapper(x, y, w, h, pixels, off, scansize);
			} catch (IOException e) {
				iox = e;
				stop();
				return;
			}
		} else {
			int[] rgbPixels = new int[w];
			for (int row = 0; row < h; ++row) {
				int rowOff = off + row * scansize;
				for (int col = 0; col < w; ++col)
					rgbPixels[col] = model.getRGB(pixels[rowOff + col]);
				try {
					encodePixelsWrapper(x, y + row, w, 1, rgbPixels, 0, w);
				} catch (IOException e) {
					iox = e;
					stop();
					return;
				}
			}
		}
	}

	public void setProperties(Hashtable props) {
	}

	private synchronized void stop() {
		encoding = false;
		notifyAll();
	}
}

/**
 * <code>GifEncoder</code> implements the <code>ImageEncoder</code> interface
 * and hence can take a java.awt.Image object and encode it as a GIF
 * output stream.  It also handles alpha transparency in the source image.
 * <p>
 */
public class GifEncoder implements ImageEncoder, Serializable {

	/**
	 * Constructs a GifEncoder ready to start encoding
	 */
	public GifEncoder() {
	}

	/**
	 * @see echopointng.image.ImageEncoder#encode(java.awt.Image, java.io.OutputStream)
	 */
	public void encode(Image image, OutputStream out) throws IOException {
		GifEncoderInternal encoder = new GifEncoderInternal(image, out);
		encoder.encode();
	}

	/**
	 * @see echopointng.image.ImageEncoder#getContentType()
	 */
	public String getContentType() {
		return "image/gif";
	}
}

class GifEncoderHashItem {
	public int count;
	public int index;
	public boolean isTransparent;

	public int rgb;

	public GifEncoderHashItem(int rgb, int count, int index, boolean isTransparent) {
		this.rgb = rgb;
		this.count = count;
		this.index = index;
		this.isTransparent = isTransparent;
	}

}

/**
 * The <code>GifEncoderInternal</code> class writes out image data to
 * an output stream in the GIF format.  It also handles alpha
 * transparency in the source image.
 * 
 */
class GifEncoderInternal extends BaseImageEncoder {

	static final int BITS = 12;

	static final int EOF = -1;

	static final int HSIZE = 5003; // 80% occupancy

	// Number of characters so far in this 'packet'
	int a_count;
	// Define the storage for the packet accumulator
	byte[] accum = new byte[256];
	// block compression parameters -- after all codes are used up,
	// and compression rate changes, start over.
	boolean clear_flg = false;

	int clearCode;
	int[] codetab = new int[HSIZE];
	IntHashtable colorHash;

	int countDown;
	int cur_accum = 0;
	int cur_bits = 0;
	int curx, cury;
	int encodeWidth, encodeHeight;
	int eofCode;
	int free_ent = 0; // first unused entry
	int g_init_bits;
	int hsize = HSIZE; // for dynamic table sizing
	int[] htab = new int[HSIZE];
	private boolean interlace = false;

	int masks[] = { 0x0000, 0x0001, 0x0003, 0x0007, 0x000F, 0x001F, 0x003F, 0x007F, 0x00FF, 0x01FF, 0x03FF, 0x07FF, 0x0FFF, 0x1FFF, 0x3FFF, 0x7FFF, 0xFFFF };
	int maxbits = BITS; // user settable max # bits/code
	int maxcode; // maximum code, given n_bits
	int maxmaxcode = 1 << BITS; // should NEVER generate this code

	int n_bits; // number of bits/code
	int passCount = 0;
	int[][] rgbPixels;
	int width, height;

	// Algorithm:  use open addressing double hashing (no chaining) on the
	// prefix code / next character combination.  We do a variant of Knuth's
	// algorithm D (vol. 3, sec. 6.4) along with G. Knott's relatively-prime
	// secondary probe.  Here, the modular division first probe is gives way
	// to a faster exclusive-or manipulation.  Also do block compression with
	// an adaptive reset, whereby the code table is cleared when the compression
	// ratio decreases, but after the table fills.  The variable-length output
	// codes are re-sized at this point, and a special CLEAR code is generated
	// for the decompressor.  Late addition:  construct the table according to
	// file size for noticeable speed improvement on small files.  Please direct
	// questions about this implementation to ames!jaw.

	// GIF Image compression - modified 'compress'
	//
	// Based on: compress.c - File compression ala IEEE Computer, June 1984.
	//
	// By Authors:  Spencer W. Thomas      (decvax!harpo!utah-cs!utah-gr!thomas)
	//              Jim McKie              (decvax!mcvax!jim)
	//              Steve Davies           (decvax!vax135!petsd!peora!srd)
	//              Ken Turkowski          (decvax!decwrl!turtlevax!ken)
	//              James A. Woods         (decvax!ihnp4!ames!jaw)
	//              Joe Orost              (decvax!vax135!petsd!joe)

	/**
	 * Constructs a <code>GifEncoder</code> from an <code>Image</code> object
	 * with no interlacing in effect.
	 * 
	 * @param img The image to encode.
	 * @param out The stream to write the GIF to.
	 * @throws IOException if the GIF cann be encoded
	 */
	public GifEncoderInternal(Image img, OutputStream out) {
		super(img, out);
	}

	/**
	 * Constructs a <code>GifEncoder</code> from an <code>Image</code> object
	 * with the ability to interlaced to output.
	 * 
	 * @param img The image to encode.
	 * @param out The stream to write the GIF to.
	 * @param interlace Wether to interlace the output.
	 * @throws IOException if the GIF cann be encoded
	 */
	public GifEncoderInternal(Image img, OutputStream out, boolean interlace) {
		super(img, out);
		this.interlace = interlace;
	}

	// Bump the 'curx' and 'cury' to point to the next pixel
	void bumpPixel() {
		// Bump the current X position
		++curx;

		// If we are at the end of a scan line, set curx back to the beginning
		// If we are interlaced, bump the cury to the appropriate spot,
		// otherwise, just increment it.
		if (curx == encodeWidth) {
			curx = 0;

			if (!interlace)
				++cury;
			else {
				switch (passCount) {
					case 0 :
						cury += 8;
						if (cury >= encodeHeight) {
							++passCount;
							cury = 4;
						}
						break;

					case 1 :
						cury += 8;
						if (cury >= encodeHeight) {
							++passCount;
							cury = 2;
						}
						break;

					case 2 :
						cury += 4;
						if (cury >= encodeHeight) {
							++passCount;
							cury = 1;
						}
						break;

					case 3 :
						cury += 2;
						break;
				}
			}
		}
	}

	// Set up the 'byte output' routine
	void char_init() {
		a_count = 0;
	}

	// Add a character to the end of the current packet, and if it is 254
	// characters, flush the packet to disk.
	void char_out(byte c, OutputStream outs) throws IOException {
		accum[a_count++] = c;
		if (a_count >= 254)
			flush_char(outs);
	}

	// Clear out the hash table

	// table clear for block compress
	void cl_block(OutputStream outs) throws IOException {
		cl_hash(hsize);
		free_ent = clearCode + 2;
		clear_flg = true;

		output(clearCode, outs);
	}

	// reset code table
	void cl_hash(int hsize) {
		for (int i = 0; i < hsize; ++i)
			htab[i] = -1;
	}

	void compress(int init_bits, OutputStream outs) throws IOException {
		int fcode;
		int i /* = 0 */;
		int c;
		int ent;
		int disp;
		int hsize_reg;
		int hshift;

		// Set up the globals:  g_init_bits - initial number of bits
		g_init_bits = init_bits;

		// Set up the necessary values
		clear_flg = false;
		n_bits = g_init_bits;
		maxcode = maxcode(n_bits);

		clearCode = 1 << (init_bits - 1);
		eofCode = clearCode + 1;
		free_ent = clearCode + 2;

		char_init();

		ent = nextPixel();

		hshift = 0;
		for (fcode = hsize; fcode < 65536; fcode *= 2)
			++hshift;
		hshift = 8 - hshift; // set hash code range bound

		hsize_reg = hsize;
		cl_hash(hsize_reg); // clear hash table

		output(clearCode, outs);

		outer_loop : while ((c = nextPixel()) != EOF) {
			fcode = (c << maxbits) + ent;
			i = (c << hshift) ^ ent; // xor hashing

			if (htab[i] == fcode) {
				ent = codetab[i];
				continue;
			} else if (htab[i] >= 0) // non-empty slot
				{
				disp = hsize_reg - i; // secondary hash (after G. Knott)
				if (i == 0)
					disp = 1;
				do {
					if ((i -= disp) < 0)
						i += hsize_reg;

					if (htab[i] == fcode) {
						ent = codetab[i];
						continue outer_loop;
					}
				} while (htab[i] >= 0);
			}
			output(ent, outs);
			ent = c;
			if (free_ent < maxmaxcode) {
				codetab[i] = free_ent++; // code -> hashtable
				htab[i] = fcode;
			} else
				cl_block(outs);
		}
		// Put out the final code.
		output(ent, outs);
		output(eofCode, outs);
	}

	/**
	 * Call this method to encode the image data out to the
	 * output stream.  This will result in a GIF89a compliant
	 * stream being written.
	 */
	public void encode() throws IOException {
		super.encode();
	}

	void encodeDone() throws IOException {
		int transparentIndex = -1;
		int transparentRgb = -1;
		// Put all the pixels into a hash table.
		colorHash = new IntHashtable();
		int index = 0;
		for (int row = 0; row < height; ++row) {
			for (int col = 0; col < width; ++col) {
				int rgb = rgbPixels[row][col];
				boolean isTransparent = ((rgb >>> 24) < 0x80);
				if (isTransparent) {
					if (transparentIndex < 0) {
						// First transparent color; remember it.
						transparentIndex = index;
						transparentRgb = rgb;
					} else if (rgb != transparentRgb) {
						// A second transparent color; replace it with
						// the first one.
						rgbPixels[row][col] = rgb = transparentRgb;
					}
				}
				GifEncoderHashItem item = (GifEncoderHashItem) colorHash.get(rgb);
				if (item == null) {
					if (index >= 256)
						throw new IOException("too many colors for a GIF");
					item = new GifEncoderHashItem(rgb, 1, index, isTransparent);
					++index;
					colorHash.put(rgb, item);
				} else
					++item.count;
			}
		}

		// Figure out how many bits to use.
		int logColors;
		if (index <= 2)
			logColors = 1;
		else if (index <= 4)
			logColors = 2;
		else if (index <= 16)
			logColors = 4;
		else
			logColors = 8;

		// Turn colors into colormap entries.
		int mapSize = 1 << logColors;
		byte[] reds = new byte[mapSize];
		byte[] grns = new byte[mapSize];
		byte[] blus = new byte[mapSize];
		for (Enumeration e = colorHash.elements(); e.hasMoreElements();) {
			GifEncoderHashItem item = (GifEncoderHashItem) e.nextElement();
			reds[item.index] = (byte) ((item.rgb >> 16) & 0xff);
			grns[item.index] = (byte) ((item.rgb >> 8) & 0xff);
			blus[item.index] = (byte) (item.rgb & 0xff);
		}

		gifEncode(out, width, height, (byte) 0, transparentIndex, logColors, reds, grns, blus);
	}

	void encodePixels(int x, int y, int w, int h, int[] rgbPixels, int off, int scansize) throws IOException {
		// Save the pixels.
		for (int row = 0; row < h; ++row)
			System.arraycopy(rgbPixels, row * scansize + off, this.rgbPixels[y + row], x, w);

	}

	void encodeStart(int width, int height) throws IOException {
		this.width = width;
		this.height = height;
		rgbPixels = new int[height][width];
	}

	// Flush the packet to disk, and reset the accumulator
	void flush_char(OutputStream outs) throws IOException {
		if (a_count > 0) {
			outs.write(a_count);
			outs.write(accum, 0, a_count);
			a_count = 0;
		}
	}

	byte getPixel(int x, int y) throws IOException {
		GifEncoderHashItem item = (GifEncoderHashItem) colorHash.get(rgbPixels[y][x]);
		if (item == null)
			throw new IOException("color not found");
		return (byte) item.index;
	}

	void gifEncode(OutputStream outs, int encodeWidth, int encodeHeight, byte Background, int Transparent, int BitsPerPixel, byte[] Red, byte[] Green, byte[] Blue) throws IOException {

		byte B;
		int LeftOfs, TopOfs;
		int ColorMapSize;
		int InitCodeSize;
		int i;

		this.encodeWidth = encodeWidth;
		this.encodeHeight = encodeHeight;
		ColorMapSize = 1 << BitsPerPixel;
		LeftOfs = TopOfs = 0;

		// Calculate number of bits we are expecting
		countDown = encodeWidth * encodeHeight;

		// Indicate which pass we are on (if interlace)
		passCount = 0;

		// The initial code size
		if (BitsPerPixel <= 1)
			InitCodeSize = 2;
		else
			InitCodeSize = BitsPerPixel;

		// Set up the current x and y position
		curx = 0;
		cury = 0;

		// Write the Magic header
		writeString(outs, "GIF89a");

		// Write out the screen width and height
		putWord(encodeWidth, outs);
		putWord(encodeHeight, outs);

		// Indicate that there is a global colour map
		B = (byte) 0x80; // Yes, there is a color map
		// OR in the resolution
		B |= (byte) ((8 - 1) << 4);
		// Not sorted
		// OR in the Bits per Pixel
		B |= (byte) ((BitsPerPixel - 1));

		// Write it out
		putByte(B, outs);

		// Write out the Background colour
		putByte(Background, outs);

		// Pixel aspect ratio - 1:1.
		//Putbyte( (byte) 49, outs );
		// Java's GIF reader currently has a bug, if the aspect ratio byte is
		// not zero it throws an ImageFormatException.  It doesn't know that
		// 49 means a 1:1 aspect ratio.  Well, whatever, zero works with all
		// the other decoders I've tried so it probably doesn't hurt.
		putByte((byte) 0, outs);

		// Write out the Global Colour Map
		for (i = 0; i < ColorMapSize; ++i) {
			putByte(Red[i], outs);
			putByte(Green[i], outs);
			putByte(Blue[i], outs);
		}

		// Write out extension for transparent colour index, if necessary.
		if (Transparent != -1) {
			putByte((byte) '!', outs);
			putByte((byte) 0xf9, outs);
			putByte((byte) 4, outs);
			putByte((byte) 1, outs);
			putByte((byte) 0, outs);
			putByte((byte) 0, outs);
			putByte((byte) Transparent, outs);
			putByte((byte) 0, outs);
		}

		// Write an Image separator
		putByte((byte) ',', outs);

		// Write the Image header
		putWord(LeftOfs, outs);
		putWord(TopOfs, outs);
		putWord(encodeWidth, outs);
		putWord(encodeHeight, outs);

		// Write out whether or not the image is interlaced
		if (interlace)
			putByte((byte) 0x40, outs);
		else
			putByte((byte) 0x00, outs);

		// Write out the initial code size
		putByte((byte) InitCodeSize, outs);

		// Go and actually compress the data
		compress(InitCodeSize + 1, outs);

		// Write out a Zero-length packet (to end the series)
		putByte((byte) 0, outs);

		// Write the GIF file terminator
		putByte((byte) ';', outs);
	}

	int maxcode(int n_bits) {
		return (1 << n_bits) - 1;
	}

	// Return the next pixel from the image
	int nextPixel() throws IOException {
		byte r;

		if (countDown == 0)
			return EOF;

		--countDown;

		r = getPixel(curx, cury);

		bumpPixel();

		return r & 0xff;
	}

	void output(int code, OutputStream outs) throws IOException {
		cur_accum &= masks[cur_bits];

		if (cur_bits > 0)
			cur_accum |= (code << cur_bits);
		else
			cur_accum = code;

		cur_bits += n_bits;

		while (cur_bits >= 8) {
			char_out((byte) (cur_accum & 0xff), outs);
			cur_accum >>= 8;
			cur_bits -= 8;
		}

		// If the next entry is going to be too big for the code size,
		// then increase it, if possible.
		if (free_ent > maxcode || clear_flg) {
			if (clear_flg) {
				maxcode = maxcode(n_bits = g_init_bits);
				clear_flg = false;
			} else {
				++n_bits;
				if (n_bits == maxbits)
					maxcode = maxmaxcode;
				else
					maxcode = maxcode(n_bits);
			}
		}

		if (code == eofCode) {
			// At EOF, write the rest of the buffer.
			while (cur_bits > 0) {
				char_out((byte) (cur_accum & 0xff), outs);
				cur_accum >>= 8;
				cur_bits -= 8;
			}

			flush_char(outs);
		}
	}

	// Write out a byte to the GIF file
	void putByte(byte b, OutputStream outs) throws IOException {
		outs.write(b);
	}

	// Write out a word to the GIF file
	void putWord(int w, OutputStream outs) throws IOException {
		putByte((byte) (w & 0xff), outs);
		putByte((byte) ((w >> 8) & 0xff), outs);
	}

	void writeString(OutputStream out, String str) throws IOException {
		byte[] buf = str.getBytes();
		out.write(buf);
	}

}

class IntHashtable extends Dictionary implements Cloneable {

	/// The total number of entries in the hash table.
	private int count;

	/// The load factor for the hashtable.
	private float loadFactor;
	/// The hash table data.
	private IntHashtableEntry table[];

	/// Rehashes the table when count exceeds this threshold.
	private int threshold;

	public IntHashtable() {
		this(101, 0.75f);
	}

	public IntHashtable(int initialCapacity) {
		this(initialCapacity, 0.75f);
	}

	public IntHashtable(int initialCapacity, float loadFactor) {
		if (initialCapacity <= 0 || loadFactor <= 0.0)
			throw new IllegalArgumentException();
		this.loadFactor = loadFactor;
		table = new IntHashtableEntry[initialCapacity];
		threshold = (int) (initialCapacity * loadFactor);
	}

	/// Clears the hash table so that it has no more elements in it.
	public synchronized void clear() {
		IntHashtableEntry tab[] = table;
		for (int index = tab.length; --index >= 0;)
			tab[index] = null;
		count = 0;
	}

	public synchronized Object clone() {
		try {
			IntHashtable t = (IntHashtable) super.clone();
			t.table = new IntHashtableEntry[table.length];
			for (int i = table.length; i-- > 0;)
				t.table[i] = (table[i] != null) ? (IntHashtableEntry) table[i].clone() : null;
			return t;
		} catch (CloneNotSupportedException e) {
			// This shouldn't happen, since we are Cloneable.
			throw new InternalError();
		}
	}

	public synchronized boolean contains(Object value) {
		if (value == null)
			throw new NullPointerException();
		IntHashtableEntry tab[] = table;
		for (int i = tab.length; i-- > 0;) {
			for (IntHashtableEntry e = tab[i]; e != null; e = e.next) {
				if (e.value.equals(value))
					return true;
			}
		}
		return false;
	}

	public synchronized boolean containsKey(int key) {
		IntHashtableEntry tab[] = table;
		int hash = key;
		int index = (hash & 0x7FFFFFFF) % tab.length;
		for (IntHashtableEntry e = tab[index]; e != null; e = e.next) {
			if (e.hash == hash && e.key == key)
				return true;
		}
		return false;
	}

	public synchronized Enumeration elements() {
		return new IntHashtableEnumerator(table, false);
	}

	public synchronized Object get(int key) {
		IntHashtableEntry tab[] = table;
		int hash = key;
		int index = (hash & 0x7FFFFFFF) % tab.length;
		for (IntHashtableEntry e = tab[index]; e != null; e = e.next) {
			if (e.hash == hash && e.key == key)
				return e.value;
		}
		return null;
	}

	public Object get(Object okey) {
		if (!(okey instanceof Integer))
			throw new InternalError("key is not an Integer");
		Integer ikey = (Integer) okey;
		int key = ikey.intValue();
		return get(key);
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public synchronized Enumeration keys() {
		return new IntHashtableEnumerator(table, true);
	}

	public synchronized Object put(int key, Object value) {
		// Make sure the value is not null.
		if (value == null)
			throw new NullPointerException();

		// Makes sure the key is not already in the hashtable.
		IntHashtableEntry tab[] = table;
		int hash = key;
		int index = (hash & 0x7FFFFFFF) % tab.length;
		for (IntHashtableEntry e = tab[index]; e != null; e = e.next) {
			if (e.hash == hash && e.key == key) {
				Object old = e.value;
				e.value = value;
				return old;
			}
		}

		if (count >= threshold) {
			// Rehash the table if the threshold is exceeded.
			rehash();
			return put(key, value);
		}

		// Creates the new entry.
		IntHashtableEntry e = new IntHashtableEntry();
		e.hash = hash;
		e.key = key;
		e.value = value;
		e.next = tab[index];
		tab[index] = e;
		++count;
		return null;
	}

	public Object put(Object okey, Object value) {
		if (!(okey instanceof Integer))
			throw new InternalError("key is not an Integer");
		Integer ikey = (Integer) okey;
		int key = ikey.intValue();
		return put(key, value);
	}

	protected void rehash() {
		int oldCapacity = table.length;
		IntHashtableEntry oldTable[] = table;

		int newCapacity = oldCapacity * 2 + 1;
		IntHashtableEntry newTable[] = new IntHashtableEntry[newCapacity];

		threshold = (int) (newCapacity * loadFactor);
		table = newTable;

		for (int i = oldCapacity; i-- > 0;) {
			for (IntHashtableEntry old = oldTable[i]; old != null;) {
				IntHashtableEntry e = old;
				old = old.next;

				int index = (e.hash & 0x7FFFFFFF) % newCapacity;
				e.next = newTable[index];
				newTable[index] = e;
			}
		}
	}

	public synchronized Object remove(int key) {
		IntHashtableEntry tab[] = table;
		int hash = key;
		int index = (hash & 0x7FFFFFFF) % tab.length;
		for (IntHashtableEntry e = tab[index], prev = null; e != null; prev = e, e = e.next) {
			if (e.hash == hash && e.key == key) {
				if (prev != null)
					prev.next = e.next;
				else
					tab[index] = e.next;
				--count;
				return e.value;
			}
		}
		return null;
	}

	public Object remove(Object okey) {
		if (!(okey instanceof Integer))
			throw new InternalError("key is not an Integer");
		Integer ikey = (Integer) okey;
		int key = ikey.intValue();
		return remove(key);
	}

	public int size() {
		return count;
	}

	public synchronized String toString() {
		int max = size() - 1;
		StringBuffer buf = new StringBuffer();
		Enumeration k = keys();
		Enumeration e = elements();
		buf.append("{");

		for (int i = 0; i <= max; ++i) {
			String s1 = k.nextElement().toString();
			String s2 = e.nextElement().toString();
			buf.append(s1 + "=" + s2);
			if (i < max)
				buf.append(", ");
		}
		buf.append("}");
		return buf.toString();
	}
}

class IntHashtableEntry {
	int hash;
	int key;
	IntHashtableEntry next;
	Object value;

	protected Object clone() {
		IntHashtableEntry entry = new IntHashtableEntry();
		entry.hash = hash;
		entry.key = key;
		entry.value = value;
		entry.next = (next != null) ? (IntHashtableEntry) next.clone() : null;
		return entry;
	}
}

class IntHashtableEnumerator implements Enumeration {
	IntHashtableEntry entry;
	int index;
	boolean keys;
	IntHashtableEntry table[];

	IntHashtableEnumerator(IntHashtableEntry table[], boolean keys) {
		this.table = table;
		this.keys = keys;
		this.index = table.length;
	}

	public boolean hasMoreElements() {
		if (entry != null)
			return true;
		while (index-- > 0)
			if ((entry = table[index]) != null)
				return true;
		return false;
	}

	public Object nextElement() {
		if (entry == null)
			while ((index-- > 0) && ((entry = table[index]) == null));
		if (entry != null) {
			IntHashtableEntry e = entry;
			entry = e.next;
			return keys ? new Integer(e.key) : e.value;
		}
		throw new NoSuchElementException("IntHashtableEnumerator");
	}
}
