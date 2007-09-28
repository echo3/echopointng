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
 * This LGPL code was written by J. David Eisenberg and can be found at :
 * 
 * 	http://www.catcode.com/pngencoder/index.html
 * 
 * v1.4 was incorporated as part of the EchoPoint library on 12/04/2003.
 * v1.5 was incorporated as part of the EchoPoint library on 29/10/2003.
 * 
 * Below is the original copyright notices as per the LGPL.
 *   
 */

/*
 * 
 * Thanks to Jay Denny at KeyPoint Software
 *    http://www.keypoint.com/
 * who let me develop this code on company time.
 *
 * You may contact me with (probably very-much-needed) improvements,
 * comments, and bug fixes at:
 *
 *   david@catcode.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * A copy of the GNU LGPL may be found at
 * http://www.gnu.org/copyleft/lesser.html,
 *
 * @author J. David Eisenberg
 * @version 1.5, 19 Oct 2003
 *
 * CHANGES:
 * --------
 * com.keypoint.PngEncoder :
 * 
 * 19-Nov-2002 : CODING STYLE CHANGES ONLY (by David Gilbert for Object Refinery Limited);
 * 19-Sep-2003 : Fix for platforms using EBCDIC (contributed by Paulo Soares);
 * 19-Oct-2003 : Change private fields to protected fields so that
 *               PngEncoderB can inherit them (JDE)
 *				 Fixed bug with calculation of nRows
 *
 * com.keypoint.PngEncoderB :
 * 
 * 19-Sep-2003 : Fix for platforms using EBCDIC (contributed by Paulo Soares);
 * 19-Oct-2003 : Change private fields to protected fields so that
 *               PngEncoderB can inherit them (JDE)
 *				 Fixed bug with calculation of nRows
 *				 Added modifications for unsigned short images
 *					(contributed by Christian at xpogen.com) 
 */

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * <code>PngEncoder</code> implements the <code>ImageEncoder</code> interface
 * and hence can take a java.awt.Image object and encode it as a PNG
 * output stream.
 * <p>
 * The Image can have a <code>DirectColorModel</code> or an <code>IndexColorModel</code>.  
 * <p>
 * NOTE : While the Echo framework has a PNG encoder built in to it, it currently
 * does not handle transparency correctly.  Therefore this encoder has been 
 * provided.
 */
public class PngEncoder implements ImageEncoder, Serializable {
	
	private transient InternalPngEncoderA encoder=null;
	private boolean   encodeAlpha = true;
	
	/**
	 * Constructs a PngEncoder ready to start encoding, with the Alpha channel
	 * being encoded.
	 */
	public PngEncoder() {
		encodeAlpha = true;
	}
	
	/**
	 * @see echopointng.image.ImageEncoder#encode(java.awt.Image, java.io.OutputStream)
	 */
	public void encode(Image image, OutputStream out) throws IOException {
		if (encoder == null) {
			//
			// The newer B encoder will only take BufferedImage objects
			// whereas the older A encoder takes Image objects.
			//
			if (image instanceof BufferedImage) {
				encoder = new InternalPngEncoderB((BufferedImage) image);
			} else {
				encoder = new InternalPngEncoderA(image);
			}
			encoder.setEncodeAlpha(isEncodeAlpha());
		} else {
			encoder.setImage(image);
		}
		encoder.encode(out,true);

	}
	/**
	 * @see echopointng.image.ImageEncoder#getContentType()
	 */
	public String getContentType() {
		return "image/png";
	}
	/** 
	 * This returns true if the Alpha channel of the image will be encoded
	 * 
	 * @return - true if the Alpha channel of the image will be encoded
	 */
	public boolean isEncodeAlpha() {
		return encodeAlpha;
	}

	/** 
	 * If this is set to true then the Alpha channel of the image will be encoded.  
	 * By this value is true by default.
	 * 
	 * @param b - true if the Alpha channel of the image is to be encoded 
	 */
	public void setEncodeAlpha(boolean b) {
		encodeAlpha = b;
	}

}

/**
 * The <code>InternalPngEncoderB</code> class is the original
 * com.keypoint.PngEncoderB class.  It has been renamed to fit
 * under the echoPoint ImageEncoder scheme.
 * 
 * @author J. David Eisenberg
 * @version 1.5, 19 Oct 2003
 *    
 */

class InternalPngEncoderB extends InternalPngEncoderA 
{

	/** PLTE tag. */
	private static final byte PLTE[] = { 80, 76, 84, 69 };

	protected BufferedImage image;
	protected WritableRaster wRaster;
	protected int tType;

	/**
	 * Class constructor
	 *
	 */
	public InternalPngEncoderB()
	{
		this( null, false, FILTER_NONE, 0 );
	}

	/**
	 * Class constructor specifying BufferedImage to encode, with no alpha channel encoding.
	 *
	 * @param image A Java BufferedImage object
	 */
	public InternalPngEncoderB( BufferedImage image )
	{
		this(image, false, FILTER_NONE, 0);
	}

	/**
	 * Class constructor specifying BufferedImage to encode, and whether to encode alpha.
	 *
	 * @param image A Java BufferedImage object
	 * @param encodeAlpha Encode the alpha channel? false=no; true=yes
	 */
	public InternalPngEncoderB( BufferedImage image, boolean encodeAlpha )
	{
		this( image, encodeAlpha, FILTER_NONE, 0 );
	}

	/**
	 * Class constructor specifying BufferedImage to encode, whether to encode alpha, and filter to use.
	 *
	 * @param image A Java BufferedImage object
	 * @param encodeAlpha Encode the alpha channel? false=no; true=yes
	 * @param whichFilter 0=none, 1=sub, 2=up
	 */
	public InternalPngEncoderB( BufferedImage image, boolean encodeAlpha,int whichFilter )
	{
		this( image, encodeAlpha, whichFilter, 0 );
	}

	/**
	 * Class constructor specifying BufferedImage source to encode, whether to encode alpha, filter to use, and compression level
	 *
	 * @param image A Java BufferedImage object
	 * @param encodeAlpha Encode the alpha channel? false=no; true=yes
	 * @param whichFilter 0=none, 1=sub, 2=up
	 * @param compLevel 0..9
	 */
	public InternalPngEncoderB( BufferedImage image, boolean encodeAlpha,int whichFilter, int compLevel )
	{
		this.image = image;
		this.encodeAlpha = encodeAlpha;
		setFilter( whichFilter );
		if (compLevel >=0 && compLevel <=9)
		{
			this.compressionLevel = compLevel;
		}
	}
	
	/**
	 * Set the BufferedImage to be encoded
	 *
	 * @param BufferedImage A Java BufferedImage object
	 */
	public void setImage( BufferedImage image )
	{
		this.image = image;
		pngBytes = null;
	}

	/**
	 * Creates an array of bytes that is the PNG equivalent of the current image, specifying whether to encode alpha or not.
	 *
	 * @param encodeAlpha boolean false=no alpha, true=encode alpha
	 * @return an array of bytes, or null if there was a problem
	 */
	public byte[] pngEncode( boolean encodeAlpha )
	{
		byte[]  pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };

		if (image == null)
		{
			System.err.println("pngEncode: image is null; returning null");
			return null;
		}
		width = image.getWidth( null );
		height = image.getHeight( null );

		if (!establishStorageInfo())
		{
			System.err.println("pngEncode: cannot establish storage info");
			return null;
		}
        
		/*
		 * start with an array that is big enough to hold all the pixels
		 * (plus filter bytes), and an extra 200 bytes for header info
		 */
		pngBytes = new byte[((width+1) * height * 3) + 200];

		/*
		 * keep track of largest byte written to the array
		 */
		maxPos = 0;

		bytePos = writeBytes( pngIdBytes, 0 );
 //       hdrPos = bytePos;
		writeHeader();
//		  dataPos = bytePos;
		if (writeImageData())
		{
			writeEnd();
			pngBytes = resizeByteArray( pngBytes, maxPos );
		}
		else
		{
			System.err.println("pngEncode: writeImageData failed => null");
			pngBytes = null;
		}
		return pngBytes;
	}

	/**
	 * Creates an array of bytes that is the PNG equivalent of the current image.
	 * Alpha encoding is determined by its setting in the constructor.
	 *
	 * @return an array of bytes, or null if there was a problem
	 */
	public byte[] pngEncode()
	{
		return pngEncode( encodeAlpha );
	}

	/**
	 * 
	 * Get and set variables that determine how picture is stored.
	 *
	 * Retrieves the writable raster of the buffered image,
	 * as well its transfer type.
	 *
	 * Sets number of output bytes per pixel, and, if only
	 * eight-bit bytes, turns off alpha encoding.
	 * @return true if 1-byte or 4-byte data, false otherwise
	 */
	protected boolean establishStorageInfo()
	{
		int dataBytes;
    
		wRaster = image.getRaster();
		dataBytes = wRaster.getNumDataElements();
		tType = wRaster.getTransferType();

		if (((tType == DataBuffer.TYPE_BYTE  ) && (dataBytes == 4)) ||
			((tType == DataBuffer.TYPE_INT   ) && (dataBytes == 1)) ||
		   // on Win 2k/ME, tType == 1, dataBytes == 1
			((tType == DataBuffer.TYPE_USHORT) && (dataBytes == 1)) )
		{
			bytesPerPixel = (encodeAlpha) ? 4 : 3;
		}
		else if ((tType == DataBuffer.TYPE_BYTE) && (dataBytes == 1))
		{
			bytesPerPixel = 1;
			encodeAlpha = false;    // one-byte samples
		}
		else
		{
			System.err.println("PNG encoder cannot establish storage info:");
			System.err.println("  TransferType == " + tType );
			System.err.println("  NumDataElements == " + dataBytes);
			return false;
		}
		return true;
	}

	/**
	 * Write a PNG "IHDR" chunk into the pngBytes array.
	 */
	protected void writeHeader()
	{
		int startPos;

		startPos = bytePos = writeInt4( 13, bytePos );
		bytePos = writeBytes( IHDR, bytePos );
		width = image.getWidth( null );
		height = image.getHeight( null );
		bytePos = writeInt4( width, bytePos );
		bytePos = writeInt4( height, bytePos );
		bytePos = writeByte( 8, bytePos ); // bit depth
		if (bytesPerPixel != 1)
		{
			bytePos = writeByte( (encodeAlpha) ? 6 : 2, bytePos ); // direct model
		}
		else
		{
			bytePos = writeByte( 3, bytePos ); // indexed
		}
		bytePos = writeByte( 0, bytePos ); // compression method
		bytePos = writeByte( 0, bytePos ); // filter method
		bytePos = writeByte( 0, bytePos ); // no interlace
		crc.reset();
		crc.update( pngBytes, startPos, bytePos-startPos );
		crcValue = crc.getValue();
		bytePos = writeInt4( (int) crcValue, bytePos );
	}

	protected void writePalette( IndexColorModel icm )
	{
		byte[] redPal = new byte[256];
		byte[] greenPal = new byte[256];
		byte[] bluePal = new byte[256];
		byte[] allPal = new byte[768];
		int i;

		icm.getReds( redPal );
		icm.getGreens( greenPal );
		icm.getBlues( bluePal );
		for (i=0; i<256; i++)
		{
			allPal[i*3  ] = redPal[i];
			allPal[i*3+1] = greenPal[i];
			allPal[i*3+2] = bluePal[i];
		}
		bytePos = writeInt4( 768, bytePos );
		bytePos = writeBytes( PLTE, bytePos );
		crc.reset();
		crc.update( PLTE );
		bytePos = writeBytes( allPal, bytePos );
		crc.update( allPal );
		crcValue = crc.getValue();
		bytePos = writeInt4( (int) crcValue, bytePos );
	}

	/**
	 * Write the image data into the pngBytes array.
	 * This will write one or more PNG "IDAT" chunks. In order
	 * to conserve memory, this method grabs as many rows as will
	 * fit into 32K bytes, or the whole image; whichever is less.
	 *
	 *
	 * @return true if no errors; false if error grabbing pixels
	 */
	protected boolean writeImageData()
	{
		int rowsLeft = height;  // number of rows remaining to write
		int startRow = 0;       // starting row to process this time through
		int nRows;              // how many rows to grab at a time

		byte[] scanLines;       // the scan lines to be compressed
		int scanPos;            // where we are in the scan lines
		int startPos;           // where this line's actual pixels start (used for filtering)
		int readPos;            // position from which source pixels are read

		byte[] compressedLines; // the resultant compressed lines
		int nCompressed;        // how big is the compressed area?

		byte[] pixels;          // storage area for byte-sized pixels
		int[] iPixels;          // storage area for int-sized pixels
		short[] sPixels;		// for Win 2000/ME ushort pixels
		final int type = image.getType();
		// TYPE_INT_RGB        = 1
		// TYPE_INT_ARGB       = 2
		// TYPE_INT_ARGB_PRE   = 3
		// TYPE_INT_BGR        = 4
		// TYPE_3BYTE_BGR      = 5
		// TYPE_4BYTE_ABGR     = 6
		// TYPE_4BYTE_ABGR_PRE = 7
		// TYPE_BYTE_GRAY      = 10
		// TYPE_BYTE_BINARY    = 12
		// TYPE_BYTE_INDEXED   = 13
		// TYPE_USHORT_GRAY    = 11
		// TYPE_USHORT_565_RGB = 8
		// TYPE_USHORT_555_RGB = 9
		// TYPE_CUSTOM         = 0.

		Deflater scrunch = new Deflater( compressionLevel );
		ByteArrayOutputStream outBytes = 
			new ByteArrayOutputStream(1024);
            
		DeflaterOutputStream compBytes =
			new DeflaterOutputStream( outBytes, scrunch );

		if (bytesPerPixel == 1)
		{
			writePalette( (IndexColorModel) image.getColorModel() );
		}

		try
		{
			while (rowsLeft > 0)
			{
				nRows = Math.min( 32767 / (width*(bytesPerPixel+1)), rowsLeft );
				nRows = Math.max( nRows, 1 );

				/*
				 * Create a data chunk. scanLines adds "nRows" for
				 * the filter bytes.
				 */
				scanLines = new byte[width * nRows * bytesPerPixel +  nRows];

				if (filter == FILTER_SUB)
				{
					leftBytes = new byte[16];
				}
				if (filter == FILTER_UP)
				{
					priorRow = new byte[width*bytesPerPixel];
				}

				final Object data =
					wRaster.getDataElements( 0, startRow, width, nRows, null );

				pixels = null;
				iPixels = null;
				sPixels = null;
				if (tType == DataBuffer.TYPE_BYTE)
				{
					pixels = (byte[]) data;
				}
				else if (tType == DataBuffer.TYPE_INT)
				{
					iPixels = (int[]) data;
				}
				else if (tType == DataBuffer.TYPE_USHORT)
				{
					sPixels = (short[]) data;
				}

				scanPos = 0;
				readPos = 0;
				startPos = 1;
				for (int i=0; i<width*nRows; i++)
				{
					if (i % width == 0)
					{
						scanLines[scanPos++] = (byte) filter; 
						startPos = scanPos;
					}

					if (bytesPerPixel == 1)	// assume TYPE_BYTE, indexed
					{
						scanLines[scanPos++] = pixels[readPos++];
					}
					else if (tType == DataBuffer.TYPE_BYTE)
					{
						scanLines[scanPos++] = pixels[readPos++];
						scanLines[scanPos++] = pixels[readPos++];
						scanLines[scanPos++] = pixels[readPos++];
						if (encodeAlpha)
						{
							scanLines[scanPos++] = pixels[readPos++];
						}
						else
						{
							readPos++;
						}
					}
					else if (tType == DataBuffer.TYPE_USHORT)
					{
						short pxl = sPixels[readPos++];
						if (type == BufferedImage.TYPE_USHORT_565_RGB) {
							scanLines[scanPos++] = (byte) ((pxl >> 8) & 0xf8);
							scanLines[scanPos++] = (byte) ((pxl >> 2) & 0xfc);
						} else {                // assume USHORT_555_RGB
							scanLines[scanPos++] = (byte) ((pxl >> 7) & 0xf8);
							scanLines[scanPos++] = (byte) ((pxl >> 2) & 0xf8);
						}
						scanLines[scanPos++] = (byte) ((pxl << 3) & 0xf8);
					}
					else      // assume tType INT and type RGB or ARGB
					{
						int pxl = iPixels[readPos++];
						scanLines[scanPos++] = (byte) ((pxl >> 16) & 0xff);
						scanLines[scanPos++] = (byte) ((pxl >>  8) & 0xff);
						scanLines[scanPos++] = (byte) ((pxl      ) & 0xff);
						if (encodeAlpha) {
							scanLines[scanPos++] = (byte) ((pxl >> 24) & 0xff);
						}
					}

					if ((i % width == width-1) && (filter != FILTER_NONE))
					{
						if (filter == FILTER_SUB)
						{
							filterSub( scanLines, startPos, width );
						}
						if (filter == FILTER_UP)
						{
							filterUp( scanLines, startPos, width );
						}
					}
				}

				/*
				 * Write these lines to the output area
				 */
				compBytes.write( scanLines, 0, scanPos );

				startRow += nRows;
				rowsLeft -= nRows;
			}
			compBytes.close();

			/*
			 * Write the compressed bytes
			 */
			compressedLines = outBytes.toByteArray();
			nCompressed = compressedLines.length;

			crc.reset();
			bytePos = writeInt4( nCompressed, bytePos );
			bytePos = writeBytes( IDAT, bytePos );
			crc.update( IDAT );
			bytePos = writeBytes( compressedLines, nCompressed, bytePos );
			crc.update( compressedLines, 0, nCompressed );

			crcValue = crc.getValue();
			bytePos = writeInt4( (int) crcValue, bytePos );
			scrunch.finish();
			return true;
		}
		catch (IOException e)
		{
			System.err.println( e.toString());
			return false;
		}
	}
}

/**
 * The <code>InternalPngEncoderA</code> class is the original
 * com.keypoint.PngEncoder class.  It has been renamed to fit
 * under the echoPoint ImageEncoder scheme.
 * 
 * @author J. David Eisenberg
 * @version 1.5, 19 Oct 2003
 *    
 */
class InternalPngEncoderA extends Object {

	/** Constant specifying that alpha channel should be encoded. */
	public static final boolean ENCODE_ALPHA = true;

	/** Constant specifying that alpha channel should not be encoded. */
	public static final boolean NO_ALPHA = false;

	/** Constants for filter (NONE) */
	public static final int FILTER_NONE = 0;

	/** Constants for filter (SUB) */
	public static final int FILTER_SUB = 1;

	/** Constants for filter (UP) */
	public static final int FILTER_UP = 2;

	/** Constants for filter (LAST) */
	public static final int FILTER_LAST = 2;
    
	/** IHDR tag. */
	protected static final byte IHDR[] = {73, 72, 68, 82};
    
	/** IDAT tag. */
	protected static final byte IDAT[] = {73, 68, 65, 84};
    
	/** IEND tag. */
	protected static final byte IEND[] = {73, 69, 78, 68};

	/** The png bytes. */
	protected byte[] pngBytes;

	/** The prior row. */
	protected byte[] priorRow;

	/** The left bytes. */
	protected byte[] leftBytes;

	/** The image. */
	protected Image image;

	/** The width. */
	protected int width, height;

	/** The byte position. */
	protected int bytePos, maxPos;

	/** CRC. */
	protected CRC32 crc = new CRC32();

	/** The CRC value. */
	protected long crcValue;

	/** Encode alpha? */
	protected boolean encodeAlpha;

	/** The filter type. */
	protected int filter;

	/** The bytes-per-pixel. */
	protected int bytesPerPixel;

	/** The compression level. */
	protected int compressionLevel;

	/**
	 * Class constructor
	 */
	public InternalPngEncoderA() {
		this(null, false, FILTER_NONE, 0);
	}

	/**
	 * Class constructor specifying Image to encode, with no alpha channel encoding.
	 *
	 * @param image A Java Image object which uses the DirectColorModel
	 * @see java.awt.Image
	 */
	public InternalPngEncoderA(Image image) {
		this(image, false, FILTER_NONE, 0);
	}

	/**
	 * Class constructor specifying Image to encode, and whether to encode alpha.
	 *
	 * @param image A Java Image object which uses the DirectColorModel
	 * @param encodeAlpha Encode the alpha channel? false=no; true=yes
	 * @see java.awt.Image
	 */
	public InternalPngEncoderA(Image image, boolean encodeAlpha) {
		this(image, encodeAlpha, FILTER_NONE, 0);
	}

	/**
	 * Class constructor specifying Image to encode, whether to encode alpha, and filter to use.
	 *
	 * @param image A Java Image object which uses the DirectColorModel
	 * @param encodeAlpha Encode the alpha channel? false=no; true=yes
	 * @param whichFilter 0=none, 1=sub, 2=up
	 * @see java.awt.Image
	 */
	public InternalPngEncoderA(Image image, boolean encodeAlpha, int whichFilter) {
		this(image, encodeAlpha, whichFilter, 0);
	}


	/**
	 * Class constructor specifying Image source to encode, whether to encode alpha, filter to use,
	 * and compression level.
	 *
	 * @param image A Java Image object
	 * @param encodeAlpha Encode the alpha channel? false=no; true=yes
	 * @param whichFilter 0=none, 1=sub, 2=up
	 * @param compLevel 0..9
	 * @see java.awt.Image
	 */
	public InternalPngEncoderA(Image image, boolean encodeAlpha, int whichFilter, int compLevel) {
		this.image = image;
		this.encodeAlpha = encodeAlpha;
		setFilter(whichFilter);
		if (compLevel >= 0 && compLevel <= 9) {
			this.compressionLevel = compLevel;
		}
	}

	/**
	 * Encodes to an outputstream with bytes that are the PNG 
	 * equivalent of the current image.
	 * 
	 * Alpha encoding is determined by its setting in the constructor.
	 *
	 * @param out OutputStream  the destination ouotput stream
	 * @throws IOException
	 */
	public void encode(OutputStream out) throws IOException {
		out.write(pngEncode());
	}

	/**
	 * Encodes to an output stream with bytes that are the PNG 
	 * equivalent of the current image, specifying whether to 
	 * encode alpha or not.
	 *
	 * @param out OutputStream  the destination ouotput stream
	 * @param encodeAlpha boolean false=no alpha, true=encode alpha
	 * @throws IOException
	 */
	public void encode(OutputStream out, boolean encodeAlpha) throws IOException {
		out.write(pngEncode(encodeAlpha));
	}	

	/**
	 * Set the image to be encoded
	 *
	 * @param image A Java Image object which uses the DirectColorModel
	 * @see java.awt.Image
	 * @see java.awt.image.DirectColorModel
	 */
	public void setImage(Image image) {
		this.image = image;
		pngBytes = null;
	}

	/**
	 * Creates an array of bytes that is the PNG equivalent of the current image, specifying
	 * whether to encode alpha or not.
	 *
	 * @param encodeAlpha boolean false=no alpha, true=encode alpha
	 * @return an array of bytes, or null if there was a problem
	 */
	public byte[] pngEncode(boolean encodeAlpha) {
		byte[]  pngIdBytes = {-119, 80, 78, 71, 13, 10, 26, 10};

		if (image == null) {
			return null;
		}
		width = image.getWidth(null);
		height = image.getHeight(null);

		/*
		 * start with an array that is big enough to hold all the pixels
		 * (plus filter bytes), and an extra 200 bytes for header info
		 */
		pngBytes = new byte[((width + 1) * height * 3) + 200];

		/*
		 * keep track of largest byte written to the array
		 */
		maxPos = 0;

		bytePos = writeBytes(pngIdBytes, 0);
		//hdrPos = bytePos;
		writeHeader();
		//dataPos = bytePos;
		if (writeImageData()) {
			writeEnd();
			pngBytes = resizeByteArray(pngBytes, maxPos);
		}
		else {
			pngBytes = null;
		}
		return pngBytes;
	}

	/**
	 * Creates an array of bytes that is the PNG equivalent of the current image.
	 * Alpha encoding is determined by its setting in the constructor.
	 *
	 * @return an array of bytes, or null if there was a problem
	 */
	public byte[] pngEncode() {
		return pngEncode(encodeAlpha);
	}

	/**
	 * Set the alpha encoding on or off.
	 *
	 * @param encodeAlpha  false=no, true=yes
	 */
	public void setEncodeAlpha(boolean encodeAlpha) {
		this.encodeAlpha = encodeAlpha;
	}

	/**
	 * Retrieve alpha encoding status.
	 *
	 * @return boolean false=no, true=yes
	 */
	public boolean getEncodeAlpha() {
		return encodeAlpha;
	}

	/**
	 * Set the filter to use
	 *
	 * @param whichFilter from constant list
	 */
	public void setFilter(int whichFilter) {
		this.filter = FILTER_NONE;
		if (whichFilter <= FILTER_LAST) {
			this.filter = whichFilter;
		}
	}

	/**
	 * Retrieve filtering scheme
	 *
	 * @return int (see constant list)
	 */
	public int getFilter() {
		return filter;
	}

	/**
	 * Set the compression level to use
	 *
	 * @param level 0 through 9
	 */
	public void setCompressionLevel(int level) {
		if (level >= 0 && level <= 9) {
			this.compressionLevel = level;
		}
	}

	/**
	 * Retrieve compression level
	 *
	 * @return int in range 0-9
	 */
	public int getCompressionLevel() {
		return compressionLevel;
	}

	/**
	 * Increase or decrease the length of a byte array.
	 *
	 * @param array The original array.
	 * @param newLength The length you wish the new array to have.
	 * @return Array of newly desired length. If shorter than the
	 *         original, the trailing elements are truncated.
	 */
	protected byte[] resizeByteArray(byte[] array, int newLength) {
		byte[]  newArray = new byte[newLength];
		int     oldLength = array.length;

		System.arraycopy(array, 0, newArray, 0, Math.min(oldLength, newLength));
		return newArray;
	}

	/**
	 * Write an array of bytes into the pngBytes array.
	 * Note: This routine has the side effect of updating
	 * maxPos, the largest element written in the array.
	 * The array is resized by 1000 bytes or the length
	 * of the data to be written, whichever is larger.
	 *
	 * @param data The data to be written into pngBytes.
	 * @param offset The starting point to write to.
	 * @return The next place to be written to in the pngBytes array.
	 */
	protected int writeBytes(byte[] data, int offset) {
		maxPos = Math.max(maxPos, offset + data.length);
		if (data.length + offset > pngBytes.length) {
			pngBytes = resizeByteArray(pngBytes, pngBytes.length + Math.max(1000, data.length));
		}
		System.arraycopy(data, 0, pngBytes, offset, data.length);
		return offset + data.length;
	}

	/**
	 * Write an array of bytes into the pngBytes array, specifying number of bytes to write.
	 * Note: This routine has the side effect of updating
	 * maxPos, the largest element written in the array.
	 * The array is resized by 1000 bytes or the length
	 * of the data to be written, whichever is larger.
	 *
	 * @param data The data to be written into pngBytes.
	 * @param nBytes The number of bytes to be written.
	 * @param offset The starting point to write to.
	 * @return The next place to be written to in the pngBytes array.
	 */
	protected int writeBytes(byte[] data, int nBytes, int offset) {
		maxPos = Math.max(maxPos, offset + nBytes);
		if (nBytes + offset > pngBytes.length) {
			pngBytes = resizeByteArray(pngBytes, pngBytes.length + Math.max(1000, nBytes));
		}
		System.arraycopy(data, 0, pngBytes, offset, nBytes);
		return offset + nBytes;
	}

	/**
	 * Write a two-byte integer into the pngBytes array at a given position.
	 *
	 * @param n The integer to be written into pngBytes.
	 * @param offset The starting point to write to.
	 * @return The next place to be written to in the pngBytes array.
	 */
	protected int writeInt2(int n, int offset) {
		byte[] temp = {(byte) ((n >> 8) & 0xff), (byte) (n & 0xff)};
		return writeBytes(temp, offset);
	}

	/**
	 * Write a four-byte integer into the pngBytes array at a given position.
	 *
	 * @param n The integer to be written into pngBytes.
	 * @param offset The starting point to write to.
	 * @return The next place to be written to in the pngBytes array.
	 */
	protected int writeInt4(int n, int offset) {
		byte[] temp = {(byte) ((n >> 24) & 0xff),
					   (byte) ((n >> 16) & 0xff),
					   (byte) ((n >> 8) & 0xff),
					   (byte) (n & 0xff)};
		return writeBytes(temp, offset);
	}

	/**
	 * Write a single byte into the pngBytes array at a given position.
	 *
	 * @param b The integer to be written into pngBytes.
	 * @param offset The starting point to write to.
	 * @return The next place to be written to in the pngBytes array.
	 */
	protected int writeByte(int b, int offset) {
		byte[] temp = {(byte) b};
		return writeBytes(temp, offset);
	}

	/**
	 * Write a PNG "IHDR" chunk into the pngBytes array.
	 */
	protected void writeHeader() {
		int startPos;

		startPos = bytePos = writeInt4(13, bytePos);
		bytePos = writeBytes(IHDR, bytePos);
		width = image.getWidth(null);
		height = image.getHeight(null);
		bytePos = writeInt4(width, bytePos);
		bytePos = writeInt4(height, bytePos);
		bytePos = writeByte(8, bytePos); // bit depth
		bytePos = writeByte((encodeAlpha) ? 6 : 2, bytePos); // direct model
		bytePos = writeByte(0, bytePos); // compression method
		bytePos = writeByte(0, bytePos); // filter method
		bytePos = writeByte(0, bytePos); // no interlace
		crc.reset();
		crc.update(pngBytes, startPos, bytePos - startPos);
		crcValue = crc.getValue();
		bytePos = writeInt4((int) crcValue, bytePos);
	}

	/**
	 * Perform "sub" filtering on the given row.
	 * Uses temporary array leftBytes to store the original values
	 * of the previous pixels.  The array is 16 bytes long, which
	 * will easily hold two-byte samples plus two-byte alpha.
	 *
	 * @param pixels The array holding the scan lines being built
	 * @param startPos Starting position within pixels of bytes to be filtered.
	 * @param width Width of a scanline in pixels.
	 */
	protected void filterSub(byte[] pixels, int startPos, int width) {
		int i;
		int offset = bytesPerPixel;
		int actualStart = startPos + offset;
		int nBytes = width * bytesPerPixel;
		int leftInsert = offset;
		int leftExtract = 0;

		for (i = actualStart; i < startPos + nBytes; i++) {
			leftBytes[leftInsert] =  pixels[i];
			pixels[i] = (byte) ((pixels[i] - leftBytes[leftExtract]) % 256);
			leftInsert = (leftInsert + 1) % 0x0f;
			leftExtract = (leftExtract + 1) % 0x0f;
		}
	}

	/**
	 * Perform "up" filtering on the given row.
	 * Side effect: refills the prior row with current row
	 *
	 * @param pixels The array holding the scan lines being built
	 * @param startPos Starting position within pixels of bytes to be filtered.
	 * @param width Width of a scanline in pixels.
	 */
	protected void filterUp(byte[] pixels, int startPos, int width) {
		int     i, nBytes;
		byte    currentByte;

		nBytes = width * bytesPerPixel;

		for (i = 0; i < nBytes; i++) {
			currentByte = pixels[startPos + i];
			pixels[startPos + i] = (byte) ((pixels[startPos  + i] - priorRow[i]) % 256);
			priorRow[i] = currentByte;
		}
	}

	/**
	 * Write the image data into the pngBytes array.
	 * This will write one or more PNG "IDAT" chunks. In order
	 * to conserve memory, this method grabs as many rows as will
	 * fit into 32K bytes, or the whole image; whichever is less.
	 *
	 *
	 * @return true if no errors; false if error grabbing pixels
	 */
	protected boolean writeImageData() {
		int rowsLeft = height;  // number of rows remaining to write
		int startRow = 0;       // starting row to process this time through
		int nRows;              // how many rows to grab at a time

		byte[] scanLines;       // the scan lines to be compressed
		int scanPos;            // where we are in the scan lines
		int startPos;           // where this line's actual pixels start (used for filtering)

		byte[] compressedLines; // the resultant compressed lines
		int nCompressed;        // how big is the compressed area?

		//int depth;              // color depth ( handle only 8 or 32 )

		PixelGrabber pg;

		bytesPerPixel = (encodeAlpha) ? 4 : 3;

		Deflater scrunch = new Deflater(compressionLevel);
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream(1024);

		DeflaterOutputStream compBytes = new DeflaterOutputStream(outBytes, scrunch);
		try {
			while (rowsLeft > 0) {
				nRows = Math.min(32767 / (width * (bytesPerPixel + 1)), rowsLeft);
				nRows = Math.max( nRows, 1 );

				int[] pixels = new int[width * nRows];

				pg = new PixelGrabber(image, 0, startRow,
					width, nRows, pixels, 0, width);
				try {
					pg.grabPixels();
				}
				catch (Exception e) {
					System.err.println("interrupted waiting for pixels!");
					return false;
				}
				if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
					System.err.println("image fetch aborted or errored");
					return false;
				}

				/*
				 * Create a data chunk. scanLines adds "nRows" for
				 * the filter bytes.
				 */
				scanLines = new byte[width * nRows * bytesPerPixel +  nRows];

				if (filter == FILTER_SUB) {
					leftBytes = new byte[16];
				}
				if (filter == FILTER_UP) {
					priorRow = new byte[width * bytesPerPixel];
				}

				scanPos = 0;
				startPos = 1;
				for (int i = 0; i < width * nRows; i++) {
					if (i % width == 0) {
						scanLines[scanPos++] = (byte) filter;
						startPos = scanPos;
					}
					scanLines[scanPos++] = (byte) ((pixels[i] >> 16) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i] >>  8) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i]) & 0xff);
					if (encodeAlpha) {
						scanLines[scanPos++] = (byte) ((pixels[i] >> 24) & 0xff);
					}
					if ((i % width == width - 1) && (filter != FILTER_NONE)) {
						if (filter == FILTER_SUB) {
							filterSub(scanLines, startPos, width);
						}
						if (filter == FILTER_UP) {
							filterUp(scanLines, startPos, width);
						}
					}
				}

				/*
				 * Write these lines to the output area
				 */
				compBytes.write(scanLines, 0, scanPos);

				startRow += nRows;
				rowsLeft -= nRows;
			}
			compBytes.close();

			/*
			 * Write the compressed bytes
			 */
			compressedLines = outBytes.toByteArray();
			nCompressed = compressedLines.length;

			crc.reset();
			bytePos = writeInt4(nCompressed, bytePos);
			bytePos = writeBytes(IDAT, bytePos);
			crc.update(IDAT);
			bytePos = writeBytes(compressedLines, nCompressed, bytePos);
			crc.update(compressedLines, 0, nCompressed);

			crcValue = crc.getValue();
			bytePos = writeInt4((int) crcValue, bytePos);
			scrunch.finish();
			return true;
		}
		catch (IOException e) {
			System.err.println(e.toString());
			return false;
		}
	}

	/**
	 * Write a PNG "IEND" chunk into the pngBytes array.
	 */
	protected void writeEnd() {
		bytePos = writeInt4(0, bytePos);
		bytePos = writeBytes(IEND, bytePos);
		crc.reset();
		crc.update(IEND);
		crcValue = crc.getValue();
		bytePos = writeInt4((int) crcValue, bytePos);
	}
}